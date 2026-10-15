// @ts-nocheck
// claim-coupon — Supabase Edge Function
// Accepts: { product_id, email, user_id? }
// Generates a unique SAVE-XXXXXX code, inserts into coupons + product_coupons,
// sends an HTML email via Brevo. Brevo failures are non-fatal.

import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

declare const Deno: any;

const CORS_HEADERS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers":
    "authorization, x-client-info, apikey, content-type",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
};

function jsonResp(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { ...CORS_HEADERS, "Content-Type": "application/json" },
  });
}

/**
 * Generate a VARCHAR(255)-safe random ID (UUID v4 format).
 * Matches the project convention: all IDs are VARCHAR(255).
 */
function generateId(): string {
  return crypto.randomUUID();
}

/** Generates a random 6-char uppercase alphanumeric coupon suffix. */
function randomSuffix(len = 6): string {
  const chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // no ambiguous chars (0/O, 1/I)
  const arr = new Uint8Array(len);
  crypto.getRandomValues(arr);
  return Array.from(arr).map((b) => chars[b % chars.length]).join("");
}

/** Simple email format check. */
function isValidEmail(email: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

serve(async (req: Request) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: CORS_HEADERS });
  }

  try {
    const body = await req.json().catch(() => ({}));
    const { product_id, email, user_id } = body;

    // ── Validation ──────────────────────────────────────────────────────────
    if (!product_id || typeof product_id !== "string") {
      return jsonResp({ ok: false, error: "product_id is required" }, 400);
    }
    if (!email || !isValidEmail(String(email))) {
      return jsonResp({ ok: false, error: "Valid email is required" }, 400);
    }

    // ── Service-role client (bypasses RLS) ──────────────────────────────────
    const supabase = createClient(
      Deno.env.get("SUPABASE_URL")!,
      Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
    );

    // ── Verify product exists ────────────────────────────────────────────────
    const { data: product, error: productErr } = await supabase
      .from("products")
      .select("product_id, name, price")
      .eq("product_id", product_id)
      .maybeSingle();

    if (productErr) {
      console.error("Product lookup:", productErr);
      return jsonResp({ ok: false, error: "Database error" }, 500);
    }
    if (!product) {
      return jsonResp({ ok: false, error: "Product not found" }, 404);
    }

    const normalizedEmail = String(email).trim().toLowerCase();

    // ── Prevent duplicate claims (same email + same product) ─────────────────
    const { data: existing } = await supabase
      .from("product_coupons")
      .select("claim_id")
      .eq("product_id", product_id)
      .eq("email", normalizedEmail)
      .maybeSingle();

    if (existing) {
      return jsonResp(
        {
          ok: false,
          error:
            "A coupon has already been claimed for this email and product.",
        },
        409,
      );
    }

    // ── Generate unique coupon code (retry up to 10x to avoid collisions) ────
    let code = "";
    for (let attempt = 0; attempt < 10; attempt++) {
      const candidate = `SAVE-${randomSuffix(6)}`;
      const { data: collision } = await supabase
        .from("coupons")
        .select("coupon_id")
        .eq("code", candidate)
        .maybeSingle();
      if (!collision) {
        code = candidate;
        break;
      }
    }
    if (!code) {
      return jsonResp(
        { ok: false, error: "Could not generate a unique coupon code" },
        500,
      );
    }

    const now = new Date();
    const validTo = new Date(now);
    validTo.setDate(validTo.getDate() + 30);

    // ── Generate IDs (project convention: VARCHAR(255), no DB default) ────────
    const couponId = generateId();
    const claimId = generateId();

    // ── Insert into coupons ──────────────────────────────────────────────────
    const { error: couponErr } = await supabase
      .from("coupons")
      .insert({
        coupon_id: couponId,
        code,
        description: `10% off ${product.name}`,
        discount_type: "percentage",
        discount_value: 10,
        is_active: true,
        usage_limit: 1,
        usage_count: 0,
        valid_from: now.toISOString(),
        valid_to: validTo.toISOString(),
      });

    if (couponErr) {
      console.error("Coupon insert error:", couponErr);
      return jsonResp({ ok: false, error: couponErr.message }, 500);
    }

    // ── Insert into product_coupons ──────────────────────────────────────────
    const { error: linkErr } = await supabase
      .from("product_coupons")
      .insert({
        claim_id: claimId,
        product_id,
        coupon_id: couponId,
        user_id: user_id || null,
        email: normalizedEmail,
        claimed_at: now.toISOString(),
        status: "claimed",
      });

    if (linkErr) {
      console.error("product_coupons insert error:", linkErr);
      // Attempt cleanup of the orphaned coupon row
      await supabase.from("coupons").delete().eq("coupon_id", couponId);
      return jsonResp({ ok: false, error: linkErr.message }, 500);
    }

    // ── Send email via Brevo ─────────────────────────────────────────────────
    let emailWarning: string | null = null;
    try {
      const brevoKey = Deno.env.get("BREVO_API_KEY");
      const senderEmail =
        Deno.env.get("BREVO_SENDER_EMAIL") || "noreply@storecraft.app";
      const senderName = Deno.env.get("BREVO_SENDER_NAME") || "StoreCraft";

      if (!brevoKey) {
        emailWarning = "BREVO_API_KEY not configured – email was not sent";
      } else {
        const validToDisplay = validTo.toLocaleDateString("en-ZA", {
          day: "numeric",
          month: "long",
          year: "numeric",
        });

        const htmlContent = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Your StoreCraft Coupon</title>
</head>
<body style="margin:0;padding:0;background:#f8fafc;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Arial,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background:#f8fafc;">
    <tr>
      <td align="center" style="padding:40px 20px;">
        <table role="presentation" width="560" cellpadding="0" cellspacing="0"
          style="background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);max-width:560px;width:100%;">

          <!-- HEADER -->
          <tr>
            <td style="background:linear-gradient(135deg,#0f766e 0%,#6366f1 100%);padding:40px 32px;text-align:center;">
              <h1 style="margin:0 0 8px;color:#ffffff;font-size:26px;font-weight:700;letter-spacing:-0.5px;">
                🎉 Your Coupon is Ready!
              </h1>
              <p style="margin:0;color:rgba(255,255,255,0.85);font-size:15px;">
                A special discount just for you from StoreCraft
              </p>
            </td>
          </tr>

          <!-- BODY -->
          <tr>
            <td style="padding:36px 32px;">
              <p style="margin:0 0 6px;color:#64748b;font-size:14px;">You've claimed a coupon for:</p>
              <p style="margin:0 0 28px;color:#1e293b;font-size:18px;font-weight:700;">${product.name}</p>

              <!-- CODE BOX — this is the main element -->
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0">
                <tr>
                  <td style="background:linear-gradient(135deg,#ccfbf1,#e0e7ff);border:2px dashed #0f766e;
                              border-radius:12px;padding:28px 24px;text-align:center;">
                    <p style="margin:0 0 6px;color:#64748b;font-size:12px;text-transform:uppercase;letter-spacing:1.5px;font-weight:600;">
                      Your Exclusive Discount Code
                    </p>
                    <p style="margin:0 0 8px;color:#0f766e;font-size:40px;font-weight:800;letter-spacing:5px;font-family:'Courier New',Courier,monospace;">
                      ${code}
                    </p>
                    <p style="margin:0;color:#6366f1;font-size:22px;font-weight:800;">
                      10% OFF
                    </p>
                  </td>
                </tr>
              </table>

              <!-- HOW TO REDEEM -->
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="margin:24px 0 0;">
                <tr>
                  <td style="background:#f8fafc;border-radius:8px;padding:16px 20px;">
                    <p style="margin:0 0 8px;color:#1e293b;font-size:14px;font-weight:700;">How to redeem:</p>
                    <ul style="margin:0;padding:0 0 0 18px;color:#475569;font-size:14px;line-height:1.8;">
                      <li>Show this code in-store at checkout</li>
                      <li>Mention it when placing a delivery order</li>
                      <li>Valid for <strong>30 days</strong> from today</li>
                      <li>Single use only &mdash; one redemption per code</li>
                    </ul>
                  </td>
                </tr>
              </table>

              <p style="margin:20px 0 0;color:#94a3b8;font-size:13px;">
                Valid until: <strong style="color:#475569;">${validToDisplay}</strong>
              </p>
            </td>
          </tr>

          <!-- FOOTER -->
          <tr>
            <td style="background:#f1f5f9;padding:20px 32px;text-align:center;border-top:1px solid #e2e8f0;">
              <p style="margin:0;color:#94a3b8;font-size:12px;line-height:1.6;">
                StoreCraft &mdash; Your trusted e-marketplace<br>
                If you did not request this coupon, you can safely ignore this email.
              </p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>`;

        const brevoRes = await fetch("https://api.brevo.com/v3/smtp/email", {
          method: "POST",
          headers: {
            "api-key": brevoKey,
            "Content-Type": "application/json",
            Accept: "application/json",
          },
          body: JSON.stringify({
            sender: { name: senderName, email: senderEmail },
            to: [{ email: normalizedEmail }],
            subject: `Your StoreCraft Coupon: ${code} — 10% off ${product.name}`,
            htmlContent,
          }),
        });

        if (!brevoRes.ok) {
          const errBody = await brevoRes.text();
          console.warn("Brevo API non-OK response:", brevoRes.status, errBody);
          emailWarning = "Email could not be sent – check your Brevo config";
        }
      }
    } catch (emailErr) {
      console.warn("Brevo send failed (non-fatal):", emailErr);
      emailWarning = "Email delivery failed – your coupon code is still saved";
    }

    const responseBody: Record<string, unknown> = { ok: true, code };
    if (emailWarning) responseBody.warning = emailWarning;
    return jsonResp(responseBody);
  } catch (err) {
    console.error("Unhandled error in claim-coupon:", err);
    return jsonResp({ ok: false, error: "Internal server error" }, 500);
  }
});

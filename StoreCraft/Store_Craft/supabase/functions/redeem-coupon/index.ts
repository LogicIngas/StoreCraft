// @ts-nocheck
// redeem-coupon — Supabase Edge Function
// Accepts: { code, staff_id }
// Looks up the coupon by code, marks the product_coupon row as redeemed.

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

serve(async (req: Request) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: CORS_HEADERS });
  }

  try {
    const { code, staff_id } = await req.json();

    // ── Validation ──────────────────────────────────────────────────────────
    if (!code || typeof code !== "string") {
      return jsonResp({ ok: false, error: "code is required" }, 400);
    }
    if (!staff_id || typeof staff_id !== "string") {
      return jsonResp({ ok: false, error: "staff_id is required" }, 400);
    }

    const supabase = createClient(
      Deno.env.get("SUPABASE_URL")!,
      Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
    );

    // ── Look up coupon by code ───────────────────────────────────────────────
    const { data: coupon, error: couponErr } = await supabase
      .from("coupons")
      .select("coupon_id, code, is_active, usage_count, usage_limit")
      .eq("code", code.trim().toUpperCase())
      .maybeSingle();

    if (couponErr) {
      console.error("Coupon lookup:", couponErr);
      return jsonResp({ ok: false, error: "Database error" }, 500);
    }
    if (!coupon) {
      return jsonResp({ ok: false, error: "Coupon code not found" }, 404);
    }

    // ── Update product_coupons row ───────────────────────────────────────────
    const { data: updated, error: updateErr } = await supabase
      .from("product_coupons")
      .update({
        status: "redeemed",
        redeemed_at: new Date().toISOString(),
        redeemed_by: staff_id,
      })
      .eq("coupon_id", coupon.coupon_id)
      .eq("status", "claimed")
      .select()
      .maybeSingle();

    if (updateErr) {
      console.error("Update error:", updateErr);
      return jsonResp({ ok: false, error: updateErr.message }, 500);
    }
    if (!updated) {
      return jsonResp(
        {
          ok: false,
          error:
            "Coupon is already redeemed, expired, or does not have a claimed status.",
        },
        409,
      );
    }

    // ── Also bump usage_count on coupons table ───────────────────────────────
    await supabase
      .from("coupons")
      .update({ usage_count: (coupon.usage_count ?? 0) + 1, is_active: false })
      .eq("coupon_id", coupon.coupon_id);

    return jsonResp({
      ok: true,
      message: "Coupon successfully redeemed",
      redemption: updated,
    });
  } catch (err) {
    console.error("Unhandled error:", err);
    return jsonResp({ ok: false, error: "Internal server error" }, 500);
  }
});

// @ts-nocheck
// submit-review — Supabase Edge Function
// Accepts: { product_id, rating, comment?, reviewer_name?, user_id? }
// Validates rating 1–5 and product existence; inserts review via service_role.

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

const handler = async (req: Request) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: CORS_HEADERS });
  }

  try {
    const body = await req.json().catch(() => ({}));
    const { product_id, rating, comment, reviewer_name, user_id } = body;

    // ── Validation ──────────────────────────────────────────────────────────
    if (!product_id || typeof product_id !== "string") {
      return jsonResp({ ok: false, error: "product_id is required" }, 400);
    }

    const ratingNum = Number(rating);
    if (!Number.isInteger(ratingNum) || ratingNum < 1 || ratingNum > 5) {
      return jsonResp(
        { ok: false, error: "rating must be an integer between 1 and 5" },
        400,
      );
    }

    // ── Service-role client (bypasses RLS) ──────────────────────────────────
    const supabaseUrl = Deno.env.get("SUPABASE_URL");
    const serviceRoleKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY");

    if (!supabaseUrl || !serviceRoleKey) {
      console.error("Missing SUPABASE_URL or SUPABASE_SERVICE_ROLE_KEY");
      return jsonResp({ ok: false, error: "Server configuration error" }, 500);
    }

    const supabase = createClient(supabaseUrl, serviceRoleKey);

    // ── Verify product exists ────────────────────────────────────────────────
    const { data: product, error: productErr } = await supabase
      .from("products")
      .select("product_id, name")
      .eq("product_id", product_id)
      .maybeSingle();

    if (productErr) {
      console.error("Product lookup error:", productErr);
      return jsonResp({ ok: false, error: "Database error" }, 500);
    }
    if (!product) {
      return jsonResp({ ok: false, error: "Product not found" }, 404);
    }

    // ── Build insert payload ─────────────────────────────────────────────────
    const isAnonymous = !user_id;
    const displayName =
      typeof reviewer_name === "string" && reviewer_name.trim()
        ? reviewer_name.trim()
        : isAnonymous
        ? "Anonymous"
        : null;

    const cleanComment =
      typeof comment === "string" && comment.trim() ? comment.trim() : null;

    const reviewId = crypto.randomUUID();

    const insertPayload = {
      review_id: reviewId,
      product_id,
      rating: ratingNum,
      comment: cleanComment,
      review_text: cleanComment,
      reviewer_name: displayName,
      is_anonymous: isAnonymous,
      user_id: user_id || null,
    };

    const { data: review, error: insertErr } = await supabase
      .from("product_reviews")
      .insert(insertPayload)
      .select()
      .single();

    if (insertErr) {
      console.error("Insert error:", insertErr);
      // Fallback: if review_id auto-generation conflict occurs, retry without explicit review_id
      const fallbackPayload = {
        product_id,
        rating: ratingNum,
        comment: cleanComment,
        review_text: cleanComment,
        reviewer_name: displayName,
        is_anonymous: isAnonymous,
        user_id: user_id || null,
      };

      const { data: review2, error: insertErr2 } = await supabase
        .from("product_reviews")
        .insert(fallbackPayload)
        .select()
        .single();

      if (insertErr2) {
        console.error("Retry insert error:", insertErr2);
        return jsonResp({ ok: false, error: insertErr2.message }, 500);
      }
      return jsonResp({ ok: true, review: review2 });
    }

    return jsonResp({ ok: true, review });
  } catch (err: any) {
    console.error("Unhandled error in submit-review:", err);
    return jsonResp({ ok: false, error: err?.message || "Internal server error" }, 500);
  }
};

// Support both native Deno.serve and standard serve fallback
if (typeof Deno !== "undefined" && typeof Deno.serve === "function") {
  Deno.serve(handler);
} else {
  serve(handler);
}

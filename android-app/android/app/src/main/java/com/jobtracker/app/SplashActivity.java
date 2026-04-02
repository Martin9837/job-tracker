package com.jobtracker.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Root frame
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#2563eb"));
        setContentView(root);

        // Logo canvas view
        LogoView logoView = new LogoView(this);
        FrameLayout.LayoutParams logoParams = new FrameLayout.LayoutParams(280, 280);
        logoParams.gravity = Gravity.CENTER;
        logoParams.bottomMargin = 80;
        root.addView(logoView, logoParams);

        // App name text
        TextView appName = new TextView(this);
        appName.setText("Job Tracker");
        appName.setTextColor(Color.WHITE);
        appName.setTextSize(32f);
        appName.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        appName.setAlpha(0f);
        appName.setLetterSpacing(-0.02f);

        FrameLayout.LayoutParams nameParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        nameParams.gravity = Gravity.CENTER;
        nameParams.topMargin = 200;
        root.addView(appName, nameParams);

        // Subtitle text
        TextView subtitle = new TextView(this);
        subtitle.setText("Field Service Manager");
        subtitle.setTextColor(Color.parseColor("#93c5fd"));
        subtitle.setTextSize(14f);
        subtitle.setAlpha(0f);

        FrameLayout.LayoutParams subParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        subParams.gravity = Gravity.CENTER;
        subParams.topMargin = 280;
        root.addView(subtitle, subParams);

        // Decorative circle top-right
        CircleView topCircle = new CircleView(this, Color.parseColor("#1d4ed8"), 0.12f);
        FrameLayout.LayoutParams tcParams = new FrameLayout.LayoutParams(500, 500);
        tcParams.gravity = Gravity.TOP | Gravity.END;
        tcParams.topMargin = -150;
        tcParams.rightMargin = -150;
        root.addView(topCircle, tcParams);

        // Decorative circle bottom-left
        CircleView bottomCircle = new CircleView(this, Color.parseColor("#1e40af"), 0.08f);
        FrameLayout.LayoutParams bcParams = new FrameLayout.LayoutParams(350, 350);
        bcParams.gravity = Gravity.BOTTOM | Gravity.START;
        bcParams.bottomMargin = -100;
        bcParams.leftMargin = -80;
        root.addView(bottomCircle, bcParams);

        // ── Animations ────────────────────────────────────────────────────
        logoView.setAlpha(0f);
        logoView.setScaleX(0.3f);
        logoView.setScaleY(0.3f);

        // Step 1: Logo bounces in (0ms)
        AnimatorSet logoAnim = new AnimatorSet();
        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(logoView, "alpha", 0f, 1f).setDuration(400);
        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(logoView, "scaleX", 0.3f, 1.08f, 1f).setDuration(600);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logoView, "scaleY", 0.3f, 1.08f, 1f).setDuration(600);
        logoAlpha.setInterpolator(new DecelerateInterpolator());
        logoScaleX.setInterpolator(new OvershootInterpolator(2f));
        logoScaleY.setInterpolator(new OvershootInterpolator(2f));
        logoAnim.playTogether(logoAlpha, logoScaleX, logoScaleY);
        logoAnim.setStartDelay(200);

        // Step 2: App name fades in (700ms)
        ObjectAnimator nameAnim = ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f);
        nameAnim.setDuration(500);
        nameAnim.setStartDelay(700);
        nameAnim.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator nameTranslate = ObjectAnimator.ofFloat(appName, "translationY", 30f, 0f);
        nameTranslate.setDuration(500);
        nameTranslate.setStartDelay(700);
        nameTranslate.setInterpolator(new DecelerateInterpolator());

        // Step 3: Subtitle fades in (950ms)
        ObjectAnimator subAnim = ObjectAnimator.ofFloat(subtitle, "alpha", 0f, 1f);
        subAnim.setDuration(400);
        subAnim.setStartDelay(950);
        subAnim.setInterpolator(new DecelerateInterpolator());

        // Step 4: Logo gentle pulse
        ObjectAnimator pulse = ObjectAnimator.ofFloat(logoView, "scaleX", 1f, 1.04f, 1f);
        ObjectAnimator pulseY = ObjectAnimator.ofFloat(logoView, "scaleY", 1f, 1.04f, 1f);
        pulse.setDuration(1200);
        pulseY.setDuration(1200);
        pulse.setStartDelay(900);
        pulseY.setStartDelay(900);
        pulse.setRepeatCount(1);
        pulseY.setRepeatCount(1);
        pulse.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseY.setInterpolator(new AccelerateDecelerateInterpolator());

        // Play all
        AnimatorSet all = new AnimatorSet();
        all.playTogether(logoAnim, nameAnim, nameTranslate, subAnim, pulse, pulseY);
        all.start();

        // Navigate to MainActivity after 2.4s
        all.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler().postDelayed(() -> {
                    // Fade out
                    root.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }).start();
                }, 400);
            }
        });
    }

    // ── Custom Logo View ──────────────────────────────────────────────────
    static class LogoView extends View {
        private final Paint briefcasePaint;
        private final Paint checkPaint;
        private float drawProgress = 1f;

        public LogoView(android.content.Context ctx) {
            super(ctx);
            briefcasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            briefcasePaint.setStyle(Paint.Style.STROKE);
            briefcasePaint.setColor(Color.WHITE);
            briefcasePaint.setStrokeWidth(12f);
            briefcasePaint.setStrokeJoin(Paint.Join.ROUND);
            briefcasePaint.setStrokeCap(Paint.Cap.ROUND);

            checkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            checkPaint.setStyle(Paint.Style.STROKE);
            checkPaint.setColor(Color.parseColor("#93c5fd"));
            checkPaint.setStrokeWidth(13f);
            checkPaint.setStrokeJoin(Paint.Join.ROUND);
            checkPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth(), h = getHeight();
            float cx = w / 2f, cy = h / 2f;
            float s = Math.min(w, h) * 0.8f;

            // Briefcase body
            float bw = s * 0.76f, bh = s * 0.50f;
            float bx = cx - bw/2f, by = cy - bh/2f + s*0.05f;
            float r = s * 0.09f;
            RectF rect = new RectF(bx, by, bx+bw, by+bh);
            canvas.drawRoundRect(rect, r, r, briefcasePaint);

            // Handle
            float hw = bw * 0.36f, hh = s * 0.18f;
            float hx = cx - hw/2f;
            float hy = by - hh;
            float hr = s * 0.055f;
            RectF handle = new RectF(hx, hy, hx+hw, hy+hh);
            canvas.drawRoundRect(handle, hr, hr, briefcasePaint);

            // Checkmark
            float ck = s * 0.28f;
            float offy = s * 0.05f;
            Path path = new Path();
            path.moveTo(cx - ck*0.60f, cy + offy);
            path.lineTo(cx - ck*0.10f, cy + offy + ck*0.55f);
            path.lineTo(cx + ck*0.62f, cy + offy - ck*0.50f);
            canvas.drawPath(path, checkPaint);
        }
    }

    // ── Decorative circle ─────────────────────────────────────────────────
    static class CircleView extends View {
        private final Paint paint;
        CircleView(android.content.Context ctx, int color, float alpha) {
            super(ctx);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(color);
            paint.setAlpha((int)(alpha * 255));
            paint.setStyle(Paint.Style.FILL);
        }
        @Override protected void onDraw(Canvas canvas) {
            canvas.drawCircle(getWidth()/2f, getHeight()/2f, Math.min(getWidth(), getHeight())/2f, paint);
        }
    }
}

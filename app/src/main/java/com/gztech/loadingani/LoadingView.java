package com.gztech.loadingani;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by gengandre on 7/15/15.
 */
public class LoadingView extends LinearLayout implements Animator.AnimatorListener {
    private int aniDuration = 500;
    private float zoomScale = 1.3f;
    private ValueAnimator centralDotColorChangeToNormal;
    private View leftDot;
    private View centralDot;
    private View rightDot;
    private ValueAnimator leftDotColorChangeToNormal;
    private AnimatorSet aniSet;
    private ValueAnimator leftDotColorChange;
    private ValueAnimator rightDotColorChange;

    public LoadingView(Context context) {
        super(context);
        initView();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.loading_view, this);

        centralDot = rootView.findViewById(R.id.active_dot);
        leftDot = rootView.findViewById(R.id.dot_left);
        rightDot = rootView.findViewById(R.id.dot_right);
        startAniMoveCenterToLeft(centralDot).start();
    }

    private AnimatorSet startAniMoveRightToCenter(View rightDot) {
        int dotDiameter = (int) getResources().getDimension(R.dimen.loading_dot_size);
        int dotPadding = (int) getResources().getDimension(R.dimen.loading_dot_padding);
        AnimatorSet moveRightToCentral = new AnimatorSet();
        ObjectAnimator rightDotToCentral = ObjectAnimator
                .ofFloat(rightDot, "translationX", 0, -dotPadding - dotDiameter);

        rightDotColorChange = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(R.color.dot_normal), getResources().getColor(R.color.dot_active));
        final GradientDrawable rightDotBg = (GradientDrawable) rightDot.getBackground();
        rightDotColorChange.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {
                rightDotBg.setColor((Integer) animator.getAnimatedValue());
            }
        });
        rightDotColorChange.setDuration(aniDuration);
        rightDotColorChange.addListener(this);
        moveRightToCentral.play(rightDotColorChange).with(rightDotToCentral);
        return moveRightToCentral;
    }

    private AnimatorSet startAniMoveCenterToRight(View centralDot) {
        int dotDiameter = (int) getResources().getDimension(R.dimen.loading_dot_size);
        int dotPadding = (int) getResources().getDimension(R.dimen.loading_dot_padding);
        AnimatorSet moveCentralToRight = new AnimatorSet();
        ObjectAnimator xOriginToXMinLeft = ObjectAnimator
                .ofFloat(centralDot, "translationX", dotPadding + dotDiameter, dotPadding)
                .setDuration(aniDuration);
        ObjectAnimator leftDotYOriginToYDown = ObjectAnimator
                .ofFloat(centralDot, "translationY", 0, dotPadding + dotDiameter)
                .setDuration(aniDuration);
        moveCentralToRight.play(xOriginToXMinLeft).with(leftDotYOriginToYDown);
        ObjectAnimator xMinLeftToXRight = ObjectAnimator
                .ofFloat(centralDot, "translationX", dotPadding, 3 * dotPadding + 2 * dotDiameter)
                .setDuration(aniDuration);
        ObjectAnimator leftDotYDownToYUp = ObjectAnimator
                .ofFloat(centralDot, "translationY", dotPadding + dotDiameter, -3 * dotDiameter)
                .setDuration(aniDuration);
        ObjectAnimator leftDotZoomInX = ObjectAnimator
                .ofFloat(centralDot, "scaleX", 1, zoomScale)
                .setDuration(aniDuration/2);
        ObjectAnimator leftDotZoomInY = ObjectAnimator
                .ofFloat(centralDot, "scaleY", 1, zoomScale)
                .setDuration(aniDuration/2);

        ObjectAnimator leftDotZoomOutX = ObjectAnimator
                .ofFloat(centralDot, "scaleX", zoomScale, 1)
                .setDuration(aniDuration/2);
        ObjectAnimator leftDotZoomOutY = ObjectAnimator
                .ofFloat(centralDot, "scaleY", zoomScale, 1)
                .setDuration(aniDuration / 2);

        moveCentralToRight.play(xMinLeftToXRight).with(leftDotYDownToYUp).after(leftDotYOriginToYDown);
        moveCentralToRight.play(leftDotZoomInX).with(leftDotZoomInY).after(leftDotYOriginToYDown);
        moveCentralToRight.play(leftDotZoomOutX).with(leftDotZoomOutY).after(leftDotZoomInX);

        ObjectAnimator xRightToXOrigin = ObjectAnimator
                .ofFloat(centralDot, "translationX", 3 * dotPadding + 2 * dotDiameter, 2 * dotPadding + 2 * dotDiameter)
                .setDuration(aniDuration);
        ObjectAnimator leftDotYUpToYOrigin = ObjectAnimator
                .ofFloat(centralDot, "translationY", -3 * dotDiameter, 0)
                .setDuration(aniDuration);

        leftDotColorChangeToNormal = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(R.color.dot_active), getResources().getColor(R.color.dot_normal));
        final GradientDrawable backgroundDrawable = (GradientDrawable) leftDot.getBackground();
        leftDotColorChangeToNormal.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {
                backgroundDrawable.setColor((Integer) animator.getAnimatedValue());
                backgroundDrawable.setStroke((int) getResources().getDimension(R.dimen.loading_dot_stroke_width), getResources().getColor(R.color.dot_active));
            }

        });
        leftDotColorChangeToNormal.setDuration(aniDuration);
        moveCentralToRight.play(xRightToXOrigin).with(leftDotYUpToYOrigin).with(leftDotColorChangeToNormal).after(leftDotZoomOutX);
        leftDotColorChangeToNormal.addListener(this);
        return moveCentralToRight;
    }

    private AnimatorSet startAniMoveLeftToCenter(View leftDot) {
        AnimatorSet moveLeftTocenter = new AnimatorSet();
        int dotDiameter = (int) getResources().getDimension(R.dimen.loading_dot_size);
        int dotPadding = (int) getResources().getDimension(R.dimen.loading_dot_padding);
        ObjectAnimator leftDotToCentral = ObjectAnimator
                .ofFloat(leftDot, "translationX", 0, dotPadding + dotDiameter);
        leftDotColorChange = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(R.color.dot_normal), getResources().getColor(R.color.dot_active));
        final GradientDrawable background = (GradientDrawable) leftDot.getBackground();
        leftDotColorChange.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {
                background.setColor((Integer) animator.getAnimatedValue());
            }
        });
        leftDotColorChange.setDuration(aniDuration);
        leftDotColorChange.addListener(this);
        moveLeftTocenter.play(leftDotToCentral).with(leftDotColorChange);
        return moveLeftTocenter;
    }

    private AnimatorSet startAniMoveCenterToLeft(View centralDot) {
        int dotDiameter = (int) getResources().getDimension(R.dimen.loading_dot_size);
        int dotPadding = (int) getResources().getDimension(R.dimen.loading_dot_padding);
        AnimatorSet centerToLeftAni = new AnimatorSet();

        //move the central dot to lower right
        ObjectAnimator yOriginToYDown = ObjectAnimator
                .ofFloat(centralDot, "translationY", 0, dotPadding + dotDiameter)
                .setDuration(aniDuration);
        ObjectAnimator xOriginToXMinRight = ObjectAnimator
                .ofFloat(centralDot, "translationX", 0, dotPadding)
                .setDuration(aniDuration);
        centerToLeftAni.playTogether(yOriginToYDown, xOriginToXMinRight);

        //move the central dot to up left
        ObjectAnimator yDownToYUp = ObjectAnimator
                .ofFloat(centralDot, "translationY", dotPadding + dotDiameter, -3 * dotDiameter)
                .setDuration(aniDuration);
        ObjectAnimator xMinRightToXLeft = ObjectAnimator
                .ofFloat(centralDot, "translationX", dotPadding, -2 * dotPadding - dotDiameter)
                .setDuration(aniDuration);

        centerToLeftAni.play(yDownToYUp).with(xMinRightToXLeft).after(yOriginToYDown);


        ObjectAnimator zoomInX = ObjectAnimator
                .ofFloat(centralDot, "scaleX", 1, zoomScale)
                .setDuration(aniDuration/2);
        ObjectAnimator zoomInY = ObjectAnimator
                .ofFloat(centralDot, "scaleY", 1, zoomScale)
                .setDuration(aniDuration/2);

        ObjectAnimator zoomOutX = ObjectAnimator
                .ofFloat(centralDot, "scaleX", zoomScale, 1)
                .setDuration(aniDuration/2);
        ObjectAnimator zoomOutY = ObjectAnimator
                .ofFloat(centralDot, "scaleY", zoomScale, 1)
                .setDuration(aniDuration / 2);
        centerToLeftAni.play(zoomInX).with(zoomInY).after(yOriginToYDown);
        centerToLeftAni.play(zoomOutX).with(zoomOutY).after(zoomInX);

        ObjectAnimator yUpToYOrigin = ObjectAnimator
                .ofFloat(centralDot, "translationY", -3 * dotDiameter, 0)
                .setDuration(aniDuration);
        ObjectAnimator xLeftToLeftDotSpot = ObjectAnimator
                .ofFloat(centralDot, "translationX", -2 * Utilities.dpToPixels(getContext(), 10) - dotDiameter, -dotPadding - dotDiameter)
                .setDuration(aniDuration);
        centerToLeftAni.play(yUpToYOrigin).with(xLeftToLeftDotSpot);
        centerToLeftAni.play(yUpToYOrigin).after(xMinRightToXLeft);

        centralDotColorChangeToNormal = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(R.color.dot_active), getResources().getColor(R.color.dot_normal));
        final GradientDrawable centralDotBg = (GradientDrawable) centralDot.getBackground();
        centralDotColorChangeToNormal.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {
                centralDotBg.setColor((Integer) animator.getAnimatedValue());
                centralDotBg.setStroke((int) getResources().getDimension(R.dimen.loading_dot_stroke_width), getResources().getColor(R.color.dot_active));
            }

        });
        centralDotColorChangeToNormal.addListener(this);
        centerToLeftAni.play(centralDotColorChangeToNormal).after(zoomOutX);
        return centerToLeftAni;
    }

    @Override
    public void onAnimationStart(Animator animator) {
        if(animator == centralDotColorChangeToNormal){
            startAniMoveLeftToCenter(leftDot).start();
        }else if(animator == leftDotColorChangeToNormal){
            startAniMoveRightToCenter(rightDot).start();
        }
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        if(animator == rightDotColorChange){
            initDot(leftDot, true);
            initDot(rightDot, true);
            initDot(centralDot, false);
            startAniMoveCenterToLeft(centralDot).start();
        }else if(animator == leftDotColorChange){
            startAniMoveCenterToRight(leftDot).start();
        }
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    public void initDot(View dot, boolean normalColor){
        dot.setTranslationX(0);
        dot.setTranslationY(0);
        if(normalColor){
            GradientDrawable dotBg = (GradientDrawable) dot.getBackground();
            dotBg.setColor(getResources().getColor(R.color.dot_normal));
            dotBg.setStroke((int) getResources().getDimension(R.dimen.loading_dot_stroke_width), getResources().getColor(R.color.dot_active));
        }
        else{
            GradientDrawable dotBg = (GradientDrawable) dot.getBackground();
            dotBg.setColor(getResources().getColor(R.color.dot_active));
        }
    }
}

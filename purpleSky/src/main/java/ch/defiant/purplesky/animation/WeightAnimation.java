package ch.defiant.purplesky.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class WeightAnimation extends Animation {
    private final float mStartWeight;
    private final float mDeltaWeight;
    private final View mContent;

    public WeightAnimation(float startWeight, float endWeight, View v) {
        mStartWeight = startWeight;
        mDeltaWeight = endWeight - startWeight;
        mContent = v;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContent.getLayoutParams();
        lp.weight = (mStartWeight + (mDeltaWeight * interpolatedTime));
        mContent.setLayoutParams(lp);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
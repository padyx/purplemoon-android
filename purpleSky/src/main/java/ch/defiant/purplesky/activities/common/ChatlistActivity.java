package ch.defiant.purplesky.activities.common;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ch.defiant.purplesky.R;

public class ChatlistActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testlayout);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        final ImageView img = (ImageView) findViewById(R.id.image);

        WeightAnimation anim = new WeightAnimation(0, 1, img);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(1000);
        anim.setStartOffset(1000);
        img.startAnimation(anim);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeightAnimation anim = new WeightAnimation(1, 0, img);
                anim.setInterpolator(new DecelerateInterpolator());
                anim.setDuration(1000);
                img.startAnimation(anim);
            }
        });
    }

    private static class WeightAnimation extends Animation {
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
}
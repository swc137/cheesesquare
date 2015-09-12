package android.support.design.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;

/**
 * Created by Ted on 2015/9/12.
 */
@CoordinatorLayout.DefaultBehavior(CusImageButton.Behavior.class)
public class CusImageButton extends ImageButton {
    public CusImageButton(Context context) {
        super(context);
    }

    public CusImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CusImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public static class Behavior extends android.support.design.widget.CoordinatorLayout.Behavior<CusImageButton> {
        private static final boolean SNACK_BAR_BEHAVIOR_ENABLED;
        private Rect mTmpRect;
        private boolean mIsAnimatingOut;
        private float mTranslationY;

        public Behavior() {
        }

        public boolean layoutDependsOn(CoordinatorLayout parent, CusImageButton child, View dependency) {
            return SNACK_BAR_BEHAVIOR_ENABLED && dependency instanceof Snackbar.SnackbarLayout;
        }

        public boolean onDependentViewChanged(CoordinatorLayout parent, CusImageButton child, View dependency) {
            if(dependency instanceof AppBarLayout) {
                AppBarLayout appBarLayout = (AppBarLayout)dependency;
                if(this.mTmpRect == null) {
                    this.mTmpRect = new Rect();
                }

                Rect rect = this.mTmpRect;
                ViewGroupUtils.getDescendantRect(parent, dependency, rect);
                if(rect.bottom <= appBarLayout.getMinimumHeightForVisibleOverlappingContent()) {
                    if(!this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
                        this.animateOut(child);
                    }
                } else if(child.getVisibility() != View.VISIBLE) {
                    this.animateIn(child);
                }
            }

            return false;
        }

        private void animateIn(CusImageButton button) {
            button.setVisibility(View.VISIBLE);
            if(Build.VERSION.SDK_INT >= 14) {
                ViewCompat.animate(button).scaleX(1.0f).scaleY(1.0f).alpha(1.0F).setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR).withLayer()
                        .setListener(null)
                        .start();
            } else {
                Animation anim = android.view.animation.AnimationUtils.loadAnimation(button.getContext(), android.support.design.R.anim.fab_in);
                anim.setDuration(200L);
                anim.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
                button.startAnimation(anim);
            }

        }

        private void animateOut(final CusImageButton button) {
            if(Build.VERSION.SDK_INT >= 14) {
                // removed the scale X & Y to avoid strange animation behavior with the FAB menu
                ViewCompat.animate(button).scaleX(0).scaleY(0).alpha(0.0F).setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR).withLayer().setListener(new ViewPropertyAnimatorListener() {
                    public void onAnimationStart(View view) {
                        Behavior.this.mIsAnimatingOut = true;
                    }

                    public void onAnimationCancel(View view) {
                        Behavior.this.mIsAnimatingOut = false;
                    }

                    public void onAnimationEnd(View view) {
                        Behavior.this.mIsAnimatingOut = false;
                        view.setVisibility(View.GONE);
                    }
                }).start();
            } else {
                Animation anim = android.view.animation.AnimationUtils.loadAnimation(button.getContext(), android.support.design.R.anim.fab_out);
                anim.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
                anim.setDuration(200L);
                anim.setAnimationListener(new AnimationUtils.AnimationListenerAdapter() {
                    public void onAnimationStart(Animation animation) {
                        Behavior.this.mIsAnimatingOut = true;
                    }

                    public void onAnimationEnd(Animation animation) {
                        Behavior.this.mIsAnimatingOut = false;
                        button.setVisibility(View.GONE);
                    }
                });
                button.startAnimation(anim);
            }
        }

        static {
            SNACK_BAR_BEHAVIOR_ENABLED = Build.VERSION.SDK_INT >= 11;
        }
    }
}



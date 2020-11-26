package inesc_id.pt.motivandroid.myTrips;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.widget.OverScroller;

import java.lang.reflect.Field;
import java.util.logging.Logger;


/**
 * MyNestedScrollView
 *
 * Workaround to allow scrolling in a fragment containing a map
 *
 * Taken from https://stackoverflow.com/questions/31829976/onclick-method-not-working-properly-after-nestedscrollview-scrolled
 *  answer from user DustinB
 *
 * */
public class MyNestedScrollView extends NestedScrollView {

//    private static final Logger sLogger = Loog.getLogger(MyNestedScrollView.class);

    private OverScroller mScroller;
    public boolean isFling = false;

    public MyNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = getOverScroller();
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);

        // here we effectively extend the super class functionality for backwards compatibility and just call invalidateOnAnimation()
        if (getChildCount() > 0) {
            ViewCompat.postInvalidateOnAnimation(this);

            // Initializing isFling to true to track fling action in onScrollChanged() method
            isFling = true;
        }
    }

    @Override
    protected void onScrollChanged(int l, final int t, final int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (isFling) {
            if (Math.abs(t - oldt) <= 3 || t == 0 || t == (getChildAt(0).getMeasuredHeight() - getMeasuredHeight())) {
                isFling = false;

                // This forces the mFinish variable in scroller to true (as explained the
                //    mentioned link above) and does the trick
                if (mScroller != null) {
                    mScroller.abortAnimation();
                }
            }
        }
    }

    private OverScroller getOverScroller() {
        Field fs = null;
        try {
            fs = this.getClass().getSuperclass().getDeclaredField("mScroller");
            fs.setAccessible(true);
            return (OverScroller) fs.get(this);
        } catch (Throwable t) {
            return null;
        }
    }
}
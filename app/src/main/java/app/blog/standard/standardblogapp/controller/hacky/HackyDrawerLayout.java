package app.blog.standard.standardblogapp.controller.hacky;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * A Hacky Drawer Layout to prevent a bug with the Photoview
 *
 * @author victor
 */
public class HackyDrawerLayout extends DrawerLayout{

    public HackyDrawerLayout(Context context) {
        super(context);
    }

    public HackyDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HackyDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

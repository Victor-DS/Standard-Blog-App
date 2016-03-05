package app.blog.standard.standardblogapp.controller.hacky;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import app.blog.standard.standardblogapp.model.util.Util;

/**
 * Hacky class to help detect a click from a touch.
 *
 * @author victor
 */
public class HackyClickListener implements View.OnTouchListener{

    /**
     * Max allowed duration for a "click", in milliseconds.
     */
    private static final int MAX_CLICK_DURATION = 1000;

    /**
     * Max allowed distance to move during a "click", in DP.
     */
    private static final int MAX_CLICK_DISTANCE = 15;

    private long pressStartTime;
    private float pressedX;
    private float pressedY;
    private boolean stayedWithinClickDistance;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                pressStartTime = System.currentTimeMillis();
                pressedX = motionEvent.getX();
                pressedY = motionEvent.getY();
                stayedWithinClickDistance = true;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (stayedWithinClickDistance
                        && distance(pressedX, pressedY, motionEvent.getX(), motionEvent.getY())
                        > MAX_CLICK_DISTANCE) {
                    stayedWithinClickDistance = false;
                }
                return false;
            }
            case MotionEvent.ACTION_UP: {
                long pressDuration = System.currentTimeMillis() - pressStartTime;
                if (pressDuration < MAX_CLICK_DURATION && stayedWithinClickDistance) {
                    return OnClickListener();
                }
            }
        }
        return false;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return Util.pxToDp(distanceInPx);
    }

    public boolean OnClickListener() {
        return false;
    }
}

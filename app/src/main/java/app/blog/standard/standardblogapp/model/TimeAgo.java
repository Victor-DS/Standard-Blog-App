package app.blog.standard.standardblogapp.model;

import app.blog.standard.standardblogapp.R;

/**
 * @author victor
 */
public class TimeAgo {

    public static final int HOURS = 0;
    public static final int DAYS = 1;

    private long time;
    private int type;

    public TimeAgo(long time, int type) {
        this.time = time;
        this.type = type;
    }

    public TimeAgo() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStringInt() {
        if(this.type == HOURS)
            return R.string.hours_ago;
        return R.string.days_ago;
    }

    private boolean isHours() {
        return this.type == HOURS;
    }
}

package app.blog.standard.standardblogapp.model.util;

/**
 * @author victor
 */
public class AlarmHelper {

    private static final int MINUTES = 60 * 1000;
    private static final int HOURS = MINUTES * 60;
    private static final int MINUTES_AFTER_BOOT = 5 * MINUTES;

    public static final int NEVER = 0;

    public static int getSyncInterval() {
        int frequency = PreferenceHelper.syncFrequency();

        if(frequency == NEVER) return NEVER;

        return (24 / frequency) * HOURS;
    }

    public static int waitAfterBoot() {
        return MINUTES_AFTER_BOOT;
    }

}

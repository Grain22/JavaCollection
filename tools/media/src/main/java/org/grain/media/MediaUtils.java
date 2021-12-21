package org.grain.media;

import javafx.util.Duration;

/**
 * @author wulifu
 */
public class MediaUtils {
    public static String DurationToString(Duration duration){
        return durationToString((int) duration.toSeconds());
    }

    public static String durationToString(int time) {
        int hour = time /3600;
        int minute = (time-hour*3600)/60;
        int second = time %60;
        return hour + ":" + minute + ":" + second;
    }
}

package xyz.akiradev.playerperks;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static long createCooldown(int days, int hours, int minutes, int seconds) {
        return Instant.now().getEpochSecond() + (days * 86400L) + (hours * 3600L) + (minutes * 60L) + seconds;
    }

    public static String calculateTime(long seconds){
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24L);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
        return day + "Days " + hours + "Hours " + minute + "Minutes " + second + "Seconds";
    }

}

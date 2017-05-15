package in.mobifirst.tagtree.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static String getDate(long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
        return sfd.format(new Date(timestamp));
    }

    public static String getTime(long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm:ss");
        return sfd.format(new Date(timestamp));
    }

    public static String getDateTime(long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sfd.format(new Date(timestamp));
    }

    public static String getDuration(long timestamp) {
        String format = String.format("%%0%dd", 2);
        timestamp = timestamp / 1000;
        String seconds = String.format(format, timestamp % 60);
        String minutes = String.format(format, (timestamp % 3600) / 60);
        String hours = String.format(format, timestamp / 3600);
        String time = hours + ":" + minutes + ":" + seconds;
        return time;
    }

    public static String getDurationInHrsAndMin(int timeDuration) {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm a");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        calendar.add(Calendar.MINUTE, timeDuration);
        return sfd.format(calendar.getTime());
    }
}

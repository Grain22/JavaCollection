package org.grain.tools.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.GregorianCalendar;

/**
 * @author laowu
 * @version 4/12/2019 11:26 AM
 */
public class DateUtils {

    /**
     * get NOW time string
     *
     * @return yyyyMMdd
     */
    public static String getTimeString() {
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(new GregorianCalendar().getTime());
    }

    /**
     * get NOW time string
     *
     * @return yyyyMMddHHmmssSSS
     */
    public static String getTimeStringLong() {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return format.format(new GregorianCalendar().getTime());
    }


    /**
     * month name
     *
     * @param month month 1-12
     * @return JANUARY
     */
    public static String getMonthName(int month) {
        return Month.of(month).name();
    }
}

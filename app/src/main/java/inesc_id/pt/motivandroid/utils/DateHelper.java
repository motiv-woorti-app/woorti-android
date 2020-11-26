package inesc_id.pt.motivandroid.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigest;

/**
 *
 * DateHelper
 *
 *  Utility functions to do date related calculations
 *
 * (C) 2017-2020 - The Woorti app is a research (non-commercial) application that was
 * developed in the context of the European research project MoTiV (motivproject.eu). The
 * code was developed by partner INESC-ID with contributions in graphics design by partner
 * TIS. The Woorti app development was one of the outcomes of a Work Package of the MoTiV
 * project.
 * The Woorti app was originally intended as a tool to support data collection regarding
 * mobility patterns from city and country-wide campaigns and provide the data and user
 * management to campaign managers.
 *
 * The Woorti app development followed an agile approach taking into account ongoing
 * feedback of partners and testing users while continuing under development. This has
 * been carried out as an iterative process deploying new app versions. Along the
 * timeline, various previously unforeseen requirements were identified, some requirements
 * Were revised, there were requests for modifications, extensions, or new aspects in
 * functionality or interaction as found useful or interesting to campaign managers and
 * other project partners. Most stemmed naturally from the very usage and ongoing testing
 * of the Woorti app. Hence, code and data structures were successively revised in a
 * way not only to accommodate this but, also importantly, to maintain compatibility with
 * the functionality, data and data structures of previous versions of the app, as new
 * version roll-out was never done from scratch.
 * The code developed for the Woorti app is made available as open source, namely to
 * contribute to further research in the area of the MoTiV project, and the app also makes
 * use of open source components as detailed in the Woorti app license.
 * This project has received funding from the European Union’s Horizon 2020 research and
 * innovation programme under grant agreement No. 770145.
 * This file is part of the Woorti app referred to as SOFTWARE.
 * */
public class DateHelper {


    /**
     * @param ts
     * @return date in "MMMM d, yyyy 'at' h:mm:ss a" format from the provided timestamp ts
     */
    public static String getDateFromTSString(long ts){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
        return simpleDateFormat.format(ts);
    }

//    public static String getDateFromTSString(float ts){
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
//        return simpleDateFormat.format(ts);
//    }


    /**
     * @param millis
     * @return date in "HH:MM:SS" from provided timestamp in milliseconds millis
     */
    public static String getHMSfromMS(long millis) {

        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

    }


    /**
     * @param ts
     * @return date in HH:mm format from provided timestamp ts in milliseconds
     */
    public static String getHoursMinutesFromTSString(long ts){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(ts);
    }

    /**
     * @param ts
     * @return date in yyyy-MM-dd format from provided timestamp ts in milliseconds
     */
    public static String getYearMonthDayFromTSString(long ts){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(ts);
    }

    /**
     * @param time
     * @return minutes and secons in "mm:ss" format from timestamp time in milliseconds
     */
    public static String getMinutesAndSecondsFromSeconds(double time){

        double timeTemp = time;

        int i=0;
        while(timeTemp>60){

            timeTemp -= 60;

            i++;
        }

        return i + ":" + (int) timeTemp;

    }

//    public static Date getDateFromRRTimeAndDate(String departureDateRouteRank, String departureTimeRouteRank){
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        try {
//            return simpleDateFormat.parse(departureDateRouteRank+" "+departureTimeRouteRank);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return new Date();
//    }

    /**
     * @param ts1
     * @param ts2
     * @return true if ts1 and ts2 belong to the same day, false otherwise
     */
    public static boolean isSameDay(long ts1, long ts2) {

        Date date1 = new Date(ts1);
        Date date2 = new Date(ts2);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        boolean sameYear = calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR);
        boolean sameMonth = calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);
        boolean sameDay = calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
        return (sameDay && sameMonth && sameYear);
    }


    /**
     * @param now
     * @param finalDate
     * @return amount of days between provided now and finalDate timestamps
     */
    public static long getDaysUntilDate(long now, long finalDate){

        if (now > finalDate) return 0;

        long diff = finalDate - now;

        if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) == 0){
            return 1;
        }

        return (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1);


    }

    /**
     * compute ammount of days with validated trips by the user
     *
     * @param digests
     * @return amount of unique days with validated trips from the digest list provided
     */
    public static int getUniqueDaysWithValidatedTrips(ArrayList<FullTripDigest> digests){

        int numDays = 0;

        long lastTS = 0l;

        for (FullTripDigest digest : digests){
            if(digest.isValidated()) {

                if(lastTS == 0l){
                    lastTS = digest.getInitTimestamp();
                    numDays++;
                }else{
                    if(!DateHelper.isSameDay(lastTS, digest.getInitTimestamp())) {
                        numDays++;
                        lastTS = digest.getInitTimestamp();
                    }
                }
            }
        }

//        Log.e("DashboardFragment", "unique days with trips " + numDays);

        return numDays;
    }

    /**
     * @param durationMillis
     * @return amount of hours from provided duration in milliseconds durationMillis
     */
    public static double getHoursFromMillis(long durationMillis){

        return NumbersUtil.roundToOneDecimalPlace(durationMillis/1000.0/60.0/60.0);

    }

}

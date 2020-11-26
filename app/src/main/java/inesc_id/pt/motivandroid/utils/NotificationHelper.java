package inesc_id.pt.motivandroid.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.home.activities.HomeDrawerActivity;

/**
 *
 * NotificationHelper
 *
 *  Utility functions to create notification channels and issue notifications
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
 */
public class NotificationHelper {

    // The id of the channel
    public static final String PRIMARY_NOTIF_CHANNEL = "default";
    public static final String SECONDARY_NOTIF_CHANNEL = "default_secondary";
    public static final String SURVEY_NOTIF_CHANNEL = "default_survey";

    public static final int PRIMARY_FOREGROUND_NOTIF_SERVICE_ID = 1001;
    public static final int SECONDARY_FOREGROUND_NOTIF_SERVICE_ID = 1002;
    public static final int SURVEY_NOTIF_SERVICE_ID = 1003;

    // The user-visible name of the channel
    public static final CharSequence name = "Motiv notification channel";

    // Configure the notification channel.
    public static final String description = "Motiv notification channel";


    public static PendingIntent buildPendingIntentTripEnded(Context context, String tripID){

        Intent goFromNotificationToValidation = new Intent(context, HomeDrawerActivity.class);
//        goFromNotificationToValidation.putExtra("FullTripToBeValidated", tripID);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                goFromNotificationToValidation, PendingIntent.FLAG_UPDATE_CURRENT);

        return contentIntent;

    }

    private static PendingIntent buildPendingIntentNotification(Context context, int code){

        Intent goFromNotificationToMenus = new Intent(context, HomeDrawerActivity.class);
//        goFromNotificationToValidation.putExtra("FullTripToBeValidated", tripID);

        goFromNotificationToMenus.putExtra(keys.NOTIFICATION_CODE_PARAMETER, code);
        goFromNotificationToMenus.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                goFromNotificationToMenus, PendingIntent.FLAG_UPDATE_CURRENT);

        return contentIntent;

    }

    @RequiresApi(Build.VERSION_CODES.O)
    public static void createChannel(Context context){

        NotificationManager
                mNotificationManager =
                (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = new NotificationChannel(PRIMARY_NOTIF_CHANNEL, name, NotificationManager.IMPORTANCE_HIGH);

        mChannel.setDescription(description);
        mNotificationManager.createNotificationChannel(mChannel);

    }



    public static Notification buildForegroundNotification(Context context, String contentTitle, String contentText, PendingIntent action) {

        NotificationCompat.Builder b=new NotificationCompat.Builder(context,PRIMARY_NOTIF_CHANNEL);

        b.setOngoing(true)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_woorti_notification_icon)
                .setColor(context.getResources().getColor(R.color.colorOrangeTripPolyline));
//                .setContentIntent(resultPendingIntent);

        if(contentTitle != null){
            b.setContentTitle(contentTitle);
        }

        if(action != null){
            b.setContentIntent(action);
        }

        return(b.build());
    }

    public static void issueNotification(Context context, Notification notification){

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(PRIMARY_FOREGROUND_NOTIF_SERVICE_ID,notification);

    }

    //////////////SECONDARY - FOR SURVEYS AND ENGAGEMENT NOTIFICATIONS

    @RequiresApi(Build.VERSION_CODES.O)
    private static void createSecondaryChannel(Context context){

        NotificationManager
                mNotificationManager =
                (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = new NotificationChannel(SECONDARY_NOTIF_CHANNEL, name, NotificationManager.IMPORTANCE_HIGH);

        mChannel.setDescription(description);
        mNotificationManager.createNotificationChannel(mChannel);

    }

    private static Notification buildSecondaryNotification(Context context, String contentTitle, String contentText, PendingIntent action) {

        NotificationCompat.Builder b=new NotificationCompat.Builder(context,SECONDARY_NOTIF_CHANNEL);

        b.setOngoing(false)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_woorti_notification_icon)
                .setColor(context.getResources().getColor(R.color.colorOrangeTripPolyline));
//                .setContentIntent(resultPendingIntent);

        if(contentTitle != null){
            b.setContentTitle(contentTitle);
        }

        if(action != null){
            b.setContentIntent(action);
        }

        return(b.build());
    }

    private static void issueSecondaryNotification(Context context, Notification notification){

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(SECONDARY_FOREGROUND_NOTIF_SERVICE_ID,notification);

    }

    /////Survey notifications

    @RequiresApi(Build.VERSION_CODES.O)
    private static void createSurveySecondaryChannel(Context context){

        NotificationManager
                mNotificationManager =
                (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = new NotificationChannel(SURVEY_NOTIF_CHANNEL, name, NotificationManager.IMPORTANCE_HIGH);

        mChannel.setDescription(description);
        mNotificationManager.createNotificationChannel(mChannel);

    }

    private static Notification buildSecondarySurveyNotification(Context context, String contentTitle, String contentText, PendingIntent action) {

        NotificationCompat.Builder b=new NotificationCompat.Builder(context,SURVEY_NOTIF_CHANNEL);

        b.setOngoing(false)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_woorti_notification_icon)
                .setColor(context.getResources().getColor(R.color.colorOrangeTripPolyline));
//                .setContentIntent(resultPendingIntent);

        if(contentTitle != null){
            b.setContentTitle(contentTitle);
        }

        if(action != null){
            b.setContentIntent(action);
        }

        return(b.build());
    }

    private static void issueSecondarySurveyNotification(Context context, Notification notification){

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(SURVEY_NOTIF_SERVICE_ID,notification);

    }

    public static void showNotificationState(String title, String text, Context context, int code){

//        PendingIntent contentIntent = NotificationHelper.buildPendingIntentTripEnded(context, "");
        PendingIntent contentIntent = NotificationHelper.buildPendingIntentNotification(context, code);

        Notification engagementNotification = NotificationHelper.buildSecondaryNotification(
                context,
                title,
                text,
                contentIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationHelper.createSecondaryChannel(context);
        }

        NotificationHelper.issueSecondaryNotification(context, engagementNotification);
    }

    public static void showNotificationSurveyState(Context context, int points){

//        PendingIntent contentIntent = NotificationHelper.buildPendingIntentTripEnded(context, "");
        PendingIntent contentIntent = NotificationHelper.buildPendingIntentNotification(context, -1);

        Notification engagementNotification = NotificationHelper.buildSecondarySurveyNotification(
                context,
                context.getString(R.string.You_Have_A_New_Survey),
                context.getString(R.string.Fill_It_And_Earn_XX_Points, points),
                contentIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationHelper.createSurveySecondaryChannel(context);
        }

        NotificationHelper.issueSecondarySurveyNotification(context, engagementNotification);
    }

    public interface keys{

        String NOTIFICATION_CODE_PARAMETER = "NotificationCode";

    }


}

package inesc_id.pt.motivandroid.data.engagementNotifications;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.home.activities.HomeDrawerActivity;

/**
 *
 * EngagementNotification
 *
 *  Data structure for engagement notifications. These notifications are meant to be shown to the
 * user periodically. "sent" field is equal to 0l if the notification hasn't yet been shown to the
 * user, otherwise is equal to the timestamp at which the notification was sent to the user.
 *
 *  * (C) 2017-2020 - The Woorti app is a research (non-commercial) application that was
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

public class EngagementNotification implements Serializable{

    public EngagementNotification(String title, String text, long sent) {
        this.title = title;
        this.text = text;
        this.sent = sent;
    }

    @Expose
    private String title;

    @Expose
    private String text;

    @Expose
    private long sent;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getSent() {
        return sent;
    }

    public void setSent(long sent) {
        this.sent = sent;
    }


    public static String getTitle(int id, Context context){

        switch (id){
            case 0:
                return context.getString(R.string.engagement_notification_1_title);
            case 1:
                return context.getString(R.string.engagement_notification_2_title);
            case 2:
                return context.getString(R.string.engagement_notification_4_title);
            case 3:
                return context.getString(R.string.engagement_notification_3_title);
            case 4:
                return context.getString(R.string.engagement_notification_5_title);
            case 5:
                return context.getString(R.string.engagement_notification_6_title);
            case 6:
                return context.getString(R.string.engagement_notification_7_title);
            case 7:
                return context.getString(R.string.engagement_notification_8_title);
            case 8:
                return context.getString(R.string.engagement_notification_9_title);
            default:
                return context.getString(R.string.engagement_notification_1_title);

        }

    }

    public static String getContent(int id, Context context){

        switch (id){
            case 0:
                return context.getString(R.string.engagement_notification_1_content);
            case 1:
                return context.getString(R.string.engagement_notification_2_content);
            case 2:
                return context.getString(R.string.engagement_notification_4_content);
            case 3:
                return context.getString(R.string.engagement_notification_3_content);
            case 4:
                return context.getString(R.string.engagement_notification_5_content);
            case 5:
                return context.getString(R.string.engagement_notification_6_content);
            case 6:
                return context.getString(R.string.engagement_notification_7_content);
            case 7:
                return context.getString(R.string.engagement_notification_8_content);
            case 8:
                return context.getString(R.string.engagement_notification_9_content);
            default:
                return context.getString(R.string.engagement_notification_1_content);

        }

    }


    /**
     * @param id of the notification being shown
     * @return the menu to open upon clicking the notification
     */
    public static int getNotificationIntentCode(int id){

        switch (id){
            case 0:
                return HomeDrawerActivity.keys.MY_TRIPS;
            case 1:
                return HomeDrawerActivity.keys.MOBILITY_COACH;
            case 2:
                return HomeDrawerActivity.keys.MY_TRIPS;
            case 3:
                return HomeDrawerActivity.keys.DASHBOARD;
            case 4:
                return HomeDrawerActivity.keys.DASHBOARD;
            case 5:
                return HomeDrawerActivity.keys.MY_TRIPS;
            case 6:
                return HomeDrawerActivity.keys.DASHBOARD;
            case 7:
                return HomeDrawerActivity.keys.MY_TRIPS;
            case 8:
                return HomeDrawerActivity.keys.DASHBOARD;
            default:
                return HomeDrawerActivity.keys.HOME;

        }

    }

    public static ArrayList<EngagementNotification> getEngagementNotifications(Context context){

        ArrayList<EngagementNotification> result = new ArrayList<>();

        result.add(new EngagementNotification(context.getString(R.string.engagement_notification_1_title), context.getString(R.string.engagement_notification_1_content), 0l));
        result.add(new EngagementNotification(context.getString(R.string.engagement_notification_2_title), context.getString(R.string.engagement_notification_2_content), 0l));
        result.add(new EngagementNotification(context.getString(R.string.engagement_notification_4_title), context.getString(R.string.engagement_notification_4_content), 0l));

        //

        result.add(new EngagementNotification(context.getString(R.string.engagement_notification_3_title), context.getString(R.string.engagement_notification_3_content), 0l));
        result.add(new EngagementNotification(context.getString(R.string.engagement_notification_5_title), context.getString(R.string.engagement_notification_5_content), 0l));
        result.add(new EngagementNotification(context.getString(R.string.engagement_notification_6_title), context.getString(R.string.engagement_notification_6_content), 0l));
        result.add(new EngagementNotification(context.getString(R.string.engagement_notification_7_title), context.getString(R.string.engagement_notification_7_content), 0l));
        result.add(new EngagementNotification(context.getString(R.string.engagement_notification_8_title), context.getString(R.string.engagement_notification_8_content), 0l));
        result.add(new EngagementNotification(context.getString(R.string.engagement_notification_9_title), context.getString(R.string.engagement_notification_9_content), 0l));






        return result;

    }

    /**
     * @param engagementNotifications user's engagement notification data
     * @return which is the next notification
     */
    public static Integer getNotificationToBeChecked(ArrayList<EngagementNotification> engagementNotifications){

        Integer i = 0;

        for (EngagementNotification engagementNotification : engagementNotifications){

            if (engagementNotification.getSent() == 0l){
                return i;
            }

            i++;
        }

        return null;

    }

}

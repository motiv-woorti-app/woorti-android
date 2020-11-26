package inesc_id.pt.motivandroid.managers;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.WorthwhilenessCO2Score;
import inesc_id.pt.motivandroid.data.engagementNotifications.EngagementNotification;
import inesc_id.pt.motivandroid.data.stories.StoryStateful;
import inesc_id.pt.motivandroid.data.surveys.Survey;
import inesc_id.pt.motivandroid.data.surveys.SurveyStateful;
import inesc_id.pt.motivandroid.data.surveys.triggers.TimedRecurringTrigger;
import inesc_id.pt.motivandroid.data.surveys.triggers.TimedTrigger;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigest;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.surveyNotification.SurveyManager;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.NotificationHelper;

/**
 *
 * EngagementManager
 *
 *   Singleton class to manage engagement actions.
 *   Checks if there are any pending notifications to se sent to the user, and if so show the
 *   notification.
 *   Also checks if there are any surveys that should already have been triggered, and if so trigger
 *   them.
 *   Checks as well if there is a story that needs to made available to the user.
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

public class EngagementManager {

    private static final Logger LOG = LoggerFactory.getLogger(EngagementManager.class.getSimpleName());

    private static EngagementManager instance;

    public static EngagementManager getInstance() {

        if(instance == null){
            instance = new EngagementManager();
        }
        return instance;
    }

    public synchronized void checkEngagement(Context context, boolean fromTripStart, boolean fromTripEnd){

        PersistentTripStorage persistentTripStorage = new PersistentTripStorage(context);

        ArrayList<Survey> surveys = persistentTripStorage.getAllSurveysObject();

        long lastTimeChecked = SharedPreferencesUtil.getLastCheckedNotificationsSurveys(context);

        LOG.debug("Last timestamp check: " + lastTimeChecked);

        checkSurveys(surveys, lastTimeChecked, context, persistentTripStorage);

        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(context, "");

        if(userSettingStateWrapper != null && userSettingStateWrapper.getUserSettings() != null){

            checkForPendingNotifications(userSettingStateWrapper, context, fromTripEnd, fromTripStart);
            SharedPreferencesUtil.setLastCheckedNotificationsSurveysTimestamp(context, DateTime.now().getMillis());

        }

    }

    UserSettingStateWrapper userSettingStateWrapper;
//    ArrayList<EngagementNotification> notificationData;

    ArrayList<EngagementNotification> notifications;

    private synchronized void checkForPendingNotifications(UserSettingStateWrapper userSettingStateWrapper, Context context, boolean fromEndTrip, boolean fromStartTrip) {

        notifications = userSettingStateWrapper.getUserSettings().getEngagementNotifications();

        ArrayList<EngagementNotification> notificationData = EngagementNotification.getEngagementNotifications(context);

        //if null, hasnt been checked, add notifications to user settings
        if (notifications == null){

            LOG.debug("Notification array = null -> adding notifications to user settings ");

            userSettingStateWrapper.getUserSettings().setEngagementNotifications(EngagementNotification.getEngagementNotifications(context));
            notifications = userSettingStateWrapper.getUserSettings().getEngagementNotifications();

            SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapper, true);

            //let's check if there are new notifications
        }else if(notifications.size() < notificationData.size()){

            LOG.debug("Notification array != null but there are new notifications");


            for (int i = notifications.size() ; i < notificationData.size(); i++){
                notifications.add(notificationData.get(i));
            }

            SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapper, true);




        }

        Integer indexToBeChecked = EngagementNotification.getNotificationToBeChecked(notifications);


        for (EngagementNotification not: notifications){

            LOG.error("User Data" + not.getTitle() + " " + DateHelper.getDateFromTSString(not.getSent()));

        }

        //not all notifications have been shown
        if(indexToBeChecked != null){

            LOG.debug("Not all notifications have been shown");

            EngagementNotification notificationToBeChecked = notifications.get(indexToBeChecked);

            switch (indexToBeChecked){

                //check if there if was first trip
                case 0:

                    LOG.debug("Index to be checked -> 0");

                    if(fromEndTrip){

                        LOG.debug("fromEndTrip = true -> let's show notification");

                        notificationToBeChecked.setSent(DateTime.now().getMillis());

                        NotificationHelper.showNotificationState(EngagementNotification.getTitle(indexToBeChecked, context), EngagementNotification.getContent(indexToBeChecked, context), context, EngagementNotification.getNotificationIntentCode(indexToBeChecked));
                        SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapper, true);
                    }

                    break;
                case 1:
                case 2:

                    LOG.debug("Index to be checked -> 1,2");

                    //check for pending

                    boolean pendingTrips = checkIfPendingTrips(context);
                    LOG.debug("PendingTrips = " + pendingTrips);
                    boolean pendingStories = checkIfPendingStories(userSettingStateWrapper);
                    LOG.debug("PendingStories = " + pendingTrips);
                    long lastSentNotification = notifications.get(indexToBeChecked-1).getSent();

                    LOG.debug("LastSentNotification = " + DateHelper.getDateFromTSString(lastSentNotification));

                    long dayAfterLastSentNotification = lastSentNotification + 24*60*60*1000;

                    LOG.debug("LastSentNotification = " + DateHelper.getDateFromTSString(dayAfterLastSentNotification));


                    if(pendingStories && pendingTrips && dayAfterLastSentNotification < DateTime.now().getMillis()){

                        LOG.debug("All conditions passed - lets show notification # " + indexToBeChecked);

                        notificationToBeChecked.setSent(DateTime.now().getMillis());
                        NotificationHelper.showNotificationState(EngagementNotification.getTitle(indexToBeChecked, context), EngagementNotification.getContent(indexToBeChecked, context), context, EngagementNotification.getNotificationIntentCode(indexToBeChecked));
                        SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapper, true);

                    }

                    break;

                case 3:

                    LOG.debug("Index to be checked -> 3 - loop starts here");
                    checkAndSendThirdNotification(context, indexToBeChecked);
                    break;

                case 4:
                case 5:
                case 6:
                case 7:
                    LOG.debug("Index to be checked -> 4,5,6,7");
                    checkAndSend4567Notification(context, indexToBeChecked);
                    break;

                case 8:
                    LOG.debug("Index to be checked -> 8 (last)");
                    checkAndSendLastNotification(context, indexToBeChecked);

                    break;



            }



        }

    }

    private synchronized void checkAndSendThirdNotification(Context context, int indexToBeChecked) {

        //check for pending

        boolean pendingTrips = checkIfPendingTrips(context);
        LOG.debug("PendingTrips = " + pendingTrips);
        boolean pendingStories = checkIfPendingStories(userSettingStateWrapper);
        LOG.debug("PendingStories = " + pendingTrips);

        //check if we already shown last notification
        EngagementNotification lastNotification = notifications.get(notifications.size()-1);

        long lastSentNotification;
        boolean needToSetLastNotificationToZero;

        if(lastNotification.getSent() == 0l){
            LOG.debug("Last notification not shown, index to check is the one before (2)");
            lastSentNotification = notifications.get(indexToBeChecked-1).getSent();
            needToSetLastNotificationToZero = false;
        }else{
            lastSentNotification = lastNotification.getSent();
            needToSetLastNotificationToZero = true;
        }

        LOG.debug("LastSentNotification = " + DateHelper.getDateFromTSString(lastSentNotification));

        long dayAfterLastSentNotification = lastSentNotification + 24*60*60*1000;

        LOG.debug("LastSentNotification = " + DateHelper.getDateFromTSString(dayAfterLastSentNotification));

        EngagementNotification notificationToBeChecked = notifications.get(indexToBeChecked);


        if(pendingStories && pendingTrips && dayAfterLastSentNotification < DateTime.now().getMillis()){

            LOG.debug("All conditions passed - lets show notification # " + indexToBeChecked);

            if(needToSetLastNotificationToZero){
                LOG.debug("Lets set last notification set timestamp to 0l");
                lastNotification.setSent(0l);
            }

            notificationToBeChecked.setSent(DateTime.now().getMillis());
            NotificationHelper.showNotificationState(EngagementNotification.getTitle(indexToBeChecked, context), EngagementNotification.getContent(indexToBeChecked, context), context, EngagementNotification.getNotificationIntentCode(indexToBeChecked));
            SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapper, true);

        }// fix to prevent from stopping the loop
        else if(dayAfterLastSentNotification < DateTime.now().getMillis()){

            LOG.debug("Skipping notification "+ indexToBeChecked);

            notificationToBeChecked.setSent(lastSentNotification);

            if(needToSetLastNotificationToZero){
                LOG.debug("Lets set last notification set timestamp to 0l");
                lastNotification.setSent(0l);
            }

            SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapper, true);

        }


    }

    private void checkAndSend4567Notification(Context context, int indexToBeChecked) {

        LOG.debug("Index to be checked -> 4,5,6,7");

        //check for pending

        boolean pendingTrips = checkIfPendingTrips(context);
        LOG.debug("PendingTrips = " + pendingTrips);

        long lastSentNotification = notifications.get(indexToBeChecked-1).getSent();

        LOG.debug("LastSentNotification = " + DateHelper.getDateFromTSString(lastSentNotification));

        long dayAfterLastSentNotification = lastSentNotification + 24*60*60*1000;

        LOG.debug("LastSentNotification = " + DateHelper.getDateFromTSString(dayAfterLastSentNotification));

        EngagementNotification notificationToBeChecked = notifications.get(indexToBeChecked);

        if(pendingTrips && dayAfterLastSentNotification < DateTime.now().getMillis()){

            LOG.debug("All conditions passed - lets show notification # " + indexToBeChecked);

            notificationToBeChecked.setSent(DateTime.now().getMillis());

            PersistentTripStorage persistentTripStorage = new PersistentTripStorage(context);

            switch (indexToBeChecked){
                case 4:
//                    showNotificationState(notificationToBeChecked.getTitle(), notificationToBeChecked.getText(), context);
                    ArrayList<FullTripDigest> digestsLastWeek = persistentTripStorage.getAllFullTripDigestsForTimeIntervalObjects(DateTime.now().minusWeeks(1).getMillis());

                    WorthwhilenessCO2Score worthwhilenessCO2Score = WorthwhilenessCO2Score.computeWorthwhilenessScore(digestsLastWeek);

                    NotificationHelper.showNotificationState(context.getString(R.string.engagement_notification_5_title, (int) worthwhilenessCO2Score.getWorthwhilenessScore()),context.getString(R.string.engagement_notification_5_content), context,EngagementNotification.getNotificationIntentCode(indexToBeChecked));

                    break;

                case 6:

                    ArrayList<FullTripDigest> digestsLastWeekForMinutes = persistentTripStorage.getAllFullTripDigestsForTimeIntervalObjects(DateTime.now().minusWeeks(1).getMillis());
                    NotificationHelper.showNotificationState(context.getString(R.string.engagement_notification_7_title, (int) WorthwhilenessCO2Score.getTimeTraveledForDigests(digestsLastWeekForMinutes)),context.getString(R.string.engagement_notification_7_content), context, EngagementNotification.getNotificationIntentCode(indexToBeChecked));

                    break;

                default:
                    NotificationHelper.showNotificationState(EngagementNotification.getTitle(indexToBeChecked, context), EngagementNotification.getContent(indexToBeChecked, context), context, EngagementNotification.getNotificationIntentCode(indexToBeChecked));
                    break;
            }

            SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapper, true);

        }


    }

    private void checkAndSendLastNotification(Context context, int indexToBeChecked) {

        LOG.debug("Index to be checked -> 4,5,6,7");

        //check for pending

        boolean pendingTrips = checkIfPendingTrips(context);
        LOG.debug("PendingTrips = " + pendingTrips);

        long lastSentNotification = notifications.get(indexToBeChecked-1).getSent();

        LOG.debug("LastSentNotification = " + DateHelper.getDateFromTSString(lastSentNotification));

        long dayAfterLastSentNotification = lastSentNotification + 24*60*60*1000;

        LOG.debug("LastSentNotification = " + DateHelper.getDateFromTSString(dayAfterLastSentNotification));

        EngagementNotification notificationToBeChecked = notifications.get(indexToBeChecked);

        if(pendingTrips && dayAfterLastSentNotification < DateTime.now().getMillis()){

            LOG.debug("All conditions passed - lets show notification # " + indexToBeChecked);
            notificationToBeChecked.setSent(DateTime.now().getMillis());

            LOG.debug("Since it's the last notification, let's set the repeatable ones to 0l");

            for(int i=3; i<notifications.size()-1; i++){
                notifications.get(i).setSent(0l);
            }

            NotificationHelper.showNotificationState(notificationToBeChecked.getTitle(), notificationToBeChecked.getText(), context, EngagementNotification.getNotificationIntentCode(indexToBeChecked));
            SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapper, true);

        }


    }

    private boolean checkIfPendingStories(UserSettingStateWrapper userSettingStateWrapper){

        ArrayList<StoryStateful> storyStatefuls =  userSettingStateWrapper.getUserSettings().getStories();

        if ((storyStatefuls != null) && (storyStatefuls.size() == 0)){
            return true;
        }

        for (StoryStateful storyStateful : storyStatefuls){

            if(!storyStateful.isRead()){
                return true;
            }

        }

        return false;
    }


    private boolean checkIfPendingTrips(Context context) {

        PersistentTripStorage persistentTripStorage = new PersistentTripStorage(context);

        String uid = null;

        if(FirebaseAuth.getInstance().getUid() != null){
            uid = FirebaseAuth.getInstance().getUid();
        }else{

            UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(context, null);

            if (userSettingStateWrapper != null && userSettingStateWrapper.getUid() != null){

                uid = userSettingStateWrapper.getUid();
            }
        }

        if (uid == null) return false;

        for(FullTripDigest digest : persistentTripStorage.getAllFullTripDigestsByUserIDObjects(uid)){
            if (!digest.isValidated()) return true;
        }

        return false;
    }

    private void checkSurveys(ArrayList<Survey> surveys, long lastCheckedTS, Context context, PersistentTripStorage persistentTripStorage) {

        for (Survey survey : surveys){

            if(survey == null || survey.getTrigger() == null){
                LOG.debug("Corrupt survey! Discarding (id=" + survey.getSurveyID() +")");
                continue;
            }

            LOG.debug("Survey id " + survey.getSurveyID() + " " + survey.getTrigger().toString());
            LOG.debug("Num triggered survey: " + persistentTripStorage.getTriggeredSurveysByIDObject(survey.getSurveyID()+"").size());

            if(survey.getTrigger() instanceof TimedTrigger){

                long triggerTS = (((TimedTrigger) survey.getTrigger()).getTimestamp());

                LOG.debug("Timed trigger triggerts= " + DateHelper.getDateFromTSString(triggerTS));

                LOG.debug("Timed trigger lastCheckedTS= " + DateHelper.getDateFromTSString(lastCheckedTS));

                LOG.debug("Timed trigger now = " + DateHelper.getDateFromTSString(DateTime.now().getMillis()));

                LOG.debug("Survey start date: " + DateHelper.getDateFromTSString((long)survey.getStartDate()));
                LOG.debug("Survey stop date: " + DateHelper.getDateFromTSString((long)survey.getStopDate()));

                //if current date outside survey start, stop time interval
                if(!SurveyManager.checkIfSurveyIsDateValid(survey)){

                    LOG.debug("Timed triggered survey outside start, stop date - continue");
                    continue;

                }

                if(persistentTripStorage.getTriggeredSurveysByIDObject(survey.getSurveyID()+"").size() > 0){
                    LOG.debug("Timed survey already launched. Continue");
                    continue;
                }else{
                    LOG.debug("Timed survey not launched already. Checking trigger ts");
                }

                if(triggerTS < DateTime.now().getMillis()){
                    SurveyManager.getInstance(context).triggerSurvey(survey, DateTime.now().getMillis(), context);
                    LOG.debug("Putting survey on the triggered surveys list");
                }


            }else if(survey.getTrigger() instanceof TimedRecurringTrigger){

                ArrayList<SurveyStateful> beforeTriggers = persistentTripStorage.getTriggeredSurveysByIDObject(survey.getSurveyID()+"");

                long initialTrigger = ((TimedRecurringTrigger) survey.getTrigger()).getTimestamp();
                long timeInterval = ((TimedRecurringTrigger) survey.getTrigger()).getTimeInBetween();

                LOG.debug("Survey start date: " + DateHelper.getDateFromTSString((long)survey.getStartDate()));
                LOG.debug("Survey stop date: " + DateHelper.getDateFromTSString((long)survey.getStopDate()));

                //if current date outside survey start, stop time interval
                if(!SurveyManager.checkIfSurveyIsDateValid(survey)){

                    LOG.debug("Timed recurring triggered survey outside start, stop date - continue");
                    continue;

                }


                //if it has not been triggered before, just check if initial trigger date already passed by
                if(beforeTriggers.size() == 0){

                    if(DateTime.now().getMillis() > initialTrigger){
                        SurveyManager.getInstance(context).triggerSurvey(survey, DateTime.now().getMillis(), context);
                    }

                }else{

                    long lastTriggeredTimestamp = beforeTriggers.get(beforeTriggers.size()-1).getTriggerTimestamp();

                    if(DateTime.now().getMillis() > (lastTriggeredTimestamp+timeInterval)){
                        SurveyManager.getInstance(context).triggerSurvey(survey, DateTime.now().getMillis(), context);
                    }

                }

            }

        }


    }

}

package inesc_id.pt.motivandroid.surveyNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Locale;

import inesc_id.pt.motivandroid.managers.EngagementManager;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.deprecated.ViewTriggeredSurveysList;
import inesc_id.pt.motivandroid.auth.FirebaseTokenManager;
import inesc_id.pt.motivandroid.data.userSettingsData.Language;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.surveys.SurveyStateful;
import inesc_id.pt.motivandroid.data.surveys.answers.Answer;
import inesc_id.pt.motivandroid.data.surveys.triggers.EventTrigger;
import inesc_id.pt.motivandroid.data.surveys.Survey;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.utils.LogsUtil;
import inesc_id.pt.motivandroid.utils.NotificationHelper;

import static org.joda.time.DateTimeZone.UTC;

/**
 * SurveyManager
 *
 *  Singleton that manages everything related to surveys. Whenever surveys are retrieved from the
 *  server, this class handles them (stores them or updates them if thats the case, triggers them
 *  if needed). Also checks if there is the need to trigger a survey when a trip has started or has
 *  finished.
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

public class SurveyManager {

    private static final Logger LOG = LoggerFactory.getLogger(SurveyManager.class.getSimpleName());

    private static SurveyManager instance;

    private Context context;

    final String LOG_TAG = LogsUtil.INIT_LOG_TAG + "SurveyManager";


    /**
     * last known timestamp locally
     */
    private int currentGlobalTimestampOnDevice;

    /**
     * true if there is a pending request for surveys to the server, false otherwise (avoid
     * inconsistencies)
     */
    private boolean pendingRequest;


    /**
     * array of survey data (raw surveys)
     */
    ArrayList<Survey> surveyArrayList;

    /**
     * array of already triggered surveys
     */
    ArrayList<SurveyStateful> triggeredSurveysList;

    PersistentTripStorage persistentTripStorage;

    private SurveyManager(Context context) {

        this.context = context.getApplicationContext();

        LocalBroadcastManager.getInstance(context).registerReceiver(
                mFullTripStartedReceiver, new IntentFilter("FullTripStarted"));

        LocalBroadcastManager.getInstance(context).registerReceiver(
                mFullTripReceiver, new IntentFilter("FullTripFinished"));


        //retrieve currentTimestamp
        currentGlobalTimestampOnDevice = SharedPreferencesUtil.readCurrentGlobalTimestampOnDevice(context);

        persistentTripStorage = new PersistentTripStorage(context);

        surveyArrayList = persistentTripStorage.getAllSurveysObject();

        Log.d(LOG_TAG, "Survey list size: "+ surveyArrayList.size());

        triggeredSurveysList = persistentTripStorage.getAllTriggeredSurveysObject();
        Log.d(LOG_TAG, "Survey list size: "+ triggeredSurveysList.size());

    }


    public static synchronized SurveyManager getInstance(Context context){

        if(instance == null){
            instance = new SurveyManager(context);
        }
        return instance;
    }

    public synchronized void handleSurveys(ArrayList<Survey> surveyList){


        for (Survey survey : surveyList){

            if(persistentTripStorage.checkIfSurveyExistsObject(survey.getSurveyID() + "")){
                Log.e("SurveyManager", "Survey already exists. Lets replace with the new version sent from server");

                //replace survey object
//                persistentTripStorage.updateTriggeredSurveyDataObject(survey);

                //if has been deleted, lets delete the survey /(survey data, and survey if answers have already been sent to the server
                if (survey.isDeleted()){
                    Log.e("SurveyManager", "Survey deleted");

                    persistentTripStorage.deleteSurveyDataObject(survey);

                    //retrieve surveys stateful from local db(contains state - if was answered, the answers etc)
                    ArrayList<SurveyStateful> surveyStatefuls = persistentTripStorage.getTriggeredSurveysByIDObject(survey.getSurveyID()+"");

                    for (SurveyStateful surveyStateful : surveyStatefuls){

                        if (surveyStateful.isHasBeenSentToServer()){
                            Log.e("SurveyManager", "Survey triggered and deleted. Answers already sent to server, delete survey");
                            persistentTripStorage.deleteTriggeredSurveyDataObject(surveyStateful);
                        }else{
                            Log.e("SurveyManager", "Survey triggered and deleted. Answers NOT YET sent to server, update its data");
                            surveyStateful.setSurvey(survey);
                            persistentTripStorage.updateTriggeredSurveyDataObject(surveyStateful);
                        }

                    }

                    //survey not deleted. Lets update survey stateful data
                }else{
                    Log.e("SurveyManager", "Survey not deleted. Lets update survey stateful data");

                    persistentTripStorage.updateSurveyDataObject(survey);

                    ArrayList<SurveyStateful> surveyStatefuls = persistentTripStorage.getTriggeredSurveysByIDObject(survey.getSurveyID()+"");
                    for (SurveyStateful surveyStateful : surveyStatefuls){

                         persistentTripStorage.updateTriggeredSurveyDataObject(surveyStateful);
                    }
                }

                //check survey stateful
            }else{
                Log.e("SurveyManager","Survey does not exist locally. ");

                if(!survey.isDeleted()){
                    // save survey persistently
                    persistentTripStorage.insertSurveyObject(survey);
                }
//                else{
//                    //do nothing
//                }

            }

//            persistentTripStorage.insertSurveyObject(survey);
        }

        surveyArrayList.clear();
        surveyArrayList.addAll(persistentTripStorage.getAllSurveysObject());

        EngagementManager.getInstance().checkEngagement(context, false, false);


    }


    public void triggerSurvey(Survey survey, long triggerTS, Context context){

        String language = "eng";

        for(Language lang : Language.getAllLanguages()){
            if(context.getResources().getConfiguration().locale.getLanguage().equals(new Locale(lang.getSmartphoneID()).getLanguage())){
                language = lang.getWoortiID();
                Log.e("Survey Test Activity", "Language matched. Will try to show survey in " + lang.getName());
                break;
            }
        }

        UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(context, "");

        SurveyStateful surveyStatefull = new SurveyStateful(survey, false, new ArrayList<Answer>(), 0l, triggerTS, userSettingStateWrapper.getUid(), false, language);
        triggeredSurveysList.add(surveyStatefull);

        persistentTripStorage.insertSurveyTriggeredObject(surveyStatefull);
//        showNotificationState("You have " + getUnansweredSurveyList() +" unanswered surveys!");

        NotificationHelper.showNotificationSurveyState(context, survey.getSurveyPoints());


    }

    public static boolean checkIfSurveyIsDateValid(Survey survey){

        if((survey.getStartDate() < DateTime.now().getMillis()) && (survey.getStopDate() > DateTime.now().getMillis())){
            return true;
        }else{
            return false;
        }

    }

    // callback called when a trip has started -> might have to trigger a survey
    private BroadcastReceiver mFullTripStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Crashlytics.log(Log.DEBUG, LOG_TAG, "Full trip started message received");

            String uid;
            try{
                uid = FirebaseAuth.getInstance().getUid();
            }catch (NullPointerException e){
                uid = FirebaseTokenManager.getInstance(context).getLastKnownUID();
            }

            surveyArrayList = persistentTripStorage.getAllSurveysObject();

            for(Survey survey : surveyArrayList){

                if(survey == null || survey.getTrigger() == null){
                    LOG.debug("Corrupt survey! Discarding (id=" + survey.getSurveyID() +")");
                    continue;
                }

                    if (survey.getTrigger() instanceof EventTrigger) {
                        Crashlytics.log(Log.DEBUG, LOG_TAG, "Trigger is event");

                        EventTrigger eventTrigger = (EventTrigger) survey.getTrigger();

                        if (eventTrigger.getTrigger().equals("starttrip")) {

                            if(!checkIfSurveyIsDateValid(survey)) {
                                LOG.debug("StartTrip triggered survey outside start, stop date - continue");
                                continue;
                            }

                            triggerSurvey(survey, DateTime.now().getMillis(), context);

                        }

                    }

            }

        }
    };

    // callback called when a trip has ended -> might have to trigger a survey
    private BroadcastReceiver mFullTripReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Crashlytics.log(Log.DEBUG, LOG_TAG, "Full trip finished message received in main activity - sent from state machine");

            Boolean realTrip = intent.getBooleanExtra("isTrip", false);

            String uid;
            try{
                uid = FirebaseAuth.getInstance().getUid();
            }catch (NullPointerException e){
                uid = FirebaseTokenManager.getInstance(context).getLastKnownUID();
            }

            surveyArrayList = persistentTripStorage.getAllSurveysObject();

            if(realTrip){

                Crashlytics.log(Log.DEBUG, LOG_TAG, "Real trip!");

                for(Survey survey : surveyArrayList) {

                    if(survey == null || survey.getTrigger() == null){
                        LOG.debug("Corrupt survey! Discarding (id=" + survey.getSurveyID() +")");
                        continue;
                    }

                        if (survey.getTrigger() instanceof EventTrigger) {
                            Crashlytics.log(Log.DEBUG, LOG_TAG, "Trigger is event");

                            EventTrigger eventTrigger = (EventTrigger) survey.getTrigger();

                            if (eventTrigger.getTrigger().equals("endTrip")) {

                                if (!checkIfSurveyIsDateValid(survey)) {
                                    LOG.debug("EndTrip triggered survey outside start, stop date - continue");
                                    continue;
                                }

                                triggerSurvey(survey, DateTime.now().getMillis(), context);

                            }

//                        if(eventTrigger.getTrigger().equals("endTrip")){
//
//                            String language = "eng";
//
//                            for(Language lang : Language.getAllLanguages()){
//                                if(context.getResources().getConfiguration().locale.getLanguage().equals(new Locale(lang.getSmartphoneID()).getLanguage())){
//                                    language = lang.getWoortiID();
//                                    Log.e("Survey Test Activity", "Language matched. Will try to show survey in " + lang.getName());
//                                    break;
//                                }
//                            }
//
//                            Crashlytics.log(Log.DEBUG, LOG_TAG, "Event is end trip");
//                            SurveyStateful surveyStatefull = new SurveyStateful(survey, false, new ArrayList<Answer>(), null, new DateTime(UTC).getMillis(), uid, false, language);
//                            triggeredSurveysList.add(surveyStatefull);
//                            persistentTripStorage.insertSurveyTriggeredObject(surveyStatefull);
//                            showNotificationState("You have " + getUnansweredSurveyList() +" unanswered surveys!");
//                        }

                        }
                }
            }

        }
    };

    //  must implement event resolution - upon a certain event check if the any of the current surveys
    // launches triggers a notification/survey to the user. E.g. trip start -> receive message from
    // trip state machine -> check if any of the surveys must be triggered
    //    DONE

    //fcm notification receives message from backend. DONE
    // Sets the pendingGlobalTimestampRequest to the one received in the message and pendingRequest to true DONE
    // Calls motiv api client to retrieve surveys DONE
    // Survey manager receives surveys and sets pendingRequest to false, currentGlobalTimestampOnDevice to the new one (that was pending) DONE

    public int getCurrentGlobalTimestampOnDevice() {
        return currentGlobalTimestampOnDevice;
    }

    public void setCurrentGlobalTimestampOnDevice(int currentGlobalTimestampOnDevice) {
        this.currentGlobalTimestampOnDevice = currentGlobalTimestampOnDevice;
        SharedPreferencesUtil.writeCurrentGlobalTimestampOnDevice(context,currentGlobalTimestampOnDevice);
    }

    public boolean isPendingRequest() {
        return pendingRequest;
    }

    public void setPendingRequest(boolean pendingRequest) {
        this.pendingRequest = pendingRequest;

    }

    public SurveyStateful getLastTriggeredSurvey(){

        if(triggeredSurveysList.size()==0) return null;

        return triggeredSurveysList.get(triggeredSurveysList.size()-1);

    }

    public Survey getLastSurvey(){

        if(surveyArrayList.size()==0) return null;

        return surveyArrayList.get(surveyArrayList.size()-1);

    }

    public int getUnansweredSurveyList(){

        int num = 0;

        for (SurveyStateful ss : triggeredSurveysList){
            if(!ss.isHasBeenAnswered()) num++;
        }

        return num;
    }


}

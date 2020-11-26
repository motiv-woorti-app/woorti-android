package inesc_id.pt.motivandroid.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import inesc_id.pt.motivandroid.auth.FCMFirebaseTokenContainer;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
//import inesc_id.pt.motivandroid.routeRank.data.RouteToBeShown;

/**
 * SharedPreferencesUtil
 *
 * Class responsible for managing (reading or writing) persistent data to shared preferences.
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

public class SharedPreferencesUtil {

    //Context context;

    //constants
    public static final String SHARED_PREFERENCES_FILE = "inesc_id.pt.motivandroid.SF_FILE";

    public static final String SHARED_PREFERENCES_VAR_CURRENT = "inesc_id.pt.motivandroid.var.currentToBeCompared";
    public static final String SHARED_PREFERENCES_VAR_TRIPSTATE = "inesc_id.pt.motivandroid.var.tripstate";
    public static final String SHARED_PREFERENCES_FCMTOKEN = "inesc_id.pt.motivandroid.var.fcmToken";

    public static final String SHARED_PREFERENCES_CURRENT_GLOBAL_TIMESTAMP_ON_DEVICE = "inesc_id.pt.motivandroid.var.currentGlobalTimestampOnDevice";

    public static final String SHARED_PREFERENCES_ONBOARDING_DATA = "inesc_id.pt.motivandroid.onboardingDataOnDevice";

    public static final String SHARED_PREFERENCES_LOGGED_TIMESTAMP = "inesc_id.pt.motivandroid.loggedTimestamp";

    public static final String SHARED_PREFERENCES_LAST_CHECKED_NOTIFICATIONS_SURVEYS = "inesc_id.pt.motivandroid.lastCheckedNotificationsSurveys";

    public static final String SHARED_PREFERENCES_HAS_RESET_TRIP_SENT_STATUS = "inesc_id.pt.motivandroid.hasResetTripSentStatus";

    public static LocationDataContainer readCurrentToBeCompared(Context context, String defaultValue){
        Gson gson = new Gson();
        if(keyExists(context,SHARED_PREFERENCES_VAR_CURRENT)) {
            return gson.fromJson(getPersistentString(context,SHARED_PREFERENCES_VAR_CURRENT, defaultValue),LocationDataContainer.class);
        }else{
            return null;
        }
    }

    public static void writeCurrentToBeCompared(Context context, LocationDataContainer locationDataContainer){

        Gson gson = new Gson();
        setPersistentString(context,SHARED_PREFERENCES_VAR_CURRENT,gson.toJson(locationDataContainer));
    }

    public static void deleteCurrentToBeCompared(Context context){
        deleteKey(context,SHARED_PREFERENCES_VAR_CURRENT);
    }

    public static int readSavedTripState(Context context, int defaultValue){
        // I assume that if the key does not exist, defaultValue is returned
        return getPersistentInt(context,SHARED_PREFERENCES_VAR_TRIPSTATE,defaultValue);
    }

    public static void writeTripState(Context context, int state){

        setPersistentInt(context,SHARED_PREFERENCES_VAR_TRIPSTATE,state);
    }

    public static void deleteTripState(Context context){
       deleteKey(context,SHARED_PREFERENCES_VAR_TRIPSTATE);
    }

    //methods to read and write to the shared preferences file defined in variable SHARED_PREFERENCES_FILE
    private static String getPersistentString(Context context, String id, String defaultValue) {
        return getPersistentPrefs(context).getString(id,defaultValue);
    }

    private static void setPersistentString(Context context, String id, String value) {
        SharedPreferences.Editor editor = getPersistentEditor(context);
        editor.putString(id, value);
        editor.commit();
    }

    private static int getPersistentInt(Context context, String id, int defaultValue) {
        return getPersistentPrefs(context).getInt(id,defaultValue);
    }

    private static void setPersistentInt(Context context, String id, int value) {
        SharedPreferences.Editor editor = getPersistentEditor(context);
        editor.putInt(id, value);
        editor.commit();
    }

    private static long getPersistentLong(Context context, String id, long defaultValue) {
        return getPersistentPrefs(context).getLong(id,defaultValue);
    }

    private static void setPersistentLong(Context context, String id, long value) {
        SharedPreferences.Editor editor = getPersistentEditor(context);
        editor.putLong(id, value);
        editor.commit();
    }

    private static SharedPreferences.Editor getPersistentEditor(Context context){
        SharedPreferences sharedPreferences = getPersistentPrefs(context);
        return sharedPreferences.edit();
    }

    private static SharedPreferences getPersistentPrefs(Context context){
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public static boolean keyExists(Context context, String key) {
        return getPersistentPrefs(context).contains(key);
    }

    private static void deleteKey(Context context, String key){
        SharedPreferences.Editor editor = getPersistentEditor(context);
        editor.remove(key);
        editor.commit();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////// FCM token


//    private static boolean isCurrentFCMTokenOnServer(Context context){
//
//        SharedPreferences.Editor editor = getPersistentEditor(context);
//
//
//    }

    public static FCMFirebaseTokenContainer readFCMFirebaseToken(Context context, String defaultValue){
        Gson gson = new Gson();
        if(keyExists(context,SHARED_PREFERENCES_FCMTOKEN)) {
            return gson.fromJson(getPersistentString(context,SHARED_PREFERENCES_FCMTOKEN, defaultValue),FCMFirebaseTokenContainer.class);
        }else{
            return null;
        }
    }

    public static void writeFCMFirebaseToken(Context context, FCMFirebaseTokenContainer fcmFirebaseTokenContainer){

        Gson gson = new Gson();
        setPersistentString(context,SHARED_PREFERENCES_FCMTOKEN,gson.toJson(fcmFirebaseTokenContainer));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////// Survey data


    public static int readCurrentGlobalTimestampOnDevice(Context context){
        return getPersistentInt(context,SHARED_PREFERENCES_CURRENT_GLOBAL_TIMESTAMP_ON_DEVICE,-1);
    }

    public static void writeCurrentGlobalTimestampOnDevice(Context context, int timestamp){
        setPersistentInt(context,SHARED_PREFERENCES_CURRENT_GLOBAL_TIMESTAMP_ON_DEVICE,timestamp);
    }



    public static long readHasResetSentTripsOnDevice(Context context){
        return getPersistentLong(context,SHARED_PREFERENCES_HAS_RESET_TRIP_SENT_STATUS,0l);
    }

    public static void writeHasResetSentTripsOnDevice(Context context, long timestamp){
        setPersistentLong(context,SHARED_PREFERENCES_HAS_RESET_TRIP_SENT_STATUS,timestamp);
    }

    public static UserSettingStateWrapper readOnboardingUserData(Context context, String defaultValue){
        Gson gson = new Gson();
        if(keyExists(context,SHARED_PREFERENCES_ONBOARDING_DATA)) {
            return gson.fromJson(getPersistentString(context,SHARED_PREFERENCES_ONBOARDING_DATA, defaultValue),UserSettingStateWrapper.class);
        }else{
            return null;
        }
    }

    public static void writeOnboardingUserData(Context context, UserSettingStateWrapper userSettingStateWrapper, boolean incrementVersion){

        if(incrementVersion){
            userSettingStateWrapper.getUserSettings().incrementVersion();
        }

        Gson gson = new Gson();
        setPersistentString(context,SHARED_PREFERENCES_ONBOARDING_DATA,gson.toJson(userSettingStateWrapper));
    }

    public static void deleteOnboarding(Context context){

        Log.e("Shared preferences", "deleting shared preferences");

        if (keyExists(context, SHARED_PREFERENCES_ONBOARDING_DATA)){
            deleteKey(context, SHARED_PREFERENCES_ONBOARDING_DATA);
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static long readCurrentLoggedTimestamp(Context context, long defaultValue){
        return getPersistentLong(context,SHARED_PREFERENCES_LOGGED_TIMESTAMP,defaultValue);
    }

    public static void writeCurrentLoggedTimestamp(Context context, long timestamp){
        setPersistentLong(context,SHARED_PREFERENCES_LOGGED_TIMESTAMP, timestamp);
    }

    public static void setLastCheckedNotificationsSurveysTimestamp(Context context, long timestamp){


        setPersistentLong(context, SHARED_PREFERENCES_LAST_CHECKED_NOTIFICATIONS_SURVEYS, timestamp);

    }

    public static long getLastCheckedNotificationsSurveys(Context context){

       return getPersistentLong(context,SHARED_PREFERENCES_LAST_CHECKED_NOTIFICATIONS_SURVEYS, 0l);

    }

}

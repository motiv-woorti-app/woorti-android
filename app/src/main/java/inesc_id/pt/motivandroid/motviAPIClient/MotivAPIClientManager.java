package inesc_id.pt.motivandroid.motviAPIClient;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import inesc_id.pt.motivandroid.managers.CampaignManager;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.managers.RewardManager;
import inesc_id.pt.motivandroid.managers.UserStatsManager;
import inesc_id.pt.motivandroid.auth.FirebaseTokenManager;
import inesc_id.pt.motivandroid.auth.data.NeedsOnboardingResponse;
import inesc_id.pt.motivandroid.auth.data.UserSchemaServer;
import inesc_id.pt.motivandroid.data.ActivityDataContainer;
import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.rewards.RewardData;
import inesc_id.pt.motivandroid.data.rewards.RewardStatus;
import inesc_id.pt.motivandroid.data.rewards.RewardStatusAllTimeResponse;
import inesc_id.pt.motivandroid.data.rewards.RewardStatusServer;
import inesc_id.pt.motivandroid.data.statsFromServer.GlobalStatsServerResponse;
import inesc_id.pt.motivandroid.data.surveys.SurveyStateful;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigest;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.FullTripServer;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.surveys.Survey;
import inesc_id.pt.motivandroid.data.surveys.tempSurveySubmission.AnsweredReportToServer;
import inesc_id.pt.motivandroid.data.tripData.TripSummary;
import inesc_id.pt.motivandroid.json.JSONUtils;
import inesc_id.pt.motivandroid.motviAPIClient.responses.GetSurveysResponse;
import inesc_id.pt.motivandroid.motviAPIClient.responses.TripPostResponse;
import inesc_id.pt.motivandroid.motviAPIClient.responses.UpdateUserDataResponse;
import inesc_id.pt.motivandroid.motviAPIClient.responses.WeatherResponse.response.WeatherResponse;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.surveyNotification.SurveyManager;
import inesc_id.pt.motivandroid.utils.DateHelper;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.joda.time.DateTimeZone.UTC;

/**
 * MotivAPIClientManager
 *
 *  Class that implements all the logic in order to interact with the Motiv restful API
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

public class MotivAPIClientManager {

    private Context context;
    final MotivAPIClientService motivAPIClientService;
    final MotivAPIClientService motivAPIClientServiceWeather;
    final MotivAPIClientService motivAPIClientServiceSurveys;

    FirebaseTokenManager firebaseTokenManager;

    PersistentTripStorage persistentTripStorage;

    SurveyManager surveyManager;

    private static final Logger LOG = LoggerFactory.getLogger(MotivAPIClientManager.class.getSimpleName());


    public WeatherResponse getLastWeather() {
        return lastWeather;
    }

    public void setLastWeather(WeatherResponse lastWeather) {
        this.lastWeather = lastWeather;
    }

    public long getLastWeatherTimestamp() {
        return lastWeatherTimestamp;
    }

    public void setLastWeatherTimestamp(long lastWeatherTimestamp) {
        this.lastWeatherTimestamp = lastWeatherTimestamp;
    }

    private WeatherResponse lastWeather;
    private long lastWeatherTimestamp;

    public MotivAPIClientManager(Context context) {

        this.context = context.getApplicationContext();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.writeTimeout(60, TimeUnit.SECONDS);

        httpClient.addInterceptor(logging);

        //TODO REPLACE WITH THE BACKEND ADDRESS
        final String BASE_URL = "https://app.motiv.gsd.inesc-id.pt:8000/api/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(JSONUtils.getInstance().getGson()))
                .client(httpClient.build())
                .build();

        Retrofit retrofitWeather = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Retrofit retrofitSurveys = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(JSONUtils.getInstance().getSurveyGSON()))
                .client(httpClient.build())
                .build();



        motivAPIClientService = retrofit.create(MotivAPIClientService.class);
        motivAPIClientServiceWeather = retrofitWeather.create(MotivAPIClientService.class);
        motivAPIClientServiceSurveys = retrofitSurveys.create(MotivAPIClientService.class);

        firebaseTokenManager = FirebaseTokenManager.getInstance(context);


        persistentTripStorage = new PersistentTripStorage(context);

        surveyManager = SurveyManager.getInstance(context);
    }


    public static MotivAPIClientManager instance = null;

    public static MotivAPIClientManager getInstance(Context context){

        if(instance == null){
            Log.e("TSM", "instantiating new MotivAPIClientManager");
            instance = new MotivAPIClientManager(context);
        }
        return instance;
    }


    public void sendConfirmedTripsToServer(){

        new MotivAPIClientManager.SendConfirmedTripsToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void sendTripSummaries(){

        new MotivAPIClientManager.MakePutTripSummaries().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void getMyRewardData(){
        new MotivAPIClientManager.MakeGetMyRewardsData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void putAndGetMyRewardStatus(){

        new MotivAPIClientManager.MakePutGetMyRewardsStatuses().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getReportingSurvey(){

        new MotivAPIClientManager.MakeGetReportingSurveyRequestTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void getStatsFromServerAndSet(){

        new MotivAPIClientManager.MakeGetUserStats().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /**
     * AsyncTask to send validated user trips that havent been yet sent to server
     */
    private class SendConfirmedTripsToServer extends AsyncTask<Void, String, String>{

        @Override
        protected String doInBackground(Void... voids) {

            LOG.debug("Trying to send all confirmed trips to server");

            ArrayList<FullTripDigest> digests = persistentTripStorage.getAllFullTripDigestsByUserIDObjects(FirebaseAuth.getInstance().getUid());

                boolean first = true;

                for (FullTripDigest fullTripDigest : digests) {

                        if (!fullTripDigest.isSentToServer() && fullTripDigest.isValidated()) {

                            if (first){
                                first = false;
                                publishProgress(context.getString(R.string.Trip_Submission_Attempt));
                            }

                            try {
                                LOG.debug("Trying to send trip from " + DateHelper.getDateFromTSString(fullTripDigest.getInitTimestamp()) + " to the server");

                                PersistentTripStorage persistentTripStorage = new PersistentTripStorage(context);
                                FullTrip tripToBeSent = persistentTripStorage.getFullTripByDate(fullTripDigest.getTripID());

                                if (tripToBeSent == null){
                                    LOG.debug( "Trip corrupted. Skipping");
                                    continue;
                                }


                                String firebaseToken = firebaseTokenManager.getLastFirebaseToken();

                                for (FullTripPart fullTripPart : tripToBeSent.getTripList()) {
                                    if (fullTripPart.isTrip()) {
                                        ((Trip) fullTripPart).setActivityDataContainers(new ArrayList<ActivityDataContainer>());
                                        ((Trip) fullTripPart).setTrueDistance(((Trip) fullTripPart).getTrueDistance());
                                    }
                                }

                                FullTripServer beingSent = new FullTripServer(tripToBeSent);

                                Call<TripPostResponse> call = motivAPIClientService.postTrip("Bearer " + firebaseToken, beingSent);

                                LOG.debug( "TRYING TO SEND TRIP TO SERVER BEFORE EXECUTE - " + tripToBeSent.getDateId());

                                Response<TripPostResponse> r = call.execute();

                                if (r.isSuccessful()) {

                                    String dateId = tripToBeSent.getDateId();
                                    Log.d("MotivAPI", "Sent Fulltrip " + dateId);
                                    tripToBeSent.setSentToServer(true);
                                    persistentTripStorage.updateFullTripDataObject(tripToBeSent, dateId);

                                    publishProgress(context.getString(R.string.Trip_Submission_Successful));

                                } else {
                                    Log.e("MotivAPI", r.raw().toString());
                                    publishProgress(context.getString(R.string.Trip_Submission_Not_Submitted));
                                    firebaseTokenManager.getAndSetFirebaseToken();
                                }




                    } catch (Exception e) {
                        e.printStackTrace();
                        LOG.error(e.getMessage());

                    }
                }
                }


            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Toast.makeText(context,progress[0],Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * AsyncTask to send user trip summaries that haven't yet been sent to the server
     */
    private class MakePutTripSummaries extends AsyncTask<Void, String, String>{


        @Override
        protected String doInBackground(Void... voids) {

            LOG.debug("Trying to send summaries to server");

            UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(context, null);

            if (userSettingStateWrapper != null && userSettingStateWrapper.getUserSettings() != null){

                long lastSummarySent = userSettingStateWrapper.getUserSettings().getLastSummarySent();

                ArrayList<FullTripDigest> digests = persistentTripStorage.getAllFullTripDigestsByUserIDTimeIntervalObjects(lastSummarySent, FirebaseAuth.getInstance().getUid());

                ArrayList<TripSummary> tripSummariesToBeSent = new ArrayList<>();

                long latestTSBeingSent = 0l;

                for (FullTripDigest fullTripDigest : digests) {
                    tripSummariesToBeSent.add(new TripSummary(fullTripDigest));

                    if (fullTripDigest.getInitTimestamp() > latestTSBeingSent){
                        latestTSBeingSent = fullTripDigest.getInitTimestamp();
                    }

                }


                if (tripSummariesToBeSent.size() > 0){

                    LOG.debug("Some summaries to be sent " + tripSummariesToBeSent.size());

                    String firebaseToken = firebaseTokenManager.getLastFirebaseToken();

                    Call<ResponseBody> call = motivAPIClientService.sendTripSummaries("Bearer " + firebaseToken, tripSummariesToBeSent);

                    try {
                        Response<ResponseBody> r = call.execute();

                        if (r.isSuccessful()) {

                            LOG.debug("Successfully sent trip summaries to server");
                            userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(context, null);
                            userSettingStateWrapper.getUserSettings().setLastSummarySent(latestTSBeingSent);
                            SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapper, true);

                        } else {
                            Log.e("MotivAPI", r.raw().toString());
                            firebaseTokenManager.getAndSetFirebaseToken();
                        }


                    }catch (Exception e){

                        e.printStackTrace();
                        LOG.error(e.getMessage());

                    }



                }


                }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Toast.makeText(context,progress[0],Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * AsyncTask to retrieve weather forecast information from the server
     */
    private class MakeGetWeatherRequestTask extends AsyncTask<Void, String, String> {

        protected String doInBackground(Void... params) {
            try {

                String firebaseToken = firebaseTokenManager.getLastFirebaseToken();
                Call<WeatherResponse> call = motivAPIClientServiceWeather.getWeather("Bearer " + firebaseToken);
                Response<WeatherResponse> r = call.execute();

                if (r.isSuccessful()) {
                    Log.d("MotivAPI", "Successfully downloaded weather info");
                    setLastWeather(r.body());
                    setLastWeatherTimestamp(new DateTime(UTC).getMillis());
                    broadcastWeatherInfo(getLastWeather(), getLastWeatherTimestamp());
                } else {
                    Log.d("MotivAPI", "Error retrieving weather info");
                    broadcastErrorInfo(r.code());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setProgressPercent(progress[0]);
            Toast.makeText(context,progress[0],Toast.LENGTH_SHORT).show();
            Log.d("Motiv API", "onpost"+progress);
        }

        protected void onPostExecute(String result) {
            // showDialog("Downloaded " + result + " bytes");
        }
    }

    /**
     * AsyncTask to retrieve weather forecast information from the server
     */
    private class MakeGetMyRewardsData extends AsyncTask<Void, String, String> {

        protected String doInBackground(Void... params) {
            try {

                String firebaseToken = firebaseTokenManager.getLastFirebaseToken();
                Call<ArrayList<RewardData>> call = motivAPIClientService.getMyRewardsData("Bearer " + firebaseToken);
                Response<ArrayList<RewardData>> r = call.execute();

                if (r.isSuccessful()) {
                    Log.d("MotivAPI--REWARD", "Successfully downloaded my rewards data");

                    if((r.body() != null)){
                        Log.d("MotivAPI--REWARD", "Reward list size > 0, assigning");
                        RewardManager.getInstance().setRewardDataArrayList(r.body());

                        for(RewardData rewardData : r.body()){
                            Log.d("MotivAPI--REWARD", "RewardID " + rewardData.getRewardName());
                        }

                        //set the retrieved array of reward data as the current valid reward data
                        RewardManager.getInstance().setRewardDataArrayList(r.body());
                        //check if all the rewards have at least an empty status
                        RewardManager.getInstance().checkAndCreateRewardStatusesForAllTimeRewards(context);

                    }

                } else {
                    Log.d("MotivAPI", "Error retrieving reward data for the provided user");
                    broadcastErrorInfo(r.code());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setProgressPercent(progress[0]);
            Toast.makeText(context,progress[0],Toast.LENGTH_SHORT).show();
            Log.d("Motiv API", "onpost"+progress);
        }

        protected void onPostExecute(String result) {
            // showDialog("Downloaded " + result + " bytes");
        }
    }

    /**
     * AsyncTask to send locally stored user reward statuses to the server. The server replies with
     * all the updated reward statuses
     */
    private class MakePutGetMyRewardsStatuses extends AsyncTask<Void, String, String> {

        protected String doInBackground(Void... params) {
            try {

                ArrayList<RewardStatus> rewardStatuses = new PersistentTripStorage(context).getRewardStatusesNotSentToServer(FirebaseAuth.getInstance().getUid());

                ArrayList<RewardStatusServer> rewardStatusesServerToSend = new ArrayList<>();

                for (RewardStatus rewardStatus : rewardStatuses){
                    rewardStatusesServerToSend.add(new RewardStatusServer(rewardStatus));

                        Log.d("MotivAPI--REWARD", "sending id " + rewardStatus.getRewardID() + rewardStatus.isHasShownPopup());

                }


                Log.d("MotivAPI--REWARD", "Reward statuses size " + rewardStatuses.size());

                String firebaseToken = firebaseTokenManager.getLastFirebaseToken();
                Call<RewardStatusAllTimeResponse> call = motivAPIClientService.getMyRewardsStatusesAllTime("Bearer " + firebaseToken, rewardStatusesServerToSend);
                Response<RewardStatusAllTimeResponse> r = call.execute();

                if (r.isSuccessful()) {
                    Log.d("MotivAPI--REWARD", "Successfully sent my rewards statuses");

                    if((r.body() != null)){
                        Log.d("MotivAPI--REWARD", "Reward list statuses size " + r.body().getRewards().size());

                        Log.d("MotivAPI--REWARD", "r.body().getNumberTotalDaysWithTrips() " + r.body().getNumberTotalDaysWithTrips());
                        Log.d("MotivAPI--REWARD", "r.body().getNumberTotalTrips() " + r.body().getNumberTotalTrips());

                        for (RewardStatusServer s : r.body().getRewards()){
                            Log.d("MotivAPI--REWARD", "receiving id " + s.getRewardID() + s.isHasShownPopup());
                        }

                        RewardManager.getInstance().saveRewardStatusesFromServer(r.body().getRewards(), context);
                        RewardManager.getInstance().setAllTimeTripValues(r.body().getNumberTotalDaysWithTrips(), r.body().getNumberTotalTrips());

                    }

                } else {
                    Log.d("MotivAPI--REWARD", "sending and retrieving statuses");
                    broadcastErrorInfo(r.code());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setProgressPercent(progress[0]);
            Toast.makeText(context,progress[0],Toast.LENGTH_SHORT).show();
            Log.d("Motiv API", "onpost"+progress);
        }

        protected void onPostExecute(String result) {
            // showDialog("Downloaded " + result + " bytes");
        }
    }



    public void makeGetCampaignsByCountryCity(String country, String city){

        String[] myTaskParams = {country, city};
        new MotivAPIClientManager.MakeGetCampaignsByCountryCity().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,myTaskParams);

    }

    /**
     * AsyncTask to retrieve campaign data for a specific city and/or country
     */
    private class MakeGetCampaignsByCountryCity extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {
            try {

                String country = params[0];
                String city = params[1];

                String firebaseToken = firebaseTokenManager.getLastFirebaseToken();


                Call<ArrayList<Campaign>> call = motivAPIClientService.getCampaignsForCountryCity(
                        "Bearer " + firebaseToken,
                        country,
                        city);

                Log.d("MotivAPI",bodyToString(call.request().body()));

                Response<ArrayList<Campaign>> r = call.execute();


                if (r.isSuccessful()) {
                    Log.d("MotivAPI", "Successfully retrieved campaigns from server");

                    Intent localIntent = new Intent(keys.broadcasteKeyCampaigns);
                    localIntent.putExtra(keys.result, keys.success);
                    localIntent.putExtra(keys.campaignsData, r.body());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);


                } else {
                    Log.d("MotivAPI", "Error retrieving campaigns from server");
                    Log.d("MotivAPI",r.message() + r.code());

                    Intent localIntent = new Intent(keys.broadcasteKeyCampaigns);
                    localIntent.putExtra(keys.result, keys.failed);
                    localIntent.putExtra(keys.errorCode, r.code());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

                }

            } catch (IOException e) {
                Log.d("MotivAPI", "Exception retrieving campaigns from server");
                e.printStackTrace();
                Intent localIntent = new Intent(keys.broadcasteKeyCampaigns);
                localIntent.putExtra(keys.result, keys.failed);
                localIntent.putExtra(keys.errorCode, 0);
                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setProgressPercent(progress[0]);
            Toast.makeText(context,progress[0],Toast.LENGTH_SHORT).show();
            Log.d("Motiv API", "onpost"+progress);
        }

        protected void onPostExecute(String result) {
            // showDialog("Downloaded " + result + " bytes");
        }
    }

    public void makeGetWeatherRequest(){

        new MotivAPIClientManager.MakeGetWeatherRequestTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void makeGetSurveysRequest(int timestamp){

        Integer[] params = {timestamp};

        new MotivAPIClientManager.MakeGetSurveysRequestTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);


    }

    public void makeOverwriteOnboardingRequest(UserSettingStateWrapper userSettingStateWrapper){

        UserSettingStateWrapper[] params = {userSettingStateWrapper};
        new MakeOverwriteOnboardingSettings().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);

    }

    public void makeUpdateOnboardingRequest(){

        new MakeUpdateOnboardingSettings().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void makePostReportSurveyToServer(AnsweredReportToServer temp){

        AnsweredReportToServer[] params = {temp};
        new MakeSendReportSurveyToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);

    }

    public void makePostSurveyAnswersToServer(){


        new MakeSendSurveyAnswerToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /**
     * AsyncTask to send a survey user answer to the server
     */
    private class MakeSendSurveyAnswerToServer extends AsyncTask<Void, String, String> {

        protected String doInBackground(Void... params) {
            try {

                PersistentTripStorage persistentTripStorage = new PersistentTripStorage(context);
                String firebaseToken = firebaseTokenManager.getLastFirebaseToken();

                for(SurveyStateful surveyStateful : persistentTripStorage.getAllTriggeredSurveysObject()){

                    if(surveyStateful.isHasBeenAnswered() && !surveyStateful.isHasBeenSentToServer()){


                        Call<ResponseBody> call = motivAPIClientServiceSurveys.sendAnswerToSurvey(
                                "Bearer " + firebaseToken,
                                surveyStateful.getAnsweredSurveyToServer()
                        );

                        Log.d("MotivAPI",bodyToString(call.request().body()));
                        Response<ResponseBody> r = call.execute();

                        if (r.isSuccessful()) {
                            Log.d("MotivAPI", "Successfully sent survey answer to server");
                            surveyStateful.setHasBeenSentToServer(true);
                            persistentTripStorage.updateTriggeredSurveyDataObject(surveyStateful);
                        } else {
                            Log.d("MotivAPI", "Error sending report data to server");
                            Log.d("MotivAPI",r.message() + r.code());

                        }

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setProgressPercent(progress[0]);
            Toast.makeText(context,progress[0],Toast.LENGTH_SHORT).show();
            Log.d("Motiv API", "onpost"+progress);
        }

        protected void onPostExecute(String result) {
            // showDialog("Downloaded " + result + " bytes");
        }
    }

    /**
     * AsyncTask to send an answer to the "Report feedback" specific survey
     */
    private class MakeSendReportSurveyToServer extends AsyncTask<AnsweredReportToServer, String, String> {

        protected String doInBackground(AnsweredReportToServer... params) {
            try {

                AnsweredReportToServer data = params[0];

                String firebaseToken = firebaseTokenManager.getLastFirebaseToken();

                Call<ResponseBody> call = motivAPIClientServiceSurveys.sendReportIssue(
                        "Bearer " + firebaseToken,
                        data);

                Log.d("MotivAPI",bodyToString(call.request().body()));

                Response<ResponseBody> r = call.execute();

                if (r.isSuccessful()) {
                    Log.d("MotivAPI", "Successfully sent report to server");

                } else {
                    Log.d("MotivAPI", "Error sending report data to server");
                    Log.d("MotivAPI",r.message() + r.code());

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setProgressPercent(progress[0]);
            Toast.makeText(context,progress[0],Toast.LENGTH_SHORT).show();
            Log.d("Motiv API", "onpost"+progress);
        }

        protected void onPostExecute(String result) {
            // showDialog("Downloaded " + result + " bytes");
        }
    }

    /**
     * AsyncTask to send onboarding/user settings to server. Firstly, it check which side has a
     * newer onboarding version (if any of them has one). Then, according to which side (local or
     * server) has the newer version it sends/retrieves the onboarding info to/from server.
     *
     * WARNING: the class name is kinda misleading. This is called when the user finishes the
     * onboarding process. The user, upon opening the app, may opt to redo the onboarding process
     * (he she shouldnt be able to but is). If the user presses the "Start now" button upon starting
     * the app, he/she is lead to the onboarding process and we are only able to acknowledge that the
     * user had previously finished the onboarding process after the last step.
     */
    private class MakeOverwriteOnboardingSettings extends AsyncTask<UserSettingStateWrapper, String, String> {

        protected String doInBackground(UserSettingStateWrapper... params) {
            try {

                UserSettingStateWrapper data = params[0];

                String firebaseToken = firebaseTokenManager.getLastFirebaseToken();

                Call<NeedsOnboardingResponse> callCheckServerVersion = motivAPIClientService.needsOnboarding(
                        "Bearer " + firebaseToken
                );

                boolean isThereLocalVersionForUid = false;
                int localVersion = -1;

                String currUID = FirebaseAuth.getInstance().getUid();

                UserSettingStateWrapper localIfExists = SharedPreferencesUtil.readOnboardingUserData(context, null);

                if (localIfExists != null){

                    if (currUID != null && localIfExists.getUid() != null && currUID.equals(localIfExists.getUserid()) && localIfExists.getUserSettings() != null){

                        isThereLocalVersionForUid = true;
                        localVersion = localIfExists.getUserSettings().getVersion();
                    }

                }

                Log.d("MotivAPI",bodyToString(callCheckServerVersion.request().body()));

                Response<NeedsOnboardingResponse> responseNeedsOnboarding = callCheckServerVersion.execute();

                if (responseNeedsOnboarding.isSuccessful()) {
                    Log.d("MotivAPI", "NeedsOnboarding server response OK");

                    boolean needsOnboarding = responseNeedsOnboarding.body().isNeedsOnboarding();
                    int serverVersion = responseNeedsOnboarding.body().getCurrentVersion();

                    Log.d("MotivAPI", "needsOnboarding " + needsOnboarding);
                    Log.d("MotivAPI", "serverVersion " + serverVersion);

                    //server says onboarding is needed
                    if(needsOnboarding){

                        //there is local version, send this version to the server
                        if(isThereLocalVersionForUid){

                            Call<UpdateUserDataResponse> call = motivAPIClientService.updateUserData(
                                    "Bearer " + firebaseToken,
                                    new UserSchemaServer(localIfExists));

                            Log.d("MotivAPI",bodyToString(call.request().body()));

                            Response<UpdateUserDataResponse> r = call.execute();

                            if(sendUserSettingsToServerAndUpdate(localIfExists, context, r, firebaseToken)){
                                broadcastHasCompletedOnboarding(true);
                            }else{
                                broadcastHasCompletedOnboardingError();
                            }

                        // there is no local version yet, lets send the onboarding just filled by the user
                        }else{

                            Call<UpdateUserDataResponse> call = motivAPIClientService.updateUserData(
                                    "Bearer " + firebaseToken,
                                    new UserSchemaServer(data));

                            Log.d("MotivAPI",bodyToString(call.request().body()));

                            Response<UpdateUserDataResponse> r = call.execute();

                            if(sendUserSettingsToServerAndUpdate(data, context, r, firebaseToken)){
                                broadcastHasCompletedOnboarding(false);
                            }else{
                                broadcastHasCompletedOnboardingError();
                            }

                        }


                    }else{
                        //no onboarding needed

                        //there is local version
                        if(isThereLocalVersionForUid){

                            //server version is outdated -> send local to server
                            if (serverVersion < localVersion){

                                Call<UpdateUserDataResponse> call = motivAPIClientService.updateUserData(
                                        "Bearer " + firebaseToken,
                                        new UserSchemaServer(localIfExists));

                                Log.d("MotivAPI",bodyToString(call.request().body()));

                                Response<UpdateUserDataResponse> r = call.execute();

                                if(sendUserSettingsToServerAndUpdate(localIfExists, context, r, firebaseToken)){
                                    broadcastHasCompletedOnboarding(true);
                                }else{
                                    broadcastHasCompletedOnboardingError();
                                }


                            //local is outdated -> retrieve from user
                            }else if(serverVersion > localVersion){

                                Call<UserSchemaServer> getOnboardingFromServerCall = motivAPIClientService.getUserData(
                                        "Bearer " + firebaseToken
                                );

                                Response<UserSchemaServer> onboardingResponse = getOnboardingFromServerCall.execute();

                                if(retrieveUserSettingsFromServer(context, onboardingResponse, firebaseToken)){
                                    broadcastHasCompletedOnboarding(true);
                                }else{
                                    broadcastHasCompletedOnboardingError();
                                }


                            }else{
                                //same version on both -> do nothing

                                broadcastHasCompletedOnboarding(true);
                            }

                        }else{
                            //the server says no onboarding is needed and there is no local version -> get settings from server

                            Call<UserSchemaServer> getOnboardingFromServerCall = motivAPIClientService.getUserData(
                                    "Bearer " + firebaseToken
                            );

                            Response<UserSchemaServer> onboardingResponse = getOnboardingFromServerCall.execute();

                            if(retrieveUserSettingsFromServer(context, onboardingResponse, firebaseToken)){
                                broadcastHasCompletedOnboarding(true);
                            }else{
                                broadcastHasCompletedOnboardingError();
                            }

                        }

                    }

                }else{
                    broadcastHasCompletedOnboardingError();
                }

            } catch (IOException e) {
                e.printStackTrace();
                broadcastHasCompletedOnboardingError();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setProgressPercent(progress[0]);
            Toast.makeText(context,progress[0],Toast.LENGTH_SHORT).show();
            Log.d("Motiv API", "onpost"+progress);
        }

        protected void onPostExecute(String result) {
            // showDialog("Downloaded " + result + " bytes");
        }
    }

    /**
     * AsyncTask to send updated user data to the server
     */
    public boolean sendUserSettingsToServerAndUpdate(UserSettingStateWrapper data, Context context, Response<UpdateUserDataResponse> r, String firebaseToken) throws IOException{

        if (r.isSuccessful()) {
            Log.d("MotivAPI", "Successfully sent onboarding data to server");

            Call<ArrayList<Campaign>> getCampaigns = motivAPIClientService.getCampaignObjectsOnCampaigns(
                    "Bearer " + firebaseToken
            );

            Response<ArrayList<Campaign>> getCampaignsResponse = getCampaigns.execute();

            if (getCampaignsResponse.isSuccessful()){
                Log.d("MotivAPI", "Successfully retrieved campaigns from server");

                data.getUserSettings().setCampaigns(getCampaignsResponse.body());
            }

            data.setSentToServer(true);
            SharedPreferencesUtil.writeOnboardingUserData(context, data, false);
            CampaignManager.getInstance(context).refreshCampaigns(context);

            return true;
        } else {
            Log.e("MotivAPI", "Error sending onboarding data to server");
            Log.e("MotivAPI",r.message() + r.code());
            return false;
        }

    }

    /**
     * AsyncTask to retrieve user settings from the server
     */
    public boolean retrieveUserSettingsFromServer(Context context, Response<UserSchemaServer> r, String firebaseToken) throws IOException{

        if (r.isSuccessful()) {

            Log.d("MotivAPI", "Successfully retrieved onboarding from server");

                UserSettingStateWrapper userSettingStateWrapperFromServer = new UserSettingStateWrapper(r.body());


                Call<ArrayList<Campaign>> getCampaigns = motivAPIClientService.getCampaignObjectsOnCampaigns(
                        "Bearer " + firebaseToken
                );


                Response<ArrayList<Campaign>> getCampaignsResponse = getCampaigns.execute();

                if (getCampaignsResponse.isSuccessful()){
                    Log.d("MotivAPI", "Successfully retrieved campaigns from server");

                    userSettingStateWrapperFromServer.getUserSettings().setCampaigns(getCampaignsResponse.body());
                }

                SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapperFromServer, false);
                CampaignManager.getInstance(context).refreshCampaigns(context);

            return true;

        }else{
            //todo error message
            Log.d("MotivAPI", "Error retrieving onboarding data from server");
            return false;

        }
    }

    /**
     * AsyncTask to send updated user settings to the server
     */
    private class MakeUpdateOnboardingSettings extends AsyncTask<Void, String, String> {

        protected String doInBackground(Void... params) {
            try {

                String firebaseToken = firebaseTokenManager.getLastFirebaseToken();

                UserSettingStateWrapper userSettingStateWrapperDisk = SharedPreferencesUtil.readOnboardingUserData(context, null);

                if (userSettingStateWrapperDisk != null && FirebaseAuth.getInstance().getUid() != null){

                    Call<UpdateUserDataResponse> call = motivAPIClientService.updateUserData(
                            "Bearer " + firebaseToken,
                            new UserSchemaServer(userSettingStateWrapperDisk));

                    Log.d("MotivAPI",bodyToString(call.request().body()));


                    Response<UpdateUserDataResponse> r = call.execute();

                    if (r.isSuccessful()) {
                        Log.d("MotivAPI", "Successfully sent onboarding data to server");
                        //fcmFirebaseTokenContainer.setSentToServer(true);
                        //FirebaseTokenManager.getInstance(context).setFcmFirebaseTokenContainer(fcmFirebaseTokenContainer);

                        Call<ArrayList<Campaign>> getCampaigns = motivAPIClientService.getCampaignObjectsOnCampaigns(
                                "Bearer " + firebaseToken
                        );


                        Response<ArrayList<Campaign>> getCampaignsResponse = getCampaigns.execute();

                        if (getCampaignsResponse.isSuccessful()){
                            Log.d("MotivAPI", "Successfully retrieved campaigns from server");

                            userSettingStateWrapperDisk.getUserSettings().setCampaigns(getCampaignsResponse.body());
                        }

                        userSettingStateWrapperDisk.setSentToServer(true);
                        SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapperDisk, false);
                        CampaignManager.getInstance(context).refreshCampaigns(context);


                    } else {
                        Log.d("MotivAPI", "Error sending onboarding data to server");
                        Log.d("MotivAPI",r.message() + r.code());


                    }

                }



            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setProgressPercent(progress[0]);
            Toast.makeText(context,progress[0],Toast.LENGTH_SHORT).show();
            Log.d("Motiv API", "onpost"+progress);
        }

        protected void onPostExecute(String result) {
            // showDialog("Downloaded " + result + " bytes");
        }
    }

    public void CheckIfOnboardingNeeded(UserSettingStateWrapper temp){

        UserSettingStateWrapper[] params = {temp};
        new CheckOnboardingNeededAndRetrieve().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);

    }

    /**
     * AsyncTask to check, when a user logs in, if he still has to do the onboarding or not, and if
     * has already completed the onboarding, retrieve it from the server if needed
     */
    private class CheckOnboardingNeededAndRetrieve extends AsyncTask<UserSettingStateWrapper, String, String> {

        protected String doInBackground(UserSettingStateWrapper... params) {
            try {

//                UserSettingStateWrapper data = params[0];

                UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(context, null);

                String firebaseToken = firebaseTokenManager.getLastFirebaseToken();

                Call<NeedsOnboardingResponse> call = motivAPIClientService.needsOnboarding(
                        "Bearer " + firebaseToken
                        );


                Log.d("MotivAPI",bodyToString(call.request().body()));

                Response<NeedsOnboardingResponse> r = call.execute();


                if (r.isSuccessful()) {
                    Log.d("MotivAPI", "NeedsOnboarding server response OK");

                    boolean needsOnboarding = r.body().isNeedsOnboarding();
                    int serverVersion = r.body().getCurrentVersion();

                    Log.d("MotivAPI", "needsOnboarding " + needsOnboarding);
                    Log.d("MotivAPI", "serverVersion " + serverVersion);

                    //if server says onboarding is needs -> lets check if there is a local copy
                    if(needsOnboarding){

                        Log.d("MotivAPI", "if server says onboarding is needs -> lets check if there is a local copy");

                        //there is a local version of the onboarding data for this user
                        if(userSettingStateWrapper != null &&
                                userSettingStateWrapper.getUserSettings() != null &&

                                (FirebaseAuth.getInstance().getUid().equals(userSettingStateWrapper.getUid())
                                        || FirebaseAuth.getInstance().getUid().equals(userSettingStateWrapper.getUserid()))
                                ){

                            Log.d("MotivAPI", "there is a local version of the onboarding data for this user");

                            Call<UpdateUserDataResponse> sendOnboardingToServerCall = motivAPIClientService.updateUserData(
                                        "Bearer " + firebaseToken,
                                        new UserSchemaServer(userSettingStateWrapper)
                                );

                                Response<UpdateUserDataResponse> onboardingResponse = sendOnboardingToServerCall.execute();

                                if (onboardingResponse.isSuccessful()) {

                                    Log.d("MotivAPI", "Sucessfully sent onboarding data to server.");

                                    Call<ArrayList<Campaign>> getCampaigns = motivAPIClientService.getCampaignObjectsOnCampaigns(
                                            "Bearer " + firebaseToken
                                    );


                                    Response<ArrayList<Campaign>> getCampaignsResponse = getCampaigns.execute();

                                    if (getCampaignsResponse.isSuccessful()){
                                        Log.d("MotivAPI", "Successfully retrieved campaigns from server");
                                        userSettingStateWrapper.getUserSettings().setCampaigns(getCampaignsResponse.body());
                                    }

                                    userSettingStateWrapper.setSentToServer(true);
                                    SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapper, false);
                                    CampaignManager.getInstance(context).refreshCampaigns(context);


                                }else{

                                    Log.e("MotivAPI", "Error sending local onboarding data to server.");
                                    //todo error message? but still continue cuz has onboarding local right?

                                }

                            Log.d("MotivAPI", "broadcastNeedsOnboardingInfo(false)");
                            broadcastNeedsOnboardingInfo(false);


                            //no local neither server versions
                        //needs onboarding!
                        }else{
                            Log.d("MotivAPI", "no local neither server versions - broadcastNeedsOnboardingInfo(true)");
                            broadcastNeedsOnboardingInfo(true);
                        }

                    // server says we dont need onboarding -> lets check if there is local version
                    }else{

                        Log.d("MotivAPI", "server says we dont need onboarding -> lets check if there is local version");

                        if((userSettingStateWrapper != null) &&
                                userSettingStateWrapper.getUserSettings() != null &&
                                (FirebaseAuth.getInstance().getUid().equals(userSettingStateWrapper.getUid())
                                        || FirebaseAuth.getInstance().getUid().equals(userSettingStateWrapper.getUserid()))
                                ){

                            // if local version is outdated ask server for the most recent version
                            if(userSettingStateWrapper.getUserSettings().getVersion() < serverVersion) {

                                Log.e("MotivAPI", "local version is outdated ask server for the most recent versionn");


                                Call<UserSchemaServer> getOnboardingFromServerCall = motivAPIClientService.getUserData(
                                        "Bearer " + firebaseToken
                                );

                                Response<UserSchemaServer> onboardingResponse = getOnboardingFromServerCall.execute();

                                if (onboardingResponse.isSuccessful()) {

                                    Log.d("MotivAPI", "Successfully retrieved onboarding from server");

                                    if(userSettingStateWrapper.getUserSettings() != null){

                                        UserSettingStateWrapper userSettingStateWrapperFromServer = new UserSettingStateWrapper(onboardingResponse.body());

//                                        Call<ArrayList<Campaign>> getCampaigns = motivAPIClientService.getCampaignsForCountryCity(
//                                                "Bearer " + firebaseToken,
//                                                userSettingStateWrapper.getUserSettings().getCountry(),
//                                                userSettingStateWrapper.getUserSettings().getCity()
//                                        );

                                        Call<ArrayList<Campaign>> getCampaigns = motivAPIClientService.getCampaignObjectsOnCampaigns(
                                                "Bearer " + firebaseToken
                                        );


                                        Response<ArrayList<Campaign>> getCampaignsResponse = getCampaigns.execute();

                                        if (getCampaignsResponse.isSuccessful()){
                                            Log.d("MotivAPI", "Successfully retrieved campaigns from server");


                                            userSettingStateWrapperFromServer.getUserSettings().setCampaigns(getCampaignsResponse.body());
                                        }


                                        SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapperFromServer, false);
                                        CampaignManager.getInstance(context).refreshCampaigns(context);

                                        broadcastNeedsOnboardingInfo(false);
                                    }else{
                                        broadcastNeedsOnboardingInfo(true);
                                    }


                                }else{

                                    //todo error message
                                    Log.d("MotivAPI", "Error retrieving onboarding data from server");

                                }

                            }
                            // if server version is outdated -> send local version to server
                            else if(userSettingStateWrapper.getUserSettings().getVersion() > serverVersion){

                                Call<UpdateUserDataResponse> sendOnboardingToServerCall = motivAPIClientService.updateUserData(
                                        "Bearer " + firebaseToken,
                                        new UserSchemaServer(userSettingStateWrapper)
                                );

                                Log.d("MotivAPI", "server version is outdated -> send local version to server");


                                Response<UpdateUserDataResponse> onboardingResponse = sendOnboardingToServerCall.execute();

                                if (onboardingResponse.isSuccessful()) {

                                    userSettingStateWrapper.setSentToServer(true);

                                    Call<ArrayList<Campaign>> getCampaigns = motivAPIClientService.getCampaignObjectsOnCampaigns(
                                            "Bearer " + firebaseToken
                                    );


                                    Response<ArrayList<Campaign>> getCampaignsResponse = getCampaigns.execute();

                                    if (getCampaignsResponse.isSuccessful()){
                                        Log.d("MotivAPI", "Successfully retrieved campaigns from server");
                                        userSettingStateWrapper.getUserSettings().setCampaigns(getCampaignsResponse.body());
                                    }

                                    SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapper, false);
                                    CampaignManager.getInstance(context).refreshCampaigns(context);

                                    Log.d("MotivAPI", "Sucessfully sent onboarding data to server.");



                                }else{

                                    Log.e("MotivAPI", "Error sending onboarding data to server. Still proceeding beacause local version is the latest");
                                    //todo error message? but still continue cuz has onboarding local right?

                                }

                                broadcastNeedsOnboardingInfo(false);


                                // same version on both sides -> do nothing
                            }else{

                                Log.d("MotivAPI", "Same version on both sides. Do nothing.");
                                broadcastNeedsOnboardingInfo(false);

                            }

                            // server says no onboarding is needed and we dont have a local copy -> retrieve onboarding
                            // data from server
                        }else{

                            Log.d("MotivAPI", "server says no onboarding is needed and we dont have a local copy -> retrieve onboarding");


                            Call<UserSchemaServer> getOnboardingFromServerCall = motivAPIClientService.getUserData(
                                    "Bearer " + firebaseToken
                            );

                            Response<UserSchemaServer> onboardingResponse = getOnboardingFromServerCall.execute();
                            UserSettingStateWrapper userSettingStateWrapperFromServer = new UserSettingStateWrapper(onboardingResponse.body());

                            if (onboardingResponse.isSuccessful()) {



//                                Call<ArrayList<Campaign>> getCampaigns = motivAPIClientService.getCampaignsForCountryCity(
//                                        "Bearer " + firebaseToken,
//                                        userSettingStateWrapperFromServer.getUserSettings().getCountry(),
//                                        userSettingStateWrapperFromServer.getUserSettings().getCity()
//                                );

                                Call<ArrayList<Campaign>> getCampaigns = motivAPIClientService.getCampaignObjectsOnCampaigns(
                                        "Bearer " + firebaseToken
                                );

                                Response<ArrayList<Campaign>> getCampaignsResponse = getCampaigns.execute();

                                if (getCampaignsResponse.isSuccessful()){
                                    Log.d("MotivAPI", "Successfully retrieved campaigns from server");
                                    userSettingStateWrapperFromServer.getUserSettings().setCampaigns(getCampaignsResponse.body());
                                }


                                SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapperFromServer, false);
                                CampaignManager.getInstance(context).refreshCampaigns(context);


                                Log.d("MotivAPI", "Successfully retrieved onboarding data from server");

                                Log.d("MotivAPI",  new Gson().toJson(onboardingResponse.body()));


                                Log.d("MotivAPI", "" + userSettingStateWrapperFromServer.getEmail());
                                Log.d("MotivAPI", "" + userSettingStateWrapperFromServer.getPushNotificationToken());
                                Log.d("MotivAPI", "" + userSettingStateWrapperFromServer.getUid());
                                Log.d("MotivAPI", "" + userSettingStateWrapperFromServer.getUserid());
                                Log.d("MotivAPI", "" + userSettingStateWrapperFromServer.isSentToServer());
                                Log.d("MotivAPI", "" + userSettingStateWrapperFromServer.getUserSettings());
                                Log.d("MotivAPI", "" + userSettingStateWrapperFromServer.getOnCampaigns());

                                if(userSettingStateWrapperFromServer.getUserSettings() != null){
                                    broadcastNeedsOnboardingInfo(false);
                                }else{
                                    broadcastNeedsOnboardingInfo(true);
                                }

                            }else{

                                Log.d("MotivAPI", "Error sending onboarding data to server");





                            }

                        }

                    }


                } else {
                    Log.d("MotivAPI", "Error interacting with server retrieving onboarding");
                    Log.d("MotivAPI",r.message() + r.code());

//                    if((userSettingStateWrapper != null) && (userSettingStateWrapper.getUid() != null) && (userSettingStateWrapper.getUid().equals(FirebaseAuth.getInstance().getUid()))){
//                        Log.e("MotivAPI", "Error retrieving onboarding data from server, but firebase user is the same as the onboarding info on disk!");
//                        broadcastNeedsOnboardingErrorInfo(true);
//                    }else{
//                        Log.e("MotivAPI", "Error retrieving onboarding data from server, firebase user is different than the one on the onboarding!");
//                        broadcastNeedsOnboardingErrorInfo(false);
//                    }
                }

            } catch (IOException e) {
                e.printStackTrace();


                UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(context, null);

                if((userSettingStateWrapper != null) && (userSettingStateWrapper.getUid() != null) && (userSettingStateWrapper.getUid().equals(FirebaseAuth.getInstance().getUid()))){
                    Log.e("MotivAPI", "Error retrieving onboarding data from server, but firebase user is the same as the onboarding info on disk!");
                    broadcastNeedsOnboardingErrorInfo(true);
                }else{
                    Log.e("MotivAPI", "Error retrieving onboarding data from server, firebase user is different than the one on the onboarding!");
                    broadcastNeedsOnboardingErrorInfo(false);

                }

            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setProgressPercent(progress[0]);
            Toast.makeText(context,progress[0],Toast.LENGTH_SHORT).show();
            Log.d("Motiv API", "onpost"+progress);
        }

        protected void onPostExecute(String result) {
            // showDialog("Downloaded " + result + " bytes");
        }


    }

    /**
     * AsyncTask to retrieve new survey data from server.
     */
    private class MakeGetSurveysRequestTask extends AsyncTask<Integer, String, String> {

        protected String doInBackground(Integer... params) {
            try {

                //last survey timestamp stored locally. This is sent to the server so that we only
                //receive new surveys
                Integer timestamp = params[0];

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                String firebaseToken = firebaseTokenManager.getLastFirebaseToken();

                Call<GetSurveysResponse> call = motivAPIClientServiceSurveys.getSurveys
                        ("Bearer " + firebaseToken,
                                timestamp
                        );

                Log.d("MotivAPI", "Trying to retrieve surveys from server - timestamp:" + timestamp);

                Log.d("MotivAPI",bodyToString(call.request().body()));

                Log.d("MotivAPI", "AfterBodyToString" + timestamp);

                try {
                    Response<GetSurveysResponse> r = call.execute();

                    surveyManager.setPendingRequest(true);

                    if (r.isSuccessful()) {
                        Log.d("MotivAPI", "Successfully received surveys");
                        //fcmFirebaseTokenContainer.setSentToServer(true);
                        //FirebaseTokenManager.getInstance(context).setFcmFirebaseTokenContainer(fcmFirebaseTokenContainer);

                        //Log.d("MotivAPI",r.raw().);

                        surveyManager.setPendingRequest(false);

                        timestamp = surveyManager.getCurrentGlobalTimestampOnDevice();
                        boolean greaterTS = false;

                        for(Survey survey: r.body().getSurveys()){
                            if(survey.getGlobalSurveyTimestamp() > timestamp) {
                                timestamp = survey.getGlobalSurveyTimestamp();
                                greaterTS = true;
                            }
                        }

                        if (greaterTS) surveyManager.setCurrentGlobalTimestampOnDevice(timestamp);


                        surveyManager.handleSurveys(r.body().getSurveys());

                    } else {
                        Log.d("MotivAPI", "Error retrieving surveys");
                        Log.d("MotivAPI",r.message() + r.code());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


                }catch (Exception e){

                    Log.e("MotivAPI", "Received null array");

                    return "";
                }


            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setProgressPercent(progress[0]);
            Toast.makeText(context,progress[0],Toast.LENGTH_SHORT).show();
            Log.d("Motiv API", "onpost"+progress);
        }

        protected void onPostExecute(String result) {
            // showDialog("Downloaded " + result + " bytes");
        }
    }

    /**
     * AsyncTask to retrieve user and global stats from the server
     */
    private class MakeGetUserStats extends AsyncTask<Integer, String, String> {

        protected String doInBackground(Integer... params) {
            try {


                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                String firebaseToken = firebaseTokenManager.getLastFirebaseToken();

                Call<GlobalStatsServerResponse> call = motivAPIClientServiceSurveys.getUserStats(
                        "Bearer " + firebaseToken
                        );

                Log.e("MotivAPI--stats",bodyToString(call.request().body()));


                try {
                    Log.d("MotivAPI--stats", "Init downloading stats " + DateTime.now().getMillis());

                    Response<GlobalStatsServerResponse> r = call.execute();

                    Log.d("MotivAPI--stats", "Ended downloading stats " + DateTime.now().getMillis());

                    if (r.isSuccessful()) {
                        Log.d("MotivAPI--stats", "Successfully received stats");

                        UserStatsManager.getInstance().setStats(r.body(), DateTime.now().getMillis());

                    } else {
                        Log.d("MotivAPI--stats", "Error retrieving surveys");
                        Log.d("MotivAPI--stats",r.message() + r.code());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }catch (Exception e){

                Log.e("MotivAPI--stats", "Received null array");
                e.printStackTrace();

                return "";
            }


            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setProgressPercent(progress[0]);
            Toast.makeText(context,progress[0],Toast.LENGTH_SHORT).show();
            Log.d("Motiv API", "onpost"+progress);
        }

        protected void onPostExecute(String result) {
            // showDialog("Downloaded " + result + " bytes");
        }
    }

    /**
     * AsyncTask to retrieve survey data for the specific "Report feedback" survey
     */
    private class MakeGetReportingSurveyRequestTask extends AsyncTask<Void, String, String> {

        protected String doInBackground(Void... params) {
            try {


                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                String firebaseToken = firebaseTokenManager.getLastFirebaseToken();

                Call<GetSurveysResponse> call = motivAPIClientServiceSurveys.getReportingSurvey
                        ("Bearer " + firebaseToken
                        );

                Log.d("MotivAPI", "Trying to retrieve reporting survey from server");

                Log.d("MotivAPI",bodyToString(call.request().body()));

                Response<GetSurveysResponse> r = call.execute();

                if (r.isSuccessful()) {
                    //fcmFirebaseTokenContainer.setSentToServer(true);
                    //FirebaseTokenManager.getInstance(context).setFcmFirebaseTokenContainer(fcmFirebaseTokenContainer);

                    //Log.d("MotivAPI",r.raw().);

                    if(r.body().getSurveys().size() < 1){
                        broadcastErrorInfo(r.code());
                    }else{
                        Log.d("MotivAPI", "Successfully received reporting survey");
                        broadcastReportingSurveyInfo(r.body().getSurveys().get(0));
                    }

                } else {
                    Log.d("MotivAPI", "Error retrieving surveys");
                    broadcastErrorInfo(r.code());
                    Log.d("MotivAPI",r.message() + r.code());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setProgressPercent(progress[0]);
            Toast.makeText(context,progress[0],Toast.LENGTH_SHORT).show();
            Log.d("Motiv API", "onpost"+progress);
        }

        protected void onPostExecute(String result) {
            // showDialog("Downloaded " + result + " bytes");
        }
    }

    public boolean checkIfWeatherStillValid(){

        int threeHoursInMilliseconds = 10800000;

        if(lastWeather != null){
            if(lastWeatherTimestamp > (new DateTime(UTC).getMillis() - threeHoursInMilliseconds)){
                return true;
            }
        }
        return false;
    }

    private void broadcastReportingSurveyInfo(Survey reportingSurvey){

        // Broadcast that the "Report feedback" has been received so that other app
        // components/modules may act on it
        Intent localIntent = new Intent(keys.reportingSurveyBroadcastKey);
        localIntent.putExtra(keys.result, keys.success);
        localIntent.putExtra(keys.reportingSurveyKey, reportingSurvey);
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

    }

    private void broadcastNeedsOnboardingInfo(boolean needsOnBoarding){

        // Broadcast that the the needs onboarding info has been received so that other app
        // components/modules may act on it
        Intent localIntent = new Intent(keys.needsOnboardingBroadcastKey);
        localIntent.putExtra(keys.result, keys.success);
        localIntent.putExtra(keys.needsOnboardingData, needsOnBoarding);
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

    }

    private void broadcastHasCompletedOnboarding(boolean discardedOnboarding){

        // Broadcast that the user has compelted the onboarding so that other app components/modules
        // may act on it
        Intent localIntent = new Intent(keys.onboardingFinishedBroadcastKey);
        localIntent.putExtra(keys.result, keys.success);
        localIntent.putExtra(keys.onboardingDiscarded, discardedOnboarding);
//        localIntent.putExtra(keys.needsOnboardingData, needsOnBoarding);
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

    }

    private void broadcastHasCompletedOnboardingError(){

        // Broadcast that there has been an error completing the onboarding so that other app
        // components/modules may act on it
        Intent localIntent = new Intent(keys.onboardingFinishedBroadcastKey);
        localIntent.putExtra(keys.result, keys.failed);
//        localIntent.putExtra(keys.needsOnboardingData, needsOnBoarding);
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

    }

    private void broadcastNeedsOnboardingErrorInfo(boolean stillLogin){

        // Broadcast that there has been an error retrieving the needs onboarding info so that other
        // app components/modules may act on it
        Intent localIntent = new Intent(keys.needsOnboardingBroadcastKey);
        localIntent.putExtra(keys.result, keys.failed);
        localIntent.putExtra(keys.allowLogin, stillLogin);
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

    }

    private void broadcastWeatherInfo(WeatherResponse weatherResponse, long timestamp){

        // Broadcast that the weather forecast info has been received so that other app
        // components/modules may act on it
        Intent localIntent = new Intent(keys.broadcastKey);
        localIntent.putExtra(keys.result, keys.success);
        localIntent.putExtra(keys.weatherData, weatherResponse);
        localIntent.putExtra(keys.weatherTimestamp, timestamp);
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

    }

    private void broadcastErrorInfo(int code){

        //Broadcast generic error
        Intent localIntent = new Intent(keys.broadcastKey);
        localIntent.putExtra(keys.result, keys.failed);
        localIntent.putExtra(keys.errorCode, code);
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        Log.d("MotivAPI", "Request failed. Code: "+ code);

    }

    public interface keys{

        String broadcastKey = "MotivAPIClientManagerResultBroadcast";
        String result = "result";
        String success = "Success";
        String failed = "Failed";
        String errorCode = "ErrorCode";
        String weatherTimestamp = "weatherTS";
        String weatherData = "weatherData";
        String needsOnBoarding = "needsOnBoarding";

        String broadcasteKeyCampaigns = "MotivAPIClientManagerCampaignsBroadcast";
        String campaignsData = "campaignsData";

        String reportingSurveyKey = "ReportingSurveyKey";
        String reportingSurveyBroadcastKey = "reportingSurveyBroadcastKey";

        String needsOnboardingBroadcastKey = "NeedsOnboardingBroadcastKey";
        String needsOnboardingData = "NeedsOnboardingData";

        String allowLogin = "AllowLogin";

        //finish onboarding stuff

        String onboardingFinishedBroadcastKey = "OnboardingFinishedBroadcastKey";
        String onboardingDiscarded = "OnboardingDiscarded";
    }

    private String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }




}

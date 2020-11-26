package inesc_id.pt.motivandroid.motviAPIClient;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.auth.data.NeedsOnboardingResponse;
import inesc_id.pt.motivandroid.auth.data.UserSchemaServer;
import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.data.tripData.FullTripServer;
import inesc_id.pt.motivandroid.data.rewards.RewardData;
import inesc_id.pt.motivandroid.data.rewards.RewardStatusAllTimeResponse;
import inesc_id.pt.motivandroid.data.rewards.RewardStatusServer;
import inesc_id.pt.motivandroid.data.statsFromServer.GlobalStatsServerResponse;
import inesc_id.pt.motivandroid.data.surveys.tempSurveySubmission.AnsweredReportToServer;
import inesc_id.pt.motivandroid.data.surveys.tempSurveySubmission.AnsweredSurveyToServer;
import inesc_id.pt.motivandroid.data.tripData.TripSummary;
import inesc_id.pt.motivandroid.motviAPIClient.responses.GetSurveysResponse;
import inesc_id.pt.motivandroid.motviAPIClient.responses.TripPostResponse;
import inesc_id.pt.motivandroid.motviAPIClient.responses.UpdateUserDataResponse;
import inesc_id.pt.motivandroid.motviAPIClient.responses.WeatherResponse.response.WeatherResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * MotivAPIClientService
 *
 *  Interface containing all the calls necessary to the motiv rest API
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

public interface MotivAPIClientService {

    /**
     * Send full trip to server.
     *
     * @param token
     * @param fullTripToBeSent
     * @return trip id if request is successful
     */
    @Headers({"Accept: application/json"})
    @POST("trips")
    Call<TripPostResponse> postTrip(
            @Header("Authorization") String token,
            @Body FullTripServer fullTripToBeSent
        );

    /**
     * Request to retrieve the weather forecast.
     * @param token
     * @return WeatherResponse if successful
     */
    @GET("weather")
    Call<WeatherResponse> getWeather(
            @Header("Authorization") String token);


    /**
     * Request to retrieve surveys from server (not yet downloaded from server)
     *
     * @param token
     * @param timestamp last locally available timestamp
     * @return Array of available surveys if successful
     */
    @GET("surveys/{timestamp}/")
    Call<GetSurveysResponse> getSurveys(
            @Header("Authorization") String token,
            @Path("timestamp") int timestamp);


    /**
     * Request to retrieve campaign data from server
     *
     * @param token
     * @param country
     * @param city
     * @return Array of available campaigns encompassing the country and/or city if successful
     */
    @GET("campaigns/{country}/{city}/")
    Call<ArrayList<Campaign>> getCampaignsForCountryCity(
            @Header("Authorization") String token,
            @Path("country") String country,
            @Path("city") String city
    );


    /**
     * Request to retrieve campaigns in which the user is enrolled
     *
     * @param token
     * @return Array of user available campaigns if successful
     */
    @GET("user/campaigns/objects")
    Call<ArrayList<Campaign>> getCampaignObjectsOnCampaigns(
            @Header("Authorization") String token
    );

    /**
     * Request to retrieve the "Report feedback" survey
     * @param token
     * @return Array containing only the "Report feedback" survey
     */
    @GET("surveys/reportingSurvey")
    Call<GetSurveysResponse> getReportingSurvey(
            @Header("Authorization") String token
            );


    /**
     * Request to send the answer to the "Report feedback" survey to the server
     *
     * @param token
     * @param surveyToServer report survey answers
     * @return OK(200) if successful
     */
    @POST("surveys/reporting")
    Call<ResponseBody> sendReportIssue(
            @Header("Authorization") String token,
            @Body AnsweredReportToServer surveyToServer
    );


    /**
     * Request to send an answer to a survey to the server
     *
     * @param token
     * @param surveyToServer survey answers
     * @return OK(200) if successful
     */
    @POST("surveys/answer")
    Call<ResponseBody> sendAnswerToSurvey(
            @Header("Authorization") String token,
            @Body AnsweredSurveyToServer surveyToServer
    );

    /**
     * Request to check if the user with the provided token has already completed the onboarding or
     * not
     *
     * @param token
     * @return true if the user still needs to complete the onboarding process, false otherwise
     */
    //new auth method
    @GET("users/needsonboarding")
    Call<NeedsOnboardingResponse> needsOnboarding(
            @Header("Authorization") String token
    );


    /**
     * Request to update user data
     *
     * @param token
     * @param userData
     * @return same as needsOnboarding call
     */
    @Headers({"Accept: application/json"})
    @PUT("users")
    Call<UpdateUserDataResponse> updateUserData(
            @Header("Authorization") String token,
            @Body UserSchemaServer userData
    );

    /**
     * Request to retrieve user data from server
     * @param token
     * @return User data if successful
     */
    //new auth method
    @GET("users/user")
    Call<UserSchemaServer> getUserData(
            @Header("Authorization") String token
    );

    /** Request to retrieve the reward data for the provided user in @param token
     * @param token
     * @return Array with reward data
     */
    @GET("rewards/my")
    Call<ArrayList<RewardData>> getMyRewardsData(
            @Header("Authorization") String token
    );

//    @Deprecated
//    @PUT("rewards/status")
//    Call<ArrayList<RewardStatusServer>> getMyRewardsStatuses(
//            @Header("Authorization") String token,
//            @Body ArrayList<RewardStatusServer> statuses
//    );


    /**
     * Request to send reward status. The device sends the status (completion status)
     * stored locally
     *
     * @param token
     * @param statuses
     * @return Server returns a list of updated rewards status, if successful
     */
    @PUT("rewards/statusAllTime")
    Call<RewardStatusAllTimeResponse> getMyRewardsStatusesAllTime(
            @Header("Authorization") String token,
            @Body ArrayList<RewardStatusServer> statuses
    );


    /**
     * Request to retrieve both user and global stats
     *
     * @param token
     * @return
     */
    @GET("stats/globalStats")
    Call<GlobalStatsServerResponse> getUserStats(
            @Header("Authorization") String token
    );


    /** Request to send trip summaries to the server
     * @param token
     * @param tripSummaries
     * @return 200(OK) if successful
     */
    @PUT("tripSummaries")
    Call<ResponseBody> sendTripSummaries(
            @Header("Authorization") String token,
            @Body ArrayList<TripSummary> tripSummaries
    );


//    //deprecated auth methods
//    @Headers({"Accept: application/json"})
//    @PUT("users/{userid}/")
//    Call<UpdateUserDataResponse> updateUser(
//            @Header("Authorization") String token,
//            @Path("userid") String userid,
//            @Body UserData userData
//    );
//
//    @POST("users/settings")
//    Call<ResponseBody> updateOnBoardingInformation(
//            @Header("Authorization") String token,
//            @Body UserSettings userSettings
//    );

}
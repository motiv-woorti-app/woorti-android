package inesc_id.pt.motivandroid.auth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.joda.time.DateTime;

import inesc_id.pt.motivandroid.motviAPIClient.MotivAPIClientManager;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.LogsUtil;
import io.fabric.sdk.android.services.events.FileRollOverManager;

import static org.joda.time.DateTimeZone.UTC;

/**
 * FirebaseTokenManager
 *
 *  Manages firebase authentication token.
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

public class FirebaseTokenManager {

    private Context context;
    private static FirebaseTokenManager instance;
    private String lastKnownUID;


    private static String LOG_TAG = LogsUtil.INIT_LOG_TAG + "FBTokenManager";

    public void setLastFirebaseToken(String lastFirebaseToken) {
        this.lastFirebaseToken = lastFirebaseToken;
    }

    public Long getLastFirebaseTokenTimestamp() {
        return lastFirebaseTokenTimestamp;
    }

    public void setLastFirebaseTokenTimestamp(Long lastFirebaseTokenTimestamp) {
        this.lastFirebaseTokenTimestamp = lastFirebaseTokenTimestamp;
    }

    private String lastFirebaseToken;
    private Long lastFirebaseTokenTimestamp;

    private String fcmFirebaseToken;

    FirebaseAuth.AuthStateListener mAuthListener;
    OnCompleteListener<GetTokenResult> onCompleteGetIdToken;

    public synchronized static FirebaseTokenManager getInstance(Context context){

        if(instance == null){
            Log.d("TSM", "instantiating new FirebaseTokenManager");
            instance = new FirebaseTokenManager(context);
        }
        return instance;
    }

    //constructor
    //initializes auth state listener
    private FirebaseTokenManager(Context context){

        this.context = context.getApplicationContext();

        Log.d(LogsUtil.INIT_LOG_TAG, "FTM constructor");

        onCompleteGetIdToken = new OnCompleteListener<GetTokenResult>() {
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    lastFirebaseToken = task.getResult().getToken();
                    lastFirebaseTokenTimestamp = new DateTime(UTC).getMillis();
                    Log.d(LOG_TAG, "Token " + lastFirebaseToken + " from "+ DateHelper.getDateFromTSString(lastFirebaseTokenTimestamp));

                } else {
                    Log.e(LOG_TAG, task.getException().toString());
                    // Handle error -> task.getException();
                }
            }
        };

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.e(LogsUtil.INIT_LOG_TAG, "User equals not null - must have been logged in, trying to get token!");
                    lastKnownUID = user.getUid();
                    user.getIdToken(true).addOnCompleteListener(onCompleteGetIdToken);
                }else{
                    Log.e(LogsUtil.INIT_LOG_TAG, "User equals null - must have been logged out!");
                    lastFirebaseToken = null;
                }
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

        //

    }

    public String getLastFirebaseToken(){
        return lastFirebaseToken;
    }

    public void getAndSetFirebaseToken() {
        try{
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnCompleteListener(onCompleteGetIdToken);
    }catch(NullPointerException e){
            Log.e(LOG_TAG, "Null pointer exception retrieving token with getIdToken");
            lastFirebaseToken = null;
        }
    }

    //FCM firebase token

//    public void setFCMToken(String token){
//        fcmFirebaseToken = token;
//    }

    public void setFcmFirebaseTokenContainer(FCMFirebaseTokenContainer fcmFirebaseTokenContainer) {
        this.fcmFirebaseTokenContainer = fcmFirebaseTokenContainer;
        SharedPreferencesUtil.writeFCMFirebaseToken(context, fcmFirebaseTokenContainer);

    }

    private FCMFirebaseTokenContainer fcmFirebaseTokenContainer;

    public void checkAndSendFCMToken(){

        FCMFirebaseTokenContainer fromSF = SharedPreferencesUtil.readFCMFirebaseToken(context, SharedPreferencesUtil.SHARED_PREFERENCES_FCMTOKEN);
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        fcmFirebaseTokenContainer = new FCMFirebaseTokenContainer(false, fcmToken);

        if(fromSF == null){
            Log.d(LOG_TAG, "No FCM token on disk!");
            if (fcmToken != null){
                SharedPreferencesUtil.writeFCMFirebaseToken(context, fcmFirebaseTokenContainer);
                Log.d(LOG_TAG, "Writing FCM token on disk, trying to send it to the server!");

//                MotivAPIClientManager.getInstance(context).makeUpdatePNTokenRequest(fcmFirebaseTokenContainer); todo uncomment
            }

        }else{

            if(fcmToken != null){

                if(fcmToken.equals(fromSF.getFCMFirebaseToken())){

                    if(!fromSF.isSentToServer()){
                        Log.d(LOG_TAG, "Token not yet sent to server, trying to send!");
                        fcmFirebaseTokenContainer = fromSF;
                        //MotivAPIClientManager.getInstance(context).makeUpdatePNTokenRequest(fcmFirebaseTokenContainer); todo uncomment
                    }else{
                        //MotivAPIClientManager.getInstance(context).makeUpdatePNTokenRequest(fcmFirebaseTokenContainer); todo uncomment

                        Log.d(LOG_TAG, "Same fcm token, already on server, doing nothing!");
                    }
                }

            }

        }

        //if equals FirebaseInstanceId.getInstance().getToken() verify if it is pending...if it is resend to server, else do nothing

    }

    public void onAuthChangedFCMToken(String fcmFirebaseToken){

        Log.d("FTM", fcmFirebaseToken);
        fcmFirebaseTokenContainer = new FCMFirebaseTokenContainer(false, fcmFirebaseToken);
        SharedPreferencesUtil.writeFCMFirebaseToken(context,fcmFirebaseTokenContainer);
//        MotivAPIClientManager.getInstance(context).makeUpdatePNTokenRequest(fcmFirebaseTokenContainer); todo uncomment

    }

    public FCMFirebaseTokenContainer getFcmFirebaseTokenContainer() {
        return fcmFirebaseTokenContainer;
    }

    public String getLastKnownUID() {
        return lastKnownUID;
    }

    public void setLastKnownUID(String lastKnownUID) {
        this.lastKnownUID = lastKnownUID;
    }

}

package inesc_id.pt.motivandroid.onboarding.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import inesc_id.pt.motivandroid.MyContextWrapper;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.auth.FirebaseTokenManager;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.home.activities.HomeDrawerActivity;
import inesc_id.pt.motivandroid.motviAPIClient.MotivAPIClientManager;
import inesc_id.pt.motivandroid.onboarding.fragments.AfterResetPasswordFragment;
import inesc_id.pt.motivandroid.onboarding.fragments.ErrorLoginFragment;
import inesc_id.pt.motivandroid.onboarding.fragments.FirstFragmentBeforeLogin;
import inesc_id.pt.motivandroid.onboarding.fragments.FirstWelcomeBackLoginFragment;
import inesc_id.pt.motivandroid.onboarding.fragments.ForgotPasswordFragment;
import inesc_id.pt.motivandroid.onboarding.fragments.StartNowOrLoginFragment;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.utils.DateHelper;

/**
 * LoginActivity
 *
 *  This is the entry point of the application. Whenever the user launches the app, he is lead to
 *  this activity.
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

public class LoginActivity extends AppCompatActivity
        implements
        FirstFragmentBeforeLogin.OnFragmentInteractionListener,
        StartNowOrLoginFragment.OnFragmentInteractionListener,
        FirstWelcomeBackLoginFragment.OnFragmentInteractionListener,
        ForgotPasswordFragment.OnFragmentInteractionListener,
        AfterResetPasswordFragment.OnFragmentInteractionListener,
        ErrorLoginFragment.OnFragmentInteractionListener{

    FirebaseAuth firebaseAuth;

    FirebaseTokenManager firebaseTokenManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase));
    }
    FrameLayout overlayLayout;


    /**
     * callback called when we successfully retrieve the firebase auth token. Now we need to check if
     * the user has already completed the onboarding of the motiv app. If so, take the user to the
     * app menu. Else, take the user to the StartOrNowOrLoginFragment in which he can go to the
     * onboarding and register
     */
    OnCompleteListener<GetTokenResult> onCompleteGetIdToken = new OnCompleteListener<GetTokenResult>() {
        public void onComplete(@NonNull Task<GetTokenResult> task) {
            if (task.isSuccessful()) {
                FirebaseTokenManager.getInstance(getApplicationContext()).setLastFirebaseToken(task.getResult().getToken());
                FirebaseTokenManager.getInstance(getApplicationContext()).setLastFirebaseTokenTimestamp(task.getResult().getIssuedAtTimestamp());
                Log.d("LoginActivity", "OnComplete Token " + task.getResult().getToken() + " from "+ DateHelper.getDateFromTSString(task.getResult().getIssuedAtTimestamp()));

                //check and update motiv server on the last known firebase auth token
                FirebaseTokenManager.getInstance(getApplicationContext()).checkAndSendFCMToken();

                //get local version (if exists) of the user settings
                UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getApplicationContext(), "notExistent");

                //check with the motiv server if the user has already completed the onboarding
                MotivAPIClientManager.getInstance(getApplicationContext()).CheckIfOnboardingNeeded(userSettingStateWrapper);

                //wait for server response -> callback sendingUserData
                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                        sendingUserData, new IntentFilter(MotivAPIClientManager.keys.needsOnboardingBroadcastKey));

            } else {
                Log.e("LoginActivity", task.getException().toString());
                Toast.makeText(getApplicationContext(), "Unable to connect with server-> Please check your internet connection", Toast.LENGTH_LONG).show();
                FirebaseTokenManager.getInstance(getApplicationContext()).setLastFirebaseToken(null);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment fragment = StartNowOrLoginFragment.newInstance("","");
                ft.replace(R.id.login_main_fragment, fragment);
                ft.commitAllowingStateLoss();
                stopLoading();

            }
        }
    };;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overlayLayout = findViewById(R.id.progress_view);

        printHashKey(getApplicationContext());
        firebaseAuth  = FirebaseAuth.getInstance();
        firebaseTokenManager = FirebaseTokenManager.getInstance(getApplicationContext());

//        TooLargeTool.startLogging(getApplication());

        Log.d("LoginActivity", "onCreate");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        //load fragment that allows the user to either login or start onboarding
        Fragment fragment = StartNowOrLoginFragment.newInstance("","");
        ft.replace(R.id.login_main_fragment, fragment);
        ft.commit();

    }


    /**
     * show circular loading bar
     */
    public void showLoading(){
        overlayLayout.setVisibility(View.VISIBLE);
    }


    /**
     * hide circular loading bar
     */
    public void stopLoading(){
        overlayLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e("LoginActivity", "onStart");

        showLoading();
        //check if user is already logged into firebase
        if(firebaseAuth.getCurrentUser() != null){
            Log.d("LoginActivity", "current != null -> taking user to main menu");
            loginSuccessful();
        }

        else
            stopLoading();

        }



    public void goToLoginPage(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = FirstWelcomeBackLoginFragment.newInstance("","");
        ft.replace(R.id.login_main_fragment, fragment).addToBackStack("FirstWelcomeBackLoginFragment");
        ft.commit();

    }

    public void goToForgotPasswordFragment(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = ForgotPasswordFragment.newInstance("","");
        ft.replace(R.id.login_main_fragment, fragment).addToBackStack("OutOfLogin");
        ft.commit();

    }

    public void goToAfterResetPassword() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = AfterResetPasswordFragment.newInstance("","");
        ft.replace(R.id.login_main_fragment, fragment);
        ft.commitAllowingStateLoss();

    }

    public void goBackToLogin() {

        getSupportFragmentManager().popBackStack("OutOfLogin",
                FragmentManager.POP_BACK_STACK_INCLUSIVE);

//    getSupportFragmentManager().popBackStack();

    }


    public void goToAuthenticationFailedScreen() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = ErrorLoginFragment.newInstance("","");
        ft.replace(R.id.login_main_fragment, fragment).addToBackStack("OutOfLogin");
        ft.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {



    }



    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("LoginActivity", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("LoginActivity", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("LoginActivity", "printHashKey()", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.login_main_fragment);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                sendingUserData);

    }


    /**
     * method called by all the onboarding/login components whenever the user has successfully
     * been logged into firebase.
     */
    public void loginSuccessful() {

            tryToGetTokenAndCheckIfOnboardingDone();

    }


    /**
     * after being logged in to firebase, let's check if he is already registered with our app (e.g.
     * if you log in with google credentials you get logged in to firebase but may have not registered
     * with the motiv app itself)
     */
    public void tryToGetTokenAndCheckIfOnboardingDone(){
        try {
            FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnCompleteListener(onCompleteGetIdToken);


    }catch(NullPointerException e){
        Log.e("LoginActivity", "Null pointer exception retrieving token with getIdToken");
        FirebaseTokenManager.getInstance(getApplicationContext()).setLastFirebaseToken(null);
        stopLoading();
    }
    }




    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "MotivAPIClientManagerResultBroadcast" is broadcasted.
    // Called when we receive the response from server stating if the user still needs to complete
    // the onboarding
    private BroadcastReceiver sendingUserData = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("receiver", "Received message broadcasted from MotivAPIClientManager");

            String action = intent.getStringExtra(MotivAPIClientManager.keys.result);

            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(this);

            if(action.equals(MotivAPIClientManager.keys.success)){

                boolean needsOnBoarding = intent.getBooleanExtra(MotivAPIClientManager.keys.needsOnboardingData, true);

//                Toast.makeText(getApplicationContext(), "Logged in successfully - Needs onboarding->" + needsOnBoarding, Toast.LENGTH_LONG).show();

                stopLoading();

                if(needsOnBoarding){
                    //onboarding has not been complete yet -> take the user to the onboarding activity
                    Intent onboardingIntent = new Intent(getApplicationContext(),OnboardingActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    onboardingIntent.putExtra(keys.hasRegistered, true);
                    startActivity(onboardingIntent);
                }else{
                    //onboarding has already been completed -> take user to the app main menu
                    startActivity(new Intent(getApplicationContext(),HomeDrawerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }

                finish();

            }else{

                int errorCode = intent.getIntExtra(MotivAPIClientManager.keys.errorCode, 0);

                boolean allowLogin = intent.getBooleanExtra(MotivAPIClientManager.keys.allowLogin, false);

                stopLoading();

                if(allowLogin){
                    //has connection but the server has failed to reply -> allow the user to go to
                    //the main menu but in offline mode
                    Toast.makeText(getApplicationContext(), "Offline mode.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),HomeDrawerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Login Failed with code "+ errorCode, Toast.LENGTH_LONG).show();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    Fragment fragment = StartNowOrLoginFragment.newInstance("","");
                    ft.replace(R.id.login_main_fragment, fragment);
                    ft.commitAllowingStateLoss();
                }

            }

        }
    };
    public void goToOnboardingStartNow(){
        Intent onboardingIntent = new Intent(getApplicationContext(),OnboardingActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        onboardingIntent.putExtra(keys.hasRegistered, false);
        startActivity(onboardingIntent);

    }

    public interface keys{

        String hasRegistered = "HAS_REGISTERED";

    }

}

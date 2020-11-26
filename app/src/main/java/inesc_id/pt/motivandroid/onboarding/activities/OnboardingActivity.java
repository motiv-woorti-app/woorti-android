package inesc_id.pt.motivandroid.onboarding.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.auth.FirebaseTokenManager;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettings;
import inesc_id.pt.motivandroid.home.activities.HomeDrawerActivity;
import inesc_id.pt.motivandroid.motviAPIClient.MotivAPIClientManager;
import inesc_id.pt.motivandroid.onboarding.fragments.AfterResetPasswordFragment;
import inesc_id.pt.motivandroid.onboarding.fragments.ErrorLoginFragment;
import inesc_id.pt.motivandroid.onboarding.fragments.FirstWelcomeBackLoginFragment;
import inesc_id.pt.motivandroid.onboarding.fragments.ForgotPasswordFragment;
import inesc_id.pt.motivandroid.onboarding.fragments.FragmentWelcomeOnboardingRegister;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding10Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding11Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding12Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding13Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding14Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding15Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding16Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding1Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding2Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding3BodyAndHealthFragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding3Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding3MindAndPleasureFragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding3ProductivityFragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding4Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding5Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding6_7Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding8Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding9Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.OnboardingFragment1_1;
import inesc_id.pt.motivandroid.onboarding.fragments.SignUpFailedFragment;
import inesc_id.pt.motivandroid.onboarding.wrappers.ModeOfTransportUsed;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.SetLanguageFragment;

/**
 * OnboardingActivity
 *
 *  In this activity, the user goes through the onboarding process. The onboarding flow goes as
 *  follows:
 *
 *  1-Onboarding1Fragment (choose app language)
 *  2-OnboardingFragment1_1 ("Help research for better transport")
 *  3-Onboarding2Fragment ("What is a worthwhile trip")
 *  4-Onboarding3Fragment ("What is a worthwhile trip")
 *  5-Onboarding3ProductivityFragment ("What is a worthwhile trip" - what is productivity)
 *  6-Onboarding3MindAndPleasureFragment ("What is a worthwhile trip" - what is enjoyment)
 *  7-Onboarding3BodyAndHealthFragment ("What is a worthwhile trip" - what is fitness)
 *  8-Onboarding4Fragment ("How important are the travel time worthwhileness elements for you, when
 *  you travel?")
 *  9-Onboarding5Fragment (select the modes of transport regularly used)
 *  10-Onboarding6_7Fragment ("How productive are you when you travel by:")
 *  11-Onboarding6_7Fragment ("How much enjoyment do you get from your travel time by:")
 *  12-Onboarding6_7Fragment ("How does your travel time contribute to your fitness when you travel by:")
 *  (if user is already authenticated, jump to 14 (privacy and data protection), otherwise go to 13
 *  (authentication))
 *  13-FragmentWelcomeOnboardingRegister (register)
 *  14-Onboarding8Fragment (accept Privacy and data protection policies)
 *  15-Onboarding9Fragment (Asks the users to accept the needed permissions)
 *  16-Onboarding10Fragment (A little about you - user name)
 *  17-Onboarding11Fragment (A little about you - user country)
 *  18-Onboarding12Fragment (A little about you - user city)
 *  (if there are no campaigns encompassing the chosen location, jump to 20)
 *  19-Onboarding13Fragment (choose campaigns)
 *  20-Onboarding14Fragment (A little about you - user age range)
 *  21-Onboarding15Fragment (A little about you - user gender)
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
public class OnboardingActivity extends AppCompatActivity implements Onboarding1Fragment.OnFragmentInteractionListener, Onboarding2Fragment.OnFragmentInteractionListener,
        Onboarding3Fragment.OnFragmentInteractionListener, Onboarding4Fragment.OnFragmentInteractionListener, Onboarding5Fragment.OnFragmentInteractionListener,
        Onboarding6_7Fragment.OnFragmentInteractionListener, Onboarding8Fragment.OnFragmentInteractionListener, Onboarding9Fragment.OnFragmentInteractionListener,
        Onboarding10Fragment.OnFragmentInteractionListener, Onboarding11Fragment.OnFragmentInteractionListener, Onboarding12Fragment.OnFragmentInteractionListener,
        Onboarding13Fragment.OnFragmentInteractionListener, Onboarding14Fragment.OnFragmentInteractionListener, Onboarding15Fragment.OnFragmentInteractionListener,
        Onboarding16Fragment.OnFragmentInteractionListener, Onboarding3BodyAndHealthFragment.OnFragmentInteractionListener, Onboarding3MindAndPleasureFragment.OnFragmentInteractionListener,
        Onboarding3ProductivityFragment.OnFragmentInteractionListener, OnboardingFragment1_1.OnFragmentInteractionListener,
        FragmentWelcomeOnboardingRegister.OnFragmentInteractionListener, SignUpFailedFragment.OnFragmentInteractionListener, AfterResetPasswordFragment.OnFragmentInteractionListener,
        ForgotPasswordFragment.OnFragmentInteractionListener, FirstWelcomeBackLoginFragment.OnFragmentInteractionListener, ErrorLoginFragment.OnFragmentInteractionListener,
        SetLanguageFragment.OnFragmentInteractionListener {

    UserSettings userSettings;
    boolean hasRegistered;

    FirebaseAuth firebaseAuth;
    FirebaseTokenManager firebaseTokenManager;

    GoogleSignInClient mGoogleSignInClient;

    private static int RC_SIGN_IN = 100;

    FrameLayout overlayLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        overlayLayout = findViewById(R.id.progress_view);


        userSettings = new UserSettings();
        hasRegistered = getIntent().getBooleanExtra(LoginActivity.keys.hasRegistered, false);

        firebaseAuth  = FirebaseAuth.getInstance();
        firebaseTokenManager = FirebaseTokenManager.getInstance(getApplicationContext());

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = Onboarding1Fragment.newInstance();
        ft.replace(R.id.onboarding_main_fragment, fragment);
        ft.commit();

    }

    public boolean isRegistered(){
        return hasRegistered;
    };

    public void saveLanguage(String lang){
        userSettings.setLang(lang);
    }

    public String getLanguage(){
        return userSettings.getLang();
    }

    public void saveUsedModesValues(ArrayList<ModeOfTransportUsed> modeOfTransportUsedValues){
        userSettings.setPreferedModesOfTransport(modeOfTransportUsedValues);
    }

    public void saveProductivityRelaxingActivity(int prod, int rel, int act){
        userSettings.setProductivityValue(prod);
        userSettings.setRelaxingValue(rel);
        userSettings.setActivityValue(act);
    }

    public void saveName(String name) {
        userSettings.setName(name);
    }

    public void saveCity(String city){
        userSettings.setCity(city);
    }

    public void saveCountry(String country){
        userSettings.setCountry(country);
    }

    public void saveAge(int minAge, int maxAge){
        userSettings.setMinAge(minAge);
        userSettings.setMaxAge(maxAge);
    }

    public void saveDegree(String degree){
        userSettings.setDegree(degree);
    }

    public void saveGender(String gender){
        userSettings.setGender(gender);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void saveOnCampaignsSelected(ArrayList<String> selectedCampaigns){

        onCampaigns.addAll(selectedCampaigns);
    }

    public ArrayList<String> onCampaigns = new ArrayList<>();

    public void completeOnBoarding(){

        //write onboarding user setting to disk
        UserSettingStateWrapper data = new UserSettingStateWrapper(userSettings, FirebaseAuth.getInstance().getUid(), false, onCampaigns);


        //try to send to server
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                messageFromMotivAPI, new IntentFilter(MotivAPIClientManager.keys.onboardingFinishedBroadcastKey));


        showLoading();
//
        MotivAPIClientManager.getInstance(getApplicationContext()).makeOverwriteOnboardingRequest(data);

    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "ActivityRecognitionResult" is broadcasted.
    private BroadcastReceiver messageFromMotivAPI = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            stopLoading();

            Log.e("receiver", "Received message broadcasted from RouteRankClientManager");

            String action = intent.getStringExtra(MotivAPIClientManager.keys.result);

            if(action.equals(MotivAPIClientManager.keys.success)){

                if (intent.getBooleanExtra(MotivAPIClientManager.keys.onboardingDiscarded, false)){
                    Toast.makeText(getApplicationContext(), getString(R.string.Onboarding_Duplicated), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.Onboarding_Successful), Toast.LENGTH_LONG).show();
                }

                Intent intentStartHomeDrawer = new Intent(getApplicationContext(),HomeDrawerActivity.class);
                intentStartHomeDrawer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                        messageFromMotivAPI);

                startActivity(intentStartHomeDrawer);

            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.Onboarding_Error), Toast.LENGTH_LONG).show();

                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                        messageFromMotivAPI);
            }

        }
    };

    public void goToRegisterOrPermissions() {


        Log.e("Onboarding activity", "has registered" + hasRegistered);
        if(hasRegistered){

            Log.e("Onboarding activity", "if has registered" + hasRegistered);


            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = Onboarding8Fragment.newInstance();
            ft.replace(R.id.onboarding_main_fragment, fragment).addToBackStack(null);
            ft.commit();

        }else{

            Log.e("Onboarding activity", "if has not registered" + hasRegistered);


            // else take user to register string
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = FragmentWelcomeOnboardingRegister.newInstance("","");
            ft.replace(R.id.onboarding_main_fragment, fragment).addToBackStack("GoToRegister");
            ft.commit();

//            FragmentWelcomeOnboardingRegister nextFragment = FragmentWelcomeOnboardingRegister.newInstance("","");
//            FragmentTransaction nextTransaction=getSupportFragmentManager().beginTransaction();
//            nextTransaction.replace(R.id.onboarding_main_fragment, nextFragment).addToBackStack(null);
//            nextTransaction.commit();
        }

    }

    public void goToLoginPageSignUpFailed(String localizedMessage) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = SignUpFailedFragment.newInstance(localizedMessage);
        ft.replace(R.id.onboarding_main_fragment, fragment).addToBackStack(null);
        ft.commit();

    }

    public void goToLoginPageAlreadySignedUp(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = FirstWelcomeBackLoginFragment.newInstance("","");
        ft.replace(R.id.onboarding_main_fragment, fragment).addToBackStack(null);
        ft.commit();

    }

    /////////////////////////////////////////////////////////////////////////////////
    //Auth part
    public void goToForgotPasswordFragment(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = ForgotPasswordFragment.newInstance("","");
        ft.replace(R.id.onboarding_main_fragment, fragment).addToBackStack("OutOfLogin");
        ft.commit();

    }

    public void goToAfterResetPassword() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = AfterResetPasswordFragment.newInstance("","");
        ft.replace(R.id.onboarding_main_fragment, fragment);
        ft.commit();

    }

    public void goBackToLogin() {

        getSupportFragmentManager().popBackStack("OutOfLogin",
                FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

    public void proceedWithOnboardingIfSuccessful(){

        getSupportFragmentManager().popBackStack("GoToRegister",
                FragmentManager.POP_BACK_STACK_INCLUSIVE);


        hasRegistered = true;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = Onboarding8Fragment.newInstance();
        ft.replace(R.id.onboarding_main_fragment, fragment).addToBackStack(null);
        ft.commit();

    }

    public void goToAuthenticationFailedScreen() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = ErrorLoginFragment.newInstance("","");
        ft.replace(R.id.onboarding_main_fragment, fragment).addToBackStack("OutOfLogin");
        ft.commit();

    }

    public void signInWithGoogle() {

        showLoading();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Log.e("FacebookRC", "" + RC_FB_SIGN_IN);

        Log.e("Rq code", "" + requestCode);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if ((requestCode == RC_SIGN_IN) && (resultCode == Activity.RESULT_OK)){

        if (requestCode == RC_SIGN_IN) {

            Log.e("Google", "onActResult()");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.e("LoginActivity", "Google sign in sucess, authenticate with firebase");

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("LoginActivity", "Google sign in failed", e);

                stopLoading();

                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.Failed_To_Connect_Check_Connectivity_Error), Toast.LENGTH_LONG).show();

            }
//        }
//        else if(requestCode == RC_FB_SIGN_IN){
//
//            Log.e("Facebook", "onActResult()");
//            // Pass the activity result back to the Facebook SDK
//            mCallbackManager.onActivityResult(requestCode, resultCode, data);
//
//        }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]

        // [END_EXCLUDE]

        showLoading();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        stopLoading();

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("LoginFragmentOld", "signInWithGoogle:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            firebaseTokenManager.getAndSetFirebaseToken();

                            proceedWithOnboardingIfSuccessful();

//                            Intent intent = new Intent(getActivity(), MainActivity.class);
//                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("LoginFragmentOld", "signInWithGoogle:failure", task.getException());
                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.Failed_To_Connect_Check_Connectivity_Error), Toast.LENGTH_LONG).show();

                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }

    public void signUpUsingCredentials(String email, String password) {

            showLoading();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            stopLoading();

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Register Fragment", "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(getApplicationContext(), "Account created successfully.!",
                                        Toast.LENGTH_SHORT).show();

                                firebaseTokenManager.getAndSetFirebaseToken();


                                proceedWithOnboardingIfSuccessful();

                            } else {
                                // If sign in fails, display a message to the user.

//                                ((FirebaseAuthException) e).getErrorCode());


                                    try {
                                        FirebaseException e = (FirebaseException) task.getException();

                                        Log.w("LoginPasswordFragment", "createUserWithEmail:failure", task.getException());

                                        if (e.getMessage().contains("[ 7: ]")){
                                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.Failed_To_Connect_Check_Connectivity_Error), Toast.LENGTH_LONG).show();
                                        }else{
                                            goToLoginPageSignUpFailed(e.getLocalizedMessage());
                                        }

                                    }catch (Exception npe){
                                                goToLoginPageSignUpFailed("Authentication failed");
                                    }

                            }

                        }
                    });


        }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                messageFromMotivAPI);


    }

    public void signInUsingCredentials(String email, String password) {

        showLoading();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        stopLoading();

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LoginPasswordFragment", "signInWithEmail:success");

                            Toast.makeText(getApplicationContext(), "Log in successful!",
                                    Toast.LENGTH_SHORT).show();


                            proceedWithOnboardingIfSuccessful();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginPasswordFragment", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            goToAuthenticationFailedScreen();
                        }

                        // ...
                    }
                });
    }

    public void showLoading(){
        overlayLayout.setVisibility(View.VISIBLE);
    }
    public void stopLoading(){
        overlayLayout.setVisibility(View.INVISIBLE);
    }

    }


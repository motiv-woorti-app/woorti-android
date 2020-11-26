package inesc_id.pt.motivandroid.onboarding.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.auth.FirebaseTokenManager;
import inesc_id.pt.motivandroid.onboarding.activities.LoginActivity;
import inesc_id.pt.motivandroid.onboarding.activities.OnboardingActivity;
import inesc_id.pt.motivandroid.utils.MiscUtils;

/**
 * FirstWelcomeBackLoginFragment
 *
 *  In this fragment, the user is presented with the options of logging in using email or google
 *  google credentials. The user may also go to the "Forgot Password?" fragment.
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
public class FirstWelcomeBackLoginFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static int RC_SIGN_IN = 100;

    private static int RC_FB_SIGN_IN;

    // Set the dimensions of the sign-in button.
//    Button googleSignInButton;
    SignInButton googleSignInButton;
    GoogleSignInClient mGoogleSignInClient;
    Button googleSignInButtonContainer;

//    LoginButton fbLoginButton;
//    CallbackManager mCallbackManager;
    Button facebookLoginButtonContainer;

    FirebaseTokenManager firebaseTokenManager;

    private FirebaseAuth mAuth;

    boolean validEmail = false;
    boolean validPassword = false;

    EditText emailEditText;
    EditText passwordEditText;

    TextView forgotPasswordTextView;

    private OnFragmentInteractionListener mListener;

    public FirstWelcomeBackLoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstWelcomeBackLoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstWelcomeBackLoginFragment newInstance(String param1, String param2) {
        FirstWelcomeBackLoginFragment fragment = new FirstWelcomeBackLoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

//        mCallbackManager = CallbackManager.Factory.create();

        mAuth = FirebaseAuth.getInstance();

        firebaseTokenManager = FirebaseTokenManager.getInstance(getContext());



    }

    Button loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login_welcome_back_first_fragment_layout, container, false);

        emailEditText =  view.findViewById(R.id.loginEmailEditText);
        passwordEditText = view.findViewById(R.id.loginPasswordEditText);

        emailEditText.addTextChangedListener(checkEmailEditText);
        passwordEditText.addTextChangedListener(checkPasswordEditText);

        loginButton = view.findViewById(R.id.loginButton);
        loginButton.setAlpha(0.35f);
        loginButton.setClickable(false);

        loginButton.setOnClickListener(this);

        googleSignInButton = view.findViewById(R.id.sign_in_button);
//        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
//        googleSignInButton.setOnClickListener(this);

//        fbLoginButton = view.findViewById(R.id.login_button);
//        fbLoginButton.setReadPermissions("email");

//        facebookLoginButtonContainer = view.findViewById(R.id.login_facebook_button);
//        facebookLoginButtonContainer.setOnClickListener(this);

        forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView);
        forgotPasswordTextView.setPaintFlags(forgotPasswordTextView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        forgotPasswordTextView.setOnClickListener(this);

//        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.e("LoginFragmentOld-fb", "facebook:onSuccess:" + loginResult);
//                handleFacebookAccessToken(loginResult.getAccessToken());
////                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
////                Log.d(TAG, "facebook:onCancel");
//                // ...
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.e("LoginFragmentOld-fb", "facebook:onError", error);
//                // ...
//            }
//        });
//
//        RC_FB_SIGN_IN = fbLoginButton.getRequestCode();

        googleSignInButtonContainer = view.findViewById(R.id.sign_in_button_custom);
        googleSignInButtonContainer.setOnClickListener(this);

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        return view;
    }

    private TextWatcher checkEmailEditText = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            validEmail = MiscUtils.isEmailValid(s.toString());
            checkEmailAndPassword();

        }
    };

    private boolean checkEmailAndPassword() {

        if(validEmail && validPassword){
            loginButton.setAlpha(1);
            loginButton.setClickable(true);
            return true;
        }else{
            loginButton.setAlpha(0.35f);
            loginButton.setClickable(false);
            return false;
        }

    }

    private TextWatcher checkPasswordEditText = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            validPassword = MiscUtils.checkPasswordValidityLogIn(s.toString());
            checkEmailAndPassword();

        }
    };

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInUsingCredentials() {

        if(checkEmailAndPassword()){

            startDialog();

            mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            stopDialog();

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LoginPasswordFragment", "signInWithEmail:success");

                                if(getActivity() != null){
                                    Toast.makeText(getActivity(), "Log in successful!",
                                            Toast.LENGTH_SHORT).show();
                                }



                                if(getActivity() instanceof LoginActivity){
                                    ((LoginActivity) getActivity()).loginSuccessful();
                                }

                                if(getActivity() instanceof OnboardingActivity){

                                    ((OnboardingActivity) getActivity()).proceedWithOnboardingIfSuccessful();

                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LoginPasswordFragment", "signInWithEmail:failure", task.getException());

//                                if(getActivity() != null) {
//
//                                    Toast.makeText(getActivity(), "Authentication failed.",
//                                            Toast.LENGTH_SHORT).show();
//                                }


                                try{
                                    FirebaseException e = (FirebaseException) task.getException();

                                    if (e.getMessage().contains("[ 7: ]")){
                                        Toast.makeText(getContext(), getContext().getString(R.string.Failed_To_Connect_Check_Connectivity_Error), Toast.LENGTH_LONG).show();
                                    }else{

                                        if(getActivity() instanceof LoginActivity){
                                            ((LoginActivity)getActivity()).goToAuthenticationFailedScreen();
                                        }

                                        if(getActivity() instanceof OnboardingActivity){
                                            ((OnboardingActivity)getActivity()).goToAuthenticationFailedScreen();
                                        }
//                                        goToLoginPageSignUpFailed(e.getLocalizedMessage());
                                    }

                                }catch (Exception e){

                                    if(getActivity() instanceof LoginActivity){
                                        ((LoginActivity)getActivity()).goToAuthenticationFailedScreen();
                                    }

                                    if(getActivity() instanceof OnboardingActivity){
                                        ((OnboardingActivity)getActivity()).goToAuthenticationFailedScreen();
                                    }

                                }






                            }

                            // ...
                        }
                    });

        }


    }

    /**
     * deal wth google OAuth response
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("FacebookRC", ""+RC_FB_SIGN_IN);

        Log.e("Rq code", ""+requestCode );

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if ((requestCode == RC_SIGN_IN) && (resultCode == Activity.RESULT_OK)){

        if (requestCode == RC_SIGN_IN){

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
//                Log.e("LoginActivity", "Google sign in failed msg " +  e.getMessage());


                Toast.makeText(getContext(), getContext().getString(R.string.Failed_To_Connect_Check_Connectivity_Error), Toast.LENGTH_LONG).show();

            }
        }else if(requestCode == RC_FB_SIGN_IN){

            Log.e("Facebook", "onActResult()");
            // Pass the activity result back to the Facebook SDK
//            mCallbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]

        // [END_EXCLUDE]

        startDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        stopDialog();

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("LoginFragmentOld", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            firebaseTokenManager.getAndSetFirebaseToken();

                            if(getActivity() instanceof LoginActivity){
                                ((LoginActivity)getActivity()).loginSuccessful();
                            }

                            if(getActivity() instanceof OnboardingActivity){
                                ((OnboardingActivity)getActivity()).proceedWithOnboardingIfSuccessful();
                            }

//                            Intent intent = new Intent(getActivity(), MainActivity.class);
//                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("LoginFragmentOld", "signInWithCredential:failure", task.getException());
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }

//    private void handleFacebookAccessToken(AccessToken token) {
//        Log.d("FacebookLoginFragment", "handleFacebookAccessToken:" + token);
//
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("FacebookLoginFragment", "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            firebaseTokenManager.getAndSetFirebaseToken();
//
//                            if(getActivity() instanceof LoginActivity){
//                                ((LoginActivity)getActivity()).loginSuccessful();
//                            }
//
//                            if(getActivity() instanceof OnboardingActivity){
//                                ((OnboardingActivity)getActivity()).proceedWithOnboardingIfSuccessful();
//                            }
//
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("FacebookLoginFragment", "signInWithCredential:failure", task.getException());
//                            Toast.makeText(getContext(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//
//                        }
//
//                        // ...
//                    }
//                });
//    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign_in_button_custom:
                signInWithGoogle();
                break;
//            case R.id.login_facebook_button:
////                fbLoginButton.performClick();
//                break;
            case R.id.loginButton:
                signInUsingCredentials();
                break;
            case R.id.forgotPasswordTextView:

                if(getActivity() instanceof LoginActivity){
                    ((LoginActivity)getActivity()).goToForgotPasswordFragment();
                }

                if(getActivity() instanceof OnboardingActivity){
                    ((OnboardingActivity)getActivity()).goToForgotPasswordFragment();
                }

                break;
            case R.id.backButton:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }

    }

    //show loading bar
    private void startDialog(){

        if (getActivity() != null) {

            if (getActivity() instanceof LoginActivity) {
                ((LoginActivity) getActivity()).showLoading();
            }

            if (getActivity() instanceof OnboardingActivity) {
                ((OnboardingActivity) getActivity()).showLoading();
            }
        }

    }

    //hide loading bar
    private void stopDialog(){

        if (getActivity() != null) {

            if (getActivity() instanceof LoginActivity) {
                ((LoginActivity) getActivity()).stopLoading();
            }

            if (getActivity() instanceof OnboardingActivity) {
                ((OnboardingActivity) getActivity()).stopLoading();
            }
        }


    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

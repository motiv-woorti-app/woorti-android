package inesc_id.pt.motivandroid.onboarding.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.auth.FirebaseTokenManager;
import inesc_id.pt.motivandroid.onboarding.activities.LoginActivity;
import inesc_id.pt.motivandroid.onboarding.activities.OnboardingActivity;
import inesc_id.pt.motivandroid.utils.MiscUtils;

/**
 * FragmentWelcomeOnboardingRegister
 *
 *  This fragment is presented to the user mid onboarding if the user is not yet registered yet.
 *  Allows the user to register using email or google credentials
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
public class FragmentWelcomeOnboardingRegister extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentWelcomeOnboardingRegister() {
        // Required empty public constructor
    }

    private static int RC_SIGN_IN = 100;

    private static int RC_FB_SIGN_IN;

    // Set the dimensions of the sign-in button.
    // Button googleSignInButton;
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
    boolean validConfirmPassword = false;

    EditText emailEditText;
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    TextView alreadySignedUpTextView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentWelcomeOnboardingRegister.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentWelcomeOnboardingRegister newInstance(String param1, String param2) {
        FragmentWelcomeOnboardingRegister fragment = new FragmentWelcomeOnboardingRegister();
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

    Button signUpButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login_welcome_onboarding_register_fragment_layout, container, false);

        emailEditText =  view.findViewById(R.id.loginEmailEditText);
        passwordEditText = view.findViewById(R.id.loginPasswordEditText);

        emailEditText.addTextChangedListener(checkEmailEditText);
        passwordEditText.addTextChangedListener(checkPasswordEditText);
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && (passwordEditText.getText().toString().length()>16 || passwordEditText.getText().toString().length()<6)){
                    Toast.makeText(getContext(),"Password must be 6-16 characters long",Toast.LENGTH_SHORT).show();
                }
            }
        });

        confirmPasswordEditText = view.findViewById(R.id.loginConfirmPasswordEditText);
        confirmPasswordEditText.addTextChangedListener(checkConfirmPasswordEditText);
        confirmPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && (confirmPasswordEditText.getText().toString().length()>16 || confirmPasswordEditText.getText().toString().length()<6)){
                    Toast.makeText(getContext(),"Password must be 6-16 characters long",Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpButton = view.findViewById(R.id.signUpButton);
        signUpButton.setAlpha(0.35f);

        signUpButton.setOnClickListener(this);
//        signUpButton.setClickable(false);

        googleSignInButton = view.findViewById(R.id.sign_in_button);
//        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
//        googleSignInButton.setOnClickListener(this);

//        fbLoginButton = view.findViewById(R.id.login_button);
//        fbLoginButton.setReadPermissions("email");
//
//        facebookLoginButtonContainer = view.findViewById(R.id.login_facebook_button);
//        facebookLoginButtonContainer.setOnClickListener(this);

        alreadySignedUpTextView = view.findViewById(R.id.alreadySignedUpTextView);
        alreadySignedUpTextView.setOnClickListener(this);



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
//
//        googleSignInButtonContainer = view.findViewById(R.id.sign_in_button_custom);
//        googleSignInButtonContainer.setOnClickListener(this);

        googleSignInButtonContainer = view.findViewById(R.id.sign_in_button_custom);
        googleSignInButtonContainer.setOnClickListener(this);

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
//                        } else {
//                            // If sign in fails, display a message to the user.
//
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




    private TextWatcher checkPasswordEditText = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            validPassword = MiscUtils.checkPasswordValiditySignUp(s.toString());
            checkEmailAndPassword();


        }
    };

    private TextWatcher checkConfirmPasswordEditText = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            validConfirmPassword= MiscUtils.checkPasswordValiditySignUp(s.toString());
            checkEmailAndPassword();

        }
    };

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
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign_in_button_custom:

                if(getActivity() instanceof OnboardingActivity){
                    ((OnboardingActivity)getActivity()).signInWithGoogle();
                }
//                v.setEnabled(false);


                break;
//            case R.id.login_facebook_button:
////                fbLoginButton.performClick();
//                break;
            case R.id.signUpButton:

                if((passwordEditText.getText().toString() == null || passwordEditText.getText().toString().length() == 0) ||
                        ((emailEditText.getText().toString() == null) || emailEditText.getText().toString().length() == 0)){

                    Toast.makeText(getContext(), "Fields must be filled", Toast.LENGTH_SHORT).show();


                }else {

                    if((passwordEditText.getText().toString().length() > 16) ||
                            (passwordEditText.getText().toString().length() < 6) ||
                                (confirmPasswordEditText.getText().toString().length() > 16) ||
                                    (confirmPasswordEditText.getText().toString().length() < 6)){

                        Toast.makeText(getContext(), getString(R.string.Password_Length_Message), Toast.LENGTH_SHORT).show();
                        return;

                    }

                    if (passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {

                        if (getActivity() instanceof OnboardingActivity) {
                            ((OnboardingActivity) getActivity()).signUpUsingCredentials(emailEditText.getText().toString(), passwordEditText.getText().toString());
                        }

                    } else {
                        Toast.makeText(getContext(), "Passwords must match!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.forgotPasswordTextView:

                if(getActivity() instanceof OnboardingActivity){
                    ((OnboardingActivity)getActivity()).goToForgotPasswordFragment();
                }

                break;
            case R.id.backButton:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            case R.id.alreadySignedUpTextView:

                ((OnboardingActivity)getActivity()).goToLoginPageAlreadySignedUp();

                break;
        }

    }

    private boolean checkEmailAndPassword() {


        if(validEmail && validPassword && validConfirmPassword){
            signUpButton.setAlpha(1);
//            signUpButton.setClickable(true);
            return true;
        }else{
            signUpButton.setAlpha(0.35f);
//            signUpButton.setClickable(false);
            return false;
        }

    }

//    private void signInWithGoogle() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

package inesc_id.pt.motivandroid.onboarding.fragments;

import android.content.Context;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.auth.FirebaseTokenManager;
import inesc_id.pt.motivandroid.onboarding.activities.LoginActivity;
import inesc_id.pt.motivandroid.onboarding.activities.OnboardingActivity;
import inesc_id.pt.motivandroid.utils.MiscUtils;

/**
 * ErrorLoginFragment
 *
 *  This fragment is shown to the user in case there was an error logging in. An error message is
 *  presented, and the user is given a new opportunity to log in. This fragment is used by both the
 *  LoginActivity and the OnboardingActivity
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
public class ErrorLoginFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ErrorLoginFragment() {
        // Required empty public constructor
    }

    FirebaseTokenManager firebaseTokenManager;

    private FirebaseAuth mAuth;

    boolean validEmail = false;
    boolean validPassword = false;

    EditText emailEditText;
    EditText passwordEditText;

    TextView forgotPasswordTextView;

    Button loginButton;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ErrorLoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ErrorLoginFragment newInstance(String param1, String param2) {
        ErrorLoginFragment fragment = new ErrorLoginFragment();
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

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login_error_fragment_layout, container, false);

        emailEditText =  view.findViewById(R.id.loginEmailEditText);
        passwordEditText = view.findViewById(R.id.loginPasswordEditText);

        emailEditText.addTextChangedListener(checkEmailEditText);
        passwordEditText.addTextChangedListener(checkPasswordEditText);

        loginButton = view.findViewById(R.id.loginButton);
        loginButton.setAlpha(0.35f);
        loginButton.setClickable(false);

        loginButton.setOnClickListener(this);

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView);
        forgotPasswordTextView.setPaintFlags(forgotPasswordTextView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        forgotPasswordTextView.setOnClickListener(this);



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

                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "Log in successful!",
                                            Toast.LENGTH_SHORT).show();
                                }


                                if(getActivity() instanceof LoginActivity){
                                    ((LoginActivity) getActivity()).loginSuccessful();
                                }

                                if(getActivity() instanceof OnboardingActivity){
                                    ((OnboardingActivity) getActivity()).proceedWithOnboardingIfSuccessful();
                                }

                            } else {

                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "Authentication failed yet again.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            // ...
                        }
                    });

        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.loginButton:
                signInUsingCredentials();
                break;

            case R.id.forgotPasswordTextView:

                if(getActivity() instanceof LoginActivity){
                    ((LoginActivity) getActivity()).goToForgotPasswordFragment();
                }

                if(getActivity() instanceof OnboardingActivity){
                    ((OnboardingActivity) getActivity()).goToForgotPasswordFragment();
                }
                break;

            case R.id.backButton:

                getActivity().getSupportFragmentManager().popBackStack();
                break;
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

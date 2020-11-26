package inesc_id.pt.motivandroid.deprecated;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.onboarding.activities.LoginActivity;
import inesc_id.pt.motivandroid.onboarding.activities.OnboardingActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragmentPassword.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragmentPassword#newInstance} factory method to
 * create an instance of this fragment.
 */
@Deprecated
public class LoginFragmentPassword extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String emailText;

    private OnFragmentInteractionListener mListener;

    private EditText passwordEditText;

    private Button signInButton;
    private Button signUpButton;

    private TextView forgotPasswordTextView;

    FirebaseAuth mAuth;

    public LoginFragmentPassword() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment LoginFragmentPassword.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragmentPassword newInstance(String email) {
        LoginFragmentPassword fragment = new LoginFragmentPassword();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            emailText = getArguments().getString(ARG_PARAM1);
        }

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login_password, container, false);

        passwordEditText = view.findViewById(R.id.emailQuestionLoginFragmentEditText);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if(!checkPasswordValidityLogin(passwordEditText.getText().toString())){
                        Toast.makeText(getContext(),"Password length must be between 6 and 16 characters", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
                return false;
            }
        });

        forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView);
        forgotPasswordTextView.setOnClickListener(this);


        signInButton = view.findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);

        signUpButton = view.findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(this);

        return view;
    }



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
        switch (v.getId()){

            case R.id.signInButton:
                signIn();
                break;

            case R.id.signUpButton:
                signUp();
                break;

            case R.id.forgotPasswordTextView:

                resetPassword();

                break;
        }
    }

    private void resetPassword() {

        startDialog();

        FirebaseAuth.getInstance().sendPasswordResetEmail(emailText)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        stopDialog();

                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Reset password email sent to " + emailText, Toast.LENGTH_LONG).show();
                            Log.d("LoginPasswordFragment", "Recovery email sent.");
                        }else{
                            Toast.makeText(getContext(), "Reset password email could not be sent", Toast.LENGTH_LONG).show();
                            Log.d("LoginPasswordFragment", "Recovery email could not be sent.");
                        }
                    }
                });

    }

    private void signUp() {

        if(checkPasswordValiditySignUp(passwordEditText.getText().toString())){

            startDialog();

            mAuth.createUserWithEmailAndPassword(emailText, passwordEditText.getText().toString())
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            stopDialog();

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LoginPasswordFragment", "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(getContext(), "Account created successfully. You will be taken to the onboarding!",
                                        Toast.LENGTH_SHORT).show();
                                ((LoginActivity) getActivity()).loginSuccessful();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LoginPasswordFragment", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
        }else{
            Toast.makeText(getContext(),"Password length must be between 6 and 16 characters", Toast.LENGTH_SHORT).show();
        }



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

    public boolean checkPasswordValidityLogin(String password) {

        if((passwordEditText.getText().toString().length() < 6)){
            return false;
        }else{
            return true;
        }

    }

    public boolean checkPasswordValiditySignUp(String password) {

        if((passwordEditText.getText().toString().length() < 6) || (passwordEditText.getText().toString().length() > 16)){
            return false;
        }else{
            return true;
        }

    }

    private void signIn() {

        if(checkPasswordValidityLogin(passwordEditText.getText().toString())){

            startDialog();

            mAuth.signInWithEmailAndPassword(emailText, passwordEditText.getText().toString())
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            stopDialog();

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LoginPasswordFragment", "signInWithEmail:success");

                                Toast.makeText(getContext(), "Log in successful!",
                                        Toast.LENGTH_SHORT).show();

                                ((LoginActivity) getActivity()).loginSuccessful();


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LoginPasswordFragment", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });

        }else{
            Toast.makeText(getContext(),"Password length must be between 6 and 16 characters", Toast.LENGTH_SHORT).show();
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

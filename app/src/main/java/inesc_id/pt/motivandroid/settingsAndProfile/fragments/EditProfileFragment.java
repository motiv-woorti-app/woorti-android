package inesc_id.pt.motivandroid.settingsAndProfile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.File;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;

/**
 * EditProfileFragment
 *
 *  Sub menu for editing profile information. Allows the user to change its:
 *  -Email (locked)
 *  -Name
 *  -Profile picture
 *
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
public class EditProfileFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    UserSettingStateWrapper userSettingStateWrapper;
    boolean validOnboarding = false;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    int REQUEST_GET_SINGLE_FILE = 1235;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //get user settings
        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "notExistent");

        if ((userSettingStateWrapper != null) && (userSettingStateWrapper.getUid().equals(FirebaseAuth.getInstance().getUid()))) {
            Log.d("ProfileAndSettings", "valid");
            validOnboarding = true;
        }else{
            Log.e("ProfileAndSettings", "invalid");
        }

    }

    EditText nameEditText;

    ImageView profilePicImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        EditText emailEditText = view.findViewById(R.id.emailEditText);

        Button finalButton = view.findViewById(R.id.doneFinalButton);
        finalButton.setOnClickListener(this);

        //retrieve firebase user profile pic
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        profilePicImageView = view.findViewById(R.id.imageView17);

        if (firebaseUser != null){
            //set email
            emailEditText.setHint(firebaseUser.getEmail());

            //if firebase user profile picture exists, load it
            if(firebaseUser.getPhotoUrl() != null) {
                Log.d("pic url", firebaseUser.getPhotoUrl().toString());

                Glide.with(getContext()).load(firebaseUser.getPhotoUrl().toString())
                        .into(profilePicImageView);

                }else{
                Log.d("pic url", "pic null");
            }
            }


        TextView changePhotoTextView = view.findViewById(R.id.changePhotoTextView);
        changePhotoTextView.setOnClickListener(this);

        if (validOnboarding){
            //set name
            nameEditText.setText(userSettingStateWrapper.getUserSettings().getName());
        }

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
            case R.id.doneFinalButton:

                //when done button is pressed, check if name is valid and save changes persistently
                if(nameEditText.getText().toString().length()>2){
                    userSettingStateWrapper.getUserSettings().setName(nameEditText.getText().toString());
                    SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);

                    if(getActivity() != null){
                        getActivity().onBackPressed();
                    }

                }else{
                    Toast.makeText(getContext(), "Invalid name", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.changePhotoTextView:

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GET_SINGLE_FILE);

                break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Log.d("EditProfileFragment", "Result_ok " + resultCode);

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_GET_SINGLE_FILE) {


                    Uri selectedImageUri = data.getData();
                    // Get the path from the Uri
                    final String path = getPathFromURI(selectedImageUri);
                    if (path != null) {
                        File f = new File(path);
                        selectedImageUri = Uri.fromFile(f);
                    }

                    // if we successfully retrieve the pic from the specified URI, send to firebase
                    //and update on screen

                    updateFirebaseUserProfilePic(selectedImageUri);

                }
            }
        } catch (Exception e) {
            Log.e("FileSelectorActivity", "File select error", e);
        }
    }


    /**
     *  get the image provided in the selectedImageUri, sends it to firebase and updates the image
     * on the screen
     *
     * @param selectedImageUri
     */
    private void updateFirebaseUserProfilePic(final Uri selectedImageUri) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(selectedImageUri)
                .build();

        if(user != null){

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("EditProfileFragment", "User profile updated.");

                                if (getActivity() != null) {
                                    Glide.with(getContext()).load(selectedImageUri)
                                            .into(profilePicImageView);
                                }

                                Toast.makeText(getContext(), "User profile updated.", Toast.LENGTH_SHORT).show();

                            }else{
                                Log.e("EditProfileFragment", "User profile NOT updated.");
                            }
                        }
                    });
        }

    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
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

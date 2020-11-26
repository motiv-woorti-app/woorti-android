package inesc_id.pt.motivandroid.home.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
//import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
//import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.BuildConfig;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigest;
import inesc_id.pt.motivandroid.home.activities.HomeDrawerActivity;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.settingsAndProfile.Option;
import inesc_id.pt.motivandroid.settingsAndProfile.OptionClickedCallback;
import inesc_id.pt.motivandroid.settingsAndProfile.adapters.OptionListAdapter;
import inesc_id.pt.motivandroid.settingsAndProfile.ProfileAndSettingsActivity;
import inesc_id.pt.motivandroid.tripStateMachine.TripAnalysis;

/**
 *
 * ProfileAndSettingsFragment
 *
 *   Main profile and settings menu fragment. Allows the user to access other profile and setting
 *   sub-menus:
 *      - Set home and work places
 *      - Demographic information
 *      - Worthwhileness settings
 *      - Campaigns
 *      - Tutorials
 *      - Transport preferences
 *      - App language
 *      - Feedback
 *      - Privacy policy
 *      - Log out
 *
 *   Also allows the user to change its profile picture and name.
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
public class ProfileAndSettingsFragment extends Fragment implements OptionClickedCallback, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1;

    private OnFragmentInteractionListener mListener;

    UserSettingStateWrapper userSettingStateWrapper;
    boolean validOnboarding = false;

    public ProfileAndSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param option Parameter 1.
     * @return A new instance of fragment ProfileAndSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileAndSettingsFragment newInstance(int option) {
        ProfileAndSettingsFragment fragment = new ProfileAndSettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, option);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }

    }

    TextView nameTextView;
    TextView emailTextView;
    ImageView userPhotoImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile_and_settings_fragments_with_list, container, false);

        nameTextView = view.findViewById(R.id.profileNameTextView);
        emailTextView = view.findViewById(R.id.profileEmailTextView);

        TextView woortiVersionTextView = view.findViewById(R.id.woortiVersionTextView);
        woortiVersionTextView.setText("Version " + BuildConfig.VERSION_NAME);

        userPhotoImageView = view.findViewById(R.id.userPhotoImageView);

        ArrayList<Option> profileAndSettingsOptions = Option.getAllProfileAndSettingOptions(getContext());
        OptionListAdapter adapter = new OptionListAdapter(getContext(), profileAndSettingsOptions, this);

        ExpandableHeightListView optionsListView = (ExpandableHeightListView) view.findViewById(R.id._dynamic);

        optionsListView.setAdapter(adapter);
        optionsListView.setExpanded(true);
        optionsListView.setDivider(null);

        Button editProfileButton = view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), ProfileAndSettingsActivity.class);
                intent.putExtra(keys.selectedOptionKey, R.string.Edit_Profile);
                startActivity(intent);
            }
        });

        ImageButton openDrawerButton = view.findViewById(R.id.openDrawerButton);
        openDrawerButton.setOnClickListener(buttonListener);

        if (mParam1 == keys.goToDemographicInfo){

            Intent intent = new Intent(getContext(), ProfileAndSettingsActivity.class);
            intent.putExtra(keys.selectedOptionKey, R.string.Demographic_Info);
            startActivity(intent);

        }

        ImageView woortiLogo = view.findViewById(R.id.imageView15);
        woortiLogo.setOnClickListener(this);

        String uid = FirebaseAuth.getInstance().getUid();

//        lv, db
//        if (uid != null && (uid.equals("E6WB5a2xvnd7ENGarYfQooubxQ52") || uid.equals("ABoCGWCiLpdo16uvOfjohJsnaT72"))){
//
//            Button button = view.findViewById(R.id.secretTestButton);
//            button.setVisibility(View.VISIBLE);
//            button.setOnClickListener(this);
//
//        }

        Log.e("pAndS", "is logging!!!!");

//        optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            public void onItemClick(AdapterView<?> adapter, View v, int position,
//                                    long arg3)
//            {

//                Option value = (Option)adapter.getItemAtPosition(position);
//
//                Log.e("pAndS", value.getText() + " " + value.getCode());
//
//                switch(value.getCode()){
//                    case R.string.SET_HOME_AND_WORK:
//
//                        Intent intent = new Intent(getContext(), ProfileAndSettingsActivity.class);
//                        intent.putExtra(keys.selectedOptionKey, R.string.SET_HOME_AND_WORK);
//                        startActivity(intent);
//
//                        break;
//
//                }
//
//                // assuming string and if you want to get the value on click of list item
//                // do what you intend to do on click of listview row
//            }
//        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "notExistent");

        if ((userSettingStateWrapper != null) && (userSettingStateWrapper.getUid().equals(FirebaseAuth.getInstance().getUid()))) {
            Log.e("ProfileAndSettings", "valid");

            validOnboarding = true;

        }else{
            Log.e("ProfileAndSettings", "invalid");
        }

        if (firebaseUser != null){
            emailTextView.setText(firebaseUser.getEmail());

//            nameTextView.setText(firebaseUser.getDisplayName());

            if(firebaseUser.getPhotoUrl() != null) {
                Log.e("pic url", firebaseUser.getPhotoUrl().toString());

                try{
                    Glide.with(this).load(firebaseUser.getPhotoUrl().toString())
                            .placeholder(R.drawable.ic_account_grey600_36dp)
//                    .override(view.getMeasuredWidth(), view.getMeasuredHeight())
                            .centerCrop()
                            .into(userPhotoImageView);


                }catch (Exception e){
                    Log.e("ProfileAndSettingsFrag", e.getMessage());
                }

            }
        }

        if (validOnboarding){
            nameTextView.setText(userSettingStateWrapper.getUserSettings().getName());
        }
    }

    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.openDrawerButton:
                    try {
                        ((HomeDrawerActivity) getActivity()).openDrawer();
                    } catch (Exception e){ Log.e("ProfileAndSettings", "Null pointer on getActivity");}
                    break;
            }
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

    @Override
    public void selectOption(Option option) {

                Log.e("pAndS", option.getText() + " " + option.getCode());

                Intent intent;

                switch(option.getCode()){
                    case R.string.Set_Home_And_Work:

                        intent = new Intent(getContext(), ProfileAndSettingsActivity.class);
                        intent.putExtra(keys.selectedOptionKey, R.string.Set_Home_And_Work);
                        startActivity(intent);

                        break;

                    case R.string.Demographic_Info:

                        intent = new Intent(getContext(), ProfileAndSettingsActivity.class);
                        intent.putExtra(keys.selectedOptionKey, R.string.Demographic_Info);
                        startActivity(intent);

                        break;

                    case R.string.Worthwhileness_Settings:

                        intent = new Intent(getContext(), ProfileAndSettingsActivity.class);
                        intent.putExtra(keys.selectedOptionKey, R.string.Worthwhileness_Settings);
                        startActivity(intent);

                        break;

                    case R.string.Tutorials:

                        intent = new Intent(getContext(), ProfileAndSettingsActivity.class);
                        intent.putExtra(keys.selectedOptionKey, R.string.Tutorials);
                        startActivity(intent);

                        break;
                    case R.string.App_Language:
                        intent = new Intent(getContext(), ProfileAndSettingsActivity.class);
                        intent.putExtra(keys.selectedOptionKey, R.string.App_Language);
                        startActivity(intent);

                        break;
                    case R.string.Transport_Preferences:

                        intent = new Intent(getContext(), ProfileAndSettingsActivity.class);
                        intent.putExtra(keys.selectedOptionKey, R.string.Transport_Preferences);
                        startActivity(intent);

                        break;
                    case R.string.Campaigns:

                        intent = new Intent(getContext(), ProfileAndSettingsActivity.class);
                        intent.putExtra(keys.selectedOptionKey, R.string.Campaigns);
                        startActivity(intent);

                        break;
                    case R.string.Feedback:

                        if(getActivity() != null && getActivity() instanceof HomeDrawerActivity){

                            ((HomeDrawerActivity) getActivity()).reportIssue();

                        }

                        break;

                    case R.string.Privacy_Policy:

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.Url_Privacy_Policy)));
                        startActivity(browserIntent);

                        break;
                    case R.string.Log_Out:
                        try {
                            ((HomeDrawerActivity) getActivity()).finishAndLogout();
                        } catch (Exception e){ Log.e("ProfileAndSettings", "Null pointer on getActivity");}
                        break;
                }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.secretTestButton:

               PersistentTripStorage persistentTripStorage = new PersistentTripStorage(getContext());

//        myTripsList = persistentTripStorage.getAllFullTripsObject();

                ArrayList<FullTripDigest> myTripsDigestsList = persistentTripStorage.getAllFullTripDigestsByUserIDObjects(FirebaseAuth.getInstance().getUid());

                //        todo: test post processing filtering for all my trips
        for (FullTripDigest fullTripDigest : myTripsDigestsList){

            TripAnalysis tripAnalysis = new TripAnalysis(false);

            FullTrip fullTrip = new PersistentTripStorage(getActivity()).getFullTripByDate(fullTripDigest.getTripID());

            try {
                tripAnalysis.analyseListOfTrips(fullTrip.getTripList(), false, false);
            }catch (Exception e){
                Log.e("MyTripsFragment",e.getMessage());
            }
        }
                break;

        }

    }

    private void displayLicensesAlertDialog() {
        WebView view = (WebView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_licenses, null);
        view.loadUrl("file:///android_asset/openSourceLicense.html");
        new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle("Open source licenses")
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
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


    public interface keys{

        String selectedOptionKey = "SELECTED_OPTION";

        int goToDemographicInfo = 1;

    }
}

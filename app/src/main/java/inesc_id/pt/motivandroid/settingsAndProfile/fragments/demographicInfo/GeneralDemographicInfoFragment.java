package inesc_id.pt.motivandroid.settingsAndProfile.fragments.demographicInfo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.userSettingsData.Country;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding11Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding14Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding15Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding16Fragment;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.settingsAndProfile.Helper;
import inesc_id.pt.motivandroid.settingsAndProfile.ProfileAndSettingsActivity;

/**
 * GeneralDemographicInfoFragment
 *
 *  Sub menu for General Demographic information. Allows the user to fill in information about its:
 *  -Age
 *  -Gender
 *  -Education
 *  -Country
 *  -City
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

public class GeneralDemographicInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static int SETTINGS_MODE = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText educationEditText;
    private EditText ageEditText;
    private EditText genderEditText;
    private EditText countryEditText;
    private EditText residenceEditText;
    private Button doneFinalButton;


    private OnFragmentInteractionListener mListener;

    public GeneralDemographicInfoFragment() {
        // Required empty public constructor
     }

    UserSettingStateWrapper userSettingStateWrapper;
    boolean validOnboarding = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralDemographicInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralDemographicInfoFragment newInstance(String param1, String param2) {
        GeneralDemographicInfoFragment fragment = new GeneralDemographicInfoFragment();
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

        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "notExistent");

        if ((userSettingStateWrapper != null) && (userSettingStateWrapper.getUid().equals(FirebaseAuth.getInstance().getUid()))) {
            Log.e("ProfileAndSettings", "valid");

            validOnboarding = true;

        }else{
            Log.e("ProfileAndSettings", "invalid");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_general_demographic_info, container, false);

        ageEditText = view.findViewById(R.id.ageEditText);
        genderEditText = view.findViewById(R.id.genderEditText);
        educationEditText = view.findViewById(R.id.educationEditText);
        countryEditText = view.findViewById(R.id.countryEditText);
        residenceEditText = view.findViewById(R.id.residenceEditText);


        doneFinalButton = view.findViewById(R.id.doneFinalButton);

        educationEditText.setOnClickListener(buttonListener);
        ageEditText.setOnClickListener(buttonListener);
        genderEditText.setOnClickListener(buttonListener);
        countryEditText.setOnClickListener(buttonListener);
        residenceEditText.setOnClickListener(buttonListener);
        doneFinalButton.setOnClickListener(buttonListener);

        return view;
    }

    //goes to the right fragment, according to the field chosen
    //since these fragments are also used in the onboarding phase, SETTINGS_MODE is used to warn the
    //fragment that the user is using the settings
    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager fragmentManager = getFragmentManager();
            switch(view.getId()){
                case R.id.ageEditText:
                    Onboarding14Fragment fragmentAge = Onboarding14Fragment.newInstance(SETTINGS_MODE);
                    fragmentManager.beginTransaction().replace(R.id.profile_and_settings_main_fragment, fragmentAge).addToBackStack(null).commit();
                    break;
                case R.id.genderEditText:
                    Onboarding15Fragment fragmentGender = Onboarding15Fragment.newInstance(SETTINGS_MODE);
                    fragmentManager.beginTransaction().replace(R.id.profile_and_settings_main_fragment, fragmentGender).addToBackStack(null).commit();
                    break;
                case R.id.countryEditText:
                    Onboarding11Fragment fragmentCountry = Onboarding11Fragment.newInstance(SETTINGS_MODE);
                    fragmentManager.beginTransaction().replace(R.id.profile_and_settings_main_fragment, fragmentCountry).addToBackStack(null).commit();
                    break;
                case R.id.residenceEditText:
                    Onboarding11Fragment fragmentCity = Onboarding11Fragment.newInstance(SETTINGS_MODE);
                    fragmentManager.beginTransaction().replace(R.id.profile_and_settings_main_fragment, fragmentCity).addToBackStack(null).commit();
                    break;
                case R.id.educationEditText:
                    Onboarding16Fragment fragmentEducation = Onboarding16Fragment.newInstance(Onboarding16Fragment.EDUCATION);
                    fragmentManager.beginTransaction().replace(R.id.profile_and_settings_main_fragment, fragmentEducation).addToBackStack(null).commit();
                    break;
                case R.id.doneFinalButton:
                    //save changes to the user settings persistently
                    SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);
                    getActivity().finish();
                    //getActivity().recreate();
                    //fragmentManager.popBackStack("General", 0);
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //Need to update local settings
        if(getActivity() instanceof ProfileAndSettingsActivity)
            userSettingStateWrapper = ((ProfileAndSettingsActivity) getActivity()).getUserSettingStateWrapper();

        if (validOnboarding){
            writeTextOptions();
        }

    }

    /**
     * retrieve former chosen settings (if any) and write them in the respective fields fields
     */
    private void writeTextOptions() {

        ageEditText.setText(userSettingStateWrapper.getUserSettings().getMinAge() + " - " + userSettingStateWrapper.getUserSettings().getMaxAge());

        Log.d("GeneralDemographic", "index " + Helper.getTextArrayPosition(userSettingStateWrapper.getUserSettings().getGender(), Helper.gendersKeys));
        Log.d("GeneralDemographic", "getgender " + userSettingStateWrapper.getUserSettings().getGender());

        int genderIndex = Helper.getTextArrayPosition(userSettingStateWrapper.getUserSettings().getGender(), Helper.gendersKeys);

        if(genderIndex != -1){
            genderEditText.setText(getString(Helper.genders[genderIndex]));
        }

        int educationIndex = Helper.getTextArrayPosition(userSettingStateWrapper.getUserSettings().getDegree(), Helper.degreesKeys);

        if(educationIndex != -1){
            educationEditText.setText(getString(Helper.degrees[educationIndex]));
        }

        countryEditText.setText(Country.getDisplayFromISOCode(userSettingStateWrapper.getUserSettings().getCountry()));
        residenceEditText.setText(userSettingStateWrapper.getUserSettings().getCity());

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

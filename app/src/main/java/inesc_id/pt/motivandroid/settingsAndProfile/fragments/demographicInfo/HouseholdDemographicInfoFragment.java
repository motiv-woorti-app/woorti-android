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
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.motviAPIClient.MotivAPIClientManager;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding16Fragment;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.settingsAndProfile.Helper;
import inesc_id.pt.motivandroid.settingsAndProfile.ProfileAndSettingsActivity;

/**
 * HouseholdDemographicInfoFragment
 *
 *  Sub menu for Household Demographic information. Allows the user to fill in information
 * about its:
 *  -Marital status
 *  -Number of people in household
 *  -Years of residence in the country
 *  -Labour status
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
public class HouseholdDemographicInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    UserSettingStateWrapper userSettingStateWrapper;
    boolean validOnboarding = false;

    private EditText maritalEditText;
    private EditText peopleEditText;
    private EditText yearsResidenceEditText;
    private EditText labourEditText;

    Button btDone;

    private OnFragmentInteractionListener mListener;

    public HouseholdDemographicInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HouseholdDemographicInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HouseholdDemographicInfoFragment newInstance() {
        HouseholdDemographicInfoFragment fragment = new HouseholdDemographicInfoFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retrieve user settings, if any
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
        View view = inflater.inflate(R.layout.fragment_household_demographic_info, container, false);

        maritalEditText = view.findViewById(R.id.maritalEditText);
        peopleEditText = view.findViewById(R.id.peopleEditText);
        yearsResidenceEditText = view.findViewById(R.id.yearsResidenceEditText);
        labourEditText = view.findViewById(R.id.labourEditText);
        btDone = view.findViewById(R.id.doneFinalButton);

        maritalEditText.setOnClickListener(buttonListener);
        peopleEditText.setOnClickListener(buttonListener);
        yearsResidenceEditText.setOnClickListener(buttonListener);
        labourEditText.setOnClickListener(buttonListener);
        btDone.setOnClickListener(buttonListener);

        return view;
    }

    /**
     * retrieve former chosen settings (if any) and write them in the respective fields fields
     */
    private void writeTextOptions() {

        int maritalIndex = Helper.getTextArrayPosition(userSettingStateWrapper.getUserSettings().getMaritalStatusHousehold(), Helper.maritalKeys);

        if(maritalIndex != -1){
            maritalEditText.setText(getString(Helper.marital[maritalIndex]));
        }

        peopleEditText.setText(userSettingStateWrapper.getUserSettings().getNumberPeopleHousehold());

        int yearsResidenceIndex = Helper.getTextArrayPosition(userSettingStateWrapper.getUserSettings().getYearsOfResidenceHousehold(), Helper.yearsResidenceKeys);

        Log.d("GeneralDemographic", "index " + yearsResidenceIndex);
        Log.d("GeneralDemographic", "getgender " + userSettingStateWrapper.getUserSettings().getYearsOfResidenceHousehold());

        if(yearsResidenceIndex != -1){
            yearsResidenceEditText.setText(getString(Helper.yearsResidence[yearsResidenceIndex]));
        }

        int labourIndex = Helper.getTextArrayPosition(userSettingStateWrapper.getUserSettings().getLabourStatusHousehold(), Helper.labourKeys);

        if(labourIndex != -1){
            labourEditText.setText(getString(Helper.labour[labourIndex]));
        }

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
    public void onResume() {
        super.onResume();

        if(getActivity() instanceof ProfileAndSettingsActivity)
            userSettingStateWrapper = ((ProfileAndSettingsActivity) getActivity()).getUserSettingStateWrapper();

        if (validOnboarding){
            writeTextOptions();
        }
    }

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager fragmentManager = getFragmentManager();
            switch(view.getId()){
                case R.id.maritalEditText:
                    Onboarding16Fragment fragmentMarital = Onboarding16Fragment.newInstance(Onboarding16Fragment.MARITAL);
                    fragmentManager.beginTransaction().replace(R.id.profile_and_settings_main_fragment, fragmentMarital).addToBackStack(null).commit();
                    break;
                case R.id.peopleEditText:
                    Onboarding16Fragment fragmentPeople = Onboarding16Fragment.newInstance(Onboarding16Fragment.PEOPLE_HOUSEHOLD);
                    fragmentManager.beginTransaction().replace(R.id.profile_and_settings_main_fragment, fragmentPeople).addToBackStack(null).commit();
                    break;
                case R.id.yearsResidenceEditText:
                    Onboarding16Fragment fragmentYears = Onboarding16Fragment.newInstance(Onboarding16Fragment.YEARS_RESIDENCE);
                    fragmentManager.beginTransaction().replace(R.id.profile_and_settings_main_fragment, fragmentYears).addToBackStack(null).commit();
                    break;
                case R.id.labourEditText:
                    Onboarding16Fragment fragmentLabours = Onboarding16Fragment.newInstance(Onboarding16Fragment.LABOUR);
                    fragmentManager.beginTransaction().replace(R.id.profile_and_settings_main_fragment, fragmentLabours).addToBackStack(null).commit();
                    break;
                case R.id.doneFinalButton:
                    //save changes to the user settings persistently
                    SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);
                    //send user reward progress to server
                    MotivAPIClientManager.getInstance(getContext()).putAndGetMyRewardStatus();
                    //send updated user settings/profile to the server
                    MotivAPIClientManager.getInstance(getContext()).makeUpdateOnboardingRequest();
                    getActivity().finish();
                    break;
            }
        }
    };

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

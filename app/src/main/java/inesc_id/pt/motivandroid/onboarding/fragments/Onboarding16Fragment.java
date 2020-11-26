package inesc_id.pt.motivandroid.onboarding.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import inesc_id.pt.motivandroid.ApplicationClass;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.onboarding.activities.OnboardingActivity;
import inesc_id.pt.motivandroid.onboarding.adapters.CheckBoxSelectionListAdapter;
import inesc_id.pt.motivandroid.settingsAndProfile.Helper;
import inesc_id.pt.motivandroid.settingsAndProfile.ProfileAndSettingsActivity;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.AdapterCallback;
import inesc_id.pt.motivandroid.utils.PopupUtil;

/**
 * Onboarding16Fragment
 *
 *  Note: this fragment is not used anymore in the onboarding, It's only used on the Profile and
 *  Settings menu
 *
 *  This fragment is used in multiple modes. It asks the user to fill in:
 *      EDUCATION = 0 (degree)
 *      OCCUPATION = 1 (occupation)
 *      MARITAL = 2 (marital status)
 *      DRIVER_LICENSE = 4 (drivers license status)
 *      YEARLY_INCOME = 5 (yearly income range)
 *      PEOPLE_HOUSEHOLD = 6 (people in the household range)
 *      YEARS_RESIDENCE = 7 (years of residence in the country)
 *      LABOUR = 8 (labour status)
 *
 *  This fragment is used in both the onboarding and the profile and settings. The mode is passed
 *  through the param ARG_PARAM1:
 *      ONBOARDING_MODE = 0;
 *      SETTINGS_MODE = 1;
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
public class Onboarding16Fragment extends Fragment implements View.OnClickListener, AdapterCallback, PopupUtil.CallbackInterface {

    //This fragment is used in multiple modes.
    //Views are dynamically modified

    public static final int EDUCATION = 0;
    public static final int OCCUPATION = 1;
    public static final int MARITAL = 2;
    public static final int NATIONALITY = 3;
    public static final int DRIVER_LICENSE = 4;
    public static final int YEARLY_INCOME = 5;
    public static final int PEOPLE_HOUSEHOLD = 6;
    public static final int YEARS_RESIDENCE = 7;
    public static final int LABOUR = 8;


    public static final String MODE = "mode";

    private OnFragmentInteractionListener mListener;

    ListView degreeListView;

    CheckBoxSelectionListAdapter degreeAdapter;

    int selectedDegreeIndex = -1;

    Button nextButton;

    int mode;

    public Onboarding16Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Onboarding16Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Onboarding16Fragment newInstance(int mode) {
        Onboarding16Fragment fragment = new Onboarding16Fragment();
        Bundle args = new Bundle();
        args.putInt(MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getInt(MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_a_little_about_you_degree_onboarding_16, container, false);

        degreeListView = view.findViewById(R.id.degreeListView);
        TextView tvTitle = view.findViewById(R.id.textView32);


        ArrayList<String> degrees = null;

        switch (mode){
//            case NATIONALITY:
//                degrees = new ArrayList<>(Arrays.asList(keys.countries));
//                tvTitle.setText(R.string.Nationality);
//                break;
            case EDUCATION:
                degrees = Helper.getDegreesTextArrayInLocale(getContext());
                tvTitle.setText(R.string.Educational_Background);
                break;
            case OCCUPATION:
                degrees = Helper.getOccupationsTextArrayInLocale(getContext());
                tvTitle.setText(R.string.Occupation);
                break;
            case MARITAL:
                degrees = Helper.getMaritalTextArrayInLocale(getContext());
                tvTitle.setText(R.string.Household_Marital);
                break;
            case DRIVER_LICENSE:
                degrees = Helper.getLicenseTextArrayInLocale(getContext());
                tvTitle.setText(R.string.Driver_License);
                break;
            case YEARLY_INCOME:
                degrees = Helper.getYearlyIncomeTextArrayInLocale();
                tvTitle.setText(R.string.Household_Yearly_Income);
                break;
            case PEOPLE_HOUSEHOLD:
                degrees = Helper.getPeopleHouseholdTextArrayInLocale();
                tvTitle.setText(R.string.Number_People_Household);
                break;
            case YEARS_RESIDENCE:
                degrees = Helper.getYearsResidenceTextArrayInLocale(getContext());
                tvTitle.setText(R.string.Household_Years_Residence);
                break;
            case LABOUR:
                degrees = Helper.getLaboutTextArrayInLocale(getContext());
                tvTitle.setText(R.string.Household_Labour_Status);
                break;
            default:
                degrees = Helper.getDegreesTextArrayInLocale(getContext());
                tvTitle.setText(R.string.Educational_Background);
        }

        degreeAdapter = new CheckBoxSelectionListAdapter(getContext(), degrees,this, -1);
        degreeListView.setAdapter(degreeAdapter);

        nextButton = view.findViewById(R.id.nextOB16Button);
        nextButton.setOnClickListener(this);
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
            case R.id.nextOB16Button:

                if(selectedDegreeIndex != -1){
                    switch (mode){
                        case EDUCATION:
                            ((ProfileAndSettingsActivity) getActivity()).saveDegree(Helper.degreesKeys[selectedDegreeIndex]);
                            break;
                        case MARITAL:
                            ((ProfileAndSettingsActivity) getActivity()).saveMaritalStatus(Helper.maritalKeys[selectedDegreeIndex]);
                            break;
                        case PEOPLE_HOUSEHOLD:
                            ((ProfileAndSettingsActivity) getActivity()).savePeopleHousehold(Helper.peopleHousehold[selectedDegreeIndex]);
                            break;
                        case YEARS_RESIDENCE:
                            ((ProfileAndSettingsActivity) getActivity()).saveYearsResidence(Helper.yearsResidenceKeys[selectedDegreeIndex]);
                            break;
                        case LABOUR:
                            ((ProfileAndSettingsActivity) getActivity()).saveLabourStatus(Helper.labourKeys[selectedDegreeIndex]);
                            break;

                    }
                    getActivity().onBackPressed();
                }else{
                    Toast.makeText(getContext(), "You must select one option", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.backButton:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    @Override
    public void clickedItem(int positionWrapper) {
        selectedDegreeIndex = positionWrapper;

        if(degreeAdapter.getItem(positionWrapper).equals(getString(R.string.Other))){
            PopupUtil.showOtherOptionDialog(getContext(), this, getString(R.string.Other));
        }


        nextButton.setClickable(true);
        nextButton.setAlpha(1);
    }

    @Override
    public void saveOtherOption(String option) {

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






    }

}

package inesc_id.pt.motivandroid.onboarding.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.onboarding.activities.OnboardingActivity;
import inesc_id.pt.motivandroid.onboarding.adapters.CheckBoxSelectionListAdapter;
import inesc_id.pt.motivandroid.settingsAndProfile.ProfileAndSettingsActivity;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.AdapterCallback;

/**
 * Onboarding14Fragment
 *
 *  Asks the user to select his age range
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
public class Onboarding14Fragment extends Fragment implements View.OnClickListener, AdapterCallback {

    private OnFragmentInteractionListener mListener;

    private static final String ARG_PARAM1 = "param1";
    private final static int ONBOARDING_MODE = 0;
    private final static int SETTINGS_MODE = 1;

    ListView ageListView;

    private int mode;

//    GenericSelectionListAdapter ageAdapter;
    CheckBoxSelectionListAdapter ageAdapter;

    int selectedAgeIndex;

    public Onboarding14Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Onboarding14Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Onboarding14Fragment newInstance(int param1) {
        Onboarding14Fragment fragment = new Onboarding14Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    Button nextButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getInt(ARG_PARAM1);
        }
        selectedAgeIndex = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_a_little_about_you_age_onboarding_14, container, false);

        ageListView = view.findViewById(R.id.ageListView);


        ArrayList<String> ageRanges = new ArrayList<>(Arrays.asList(keys.ageRangeString));

        ageAdapter = new CheckBoxSelectionListAdapter(getContext(),ageRanges,  this, selectedAgeIndex);
        ageListView.setAdapter(ageAdapter);

        Button backButton = view.findViewById(R.id.backButton);
        if(mode == SETTINGS_MODE)
            backButton.setVisibility(View.GONE);
        backButton.setOnClickListener(this);

        nextButton = view.findViewById(R.id.nextOB14Button);
        nextButton.setOnClickListener(this);

        if(selectedAgeIndex == -1) {
            nextButton.setClickable(false);
            nextButton.setAlpha(0.35f);
        }
        else{
            nextButton.setClickable(true);
            nextButton.setAlpha(1);
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
            case R.id.nextOB14Button:

                if(selectedAgeIndex != -1){
                    int minAgeRange = keys.ageRangeMin[selectedAgeIndex];
                    int maxAgeRange = keys.ageRangeMax[selectedAgeIndex];

                    if(mode == ONBOARDING_MODE){
                        if(getActivity() instanceof  OnboardingActivity)
                            ((OnboardingActivity) getActivity()).saveAge(minAgeRange, maxAgeRange);

                        Onboarding15Fragment nextFragment = Onboarding15Fragment.newInstance(ONBOARDING_MODE);
                        //using Bundle to send data
                        FragmentTransaction nextTransaction=getFragmentManager().beginTransaction();
                        nextTransaction.replace(R.id.onboarding_main_fragment, nextFragment).addToBackStack(null);
                        nextTransaction.commit();
                    }else{  //SETTINGS MODE
                        Log.d("SETTINGS", "Saving Age");
                        if(getActivity() instanceof ProfileAndSettingsActivity)
                            ((ProfileAndSettingsActivity) getActivity()).saveAge(minAgeRange, maxAgeRange);

                        getActivity().onBackPressed();
                    }



                }else{
                    Toast.makeText(getContext(), "You must select one age range", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.backButton:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    @Override
    public void clickedItem(int positionWrapper) {

        selectedAgeIndex = positionWrapper;

        nextButton.setClickable(true);
        nextButton.setAlpha(1);


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

        String[] ageRangeString = {"16-19", "20-24", "25-29", "30-39", "40-49", "50-64", "65-74", "75 +"};

        int[] ageRangeMin = {16, 20, 25, 30, 40, 50, 65, 75};
        int[] ageRangeMax = {19, 24, 29, 39, 49, 64, 74, 129};


    }
}

package inesc_id.pt.motivandroid.onboarding.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.data.userSettingsData.Country;
import inesc_id.pt.motivandroid.motviAPIClient.MotivAPIClientManager;
import inesc_id.pt.motivandroid.onboarding.activities.OnboardingActivity;
import inesc_id.pt.motivandroid.onboarding.adapters.RightArrowSelectionListAdapter;
import inesc_id.pt.motivandroid.settingsAndProfile.ProfileAndSettingsActivity;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.AdapterCallback;
import inesc_id.pt.motivandroid.utils.PopupUtil;

/**
 * Onboarding12Fragment
 *
 *  Asks the user in what city he/she is. Also retrieves the campaigns encompassing the chosen
 *  location.
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
public class Onboarding12Fragment extends Fragment implements AdapterCallback, View.OnClickListener, PopupUtil.CallbackInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    private static final String GENERAL_FRAGMENT_TAG = "General";

    private final static int ONBOARDING_MODE = 0;
    private final static int SETTINGS_MODE = 1;

    // TODO: Rename and change types of parameters
    private String countryString;
    private int countryPosition;
    private String city;

    private String displayCountryName;

    boolean retrievedCampaigns = false;
    boolean receiverRegistered = false;

    int mode;

    private OnFragmentInteractionListener mListener;

    public Onboarding12Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Onboarding12Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Onboarding12Fragment newInstance(String param1, int param2, int param3, String param4) {
        Onboarding12Fragment fragment = new Onboarding12Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            countryString = getArguments().getString(ARG_PARAM1);
            countryPosition = getArguments().getInt(ARG_PARAM2);
            mode = getArguments().getInt(ARG_PARAM3);
            displayCountryName = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a_little_about_you_where_city_onboarding_12, container, false);

        TextView tvQuestion = view.findViewById(R.id.textView32);
        tvQuestion.setText(getString(R.string.Where_City, displayCountryName));

        ExpandableHeightListView citiesListView = view.findViewById(R.id.citiesListView);
        citiesListView.setExpanded(true);
        ArrayList<String> citiesList = new ArrayList<>(Country.getCityListPerCountry(getContext(), countryString));

        RightArrowSelectionListAdapter adapter = new RightArrowSelectionListAdapter(this, citiesList, getContext(), city);
        citiesListView.setAdapter(adapter);

        TextView tvTitle = view.findViewById(R.id.textView31);
        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        if(mode == ONBOARDING_MODE){
            backButton.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);
        }
        if(mode == SETTINGS_MODE){
            backButton.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
        }


        ///todo delete

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

    private void goToNextFragment(){
        if(mode == ONBOARDING_MODE && (getActivity() instanceof OnboardingActivity)) {
            ((OnboardingActivity) getActivity()).saveCity(city);

            //Get campaigns list to decide next fragment
            MotivAPIClientManager.getInstance(getContext()).makeGetCampaignsByCountryCity(countryString, city);
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                    receivingCampaignData, new IntentFilter(MotivAPIClientManager.keys.broadcasteKeyCampaigns));
            receiverRegistered = true;
        }
        else if(mode == SETTINGS_MODE && (getActivity() instanceof ProfileAndSettingsActivity)) {
            ((ProfileAndSettingsActivity) getActivity()).saveCountry(countryString);
            ((ProfileAndSettingsActivity) getActivity()).saveCity(city);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStack(GENERAL_FRAGMENT_TAG, 0);
        }
    }

    @Override
    public void clickedItem(int position) {
        city = Country.getCityListPerCountry(getContext(), countryString).get(position);

        if(city.equals(getString(R.string.Other))){
            PopupUtil.showOtherOptionDialog(getContext(), this, getString(R.string.OtherCity));
        }
        else {
            goToNextFragment();
        }
    }

    @Override
    public void saveOtherOption(String option) {
        this.city = option;
        goToNextFragment();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backButton:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }


    // callback called when the campaign data is retrieved from the motiv server
    private BroadcastReceiver receivingCampaignData = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("receiver", "Received message broadcasted from MotivAPIClientManager");

            if (isAdded()) {

                String action = intent.getStringExtra(MotivAPIClientManager.keys.result);

                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this);

                if (action.equals(MotivAPIClientManager.keys.success)) {

                    ArrayList<Campaign> campaigns = (ArrayList<Campaign>) intent.getSerializableExtra(MotivAPIClientManager.keys.campaignsData);

                    Log.d("OnboardingCampaigns", "There are " + campaigns.size() + " campaigns");

                    if (campaigns.size() > 0) {
                        retrievedCampaigns = true;
                        Onboarding13Fragment nextFragment = Onboarding13Fragment.newInstance(countryString, city, campaigns);
                        //using Bundle to send data
                        FragmentTransaction nextTransaction = getFragmentManager().beginTransaction();
                        nextTransaction.replace(R.id.onboarding_main_fragment, nextFragment).addToBackStack(null);
                        nextTransaction.commit();
                    } else {
                        ((OnboardingActivity) getActivity()).saveOnCampaignsSelected(new ArrayList<String>());
                        Onboarding14Fragment nextFragment = Onboarding14Fragment.newInstance(ONBOARDING_MODE);
                        //using Bundle to send data
                        FragmentTransaction nextTransaction = getFragmentManager().beginTransaction();
                        nextTransaction.replace(R.id.onboarding_main_fragment, nextFragment).addToBackStack(null);
                        nextTransaction.commit();

                    }

                    //updateUI(campaigns);

                } else {
                    int errorCode = intent.getIntExtra(MotivAPIClientManager.keys.errorCode, 0);
//                Toast.makeText(getContext(), "Failed to retrieve camapaign data -> error code: "+ errorCode, Toast.LENGTH_LONG).show();

                    Log.e("Onboarding12Fragment", "Failed to retrieve camapaign data -> error code: " + errorCode);

                    ((OnboardingActivity) getActivity()).saveOnCampaignsSelected(new ArrayList<String>());
                    Onboarding14Fragment nextFragment = Onboarding14Fragment.newInstance(ONBOARDING_MODE);
                    //using Bundle to send data
                    FragmentTransaction nextTransaction = getFragmentManager().beginTransaction();
                    nextTransaction.replace(R.id.onboarding_main_fragment, nextFragment).addToBackStack(null);
                    nextTransaction.commitAllowingStateLoss();


                }

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

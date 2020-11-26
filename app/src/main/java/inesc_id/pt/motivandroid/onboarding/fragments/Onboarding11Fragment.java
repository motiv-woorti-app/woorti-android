package inesc_id.pt.motivandroid.onboarding.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.userSettingsData.Country;
import inesc_id.pt.motivandroid.onboarding.activities.OnboardingActivity;
import inesc_id.pt.motivandroid.onboarding.adapters.RightArrowSelectionListAdapter;
import inesc_id.pt.motivandroid.settingsAndProfile.ProfileAndSettingsActivity;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.AdapterCallback;

/**
 * Onboarding11Fragment
 *
 *  Asks the user in what country he/she is
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
public class Onboarding11Fragment extends Fragment implements AdapterCallback, View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    private static final String ARG_PARAM1 = "param1";
    private final static int ONBOARDING_MODE = 0;
    private final static int SETTINGS_MODE = 1;

    String country;

    int mode;

    public Onboarding11Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Onboarding11Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Onboarding11Fragment newInstance(int param1) {
        Onboarding11Fragment fragment = new Onboarding11Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getInt(ARG_PARAM1);
        }
    }

    ExpandableHeightListView countriesListView;

    boolean otherMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a_little_about_you_where_onboarding_11, container, false);

        countriesListView = view.findViewById(R.id.countryListView);
        countriesListView.setExpanded(true);
//        ArrayList<String> countries = new ArrayList<>(Country.getSelectedCountryListDisplayName());

        RightArrowSelectionListAdapter adapter = new RightArrowSelectionListAdapter(this, Country.getSelectedCountryListDisplayName(getContext()), getContext(), country);
        countriesListView.setAdapter(adapter);

        Button backButton = view.findViewById(R.id.backButton);
        TextView tvTitle = view.findViewById(R.id.textView31);
        if(mode == ONBOARDING_MODE){
            backButton.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);
        }
        if(mode == SETTINGS_MODE){
            backButton.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
        }

        otherMode = false;

        backButton.setOnClickListener(this);

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

    private void goToNextFragment(int position, String displayName){
        if(mode == ONBOARDING_MODE && (getActivity() instanceof OnboardingActivity)) {
            ((OnboardingActivity) getActivity()).saveCountry(country);
            Onboarding12Fragment nextFragment = Onboarding12Fragment.newInstance(country, position, ONBOARDING_MODE, displayName);
            FragmentTransaction nextTransaction = getFragmentManager().beginTransaction();
            nextTransaction.replace(R.id.onboarding_main_fragment, nextFragment).addToBackStack(null);
            nextTransaction.commit();
        }
        else if(mode == SETTINGS_MODE && (getActivity() instanceof ProfileAndSettingsActivity)) {
            Onboarding12Fragment nextFragment = Onboarding12Fragment.newInstance(country, position, SETTINGS_MODE, displayName);
            FragmentTransaction nextTransaction = getFragmentManager().beginTransaction();
            nextTransaction.replace(R.id.profile_and_settings_main_fragment, nextFragment).addToBackStack(null);
            nextTransaction.commit();
        }
    }


    @Override
    public void clickedItem(int position) {

        String displayName;

        if(otherMode){
            Country selected = Country.getFullCountryList().get(position);
            country = selected.getIso();
            displayName = selected.getName();
        }else{
            Country selected = Country.getSelectedCountryList(getContext()).get(position);
            country = selected.getIso();
            displayName = selected.getName();
        }
//        country = places.countries[position];

        if(!otherMode && Country.getOtherISOCode().equals(country)){

            RightArrowSelectionListAdapter adapter = new RightArrowSelectionListAdapter(this, Country.getFullCountryListDisplayName(), getContext(), country);
            countriesListView.setAdapter(adapter);

            otherMode = true;

//            PopupUtil.showOtherOptionDialog(getContext(), this, getString(R.string.OtherCountry));
        }
        else {
            if(!otherMode) {
                goToNextFragment(position, displayName);
            }else{
                goToNextFragment(Country.getSelectedCountryList(getContext()).size()-1, displayName);
            }
            }
    }

//    @Override
//    public void saveOtherOption(String country) {
//        this.country = country;
//        goToNextFragment(places.otherIndex);
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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

    public interface places{

        int otherIndex = 10;

//        String[] countries = {"Portugal",
//                "Slovakia",
//                "Belgium",
//                "Spain",
//                "Finland",
//                "Switzerland",
//                "Italy",
//                "France",
//                "Norway",
//                "Croatia",
//                "Other",
//                };

        String[][] cities = {{"Lisboa", "Porto", "Other"},
                {"Žilina", "Bratislava", "Other"},
                {"Bruxelles", "Antwerpen", "Other"},
                {"Madrid", "Barcelona", "Other"},
                {"Helsinki", "Other"},
                {"Zürich", "Lausanne", "Genève", "Other"},
                {"Milano", "Other"},
                {"Paris", "Lyon", "Grenoble", "Other"},
                {"Oslo", "Other"},
                {"Zagreb", "Other"},
                {"Other"}};

    }

}

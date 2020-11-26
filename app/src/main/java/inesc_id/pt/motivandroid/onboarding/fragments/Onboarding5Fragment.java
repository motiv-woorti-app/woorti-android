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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.HashMap;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetectedWrapper;
import inesc_id.pt.motivandroid.onboarding.adapters.ModesOfTransportUsedAdapter;
import inesc_id.pt.motivandroid.onboarding.wrappers.ModeOfTransportUsed;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;

/**
 * Onboarding5Fragment
 *
 *  Asks the user to select the modes of transport regularly used.
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
public class Onboarding5Fragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static int ONBOARDING_MODE = 0;
    private final static int SETTINGS_MODE = 1;

    private ModesOfTransportUsedAdapter adapterPublic;
    private ModesOfTransportUsedAdapter adapterActive;
    private ModesOfTransportUsedAdapter adapterPrivate;

    ArrayList<ModeOfTransportUsed> modeOfTransportUsedArrayList;

    ArrayList<ModeOfTransportUsed> selectedUsedModes;

    private OnFragmentInteractionListener mListener;

    int mode;

    UserSettingStateWrapper userSettingStateWrapper;

    ScrollView scrollview;

    public Onboarding5Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment Onboarding5Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Onboarding5Fragment newInstance(int param1) {
        Onboarding5Fragment fragment = new Onboarding5Fragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_modes_of_transport_used_onboarding_5, container, false);

        scrollview = view.findViewById(R.id.scrollView2);

        if(mode == ONBOARDING_MODE){
            selectedUsedModes = new ArrayList<>();
        }
        else {
            userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "notExistent");
            selectedUsedModes = userSettingStateWrapper.getUserSettings().getPreferedModesOfTransport();
        }

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Public Transport");
        labels.add("Active/Semi-active");
        labels.add("Private Motorised");

        //Fetch list of modes
        ArrayList<ActivityDetectedWrapper> activityWrappers = ActivityDetectedWrapper.getPublicFullList();
        activityWrappers.addAll(ActivityDetectedWrapper.getActiveFullList());
        activityWrappers.addAll(ActivityDetectedWrapper.getPrivateFullList());


        modeOfTransportUsedArrayList = new ArrayList<>();

        HashMap<String, ArrayList<ModeOfTransportUsed>> usedTransportsMap =
                new HashMap<String, ArrayList<ModeOfTransportUsed>>();

        for (ActivityDetectedWrapper activityDetectedWrapper : activityWrappers){

            ModeOfTransportUsed mode = new ModeOfTransportUsed(false, activityDetectedWrapper.getText(), activityDetectedWrapper.getCode());

            //Recover saved mode from persistent memory
            if(selectedUsedModes != null) {
                ModeOfTransportUsed savedMode = getModeFromArray(mode, selectedUsedModes);
                if(savedMode != null) {
                    mode = savedMode;
                    mode.setUsed(true);
                }
            }

            modeOfTransportUsedArrayList.add(mode);

            //Split transports by correct label
            String tempLabel = activityDetectedWrapper.getLabel();
            ArrayList<ModeOfTransportUsed> tempLabelTransports = usedTransportsMap.get(tempLabel);
            //Create array if it doesn't exist yet
            if (tempLabelTransports == null) {
                tempLabelTransports = new ArrayList<ModeOfTransportUsed>();
                usedTransportsMap.put(tempLabel, tempLabelTransports);
            }
            tempLabelTransports.add(mode);
        }


        //Set Adapter for Public Transport List
        adapterPublic = new ModesOfTransportUsedAdapter(usedTransportsMap.get("Public Transport"), getContext());
        ExpandableHeightListView usedPublicTransportsList = view.findViewById(R.id.usedPublicTransportsList);
        usedPublicTransportsList.setExpanded(true);
        usedPublicTransportsList.setAdapter(adapterPublic);


        //Set Adapter for Active Transport List
        adapterActive = new ModesOfTransportUsedAdapter(usedTransportsMap.get("Active/Semi-active"), getContext());
        ExpandableHeightListView usedActiveTransportList = view.findViewById(R.id.usedActiveTransportList);
        usedActiveTransportList.setExpanded(true);
        usedActiveTransportList.setAdapter(adapterActive);

        //Set Adapter for Private Transport List
        adapterPrivate = new ModesOfTransportUsedAdapter(usedTransportsMap.get("Private Motorised"), getContext());
        ExpandableHeightListView usedPrivateMotorised = view.findViewById(R.id.usedPrivateMotorised);
        usedPrivateMotorised.setExpanded(true);
        usedPrivateMotorised.setAdapter(adapterPrivate);

        Button backButton = view.findViewById(R.id.backButton);
        Button nextButton = view.findViewById(R.id.nextOB5Button);
        TextView tvTitle = view.findViewById(R.id.textView31);
        if(mode == SETTINGS_MODE) {
            tvTitle.setVisibility(View.INVISIBLE);
            backButton.setVisibility(View.GONE);
        }
        nextButton.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.nextOB5Button:

                //Get transports from each transport label
                selectedUsedModes = adapterPublic.getUsedModes();
                selectedUsedModes.addAll(adapterActive.getUsedModes());
                selectedUsedModes.addAll(adapterPrivate.getUsedModes());

                if(selectedUsedModes.size() == 0){
                    Toast.makeText(getContext(), "You must choose at least one mode of transport!", Toast.LENGTH_LONG).show();
                }else{
                    int fragmentId;
                    if(mode == ONBOARDING_MODE)
                        fragmentId = R.id.onboarding_main_fragment;
                    else
                        fragmentId = R.id.profile_and_settings_main_fragment;

                     Onboarding6_7Fragment mfragment = Onboarding6_7Fragment.newInstance(Onboarding6_7Fragment.keys.PRODUCTIVE, selectedUsedModes, mode);
                    //using Bundle to send data
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(fragmentId, mfragment).addToBackStack(null);
                    transaction.commit();

                }

                break;
            case R.id.backButton:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

        }
    }


    private ModeOfTransportUsed getModeFromArray(ModeOfTransportUsed mode, ArrayList<ModeOfTransportUsed> array){
        for(ModeOfTransportUsed m : array){
            if(mode.getModalityIntCode() == m.getModalityIntCode())
                return m;
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Force scroll to start at top
        scrollview.smoothScrollTo(0, 0);
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

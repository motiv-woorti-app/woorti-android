package inesc_id.pt.motivandroid.onboarding.fragments;

import android.content.Context;
import android.content.res.Resources;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.ApplicationClass;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.onboarding.activities.OnboardingActivity;
import inesc_id.pt.motivandroid.onboarding.adapters.ModesOfTransportHowProductiveRelaxingAdapter;
import inesc_id.pt.motivandroid.onboarding.wrappers.ModeOfTransportUsed;
import inesc_id.pt.motivandroid.settingsAndProfile.ProfileAndSettingsActivity;

/**
 * Onboarding6_7Fragment
 *
 *  Asks the user to rate the formerly chosen modes of transport used in terms of "Productivity",
 *  "Fitness" and "Enjoyment"
 *
 *  This fragment is presented once for each term. The term is passed through arg ARG_PARAM2
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


public class Onboarding6_7Fragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private final static int ONBOARDING_MODE = 0;
    private final static int SETTINGS_MODE = 1;

    // TODO: Rename and change types of parameters


    private OnFragmentInteractionListener mListener;

    public Onboarding6_7Fragment() {
        // Required empty public constructor
    }

    int mode;
    int settingsorOBMode;

    ArrayList<ModeOfTransportUsed> modesOfTransportUsed;

    ModesOfTransportHowProductiveRelaxingAdapter adapter;

    Button nextButton;
    ScrollView scrollview;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mode Parameter 1.
     * @param modesOfTransportUsed Parameter 2.
     * @return A new instance of fragment onboarding6Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Onboarding6_7Fragment newInstance(int mode, ArrayList<ModeOfTransportUsed> modesOfTransportUsed, int settingsOrOBmode) {
        Onboarding6_7Fragment fragment = new Onboarding6_7Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, mode);
        args.putSerializable(ARG_PARAM2, modesOfTransportUsed);
        args.putInt(ARG_PARAM3, settingsOrOBmode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getInt(ARG_PARAM1);
            modesOfTransportUsed = (ArrayList<ModeOfTransportUsed>) getArguments().getSerializable(ARG_PARAM2);
            settingsorOBMode = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_how_productive_onboarding_6, container, false);
        nextButton = view.findViewById(R.id.nextOB6Button);
        TextView tvTitle = view.findViewById(R.id.textView31);
        TextView tvQuestion = view.findViewById(R.id.textView32);
        TextView tvLeftValue = view.findViewById(R.id.textView40);
        TextView tvRightValue = view.findViewById(R.id.textView39);
        scrollview = view.findViewById(R.id.scrollView2);

        switch (mode){
            case keys.PRODUCTIVE:
                tvQuestion.setText(keys.PRODUCTIVE_QUESTION);
                tvLeftValue.setText(keys.PRODUCTIVE_LEFT_VALUE);
                tvRightValue.setText(keys.PRODUCTIVE_RIGHT_VALUE);
                break;
            case keys.ENJOYMENT:
                tvQuestion.setText(keys.ENJOYMENT_QUESTION);
                tvLeftValue.setText(keys.ENJOYMENT_LEFT_VALUE);
                tvRightValue.setText(keys.ENJOYMENT_RIGHT_VALUE);
                break;
            case keys.FITNESS:
                tvQuestion.setText(keys.FITNESS_QUESTION);
                tvLeftValue.setText(keys.FITNESS_LEFT_VALUE);
                tvRightValue.setText(keys.FITNESS_RIGHT_VALUE);
                break;
        }

        adapter = new ModesOfTransportHowProductiveRelaxingAdapter(modesOfTransportUsed, getContext(), mode);
        ExpandableHeightListView modesUsedRatingsListView = view.findViewById(R.id.modesOfTransportUsedListView);
        modesUsedRatingsListView.setExpanded(true);
        modesUsedRatingsListView.setAdapter(adapter);

        Button backButton = view.findViewById(R.id.backButton);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        if(settingsorOBMode == SETTINGS_MODE){
            backButton.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
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
        switch(v.getId()){
            case R.id.nextOB6Button:
                Onboarding6_7Fragment nextFragment;
                FragmentTransaction nextTransaction;

                int fragmentId;
                if(settingsorOBMode == ONBOARDING_MODE)
                    fragmentId = R.id.onboarding_main_fragment;
                else
                    fragmentId = R.id.profile_and_settings_main_fragment;


                switch (mode){
                    case keys.PRODUCTIVE:
                        nextFragment = Onboarding6_7Fragment.newInstance(keys.ENJOYMENT, adapter.getModes(), settingsorOBMode);
                        nextTransaction=getFragmentManager().beginTransaction();
                        nextTransaction.replace(fragmentId, nextFragment).addToBackStack(null);
                        nextTransaction.commit();
                        break;
                    case keys.ENJOYMENT:
                        nextFragment = Onboarding6_7Fragment.newInstance(keys.FITNESS, adapter.getModes(), settingsorOBMode);
                        nextTransaction=getFragmentManager().beginTransaction();
                        nextTransaction.replace(fragmentId, nextFragment).addToBackStack(null);
                        nextTransaction.commit();
                        break;
                    case keys.FITNESS:
                        if((settingsorOBMode == ONBOARDING_MODE) && getActivity() instanceof OnboardingActivity) {
                            ((OnboardingActivity) getActivity()).saveUsedModesValues(adapter.getModes());
                            ((OnboardingActivity) getActivity()).goToRegisterOrPermissions();
                        }
                        else if((settingsorOBMode == SETTINGS_MODE) && getActivity() instanceof ProfileAndSettingsActivity) {
                            ((ProfileAndSettingsActivity) getActivity()).saveRegularModes(adapter.getModes());
                            getActivity().finish();
                        }
                        break;
                    }

                    break;
            case R.id.backButton:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
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

    public interface keys {

        int PRODUCTIVE = 0;
        int ENJOYMENT = 1;
        int FITNESS = 2;

        String PRODUCTIVE_QUESTION = ApplicationClass.resources.getString(R.string.Rating_Prefered_Mot_Prod_Title);
        String ENJOYMENT_QUESTION = ApplicationClass.resources.getString(R.string.Rating_Prefered_Mot_Enj_Title);
        String FITNESS_QUESTION = ApplicationClass.resources.getString(R.string.Rating_Prefered_Mot_Fit_Title);

        String PRODUCTIVE_LEFT_VALUE = ApplicationClass.resources.getString(R.string.Not_Productive);
        String PRODUCTIVE_RIGHT_VALUE = ApplicationClass.resources.getString(R.string.Productive);

        String ENJOYMENT_LEFT_VALUE = ApplicationClass.resources.getString(R.string.Not_Enjoying);
        String ENJOYMENT_RIGHT_VALUE = ApplicationClass.resources.getString(R.string.Enjoying);

        String FITNESS_LEFT_VALUE = ApplicationClass.resources.getString(R.string.Doesnt_Improve_My_Fitness);
        String FITNESS_RIGHT_VALUE = ApplicationClass.resources.getString(R.string.Improves_My_Fitness);
    }
}

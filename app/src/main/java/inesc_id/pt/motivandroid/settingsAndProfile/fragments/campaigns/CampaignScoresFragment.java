package inesc_id.pt.motivandroid.settingsAndProfile.fragments.campaigns;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.managers.CampaignManager;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.data.pointSystem.CampaignScore;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.settingsAndProfile.ProfileAndSettingsActivity;
import inesc_id.pt.motivandroid.settingsAndProfile.adapters.ChooseCampaignListAdapter;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.AdapterCallback;

/**
 * CampaignScoresFragment
 *
 *  Lists campaign scores. Allows the user to change the currently highlighted campaign
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

public class CampaignScoresFragment extends Fragment implements AdapterCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CampaignScoresFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CampaignScoresFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CampaignScoresFragment newInstance(String param1, String param2) {
        CampaignScoresFragment fragment = new CampaignScoresFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    UserSettingStateWrapper userSettingStateWrapper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "");

    }

    ArrayList<Campaign> campaignList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_campaign_scores, container, false);

        //get user campaigns scores
        ArrayList<CampaignScore> userCampaignScore = userSettingStateWrapper.getUserSettings().getPointsPerCampaign();

        //get campaigns data
        campaignList = CampaignManager.getInstance(getContext()).getCampaigns();

        int selectedOption = -1;

        //check what is the previously selected campaign and select it
        if (userSettingStateWrapper.getUserSettings().getChosenDefaultCampaignID() != null){
            int i = 0;
            for (Campaign campaign : campaignList){
                if (campaign.getCampaignID().equals(userSettingStateWrapper.getUserSettings().getChosenDefaultCampaignID())){
                    selectedOption = i;
                    break;
                }
                i++;
            }

        }


        ChooseCampaignListAdapter adapter = new ChooseCampaignListAdapter(getContext(), campaignList, userCampaignScore, this, selectedOption);

        ListView optionsListView = view.findViewById(R.id._dynamic);

        optionsListView.setAdapter(adapter);
        optionsListView.setDivider(null);

        if(getActivity() instanceof ProfileAndSettingsActivity) {
            ((ProfileAndSettingsActivity) getActivity()).setTitleTextView(getString(R.string.Campaign_Scores));
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

    /**
     *  called when an option is chosen in the ChooseCampaignListAdapter. Saves the campaign chosen
     * by the user persistently in the user settings.
     *
     * @param positionWrapper
     */
    @Override
    public void clickedItem(int positionWrapper) {

        String campaignDefaultChosen = campaignList.get(positionWrapper).getCampaignID();

        userSettingStateWrapper.getUserSettings().setChosenDefaultCampaignID(campaignDefaultChosen);

        SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);

        Toast.makeText(getContext(), "Default campaign changed", Toast.LENGTH_LONG).show();

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

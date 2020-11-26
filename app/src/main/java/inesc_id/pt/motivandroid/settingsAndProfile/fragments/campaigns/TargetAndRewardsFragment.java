package inesc_id.pt.motivandroid.settingsAndProfile.fragments.campaigns;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.managers.RewardManager;
import inesc_id.pt.motivandroid.data.rewards.RewardData;
import inesc_id.pt.motivandroid.data.rewards.RewardStatus;
import inesc_id.pt.motivandroid.motviAPIClient.MotivAPIClientManager;
import inesc_id.pt.motivandroid.settingsAndProfile.ProfileAndSettingsActivity;
import inesc_id.pt.motivandroid.settingsAndProfile.adapters.TargetAndRewardsListAdapter;

/**
 * TargetAndRewardsFragment
 *
 *  Lists user progress for each available reward.
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

public class TargetAndRewardsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TargetAndRewardsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TargetAndRewardsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TargetAndRewardsFragment newInstance(String param1, String param2) {
        TargetAndRewardsFragment fragment = new TargetAndRewardsFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_target_and_rewards, container, false);

        if(getActivity() instanceof ProfileAndSettingsActivity) {
            ((ProfileAndSettingsActivity) getActivity()).setTitleTextView(getString(R.string.Targets_And_Rewards));
        }

        //try to retrieve latest the latest user reward data from the server
        MotivAPIClientManager.getInstance(getContext()).getMyRewardData();

        ExpandableHeightListView rewardListView = view.findViewById(R.id._dynamic);
        rewardListView.setExpanded(true);
        rewardListView.setSelector(android.R.color.transparent);

        //get reward data
        ArrayList<RewardData> rewardDataArrayList = RewardManager.getInstance().getValidRewardsDataArrayList();

        //list reward data and user progress
        TargetAndRewardsListAdapter adapter = new TargetAndRewardsListAdapter(getContext(), rewardDataArrayList);
        rewardListView.setAdapter(adapter);

        boolean alreadyShowingPopup = false;

        for(RewardData rewardData : rewardDataArrayList){

            //get user progress for the rewardData reward
            RewardStatus rewardStatus = RewardManager.getInstance().getRewardStatus(getContext(), rewardData.getRewardId());

            if (rewardStatus != null){

                if(!alreadyShowingPopup && !rewardStatus.isHasShownPopup()){

                    if(checkIfWasCompletedAndShowPopup(rewardStatus, rewardData)){

                        Log.d("Reward homefrag", "hasShownPopup " + rewardStatus.isHasShownPopup() + "reward id " + rewardData.getRewardId() + " version " + rewardData.getRewardId());

                        RewardManager.getInstance().setRewardAsShown(rewardData.getRewardId(), getContext());

                        Log.d("Reward homefrag", "hasShownPopup after " + RewardManager.getInstance().getRewardStatus(getContext(), rewardData.getRewardId()).isHasShownPopup());

                        alreadyShowingPopup = true;
                    }

                }

            }

        }


        return view;
    }

    /**
     * @param rewardStatus user progress for rewardData
     * @param rewardData reward data
     * @return true if is showing popup for completing the reward, false otherwise
     */
    private boolean checkIfWasCompletedAndShowPopup(RewardStatus rewardStatus, RewardData rewardData) {

        if (rewardData.getTargetValue() < rewardStatus.getCurrentValue()){

            showCompletedRewardPopup(rewardData.getRewardName());
            return true;

        }else{

            return false;

        }

    }

    /**
     * shows popup to warn user that it has just completed a reward named completedRewardName
     *
     * @param completedRewardName
     */
    private void showCompletedRewardPopup(String completedRewardName) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.reward_completed_popup_layout, null);

        mBuilder.setView(mView);

        ImageView imageView = mView.findViewById(R.id.squirrelImageView);

        Glide.with(this.getContext()).load(R.drawable.onboarding_illustrations_enjoyment).into(imageView);

        TextView rewardNameCompletedTextView = mView.findViewById(R.id.popUpMessageTextView);

        rewardNameCompletedTextView.setText(getString(R.string.You_Have_Completed_Your_Target, completedRewardName));

        final AlertDialog dialog = mBuilder.create();

        Button continueButton = mView.findViewById(R.id.continueButton);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //call method on fragment to show add activities dialog
            }
        });

        dialog.show();


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

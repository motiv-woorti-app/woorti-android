package inesc_id.pt.motivandroid.settingsAndProfile.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.managers.CampaignManager;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.managers.RewardManager;
import inesc_id.pt.motivandroid.data.rewards.RewardData;
import inesc_id.pt.motivandroid.data.rewards.RewardDescription;
import inesc_id.pt.motivandroid.data.rewards.RewardStatus;

/**
 * TargetAndRewardsListAdapter
 *
 *   Adapter used to draw target and target and rewards list. Shows reward info and and what's left
 *  for the user to reach the reward target.
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


public class TargetAndRewardsListAdapter extends ArrayAdapter<RewardData> {

    private Context mContext;
    private ArrayList<RewardData> rewardList;


    public TargetAndRewardsListAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<RewardData> list) {
        super(context, 0 , list);
        mContext = context;
        rewardList = list;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null){
                listItem = LayoutInflater.from(mContext).inflate(R.layout.reward_layout_profile_and_settings, parent, false);
        }

        //reward name
        TextView rewardNameTextView = listItem.findViewById(R.id.rewardNameTextView);
        rewardNameTextView.setText(rewardList.get(position).getRewardName());

        //target campaign
        TextView targetCampaignTextView = listItem.findViewById(R.id.targetCampaignTextView);

        String campaignName = CampaignManager.getInstance(getContext()).getCampaignName(rewardList.get(position).getTargetCampaignId());

        if (campaignName != null){
            targetCampaignTextView.setText(campaignName);
        }else{
            targetCampaignTextView.setText(rewardList.get(position).getTargetCampaignId());
        }

        //organizer name
        TextView organizerNameTextView = listItem.findViewById(R.id.organizerNameTextView);
        organizerNameTextView.setText(rewardList.get(position).getOrganizerName());

        RewardDescription rewardDescription = rewardList.get(position).getRewardDescription("por");

        //short description
        TextView shortDescriptionTextView = listItem.findViewById(R.id.shortDescriptionTextView);
        shortDescriptionTextView.setText(rewardDescription.getShortDescription());

        //long description
        TextView longDescriptionTextView = listItem.findViewById(R.id.longDescriptionTextView);
        longDescriptionTextView.setText(rewardDescription.getLongDescription());

        //link to contact
        TextView linkToContactTextView = listItem.findViewById(R.id.linkToContactTextView);
        linkToContactTextView.setText(rewardList.get(position).getLinkToContact());


        TextView rewardProgressTextView = listItem.findViewById(R.id.rewardProgressTextView);

        //reward progress
        drawCurrentProgress(rewardProgressTextView, rewardList.get(position));

        return listItem;
    }

    /**
     *   takes one reward data, gets the user current progress for the specified reward and writes
     *  the progress on the provided progressTextView
     *
     * @param progressTextView
     * @param rewardData
     */
    public void drawCurrentProgress(TextView progressTextView, RewardData rewardData){

        int currentProgress = 0;


        switch (rewardData.getTargetType()){
            case RewardData.keys.POINTS:
            case RewardData.keys.DAYS:
            case RewardData.keys.TRIPS:

                RewardStatus rewardStatus = RewardManager.getInstance().getRewardStatus(getContext(), rewardData.getRewardId());

                //if there is a reward status for the reward, use that value, otherwise is 0
                if (rewardStatus != null){
                    currentProgress = rewardStatus.getCurrentValue();
                }else{
                    currentProgress = 0;
                }

                break;

            case RewardData.keys.POINTS_ALL_TIME:

                currentProgress = CampaignManager.getInstance(
                        getContext()).getCampaignScore(rewardData.getTargetCampaignId(), getContext());

                break;
            case RewardData.keys.DAYS_ALL_TIME:
                currentProgress = RewardManager.getInstance().getDaysWithTripsAllTime();
                break;
            case RewardData.keys.TRIPS_ALL_TIME:
                currentProgress = RewardManager.getInstance().getTripsAllTime();
                break;
        }

        StringBuilder progress = new StringBuilder();
        progress.append(currentProgress);
        progress.append("/");
        progress.append(rewardData.getTargetValue());
        progress.append(" ");

        //append to the progress textview accoring to the reward type
        switch (rewardData.getTargetType()){
            case RewardData.keys.POINTS:
                progress.append(getContext().getString(R.string.Points));
                break;
            case RewardData.keys.DAYS:
                progress.append(getContext().getString(R.string.Days));
                break;
            case RewardData.keys.TRIPS:
                progress.append(getContext().getString(R.string.Trips));
                break;
            case RewardData.keys.POINTS_ALL_TIME:
                progress.append(getContext().getString(R.string.Points_All_Time));
                break;
            case RewardData.keys.DAYS_ALL_TIME:
                progress.append(getContext().getString(R.string.Days_All_Time));
                break;
            case RewardData.keys.TRIPS_ALL_TIME:
                progress.append(getContext().getString(R.string.Trips_All_Time));
                break;
        }

        progressTextView.setText(progress.toString());

    }

}

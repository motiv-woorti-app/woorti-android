package inesc_id.pt.motivandroid.settingsAndProfile.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.data.pointSystem.CampaignScore;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.AdapterCallback;

/**
 * ChooseCampaignListAdapter
 *
 *  Adapter used to draw a list of which campaign the user is enrolled in, and the corresponding
 * current scores of each campaign. Also allows the user to choose of the campaign to be highlighted
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

public class ChooseCampaignListAdapter extends ArrayAdapter<Campaign> {

    private Context mContext;
    private ArrayList<Campaign> optionsList;

    private int selectedOption;
    AdapterCallback adapterCallback;

    private ArrayList<CampaignScore> campaignScores;

    public ChooseCampaignListAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<Campaign> list, ArrayList<CampaignScore> campaignScores,  AdapterCallback adapterCallback, int selectedOption) {
        super(context, 0 , list);
        mContext = context;
        optionsList = list;

        this.selectedOption = selectedOption;

        this.adapterCallback = adapterCallback;
        this.campaignScores = campaignScores;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null){

            /** if list has only one option (rounded corners on top and bottom **/
            if(optionsList.size() == 1){

                listItem = LayoutInflater.from(mContext).inflate(R.layout.only_one_choose_campaign_scores_item_layout, parent, false);

            }else{
                /** if list has only one option*/
                if (position == 0) {
                    /** first position (top corners rounded) */
                    listItem = LayoutInflater.from(mContext).inflate(R.layout.top_choose_campaign_scores_item_layout, parent, false);
                }else if (position == optionsList.size()-1){
                    /** last position (bottom corners rounded) */
                    listItem = LayoutInflater.from(mContext).inflate(R.layout.bottom_choose_campaign_scores_item_layout, parent, false);
                }else{
                    /** middle positions (no corners rounded) */
                    listItem = LayoutInflater.from(mContext).inflate(R.layout.mid_choose_campaign_scores_item_layout, parent, false);
                }

            }

        }

        TextView option = (TextView) listItem.findViewById(R.id.campaignNameTextView);
        option.setText(optionsList.get(position).getName());

        TextView score = (TextView) listItem.findViewById(R.id.campaignPointsTextView);

        int campaignPoints = 0;

        for (CampaignScore campaignScore : campaignScores){
            if (campaignScore.getCampaignID().equals(optionsList.get(position).getCampaignID())){
                campaignPoints = campaignScore.getCampaignScore();
            }
        }

        score.setText(campaignPoints+"");

        /** change the color and embolden the selected option **/
        if(position == selectedOption){
            ((CheckBox) listItem.findViewById(R.id.checkbox)).setChecked(true);

            option.setTypeface(option.getTypeface(), Typeface.BOLD);
            option.setTextColor(getContext().getResources().getColor(R.color.colorOrangeTripPolyline));
        }else{
            ((CheckBox) listItem.findViewById(R.id.checkbox)).setChecked(false);
            option.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL), Typeface.NORMAL);
            option.setTextColor(getContext().getResources().getColor(R.color.defaultTextColor));
        }

        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position != selectedOption){
                    // when dataset has changed - getView will be called
                    //again and checkbox and textview will have the correct properties
                    selectedOption = position;
                    notifyDataSetChanged();

                    /** warn activity so that it could act on this change (save selected option) **/
                    adapterCallback.clickedItem(position);
                }

            }
        });

        return listItem;
    }

}

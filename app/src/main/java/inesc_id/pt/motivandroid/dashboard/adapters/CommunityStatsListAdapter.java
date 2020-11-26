package inesc_id.pt.motivandroid.dashboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.util.NumberUtils;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.dashboard.holders.CommunityStatsHolder;
import inesc_id.pt.motivandroid.dashboard.activities.UserStatsActivity;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.utils.NumbersUtil;

/**
 * CommunityStatsListAdapter
 *
 *  Adapter to list user and community stats
 *
 *  * (C) 2017-2020 - The Woorti app is a research (non-commercial) application that was
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

public class CommunityStatsListAdapter extends ArrayAdapter<CommunityStatsHolder>{

    private ArrayList<CommunityStatsHolder> dataSet = new ArrayList<>();
    private Context mContext;
    int mode;

    private static class ViewHolder {
        ImageView transportModeIcon;
        TextView transportModeText;

        TextView valueUserTextView;
        TextView percentageUserTextView;

        TextView valueCommunityTextView;
        TextView percentageCommunityTextView;

        ProgressBar citizensProgressBar;
        ProgressBar userProgressBar;

        TextView descriptionTextView;

    }

    public CommunityStatsListAdapter(ArrayList<CommunityStatsHolder> data, Context context, int mode) {
        super(context, R.layout.modes_used_stats_community_listitem, data);
        //this.dataSet.addAll(data);

        this.mContext = context.getApplicationContext();

        this.dataSet = data;
        this.mode = mode;

    }

    @Override
    public int getViewTypeCount() {
        return dataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final CommunityStatsHolder dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.modes_used_stats_community_listitem, parent, false);

            if (position == 0){

                viewHolder.descriptionTextView = convertView.findViewById(R.id.descriptionTextView);

                switch (mode){

                    case UserStatsActivity.keys.TIME_TRAVELED_COMMUNITY_MODE:
                        viewHolder.descriptionTextView.setText(getContext().getString(R.string.Avg_Minutes));
                        break;

                    case UserStatsActivity.keys.DISTANCE_TRAVELED_COMMUNITY_MODE:
                        viewHolder.descriptionTextView.setText(getContext().getString(R.string.Avg_Kilometers));
                        break;

                    case UserStatsActivity.keys.CO2_COMMUNITY_MODE:
                        viewHolder.descriptionTextView.setText(getContext().getString(R.string.Avg_Kilograms));
                        break;

                    case UserStatsActivity.keys.CALORIES_COMMUNITY_MODE:
                        viewHolder.descriptionTextView.setText(getContext().getString(R.string.Avg_Calories));
                        break;

                }

                viewHolder.descriptionTextView.setVisibility(View.VISIBLE);

                if (dataSet.size() == 1){

                    convertView.setBackground(getContext().getResources().getDrawable(R.drawable.all_rounded_shape));


                }else{

                    convertView.setBackground(getContext().getResources().getDrawable(R.drawable.top_gradient_shape));

                }

            }else if(position == dataSet.size()-1){
                convertView.setBackground(getContext().getResources().getDrawable(R.drawable.bottom_gradient_profile_option_shape));
            }else{
                convertView.setBackground(getContext().getResources().getDrawable(R.drawable.mid_gradient_profile_option_shape));
            }

            viewHolder.citizensProgressBar = convertView.findViewById(R.id.citizensProgressBar);
            viewHolder.userProgressBar = convertView.findViewById(R.id.userProgressBar);

            if (dataModel.getValueCommunity() >= dataModel.getValueUser()){

                viewHolder.citizensProgressBar.setMax(NumbersUtil.roundNoDecimalPlace(dataModel.getValueCommunity()*10));
                viewHolder.userProgressBar.setMax(NumbersUtil.roundNoDecimalPlace(dataModel.getValueCommunity()*10));
            }else {

                viewHolder.citizensProgressBar.setMax(NumbersUtil.roundNoDecimalPlace(dataModel.getValueUser()*10));
                viewHolder.userProgressBar.setMax(NumbersUtil.roundNoDecimalPlace(dataModel.getValueUser()*10));
            }

            viewHolder.userProgressBar.setProgress(NumbersUtil.roundNoDecimalPlace(dataModel.getValueUser()*10));
            viewHolder.citizensProgressBar.setProgress(NumbersUtil.roundNoDecimalPlace(dataModel.getValueCommunity()*10));

            viewHolder.transportModeIcon = (ImageView) convertView.findViewById(R.id.transportModeIcon);
            viewHolder.transportModeText = (TextView) convertView.findViewById(R.id.transportModeText);

            viewHolder.transportModeIcon.setImageResource(ActivityDetected.getTransportIconFromInt(dataModel.getMode()));
            viewHolder.transportModeText.setText(ActivityDetected.getFormalTransportNameWithContext(dataModel.getMode(), mContext));

            viewHolder.valueCommunityTextView = convertView.findViewById(R.id.citizensValueTextView);
            viewHolder.valueUserTextView = convertView.findViewById(R.id.userValueTextView);

            viewHolder.valueCommunityTextView.setText(dataModel.getValueCommunity() + "");
            viewHolder.valueUserTextView.setText(dataModel.getValueUser() + "");

            viewHolder.percentageCommunityTextView = convertView.findViewById(R.id.citizensPercentageTextView);
            viewHolder.percentageUserTextView = convertView.findViewById(R.id.userPercentageTextView);

            viewHolder.percentageCommunityTextView.setText(dataModel.getPercentageCommunity() + "%");
            viewHolder.percentageUserTextView.setText(dataModel.getPercentageUser() + "%");

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }


}
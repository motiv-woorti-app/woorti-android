package inesc_id.pt.motivandroid.myTrips.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityHelper;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityLeg;

/**
 *
 * TripActivitiesGridListAdapter
 *
 *   Adapter to draw a list of activities in a grid like fashion, and allow the user to select
 *   multiple activities.
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
public class TripActivitiesGridListAdapter extends ArrayAdapter<ActivityLeg> {

    public ArrayList<ActivityLeg> dataSet;
    private Context mContext;
    public ArrayList<ActivityLeg> selectedActivities;
    public int type;

    private static class ViewHolder {
        TextView tripActivityTextView;
        ImageView tripActivityIconImageView;

    }

    public TripActivitiesGridListAdapter(ArrayList<ActivityLeg> data, Context context, ArrayList<ActivityLeg> selectedActivities, int type) {
        super(context, R.layout.objective_trip_list_item, data);
        this.dataSet = data;
        this.mContext = context;
        this.selectedActivities = selectedActivities;
        this.type = type;
    }

    @Override
    public int getCount() {
        return dataSet.size();
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final ActivityLeg dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final TripActivitiesGridListAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new TripActivitiesGridListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.objective_trip_list_item, parent, false);

            viewHolder.tripActivityTextView = (TextView) convertView.findViewById(R.id.objetiveTripTextView);
            viewHolder.tripActivityIconImageView = (ImageView) convertView.findViewById(R.id.objetiveTripIconImageView);

            viewHolder.tripActivityTextView.setText(dataModel.getActivityText());

            viewHolder.tripActivityIconImageView.setImageResource(dataModel.getIcon());

            //Log.d("adapter",  "selected size:" + selectedActivities.size());

            int present = -1;

        if(ActivityLeg.getIndex(dataModel, selectedActivities) != -1){
//                Log.d("adapter", "!= -1");
                convertView.setBackground(getContext().getResources().getDrawable(R.drawable.grid_selected_item_shape));
            }else{
//                Log.d("adapter", "== -1");
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }

    public void updateViews(){
        dataSet.clear();

        switch(type) {
            case ActivityLeg.keys.PRODUCTIVITY:
                dataSet.addAll(ActivityHelper.getProductivityActivityFullList());
                break;
            case ActivityLeg.keys.MIND:
                dataSet.addAll(ActivityHelper.getMindActivityFullList());
                break;
            case ActivityLeg.keys.BODY:
                dataSet.addAll(ActivityHelper.getBodyActivityFullList());
                break;
        }

        notifyDataSetChanged();
    }


}
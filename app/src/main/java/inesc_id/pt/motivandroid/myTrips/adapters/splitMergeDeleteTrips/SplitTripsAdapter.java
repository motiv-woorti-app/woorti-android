package inesc_id.pt.motivandroid.myTrips.adapters.splitMergeDeleteTrips;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigest;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.LocationUtils;

/**
 *
 * SplitTripsAdapter
 *
 *   Adapter to draw a list of selectable trips to be split (only one selectable)
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

public class SplitTripsAdapter extends ArrayAdapter<FullTripDigest> {

    private ArrayList<FullTripDigest> dataSet = new ArrayList<>();
    private Context mContext;


    private static class ViewHolder {
        TextView tripDate;
        TextView fromPlace;
        TextView toPlace;
        TextView fromTimestamp;
        TextView toTimestamp;

    }

    public SplitTripsAdapter(ArrayList<FullTripDigest> data, Context context) {
        super(context, R.layout.trip_summary_rp_list_item_view, data);
        //this.dataSet.addAll(data);
        this.dataSet = data;
        this.mContext = context;

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
        final FullTripDigest dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.mytrips_split_full_trips_list_item, parent, false);

            viewHolder.tripDate = (TextView) convertView.findViewById(R.id.dateOfTheTripListView);
            viewHolder.fromPlace = (TextView) convertView.findViewById(R.id.fromPlaceTextView);
            viewHolder.toPlace = (TextView) convertView.findViewById(R.id.toPlaceTextView);
            viewHolder.fromTimestamp = (TextView) convertView.findViewById(R.id.fromTimestamp);
            viewHolder.toTimestamp = (TextView) convertView.findViewById(R.id.toTimestamp);


            if (dataModel.getStartAddress() != null){
                viewHolder.fromPlace.setText(dataModel.getStartAddress());
            }else{
                viewHolder.fromPlace.setText(LocationUtils.getTextLatLng(dataModel.getStartLocation()));
            }

            if (dataModel.getFinalAddress() != null){
                viewHolder.toPlace.setText(dataModel.getFinalAddress());
            }else{
                viewHolder.toPlace.setText(LocationUtils.getTextLatLng(dataModel.getFinalLocation()));
            }

            viewHolder.tripDate.setText(getDateText(dataModel.getInitTimestamp()));

            viewHolder.fromTimestamp.setText(DateHelper.getHoursMinutesFromTSString(dataModel.getInitTimestamp()));
            viewHolder.toTimestamp.setText(DateHelper.getHoursMinutesFromTSString(dataModel.getEndTimestamp()));

            if(position == 0){
                viewHolder.tripDate.setVisibility(View.VISIBLE);
            }else{

                if(DateHelper.isSameDay(dataModel.getInitTimestamp(), dataSet.get(position-1).getInitTimestamp())){
                    viewHolder.tripDate.setVisibility(View.GONE);
                }

            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }

    private String getDateText(long ts){

        Calendar now = Calendar.getInstance();

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        Calendar fromTS = Calendar.getInstance();
        fromTS.setTimeInMillis(ts);

        if (now.get(Calendar.YEAR) == fromTS.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == fromTS.get(Calendar.DAY_OF_YEAR)){

            return getContext().getResources().getString(R.string.Today);

        }else if(yesterday.get(Calendar.YEAR) == fromTS.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == fromTS.get(Calendar.DAY_OF_YEAR)){

            return getContext().getResources().getString(R.string.Yesterday);

        }else{
            return DateHelper.getYearMonthDayFromTSString(ts);
        }

    }


}
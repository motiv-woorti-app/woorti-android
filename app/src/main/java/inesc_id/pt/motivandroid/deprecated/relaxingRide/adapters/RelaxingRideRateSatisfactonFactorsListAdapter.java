package inesc_id.pt.motivandroid.deprecated.relaxingRide.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.validationAndRating.RelaxingProductiveFactor;

//public class RelaxingRideFactorsListAdapter {
//}

@Deprecated
public class RelaxingRideRateSatisfactonFactorsListAdapter extends ArrayAdapter<RelaxingProductiveFactor> {

    private ArrayList<RelaxingProductiveFactor> dataSet;
    private Context mContext;


    private static class ViewHolder {
        TextView relaxingFactorTextView;
        SeekBar factorSatisfactionSeekBar;

    }

    public RelaxingRideRateSatisfactonFactorsListAdapter(ArrayList<RelaxingProductiveFactor> data, Context context) {
        super(context, R.layout.relaxing_ride_rate_service_list_item, data);
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
        final RelaxingProductiveFactor dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.relaxing_ride_rate_service_list_item, parent, false);

            viewHolder.factorSatisfactionSeekBar = (SeekBar) convertView.findViewById(R.id.serviceSatisfactionSeekbar);

            viewHolder.factorSatisfactionSeekBar.setMax(10);
            viewHolder.factorSatisfactionSeekBar.setProgress(5);
            viewHolder.factorSatisfactionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChanged = 5;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChanged = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    progressChanged = seekBar.getProgress();

                        dataModel.setSatisfactionFactor(progressChanged * 10);


                }
            });


            viewHolder.relaxingFactorTextView = (TextView) convertView.findViewById(R.id.serviceTextView);
            viewHolder.relaxingFactorTextView.setText(dataModel.getText());


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }

    public ArrayList<RelaxingProductiveFactor> getRatedFactors(){

        return dataSet;

    }

}
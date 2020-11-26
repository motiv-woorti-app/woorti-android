package inesc_id.pt.motivandroid.deprecated.relaxingRide.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.validationAndRating.RelaxingProductiveFactor;

//public class RelaxingRideFactorsListAdapter {
//}

@Deprecated
public class RelaxingRideFactorsListAdapter extends ArrayAdapter<RelaxingProductiveFactor> {

    private ArrayList<RelaxingProductiveFactor> dataSet = new ArrayList<>();
    private Context mContext;

    private ArrayList<RelaxingProductiveFactor> selected = new ArrayList<>();

    private static class ViewHolder {
        TextView relaxingFactorTextView;
        RadioButton radioButton;

    }

    public RelaxingRideFactorsListAdapter(ArrayList<RelaxingProductiveFactor> data, Context context) {
        super(context, R.layout.relaxing_ride__factors_that_affected_list_item, data);
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
            convertView = inflater.inflate(R.layout.relaxing_ride__factors_that_affected_list_item, parent, false);

            viewHolder.radioButton = (RadioButton) convertView.findViewById(R.id.radioButton);

            viewHolder.radioButton.setOnCheckedChangeListener( new RadioButton.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){
                        selected.add(dataModel);
                    }

                }

            });

            viewHolder.relaxingFactorTextView = (TextView) convertView.findViewById(R.id.relaxingFactorTextView);


            viewHolder.relaxingFactorTextView.setText(dataModel.getText());


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }

    public ArrayList<RelaxingProductiveFactor> getSelectedFactors(){

        return selected;

    }

}
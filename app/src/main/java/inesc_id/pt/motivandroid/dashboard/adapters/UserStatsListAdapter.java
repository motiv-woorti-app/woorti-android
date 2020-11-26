package inesc_id.pt.motivandroid.dashboard.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.dashboard.holders.UserStatsHolder;
import inesc_id.pt.motivandroid.dashboard.activities.UserStatsActivity;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.onboarding.wrappers.ModeOfTransportUsed;

/**
 * UserStatsListAdapter
 *
 *  Adapter to list user stats
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

public class UserStatsListAdapter extends ArrayAdapter<UserStatsHolder>{

    private ArrayList<UserStatsHolder> dataSet = new ArrayList<>();
    private Context mContext;
    int mode;

    private static class ViewHolder {
        ImageView transportModeIcon;
        TextView transportModeText;

        TextView value1TextView;
        TextView value2TextView;

    }

    public UserStatsListAdapter(ArrayList<UserStatsHolder> data, Context context, int mode) {
        super(context, R.layout.modes_used_stats_user_listitem, data);
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
        final UserStatsHolder dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.modes_used_stats_user_listitem, parent, false);

            viewHolder.transportModeIcon = (ImageView) convertView.findViewById(R.id.transportModeIcon);
            viewHolder.transportModeText = (TextView) convertView.findViewById(R.id.transportModeText);

            viewHolder.value1TextView = (TextView) convertView.findViewById(R.id.value1TextView);
            viewHolder.value2TextView = (TextView) convertView.findViewById(R.id.value2TextView);

            viewHolder.value2TextView.setText(dataModel.getParam2() + "%");

            viewHolder.transportModeIcon.setImageResource(ActivityDetected.getTransportIconFromInt(dataModel.getMode()));
            viewHolder.transportModeText.setText(ActivityDetected.getFormalTransportNameWithContext(dataModel.getMode(), mContext));

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(dataModel.getParam1());
            stringBuilder.append(" ");


            switch (mode){

                case UserStatsActivity.keys.TIME_TRAVELED_USER_MODE:
                    stringBuilder.append("min");
                    break;

                case UserStatsActivity.keys.DISTANCE_TRAVELED_USER_MODE:
                    stringBuilder.append("km");
                    break;

                case UserStatsActivity.keys.CO2_USER_MODE:
                    stringBuilder.append("kg");
                    break;

                case UserStatsActivity.keys.CALORIES_USER_MODE:
                    stringBuilder.append("cal");
                    break;

            }

            viewHolder.value1TextView.setText(stringBuilder.toString());

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }

    private void showOtherOptionDialog(final int position, final ModeOfTransportUsed mode) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View mView = inflater.inflate(R.layout.dialog_add_activity_new_mytrips_other_option, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        final EditText otherOptionEditText = mView.findViewById(R.id.otherOptionEditText);

        Button saveOtherOptionButton = mView.findViewById(R.id.saveOtherButton);

        Button backOtherOptionButton = mView.findViewById(R.id.backButton);

        backOtherOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode.setUsed(false);
                dialog.dismiss();
            }
        });

        saveOtherOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String option = otherOptionEditText.getText().toString();

                if (option.length() >= 3){

                    //Update new transport name in the model
                    mode.setModalityTextCode(option);

                    dialog.dismiss();

                }else{
                    Toast.makeText(getContext(), "Must be at least 3 letters long", Toast.LENGTH_SHORT).show();
                }

                //call method on fragment to show add activities dialog
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

}
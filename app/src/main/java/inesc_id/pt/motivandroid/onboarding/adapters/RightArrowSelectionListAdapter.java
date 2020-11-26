package inesc_id.pt.motivandroid.onboarding.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.AdapterCallback;

/**
 *  ModesOfTransportUsedAdapter
 *
 *   General purpose adapter. Lists a list of strings. (used for selection of country and city)
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
public class RightArrowSelectionListAdapter extends ArrayAdapter<String> {

    private ArrayList<String> dataSet = new ArrayList<>();
    private Context mContext;
    private int selectedIndex;
    private String selectedCountry;

    AdapterCallback adapterCallback;

    private static class ViewHolder {
        TextView genericText;
    }

    public RightArrowSelectionListAdapter(AdapterCallback adapterCallback, ArrayList<String> data, Context context, String selectedCountry) {
        super(context, R.layout.onboarding_generic_places_listitem, data);
        //this.dataSet.addAll(data);

        this.adapterCallback = adapterCallback;
        this.mContext = context;
        this.selectedCountry = selectedCountry;
        this.dataSet = data;
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
        final String dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            if(position == dataSet.size()-1)
                convertView = inflater.inflate(R.layout.bottom_options_list_item_layout, parent, false);
            else
                convertView = inflater.inflate(R.layout.mid_options_list_item_layout, parent, false);

            viewHolder.genericText = (TextView) convertView.findViewById(R.id.optionTextTextView);
            viewHolder.genericText.setText(dataModel);
            if(selectedCountry != null && selectedCountry.equals(dataModel)) {
                viewHolder.genericText.setTypeface(ResourcesCompat.getFont(getContext(), R.font.montserrat_bold));
                viewHolder.genericText.setTextColor(mContext.getResources().getColor(R.color.colorOrangeTripPolyline));
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterCallback.clickedItem(position);
            }
        });

        return convertView;
    }

    public void setSelectedIndex(int selectedIndex){
        this.selectedIndex = selectedIndex;
    }

}
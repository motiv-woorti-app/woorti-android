package inesc_id.pt.motivandroid.testingCarousel;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.List;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetectedWrapper;

/**
 *  CorrectModalityMergeAdapter
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
public class CorrectModalityMergeAdapter extends RecyclerView.Adapter<CorrectModalityMergeAdapter.ViewHolder> {



    private List<ActivityDetectedWrapper> data;



    int selectedItem;

    public CorrectModalityMergeAdapter(List<ActivityDetectedWrapper> data, DiscreteScrollView scrollView, int prevModality) {
        this.data = data;
        this.scrollView = scrollView;
        scrollView.scrollToPosition(prevModality);
    }

    DiscreteScrollView scrollView;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.carousel_activity_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        Glide.with(holder.itemView.getContext())
//                .load(data.get(position).getImage())
//                .into(holder.image);

        ActivityDetectedWrapper item = data.get(position);
        holder.setIsRecyclable(false);
        holder.bindType(item);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setSelectedItem(int selectedItem){
//        notifyItemChanged(this.selectedItem);

//        RecyclerView.ViewHolder oldViewOlder = scrollView.getViewHolder(this.selectedItem);
//        TextView textViewOld = oldViewOlder.itemView.findViewById(R.id.textTransportMode);
//        textViewOld.setTextColor(Color.BLACK);
//
//        RecyclerView.ViewHolder viewOlder = scrollView.getViewHolder(selectedItem);
//        TextView textView = viewOlder.itemView.findViewById(R.id.textTransportMode);
//        textView.setTextColor(Color.RED);

        this.selectedItem = selectedItem;


//        this.data.add(new ActivityDetectedWrapper(R.drawable.ic_train_black_24dp, "train"));

//        this.notifyDataSetChanged();



    }

    public int getSelectedItem() {
        return selectedItem;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.iconTransportMode);
            text = (TextView) itemView.findViewById(R.id.textTransportMode);
        }

        public void bindType(ActivityDetectedWrapper item) {


//            LocationDataContainer departure = fullTripToBeValidated.getDeparturePlace();
            text.setText(item.getText());

//            if(selectedItem == getAdapterPosition()){
//                text.setTextColor(Color.RED);
//            }

            icon.setImageResource(item.getIcon());

        }


    }

    public List<ActivityDetectedWrapper> getData() {
        return data;
    }

    public void setData(List<ActivityDetectedWrapper> data) {
        this.data = data;
    }

}
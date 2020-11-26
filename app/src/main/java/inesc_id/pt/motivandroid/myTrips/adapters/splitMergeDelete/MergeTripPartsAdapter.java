package inesc_id.pt.motivandroid.myTrips.adapters.splitMergeDelete;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.FullTripPartValidationWrapper;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.data.tripData.TripArrivalDummy;
import inesc_id.pt.motivandroid.data.tripData.TripDepartureDummy;
import inesc_id.pt.motivandroid.myTrips.AdapterCallback;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.NumbersUtil;

/**
 *
 * MergeTripPartsAdapter
 *
 *   Adapter to draw a list of selectable legs/transfers to be merged/deleted.
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

public class MergeTripPartsAdapter extends RecyclerView.Adapter<MergeTripPartsAdapter.ViewHolder>{

    FullTrip fullTripToBeValidated;
    Context context;
    public ArrayList<FullTripPartValidationWrapper> tripPartList;

    public ArrayList<FullTripPartValidationWrapper> getToBeMerged() {
        return toBeMerged;
    }

    public void setToBeMerged(ArrayList<FullTripPartValidationWrapper> toBeMerged) {
        this.toBeMerged = toBeMerged;
    }

    public ArrayList<FullTripPartValidationWrapper> toBeMerged;

    AdapterCallback adapterCallback;


    public MergeTripPartsAdapter(FullTrip fullTripToBeValidated, Context context) {
        this.fullTripToBeValidated = fullTripToBeValidated;
        this.context = context;
//        this.adapterCallback = adapterCallback;

        //build dummy leg list

        tripPartList = new ArrayList<>();
        toBeMerged = new ArrayList<>();

//        Log.e("mod pos", position + "");
//        Log.e("mod", "leg mod " + packageModel.getLeg().getModality());
//        Log.e("mod", ActivityDetected.keys.modalities[packageModel.getLeg().getModality()]);
//        Log.e("mod", ActivityDetected.getSurveyModalityValue(realMod) + "");

//        Trip firstLeg = fullTripToBeValidated.getAllLegs().get(0);
//        Trip lastLeg = fullTripToBeValidated.getAllLegs().get(fullTripToBeValidated.getAllLegs().size()-1);

        TripDepartureDummy dummyStart = new TripDepartureDummy(null, 0, 0);
        TripArrivalDummy dummyFinish = new TripArrivalDummy(null, 0, 0);

        FullTripPartValidationWrapper departureValidationWrapper = new FullTripPartValidationWrapper(dummyStart, -1);
        FullTripPartValidationWrapper arrivalValidationWrapper = new FullTripPartValidationWrapper(dummyFinish, -1);

        tripPartList.add(departureValidationWrapper);

        int i = 0;
        for(FullTripPart fullTripPart : fullTripToBeValidated.getTripList()){

            FullTripPartValidationWrapper toAdd = new FullTripPartValidationWrapper(fullTripPart, i);
            tripPartList.add(toAdd);

            i++;
        }

        tripPartList.add(tripPartList.size(), arrivalValidationWrapper); //todo check if it is size or size-1

//        for(FullTripPart leg : fullTripToBeValidated.getTripList()){
//
//        }
//
//        if(!routeToBeShown.getJourneyLegs().get(routeToBeShown.getJourneyLegs().size()-1).getTransport().equals("arrival")){
//            Leg dummyArrivalLeg = new Leg();
//            String arrivalTime = routeToBeShown.getJourneyLegs().get(routeToBeShown.getJourneyLegs().size()-1).getArrivalTime();
//            dummyArrivalLeg.setTransport("arrival");
//            dummyArrivalLeg.setarrivalTime(arrivalTime);
//            this.routeToBeShown.addLeg(dummyArrivalLeg);
//        }


    }

    @NonNull
    @Override
    public MergeTripPartsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = null;
        switch (viewType) {
            case FullTripPart.keys.DUMMY_DEPARTURE_LEG:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_delete_split_departure_item, viewGroup, false);
                return new DepartureViewHolder(view);
            case FullTripPart.keys.DUMMY_ARRIVAL_LEG:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_delete_split_arrival_item, viewGroup, false);
                return new ArrivalViewHolder(view);
            case FullTripPart.keys.VALIDATED_LEG:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_delete_trippart_item, viewGroup, false);
                return new TripPartViewHolder(view);
            case FullTripPart.keys.NOT_VALIDATED_LEG:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_delete_trippart_item, viewGroup, false);
                return new TripPartViewHolder(view);

            case FullTripPart.keys.WAITING_EVENT:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_delete_trippart_item, viewGroup, false);
                return new TripPartViewHolder(view);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FullTripPart item = tripPartList.get(position).getFullTripPart();
        holder.setIsRecyclable(false);
        holder.bindType(item);
    }

    @Override
    public int getItemCount() {
        return tripPartList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return tripPartList.get(position).getFullTripPart().getFullTripPartType();

    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindType(FullTripPart item);
    }

    public class ArrivalViewHolder extends MergeTripPartsAdapter.ViewHolder {

        public TextView arrivalPlaceTextView;

        public ArrivalViewHolder(View view) {
            super(view);

            arrivalPlaceTextView = view.findViewById(R.id.arrivalPlaceTextView);
        }

        @Override
        public void bindType(FullTripPart item) {

            arrivalPlaceTextView.setText(fullTripToBeValidated.getFinalTextLocation());

        }
    }

    public class DepartureViewHolder extends MergeTripPartsAdapter.ViewHolder {

        public TextView departurePlaceTextView;

        public DepartureViewHolder(View view) {
            super(view);

            departurePlaceTextView = view.findViewById(R.id.departurePlaceTextView);
        }

        @Override
        public void bindType(FullTripPart item) {

            departurePlaceTextView.setText(fullTripToBeValidated.getInitialTextLocation());

        }
    }


    public class TripPartViewHolder extends MergeTripPartsAdapter.ViewHolder {

        public TextView transportInfo;
        public ImageView transportIcon;
        public CheckBox checkBox;
        public TextView timeTextView;

        public View wholeView;

        public TripPartViewHolder(View view) {
            super(view);

            transportInfo = (TextView) view.findViewById(R.id.transportInfoTextView);
            transportIcon = (ImageView) view.findViewById(R.id.legTransportIconImageView);
            timeTextView = (TextView) view.findViewById(R.id.timeTextView);
            checkBox = view.findViewById(R.id.checkbox);
            wholeView = view;

        }

        @Override
        public void bindType(FullTripPart item) {

            if(toBeMerged.indexOf(tripPartList.get(getAdapterPosition())) != -1){
                checkBox.setChecked(true);
            }

            wholeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkBox.isChecked()){
                            Log.d("merge", "added wrapper " + getAdapterPosition() + " real index " + tripPartList.get(getAdapterPosition()).getRealIndex());
                            toBeMerged.remove(tripPartList.get(getAdapterPosition()));
                            checkBox.setChecked(false);
                    }else{
                        toBeMerged.add(tripPartList.get(getAdapterPosition()));
                        checkBox.setChecked(true);
                    }

//                startActivity(createSplitsLegsIntent());
                    //call method on fragment to show add activities dialog
                }
            });

//            radioButton.setOnCheckedChangeListener( new RadioButton.OnCheckedChangeListener(){
//
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                    Log.d("merge", "added wrapper " + getAdapterPosition() + " real index " + tripPartList.get(getAdapterPosition()).getRealIndex());
//
//
//                    if(isChecked){
//                        Log.d("merge", "added wrapper " + getAdapterPosition() + " real index " + tripPartList.get(getAdapterPosition()).getRealIndex());
//                        toBeMerged.add(tripPartList.get(getAdapterPosition()));
//                    }
//
//                }
//
//            });

            if(item instanceof Trip) {

                int modality = ((Trip) item).getSugestedModeOfTransport();

                if (((Trip) item).getCorrectedModeOfTransport() != -1){
                    modality = ((Trip) item).getCorrectedModeOfTransport();
                }

                int transportIconDrawable = ActivityDetected.getTransportIconFromInt(modality);
                transportIcon.setImageResource(transportIconDrawable);

//                if (modality == ActivityDetected.keys.walking) {
//
//                    double distanceWalked = ((Trip) item).getDistanceTraveled();
//
//                    transportInfo.setText(context.getString(R.string.Walk_For_Distance, distanceWalked +"m"));
//
//                } else {
//                    transportInfo.setText(ActivityDetected.keys.modalities[modality]);
//                }

                if(modality == ActivityDetected.keys.walking){

                    double distanceWalked = ((Trip) item).getDistanceTraveled();

                    transportInfo.setText(context.getString(R.string.Walk_For_Distance, Math.round(distanceWalked) +"m"));
                }else{
//                transportInfo.setText(ActivityDetected.keys.transportName[mode]);
                    transportInfo.setText(ActivityDetected.getFormalTransportNameWithContext(modality, context) + " - " + NumbersUtil.roundToOneDecimalPlace(((Trip)item).getDistanceTraveled()/1000.0) +"km");

                }

            }else{

                transportIcon.setImageResource(R.drawable.mytrips_navigation_transfer);
                transportInfo.setText(context.getString(R.string.Transfer));


            }

            timeTextView.setText(
                    DateHelper.getHoursMinutesFromTSString(item.getInitTimestamp()));
        }
    }

    public void validateAllLegs(){
        for (FullTripPartValidationWrapper fullTripPart : tripPartList){

            if(fullTripPart.getFullTripPart().isTrip()){

                Trip trip = (Trip) fullTripPart.getFullTripPart();

                if (trip.getCorrectedModeOfTransport() == -1){
                    trip.setCorrectedModeOfTransport(trip.getSugestedModeOfTransport());
                }

            }

        }
        notifyDataSetChanged();
    }

}
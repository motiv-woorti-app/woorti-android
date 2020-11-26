package inesc_id.pt.motivandroid.myTrips.adapters.splitMergeDelete;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import inesc_id.pt.motivandroid.myTrips.AdapterCallbackSplit;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.NumbersUtil;

/**
 *
 * SplitLegsAdapter
 *
 *   Adapter to draw a list of selectable legs/transfers to be split (only one selectable)
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

public class SplitLegsAdapter extends RecyclerView.Adapter<SplitLegsAdapter.ViewHolder>{

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

    AdapterCallbackSplit adapterCallback;


    public SplitLegsAdapter(FullTrip fullTripToBeValidated, Context context, AdapterCallbackSplit adapterCallbackSplit) {
        this.fullTripToBeValidated = fullTripToBeValidated;
        this.context = context;
        this.adapterCallback = adapterCallbackSplit;

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

        tripPartList.add(tripPartList.size(), arrivalValidationWrapper);

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
    public SplitLegsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

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
                        .inflate(R.layout.mytrips_split_leg_item, viewGroup, false);
                return new TripPartViewHolder(view);
            case FullTripPart.keys.NOT_VALIDATED_LEG:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_split_leg_item, viewGroup, false);
                return new TripPartViewHolder(view);

            case FullTripPart.keys.WAITING_EVENT:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_split_transfer_item, viewGroup, false);
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

    public class ArrivalViewHolder extends SplitLegsAdapter.ViewHolder {

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

    public class DepartureViewHolder extends SplitLegsAdapter.ViewHolder {

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


    public class TripPartViewHolder extends SplitLegsAdapter.ViewHolder {

        public TextView transportInfo;
        public ImageView transportIcon;
        public TextView timeTextView;

        public View wholeView;

        private RecyclerViewClickListener mListener;


        public TripPartViewHolder(View view) {
            super(view);

            transportInfo = (TextView) view.findViewById(R.id.transportInfoTextView);
            transportIcon = (ImageView) view.findViewById(R.id.legTransportIconImageView);
            timeTextView = (TextView) view.findViewById(R.id.timeTextView);

            wholeView = view;
        }

        @Override
        public void bindType(FullTripPart item) {

            wholeView.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {

                  adapterCallback.splitLegs(tripPartList.get(getAdapterPosition()));
//
                                             }
                                         }
            );

            if(item instanceof Trip) {

                int modality = ((Trip) item).getSugestedModeOfTransport();

                if (((Trip) item).getCorrectedModeOfTransport() != -1){
                    modality = ((Trip) item).getCorrectedModeOfTransport();
                }

                int transportIconDrawable = ActivityDetected.getTransportIconFromInt(modality);
                transportIcon.setImageResource(transportIconDrawable);

                if(modality == ActivityDetected.keys.walking){

                    double distanceWalked = ((Trip) item).getDistanceTraveled();

                    transportInfo.setText(context.getString(R.string.Walk_For_Distance, Math.round(distanceWalked) +"m"));
                }else{
//                transportInfo.setText(ActivityDetected.keys.transportName[mode]);
                    transportInfo.setText(ActivityDetected.getFormalTransportNameWithContext(modality, context) + " - " + NumbersUtil.roundToOneDecimalPlace(((Trip)item).getDistanceTraveled()/1000.0) +"km");

                }



                timeTextView.setText(DateHelper.getHoursMinutesFromTSString(item.getInitTimestamp()));
            }

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

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }

}
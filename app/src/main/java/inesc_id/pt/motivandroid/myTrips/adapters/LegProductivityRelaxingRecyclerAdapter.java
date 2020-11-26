package inesc_id.pt.motivandroid.myTrips.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.whinc.widget.ratingbar.RatingBar;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.FullTripPartValidationWrapper;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.data.tripData.TripArrivalDummy;
import inesc_id.pt.motivandroid.data.tripData.TripDepartureDummy;
import inesc_id.pt.motivandroid.deprecated.relaxingRide.AdapterCallbackRelaxingRating;
import inesc_id.pt.motivandroid.utils.DateHelper;

@Deprecated
public class LegProductivityRelaxingRecyclerAdapter extends RecyclerView.Adapter<LegProductivityRelaxingRecyclerAdapter.ViewHolder>{

    FullTrip fullTripToBeValidated;
    Context context;
    public ArrayList<FullTripPartValidationWrapper> tripPartList;

    AdapterCallbackRelaxingRating adapterCallback;



    public LegProductivityRelaxingRecyclerAdapter(FullTrip fullTripToBeValidated, Context context, AdapterCallbackRelaxingRating adapterCallbackRelaxingRating) {
        this.fullTripToBeValidated = fullTripToBeValidated;
        this.context = context;
//        this.adapterCallback = adapterCallback;

        //build dummy leg list

        tripPartList = new ArrayList<>();

        adapterCallback = adapterCallbackRelaxingRating;

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
    public LegProductivityRelaxingRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = null;
        switch (viewType) {
            case FullTripPart.keys.DUMMY_DEPARTURE_LEG:  //todo
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_legvalidation_departure, viewGroup, false);
                return new DepartureViewHolder(view);
            case FullTripPart.keys.DUMMY_ARRIVAL_LEG:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_legvalidation_arrival, viewGroup, false);
                return new ArrivalViewHolder(view);
            case FullTripPart.keys.VALIDATED_LEG:  //todo
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_legvalidation_relaxing_productivity, viewGroup, false);
                return new TripPartViewHolder(view);
            case FullTripPart.keys.NOT_VALIDATED_LEG:  //todo
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_legvalidation_relaxing_productivity, viewGroup, false);
                return new TripPartViewHolder(view);

            case FullTripPart.keys.WAITING_EVENT:  //todo
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_legvalidation_relaxing_productivity, viewGroup, false);
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

    public class ArrivalViewHolder extends LegProductivityRelaxingRecyclerAdapter.ViewHolder {

        public TextView arrivalPlaceTextView;

        public ArrivalViewHolder(View view) {
            super(view);

            arrivalPlaceTextView = view.findViewById(R.id.arrivalPlaceTextView);
        }

        @Override
        public void bindType(FullTripPart item) {


//            LocationDataContainer departure = fullTripToBeValidated.getDeparturePlace();
            LocationDataContainer arrival = fullTripToBeValidated.getArrivalPlace();


            arrivalPlaceTextView.setText(fullTripToBeValidated.getFinalTextLocation());

        }
    }

    public class DepartureViewHolder extends LegProductivityRelaxingRecyclerAdapter.ViewHolder {

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

    public class TransferViewHolder extends LegProductivityRelaxingRecyclerAdapter.ViewHolder {

        public TransferViewHolder(View view) {
            super(view);
        }

        @Override
        public void bindType(FullTripPart item) {

        }
    }

    public class TripPartViewHolder extends LegProductivityRelaxingRecyclerAdapter.ViewHolder {

        public TextView transportInfo;
        public RatingBar ratingBarProductivity;
        public RatingBar ratingBarRelaxing;
        public ImageView transportIcon;
        public TextView legTimeIntervalTextView;

        public TripPartViewHolder(View view) {
            super(view);

            transportInfo = (TextView) view.findViewById(R.id.transportInfoTextView);
            legTimeIntervalTextView = (TextView) view.findViewById(R.id.legTimeIntervalTextView);
            ratingBarProductivity = (RatingBar) view.findViewById(R.id.ratingBar);
            ratingBarRelaxing = (RatingBar) view.findViewById(R.id.ratingBar2);
            transportIcon = (ImageView) view.findViewById(R.id.legTransportIconImageView);

        }

        @Override
        public void bindType(final FullTripPart item) {

            ratingBarProductivity.setCount(item.getProductivityRelaxingRating().getProductivity());
            ratingBarRelaxing.setCount(item.getProductivityRelaxingRating().getRelaxing());

//            ratingBarProductivity.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
//                @Override
//                public void onChange(RatingBar view, int preCount, int curCount) {
//                    Log.i("TAG", String.format("previous count:%d, current count:%d", preCount, curCount));
//                    item.getProductivityRelaxingRating().setProductivity(curCount);
//                    adapterCallback.openRelaxingDialog(getAdapterPosition() - 1, LegProductivityRelaxingFragment.keys.PRODUCTIVE_FACTORS);
//
//                }
//            });
//
//            ratingBarRelaxing.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
//                @Override
//                public void onChange(RatingBar view, int preCount, int curCount) {
//                    Log.i("TAG", String.format("previous count:%d, current count:%d", preCount, curCount));
//                    item.getProductivityRelaxingRating().setRelaxing(curCount);
//
//                    //plus one because of the departure validation wrappper
//                    adapterCallback.openRelaxingDialog(getAdapterPosition() - 1, LegProductivityRelaxingFragment.keys.RELAXING_FACTORS);
//
//                }
//            });

            legTimeIntervalTextView.setText(DateHelper.getHoursMinutesFromTSString(item.getInitTimestamp()));

            if(item instanceof Trip) {

                int modality;

                if (((Trip) item).getCorrectedModeOfTransport() == -1){
                    modality = ((Trip) item).getSugestedModeOfTransport();
                }else{
                    modality = (((Trip) item).getCorrectedModeOfTransport());
                }

                int transportIconDrawable = ActivityDetected.getTransportIconFromInt(modality);
                transportIcon.setImageResource(transportIconDrawable);



                if (modality == ActivityDetected.keys.walking) {

                    double distanceWalked = ((Trip) item).getDistanceTraveled();

                    transportInfo.setText(context.getString(R.string.Walk_For_Distance, distanceWalked +"m"));

                } else {
                    transportInfo.setText(ActivityDetected.keys.modalities[modality]);
                }

            }else{

                transportIcon.setImageResource(R.drawable.mytrips_navigation_transfer);
                transportInfo.setText(context.getString(R.string.Transfer));

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

}


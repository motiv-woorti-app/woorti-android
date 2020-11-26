package inesc_id.pt.motivandroid.myTrips.adapters.splitMergeDelete;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

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
import inesc_id.pt.motivandroid.myTrips.AdapterCallback;
import inesc_id.pt.motivandroid.utils.LocationUtils;

@Deprecated
public class DeleteTripPartsAdapter extends RecyclerView.Adapter<DeleteTripPartsAdapter.ViewHolder>{

    FullTrip fullTripToBeValidated;
    Context context;
    public ArrayList<FullTripPartValidationWrapper> tripPartList;

    AdapterCallback adapterCallback;

    ArrayList<Integer> toBeDeleted = new ArrayList<>();



    public DeleteTripPartsAdapter(FullTrip fullTripToBeValidated, Context context) {
        this.fullTripToBeValidated = fullTripToBeValidated;
        this.context = context;
//        this.adapterCallback = adapterCallback;

        //build dummy leg list

        tripPartList = new ArrayList<>();

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
    public DeleteTripPartsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

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
                return new TransferViewHolder(view);

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

    public class ArrivalViewHolder extends DeleteTripPartsAdapter.ViewHolder {

        public TextView arrivalPlaceTextView;

        public ArrivalViewHolder(View view) {
            super(view);

            arrivalPlaceTextView = view.findViewById(R.id.arrivalPlaceTextView);
        }

        @Override
        public void bindType(FullTripPart item) {


//            LocationDataContainer departure = fullTripToBeValidated.getDeparturePlace();
            LocationDataContainer arrival = fullTripToBeValidated.getArrivalPlace();


            arrivalPlaceTextView.setText(arrival.getLatitude() + " " + arrival.getLongitude());

        }
    }

    public class DepartureViewHolder extends DeleteTripPartsAdapter.ViewHolder {

        public TextView departurePlaceTextView;

        public DepartureViewHolder(View view) {
            super(view);

            departurePlaceTextView = view.findViewById(R.id.departurePlaceTextView);
        }

        @Override
        public void bindType(FullTripPart item) {


//            LocationDataContainer departure = fullTripToBeValidated.getDeparturePlace();
            LocationDataContainer departure = fullTripToBeValidated.getDeparturePlace();

            departurePlaceTextView.setText(departure.getLatitude() + " " + departure.getLongitude());

        }
    }

    public class TransferViewHolder extends DeleteTripPartsAdapter.ViewHolder {

        public TransferViewHolder(View view) {
            super(view);
        }

        @Override
        public void bindType(FullTripPart item) {

        }
    }

    public class TripPartViewHolder extends DeleteTripPartsAdapter.ViewHolder {

        public TextView transportInfo;
        public ImageView transportIcon;
        public RadioButton radioButton;

        public TripPartViewHolder(View view) {
            super(view);

            transportInfo = (TextView) view.findViewById(R.id.transportInfoTextView);
            transportIcon = (ImageView) view.findViewById(R.id.legTransportIconImageView);
            radioButton = (RadioButton) view.findViewById(R.id.deleteLegRadioButton);

        }

        @Override
        public void bindType(final FullTripPart item) {

            radioButton.setOnCheckedChangeListener( new RadioButton.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){
                        toBeDeleted.add(getAdapterPosition());
                    }

                }

            });

            if(item instanceof Trip) {

                int transportIconDrawable = ActivityDetected.getTransportIconFromInt(((Trip) item).getSugestedModeOfTransport());
                transportIcon.setImageResource(transportIconDrawable);

                if (((Trip) item).getSugestedModeOfTransport() == ActivityDetected.keys.walking) {

                    double distanceWalked = LocationUtils.meterDistanceBetweenTwoLocations(((Trip) item).getLocationDataContainers().get(0), ((Trip) item).getLocationDataContainers().get(((Trip) item).getLocationDataContainers().size() - 1));

                    transportInfo.setText(context.getString(R.string.Walk_For_Distance, distanceWalked +"m"));

                } else {
                    transportInfo.setText(ActivityDetected.keys.modalities[((Trip) item).getSugestedModeOfTransport()]);
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
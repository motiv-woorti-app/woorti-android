package inesc_id.pt.motivandroid.myTrips.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityLeg;
import inesc_id.pt.motivandroid.data.validationAndRating.ProductivityRelaxingLegRating;
import inesc_id.pt.motivandroid.myTrips.AdapterCallbackAddActivities;
import inesc_id.pt.motivandroid.deprecated.MyTripsAddActivitiesFragment;
import inesc_id.pt.motivandroid.utils.DateHelper;

@Deprecated
public class LegAddActivitiesRecyclerAdapter extends RecyclerView.Adapter<LegAddActivitiesRecyclerAdapter.ViewHolder>{

    FullTrip fullTripToBeValidated;
    Context context;
    public ArrayList<FullTripPartValidationWrapper> tripPartList;

    AdapterCallbackAddActivities adapterCallback;

    public LegAddActivitiesRecyclerAdapter(FullTrip fullTripToBeValidated, Context context, AdapterCallbackAddActivities adapterCallback) {
        this.fullTripToBeValidated = fullTripToBeValidated;
        this.context = context;
        this.adapterCallback = adapterCallback;

        //build dummy leg list

        tripPartList = new ArrayList<>();

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
    public LegAddActivitiesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

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
            case FullTripPart.keys.FULL_TRIP_PART_WITH_ACTIVITIES:  //todo
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_legvalidation_add_activities_listitem_with_activities, viewGroup, false);
                return new TripPartWithActivitiesViewHolder(view);
            case FullTripPart.keys.FULL_TRIP_PART_WITHOUT_ACTIVITIES:  //todo
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_legvalidation_add_activities_listitem_without_activities, viewGroup, false);
                return new TripPartWithoutActivitiesViewHolder(view);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FullTripPartValidationWrapper item = tripPartList.get(position);
        holder.setIsRecyclable(false);
        holder.bindType(item);
    }

    @Override
    public int getItemCount() {
        return tripPartList.size();
    }

    @Override
    public int getItemViewType(int position) {

        switch (tripPartList.get(position).getFullTripPart().getFullTripPartType()) {
            case FullTripPart.keys.DUMMY_DEPARTURE_LEG:  //todo
                return FullTripPart.keys.DUMMY_DEPARTURE_LEG;
            case FullTripPart.keys.DUMMY_ARRIVAL_LEG:
                return  FullTripPart.keys.DUMMY_ARRIVAL_LEG;
        }

        if(tripPartList.get(position).getFullTripPart().getLegActivities().size() > 0){
            return FullTripPart.keys.FULL_TRIP_PART_WITH_ACTIVITIES;
        }else{
            return FullTripPart.keys.FULL_TRIP_PART_WITHOUT_ACTIVITIES;
        }

    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindType(FullTripPartValidationWrapper item);
    }

    public class ArrivalViewHolder extends LegAddActivitiesRecyclerAdapter.ViewHolder {

        public TextView arrivalPlaceTextView;

        public ArrivalViewHolder(View view) {
            super(view);

            arrivalPlaceTextView = view.findViewById(R.id.arrivalPlaceTextView);
        }

        @Override
        public void bindType(FullTripPartValidationWrapper item) {

            arrivalPlaceTextView.setText(fullTripToBeValidated.getFinalTextLocation());

        }
    }

    public class DepartureViewHolder extends LegAddActivitiesRecyclerAdapter.ViewHolder {

        public TextView departurePlaceTextView;

        public DepartureViewHolder(View view) {
            super(view);

            departurePlaceTextView = view.findViewById(R.id.departurePlaceTextView);
        }

        @Override
        public void bindType(FullTripPartValidationWrapper item) {

            departurePlaceTextView.setText(fullTripToBeValidated.getInitialTextLocation());

        }
    }

    public class TripPartWithActivitiesViewHolder extends LegAddActivitiesRecyclerAdapter.ViewHolder {

        public ImageView transportIcon;
        public TextView transportInfo;
        public TextView productivityValue;
        public TextView relaxingValue;
        HorizontalScrollView horizontalScrollViewActivities;
//        public Button addActivitiesToLegButton;
        public LinearLayout legActivitiesIconsLayout;
        public TextView legTimeIntervalTextView;

        public Button editActivitiesButton;



        public TripPartWithActivitiesViewHolder(View view) {
            super(view);

            transportIcon = (ImageView) view.findViewById(R.id.legTransportIconImageView);
            transportInfo = (TextView) view.findViewById(R.id.transportInfoTextView);
            productivityValue = (TextView) view.findViewById(R.id.productivityValueTextView);
            relaxingValue = (TextView) view.findViewById(R.id.relaxingValueTextView);
            horizontalScrollViewActivities = (HorizontalScrollView) view.findViewById(R.id.horizontal_scroll);
//            addActivitiesToLegButton = (Button) view.findViewById(R.id.addActivitesToLegButton);
            legActivitiesIconsLayout = (LinearLayout) view.findViewById(R.id.linearActivitiescons);
            legTimeIntervalTextView = (TextView) view.findViewById(R.id.legTimeIntervalTextView);
            editActivitiesButton = (Button) view.findViewById(R.id.editActivitiesButton);

        }

        @Override
        public void bindType(final FullTripPartValidationWrapper item) {

            legTimeIntervalTextView.setText(DateHelper.getHoursMinutesFromTSString(item.getFullTripPart().getInitTimestamp()));

//            addActivitiesToLegButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    adapterCallback.openModalDialog(item,0);
//
//                    //call method on fragment to show bottom sheet
//                }
//            });

            editActivitiesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterCallback.openModalDialog(item, MyTripsAddActivitiesFragment.keys.PRODUCTIVITY_TAB);
                    //call method on fragment to show add activities dialog
                }
            });

            if(item.getFullTripPart().getLegActivities() != null) {

                for (ActivityLeg activityLeg : item.getFullTripPart().getLegActivities()) {


                    ImageView imageView = new ImageView(context);
                    imageView.setPadding(0, 0, 30, 0);
                    imageView.setImageResource(activityLeg.getIcon());
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    legActivitiesIconsLayout.addView(imageView);

                }

                horizontalScrollViewActivities.bringToFront();
            }


            productivityValue.setText((float) item.getFullTripPart().getProductivityRelaxingRating().getProductivity()/ ProductivityRelaxingLegRating.keys.maxCount * 100 + " %");
            relaxingValue.setText((float) item.getFullTripPart().getProductivityRelaxingRating().getRelaxing()/ ProductivityRelaxingLegRating.keys.maxCount * 100 + " %");

            if(item.getFullTripPart() instanceof Trip) {

                int modality;

                if (((Trip) item.getFullTripPart()).getCorrectedModeOfTransport() == -1){
                    modality = ((Trip) item.getFullTripPart()).getSugestedModeOfTransport();
                }else{
                    modality = (((Trip) item.getFullTripPart()).getCorrectedModeOfTransport());
                }

                int transportIconDrawable = ActivityDetected.getTransportIconFromInt(modality);
                transportIcon.setImageResource(transportIconDrawable);

                if (modality == ActivityDetected.keys.walking) {

                    double distanceWalked = ((Trip) item.getFullTripPart()).getDistanceTraveled();

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

    public class TripPartWithoutActivitiesViewHolder extends LegAddActivitiesRecyclerAdapter.ViewHolder {

        public ImageView transportIcon;
        public TextView transportInfo;

        //        public Button addActivitiesToLegButton;
        public LinearLayout legActivitiesIconsLayout;
        public TextView legTimeIntervalTextView;

        public ImageView productivityButton;
        public ImageView mindButton;
        public ImageView bodyButton;


        public TripPartWithoutActivitiesViewHolder(View view) {
            super(view);

            transportIcon = (ImageView) view.findViewById(R.id.legTransportIconImageView);
            transportInfo = (TextView) view.findViewById(R.id.transportInfoTextView);


//            addActivitiesToLegButton = (Button) view.findViewById(R.id.addActivitesToLegButton);
            legTimeIntervalTextView = (TextView) view.findViewById(R.id.legTimeIntervalTextView);

            productivityButton = (ImageView) view.findViewById(R.id.productivityButtonImageView);
            mindButton = (ImageView) view.findViewById(R.id.mindButtonImageView);
            bodyButton = (ImageView) view.findViewById(R.id.bodyButtonImageView);

        }

        @Override
        public void bindType(final FullTripPartValidationWrapper item) {

            legTimeIntervalTextView.setText(DateHelper.getHoursMinutesFromTSString(item.getFullTripPart().getInitTimestamp()));

            productivityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    adapterCallback.openModalDialog(item, MyTripsAddActivitiesFragment.keys.PRODUCTIVITY_TAB);
                    //call method on fragment to show add activities dialog
                }
            });

            mindButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    adapterCallback.openModalDialog(item, MyTripsAddActivitiesFragment.keys.MIND_TAB);
                }
            });

            bodyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    adapterCallback.openModalDialog(item, MyTripsAddActivitiesFragment.keys.BODY_TAB);
                }
            });

//            addActivitiesToLegButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    adapterCallback.openModalDialog(item,0);
//
//                    //call method on fragment to show bottom sheet
//                }
//            });

            if(item.getFullTripPart() instanceof Trip) {

                int modality;

                if (((Trip) item.getFullTripPart()).getCorrectedModeOfTransport() == -1){
                    modality = ((Trip) item.getFullTripPart()).getSugestedModeOfTransport();
                }else{
                    modality = (((Trip) item.getFullTripPart()).getCorrectedModeOfTransport());
                }

                int transportIconDrawable = ActivityDetected.getTransportIconFromInt(modality);
                transportIcon.setImageResource(transportIconDrawable);

                if (modality == ActivityDetected.keys.walking) {

                    double distanceWalked = ((Trip) item.getFullTripPart()).getDistanceTraveled();

                    transportInfo.setText(context.getString(R.string.Walk_For_Distance, distanceWalked +"m"));

                } else {
                    transportInfo.setText(ActivityDetected.keys.modalities[modality]);
                }
            }else{

                transportIcon.setImageResource(R.drawable.mytrips_navigation_transfer);
                transportInfo.setText("Transfer");

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

    public void updateDataset(FullTrip fullTrip){
//        this.fullTripToBeValidated = fullTrip;

        tripPartList = new ArrayList<>();

        this.fullTripToBeValidated = fullTrip;

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


//        this.fullTripToBeValidated.getTripList().clear();
//        this.fullTripToBeValidated.getTripList().addAll(fullTrip.getTripList());
        notifyDataSetChanged();
    }



}


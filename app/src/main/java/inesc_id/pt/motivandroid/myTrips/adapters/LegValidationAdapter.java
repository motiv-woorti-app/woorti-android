package inesc_id.pt.motivandroid.myTrips.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
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
 * LegValidationAdapter
 *
 *   Adapter to draw a list of legs, and allow the user to correct the modality of the legs.
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
public class LegValidationAdapter extends RecyclerView.Adapter<LegValidationAdapter.ViewHolder>{

    FullTrip fullTripToBeValidated;
    Context context;
    public ArrayList<FullTripPartValidationWrapper> tripPartList;

    AdapterCallback adapterCallback;

    FullTripPart itemBeingChanged;

    UserSettingStateWrapper userSettingStateWrapper;

//    untested todo
//    ArrayList<MOTGroup> userMotGroups;

    public LegValidationAdapter(FullTrip fullTripToBeValidated, Context context, AdapterCallback adapterCallback) {
        this.fullTripToBeValidated = fullTripToBeValidated;
        this.context = context;
        this.adapterCallback = adapterCallback;

        //build dummy leg list

        tripPartList = new ArrayList<>();

        ///////////////////////////////////////////////////////////
        // new mot groups stuff - untested

        //    untested todo
//        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(context, "notExistent");
//
//        if ((userSettingStateWrapper != null) && (userSettingStateWrapper.getUid().equals(FirebaseAuth.getInstance().getUid()))) {
//            Log.e("ProfileAndSettings", "valid");
//
//            if(userSettingStateWrapper.getUserSettings().getMotGroups() != null){
//                userMotGroups = userSettingStateWrapper.getUserSettings().getMotGroups();
//            }else{
//                userMotGroups = MOTGroup.getDefaultGroups();
//            }
//
//        }else{
//            Log.e("ProfileAndSettings", "invalid");
//
//            userMotGroups = MOTGroup.getDefaultGroups();
//
//        }

        //////////////////////////////////////////////////////////////

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

    }

    @NonNull
    @Override
    public LegValidationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

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
                        .inflate(R.layout.mytrips_legvalidation_leg_validated, viewGroup, false);
                return new ValidatedLegViewHolder(view);
            case FullTripPart.keys.NOT_VALIDATED_LEG:  //todo
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.mytrips_legvalidation_leg_notvalidated, viewGroup, false);
                return new NotValidatedLegViewHolder(view);

            case FullTripPart.keys.WAITING_EVENT:  //todo
            view = LayoutInflater
                    .from(viewGroup.getContext())
                    .inflate(R.layout.mytrips_legvalidation_transfer, viewGroup, false);
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

    public class ArrivalViewHolder extends LegValidationAdapter.ViewHolder {

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

    public class DepartureViewHolder extends LegValidationAdapter.ViewHolder {

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

    public class TransferViewHolder extends LegValidationAdapter.ViewHolder {

        public TextView transferInitTimeTextView;

        public TransferViewHolder(View view) {
            super(view);
            transferInitTimeTextView = view.findViewById(R.id.transferInitTimeTextView);
        }


        @Override
        public void bindType(FullTripPart item) {

            String initTime =  DateHelper.getHoursMinutesFromTSString(item.getInitTimestamp());

            transferInitTimeTextView.setText(initTime);

        }
    }

    public class NotValidatedLegViewHolder extends LegValidationAdapter.ViewHolder {

        public TextView transportInfo;
        public Button validateYesButton;
        public Button validateNoButton;
        public ImageView transportIcon;
        public TextView modalityQuestionTextView;
        public TextView legTimeIntervalTextView;

        public NotValidatedLegViewHolder(View view) {
            super(view);

            transportInfo = (TextView) view.findViewById(R.id.transportInfoTextView);
            modalityQuestionTextView = (TextView) view.findViewById(R.id.modalityQuestionValidationTextView);
            validateYesButton = (Button) view.findViewById(R.id.legValidationYesButton);
            validateNoButton = (Button) view.findViewById(R.id.legValidationNoButton);
            transportIcon = (ImageView) view.findViewById(R.id.legTransportIconImageView);
            legTimeIntervalTextView = (TextView) view.findViewById(R.id.legTimeIntervalTextView);


        }

        @Override
        public void bindType(final FullTripPart item) {

            validateYesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((Trip) item).setCorrectedModeOfTransport(((Trip) item).getSugestedModeOfTransport());
                    adapterCallback.somethingChanged();
                    notifyDataSetChanged();


                }
            });

            Log.e("LegValidationAdapter", "modeOfTransport " + (((Trip) item).getModality()));
            Log.e("LegValidationAdapter", "detectedModeOfTransport " + (((Trip) item).getSugestedModeOfTransport()));
            Log.e("LegValidationAdapter", "correctedModeOfTransport " + (((Trip) item).getCorrectedModeOfTransport()));

            validateNoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    itemBeingChanged = item;

                    adapterCallback.openChangeModalityDialog(getAdapterPosition());

                }
            });



            Log.d("bindnotvalidated", "not validated bind");

            int transportIconDrawable = ActivityDetected.getTransportIconFromInt(((Trip) item).getSugestedModeOfTransport());
            transportIcon.setImageResource(transportIconDrawable);

            int mode;


            mode = ((Trip) item).getSugestedModeOfTransport();

            Log.d("suggested", "suggested mode " + ((Trip) item).getSugestedModeOfTransport());

            legTimeIntervalTextView.setText(DateHelper.getHoursMinutesFromTSString(item.getInitTimestamp()));

            if(mode == ActivityDetected.keys.walking){

                double distanceWalked = ((Trip) item).getDistanceTraveled();

                transportInfo.setText(context.getString(R.string.Walk_For_Distance, Math.round(distanceWalked) +"m"));
            }else{
                transportInfo.setText(ActivityDetected.getFormalTransportNameWithContext(mode, context) + " - " + NumbersUtil.roundToOneDecimalPlace(((Trip)item).getDistanceTraveled()/1000.0) +"km");
            }

            if(mode == ActivityDetected.keys.walking){

                modalityQuestionTextView.setText(context.getString(R.string.Were_You_Walking_Or_Running, context.getString(R.string.Walking)));
            }else if(mode == ActivityDetected.keys.running){

                modalityQuestionTextView.setText(context.getString(R.string.Were_You_Walking_Or_Running, context.getString(R.string.Running)));
            }else{

                modalityQuestionTextView.setText(context.getString(R.string.Did_You_Go_The_Mode, ActivityDetected.getFormalTransportNameWithContext(mode, context)));
            }

        }
    }

    public class ValidatedLegViewHolder extends LegValidationAdapter.ViewHolder {

        public TextView transportInfo;

        public ImageView transportIcon;

        public TextView legTimeIntervalValidatedTextView;

        public View wholeView;

        public ValidatedLegViewHolder(View view) {
            super(view);

            transportInfo = (TextView) view.findViewById(R.id.transportInfoTextView);

            transportIcon = (ImageView) view.findViewById(R.id.legTransportIconImageView);

            legTimeIntervalValidatedTextView = (TextView) view.findViewById(R.id.legTimeIntervalValidatedTextView);

            wholeView = view;

        }

        @Override
        public void bindType(final FullTripPart item) {

            Log.d("bind VALIDATED", "bind VALIDATED");

//            int transportIconDrawable = ActivityDetected.getTransportIconInt(ActivityDetected.keys.modalities[((Trip) item).getCorrectedModeOfTransport()]);
            int transportIconDrawable = ActivityDetected.getTransportIconFadedFromInt(((Trip) item).getCorrectedModeOfTransport());
            transportIcon.setImageResource(transportIconDrawable);


            Log.d("LegValidationAdapter", "modeOfTransport " + (((Trip) item).getModality()));
            Log.d("LegValidationAdapter", "detectedModeOfTransport " + (((Trip) item).getSugestedModeOfTransport()));
            Log.d("LegValidationAdapter", "correctedModeOfTransport " + (((Trip) item).getCorrectedModeOfTransport()));


            legTimeIntervalValidatedTextView.setText(DateHelper.getHoursMinutesFromTSString(item.getInitTimestamp()));

            if(((Trip) item).getCorrectedModeOfTransport() == ActivityDetected.keys.walking){

                double distanceWalked = ((Trip) item).getDistanceTraveled();

                transportInfo.setText(context.getString(R.string.Walk_For_Distance, Math.round(distanceWalked) +"m"));
            }else{
                transportInfo.setText(ActivityDetected.getFormalTransportNameWithContext(((Trip) item).getCorrectedModeOfTransport(), context) + " - " + NumbersUtil.roundToOneDecimalPlace(((Trip)item).getDistanceTraveled()/1000.0) +"km");
            }

            wholeView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                      ((Trip) item).setCorrectedModeOfTransport(-1);
                      adapterCallback.somethingChanged();
                      notifyDataSetChanged();
//
                          }
                      }
            );
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

    public void changeModality(int modality){

        ((Trip)itemBeingChanged).setCorrectedModeOfTransport(modality);

        this.notifyDataSetChanged();

    }

    public FullTripPart getItemBeingChanged() {
        return itemBeingChanged;
    }

    public void setItemBeingChanged(FullTripPart itemBeingChanged) {
        this.itemBeingChanged = itemBeingChanged;
    }

}


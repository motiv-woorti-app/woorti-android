package inesc_id.pt.motivandroid.myTrips.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPartValidationWrapper;
import inesc_id.pt.motivandroid.myTrips.adapters.TripActivitiesGridListAdapter;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;

@Deprecated
public class ModalBottomSheetEditTrips extends BottomSheetDialogFragment implements View.OnClickListener {

    FullTripPartValidationWrapper fullTripPartValidationWrapper;
    FullTrip fullTrip;
    TripActivitiesGridListAdapter adapter;

    PersistentTripStorage persistentTripStorage;

//    public static ModalBottomSheetEditTrips newInstance(FullTrip fullTrip, FullTripPartValidationWrapper fullTripPartValidationWrapper) {
    public static ModalBottomSheetEditTrips newInstance() {
        ModalBottomSheetEditTrips fragment = new ModalBottomSheetEditTrips();
        Bundle args = new Bundle();
//        args.putSerializable(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTrip);
//        args.putSerializable(MyTripsFragment.keys.FULL_TRIP_VALIDATION_WRAPPER, fullTripPartValidationWrapper);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
//            fullTripPartValidationWrapper = (FullTripPartValidationWrapper) getArguments().getSerializable(MyTripsFragment.keys.FULL_TRIP_VALIDATION_WRAPPER);
//            fullTrip = (FullTrip) getArguments().getSerializable(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED);

        }

//        persistentTripStorage = new PersistentTripStorage(getContext());
    }


    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }


    };


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_dialog_edittrip_option, null);


        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();

        Log.d("layout",  "content view width" + contentView.getWidth() + "height" + contentView.getHeight());
        Log.d("layout",  "parent width" + ((View) contentView.getParent()).getWidth() + "height" + ((View) contentView.getParent()).getHeight());


        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
//              ((BottomSheetBehavior) behavior).setHideable(false);
        }

        Button splitLegButton = contentView.findViewById(R.id.splitButton);
        splitLegButton.setOnClickListener(this);

        Button mergeLegsButton = contentView.findViewById(R.id.mergeButton);
        mergeLegsButton.setOnClickListener(this);

        Button deleteLegsButton = contentView.findViewById(R.id.deleteButton);
        deleteLegsButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.doneActivitiesButton:

                Log.d("addacts", "edit trips doneactivitiesbutton");

                //fullTrip.updateLegActivities(fullTripPartValidationWrapper, adapter.selectedActivities);
//                Log.d("modal button", "sizeend selected" + adapter.selectedActivities.size());
                //persistentTripStorage.updateFullTripDataObject(fullTrip,fullTrip.getDateId());

                //send broadcast so that add activities fragment can rebuild todo
                Intent localIntent = new Intent(keys.FULL_TRIP_DATA_CHANGED);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(localIntent);

                dismiss();
                break;
        }
    }

    public interface keys{

        String FULL_TRIP_DATA_CHANGED = "fullTripDataChanged";

    }

}

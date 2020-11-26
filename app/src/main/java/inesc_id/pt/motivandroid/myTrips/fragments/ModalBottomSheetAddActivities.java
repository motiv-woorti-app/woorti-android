package inesc_id.pt.motivandroid.myTrips.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityHelper;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityLeg;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.myTrips.adapters.TripActivitiesGridListAdapter;
import inesc_id.pt.motivandroid.persistence.TripKeeper;

/**
 *  ModalBottomSheetAddActivities
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
public class ModalBottomSheetAddActivities extends BottomSheetDialogFragment implements View.OnClickListener {

    int fullTripPartValidationIndex;
    String fullTripID;

    FullTrip fullTrip;
    TripActivitiesGridListAdapter adapter;

//    PersistentTripStorage persistentTripStorage;

    TripKeeper tripKeeper;



    public static ModalBottomSheetAddActivities newInstance(String fullTripID, int fullTripPartValidationIndex) {
        ModalBottomSheetAddActivities fragment = new ModalBottomSheetAddActivities();
        Bundle args = new Bundle();

        args.putString(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTripID);
        args.putInt(MyTripsFragment.keys.FULL_TRIP_VALIDATION_INDEX, fullTripPartValidationIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            fullTripPartValidationIndex =  getArguments().getInt(MyTripsFragment.keys.FULL_TRIP_VALIDATION_INDEX);
            fullTripID = getArguments().getString(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED);

        }

        tripKeeper = TripKeeper.getInstance(getContext());

        //persistentTripStorage = new PersistentTripStorage(getContext());
//        fullTrip = persistentTripStorage.getFullTripByDate(fullTripID);
        fullTrip = tripKeeper.getCurrentFullTrip(fullTripID);
    }


    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
//            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
//                dismiss();
//            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }


    };


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_activities_layout, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();


            //layoutParams.setBehavior(new UserLockBottomSheetBehavior());


        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
              ((BottomSheetBehavior) behavior).setHideable(false);
        }

        Button doneButton = contentView.findViewById(R.id.doneActivitiesButton);

        doneButton.setOnClickListener(this);

        Log.d("layout",  "content view width" + contentView.getWidth() + "height" + contentView.getHeight());
        Log.d("layout",  "parent width" + ((View) contentView.getParent()).getWidth() + "height" + ((View) contentView.getParent()).getHeight());




        final GridView activitiesGridView = contentView.findViewById(R.id.objectiveOfTheTripGridView);
        activitiesGridView.setNumColumns(3);
        activitiesGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        activitiesGridView.setGravity(Gravity.CENTER);

        activitiesGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        final ArrayList<ActivityLeg> activityLegs = ActivityHelper.getProductivityActivityFullList();

        final ArrayList<ActivityLeg> selectedActivities;

        if(fullTrip.getTripList().get(fullTripPartValidationIndex).getLegActivities() == null){
            selectedActivities = new ArrayList<>();
        }else{
            selectedActivities = fullTrip.getTripList().get(fullTripPartValidationIndex).getLegActivities();
        }

        adapter = new TripActivitiesGridListAdapter(activityLegs,getContext(), selectedActivities, ActivityLeg.keys.PRODUCTIVITY);

        activitiesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                int index = ActivityLeg.getIndex(adapter.dataSet.get(position), adapter.selectedActivities);

                if(index == -1){
                    adapter.selectedActivities.add(adapter.dataSet.get(position));
//                    Log.d("adapter", "was not previously selected");
                    v.setBackgroundColor(Color.GRAY);
//                    Log.d("selected size", "size selected was not" + selectedActivities.size());

                }else{
                    adapter.selectedActivities.remove(index);
//                    Log.d("adapter", "was previously selected");
                    v.setBackgroundColor(Color.TRANSPARENT);
//                    Log.d("selected size", "size selected was" + selectedActivities.size());
                }



            }
        });

        activitiesGridView.setAdapter(adapter);

//        activitiesGridView.setOnItemClickListener(this);




    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.doneActivitiesButton:

                Log.d("addacts", "add activities done button");


//                fullTrip.updateLegActivities(fullTripPartValidationIndex, adapter.selectedActivities);
//                Log.d("modal button", "sizeend selected" + adapter.selectedActivities.size());

                tripKeeper.setCurrentFullTrip(fullTrip);
//                persistentTripStorage.updateFullTripDataObject(fullTrip,fullTrip.getDateId());

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

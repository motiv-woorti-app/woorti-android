package inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetectedWrapper;
import inesc_id.pt.motivandroid.persistence.TripKeeper;
import inesc_id.pt.motivandroid.testingCarousel.CorrectModalityMergeAdapter;

@Deprecated
public class ModalBottomSheetCorrectMode extends BottomSheetDialogFragment implements View.OnClickListener, DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder> {

//    FullTripPartValidationWrapper fullTripPartValidationWrapper;
//    FullTrip fullTrip;

    DiscreteScrollView scrollViewModalities;

    CorrectModalityMergeAdapter adapter;

//    PersistentTripStorage persistentTripStorage;

    int prevModality;

    int selectedModality;

    TripKeeper tripKeeper;


    public static ModalBottomSheetCorrectMode newInstance(int prevModality) {
        ModalBottomSheetCorrectMode fragment = new ModalBottomSheetCorrectMode();
        Bundle args = new Bundle();
        args.putSerializable(keys.MERGE_PREVIOUS_MODALITY, prevModality);
//        args.putSerializable(MyTripsFragment.keys.FULL_TRIP_VALIDATION_WRAPPER, fullTripPartValidationWrapper); todo
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
//            fullTripPartValidationWrapper = (FullTripPartValidationWrapper) getArguments().getSerializable(MyTripsFragment.keys.FULL_TRIP_VALIDATION_WRAPPER); todo
//            fullTrip = (FullTrip) getArguments().getSerializable(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED); todo
            this.prevModality = getArguments().getInt(keys.MERGE_PREVIOUS_MODALITY);

        }

//        persistentTripStorage = new PersistentTripStorage(getContext());


        tripKeeper = TripKeeper.getInstance(getContext());


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
        View contentView = View.inflate(getContext(), R.layout.bottom_dialog_correct_transport_mode_merge, null);
//        View contentView = View.inflate(getContext(), R.layout.bottom_dialog_correct_transport_new_mytrips, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();

            //layoutParams.setBehavior(new UserLockBottomSheetBehavior());

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
              ((BottomSheetBehavior) behavior).setHideable(false);
        }

        Button saveButton = contentView.findViewById(R.id.saveModalitiesButton);
        saveButton.setOnClickListener(this);

        //doneButton.setOnClickListener(this);

//        Log.d("layout",  "content view width" + contentView.getWidth() + "height" + contentView.getHeight());
//        Log.d("layout",  "parent width" + ((View) contentView.getParent()).getWidth() + "height" + ((View) contentView.getParent()).getHeight());


        scrollViewModalities = contentView.findViewById(R.id.picker);

        adapter = new CorrectModalityMergeAdapter(ActivityDetectedWrapper.getFullList(),scrollViewModalities, prevModality);
        scrollViewModalities.setAdapter(adapter);

        scrollViewModalities.addOnItemChangedListener(this);

        scrollViewModalities.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.05f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                .build());


        scrollViewModalities.setSlideOnFling(true);

//        final GridView activitiesGridView = contentView.findViewById(R.id.objectiveOfTheTripGridView);
//        activitiesGridView.setNumColumns(3);
//        activitiesGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
//        activitiesGridView.setGravity(Gravity.CENTER);
//
//        activitiesGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
//
//        final ArrayList<ActivityLeg> activityLegs = ActivityLeg.getActivityFullList();
//
//        final ArrayList<ActivityLeg> selectedActivities;
//
//        if(fullTripPartValidationWrapper.getFullTripPart().getLegActivities() == null){
//            selectedActivities = new ArrayList<>();
//        }else{
//            selectedActivities = fullTripPartValidationWrapper.getFullTripPart().getLegActivities();
//        }
//
//        adapter = new TripActivitiesGridListAdapter(activityLegs,getContext(), selectedActivities);
//
//        activitiesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//
//                int index = ActivityLeg.getIndex(adapter.dataSet.get(position), adapter.selectedActivities);
//
//                if(index == -1){
//                    adapter.selectedActivities.add(adapter.dataSet.get(position));
////                    Log.d("adapter", "was not previously selected");
//                    v.setBackgroundColor(Color.GRAY);
////                    Log.d("selected size", "size selected was not" + selectedActivities.size());
//
//                }else{
//                    adapter.selectedActivities.remove(index);
////                    Log.d("adapter", "was previously selected");
//                    v.setBackgroundColor(Color.TRANSPARENT);
////                    Log.d("selected size", "size selected was" + selectedActivities.size());
//                }
//
//
//
//            }
//        });
//
//        activitiesGridView.setAdapter(adapter);



//        activitiesGridView.setOnItemClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.saveModalitiesButton:

//                fullTrip.updateLegActivities(fullTripPartValidationWrapper, adapter.selectedActivities);
////                Log.d("modal button", "sizeend selected" + adapter.selectedActivities.size());
//                persistentTripStorage.updateFullTripDataObject(fullTrip,fullTrip.getDateId());
//
//                //send broadcast so that add activities fragment can rebuild todo
//                Intent localIntent = new Intent(keys.FULL_TRIP_DATA_CHANGED);
//                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(localIntent);

                sendResult(ModalBottomSheetCorrectMode.keys.CORRECT_MODALITY_DIALOG_REQUEST_CODE, selectedModality);

                dismiss();
                break;
        }
    }

    private void sendResult(int REQUEST_CODE, int RESULT) {
        Intent intent = new Intent();
        intent.putExtra(ModalBottomSheetCorrectMode.keys.CHANGE_MODALITY_RESULT, RESULT);
        getTargetFragment().onActivityResult(
                getTargetRequestCode(), REQUEST_CODE, intent);
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {

        selectedModality = adapter.getData().get(adapterPosition).getCode();

    }

    public interface keys{

        String FULL_TRIP_DATA_CHANGED = "fullTripDataChanged";
        String MERGE_PREVIOUS_MODALITY = "mergePreviousModality";


        int CORRECT_MODALITY_DIALOG_REQUEST_CODE = 0;
        String CHANGE_MODALITY_RESULT = "changeModalityResult";

    }

}

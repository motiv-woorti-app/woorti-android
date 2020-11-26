package inesc_id.pt.motivandroid.deprecated;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPartValidationWrapper;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityHelper;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityLeg;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.myTrips.AdapterCallbackAddActivities;
import inesc_id.pt.motivandroid.myTrips.activities.MyTripsActivity;
import inesc_id.pt.motivandroid.myTrips.adapters.LegAddActivitiesRecyclerAdapter;
import inesc_id.pt.motivandroid.myTrips.adapters.TripActivitiesGridListAdapter;
import inesc_id.pt.motivandroid.myTrips.fragments.ModalBottomSheetAddActivities;
import inesc_id.pt.motivandroid.persistence.TripKeeper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyTripsAddActivitiesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyTripsAddActivitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@Deprecated
public class MyTripsAddActivitiesFragment extends Fragment implements AdapterCallbackAddActivities, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    FullTrip fullTripBeingValidated;

    String fullTripID;

    private OnFragmentInteractionListener mListener;

    public MyTripsAddActivitiesFragment() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    LegAddActivitiesRecyclerAdapter adapter;

    Button doneButton;

//    PersistentTripStorage persistentTripStorage;

    TripKeeper tripKeeper;

    TripActivitiesGridListAdapter productivityListAdapter;
    TripActivitiesGridListAdapter mindListAdapter;
    TripActivitiesGridListAdapter bodyListAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fullTrip Parameter 1.
     * @return A new instance of fragment MyTripsAddActivitiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyTripsAddActivitiesFragment newInstance(String fullTrip) {
        MyTripsAddActivitiesFragment fragment = new MyTripsAddActivitiesFragment();
        Bundle args = new Bundle();
        args.putString(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTrip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            fullTripID = getArguments().getString(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED);

//            persistentTripStorage = new PersistentTripStorage(getContext());

            tripKeeper = TripKeeper.getInstance(getContext());


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fullTripBeingValidated = tripKeeper.getCurrentFullTrip(fullTripID);

        if(getActivity() instanceof MyTripsActivity){
            ((MyTripsActivity) getActivity()).computeAndUpdateTripScore();
        }


//        fullTripBeingValidated = persistentTripStorage.getFullTripByDate(fullTripID);
        adapter = new LegAddActivitiesRecyclerAdapter(fullTripBeingValidated, getContext(), this);

        View view = inflater.inflate(R.layout.fragment_my_trips_add_activities, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewAddActivities);

        GridLayoutManager recyclerLayoutManager = new GridLayoutManager(getContext(),1);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setAdapter(adapter);

        doneButton = view.findViewById(R.id.doneFinalButton);
        doneButton.setOnClickListener(this);



//        LinearLayout llBottomSheet = (LinearLayout) view.findViewById(R.id.bottom_sheet);
//        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);



        //bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());


        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void open(RadioButton productivityButton,
                                 RadioButton mindButton,
                                 RadioButton bodyButton,
                                 GridView productivityGridView,
                                 GridView mindGridView,
                                 GridView bodyGridView,
                                 int code){

        switch(code){
            case keys.PRODUCTIVITY_TAB:

                mindButton.setChecked(false);
                bodyButton.setChecked(false);

                mindButton.setTextColor(Color.parseColor("#8ED7FA"));
                bodyButton.setTextColor(Color.parseColor("#C1E7C7"));

                productivityButton.setTextColor(Color.WHITE);

                productivityGridView.setVisibility(View.VISIBLE);
                mindGridView.setVisibility(View.INVISIBLE);
                bodyGridView.setVisibility(View.INVISIBLE);

                break;

            case keys.MIND_TAB:

                productivityButton.setChecked(false);
                bodyButton.setChecked(false);

                productivityButton.setTextColor(Color.parseColor("#FFD790"));
                bodyButton.setTextColor(Color.parseColor("#C1E7C7"));

                mindButton.setTextColor(Color.WHITE);

                productivityGridView.setVisibility(View.INVISIBLE);
                mindGridView.setVisibility(View.VISIBLE);
                bodyGridView.setVisibility(View.INVISIBLE);

                break;

            case keys.BODY_TAB:

                productivityButton.setChecked(false);
                mindButton.setChecked(false);

                productivityButton.setTextColor(Color.parseColor("#FFD790"));
                mindButton.setTextColor(Color.parseColor("#8ED7FA"));

                bodyButton.setTextColor(Color.WHITE);

                bodyGridView.setVisibility(View.VISIBLE);

                productivityGridView.setVisibility(View.INVISIBLE);
                mindGridView.setVisibility(View.INVISIBLE);

                break;

        }


    }

    public void instantiateListAdapters(FullTripPartValidationWrapper fullTripPartValidationWrapper){

        final ArrayList<ActivityLeg> prodActivitiesFullList = ActivityHelper.getProductivityActivityFullList();

        final ArrayList<ActivityLeg> selectedProductivityActivities;

        if(fullTripBeingValidated.getTripList().get(fullTripPartValidationWrapper.getRealIndex()).getProdActivities() == null){
            selectedProductivityActivities = new ArrayList<>();
        }else{
            selectedProductivityActivities = fullTripBeingValidated.getTripList().get(fullTripPartValidationWrapper.getRealIndex()).getProdActivities();
        }

        productivityListAdapter = new TripActivitiesGridListAdapter(prodActivitiesFullList,getContext(), selectedProductivityActivities, ActivityLeg.keys.PRODUCTIVITY);

        ///////////////////////////////

        // if there are different types
        // final ArrayList<ActivityLeg> activityLegs = ActivityLeg.getActivityFullList();

        final ArrayList<ActivityLeg> mindActivitiesFullList = ActivityHelper.getMindActivityFullList();


        final ArrayList<ActivityLeg> selectedMindActivities;

        if(fullTripBeingValidated.getTripList().get(fullTripPartValidationWrapper.getRealIndex()).getMindActivities() == null){
            selectedMindActivities = new ArrayList<>();
        }else{
            selectedMindActivities = fullTripBeingValidated.getTripList().get(fullTripPartValidationWrapper.getRealIndex()).getMindActivities();
        }

        mindListAdapter = new TripActivitiesGridListAdapter(mindActivitiesFullList,getContext(), selectedMindActivities, ActivityLeg.keys.MIND);

        ///////////////////////////////

        // same same
        // final ArrayList<ActivityLeg> activityLegs = ActivityLeg.getActivityFullList();

        final ArrayList<ActivityLeg> bodyActivitiesFullList = ActivityHelper.getBodyActivityFullList();

        final ArrayList<ActivityLeg> selectedBodyActivities;

        if(fullTripBeingValidated.getTripList().get(fullTripPartValidationWrapper.getRealIndex()).getBodyActivities() == null){
            selectedBodyActivities = new ArrayList<>();
        }else{
            selectedBodyActivities = fullTripBeingValidated.getTripList().get(fullTripPartValidationWrapper.getRealIndex()).getBodyActivities();
        }

        bodyListAdapter = new TripActivitiesGridListAdapter(bodyActivitiesFullList,getContext(), selectedBodyActivities, ActivityLeg.keys.BODY);

    }

    public void setGridViewsOptions(GridView gridView){

        gridView.setNumColumns(3);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setGravity(Gravity.CENTER);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

    }

    @Override
    public void openModalDialog(final FullTripPartValidationWrapper fullTripPartValidationWrapper, int option) {

//        ModalBottomSheetAddActivities bottomSheetDialogFragment = ModalBottomSheetAddActivities.newInstance(fullTripID,fullTripPartValidationWrapper.getRealIndex());
//        bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_activity_new_mytrips, null);

        final RadioButton productivityButton = mView.findViewById(R.id.productivityTabButton);
        final RadioButton mindButton = mView.findViewById(R.id.mindTabButton);
        final RadioButton bodyButton = mView.findViewById(R.id.bodyTabButton);

        final Button saveModalitiesButton = mView.findViewById(R.id.saveModalitiesButton);

        final GridView productivityGridView =  mView.findViewById(R.id.productivityActivitiesGridView);
        setGridViewsOptions(productivityGridView);

        final GridView mindGridView =  mView.findViewById(R.id.mindActivitiesGridView);
        setGridViewsOptions(mindGridView);

        final GridView bodyGridView =  mView.findViewById(R.id.bodyActivitiesGridView);
        setGridViewsOptions(bodyGridView);

        instantiateListAdapters(fullTripPartValidationWrapper);

        assignItemClickListeners(productivityGridView, mindGridView, bodyGridView);

        productivityGridView.setAdapter(productivityListAdapter);
        mindGridView.setAdapter(mindListAdapter);
        bodyGridView.setAdapter(bodyListAdapter);


        productivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                open(productivityButton, mindButton, bodyButton, productivityGridView, mindGridView, bodyGridView, keys.PRODUCTIVITY_TAB);
            }
        });

        mindButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                open(productivityButton, mindButton, bodyButton, productivityGridView, mindGridView, bodyGridView, keys.MIND_TAB);
            }
        });

        bodyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                open(productivityButton, mindButton, bodyButton, productivityGridView, mindGridView, bodyGridView, keys.BODY_TAB);
            }
        });

        switch (option){

            case keys.PRODUCTIVITY_TAB:
                open(productivityButton, mindButton, bodyButton, productivityGridView, mindGridView, bodyGridView, keys.PRODUCTIVITY_TAB);
                productivityButton.setChecked(true);

                break;

            case keys.MIND_TAB:
                open(productivityButton, mindButton, bodyButton, productivityGridView, mindGridView, bodyGridView, keys.MIND_TAB);
                mindButton.setChecked(true);
                break;

            case keys.BODY_TAB:
                open(productivityButton, mindButton, bodyButton, productivityGridView, mindGridView, bodyGridView, keys.BODY_TAB);
                bodyButton.setChecked(true);

                break;

        }

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        saveModalitiesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                fullTripBeingValidated.updateLegActivities(fullTripPartValidationWrapper.getRealIndex(),
                        productivityListAdapter.selectedActivities,
                        mindListAdapter.selectedActivities,
                        bodyListAdapter.selectedActivities);

//                Log.d("modal button", "sizeend selected" + adapter.selectedActivities.size());

                tripKeeper.setCurrentFullTrip(fullTripBeingValidated);
                adapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });

//        Button buttonSaveModalityChange = mView.findViewById(R.id.saveModalitiesButton);


        //fica para o done
//        buttonSaveModalityChange.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                if(selectedModality != null){
//
//                    ((Trip) fullTripBeingValidated.getTripList().get(legBeingCorrected.getRealIndex())).setCorrectedModeOfTransport(selectedModality.getCode());
//
//                    //PersistentTripStorage persistentTripStorage = new PersistentTripStorage(getContext());
//                    //persistentTripStorage.updateFullTripDataObject(fullTripBeingValidated, fullTripBeingValidated.getDateId());
//
//                    tripKeeper.setCurrentFullTrip(fullTripBeingValidated);
//
//                    adapter.changeModality(selectedModality.getCode());
//                    somethingChanged();
//
//                    dialog.dismiss();
//
//                }else{
//
//                }
//
//            }
//        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

    }

    public boolean checkAlreadyMaxNumberOfActivities(){

        return ((productivityListAdapter.selectedActivities.size() +
                mindListAdapter.selectedActivities.size() +
                bodyListAdapter.selectedActivities.size()) >= 3);

    }

    private void assignItemClickListeners(final GridView prodGridView, GridView mindGridView, final GridView bodyGridView) {

        prodGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                int index = ActivityLeg.getIndex(productivityListAdapter.dataSet.get(position), productivityListAdapter.selectedActivities);

                if(index == -1){

                    if (checkAlreadyMaxNumberOfActivities()){

                        Toast.makeText(getContext(),"You can only choose 3 activities per leg!", Toast.LENGTH_SHORT).show();

                    }else{

                        if(position == productivityListAdapter.dataSet.size()-1){

                            showOtherOptionDialog(productivityListAdapter ,position, v);

                        }else{
                            productivityListAdapter.selectedActivities.add(productivityListAdapter.dataSet.get(position));
                            v.setBackgroundColor(Color.parseColor("#FDF2E6"));
                        }

                    }
//                    Log.d("selected size", "size selected was not" + selectedActivities.size());

                }else{
                    productivityListAdapter.selectedActivities.remove(index);
//                    Log.d("adapter", "was previously selected");
                    v.setBackgroundColor(Color.TRANSPARENT);
//                    Log.d("selected size", "size selected was" + selectedActivities.size());
                }
            }
        });

        mindGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                int index = ActivityLeg.getIndex(mindListAdapter.dataSet.get(position), mindListAdapter.selectedActivities);

                if(index == -1){

                    if (checkAlreadyMaxNumberOfActivities()){
                        Toast.makeText(getContext(),"You can only choose 3 activities per leg!", Toast.LENGTH_SHORT).show();
                    }else{
                    mindListAdapter.selectedActivities.add(mindListAdapter.dataSet.get(position));
//                    Log.d("adapter", "was not previously selected");
                    v.setBackgroundColor(Color.parseColor("#FDF2E6"));
//                    Log.d("selected size", "size selected was not" + selectedActivities.size());
                    }

                }else{
                    mindListAdapter.selectedActivities.remove(index);
//                    Log.d("adapter", "was previously selected");
                    v.setBackgroundColor(Color.TRANSPARENT);
//                    Log.d("selected size", "size selected was" + selectedActivities.size());
                }
            }
        });

        bodyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                int index = ActivityLeg.getIndex(bodyListAdapter.dataSet.get(position), bodyListAdapter.selectedActivities);

                if(index == -1){

                    if (checkAlreadyMaxNumberOfActivities()){
                        Toast.makeText(getContext(),"You can only choose 3 activities per leg!", Toast.LENGTH_SHORT).show();
                    }else {
                        if(position == bodyListAdapter.dataSet.size()-1){

                            showOtherOptionDialog(bodyListAdapter ,position, v);

                        }else{
                            bodyListAdapter.selectedActivities.add(bodyListAdapter.dataSet.get(position));
                            v.setBackgroundColor(Color.parseColor("#FDF2E6"));
                        }
                    }

                }else{
                    bodyListAdapter.selectedActivities.remove(index);
//                    Log.d("adapter", "was previously selected");
                    v.setBackgroundColor(Color.TRANSPARENT);
//                    Log.d("selected size", "size selected was" + selectedActivities.size());
                }
            }
        });

    }

    private void showOtherOptionDialog(final TripActivitiesGridListAdapter adapter, final int position, final View view) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_activity_new_mytrips_other_option, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        final EditText otherOptionEditText = mView.findViewById(R.id.otherOptionEditText);

        Button saveOtherOptionButton = mView.findViewById(R.id.saveOtherButton);

        Button backOtherOptionButton = mView.findViewById(R.id.backButton);

        backOtherOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveOtherOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String option = otherOptionEditText.getText().toString();

                if (option.length() >= 3){

                    adapter.selectedActivities.add(adapter.dataSet.get(position));
                    adapter.selectedActivities.get(adapter.selectedActivities.size()-1).setActivityText(option);
                    view.setBackgroundColor(Color.parseColor("#FDF2E6"));
                    dialog.dismiss();

                }else{
                    Toast.makeText(getContext(), "Must be at least 3 letters long", Toast.LENGTH_SHORT).show();
                }

                //call method on fragment to show add activities dialog
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("onresume", "onresume");

        fullTripBeingValidated = tripKeeper.getCurrentFullTrip(fullTripID);

//        fullTripBeingValidated = persistentTripStorage.getFullTripByDate(fullTripID);
        adapter.updateDataset(fullTripBeingValidated);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(tripDataChanged, new IntentFilter(ModalBottomSheetAddActivities.keys.FULL_TRIP_DATA_CHANGED));

    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.d("onpause", "onpause");

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(tripDataChanged);

    }

    // with an action named "ActivityRecognitionResult" is broadcasted.
    private BroadcastReceiver tripDataChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Crashlytics.log(Log.DEBUG, "Add activities fragment", "FullTrip data changed! Must get updated data from persistence");

            fullTripBeingValidated = tripKeeper.getCurrentFullTrip(fullTripID);

            //fullTripBeingValidated = persistentTripStorage.getFullTripByDate(fullTripBeingValidated.getDateId());

            adapter.updateDataset(fullTripBeingValidated);

//            refresh();



        }
    };

    private void refresh() {
        adapter = new LegAddActivitiesRecyclerAdapter(fullTripBeingValidated, getContext(), this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.doneFinalButton:
                fullTripBeingValidated.setValidated(true);

//                tripKeeper.setCurrentFullTrip(fullTripBeingValidated);
                tripKeeper.saveFullTripPersistently(fullTripBeingValidated);

//                MotivAPIClientManager motivAPIClientManager = MotivAPIClientManager.getInstance(getContext());
//                motivAPIClientManager.sendConfirmedTripsToServer();

//                persistentTripStorage.updateFullTripDataObject(fullTripBeingValidated, fullTripBeingValidated.getDateId());
                getActivity().finish();
                break;


        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public interface keys{

        int PRODUCTIVITY_TAB = 0;
        int MIND_TAB = 1;
        int BODY_TAB = 2;

    }


}

package inesc_id.pt.motivandroid.myTrips.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetectedWrapper;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPartValidationWrapper;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.data.validationAndRating.TripPartFactor;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.myTrips.AdapterCallback;
import inesc_id.pt.motivandroid.myTrips.MyNestedScrollView;
import inesc_id.pt.motivandroid.myTrips.WorkaroundMapFragment;
import inesc_id.pt.motivandroid.myTrips.activities.MyTripsActivity;
import inesc_id.pt.motivandroid.myTrips.adapters.LegValidationAdapter;
import inesc_id.pt.motivandroid.myTrips.adapters.splitMergeDelete.ModalityCorrectionGridListAdapter;
import inesc_id.pt.motivandroid.persistence.TripKeeper;
import inesc_id.pt.motivandroid.utils.DensityUtil;
import inesc_id.pt.motivandroid.utils.MotivMapUtils;

/**
 * LegValidationFragment
 *
 *  First fragment of the trip validation sequence. Allows the user to see the trip on the map and
 *  correct a leg's detected mode of transport
 *
 *  trip id is passed to the fragment using param MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED
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
public class LegValidationFragment extends Fragment implements AdapterCallback, View.OnClickListener, OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final Logger LOG = LoggerFactory.getLogger(LegValidationFragment.class.getSimpleName());

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    FullTrip fullTripBeingValidated;
    String fullTripID;


    /**
     * list of legs with the option to correct them
     */
    RecyclerView legsBeingValidatedListView;
    LegValidationAdapter adapter;

    Button confirmAllOrNextButton;

    /**
     * "continue" button status -> true if all legs have been validated
     */
    public static boolean buttonStatus;

    private OnFragmentInteractionListener mListener;

    TripKeeper tripKeeper;

    /**
     * temporary object holding a leg being corrected
     */
    FullTripPartValidationWrapper legBeingCorrected;
    int wrapPosition;

    //each category of modes of transport is represented through a gridview
    private ModalityCorrectionGridListAdapter correctModalityPublicTransportAdapter;
    private ModalityCorrectionGridListAdapter correctModalityActiveTransportAdapter;
    private ModalityCorrectionGridListAdapter correctModalityPrivateMotorisedAdapter;

    /**
     * list of available public modes of transport
     */
    private ArrayList<ActivityDetectedWrapper> publicModesList;

    /**
     * list of available active modes of transport
     */
    private ArrayList<ActivityDetectedWrapper> activeModesList;

    /**
     * list of available private modes of transport
     */
    private ArrayList<ActivityDetectedWrapper> privateModesList;


    /**
     * selected modality (if any leg is being corrected by the user)
     */
    ActivityDetectedWrapper selectedModality;

    private GoogleMap mMap;

    MyNestedScrollView nestedScrollView;

    public LegValidationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fullTrip Parameter 1.
     * @return A new instance of fragment LegValidationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LegValidationFragment newInstance(String fullTrip) {
        LegValidationFragment fragment = new LegValidationFragment();

        Bundle args = new Bundle();
        args.putString(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTrip);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            fullTripID = getArguments().getString(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED);

            tripKeeper = TripKeeper.getInstance(getContext());
        }
    }

    SupportMapFragment mapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_leg_validation, container, false);

        legsBeingValidatedListView = view.findViewById(R.id.legsBeingValidatedListView);

        confirmAllOrNextButton = view.findViewById(R.id.confirmOrNextValidationButton);

        confirmAllOrNextButton.setOnClickListener(this);

        nestedScrollView = view.findViewById(R.id.scrollView);

        ViewCompat.setNestedScrollingEnabled(legsBeingValidatedListView, false);

        // check if we have got the googleMap already
        if (mMap == null) {
            mapFragment = (WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
            mapFragment.getMapAsync(this);
        }

        //compute and show possible user max score for this task
        int possibleScore = 0;
        fullTripBeingValidated = tripKeeper.getCurrentFullTrip(fullTripID);
        for(FullTripPart fullTripPart : fullTripBeingValidated.getTripList()){
            if(fullTripPart.isTrip()){
                possibleScore += ((Trip) fullTripPart).getLegScored(getContext()).getPossibleTransportPoints();
            }
        }
        if(getActivity() instanceof  MyTripsActivity){
            ((MyTripsActivity) getActivity()).updatePossibleScore(possibleScore);
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        fullTripBeingValidated = tripKeeper.getCurrentFullTrip(fullTripID);

        SupportMapFragment mapFragment = (WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }

        //draw list of legs
        adapter = new LegValidationAdapter(fullTripBeingValidated, getContext(), this);

        GridLayoutManager recyclerLayoutManager = new GridLayoutManager(getContext(),1);

        legsBeingValidatedListView.setLayoutManager(recyclerLayoutManager);

        legsBeingValidatedListView.setAdapter(adapter);

        if (mMap != null){
            mMap.clear();
            MotivMapUtils.drawRouteOnMap(fullTripBeingValidated, mMap, getContext().getApplicationContext(), height, width);
        }else{
            mapFragment = (WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
            mapFragment.getMapAsync(this);
        }

        if(fullTripBeingValidated.areAllLegsValidated()){
            setButtonToNext();
        }else{
            setButtonToConfirm();
        }

        Log.d("LegValidationFragment", "onresume");
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("LegValidationFragment", "onLowMemory");
        mapFragment.onLowMemory();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirmOrNextValidationButton:
                confirmButtonAction();
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


    /**
     *  called when some has changed (e.g. user corrected a leg's modality) and we need to redraw
     * trip or change the state of the "next/confirm" button
     */
    public void somethingChanged(){

        if(areAllLegsValidated()){
            setButtonToNext();
        }else{
            setButtonToConfirm();
        }

        if(mMap != null) {

            mMap.clear();
            MotivMapUtils.drawRouteOnMap(fullTripBeingValidated, mMap, getContext().getApplicationContext(), height, width);
        }else{
            SupportMapFragment mapFragment = (WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
            mapFragment.getMapAsync(this);
        }
    }

    public void setGridViewsOptions(GridView gridView){

        gridView.setNumColumns(3);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setGravity(Gravity.CENTER);
        gridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);

    }

    AlertDialog dialog;


    /**
     * called from LegValidationAdapter (when the users clicks on a leg to correct it mode of transport)
     *
     * @param positionWrapper
     */
    @Override
    public void openChangeModalityDialog(int positionWrapper) {

        if (dialog != null && dialog.isShowing()){
            return;
        }

        //check what leg is being corrected
        legBeingCorrected = adapter.tripPartList.get(positionWrapper);

        wrapPosition = positionWrapper;

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.bottom_dialog_correct_transport_mytrips_v3, null);

        Log.d("LegValidationFragment", "openChangeModalityDialog");

        final RadioButton publicTransportButton = mView.findViewById(R.id.publicTransportTabButton);
        final RadioButton activeTransportButton = mView.findViewById(R.id.activeTransportTabButton);
        final RadioButton privateMotorisedButton = mView.findViewById(R.id.privateMotorisedTabButton);

        final Button buttonSaveModalityChange = mView.findViewById(R.id.saveModalitiesButton);

        final GridView publicTransportGridView =  mView.findViewById(R.id.publicTransportGridView);
        setGridViewsOptions(publicTransportGridView);


        selectedModality = null;

        publicTransportGridView.setVisibility(View.VISIBLE);

        //get available modes of transport for each category
        publicModesList = ActivityDetectedWrapper.getPublicFullList();
        activeModesList = ActivityDetectedWrapper.getActiveFullList();
        privateModesList = ActivityDetectedWrapper.getPrivateFullList();

        correctModalityPublicTransportAdapter = new ModalityCorrectionGridListAdapter(publicModesList ,getContext());
        correctModalityActiveTransportAdapter= new ModalityCorrectionGridListAdapter(activeModesList ,getContext());
        correctModalityPrivateMotorisedAdapter = new ModalityCorrectionGridListAdapter(privateModesList ,getContext());

        publicTransportGridView.setAdapter(correctModalityPublicTransportAdapter);

        publicTransportGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View v,
                                    final int position, long id) {


                selectedModality = (ActivityDetectedWrapper) parent.getItemAtPosition(position);

                publicTransportGridView.requestFocusFromTouch();
                publicTransportGridView.setSelection(position);

                switch (mode){
                    case keys.PUBLIC_TRANSPORT_TAB:

                        correctModalityPublicTransportAdapter.setSelected(position);

                        correctModalityPrivateMotorisedAdapter.setSelected(-1);
                        correctModalityActiveTransportAdapter.setSelected(-1);

                        if(position == correctModalityPublicTransportAdapter.dataSet.size()-1){

                            showOtherOptionDialog(buttonSaveModalityChange, correctModalityPublicTransportAdapter ,position, v);

                        }

                        break;

                    case keys.ACTIVE_TRANSPORT_TAB:

                        correctModalityActiveTransportAdapter.setSelected(position);

                        correctModalityPrivateMotorisedAdapter.setSelected(-1);
                        correctModalityPublicTransportAdapter.setSelected(-1);

                        if(position == correctModalityActiveTransportAdapter.dataSet.size()-1){

                            showOtherOptionDialog(buttonSaveModalityChange, correctModalityActiveTransportAdapter ,position, v);

                        }

                        break;

                    case keys.PRIVATE_MOTORISED_TRANSPORT_TAB:

                        correctModalityPrivateMotorisedAdapter.setSelected(position);

                        correctModalityPublicTransportAdapter.setSelected(-1);
                        correctModalityActiveTransportAdapter.setSelected(-1);

                        if(position == correctModalityPrivateMotorisedAdapter.dataSet.size()-1){

                            showOtherOptionDialog(buttonSaveModalityChange, correctModalityPrivateMotorisedAdapter ,position, v);

                        }

                        break;
                }


            }
        });

        //open the category of the suggested/detected mode of transport
        open(publicTransportButton, activeTransportButton, privateMotorisedButton, publicTransportGridView,
                ActivityDetected.getTransportCategoryOfTransport(((Trip) fullTripBeingValidated.getTripList().get(legBeingCorrected.getRealIndex())).getSugestedModeOfTransport()));


        publicTransportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                open(publicTransportButton, activeTransportButton, privateMotorisedButton, publicTransportGridView,  keys.PUBLIC_TRANSPORT_TAB);
            }
        });

        activeTransportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                open(publicTransportButton, activeTransportButton, privateMotorisedButton, publicTransportGridView,  keys.ACTIVE_TRANSPORT_TAB);
            }
        });

        privateMotorisedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                open(publicTransportButton, activeTransportButton, privateMotorisedButton, publicTransportGridView,  keys.PRIVATE_MOTORISED_TRANSPORT_TAB);
            }
        });


        mBuilder.setView(mView);
        dialog = mBuilder.create();

        buttonSaveModalityChange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(selectedModality != null ){

                    ((Trip) fullTripBeingValidated.getTripList().get(legBeingCorrected.getRealIndex())).setCorrectedModeOfTransport(selectedModality.getCode());

                    ((Trip) fullTripBeingValidated.getTripList().get(legBeingCorrected.getRealIndex())).setActivitiesFactors(new ArrayList<TripPartFactor>());
                    ((Trip) fullTripBeingValidated.getTripList().get(legBeingCorrected.getRealIndex())).setComfortAndPleasantFactors(new ArrayList<TripPartFactor>());
                    ((Trip) fullTripBeingValidated.getTripList().get(legBeingCorrected.getRealIndex())).setGettingThereFactors(new ArrayList<TripPartFactor>());
                    ((Trip) fullTripBeingValidated.getTripList().get(legBeingCorrected.getRealIndex())).setWhileYouRideFactors(new ArrayList<TripPartFactor>());
                    ((Trip) fullTripBeingValidated.getTripList().get(legBeingCorrected.getRealIndex())).setOtherFactors("");

                    //PersistentTripStorage persistentTripStorage = new PersistentTripStorage(getContext());
                    //persistentTripStorage.updateFullTripDataObject(fullTripBeingValidated, fullTripBeingValidated.getDateId());

                    tripKeeper.setCurrentFullTrip(fullTripBeingValidated);

                    adapter.changeModality(selectedModality.getCode());
                    somethingChanged();

                    dialog.dismiss();

                }else{

                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

    }


    /**
     * shows other alert dialog for the user to state a new mode of transport
     *
     * @param saveModalitiesButton
     * @param adapter
     * @param position
     * @param view
     */
    private void showOtherOptionDialog(final Button saveModalitiesButton, final ModalityCorrectionGridListAdapter adapter, final int position, final View view) {

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

                    selectedModality = adapter.dataSet.get(position);
                    selectedModality.setLabel(option);
                    dialog.dismiss();
                    saveModalitiesButton.performClick();

                }else{
                    Toast.makeText(getContext(), "Must be at least 3 letters long", Toast.LENGTH_SHORT).show();
                }

                //call method on fragment to show add activities dialog
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    //look at https://stackoverflow.com/questions/6097793/cant-unselect-listview-item

    int mode;


    /**
     * opens the tab of the category of modes of transport passed in @param code
     *
     * @param publicTransportButton
     * @param activeTransportButton
     * @param privateMotorisedTransportButton
     * @param publicTransportGridView
     * @param code
     */
    public void open(RadioButton publicTransportButton,
                     RadioButton activeTransportButton,
                     RadioButton privateMotorisedTransportButton,
                     final GridView publicTransportGridView,
                     int code){

        switch(code){
            case keys.PUBLIC_TRANSPORT_TAB:

                activeTransportButton.setChecked(false);
                privateMotorisedTransportButton.setChecked(false);

                publicTransportButton.setChecked(true);

                activeTransportButton.setTextColor(Color.parseColor("#ED7E03"));
                privateMotorisedTransportButton.setTextColor(Color.parseColor("#ED7E03"));

                publicTransportButton.setTextColor(Color.WHITE);

                publicTransportGridView.setAdapter(correctModalityPublicTransportAdapter);

                if(correctModalityPublicTransportAdapter.getSelected() != -1){

                    publicTransportGridView.requestFocusFromTouch();
//                    publicTransportGridView.setItemChecked(2,true);
                    publicTransportGridView.setSelection(correctModalityPublicTransportAdapter.getSelected());
                    publicTransportGridView.refreshDrawableState();

                }

                mode = keys.PUBLIC_TRANSPORT_TAB;

                Log.d("LegValidationFragment" ,"GETsELECTED:" + correctModalityActiveTransportAdapter.getSelected());
//                Log.e("LegValidationFragment", "onitem click checked: " + publicTransportGridView.getCheckedItemPosition());


                break;

            case keys.ACTIVE_TRANSPORT_TAB:

//                Log.d("LegValidationFragment" ,"GETsELECTED:" + correctModalityActiveTransportAdapter.getSelected());


                publicTransportButton.setChecked(false);
                privateMotorisedTransportButton.setChecked(false);

                activeTransportButton.setChecked(true);


                publicTransportButton.setTextColor(Color.parseColor("#ED7E03"));
                privateMotorisedTransportButton.setTextColor(Color.parseColor("#ED7E03"));

                activeTransportButton.setTextColor(Color.WHITE);

                publicTransportGridView.setAdapter(correctModalityActiveTransportAdapter);


                if(correctModalityActiveTransportAdapter.getSelected() != -1){

                    publicTransportGridView.requestFocusFromTouch();
                    publicTransportGridView.setSelection(correctModalityActiveTransportAdapter.getSelected());
                    publicTransportGridView.refreshDrawableState();

                }

                mode = keys.ACTIVE_TRANSPORT_TAB;

                break;

            case keys.PRIVATE_MOTORISED_TRANSPORT_TAB:

//                Log.e("LegValidationFragment" ,"GETsELECTED:" + correctModalityActiveTransportAdapter.getSelected());

                publicTransportButton.setChecked(false);
                activeTransportButton.setChecked(false);

                privateMotorisedTransportButton.setChecked(true);

                mode = keys.PRIVATE_MOTORISED_TRANSPORT_TAB;

                publicTransportButton.setTextColor(Color.parseColor("#ED7E03"));
                activeTransportButton.setTextColor(Color.parseColor("#ED7E03"));

                privateMotorisedTransportButton.setTextColor(Color.WHITE);

                publicTransportGridView.setAdapter(correctModalityPrivateMotorisedAdapter);

                if(correctModalityPrivateMotorisedAdapter.getSelected() != -1){

                    publicTransportGridView.requestFocusFromTouch();
//                    publicTransportGridView.setItemChecked(2,true);
                    publicTransportGridView.setSelection(correctModalityPrivateMotorisedAdapter.getSelected());
                    publicTransportGridView.refreshDrawableState();

                }


                break;

        }


    }

    public void setButtonToConfirm(){
        confirmAllOrNextButton.setText(getString(R.string.Confirm_All));
        buttonStatus = false;
    }

    public void setButtonToNext(){
        confirmAllOrNextButton.setText(getString(R.string.Next));
        buttonStatus = true;
    }

    public boolean areAllLegsValidated(){

        for(FullTripPartValidationWrapper fullTripPart : adapter.tripPartList){
            if(fullTripPart.getFullTripPart().isTrip()){

                Trip trip = (Trip) fullTripPart.getFullTripPart();

                if (trip.getCorrectedModeOfTransport() == -1){
                    return false;
                }

            }
        }

        return true;

    }

    int height;
    int width;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        ((WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch()
                    {
                        nestedScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });

        View mapView = ((WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map)).getView();
        mapView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        height = DensityUtil.convertDpIntoPx(getContext(), mapView.getMeasuredHeight());
        width = DensityUtil.convertDpIntoPx(getContext(), mapView.getMeasuredWidth());

        MotivMapUtils.drawRouteOnMap(fullTripBeingValidated, mMap, getContext().getApplicationContext(), height, width);

    }


    /**
     * if all legs are validated, proceed to the next trip validation fragment
     * if not, validate not yet validated legs
     */
    public void confirmButtonAction(){

        Log.d("LegValidationFragment", "button status: " + buttonStatus);

        if(buttonStatus){
            //every leg has been validated
            fullTripBeingValidated.updateLegCorrectedModeOfTransport(adapter.tripPartList);

            for(FullTripPart fullTripPart : fullTripBeingValidated.getTripList()){
                if(fullTripPart.isTrip()){
                    ((Trip) fullTripPart).getLegScored(getContext()).answerMode();
                }

            }

            tripKeeper.setCurrentFullTrip(fullTripBeingValidated);

            if(getActivity() instanceof MyTripsActivity){
                ((MyTripsActivity) getActivity()).computeAndUpdateTripScore();
            }

            RatingStarsFragment mfragment = RatingStarsFragment.newInstance(RatingStarsFragment.keys.FEEL_ABOUT_TRIP_MODE, fullTripBeingValidated.getDateId(), -1);
            //using Bundle to send data
            FragmentTransaction transaction=getFragmentManager().beginTransaction();
            transaction.replace(R.id.my_trips_main_fragment, mfragment).addToBackStack(null);
            transaction.commit();

        }else{
            //not yet validated
            adapter.validateAllLegs();
            setButtonToNext();
        }
    }

    public interface keys{

        int PUBLIC_TRANSPORT_TAB = 0;
        int ACTIVE_TRANSPORT_TAB = 1;
        int PRIVATE_MOTORISED_TRANSPORT_TAB = 2;

    }

}

package inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetectedWrapper;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.FullTripPartValidationWrapper;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.myTrips.activities.DeleteSplitMergeLegsActivity;
import inesc_id.pt.motivandroid.myTrips.adapters.LegValidationAdapter;
import inesc_id.pt.motivandroid.myTrips.adapters.splitMergeDelete.ModalityCorrectionGridListAdapter;
import inesc_id.pt.motivandroid.myTrips.fragments.LegValidationFragment;
import inesc_id.pt.motivandroid.persistence.TripKeeper;
import inesc_id.pt.motivandroid.tripStateMachine.TripAnalysis;
import inesc_id.pt.motivandroid.utils.MotivMapUtils;

/**
 * MergeTripPartsConfirmOnMapFragment
 *
 * Highlights on a map the selected legs/transfers to be merged. User can confirm or cancel. User
 * chooses corrected mode of transport to be assigned to new merged leg.
 *
 *
 * Trip id is passed through FULLTRIP_ID parameter
 * Legs to be deleted are passed through LEGS_TO_BE_MERGED
 *
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
public class MergeTripPartsConfirmOnMapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FULLTRIP_ID = "fullTripID";
    private static final String LEGS_TO_BE_MERGED = "toBeMerged";

    // TODO: Rename and change types of parameters
    private String fullTripID;

    private OnFragmentInteractionListener mListener;

    private FullTrip fullTrip;

    //full trip parts to be merged (legs or transfers)
    private ArrayList<FullTripPartValidationWrapper> toBeMerged;

    private Button mergeButton;

    private GoogleMap mMap;

    TripKeeper tripKeeper;
    ActivityDetectedWrapper selectedModality;

    //mode correction  -> one grid view per mode category/tab
    private ModalityCorrectionGridListAdapter correctModalityPublicTransportAdapter;
    private ModalityCorrectionGridListAdapter correctModalityActiveTransportAdapter;
    private ModalityCorrectionGridListAdapter correctModalityPrivateMotorisedAdapter;

    private ArrayList<ActivityDetectedWrapper> publicModesList;
    private ArrayList<ActivityDetectedWrapper> activeModesList;
    private ArrayList<ActivityDetectedWrapper> privateModesList;


    public MergeTripPartsConfirmOnMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment MergeTripPartsConfirmOnMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MergeTripPartsConfirmOnMapFragment newInstance(String fullTripID, ArrayList<FullTripPartValidationWrapper> toBeMerged) {
        MergeTripPartsConfirmOnMapFragment fragment = new MergeTripPartsConfirmOnMapFragment();
        Bundle args = new Bundle();
        args.putString(FULLTRIP_ID, fullTripID);
        args.putSerializable(LEGS_TO_BE_MERGED, toBeMerged);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fullTripID = getArguments().getString(FULLTRIP_ID);
            toBeMerged = (ArrayList<FullTripPartValidationWrapper>) getArguments().getSerializable(LEGS_TO_BE_MERGED);
        }

        tripKeeper = TripKeeper.getInstance(getContext());
        fullTrip = tripKeeper.getCurrentFullTrip(fullTripID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_merge_trip_parts_confirm_on_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
        }

        // R.id.map is a FrameLayout, not a Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        mergeButton = view.findViewById(R.id.mergeTripPartOnMapButton);
        mergeButton.setOnClickListener(this);

        ((DeleteSplitMergeLegsActivity) getActivity()).setTitleText(getString(R.string.Confirm_The_Legs_To_Merge));

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        int first = toBeMerged.get(0).getRealIndex();
        int last = toBeMerged.get(toBeMerged.size()-1).getRealIndex();

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.BLACK);



        for(int i = first; i <= last; i++){

            FullTripPart current = fullTrip.getTripList().get(i);

            if(current.getLocationDataContainers().size()==0){
                Toast.makeText(getContext(), "leg " + i + " corrupted from earlier versions", Toast.LENGTH_LONG).show();
                continue;
            }

            for(LocationDataContainer lcd : current.getLocationDataContainers()){
                polyOptions.add(new LatLng(lcd.getLatitude(), lcd.getLongitude()));
            }

            Drawable modeDrawable;

            //set leg/transfer icon drawable
            if(current.isTrip()) {

                if (((Trip) current).getCorrectedModeOfTransport() == -1) {

                    modeDrawable = getResources().getDrawable(ActivityDetected.getTransportIconFromInt(((Trip) current).getSugestedModeOfTransport()));
                } else {
                    modeDrawable = getResources().getDrawable(ActivityDetected.getTransportIconFromInt(((Trip) current).getCorrectedModeOfTransport()));
                }

            }else{
                modeDrawable = getResources().getDrawable(R.drawable.mytrips_navigation_transfer);
            }

            MarkerOptions initLegMarker = new MarkerOptions().position(
                    current.getLocationDataContainers().get(0).getLatLng()).title("Leg Start");

            initLegMarker.icon(MotivMapUtils.getMarkerIconFromDrawable(modeDrawable, 0.5f));
            mMap.addMarker(initLegMarker);

        }

        mMap.addPolyline(polyOptions);

        //set camera position
        LatLng initialPosition = toBeMerged.get(0).getFullTripPart().getLocationDataContainers().get(0).getLatLng();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 15));

    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.mergeTripPartOnMapButton:

                openChangeModalityDialog2();

        }

    }

    public void doFinalMerge(){
        TripAnalysis tripAnalysis = new TripAnalysis(false);
        tripAnalysis.merge(fullTrip, toBeMerged, selectedModality.getCode());
        fullTrip.setNumMerges(fullTrip.getNumMerges()+1);
        tripKeeper.setCurrentFullTrip(fullTrip);
        getActivity().finish();
    }

    int mode;

    /**
     * opens dialog to allow the user to choose the correct modeof transport for the newly merged leg
     */
    public void openChangeModalityDialog2() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.bottom_dialog_correct_transport_mytrips_v3, null);

        //tab buttons
        final RadioButton publicTransportButton = mView.findViewById(R.id.publicTransportTabButton);
        final RadioButton activeTransportButton = mView.findViewById(R.id.activeTransportTabButton);
        final RadioButton privateMotorisedButton = mView.findViewById(R.id.privateMotorisedTabButton);

        final GridView modeCorrectionGridView =  mView.findViewById(R.id.publicTransportGridView);
        setGridViewsOptions(modeCorrectionGridView);

        selectedModality = null;

        modeCorrectionGridView.setVisibility(View.VISIBLE);

        //get list of modes for each mode transport category
        publicModesList = ActivityDetectedWrapper.getPublicFullList();
        activeModesList = ActivityDetectedWrapper.getActiveFullList();
        privateModesList = ActivityDetectedWrapper.getPrivateFullList();

        //each tab/category has its own grid view
        correctModalityPublicTransportAdapter = new ModalityCorrectionGridListAdapter(publicModesList ,getContext());
        correctModalityActiveTransportAdapter= new ModalityCorrectionGridListAdapter(activeModesList ,getContext());
        correctModalityPrivateMotorisedAdapter = new ModalityCorrectionGridListAdapter(privateModesList ,getContext());

        modeCorrectionGridView.setAdapter(correctModalityPublicTransportAdapter);
        modeCorrectionGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View v,
                                    final int position, long id) {

                selectedModality = (ActivityDetectedWrapper) parent.getItemAtPosition(position);

                modeCorrectionGridView.requestFocusFromTouch();

                //rebuild state
                modeCorrectionGridView.setSelection(position);

                switch (mode){
                    case LegValidationFragment.keys.PUBLIC_TRANSPORT_TAB:

                        correctModalityPublicTransportAdapter.setSelected(position);

                        correctModalityPrivateMotorisedAdapter.setSelected(-1);
                        correctModalityActiveTransportAdapter.setSelected(-1);

                        //last option for each category allows the user to add its own custom option
                        if(position == correctModalityPublicTransportAdapter.dataSet.size()-1){
                            showOtherOptionDialog(correctModalityPublicTransportAdapter ,position, v);
                        }

                        break;

                    case LegValidationFragment.keys.ACTIVE_TRANSPORT_TAB:

                        correctModalityActiveTransportAdapter.setSelected(position);

                        correctModalityPrivateMotorisedAdapter.setSelected(-1);
                        correctModalityPublicTransportAdapter.setSelected(-1);

                        //last option for each category allows the user to add its own custom option
                        if(position == correctModalityActiveTransportAdapter.dataSet.size()-1){
                            showOtherOptionDialog(correctModalityActiveTransportAdapter ,position, v);
                        }

                        break;

                    case LegValidationFragment.keys.PRIVATE_MOTORISED_TRANSPORT_TAB:

                        correctModalityPrivateMotorisedAdapter.setSelected(position);

                        correctModalityPublicTransportAdapter.setSelected(-1);
                        correctModalityActiveTransportAdapter.setSelected(-1);

                        //last option for each category allows the user to add its own custom option
                        if(position == correctModalityPrivateMotorisedAdapter.dataSet.size()-1){
                            showOtherOptionDialog(correctModalityPrivateMotorisedAdapter ,position, v);
                        }

                        break;
                }


            }
        });

        open(publicTransportButton, activeTransportButton, privateMotorisedButton, modeCorrectionGridView,  LegValidationFragment.keys.PUBLIC_TRANSPORT_TAB);

        publicTransportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                open(publicTransportButton, activeTransportButton, privateMotorisedButton, modeCorrectionGridView,  LegValidationFragment.keys.PUBLIC_TRANSPORT_TAB);
            }
        });

        activeTransportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                open(publicTransportButton, activeTransportButton, privateMotorisedButton, modeCorrectionGridView,  LegValidationFragment.keys.ACTIVE_TRANSPORT_TAB);
            }
        });

        privateMotorisedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                open(publicTransportButton, activeTransportButton, privateMotorisedButton, modeCorrectionGridView,  LegValidationFragment.keys.PRIVATE_MOTORISED_TRANSPORT_TAB);
            }
        });


        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        Button buttonSaveModalityChange = mView.findViewById(R.id.saveModalitiesButton);

        buttonSaveModalityChange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(selectedModality != null){
                    doFinalMerge();
                    dialog.dismiss();

                }else{

                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();


    }


    /**
     * open transport mode correction dialog. The tab selected initially is chosen according to
     * @param code
     *
     * @param publicTransportButton public transport tab button
     * @param activeTransportButton active transport tab button
     * @param privateMotorisedTransportButton private motorised transport tab button
     * @param modeCorrectionGridView grid view to list modes of transport
     * @param code
     */
    public void open(RadioButton publicTransportButton, RadioButton activeTransportButton,
                     RadioButton privateMotorisedTransportButton, final GridView modeCorrectionGridView,
                     int code){

        switch(code){
            case LegValidationFragment.keys.PUBLIC_TRANSPORT_TAB:

                //check/uncheck selected tab
                activeTransportButton.setChecked(false);
                privateMotorisedTransportButton.setChecked(false);
                publicTransportButton.setChecked(true);

                //set text colour for selected/not selected tab
                activeTransportButton.setTextColor(Color.parseColor("#ED7E03"));
                privateMotorisedTransportButton.setTextColor(Color.parseColor("#ED7E03"));
                publicTransportButton.setTextColor(Color.WHITE);

                modeCorrectionGridView.setAdapter(correctModalityPublicTransportAdapter);

                //rebuild state
                if(correctModalityPublicTransportAdapter.getSelected() != -1){

                    modeCorrectionGridView.requestFocusFromTouch();
                    modeCorrectionGridView.setSelection(correctModalityPublicTransportAdapter.getSelected());
                    modeCorrectionGridView.refreshDrawableState();

                }

                mode = LegValidationFragment.keys.PUBLIC_TRANSPORT_TAB;

                Log.d("LegValidationFragment" ,"GETsELECTED:" + correctModalityActiveTransportAdapter.getSelected());


                break;

            case LegValidationFragment.keys.ACTIVE_TRANSPORT_TAB:

                Log.d("LegValidationFragment" ,"GETsELECTED:" + correctModalityActiveTransportAdapter.getSelected());

                //check/uncheck selected tab
                publicTransportButton.setChecked(false);
                privateMotorisedTransportButton.setChecked(false);
                activeTransportButton.setChecked(true);

                //set text colour for selected/not selected tab
                publicTransportButton.setTextColor(Color.parseColor("#ED7E03"));
                privateMotorisedTransportButton.setTextColor(Color.parseColor("#ED7E03"));
                activeTransportButton.setTextColor(Color.WHITE);

                modeCorrectionGridView.setAdapter(correctModalityActiveTransportAdapter);

                //rebuild state
                if(correctModalityActiveTransportAdapter.getSelected() != -1){

                    modeCorrectionGridView.requestFocusFromTouch();
                    modeCorrectionGridView.setSelection(correctModalityActiveTransportAdapter.getSelected());
                    modeCorrectionGridView.refreshDrawableState();

                }

                mode = LegValidationFragment.keys.ACTIVE_TRANSPORT_TAB;


                break;

            case LegValidationFragment.keys.PRIVATE_MOTORISED_TRANSPORT_TAB:

                Log.d("LegValidationFragment" ,"GETsELECTED:" + correctModalityActiveTransportAdapter.getSelected());

                //check/uncheck selected tab
                publicTransportButton.setChecked(false);
                activeTransportButton.setChecked(false);
                privateMotorisedTransportButton.setChecked(true);

                mode = LegValidationFragment.keys.PRIVATE_MOTORISED_TRANSPORT_TAB;

                //set text colour for selected/not selected tab
                publicTransportButton.setTextColor(Color.parseColor("#ED7E03"));
                activeTransportButton.setTextColor(Color.parseColor("#ED7E03"));
                privateMotorisedTransportButton.setTextColor(Color.WHITE);

                modeCorrectionGridView.setAdapter(correctModalityPrivateMotorisedAdapter);

                //rebuild state
                if(correctModalityPrivateMotorisedAdapter.getSelected() != -1){

                    modeCorrectionGridView.requestFocusFromTouch();
                    modeCorrectionGridView.setSelection(correctModalityPrivateMotorisedAdapter.getSelected());
                    modeCorrectionGridView.refreshDrawableState();

                }
                break;
        }
    }


    /**
     * shows dialog for the user to add a custom non listed mode of transport
     *
     * @param adapter
     * @param position
     * @param view
     */
    private void showOtherOptionDialog(final ModalityCorrectionGridListAdapter adapter, final int position, final View view) {

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

                }else{
                    Toast.makeText(getContext(), "Must be at least 3 letters long", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    public void setGridViewsOptions(GridView gridView){

        gridView.setNumColumns(3);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setGravity(Gravity.CENTER);
        gridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);

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
}

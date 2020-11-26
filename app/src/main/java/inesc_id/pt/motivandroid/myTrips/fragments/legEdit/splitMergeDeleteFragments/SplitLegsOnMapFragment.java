package inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.FullTripPartValidationWrapper;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.myTrips.activities.DeleteSplitMergeLegsActivity;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.TripKeeper;
import inesc_id.pt.motivandroid.tripStateMachine.TripAnalysis;
import inesc_id.pt.motivandroid.utils.LocationUtils;
import inesc_id.pt.motivandroid.utils.MotivMapUtils;

/**
 * SplitLegsOnMapFragment
 *
 * Highlights on a map the selected leg to be split, and allows the user to choose the specific
 * location on the map in which the leg will be split
 *
 * Trip id is passed through SplitLegsListFragment.keys.TRIP_TO_BE_CHANGED parameter
 * Legs to be split is passed through SplitLegsListFragment.keys.LEG_TO_BE_SPLIT
 * If the selected leg to be split is the last -> SplitLegsListFragment.keys.IS_LAST_LEG
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
public class SplitLegsOnMapFragment extends Fragment implements OnMapReadyCallback,  GoogleMap.OnCameraIdleListener, View.OnClickListener, GoogleMap.OnCameraMoveCanceledListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private GoogleMap mMap;

    private FullTripPartValidationWrapper legToBeSplit;
    private int legToBeSplitIndex;

    private boolean isLastLeg;

    private OnFragmentInteractionListener mListener;

    private Marker centerMarker;

    Button splitButton;

    TripAnalysis tripAnalysis;

    LocationDataContainer nearestPoint;

    boolean firstTouch = true;

    String fullTripID;
    FullTrip fullTrip;

    PersistentTripStorage persistentTripStorage;

    TripKeeper tripKeeper;

    public SplitLegsOnMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param legToBeSplit Parameter 1.
     * @return A new instance of fragment SplitLegsOnMapFragment.
     */
    public static SplitLegsOnMapFragment newInstance(int legToBeSplit, boolean isLastLeg, String fullTripID) {
        SplitLegsOnMapFragment fragment = new SplitLegsOnMapFragment();
        Bundle args = new Bundle();

        args.putInt(SplitLegsListFragment.keys.LEG_TO_BE_SPLIT, legToBeSplit);
        args.putBoolean(SplitLegsListFragment.keys.IS_LAST_LEG, isLastLeg);
        args.putString(SplitLegsListFragment.keys.TRIP_TO_BE_CHANGED, fullTripID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            legToBeSplitIndex =  getArguments().getInt(SplitLegsListFragment.keys.LEG_TO_BE_SPLIT);
            isLastLeg = (Boolean) getArguments().getBoolean(SplitLegsListFragment.keys.IS_LAST_LEG);
            fullTripID = getArguments().getString(SplitLegsListFragment.keys.TRIP_TO_BE_CHANGED);
        }

        persistentTripStorage = new PersistentTripStorage(getContext());

        tripKeeper = TripKeeper.getInstance(getContext());

        fullTrip = tripKeeper.getCurrentFullTrip(fullTripID);
        legToBeSplit = new FullTripPartValidationWrapper(fullTrip.getTripList().get(legToBeSplitIndex), legToBeSplitIndex);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_split_legs_on_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
        }

        // R.id.map is a FrameLayout, not a Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        splitButton = view.findViewById(R.id.processSplitLegButton);
        splitButton.setVisibility(View.INVISIBLE);
        splitButton.setOnClickListener(this);

        ((DeleteSplitMergeLegsActivity) getActivity()).setTitleText(getString(R.string.Move_The_Map_To_Split_The_Leg));

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

    // selected leg polyline options
    public static final int PATTERN_DASH_LENGTH_PX = 20;
    public static final int PATTERN_GAP_LENGTH_PX = 20;
    public static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    public static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    public static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.BLACK);

        for(LocationDataContainer lcd : legToBeSplit.getFullTripPart().getLocationDataContainers()){
            polyOptions.add(new LatLng(lcd.getLatitude(), lcd.getLongitude()));
        }

        FullTripPart ftp = legToBeSplit.getFullTripPart();

        if(legToBeSplit.getFullTripPart().isTrip()){

            Drawable modeDrawable;

            //draw mode of transport icon
            if(((Trip) legToBeSplit.getFullTripPart()).getCorrectedModeOfTransport() == -1) {
                modeDrawable = getResources().getDrawable(ActivityDetected.getTransportIconFromInt(((Trip) ftp).getSugestedModeOfTransport()));
            }else{
                modeDrawable = getResources().getDrawable(ActivityDetected.getTransportIconFromInt(((Trip) ftp).getCorrectedModeOfTransport()));
            }

            MarkerOptions initLegMarker = new MarkerOptions().position(
                    ftp.getLocationDataContainers().get(0).getLatLng()).title("Leg Start");

            initLegMarker.icon(MotivMapUtils.getMarkerIconFromDrawable(modeDrawable, 0.5f));
            mMap.addMarker(initLegMarker);

            MarkerOptions finishLegMarker = new MarkerOptions().position(
                    ftp.getLocationDataContainers().get(ftp.getLocationDataContainers().size()-1).getLatLng()).title("Leg finish");

            Drawable finishDrawable = getResources().getDrawable(R.drawable.ic_location_on_black_12dp);
            finishLegMarker.icon(MotivMapUtils.getMarkerIconFromDrawable(finishDrawable, 0.5f));

            mMap.addMarker(finishLegMarker);

        }

        polyOptions.pattern(PATTERN_POLYGON_ALPHA);
        mMap.addPolyline(polyOptions);

        //set map camera position
        LatLng initialPosition = new LatLng(legToBeSplit.getFullTripPart().getLocationDataContainers().get(0).getLatitude(),legToBeSplit.getFullTripPart().getLocationDataContainers().get(0).getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 15));

        centerMarker = mMap.addMarker(new MarkerOptions().position(initialPosition).title("Position to cut").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.setOnCameraIdleListener(this);

        mMap.setOnCameraMoveCanceledListener(this);

        tripAnalysis = new TripAnalysis(false);

    }


    /**
     * when the user moves the camera, find the nearest location to the center of the map and save it
     * as the selected point to split the leg
     */
    @Override
    public void onCameraIdle() {

        if(!firstTouch){

            mMap.setOnCameraIdleListener(null);

        LatLng positionMoved  = mMap.getCameraPosition().target;

        nearestPoint = LocationUtils.findNearestLDC(positionMoved, legToBeSplit.getFullTripPart().getLocationDataContainers());

        splitButton.setVisibility(View.VISIBLE);

        centerMarker.setPosition(nearestPoint.getLatLng());

         }else{
            firstTouch = false;
        }

        mMap.setOnCameraIdleListener(this);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.processSplitLegButton:

                if(legToBeSplit.getFullTripPart().getLocationDataContainers().size() < 4){
                    Toast.makeText(getContext(), getString(R.string.The_Leg_Is_Too_Small_To_Be_Split), Toast.LENGTH_LONG).show();
                }

                ArrayList<Trip> splitLegs = tripAnalysis.splitLeg((Trip)legToBeSplit.getFullTripPart(), nearestPoint, isLastLeg);

                if(splitLegs != null){

                    FullTrip fullTripSplit = tripAnalysis.removeAndInsertLegsIntoTrip(fullTrip,splitLegs, legToBeSplit.getRealIndex());
                    fullTripSplit.setNumSplits(fullTrip.getNumSplits() + 1);

                    Log.d("Split Legs", "leg number: " + fullTripSplit.getTripList().size());
                    Log.d("Split Legs", "get date id split: " + fullTripSplit.getDateId());
                    Log.d("Split Legs", "get date id: " + fullTrip.getDateId());

                    tripKeeper.setCurrentFullTrip(fullTripSplit);
                    persistentTripStorage.updateFullTripDataObject(fullTripSplit, fullTrip.getDateId());

                    getActivity().finish();

//                    Toast.makeText(getContext(), "Leg split successfully", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(),getString(R.string.Error_Splitting_Leg), Toast.LENGTH_LONG).show();
                }


                break;
        }

    }

    @Override
    public void onCameraMoveCanceled() {

        Log.d("camera", "onmovecanceled");

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

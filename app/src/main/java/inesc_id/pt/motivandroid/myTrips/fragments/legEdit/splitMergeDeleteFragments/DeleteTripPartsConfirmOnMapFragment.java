package inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.FullTripPartValidationWrapper;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetectedWrapper;
import inesc_id.pt.motivandroid.myTrips.activities.DeleteSplitMergeLegsActivity;
import inesc_id.pt.motivandroid.myTrips.adapters.splitMergeDelete.ModalityCorrectionGridListAdapter;
import inesc_id.pt.motivandroid.persistence.TripKeeper;
import inesc_id.pt.motivandroid.tripStateMachine.TripAnalysis;
import inesc_id.pt.motivandroid.utils.MotivMapUtils;

/**
 * DeleteTripPartsConfirmOnMapFragment
 *
 * Highlights on a map the selected legs/transfers to be deleted. User can confirm or cancel
 *
 * Trip id is passed through FULLTRIP_ID parameter
 * Legs to be deleted are passed through LEGS_TO_BE_DELETED
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
public class DeleteTripPartsConfirmOnMapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FULLTRIP_ID = "fullTripID";
    private static final String LEGS_TO_BE_DELETED = "toBeDeleted";

    // TODO: Rename and change types of parameters
    private String fullTripID;

    private OnFragmentInteractionListener mListener;

    private FullTrip fullTrip;
    private ArrayList<FullTripPartValidationWrapper> toBeDeleted;

    private Button mergeButton;

    private GoogleMap mMap;

    TripKeeper tripKeeper;

    public DeleteTripPartsConfirmOnMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment MergeTripPartsConfirmOnMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeleteTripPartsConfirmOnMapFragment newInstance(String fullTripID, ArrayList<FullTripPartValidationWrapper> toBeMerged) {
        DeleteTripPartsConfirmOnMapFragment fragment = new DeleteTripPartsConfirmOnMapFragment();
        Bundle args = new Bundle();
        args.putString(FULLTRIP_ID, fullTripID);
        args.putSerializable(LEGS_TO_BE_DELETED, toBeMerged);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fullTripID = getArguments().getString(FULLTRIP_ID);
            toBeDeleted = (ArrayList<FullTripPartValidationWrapper>) getArguments().getSerializable(LEGS_TO_BE_DELETED);
        }

        tripKeeper = TripKeeper.getInstance(getContext());
        fullTrip = tripKeeper.getCurrentFullTrip(fullTripID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_delete_trip_parts_confirm_on_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
        }

        // R.id.map is a FrameLayout, not a Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        mergeButton = view.findViewById(R.id.mergeTripPartOnMapButton);
        mergeButton.setOnClickListener(this);

        ((DeleteSplitMergeLegsActivity) getActivity()).setTitleText(getString(R.string.Confirm_The_Legs_To_delete));


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

        int first = toBeDeleted.get(0).getRealIndex();
        int last = toBeDeleted.get(toBeDeleted.size()-1).getRealIndex();

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

        LatLng initialPosition = toBeDeleted.get(0).getFullTripPart().getLocationDataContainers().get(0).getLatLng();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 15));

    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.mergeTripPartOnMapButton:

                deleteTripParts();

        }

    }

    private void deleteTripParts() {

        TripAnalysis tripAnalysis = new TripAnalysis(false);

        tripAnalysis.deleteTripParts(fullTrip, toBeDeleted);

        fullTrip.setNumDeletes(fullTrip.getNumDeletes()+1);

        tripKeeper.setCurrentFullTrip(fullTrip);

        getActivity().finish();

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

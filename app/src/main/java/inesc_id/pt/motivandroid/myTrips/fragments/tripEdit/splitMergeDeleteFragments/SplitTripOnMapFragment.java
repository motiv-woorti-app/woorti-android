package inesc_id.pt.motivandroid.myTrips.fragments.tripEdit.splitMergeDeleteFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigest;
import inesc_id.pt.motivandroid.myTrips.activities.DeleteSplitMergeTripsActivity;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.tripStateMachine.FullTripMergeSplitDelete;
import inesc_id.pt.motivandroid.utils.MotivMapUtils;

/**
 * SplitTripOnMapFragment
 *
 * Alows the user to choose the specific leg to split the trip on. The tro+
 *
 * Trip digest to be split is passed through ARG_PARAM1 parameter
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
public class SplitTripOnMapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;

    public SplitTripOnMapFragment() {
        // Required empty public constructor
    }

    FullTripDigest digestToBeSplit;

    private Button splitButton;

    int legToSplit;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SplitTripOnMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SplitTripOnMapFragment newInstance(FullTripDigest param1) {
        SplitTripOnMapFragment fragment = new SplitTripOnMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    FullTrip fullTripToSplit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            digestToBeSplit = (FullTripDigest) getArguments().getSerializable(ARG_PARAM1);
        }

        PersistentTripStorage persistentTripStorage = new PersistentTripStorage(getContext());
        fullTripToSplit = persistentTripStorage.getFullTripByDate(digestToBeSplit.getTripID());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_split_trip_on_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
        }

        // R.id.map is a FrameLayout, not a Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        splitButton = view.findViewById(R.id.splitTripPartOnMapButton);
        splitButton.setOnClickListener(this);

        splitButton.setClickable(false);
        splitButton.setAlpha(0.35f);

        ((DeleteSplitMergeTripsActivity) getActivity()).setTitleText(getString(R.string.Click_On_The_Marker_To_Split_The_Trip));

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.splitTripPartOnMapButton:

                FullTripMergeSplitDelete fullTripMergeSplitDelete = new FullTripMergeSplitDelete(getContext());
                fullTripMergeSplitDelete.splitFullTrip(digestToBeSplit, legToSplit);

                Toast.makeText(getContext(), getString(R.string.Trip_Splitted_Successfully), Toast.LENGTH_LONG).show();

                getActivity().finish();

                break;
        }


    }

    GoogleMap mMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MotivMapUtils.drawRouteOnMap(fullTripToSplit, googleMap, getContext(), -1, -1);

        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                try {
                    int position = (int) (marker.getTag());
                    //Using position get Value from arraylist

                    if (position == fullTripToSplit.getTripList().size()-1){

                        Toast.makeText(getContext(), getString(R.string.Please_Choose_Other_Leg_Transfer_Not_Last), Toast.LENGTH_LONG).show();
                        splitButton.setClickable(false);
                        splitButton.setAlpha(0.35f);

                    }else{

                        legToSplit = position;

                        splitButton.setAlpha(1);
                        splitButton.setClickable(true);

                    }

                }catch (NullPointerException e){
                    Toast.makeText(getContext(), getString(R.string.Please_Choose_Leg_Transfer_To_Split), Toast.LENGTH_LONG).show();
                    splitButton.setClickable(false);
                    splitButton.setAlpha(0.35f);
                }



                return false;
            }
        });


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

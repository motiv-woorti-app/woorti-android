package inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPartValidationWrapper;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.myTrips.activities.DeleteSplitMergeLegsActivity;
import inesc_id.pt.motivandroid.myTrips.adapters.splitMergeDelete.MergeTripPartsAdapter;
import inesc_id.pt.motivandroid.myTrips.sort.SortFullTripPartValidationWrapperByRealIndex;
import inesc_id.pt.motivandroid.persistence.TripKeeper;

import static inesc_id.pt.motivandroid.utils.NumbersUtil.areTripPartsConsecutive;

/**
 * DeleteTripPartFragment
 *
 * Allows the user to choose one or more trip legs to delete
 *
 * Trip id is passed through MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED parameter
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
public class DeleteTripPartFragment extends Fragment implements View.OnClickListener {

    FullTrip fullTrip;

    String fullTripID;

    MergeTripPartsAdapter deleteTripPartsAdapter;

    private OnFragmentInteractionListener mListener;

    ArrayList<FullTripPartValidationWrapper> toBeDeleted;

    public DeleteTripPartFragment() {
        // Required empty public constructor
    }

    TripKeeper tripKeeper;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fullTripID Parameter 1.
     * @return A new instance of fragment DeleteTripPartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeleteTripPartFragment newInstance(String fullTripID) {
        DeleteTripPartFragment fragment = new DeleteTripPartFragment();
        Bundle args = new Bundle();
        args.putSerializable(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTripID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           fullTripID = (String) getArguments().getSerializable(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED);
        }

        tripKeeper = TripKeeper.getInstance(getContext());

        fullTrip = tripKeeper.getCurrentFullTrip(fullTripID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_delete_trip_part, container, false);

        RecyclerView deleteLegsRecyclerView = view.findViewById(R.id.tripsToDeleteRecyclerView);

        GridLayoutManager recyclerLayoutManager = new GridLayoutManager(getContext(),1);

        deleteLegsRecyclerView.setLayoutManager(recyclerLayoutManager);

        //lists legs that can be selected to be deleted
        deleteTripPartsAdapter = new MergeTripPartsAdapter(fullTrip, getContext());

        deleteLegsRecyclerView.setAdapter(deleteTripPartsAdapter);

        ((DeleteSplitMergeLegsActivity) getActivity()).setTitleText(getString(R.string.Delete));

        Button deleteTripPartButton = view.findViewById(R.id.deleteTripPartButton);
        deleteTripPartButton.setOnClickListener(this);

        return view;
    }

    public void checkIfConsecutiveTripPartsAndGoToConfirmationFragment() {

        toBeDeleted = deleteTripPartsAdapter.getToBeMerged();

        SortFullTripPartValidationWrapperByRealIndex sort = new SortFullTripPartValidationWrapperByRealIndex();
        Collections.sort(toBeDeleted, sort);

        if(toBeDeleted.size() < 1) {
            Toast.makeText(getContext(), getString(R.string.You_Must_Choose_One_Leg_To_Delete), Toast.LENGTH_LONG).show();
        }else if((toBeDeleted.size() == fullTrip.getTripList().size())){
            Toast.makeText(getContext(), getString(R.string.You_Cannot_Delete_All_Trips_Legs), Toast.LENGTH_LONG).show();
        }
        else if(!areTripPartsConsecutive(toBeDeleted)){
            Toast.makeText(getContext(), getString(R.string.You_Must_Choose_Consecutive_Legs_To_Delete), Toast.LENGTH_LONG).show();
        }else{
            //if delete selection is valid, go to confirm delete legs fragment
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment fragment = DeleteTripPartsConfirmOnMapFragment.newInstance(fullTripID, toBeDeleted);
            ft.replace(R.id.mergeSplitDeleteMainFragment, fragment).addToBackStack(null);
            ft.commit();

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.deleteTripPartButton:

                checkIfConsecutiveTripPartsAndGoToConfirmationFragment();

                break;
        }

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

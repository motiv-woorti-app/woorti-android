package inesc_id.pt.motivandroid.myTrips.fragments;

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

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.deprecated.MyTripsAddActivitiesFragment;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.myTrips.activities.MyTripsActivity;
import inesc_id.pt.motivandroid.myTrips.adapters.OverviewAfterRelaxingProductivityAdapter;
import inesc_id.pt.motivandroid.persistence.TripKeeper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OverviewAfterRelaxingProductivityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OverviewAfterRelaxingProductivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@Deprecated
public class OverviewAfterRelaxingProductivityFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match

    RecyclerView relaxingProductiveRecyclerView;

    Button nextButton;

    OverviewAfterRelaxingProductivityAdapter adapter;

//    PersistentTripStorage persistentTripStorage;

    private OnFragmentInteractionListener mListener;

    FullTrip fullTripBeingValidated;
    String fullTripID;

//    PersistentTripStorage persistentTripStorage;

    TripKeeper tripKeeper;


    public OverviewAfterRelaxingProductivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment OverviewAfterRelaxingProductivityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverviewAfterRelaxingProductivityFragment newInstance(String fullTrip) {
        OverviewAfterRelaxingProductivityFragment fragment = new OverviewAfterRelaxingProductivityFragment();
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
        }

        tripKeeper = TripKeeper.getInstance(getContext());

//        persistentTripStorage = new PersistentTripStorage(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_leg_productivity_relaxing, container, false);

        relaxingProductiveRecyclerView = view.findViewById(R.id.productivityRelaxingRecyclerView);

        nextButton = view.findViewById(R.id.confirmAllRelaxingProductiveButton);
        nextButton.setOnClickListener(this);
        nextButton.setText(getString(R.string.Next));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//        fullTripBeingValidated = persistentTripStorage.getFullTripByDate(fullTripID);
        fullTripBeingValidated = tripKeeper.getCurrentFullTrip(fullTripID);

        if(getActivity() instanceof MyTripsActivity){
            ((MyTripsActivity) getActivity()).computeAndUpdateTripScore();
        }


        adapter = new OverviewAfterRelaxingProductivityAdapter(fullTripBeingValidated, getContext());

        GridLayoutManager recyclerLayoutManager = new GridLayoutManager(getContext(),1);

        relaxingProductiveRecyclerView.setLayoutManager(recyclerLayoutManager);

        relaxingProductiveRecyclerView.setAdapter(adapter);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.confirmAllRelaxingProductiveButton:

//                fullTrip.updateRelaxingProductivity(adapter.tripPartList);
//                persistentTripStorage.updateFullTripDataObject(fullTrip,fullTrip.getDateId());
                MyTripsAddActivitiesFragment mfragment = MyTripsAddActivitiesFragment.newInstance(fullTripBeingValidated.getDateId());
                //using Bundle to send data
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.my_trips_main_fragment, mfragment).addToBackStack(null);
                transaction.commit();

                break;

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

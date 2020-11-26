package inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPartValidationWrapper;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.myTrips.AdapterCallbackSplit;
import inesc_id.pt.motivandroid.myTrips.activities.DeleteSplitMergeLegsActivity;
import inesc_id.pt.motivandroid.myTrips.adapters.splitMergeDelete.SplitLegsAdapter;
import inesc_id.pt.motivandroid.persistence.TripKeeper;

/**
 *  SplitLegsListFragment
 *
 * Allows the user to choose one trip legs to split into two leg
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
public class SplitLegsListFragment extends Fragment implements AdapterCallbackSplit {

    FullTrip fullTrip;
    String fullTripID;

    SplitLegsAdapter splitLegsAdapter;

    RecyclerView splitLegsRecyclerView;

    private OnFragmentInteractionListener mListener;

    TripKeeper tripKeeper;


//    PersistentTripStorage persistentTripStorage;

    public SplitLegsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fullTrip Parameter 1.
     * @return A new instance of fragment SplitLegsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SplitLegsListFragment newInstance(String fullTrip) {
        SplitLegsListFragment fragment = new SplitLegsListFragment();
        Bundle args = new Bundle();
        args.putString(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTrip);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fullTripID =  getArguments().getString(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED);
        }

        tripKeeper = TripKeeper.getInstance(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        fullTrip = tripKeeper.getCurrentFullTrip(fullTripID);

        GridLayoutManager recyclerLayoutManager = new GridLayoutManager(getContext(),1);

        splitLegsRecyclerView.setLayoutManager(recyclerLayoutManager);

        splitLegsAdapter = new SplitLegsAdapter(fullTrip, getContext(), this);

        //list legs for the user to select the one to be split
        splitLegsRecyclerView.setAdapter(splitLegsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_split_legs_list, container, false);

        splitLegsRecyclerView = view.findViewById(R.id.legsToSplitRecyclerView);

        ((DeleteSplitMergeLegsActivity) getActivity()).setTitleText(getResources().getString(R.string.Split));

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
    public void splitLegs(FullTripPartValidationWrapper legToBeSplit) {

        //although this is checked before, just to be sure

        if(legToBeSplit.getFullTripPart().isTrip()){

            boolean isLastLeg = false;

            if (legToBeSplit.getRealIndex() == fullTrip.getTripList().size()-1){
                isLastLeg = true;
            }

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment fragment = SplitLegsOnMapFragment.newInstance(legToBeSplit.getRealIndex(), isLastLeg, fullTrip.getDateId());
            ft.replace(R.id.mergeSplitDeleteMainFragment, fragment).addToBackStack(null);
            ft.commit();

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

        String LEG_TO_BE_SPLIT = "legToBeSplit";
        String IS_LAST_LEG = "isLastLeg";
        String TRIP_TO_BE_CHANGED = "fullTripToBeChanged";

    }

}

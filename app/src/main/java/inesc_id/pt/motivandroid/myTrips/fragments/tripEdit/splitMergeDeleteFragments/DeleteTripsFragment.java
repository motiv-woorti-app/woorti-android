package inesc_id.pt.motivandroid.myTrips.fragments.tripEdit.splitMergeDeleteFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigest;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigestWrapper;
import inesc_id.pt.motivandroid.myTrips.activities.DeleteSplitMergeTripsActivity;
import inesc_id.pt.motivandroid.myTrips.adapters.splitMergeDeleteTrips.MergeDeleteTripsAdapter;
import inesc_id.pt.motivandroid.myTrips.sort.SortFullTripDigestsByRecentDate;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.tripStateMachine.FullTripMergeSplitDelete;

/**
 * DeleteTripsFragment
 *
 * Allows the user to choose one or more trips to delete
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
public class DeleteTripsFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    MergeDeleteTripsAdapter adapter;
    Button deleteTripsButton;


    public DeleteTripsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeleteTripsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeleteTripsFragment newInstance(String param1, String param2) {
        DeleteTripsFragment fragment = new DeleteTripsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delete_trips, container, false);

        ((DeleteSplitMergeTripsActivity) getActivity()).setTitleText(getString(R.string.Delete));

        PersistentTripStorage persistentTripStorage = new PersistentTripStorage(getContext());
        ArrayList<FullTripDigest> digests = persistentTripStorage.getAllFullTripDigestsByUserIDObjects(FirebaseAuth.getInstance().getUid());

        ListView listView = view.findViewById(R.id.tripsToMergeListView);

        deleteTripsButton = view.findViewById(R.id.deleteTripsButton);
        deleteTripsButton.setOnClickListener(this);

        if(digests.size() != 0){
            //list trips from most recent to less recent
            SortFullTripDigestsByRecentDate sortFullTripByRecentDate = new SortFullTripDigestsByRecentDate();
            Collections.sort(digests, sortFullTripByRecentDate);
            ArrayList<FullTripDigestWrapper> digestWrappers = FullTripDigestWrapper.getDigestWrapperList(digests);
            adapter = new MergeDeleteTripsAdapter(digestWrappers, getContext());
            listView.setAdapter(adapter);
        }else{
            Toast.makeText(getContext(), getString(R.string.You_Have_No_Trips), Toast.LENGTH_LONG).show();
            adapter = new MergeDeleteTripsAdapter(new ArrayList<FullTripDigestWrapper>(), getContext());

            deleteTripsButton.setAlpha(0.35f);
            deleteTripsButton.setClickable(false                                                                                                   );

        }





        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.deleteTripsButton:


                ArrayList<FullTripDigestWrapper> toBeDeleted = adapter.getToBeMergedOrDeleted();

                if(toBeDeleted.size() == 0){

                    Toast.makeText(getContext(), getString(R.string.You_Must_Choose_One_Trip_To_Delete), Toast.LENGTH_SHORT).show();

                }else{

                    FullTripMergeSplitDelete fullTripMergeSplitDelete = new FullTripMergeSplitDelete(getContext());
                    fullTripMergeSplitDelete.deleteFullTrips(toBeDeleted);

                    Toast.makeText(getContext(), getString(R.string.Trips_Deleted_Successfuly), Toast.LENGTH_SHORT).show();
                    getActivity().finish();

                }

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

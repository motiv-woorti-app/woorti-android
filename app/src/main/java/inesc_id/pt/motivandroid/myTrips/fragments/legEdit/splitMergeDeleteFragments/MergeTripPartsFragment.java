package inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetectedWrapper;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPartValidationWrapper;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.myTrips.activities.DeleteSplitMergeLegsActivity;
import inesc_id.pt.motivandroid.myTrips.adapters.splitMergeDelete.MergeTripPartsAdapter;
import inesc_id.pt.motivandroid.myTrips.adapters.splitMergeDelete.ModalityCorrectionGridListAdapter;
import inesc_id.pt.motivandroid.myTrips.sort.SortFullTripPartValidationWrapperByRealIndex;
import inesc_id.pt.motivandroid.persistence.TripKeeper;
import inesc_id.pt.motivandroid.tripStateMachine.TripAnalysis;

import static inesc_id.pt.motivandroid.utils.NumbersUtil.areTripPartsConsecutive;

/**
 * MergeTripPartsFragment
 *
 * Allows the user to choose one or more trip legs/transfers to merge into one leg
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
public class MergeTripPartsFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    String fullTripID;
    FullTrip fullTrip;

    MergeTripPartsAdapter mergeTripPartsAdapter;

    Button mergeTripPartsButton;

    ArrayList<FullTripPartValidationWrapper> toBeMerged;
    boolean waitingForModality = false;

    RecyclerView mergeLegsRecyclerView;

    TripKeeper tripKeeper;

    private ModalityCorrectionGridListAdapter correctModalityAdapter;
    ActivityDetectedWrapper selectedModality;


    public MergeTripPartsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fullTrip Parameter 1.
     * @return A new instance of fragment MergeTripPartsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MergeTripPartsFragment newInstance(String fullTrip) {
        MergeTripPartsFragment fragment = new MergeTripPartsFragment();
        Bundle args = new Bundle();

        Log.e("MergeTripPartsFragment", fullTrip);

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
        fullTrip = tripKeeper.getCurrentFullTrip(fullTripID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_merge_trip_parts, container, false);

        mergeLegsRecyclerView = view.findViewById(R.id.tripsToDeleteRecyclerView);

        GridLayoutManager recyclerLayoutManager = new GridLayoutManager(getContext(),1);

        mergeLegsRecyclerView.setLayoutManager(recyclerLayoutManager);

        mergeTripPartsAdapter = new MergeTripPartsAdapter(fullTrip, getContext());

        mergeTripPartsButton = view.findViewById(R.id.mergeTripPartButton);

        mergeTripPartsButton.setOnClickListener(this);

        //show selectable list of legs/transfers to merge
        mergeLegsRecyclerView.setAdapter(mergeTripPartsAdapter);

        ((DeleteSplitMergeLegsActivity) getActivity()).setTitleText(getResources().getString(R.string.Merge));

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
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.mergeTripPartButton:

                checkIfConsecutiveTripPartsAndGoToConfirmationFragment();

                break;
        }

    }

    public void checkIfConsecutiveTripPartsAndGoToConfirmationFragment() {

        toBeMerged = mergeTripPartsAdapter.getToBeMerged();

        //sort them incrementally
        SortFullTripPartValidationWrapperByRealIndex sort = new SortFullTripPartValidationWrapperByRealIndex();
        Collections.sort(toBeMerged, sort);

        if(toBeMerged.size() < 2){
            Toast.makeText(getContext(), getString(R.string.You_Must_Choose_Two_Legs_To_Merge), Toast.LENGTH_LONG).show();
        }else if(!areTripPartsConsecutive(toBeMerged)){
            Toast.makeText(getContext(), getString(R.string.You_Must_Choose_Consecutive_Legs_To_Merge), Toast.LENGTH_LONG).show();
        }else{//if selectable legs are "mergeable" go to confirm merge on map fragment

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment fragment = MergeTripPartsConfirmOnMapFragment.newInstance(fullTripID, toBeMerged);
            ft.replace(R.id.mergeSplitDeleteMainFragment, fragment).addToBackStack(null);
            ft.commit();

            waitingForModality = true;

        }

    }

    //new way of correcting transport modality
    public void openChangeModalityDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.bottom_dialog_correct_transport_new_mytrips, null);

        final GridView modalitiesGridView =  mView.findViewById(R.id.objectiveOfTheTripGridView);
        modalitiesGridView.setNumColumns(3);
        modalitiesGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        modalitiesGridView.setGravity(Gravity.CENTER);

        modalitiesGridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);

        selectedModality = null;
        correctModalityAdapter = new ModalityCorrectionGridListAdapter(ActivityDetectedWrapper.getFullList() ,getContext());

        modalitiesGridView.setSelector(R.color.selectedModalityGridBackgroundColor);

        modalitiesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                selectedModality = correctModalityAdapter.getItem(position);

            }
        });

        modalitiesGridView.setAdapter(correctModalityAdapter);


        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        Button buttonSaveModalityChange = mView.findViewById(R.id.saveModalitiesButton);

        buttonSaveModalityChange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(selectedModality != null){

                    Log.d("changed modality", "changed to "+ selectedModality.getText());

                    TripAnalysis tripAnalysis = new TripAnalysis(false);

                    tripAnalysis.merge(fullTrip, toBeMerged, selectedModality.getCode());

                    tripKeeper.setCurrentFullTrip(fullTrip);

                    mergeTripPartsAdapter.notifyDataSetChanged();

                    mergeTripPartsAdapter = new MergeTripPartsAdapter(fullTrip, getContext());
                    mergeLegsRecyclerView.setAdapter(mergeTripPartsAdapter);

                    dialog.dismiss();

                }else{

                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

    }


    //deprecated
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure fragment codes match up
        if (requestCode == ModalBottomSheetCorrectMode.keys.CORRECT_MODALITY_DIALOG_REQUEST_CODE) {
            int modality = data.getIntExtra(
                    ModalBottomSheetCorrectMode.keys.CHANGE_MODALITY_RESULT, 0);

            Log.d("changed modality", "changed to "+ modality);

            TripAnalysis tripAnalysis = new TripAnalysis(false);

            FullTrip fullTripMerged = tripAnalysis.merge(fullTrip, toBeMerged, modality);

            tripKeeper.setCurrentFullTrip(fullTrip);

            mergeTripPartsAdapter = new MergeTripPartsAdapter(fullTripMerged, getContext());
            mergeLegsRecyclerView.setAdapter(mergeTripPartsAdapter);

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

}

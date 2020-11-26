package inesc_id.pt.motivandroid.myTrips.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.data.tripData.WaitingEvent;
import inesc_id.pt.motivandroid.data.validationAndRating.ValueFromTrip;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityHelper;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityLeg;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.GenericActivityLeg;
import inesc_id.pt.motivandroid.myTrips.activities.MyTripsActivity;
import inesc_id.pt.motivandroid.myTrips.adapters.TripActivitiesGridListAdapter;
import inesc_id.pt.motivandroid.persistence.TripKeeper;

/**
 * MyTripsValueObtained
 *
 * In this fragment, the user is asked to answer the question "What value did you take from your
 * time on this part of the trip?". The user answers in terms of "Paid Work", "Personal Tasks", "En-
 * joyment", "Fitness" (one seekbar for each one of them).
 *
 * User answer is saved using ValueFromTrip class. code corresponds to:
 *   PAID_WORK = 0;
 *   PERSONAL_TASKS = 1;
 *   ENJOYMENT = 2;
 *   FITNESS = 3;
 *
 * value:
 *   0 - "None"
 *   1 - "Some"
 *   2 - "High"
 *
 * Leg number is passed through ARG_PARAM2 parameter
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

public class MyTripsValueObtained extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    TripActivitiesGridListAdapter adapter;

    // TODO: Rename and change types of parameters

    FullTrip fullTripBeingValidated;
    String fullTripID;

    int leg;

    TripKeeper tripKeeper;

    private ArrayList<ValueFromTrip> values;

    private OnFragmentInteractionListener mListener;

    public MyTripsValueObtained() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment MyTripsValueObtained.
     */
    // TODO: Rename and change types and number of parameters
    public static MyTripsValueObtained newInstance(String fullTripID, int leg) {
        MyTripsValueObtained fragment = new MyTripsValueObtained();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, fullTripID);
        args.putInt(ARG_PARAM2, leg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            fullTripID = getArguments().getString(ARG_PARAM1);
            leg = getArguments().getInt(ARG_PARAM2);

            tripKeeper = TripKeeper.getInstance(getContext());
            fullTripBeingValidated = tripKeeper.getCurrentFullTrip(fullTripID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mytrips_value_obtained, container, false);


        if(getActivity() instanceof MyTripsActivity){
            ((MyTripsActivity) getActivity()).computeAndUpdateTripScore();
        }

        final SeekBar sbPaidWork = view.findViewById(R.id.sbPaidWork);
        final SeekBar sbPersonalTaks = view.findViewById(R.id.sbPersonalTasks);
        final SeekBar sbEnjoyment = view.findViewById(R.id.sbEnjoyment);
        final SeekBar sbFitness = view.findViewById(R.id.sbFitness);

        Button btNext = view.findViewById(R.id.btNext);

        TextView skipTextView = view.findViewById(R.id.tvSkip);
        SpannableString content = new SpannableString(getString(R.string.Skip));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        skipTextView.setText(content);

        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    MyTripsFactorsWastedTime mfragment = MyTripsFactorsWastedTime.newInstance(fullTripID, leg);
                    //using Bundle to send data
                    FragmentTransaction transaction=getFragmentManager().beginTransaction();
                    transaction.replace(R.id.my_trips_main_fragment, mfragment).addToBackStack(null);
                    transaction.commit();


            }
        });

        //rebuild state
        if(fullTripBeingValidated.getTripList().get(leg).getValueFromTript() != null){

            values = fullTripBeingValidated.getTripList().get(leg).getValueFromTript();

            sbPaidWork.setProgress(values.get(ValueFromTrip.keys.PAID_WORK).getValue());
            sbPersonalTaks.setProgress(values.get(ValueFromTrip.keys.PERSONAL_TASKS).getValue());
            sbEnjoyment.setProgress(values.get(ValueFromTrip.keys.ENJOYMENT).getValue());
            sbFitness.setProgress(values.get(ValueFromTrip.keys.FITNESS).getValue());


        }else{

            values = ValueFromTrip.getPopulatedValueArray();

            sbPaidWork.setProgress(0);
            sbPersonalTaks.setProgress(0);
            sbEnjoyment.setProgress(0);
            sbFitness.setProgress(0);

        }



        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if((sbPaidWork.getProgress() == 0) && (sbPersonalTaks.getProgress() == 0) && (sbEnjoyment.getProgress() == 0) && (sbFitness.getProgress() == 0) ){

                    fullTripBeingValidated.getTripList().get(leg).setValueFromTript(values);

                    MyTripsFactorsWastedTime mfragment = MyTripsFactorsWastedTime.newInstance(fullTripID, leg);
                    //using Bundle to send data
                    FragmentTransaction transaction=getFragmentManager().beginTransaction();
                    transaction.replace(R.id.my_trips_main_fragment, mfragment).addToBackStack(null);
                    transaction.commit();

                }else { // if user selected any option other than none for all categories, show the
                        //the activity selection screen

                    showActivitySelectionScreen();
                }


            }
        });

        sbPaidWork.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                values.get(ValueFromTrip.keys.PAID_WORK).setValue(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbPersonalTaks.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                values.get(ValueFromTrip.keys.PERSONAL_TASKS).setValue(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbEnjoyment.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                values.get(ValueFromTrip.keys.ENJOYMENT).setValue(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbFitness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                values.get(ValueFromTrip.keys.FITNESS).setValue(i);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // compute possible score for this specific form/screen/question
        int possibleScore = 0;
        if(fullTripBeingValidated.getTripList().get(leg).isTrip()){
            possibleScore = ((Trip) fullTripBeingValidated.getTripList().get(leg)).getLegScored(getContext()).getPossibleActivitiesPoints();
        }else{
            possibleScore = ((WaitingEvent) fullTripBeingValidated.getTripList().get(leg)).getWaitingEventScored(getContext()).getPossibleActivitiesPoints();
        }

        //show possible score
        if(getActivity() instanceof  MyTripsActivity){
            ((MyTripsActivity) getActivity()).updatePossibleScore(possibleScore);
        }

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
     * shows activity selection popup. Asks the user "What exactly did you value doing?"
     */
    private void showActivitySelectionScreen() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View mView = inflater.inflate(R.layout.mytrips_activity_while_travelling, null);


        FullTripPart fullTripPart = fullTripBeingValidated.getTripList().get(leg);

        int modality;

        if(fullTripPart.isTrip()){
            modality = ((Trip) fullTripBeingValidated.getTripList().get(leg)).getCorrectedModeOfTransport();
        }else{
            modality = -1;
        }

        //get activity list for the specific mode of transport (of the leg)
        ArrayList<ActivityLeg> activitiesFullList = ActivityHelper.getActivityFullList(modality, getContext());

        ArrayList<ActivityLeg> selectedActivities;

        //rebuild state if exists already
        if(fullTripBeingValidated.getTripList().get(leg).getGenericActivities() != null){

            selectedActivities = fullTripBeingValidated.getTripList().get(leg).getGenericActivities();

        }else{

            selectedActivities = new ArrayList<>();

        }

        //activity selection gridview
        final GridView gridView = mView.findViewById(R.id.gridActivities);

        final Button nextButton = mView.findViewById(R.id.btDone);

        final TextView skipTextView = mView.findViewById(R.id.skipTextView);
        SpannableString content = new SpannableString(getString(R.string.Skip));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        skipTextView.setText(content);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        //user skips activity selection->go to next fragment
        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    fullTripBeingValidated.getTripList().get(leg).setValueFromTript(values);
                    tripKeeper.setCurrentFullTrip(fullTripBeingValidated);

                    MyTripsFactorsWastedTime mfragment = MyTripsFactorsWastedTime.newInstance(fullTripID, leg);
                    //using Bundle to send data
                    FragmentTransaction transaction=getFragmentManager().beginTransaction();
                    transaction.replace(R.id.my_trips_main_fragment, mfragment).addToBackStack(null);
                    transaction.commit();

                    dialog.dismiss();

            }
        });

        //user skips activity selection->go to next fragment
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(adapter.selectedActivities.size()< 1){
                    Toast.makeText(getContext(), "Please select one option or skip", Toast.LENGTH_SHORT).show();
                }else{

                    //save selected options
                    fullTripBeingValidated.getTripList().get(leg).setValueFromTript(values);
                    fullTripBeingValidated.getTripList().get(leg).setGenericActivities(adapter.selectedActivities);

                    tripKeeper.setCurrentFullTrip(fullTripBeingValidated);

                    //update score for the trip
                    if(fullTripBeingValidated.getTripList().get(leg).isTrip()){
                        ((Trip) fullTripBeingValidated.getTripList().get(leg)).getLegScored(getContext()).answerActivities();
                    }else{
                        ((WaitingEvent) fullTripBeingValidated.getTripList().get(leg)).getWaitingEventScored(getContext()).answerActivities();
                    }

                    MyTripsFactorsWastedTime mfragment = MyTripsFactorsWastedTime.newInstance(fullTripID, leg);
                    //using Bundle to send data
                    FragmentTransaction transaction=getFragmentManager().beginTransaction();
                    transaction.replace(R.id.my_trips_main_fragment, mfragment).addToBackStack(null);
                    transaction.commit();

                    dialog.dismiss();


                }

            }
        });

        gridView.setNumColumns(3);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setGravity(Gravity.CENTER);
        gridView.setVerticalSpacing(10);
        gridView.setHorizontalSpacing(5);

//        gridView.setPadding(1,5, 1, 5);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GenericActivityLeg selectedItem = (GenericActivityLeg) adapterView.getItemAtPosition(i);


                int selectedPosition = adapter.selectedActivities.indexOf(selectedItem);

                if(selectedPosition > -1){

                    adapter.selectedActivities.remove(selectedPosition);
                    view.setBackgroundColor(Color.TRANSPARENT);
//                    selectedActivities.remove(selectedItem);
                }
                else{

                    if(i == adapter.dataSet.size()-1){

                        showOtherOptionDialog(adapter ,i, view);

                    }else {

                        adapter.selectedActivities.add(selectedItem);
                        view.setBackground(getContext().getResources().getDrawable(R.drawable.grid_selected_item_shape));
//                        selectedActivities.add(selectedItem);
                    }
                }
            }
        });






        adapter = new TripActivitiesGridListAdapter(activitiesFullList, getContext(), selectedActivities, 0);

        gridView.setAdapter(adapter);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }


    /**
     * popup to allow the user to add a custom activity not mentioned in the presented options
     *
     * @param adapter
     * @param position
     * @param view
     */
    private void showOtherOptionDialog(final TripActivitiesGridListAdapter adapter, final int position, final View view) {

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

                    adapter.selectedActivities.add(adapter.dataSet.get(position));
                    adapter.selectedActivities.get(adapter.selectedActivities.size()-1).setActivityText(option);
                    view.setBackground(getContext().getResources().getDrawable(R.drawable.grid_selected_item_shape));
                    dialog.dismiss();

                }else{
                    Toast.makeText(getContext(), "Must be at least 3 letters long", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }


}

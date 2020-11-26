package inesc_id.pt.motivandroid.myTrips.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.data.tripData.WaitingEvent;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.data.validationAndRating.TripPartFactor;
import inesc_id.pt.motivandroid.myTrips.activities.MyTripsActivity;
import inesc_id.pt.motivandroid.persistence.TripKeeper;

/**
 * MyTripsFactorsWastedTime
 *
 * In this fragment, the user is asked to "Select the most important factors for the quality of your
 * travel time". Groups (categories of factors) are shown according to the mode of transport of the
 * selected leg. For each factor, the user may assign a negative rating ("-"), a positive rating
 * ("+), or neutral ("+" and "-")
 *
 *
 * Leg number is passed through ARG_PARAM2 parameter
 * Trip id is passed through ARG_PARAM1 parameter
 *
 * @param fullTripID id of current trip being evaluated
 * @param fullTripBeingPassed current trip being evaluated
 * @param tripKeeper
 * @param leg current leg being evaluated
 * @param modality validated mode of transport associated with current leg
 * @param etOtherComments EditText for other comments
 * @param btNext Button to go to next screen
 * @param numAsweredFactors number of answered factors
 * @param orderedFactorGroups List of factor categories ordered according to requirements
 * @param factorMap Map with list of factors per category
 * @param savedFactorsMap Map with list of saved factors per category
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
public class MyTripsFactorsWastedTime extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final int CLICKABLE =0;
    private static final int UNCLICKABLE = 1;
    private static final int ADDED = 0;
    private static final int REMOVED = 1;

    private String fullTripID;

    FullTrip fullTripBeingPassed;
    TripKeeper tripKeeper;

    int leg;
    int modality;

    EditText etOtherComments;
    Button btNext;

    int numAsweredFactors = 0;


    int selectedLang = 0;

    private OnFragmentInteractionListener mListener;

    private ArrayList<String> orderedFactorGroups = new ArrayList<>();

    private HashMap<String, ArrayList<TripPartFactor>> factorMap = new HashMap<>();
    private HashMap<String, ArrayList<TripPartFactor>> savedFactorsMap = new HashMap<>();

    int selectedFactorsCount = 0;

    public MyTripsFactorsWastedTime() {
        // Required empty public constructor
    }

    /*
     * Instantiate new MyTripsFactorsWastedTime fragment
     * @param fullTripID id of trip being evaluated
     * @param leg current leg being evaluated
     */
    public static MyTripsFactorsWastedTime newInstance(String fullTripID, int leg) {
        MyTripsFactorsWastedTime fragment = new MyTripsFactorsWastedTime();
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
        }

        tripKeeper = TripKeeper.getInstance(getContext());
        fullTripBeingPassed = tripKeeper.getCurrentFullTrip(fullTripID);

        if(getActivity() instanceof MyTripsActivity){
            ((MyTripsActivity) getActivity()).computeAndUpdateTripScore();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.mytrips_factors_wasted_time, container, false);

        btNext = view.findViewById(R.id.btNext);

        final TextView tvSkip = view.findViewById(R.id.tvSkip);
        SpannableString content = new SpannableString(getString(R.string.Skip));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvSkip.setText(content);

        final LinearLayout layoutGroupsBox = view.findViewById(R.id.layoutGroupsBox);
        etOtherComments = view.findViewById(R.id.etOtherComments);
        etOtherComments.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if(cs.length()>0)
                    setNextButtonStyle(CLICKABLE);
                if(cs.length()==0 && !hasAtLeastOneFactor())
                    setNextButtonStyle(UNCLICKABLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        FullTripPart fullTripPart = fullTripBeingPassed.getTripList().get(leg);

        //Get factors previously saved
        savedFactorsMap.put(TripPartFactor.getFactorGroup(getContext(), keys.ACTIVITIES), fullTripPart.getActivitiesFactors());
        savedFactorsMap.put(TripPartFactor.getFactorGroup(getContext(), keys.GETTING_THERE), fullTripPart.getGettingThereFactors());
        savedFactorsMap.put(TripPartFactor.getFactorGroup(getContext(), keys.COMFORT_AND_PLEASANTNESS), fullTripPart.getComfortAndPleasantFactors());
        savedFactorsMap.put(TripPartFactor.getFactorGroup(getContext(), keys.WHILE_YOU_RIDE), fullTripPart.getWhileYouRideFactors());

        selectedFactorsCount = 0;
        populateFactors();

        //Make next button unavailable without factors and comments
        if(selectedFactorsCount == 0 && etOtherComments.getText().length()==0){
            btNext.setClickable(false);
            btNext.setAlpha(0.35f);
        }

        //Restore Saved Other Comments
        if(fullTripPart.getOtherFactors() != null) {
            etOtherComments.setText(fullTripPart.getOtherFactors());
        }


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        etOtherComments.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId,
                                          KeyEvent keyEvent) { //triggered when done editing (as clicked done on keyboard)

                if ((actionId == EditorInfo.IME_ACTION_DONE)) {


                    etOtherComments.clearFocus();


                    tvSkip.setFocusableInTouchMode(true);
                    tvSkip.requestFocus();

                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                    return true;

                }
                return false;
            }
        });


        inflateViews(layoutGroupsBox);

        btNext.setOnClickListener(finishFragmentListener);
        tvSkip.setOnClickListener(finishFragmentListener);

        // SET POSSIBLE POINTS FOR THIS FORM
        int possibleScore = 0;
        if (fullTripPart.isTrip())
            possibleScore = ((Trip) fullTripPart).getLegScored(getContext()).getPossibleWorthPoints();
        else
            possibleScore = ((WaitingEvent) fullTripPart).getWaitingEventScored(getContext()).getPossibleWorthPoints();

        if(getActivity() instanceof  MyTripsActivity){
            ((MyTripsActivity) getActivity()).updatePossibleScore(possibleScore);
        }

        return view;
    }

    /*
     * Inflate All groups according to orderedFactorGroups and inflate its corresponding factor views
     * @param layoutGroupsBox container for the factors
     */
    private void inflateViews(LinearLayout layoutGroupsBox){
        if(layoutGroupsBox.getChildCount() > 1) return;

        //Inflate two dimensions: Group view and specific factor list
        for(String group : orderedFactorGroups){
            ArrayList<TripPartFactor> groupedFactors = factorMap.get(group);
            if(groupedFactors == null || groupedFactors.isEmpty()) continue;

            LayoutInflater inflater = LayoutInflater.from(getContext());
            LinearLayout groupView = (LinearLayout) inflater.inflate(R.layout.mytrips_factors_wasted_time_group, null, false);

            groupView.setTag(group);

            ConstraintLayout clickableGroupView = groupView.findViewById(R.id.expandableGroup);

            TextView tvGroup = groupView.findViewById(R.id.tvGroup);

            if(modality == -1 && group.equals(TripPartFactor.getFactorGroup(getContext(), keys.WHILE_YOU_RIDE)))
                group = TripPartFactor.getFactorGroup(getContext(), keys.While_You_Are_There);
            tvGroup.setText(group);

            LinearLayout childFactorsLayout = groupView.findViewById(R.id.childFactorsLayout);

            //Inflate factors for group
            for(TripPartFactor factor : groupedFactors){
                final ConstraintLayout factorView = (ConstraintLayout) inflater.inflate(R.layout.mytrips_factors_wasted_time_item, null, false);

                TextView tvFactor = factorView.findViewById(R.id.tvFactor);
                ConstraintLayout clAdd = factorView.findViewById(R.id.clAdd);
                ConstraintLayout clRemove = factorView.findViewById(R.id.clRemove);
                ImageButton btAdd = factorView.findViewById(R.id.buttonAdd);
                ImageButton btRemove = factorView.findViewById(R.id.buttonRemove);

                String factorName = factor.name;

                tvFactor.setText(factorName);
                btAdd.setOnClickListener(buttonListener);
                btRemove.setOnClickListener(buttonListener);

                updateFactorColorScheme(factor, tvFactor, clRemove, clAdd, btRemove, btAdd);

                childFactorsLayout.addView(factorView);
            }
            clickableGroupView.setOnClickListener(groupClickListener);
            layoutGroupsBox.addView(groupView);

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

    /*
     * Updates factorsMap with all groups and corresponding factors
     */
    private void populateFactors(){

        //Populate group title list
        orderedFactorGroups.clear();


        FullTripPart fullTripPart = fullTripBeingPassed.getTripList().get(leg);

        if(fullTripPart.isTrip()){
            modality = ((Trip) fullTripBeingPassed.getTripList().get(leg)).getCorrectedModeOfTransport();
        }else{
            modality = -1;
        }

        String transportGroup = ActivityDetected.getTransportLabel(modality);

        ArrayList<TripPartFactor> factorsTextACT;
        ArrayList<TripPartFactor> factorsTextGT;
        ArrayList<TripPartFactor> factorsTextWYR;
        ArrayList<TripPartFactor> factorsTextCP;

        //Get factors in string format
        switch (transportGroup){
            default:
                //public transfer or answer
            case ActivityDetected.keys.PUBLIC_TRANSPORT:
                factorsTextACT = TripPartFactor.getPublicTransportACTFactors(modality, getContext());
                factorsTextCP = TripPartFactor.getPublicTransportCPFactors(modality, getContext());
                factorsTextGT = TripPartFactor.getPublicTransportGTFactors(modality, getContext());
                factorsTextWYR = TripPartFactor.getPublicTransportWYRFactors(modality, getContext());

                orderedFactorGroups.add(0, TripPartFactor.getFactorGroup(getContext(), keys.GETTING_THERE));
                orderedFactorGroups.add(1,  TripPartFactor.getFactorGroup(getContext(), keys.WHILE_YOU_RIDE));
                orderedFactorGroups.add(2,  TripPartFactor.getFactorGroup(getContext(), keys.COMFORT_AND_PLEASANTNESS));
                orderedFactorGroups.add(3, TripPartFactor.getFactorGroup(getContext(), keys.ACTIVITIES));

                break;
            case ActivityDetected.keys.ACTIVE_TRANSPORT:
                factorsTextACT = TripPartFactor.getActiveTransportACTFactors(modality, getContext());
                factorsTextCP = TripPartFactor.getActiveTransportCPFactors(modality, getContext());                                  //NO CP factors in active
                factorsTextGT = TripPartFactor.getActiveTransportGTFactors(modality, getContext());
                orderedFactorGroups.add(0, TripPartFactor.getFactorGroup(getContext(), keys.GETTING_THERE));
                orderedFactorGroups.add(1,  TripPartFactor.getFactorGroup(getContext(), keys.COMFORT_AND_PLEASANTNESS));
                orderedFactorGroups.add(2, TripPartFactor.getFactorGroup(getContext(), keys.ACTIVITIES));

                factorsTextWYR = new ArrayList<>();
                break;
            case ActivityDetected.keys.PRIVATE_TRANSPORT:
                factorsTextACT = TripPartFactor.getPrivateTransportACTFactors(modality, getContext());
                factorsTextCP = TripPartFactor.getPrivateTransportCPFactors(modality, getContext());                                     //NO CP factors in private
                factorsTextGT = TripPartFactor.getPrivateTransportGTFactors(modality, getContext());
                orderedFactorGroups.add(0, TripPartFactor.getFactorGroup(getContext(), keys.GETTING_THERE));
                orderedFactorGroups.add(1,  TripPartFactor.getFactorGroup(getContext(), keys.COMFORT_AND_PLEASANTNESS));
                orderedFactorGroups.add(2, TripPartFactor.getFactorGroup(getContext(), keys.ACTIVITIES));
                factorsTextWYR = new ArrayList<>();
                break;
        }

        //ACTIVITIES FACTORS - Populate objects from text format
        ArrayList<TripPartFactor> factorsACT = new ArrayList<>();
        for(TripPartFactor tripPartFactor: factorsTextACT) {
            TripPartFactor factor = restoreSavedFactor(tripPartFactor, TripPartFactor.getFactorGroup(getContext(), keys.ACTIVITIES));
            factorsACT.add(factor);
        }
        factorMap.put(TripPartFactor.getFactorGroup(getContext(), keys.ACTIVITIES), factorsACT);

        //COMFORT AND PLEASANTNESS
        ArrayList<TripPartFactor> factorsCP = new ArrayList<>();
        for(TripPartFactor tripPartFactor: factorsTextCP) {
            TripPartFactor factor = restoreSavedFactor(tripPartFactor, TripPartFactor.getFactorGroup(getContext(), keys.COMFORT_AND_PLEASANTNESS));
            factorsCP.add(factor);
        }
        if(!factorsCP.isEmpty())
            factorMap.put(TripPartFactor.getFactorGroup(getContext(), keys.COMFORT_AND_PLEASANTNESS), factorsCP);

        //GETTING THERE
        ArrayList<TripPartFactor> factorsGT = new ArrayList<>();
        for(TripPartFactor tripPartFactor: factorsTextGT) {
            TripPartFactor factor = restoreSavedFactor(tripPartFactor, TripPartFactor.getFactorGroup(getContext(), keys.GETTING_THERE));
            factorsGT.add(factor);
        }
        factorMap.put(TripPartFactor.getFactorGroup(getContext(), keys.GETTING_THERE), factorsGT);

        //WHILE YOU RIDE
        ArrayList<TripPartFactor> factorsWYR = new ArrayList<>();
        for(TripPartFactor tripPartFactor: factorsTextWYR) {
            TripPartFactor factor = restoreSavedFactor(tripPartFactor, TripPartFactor.getFactorGroup(getContext(), keys.WHILE_YOU_RIDE));
            factorsWYR.add(factor);
        }

        if(!factorsWYR.isEmpty())
            factorMap.put(TripPartFactor.getFactorGroup(getContext(), keys.WHILE_YOU_RIDE), factorsWYR);


    }

    /*
     * check if factor is saved and try to restore it
     * @param factor factor to be restored
     * @param group group of the respective factor
     */
    private TripPartFactor restoreSavedFactor(TripPartFactor factor, String group){

        if ((savedFactorsMap.get(group) != null) && savedFactorsMap.get(group).contains(factor)) {
            int index = savedFactorsMap.get(group).indexOf(factor);
            TripPartFactor savedFactor = savedFactorsMap.get(group).get(index);
            factor.plus = savedFactor.plus;
            factor.minus = savedFactor.minus;
            selectedFactorsCount++;
        }
        return factor;
    }

    /*
     * get TripPartFactor object from group and factor name
     * @param group group of the respective factor
     * @param name name of the factor
     */
    private TripPartFactor getFactor(String group, String name){

        for(TripPartFactor f : factorMap.get(group)){
            if(name.equals(f.name))
                return f;
        }
        return null;
    }

    /*
     * OnClickListener called when clicking Next Button or Skip
     * Skip ignores all factor choices
     * Next requires at least one factor completed
     */
    View.OnClickListener finishFragmentListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.tvSkip:
                    goToNextFragment(true);
                    break;
                case R.id.btNext:
                    if(!hasAtLeastOneFactor() && !isCommentFilled()){
                        Toast.makeText(getContext(), "Hey! Select factors first, or skip.", Toast.LENGTH_SHORT).show();
                        break;
                    }


                    goToNextFragment(false);
                    break;
            }
        }
    };

    /*
     * Save chosen factors in current trip
     * @param fullTripPart current trip being evaluated
     */
    public void saveFactorsMap(FullTripPart fullTripPart){
        savedFactorsMap.clear();
        for (String group : factorMap.keySet()) {
            ArrayList<TripPartFactor> groupedFactors = new ArrayList<>();
            for (TripPartFactor tempFactor : factorMap.get(group)) {
                if(tempFactor.minus || tempFactor.plus){
                    groupedFactors.add(tempFactor);
                }
            }
            savedFactorsMap.put(group, groupedFactors);
        }

        //Save value on FullTripPart
        fullTripPart.setActivitiesFactors(savedFactorsMap.get(TripPartFactor.getFactorGroup(getContext(), keys.ACTIVITIES)));
        fullTripPart.setComfortAndPleasantFactors(savedFactorsMap.get(TripPartFactor.getFactorGroup(getContext(), keys.COMFORT_AND_PLEASANTNESS)));
        fullTripPart.setGettingThereFactors(savedFactorsMap.get(TripPartFactor.getFactorGroup(getContext(), keys.GETTING_THERE)));
        fullTripPart.setWhileYouRideFactors(savedFactorsMap.get(TripPartFactor.getFactorGroup(getContext(), keys.WHILE_YOU_RIDE)));
        fullTripPart.setOtherFactors(etOtherComments.getText().toString());
    }

    /*
     * Store information and go to next fragment
     * @param skipFlag boolean indicating if user wants to skip
     */
    public void goToNextFragment(boolean skipFlag){
        FullTripPart fullTripPart = fullTripBeingPassed.getTripList().get(leg);

        saveFactorsMap(fullTripPart);

        printFactors();

        //SKIP IS EQUAL TO NEXT, JUST DOESN'T UPDATE SCORE
        if(!skipFlag) {
            Log.d("FactorsWastedTime", "Updating Score");
            if (fullTripPart.isTrip()) {
                ((Trip) fullTripPart).getLegScored(getContext()).answerWorthwhileness();
            } else {
                ((WaitingEvent) fullTripPart).getWaitingEventScored(getContext()).answerWorthwhileness();
            }
        }

        tripKeeper.setCurrentFullTrip(fullTripBeingPassed);

        ThinkingAboutYourTripFragment mfragment = ThinkingAboutYourTripFragment.newInstance(fullTripID);
        //using Bundle to send data
        FragmentTransaction transaction=getFragmentManager().beginTransaction();
        transaction.replace(R.id.my_trips_main_fragment, mfragment).addToBackStack(null);
        transaction.commit();
    }

    /*
     * Listener for group containers to collapse/show respective child factors
     */
    View.OnClickListener groupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout groupLayout = (LinearLayout) view.getParent();
            LinearLayout childFactorsLayout = groupLayout.findViewById(R.id.childFactorsLayout);
            TextView tvGroupTitle = view.findViewById(R.id.tvGroup);

            //Hide or show factors logic
            if(childFactorsLayout.getVisibility() == View.GONE) {
                childFactorsLayout.setVisibility(View.VISIBLE);
                tvGroupTitle.setTextColor(Color.parseColor("#ED7E03"));
                tvGroupTitle.setTypeface(ResourcesCompat.getFont(getContext(), R.font.montserrat_bold));
            }
            else {
                childFactorsLayout.setVisibility(View.GONE);
                tvGroupTitle.setTextColor(getResources().getColor(R.color.defaultTextColor));
                tvGroupTitle.setTypeface(ResourcesCompat.getFont(getContext(), R.font.montserrat));
            }
        }
    };

    /*
     * Listener for factors butttons (add and remove)
     * Toggles factor state and updates UI
     */
    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ConstraintLayout itemView = (ConstraintLayout) ((ViewGroup) view.getParent()).getParent();

            //mytrips_factors_wasted_time_group inflated view
            LinearLayout groupView = (LinearLayout) ((ViewGroup) itemView.getParent()).getParent();

            //Clickable group view
            ConstraintLayout expandableGroup = groupView.findViewById(R.id.expandableGroup);

            TextView tvFactor = itemView.findViewById(R.id.tvFactor);
            ConstraintLayout clAdd = itemView.findViewById(R.id.clAdd);
            ConstraintLayout clRemove = itemView.findViewById(R.id.clRemove);
            ImageButton btAdd = itemView.findViewById(R.id.buttonAdd);
            ImageButton btRemove = itemView.findViewById(R.id.buttonRemove);

            int viewId = view.getId();
            String groupTitle = (String) groupView.getTag();
            TripPartFactor factor = getFactor(groupTitle, tvFactor.getText().toString());

            checkIfFactorWasAddedOrRemoved(ADDED, groupTitle, factor);

            switch(viewId){
                case R.id.buttonAdd:
                    factor.toggleAdd();
                    break;
                case R.id.buttonRemove:
                    factor.toggleRemove();
                    break;
            }

            checkIfFactorWasAddedOrRemoved(REMOVED, groupTitle, factor);


            updateFactorColorScheme(factor, tvFactor, clRemove, clAdd, btRemove, btAdd);
        }
    };

    /*
     * Take action depending if factor was added or removed
     * If added, add to savedFactors and allow Next Button to be clicked
     * If Removed, remove from savedFactors and when selected factors = 0 disables Next Button
     */
    private void checkIfFactorWasAddedOrRemoved(int mode, String groupTitle, TripPartFactor factor){
        if(!factor.minus && !factor.plus) {
            if(mode == ADDED) {
                if (savedFactorsMap.get(groupTitle) != null) {
                    savedFactorsMap.get(groupTitle).add(factor);
                } else {
                    ArrayList<TripPartFactor> newFactorList = new ArrayList<TripPartFactor>();
                    newFactorList.add(factor);
                    savedFactorsMap.put(groupTitle, newFactorList);
                }

                if (selectedFactorsCount == 0) {
                    setNextButtonStyle(CLICKABLE);
                }

                selectedFactorsCount++;
            }
            else if(mode == REMOVED){
                if(savedFactorsMap.get(groupTitle) != null) {
                    savedFactorsMap.get(groupTitle).remove(factor);

                    selectedFactorsCount--;

                    if(selectedFactorsCount == 0 && !isCommentFilled()){
                        setNextButtonStyle(UNCLICKABLE);
                    }
                }
            }
        }
    }


    /*
     * Update Next Button depending on mode (CLICKABLE or UNCLICKABLE)
     */
    public void setNextButtonStyle(int mode){
        switch (mode){
            case CLICKABLE:
                btNext.setClickable(true);
                btNext.setAlpha(1);
                break;
            case UNCLICKABLE:
                btNext.setClickable(false);
                btNext.setAlpha(0.35f);
                break;
        }
    }

    /*
     * Update color scheme of the factor according to buttons state.
     * Factor can be grey (none selected), green (plus selected), orange (both selected) or red (minus selected)
     * @param factor object representing factor
     * @param tvFactor Textview for factor text
     * @param clRemove container of Minus button
     * @param clAdd container of Plus button
     * @param btRemove image of Minus button
     * @param btAdd image of Plus button
     */
    public void updateFactorColorScheme(TripPartFactor factor, TextView tvFactor, ConstraintLayout clRemove,
                                        ConstraintLayout clAdd, ImageButton btRemove,ImageButton btAdd ){

        if(factor.plus && !factor.minus) {
            tvFactor.setTextColor(getResources().getColor(R.color.greenFactor));
            clAdd.setBackground(getResources().getDrawable(R.drawable.factors_wasted_time_button_green));
            btAdd.setColorFilter(Color.WHITE);
            clRemove.setBackground(getResources().getDrawable(R.drawable.factors_wasted_time_button_default));
            btRemove.setColorFilter(Color.BLACK);

        }
        else if(!factor.plus && !factor.minus){
            tvFactor.setTextColor(getResources().getColor(R.color.defaultTextColor));
            clAdd.setBackground(getResources().getDrawable(R.drawable.factors_wasted_time_button_default));
            btAdd.setColorFilter(Color.BLACK);
            clRemove.setBackground(getResources().getDrawable(R.drawable.factors_wasted_time_button_default));
            btRemove.setColorFilter(Color.BLACK);
        }
        else if(!factor.plus && factor.minus) {
            tvFactor.setTextColor(getResources().getColor(R.color.redFactor));
            clAdd.setBackground(getResources().getDrawable(R.drawable.factors_wasted_time_button_default));
            btAdd.setColorFilter(Color.BLACK);
            clRemove.setBackground(getResources().getDrawable(R.drawable.factors_wasted_time_button_red));
            btRemove.setColorFilter(Color.WHITE);
        }
        else if(factor.plus && factor.minus) {
            tvFactor.setTextColor(getResources().getColor(R.color.orangeFactor));
            clAdd.setBackground(getResources().getDrawable(R.drawable.factors_wasted_time_button_orange));
            clRemove.setBackground(getResources().getDrawable(R.drawable.factors_wasted_time_button_orange));
            btAdd.setColorFilter(Color.WHITE);
            btRemove.setColorFilter(Color.WHITE);
        }
    }

    /*
     * Check if at least one factor is selected
     */
    public boolean hasAtLeastOneFactor(){
        for(String key : factorMap.keySet()){
            for(TripPartFactor factor: factorMap.get(key)){
                if(factor.plus || factor.minus)
                    return true;
            }
        }
        return false;
    }

    /*
     * DEBUG - print current saved factors
     */
    public boolean printFactors(){
        for(String key : savedFactorsMap.keySet()){
            for(TripPartFactor factor: savedFactorsMap.get(key)){
                if(factor.plus || factor.minus)
                    Log.e("Factors", "code " + factor.code + "name " + factor.name);
            }
        }
        return false;
    }

    /*
     * Check if Comment section has text
     */
    public boolean isCommentFilled(){
        if(etOtherComments.getText().length()==0)
            return false;
        return true;
    }

    public interface keys {

        int ACTIVITIES = 0;
        int COMFORT_AND_PLEASANTNESS = 1;
        int GETTING_THERE = 2;
        int WHILE_YOU_RIDE = 3;
        int While_You_Are_There = 4;

    }
}

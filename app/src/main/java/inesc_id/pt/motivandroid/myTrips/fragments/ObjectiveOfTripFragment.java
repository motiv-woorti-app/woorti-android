package inesc_id.pt.motivandroid.myTrips.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.userSettingsData.HomeWorkAddress;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.validationAndRating.TripObjectiveWrapper;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.myTrips.activities.MyTripsActivity;
import inesc_id.pt.motivandroid.myTrips.adapters.TripObjectiveGridListAdapter;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.persistence.TripKeeper;

/**
 * ObjectiveOfTripFragment
 *
 *  This fragment presents the user with three questions about the objective of the trip being
 *  validated. The questions must be answered sequentially, the next question is only unlocked/enabled
 *  when the user answers the previous question.
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
public class ObjectiveOfTripFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private OnFragmentInteractionListener mListener;


    /**
     * user answer to the first question (trip objectives selected by the user)
     */
    ArrayList<TripObjectiveWrapper> tripObjectiveWrappers;


    /**
     * user answer to the second question
     */
    int didYouArriveAtAFixedTimeAnswer = -1;

    /**
     * user answer to the third question
     */
    int howOftenAnswerTimeAnswer = -1;

    FullTrip fullTripBeingPassed;
    String fullTripID;

    TripKeeper tripKeeper;

    ExpandableHeightGridView objectiveGrid;
    TripObjectiveGridListAdapter adapter;

    UserSettingStateWrapper userSettingStateWrapper;

    public ObjectiveOfTripFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment ObjectiveOfTripFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ObjectiveOfTripFragment newInstance(String fullTripID){
        ObjectiveOfTripFragment fragment = new ObjectiveOfTripFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
            args.putString(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTripID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fullTripID  = getArguments().getString(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED);
        }

//        persistentTripStorage = new PersistentTripStorage(getContext());

        tripKeeper = TripKeeper.getInstance(getContext());

        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "");

        tripObjectiveWrappers = new ArrayList<>();

        int i  = 0;
        for(String objective : TripObjectiveWrapper.getAllObjectiveTexts(getContext())){
            tripObjectiveWrappers.add(new TripObjectiveWrapper(0,i,objective));
            i++;
        }



    }


    //layout containing second question
    ConstraintLayout fixedTimeYesOrNoQuestion;

    //possible answers to second question
    Button yesButton;
    Button noButton;
    TextView notSureYesOrNo;

    //layout containing third question
    ConstraintLayout howOftenQuestion;

    //possible answers to third question
    RadioButton regularlyButton;
    RadioButton occasionallyButton;
    RadioButton firstTimeButton;
    TextView notSureHowOften;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_objective_of_trip, container, false);

        Log.d("ObjectiveOfTrip", "oncreateview");

        fullTripBeingPassed = tripKeeper.getCurrentFullTrip(fullTripID);

        //build grid for the first question (objective of trip)
        objectiveGrid = view.findViewById(R.id.objectiveOfTheTripGridView);
        objectiveGrid.setNumColumns(3);
        objectiveGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        objectiveGrid.setGravity(Gravity.CENTER);
        objectiveGrid.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        objectiveGrid.setExpanded(true);

        ArrayList<TripObjectiveWrapper> selected = new ArrayList<>();

        // check if this question has already been answered, and if so recover the previously selected
        //options
        if(fullTripBeingPassed.getObjectivesOfTheTrip() != null){
            selected = fullTripBeingPassed.getObjectivesOfTheTrip();
        }

        if(getActivity() instanceof MyTripsActivity){
            ((MyTripsActivity) getActivity()).computeAndUpdateTripScore();
        }


        adapter = new TripObjectiveGridListAdapter(tripObjectiveWrappers,getContext(), selected);

        objectiveGrid.setAdapter(adapter);

        objectiveGrid.setOnItemClickListener(this);


        // second question
        fixedTimeYesOrNoQuestion = view.findViewById(R.id.include3);
        fixedTimeYesOrNoQuestion.setAlpha(0.5f);
        showOverlay(fixedTimeYesOrNoQuestion.findViewById(R.id.overlay));

        yesButton = fixedTimeYesOrNoQuestion.findViewById(R.id.yesAnswerButton2);
        noButton = fixedTimeYesOrNoQuestion.findViewById(R.id.noAnswerButton2);

        notSureYesOrNo = fixedTimeYesOrNoQuestion.findViewById(R.id.notSureYesOrNoTextView);
        SpannableString content = new SpannableString(getString(R.string.Not_Sure));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        notSureYesOrNo.setText(content);

        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);
        notSureYesOrNo.setOnClickListener(this);

        //third question
        howOftenQuestion = view.findViewById(R.id.include2);
        howOftenQuestion.setAlpha(0.5f);
        showOverlay(fixedTimeYesOrNoQuestion.findViewById(R.id.overlay));

        regularlyButton = howOftenQuestion.findViewById(R.id.regularlyButton);
        occasionallyButton = howOftenQuestion.findViewById(R.id.ocasionallyButton);
        firstTimeButton = howOftenQuestion.findViewById(R.id.firstTimeButton);
        notSureHowOften = howOftenQuestion.findViewById(R.id.notSureHowOftenTextView);
        notSureHowOften.setText(content);  //Underlined "Not Sure"


        regularlyButton.setOnClickListener(this);
        occasionallyButton.setOnClickListener(this);
        firstTimeButton.setOnClickListener(this);
        notSureHowOften.setOnClickListener(this);

        //if we can recover any selected objectives, unlock second question
        if(selected.size() > 0){
            unlockSecondQuestion();
        }

        // check if this question has already been answered, and if so recover the previously selected
        //option (second question)
        if (fullTripBeingPassed.getDidYouHaveToArrive() != null){
            setupFixedTimeQuestion(fullTripBeingPassed.getDidYouHaveToArrive());
        }

        // check if this question has already been answered, and if so recover the previously selected
        //option (second question)
        if(fullTripBeingPassed.getHowOften() != null){
            setupHowOftenQuestion(fullTripBeingPassed.getHowOften());
        }

        //compute and show possible user max score for this task
        int possibleScore;
        possibleScore = fullTripBeingPassed.getTripScored(getContext()).getPossiblePurposePoints();
        if(getActivity() instanceof  MyTripsActivity){
            ((MyTripsActivity) getActivity()).updatePossibleScore(possibleScore);
        }
        return view;
    }

    private void showOverlay(View viewById) {
        viewById.setVisibility(View.VISIBLE);
        viewById.findViewById(R.id.overlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void hideOverlay(View viewById) {
        viewById.findViewById(R.id.overlay).setVisibility(View.GONE);
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
     *  build popup that allows the user to provide another objective other than the ones presented
     * on the grid view
     *
     * @param adapter
     * @param position
     * @param view
     */
    private void showOtherOptionDialog(final TripObjectiveGridListAdapter adapter, final int position, final View view) {

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

//                    selectedModality = adapter.dataSet.get(position);
//                    selectedModality.setLabel(option);

                    adapter.dataSet.get(position).setTripObjectiveString(option);
                    adapter.selectedObjectives.add(adapter.dataSet.get(position));
                    view.setBackground(getResources().getDrawable(R.drawable.grid_selected_item_shape));

                    dialog.dismiss();

                }else{
                    Toast.makeText(getContext(), "Must be at least 3 letters long", Toast.LENGTH_SHORT).show();
                }

                //call method on fragment to show add activities dialog
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    boolean unlockedSecondQuestion = false;
    boolean unlockedThirdQuestion = false;

    public void unlockSecondQuestion(){

        if(!unlockedSecondQuestion){

            hideOverlay(fixedTimeYesOrNoQuestion.findViewById(R.id.overlay));
            fixedTimeYesOrNoQuestion.setAlpha(1);
            //set alpha
            //set unclickable

            unlockedSecondQuestion = true;
        }

    }

    public void unlockThirdQuestion(){

        if(!unlockedThirdQuestion){

            hideOverlay(howOftenQuestion.findViewById(R.id.overlay));
            howOftenQuestion.setAlpha(1);
            //set alpha
            //set unclickable

            unlockedThirdQuestion = true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int index = TripObjectiveWrapper.getIndex(adapter.dataSet.get(position), adapter.selectedObjectives);

        //if item click is not already selected
        if(index == -1){

                //check if user has already filled its home address info and if not, show the popup
                //to set home address
                if(position == TripObjectiveWrapper.keys.HOME && userSettingStateWrapper.getUserSettings().getHomeAddress()==null){

                    String homeAddress = fullTripBeingPassed.getFinalTextLocation();
                    LatLng homeLocation = fullTripBeingPassed.getArrivalPlace().getLatLng();

                    showHomeWorkPopup(keys.HOME, homeAddress, homeLocation);

                }

                //check if user has already filled its work address info and if not, show the popup
                //to set work address
                if(position == TripObjectiveWrapper.keys.WORK && userSettingStateWrapper.getUserSettings().getWorkAddress()==null){

                    String workAddress = fullTripBeingPassed.getFinalTextLocation();
                    LatLng workLocation = fullTripBeingPassed.getArrivalPlace().getLatLng();

                    showHomeWorkPopup(keys.WORK, workAddress, workLocation);

                }

                //check if the option selected is "other" and show popup to answer, otherwise just
                //add the pressed option to the selected objectives array
                if(position == adapter.dataSet.size()-1){

                    showOtherOptionDialog(adapter ,position, view);

                }else{
                    adapter.selectedObjectives.add(adapter.dataSet.get(position));
                    view.setBackground(getResources().getDrawable(R.drawable.grid_selected_item_shape));


                }

        //if was already selected, deselect and remove from selected objectives array
        }else{
            adapter.selectedObjectives.remove(index);
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        unlockSecondQuestion();

    }

    private void showHomeWorkPopup(final int type, final String address, final LatLng location) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View mView = inflater.inflate(R.layout.set_home_or_work_address_popup_layout, null);

        TextView titleTextView = mView.findViewById(R.id.setHomeOrWorkTitlePopupTextView);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        switch (type){

            case keys.HOME:

                titleTextView.setText(getString(R.string.Do_You_Want_To_Set_This_Location_Home));

                break;

            case keys.WORK:

                titleTextView.setText(getString(R.string.Do_You_Want_To_Set_This_Location_Work));

                break;
        }



        final Button yesButton = mView.findViewById(R.id.yesAnswerButton2);
        final Button noButton = mView.findViewById(R.id.noAnswerButton2);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (type){

                    case keys.HOME:

                        Log.d("ObjectiveOfTripFragment", "home yes" + location.toString() + " " + address);

                        userSettingStateWrapper.getUserSettings().setHomeAddress(new HomeWorkAddress(new inesc_id.pt.motivandroid.data.tripData.LatLng(location.latitude, location.longitude), address));
                        break;

                    case keys.WORK:
                        userSettingStateWrapper.getUserSettings().setWorkAddress(new HomeWorkAddress(new inesc_id.pt.motivandroid.data.tripData.LatLng(location.latitude, location.longitude), address));
                        Log.d("ObjectiveOfTripFragment", "home no" +location.toString() + " " + address);


                        break;
                }

                SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);

                dialog.dismiss();

            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (type){

                    case keys.HOME:
                        userSettingStateWrapper.getUserSettings().setHomeAddress(new HomeWorkAddress());
                        break;

                    case keys.WORK:
                        userSettingStateWrapper.getUserSettings().setWorkAddress(new HomeWorkAddress());
                        break;
                }
                SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);

                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    /**
     * setup second question
     *
     * @param answer previously selected option, if selected
     */
    public void setupFixedTimeQuestion(int answer){

        switch (answer){

            case keys.YES:

                didYouArriveAtAFixedTimeAnswer = keys.YES;
                noButton.setAlpha(0.25f);
                yesButton.setAlpha(1);

                unlockThirdQuestion();

                break;

            case keys.NO:

                didYouArriveAtAFixedTimeAnswer = keys.NO;
                unlockThirdQuestion();

                noButton.setAlpha(1);
                yesButton.setAlpha(0.25f);
                break;

            case keys.NOT_SURE_YES_OR_NO:

                didYouArriveAtAFixedTimeAnswer = keys.NOT_SURE_YES_OR_NO;
                unlockThirdQuestion();

                yesButton.setAlpha(0.25f);
                noButton.setAlpha(0.25f);

                break;


        }

    }

    /**
     * setup third question
     *
     * @param answer previously selected option, if selected
     */
    public void setupHowOftenQuestion(int answer){

        switch (answer){

            case keys.REGULARY:

                howOftenAnswerTimeAnswer = keys.REGULARY;

                regularlyButton.setChecked(true);

                occasionallyButton.setChecked(false);
                firstTimeButton.setChecked(false);

                break;

            case  keys.OCCASIONALLY:

                howOftenAnswerTimeAnswer = keys.OCCASIONALLY;
                firstTimeButton.setChecked(false);
                regularlyButton.setChecked(false);

                occasionallyButton.setChecked(true);


                break;

            case keys.FIRST_TIME:

                howOftenAnswerTimeAnswer = keys.FIRST_TIME;
                regularlyButton.setChecked(false);
                occasionallyButton.setChecked(false);

                firstTimeButton.setChecked(true);


                break;

            case keys.NOT_SURE_HOW_OFTEN:

                howOftenAnswerTimeAnswer = keys.NOT_SURE_HOW_OFTEN;

                regularlyButton.setChecked(false);
                occasionallyButton.setChecked(false);
                firstTimeButton.setChecked(false);

//                notSureHowOften.setChecked(true);

                break;


        }

    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.yesAnswerButton2:
                setupFixedTimeQuestion(keys.YES);
                break;

            case R.id.noAnswerButton2:
                setupFixedTimeQuestion(keys.NO);
                break;

            case R.id.notSureYesOrNoTextView:
                setupFixedTimeQuestion(keys.NOT_SURE_YES_OR_NO);
                break;

            case R.id.regularlyButton:

                setupHowOftenQuestion(keys.REGULARY);
                //save and go to next fragment
                setObjectiveAndProceed();

                break;

            case R.id.ocasionallyButton:

                setupHowOftenQuestion(keys.OCCASIONALLY);
                //save and go to next fragment
                setObjectiveAndProceed();


                break;

            case R.id.firstTimeButton:


                setupHowOftenQuestion(keys.FIRST_TIME);
                //save and go to next fragment
                setObjectiveAndProceed();


                break;

            case R.id.notSureHowOftenTextView:

                setupHowOftenQuestion(keys.NOT_SURE_HOW_OFTEN);
                //save and go to next fragment
                setObjectiveAndProceed();

                break;



        }
    }

    private void setObjectiveAndProceed() {

        if (adapter.selectedObjectives.size() < 1){
            Toast.makeText(getContext(), "You must select the purpose", Toast.LENGTH_SHORT).show();
        }else{
            //save selected answers on the trip
            fullTripBeingPassed.setObjectivesOfTheTrip(adapter.selectedObjectives);
            fullTripBeingPassed.setHowOften(howOftenAnswerTimeAnswer);
            fullTripBeingPassed.setDidYouHaveToArrive(didYouArriveAtAFixedTimeAnswer);

            unlockedSecondQuestion = false;
            unlockedThirdQuestion = false;

                tripKeeper.setCurrentFullTrip(fullTripBeingPassed);

                fullTripBeingPassed.getTripScored(getContext()).answerPurpose();

                //if trip has only one leg -> jump to RatingStarsFragment
                if (fullTripBeingPassed.getTripList().size() == 1){

                    RatingStarsFragment mfragment = RatingStarsFragment.newInstance(RatingStarsFragment.keys.TRAVEL_TIME_WASTED_MODE, fullTripBeingPassed.getDateId(), 0);
                    //using Bundle to send data
                    FragmentTransaction transaction=getFragmentManager().beginTransaction();
                    transaction.replace(R.id.my_trips_main_fragment, mfragment).addToBackStack(null);
                    transaction.commit();

                }else{
                    //more than one leg -> go to SelectPartOfTripForFurtherFeedbackFragment for the
                    //user to select which leg/waiting he wants to report on

                    SelectPartOfTripForFurtherFeedbackFragment mfragment = SelectPartOfTripForFurtherFeedbackFragment.newInstance(fullTripBeingPassed.getDateId());
                    //using Bundle to send data
                    FragmentTransaction transaction=getFragmentManager().beginTransaction();
                    transaction.replace(R.id.my_trips_main_fragment, mfragment).addToBackStack(null);
                    transaction.commit();

                }


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

        //fixed time
        int YES = 1;
        int NO = 0;
        int NOT_SURE_YES_OR_NO = -1;

        //how often
        int REGULARY = 0;
        int OCCASIONALLY = 1;
        int FIRST_TIME = 2;
        int NOT_SURE_HOW_OFTEN = -1;

        int HOME = 0;
        int WORK = 1;

    }

}

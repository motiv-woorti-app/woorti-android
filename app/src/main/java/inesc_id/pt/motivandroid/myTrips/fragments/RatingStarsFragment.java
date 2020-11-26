package inesc_id.pt.motivandroid.myTrips.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.RatingBar;
import android.widget.TextView;

import com.whinc.widget.ratingbar.RatingBar;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.myTrips.activities.MyTripsActivity;
import inesc_id.pt.motivandroid.persistence.TripKeeper;

/**
 * RatingStarsFragment
 *
 *  In this fragment the user answers a question using a scale of 1 to 5 stars.
 *
 *  This fragment is used in two modes: keys.FEEL_ABOUT_TRIP_MODE in which the users rates the trip
 *  in terms of wasted time (overall score - after TripValidationFragment), and keys.TRAVEL_TIME_WASTED_MODE
 *  in which the user chooses one leg to rate in terms of wasted time. In the latter case, a leg
 *  number must be passed to the fragment using ARG_PARAM2.
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
public class RatingStarsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mode;

    FullTrip fullTripBeingPassed;
    String fullTripID;

//    PersistentTripStorage persistentTripStorage;

    TripKeeper tripKeeper;

    private OnFragmentInteractionListener mListener;

    int leg =-1;

    int ratingBarCount;

    public RatingStarsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment RatingStarsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RatingStarsFragment newInstance(int param1, String fullTripID, int leg) {
        RatingStarsFragment fragment = new RatingStarsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTripID);
        args.putInt(ARG_PARAM2, leg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getInt(ARG_PARAM1);
        }
        tripKeeper = TripKeeper.getInstance(getContext());
        fullTripID  = getArguments().getString(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED);
        fullTripBeingPassed = tripKeeper.getCurrentFullTrip(fullTripID);
        leg = getArguments().getInt(ARG_PARAM2);

    }

    TextView questionTextView;
    TextView minRangeTextView;
    TextView maxRangeTextView;

    TextView subQuestionTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_two_options_feedback, container, false);

        questionTextView = view.findViewById(R.id.tvTitle);
        minRangeTextView = view.findViewById(R.id.tvFirstOption);
        maxRangeTextView = view.findViewById(R.id.tvSecondOption);

        subQuestionTextView = view.findViewById(R.id.subTextTextView);

        //compute and show max possible user score for this task
        if(getActivity() instanceof MyTripsActivity){
            ((MyTripsActivity) getActivity()).computeAndUpdateTripScore();
            ((MyTripsActivity) getActivity()).updatePossibleScore(0);
        }

        final RatingBar ratingBar = view.findViewById(R.id.ratingBar);

        switch (mode){

            case keys.FEEL_ABOUT_TRIP_MODE:

                //setup title, min range and max range description
                setup(getResources().getString(R.string.Overall_Feel_About_Trip),
                        getResources().getString(R.string.Lousy),
                        getResources().getString(R.string.Great),
                        null
                );

                //check if this question has already been answered and recover
                if (fullTripBeingPassed.getOverallScore() != null){

                    ratingBar.setCount(fullTripBeingPassed.getOverallScore());
                    ratingBarCount = fullTripBeingPassed.getOverallScore();

                }

                break;

            case keys.TRAVEL_TIME_WASTED_MODE:

                //setup title, min range and max range description
                setup(getResources().getString(R.string.Travel_Time_Wasted_Question_Text),
                        getResources().getString(R.string.Travel_Time_Wasted_Min_Text),
                        getResources().getString(R.string.Travel_Time_Wasted_Max_Text),
                        getResources().getString(R.string.Travel_Time_Wasted_Question_Text_Second_Paragraph)
                );

                //check if this question has already been answered and recover
                if (fullTripBeingPassed.getTripList().get(leg).getWastedTime() != null){

                    ratingBar.setCount(fullTripBeingPassed.getTripList().get(leg).getWastedTime());
                    ratingBarCount = fullTripBeingPassed.getTripList().get(leg).getWastedTime();

                }
                break;
        }

        //rating stars on touch listener (ACTION_DOWN -> save user input when it presses the star)
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setPressed(true);
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    float touchPositionX = event.getX();
                    float width = ratingBar.getWidth();
                    float starsf = (touchPositionX / width) * 5.0f;
                    int stars = (int)starsf + 1;

                    Log.d("RatingStarsFragment", "stars " + stars);

                    ratingBar.setCount(stars);
                    ratingBarCount = stars;
                    v.setPressed(false);
                    moveForward();          //Go to the next fragment
                }

                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setPressed(false);
                }

                return true;
            }});

        return view;
    }



    private void setup(String question, String min, String max, String subText) {

        questionTextView.setText(question);
        minRangeTextView.setText(min);
        maxRangeTextView.setText(max);

        if (subText != null){
            subQuestionTextView.setVisibility(View.VISIBLE);
            subQuestionTextView.setText(subText);
        }else{
            subQuestionTextView.setVisibility(View.GONE);
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
        Log.d("RATING", "Detach");
        super.onDetach();
        mListener = null;
    }

    public void moveForward(){
        switch (mode){

            case keys.FEEL_ABOUT_TRIP_MODE:

                fullTripBeingPassed.setOverallScore(ratingBarCount);

                // careful - trip wise
                ObjectiveOfTripFragment mfragment = ObjectiveOfTripFragment.newInstance(fullTripBeingPassed.getDateId());

                Log.d("RatingStarsFragment", "Feel about trip mode (overall score) " + ratingBarCount);

                //using Bundle to send data
                //move to ObjectiveOfTripFragment fragment
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.my_trips_main_fragment, mfragment).addToBackStack(null);
                transaction.commit();

                break;

            case keys.TRAVEL_TIME_WASTED_MODE:

                if (leg >= fullTripBeingPassed.getTripList().size()){
                    leg = fullTripBeingPassed.getTripList().size()-1;
                }

                fullTripBeingPassed.getTripList().get(leg).setWastedTime(ratingBarCount);

                Log.d("RatingStarsFragment", "Travel time wasted (wasted) " + ratingBarCount);

                MyTripsValueObtained mfragment2 = MyTripsValueObtained.newInstance(fullTripBeingPassed.getDateId(), leg);
                //using Bundle to send data
                //move to MyTripsValueObtained fragment
                FragmentTransaction transaction2=getFragmentManager().beginTransaction();
                transaction2.replace(R.id.my_trips_main_fragment, mfragment2).addToBackStack(null);
                transaction2.commit();

                break;

        }

        tripKeeper.setCurrentFullTrip(fullTripBeingPassed);
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

        int TRAVEL_TIME_WASTED_MODE = 0;
        int FEEL_ABOUT_TRIP_MODE = 1;

    }

}

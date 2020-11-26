package inesc_id.pt.motivandroid.mobilityCoach.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.utils.GraphHelper;

/**
 * SetMobilityGoalFragment
 *
 *   This fragment allows the user to set its mobility goal/objective. The mobility goal to be set
 *   is passed to this fragment using the parameter "param1":
 *
 *      PRODUCTIVITY_MODE = 0
 *      ACTIVITY_MODE = 1
 *      RELAXING_MODE = 2
 *
 *   This fragment plots a x,y graph with the current evolution of the user chosen mobility goal,
 *   and a seekbar for the user to edit the mobility goal (from 0 to 100)
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

public class SetMobilityGoalFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MODE = "param1";

    // TODO: Rename and change types of parameters
    private int mode;

    private OnFragmentInteractionListener mListener;

    UserSettingStateWrapper userSettingStateWrapper;
    boolean validOnboarding = false;

    SeekBar goalSeekBar;


    public SetMobilityGoalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SetMobilityGoalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetMobilityGoalFragment newInstance(int param1) {
        SetMobilityGoalFragment fragment = new SetMobilityGoalFragment();
        Bundle args = new Bundle();
        args.putInt(MODE, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getInt(MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_mobility_goal, container, false);

        TextView yourScoreTextView = view.findViewById(R.id.yourScoreTextView);
        Button setGoalButton = view.findViewById(R.id.setGoalButton);
        TextView goalMessageTextView = view.findViewById(R.id.goalMessageTextView);
        TextView gainInTextView = view.findViewById(R.id.gainInTextView);
        ImageView squirrelImageView = view.findViewById(R.id.squirrelImageView);
        SeekBar seekBar = view.findViewById(R.id.currentScoreSeekBar);

        goalSeekBar = view.findViewById(R.id.seekBarGoal);

        setGoalButton.setOnClickListener(this);

        //set up mobility goal seekbar
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "notExistent");

        if ((userSettingStateWrapper != null) && (userSettingStateWrapper.getUid().equals(FirebaseAuth.getInstance().getUid()))) {
            Log.e("ProfileAndSettings", "valid");
            validOnboarding = true;
        }else{
            Log.e("ProfileAndSettings", "invalid");
        }

        //init - change elements according to the mode (0, 1, 2)

        switch(mode){
            case keys.PRODUCTIVITY_MODE:

                yourScoreTextView.setText(R.string.your_productivity_score);
                setGoalButton.setText(R.string.set_a_goal_for_productivity);
                goalMessageTextView.setText(R.string.productivity_goal_message);
                gainInTextView.setText(R.string.gain_in_productivity_mins);
                squirrelImageView.setImageResource(R.drawable.mobility_coach_squirrel_setgoal_bottom_productivity);

                break;
            case keys.ACTIVITY_MODE:

                yourScoreTextView.setText(R.string.your_activity_score);
                setGoalButton.setText(R.string.set_a_goal_for_activity);
                goalMessageTextView.setText(R.string.activity_goal_message);
                gainInTextView.setText(R.string.gain_in_activity_mins);
                squirrelImageView.setImageResource(R.drawable.mobility_coach_squirrel_setgoal_bottom_activity);

                break;
            case keys.RELAXING_MODE:

                yourScoreTextView.setText(R.string.your_relaxing_score);
                setGoalButton.setText(R.string.set_a_goal_for_relaxing);
                goalMessageTextView.setText(R.string.relaxing_goal_message);
                gainInTextView.setText(R.string.gain_in_relaxing_mins);
                squirrelImageView.setImageResource(R.drawable.mobility_coach_squirrel_setgoal_bottom_relaxing);

                break;
        }

        GraphView graph = (GraphView) view.findViewById(R.id.graph);

        //dummy values
        DataPoint[] dataPoints = {
                new DataPoint(1, 50),
                new DataPoint(2, 30),
                new DataPoint(3, 25),
                new DataPoint(4, 5),
                new DataPoint(5, 80)
        };

        boolean isSettingMobilityGoal = true;

        Resources resources = getResources();
        GraphHelper.drawGraph(graph, dataPoints, isSettingMobilityGoal, 0, resources);

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
            case R.id.setGoalButton:

                saveGoal();
                openSetGoalPopUp();

                break;
        }

    }

    private void saveGoal() {

        if(validOnboarding) {


            userSettingStateWrapper.getUserSettings().setHasSetMobilityGoal(true);
            userSettingStateWrapper.getUserSettings().setMobilityGoalChosen(mode);
            userSettingStateWrapper.getUserSettings().setMobilityGoal(goalSeekBar.getProgress());

            SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);
        }
    }

    //open a popup to inform the user that the mobiliity goal has been chosen successfully
    public void openSetGoalPopUp(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.mobility_coach_after_set_goal_popup_layout, null);

        ImageView squirrelAfterGoalSetImageView = mView.findViewById(R.id.popUpImageView);
        TextView popUpMessage = mView.findViewById(R.id.popUpMessageTextView);

        switch(mode){
            case keys.PRODUCTIVITY_MODE:
                squirrelAfterGoalSetImageView.setImageResource(R.drawable.mobility_coach_squirrel_after_set_goal_productivity);
                popUpMessage.setText(R.string.popup_after_goal_set_productivity);

                break;
            case keys.ACTIVITY_MODE:
                squirrelAfterGoalSetImageView.setImageResource(R.drawable.mobility_coach_squirrel_after_set_goal_activity);
                popUpMessage.setText(R.string.popup_after_goal_set_activity);

                break;
            case keys.RELAXING_MODE:
                squirrelAfterGoalSetImageView.setImageResource(R.drawable.mobility_coach_squirrel_after_set_goal_relaxing);
                popUpMessage.setText(R.string.popup_after_goal_set_relaxing);

                break;
        }


        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button closeButton = mView.findViewById(R.id.closePopUpButton);



        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                dialog.dismiss();
                getActivity().finish();

            }
        });

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

        int PRODUCTIVITY_MODE = 0;
        int ACTIVITY_MODE = 1;
        int RELAXING_MODE = 2;

    }
}

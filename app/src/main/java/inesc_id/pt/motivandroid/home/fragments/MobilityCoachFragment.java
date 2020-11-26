package inesc_id.pt.motivandroid.home.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.firebase.auth.FirebaseAuth;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.makeramen.roundedimageview.RoundedImageView;

import org.joda.time.DateTime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.stories.Story;
import inesc_id.pt.motivandroid.data.stories.StoryStateful;
import inesc_id.pt.motivandroid.home.activities.HomeDrawerActivity;
import inesc_id.pt.motivandroid.mobilityCoach.activities.MobilityCoachActivity;
import inesc_id.pt.motivandroid.mobilityCoach.activities.StoryActivity;
import inesc_id.pt.motivandroid.mobilityCoach.fragments.SetMobilityGoalFragment;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;

//import inesc_id.pt.motivandroid.routeRank.data.RouteToBeShown;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.GraphHelper;

import static org.joda.time.DateTimeZone.UTC;

/**
 *
 * MobilityCoachFragment
 *
 * Allows the user to read stories. A new story is added each day (until there are no more stories to
 * show).
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
//public class MobilityCoachFragment extends Fragment implements AdapterView.OnItemClickListener { todo uncomment for RR
public class MobilityCoachFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LinearLayout mobilityModulesLinearLayout;

    //todo uncomment for RR
//    PlanTripItemAdapter adapter;

    UserSettingStateWrapper userSettingStateWrapper;
    boolean validOnboarding = false;

    TextView yourScoreTextView;

    View view;

    ConstraintLayout yourMobilityGoalLayout;
    ConstraintLayout suggestedAlternativeTripsLayout;

    LayoutInflater layoutInflater;



    public MobilityCoachFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MobilityCoachFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MobilityCoachFragment newInstance(String param1, String param2) {
        MobilityCoachFragment fragment = new MobilityCoachFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    ArrayList<StoryStateful> storiesStateful;
    ArrayList<Story> storiesData;

    boolean resetStories= false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "notExistent");

        if ((userSettingStateWrapper != null) && (userSettingStateWrapper.getUid().equals(FirebaseAuth.getInstance().getUid()))) {
            Log.e("ProfileAndSettings", "valid");
            validOnboarding = true;

            if(resetStories){
                userSettingStateWrapper.getUserSettings().setStories(new ArrayList<StoryStateful>());
                SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);
            }

            storiesData = Story.getDummmyStoryList(getContext());
            storiesStateful = userSettingStateWrapper.getUserSettings().getStories();

            if (!(storiesStateful == null || storiesStateful.size() == 0)){

                int zeroTimestampCounters = 0;
                for (StoryStateful storyStateful : storiesStateful){

                    if (storyStateful.getReadTimestamp() == 0l){
                        zeroTimestampCounters++;
                    }

                }

                if(zeroTimestampCounters > 1){
                    Log.e("MobilityCoachFragment", "Corrupted stories, reinitializing");
                    storiesStateful = null;
                }

            }


            checkAndAddNewStoryStateful(storiesData);


        }else{
            Log.e("ProfileAndSettings", "invalid");

        }

    }

    private void checkAndAddNewStoryStateful(ArrayList<Story> stories) {

        boolean needToWrite = false;

        //if no stories in settings/onboarding -> add first story of story list
        if((storiesStateful == null) || (storiesStateful.size() == 0)){

            Log.d("MobilityCoachFragment","No stories stateful in onboarding -> adding first story");

            storiesStateful = new ArrayList<>();

            storiesStateful.add(new StoryStateful(stories.get(0).getStoryID(), false, 0, new DateTime(UTC).getMillis()));
            userSettingStateWrapper.getUserSettings().setStories(storiesStateful);
            needToWrite = true;

            //else if last story shown was read already -> check if there are more stories and add to settings/onboarding stories list
        }else{

            StoryStateful lastStoryStateful = storiesStateful.get(storiesStateful.size()-1);

            Story nextStoryIfExists = Story.getStoryById(lastStoryStateful.getStoryID() + 1, storiesData);

            if(lastStoryStateful.isRead() && nextStoryIfExists != null){
                storiesStateful.add(new StoryStateful(nextStoryIfExists.getStoryID(), false, 0l, computeAvailableTime()));
                userSettingStateWrapper.getUserSettings().setStories(storiesStateful);
                needToWrite = true;
            }

        }

        if (needToWrite){

            Log.e("MobilityCoachFragment", "needs to write = " + true);
            SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);

        }

    }

    LinearLayout storiesLinearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_mobility_coach, container, false);

        ImageView goToSetGoalButton = view.findViewById(R.id.setGoalImageButton);


        goToSetGoalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSetGoalPopUp();
            }
        });

        //TODO SET VISIBILITY VISIBLE JUST FOR RELEASE
        goToSetGoalButton.setVisibility(View.INVISIBLE);

        layoutInflater = inflater;
        mobilityModulesLinearLayout = (LinearLayout) view.findViewById(R.id.mobilityModulesLayout);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // draw stories


        ConstraintLayout storiesModuleLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.your_mobility_journey_mobility_coach_layout, null, false);
        mobilityModulesLinearLayout.addView(storiesModuleLayout);

        storiesLinearLayout = (LinearLayout) view.findViewById(R.id.storiesLinearLayout);
//        storiesLinearLayout.setVerticalGravity(Gravity.BOTTOM);

//        drawStories(storiesStateful, storiesLinearLayout, layoutInflater);

        ImageButton openDrawerButton = view.findViewById(R.id.openDrawerButton);
        openDrawerButton.setOnClickListener(buttonListener);

        return view;
    }

    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.openDrawerButton:
                    try {
                        ((HomeDrawerActivity) getActivity()).openDrawer();
                    } catch (Exception e){ Log.e("ProfileAndSettings", "Null pointer on getActivity");}
                    break;
            }
        }
    };


    /**
     * This method draws the list of stories.
     *
     * @param stories user stories state
     * @param storiesLinearLayout
     * @param inflater
     */
    private void drawStories(final ArrayList<StoryStateful> stories, LinearLayout storiesLinearLayout, LayoutInflater inflater) {

        storiesLinearLayout.removeAllViews();

        int i = 0;

        Log.e("MobilityCoachFragment", "stories stateful.size()" + storiesStateful.size());

        for (final StoryStateful storyStateful : stories) {

            Log.e("MobilityCoachFragment", "story id" + storyStateful.getStoryID());

            final int storyState;
            int resource;

            final Story currStory = Story.getStoryById(storyStateful.getStoryID(), storiesData);

            if (storyStateful.isRead()) {
                Log.d("MobilityCoachFragment","Story read");
                storyState = keys.READ_STORY;
                resource = R.layout.story_list_item_layout;
            } else if (storyStateful.getAvailableTimestamp() < new DateTime(UTC).getMillis()) {
                Log.d("MobilityCoachFragment","StoryAvailable but not read");
                storyState = keys.AVAILABLE_STORY_NOT_READ;
                resource = R.layout.story_list_item_layout;
            } else {
                Log.d("MobilityCoachFragment","Coming tomorrow story");
                storyState = keys.COMING_TOMORROW_STORY;
                resource = R.layout.story_list_item_layout;
            }

            ConstraintLayout storyListItem = (ConstraintLayout) inflater.inflate(resource, null, false);
            storiesLinearLayout.addView(storyListItem,0);

            //fill story content
            RoundedImageView storyImageView = storyListItem.findViewById(R.id.storyImageView);

            Glide.with(this).load(currStory.getImageDrawable())
//                    .override(view.getMeasuredWidth(), view.getMeasuredHeight())
                    .into(storyImageView);

            TextView storyTitleTextView = storyListItem.findViewById(R.id.storyTitleTextView);
            storyTitleTextView.setText(currStory.getLessonTitle());

            TextView storyContentTitleTextView = storyListItem.findViewById(R.id.storyContentTitleTextView);

            if(storyState == keys.COMING_TOMORROW_STORY){
                storyContentTitleTextView.setText(getString(R.string.Coming_Tomorrow));
            }else {
                storyContentTitleTextView.setText(currStory.getContentTitle());
            }

            if (storyState == keys.AVAILABLE_STORY_NOT_READ || storyState == keys.READ_STORY) {
                storyListItem.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        // if user opens a story

                        //if story is available but not yet been read
                        if (storyState == keys.AVAILABLE_STORY_NOT_READ) {

                            storyStateful.setRead(true);
                            storyStateful.setReadTimestamp(new DateTime(UTC).getMillis());

                            //lets add the next available story to the list of user stories state
                            Story nextStoryIfExists = Story.getStoryById(storyStateful.getStoryID() + 1, storiesData);

                            if (nextStoryIfExists != null) {
                                //dummy
                                stories.add(new StoryStateful(nextStoryIfExists.getStoryID(), false, 0, computeAvailableTime()));
                            }

                            userSettingStateWrapper.getUserSettings().setStories(stories);
                            SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);

                        }

                        Intent intentStory = new Intent(getContext(), StoryActivity.class);
                        intentStory.putExtra(StoryActivity.keys.INTENT_STORY, currStory);
                        startActivity(intentStory);
                    }
                });
            }

                //draw the line between the stories (dotted or full)
                if (stories.size() != 1) {

                    if (i == (stories.size() - 1)) {
                        ConstraintLayout storyListItemDotted = (ConstraintLayout) inflater.inflate(R.layout.story_list_item_layout_dotted_divider, null, false);
                        storiesLinearLayout.addView(storyListItemDotted,0);

                    } else {
                        ConstraintLayout storyListItemLine = (ConstraintLayout) inflater.inflate(R.layout.story_list_item_layout_line_divider, null, false);
                        storiesLinearLayout.addView(storyListItemLine,0);
                    }

                }

                i++;
            }

        }


    /**
     * @return when should the next story be available to the user according to the current time.
     * (after 3am)
     */
    private long computeAvailableTime() {

        Calendar date = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 3);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        Log.d("MobilityCoachFragment" , DateHelper.getDateFromTSString(date.getTimeInMillis()));

        if (date.getTimeInMillis() < new DateTime(UTC).getMillis()){
            Log.d("MobilityCoachFragment" , "3am already passed!");
            date.add(Calendar.DAY_OF_MONTH, 1);
            Log.d("MobilityCoachFragment" , DateHelper.getDateFromTSString(date.getTimeInMillis()));
        }else{
            Log.d("MobilityCoachFragment" , "3a still to come!");
        }

        return date.getTimeInMillis();
    }

//    private void setAsReadAndUnlockNextStory(StoryStateful storyStateful) {
//
//        storyStateful.setRead(true);
//
//
//    }

    /**
     * add the mobility goal evolution graph
     */
    private void addYourMobilityGoalLayout(){

        yourMobilityGoalLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.your_mobility_goal_mobility_coach_layout, null, false);
        mobilityModulesLinearLayout.addView(yourMobilityGoalLayout, 0);

    }


    /**
     * update mobility goals evolution graph according to the one chosen by the user
     */
    private void updateGraphValues(){

        yourScoreTextView = view.findViewById(R.id.yourScoreTextView);

        switch(userSettingStateWrapper.getUserSettings().getMobilityGoalChosen()){

            case SetMobilityGoalFragment.keys.PRODUCTIVITY_MODE:
                yourScoreTextView.setText(R.string.your_productivity_score);
                break;

            case SetMobilityGoalFragment.keys.ACTIVITY_MODE:
                yourScoreTextView.setText(R.string.your_activity_score);

                break;

            case SetMobilityGoalFragment.keys.RELAXING_MODE:

                yourScoreTextView.setText(R.string.your_relaxing_score);

                break;
        }

        GraphView graph = (GraphView) view.findViewById(R.id.graph);

        graph.removeAllSeries();

        //dummy values
        DataPoint[] dataPoints = {
                new DataPoint(0, 10),
                new DataPoint(1, 20),
                new DataPoint(2, 30),
                new DataPoint(3, 40),
                new DataPoint(4, 40),
                new DataPoint(5, 40)
        };

        GraphHelper.drawGraph(graph, dataPoints, false, userSettingStateWrapper.getUserSettings().getMobilityGoal() ,getResources());

    }

    private void addSuggestedAlternativeTripsLayout(int index){

        suggestedAlternativeTripsLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.suggested_alternative_trips_mobility_coach_layout, null, false);
        mobilityModulesLinearLayout.addView(suggestedAlternativeTripsLayout, index);

    }

//    private void updateSuggestedAlternativeTripsLayout() {
//
//        ExpandableHeightListView alternativeListView = suggestedAlternativeTripsLayout.findViewById(R.id.alternativeTripsListView);
//        alternativeListView.setExpanded(true);
//
//        ArrayList<RouteToBeShown> routes = new ArrayList<>();
//        routes.add(SharedPreferencesUtil.readLastSearchedRoute(getContext()));
//
//        //just to render the recycler view
//        adapter = new PlanTripItemAdapter(routes,getContext());
//
//        alternativeListView.setAdapter(adapter);
//        alternativeListView.setOnItemClickListener(this);
//
//    }

    @Override
    public void onResume() {
        super.onResume();

        //check which modules have already been drawn -> add them or update them
        // check if a mobility goal was chosen by the user, and if so show "Your mobility goal"

        Log.e("MobilityCoach", "onresume");

        if(validOnboarding){

            userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "notExistent");

            storiesStateful = userSettingStateWrapper.getUserSettings().getStories();

            drawStories(storiesStateful, storiesLinearLayout, layoutInflater);



            //check if a goal has been set - if so add child else do nothing
            if((userSettingStateWrapper.getUserSettings().isHasSetMobilityGoal())){

                //has not been drawn todo uncomment JUST FOR RELEASE
//                if((yourMobilityGoalLayout == null) || (mobilityModulesLinearLayout.indexOfChild(yourMobilityGoalLayout) == -1)){
//                    addYourMobilityGoalLayout();
//                }

                //always update graph values todo uncomment JUST FOR RELEASE
//                updateGraphValues();



            }else{
                //Toast.makeText(getContext(), "No mobility goal defined", Toast.LENGTH_LONG).show();
            }

        }


        //check if there was a trip search, and if so show "Alternative trips suggestions" TODO UNCOMMENT JUST FOR RELEASE
//        if(SharedPreferencesUtil.readLastSearchedRoute(getContext()) != null){
//
//            if((suggestedAlternativeTripsLayout == null) || (mobilityModulesLinearLayout.indexOfChild(suggestedAlternativeTripsLayout) == -1)){
//
//                // check if yourMobilityGoal has been drawn already - if so draw suggested alternative trips under it (next index)
//                if((yourMobilityGoalLayout == null) || (mobilityModulesLinearLayout.indexOfChild(yourMobilityGoalLayout) == -1)) {
//                    addSuggestedAlternativeTripsLayout(0);
//                }else{
//                    int indexToDraw = mobilityModulesLinearLayout.indexOfChild(yourMobilityGoalLayout) + 1;
//                    addSuggestedAlternativeTripsLayout(indexToDraw);
//                }
//
//            }
//
//            updateSuggestedAlternativeTripsLayout();
//
//        }




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

    public void openSetGoalPopUp(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.mobility_coach_lets_set_goal_popup_layout, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button beginButton = mView.findViewById(R.id.setGoalButton);

        beginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(getContext(), MobilityCoachActivity.class));
                dialog.dismiss();

            }
        });

    }

    public interface keys{

        //story state

        int READ_STORY = 0;
        int COMING_TOMORROW_STORY = 1;
        int AVAILABLE_STORY_NOT_READ = 2;

    }

}

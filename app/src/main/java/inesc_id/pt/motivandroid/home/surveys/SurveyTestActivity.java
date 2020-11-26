package inesc_id.pt.motivandroid.home.surveys;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.google.firebase.auth.FirebaseAuth;

import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import inesc_id.pt.motivandroid.ApplicationClass;
import inesc_id.pt.motivandroid.BuildConfig;
import inesc_id.pt.motivandroid.home.activities.HomeDrawerActivity;
import inesc_id.pt.motivandroid.managers.CampaignManager;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.managers.RewardManager;
import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.data.pointSystem.CampaignScore;
import inesc_id.pt.motivandroid.data.userSettingsData.Language;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.rewards.RewardData;
import inesc_id.pt.motivandroid.home.surveys.adapters.QuestionRecyclerViewAdapter;
import inesc_id.pt.motivandroid.data.surveys.SurveyStateful;
import inesc_id.pt.motivandroid.data.surveys.answers.Answer;
import inesc_id.pt.motivandroid.data.surveys.tempSurveySubmission.AnsweredReportToServer;
import inesc_id.pt.motivandroid.data.surveys.tempSurveySubmission.AnsweredSurveyToServer;
import inesc_id.pt.motivandroid.home.SurveyAdapterCallback;
import inesc_id.pt.motivandroid.motviAPIClient.MotivAPIClientManager;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;

import static org.joda.time.DateTimeZone.UTC;

/**
 *
 * SurveyTestActivity
 *
 *   Activity that presents a survey to be answered by the user. There are two possible modes,
 *   according to the flag passed in the intent with the name "isReporting". If true, the activity
 *   will be presenting the "Report issue" survey.
 *   The survey (either "Reporting issue" or other survey created in the backoffice) is passed to this
 *   activity through the intent serializable named "SurveyStateful"
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
public class SurveyTestActivity extends AppCompatActivity implements View.OnClickListener, SurveyAdapterCallback {

    QuestionRecyclerViewAdapter questionRecyclerViewAdapter;

    SurveyStateful surveyStateful;

    // recycler view to list the questions of the survey
    private RecyclerView questionRecyclerView;

//    Button answerSurveyButton;

    public PersistentTripStorage persistentTripStorage;

    //segmented progress bar that the indicates the progress to the user
    public SegmentedProgressBar segmentedProgressBar;

    public Button beginSurveyButton;
    public Button confirmAllSurveyButton;

    public boolean isReportingIssue;

    ImageButton closeSurveyButton;

    TextView skipTextView;

//    final int CODE_SEND = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isReportingIssue = getIntent().getBooleanExtra("isReporting",false);
        surveyStateful = (SurveyStateful) getIntent().getSerializableExtra("SurveyStateful");

        //check if survey to be shown is valid
        if(surveyStateful.getSurvey().getQuestions().size() == 0){
            Toast.makeText(this, "Empty survey", Toast.LENGTH_LONG).show();
            finish();
        }


        if(isReportingIssue) {
            setContentView(R.layout.activity_survey_test);

            skipTextView = findViewById(R.id.skipTextView);
            skipTextView.setOnClickListener(this);

            skipTextView.setAlpha(0.45f);
            skipTextView.setClickable(false);

        }else{

            setContentView(R.layout.activity_survey_layout);
        }

        TextView surveyTitle = findViewById(R.id.surveyTitleTextView);
        TextView surveyDescription = findViewById(R.id.surveyDescription);
        TextView surveyBoxTitle = findViewById(R.id.surveyTitleBoxTextView);

        questionRecyclerView = (RecyclerView) findViewById(R.id.questionRecyclerView);
        GridLayoutManager recyclerLayoutManager = new GridLayoutManager(this,1);
        questionRecyclerView.setLayoutManager(recyclerLayoutManager);


        if(isReportingIssue){
            surveyTitle.setText(R.string.Issue_Form);
            surveyBoxTitle.setText(R.string.Issue_Form);
            surveyDescription.setText(R.string.Thank_You_For_Reporting_An_Issue);
        }else{

            String title = surveyStateful.getSurvey().getSurveyName();
            String description = surveyStateful.getSurvey().getDescription();

            Log.d("SurveyTestActivity", "title " + title);
            Log.d("SurveyTestActivity", "description " + description);


            if(title != null){
                surveyTitle.setText(title);
                surveyBoxTitle.setText(title);
            }else{
                surveyTitle.setText("Generic title survey");
                surveyBoxTitle.setText("Generic title survey");
            }

            if(description != null){
                surveyDescription.setText(description);
            }else{
                surveyDescription.setText("Generic survey description");
            }

            int surveyPoints = surveyStateful.getSurvey().getSurveyPoints();

            TextView pointsTextView = findViewById(R.id.pointsTextView);
            pointsTextView.setText("+" + surveyPoints + "pts");

        }

        segmentedProgressBar = findViewById(R.id.questionsSegmentedProgressBar);

        try{
            segmentedProgressBar.setSegmentCount(surveyStateful.getSurvey().getQuestions().size());
        }catch (Exception e){
            segmentedProgressBar.setSegmentCount(1);
        }

        TextView numQuestionsTextView = findViewById(R.id.numQuestionsTextView);
        numQuestionsTextView.setText(getString(R.string.N_Questions, surveyStateful.getSurvey().getQuestions().size()));

        persistentTripStorage = new PersistentTripStorage(this);

        String language = "eng";

        //try to match the user chosen language and the survey available languages (eng if no match
        //is found
        for(Language lang : Language.getAllLanguages()){
            if(getResources().getConfiguration().locale.getLanguage().equals(new Locale(lang.getSmartphoneID()).getLanguage())){
                language = lang.getWoortiID();
                Log.e("Survey Test Activity", "Language matched. Will try to show survey in " + lang.getName());
                break;
            }
        }

        questionRecyclerViewAdapter = new QuestionRecyclerViewAdapter(surveyStateful, getApplicationContext(), this, language);
        questionRecyclerView.setAdapter(questionRecyclerViewAdapter);

        questionRecyclerView.setItemAnimator(null);

        closeSurveyButton = findViewById(R.id.closeSurveyButton);
        closeSurveyButton.setOnClickListener(this);


        beginSurveyButton = findViewById(R.id.beginSurveyButton);
        beginSurveyButton.setOnClickListener(this);

        confirmAllSurveyButton = findViewById(R.id.confirmAllSurveyButton);
        confirmAllSurveyButton.setOnClickListener(this);

        confirmAllSurveyButton.setAlpha(0.45f);
        confirmAllSurveyButton.setClickable(false);

    }

    long logTS = 0l;
    String uid;


    /**
     * method to send an email with the log files after answering a "Report issue" survey
     */
    public void sendLogEmail(){
        //startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:to@gmail.com")));


        String[] logFiles = {"motivlog0.log", "motivlog1.log", "motivlog2.log"};

        final Intent emailIntent = new Intent( Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
//                new String[] { "woortiissuereporting@gmail.com" });
                new String[] { ApplicationClass.reportingEmail });

        logTS = new DateTime(UTC).getMillis();
        uid = FirebaseAuth.getInstance().getUid();

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Log from " + FirebaseAuth.getInstance().getCurrentUser().getEmail() + " " + uid+logTS);


        emailIntent.putExtra(Intent.EXTRA_TEXT,
                "Please send this email as is."
        );

        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        int existingFiles = 0;

        ArrayList<Uri> URIArrayList=new ArrayList<Uri>();

        for(String logFileName : logFiles){

            File file = new File(Environment.getExternalStorageDirectory().toString() +"/motivAndroidLogs" + "/"+logFileName);
            if(file.exists()){
                existingFiles ++;
                System.out.println("file is already there");
//                Uri uri = Uri.fromFile(file);

                //per https://robusttechhouse.com/how-to-share-files-securely-with-fileprovider/
                Uri uri = FileProvider.getUriForFile(getApplicationContext(), "inesc_id.pt.motivandroid.logfileprovider", file);

                URIArrayList.add(uri);
            }else{
                System.out.println("Not find file ");
                break;
            }
        }

        if (existingFiles == 0) {
            Toast.makeText(getApplicationContext(), "Attachment Error", Toast.LENGTH_SHORT).show();
            return;
        }else{
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, URIArrayList);

            Log.d("Home Drawer Activity", "Attachment list size: " + URIArrayList.size());


            startActivity(Intent.createChooser(
                    emailIntent, "Send mail..."));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.skipTextView:

                MotivAPIClientManager.getInstance(getApplicationContext()).makePostReportSurveyToServer(getAnsweredSurvey());

                showFinalPopup();

                break;

            case R.id.confirmAllSurveyButton:

                if(isReportingIssue){

                    MotivAPIClientManager.getInstance(getApplicationContext()).makePostReportSurveyToServer(getAnsweredSurvey());

                    sendLogEmail();

                    showFinalPopup();

                    Toast.makeText(getApplicationContext(), "Sending report to server", Toast.LENGTH_LONG).show();

                }else {

                    SurveyStateful answeredSurveyStateful = questionRecyclerViewAdapter.getAnsweredSurveyStateful();
                    answeredSurveyStateful.setHasBeenAnswered(true);
                    answeredSurveyStateful.setAnswerTimestamp(DateTime.now().getMillis());

                    ArrayList<RewardData> completedRewards = updateGlobalScoreForCampaignsAndSave();

                    persistentTripStorage.updateTriggeredSurveyDataObject(answeredSurveyStateful);

                    MotivAPIClientManager.getInstance(getApplicationContext()).makePostSurveyAnswersToServer();

                    boolean isShowingCompleteRewardPopup = false;

                    for (RewardData completedReward : completedRewards){
                        isShowingCompleteRewardPopup = true;
                        showCompletedRewardPopup(completedReward.getRewardName());

                        RewardManager.getInstance().setRewardAsShown(completedReward.getRewardId(), getApplicationContext());

                        break;
                    }



                    for (Answer a : answeredSurveyStateful.getAnswersArrayList()) {
                        Log.d("answer", a.toString());
                    }

//                    Toast.makeText(getApplicationContext(), "Trying to send survey answers to server", Toast.LENGTH_LONG).show();

                    if (!isShowingCompleteRewardPopup) {
                        finish();
                    }
                }

                break;
            case R.id.beginSurveyButton:
                questionRecyclerViewAdapter.beginSurvey();
//                beginSurveyButton.setAlpha(0.20f);
//                beginSurveyButton.setEnabled(false);
                break;

            case R.id.closeSurveyButton:

                onBackPressed();

                break;

        }
    }

    /**
     * update the user scores for the user related campaigns. Also checks if any reward has been
     * completed
     *
     * @return array of completed rewards
     */
    public ArrayList<RewardData> updateGlobalScoreForCampaignsAndSave(){

        CampaignManager campaignManager = CampaignManager.getInstance(getApplicationContext());

        ArrayList<String> targetedSurveyCampaigns = surveyStateful.getSurvey().getLaunch().getCampaignIDs();

        ArrayList<Campaign> campaignArrayList = campaignManager.getCampaignIDsToPointSurveys(targetedSurveyCampaigns);

        UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getApplicationContext(), "default");

        ArrayList<RewardData> completedRewards = new ArrayList<>();

        for (Campaign campaign : campaignArrayList) {

            int surveyScore = surveyStateful.getSurvey().getSurveyPoints();
            String campaignID = campaign.getCampaignID();

            CampaignScore.updateCampaignScoreForSurvey(surveyScore, campaignID, userSettingStateWrapper.getUserSettings().getPointsPerCampaign());

            completedRewards.addAll(RewardManager.getInstance().checkAndAssignPointsForSurvey(campaignID, getApplicationContext(), surveyScore));

        }

        SharedPreferencesUtil.writeOnboardingUserData(getApplicationContext(), userSettingStateWrapper, true);


        return completedRewards;

    }


    /**
     * shows popup to warn the user that he has successfully achieved a reward
     *
     * @param completedRewardName name of the reward achieved by the user
     */
    private void showCompletedRewardPopup(String completedRewardName) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getApplicationContext());
        View mView = getLayoutInflater().inflate(R.layout.reward_completed_popup_layout, null);

        mBuilder.setView(mView);

        ImageView imageView = mView.findViewById(R.id.squirrelImageView);

        Glide.with(getApplicationContext()).load(R.drawable.onboarding_illustrations_enjoyment).into(imageView);

        TextView rewardNameCompletedTextView = mView.findViewById(R.id.popUpMessageTextView);

        rewardNameCompletedTextView.setText(getString(R.string.You_Have_Completed_Your_Target, completedRewardName));

        final AlertDialog dialog = mBuilder.create();

        Button continueButton = mView.findViewById(R.id.continueButton);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                    finish();

                //call method on fragment to show add activities dialog
            }
        });

        dialog.show();


    }


    /**
     * show popup that warns the user that he has successfully reported an issue, and asks if the
     * user wants to proceed to report another issue or return to the previous menu
     */
    private void showFinalPopup() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.report_issue_popup_layout, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        final Button confirmAndLeaveButton = mView.findViewById(R.id.confirmAndLeaveButton);
        confirmAndLeaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                finish();


            }
        });

        final Button confirmAndSubmitMoreButton = mView.findViewById(R.id.confirmAndSubmitMoreButton);
        confirmAndSubmitMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                Intent returnIntent = new Intent();


                setResult(HomeDrawerActivity.RELOAD_REPORTING, returnIntent);
                finish();

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }


    /**
     * method to generate "Report issue" survey answer in the format the server is expecting
     *
     * @return
     */
    public AnsweredReportToServer getAnsweredSurvey(){

        SurveyStateful answeredSurveyStateful = questionRecyclerViewAdapter.getAnsweredSurveyStateful();
        answeredSurveyStateful.setHasBeenAnswered(true);

        logTS = new DateTime(UTC).getMillis();

        AnsweredSurveyToServer answeredSurveyToServer = surveyStateful.getAnsweredSurveyToServer();
        answeredSurveyToServer.setAnswerDate(logTS);

        answeredSurveyToServer.setUid(FirebaseAuth.getInstance().getUid());
        answeredSurveyToServer.setVersion(answeredSurveyStateful.getSurvey().getVersion());

        AnsweredReportToServer answeredReportToServer = AnsweredReportToServer.getAnsweredReportToSurvey(
                answeredSurveyToServer,
                FirebaseAuth.getInstance().getUid() + logTS,
                "Android: " + android.os.Build.VERSION.SDK_INT + " App version: "+ BuildConfig.VERSION_NAME);

        return answeredReportToServer;

    }

    @Override
    public void updateProgessBar(ArrayList<Answer> answerArrayList) {

    }

    @Override
    public void updateProgessBar(int progress) {

        segmentedProgressBar.setCompletedSegments(progress);

    }

    @Override
    public void enableConfirmAllButton(){

        confirmAllSurveyButton.setAlpha(1);
        confirmAllSurveyButton.setClickable(true);

        if(isReportingIssue) {
            skipTextView.setAlpha(1);
            skipTextView.setClickable(true);
        }
    }
}

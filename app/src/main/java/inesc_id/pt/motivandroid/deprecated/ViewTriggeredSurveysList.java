package inesc_id.pt.motivandroid.deprecated;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.surveys.SurveyStateful;
import inesc_id.pt.motivandroid.data.surveys.questions.Question;
import inesc_id.pt.motivandroid.data.surveys.questions.RadioButtonQuestion;
import inesc_id.pt.motivandroid.home.surveys.SurveyTestActivity;
import inesc_id.pt.motivandroid.deprecated.listAdapters.TriggeredSurveysListAdapter;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;

@Deprecated
public class ViewTriggeredSurveysList extends AppCompatActivity
//        implements View.OnClickListener
{

    ListView triggeredSurveysListView;

    public PersistentTripStorage persistentTripStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_triggered_surveys_list);

        triggeredSurveysListView = findViewById(R.id.triggeredSurveysListView);

        triggeredSurveysListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                // Start your Activity according to the item just clicked.

                SurveyStateful item = (SurveyStateful) parent.getItemAtPosition(position);

                Log.e("onitem",item.toString());

                if(!item.isHasBeenAnswered()) {

                    Intent intentShowSurvey = new Intent(getApplicationContext(), SurveyTestActivity.class);
                    intentShowSurvey.putExtra("SurveyStateful", item);

                    Log.d("size surveys", "size " + item.getSurvey().getQuestions().size());
                    Log.d("size surveys", "pref lang " + item.getSurvey().getDefaultLanguage());

                    for(Question question : item.getSurvey().getQuestions()){

                        if(question instanceof RadioButtonQuestion){

                            Log.e("print map", new Gson().toJson(item.getSurvey().getQuestions()));
                        }

                    }

//                intentShowSurvey.putExtra("SurveyStatefulTriggerTimestamp", item.getTriggerTimestamp());
//                intentShowSurvey.putExtra("SurveyStatefulID", item.getSurvey().getSurveyID());

                    startActivity(intentShowSurvey);
                }
            }
        });

        persistentTripStorage = new PersistentTripStorage(this);

//        Button sampleSurvey = findViewById(R.id.sampleSurveyButton);
//        sampleSurvey.setOnClickListener(this);

    }

    @Override
    protected void onResume(){
        super.onResume();
        getTriggeredSurveysFromStorageAndList();

    }

    public void getTriggeredSurveysFromStorageAndList(){
        ArrayList<SurveyStateful> savedTriggeredSurveys = persistentTripStorage.getAllTriggeredSurveysObject();

//        ReverseTripTimeOrder customComparator = new ReverseTripTimeOrder();
//
//        Collections.sort(savedFullTrips, customComparator);

        ArrayAdapter<SurveyStateful> itemsAdapter =
                new ArrayAdapter<SurveyStateful>(getApplicationContext(), android.R.layout.simple_list_item_1, savedTriggeredSurveys);

        triggeredSurveysListView.setAdapter(new TriggeredSurveysListAdapter(getApplicationContext(), savedTriggeredSurveys));
    }

//    public void createAndShowReportIssue(){
//
//        ArrayList<String> answers = new ArrayList<>();
//        answers.add("Trip not detected (when it should be)");
//        answers.add("Trip wrongly detected (when it should not be)");
//        answers.add("Travel mode wrongly detected in a leg (e.g., car instead of walking)");
//        answers.add("Interface/Usability");
//
//        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
//
//        HashMap<String, String> question = new HashMap<>();
//        question.put("en", "What type of error was it?");
//
//        HashMap<String, ArrayList<String>> answer = new HashMap<>();
//        answer.put("en", answers);
//        multipleChoiceQuestion.setQuestion(question);
//        multipleChoiceQuestion.setOptions(answer);
//
//
////        multipleChoiceQuestion.setDeleted(true);
////
////        RadioButtonQuestion radioButtonQuestion = new RadioButtonQuestion();
////        radioButtonQuestion.setQuestion("Radio Button question?");
////        radioButtonQuestion.setOptions(answers);
////
////        ShortTextQuestion shortTextQuestion = new ShortTextQuestion();
////        shortTextQuestion.setQuestion("Short text question?");
////
////        YesOrNoQuestion yesOrNoQuestion = new YesOrNoQuestion();
////        yesOrNoQuestion.setQuestion("Yes or no question?");
////
//        ArrayList<Question> questionArrayList = new ArrayList<>();
////        questionArrayList.add(shortTextQuestion);
////        questionArrayList.add(radioButtonQuestion);
//        questionArrayList.add(multipleChoiceQuestion);
////        questionArrayList.add(yesOrNoQuestion);
////
//        Survey survey = new Survey();
//        survey.setQuestions(questionArrayList);
////
//        SurveyStateful surveyStateful = new SurveyStateful(survey,false, new ArrayList<Answer>(), 0l, 0l, "");
//
//        Intent intentShowSurvey = new Intent(getApplicationContext(), SurveyTestActivity.class);
//        intentShowSurvey.putExtra("SurveyStateful", surveyStateful);
//
////                intentShowSurvey.putExtra("SurveyStatefulTriggerTimestamp", item.getTriggerTimestamp());
////                intentShowSurvey.putExtra("SurveyStatefulID", item.getSurvey().getSurveyID());
//
//        startActivity(intentShowSurvey);
//
//    }


//    @Override
//    public void onClick(View v) {
//
//        switch (v.getId()){
//            case R.id.sampleSurveyButton:
//                createAndShowReportIssue();
//                break;
//        }
//
//    }
}

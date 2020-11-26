package inesc_id.pt.motivandroid.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.data.tripData.WaitingEvent;
import inesc_id.pt.motivandroid.data.surveys.answers.Answer;
import inesc_id.pt.motivandroid.data.surveys.answers.DropdownAnswer;
import inesc_id.pt.motivandroid.data.surveys.answers.MultipleChoiceAnswer;
import inesc_id.pt.motivandroid.data.surveys.answers.ParagraphAnswer;
import inesc_id.pt.motivandroid.data.surveys.answers.RadioButtonAnswer;
import inesc_id.pt.motivandroid.data.surveys.answers.ShortTextAnswer;
import inesc_id.pt.motivandroid.data.surveys.answers.SliderAnswer;
import inesc_id.pt.motivandroid.data.surveys.answers.YesOrNoAnswer;
import inesc_id.pt.motivandroid.data.surveys.questions.DropdownQuestion;
import inesc_id.pt.motivandroid.data.surveys.questions.ParagraphQuestion;
import inesc_id.pt.motivandroid.data.surveys.questions.YesOrNoQuestion;
import inesc_id.pt.motivandroid.data.surveys.triggers.EventTrigger;
import inesc_id.pt.motivandroid.data.surveys.triggers.TimedRecurringTrigger;
import inesc_id.pt.motivandroid.data.surveys.triggers.TimedTrigger;
import inesc_id.pt.motivandroid.data.surveys.triggers.Trigger;
import inesc_id.pt.motivandroid.data.surveys.questions.MultipleChoiceQuestion;
import inesc_id.pt.motivandroid.data.surveys.questions.Question;
import inesc_id.pt.motivandroid.data.surveys.questions.RadioButtonQuestion;
import inesc_id.pt.motivandroid.data.surveys.questions.ShortTextQuestion;
import inesc_id.pt.motivandroid.data.surveys.questions.SliderQuestion;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityLeg;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.BodyActivityLeg;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.GenericActivityLeg;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.MindActivityLeg;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ProductivityActivityLeg;

/**
 *
 * JSONUtils
 *
 *   Class that generates two GSON with different configurations. One for general purposes ("gson"),
 *   another for encoding and decoding surveys.
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

public class JSONUtils {

    private Gson gson;

    private Gson gsonSurvey;

    private static JSONUtils instance;

    public static JSONUtils getInstance(){
        if(instance == null){
            instance = new JSONUtils();
            return instance;
        }else{
            return instance;
        }
    }

    private JSONUtils(){

        //for trip data
        GsonBuilder builder = new GsonBuilder();
        //builder.registerTypeAdapter(AtomicInteger.class, new AtomicIntegerTypeAdapter());
        RuntimeTypeAdapterFactory<FullTripPart> adapter = RuntimeTypeAdapterFactory
                .of(FullTripPart.class)
                .registerSubtype(Trip.class)
                .registerSubtype(WaitingEvent.class);

        RuntimeTypeAdapterFactory<ActivityLeg> activitiesAdapter = RuntimeTypeAdapterFactory
                .of(ActivityLeg.class)
                .registerSubtype(BodyActivityLeg.class)
                .registerSubtype(ProductivityActivityLeg.class)
                .registerSubtype(MindActivityLeg.class)
                .registerSubtype(GenericActivityLeg.class);

        builder.registerTypeAdapterFactory(adapter);
        builder.registerTypeAdapterFactory(activitiesAdapter);

//        builder.setPrettyPrinting().excludeFieldsWithoutExposeAnnotation();
        builder.excludeFieldsWithoutExposeAnnotation();
        builder.serializeSpecialFloatingPointValues();
        gson = builder.create();

        //for survey data
        GsonBuilder builderSurveys = new GsonBuilder();
        RuntimeTypeAdapterFactory<Trigger> adapterSurveyTrigger = RuntimeTypeAdapterFactory
                .of(Trigger.class)
                .registerSubtype(TimedRecurringTrigger.class, "timedRecurringTrigger")
                .registerSubtype(EventTrigger.class, "eventTrigger")
                .registerSubtype(TimedTrigger.class, "timedTrigger");

        RuntimeTypeAdapterFactory<Question> adapterSurveyQuestions = RuntimeTypeAdapterFactory
                .of(Question.class, "questionType")
                .registerSubtype(ParagraphQuestion.class, "paragraph")
                .registerSubtype(MultipleChoiceQuestion.class, "checkboxes")
                .registerSubtype(RadioButtonQuestion.class, "multipleChoice")
                .registerSubtype(ShortTextQuestion.class, "shortText")
                .registerSubtype(SliderQuestion.class,"scale")
                .registerSubtype(DropdownQuestion.class, "dropdown")
                .registerSubtype(YesOrNoQuestion.class, "yesNo");

        RuntimeTypeAdapterFactory<Answer> adapterSurveyAnswers = RuntimeTypeAdapterFactory
                .of(Answer.class, "questionType")
                .registerSubtype(MultipleChoiceAnswer.class, "checkboxes")
                .registerSubtype(RadioButtonAnswer.class, "multipleChoice")
                .registerSubtype(ShortTextAnswer.class, "shortText")
                .registerSubtype(SliderAnswer.class,"scale")
                .registerSubtype(DropdownAnswer.class, "dropdown")
                .registerSubtype(YesOrNoAnswer.class, "yesNo")
                .registerSubtype(ParagraphAnswer.class,"paragraph");

        builderSurveys.registerTypeAdapterFactory(adapterSurveyTrigger);
        builderSurveys.registerTypeAdapterFactory(adapterSurveyQuestions);
        builderSurveys.registerTypeAdapterFactory(adapterSurveyAnswers);

        builderSurveys.serializeSpecialFloatingPointValues();
        gsonSurvey = builderSurveys.create();

    }



    public Gson getGson(){
        return gson;
    }

    public Gson getSurveyGSON(){

        return gsonSurvey;

    }

}

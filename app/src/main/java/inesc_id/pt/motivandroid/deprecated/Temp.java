package inesc_id.pt.motivandroid.deprecated;
import java.io.Serializable;
import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.surveys.tempSurveySubmission.TempAnsweredSurvey;

@Deprecated
public class Temp implements Serializable{

    ArrayList<TempAnsweredSurvey> answeredSurveys;

    public Temp(ArrayList<TempAnsweredSurvey> answeredSurveys) {
        this.answeredSurveys = answeredSurveys;
    }

    public ArrayList<TempAnsweredSurvey> getAnsweredSurveys() {
        return answeredSurveys;
    }

    public void setAnsweredSurveys(ArrayList<TempAnsweredSurvey> answeredSurveys) {
        this.answeredSurveys = answeredSurveys;
    }

}

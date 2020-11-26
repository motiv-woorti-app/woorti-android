package inesc_id.pt.motivandroid.data.surveys.tempSurveySubmission;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.surveys.answers.Answer;
import inesc_id.pt.motivandroid.data.surveys.triggers.Trigger;

/**
 *  AnsweredSurveyToServer
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
public class AnsweredSurveyToServer{

    long answerDate;
    ArrayList<Answer> answers;
    String uid;
    String lang;
    int surveyID;
    long triggerDate;
    int version;

    public AnsweredSurveyToServer(long answerDate, ArrayList<Answer> answers, String uid, String lang, int surveyID, long triggeredDate, int version) {
        this.answerDate = answerDate;
        this.answers = answers;
        this.uid = uid;
        this.lang = lang;
        this.surveyID = surveyID;
        this.triggerDate = triggeredDate;
        this.version = version;
    }

    public long getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(long answerDate) {
        this.answerDate = answerDate;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getSurveyID() {
        return surveyID;
    }

    public void setSurveyID(int surveyID) {
        this.surveyID = surveyID;
    }

    public long getTriggeredDate() {
        return triggerDate;
    }

    public void setTriggeredDate(long triggeredDate) {
        this.triggerDate = triggeredDate;
    }


    public long getTriggerDate() {
        return triggerDate;
    }

    public void setTriggerDate(long triggerDate) {
        this.triggerDate = triggerDate;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}

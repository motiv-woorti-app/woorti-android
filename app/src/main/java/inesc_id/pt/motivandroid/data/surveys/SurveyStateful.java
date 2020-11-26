package inesc_id.pt.motivandroid.data.surveys;

import java.io.Serializable;
import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.surveys.answers.Answer;
import inesc_id.pt.motivandroid.data.surveys.tempSurveySubmission.AnsweredSurveyToServer;
import inesc_id.pt.motivandroid.utils.DateHelper;

/**
 * SurveyStateful
 *
 *      Holds the state of a survey, including if it has already been answered, the answers, if has
 *  already been triggered, language answered, and if it has already been sent to the server
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

public class SurveyStateful implements Serializable{

    Survey survey;
    boolean hasBeenAnswered;
    public ArrayList<Answer> answersArrayList;
    long answerTimestamp;
    long triggerTimestamp;
    String userid;
    boolean hasBeenSentToServer;
    String lang;

    public SurveyStateful(Survey survey, boolean hasBeenAnswered, ArrayList<Answer> answersArrayList, long answerTimestamp, long triggerTimestamp, String userid, boolean hasBeenSentToServer, String lang) {
        this.survey = survey;
        this.hasBeenAnswered = hasBeenAnswered;
        this.answersArrayList = answersArrayList;
        this.answerTimestamp = answerTimestamp;
        this.triggerTimestamp = triggerTimestamp;
        this.userid = userid;
        this.hasBeenSentToServer = hasBeenSentToServer;
        this.lang = lang;
    }

//    public SurveyStatefull(Survey survey, boolean hasBeenAnswered, ArrayList<Answer> answersArrayList) {
//        this.survey = survey;
//        this.hasBeenAnswered = hasBeenAnswered;
//        this.answersArrayList = answersArrayList;
//    }

//    public SurveyStateful() {
//        answersArrayList = new ArrayList<>();
//    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public boolean isHasBeenAnswered() {
        return hasBeenAnswered;
    }

    public void setHasBeenAnswered(boolean hasBeenAnswered) {
        this.hasBeenAnswered = hasBeenAnswered;
    }

    public ArrayList<Answer> getAnswersArrayList() {
        return answersArrayList;
    }

    public void setAnswersArrayList(ArrayList<Answer> answersArrayList) {
        this.answersArrayList = answersArrayList;
    }

    public long getAnswerTimestamp() {
        return answerTimestamp;
    }

    public void setAnswerTimestamp(long answerTimestamp) {
        this.answerTimestamp = answerTimestamp;
    }

    public long getTriggerTimestamp() {
        return triggerTimestamp;
    }

    public void setTriggerTimestamp(long triggerTimestamp) {
        this.triggerTimestamp = triggerTimestamp;
    }

    public String toString(){
        return "ID: " + survey.getSurveyID() + " " + DateHelper.getDateFromTSString(getTriggerTimestamp()) + " " + survey.trigger.toString();
    }

    public String getTriggeredSurveyID(){
        return survey.getSurveyID()+"_"+triggerTimestamp;
    }

//    public void setAnswerTimestamp(Long answerTimestamp) {
//        this.answerTimestamp = answerTimestamp;
//    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public boolean isHasBeenSentToServer() {
        return hasBeenSentToServer;
    }

    public void setHasBeenSentToServer(boolean hasBeenSentToServer) {
        this.hasBeenSentToServer = hasBeenSentToServer;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public AnsweredSurveyToServer getAnsweredSurveyToServer (){

        return new AnsweredSurveyToServer(answerTimestamp, answersArrayList, userid, lang, survey.getSurveyID(), triggerTimestamp, survey.getVersion());

    }

}

package inesc_id.pt.motivandroid.data.surveys.questions;

import java.io.Serializable;
import java.util.HashMap;

import inesc_id.pt.motivandroid.data.surveys.answers.Answer;

/**
 * Question
 *
 * Superclass for classes representing multiple question types.
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

public abstract class Question implements Serializable{


    /**
     *  Hashmap containing the question in multiple languages (if available). Key equals language code,
     * and value corresponds to the question text itself.
     */
    HashMap<String, String> question;

    String questionId;
    boolean deleted;
    boolean showing = false;

    public String languageOfCreation;

    public Question(){

    }


    /**
     * @param preferredLanguage
     * @return question text in the preferred language (if available), else returns the text in
     * the language in which it was created
     */
    public String getQuestion(String preferredLanguage) {

        return question.containsKey(preferredLanguage) ? question.get(preferredLanguage) : question.get(languageOfCreation);

    }


    public void setQuestion(HashMap<String, String> question) {
        this.question = question;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isShowing() {
        return showing;
    }

    public void setShowing(boolean showing) {
        this.showing = showing;
    }

    public abstract int getType();

    public String getLanguageOfCreation() {
        return languageOfCreation;
    }

    public void setLanguageOfCreation(String languageOfCreation) {
        this.languageOfCreation = languageOfCreation;
    }

    public interface keys{
        int shortText = 0;
        int multipleChoice = 1;
        int scale = 2;
        int checkBoxes = 3;
        int paragraph = 4;
        int dropdown = 5;
        int grid = 6;
        int yesOrNo = 7;

    }

    public abstract Answer getEmptyAnswer();

}

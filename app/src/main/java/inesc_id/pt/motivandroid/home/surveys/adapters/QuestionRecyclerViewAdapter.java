package inesc_id.pt.motivandroid.home.surveys.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.surveys.SurveyStateful;
import inesc_id.pt.motivandroid.data.surveys.answers.DropdownAnswer;
import inesc_id.pt.motivandroid.data.surveys.answers.MultipleChoiceAnswer;
import inesc_id.pt.motivandroid.data.surveys.answers.ParagraphAnswer;
import inesc_id.pt.motivandroid.data.surveys.answers.RadioButtonAnswer;
import inesc_id.pt.motivandroid.data.surveys.answers.ShortTextAnswer;
import inesc_id.pt.motivandroid.data.surveys.answers.SliderAnswer;
import inesc_id.pt.motivandroid.data.surveys.answers.YesOrNoAnswer;
import inesc_id.pt.motivandroid.data.surveys.questions.DropdownQuestion;
import inesc_id.pt.motivandroid.data.surveys.questions.MultipleChoiceQuestion;
import inesc_id.pt.motivandroid.data.surveys.questions.Question;
import inesc_id.pt.motivandroid.data.surveys.questions.RadioButtonQuestion;
import inesc_id.pt.motivandroid.data.surveys.questions.SliderQuestion;
import inesc_id.pt.motivandroid.home.SurveyAdapterCallback;


/**
 *
 * QuestionRecyclerViewAdapter
 *
 *   Adapter to list the survey questions, according to the type of question (Question.getType()):
 *    - Question.keys.shortText => Short text type question
 *    - Question.keys.checkBoxes => Checkbox type question
 *    - Question.keys.multipleChoice => Multiple choice type question (radio button answer)
 *    - Question.keys.scale => Scale type question
 *    - Question.keys.dropdown => Dropdown type question
 *    - Question.keys.yesOrNo => Yes or no type question
 *    - Question.keys.paragraph => Paragraph type question
 *
 *  Each question is only "unlocked" when the previous one has been answered. This "shadowing" is done
 *  using and overlay view on top of each question (showOverlay and hideOverlay methods).
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
public class QuestionRecyclerViewAdapter extends
        RecyclerView.Adapter<QuestionRecyclerViewAdapter.ViewHolder>{

    private SurveyStateful survey;
    private Context context;
    private SurveyAdapterCallback callback;

    String language;

    public QuestionRecyclerViewAdapter(SurveyStateful survey
            , Context ctx, SurveyAdapterCallback callback, String language) {
        this.survey = survey;
        this.context = ctx;
        this.callback = callback;

        for(Question question : survey.getSurvey().getQuestions()){
            survey.answersArrayList.add(question.getEmptyAnswer());
        }

        Log.d("question adapter", "answers" + survey.answersArrayList.size());

        this.language = language;
    }

    @Override
    public int getItemViewType(int position) {
        return survey.getSurvey().getQuestions().get(position).getType();
    }

    @Override
    public QuestionRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = null;

        //inflate question according to its type

        switch (viewType) {
            case Question.keys.shortText:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.home_surveys_short_text_answer_layout, viewGroup, false);
                return new ShortTextViewHolder(view);
            case Question.keys.checkBoxes:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.home_surveys_checkbox_answer_layout, viewGroup, false);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // do something here
                    }
                });

                return new MultipleChoiceViewHolder(view);
            case Question.keys.multipleChoice:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.home_surveys_radio_button_answer_layout, viewGroup, false);
                return new RadioButtonViewHolder(view);
            case Question.keys.scale:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.slider_view, viewGroup, false);
                return new SliderViewHolder(view);
            case Question.keys.dropdown:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.dropdown_view, viewGroup, false);
                return new DropdownViewHolder(view);
            case Question.keys.yesOrNo:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.home_surveys_yes_or_no_answer_layout, viewGroup, false);
                return new YesOrNoViewHolder(view);
            case Question.keys.paragraph:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.home_surveys_short_text_answer_layout, viewGroup, false);
                return new ParagraphViewHolder(view);
        }

        return null;


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question item = survey.getSurvey().getQuestions().get(position);
        holder.setIsRecyclable(false);
        holder.bindType(item);
    }

    public SurveyStateful getAnsweredSurveyStateful(){
        return survey;
    }


    public ArrayList<Question> getQuestions(){
        return survey.getSurvey().getQuestions();
    }

    @Override
    public int getItemCount() {
        return survey.getSurvey().getQuestions().size();
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindType(Question item);
    }

    public class ShortTextViewHolder extends ViewHolder {

        public TextView question;
        public EditText answerShortText;
        public Button doneButton;

        //public View overlay;

        public ShortTextViewHolder(View view) {
            super(view);

            Log.d("question", "shorttext");

            setIsRecyclable(false);

            question = (TextView) view.findViewById(R.id.question);
            answerShortText = view.findViewById(R.id.answerEditText);

            doneButton = view.findViewById(R.id.doneAnswerButton);

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(answerShortText.getText().toString().length() == 0){

                        Toast.makeText(context, "Please answer the question", Toast.LENGTH_SHORT).show();


                    }else{

                        ShortTextAnswer answer = (ShortTextAnswer) survey.answersArrayList.get(getAdapterPosition());

                        if(!answer.isHasBeenChanged()){
                            answer.setHasBeenChanged(true);
                            checkAndShowNextQuestion(getAdapterPosition());

                            // add points
                        }
                        answer.setAnswer(answerShortText.getText().toString());
                        survey.answersArrayList.set(getAdapterPosition(), answer);
                        answerShortText.clearFocus();
                    }

                }
            });


        }



        @Override
        public void bindType(Question item) {

            if (!item.isShowing()){

                showOverlay(itemView.findViewById(R.id.overlay));

                question.setText(item.getQuestion(language));
                //question.setText("stuff");
                ShortTextAnswer shortTextAnswer = (ShortTextAnswer) survey.getAnswersArrayList().get(getAdapterPosition());

                if(shortTextAnswer.getAnswer() != null){
                    answerShortText.setText(shortTextAnswer.getAnswer());
                }

                Log.d("questionAdapter", "notshowing");
            }else{
                Log.d("questionAdapter", "showing");
                hideOverlay(itemView.findViewById(R.id.overlay));
            }

        }
    }

    public class ParagraphViewHolder extends ViewHolder {

        public TextView question;
        public EditText answerShortText;
        public Button doneButton;

        //public View overlay;

        public ParagraphViewHolder(View view) {
            super(view);

            Log.d("question", "shorttext");

            setIsRecyclable(false);



            question = (TextView) view.findViewById(R.id.question);
            answerShortText = view.findViewById(R.id.answerEditText);

            doneButton = view.findViewById(R.id.doneAnswerButton);

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(answerShortText.getText().toString().length() == 0){

                        Toast.makeText(context, "Please answer the question", Toast.LENGTH_SHORT).show();


                    }else{

                        ParagraphAnswer answer = (ParagraphAnswer) survey.answersArrayList.get(getAdapterPosition());

                        if(!answer.isHasBeenChanged()){
                            answer.setHasBeenChanged(true);
                            checkAndShowNextQuestion(getAdapterPosition());

                            // add points
                        }
                        answer.setAnswer(answerShortText.getText().toString());
                        survey.answersArrayList.set(getAdapterPosition(), answer);
                        answerShortText.clearFocus();
                    }

                }
            });


        }



        @Override
        public void bindType(Question item) {

//            if (!item.isShowing()){
//                itemView.setAlpha(0.45f);
//                enableDisableView(itemView, false);
//            }else{
//                enableDisableView(itemView, true);
//            }

            if (!item.isShowing()){

                showOverlay(itemView.findViewById(R.id.overlay));

                question.setText(item.getQuestion(language));
                //question.setText("stuff");
                ParagraphAnswer shortTextAnswer = (ParagraphAnswer) survey.getAnswersArrayList().get(getAdapterPosition());

                if(shortTextAnswer.getAnswer() != null){
                    answerShortText.setText(shortTextAnswer.getAnswer());
                }

                Log.d("questionAdapter", "notshowing");
            }else{
                Log.d("questionAdapter", "showing");
                hideOverlay(itemView.findViewById(R.id.overlay));
            }

        }
    }

    public class YesOrNoViewHolder extends ViewHolder {

        public TextView question;
        public Button yesButton;
        public Button noButton;


        public YesOrNoViewHolder(View view) {
            super(view);

            question = (TextView) view.findViewById(R.id.question);

            yesButton = view.findViewById(R.id.yesAnswerButton);
            noButton = view.findViewById(R.id.noAnswerButton);

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    YesOrNoAnswer answer = (YesOrNoAnswer) survey.answersArrayList.get(getAdapterPosition());

                        if(!answer.isHasBeenChanged()){
                            answer.setHasBeenChanged(true);
                            checkAndShowNextQuestion(getAdapterPosition());

                            // add points
                        }
                        answer.setAnswer(true);
                        survey.answersArrayList.set(getAdapterPosition(), answer);
                    }
            });

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    YesOrNoAnswer answer = (YesOrNoAnswer) survey.answersArrayList.get(getAdapterPosition());

                    if(!answer.isHasBeenChanged()){
                        answer.setHasBeenChanged(true);
                        checkAndShowNextQuestion(getAdapterPosition());

                        // add points
                    }
                    answer.setAnswer(false);
                    survey.answersArrayList.set(getAdapterPosition(), answer);
                }
            });


        }



        @Override
        public void bindType(Question item) {

            if (!item.isShowing()){

                question.setText(item.getQuestion("en"));
                showOverlay(itemView.findViewById(R.id.overlay));
                Log.d("questionAdapter", "notshowing");

            }else{

                Log.d("questionAdapter", "showing");
                hideOverlay(itemView.findViewById(R.id.overlay));

            }

        }
    }

    public class MultipleChoiceViewHolder extends ViewHolder {

        public TextView question;
        public LinearLayout answersLinearLayout;

        public Button doneAnsweringButton;

        public Set<Integer> answerSet;

        public MultipleChoiceViewHolder(View view) {
            super(view);


            question = (TextView) view.findViewById(R.id.question);

            answersLinearLayout = (LinearLayout) view.findViewById(R.id.checkboxesAnswersLinearLayout);

            answerSet = new HashSet<Integer>();

            doneAnsweringButton = view.findViewById(R.id.doneAnswerButton);

            doneAnsweringButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                        MultipleChoiceAnswer answer = (MultipleChoiceAnswer) survey.answersArrayList.get(getAdapterPosition());

                        if(!answer.isHasBeenChanged()){
                            answer.setHasBeenChanged(true);
                            checkAndShowNextQuestion(getAdapterPosition());
                            // add points
                        }

                        //convert set to list
                        ArrayList<Integer> mainList = new ArrayList<Integer>();
                        mainList.addAll(answerSet);

                        answer.setAnswer(mainList);
                        survey.answersArrayList.set(getAdapterPosition(), answer);

                }
            });
        }

        @Override
        public void bindType(Question item) {

//            if (!item.isShowing()){
//                itemView.setAlpha(0.45f);
//                enableDisableView(itemView, false);
//            }else{
//                enableDisableView(itemView, true);
//            }

            if (!item.isShowing()){
                showOverlay(itemView.findViewById(R.id.overlay));

                final MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) item;
                ArrayList<String> options = ((MultipleChoiceQuestion) item).getOptions(language, survey.getSurvey().getDefaultLanguage());

                question.setText(item.getQuestion(language));


                for (String option : options){

                    LayoutInflater inflater = LayoutInflater.from(context);
                    final ConstraintLayout row = (ConstraintLayout) inflater.inflate(R.layout.answer_checkboxes_option, null, false);

                    ImageView check = row.findViewById(R.id.checkedImageView);
                    check.setImageResource(R.drawable.checked_checkbox);
//                check.setVisibility(View.VISIBLE);

                    answersLinearLayout.addView(row);
                    CheckBox cb1 = row.findViewById(R.id.optionCheckbox);
                    cb1.setText(option);
//                answerCBArrayList.add(cb1);

                    cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (buttonView.isChecked()) {
                                Log.d("question recycler view", "ischecked "+ buttonView.isChecked());
//                            ConstraintLayout constraintLayoutParent = (ConstraintLayout) buttonView.getParent();
//                            constraintLayoutParent.findViewById(R.id.checkedImageView).setVisibility(View.VISIBLE);
                                row.findViewById(R.id.checkedImageView).setVisibility(View.VISIBLE);

                                answerSet.add(answersLinearLayout.indexOfChild(row));

                            }
                            else
                            {
                                Log.d("question recycler view", "ischecked "+ buttonView.isChecked());

                                answerSet.remove(answersLinearLayout.indexOfChild(row));

                                row.findViewById(R.id.checkedImageView).setVisibility(View.INVISIBLE);

                            }
                        }

                    });

                }

            }else{

                hideOverlay(itemView.findViewById(R.id.overlay));

            }


        }
    }

    public class SliderViewHolder extends ViewHolder {

        public TextView question;
        public SeekBar answer;
        public TextView currentAnswer;
        public TextView minScaleIndicatorTextView;
        public TextView maxScaleIndicatorTextView;
        public TextView doneButton;

//        public RadioGroup priceGroup;
//        public Switch wrongLegSwitch;

        public SliderViewHolder(View view) {
            super(view);

            setIsRecyclable(false);

            question = (TextView) view.findViewById(R.id.question);
            answer = (SeekBar) view.findViewById(R.id.answerSeekBar);
            currentAnswer = (TextView) view.findViewById(R.id.valueSeekBar);

            minScaleIndicatorTextView = view.findViewById(R.id.minScaleIndicatorTextView);
            maxScaleIndicatorTextView = view.findViewById(R.id.maxScaleIndicatorTextView);

            doneButton = view.findViewById(R.id.doneAnswerButton);
        }



        @Override
        public void bindType(Question item) {


            if (!item.isShowing()) {
                showOverlay(itemView.findViewById(R.id.overlay));

                setIsRecyclable(false);

                SliderQuestion sliderQuestion = (SliderQuestion) item;

                question.setText(item.getQuestion(language));


//            final int minRange = sliderQuestion.getMinSliderRange();
//            final int maxRange = sliderQuestion.getMaxSliderRange();

                final int minRange = sliderQuestion.getMinRange();
                final int maxRange = sliderQuestion.getMaxRange();

                Log.d("Log", "minRange " + minRange);
                Log.d("Log", "maxRange " + maxRange);

                answer.setMax(maxRange - minRange);

                minScaleIndicatorTextView.setText(minRange + "");
                maxScaleIndicatorTextView.setText(maxRange + "");

                SliderAnswer sliderAnswer = (SliderAnswer) survey.answersArrayList.get(getAdapterPosition());

                if (sliderAnswer.isBeenChanged()) {
                    answer.setProgress(sliderAnswer.getAnswer() - minRange);
                    currentAnswer.setText("" + sliderAnswer.getAnswer());
                } else {
                    answer.setProgress((maxRange-minRange)/2);
                    sliderAnswer.setAnswer(minRange);
                    sliderAnswer.setBeenChanged(true);
                    currentAnswer.setText("" + (maxRange-minRange)/2);
                    survey.answersArrayList.set(getAdapterPosition(), sliderAnswer);
                }

                answer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progressChanged = minRange;

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressChanged = minRange + progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        progressChanged = minRange + seekBar.getProgress();
                        SliderAnswer answer = (SliderAnswer) survey.answersArrayList.get(getAdapterPosition());
                        answer.setAnswer(progressChanged);
                        answer.setBeenChanged(true);
                        survey.answersArrayList.set(getAdapterPosition(), answer);
                        currentAnswer.setText(progressChanged+"");
                    }
                });


                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        checkAndShowNextQuestion(getAdapterPosition());

                    }
                });

            }else{
                hideOverlay(itemView.findViewById(R.id.overlay));
            }
        }
    }

    public class RadioButtonViewHolder extends ViewHolder {

        public TextView question;
        public LinearLayout answersLinearLayout;

        public ArrayList<RadioButton> answerArrayList = new ArrayList<>();


        public RadioButtonViewHolder(View view) {
            super(view);


            setIsRecyclable(false);

            question = (TextView) view.findViewById(R.id.question);
            answersLinearLayout =  view.findViewById(R.id.radioButtonAnswersLinearLayout);

        }


        @Override
        public void bindType(Question item) {


            //initial rendering
            if (!item.isShowing()) {
                //itemView.setAlpha(0.45f);


                showOverlay(itemView.findViewById(R.id.overlay));

                Log.d("radiobuttonquestion", "not showing");

                RadioButtonQuestion radioButtonQuestion = (RadioButtonQuestion) item;

                question.setText(item.getQuestion(language));

                LayoutInflater inflater = LayoutInflater.from(context);

                for(String option : radioButtonQuestion.getOptions(language, survey.getSurvey().getDefaultLanguage())){

                    ConstraintLayout row = (ConstraintLayout) inflater.inflate(R.layout.answer_mutiple_choice, null, false);
                    answersLinearLayout.addView(row);

                    final RadioButton radioButton = row.findViewById(R.id.radioButton2);
                    radioButton.setText(option);

                    answerArrayList.add(radioButton);

                    radioButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            int selectedIndex = answerArrayList.indexOf((RadioButton) v);
                            Log.d("QuestionRecyclerView", "index chosen " + selectedIndex);
                            ((RadioButton )v).setTypeface(null, Typeface.BOLD);

                            RadioButtonAnswer radioButtonAnswer = (RadioButtonAnswer) survey.getAnswersArrayList().get(getAdapterPosition());

                            int i = 0;
                            for(RadioButton rb : answerArrayList){

                                if(i != selectedIndex){
                                    rb.setChecked(false);
                                    rb.setTypeface(null, Typeface.NORMAL);
                                }
                                i++;
                            }

                            if(!radioButtonAnswer.isHasBeenChanged()){
                                checkAndShowNextQuestion(getAdapterPosition());
                            }
                            radioButtonAnswer.setAnswer(selectedIndex);


                        }
                    });

                }

            //after notifyItemChanged()
            }else{
                itemView.findViewById(R.id.overlay).setVisibility(View.GONE);
                Log.d("radiobuttonquestion", "showing");
            }

            setIsRecyclable(false);

        }
    }


    /**
     * Show overlay on top of the view passed as param
     *
     * @param viewById
     */
    private void showOverlay(View viewById) {
        viewById.setVisibility(View.VISIBLE);
        viewById.findViewById(R.id.overlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void hideOverlay(View viewById) {
        viewById.setVisibility(View.GONE);
    }

    public class DropdownViewHolder extends ViewHolder {

        public TextView question;
        public Spinner answers;


//        public RadioGroup priceGroup;
//        public Switch wrongLegSwitch;

        public DropdownViewHolder(View view) {
            super(view);

            setIsRecyclable(false);

            question = (TextView) view.findViewById(R.id.question);
            answers = (Spinner) view.findViewById(R.id.answers);

        }


        @Override
        public void bindType(Question item) {

            setIsRecyclable(false);

            DropdownQuestion dropdownQuestion = (DropdownQuestion) item;

            question.setText(item.getQuestion(language));

            DropdownAnswer dropdownAnswer = (DropdownAnswer) survey.getAnswersArrayList().get(getAdapterPosition());

            Log.d("dropdown", "getans " + dropdownAnswer.getAnswer());

            ArrayList<String> options = ((DropdownQuestion) item).getOptions(language,survey.getSurvey().getDefaultLanguage());
            ArrayAdapter aa = new ArrayAdapter(context,android.R.layout.simple_spinner_item, options);
            answers.setAdapter(aa);

            answers.setSelection(dropdownAnswer.getAnswer());

                        answers.setOnItemSelectedListener(new
                                                      AdapterView.OnItemSelectedListener() {
                                                          @Override
                                                          public void onItemSelected(AdapterView<?> parent, View view, int
                                                                  position, long id) {
                                                              // On selecting a spinner item

                                                              String item = parent.getItemAtPosition(position).toString();
                                                              Log.d("dropdown", "selected" + position);

                                                              DropdownAnswer dropdownAnswer = (DropdownAnswer) survey.getAnswersArrayList().get(getAdapterPosition());
                                                              ((DropdownAnswer) survey.getAnswersArrayList().get(getAdapterPosition())).setAnswer(position);
                                                          }

                                                          @Override
                                                          public void onNothingSelected(AdapterView<?> parent) {
                                                              // todo for nothing selected
                                                          }
                                                      });

        }
    }

    public void checkAndShowNextQuestion(int currentPosition){

        //questions before the last
        if(getQuestions().size() > currentPosition + 1){
            if(!getQuestions().get(currentPosition+1).isShowing()) {
                getQuestions().get(currentPosition + 1).setShowing(true);
                notifyItemChanged(currentPosition+1);
//                notifyDataSetChanged();
                callback.updateProgessBar(currentPosition+1);
            }
        }else{
            //last question
            //unlock confirm all button
            callback.enableConfirmAllButton();
            callback.updateProgessBar(currentPosition+1);
        }

    }

    public void beginSurvey(){
        survey.getSurvey().getQuestions().get(0).setShowing(true);
        notifyItemChanged(0);


//        notifyDataSetChanged();
    }

}

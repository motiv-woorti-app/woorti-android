package inesc_id.pt.motivandroid.tripStateMachine;

import android.content.Context;
import android.content.res.AssetManager;

import org.dmg.pmml.FieldName;
import org.joda.time.DateTime;
import org.jpmml.android.EvaluatorUtil;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.ModelField;
import org.jpmml.evaluator.OutputField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.tripStateMachine.dataML.MLAlgorithmInput;
import inesc_id.pt.motivandroid.tripStateMachine.dataML.MLInputMetadata;
import kotlin.jvm.Synchronized;

import static org.joda.time.DateTimeZone.UTC;

/**
 *  Classifier
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
public class Classifier {

    private static final Logger LOG = LoggerFactory.getLogger(Classifier.class.getSimpleName());


    private static Classifier instance = null;
    private static AssetManager assetManager = null;

    private Evaluator evaluator;
    private static String evaluatorType;


    private Classifier() {
//        evaluatorType = "randomForest.pmml.ser";
        loadEvaluator(evaluatorType);
    }

    public static void initClassifier(Context context, String evaluatorFileName){
        assetManager = context.getAssets();
        evaluatorType = evaluatorFileName;
    }

    public static Classifier getInstance(){
        if (Classifier.instance == null){
            Classifier.instance = new Classifier();
        }
        return Classifier.instance;
    }


    private Evaluator createEvaluator(String name) throws Exception {
//        AssetManager assetManager = getAssets();
        try(InputStream is = assetManager.open(name)){
            return EvaluatorUtil.createEvaluator(is);
        }
    }

    static private List<FieldName> getNames(List<? extends ModelField> modelFields){
        List<FieldName> names = new ArrayList<>(modelFields.size());

        for(ModelField modelField : modelFields){
            FieldName name = modelField.getName();

            names.add(name);
        }

        return names;
    }



    private void loadEvaluator(String evaluatorFileName){
        LOG.debug( "Importing evaluator " + evaluatorFileName +  "at " + new DateTime(UTC).getMillis());
        try {
            evaluator = createEvaluator(evaluatorFileName);
            evaluator.verify();
        } catch(Exception e){
            throw new RuntimeException(e);
        }

        LOG.debug( "Evaluator imported" +  "at " + new DateTime(UTC).getMillis());
    }

    public List<FieldName> getActiveFields(){
        return getNames(evaluator.getActiveFields());
    }

    public List<FieldName> getTargetFields(){
        return getNames(evaluator.getTargetFields());
    }

    public List<FieldName> getOutputFields(){
        return getNames(evaluator.getOutputFields());
    }


    public HashMap<Integer, Double> getEvaluatorResult(double[] values){

        Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();

        List<InputField> inputFields = evaluator.getInputFields();
        for(InputField inputField : inputFields){
            FieldName inputFieldName = inputField.getName();

            // The raw (ie. user-supplied) value could be any Java primitive value
            Object rawValue = getValueByInput(inputFieldName, values);

            // The raw value is passed through: 1) outlier treatment, 2) missing value treatment, 3) invalid value treatment and 4) type conversion
            FieldValue inputFieldValue = inputField.prepare(rawValue);

            arguments.put(inputFieldName, inputFieldValue);
        }

        Map<FieldName, ?> results = evaluator.evaluate(arguments);

        List<OutputField> outputFields = evaluator.getOutputFields();
        HashMap<Integer, Double> predicts = new HashMap<>();
        for(OutputField outputField : outputFields){
            FieldName outputFieldName = outputField.getName();
            Object outputFieldValue = results.get(outputFieldName);

            Integer mode = Integer.valueOf(outputField.getOutputField().getValue());
            Double prob = (Double) outputFieldValue;
            predicts.put(mode, prob);
            LOG.error( "Field: " + mode + "; Res: " + prob);
        }
        return predicts;
    }


    public MLInputMetadata evaluateSegment(MLAlgorithmInput mlAlgorithmInput){

        HashMap<Integer, Double> predicts = new HashMap<>();

        if((mlAlgorithmInput.getProcessedPoints().getAvgSpeed() <= keys.STILL_AVG_SPEED_FILTER)
                && (mlAlgorithmInput.getAccelsBelowFilter() >= keys.STILL_ACCELS_BELOW_FILTER)){

            List<OutputField> outputFields = evaluator.getOutputFields();
            for(OutputField outputField : outputFields){
                Integer mode = Integer.valueOf(outputField.getOutputField().getValue());
                Double prob = 0.0;
                predicts.put(mode, prob);
            }
            predicts.put(ActivityDetected.keys.still, 1.0);

        }else{
            predicts = getEvaluatorResult(mlAlgorithmInput);
            predicts.put(ActivityDetected.keys.still, 0.0);
        }

        MLInputMetadata result = new MLInputMetadata(mlAlgorithmInput, predicts);
        LOG.debug( "Segment Evaluated!!" + result.getBestMode().getKey());
        return result;
    }

    private HashMap<Integer, Double> getEvaluatorResult(MLAlgorithmInput input){

        Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();

        List<InputField> inputFields = evaluator.getInputFields();
        for(InputField inputField : inputFields){
            FieldName inputFieldName = inputField.getName();

            // The raw (ie. user-supplied) value could be any Java primitive value
            Object rawValue = getValueByInput(inputFieldName, input);

            // The raw value is passed through: 1) outlier treatment, 2) missing value treatment, 3) invalid value treatment and 4) type conversion
            FieldValue inputFieldValue = inputField.prepare(rawValue);

            arguments.put(inputFieldName, inputFieldValue);
        }

        Map<FieldName, ?> results = evaluator.evaluate(arguments);

        List<OutputField> outputFields = evaluator.getOutputFields();
        HashMap<Integer, Double> predicts = new HashMap<>();
        for(OutputField outputField : outputFields){
            FieldName outputFieldName = outputField.getName();
            Object outputFieldValue = results.get(outputFieldName);

            Integer mode = Integer.valueOf(outputField.getOutputField().getValue());
            Double prob = (Double) outputFieldValue;
            predicts.put(mode, prob);
            LOG.debug( "Field: " + mode + "; Res: " + prob);
        }
        return predicts;
    }


    private Object getValueByInput(FieldName inputFieldName, MLAlgorithmInput input) {
        // Log.d("---InputFiled:", inputFieldName.getValue());
        switch (inputFieldName.getValue()) {
            case "estimatedSpeed":
                return input.getProcessedPoints().getEstimatedSpeed();
            case "OS":
                return input.getOSVersion();
            case "accelsBelowFilter":
                return input.getAccelsBelowFilter();
            case "accelBetw_03_06":
                return input.getProcessedAccelerations().getBetween_03_06();
            case "accelBetw_06_1":
                return input.getProcessedAccelerations().getBetween_06_1();
            case "accelBetw_1_3":
                return input.getProcessedAccelerations().getBetween_1_3();
            case "accelBetw_3_6":
                return input.getProcessedAccelerations().getBetween_3_6();
            case "accelAbove_6":
                return input.getProcessedAccelerations().getAbove_6();
            case "avgFilteredAccel":
                return input.getAvgFilteredAccels();
            case "avgAccel":
                return input.getProcessedAccelerations().getAvgAccel();
            case "maxAccel":
                return input.getProcessedAccelerations().getMaxAccel();
            case "minAccel":
                return input.getProcessedAccelerations().getMinAccel();
            case "stdDevAccel":
                return input.getProcessedAccelerations().getStdDevAccel();
            case "avgSpeed":
                return input.getProcessedPoints().getAvgSpeed();
            case "maxSpeed":
                return input.getProcessedPoints().getMaxSpeed();
            case "minSpeed":
                return input.getProcessedPoints().getMinSpeed();
            case "stdDevSpeed":
                return input.getProcessedPoints().getStdDevSpeed();
            case "avgAcc":
                return input.getProcessedPoints().getAvgAcc();
            case "maxAcc":
                return input.getProcessedPoints().getMaxAcc();
            case "minAcc":
                return input.getProcessedPoints().getMinAcc();
            case "stdDevAcc":
                return input.getProcessedPoints().getStdDevAcc();
            case "gpsTimeMean":
                return input.getProcessedPoints().getGpsTimeMean();
            case "distance":
                return input.getProcessedPoints().getDistance();
        }
        throw new RuntimeException("Unknown input field: " + inputFieldName.getValue());
    }



    // Tests only:
    private Object getValueByInput(FieldName inputFieldName, double[] values) {
        // Log.d("---InputFiled:", inputFieldName.getValue());
        switch (inputFieldName.getValue()) {
            case "estimatedSpeed":
                return values[0];
            case "OS":
                return values[1];
            case "accelsBelowFilter":
                return values[2];
            case "avgFilteredAccel":
                return values[3];
            case "avgAccel":
                return values[4];
            case "maxAccel":
                return values[5];
            case "minAccel":
                return values[6];
            case "stdDevAccel":
                return values[7];
            case "avgSpeed":
                return values[8];
            case "maxSpeed":
                return values[9];
            case "minSpeed":
                return values[10];
            case "stdDevSpeed":
                return values[11];
            case "avgAcc":
                return values[12];
            case "maxAcc":
                return values[13];
            case "minAcc":
                return values[13];
            case "stdDevAcc":
                return values[13];
            case "gpsTimeMean":
                return values[13];
            case "distance":
                return values[13];
        }
        throw new RuntimeException("Unknown input field: " + inputFieldName.getValue());
    }

    public interface keys{

        double STILL_AVG_SPEED_FILTER = 2.5;
        double STILL_ACCELS_BELOW_FILTER = 0.8;

    }
}

package inesc_id.pt.motivandroid.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.rewards.RewardStatus;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigest;
import inesc_id.pt.motivandroid.data.surveys.Survey;
import inesc_id.pt.motivandroid.data.surveys.SurveyStateful;
import inesc_id.pt.motivandroid.tripStateMachine.dataML.MLInputMetadata;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.ToStringSample;
import inesc_id.pt.motivandroid.data.tripData.AccelerationData;
import inesc_id.pt.motivandroid.data.ActivityDataContainer;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.json.JSONUtils;

import static org.joda.time.DateTimeZone.UTC;

/**
 * PersistentTripStorage
 *
 * Class responsible for managing (reading or writing) persistent data to disk.
 * All data is saved to an SQLITE db except for raw trip data (too large to be kept in the database)
 * which is written directly to the file system
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

public class PersistentTripStorage {

    private TripStorageDBHelper tripStorageDBHelper;

    Context context;

    private static PersistentTripStorage instance;

    public static synchronized PersistentTripStorage getInstance(Context context) {

        if (instance != null){
            instance = new PersistentTripStorage(context);
        }

        return instance;
    }

    public PersistentTripStorage(Context context) {
        this.context = context.getApplicationContext();
        tripStorageDBHelper = new TripStorageDBHelper(context, true);

    }


    public ArrayList<Survey> getAllSurveysObject() {

        ArrayList<Survey> resultSet = new ArrayList<>();

        try(Cursor cursor = tripStorageDBHelper.getAllSurveyData()) {
            while (cursor.moveToNext()) {

                Gson gson = JSONUtils.getInstance().getSurveyGSON();
                Survey result = gson.fromJson(cursor.getString(2), Survey.class);
                resultSet.add(result);
            }
        }

        this.tripStorageDBHelper.close();

        return  resultSet;
    }

    public boolean checkIfSurveyExistsObject(String surveyIDToCheck){


        try(Cursor cursor = tripStorageDBHelper.getSurveyData(surveyIDToCheck)) {
            while (cursor.moveToNext()) {
                Log.e("PersistentTripStorage", "Survey Exists already");
                return true;
            }
        }

        this.tripStorageDBHelper.close();

        return false;

    }

    public boolean insertSurveyObject(Survey survey) {

        Gson gson = JSONUtils.getInstance().getSurveyGSON();
        String json = gson.toJson(survey);

        int surveyID = survey.getSurveyID();

        tripStorageDBHelper.insertSurvey(json, surveyID+"");

        /*String rep = ToStringSample.objectToString(fullTrip);
        tripStorageDBHelper.insertFullTrip(rep);*/

        this.tripStorageDBHelper.close();

        return true;
    }

    public boolean updateSurveyDataObject(Survey survey){

        Gson gson = JSONUtils.getInstance().getSurveyGSON();
        String surveyInJson = gson.toJson(survey, Survey.class);

        tripStorageDBHelper.updateSurveyData(surveyInJson, survey.getSurveyID()+"");

        this.tripStorageDBHelper.close();

        return true;
    }

    public boolean deleteSurveyDataObject(Survey survey){

        tripStorageDBHelper.deleteSurvey(survey.getSurveyID()+"");

        this.tripStorageDBHelper.close();

        return true;
    }

    public ArrayList<SurveyStateful> getAllTriggeredSurveysObject() {

        ArrayList<SurveyStateful> resultSet = new ArrayList<>();

        try(Cursor cursor = tripStorageDBHelper.getAllTriggeredSurveyData()) {
            while (cursor.moveToNext()) {

                Gson gson = JSONUtils.getInstance().getSurveyGSON();
                SurveyStateful result = gson.fromJson(cursor.getString(2), SurveyStateful.class);
                resultSet.add(result);
            }
        }

        this.tripStorageDBHelper.close();

        return  resultSet;
    }

    public ArrayList<SurveyStateful> getTriggeredSurveysByIDObject(String surveyID) {

        ArrayList<SurveyStateful> resultSet = new ArrayList<>();

        try(Cursor cursor = tripStorageDBHelper.getAllTriggeredSurveyDataByID(surveyID)) {
            while (cursor.moveToNext()) {

                Gson gson = JSONUtils.getInstance().getSurveyGSON();
                SurveyStateful result = gson.fromJson(cursor.getString(2), SurveyStateful.class);
                resultSet.add(result);
            }
        }

        this.tripStorageDBHelper.close();

        return  resultSet;
    }

    public boolean insertSurveyTriggeredObject(SurveyStateful triggeredSurvey) {

        Gson gson = JSONUtils.getInstance().getSurveyGSON();
        String json = gson.toJson(triggeredSurvey);

        tripStorageDBHelper.insertTriggeredSurvey(json, triggeredSurvey.getSurvey().getSurveyID()+"", triggeredSurvey.getTriggerTimestamp());

        /*String rep = ToStringSample.objectToString(fullTrip);
        tripStorageDBHelper.insertFullTrip(rep);*/

        return true;
    }

    public boolean deleteTriggeredSurveyDataObject(SurveyStateful surveyStateful){

        tripStorageDBHelper.deleteTriggeredSurvey(surveyStateful.getSurvey().getSurveyID()+"", surveyStateful.getTriggerTimestamp());
        this.tripStorageDBHelper.close();

        return true;
    }

    public boolean updateTriggeredSurveyDataObject(SurveyStateful survey){

        Gson gson = JSONUtils.getInstance().getSurveyGSON();
        String surveyInJson = gson.toJson(survey, SurveyStateful.class);

        tripStorageDBHelper.updateTriggeredSurveyData(surveyInJson, survey.getSurvey().getSurveyID()+"", survey.getTriggerTimestamp());

        this.tripStorageDBHelper.close();

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////// Reward storage helper

    public ArrayList<RewardStatus> getRewardStatusesByUIDObject(String UID) {

        ArrayList<RewardStatus> resultSet = new ArrayList<>();

        try(Cursor cursor = tripStorageDBHelper.getRewardStatusDataByUID(UID)) {
            while (cursor.moveToNext()) {

                Gson gson = JSONUtils.getInstance().getSurveyGSON();
                RewardStatus result = gson.fromJson(cursor.getString(4), RewardStatus.class);
                resultSet.add(result);
            }
        }

        this.tripStorageDBHelper.close();

        return  resultSet;
    }

    public synchronized boolean insertOrReplaceRewardStatusObject(RewardStatus rewardStatus, String UID) {

        Gson gson = JSONUtils.getInstance().getSurveyGSON();
        String json = gson.toJson(rewardStatus);

        tripStorageDBHelper.insertOrReplaceRewardStatus(json, UID, rewardStatus.isHasBeenSentToServer(), rewardStatus.getRewardID());

        /*String rep = ToStringSample.objectToString(fullTrip);
        tripStorageDBHelper.insertFullTrip(rep);*/

        return true;
    }



    public boolean updateRewardStatusObject(RewardStatus rewardStatus, String UID){

        Gson gson = JSONUtils.getInstance().getSurveyGSON();
        String json = gson.toJson(rewardStatus);

        tripStorageDBHelper.updateRewardStatus(json, UID, rewardStatus.isHasBeenSentToServer(), rewardStatus.getRewardID());

        return true;

    }

    public boolean deleteRewardStatusesDataObject(RewardStatus rewardStatus, String UID){

        tripStorageDBHelper.deleteRewardStatus(rewardStatus.getRewardID(), UID);
        this.tripStorageDBHelper.close();

        return true;
    }

    public ArrayList<RewardStatus> getRewardStatusesNotSentToServer(String UID){

        ArrayList<RewardStatus> resultSet = new ArrayList<>();

        try(Cursor cursor = tripStorageDBHelper.getNotSentRewardStatusDataByUID(UID)) {
            while (cursor.moveToNext()) {

                Gson gson = JSONUtils.getInstance().getSurveyGSON();
                RewardStatus result = gson.fromJson(cursor.getString(4), RewardStatus.class);
                resultSet.add(result);
            }
        }

        this.tripStorageDBHelper.close();

        return  resultSet;

    }

    //returns null if not exists
    public RewardStatus getRewardStatus(String UID, String rewardID){

        RewardStatus result = null;

        try(Cursor cursor = tripStorageDBHelper.getRewardStatusData(rewardID,UID)) {
            while (cursor.moveToNext()) {

                Gson gson = JSONUtils.getInstance().getSurveyGSON();
                result = gson.fromJson(cursor.getString(4), RewardStatus.class);
            }
        }

        this.tripStorageDBHelper.close();

        return result;

    }

    public void deleteAllRewardsObject(){

        tripStorageDBHelper.deleteAllRewardEntries();

    }

//    public boolean updateTriggeredSurveyDataObject(SurveyStateful survey){
//
//        Gson gson = JSONUtils.getInstance().getSurveyGSON();
//        String surveyInJson = gson.toJson(survey, SurveyStateful.class);
//
//        tripStorageDBHelper.updateTriggeredSurveyData(surveyInJson, survey.getSurvey().getSurveyID()+"", survey.getTriggerTimestamp());
//
//        this.tripStorageDBHelper.close();
//
//        return true;
//    }


    public ArrayList<FullTrip> getAllFullTripsObject() {

        //see https://stackoverflow.com/questions/10723770/whats-the-best-way-to-iterate-an-android-cursor

        ArrayList<FullTrip> resultSet = new ArrayList<>();

        try(Cursor cursor = tripStorageDBHelper.getAllFullTrips()) {
            while (cursor.moveToNext()) {


                try {
                    Gson gson = JSONUtils.getInstance().getGson();
                    FullTrip result = gson.fromJson(cursor.getString(1), FullTrip.class);
                    resultSet.add(result);
                }catch(Exception e){
                    Crashlytics.log(Log.DEBUG,"Persistence",e.getMessage());

                }
                /*FullTrip result = (FullTrip) ToStringSample.stringToObject(cursor.getString(1));
                resultSet.add(result);*/

            }
        }catch (Exception e){
            Crashlytics.log(Log.DEBUG,"Persistence resource",e.getMessage());

        }

        tripStorageDBHelper.close();

        return  resultSet;
    }






    public ArrayList<String> getAllFullTripsDates() {

        //see https://stackoverflow.com/questions/10723770/whats-the-best-way-to-iterate-an-android-cursor

        //final Cursor cursor = tripStorageDBHelper.getFullTripDates();

        ArrayList<String> resultSet = new ArrayList<>();

        try(Cursor cursor = tripStorageDBHelper.getFullTripDates()) {
            while (cursor.moveToNext()) {

                String result = cursor.getString(0);
                resultSet.add(result);

                /*FullTrip result = (FullTrip) ToStringSample.stringToObject(cursor.getString(1));
                resultSet.add(result);*/

            }
        }

        this.tripStorageDBHelper.close();

        return  resultSet;
    }

    public FullTrip getFullTripByDate(String date) {

        //see https://stackoverflow.com/questions/10723770/whats-the-best-way-to-iterate-an-android-cursor

        FullTrip resultSet = null;

//        try (Cursor cursor = tripStorageDBHelper.getFullTripByDate(date)) {
//
//                cursor.moveToFirst();
//
//                Gson gson = JSONUtils.getInstance().getGson();
//                resultSet = gson.fromJson(cursor.getString(1), FullTrip.class);
//
//            }

        //read from object directly
//        long currentTS = new DateTime(UTC).getMillis();
//        Log.d("Persistence", "Started reading at: " + currentTS );
//        resultSet = readFullTripFromDisk(date);
//        Log.d("Persistence", "Stopped reading - Time Elapsed= " + (new DateTime(UTC).getMillis() - currentTS));

        //read from json
        long currentTS = new DateTime(UTC).getMillis();
        Log.d("Persistence", "Started reading at: " + currentTS );
        resultSet = readFullTripFromDiskWithJSON(date);
        Log.d("Persistence", "Stopped reading - Time Elapsed= " + (new DateTime(UTC).getMillis() - currentTS));

        this.tripStorageDBHelper.close();

        return  resultSet;
    }

    public boolean deleteFullTripByDate(String tripID){

        tripStorageDBHelper.deleteFullTripData(tripID);
        tripStorageDBHelper.deleteFullTripDigestData(tripID);

        this.tripStorageDBHelper.close();

        return true;

    }

    public boolean updateFullTripDataObject(FullTrip fullTrip, String date){

//        Gson gson = JSONUtils.getInstance().getGson();
//        String fullTripInJson = gson.toJson(fullTrip, FullTrip.class);

//        tripStorageDBHelper.updateFullTripData(fullTripInJson, date);

//        Log.e("Persistence","initaddress" +  fullTrip.getFullTripDigest().getStartAddress());
//        Log.e("Persistence","finaladdress" +  fullTrip.getFullTripDigest().getFinalAddress());
//
//        Log.e("Persistence", fullTrip.getDateId());
//        Log.d("Persistence", fullTrip.getFullTripDigest().getTripID());

//        updateFullTripDigestDataObject(fullTripDigest, fullTripDigest.getTripID());


        //write object in json to disk
        writeFullTripToDiskWithJSON(fullTrip);

        Gson gson = JSONUtils.getInstance().getGson();

        String fullTripDigestInJson = gson.toJson(fullTrip.getFullTripDigest(), FullTripDigest.class);

        tripStorageDBHelper.updateFullTripDigestData(fullTripDigestInJson, date);

        this.tripStorageDBHelper.close();

        //write object directly to disk
//        writeFullTripToDisk(fullTrip);
//
//        Gson gson = JSONUtils.getInstance().getGson();
//        String fullTripDigestInJson = gson.toJson(fullTrip.getFullTripDigest(), FullTripDigest.class);
//
//        tripStorageDBHelper.updateFullTripDigestData(fullTripDigestInJson, date);

        return true;
    }



    public boolean insertFullTripObject(FullTrip fullTrip) {

        /*Gson gson = new Gson();
        String json = gson.toJson(fullTrip);

        tripStorageDBHelper.insertFullTrip(json);*/

//        Gson gson = JSONUtils.getInstance().getGson();
//        String json = gson.toJson(fullTrip);

//        String userID = fullTrip.getUserID();
//        Long initTimestamp = fullTrip.getInitTimestamp();

//        tripStorageDBHelper.insertFullTrip(json, userID, initTimestamp);


        // write object to disk directly
//        long currentTS = new DateTime(UTC).getMillis();
//        Log.d("Persistence", "Started writing at: " + currentTS );
//        writeFullTripToDisk(fullTrip);
//        Log.d("Persistence", "Stopped writing - Time Elapsed= " + (new DateTime(UTC).getMillis() - currentTS));
//
//        insertFullTripDigestObject(fullTrip.getFullTripDigest());


        //write in json

        long currentTS = new DateTime(UTC).getMillis();
        Log.d("Persistence", "Started writing at: " + currentTS );
        writeFullTripToDiskWithJSON(fullTrip);
        Log.d("Persistence", "Stopped writing - Time Elapsed= " + (new DateTime(UTC).getMillis() - currentTS));

        insertFullTripDigestObject(fullTrip.getFullTripDigest());

        this.tripStorageDBHelper.close();

        return true;
    }


    @Deprecated
    public boolean writeFullTripToDisk(FullTrip fullTrip){

//        Gson gson = JSONUtils.getInstance().getGson();
//
//        try (OutputStream fileOut = context.openFileOutput(fullTrip.getDateId(), Context.MODE_PRIVATE);
//             OutputStream bufferedOut = new BufferedOutputStream(fileOut);
//             Writer writer = new OutputStreamWriter(bufferedOut)) {
//             gson.toJson(fullTrip, FullTrip.class, writer);
//
//
//
////            String s = ToStringSample.objectToString(fullTrip);
////            writer.write(s);
//
//
//
//             return true;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }

        File tripDataDir = new File(context.getFilesDir().getPath() + "/tripData");

        if(!tripDataDir.exists()) {
            tripDataDir.mkdir();
        }

        try {
            String filePath = context.getFilesDir().getPath() + "/tripData/" + fullTrip.getDateId();

            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            // write object to file
            oos.writeObject(fullTrip);
            System.out.println("Done");
            // closing resources
            oos.close();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean writeFullTripToDiskWithJSON(FullTrip fullTrip){

//        Gson gson = JSONUtils.getInstance().getGson();
//
//        try (OutputStream fileOut = context.openFileOutput(fullTrip.getDateId(), Context.MODE_PRIVATE);
//             OutputStream bufferedOut = new BufferedOutputStream(fileOut);
//             Writer writer = new OutputStreamWriter(bufferedOut)) {
//             gson.toJson(fullTrip, FullTrip.class, writer);
//
//
//
////            String s = ToStringSample.objectToString(fullTrip);
////            writer.write(s);
//
//
//
//             return true;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }

        try {

            Gson gson = JSONUtils.getInstance().getGson();
            String fulltripjson = gson.toJson(fullTrip, FullTrip.class);


            String filePath = context.getFilesDir().getPath() + "/" + fullTrip.getDateId();

            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            // write object to file



            oos.writeObject(fulltripjson);



            System.out.println("Done");
            // closing resources
            oos.close();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Deprecated
    public FullTrip readFullTripFromDisk(String tripID){

//        Gson gson = JSONUtils.getInstance().getGson();
//
//        try (InputStream fileIn = context.openFileInput(tripID);
//             BufferedInputStream bufferedIn = new BufferedInputStream(fileIn);
//             Reader reader = new InputStreamReader(bufferedIn, StandardCharsets.UTF_8)) {
//             FullTrip result = gson.fromJson(reader, FullTrip.class);
//
////            FullTrip result = (FullTrip) ToStringSample.stringToObject(reader.toString());
////            resultSet.add(result);
//
//             return result;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }

        String filePath = context.getFilesDir().getPath()  + "/tripData/" + tripID;

        FileInputStream is = null;
        try {
            is = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(is);
            FullTrip emp = (FullTrip) ois.readObject();
            ois.close();
            is.close();

            return emp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public FullTrip readFullTripFromDiskWithJSON(String tripID){

//        Gson gson = JSONUtils.getInstance().getGson();
//
//        try (InputStream fileIn = context.openFileInput(tripID);
//             BufferedInputStream bufferedIn = new BufferedInputStream(fileIn);
//             Reader reader = new InputStreamReader(bufferedIn, StandardCharsets.UTF_8)) {
//             FullTrip result = gson.fromJson(reader, FullTrip.class);
//
////            FullTrip result = (FullTrip) ToStringSample.stringToObject(reader.toString());
////            resultSet.add(result);
//
//             return result;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }

        String filePath = context.getFilesDir().getPath()  + "/" + tripID;

        FileInputStream is = null;
        try {
            is = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(is);
            String emp = (String) ois.readObject();
            ois.close();
            is.close();

            Gson gson = JSONUtils.getInstance().getGson();
            FullTrip fullTrip = gson.fromJson(emp, FullTrip.class);

            return fullTrip;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void readFullTripsErasedAndRecreateDigest(){

//        Gson gson = JSONUtils.getInstance().getGson();
//
//        try (InputStream fileIn = context.openFileInput(tripID);
//             BufferedInputStream bufferedIn = new BufferedInputStream(fileIn);
//             Reader reader = new InputStreamReader(bufferedIn, StandardCharsets.UTF_8)) {
//             FullTrip result = gson.fromJson(reader, FullTrip.class);
//
////            FullTrip result = (FullTrip) ToStringSample.stringToObject(reader.toString());
////            resultSet.add(result);
//
//             return result;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }

        String filePath = context.getFilesDir().getPath()  + "/";

        File[] files = new File(filePath).listFiles();

        for (File file : files){

            FileInputStream is = null;

            try {

                Log.e("PersistentTripStorage", "Trying to recreate trip " + file.getPath());

                is = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(is);
                String emp = (String) ois.readObject();
                ois.close();
                is.close();

                Gson gson = JSONUtils.getInstance().getGson();
                FullTrip fullTrip = gson.fromJson(emp, FullTrip.class);

                String digest = gson.toJson(fullTrip.getFullTripDigest(), FullTripDigest.class);

                tripStorageDBHelper.insertFullTripDigest(digest, FirebaseAuth.getInstance().getUid(), fullTrip.getFullTripDigest().getTripID());


            } catch (Exception e) {
                e.printStackTrace();
            }

        }



    }


    public boolean insertLocationObject(LocationDataContainer location) {

        Gson gson = new Gson();
        String json = gson.toJson(location);
        tripStorageDBHelper.insertLocation(json);

        this.tripStorageDBHelper.close();

        return true;
    }

    public ArrayList<LocationDataContainer> getAllLocationObjects() {

        ArrayList<LocationDataContainer> resultSet = new ArrayList<>();

        Gson gson = new Gson();

        try(Cursor cursor = tripStorageDBHelper.getAllSavedLocations()) {
            while (cursor.moveToNext()) {
                //LocationDataContainer result = (LocationDataContainer) (cursor.getString(1));
                LocationDataContainer result = gson.fromJson((cursor.getString(1)),LocationDataContainer.class);
                resultSet.add(result);
            }
        }

        this.tripStorageDBHelper.close();

        return  resultSet;
    }

    public boolean dropSavedLocations() {

        tripStorageDBHelper.dropAllLocationRecords();

        this.tripStorageDBHelper.close();

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Full trip digest

    public boolean updateFullTripDigestDataObject(FullTripDigest fullTrip, String date){

        Gson gson = JSONUtils.getInstance().getGson();
        String fullTripDigestInJson = gson.toJson(fullTrip, FullTripDigest.class);

        tripStorageDBHelper.updateFullTripDigestData(fullTripDigestInJson, date);

        this.tripStorageDBHelper.close();

        return true;
    }

//    public ArrayList<FullTripDigest> getAllFullTripDigestsObjects() {
//
//        //see https://stackoverflow.com/questions/10723770/whats-the-best-way-to-iterate-an-android-cursor
//
//        ArrayList<FullTripDigest> resultSet = new ArrayList<>();
//
//        try(Cursor cursor = tripStorageDBHelper.getAllFullTripDigests()) {
//            while (cursor.moveToNext()) {
//
//
//                try {
//                    Gson gson = JSONUtils.getInstance().getGson();
//                    FullTripDigest result = gson.fromJson(cursor.getString(1), FullTripDigest.class);
//                    resultSet.add(result);
//                }catch(Exception e){
//                    Crashlytics.log(Log.DEBUG,"Persistence",e.getMessage());
//                }
//                /*FullTrip result = (FullTrip) ToStringSample.stringToObject(cursor.getString(1));
//                resultSet.add(result);*/
//
//            }
//        }catch (Exception e){
//            Crashlytics.log(Log.DEBUG,"Persistence resource",e.getMessage());
//
//        }
//
//        this.tripStorageDBHelper.close();
//
//        return  resultSet;
//    }

    public ArrayList<FullTripDigest> getAllFullTripDigestsByUserIDTimeIntervalObjects(long initTimestamp, String uid) {

        //see https://stackoverflow.com/questions/10723770/whats-the-best-way-to-iterate-an-android-cursor

        ArrayList<FullTripDigest> resultSet = new ArrayList<>();

        try(Cursor cursor = tripStorageDBHelper.getFullTripDigestsByTimeIntervalAndUID(initTimestamp,uid)) {
            while (cursor.moveToNext()) {


                try {
                    Gson gson = JSONUtils.getInstance().getGson();
                    FullTripDigest result = gson.fromJson(cursor.getString(1), FullTripDigest.class);
                    resultSet.add(result);
                }catch(Exception e){
                    Crashlytics.log(Log.DEBUG,"Persistence",e.getMessage());
                }
                /*FullTrip result = (FullTrip) ToStringSample.stringToObject(cursor.getString(1));
                resultSet.add(result);*/

            }
        }catch (Exception e){
            Crashlytics.log(Log.DEBUG,"Persistence resource",e.getMessage());

        }

        this.tripStorageDBHelper.close();

        return  resultSet;
    }

    public ArrayList<FullTripDigest> getAllFullTripDigestsByUserIDObjects(String uid) {

        //see https://stackoverflow.com/questions/10723770/whats-the-best-way-to-iterate-an-android-cursor

        ArrayList<FullTripDigest> resultSet = new ArrayList<>();

        try(Cursor cursor = tripStorageDBHelper.getAllFullTripDigestsByUID(uid)) {
            while (cursor.moveToNext()) {


                try {
                    Gson gson = JSONUtils.getInstance().getGson();
                    FullTripDigest result = gson.fromJson(cursor.getString(1), FullTripDigest.class);
                    resultSet.add(result);
                }catch(Exception e){
                    Crashlytics.log(Log.DEBUG,"Persistence",e.getMessage());
                }
                /*FullTrip result = (FullTrip) ToStringSample.stringToObject(cursor.getString(1));
                resultSet.add(result);*/

            }
        }catch (Exception e){
            Crashlytics.log(Log.DEBUG,"Persistence resource",e.getMessage());

        }

        this.tripStorageDBHelper.close();

        return  resultSet;
    }

    public ArrayList<FullTripDigest> getAllFullTripDigestsForTimeIntervalObjects(long initTimespanTimestamp) {

        //see https://stackoverflow.com/questions/10723770/whats-the-best-way-to-iterate-an-android-cursor

        ArrayList<FullTripDigest> resultSet = new ArrayList<>();

        try(Cursor cursor = tripStorageDBHelper.getFullTripDigestsByTimeInterval(initTimespanTimestamp)) {
            while (cursor.moveToNext()) {

                try {
                    Gson gson = JSONUtils.getInstance().getGson();
                    FullTripDigest result = gson.fromJson(cursor.getString(1), FullTripDigest.class);
                    resultSet.add(result);
                }catch(Exception e){
                    Crashlytics.log(Log.DEBUG,"Persistence",e.getMessage());
                }
                /*FullTrip result = (FullTrip) ToStringSample.stringToObject(cursor.getString(1));
                resultSet.add(result);*/

            }
        }catch (Exception e){
            Crashlytics.log(Log.DEBUG,"Persistence resource",e.getMessage());

        }

        tripStorageDBHelper.close();

        return  resultSet;
    }

    public int unsendAllFullTripDigestsFrom25October() {

        //see https://stackoverflow.com/questions/10723770/whats-the-best-way-to-iterate-an-android-cursor

        long october25TS = 1571961600000l;

        ArrayList<FullTripDigest> resultSet = new ArrayList<>();

        try(Cursor cursor = tripStorageDBHelper.getFullTripDigestsByTimeInterval(october25TS)) {
            while (cursor.moveToNext()) {

                try {
                    Gson gson = JSONUtils.getInstance().getGson();
                    FullTripDigest result = gson.fromJson(cursor.getString(1), FullTripDigest.class);
                    resultSet.add(result);
                }catch(Exception e){
                    Crashlytics.log(Log.DEBUG,"Persistence",e.getMessage());
                }
                /*FullTrip result = (FullTrip) ToStringSample.stringToObject(cursor.getString(1));
                resultSet.add(result);*/

            }
        }catch (Exception e){
            Crashlytics.log(Log.DEBUG,"Persistence resource",e.getMessage());

        }

        tripStorageDBHelper.close();


        Crashlytics.log(Log.DEBUG,"Persistence", "Trying to unsend "+ resultSet.size() + " trip digests");

        for (FullTripDigest digest : resultSet){
            digest.setSentToServer(false);
            updateFullTripDigestDataObject(digest,digest.getTripID());
        }


        return resultSet.size();
    }

    public boolean insertFullTripDigestObject(FullTripDigest fullTripDigest) {

        /*Gson gson = new Gson();
        String json = gson.toJson(fullTrip);

        tripStorageDBHelper.insertFullTrip(json);*/

        Gson gson = JSONUtils.getInstance().getGson();
        String json = gson.toJson(fullTripDigest);

        String userID = fullTripDigest.getUserID();
        Long initTimestamp = fullTripDigest.getInitTimestamp();
        String tripID = initTimestamp + "";

        tripStorageDBHelper.insertFullTripDigest(json, userID, tripID);

        /*String rep = ToStringSample.objectToString(fullTrip);
        tripStorageDBHelper.insertFullTrip(rep);*/

        this.tripStorageDBHelper.close();

        return true;
    }

    public boolean deleteFullTripDigestObject(String dateID) {

        /*Gson gson = new Gson();
        String json = gson.toJson(fullTrip);

        tripStorageDBHelper.insertFullTrip(json);*/

        tripStorageDBHelper.deleteFullTripDigestData(dateID);

        /*String rep = ToStringSample.objectToString(fullTrip);
        tripStorageDBHelper.insertFullTrip(rep);*/



        return true;
    }

    //////////////////////////////////////////
    ////////////////ML
    //////////////////////////////////////////

    public boolean insertMLInputObject(MLInputMetadata mlInputMetadata) {

        Gson gson = new Gson();
        String json = gson.toJson(mlInputMetadata);
        tripStorageDBHelper.insertMLInputMetadata(json);

        this.tripStorageDBHelper.close();

        return true;
    }

    public ArrayList<MLInputMetadata> getAllMLInputObjects() {

        ArrayList<MLInputMetadata> resultSet = new ArrayList<>();

        Gson gson = new Gson();

        try(Cursor cursor = tripStorageDBHelper.getAllMLInputMetadata()) {
            while (cursor.moveToNext()) {
                //LocationDataContainer result = (LocationDataContainer) (cursor.getString(1));
                MLInputMetadata result = gson.fromJson((cursor.getString(1)),MLInputMetadata.class);
                resultSet.add(result);
            }
        }

        this.tripStorageDBHelper.close();

        return  resultSet;
    }

    public boolean dropAllMLInputObjects() {

        tripStorageDBHelper.dropAllMLInputMetadata();
        return true;
    }

    //////////////////////////////////////////
    ////////////////ML
    //////////////////////////////////////////



    public boolean insertActivityObject(ActivityDataContainer activityDataContainer) {

        Gson gson = new Gson();
        String json = gson.toJson(activityDataContainer);
        tripStorageDBHelper.insertActivity(json);

        this.tripStorageDBHelper.close();

        return true;
    }

    public ArrayList<ActivityDataContainer> getAllActivityObjects() {

        ArrayList<ActivityDataContainer> resultSet = new ArrayList<>();
        Gson gson = new Gson();


        try(Cursor cursor = tripStorageDBHelper.getAllSavedActivities()){
            while (cursor.moveToNext()) {
                ActivityDataContainer result = gson.fromJson((cursor.getString(1)),ActivityDataContainer.class);
                resultSet.add(result);
            }
        }

        this.tripStorageDBHelper.close();

        return  resultSet;
    }

    public boolean dropSavedActivities() {

        tripStorageDBHelper.dropAllActivityRecords();
        return true;
    }

    public boolean insertTripPart(FullTripPart fullTripPart) {

        String rep = ToStringSample.objectToString(fullTripPart);
        tripStorageDBHelper.insertFullTripPart(rep);

        this.tripStorageDBHelper.close();

        return true;
    }

    public boolean insertTripPartList(ArrayList<FullTripPart> fullTripPartArrayList){

        ArrayList<String> temp = new ArrayList<>();

        for(FullTripPart ftp :fullTripPartArrayList){
            temp.add(ToStringSample.objectToString(ftp));
        }

        tripStorageDBHelper.insertFullTripParts(temp);

        this.tripStorageDBHelper.close();

        return true;
    }

    public ArrayList<FullTripPart> getAllSavedFullTripPartObjects() {

        ArrayList<FullTripPart> resultSet = new ArrayList<>();

        try(Cursor cursor = tripStorageDBHelper.getAllFullTripParts()) {
            if(cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    FullTripPart result = (FullTripPart) ToStringSample.stringToObject(cursor.getString(1));
                    resultSet.add(result);
                }
            }
        }

        this.tripStorageDBHelper.close();

        return  resultSet;
    }

    public boolean dropAllSavedFullTripParts() {

        tripStorageDBHelper.dropAllFullTripParts();

        this.tripStorageDBHelper.close();
        return true;
    }

    ///////////
    //ACCELERATIONS

    public boolean insertAccelerationListObjects(ArrayList<AccelerationData> accelerationsArrayList){

        ArrayList<String> temp = new ArrayList<>();

        Gson gson = JSONUtils.getInstance().getGson();

        for(AccelerationData ad : accelerationsArrayList){

            temp.add(gson.toJson(ad));
        }

        tripStorageDBHelper.insertAccelerationValues(temp);
        this.tripStorageDBHelper.close();
        return true;
    }

    public ArrayList<AccelerationData> getAllSavedAccelerationObjects() {

        ArrayList<AccelerationData> resultSet = new ArrayList<>();

        Gson gson = JSONUtils.getInstance().getGson();

        try(Cursor cursor = tripStorageDBHelper.getAllAccelerationValues()) {
            while (cursor.moveToNext()) {
                AccelerationData result = (AccelerationData) gson.fromJson(cursor.getString(1), AccelerationData.class);
                resultSet.add(result);
            }
        }
        this.tripStorageDBHelper.close();
        return  resultSet;
    }

    public boolean dropAllSavedAccelerationObjects() {

        tripStorageDBHelper.dropAllAccelerationValues();

        this.tripStorageDBHelper.close();
        return true;
    }



    //see https://www.androidauthority.com/use-sqlite-store-data-app-599743/
    private class TripStorageDBHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "TripLocalDB.db";
        private static final int DATABASE_VERSION = 44;

        //full trip storage constants
        public static final String TRIP_TABLE_NAME = "FullTripsTable";
        public static final String TRIP_COLUMN_ID = "_id";
        public static final String TRIP_COLUMN_DATA = "data";
        public static final String TRIP_COLUMN_USERID = "uid";
        public static final String TRIP_COLUMN_INITDATE = "initDate";

        public static final String TRIP_DIGEST_TABLE_NAME = "FullTripsDigestTable";
        public static final String TRIP_DIGEST_COLUMN_ID = "_id";
        public static final String TRIP_DIGEST_COLUMN_DATA = "data";
        public static final String TRIP_DIGEST_COLUMN_USERID = "uid";
        public static final String TRIP_DIGEST_COLUMN_INITDATE = "initDate";

        //snapshot data constants

            //last locations saved
        public static final String SAVED_LOCATIONS_TABLE_NAME = "SavedLocationsTable";
        public static final String SAVED_LOCATIONS_COLUMN_ID = "_id";
        public static final String SAVED_LOCATIONS_COLUMN_DATA = "location";

        //last activity data saved
        public static final String SAVED_ACTIVITIES_TABLE_NAME = "SavedActivitiesTable";
        public static final String SAVED_ACTIVITIES_COLUMN_ID = "_id";
        public static final String SAVED_ACTIVITIES_COLUMN_DATA = "data";

            //temp full trip parts
        public static final String SNAPSHOTS_TRIPPARTS_TABLE_NAME="SnapshotFullTripPartsTable";
        public static final String SNAPSHOTS_TRIPPARTS_COLUMN_ID="_id";
        public static final String SNAPSHOTS_TRIPPARTS_COLUMN_DATA="fulltrippartdata";

        //temp last acceleration values
        public static final String SNAPSHOTS_ACCELERATIONS_TABLE_NAME="SnapshotAccelerationsTable";
        public static final String SNAPSHOTS_ACCELERATIONS_COLUMN_ID="_id";
        public static final String SNAPSHOTS_ACCELERATIONS_COLUMN_DATA="accelerationvalue";

        //ML input data
        public static final String SNAPSHOTS_ML_INPUT_METADATA_TABLE_NAME = "MLInputMetadata";
        public static final String SNAPSHOTS_ML_INPUT_METADATA_COLUMN_ID  = "_id";
        public static final String SNAPSHOTS_ML_INPUT_METADATA_COLUMN_DATA = "data";

        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        ///// Survey data

        public static final String SURVEYS_TABLE_NAME="SurveysTable";
        public static final String SURVEYS_COLUMN_ID="_id";
        public static final String SURVEYS_COLUMN_SURVEY_ID="SurveyID";
        public static final String SURVEYS_COLUMN_DATA="SurveyData";


        public static final String TRIGGERED_SURVEYS_TABLE_NAME="TriggeredSurveysTable";
        public static final String TRIGGERED_SURVEYS_COLUMN_ID="_id";
        public static final String TRIGGERED_SURVEYS_COLUMN_DATA="TriggeredSurveyData";
        //surveyid+triggertimestamp
        public static final String TRIGGERED_SURVEYS_COLUMN_SURVEY_ID="TriggeredSurveyID";


        //

        public static final String REWARD_STATUSES_TABLE_NAME="RewardStatusesTable";
        public static final String REWARD_STATUSES_COLUMN_ID="_id";
        public static final String REWARD_STATUSES_REWARD_ID="RewardID";
        public static final String REWARD_STATUSES_COLUMN_DATA="RewardStatusData";
        public static final String REWARD_STATUSES_UID_COLUMN="RewardStatusUID";
        public static final String REWARD_STATUSES_HAS_BEEN_SENT_COLUMN="RewardStatusHasBeenSent";


        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        ///// Log data

        public static final String LOG_TABLE_NAME="LogTable";
        public static final String LOG_COLUMN_ID="_id";
        public static final String LOG_COLUMN_DATA="LogData";
        //surveyid+triggertimestamp
        public static final String LOG_TIMESTAMP="LogTimestamp";

        boolean walModeEnabled;

        public TripStorageDBHelper(Context context, boolean gWalMode) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            //setIdleConnectionTimeout(5000);

            walModeEnabled = gWalMode;

//            if(walModeEnabled) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    setWriteAheadLoggingEnabled(true);
//                    Log.d("sqlLiteHelper", "Setting WAL");
//                }
//            }
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            Log.e("sqlite","oncreate");

            db.execSQL("CREATE TABLE " + TRIP_TABLE_NAME + "(" + TRIP_COLUMN_ID + " INTEGER PRIMARY KEY, " + TRIP_COLUMN_DATA + " TEXT, " + TRIP_COLUMN_INITDATE + " TEXT UNIQUE, " + TRIP_COLUMN_USERID + " TEXT)"
            );

            db.execSQL("CREATE TABLE " + TRIP_DIGEST_TABLE_NAME + "(" + TRIP_DIGEST_COLUMN_ID + " INTEGER PRIMARY KEY, " + TRIP_DIGEST_COLUMN_DATA + " TEXT, " + TRIP_DIGEST_COLUMN_INITDATE + " TEXT UNIQUE, " + TRIP_DIGEST_COLUMN_USERID + " TEXT)"
            );

            db.execSQL("CREATE TABLE " + SAVED_LOCATIONS_TABLE_NAME + "(" +
                    SAVED_LOCATIONS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    SAVED_LOCATIONS_COLUMN_DATA + " TEXT)"
            );

            db.execSQL("CREATE TABLE " + SNAPSHOTS_TRIPPARTS_TABLE_NAME + "(" +
                    SNAPSHOTS_TRIPPARTS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    SNAPSHOTS_TRIPPARTS_COLUMN_DATA + " TEXT)"
            );

            db.execSQL("CREATE TABLE " + SAVED_ACTIVITIES_TABLE_NAME + "(" +
                    SAVED_ACTIVITIES_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    SAVED_ACTIVITIES_COLUMN_DATA + " TEXT)"
            );

            db.execSQL("CREATE TABLE " + SNAPSHOTS_ACCELERATIONS_TABLE_NAME + "(" +
                    SNAPSHOTS_ACCELERATIONS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    SNAPSHOTS_ACCELERATIONS_COLUMN_DATA+ " TEXT)"
            );

            db.execSQL("CREATE TABLE " + SURVEYS_TABLE_NAME + "(" + SURVEYS_COLUMN_ID + " INTEGER PRIMARY KEY, " + SURVEYS_COLUMN_SURVEY_ID+ " TEXT, " + SURVEYS_COLUMN_DATA + " TEXT)");

            db.execSQL("CREATE TABLE " + TRIGGERED_SURVEYS_TABLE_NAME + "(" + TRIGGERED_SURVEYS_COLUMN_ID + " INTEGER PRIMARY KEY, " + TRIGGERED_SURVEYS_COLUMN_SURVEY_ID + " TEXT, " + TRIGGERED_SURVEYS_COLUMN_DATA + " TEXT)");


            //ML
            db.execSQL("CREATE TABLE " + SNAPSHOTS_ML_INPUT_METADATA_TABLE_NAME + "(" +
                    SNAPSHOTS_ML_INPUT_METADATA_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    SNAPSHOTS_ML_INPUT_METADATA_COLUMN_DATA + " TEXT)"
            );

            db.execSQL("CREATE TABLE " + REWARD_STATUSES_TABLE_NAME + "(" + REWARD_STATUSES_COLUMN_ID + ", " + REWARD_STATUSES_UID_COLUMN + " TEXT, " +  REWARD_STATUSES_HAS_BEEN_SENT_COLUMN + " BOOLEAN, "+  REWARD_STATUSES_REWARD_ID + " BOOLEAN,"+ REWARD_STATUSES_COLUMN_DATA + " TEXT ," + " PRIMARY KEY (" + REWARD_STATUSES_UID_COLUMN + "," + REWARD_STATUSES_REWARD_ID +"))");

//            db.execSQL("CREATE TABLE " + REWARD_STATUSES_TABLE_NAME + "(" + REWARD_STATUSES_COLUMN_ID + " INTEGER PRIMARY KEY, " + REWARD_STATUSES_UID_COLUMN + " TEXT, " +  REWARD_STATUSES_HAS_BEEN_SENT_COLUMN + " BOOLEAN, "+  REWARD_STATUSES_REWARD_ID + " BOOLEAN,"+ REWARD_STATUSES_COLUMN_DATA + " TEXT)");

//            db.execSQL("CREATE TABLE " + LOG_TABLE_NAME + "(" +
//                    LOG_COLUMN_ID + " INTEGER PRIMARY KEY, " +
//                    LOG_TIMESTAMP + " INTEGER, " +
//                    LOG_COLUMN_DATA + " TEXT)"
//            );

        }

        void deleteRecursive(File fileOrDirectory) {

//            if (fileOrDirectory.isDirectory()) {
//                Log.e("Persistence", "isDirectory" + fileOrDirectory.isDirectory());
//                for (File child : fileOrDirectory.listFiles()) {
//                    Log.e("Persistence", "child " + child);
//                    deleteRecursive(child);
//                }
//            }


            if (fileOrDirectory.isDirectory())
            {
                String[] children = fileOrDirectory.list();

                if(children != null){

                    for (int i = 0; i < children.length; i++)
                    {
                        new File(fileOrDirectory, children[i]).delete();
                    }

                }
            }

        }

        // full trip storage methods
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



            Log.e("Persistence", "old " + oldVersion + " new " + newVersion);

//            if(oldVersion<39 && (newVersion >= 40)){
//
//
//                db.execSQL("CREATE TABLE " + REWARD_STATUSES_TABLE_NAME + "(" + REWARD_STATUSES_COLUMN_ID + ", " + REWARD_STATUSES_UID_COLUMN + " TEXT, " +  REWARD_STATUSES_HAS_BEEN_SENT_COLUMN + " BOOLEAN, "+  REWARD_STATUSES_REWARD_ID + " BOOLEAN,"+ REWARD_STATUSES_COLUMN_DATA + " TEXT ," + " PRIMARY KEY (" + REWARD_STATUSES_UID_COLUMN + "," + REWARD_STATUSES_REWARD_ID +"))");
//
//
////            db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE_NAME);
//
//
//            }else if (oldVersion == 39 && (newVersion == 40)){
//
//                db.execSQL("DROP TABLE IF EXISTS " + REWARD_STATUSES_TABLE_NAME);
//                db.execSQL("CREATE TABLE " + REWARD_STATUSES_TABLE_NAME + "(" + REWARD_STATUSES_COLUMN_ID + ", " + REWARD_STATUSES_UID_COLUMN + " TEXT, " +  REWARD_STATUSES_HAS_BEEN_SENT_COLUMN + " BOOLEAN, "+  REWARD_STATUSES_REWARD_ID + " BOOLEAN,"+ REWARD_STATUSES_COLUMN_DATA + " TEXT ," + " PRIMARY KEY (" + REWARD_STATUSES_UID_COLUMN + "," + REWARD_STATUSES_REWARD_ID +"))");
//
//
//            }


            if((oldVersion) < 41 && (newVersion > 43)){
                db.execSQL("DROP TABLE IF EXISTS " + REWARD_STATUSES_TABLE_NAME);
                db.execSQL("CREATE TABLE " + REWARD_STATUSES_TABLE_NAME + "(" + REWARD_STATUSES_COLUMN_ID + ", " + REWARD_STATUSES_UID_COLUMN + " TEXT, " +  REWARD_STATUSES_HAS_BEEN_SENT_COLUMN + " BOOLEAN, "+  REWARD_STATUSES_REWARD_ID + " BOOLEAN,"+ REWARD_STATUSES_COLUMN_DATA + " TEXT ," + " PRIMARY KEY (" + REWARD_STATUSES_UID_COLUMN + "," + REWARD_STATUSES_REWARD_ID +"))");
            }else{
                db.execSQL("CREATE TABLE IF NOT EXISTS " + REWARD_STATUSES_TABLE_NAME + "(" + REWARD_STATUSES_COLUMN_ID + ", " + REWARD_STATUSES_UID_COLUMN + " TEXT, " +  REWARD_STATUSES_HAS_BEEN_SENT_COLUMN + " BOOLEAN, "+  REWARD_STATUSES_REWARD_ID + " BOOLEAN,"+ REWARD_STATUSES_COLUMN_DATA + " TEXT ," + " PRIMARY KEY (" + REWARD_STATUSES_UID_COLUMN + "," + REWARD_STATUSES_REWARD_ID +"))");
            }



//            if(newVersion == 43){
//                unsendAllFullTripDigestsFrom25October();
//                Crashlytics.log(Log.DEBUG,"Persistence", "On upgrade: Unsend all trips prior to 25 october");
//
//
//            }


//            else{
//
//                db.execSQL("DROP TABLE IF EXISTS " + TRIP_TABLE_NAME);
//                db.execSQL("DROP TABLE IF EXISTS " + TRIP_DIGEST_TABLE_NAME);
//                db.execSQL("DROP TABLE IF EXISTS " + SAVED_LOCATIONS_TABLE_NAME);
//                db.execSQL("DROP TABLE IF EXISTS " + SAVED_ACTIVITIES_TABLE_NAME);
//                db.execSQL("DROP TABLE IF EXISTS " + SNAPSHOTS_TRIPPARTS_TABLE_NAME);
//                db.execSQL("DROP TABLE IF EXISTS " + SNAPSHOTS_ACCELERATIONS_TABLE_NAME);
//                db.execSQL("DROP TABLE IF EXISTS " + SURVEYS_TABLE_NAME);
//                db.execSQL("DROP TABLE IF EXISTS " + TRIGGERED_SURVEYS_TABLE_NAME);
//                db.execSQL("DROP TABLE IF EXISTS " + SNAPSHOTS_ML_INPUT_METADATA_TABLE_NAME);
//                db.execSQL("DROP TABLE IF EXISTS " + REWARD_STATUSES_TABLE_NAME);
//
//                onCreate(db);
//
//            }
        }


//        public boolean insertFullTrip(String data, String userID, Long initTimestamp) {
//            SQLiteDatabase db = getWritableDatabase();
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(TRIP_COLUMN_DATA, data);
//            contentValues.put(TRIP_COLUMN_USERID, userID);
//            contentValues.put(TRIP_COLUMN_INITDATE, DateHelper.getDateFromTSString(initTimestamp));
//            db.insert(TRIP_TABLE_NAME, null, contentValues);
//
//            db.close();
//
//            return true;
//        }
//
//        public boolean updateFullTripData(String data, String initDate){
//
//            SQLiteDatabase db = getWritableDatabase();
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(TRIP_COLUMN_DATA, data);
//
//            db.update(TRIP_TABLE_NAME, contentValues, TRIP_COLUMN_INITDATE + " = ?", new String[]{initDate});
//
//            db.close();
//
//            return true;
//
//        }

        public boolean deleteFullTripData(String initDate){

            SQLiteDatabase db = getWritableDatabase();
            db.delete(TRIP_TABLE_NAME, TRIP_COLUMN_INITDATE + " = ?", new String[]{initDate});

            db.close();

            return true;

        }

        public Cursor getFullTrip(int id) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + TRIP_TABLE_NAME + " WHERE " + TRIP_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
            return res;
        }

        public Cursor getFullTripByDate(String date) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + TRIP_TABLE_NAME + " WHERE " + TRIP_COLUMN_INITDATE + "=?" + " LIMIT 1", new String[]{date});



            return res;
        }

        public Cursor getFullTripDates(){
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT initDate FROM " + TRIP_TABLE_NAME,null);

            return res;
        }

        public Cursor getAllFullTrips() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + TRIP_TABLE_NAME, null);

            return res;
        }

        public void dropAllFullTrips(){
            SQLiteDatabase db = this.getReadableDatabase();
            db.delete(TRIP_TABLE_NAME,null,null);

            db.close();

        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Full trip digest

        public boolean insertFullTripDigest(String data, String userID, String tripID) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TRIP_DIGEST_COLUMN_DATA, data);
            contentValues.put(TRIP_DIGEST_COLUMN_USERID, userID);
            contentValues.put(TRIP_DIGEST_COLUMN_INITDATE, tripID);
            db.insert(TRIP_DIGEST_TABLE_NAME, null, contentValues);

            db.close();

            return true;
        }

        public boolean updateFullTripDigestData(String data, String initDate){

            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TRIP_DIGEST_COLUMN_DATA, data);

            db.update(TRIP_DIGEST_TABLE_NAME, contentValues, TRIP_DIGEST_COLUMN_INITDATE + " = ?", new String[]{initDate});

            db.close();

            return true;

        }

        public boolean deleteFullTripDigestData(String initDate){

            SQLiteDatabase db = getWritableDatabase();
            db.delete(TRIP_DIGEST_TABLE_NAME, TRIP_DIGEST_COLUMN_INITDATE + " = ?", new String[]{initDate});

            db.close();

            return true;

        }

        public Cursor getFullTripDigestByDate(String date) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + TRIP_DIGEST_TABLE_NAME + " WHERE " + TRIP_DIGEST_COLUMN_INITDATE + "=?" + " LIMIT 1", new String[]{date});

            return res;
        }


        public Cursor getAllFullTripDigests() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + TRIP_DIGEST_TABLE_NAME, null);

            return res;
        }

        public Cursor getAllFullTripDigestsByUID(String uid) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + TRIP_DIGEST_TABLE_NAME + " WHERE " + TRIP_DIGEST_COLUMN_USERID + "=?" , new String[]{uid});

            return res;
        }



        public Cursor getFullTripDigestsByTimeInterval(long initIntervalTimestamp) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + TRIP_DIGEST_TABLE_NAME + " WHERE " + TRIP_DIGEST_COLUMN_INITDATE + ">?", new String[]{""+initIntervalTimestamp});

            return res;
        }

        public Cursor getFullTripDigestsByTimeIntervalAndUID(long initIntervalTimestamp, String uid) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + TRIP_DIGEST_TABLE_NAME + " WHERE " + TRIP_DIGEST_COLUMN_INITDATE + ">?" + " AND " + TRIP_DIGEST_COLUMN_USERID  + "=?" , new String[]{""+initIntervalTimestamp, uid});

            return res;
        }


//        public void dropAllFullTripDigests(){
//            SQLiteDatabase db = this.getReadableDatabase();
//            db.delete(TRIP_DIGEST_TABLE_NAME,null,null);
//
//            db.close();
//
//        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        //snapshot data

        public boolean insertFullTripPart(String data) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(SNAPSHOTS_TRIPPARTS_COLUMN_DATA, data);
            db.insert(SNAPSHOTS_TRIPPARTS_TABLE_NAME, null, contentValues);

            db.close();

            return true;
        }

        public boolean insertFullTripParts(ArrayList<String> data) {
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            try{
                for(String tripPart : data){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(SNAPSHOTS_TRIPPARTS_COLUMN_DATA, tripPart);
                    db.insert(SNAPSHOTS_TRIPPARTS_TABLE_NAME, null, contentValues);
                }
                db.setTransactionSuccessful();
            }finally{
                db.endTransaction();

            }

            db.close();

            return true;
        }

        /*public Cursor getFullTripPart(int id) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + SNAPSHOTS_TRIPPARTS_TABLE_NAME + " WHERE " + SNAPSHOTS_TRIPPARTS_COLUMN_ID + "=", new String[]{Integer.toString(id)});
            return res;
        }*/

        public Cursor getAllFullTripParts() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + SNAPSHOTS_TRIPPARTS_TABLE_NAME, null);

            return res;
        }

        public void dropAllFullTripParts(){
            SQLiteDatabase db = this.getReadableDatabase();
            db.delete(SNAPSHOTS_TRIPPARTS_TABLE_NAME,null,null);

            db.close();
        }

        // snapshot db methods
        public boolean insertLocation(String data) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(SAVED_LOCATIONS_COLUMN_DATA, data);
            db.insert(SAVED_LOCATIONS_TABLE_NAME, null, contentValues);

            db.close();

            return true;
        }

        public Cursor getAllSavedLocations() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + SAVED_LOCATIONS_TABLE_NAME, null);


            return res;
        }

        public void dropAllLocationRecords(){
            SQLiteDatabase db = this.getReadableDatabase();
            db.delete(SAVED_LOCATIONS_TABLE_NAME,null,null);

            db.close();

        }

        public Cursor getAllSavedActivities() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + SAVED_ACTIVITIES_TABLE_NAME, null);


            return res;
        }

        public void dropAllActivityRecords(){
            SQLiteDatabase db = this.getReadableDatabase();
            db.delete(SAVED_ACTIVITIES_TABLE_NAME,null,null);

            db.close();
        }

        public boolean insertActivity(String data) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(SAVED_ACTIVITIES_COLUMN_DATA, data);
            db.insert(SAVED_ACTIVITIES_TABLE_NAME, null, contentValues);

            db.close();

            return true;
        }



        // snapshot data constants
        public boolean insertTripPart(String data, String tripId) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(SNAPSHOTS_TRIPPARTS_COLUMN_DATA, data);
            db.insert(SNAPSHOTS_TRIPPARTS_COLUMN_DATA, null, contentValues);


            return true;
        }

        public Cursor getAllTripParts() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + SNAPSHOTS_TRIPPARTS_TABLE_NAME, null);


            return res;
        }

        public void dropAllTripParts(){
            SQLiteDatabase db = this.getReadableDatabase();
            db.delete(SNAPSHOTS_TRIPPARTS_TABLE_NAME,null,null);

        }


        public boolean insertAccelerationValues(ArrayList<String> accelerationData) {
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            try{
                for(String accelerationValue : accelerationData){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(SNAPSHOTS_ACCELERATIONS_COLUMN_DATA, accelerationValue);
                    db.insert(SNAPSHOTS_ACCELERATIONS_TABLE_NAME, null, contentValues);
                }
                db.setTransactionSuccessful();
            }finally{
                db.endTransaction();
            }

            db.close();

            return true;
        }

        public Cursor getAllAccelerationValues() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + SNAPSHOTS_ACCELERATIONS_TABLE_NAME, null);


            return res;
        }

        public void dropAllAccelerationValues(){
            SQLiteDatabase db = this.getReadableDatabase();
            db.delete(SNAPSHOTS_ACCELERATIONS_TABLE_NAME,null,null);

            db.close();
        }

        public boolean insertMLInputMetadata(String data) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(SNAPSHOTS_ML_INPUT_METADATA_COLUMN_DATA, data);
            db.insert(SNAPSHOTS_ML_INPUT_METADATA_TABLE_NAME, null, contentValues);

            db.close();

            return true;
        }

        public Cursor getAllMLInputMetadata() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + SNAPSHOTS_ML_INPUT_METADATA_TABLE_NAME, null);

            return res;
        }

        public void dropAllMLInputMetadata(){
            SQLiteDatabase db = this.getReadableDatabase();
            db.delete(SNAPSHOTS_ML_INPUT_METADATA_TABLE_NAME,null,null);

            db.close();

        }


        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        ///// Survey storage methods

        //survey storage methods
        public boolean insertSurvey(String data, String surveyID) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(SURVEYS_COLUMN_DATA, data);
            contentValues.put(SURVEYS_COLUMN_SURVEY_ID, surveyID);

            db.insert(SURVEYS_TABLE_NAME, null, contentValues);

            db.close();

            return true;
        }

        public boolean updateSurveyData(String data, String surveyID){

            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(SURVEYS_COLUMN_DATA, data);

            db.update(SURVEYS_TABLE_NAME, contentValues, SURVEYS_COLUMN_SURVEY_ID + " = ?", new String[]{surveyID});

            db.close();

            return true;

        }

        public boolean deleteSurvey(String surveyID){
            SQLiteDatabase db = getWritableDatabase();
            db.delete(SURVEYS_TABLE_NAME, SURVEYS_COLUMN_SURVEY_ID + " = ?", new String[]{surveyID});

            db.close();

            return true;
        }

        public Cursor getSurveyData(String surveyID) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + SURVEYS_TABLE_NAME + " WHERE " + SURVEYS_COLUMN_SURVEY_ID + "=?" + " LIMIT 1", new String[]{surveyID});


            return res;
        }

        public Cursor getAllSurveyData() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + SURVEYS_TABLE_NAME, null);


            return res;
        }



        //triggered surveys
        public boolean insertTriggeredSurvey(String data, String surveyID, long triggerDate) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TRIGGERED_SURVEYS_COLUMN_DATA, data);
            contentValues.put(TRIGGERED_SURVEYS_COLUMN_SURVEY_ID, surveyID+"_"+triggerDate);

            db.insert(TRIGGERED_SURVEYS_TABLE_NAME, null, contentValues);

            db.close();

            return true;
        }

        public boolean updateTriggeredSurveyData(String data, String surveyID, long triggerDate){

            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TRIGGERED_SURVEYS_COLUMN_DATA, data);

            db.update(TRIGGERED_SURVEYS_TABLE_NAME, contentValues, TRIGGERED_SURVEYS_COLUMN_SURVEY_ID + " = ?", new String[]{surveyID+"_"+triggerDate});

            db.close();

            return true;

        }

        public boolean deleteTriggeredSurvey(String surveyID, long triggerDate ){
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TRIGGERED_SURVEYS_TABLE_NAME, TRIGGERED_SURVEYS_COLUMN_SURVEY_ID + " = ?", new String[]{surveyID+"_"+triggerDate});


            return true;
        }

        public Cursor getSurveyData(String surveyID, long triggerDate) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + TRIGGERED_SURVEYS_TABLE_NAME + " WHERE " + TRIGGERED_SURVEYS_COLUMN_SURVEY_ID + "=?" + " LIMIT 1", new String[]{surveyID + "_"+ triggerDate});


            return res;
        }

        public Cursor getAllTriggeredSurveyData() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + TRIGGERED_SURVEYS_TABLE_NAME, null);


            return res;
        }

        public Cursor getAllTriggeredSurveyDataByID(String surveyID) {

            String[] selectionArgs = { surveyID+ "_" + "%"};

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + TRIGGERED_SURVEYS_TABLE_NAME + " WHERE " + TRIGGERED_SURVEYS_COLUMN_SURVEY_ID + " like ?", selectionArgs);


            return res;
        }

        public Cursor getStatefulSurveyDataByID(String surveyID) {

            String[] selectionArgs = { surveyID+ "_" + "%"};

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + TRIGGERED_SURVEYS_TABLE_NAME + " WHERE " + TRIGGERED_SURVEYS_COLUMN_SURVEY_ID + " like ?" + " LIMIT 1", selectionArgs);


            return res;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        ///// Reward Status storage methods

        //Reward storage methods
        public boolean insertRewardStatus(String rewardStatus, String uid, boolean hasBeenSentToServer, String rewardID) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(REWARD_STATUSES_COLUMN_DATA, rewardStatus);
            contentValues.put(REWARD_STATUSES_HAS_BEEN_SENT_COLUMN, hasBeenSentToServer);
            contentValues.put(REWARD_STATUSES_REWARD_ID, rewardID);
            contentValues.put(REWARD_STATUSES_UID_COLUMN, uid);

            db.insert(REWARD_STATUSES_TABLE_NAME, null, contentValues);

            db.close();

            return true;
        }

        public boolean updateRewardStatus(String rewardStatus, String uid, boolean hasBeenSentToServer, String rewardID){

            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(REWARD_STATUSES_COLUMN_DATA, rewardStatus);
            contentValues.put(REWARD_STATUSES_HAS_BEEN_SENT_COLUMN, hasBeenSentToServer);
            contentValues.put(REWARD_STATUSES_REWARD_ID, rewardID);
            contentValues.put(REWARD_STATUSES_UID_COLUMN, uid);

            db.update(REWARD_STATUSES_TABLE_NAME, contentValues, REWARD_STATUSES_REWARD_ID + " = ? AND " + REWARD_STATUSES_UID_COLUMN + " = ? " , new String[]{rewardID, uid});

            db.close();

            return true;

        }

        public boolean insertOrReplaceRewardStatus(String rewardStatus, String uid, boolean hasBeenSentToServer, String rewardID){

            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(REWARD_STATUSES_COLUMN_DATA, rewardStatus);
            contentValues.put(REWARD_STATUSES_HAS_BEEN_SENT_COLUMN, hasBeenSentToServer);
            contentValues.put(REWARD_STATUSES_REWARD_ID, rewardID);
            contentValues.put(REWARD_STATUSES_UID_COLUMN, uid);

            db.replace(REWARD_STATUSES_TABLE_NAME, null, contentValues);

            db.close();

            return true;

        }

        public boolean deleteRewardStatus(String rewardID, String UID){
            SQLiteDatabase db = getWritableDatabase();
            db.delete(REWARD_STATUSES_TABLE_NAME, REWARD_STATUSES_REWARD_ID + " = ? AND " + REWARD_STATUSES_UID_COLUMN + " = ? " , new String[]{rewardID, UID});

            db.close();

            return true;
        }

        public boolean deleteAllRewardEntries(){

            SQLiteDatabase db = getWritableDatabase();
            db.delete(REWARD_STATUSES_TABLE_NAME, null, null);
            db.close();
            return true;
        }

        public Cursor getRewardStatusData(String rewardID, String UID) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + REWARD_STATUSES_TABLE_NAME + " WHERE " + REWARD_STATUSES_REWARD_ID + "=? AND " + REWARD_STATUSES_UID_COLUMN + "=?" + " LIMIT 1", new String[]{rewardID, UID});


            return res;
        }

        public Cursor getAllRewardStatusesData() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + REWARD_STATUSES_TABLE_NAME, null);


            return res;
        }

        public Cursor getRewardStatusDataByUID(String UID) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + REWARD_STATUSES_TABLE_NAME + " WHERE " + REWARD_STATUSES_UID_COLUMN + "=?", new String[]{UID});


            return res;
        }

        public Cursor getNotSentRewardStatusDataByUID(String UID) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM " + REWARD_STATUSES_TABLE_NAME + " WHERE " + REWARD_STATUSES_UID_COLUMN + "=? AND " + REWARD_STATUSES_HAS_BEEN_SENT_COLUMN + "=? ", new String[]{UID, "0"});

            return res;
        }



        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        ///// LOG DATA

//        public boolean insertLog(String message, long timestamp) {
//            SQLiteDatabase db = getWritableDatabase();
//            ContentValues contentValues = new ContentValues();
//
//            contentValues.put(LOG_COLUMN_DATA, message);
//            contentValues.put(LOG_TIMESTAMP, timestamp);
//
//            db.insert(LOG_TABLE_NAME, null, contentValues);
//
//            db.close();
//
//            return true;
//        }
//
//        public Cursor getAllSavedLogMessages() {
//            SQLiteDatabase db = this.getReadableDatabase();
//            Cursor res = db.rawQuery("SELECT * FROM " + SAVED_LOCATIONS_TABLE_NAME, null);
//
//            return res;
//        }
//
//        public void dropAllLogRecords(){
//            SQLiteDatabase db = this.getReadableDatabase();
//            db.delete(SAVED_LOCATIONS_TABLE_NAME,null,null);
//
//            db.close();
//
//        }

    }




}

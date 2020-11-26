package inesc_id.pt.motivandroid.persistence;

import android.content.Context;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.tripData.AccelerationData;
import inesc_id.pt.motivandroid.data.ActivityDataContainer;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;

/**
 * TSMSnapshotHelper
 *
 * Class responsible for managing (reading or writing) temporary trip state machine snapshot data.
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

public class TSMSnapshotHelper {

    //from sharedPreferences - saved state and current to be compared location

    PersistentTripStorage persistentTripStorage;
    Context context;

    public TSMSnapshotHelper(Context context){

        persistentTripStorage = new PersistentTripStorage(context);
        this.context = context;
    }

    //default value must be != null (int)
    public int getSavedState(int defaultValue){
        return SharedPreferencesUtil.readSavedTripState(context,defaultValue);
    }

    public LocationDataContainer getSavedCurrentToBeCompared(String defaultValue){
        return SharedPreferencesUtil.readCurrentToBeCompared(context,defaultValue);
    }

    public boolean existsSavedState(){
        return SharedPreferencesUtil.keyExists(context,SharedPreferencesUtil.SHARED_PREFERENCES_VAR_TRIPSTATE);
    }

    public boolean existsSavedCurrentToBeCompared(){
        return SharedPreferencesUtil.keyExists(context,SharedPreferencesUtil.SHARED_PREFERENCES_VAR_CURRENT);
    }

    public void saveState(int state){
        SharedPreferencesUtil.writeTripState(context,state);
    }

    public void saveCurrentToBeCompared(LocationDataContainer locationDataContainer){
        SharedPreferencesUtil.writeCurrentToBeCompared(context,locationDataContainer);
    }

    public void deleteState(){
        SharedPreferencesUtil.deleteTripState(context);
    }

    public void deleteCurrentToBeCompared(){
        SharedPreferencesUtil.deleteCurrentToBeCompared(context);
    }

    //from sqlite database - saved locations, activities and full trip parts

    //read methods
    public ArrayList<LocationDataContainer> getSavedLocations(){
        return persistentTripStorage.getAllLocationObjects();
    }

    public ArrayList<ActivityDataContainer> getSavedActivities(){
        return persistentTripStorage.getAllActivityObjects();
    }

    public ArrayList<FullTripPart> getSavedFullTripParts(){
        return persistentTripStorage.getAllSavedFullTripPartObjects();
    }

    public ArrayList<AccelerationData> getSavedAccelerationValues(){
        return persistentTripStorage.getAllSavedAccelerationObjects();
    }

    //write methods
    public void saveLocation(LocationDataContainer locationDataContainer){
        persistentTripStorage.insertLocationObject(locationDataContainer);
    }

    @Deprecated
    public void saveActivity(ActivityDataContainer activityDataContainer){
        persistentTripStorage.insertActivityObject(activityDataContainer);
    }

    public void saveFullTripPart(FullTripPart fullTripPart){
        persistentTripStorage.insertTripPart(fullTripPart);
    }

    public void saveFullTripParts(ArrayList<FullTripPart> fullTripPart){
        persistentTripStorage.insertTripPartList(fullTripPart);
    }

    public void saveAccelerationValues(ArrayList<AccelerationData> accelerationDataArrayList){
        persistentTripStorage.insertAccelerationListObjects(accelerationDataArrayList);
    }

    //delete methods
    public void deleteSavedLocations(){
        persistentTripStorage.dropSavedLocations();
    }

    public void deleteSavedFullTripParts(){
        persistentTripStorage.dropAllSavedFullTripParts();
    }

    public void deleteSavedAccelerationValues(){
        persistentTripStorage.dropAllSavedAccelerationObjects();
    }

    public void deleteMLInputMetadata(){
        persistentTripStorage.dropAllMLInputObjects();
    }

    public void deleteAllSnapshotRecords(){
        deleteSavedLocations();
        deleteSavedFullTripParts();
        deleteState();
        deleteCurrentToBeCompared();
        deleteSavedAccelerationValues();
        deleteMLInputMetadata();
    }


}

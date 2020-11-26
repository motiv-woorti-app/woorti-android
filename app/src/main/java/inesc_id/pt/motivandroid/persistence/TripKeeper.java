package inesc_id.pt.motivandroid.persistence;

import android.content.Context;
import android.util.Log;

import inesc_id.pt.motivandroid.data.tripData.FullTrip;

/**
 * TripKeeper
 *
 * Class responsible for managing the object of the last trip loaded from the filesystem. Every app
 * component that needs to access a trip object, accesses it through this class. This is done in
 * order to avoid reading the same trip from disk multiple consecutive times.
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

public class TripKeeper {

    private static TripKeeper instance;

    public FullTrip currentFullTrip;
    public Context context;

    public static synchronized TripKeeper getInstance(Context context){

        if(instance == null){
            instance = new TripKeeper(context);
            Log.e("TripKeeper", "trip keeper null");
        }
        return instance;
    }

    PersistentTripStorage persistentTripStorage;

    public TripKeeper(Context context) {
        this.context = context.getApplicationContext();
        persistentTripStorage = new PersistentTripStorage(context);
    }

    public synchronized FullTrip getCurrentFullTrip(String tripID){

        if((currentFullTrip == null)){
            Log.d("TripKeeper", "Trip not in memory. Getting it from disk!");
            this.currentFullTrip = persistentTripStorage.getFullTripByDate(tripID);
        }else if(!currentFullTrip.getDateId().equals(tripID)){
            Log.d("TripKeeper", "Trip ids dont match!");
            Log.e("TripKeeper", tripID);
            Log.e("TripKeeper", currentFullTrip.getDateId());
            this.currentFullTrip = persistentTripStorage.getFullTripByDate(tripID);
        }else{
            Log.d("TripKeeper", "Trip in memory. Returning!");
        }

        return currentFullTrip;
    }

    public synchronized void setCurrentFullTrip(FullTrip fullTrip){
        this.currentFullTrip = fullTrip;
    }

    public synchronized void saveFullTripPersistently(FullTrip fullTrip){
        Log.d("TripKeeper", "Trying to save edited !");
        this.currentFullTrip = fullTrip;
        persistentTripStorage.updateFullTripDataObject(currentFullTrip, currentFullTrip.getDateId());
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        Log.e("TripKeeper", "finalize");

    }
}

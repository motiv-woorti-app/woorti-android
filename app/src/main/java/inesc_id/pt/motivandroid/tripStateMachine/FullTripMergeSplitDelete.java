package inesc_id.pt.motivandroid.tripStateMachine;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigest;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigestWrapper;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.TripKeeper;

/**
 *  FullTripMergeSplitDelete
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
public class FullTripMergeSplitDelete {

    Context context;
    PersistentTripStorage persistentTripStorage;
    TripAnalysis tripAnalysis;


    public FullTripMergeSplitDelete(Context context){

        this.context = context;
        persistentTripStorage = new PersistentTripStorage(context);
        tripAnalysis = new TripAnalysis(false);

    }

    public void joinFullTrips(ArrayList<FullTrip> fullTrips){

        try {
            FullTrip merged = tripAnalysis.mergeFullTrips(fullTrips);

            for (FullTrip fullTrip : fullTrips){
                persistentTripStorage.deleteFullTripByDate(fullTrip.getDateId());
            }

            persistentTripStorage.insertFullTripObject(merged);

            TripKeeper.getInstance(context).setCurrentFullTrip(null);
        }catch (Exception e){

            Toast.makeText(context, "Error merging trips", Toast.LENGTH_SHORT).show();

        }

    }

    //minimum 0 - split after the first leg
    public void splitFullTrip(FullTripDigest fullTripDigest, int legChosenToSplit){

        FullTrip fullTripToSplit = persistentTripStorage.getFullTripByDate(fullTripDigest.getTripID());

        ArrayList<FullTripPart> splitTripParts1 = new ArrayList<>();
        ArrayList<FullTripPart> splitTripParts2 = new ArrayList<>();

        int i = 0;
        for(FullTripPart ftp : fullTripToSplit.getTripList()){

            if(i <= legChosenToSplit){
                splitTripParts1.add(ftp);
            }else{
                splitTripParts2.add(ftp);
            }

            i++;
        }

        try {

            persistentTripStorage.deleteFullTripByDate(fullTripDigest.getTripID());

            FullTrip splittedTrip1 = tripAnalysis.analyseListOfTrips(splitTripParts1, fullTripToSplit.isManualTripStart(), false);
            FullTrip splittedTrip2 = tripAnalysis.analyseListOfTrips(splitTripParts2, false, fullTripToSplit.isManualTripEnd());

            persistentTripStorage.insertFullTripObject(splittedTrip1);
            persistentTripStorage.insertFullTripObject(splittedTrip2);

            TripKeeper.getInstance(context).setCurrentFullTrip(null);
        }catch (Exception e){

            Toast.makeText(context, "Error spliting trips", Toast.LENGTH_SHORT).show();

        }

    }

    public void deleteFullTrips(ArrayList<FullTripDigestWrapper> fullTripsToDelete){
        for (FullTripDigestWrapper fullTrip : fullTripsToDelete){
            persistentTripStorage.deleteFullTripByDate(fullTrip.getFullTripDigest().getTripID());
        }

        TripKeeper.getInstance(context).setCurrentFullTrip(null);


    }


}

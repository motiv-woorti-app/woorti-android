package inesc_id.pt.motivandroid.data.validationAndRating.activities;

import android.content.Context;
import android.content.res.Resources;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import inesc_id.pt.motivandroid.ApplicationClass;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;

/**
 *  GenericActivityLeg
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
public class GenericActivityLeg extends ActivityLeg implements Serializable {


    public GenericActivityLeg(String activityText, String textCode, int intCode, boolean selected) {
        this.activityText = activityText;
        this.textCode = textCode;
        this.intCode = intCode;
    }

    public GenericActivityLeg() {

    }

    @Override
    public int getIcon() {
        return keys.IconsDrawables[getIntCode()];
    }




    //Return activity array according to transport mode
    public static ArrayList<Integer> getActivitiesArray(int modeOfTransportCode){
        ArrayList<Integer> activitiesArray = new ArrayList<>();

        switch(modeOfTransportCode){
            case ActivityDetected.keys.car:
            case ActivityDetected.keys.bicycle:
            case ActivityDetected.keys.walking:
            case ActivityDetected.keys.running:
            case ActivityDetected.keys.electricBike:
            case ActivityDetected.keys.bikeSharing:
            case ActivityDetected.keys.microScooter:
            case ActivityDetected.keys.skate:
            case ActivityDetected.keys.motorcycle:
            case ActivityDetected.keys.moped:
            case ActivityDetected.keys.carSharing:
            case ActivityDetected.keys.wheelChair:
            case ActivityDetected.keys.cargoBike:
            case ActivityDetected.keys.electricWheelchair:
                activitiesArray.add(0);
//              activitiesArray.add(1);
                activitiesArray.add(2);
//              activitiesArray.add(3);
//              activitiesArray.add(4);
                activitiesArray.add(5);
//              activitiesArray.add(6);
                activitiesArray.add(7);
                activitiesArray.add(8);
                activitiesArray.add(9);
                activitiesArray.add(10);
                activitiesArray.add(11);
                activitiesArray.add(12);
                break;
            case ActivityDetected.keys.still:
            case ActivityDetected.keys.train:
            case ActivityDetected.keys.tram:
            case ActivityDetected.keys.subway:
            case ActivityDetected.keys.ferry:
            case ActivityDetected.keys.plane:
            case ActivityDetected.keys.bus:
            case ActivityDetected.keys.carPassenger:
            case ActivityDetected.keys.taxi:
            case ActivityDetected.keys.rideHailing:
            case ActivityDetected.keys.busLongDistance:
            case ActivityDetected.keys.intercityTrain:
            case ActivityDetected.keys.highSpeedTrain:
            case ActivityDetected.keys.carSharingPassenger:
//              activitiesArray.add(0);
                activitiesArray.add(1);
                activitiesArray.add(2);
                activitiesArray.add(3);
                activitiesArray.add(4);
                activitiesArray.add(5);
                activitiesArray.add(6);
                activitiesArray.add(7);
                activitiesArray.add(8);
                activitiesArray.add(9);
                activitiesArray.add(10);
                activitiesArray.add(11);
                activitiesArray.add(12);
                break;
            default:
                activitiesArray.add(1);
                activitiesArray.add(2);
                activitiesArray.add(3);
                activitiesArray.add(4);
                activitiesArray.add(5);
                activitiesArray.add(6);
                activitiesArray.add(7);
                activitiesArray.add(8);
                activitiesArray.add(9);
                activitiesArray.add(10);
                activitiesArray.add(11);
                activitiesArray.add(12);
        }

        return activitiesArray;
    }

    public static String getFirstActivityForDrivingMode(int modeOfTransportCode, Context context){
        switch (modeOfTransportCode){
            case ActivityDetected.keys.walking:
                return context.getString(R.string.Walking);
            case ActivityDetected.keys.running:
                return context.getString(R.string.Running);
            case ActivityDetected.keys.wheelChair:
            case ActivityDetected.keys.moped:
            case ActivityDetected.keys.motorcycle:
            case ActivityDetected.keys.electricWheelchair:
                return context.getString(R.string.Riding);
            case ActivityDetected.keys.bicycle:
            case ActivityDetected.keys.electricBike:
            case ActivityDetected.keys.cargoBike:
            case ActivityDetected.keys.bikeSharing:
            case ActivityDetected.keys.microScooter:
                return context.getString(R.string.Cycling);
            case ActivityDetected.keys.skate:
                return context.getString(R.string.Skating);
            case ActivityDetected.keys.car:
            case ActivityDetected.keys.carSharing:
                return context.getString(R.string.Driving);
            default:
                return context.getString(R.string.Riding);
        }



    }

    public static String getActivityTextFromTextCode(String code, Context context){

        int i = 0;

        for (String actCode : keys.ActivitiesCodeText){

            if(actCode.equals(code)){
                return context.getString(keys.ActivitiesText[i]);
            }
            i++;
        }

        return "default";

    }

    public interface keys{

        ////////////////////////////////////////////////////////////////////////////////////////////
        //////Body


        //Updated Activities
        int[] ActivitiesText = {
                R.string.Driving_Cycling_Walking,
//                "Driving/ Cycling/ Walking",
//                "Relaxing or sleeping",
                R.string.Relaxing_Sleeping,
//                "Browsing or social media",
                R.string.Browsing_Social_Media,
//                "Reading / writing (paper)",
                R.string.Reading_Writing_Paper,
//                "Reading / writing (device)",
                R.string.Reading_Writing_Device,
//                "Listening to audio",
                R.string.Listening_To_Audio,
//                "Watching video or gaming",
                R.string.Walking_Video_Gaming,
//                "Talking (including phone)",
                R.string.Talking_Including_Phone,
//                "Accompanying someone",
                R.string.Accompanying_Someone,
//                "Eating / drinking",
                R.string.Eating_Drinking,
//                "Personal caring",
                R.string.Personal_Caring,
//                "Thinking",
                R.string.Thinking,
//                "Other"
                R.string.Other

        };


        //Updated codes according to activity
        String[] ActivitiesCodeText = {
                "MTActivityDriving",
                "MTActivityRelaxing",
                "MTActivityBrowsing",
                "MTActivityReadingPaper",
                "MTActivityReadingDevice",
                "MTActivityListening",
                "MTActivityWatching",
                "MTActivityTalking",
                "MTActivityAccompanying",
                "MTActivityEating",
                "MTActivityPersonalCare",
                "MTActivityThinking",
                "MTActivityOther"
        };

        int[] IconsDrawables = {
                R.drawable.mytrips_activities_driving_cycling_walking,
                R.drawable.mytrips_activities_relaxing_sleeping,
                R.drawable.mytrips_activities_browsing_social_media,
                R.drawable.mytrips_activities_reading_writing_paper,
                R.drawable.mytrips_activities_reading_writing_digital,
                R.drawable.mytrips_activities_listening_audio,
                R.drawable.mytrips_activities_watching_video_gaming,
                R.drawable.mytrips_activities_talking,
                R.drawable.mytrips_activities_accompanying_someone,
                R.drawable.mytrips_activities_eating_drinking,
                R.drawable.mytrips_activities_personal_caring,
                R.drawable.mytrips_activities_thinking,
                R.drawable.mytrips_activities_other
        };



    }
}


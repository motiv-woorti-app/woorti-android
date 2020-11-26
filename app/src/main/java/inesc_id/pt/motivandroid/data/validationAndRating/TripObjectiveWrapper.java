package inesc_id.pt.motivandroid.data.validationAndRating;

import android.content.Context;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

import inesc_id.pt.motivandroid.ApplicationClass;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityLeg;
/**
 * TripObjectiveWrapper
 *
 * Wrapper for the trip objective
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
public class TripObjectiveWrapper implements Serializable {

    int tripObjectiveIconDrawable;

    @Expose
    int tripObjectiveCode;

    @Expose
    String tripObjectiveString;

    public TripObjectiveWrapper(int tripObjectiveIconDrawable, int tripObjectiveCode, String tripObjectiveString) {
        this.tripObjectiveIconDrawable = tripObjectiveIconDrawable;
        this.tripObjectiveCode = tripObjectiveCode;
        this.tripObjectiveString = tripObjectiveString;
    }

    public int getTripObjectiveIconDrawable() {
        return tripObjectiveIconDrawable;
    }

    public void setTripObjectiveIconDrawable(int tripObjectiveIconDrawable) {
        this.tripObjectiveIconDrawable = tripObjectiveIconDrawable;
    }

    public int getTripObjectiveCode() {
        return tripObjectiveCode;
    }

    public void setTripObjectiveCode(int tripObjectiveCode) {
        this.tripObjectiveCode = tripObjectiveCode;
    }

    public String getTripObjectiveString() {
        return tripObjectiveString;
    }

    public void setTripObjectiveString(String tripObjectiveString) {
        this.tripObjectiveString = tripObjectiveString;
    }

    public static int getIndex(TripObjectiveWrapper objective, ArrayList<TripObjectiveWrapper> objectivesSelected){

        int i = 0;
        for(TripObjectiveWrapper testingActivity : objectivesSelected){

            if (objective.getTripObjectiveCode() == testingActivity.getTripObjectiveCode()){
                return i;
            }

            i++;
        }
        return -1;
    }

//    public static String getObjectiveTextFromInt(int code, Context context){
//
//        return context.getString(keys.objectives[code]);
//
//    }

    public static ArrayList<String> getAllObjectiveTexts(Context context){

        ArrayList<String> result = new ArrayList<>();

        for(int i : keys.objectives){
            result.add(context.getString(i));
        }

        return result;

    }

    public interface keys {

        int[] objectives = {
                R.string.Home,
                R.string.Work,
                R.string.School_Education,
                R.string.Everyday_Shopping,
                R.string.Business_Trip,
                R.string.Leisure_Hobby,
                R.string.Pick_Up_Drop_Off,
                R.string.Personal_Tasks_Errands,
                R.string.Trip_Itself,
                R.string.Other
        };



        int HOME = 0;
        int WORK = 1;
        int SCHOOL = 2;
        int SHOPPING = 3;
        int BUSINESS = 4;
        int HOBBY = 5;
        int PICKUP_DROP_SOMEONE = 6;
        int PERSONAL_TASKS = 7;
        int TRIP_ITSELf = 8;
        int OTHER = 9;

        //special
        String otherString = "other";

        int[] iconsDrawables = {
                R.drawable.mytrips_illustrations_objectives_home,
                R.drawable.mytrips_illustrations_objectives_work,
                R.drawable.mytrips_illustrations_objectives_school_education,
                R.drawable.mytrips_illustrations_objectives_everyday_shopping,
                R.drawable.mytrips_illustrations_objectives_business_trip,
                R.drawable.mytrips_illustrations_objectives_leisure_hobby,
                R.drawable.mytrips_illustrations_objectives_pick_up_drop_off_someone,
                R.drawable.mytrips_illustrations_objectives_personal_family_task,
                R.drawable.mytrips_illustrations_objectives_the_trip_itself,
                R.drawable.mytrips_illustrations_objectives_other

        };



    }

}

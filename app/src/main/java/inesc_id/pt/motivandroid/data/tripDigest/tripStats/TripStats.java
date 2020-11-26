package inesc_id.pt.motivandroid.data.tripDigest.tripStats;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityLeg;

/**
 * TripStats
 *
 *      Data structure to keep some trip stats (so that these don't have to be computed every time
 *  that they are needed)
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
public class TripStats implements Serializable{

    public TripStats() {
    }

    @Expose
    private ArrayList<ModalityTimeDistanceCounter> modalityTimeDistanceCounters = new ArrayList<>();

    @Expose
    private ActivityTimeCounter activityTimeCounter = new ActivityTimeCounter(0, new ArrayList<ActivityLeg>());

    @Expose
    private WorthwhilenessScore worthwhilenessScore = new WorthwhilenessScore();

    @Expose
    private int caloriesSpentValue;

    @Expose
    private int c02FootprintValue;

    public int getCaloriesSpentValue() {
        return caloriesSpentValue;
    }

    public TripStats(ArrayList<ModalityTimeDistanceCounter> modalityTimeDistanceCounters, ActivityTimeCounter activityTimeCounter, WorthwhilenessScore worthwhilenessScore) {
        this.modalityTimeDistanceCounters = modalityTimeDistanceCounters;
        this.activityTimeCounter = activityTimeCounter;
        this.worthwhilenessScore = worthwhilenessScore;
    }

    public ArrayList<ModalityTimeDistanceCounter> getModalityTimeDistanceCounters() {
        return modalityTimeDistanceCounters;
    }

    public void setModalityTimeDistanceCounters(ArrayList<ModalityTimeDistanceCounter> modalityTimeDistanceCounters) {
        this.modalityTimeDistanceCounters = modalityTimeDistanceCounters;
    }

    public ActivityTimeCounter getActivityTimeCounter() {
        return activityTimeCounter;
    }

    public void setActivityTimeCounter(ActivityTimeCounter activityTimeCounter) {
        this.activityTimeCounter = activityTimeCounter;
    }

    public WorthwhilenessScore getWorthwhilenessScore() {
        return worthwhilenessScore;
    }

    public void setWorthwhilenessScore(WorthwhilenessScore worthwhilenessScore) {
        this.worthwhilenessScore = worthwhilenessScore;
    }



    public void setCaloriesSpentValue(int caloriesSpentValue) {
        this.caloriesSpentValue = caloriesSpentValue;
    }

    public int getC02FootprintValue() {
        return c02FootprintValue;
    }

    public void setC02FootprintValue(int c02FootprintValue) {
        this.c02FootprintValue = c02FootprintValue;
    }


}

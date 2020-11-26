package inesc_id.pt.motivandroid.deprecated;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.ActivityDataContainer;

/**
 * Created by admin on 1/22/18.
 */

@Deprecated
public class BatchData {

    public long initTimestamp;
    public long endTimestamp;
    public ArrayList<ActivityDataContainer> batchActivities;
    public int mostProbableActivity;

    public BatchData(long initTimestamp, long endTimestamp, ArrayList<ActivityDataContainer> batchActivities, int mostProbableActivity) {
        this.initTimestamp = initTimestamp;
        this.endTimestamp = endTimestamp;
        this.batchActivities = batchActivities;
        this.mostProbableActivity = mostProbableActivity;
    }

    public long getInitTimestamp() {
        return initTimestamp;
    }

    public void setInitTimestamp(long initTimestamp) {
        this.initTimestamp = initTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public ArrayList<ActivityDataContainer> getBatchActivities() {
        return batchActivities;
    }

    public void setBatchActivities(ArrayList<ActivityDataContainer> batchActivities) {
        this.batchActivities = batchActivities;
    }

    public int getMostProbableActivity() {
        return mostProbableActivity;
    }

    public void setMostProbableActivity(int mostProbableActivity) {
        this.mostProbableActivity = mostProbableActivity;
    }

}

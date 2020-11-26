package inesc_id.pt.motivandroid.data.rewards;

import java.io.Serializable;
import java.util.ArrayList;

@Deprecated
public class CompletedRewardsToShow implements Serializable{

    ArrayList<String> rewardsCompleted = new ArrayList<>();
    String uid;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void addRewardCompleted(String uid, String rewardID){

        if (uid != null && uid.equals(this.uid)){

            rewardsCompleted.add(rewardID);

        }else{

            this.uid = uid;
            rewardsCompleted.clear();
            rewardsCompleted.add(rewardID);

        }

    }

}

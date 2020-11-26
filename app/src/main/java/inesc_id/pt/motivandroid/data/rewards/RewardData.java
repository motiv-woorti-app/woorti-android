package inesc_id.pt.motivandroid.data.rewards;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 *
 * RewardData
 *
 *  Class representing the data structure of a reward
 *
 *      rewardId: Id of the reward
 *      rewardName: Name of the reward
 *      targetCampaignId: Id of campaign eligible to the reward
 *      startDate: start date of the reward
 *      endDate: end date of the reward
 *      targetType: type of the reward target (points, days, trips, etc.)
 *      targetValue: minimum target value to be eligible for reward
 *      organizerName: organizer of the reward
 *      linkToContact: url to contact organizer
 *      removed: indicates wheter reward was removed
 *      defaultLanguage: default language of the reward
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
public class RewardData implements Serializable{

    public RewardData() {
    }

    public RewardData(String rewardId, String rewardName, String targetCampaignId, long startDate, long endDate, int targetType, int targetValue, String organizerName, String linkToContact, boolean removed, HashMap<String, RewardDescription> descriptions, String defaultLanguage) {
        this.rewardId = rewardId;
        this.targetCampaignId = targetCampaignId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetType = targetType;
        this.rewardName = rewardName;
        this.targetValue = targetValue;
        this.organizerName = organizerName;
        this.linkToContact = linkToContact;
        this.removed = removed;
        this.descriptions = descriptions;
        this.defaultLanguage = defaultLanguage;
    }



    @Expose
    public String rewardId;

    @Expose
    public String rewardName;

    @Expose
    public String targetCampaignId;

    @Expose
    public long startDate;

    @Expose
    public long  endDate;

    @Expose
    public int targetType;

    @Expose
    public int targetValue;

    @Expose
    public String organizerName;

    @Expose
    public String linkToContact;

    @Expose
    public boolean removed;

    @Expose
    public HashMap<String, RewardDescription> descriptions;

    @Expose
    public String defaultLanguage;

    public RewardDescription getRewardDescription(String preferredLanguage) {

        return descriptions.containsKey(preferredLanguage) ? descriptions.get(preferredLanguage) : descriptions.get(defaultLanguage);

    }

    public static ArrayList<RewardData> getDummyRewardList(){

        HashMap<String, RewardDescription> rewardDescriptionHashMap = new HashMap<>();

        RewardDescription rewardDescriptionEnglish = new RewardDescription("ShortDescription", "LongDescription");
        RewardDescription rewardDescriptionPortuguese = new RewardDescription("DescriçãoCurta", "DescriçãoLonga");

        rewardDescriptionHashMap.put("eng", rewardDescriptionEnglish);
        rewardDescriptionHashMap.put("por", rewardDescriptionPortuguese);

        ArrayList<RewardData> result = new ArrayList<>();

        result.add(new RewardData("RewardID1","Reward Name 1" ,"TargetCampaignID", 0, 1000000000, 0, 100, "Organizer Name", "www.linkToContact.com",false, rewardDescriptionHashMap , "eng"));
        result.add(new RewardData("RewardID2", "Reward Name 2","TargetCampaignID", 0, 1000000000, 0, 100, "Organizer Name", "www.linkToContact.com", false,rewardDescriptionHashMap , "eng"));
        result.add(new RewardData("RewardID3", "Reward Name 3", "TargetCampaignID", 0, 1000000000, 0, 100, "Organizer Name", "www.linkToContact.com", false,rewardDescriptionHashMap , "eng"));
        result.add(new RewardData("RewardID4", "Reward Name 4", "TargetCampaignID", 0, 1000000000, 0, 100, "Organizer Name", "www.linkToContact.com", false,rewardDescriptionHashMap , "eng"));

        return result;

    }

    public interface keys{

        int POINTS = 1;
        int DAYS = 2;
        int TRIPS = 3;
        int POINTS_ALL_TIME = 4;
        int DAYS_ALL_TIME = 5;
        int TRIPS_ALL_TIME = 6;

    }

    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
    }

    public String getTargetCampaignId() {
        return targetCampaignId;
    }

    public void setTargetCampaignId(String targetCampaignId) {
        this.targetCampaignId = targetCampaignId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getTargetType() {
        return targetType;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(int targetValue) {
        this.targetValue = targetValue;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getLinkToContact() {
        return linkToContact;
    }

    public void setLinkToContact(String linkToContact) {
        this.linkToContact = linkToContact;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public HashMap<String, RewardDescription> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(HashMap<String, RewardDescription> descriptions) {
        this.descriptions = descriptions;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }
}

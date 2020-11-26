package inesc_id.pt.motivandroid.data.userSettingsData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.data.engagementNotifications.EngagementNotification;
import inesc_id.pt.motivandroid.data.pointSystem.CampaignScore;
import inesc_id.pt.motivandroid.data.stories.StoryStateful;
import inesc_id.pt.motivandroid.onboarding.wrappers.ModeOfTransportUsed;

/**
 * UserSettings
 *
 * Data structure representing the user settings.
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

public class UserSettings {

    public UserSettings(){
//        motGroups = MOTGroup.getDefaultGroups();
    }

//    @Expose
//    ArrayList<MOTGroup> motGroups;

    public boolean getSeenBateryPopup() {
        return seenBateryPopup;
    }

    public void setSeenBateryPopup(boolean seenBateryPopup) {
        this.seenBateryPopup = seenBateryPopup;
    }

    @Expose
    boolean seenBateryPopup;

    @Expose
    String gender;

    @Expose
    @SerializedName("relValue")
    int relaxingValue = -1;

    @Expose
    @SerializedName("prodValue")
    int productivityValue =-1;

    @Expose
    @SerializedName("actValue")
    int activityValue = -1;

    @Expose
    @SerializedName("preferedMots")
    ArrayList<ModeOfTransportUsed> preferedModesOfTransport;

    @Expose
    String country;

    @Expose
    String city;

    @Expose
    String email;

    @Expose
    String name;

    @Expose
    String degree;

    @Expose
    String lang;

    @Expose
    int minAge;

    @Expose
    int maxAge;

    @Expose
    boolean hasSetMobilityGoal = false;

    @Expose
    int mobilityGoalChosen;

    @Expose
    int mobilityGoalPoints;

    @Expose
    String maritalStatusHousehold;

    @Expose
    String numberPeopleHousehold;

    @Expose
    String yearsOfResidenceHousehold;

    @Expose
    String labourStatusHousehold;

    @Expose
    boolean dontShowTellUsMorePopup;

    @Expose
    boolean dontShowBlockPopup;

    public int getFullPoints() {
        return fullPoints;
    }

    public void setFullPoints(int fullPoints) {
        this.fullPoints = fullPoints;
    }

    @Deprecated
    @Expose
    int fullPoints = 0;

    public ArrayList<CampaignScore> getPointsPerCampaign() {
        return pointsPerCampaign;
    }

    public void setPointsPerCampaign(ArrayList<CampaignScore> pointsPerCampaign) {
        this.pointsPerCampaign = pointsPerCampaign;
    }

    @Expose
    private ArrayList<CampaignScore> pointsPerCampaign = new ArrayList<>();

    @Expose
    int version = 0;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void incrementVersion() {
        this.version++;
    }

    @Expose
    ArrayList<StoryStateful> stories = new ArrayList<>();

//    @Expose
//    long registerDate;

    public ArrayList<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(ArrayList<Campaign> campaigns) {
        this.campaigns = campaigns;
    }

    @Expose
    ArrayList<Campaign> campaigns;

    public HomeWorkAddress getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(HomeWorkAddress homeAddress) {
        this.homeAddress = homeAddress;
    }

    public HomeWorkAddress getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(HomeWorkAddress workAddress) {
        this.workAddress = workAddress;
    }

    @Expose
    HomeWorkAddress homeAddress;

    @Expose
    HomeWorkAddress workAddress;

    public String getChosenDefaultCampaignID() {
        return chosenDefaultCampaignID;
    }

    public void setChosenDefaultCampaignID(String chosenDefaultCampaignID) {
        this.chosenDefaultCampaignID = chosenDefaultCampaignID;
    }

    @Expose
    String chosenDefaultCampaignID;

    public boolean isHasReportedTrip() {
        return hasReportedTrip;
    }

    public void setHasReportedTrip(boolean hasReportedTrip) {
        this.hasReportedTrip = hasReportedTrip;
    }

    public boolean isHasGoneToDashboard() {
        return hasGoneToDashboard;
    }

    public void setHasGoneToDashboard(boolean hasGoneToDashboard) {
        this.hasGoneToDashboard = hasGoneToDashboard;
    }

    @Expose
    boolean hasReportedTrip;

    @Expose
    boolean hasGoneToDashboard;


    public ArrayList<EngagementNotification> getEngagementNotifications() {
        return engagementNotifications;
    }

    public void setEngagementNotifications(ArrayList<EngagementNotification> engagementNotifications) {
        this.engagementNotifications = engagementNotifications;
    }

    @Expose
    ArrayList<EngagementNotification> engagementNotifications;

    public long getLastSummarySent() {
        return lastSummarySent;
    }

    public void setLastSummarySent(long lastSummarySent) {
        this.lastSummarySent = lastSummarySent;
    }

    @Expose
    long lastSummarySent;

//    public ArrayList<MOTGroup> getMotGroups() {
//        return motGroups;
//    }
//
//    public void setMotGroups(ArrayList<MOTGroup> motGroups) {
//        this.motGroups = motGroups;
//    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getRelaxingValue() {
        return relaxingValue;
    }

    public void setRelaxingValue(int relaxingValue) {
        this.relaxingValue = relaxingValue;
    }

    public int getProductivityValue() {
        return productivityValue;
    }

    public void setProductivityValue(int productivityValue) {
        this.productivityValue = productivityValue;
    }

    public int getActivityValue() {
        return activityValue;
    }

    public void setActivityValue(int activityValue) {
        this.activityValue = activityValue;
    }

    public ArrayList<ModeOfTransportUsed> getPreferedModesOfTransport() {
        return preferedModesOfTransport;
    }

    public void setPreferedModesOfTransport(ArrayList<ModeOfTransportUsed> preferedModesOfTransport) {
        this.preferedModesOfTransport = preferedModesOfTransport;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public boolean isHasSetMobilityGoal() {
        return hasSetMobilityGoal;
    }

    public void setHasSetMobilityGoal(boolean hasSetMobilityGoal) {
        this.hasSetMobilityGoal = hasSetMobilityGoal;
    }

    public int getMobilityGoalChosen() {
        return mobilityGoalChosen;
    }

    public void setMobilityGoalChosen(int mobilityGoalDefined) {
        this.mobilityGoalChosen = mobilityGoalDefined;
    }

    public int getMobilityGoal() {
        return mobilityGoalPoints;
    }

    public void setMobilityGoal(int mobilityGoal) {
        this.mobilityGoalPoints = mobilityGoal;
    }

//    public long getRegisterDate() {
//        return registerDate;
//    }
//
//    public void setRegisterDate(long registerDate) {
//        this.registerDate = registerDate;
//    }

    public ArrayList<StoryStateful> getStories() {
        return stories;
    }

    public void setStories(ArrayList<StoryStateful> stories) {
        this.stories = stories;
    }

    public String getMaritalStatusHousehold() {
        return maritalStatusHousehold;
    }

    public void setMaritalStatusHousehold(String maritalStatusHousehold) {
        this.maritalStatusHousehold = maritalStatusHousehold;
    }

    public String getNumberPeopleHousehold() {
        return numberPeopleHousehold;
    }

    public void setNumberPeopleHousehold(String numberPeopleHousehold) {
        this.numberPeopleHousehold = numberPeopleHousehold;
    }

    public String getYearsOfResidenceHousehold() {
        return yearsOfResidenceHousehold;
    }

    public void setYearsOfResidenceHousehold(String yearsOfResidenceHousehold) {
        this.yearsOfResidenceHousehold = yearsOfResidenceHousehold;
    }

    public String getLabourStatusHousehold() {
        return labourStatusHousehold;
    }

    public void setLabourStatusHousehold(String labourStatusHousehold) {
        this.labourStatusHousehold = labourStatusHousehold;
    }

    public boolean isDontShowTellUsMorePopup() {
        return dontShowTellUsMorePopup;
    }

    public void setDontShowTellUsMorePopup(boolean dontShowTellUsMorePopup) {
        this.dontShowTellUsMorePopup = dontShowTellUsMorePopup;
    }

    public boolean isDontShowBlockPopup() {
        return dontShowBlockPopup;
    }

    public void setDontShowBlockPopup(boolean dontShowBlockPopup) {
        this.dontShowBlockPopup = dontShowBlockPopup;
    }

    public boolean hasUnfilledHousehold(){

        if ((maritalStatusHousehold == null || maritalStatusHousehold.equals("")) ||
                (numberPeopleHousehold == null || numberPeopleHousehold.equals("")) ||
                    (yearsOfResidenceHousehold == null || yearsOfResidenceHousehold.equals("")) ||
                        (labourStatusHousehold == null || labourStatusHousehold.equals(""))){

            return true;

        }
            return false;
    }


    }
package inesc_id.pt.motivandroid.auth.data;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettings;

/**
 * UserSchemaServer
 *
 *  Holder for the user settings.
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

public class UserSchemaServer implements Serializable{

    public UserSchemaServer(String email, String pushNotificationToken, String userid, ArrayList<String> onCampaigns, UserSettings userSettings) {
        this.email = email;
        this.pushNotificationToken = pushNotificationToken;
        this.userid = userid;
        this.onCampaigns = onCampaigns;
        this.userSettings = userSettings;
    }

    public UserSchemaServer(UserSettingStateWrapper userSettingStateWrapper) {
        this.email = userSettingStateWrapper.getUserSettings().getEmail();
        this.pushNotificationToken = userSettingStateWrapper.getPushNotificationToken();
        this.userid = userSettingStateWrapper.getUserid();
        this.onCampaigns = userSettingStateWrapper.getOnCampaigns();
        this.userSettings = userSettingStateWrapper.getUserSettings();
    }

    public UserSchemaServer() {
    }

    @Expose
    private String email;

    @Expose
    private String pushNotificationToken;

    @Expose
    private String userid;

    @Expose
    private ArrayList<String> onCampaigns;

    @Expose
    private UserSettings userSettings;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPushNotificationToken() {
        return pushNotificationToken;
    }

    public void setPushNotificationToken(String pushNotificationToken) {
        this.pushNotificationToken = pushNotificationToken;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public ArrayList<String> getOnCampaigns() {
        return onCampaigns;
    }

    public void setOnCampaigns(ArrayList<String> onCampaigns) {
        this.onCampaigns = onCampaigns;
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }


}

package inesc_id.pt.motivandroid.settingsAndProfile;

import  android.content.Context;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;

/**
 * Option
 *
 * Class representing each option on a menu.
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

public class Option {


    /**
     * option text to be shown to the user (in its chosen language)
     */
    String text;


    /**
     * universal code for the option
     */
    int code;

    public Option(String text, int code) {
        this.text = text;
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static ArrayList<Option> getAllProfileAndSettingOptions(Context context){

        ArrayList<Option> result = new ArrayList<>();

        for (int code : keys.profileAndSettingsOptionsArrayList){
            result.add(new Option(context.getResources().getString(code), code));
        }

        return result;

    }

    public static ArrayList<Option> getCampaignsProfileAndSettingOptions(Context context){

        ArrayList<Option> result = new ArrayList<>();

        for (int code : keys.profileAndSettingsCampaignsOptionsArrayList){
            result.add(new Option(context.getResources().getString(code), code));
        }

        return result;

    }

    public static ArrayList<Option> getHomeAndWorkOptionsArrayList(Context context){

        ArrayList<Option> result = new ArrayList<>();

        for (int code : keys.setHomeAndWorkOptionsArrayList){
            result.add(new Option(context.getResources().getString(code), code));
        }

        return result;

    }

    public static ArrayList<Option> getDemographicOptionsArrayList(Context context){

        ArrayList<Option> result = new ArrayList<>();

        for (int code : keys.demographicOptionsArrayList){
            result.add(new Option(context.getResources().getString(code), code));
        }

        return result;

    }

    public static ArrayList<Option> getTutorialOptionsArrayList(Context context){

        ArrayList<Option> result = new ArrayList<>();

        for (int code : keys.tutorialOptionsArrayList){
            result.add(new Option(context.getResources().getString(code), code));
        }

        return result;

    }

    public static ArrayList<Option> getEducationOptionsArrayList(Context context){

        ArrayList<Option> result = new ArrayList<>();

        for (int code : keys.educationArrayList){
            result.add(new Option(context.getResources().getString(code), code));
        }

        return result;

    }

    public interface keys{

        /**
         *  arrays of codes for the options of each menu. The code equals to the id of the string in
         * strings.xml
         */

        /** main menu options **/
        int[] profileAndSettingsOptionsArrayList = {
                R.string.Set_Home_And_Work,
                R.string.Demographic_Info,
                R.string.Worthwhileness_Settings,
                R.string.Campaigns,
                R.string.Tutorials,
                R.string.Transport_Preferences,
                R.string.App_Language,
                R.string.Feedback,
                R.string.Privacy_Policy,
                R.string.Log_Out
        };

        /** campaign sub menu options **/
        int[] profileAndSettingsCampaignsOptionsArrayList = {
                R.string.Campaign_Scores
                ,
                R.string.Targets_And_Rewards
        };

        /** set home and work sub menu options **/
        int[] setHomeAndWorkOptionsArrayList = {
                R.string.Home,
                R.string.Work
        };

        /** demographic info sub menu options **/
        int[] demographicOptionsArrayList = {
                R.string.Demographic_Option_General,
                R.string.Household
                //R.string.DEMOGRAPHIC_OPTION_HOUSEHOLD_DATA,
                //R.string.DEMOGRAPHIC_OPTION_MOBILITY_CAPABILITIES,
                //R.string.DEMOGRAPHIC_OPTION_MOBILITY_PREFERENCES,
                //R.string.DEMOGRAPHIC_OPTION_EXPERIENCE_FACTORS
        };

        /** tutorials sub menu options **/
        int[] tutorialOptionsArrayList = {

            R.string.Tutorial_Option_What_Is_Worthwhileness,
                R.string.Trip_Validation_Tutorial

        };

        /** education sub menu options **/
        int[] educationArrayList = {
                R.string.Education_8th_Grade,
                R.string.Education_Some_Highschool,
                R.string.Education_Highschool,
                R.string.Education_Some_College,
                R.string.Education_Training,
                R.string.Education_Bachelor,
                R.string.Education_Master,
                R.string.Education_Professional,
                R.string.Education_Doctorate,
                R.string.Education_Associate,
                R.string.Education_No_Schooling
        };


        }
    }


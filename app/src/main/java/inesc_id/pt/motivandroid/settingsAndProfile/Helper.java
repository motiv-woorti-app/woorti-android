package inesc_id.pt.motivandroid.settingsAndProfile;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

import inesc_id.pt.motivandroid.ApplicationClass;
import inesc_id.pt.motivandroid.R;

/**
 * Helper
 *
 *  Class to help build the menus. Provides the array of options in the chosen locale for each
 * option list
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

public class Helper {

    /**
     * @param wantedCode
     * @param codes
     * @return given a wantedCode and an array of codes, returns the position of the wanted code
     *  in the array. If not exists, returns -1
     */
    public static int getTextArrayPosition(String wantedCode, String[] codes){

        int i = 0;

        for(String code : codes){

            if(code.equals(wantedCode)){
                return i;
            }
            i++;
        }

        return -1;
    }


    /** gender sub menu option codes **/
    public static int[] genders = {
            R.string.Male,
            R.string.Female,
            R.string.Other
    };

    public static ArrayList<String> getGenderTextArrayInLocale(Context context){

        ArrayList<String> result = new ArrayList<>();

        for (int code : genders){
            result.add(context.getString(code));
        }

        return result;

    }

    /** gender sub menu option keys (to ensure compatibility with former versions) **/
    public static String[] gendersKeys = {
            "Male",
            "Female",
            "Other"
    };

    /** degree sub menu option keys  (to ensure compatibility with former versions) **/
    public static String[] degreesKeys = {
            "Basic (up to 10th grade)",
            "High school (12th grade)",
            "University"
    };

    public static ArrayList<String> getDegreesTextArrayInLocale(Context context){

        ArrayList<String> result = new ArrayList<>();

        for (int code : degrees){
            result.add(context.getString(code));
        }

        return result;

    }

    /** degrees sub menu option codes **/
    public static int[] degrees = {
            R.string.Education_Basic,
            R.string.Education_Highschool,
            R.string.Education_University
    };

    public static ArrayList<String> getOccupationsTextArrayInLocale(Context context){

        ArrayList<String> result = new ArrayList<>();

        for (int code : occupations){
            result.add(context.getString(code));
        }

        return result;

    }

    /** occupation sub menu option codes **/
    public static int[] occupations = {
            R.string.Ocupation_Student,
            R.string.Ocupation_Schedule_Worker,
            R.string.Ocupation_Flexible_worker,
            R.string.Ocupation_Selfemployed,
            R.string.Ocupation_Seeking_Work,
            R.string.Ocupation_Inactive,
            R.string.Ocupation_Maternity,
            R.string.Ocupation_Military
    };

    public static ArrayList<String> getMaritalTextArrayInLocale(Context context){

        ArrayList<String> result = new ArrayList<>();

        for (int code : marital){
            result.add(context.getString(code));
        }

        return result;

    }

    /** marital status sub menu option keys  (to ensure compatibility with former versions) **/
    public static String[] maritalKeys = {
            "Single",
            "Married",
            "Registered partnership",
            "Divorced",
            "Widowed"
    };

    /** marital status sub menu option codes **/
    public static int[] marital = {
            R.string.Marital_Single,
            R.string.Marital_Married,
            R.string.Marital_Partnership,
            R.string.Marital_Divorced,
            R.string.Marital_Widowed
    };

    /** license sub menu option codes **/
    public static int[] license = {
            R.string.Yes,
            R.string.No
    };

    public static ArrayList<String> getLicenseTextArrayInLocale(Context context){

        ArrayList<String> result = new ArrayList<>();

        for (int code : license){
            result.add(context.getString(code));
        }

        return result;

    }

    /** yearly income sub menu options (code = text untranslatable) **/
    public static String[] yearlyIncome = { "<10.000€", "10.000-15.000€", "15.000-20.000€", "20.000-30.000€",
            "30.000-45.000€", "45.000-70.000€", "70.000-100.000€", "100.000€-150.000€",
            "150.000-250.000€", ">250.000€" };

    public static ArrayList<String> getYearlyIncomeTextArrayInLocale(){

        return new ArrayList<>(Arrays.asList(yearlyIncome));

    }
    /** people household sub menu options (code = text untranslatable) **/
    public static String[] peopleHousehold = {"1", "2", "3", "4", "5+"};

    public static ArrayList<String> getPeopleHouseholdTextArrayInLocale(){

        return new ArrayList<>(Arrays.asList(peopleHousehold));

    }

    /** years of residence sub menu option keys (to ensure compatibility with former versions) **/
    public static String[] yearsResidenceKeys = {
            "Less than 1",
            "1 to 5",
            "More than 5"
    };

    public static ArrayList<String> getYearsResidenceTextArrayInLocale(Context context){

        ArrayList<String> result = new ArrayList<>();

        for (int code : yearsResidence){
            result.add(context.getString(code));
        }

        return result;

    }

    /** years of residence sub menu option codes **/
    public static int[] yearsResidence = {
            R.string.Years_Residence_Less_Than_One,
            R.string.Years_Residence_One_To_Five,
            R.string.Years_Residence_More_Than_Five
    };

    /** labour sub menu option keys (to ensure compatibility with former versions) **/
    public static String[] labourKeys = {
            "Student",
            "Employed full Time",
            "Employed part-time",
            "Unemployed",
            "Pensioner"
    };

    public static ArrayList<String> getLaboutTextArrayInLocale(Context context){

        ArrayList<String> result = new ArrayList<>();

        for (int code : labour){
            result.add(context.getString(code));
        }

        return result;

    }

    /** labour sub menu option codes **/
    public static int[] labour = {
            R.string.Labour_Student,
            R.string.Labour_Employed_Full_Time,
            R.string.Labour_Employed_Part_Time,
            R.string.Labour_Unemployed,
            R.string.Labour_Pensioner
    };

}

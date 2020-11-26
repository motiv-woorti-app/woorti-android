package inesc_id.pt.motivandroid.data.validationAndRating;

import android.content.Context;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;

/**
 * TripPartFactor
 *
 * Class representing a leg/transfer satisfaction factor.
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
public class TripPartFactor implements Serializable{

    @Expose
    public int code;

    public String name;

    @Expose
    public boolean plus;

    @Expose
    public boolean minus;

    public TripPartFactor(int code, String name){
        this.code = code;
        this.name = name;
    }

    public void toggleAdd(){
        plus = !plus;
    }

    public void toggleRemove(){
        minus = !minus;
    }


    public static ArrayList<TripPartFactor> getPublicTransportACTFactors(int mode, Context context){

        ArrayList<TripPartFactor> factorsArray = new ArrayList<>();

        switch (mode) {
            case ActivityDetected.keys.subway:
            case ActivityDetected.keys.tram:
            case ActivityDetected.keys.bus:
            case ActivityDetected.keys.busLongDistance:
            case ActivityDetected.keys.train:
            case ActivityDetected.keys.intercityTrain:
            case ActivityDetected.keys.highSpeedTrain:
            case ActivityDetected.keys.ferry:
            case ActivityDetected.keys.plane:
            default:
                factorsArray.add(new TripPartFactor(keys.Ability_To_Do_What_I_Wanted,context.getString(R.string.Ability_To_Do_What_I_Wanted)));
                break;
        }
        return factorsArray;
    }


    //While you ride factors for public transport
    public static ArrayList<TripPartFactor> getPublicTransportWYRFactors(int mode, Context context){
        ArrayList<TripPartFactor> factorsArray = new ArrayList<>();

        switch (mode){
            case ActivityDetected.keys.subway:
                factorsArray.add(new TripPartFactor(keys.Todays_Weather,context.getString(R.string.Todays_Weather)));
                factorsArray.add(new TripPartFactor(keys.Crowdedness_Seating,context.getString(R.string.Crowdedness_Seating)));
                factorsArray.add(new TripPartFactor(keys.Internet_Connectivity,context.getString(R.string.Internet_Connectivity)));
                factorsArray.add(new TripPartFactor(keys.Charging_Opportunity,context.getString(R.string.Charging_Opportunity)));
                factorsArray.add(new TripPartFactor(keys.Food_Drink_Allowed,context.getString(R.string.Food_Drink_Allowed)));
                factorsArray.add(new TripPartFactor(keys.Car_Bike_Parking_At_Transfer_Point,context.getString(R.string.Car_Bike_Parking_At_Transfer_Point)));
                break;
            case ActivityDetected.keys.tram:
            case ActivityDetected.keys.bus:
                factorsArray.add(new TripPartFactor(keys.Todays_Weather,context.getString(R.string.Todays_Weather)));
                factorsArray.add(new TripPartFactor(keys.Crowdedness_Seating,context.getString(R.string.Crowdedness_Seating)));
                factorsArray.add(new TripPartFactor(keys.Internet_Connectivity,context.getString(R.string.Internet_Connectivity)));
                factorsArray.add(new TripPartFactor(keys.Charging_Opportunity,context.getString(R.string.Charging_Opportunity)));
                factorsArray.add(new TripPartFactor(keys.Tables,context.getString(R.string.Tables)));
                factorsArray.add(new TripPartFactor(keys.Food_Drink_Allowed,context.getString(R.string.Food_Drink_Allowed)));
                factorsArray.add(new TripPartFactor(keys.Car_Bike_Parking_At_Transfer_Point,context.getString(R.string.Car_Bike_Parking_At_Transfer_Point)));
                break;
            case ActivityDetected.keys.busLongDistance:
            case ActivityDetected.keys.train:
                factorsArray.add(new TripPartFactor(keys.Todays_Weather,context.getString(R.string.Todays_Weather)));
                factorsArray.add(new TripPartFactor(keys.Crowdedness_Seating,context.getString(R.string.Crowdedness_Seating)));
                factorsArray.add(new TripPartFactor(keys.Internet_Connectivity,context.getString(R.string.Internet_Connectivity)));
                factorsArray.add(new TripPartFactor(keys.Charging_Opportunity,context.getString(R.string.Charging_Opportunity)));
                factorsArray.add(new TripPartFactor(keys.Tables,context.getString(R.string.Tables)));
                factorsArray.add(new TripPartFactor(keys.Toilets,context.getString(R.string.Toilets)));
                factorsArray.add(new TripPartFactor(keys.Food_Drink_Allowed,context.getString(R.string.Food_Drink_Allowed)));
                factorsArray.add(new TripPartFactor(keys.Food_Drink_Available,context.getString(R.string.Food_Drink_Available)));
                factorsArray.add(new TripPartFactor(keys.Entertainment,context.getString(R.string.Entertainment)));
                factorsArray.add(new TripPartFactor(keys.Car_Bike_Parking_At_Transfer_Point,context.getString(R.string.Car_Bike_Parking_At_Transfer_Point)));
                break;
            case ActivityDetected.keys.plane:
                factorsArray.add(new TripPartFactor(keys.Todays_Weather,context.getString(R.string.Todays_Weather)));
                factorsArray.add(new TripPartFactor(keys.Internet_Connectivity,context.getString(R.string.Internet_Connectivity)));
                factorsArray.add(new TripPartFactor(keys.Charging_Opportunity,context.getString(R.string.Charging_Opportunity)));
                factorsArray.add(new TripPartFactor(keys.Tables,context.getString(R.string.Tables)));
                factorsArray.add(new TripPartFactor(keys.Toilets,context.getString(R.string.Toilets)));
                factorsArray.add(new TripPartFactor(keys.Food_Drink_Available,context.getString(R.string.Food_Drink_Available)));
                factorsArray.add(new TripPartFactor(keys.Shopping_Retail,context.getString(R.string.Shopping_Retail)));
                factorsArray.add(new TripPartFactor(keys.Entertainment,context.getString(R.string.Entertainment)));
                factorsArray.add(new TripPartFactor(keys.Car_Bike_Parking_At_Transfer_Point,context.getString(R.string.Car_Bike_Parking_At_Transfer_Point)));
                break;
            case ActivityDetected.keys.intercityTrain:
            case ActivityDetected.keys.highSpeedTrain:
            case ActivityDetected.keys.ferry:
                factorsArray.add(new TripPartFactor(keys.Todays_Weather,context.getString(R.string.Todays_Weather)));
                factorsArray.add(new TripPartFactor(keys.Crowdedness_Seating,context.getString(R.string.Crowdedness_Seating)));
                factorsArray.add(new TripPartFactor(keys.Internet_Connectivity,context.getString(R.string.Internet_Connectivity)));
                factorsArray.add(new TripPartFactor(keys.Charging_Opportunity,context.getString(R.string.Charging_Opportunity)));
                factorsArray.add(new TripPartFactor(keys.Tables,context.getString(R.string.Tables)));
                factorsArray.add(new TripPartFactor(keys.Toilets,context.getString(R.string.Toilets)));
                factorsArray.add(new TripPartFactor(keys.Food_Drink_Available,context.getString(R.string.Food_Drink_Available)));
                factorsArray.add(new TripPartFactor(keys.Shopping_Retail,context.getString(R.string.Shopping_Retail)));
                factorsArray.add(new TripPartFactor(keys.Entertainment,context.getString(R.string.Entertainment)));
                factorsArray.add(new TripPartFactor(keys.Car_Bike_Parking_At_Transfer_Point,context.getString(R.string.Car_Bike_Parking_At_Transfer_Point)));
                break;
            case -1:
                factorsArray.add(new TripPartFactor(keys.Todays_Weather,context.getString(R.string.Todays_Weather)));
                factorsArray.add(new TripPartFactor(keys.Crowdedness_Seating,context.getString(R.string.Crowdedness_Seating)));
                factorsArray.add(new TripPartFactor(keys.Internet_Connectivity,context.getString(R.string.Internet_Connectivity)));
                factorsArray.add(new TripPartFactor(keys.Charging_Opportunity,context.getString(R.string.Charging_Opportunity)));
                factorsArray.add(new TripPartFactor(keys.Tables,context.getString(R.string.Tables)));
                factorsArray.add(new TripPartFactor(keys.Toilets,context.getString(R.string.Toilets)));
                factorsArray.add(new TripPartFactor(keys.Food_Drink_Allowed,context.getString(R.string.Food_Drink_Allowed)));
                factorsArray.add(new TripPartFactor(keys.Food_Drink_Available,context.getString(R.string.Food_Drink_Available)));
                factorsArray.add(new TripPartFactor(keys.Shopping_Retail,context.getString(R.string.Shopping_Retail)));
                factorsArray.add(new TripPartFactor(keys.Entertainment,context.getString(R.string.Entertainment)));
                factorsArray.add(new TripPartFactor(keys.Car_Bike_Parking_At_Transfer_Point,context.getString(R.string.Car_Bike_Parking_At_Transfer_Point)));
                break;

        }
        return factorsArray;
    }

//    //Getting there factors for public transport
//    public static ArrayList<String> getPublicTransportGTFactors(int mode, Context context){
//        ArrayList<String> factorsArray = new ArrayList<>();
//
//        switch (mode){
//            case ActivityDetected.keys.subway:
//            case ActivityDetected.keys.tram:
//            case ActivityDetected.keys.bus:
//            case ActivityDetected.keys.busLongDistance:
//            case ActivityDetected.keys.train:
//            case ActivityDetected.keys.intercityTrain:
//            case ActivityDetected.keys.highSpeedTrain:
//                factorsArray.add(context.getString(R.string.Simplicity_Difficulty_Of_The_Route)));
//                factorsArray.add(context.getString(R.string.Schedule_Reliability)));
//                factorsArray.add(context.getString(R.string.Security_And_Safety)));
//                factorsArray.add(context.getString(R.string.Space_Onboard_For_Lugagge_Pram_Bicycle)));
//                factorsArray.add(context.getString(R.string.Ability_To_Take_Pets_Along)));
//                factorsArray.add(context.getString(R.string.Payment_And_Tickets)));
//                factorsArray.add(context.getString(R.string.Convenient_Access_Lifts_Boarding)));
//                factorsArray.add(context.getString(R.string.Route_Planning_Navigation_Tools)));
//                factorsArray.add(context.getString(R.string.Information_And_Signs)));
//                factorsArray.add(context.getString(R.string.Checkin_Security_And_Boarding)));
//                break;
//            case ActivityDetected.keys.ferry:
//            case ActivityDetected.keys.plane:
//            default:
//                factorsArray.add(context.getString(R.string.Simplicity_Difficulty_Of_The_Route)));
//                factorsArray.add(context.getString(R.string.Schedule_Reliability)));
//                factorsArray.add(context.getString(R.string.Security_And_Safety)));
//                factorsArray.add(context.getString(R.string.Space_Onboard_For_Lugagge_Pram_Bicycle)));
//                factorsArray.add(context.getString(R.string.Ability_To_Take_Pets_Along)));
//                factorsArray.add(context.getString(R.string.Payment_And_Tickets)));
//                factorsArray.add(context.getString(R.string.Convenient_Access_Lifts_Boarding)));
//                factorsArray.add(context.getString(R.string.Route_Planning_Navigation_Tools)));
//                factorsArray.add(context.getString(R.string.Information_And_Signs)));
//                break;
//        }
//        return factorsArray;
//    }

    //Getting there factors for public transport
    public static ArrayList<TripPartFactor> getPublicTransportGTFactors(int mode, Context context){
        ArrayList<TripPartFactor> factorsArray = new ArrayList<>();

        switch (mode){
            case ActivityDetected.keys.subway:
            case ActivityDetected.keys.tram:
            case ActivityDetected.keys.bus:
            case ActivityDetected.keys.busLongDistance:
            case ActivityDetected.keys.train:
            case ActivityDetected.keys.intercityTrain:
            case ActivityDetected.keys.highSpeedTrain:
                factorsArray.add(new TripPartFactor(keys.Simplicity_Difficulty_Of_The_Route,context.getString(R.string.Simplicity_Difficulty_Of_The_Route)));
                factorsArray.add(new TripPartFactor(keys.Schedule_Reliability,context.getString(R.string.Schedule_Reliability)));
                factorsArray.add(new TripPartFactor(keys.Security_And_Safety,context.getString(R.string.Security_And_Safety)));
                factorsArray.add(new TripPartFactor(keys.Space_Onboard_For_Lugagge_Pram_Bicycle,context.getString(R.string.Space_Onboard_For_Lugagge_Pram_Bicycle)));
                factorsArray.add(new TripPartFactor(keys.Ability_To_Take_Pets_Along,context.getString(R.string.Ability_To_Take_Pets_Along)));
                factorsArray.add(new TripPartFactor(keys.Payment_And_Tickets,context.getString(R.string.Payment_And_Tickets)));
                factorsArray.add(new TripPartFactor(keys.Good_Accessibility_Lifts_Boarding,context.getString(R.string.Convenient_Access_Lifts_Boarding)));
                factorsArray.add(new TripPartFactor(keys.Route_Planning_Navigation_Tools,context.getString(R.string.Route_Planning_Navigation_Tools)));
                factorsArray.add(new TripPartFactor(keys.Information_And_Signs,context.getString(R.string.Information_And_Signs)));
                factorsArray.add(new TripPartFactor(keys.Checkin_Security_And_Boarding,context.getString(R.string.Checkin_Security_And_Boarding)));
                break;
            case ActivityDetected.keys.ferry:
            case ActivityDetected.keys.plane:
                factorsArray.add(new TripPartFactor(keys.Simplicity_Difficulty_Of_The_Route,context.getString(R.string.Simplicity_Difficulty_Of_The_Route)));
                factorsArray.add(new TripPartFactor(keys.Schedule_Reliability,context.getString(R.string.Schedule_Reliability)));
                factorsArray.add(new TripPartFactor(keys.Security_And_Safety,context.getString(R.string.Security_And_Safety)));
                factorsArray.add(new TripPartFactor(keys.Space_Onboard_For_Lugagge_Pram_Bicycle,context.getString(R.string.Space_Onboard_For_Lugagge_Pram_Bicycle)));
                factorsArray.add(new TripPartFactor(keys.Ability_To_Take_Pets_Along,context.getString(R.string.Ability_To_Take_Pets_Along)));
                factorsArray.add(new TripPartFactor(keys.Payment_And_Tickets,context.getString(R.string.Payment_And_Tickets)));
                factorsArray.add(new TripPartFactor(keys.Good_Accessibility_Lifts_Boarding,context.getString(R.string.Convenient_Access_Lifts_Boarding)));
                factorsArray.add(new TripPartFactor(keys.Route_Planning_Navigation_Tools,context.getString(R.string.Route_Planning_Navigation_Tools)));
                factorsArray.add(new TripPartFactor(keys.Information_And_Signs,context.getString(R.string.Information_And_Signs)));
                break;
            case -1:
                factorsArray.add(new TripPartFactor(keys.Security_And_Safety,context.getString(R.string.Security_And_Safety)));
                factorsArray.add(new TripPartFactor(keys.Space_Onboard_For_Lugagge_Pram_Bicycle,context.getString(R.string.Space_Onboard_For_Lugagge_Pram_Bicycle)));
                factorsArray.add(new TripPartFactor(keys.Ability_To_Take_Pets_Along,context.getString(R.string.Ability_To_Take_Pets_Along)));
                factorsArray.add(new TripPartFactor(keys.Good_Accessibility_Lifts_Boarding,context.getString(R.string.Convenient_Access_Lifts_Boarding)));
                factorsArray.add(new TripPartFactor(keys.Information_And_Signs,context.getString(R.string.Information_And_Signs)));
                factorsArray.add(new TripPartFactor(keys.Checkin_Security_And_Boarding,context.getString(R.string.Checkin_Security_And_Boarding)));
                factorsArray.add(new TripPartFactor(keys.Benches_Toilets_Etc,context.getString(R.string.Benches_Toilets_Etc)));
                factorsArray.add(new TripPartFactor(keys.Facilities_Shower_Lockers,context.getString(R.string.Facilities_Shower_Lockers)));
                break;
        }
        return factorsArray;
    }

    //Getting there factors for public transport
    public static ArrayList<TripPartFactor> getPublicTransportCPFactors(int mode, Context context){
        ArrayList<TripPartFactor> factorsArray = new ArrayList<>();

        switch (mode){
            case ActivityDetected.keys.subway:
            case ActivityDetected.keys.tram:
            case ActivityDetected.keys.bus:
            case ActivityDetected.keys.busLongDistance:
            case ActivityDetected.keys.train:
            case ActivityDetected.keys.intercityTrain:
            case ActivityDetected.keys.highSpeedTrain:
            case ActivityDetected.keys.ferry:
            case ActivityDetected.keys.plane:
                factorsArray.add(new TripPartFactor(keys.Vehicle_Ride_Smoothness,context.getString(R.string.Vehicle_Ride_Smoothness)));
                factorsArray.add(new TripPartFactor(keys.Seating_Quality_Personal_Space,context.getString(R.string.Seating_Quality_Personal_Space)));
                factorsArray.add(new TripPartFactor(keys.Other_People,context.getString(R.string.Other_People)));
                factorsArray.add(new TripPartFactor(keys.Privacy,context.getString(R.string.Privacy)));
                factorsArray.add(new TripPartFactor(keys.Noise_Level,context.getString(R.string.Noise_Level)));
                factorsArray.add(new TripPartFactor(keys.Air_Quality_Temperature,context.getString(R.string.Air_Quality_Temperature)));
                factorsArray.add(new TripPartFactor(keys.Cleanliness,context.getString(R.string.Cleanliness)));
                factorsArray.add(new TripPartFactor(keys.Urban_Scenery_Atmosphere,context.getString(R.string.Urban_Scenery_Atmosphere)));
                factorsArray.add(new TripPartFactor(keys.Scenery,context.getString(R.string.Scenery)));
                break;
            case -1:
                factorsArray.add(new TripPartFactor(keys.Seating_Quality_Personal_Space,context.getString(R.string.Seating_Quality_Personal_Space)));
                factorsArray.add(new TripPartFactor(keys.Other_People,context.getString(R.string.Other_People)));
                factorsArray.add(new TripPartFactor(keys.Privacy,context.getString(R.string.Privacy)));
                factorsArray.add(new TripPartFactor(keys.Noise_Level,context.getString(R.string.Noise_Level)));
                factorsArray.add(new TripPartFactor(keys.Air_Quality_Temperature,context.getString(R.string.Air_Quality_Temperature)));
                factorsArray.add(new TripPartFactor(keys.Cleanliness,context.getString(R.string.Cleanliness)));
                factorsArray.add(new TripPartFactor(keys.Urban_Scenery_Atmosphere,context.getString(R.string.Urban_Scenery_Atmosphere)));
                factorsArray.add(new TripPartFactor(keys.Scenery,context.getString(R.string.Scenery)));
                factorsArray.add(new TripPartFactor(keys.Lighting_Visibility,context.getString(R.string.Lighting_Visibility)));

                break;
        }
        return factorsArray;
    }

//    /////// ACTIVE TRANSPORT POPULATE FACTORS //////////
//    public static ArrayList<String> getActiveTransportACTFactors(int mode, Context context){
//        ArrayList<String> factorsArray = new ArrayList<>();
//
//        switch (mode){
//            case ActivityDetected.keys.walking:
//            case ActivityDetected.keys.running:
//            case ActivityDetected.keys.wheelChair:
//            case ActivityDetected.keys.microScooter:
//            case ActivityDetected.keys.skate:
//            case ActivityDetected.keys.bicycle:
//            case ActivityDetected.keys.electricBike:
//            case ActivityDetected.keys.cargoBike:
//            case ActivityDetected.keys.bikeSharing:
//            default:
//                factorsArray.add(context.getString(R.string.Ability_To_Do_What_I_Wanted)));
//                break;
//        }
//        return factorsArray;
//    }

    /////// ACTIVE TRANSPORT POPULATE FACTORS //////////
    public static ArrayList<TripPartFactor> getActiveTransportACTFactors(int mode, Context context){
        ArrayList<TripPartFactor> factorsArray = new ArrayList<>();

        switch (mode){
            case ActivityDetected.keys.walking:
            case ActivityDetected.keys.running:
            case ActivityDetected.keys.wheelChair:
            case ActivityDetected.keys.microScooter:
            case ActivityDetected.keys.skate:
            case ActivityDetected.keys.bicycle:
            case ActivityDetected.keys.electricBike:
            case ActivityDetected.keys.cargoBike:
            case ActivityDetected.keys.bikeSharing:
            default:
                factorsArray.add(new TripPartFactor(keys.Ability_To_Do_What_I_Wanted,context.getString(R.string.Ability_To_Do_What_I_Wanted)));
                break;
        }
        return factorsArray;
    }
    


    public static ArrayList<TripPartFactor> getActiveTransportGTFactors(int mode, Context context){
        ArrayList<TripPartFactor> factorsArray = new ArrayList<>();

        switch (mode){
            case ActivityDetected.keys.walking:
            case ActivityDetected.keys.running:
            case ActivityDetected.keys.wheelChair:
            case ActivityDetected.keys.microScooter:
            case ActivityDetected.keys.skate:
                factorsArray.add(new TripPartFactor(keys.Simplicity_Difficulty_Of_The_Route,context.getString(R.string.Simplicity_Difficulty_Of_The_Route)));
                factorsArray.add(new TripPartFactor(keys.Road_Path_Availability_And_Safety,context.getString(R.string.Road_Path_Availability_And_Safety)));
                factorsArray.add(new TripPartFactor(keys.Good_Accessibility_Lifts_Ramps,context.getString(R.string.Accessibility_Escalators_Lifts_Ramps_Stairs_Etc)));
                factorsArray.add(new TripPartFactor(keys.Traffic_Signals_Crossings,context.getString(R.string.Traffic_Signals_Crossings)));
                factorsArray.add(new TripPartFactor(keys.Route_Planning_Navigation_Tools,context.getString(R.string.Route_Planning_Navigation_Tools)));
                factorsArray.add(new TripPartFactor(keys.Information_And_Signs,context.getString(R.string.Information_And_Signs)));
                factorsArray.add(new TripPartFactor(keys.Ability_To_Carry_Bags_Luggage_Etc,context.getString(R.string.Ability_To_Carry_Bags_Luggage_Etc)));
                factorsArray.add(new TripPartFactor(keys.Ability_To_Take_Kids_Or_Pets_Along,context.getString(R.string.Ability_To_Take_Kids_Or_Pets_Along)));
                factorsArray.add(new TripPartFactor(keys.Crowding_Congestion,context.getString(R.string.Crowding_Congestion)));
                factorsArray.add(new TripPartFactor(keys.Schedule_Reliability,context.getString(R.string.Schedule_Reliability)));
                factorsArray.add(new TripPartFactor(keys.Benches_Toilets_Etc,context.getString(R.string.Benches_Toilets_Etc)));
                factorsArray.add(new TripPartFactor(keys.Facilities_Shower_Lockers,context.getString(R.string.Facilities_Shower_Lockers)));
                break;
            case ActivityDetected.keys.bicycle:
            case ActivityDetected.keys.electricBike:
            case ActivityDetected.keys.cargoBike:
            case ActivityDetected.keys.bikeSharing:
                factorsArray.add(new TripPartFactor(keys.Simplicity_Difficulty_Of_The_Route,context.getString(R.string.Simplicity_Difficulty_Of_The_Route)));
                factorsArray.add(new TripPartFactor(keys.Road_Path_Availability_And_Safety,context.getString(R.string.Road_Path_Availability_And_Safety)));
                factorsArray.add(new TripPartFactor(keys.Good_Accessibility_Lifts_Ramps,context.getString(R.string.Accessibility_Escalators_Lifts_Ramps_Stairs_Etc)));
                factorsArray.add(new TripPartFactor(keys.Traffic_Signals_Crossings,context.getString(R.string.Traffic_Signals_Crossings)));
                factorsArray.add(new TripPartFactor(keys.Route_Planning_Navigation_Tools,context.getString(R.string.Route_Planning_Navigation_Tools)));
                factorsArray.add(new TripPartFactor(keys.Information_And_Signs,context.getString(R.string.Information_And_Signs)));
                factorsArray.add(new TripPartFactor(keys.Ability_To_Carry_Bags_Luggage_Etc,context.getString(R.string.Ability_To_Carry_Bags_Luggage_Etc)));
                factorsArray.add(new TripPartFactor(keys.Ability_To_Take_Kids_Or_Pets_Along,context.getString(R.string.Ability_To_Take_Kids_Or_Pets_Along)));
                factorsArray.add(new TripPartFactor(keys.Crowding_Congestion,context.getString(R.string.Crowding_Congestion)));
                factorsArray.add(new TripPartFactor(keys.Schedule_Reliability,context.getString(R.string.Schedule_Reliability)));
                factorsArray.add(new TripPartFactor(keys.Benches_Toilets_Etc,context.getString(R.string.Benches_Toilets_Etc)));
                factorsArray.add(new TripPartFactor(keys.Facilities_Shower_Lockers,context.getString(R.string.Facilities_Shower_Lockers)));
                factorsArray.add(new TripPartFactor(keys.Parking_At_End_Points,context.getString(R.string.Parking_At_End_Points)));
                break;
            default:

        }
        return factorsArray;
    }


    public static ArrayList<TripPartFactor> getActiveTransportCPFactors(int mode, Context context){
        ArrayList<TripPartFactor> factorsArray = new ArrayList<>();

        switch (mode){
            case ActivityDetected.keys.walking:
            case ActivityDetected.keys.running:
            case ActivityDetected.keys.wheelChair:
            case ActivityDetected.keys.microScooter:
            case ActivityDetected.keys.skate:
            case ActivityDetected.keys.bicycle:
            case ActivityDetected.keys.electricBike:
            case ActivityDetected.keys.cargoBike:
            case ActivityDetected.keys.bikeSharing:
            default:
                factorsArray.add(new TripPartFactor(keys.Todays_Weather,context.getString(R.string.Todays_Weather)));
                factorsArray.add(new TripPartFactor(keys.Road_Path_Quality,context.getString(R.string.Road_Path_Quality)));
                factorsArray.add(new TripPartFactor(keys.Road_Path_Directness,context.getString(R.string.Road_Path_Directness)));
                factorsArray.add(new TripPartFactor(keys.Noise_Level,context.getString(R.string.Noise_Level)));
                factorsArray.add(new TripPartFactor(keys.Air_Quality,context.getString(R.string.Air_Quality)));
                factorsArray.add(new TripPartFactor(keys.Lighting_Visibility,context.getString(R.string.Lighting_Visibility)));
                factorsArray.add(new TripPartFactor(keys.Scenery,context.getString(R.string.Scenery)));
                factorsArray.add(new TripPartFactor(keys.Other_People,context.getString(R.string.Other_People)));
                factorsArray.add(new TripPartFactor(keys.Cars_Other_Vehicles,context.getString(R.string.Cars_Other_Vehicles)));
                break;

        }
        return factorsArray;
    }


    /////// PRIVATE TRANSPORT POPULATE FACTORS //////////
    public static ArrayList<TripPartFactor> getPrivateTransportACTFactors(int mode, Context context){
        ArrayList<TripPartFactor> factorsArray = new ArrayList<>();

        switch (mode){
            case ActivityDetected.keys.car:
            case ActivityDetected.keys.carPassenger:
            case ActivityDetected.keys.taxi:
            case ActivityDetected.keys.rideHailing:
            case ActivityDetected.keys.carSharing:
            case ActivityDetected.keys.carSharingPassenger:
            case ActivityDetected.keys.moped:
            case ActivityDetected.keys.motorcycle:
            case ActivityDetected.keys.electricWheelchair:
            default:
                factorsArray.add(new TripPartFactor(keys.Ability_To_Do_What_I_Wanted,context.getString(R.string.Ability_To_Do_What_I_Wanted)));
                break;
        }
        return factorsArray;
    }


    public static ArrayList<TripPartFactor> getPrivateTransportGTFactors(int mode, Context context){
        ArrayList<TripPartFactor> factorsArray = new ArrayList<>();

        switch (mode){
            case ActivityDetected.keys.car:
            case ActivityDetected.keys.carSharing:
            case ActivityDetected.keys.moped:
            case ActivityDetected.keys.motorcycle:
            case ActivityDetected.keys.electricWheelchair:
                factorsArray.add(new TripPartFactor(keys.Simplicity_Difficulty_Of_The_Route,context.getString(R.string.Simplicity_Difficulty_Of_The_Route)));
                factorsArray.add(new TripPartFactor(keys.Traffic_Congestion_Delays,context.getString(R.string.Traffic_Congestion_Delays)));
                factorsArray.add(new TripPartFactor(keys.Schedule_Reliability,context.getString(R.string.Schedule_Reliability)));
                factorsArray.add(new TripPartFactor(keys.Security_And_Safety,context.getString(R.string.Security_And_Safety)));
                factorsArray.add(new TripPartFactor(keys.Space_Onboard_For_Lugagge_Pram_Bicycle,context.getString(R.string.Space_Onboard_For_Lugagge_Pram_Bicycle)));
                factorsArray.add(new TripPartFactor(keys.Ability_To_Take_Kids_Or_Pets_Along,context.getString(R.string.Ability_To_Take_Kids_Or_Pets_Along)));
                factorsArray.add(new TripPartFactor(keys.Route_Planning_Navigation_Tools,context.getString(R.string.Route_Planning_Navigation_Tools)));
                factorsArray.add(new TripPartFactor(keys.Information_And_Signs,context.getString(R.string.Information_And_Signs)));
                factorsArray.add(new TripPartFactor(keys.Parking_At_End_Points,context.getString(R.string.Parking_At_End_Points)));
                break;
            case ActivityDetected.keys.carPassenger:
            case ActivityDetected.keys.carSharingPassenger:
                factorsArray.add(new TripPartFactor(keys.Simplicity_Difficulty_Of_The_Route,context.getString(R.string.Simplicity_Difficulty_Of_The_Route)));
                factorsArray.add(new TripPartFactor(keys.Traffic_Congestion_Delays,context.getString(R.string.Traffic_Congestion_Delays)));
                factorsArray.add(new TripPartFactor(keys.Schedule_Reliability,context.getString(R.string.Schedule_Reliability)));
                factorsArray.add(new TripPartFactor(keys.Security_And_Safety,context.getString(R.string.Security_And_Safety)));
                factorsArray.add(new TripPartFactor(keys.Space_Onboard_For_Lugagge_Pram_Bicycle,context.getString(R.string.Space_Onboard_For_Lugagge_Pram_Bicycle)));
                factorsArray.add(new TripPartFactor(keys.Ability_To_Take_Kids_Or_Pets_Along,context.getString(R.string.Ability_To_Take_Kids_Or_Pets_Along)));
                factorsArray.add(new TripPartFactor(keys.Route_Planning_Navigation_Tools,context.getString(R.string.Route_Planning_Navigation_Tools)));
                factorsArray.add(new TripPartFactor(keys.Parking_At_End_Points,context.getString(R.string.Parking_At_End_Points)));
                break;
            case ActivityDetected.keys.taxi:
            default:
                factorsArray.add(new TripPartFactor(keys.Simplicity_Difficulty_Of_The_Route,context.getString(R.string.Simplicity_Difficulty_Of_The_Route)));
                factorsArray.add(new TripPartFactor(keys.Traffic_Congestion_Delays,context.getString(R.string.Traffic_Congestion_Delays)));
                factorsArray.add(new TripPartFactor(keys.Schedule_Reliability,context.getString(R.string.Schedule_Reliability)));
                factorsArray.add(new TripPartFactor(keys.Security_And_Safety,context.getString(R.string.Security_And_Safety)));
                factorsArray.add(new TripPartFactor(keys.Space_Onboard_For_Lugagge_Pram_Bicycle,context.getString(R.string.Space_Onboard_For_Lugagge_Pram_Bicycle)));
                factorsArray.add(new TripPartFactor(keys.Ability_To_Take_Kids_Or_Pets_Along,context.getString(R.string.Ability_To_Take_Kids_Or_Pets_Along)));
                factorsArray.add(new TripPartFactor(keys.Route_Planning_Navigation_Tools,context.getString(R.string.Route_Planning_Navigation_Tools)));
                break;
        }
        return factorsArray;
    }

    //Get factor by label index
    //0, 1, 2, 3
    public static String getFactorGroup(Context context, int index){
        switch(index){
            case 0:
                return context.getString(R.string.Activities);
            case 1:
                return context.getString(R.string.Comfort_And_Pleasantness);
            case 2:
                return context.getString(R.string.Getting_There);
            case 3:
                return context.getString(R.string.While_You_Ride);
            case 4:
                return context.getString(R.string.While_You_Are_There);
            default:
                return context.getString(R.string.Activities);

        }
    }

//    public static ArrayList<String> getPrivateTransportWYRFactors(int mode, Context context){
//        ArrayList<String> factorsArray = new ArrayList<>();
//
//        switch (mode){
//            case ActivityDetected.keys.car:
//            case ActivityDetected.keys.carPassenger:
//            case ActivityDetected.keys.taxi:
//            case ActivityDetected.keys.rideHailing:
//            case ActivityDetected.keys.carSharing:
//            case ActivityDetected.keys.carSharingPassenger:
//                factorsArray.add(context.getString(R.string.Todays_Weather)));
//                factorsArray.add(context.getString(R.string.Road_Quality_Vehicle_Ride_Smoothness)));
//                factorsArray.add(context.getString(R.string.Vehicle_Quality)));
//                factorsArray.add(context.getString(R.string.Charging_Opportunity)));
//                factorsArray.add(context.getString(R.string.Privacy)));
//                factorsArray.add(context.getString(R.string.Seat_Comfort)));
//                factorsArray.add(context.getString(R.string.Noise_Level)));
//                factorsArray.add(context.getString(R.string.Air_Quality_Temperature)));
//                factorsArray.add(context.getString(R.string.Scenery)));
//                factorsArray.add(context.getString(R.string.Other_Passengers)));
//                factorsArray.add(context.getString(R.string.Other_Cars_Vehicles)));
//                break;
//            case ActivityDetected.keys.moped:
//            case ActivityDetected.keys.motorcycle:
//            case ActivityDetected.keys.wheelChair:
//            default:
//                factorsArray.add(context.getString(R.string.Todays_Weather)));
//                factorsArray.add(context.getString(R.string.Road_Quality_Vehicle_Ride_Smoothness)));
//                factorsArray.add(context.getString(R.string.Vehicle_Quality)));
//                factorsArray.add(context.getString(R.string.Charging_Opportunity)));
//                factorsArray.add(context.getString(R.string.Privacy)));
//                factorsArray.add(context.getString(R.string.Seat_Comfort)));
//                factorsArray.add(context.getString(R.string.Noise_Level)));
//                factorsArray.add(context.getString(R.string.Scenery)));
//                factorsArray.add(context.getString(R.string.Other_Passengers)));
//                factorsArray.add(context.getString(R.string.Other_Cars_Vehicles)));
//                break;
//        }
//        return factorsArray;
//    }

    public static ArrayList<TripPartFactor> getPrivateTransportCPFactors(int mode, Context context){
        ArrayList<TripPartFactor> factorsArray = new ArrayList<>();

        switch (mode){
            case ActivityDetected.keys.car:
            case ActivityDetected.keys.carPassenger:
            case ActivityDetected.keys.taxi:
            case ActivityDetected.keys.rideHailing:
            case ActivityDetected.keys.carSharing:
            case ActivityDetected.keys.carSharingPassenger:
                factorsArray.add(new TripPartFactor(keys.Todays_Weather,context.getString(R.string.Todays_Weather)));
                factorsArray.add(new TripPartFactor(keys.Road_Quality_Vehicle_Ride_Smoothness,context.getString(R.string.Road_Quality_Vehicle_Ride_Smoothness)));
                factorsArray.add(new TripPartFactor(keys.Vehicle_Quality,context.getString(R.string.Vehicle_Quality)));
                factorsArray.add(new TripPartFactor(keys.Charging_Opportunity,context.getString(R.string.Charging_Opportunity)));
                factorsArray.add(new TripPartFactor(keys.Privacy,context.getString(R.string.Privacy)));
                factorsArray.add(new TripPartFactor(keys.Seat_Comfort,context.getString(R.string.Seat_Comfort)));
                factorsArray.add(new TripPartFactor(keys.Noise_Level,context.getString(R.string.Noise_Level)));
                factorsArray.add(new TripPartFactor(keys.Urban_Scenery_Atmosphere,context.getString(R.string.Urban_Scenery_Atmosphere)));
                factorsArray.add(new TripPartFactor(keys.Scenery,context.getString(R.string.Scenery)));
                factorsArray.add(new TripPartFactor(keys.Other_Passengers,context.getString(R.string.Other_Passengers)));
                factorsArray.add(new TripPartFactor(keys.Other_Cars_Vehicles,context.getString(R.string.Other_Cars_Vehicles)));
                break;
            case ActivityDetected.keys.moped:
            case ActivityDetected.keys.motorcycle:
            case ActivityDetected.keys.wheelChair:
            default:
                factorsArray.add(new TripPartFactor(keys.Todays_Weather,context.getString(R.string.Todays_Weather)));
                factorsArray.add(new TripPartFactor(keys.Road_Quality_Vehicle_Ride_Smoothness,context.getString(R.string.Road_Quality_Vehicle_Ride_Smoothness)));
                factorsArray.add(new TripPartFactor(keys.Vehicle_Quality,context.getString(R.string.Vehicle_Quality)));
                factorsArray.add(new TripPartFactor(keys.Charging_Opportunity,context.getString(R.string.Charging_Opportunity)));
                factorsArray.add(new TripPartFactor(keys.Privacy,context.getString(R.string.Privacy)));
                factorsArray.add(new TripPartFactor(keys.Seat_Comfort,context.getString(R.string.Seat_Comfort)));
                factorsArray.add(new TripPartFactor(keys.Noise_Level,context.getString(R.string.Noise_Level)));
                factorsArray.add(new TripPartFactor(keys.Scenery,context.getString(R.string.Scenery)));
                factorsArray.add(new TripPartFactor(keys.Other_Passengers,context.getString(R.string.Other_Passengers)));
                factorsArray.add(new TripPartFactor(keys.Other_Cars_Vehicles,context.getString(R.string.Other_Cars_Vehicles)));
                break;
        }
        return factorsArray;
    }


    //Equals only compares factors code because factor name isn't saved in memory
    //Must only compare factors within the same group (Label)
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TripPartFactor)) {
            return false;
        }
        TripPartFactor other = (TripPartFactor) obj;
        return this.code == other.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name);
    }

    @Override
    public String toString(){
        return "Factor code=" + this.code + ", name=[" + this.name + "]";
    }

    public interface keys {

        static final Map<Integer , Integer> factorsMap = new HashMap<Integer , Integer>() {{
                put(1001, R.string.Ability_To_Do_What_I_Wanted);  //TODO Change String
                put(1101, R.string.Simplicity_Difficulty_Of_The_Route);
                put(1102, R.string.Schedule_Reliability);  //TODO Change String
                put(1103, R.string.Security_And_Safety);
                put(1104, R.string.Space_Onboard_For_Lugagge_Pram_Bicycle);   //TODO Change String
                put(1105, R.string.Ability_To_Take_Kids_Or_Pets_Along);
                put(1106, R.string.Payment_And_Tickets);
                put(1107, R.string.Convenient_Access_Lifts_Boarding);              //TODO Change String
                put(1108, R.string.Route_Planning_Navigation_Tools);
                put(1109, R.string.Information_And_Signs);
                put(1110, R.string.Checkin_Security_And_Boarding);

                put(1201, R.string.Todays_Weather);
                put(1202, R.string.Crowdedness_Seating);
                put(1203, R.string.Internet_Connectivity);
                put(1204, R.string.Charging_Opportunity);
                put(1205, R.string.Tables);
                put(1206, R.string.Toilets);
                put(1207, R.string.Food_Drink_Allowed);
                put(1208, R.string.Food_Drink_Available);
                put(1209, R.string.Shopping_Retail);
                put(1210, R.string.Entertainment);
                put(1211, R.string.Car_Bike_Parking_At_Transfer_Point);

                put(1301, R.string.Vehicle_Ride_Smoothness);
                put(1302, R.string.Seating_Quality_Personal_Space);
                put(1303, R.string.Other_People);
                put(1304, R.string.Privacy);
                put(1305, R.string.Noise_Level);
                put(1306, R.string.Air_Quality_Temperature);
                put(1307, R.string.Cleanliness);
                put(1308, R.string.General_Atmosphere_Design); //TODO Change String
                put(1309, R.string.Scenery);                   //TODO Change String

                put(2001, R.string.Ability_To_Do_What_I_Wanted);

                put(2101, R.string.Simplicity_Difficulty_Of_The_Route);
                put(2102, R.string.Road_Path_Quality);                               //TODO Check string
                put(2103, R.string.Accessibility_Escalators_Lifts_Ramps_Stairs_Etc); //TODO Change String
                put(2104, R.string.Traffic_Signals_Crossings);
                put(2105, R.string.Route_Planning_Navigation_Tools);
                put(2106, R.string.Information_And_Signs);
                put(2107, R.string.Ability_To_Carry_Bags_Luggage_Etc);
                put(2108, R.string.Ability_To_Take_Kids_Or_Pets_Along);
                put(2109, R.string.Crowding_Congestion);
                put(2110, R.string.Predictability_Of_Travel_Time);    //TODO Add String
                put(2111, R.string.Benches_Toilets_Etc);
                put(2112, R.string.Facilities_Shower_Lockers);
                put(2113, R.string.Parking_At_End_Points);

                put(2201, R.string.Todays_Weather);
                put(2202, R.string.Road_Path_Quality);   //TODO Check String
                put(2203, R.string.Road_Path_Directness);
                put(2204, R.string.Noise_Level);
                put(2205, R.string.Air_Quality_Temperature);  //TODO Check String
                put(2206, R.string.Lighting_Visibility);
                put(2207, R.string.Urban_Scenery_Atmosphere); //TODO Add String
                put(2208, R.string.Scenery);                  //TODO Change String
                put(2209, R.string.Other_People);
                put(2210, R.string.Cars_Other_Vehicles);

                put(3001, R.string.Ability_To_Do_What_I_Wanted);

                put(3101, R.string.Simplicity_Difficulty_Of_The_Route);
                put(3102, R.string.Traffic_Congestion_Delays);
                put(3103, R.string.Predictability_Of_Travel_Time);
                put(3104, R.string.Security_And_Safety);
                put(3105, R.string.Space_Onboard_For_Lugagge_Pram_Bicycle);
                put(3106, R.string.Ability_To_Take_Kids_Or_Pets_Along);
                put(3107, R.string.Route_Planning_Navigation_Tools);
                put(3108, R.string.Information_And_Signs);
                put(3109, R.string.Parking_At_End_Points);

                put(3201, R.string.Todays_Weather);
                put(3202, R.string.Road_Quality_Vehicle_Ride_Smoothness);
                put(3203, R.string.Vehicle_Quality);
                put(3204, R.string.Charging_Opportunity);
                put(3205, R.string.Privacy);
                put(3206, R.string.Seat_Comfort);
                put(3207, R.string.Noise_Level);
                put(3208, R.string.Air_Quality_Temperature);
                put(3209, R.string.Urban_Scenery_Atmosphere); //TODO Add String
                put(3210, R.string.Scenery);                  //TODO Change String
                put(3211, R.string.Other_Passengers);
                put(3212, R.string.Other_Cars_Vehicles);
        }};


        ///////////////////////////
        //public transport/transfer
        // //activities
        int Ability_To_Do_What_I_Wanted = 1001;

        // //getting there
        int Simplicity_Difficulty_Of_The_Route = 1101;
        int Schedule_Reliability = 1102;
        int Security_And_Safety = 1103;
        int Space_Onboard_For_Lugagge_Pram_Bicycle = 1104;
        int Ability_To_Take_Pets_Along = 1105;
        int Payment_And_Tickets = 1106;
        int Good_Accessibility_Lifts_Boarding = 1107;
        int Route_Planning_Navigation_Tools = 1108;
        int Information_And_Signs = 1109;
        int Checkin_Security_And_Boarding = 1110;

        // //while you ride
        int Todays_Weather = 1201;
        int Crowdedness_Seating = 1202;
        int Internet_Connectivity = 1203;
        int Charging_Opportunity = 1204;
        int Tables = 1205;
        int Toilets = 1206;
        int Food_Drink_Allowed = 1207;
        int Food_Drink_Available = 1208;
        int Shopping_Retail = 1209;
        int Entertainment = 1210;
        int Car_Bike_Parking_At_Transfer_Point = 1211;

        //Comfort and pleasantness
        int Vehicle_Ride_Smoothness = 1301;
        int Seating_Quality_Personal_Space = 1302;
        int Other_People = 1303;
        int Privacy = 1304;
        int Noise_Level = 1305;
        int Air_Quality_Temperature = 1306;
        int Cleanliness = 1307;
        int Urban_Scenery_Atmosphere = 1308;
        int Scenery = 1309;






        ///////////////////////////
        //Active transport
        // //activities
//        int Ability_To_Do_What_I_Wanted = 2001;

        //getting there
//        int Simplicity_Difficulty_Of_The_Route = 2101;
        int Road_Path_Availability_And_Safety = 2102;
        int Good_Accessibility_Lifts_Ramps = 2103;
        int Traffic_Signals_Crossings = 2104;
//        int Route_Planning_Navigation_Tools = 2105;
//        int Information_And_Signs = 2106;
        int Ability_To_Carry_Bags_Luggage_Etc = 2107;
        int Ability_To_Take_Kids_Or_Pets_Along = 2108;
        int Crowding_Congestion = 2109;
        int Predictability_Travel_Time = 2110;
        int Benches_Toilets_Etc = 2111;
        int Facilities_Shower_Lockers = 2112;
        int Parking_At_End_Points = 2113;

        //Comfort and pleasantness
//        int Todays_Weather = 2201;
        int Road_Path_Quality = 2202;
        int Road_Path_Directness = 2203;
//        int Noise_Level = 2204;
        int Air_Quality = 2205;
        int Lighting_Visibility = 2206;
//        int Urban_Scenery_And_Atmosphere = 2207;
//        int Nature_And_Scenery = 2208;
//        int Other_People = 2209;
        int Cars_Other_Vehicles = 2210;





        ///////////////////////////
        //Private motorized
        // //activities
//        int Ability_To_Do_What_I_Wanted = 3001;

        // getting there
//        int Simplicity_Difficulty_Of_The_Route  = 3101;
        int Traffic_Congestion_Delays = 3102;
        int Predictability_Arrival_Time = 3103;
//        int Security_And_Safety = 3104;
        int Space_For_Luggage_Pram_Bicycle_Etc = 3105;
//        int Ability_To_Take_Kids_Or_Pets_Along = 3106;
//        int Route_Planning_Navigation_Tools = 3107;
//        int Information_And_Signs = 3108;
//        int Parking_At_End_Points = 3109;

        //Comfort and pleasantness
//        int Todays_Weather = 3201;
        int Road_Quality_Vehicle_Ride_Smoothness = 3202;
        int Vehicle_Quality = 3203;
//        int Charging_Opportunity = 3204;
//        int Privacy = 3205;
        int Seat_Comfort = 3206;
//        int Noise_Level = 3207;
//        int Air_Quality_Temperature = 3208;
//        int Urban_Scenery_And_Atmosphere = 3209;
//        int Nature_And_Scenery = 3210;
        int Other_Passengers = 3211;
        int Other_Cars_Vehicles = 3212;


    }

}

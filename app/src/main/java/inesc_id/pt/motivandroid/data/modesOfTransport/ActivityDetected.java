package inesc_id.pt.motivandroid.data.modesOfTransport;

import android.content.Context;
import android.util.Log;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

import inesc_id.pt.motivandroid.ApplicationClass;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.myTrips.fragments.LegValidationFragment;
import inesc_id.pt.motivandroid.onboarding.adapters.ModesOfTransportUsedAdapter;
import inesc_id.pt.motivandroid.onboarding.wrappers.ModeOfTransportUsed;

/**
 *
 * ActivityDetected
 *
 *      Data structure for modes of transport detected. "type" field corresponds to the id of the
 *  mode of transport, and the confidence level corresponds to the degree of certainty (from 1 to 100)
 *
 *  * (C) 2017-2020 - The Woorti app is a research (non-commercial) application that was
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

public class ActivityDetected implements Serializable {

    @Expose
    int type;
    @Expose
    int confidenceLevel;

    public ActivityDetected(int type, int confidenceLevel) {
        this.type = type;
        this.confidenceLevel = confidenceLevel;
    }

    public int getType() {
        return type;
    }

//    public void setType(int type) {
//        this.type = type;
//    }

    public int getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(int confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public static int getRealModalityValue(String modality){

        int i = 0;
        for (String m : keys.modalities){
            if(m.equals(modality)){
                return i;
            }
            i++;
        }
        return keys.inexistent;
    }

    public static int getSurveyModalityValue(String realModality){

        if(realModality.equals(keys.modalities[keys.vehicle])){
            int i = 0;
            for (String m : keys.surveyModalities){

                if(m.equals(keys.modalities[keys.car])){
                    return i;
                }
                i++;
            }

        }else if(realModality.equals(keys.modalities[keys.tilting]) || realModality.equals(keys.modalities[keys.unknown])){
            int i = 0;
            for (String m : keys.surveyModalities){

                if(m.equals(keys.modalities[keys.other])){
                    return i;
                }
                i++;
            }

        }else if(realModality.equals(keys.modalities[keys.still])){
        int i = 0;
        for (String m : keys.surveyModalities){

            if(m.equals(keys.modalities[keys.train])){
                return i;
            }
            i++;
        }

    }

        int i = 0;
        for (String m : keys.surveyModalities){

            if(m.equals(realModality)){
                return i;
            }
            i++;
        }
        //default behaviour
        return 10;
    }

    public static int getTransportIconInt(String transport){
        switch(transport){
            case "car":
                return R.drawable.ic_directions_car_black_24dp;
            case "vehicule":
                return R.drawable.ic_directions_car_black_24dp;
            case "bicycle":
                return  R.drawable.ic_directions_bike_black_24dp;
            case "vehicle":
                return R.drawable.ic_directions_car_black_24dp;
            case "bus":
                return R.drawable.ic_directions_bus_black_24dp;
            case "train":
                return R.drawable.ic_train_black_24dp;
            case "walking":
                return R.drawable.ic_directions_walk_black_24dp;
            case "boat":
                return R.drawable.ic_directions_boat_black_24dp;
            case "bike":
                return R.drawable.ic_directions_bike_black_24dp;
            case "subway":
                return R.drawable.ic_directions_subway_black_24dp;
            case "walk":
                return R.drawable.ic_directions_walk_black_24dp;
            case "tram":
                return R.drawable.ic_tram_black_24dp;
            case "metro":
                return R.drawable.ic_tram_black_24dp;

        }
        //by default - todo
        return R.drawable.ic_add_black_24dp;
    }

    public static int getTransportIconBubbleFromInt(int transport){
        switch(transport){
            case keys.vehicle:
                return R.drawable.mytrips_transports_private_car_driver_bubble;
            case keys.car:
                return R.drawable.mytrips_transports_private_car_driver_bubble;
            case keys.bus:
                return R.drawable.mytrips_transports_bus_bubble;
            case keys.train:
                return R.drawable.mytrips_transports_urban_train_bubble;
            case keys.walking:
                return R.drawable.mytrips_transports_walking_bubble;
            case keys.ferry:
                return R.drawable.mytrips_transports_ferry_boat_bubble;
            case keys.bicycle:
                return R.drawable.mytrips_transports_bicycle_bubble;
            case keys.subway:
                return R.drawable.mytrips_transports_metro_bubble;
            case keys.onfoot:
                return R.drawable.mytrips_transports_walking_bubble;
            case keys.tram:
                return R.drawable.mytrips_transports_tram_bubble;
            case keys.plane:
                return R.drawable.mytrips_transports_airplane_bubble;
            case keys.electricBike:
                return R.drawable.mytrips_transports_electric_bicycle_bubble;
            case keys.bikeSharing:
                return R.drawable.mytrips_transports_bike_sharing_bubble;
            case keys.microScooter:
                return R.drawable.mytrips_transports_micro_scooter_bubble;
            case keys.skate:
                return R.drawable.mytrips_transports_skateboard_bubble;
            case keys.motorcycle:
                return R.drawable.mytrips_transports_motorcycle_bubble;
            case keys.moped:
                return R.drawable.mytrips_transports_moped_bubble;
            case keys.carPassenger:
                return R.drawable.mytrips_transports_private_car_passenger_bubble;
            case keys.taxi:
                return R.drawable.mytrips_transports_taxi_bubble;
            case keys.rideHailing:
                return R.drawable.mytrips_transports_taxi_bubble;
            case keys.carSharing:
                return R.drawable.mytrips_transports_car_sharing_driver_bubble;
            case keys.carpooling:
                return R.drawable.mytrips_transports_car_sharing_passenger_bubble;
            case keys.other:
            case keys.otherActive:
            case keys.otherPrivate:
            case keys.otherPublic:
                return R.drawable.mytrips_transports_other_bubble;
            case keys.busLongDistance:
                return R.drawable.mytrips_transports_coach_bubble;
            case keys.highSpeedTrain:
                return R.drawable.mytrips_transports_high_speed_train_bubble;
            case keys.intercityTrain:
                return R.drawable.mytrips_transports_regional_intercity_train_bubble;
            case keys.wheelChair:
                return R.drawable.mytrips_transports_wheelchair_bubble;
            case keys.cargoBike:
                return R.drawable.mytrips_transports_cargo_bike_bubble;
            case keys.carSharingPassenger:
                return R.drawable.mytrips_transports_car_sharing_passenger_bubble;
            case keys.electricWheelchair:
                return R.drawable.mytrips_transports_electric_wheelchair_bubble;
            case keys.running:
                return R.drawable.mytrips_transports_jogging_bubble;
            //TODO Complete icon list
            default:
                return R.drawable.right_arrow_between_transport_icons;

        }
    }

    public static int getTransportIconButtonFromInt(int transport){
        switch(transport){
            case keys.vehicle:
                return R.drawable.mytrips_transports_private_car_driver_button;
            case keys.car:
                return R.drawable.mytrips_transports_private_car_driver_button;
            case keys.bus:
                return R.drawable.mytrips_transports_bus_button;
            case keys.train:
                return R.drawable.mytrips_transports_urban_train_button;
            case keys.walking:
                return R.drawable.mytrips_transports_walking_button;
            case keys.ferry:
                return R.drawable.mytrips_transports_ferry_boat_button;
            case keys.bicycle:
                return R.drawable.mytrips_transports_bicycle_button;
            case keys.subway:
                return R.drawable.mytrips_transports_metro_button;
            case keys.onfoot:
                return R.drawable.mytrips_transports_walking_button;
            case keys.tram:
                return R.drawable.mytrips_transports_tram_button;
            case keys.plane:
                return R.drawable.mytrips_transports_airplane_button;
            case keys.electricBike:
                return R.drawable.mytrips_transports_electric_bicycle_button;
            case keys.bikeSharing:
                return R.drawable.mytrips_transports_bike_sharing_button;
            case keys.microScooter:
                return R.drawable.mytrips_transports_micro_scooter_button;
            case keys.skate:
                return R.drawable.mytrips_transports_skateboard_button;
            case keys.motorcycle:
                return R.drawable.mytrips_transports_motorcycle_button;
            case keys.moped:
                return R.drawable.mytrips_transports_moped_button;
            case keys.carPassenger:
                return R.drawable.mytrips_transports_private_car_passenger_button;
            case keys.taxi:
                return R.drawable.mytrips_transports_taxi_button;
            case keys.rideHailing:
                return R.drawable.mytrips_transports_taxi_button;
            case keys.carSharing:
                return R.drawable.mytrips_transports_car_sharing_driver_button;
            case keys.carpooling:
                return R.drawable.mytrips_transports_car_sharing_passenger_button;
            case keys.other:
            case keys.otherActive:
            case keys.otherPrivate:
            case keys.otherPublic:
                return R.drawable.mytrips_transports_other_button;
            case keys.busLongDistance:
                return R.drawable.mytrips_transports_coach_button;
            case keys.highSpeedTrain:
                return R.drawable.mytrips_transports_high_speed_train_button;
            case keys.intercityTrain:
                return R.drawable.mytrips_transports_regional_intercity_train_button;
            case keys.wheelChair:
                return R.drawable.mytrips_transports_wheelchair_button;
            case keys.cargoBike:
                return R.drawable.mytrips_transports_cargo_bike_button;
            case keys.carSharingPassenger:
                return R.drawable.mytrips_transports_car_sharing_passenger_button;
            case keys.electricWheelchair:
                return R.drawable.mytrips_transports_electric_wheelchair_button;
            case keys.running:
                return R.drawable.mytrips_transports_jogging_button;
            //TODO Complete icon list
            default:
                return R.drawable.right_arrow_between_transport_icons;

        }
    }

    public static int getTransportIconFromInt(int transport){
        switch(transport){
            case keys.vehicle:
                return R.drawable.mytrips_transports_normal_private_car_driver;
            case keys.car:
                return R.drawable.mytrips_transports_normal_private_car_driver;
            case keys.bus:
                return R.drawable.mytrips_transports_normal_bus;
            case keys.train:
                return R.drawable.mytrips_transports_normal_urban_train;
            case keys.walking:
                return R.drawable.mytrips_transports_normal_walking;
            case keys.ferry:
                return R.drawable.mytrips_transports_normal_ferry_boat;
            case keys.bicycle:
                return R.drawable.mytrips_transports_normal_bicycle;
            case keys.subway:
                return R.drawable.mytrips_transports_normal_metro;
            case keys.onfoot:
                return R.drawable.mytrips_transports_normal_walking;
            case keys.tram:
                return R.drawable.mytrips_transports_normal_tram;
            case keys.plane:
                return R.drawable.mytrips_transports_normal_airplane;
            case keys.electricBike:
                 return R.drawable.mytrips_transports_normal_electric_bike;
            case keys.bikeSharing:
                return R.drawable.mytrips_transports_normal_bike_sharing;
            case keys.microScooter:
                return R.drawable.mytrips_transports_normal_micro_scooter;
            case keys.skate:
                return R.drawable.mytrips_transports_normal_skateboard;
            case keys.motorcycle:
                return R.drawable.mytrips_transports_normal_motorcycle;
            case keys.moped:
                return R.drawable.mytrips_transports_normal_moped;
            case keys.carPassenger:
                return R.drawable.mytrips_transports_normal_private_car_passenger;
            case keys.taxi:
                return R.drawable.mytrips_transports_normal_taxi;
            case keys.rideHailing:
                return R.drawable.mytrips_transports_normal_taxi;
            case keys.carSharing:
                return R.drawable.mytrips_transports_normal_car_sharing_driver;
            case keys.carpooling:
                return R.drawable.mytrips_transports_normal_car_sharing_passenger;
            case keys.other:
            case keys.otherActive:
            case keys.otherPrivate:
            case keys.otherPublic:
                return R.drawable.mytrips_transports_normal_other;
            case keys.busLongDistance:
                return R.drawable.mytrips_transports_normal_coach;
            case keys.highSpeedTrain:
                return R.drawable.mytrips_transports_normal_high_speed_train;
            case keys.intercityTrain:
                return R.drawable.mytrips_transports_normal_regional_intercity_train;
            case keys.wheelChair:
                return R.drawable.mytrips_transports_normal_wheelchair;
            case keys.cargoBike:
                return R.drawable.mytrips_transports_normal_cargo_bike;
            case keys.carSharingPassenger:
                return R.drawable.mytrips_transports_normal_car_sharing_passenger;
            case keys.electricWheelchair:
                return R.drawable.mytrips_transports_normal_electric_wheelchair;
            case keys.running:
                return R.drawable.mytrips_transports_normal_jogging;
            //TODO Complete icon list
            default:
                 return R.drawable.right_arrow_between_transport_icons;

        }
    }

    public static int getTransportIconFadedFromInt(int transport){
        switch(transport){
            case keys.vehicle:
                return R.drawable.mytrips_transports_faded_private_car_driver_orange;
            case keys.car:
                return R.drawable.mytrips_transports_faded_private_car_driver_orange;
            case keys.bus:
                return R.drawable.mytrips_transports_faded_bus_orange;
            case keys.train:
                return R.drawable.mytrips_transports_faded_urban_train_orange;
            case keys.walking:
                return R.drawable.mytrips_transports_faded_walking_orange;
            case keys.ferry:
                return R.drawable.mytrips_transports_faded_ferry_boat_orange;
            case keys.bicycle:
                return R.drawable.mytrips_transports_faded_bike_sharing_orange;
            case keys.subway:
                return R.drawable.mytrips_transports_faded_metro_orange;
            case keys.onfoot:
                return R.drawable.mytrips_transports_faded_walking_orange;
            case keys.tram:
                return R.drawable.mytrips_transports_faded_tram_orange;
            case keys.plane:
                return R.drawable.mytrips_transports_faded_airplane_orange;
            case keys.electricBike:
                return R.drawable.mytrips_transports_faded_electric_bike_orange;
            case keys.bikeSharing:
                return R.drawable.mytrips_transports_faded_bike_sharing_orange;
            case keys.microScooter:
                return R.drawable.mytrips_transports_faded_micro_scooter_orange;
            case keys.skate:
                return R.drawable.mytrips_transports_faded_skateboard_orange;
            case keys.motorcycle:
                return R.drawable.mytrips_transports_faded_motorcycle_orange;
            case keys.moped:
                return R.drawable.mytrips_transports_faded_moped_orange;
            case keys.carPassenger:
                return R.drawable.mytrips_transports_faded_private_car_passenger_orange;
            case keys.taxi:
                return R.drawable.mytrips_transports_faded_taxi_orange;
            case keys.rideHailing:
                return R.drawable.mytrips_transports_faded_taxi_orange;
            case keys.carSharing:
                return R.drawable.mytrips_transports_faded_car_sharing_driver_orange;
            case keys.carpooling:
                return R.drawable.mytrips_transports_faded_car_sharing_passenger_orange;
            case keys.other:
            case keys.otherActive:
            case keys.otherPrivate:
            case keys.otherPublic:
                return R.drawable.mytrips_transports_faded_other_orange;
            case keys.busLongDistance:
                return R.drawable.mytrips_transports_faded_coach_orange;
            case keys.highSpeedTrain:
                return R.drawable.mytrips_transports_faded_high_speed_train_orange;
            case keys.intercityTrain:
                return R.drawable.mytrips_transports_faded_regional_intercity_train_orange;
            case keys.wheelChair:
                return R.drawable.mytrips_transports_faded_wheelchair_orange;
            case keys.cargoBike:
                return R.drawable.mytrips_transports_faded_cargo_bike_orange;
            case keys.carSharingPassenger:
                return R.drawable.mytrips_transports_faded_car_sharing_passenger_orange;
            case keys.electricWheelchair:
                return R.drawable.mytrips_transports_faded_electric_wheelchair_orange;
            case keys.running:
                return R.drawable.mytrips_transports_faded_jogging_orange;
            //TODO Complete icon list
            default:
                return R.drawable.right_arrow_between_transport_icons;

        }
    }


    public static String getTransportLabel(int transport){
        switch(transport){
            case keys.subway:
            case keys.tram:
            case keys.bus:
            case keys.busLongDistance:
            case keys.train:
            case keys.intercityTrain:
            case keys.highSpeedTrain:
            case keys.ferry:
            case keys.plane:
            case keys.otherPublic:
                return keys.PUBLIC_TRANSPORT;
            case keys.walking:
            case keys.running:
            case keys.wheelChair:
            case keys.bicycle:
            case keys.electricBike:
            case keys.cargoBike:
            case keys.bikeSharing:
            case keys.microScooter:
            case keys.otherActive:
                return keys.ACTIVE_TRANSPORT;
            case keys.car:
            case keys.carPassenger:
            case keys.taxi:
            case keys.carSharing:
            case keys.carSharingPassenger:
            case keys.moped:
            case keys.motorcycle:
            case keys.electricWheelchair:
            case keys.otherPrivate:
                return keys.PRIVATE_TRANSPORT;
            default:
                return "Public Transport";
            //TODO Add more transports!

        }
    }

    public static int getTransportCategoryOfTransport(int transport){
        switch(transport){
            case keys.subway:
            case keys.tram:
            case keys.bus:
            case keys.busLongDistance:
            case keys.train:
            case keys.intercityTrain:
            case keys.highSpeedTrain:
            case keys.ferry:
            case keys.plane:
            case keys.otherPublic:
                return LegValidationFragment.keys.PUBLIC_TRANSPORT_TAB;
            case keys.walking:
            case keys.running:
            case keys.wheelChair:
            case keys.bicycle:
            case keys.electricBike:
            case keys.cargoBike:
            case keys.bikeSharing:
            case keys.microScooter:
            case keys.skate:
            case keys.otherActive:
                return LegValidationFragment.keys.ACTIVE_TRANSPORT_TAB;
            case keys.car:
            case keys.carPassenger:
            case keys.taxi:
            case keys.carSharing:
            case keys.carSharingPassenger:
            case keys.moped:
            case keys.motorcycle:
            case keys.electricWheelchair:
            case keys.otherPrivate:
                return LegValidationFragment.keys.PRIVATE_MOTORISED_TRANSPORT_TAB;
            default:
                return LegValidationFragment.keys.PUBLIC_TRANSPORT_TAB;
            //TODO Add more transports!

        }
    }

    public static String getFormalTransportName(int mode){
        return keys.transportName[mode];
    }

    public static String getFormalTransportNameWithContext(int mode, Context context){



        return context.getString(keys.transportNameStringCodes[mode]);
    }

    public interface keys{

        String[] modalities = {"vehicle","bicycle","onfoot",
                               "still","unknown","tilting",
                                "inexistant","walking", "running",
                                "car","train","tram",
                                 "subway","ferry","plane", "bus", "electricBike","bikeSharing",
                                "microScooter", "skate", "motorcycle", "moped", "carPassenger",
                                "taxi", "rideHailing", "carSharing", "carpooling",
                                "busLongDistance", "highSpeedTrain", "other", "otherPublic",
                                "otherActive", "otherPrivate", "intercity", "wheelChair", "cargoBike", "carSharingPassenger", "electricWheelchair" };

        int[] transportNameStringCodes = {
                        R.string.Bicycle, // vehicle inexistant
                R.string.Bicycle,
                        R.string.Bicycle,  //onfoot inexistant
                        R.string.Bicycle,   //still inexistant
                        R.string.Bicycle, //unknown inexistant
                        R.string.Bicycle, //tilting inexsitent
                        R.string.Walking,
                R.string.Walking,
                R.string.Jogging_Running,
                R.string.Car_Driver,
                R.string.Urban_Train,
                R.string.Tram,
                R.string.Metro,
                R.string.Ferry_Boat,
                R.string.Plane,
                R.string.Bus_Trolley_Bus,
                R.string.Electric_Bike,
                R.string.Bike_Sharing,
                R.string.Micro_Scooter,
                R.string.Skate,
                R.string.Motorcycle,
                R.string.Moped,
                R.string.Car_Passenger,
                R.string.Taxi_Ride_Hailing,
                        R.string.Car_Sharing_Rental_Driver,                                                              //deprecated
                R.string.Car_Sharing_Rental_Driver,
                        R.string.Car_Sharing_Rental_Driver,                                                               //deprecated
                R.string.Coach_Long_Distance_Bus,
                R.string.high_speed_train,
                R.string.Other,
                R.string.Other_Public_Transport,
                R.string.Other_Active_Semi_Active,
                R.string.Other_Private_Motorised,
                R.string.Regional_Intercity_Train,
                R.string.Wheelchair,
                R.string.Cargo_Bike,
                R.string.Car_Sharing_Rental_Passenger,
                R.string.Electric_Wheelchair_Cart
        };

        String[] transportName = {
                "vehicle",
                ApplicationClass.resources.getString(R.string.Bicycle),
                "onfoot",
                "still",
                "unknown",
                "tilting",
                "inexistant",
                ApplicationClass.resources.getString(R.string.Walking),
                ApplicationClass.resources.getString(R.string.Jogging_Running),
                ApplicationClass.resources.getString(R.string.Car_Driver),
                ApplicationClass.resources.getString(R.string.Urban_Train),
                ApplicationClass.resources.getString(R.string.Tram),
                ApplicationClass.resources.getString(R.string.Metro),
                ApplicationClass.resources.getString(R.string.Ferry_Boat),
                ApplicationClass.resources.getString(R.string.Plane),
                ApplicationClass.resources.getString(R.string.Bus_Trolley_Bus),
                ApplicationClass.resources.getString(R.string.Electric_Bike),
                ApplicationClass.resources.getString(R.string.Bike_Sharing),
                ApplicationClass.resources.getString(R.string.Micro_Scooter),
                ApplicationClass.resources.getString(R.string.Skate),
                ApplicationClass.resources.getString(R.string.Motorcycle),
                ApplicationClass.resources.getString(R.string.Moped),
                ApplicationClass.resources.getString(R.string.Car_Passenger),
                ApplicationClass.resources.getString(R.string.Taxi_Ride_Hailing),
                "rideHailing",                                                              //deprecated
                ApplicationClass.resources.getString(R.string.Car_Sharing_Rental_Driver),
                "carpooling",                                                               //deprecated
                ApplicationClass.resources.getString(R.string.Coach_Long_Distance_Bus),
                ApplicationClass.resources.getString(R.string.high_speed_train),
                ApplicationClass.resources.getString(R.string.Other),
                ApplicationClass.resources.getString(R.string.Other_Public_Transport),
                ApplicationClass.resources.getString(R.string.Other_Active_Semi_Active),
                ApplicationClass.resources.getString(R.string.Other_Private_Motorised),
                ApplicationClass.resources.getString(R.string.Regional_Intercity_Train),
                ApplicationClass.resources.getString(R.string.Wheelchair),
                ApplicationClass.resources.getString(R.string.Cargo_Bike),
                ApplicationClass.resources.getString(R.string.Car_Sharing_Rental_Passenger),
                ApplicationClass.resources.getString(R.string.Electric_Wheelchair_Cart)
        };


        /**
         * id of each mode of transport
         */

        int vehicle = 0;
        int bicycle = 1;
        int onfoot = 2;
        int still = 3;
        int unknown = 4;
        int tilting = 5;
        int inexistent = 6;
        int walking = 7;
        int running = 8;
        int car = 9;
        int train = 10;
        int tram = 11;
        int subway = 12;
        int ferry = 13;
        int plane = 14;
        int bus = 15;
        int electricBike = 16;
        int bikeSharing = 17;
        int microScooter = 18;
        int skate = 19;
        int motorcycle = 20;
        int moped = 21;
        int carPassenger = 22;
        int taxi = 23;
        int rideHailing = 24;
        int carSharing = 25;
        int carpooling = 26;
        int busLongDistance = 27;
        int highSpeedTrain = 28;
        int other = 29;
        int otherPublic = 30;
        int otherActive = 31;
        int otherPrivate = 32;
        int intercityTrain = 33;
        int wheelChair = 34;
        int cargoBike = 35;
        int carSharingPassenger = 36;
        int electricWheelchair = 37;

        String[] surveyModalities = {modalities[walking],
                                    modalities[running],
                                    modalities[bicycle],
                                    modalities[bus],
                                    modalities[train],
                                    modalities[car],
                                    modalities[subway],
                                    modalities[tram],
                                    modalities[ferry],
                                    modalities[plane],
                                    modalities[other]

        };

        String PUBLIC_TRANSPORT = "Public Transport";
        String ACTIVE_TRANSPORT = "Active/Semi-active";
        String PRIVATE_TRANSPORT = "Private Motorised";

        int PRODUCTIVITY_TYPE = 0;
        int ENJOYMENT_TYPE = 1;
        int FITNESS_TYPE = 2;

    }

    public static boolean isModeValid(int modeToCheck){

        switch (modeToCheck){
            case keys.bicycle:
            case keys.walking:
            case keys.running:
            case keys.car:
            case keys.train:
            case keys.tram:
            case keys.subway:
            case keys.ferry:
            case keys.plane:
            case keys.bus:
            case keys.electricBike:
            case keys.bikeSharing:
            case keys.microScooter:
            case keys.skate:
            case keys.motorcycle:
            case keys.moped:
            case keys.carPassenger:
            case keys.taxi:
            case keys.carSharing:
            case keys.busLongDistance:
            case keys.highSpeedTrain:
            case keys.otherPublic:
            case keys.otherActive:
            case keys.otherPrivate:
            case keys.intercityTrain:
            case keys.wheelChair:
            case keys.cargoBike:
            case keys.carSharingPassenger:
            case keys.electricWheelchair:
                return true;
        }

        return false;

    }


    /**
     * @param mode
     * @param userModes
     * @return computed worthwhileness weight for the specified mode, according to the preferences
     *  asssigned to each mode of transport by the user
     */
    public static WorthwhilenessValues getWorthwhilenessWeightForMode(int mode, ArrayList<ModeOfTransportUsed> userModes){

        if(userModes != null && userModes.size() > 0){

            for (ModeOfTransportUsed userMode : userModes){

                if(userMode.getModalityIntCode() == mode){

                    return new WorthwhilenessValues(userMode.getProductiveRating(), userMode.getEnjoymentRating(), userMode.getFitnessRating());

                }

            }

        }

        return getWorthwhilenessDefaultWeightforMode(mode);

    }

    /**
     * @param mode
     * @return worthwhileness score for the mode, using default preference values
     */
    public static WorthwhilenessValues getWorthwhilenessDefaultWeightforMode(int mode){



        switch(mode){

            case keys.subway:
            case keys.train:
                return new WorthwhilenessValues(70, 70, 10);

            case keys.tram:

            return new WorthwhilenessValues(50, 60, 10);

            case keys.bus:
            case keys.busLongDistance:

                return new WorthwhilenessValues(40, 50, 10);

            case keys.intercityTrain:
            case keys.highSpeedTrain:

                return new WorthwhilenessValues(80, 80, 10);

            case keys.ferry:

                return new WorthwhilenessValues(60, 70, 10);

            case keys.plane:

                return new WorthwhilenessValues(60, 50, 0);

            case keys.walking:
            case keys.wheelChair:

                return new WorthwhilenessValues(20, 70, 90);

            case keys.running:

                return new WorthwhilenessValues(10, 70, 100);

            case keys.bicycle:
            case keys.electricBike:
            case keys.cargoBike:
            case keys.bikeSharing:
            case keys.microScooter:
            case keys.skate:

                return new WorthwhilenessValues(10, 70, 80);

            case keys.car:
            case keys.carSharing:

                return new WorthwhilenessValues(20, 40, 0);

            case keys.carPassenger:
            case keys.carSharingPassenger:
            case keys.taxi:

                return new WorthwhilenessValues(40, 40, 0);

            case keys.moped:
            case keys.motorcycle:

                return new WorthwhilenessValues(0, 40, 0);

            case keys.electricWheelchair:

            return new WorthwhilenessValues(20, 70, 10);

            default:

                break;

        }

        return new WorthwhilenessValues(0, 0, 0);

    }

    /**
     * @param distance (in meters)
     * @param mode of transport
     * @return computed co2 (in grams)
     */

    public static double getCO2ValueForDistanceAndMode(long distance, int mode){

            double carbonPerKm;

            switch (mode){

                case keys.electricBike:
                    carbonPerKm = 6.0;
                break;

                case keys.microScooter:
                    carbonPerKm = 12.0;
                break;

                case keys.subway:
                case keys.tram:
                case keys.train:
                case keys.intercityTrain:
                    carbonPerKm = 14.0;
                break;

                case keys.electricWheelchair:
                    carbonPerKm = 15.0;
                break;

                case keys.highSpeedTrain:
                    carbonPerKm = 25.0;
                break;

                case keys.moped:
                    carbonPerKm = 60.0;
                break;

                case keys.bus:
                case keys.busLongDistance:
                    carbonPerKm = 68.0;
                break;

                case keys.carPassenger:
                case keys.carSharingPassenger:
                case keys.motorcycle:
                    carbonPerKm = 80.0;
                break;

                case keys.rideHailing:
                    carbonPerKm = 100.0;
                break;

                case keys.car:
                case keys.carSharing:
                    carbonPerKm = 120.0;
                break;

                case keys.ferry:
                    carbonPerKm = 256.5;
                break;

                case keys.plane:
                    carbonPerKm = 285;
                break;

                default:
                    return 0.0;
            }

            return carbonPerKm * (distance/1000.0);

    }

    /**
     * @param distance (in meters)
     * @param mode of transport
     * @return computed co2 (in grams)
     */

    //co2 (carbon footprint)
    public static double getCO2ValueForDistanceAndMode(double distance, int mode){

        double carbonPerKm;

        switch (mode){

            case keys.electricBike:
                carbonPerKm = 6.0;
                break;

            case keys.microScooter:
                carbonPerKm = 12.0;
                break;

            case keys.subway:
            case keys.tram:
            case keys.train:
            case keys.intercityTrain:
                carbonPerKm = 14.0;
                break;

            case keys.electricWheelchair:
                carbonPerKm = 15.0;
                break;

            case keys.highSpeedTrain:
                carbonPerKm = 25.0;
                break;

            case keys.moped:
                carbonPerKm = 60.0;
                break;

            case keys.bus:
            case keys.busLongDistance:
                carbonPerKm = 68.0;
                break;

            case keys.carPassenger:
            case keys.carSharingPassenger:
            case keys.motorcycle:
                carbonPerKm = 80.0;
                break;

            case keys.rideHailing:
                carbonPerKm = 100.0;
                break;

            case keys.car:
            case keys.carSharing:
                carbonPerKm = 120.0;
                break;

            case keys.ferry:
                carbonPerKm = 256.5;
                break;

            case keys.plane:
                carbonPerKm = 285;
                break;

            default:
                return 0.0;
        }

        return carbonPerKm * (distance/1000.0);

    }

    //calories

    /**
     * @param mode of transport
     * @param distance (meters)
     * @return computed ammount of calories burnt for the provided distance and mode
     */
    public static double getCaloriesFromModeAndDistance(int mode, double distance){

            if (distance == 0){
                return 0;
            }

            int calsPerKms = 0;

            switch (mode) {

            case keys.electricBike:

                calsPerKms = 13;
            break;

            case keys.bicycle:
            case keys.bikeSharing:
            case keys.skate:

                calsPerKms = 26;
            break;

            case keys.cargoBike:

                calsPerKms = 39;
            break;

            case keys.walking:
            case keys.wheelChair:

                calsPerKms = 45;
            break;

            case keys.running:

                calsPerKms = 90;
            break;
            }

            return (distance/1000)* calsPerKms;

    }

    //get the factor c for (prod, enj, fitness)
//    WorthwhilenessValues getDefaultWorthwhilenessWeight() {
//
//        return new WorthwhilenessValues(40, 40 ,20);
//
//    }


    //productive

    /**
     * @param duration (minutes)
     * @param modeOfTransport
     * @param type of category desired (productivity, enjoyment or fitness score)
     * @param modes array containing the user's values assigned for productivity, enjoyment, fit-
     *              for each mode
     * @return computed worthwhileness score for a leg for the desired category ((productivity,
     *              enjoyment or fitness score)
     */
    //duration in minutes
    public static double getProdEnjoyFitnessWorthFromLeg(double duration, int modeOfTransport, int type, ArrayList<ModeOfTransportUsed> modes){

        WorthwhilenessValues modalityValues = ActivityDetected.getWorthwhilenessWeightForMode(modeOfTransport, modes);

        float weight = 0;

        switch(type){

            case keys.PRODUCTIVITY_TYPE:

                weight = modalityValues.getProductivityValue()/100;
                break;

            case keys.ENJOYMENT_TYPE:

                weight = modalityValues.getEnjoymentValue()/100;
                break;

            case keys.FITNESS_TYPE:

                weight = modalityValues.getFitnessValue()/100;
                break;

        }

        Log.e("TripStats", "duration " + duration + " weight " + weight);

        return duration * weight;

    }

    /**
     * @param mode of transport
     * @param modesUsed array containing the user's values assigned for productivity, enjoyment, fit-
     *              for each mode
     * @param definedUserValues for productivity, enjoyment, fitness goals
     * @param legDuration
     * @return general worthwhileness score for a leg
     */

    public static double getTotalWorthFromLeg(int mode, ArrayList<ModeOfTransportUsed> modesUsed, WorthwhilenessValues definedUserValues, double legDuration) {

        WorthwhilenessValues modalityValues = getWorthwhilenessWeightForMode(mode, modesUsed);

        double top = ((modalityValues.getProductivityValue()/100) * (definedUserValues.getProductivityValue()/100))
                         + ((modalityValues.getEnjoymentValue()/100) * (definedUserValues.getEnjoymentValue()/100))
                            + ((modalityValues.getFitnessValue()/100) * (definedUserValues.getFitnessValue()/100));

        double bottom = (definedUserValues.getProductivityValue() / 100)
                + (definedUserValues.getFitnessValue() / 100)
                + (definedUserValues.getEnjoymentValue() / 100);

        return (top/bottom)*legDuration;

    }


}

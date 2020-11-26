package inesc_id.pt.motivandroid.managers;

import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;

import inesc_id.pt.motivandroid.data.statsFromServer.GlobalStatsServerResponse;
import inesc_id.pt.motivandroid.data.statsFromServer.StatsFromTimeIntervalStruct;
import inesc_id.pt.motivandroid.data.statsFromServer.StatsFromTimeIntervalStructForUser;
import inesc_id.pt.motivandroid.home.fragments.DashboardFragment;
import inesc_id.pt.motivandroid.motviAPIClient.MotivAPIClientManager;

/**
 *
 * UserStatsManager
 *
 *   Singleton class to manage user and community stats. Tries to ensure that the stats that the user
 *   sees are fresh (less than 6 hours)
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
public class UserStatsManager {
    private static UserStatsManager instance;

    public long getDownloadTimestamp() {
        return downloadTimestamp;
    }

    long downloadTimestamp = -1;
    GlobalStatsServerResponse stats;

    public synchronized static UserStatsManager getInstance(){

            if (instance == null) {
                instance = new UserStatsManager();
            }

        return instance;
    }

    public synchronized void setStats(GlobalStatsServerResponse globalStatsServerResponse, long downloadTimestamp) {

        synchronized (statsLock) {

            this.stats = globalStatsServerResponse;
            this.downloadTimestamp = downloadTimestamp;

        }
    }

    final Object statsLock = new Object();

    /**
     * check if the version of the statistics locally is fresh, and if not try to redownload them
     *
     * @param context
     */
    public synchronized void checkIfStatsAreFreshAndRetrieve(Context context){

        if (DateTime.now().minusHours(6).getMillis() > getDownloadTimestamp()){
            Log.e("UserStatsManger", "Stats are NOT fresh. Trying to retrieve stats from server");
            MotivAPIClientManager.getInstance(context).getStatsFromServerAndSet();
            return;
        }
        Log.e("UserStatsManger", "Stats are fresh. Doing nothing!");

    }


    public StatsFromTimeIntervalStruct getCityStats(int timeIntervalKey){

        synchronized (statsLock) {


            if (stats == null) return null;

            try {
                switch (timeIntervalKey) {

                    case DashboardFragment.keys.day1:
                        return stats.getCity().getDay1();

                    case DashboardFragment.keys.day3:
                        return stats.getCity().getDay3();

                    case DashboardFragment.keys.day7:
                        return stats.getCity().getDay7();

                    case DashboardFragment.keys.day30:
                        return stats.getCity().getDay30();

                    case DashboardFragment.keys.day365:
                        return stats.getCity().getDay365();

                    case DashboardFragment.keys.ever:
                        return stats.getCity().getEver();

                }

            } catch (Exception e) {

                Log.e("UserStatsManager", "Exception getting stats for country");
                return null;

            }

        return null;
        }
    }

    public StatsFromTimeIntervalStruct getCountryStats(int timeIntervalKey){

        synchronized (statsLock) {


            if (stats == null) return null;

            try {

                switch (timeIntervalKey) {

                    case DashboardFragment.keys.day1:
                        return stats.getCountry().getDay1();

                    case DashboardFragment.keys.day3:
                        return stats.getCountry().getDay3();

                    case DashboardFragment.keys.day7:
                        return stats.getCountry().getDay7();

                    case DashboardFragment.keys.day30:
                        return stats.getCountry().getDay30();

                    case DashboardFragment.keys.day365:
                        return stats.getCountry().getDay365();

                    case DashboardFragment.keys.ever:
                        return stats.getCountry().getEver();

                }

            } catch (Exception e) {

                Log.e("UserStatsManager", "Exception getting stats for country");
                return null;

            }

            return null;
        }
    }

    public StatsFromTimeIntervalStruct getCampaignStats(int timeIntervalKey, String campaignID) {

        synchronized (statsLock) {

            try {

                switch (timeIntervalKey) {

                    case DashboardFragment.keys.day1:
                        return stats.getCampaigns().get(campaignID).getDay1();

                    case DashboardFragment.keys.day3:
                        return stats.getCampaigns().get(campaignID).getDay3();

                    case DashboardFragment.keys.day7:
                        return stats.getCampaigns().get(campaignID).getDay7();

                    case DashboardFragment.keys.day30:
                        return stats.getCampaigns().get(campaignID).getDay30();

                    case DashboardFragment.keys.day365:
                        return stats.getCampaigns().get(campaignID).getDay365();

                    case DashboardFragment.keys.ever:
                        return stats.getCampaigns().get(campaignID).getEver();

                }

            } catch (Exception e) {

                Log.e("UserStatsManager", "Exception getting stats for campaign " + campaignID);
                return null;

            }

            return null;

        }
    }

    public StatsFromTimeIntervalStructForUser getUserStats(int timeIntervalKey){

        synchronized (statsLock) {

            if (stats == null) return null;

            try {

                switch (timeIntervalKey) {

                    case DashboardFragment.keys.day1:
                        return stats.getUser().getDay1();

                    case DashboardFragment.keys.day3:
                        return stats.getUser().getDay3();

                    case DashboardFragment.keys.day7:
                        return stats.getUser().getDay7();

                    case DashboardFragment.keys.day30:
                        return stats.getUser().getDay30();

                    case DashboardFragment.keys.day365:
                        return stats.getUser().getDay365();

                    case DashboardFragment.keys.ever:
                        return stats.getUser().getEver();

                }

            } catch (Exception e) {

                Log.e("UserStatsManager", "Exception getting stats for user");
                return null;

            }

            return null;
        }
    }

}

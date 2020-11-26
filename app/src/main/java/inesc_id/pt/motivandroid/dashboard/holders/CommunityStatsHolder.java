package inesc_id.pt.motivandroid.dashboard.holders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;

/**
 * CommunityStatsHolder
 *
 *  Holder for each mode of transport user and community stats
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

public class CommunityStatsHolder implements Serializable{

    private int percentageUser;
    private int percentageCommunity;

    private double valueUser;
    private double valueCommunity;

    private int mode;

    public CommunityStatsHolder(int percentageUser, int percentageCommunity, double valueUser, double valueCommunity, int mode) {
        this.percentageUser = percentageUser;
        this.percentageCommunity = percentageCommunity;
        this.valueUser = valueUser;
        this.valueCommunity = valueCommunity;
        this.mode = mode;
    }

    public int getPercentageUser() {
        return percentageUser;
    }

    public void setPercentageUser(int percentageUser) {
        this.percentageUser = percentageUser;
    }

    public int getPercentageCommunity() {
        return percentageCommunity;
    }

    public void setPercentageCommunity(int percentageCommunity) {
        this.percentageCommunity = percentageCommunity;
    }

    public double getValueUser() {
        return valueUser;
    }

    public void setValueUser(int valueUser) {
        this.valueUser = valueUser;
    }

    public double getValueCommunity() {
        return valueCommunity;
    }

    public void setValueCommunity(int valueCommunity) {
        this.valueCommunity = valueCommunity;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public static boolean isModeOnArray(int mode, ArrayList<CommunityStatsHolder> communityStatsHolders){

        boolean result = false;

        for(CommunityStatsHolder communityStatsHolder : communityStatsHolders){

            if (communityStatsHolder.getMode() == mode){
                result = true;
                break;
            }

        }

        return result;
    }

    public static void removeInvalidModesAndStats(ArrayList<CommunityStatsHolder> commStatsHolders){

        ListIterator<CommunityStatsHolder> iter = commStatsHolders.listIterator();

        while(iter.hasNext()){

            CommunityStatsHolder curr = iter.next();

            if(!ActivityDetected.isModeValid(curr.getMode()) || (curr.getValueCommunity() == 0.0 && curr.getPercentageUser() == 0)){
                iter.remove();
            }
        }

    }
}

package inesc_id.pt.motivandroid.dashboard.holders;

import com.firebase.ui.auth.data.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;

/**
 * UserStatsHolder
 *
 *  Holder for each mode of transport user stats
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
public class UserStatsHolder implements Serializable{

    private double param1;
    private int param2;
    int mode;

    public UserStatsHolder(double param1, int param2, int mode) {
        this.param1 = param1;
        this.param2 = param2;
        this.mode = mode;
    }

    public double getParam1() {
        return param1;
    }

    public void setParam1(long param1) {
        this.param1 = param1;
    }

    public void setParam1(int param1) {
        this.param1 = param1;
    }

    public int getParam2() {
        return param2;
    }

    public void setParam2(int param2) {
        this.param2 = param2;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public static UserStatsHolder getStatsForMode(ArrayList<UserStatsHolder> userStatsHolders, int mode){

        UserStatsHolder result = null;

        for(UserStatsHolder holder : userStatsHolders){

            if (holder.getMode() == mode){

                result = holder;
                break;

            }

        }

        return result;

    }

    public static void removeInvalidModesAndStats(ArrayList<UserStatsHolder> userStatsHolders){

        ListIterator<UserStatsHolder> iter = userStatsHolders.listIterator();

        while(iter.hasNext()){

            UserStatsHolder curr = iter.next();

            if(!ActivityDetected.isModeValid(curr.getMode()) || (curr.getParam1() == 0.0 && curr.getParam2() == 0)){
                iter.remove();
            }
        }

    }

}

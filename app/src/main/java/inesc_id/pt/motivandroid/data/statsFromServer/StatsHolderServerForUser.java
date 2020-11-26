package inesc_id.pt.motivandroid.data.statsFromServer;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 *
 * StatsHolderServer
 *
 * Holds user stats for multiple time intervals
 *
 *  day1: Stats for last day
 *  day3: Stats for last three days
 *  day7: Stats for last seven days
 *  day30: Stats for last thirty days
 *  day365: Stats for last year
 *  era: all-time stats
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
public class StatsHolderServerForUser implements Serializable{

    public StatsHolderServerForUser() {
    }

    @Expose
    StatsFromTimeIntervalStructForUser day1;

    @Expose
    StatsFromTimeIntervalStructForUser day3;

    @Expose
    StatsFromTimeIntervalStructForUser day7;

    @Expose
    StatsFromTimeIntervalStructForUser day30;

    @Expose
    StatsFromTimeIntervalStructForUser day365;

    @Expose
    StatsFromTimeIntervalStructForUser ever;

    public StatsFromTimeIntervalStructForUser getDay1() {
        return day1;
    }

    public void setDay1(StatsFromTimeIntervalStructForUser day1) {
        this.day1 = day1;
    }

    public StatsFromTimeIntervalStructForUser getDay3() {
        return day3;
    }

    public void setDay3(StatsFromTimeIntervalStructForUser day3) {
        this.day3 = day3;
    }

    public StatsFromTimeIntervalStructForUser getDay7() {
        return day7;
    }

    public void setDay7(StatsFromTimeIntervalStructForUser day7) {
        this.day7 = day7;
    }

    public StatsFromTimeIntervalStructForUser getDay30() {
        return day30;
    }

    public void setDay30(StatsFromTimeIntervalStructForUser day30) {
        this.day30 = day30;
    }

    public StatsFromTimeIntervalStructForUser getDay365() {
        return day365;
    }

    public void setDay365(StatsFromTimeIntervalStructForUser day365) {
        this.day365 = day365;
    }

    public StatsFromTimeIntervalStructForUser getEver() {
        return ever;
    }

    public void setEver(StatsFromTimeIntervalStructForUser ever) {
        this.ever = ever;
    }
}

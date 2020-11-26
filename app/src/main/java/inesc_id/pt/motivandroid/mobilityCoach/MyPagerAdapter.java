package inesc_id.pt.motivandroid.mobilityCoach;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import inesc_id.pt.motivandroid.mobilityCoach.fragments.SetMobilityGoalFragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding1Fragment;

/**
 * MyPagerAdapter
 *
 *   Adapter to draw the "carousel" of fragments (scroll horizontally between the three mobility goals
 *   fragments)
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

public class MyPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 3;

    public MyPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);

    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }


    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show Productivity
                return SetMobilityGoalFragment.newInstance(SetMobilityGoalFragment.keys.PRODUCTIVITY_MODE);
            case 1: // Fragment # 1 - This will show Activity
                return SetMobilityGoalFragment.newInstance(SetMobilityGoalFragment.keys.ACTIVITY_MODE);
            case 2: // Fragment # 2 - This will show Relaxing
                return SetMobilityGoalFragment.newInstance(SetMobilityGoalFragment.keys.RELAXING_MODE);
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }




}





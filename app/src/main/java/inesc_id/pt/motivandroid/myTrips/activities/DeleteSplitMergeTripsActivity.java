package inesc_id.pt.motivandroid.myTrips.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments.MergeTripPartsFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments.SplitLegsListFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.tripEdit.splitMergeDeleteFragments.DeleteTripsFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.tripEdit.splitMergeDeleteFragments.MergeTripFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.tripEdit.splitMergeDeleteFragments.MergeTripsConfirmOnMapFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.tripEdit.splitMergeDeleteFragments.SplitTripFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.tripEdit.splitMergeDeleteFragments.SplitTripOnMapFragment;

/**
 * DeleteSplitMergeTripsActivity
 *
 * According to the mode passed in the parameter(keys.mode), this activity instantiates the correct
 * fragment to allow the user to split, delete and merge a TRIP
 *
 * Trip id is passed through MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED parameter
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

public class DeleteSplitMergeTripsActivity extends AppCompatActivity implements View.OnClickListener,
        MergeTripFragment.OnFragmentInteractionListener, MergeTripsConfirmOnMapFragment.OnFragmentInteractionListener,
        SplitTripFragment.OnFragmentInteractionListener, SplitTripOnMapFragment.OnFragmentInteractionListener,
        DeleteTripsFragment.OnFragmentInteractionListener{

    int mode;
    TextView titleTextView;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        mode = getIntent().getIntExtra(DeleteSplitMergeLegsActivity.keys.mode, 0);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = MergeTripFragment.newInstance("","");

        switch (mode){
            case keys.MERGE_MODE:
                fragment = MergeTripFragment.newInstance("","");
                break;
            case keys.SPLIT_MODE:
                fragment = SplitTripFragment.newInstance("","");
                break;
            case keys.DELETE_MODE:
                fragment = DeleteTripsFragment.newInstance("","");
                break;
            default:
        }

        titleTextView = findViewById(R.id.titleTextView);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(this);

        ft.replace(R.id.mergeSplitDeleteMainFragment, fragment);
        ft.commit();

    }

    public void setTitleText(String title){
        titleTextView.setText(title);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.backButton:
                onBackPressed();
                break;

        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public interface keys{
        int MERGE_MODE = 0;
        int SPLIT_MODE = 1;
        int DELETE_MODE = 2;

        String mode = "mode";
    }

}

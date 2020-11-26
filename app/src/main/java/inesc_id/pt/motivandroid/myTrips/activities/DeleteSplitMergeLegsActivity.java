package inesc_id.pt.motivandroid.myTrips.activities;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.myTrips.adapters.splitMergeDelete.DeleteTripPartsAdapter;
import inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments.DeleteTripPartFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments.DeleteTripPartsConfirmOnMapFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments.MergeTripPartsConfirmOnMapFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments.MergeTripPartsFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments.SplitLegsListFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments.SplitLegsOnMapFragment;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;

/**
 * DeleteSplitMergeLegsActivity
 *
 * According to the mode passed in the parameter(keys.mode), this activity instantiates the correct
 * fragment to allow the user to split, delete and merge LEGS
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

public class DeleteSplitMergeLegsActivity extends AppCompatActivity implements DeleteTripPartFragment.OnFragmentInteractionListener,
                                                                                MergeTripPartsFragment.OnFragmentInteractionListener,
                                                                                    SplitLegsListFragment.OnFragmentInteractionListener,
                                                                                        SplitLegsOnMapFragment.OnFragmentInteractionListener,
                                                                                            MergeTripPartsConfirmOnMapFragment.OnFragmentInteractionListener, View.OnClickListener,
                                                                                                DeleteTripPartsConfirmOnMapFragment.OnFragmentInteractionListener{

    String fullTripID;
    int mode;

    TextView titleTextView;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_split_merge2);

        mode = getIntent().getIntExtra(keys.mode, 0);

        fullTripID = getIntent().getStringExtra(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED);

        Log.d("DeleteSplitMergeLegsA", fullTripID);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = SplitLegsListFragment.newInstance(fullTripID);



        switch (mode){
            case keys.MERGE_MODE:
                fragment = MergeTripPartsFragment.newInstance(fullTripID);
                break;
            case keys.SPLIT_MODE:
                fragment = SplitLegsListFragment.newInstance(fullTripID);
                break;
            case keys.DELETE_MODE:
                fragment = DeleteTripPartFragment.newInstance(fullTripID);
                break;
            default:
        }

        titleTextView = findViewById(R.id.titleTextView);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(this);

        ft.replace(R.id.mergeSplitDeleteMainFragment, fragment);
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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


    public interface keys{
        int MERGE_MODE = 0;
        int SPLIT_MODE = 1;
        int DELETE_MODE = 2;

        String mode = "mode";
    }

}

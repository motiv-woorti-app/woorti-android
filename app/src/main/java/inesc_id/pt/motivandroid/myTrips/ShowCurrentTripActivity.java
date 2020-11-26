package inesc_id.pt.motivandroid.myTrips;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.myTrips.activities.DeleteSplitMergeLegsActivity;
import inesc_id.pt.motivandroid.myTrips.activities.MyTripsActivity;
import inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments.DeleteTripPartFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments.MergeTripPartsFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments.SplitLegsListFragment;
import inesc_id.pt.motivandroid.persistence.TripKeeper;
import inesc_id.pt.motivandroid.utils.MotivMapUtils;

/**
 * ShowCurrentTripActivity
 *
 * Shows the route of the trip currently being validated. Also offers the option to edit the trip(
 * merge, delete, split)
 *
 * Trip id is passed through MyTripsActivity.keys.CURRENT_TRIP parameter
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
public class ShowCurrentTripActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,
        SplitLegsListFragment.OnFragmentInteractionListener,
        MergeTripPartsFragment.OnFragmentInteractionListener,
        DeleteTripPartFragment.OnFragmentInteractionListener {

    FullTrip fullTripBeingShown;

    GoogleMap mMap;

    Button backButton;

    ImageView splitButton;
    ImageView mergeButton;
    ImageView deleteButton;

    private String fullTripId;

    TripKeeper tripKeeper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_current_trip);

        tripKeeper = TripKeeper.getInstance(getApplicationContext());

        Intent intent = getIntent();

        fullTripId = intent.getStringExtra(MyTripsActivity.keys.CURRENT_TRIP);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        splitButton = findViewById(R.id.splitLegsImageViewButton);
        mergeButton = findViewById(R.id.mergeLegsImageViewButton);
        deleteButton = findViewById(R.id.deleteLegsImageViewButton);

        splitButton.setOnClickListener(this);
        mergeButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        fullTripBeingShown = tripKeeper.getCurrentFullTrip(fullTripId);

        if(mMap != null){ //prevent crashing if the map doesn't exist yet (eg. on starting activity)
            mMap.clear();

            MotivMapUtils.drawRouteOnMap(fullTripBeingShown, mMap, getApplicationContext(), -1, -1);
            // add markers from database to the map
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        MotivMapUtils.drawRouteOnMap(fullTripBeingShown, mMap, getApplicationContext(), -1, -1);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.backButton:
                onBackPressed();
            break;
            case R.id.splitLegsImageViewButton:
                startActivity(createSplitsLegsIntent());
                break;
            case R.id.mergeLegsImageViewButton:
                startActivity(createMergeLegsIntent());
                break;
            case R.id.deleteLegsImageViewButton:
                startActivity(createDeleteLegsIntent());
                break;
        }

    }

    public Intent createDeleteLegsIntent(){
        Intent deleteIntent = new Intent(getApplicationContext(), DeleteSplitMergeLegsActivity.class);
        deleteIntent.putExtra(DeleteSplitMergeLegsActivity.keys.mode, DeleteSplitMergeLegsActivity.keys.DELETE_MODE);
        deleteIntent.putExtra(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTripId);

        return deleteIntent;
    }

    public Intent createMergeLegsIntent(){
        Intent mergeIntent = new Intent(getApplicationContext(), DeleteSplitMergeLegsActivity.class);
        mergeIntent.putExtra(DeleteSplitMergeLegsActivity.keys.mode, DeleteSplitMergeLegsActivity.keys.MERGE_MODE);
        mergeIntent.putExtra(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTripId);

        return mergeIntent;
    }

    public Intent createSplitsLegsIntent(){
        Intent splitIntent = new Intent(getApplicationContext(), DeleteSplitMergeLegsActivity.class);
        splitIntent.putExtra(DeleteSplitMergeLegsActivity.keys.mode, DeleteSplitMergeLegsActivity.keys.SPLIT_MODE);
        splitIntent.putExtra(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTripId);

        return splitIntent;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

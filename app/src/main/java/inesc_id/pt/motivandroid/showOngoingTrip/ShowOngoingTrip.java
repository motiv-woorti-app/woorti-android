package inesc_id.pt.motivandroid.showOngoingTrip;

import android.support.v7.app.AppCompatActivity;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.tripStateMachine.TripStateMachine;
import inesc_id.pt.motivandroid.utils.MotivMapUtils;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
/**
 * ShowOngoingTrip
 *
 *  This activity shows the currently ongoing trip on a map, if there is one.
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

public class ShowOngoingTrip extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    OngoingTripWrapper ongoingTripWrapper;

    GoogleMap mMap;

    TextView description;
    TextView stateTextView;

    Button refreshCurrentTripButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ongoing_trip);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        stateTextView = findViewById(R.id.stateTextView);


        description = findViewById(R.id.legsBeingValidatedListView);

        refreshCurrentTripButton = findViewById(R.id.refreshButton);

        refreshCurrentTripButton.setClickable(false);
        refreshCurrentTripButton.setVisibility(View.INVISIBLE);

        refreshCurrentTripButton.setOnClickListener(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        refreshOngoingTripUI();
        refreshCurrentTripButton.setClickable(true);
        refreshCurrentTripButton.setVisibility(View.VISIBLE);

    }

    private void refreshOngoingTripUI() {

        ongoingTripWrapper = TripStateMachine.getInstance(getApplicationContext(), false, true).getCurrentOngoingTrip();

        description.setText("");

        if(ongoingTripWrapper == null){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.No_Ongoing_Trip_To_Show), Toast.LENGTH_SHORT).show();
            stateTextView.setText("Still");
        }else{

            if(ongoingTripWrapper.currentState == 1){
                stateTextView.setText("Trip");
            }else if(ongoingTripWrapper.currentState == 2){
                stateTextView.setText("Waiting event");
            }

            MotivMapUtils.drawRouteOnMapOngoing(ongoingTripWrapper.getIdentifiedLegs(), ongoingTripWrapper.getCurrentLocations(), mMap, getApplicationContext());


            for (FullTripPart fullTripPart : ongoingTripWrapper.getIdentifiedLegs()){

                description.append(fullTripPart.getDescription());

            }

        }




    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.refreshButton:

                if (mMap != null) {
                    refreshOngoingTripUI();
                }

                break;
        }

    }
}

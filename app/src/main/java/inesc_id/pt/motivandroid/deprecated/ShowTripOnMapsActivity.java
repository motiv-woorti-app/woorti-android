package inesc_id.pt.motivandroid.deprecated;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.AccelerationData;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.motviAPIClient.MotivAPIClientManager;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.tripStateMachine.TripAnalysis;

@Deprecated
public class ShowTripOnMapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;

    private FullTrip fullTripToBeShown;
    private String fullTripToBeShownDate;

    TextView description;

    Button validateModalities;
    Button confirmTripButton;

    PersistentTripStorage persistentTripStorage;

    MotivAPIClientManager motivAPIClientManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_trip_on_maps);

        persistentTripStorage = new PersistentTripStorage(getApplicationContext());

        fullTripToBeShownDate = (String) getIntent().getSerializableExtra("FullTripToBeShown");

        fullTripToBeShown = persistentTripStorage.getFullTripByDate(fullTripToBeShownDate);

        //fullTripToBeShown = (FullTrip) getIntent().getSerializableExtra("FullTripToBeShown");

        description = (TextView) findViewById(R.id.fullTripDescription);
        description.setMovementMethod(new ScrollingMovementMethod());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        validateModalities = findViewById(R.id.validateModalitiesButton);
        validateModalities.setOnClickListener(this);

        confirmTripButton = findViewById(R.id.confirmTripButton);
        confirmTripButton.setOnClickListener(this);

        motivAPIClientManager = MotivAPIClientManager.getInstance(getApplicationContext());
        Log.e("showMaps", "oncreate");


    }

    @Override
    protected void onResume() {
        super.onResume();
        fullTripToBeShown = persistentTripStorage.getFullTripByDate(fullTripToBeShownDate);

        if(fullTripToBeShown.isSentToServer()){
            validateModalities.setVisibility(View.INVISIBLE);
            confirmTripButton.setVisibility(View.INVISIBLE);
        }

        if(!fullTripToBeShown.isValidated()){
            confirmTripButton.setVisibility(View.INVISIBLE);

        }
        Log.e("showMaps", "onresume");

        //if map has already been initialized...this is case where the user goes to
        // the validate screen, presses back and the ui must be updated accordingly
        if(mMap!=null){
            updateMapAndUi();
        }

    }

    /*private static class AtomicIntegerTypeAdapter
            implements JsonSerializer<AtomicInteger>, JsonDeserializer<AtomicInteger> {
        @Override public JsonElement serialize(AtomicInteger src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.incrementAndGet());
        }


        @Override public AtomicInteger deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            int intValue = json.getAsInt();
            return new AtomicInteger(--intValue);
        }
    }*/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.e("showMaps", "onmapready");
        updateMapAndUi();

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void updateMapAndUi(){

        mMap.clear();

        description.setText(fullTripToBeShown.getDescription());

        for(FullTripPart ftp : fullTripToBeShown.getTripList()){


            Log.d("Locations", "loc size" + ftp.getLocationDataContainers().size());

            if(ftp.getLocationDataContainers().size() == 0){
                Log.d("Locations", "ldc size = 0");

                if(ftp.isTrip()){

                    Log.d("Locations", "suggested" + ((Trip) ftp).getSugestedModeOfTransport());
                    Log.d("Locations", "accels: " + ((Trip) ftp).getAccelerationData().size());

                }

            }else {


                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.width(10);

                if (ftp.isTrip()) {
                    lineOptions.color(Color.BLUE);
                } else {
                    lineOptions.color(Color.RED);
                }
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
                String initDateFtp = sdf.format(ftp.getInitTimestamp());

                Log.e("showMaps", "trip part starting at" + initDateFtp + "");

                Polyline currentPolyline = mMap.addPolyline(lineOptions);

                LocationDataContainer init = ftp.getLocationDataContainers().get(0);
                LocationDataContainer end = ftp.getLocationDataContainers().get(ftp.getLocationDataContainers().size() - 1);

                LatLng markerInit = new LatLng(init.getLatitude(), init.getLongitude());
                LatLng markerEnd = new LatLng(end.getLatitude(), end.getLongitude());

                if (ftp.isTrip()) {
                    //mMap.addMarker(new MarkerOptions().position(markerInit).title("Init Trip").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(markerEnd).title("End Leg").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    //marker.showInfoWindow();
                    Log.e("showMaps", "modality" + ((Trip) ftp).getModality());
                    Log.e("showMaps", "suggested" + ((Trip) ftp).getSugestedModeOfTransport());
                    Log.e("showMaps", "corrected" + ((Trip) ftp).getCorrectedModeOfTransport());

                } else {
                    //mMap.addMarker(new MarkerOptions().position(markerInit).title("Init Waiting Event").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(markerEnd).title("End Waiting Event").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    //marker.showInfoWindow();
                }

                List<LatLng> latLngList = new ArrayList<>();

                for (LocationDataContainer ldc : ftp.getLocationDataContainers()) {

                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
                    String initSysDate = sdf2.format(ldc.getSysTimestamp());

                    String initLocDate = sdf2.format(ldc.getLocTimestamp());

                    latLngList.add(new LatLng(ldc.getLatitude(), ldc.getLongitude()));
                    Log.d("showMaps", ldc.getLatitude() + " " + ldc.getLongitude() + " acc:" + ldc.getAccuracy() + " speed:" + ldc.getSpeed() + " " + initSysDate + " " + initLocDate);

                }


                if (ftp.isTrip()) {

                    Trip current = (Trip) ftp;

                    Log.e("acc", "accelerometer points trip" + current.getAccelerationData().size() + "");

                    for (AccelerationData ad : ((Trip) ftp).getAccelerationData()) {

                        SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
                        String initDate = sdf2.format(ad.getTimestamp());

                        Log.d("acc", ad.getxValue() + " " + ad.getyValue() + " " + ad.getzValue() + " " + initDate);

                    }

//                Log.d("acc",current.getAccelerationAverage()+"");
//
//                for (ActivityDataContainer adc : ((Trip) ftp).getActivityDataContainers()){
//
//                    List<ActivityDetected> la = adc.getListOfDetectedActivities();
//
//                    StringBuilder sb = new StringBuilder();
//
//                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
//                    String initDate = sdf2.format(adc.getTimestamp());
//
//                    for(ActivityDetected da : la){
//
//                        if(da.getType()>8){
//                            sb.append("mod " + da.getType() + " at "+ da.getConfidenceLevel() +"%");
//                        }else{
//                            sb.append("mod " + ActivityDetected.keys.modalities[da.getType()] + " at "+ da.getConfidenceLevel() +"%");
//                        }
//                    }
//
//
//
//                    sb.append(" at" + initDate);
//                    Log.d("showMaps", sb.toString());
//                }

                    TripAnalysis ta = new TripAnalysis(false);
                /*int[] vec = ta.classifyBatchByMod(((Trip) ftp).getActivityDataContainers(), ftp.getInitTimestamp(), ftp.getEndTimestamp());

                Log.e("showMaps", "scores " + Arrays.toString(vec));

                try {

                    double distanceKM = current.getDistanceTraveled();
                    double timeHour = (current.getEndTimestamp() - current.getInitTimestamp())/3600000.0;

                    Log.e("showMaps", "distanceTraveled " +  distanceKM);
                    Log.e("showMaps", "elapsedTime "+ timeHour);

                    Log.e("showMaps", "avg speed " +  distanceKM/timeHour);
                }catch(Exception e){
                    Log.e("showMaps", "one location, divide by 0");
                }*/

//                    ta.analyseTripPart(ftp.getLocationDataContainers(), ((Trip) ftp).getActivityDataContainers(), true, 0, ((Trip) ftp).getAccelerationData());

                }


                currentPolyline.setPoints(latLngList);
            }//todo remove
            //mMap.moveCamera(CameraUpdateFactory.newLatLng());
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(fullTripToBeShown.getTripList().get(0).getLocationDataContainers().get(0).getLatitude(),fullTripToBeShown.getTripList().get(0).getLocationDataContainers().get(0).getLongitude()), 17));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.validateModalitiesButton:
//                Intent intent = new Intent(getApplicationContext(),ValidateModalitiesActivity.class);
                //intent.putExtra("FullTripToBeShown", fullTripToBeShown);
//                intent.putExtra("FullTripToBeValidated", fullTripToBeShownDate);

//                startActivity(intent);
                break;
            case R.id.confirmTripButton:

//                motivAPIClientManager.sendTripToServer(fullTripToBeShown);
                finish();
// )){
//                    Toast.makeText(getApplicationContext(), "Trip sent to the server successfully", Toast.LENGTH_LONG).show();
//                    finish();
//                }else{
//                    Toast.makeText(getApplicationContext(),"Trip update to server failed",Toast.LENGTH_LONG).show();
//                }

                break;

        }
    }
}

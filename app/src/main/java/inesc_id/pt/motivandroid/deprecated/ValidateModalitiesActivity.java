package inesc_id.pt.motivandroid.deprecated;

import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
import com.google.maps.android.PolyUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.AccelerationData;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.deprecated.modalityValidation.LegMarkerPolylineHolder;
import inesc_id.pt.motivandroid.deprecated.modalityValidation.LegModel;
import inesc_id.pt.motivandroid.deprecated.modalityValidation.PackageRecyclerViewAdapter;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.tripStateMachine.TripAnalysis;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.LocationUtils;

@Deprecated
public class ValidateModalitiesActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, AdapterView.OnItemClickListener {

    FullTrip fullTripToBeValidated;
    String fullTripToBeValidatedDate;

    private RecyclerView packageRecyclerView;

    Button validateTripButton;

    PackageRecyclerViewAdapter recyclerViewAdapter;

    PersistentTripStorage persistentTripStorage;

    GoogleMap mMap;

    ArrayList<LegMarkerPolylineHolder> legMarkerPolylineHolderArrayList;

    ListView legListView;

    int selectedLeg = -1;

    TripAnalysis tripAnalysis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_modalities);

        //fullTripToBeValidated = (FullTrip) getIntent().getSerializableExtra("FullTripToBeShown");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        legMarkerPolylineHolderArrayList = new ArrayList<>();

        persistentTripStorage = new PersistentTripStorage(getApplicationContext());

        fullTripToBeValidatedDate = (String) getIntent().getSerializableExtra("FullTripToBeValidated");

        fullTripToBeValidated = persistentTripStorage.getFullTripByDate(fullTripToBeValidatedDate);

        validateTripButton = findViewById(R.id.validateTripButton);
        validateTripButton.setOnClickListener(this);

        packageRecyclerView = (RecyclerView) findViewById(R.id.legList);

        GridLayoutManager recyclerLayoutManager = new GridLayoutManager(this,2);
        packageRecyclerView.setLayoutManager(recyclerLayoutManager);

        Log.e("viewcount", packageRecyclerView.getChildCount()+"");

        legListView = findViewById(R.id.legListView);
        legListView.setOnItemClickListener(this);

        tripAnalysis = new TripAnalysis(false);

    }

    private List<LegModel> getLegModels(FullTrip fullTripToBeValidated){
        List<LegModel> modelList = new ArrayList<>();

        int index = 0;
        for(FullTripPart fullTripPart : fullTripToBeValidated.getTripList()){


            if(fullTripPart.isTrip()){
                modelList.add(new LegModel((Trip)fullTripPart,index));
            }
            index++;
        }

        return modelList;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.validateTripButton:

                for(LegModel legModel : recyclerViewAdapter.getLegList()) {
                    /*Log.e("validation", "Trip part" + legModel.getTripPartIndex());
                    Log.e("validation", "Old modality " + legModel.getLeg().getModality());
                    Log.e("validation", "New modality " + legModel.getSelectedIndex());

                    Log.e("validation", "Old isWrong"+ legModel.getLeg().isWrongLeg());
                    Log.e("validation", "New isWrong"+ legModel.isWrongLeg())*/

                    //set validated mode of transport
                    ((Trip) fullTripToBeValidated.getTripList().get(legModel.getTripPartIndex())).setCorrectedModeOfTransport(legModel.getSelectedIndex());

                    //set wrong leg
                    ((Trip) fullTripToBeValidated.getTripList().get(legModel.getTripPartIndex())).setWrongLeg(legModel.isWrongLeg());

                    fullTripToBeValidated.setValidated(true);

                }


                try {
                    Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(fullTripToBeValidated.getTripList().get(0).getLocationDataContainers().get(0).getLatitude(),
                            fullTripToBeValidated.getTripList().get(0).getLocationDataContainers().get(0).getLongitude(), 1);
                    if (addresses.isEmpty()) {
                        Log.e("geocoder","waiting for");
                    } else {
                        if (addresses.size() > 0) {
                            Log.e("validate", addresses.get(0).getCountryName());
                            Log.e("validate", addresses.get(0).getAdminArea());

                            fullTripToBeValidated.setCountry(addresses.get(0).getCountryName());
                            fullTripToBeValidated.setCityInfo(addresses.get(0).getAdminArea());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // getFromLocation() may sometimes fail
                }

                persistentTripStorage = new PersistentTripStorage(getApplicationContext());
                persistentTripStorage.updateFullTripDataObject(fullTripToBeValidated,fullTripToBeValidatedDate);
                finish();

                break;
        }

    }

    public void updateMapAndLegListView(){

        Log.d("validateMap", "onMapReady");

        int legNumber = 0;
        int fullTripPartIndex = 0;

        mMap.clear();
        legMarkerPolylineHolderArrayList = new ArrayList<>();

        recyclerViewAdapter = new
                PackageRecyclerViewAdapter(getLegModels(fullTripToBeValidated),this);
        packageRecyclerView.setAdapter(recyclerViewAdapter);

        int ftpIndex = 0;

        for(FullTripPart ftp : fullTripToBeValidated.getTripList()) {


            Log.e("triplist", "index" + ftpIndex);
            Log.e("triplist", "numLocs" + ftp.getLocationDataContainers().size());
            Log.e("triplist", "isTrip" + ftp.isTrip());

            for (LocationDataContainer ldc : ftp.getLocationDataContainers()){
                Log.e("locationdatacontainer", DateHelper.getDateFromTSString(ldc.getSysTimestamp()));
            }

            if(ftp.isTrip()) {
                Log.e("triplist", "numsAccels " + ((Trip) ftp).getAccelerationData().size());

                for(AccelerationData accelerationData : ((Trip) ftp).getAccelerationData()){
                    Log.e("accel", DateHelper.getDateFromTSString(accelerationData.getTimestamp()));


                }
            }

            ftpIndex++;
        }


        for(FullTripPart ftp : fullTripToBeValidated.getTripList()){

            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.width(10);

            if(ftp.isTrip()) {
                lineOptions.color(Color.BLUE);
            }else{
                lineOptions.color(Color.GREEN);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
            String initDateFtp = sdf.format(ftp.getInitTimestamp());

            Log.e("showMaps", "trip part starting at" + initDateFtp+ "");

            Polyline currentPolyline = mMap.addPolyline(lineOptions);

            LocationDataContainer init = ftp.getLocationDataContainers().get(0);
            LocationDataContainer end = ftp.getLocationDataContainers().get(ftp.getLocationDataContainers().size()-1);

            LatLng markerInit = new LatLng(init.getLatitude(),init.getLongitude());
            LatLng markerEnd = new LatLng(end.getLatitude(),end.getLongitude());

            List<LatLng> latLngList = new ArrayList<>();

            for(LocationDataContainer ldc : ftp.getLocationDataContainers()){

                SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
                String initSysDate = sdf2.format(ldc.getSysTimestamp());

                String initLocDate = sdf2.format(ldc.getLocTimestamp());

                latLngList.add(new LatLng(ldc.getLatitude(),ldc.getLongitude()));
                Log.d("showMaps", ldc.getLatitude() + " " + ldc.getLongitude() + " acc:" + ldc.getAccuracy() + " speed:" + ldc.getSpeed() + " " + initSysDate + " " + initLocDate);

            }
            currentPolyline.setPoints(latLngList);


            if(ftp.isTrip()) {
                lineOptions.color(Color.BLUE);
                Marker marker = mMap.addMarker(new MarkerOptions().position(markerEnd).title("End Leg " + legNumber).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                boolean hasNextTrip = false;
                if(fullTripToBeValidated.getTripList().size() > fullTripPartIndex + 1 && fullTripToBeValidated.getTripList().get(fullTripPartIndex+1).isTrip()){
                    hasNextTrip = true;
                }

                LegMarkerPolylineHolder legMarkerPolylineHolder = new LegMarkerPolylineHolder(marker,currentPolyline,fullTripPartIndex,hasNextTrip, legNumber);
                legMarkerPolylineHolderArrayList.add(legMarkerPolylineHolder);
                legNumber++;

            }else{
                mMap.addMarker(new MarkerOptions().position(markerEnd).title("End Waiting Event").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                lineOptions.color(Color.GREEN);
            }

            fullTripPartIndex++;
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(fullTripToBeValidated.getTripList().get(0).getLocationDataContainers().get(0).getLatitude(),fullTripToBeValidated.getTripList().get(0).getLocationDataContainers().get(0).getLongitude()), 12));

        ArrayAdapter<LegMarkerPolylineHolder> itemsAdapter =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, legMarkerPolylineHolderArrayList);
        legListView.setAdapter(itemsAdapter);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                for(LegMarkerPolylineHolder m : legMarkerPolylineHolderArrayList){

                    if(m.getLegMarker().equals(marker)){

                        int fullTripPartIndex = m.getFullTripPartIndex();

                        if(m.isNextPartTrip()){

                            showConfirmJoinPopup(fullTripPartIndex);

                        }else{
                            Toast.makeText(getApplicationContext(), "Unable to join with next trip part, not a leg", Toast.LENGTH_LONG).show();
                        }

                    }

                }

                return false;
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if(selectedLeg == -1){
                    Toast.makeText( getApplicationContext(),"You must select a leg number from the list", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    if(PolyUtil.isLocationOnPath(latLng, legMarkerPolylineHolderArrayList.get(selectedLeg).getLegPolyline().getPoints(),false,25)){

                        int fullTripPartIndex = legMarkerPolylineHolderArrayList.get(selectedLeg).getFullTripPartIndex();
                        LocationDataContainer nearestPoint = LocationUtils.findNearestLDC(latLng, fullTripToBeValidated.getTripList().get(fullTripPartIndex).getLocationDataContainers());

                        if(nearestPoint!=null){

                            //is last leg
                            boolean isLastLeg = (fullTripPartIndex == legMarkerPolylineHolderArrayList.size() -1);
                            ArrayList<Trip> splittedLegs = tripAnalysis.splitLeg((Trip)fullTripToBeValidated.getTripList().get(fullTripPartIndex),nearestPoint, isLastLeg);

                            if(splittedLegs!=null){
                                fullTripToBeValidated = tripAnalysis.removeAndInsertLegsIntoTrip(fullTripToBeValidated,splittedLegs,fullTripPartIndex);
                                updateMapAndLegListView();
                            }else{
                                Toast.makeText(getApplicationContext(),"Error splitting.", Toast.LENGTH_LONG).show();
                            }

                        }else{
                            Toast.makeText(getApplicationContext(),"Error computing nearest point to the leg.", Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(getApplicationContext(),"The location you pressed is more than 25 meters away from any point on the leg. Select a location closer to the leg please.", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateMapAndLegListView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        for(LegMarkerPolylineHolder lmph : legMarkerPolylineHolderArrayList){lmph.getLegPolyline().setColor(Color.BLUE);}

        selectedLeg = position;

        legMarkerPolylineHolderArrayList.get(position).getLegPolyline().setColor(Color.RED);
        legMarkerPolylineHolderArrayList.get(position).getLegMarker().showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(legMarkerPolylineHolderArrayList.get(position).getLegPolyline().getPoints().get(0),15));
    }

    public void showConfirmJoinPopup(int index){

        final int fullTripPartIndex = index;

        AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
        builder1.setMessage("Want to join with the next leg?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

//                        Trip tripJoined = tripAnalysis.joinLegs(
//                                (Trip)fullTripToBeValidated.getTripList().get(fullTripPartIndex),
//                                (Trip)fullTripToBeValidated.getTripList().get(fullTripPartIndex+1)
//                        );

//                        fullTripToBeValidated = tripAnalysis.removeAndInsertLegsIntoTrip(fullTripToBeValidated, tripJoined, fullTripPartIndex);

                        updateMapAndLegListView();

                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}

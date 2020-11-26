package inesc_id.pt.motivandroid.deprecated;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.deprecated.listAdapters.FullTripListAdapter;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;

@Deprecated
public class ViewTripsActivity extends AppCompatActivity {

    ListView tripListView;

    public PersistentTripStorage persistentTripStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trips);

        tripListView = findViewById(R.id.tripListView);

        persistentTripStorage = new PersistentTripStorage(this);
        //getFullTripsFromStorageAndList();

        tripListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                // Start your Activity according to the item just clicked.

                FullTrip item = (FullTrip) parent.getItemAtPosition(position);

                Log.e("onitem",item.toString());

                Intent intentManageRoute = new Intent(getApplicationContext(), ShowTripOnMapsActivity.class);

                intentManageRoute.putExtra("FullTripToBeShown", item.toString());

                //intentManageRoute.putExtra("key",key);
                //intentSeeRouteDetails.putExtra("facebookID", facebookID);
                startActivity(intentManageRoute);

            }
        });

        tripListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id){
                // Start your Activity according to the item just clicked.

                FullTrip item = (FullTrip) parent.getItemAtPosition(position);

                String dateId = item.getDateId();

                showDeleteTripPopup(dateId);

                return true;
            }
        });


        Log.e("activity","onCreate");

        //LocalBroadcastManager.getInstance(this).registerReceiver(
        //        mFullTripReceiver, new IntentFilter("FullTripFinished"));


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(myService);
            getApplicationContext().bindService(myService, mConnection, Context.BIND_AUTO_CREATE);
        } else {
            getApplicationContext().startService(myService);
            getApplicationContext().bindService(myService, mConnection, Context.BIND_AUTO_CREATE);
        }*/

    }

    public void showDeleteTripPopup(final String date){

        AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
        builder1.setMessage("Do you really want to delete the trip?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        persistentTripStorage.deleteFullTripByDate(date);
                        getFullTripsFromStorageAndList();

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

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("activity", "onResume");

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mFullTripReceiver, new IntentFilter("FullTripFinished"));

        getFullTripsFromStorageAndList();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("activity","onStop");
        // Unbind from the service

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mFullTripReceiver);
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "ActivityRecognitionResult" is broadcasted.
    private BroadcastReceiver mFullTripReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get extra data included in the Intent
            //FullTrip message = (FullTrip) intent.getSerializableExtra("result");

            //listOfActivityDataToBeSent.add(message);

            //ArrayAdapter<FullTrip> itemsAdapter =
            //       new ArrayAdapter<FullTrip>(getApplicationContext(), android.R.layout.simple_list_item_1, message);

            //tripListView.setAdapter(itemsAdapter);

            //itemsAdapter.add(message.get(1));

            //Log.e("ww",message.getAverageSpeed()+"");

            Log.e("receiver", "Full trip finished message received in main activity - sent from state machine");

            getFullTripsFromStorageAndList();

        }
    };

    public void getFullTripsFromStorageAndList(){
        ArrayList<FullTrip> savedFullTrips = persistentTripStorage.getAllFullTripsObject();

//        ReverseTripTimeOrder customComparator = new ReverseTripTimeOrder();
//
//        Collections.sort(savedFullTrips, customComparator);

        ArrayAdapter<FullTrip> itemsAdapter =
                new ArrayAdapter<FullTrip>(getApplicationContext(), android.R.layout.simple_list_item_1, savedFullTrips);

        tripListView.setAdapter(new FullTripListAdapter(getApplicationContext(), savedFullTrips));
    }



}

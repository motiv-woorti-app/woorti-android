package inesc_id.pt.motivandroid;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import inesc_id.pt.motivandroid.home.activities.HomeDrawerActivity;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * OnBootReceiver
 *
 *  Broadcast receiver used to try to start the trip recognition service when the phone is booted.
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


public class OnBootReceiver extends BroadcastReceiver {

    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final Logger LOG = LoggerFactory.getLogger(OnBootReceiver.class.getSimpleName());


    public int getLocationMode(Context context)
    {
        try {

            Log.e("LocationMode", "location mode " + Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE));

            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
//            0 = LOCATION_MODE_OFF
//            1 = LOCATION_MODE_SENSORS_ONLY
//            2 = LOCATION_MODE_BATTERY_SAVING
//            3 = LOCATION_MODE_HIGH_ACCURACY
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 0;
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (EasyPermissions.hasPermissions(context, perms)) {

            LOG.debug("Location mode " + getLocationMode(context));

            Intent myService = new Intent(context, ActivityRecognitionService.class);

            if(getLocationMode(context) == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY || (BuildConfig.VERSION_CODE >= 28)) {

                LOG.debug("Has permissions and high accuracy enabled");

                if(FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getUid() != null){

                    LOG.debug("Trying to start service on boot - uid and user NOT null");


                    ContextCompat.startForegroundService(context, myService);
//                    context.bindService(myService, mConnection, Context.BIND_AUTO_CREATE);

                }else{

                    LOG.debug("uid and user null - NOT starting service");

                }

            }else{

                LOG.debug("HighAccuracy not enabled - NOT starting service");


            }
        } else {

            LOG.debug("No permissions to restart service on boot - NOT starting service");

        }


    }
}
package inesc_id.pt.motivandroid.deprecated;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.auth.FirebaseTokenManager;

@Deprecated
public class LogInActivity extends AppCompatActivity {


    FirebaseAuth auth = FirebaseAuth.getInstance();

    List<AuthUI.IdpConfig> providers;

    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 100;

    private boolean testing = false;

    FirebaseTokenManager firebaseTokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Choose authentication providers
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        firebaseTokenManager = FirebaseTokenManager.getInstance(getApplicationContext());
    }


    public boolean pingGoogle(){

        try {
            URL url = new URL("https://www.google.com");

            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent", "Android Application:"+ 1);
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1000 * 30); // mTimeout is in seconds
            urlc.connect();

            if (urlc.getResponseCode() == 200) {
                Log.e("ping","getResponseCode == 200");
                return new Boolean(true);
            }
            return false;
        } catch (MalformedURLException  e1) {
            e1.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null) {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            firebaseTokenManager.getAndSetFirebaseToken();

            startActivity(intent);

        } else {

            if(!testing) {

                        new MakePingTask().execute();


            }else{
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                showToast("No internet connection...signing in for testing purposes");
            }

        }


    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            Log.e("SignIn","sign in");


            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                //firebaseTokenManager.getAndSetFirebaseToken();

                //showToast("Logged in successfully "+auth.getCurrentUser().getDisplayName());
                startActivity(intent);
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showToast("SignInFailed");
                    startActivityForResult(
                            // Get an instance of AuthUI based on the default app
                            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),
                            RC_SIGN_IN);
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showToast("No internet connection...signing in for testing purposes");

                    if(!testing) {
                        startActivityForResult(
                                // Get an instance of AuthUI based on the default app
                                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),
                                RC_SIGN_IN);



                    }else{
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                    return;
                }

            }
        }
    }

    private void showToast(String errorMessageRes) {

        Toast.makeText(getApplicationContext(), errorMessageRes, Toast.LENGTH_LONG).show();
    }

    class MakePingTask extends AsyncTask<Void, Void, Boolean> {

        protected void onPostExecute(Boolean isConnected) {

            if(isConnected) {
                startActivityForResult(
                        // Get an instance of AuthUI based on the default app
                        AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),
                        RC_SIGN_IN);
            }else{


                AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(LogInActivity.this);

                // set title
                alertDialogBuilder.setTitle("Wifi Settings");

                // set dialog message
                alertDialogBuilder
                        .setMessage("You must have internet connection to log into Motiv")
                        .setCancelable(false)
                        .setPositiveButton("WiFi settings",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //enable wifi
                                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cellular data",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //disable wifi
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .setNeutralButton("Exit app",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                        //disable wifi
                        finish();
                    }
                    });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            return pingGoogle();

        }
    }


}

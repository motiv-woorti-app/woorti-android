package inesc_id.pt.motivandroid.deprecated;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.motviAPIClient.MotivAPIClientManager;
import inesc_id.pt.motivandroid.motviAPIClient.responses.WeatherResponse.response.WeatherResponse;

import inesc_id.pt.motivandroid.utils.DateHelper;

@Deprecated
public class WeatherActivity extends AppCompatActivity {

    MotivAPIClientManager motivAPIClientManager;

    TextView weatherTimestampTextView;
    TextView weatherDataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        weatherTimestampTextView = findViewById(R.id.lastWeatherTimestampTextView);
        weatherDataTextView = findViewById(R.id.weatherDataTimestampTextView);
        weatherDataTextView.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    protected void onResume() {
        super.onResume();

        motivAPIClientManager = MotivAPIClientManager.getInstance(getApplicationContext());

        if(motivAPIClientManager.checkIfWeatherStillValid()){
            Log.d("weather", "Current weather data still valid.");
            weatherDataTextView.setText(motivAPIClientManager.getLastWeather().toString());
            weatherTimestampTextView.setText(DateHelper.getDateFromTSString(motivAPIClientManager.getLastWeatherTimestamp()));
        }else{
            Log.d("weather", "Current weather data not valid. Resquesting new data.");
            motivAPIClientManager.makeGetWeatherRequest();
            registerWeatherRequestListener();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterWeatherRequestListener();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
////        motivAPIClientManager = MotivAPIClientManager.getInstance(getApplicationContext());
//        unregisterWeatherRequestListener();
//
//    }

    private void registerWeatherRequestListener(){

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                weatherDataFromMotivAPI, new IntentFilter(MotivAPIClientManager.keys.broadcastKey));

    }

    private void unregisterWeatherRequestListener(){

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                weatherDataFromMotivAPI);

    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "MotivAPIClientManagerResultBroadcast" is broadcasted.
    private BroadcastReceiver weatherDataFromMotivAPI = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("receiver", "Received message broadcasted from MotivAPIClientManager");

            String action = intent.getStringExtra(MotivAPIClientManager.keys.result);

            if(action.equals(MotivAPIClientManager.keys.success)){

                WeatherResponse weatherResponse = (WeatherResponse) intent.getSerializableExtra(MotivAPIClientManager.keys.weatherData);
                long weatherTimestamp = intent.getLongExtra(MotivAPIClientManager.keys.weatherTimestamp, 0);

                weatherDataTextView.setText(weatherResponse.toString());
                weatherTimestampTextView.setText(DateHelper.getDateFromTSString(weatherTimestamp));

            }else{
                int errorCode = intent.getIntExtra(MotivAPIClientManager.keys.errorCode, 0);
                Toast.makeText(getApplicationContext(), "Weather Request Failed with code "+ errorCode, Toast.LENGTH_LONG).show();
            }

        }
    };


}

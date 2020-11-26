package inesc_id.pt.motivandroid;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.crashlytics.android.Crashlytics;


import org.joda.time.DateTime;

import java.io.File;
import java.util.Locale;

import inesc_id.pt.motivandroid.data.userSettingsData.Language;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.logging.CrashlyticsLoggerHandler;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import pl.brightinventions.slf4android.FileLogHandlerConfiguration;
import pl.brightinventions.slf4android.LoggerConfiguration;

/**
 * Application Class
 *
 *  Called when the application is starting, before any other application objects have been created.
 * It's used for two purposes: workaround to set the language to the one chosen by the user, and set
 * up the logging mechanism to write logs persistently
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
public class ApplicationClass extends Application {

    public static Resources resources;

    public static String reportingEmail = "dummyreportmail@dummy.com";

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        configureLogging();

        resources = getResources();

        //lets check last user settings stored, and set app language
        UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getApplicationContext(), "");

        if(userSettingStateWrapper != null && userSettingStateWrapper.getUserSettings().getLang() != null){

            Locale locale = new Locale(Language.getLanguageSmartphoneFromWoortiID(userSettingStateWrapper.getUserSettings().getLang()));
            Locale.setDefault(locale);
            Resources res = getApplicationContext().getResources();
            Configuration config = new Configuration(res.getConfiguration());
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());


            Log.d("ApplicationClass", "getLang: " + userSettingStateWrapper.getUserSettings().getLang());
            Log.d("ApplicationClass", "getLangSmartphoneFromWoortiID : " + Language.getLanguageSmartphoneFromWoortiID(userSettingStateWrapper.getUserSettings().getLang()));

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Resources activityRes = getApplicationContext().getResources();
                Configuration activityConf = activityRes.getConfiguration();
                activityConf.setLocale(locale);
                activityRes.updateConfiguration(activityConf, activityRes.getDisplayMetrics());

            }


        }

        long hasResetSentToServerTripStatus =  SharedPreferencesUtil.readHasResetSentTripsOnDevice(getApplicationContext());

        if (hasResetSentToServerTripStatus == 0l){
            PersistentTripStorage persistentTripStorage = new PersistentTripStorage(getApplicationContext());
            int unsentTrips = persistentTripStorage.unsendAllFullTripDigestsFrom25October();
            Crashlytics.log(Log.DEBUG,"Persistence", "Reset sent status on " + unsentTrips);
            SharedPreferencesUtil.writeHasResetSentTripsOnDevice(getApplicationContext(), DateTime.now().getMillis());
        }else{
            Crashlytics.log(Log.DEBUG,"Persistence", "No need to reset status!");
        }

    }


    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }


    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void configureLogging(){

        LoggerConfiguration.configuration()
                .removeRootLogcatHandler()
                .addHandlerToRootLogger(new CrashlyticsLoggerHandler());


//        LoggerConfiguration.configuration().setLogLevel(this, LogLevel.DEBUG);

        FileLogHandlerConfiguration fileHandler = LoggerConfiguration.fileLogHandler(this);

        //4mb max limit log file size
        fileHandler.setLogFileSizeLimitInBytes(1024 * 4096);
        //2 rotating files
        fileHandler.setRotateFilesCountLimit(3);

        File root = Environment.getExternalStorageDirectory();

        File myDir = new File(root + "/motivAndroidLogs");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        fileHandler.setFullFilePathPattern(myDir.getAbsolutePath() + "/motivlog%g.log");
        LoggerConfiguration.configuration().addHandlerToRootLogger(fileHandler);

    }


}
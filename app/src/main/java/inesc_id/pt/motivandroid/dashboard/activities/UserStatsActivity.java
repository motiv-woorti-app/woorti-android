package inesc_id.pt.motivandroid.dashboard.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.Collections;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.dashboard.adapters.UserStatsListAdapter;
import inesc_id.pt.motivandroid.dashboard.holders.UserStatsHolder;
import inesc_id.pt.motivandroid.dashboard.sort.SortUserStatsHolderByDecValue;

/**
 * UserStatsActivity
 *
 *  This activity lists statistics about the user usage regarding the multiple modes of transport.
 *  There are 4 different modes, according to the @param passed through keys.modeToDrawIntentKey:
 *
 *  - TIME_TRAVELED_USER_MODE = 0 (ratio of time travelled by the user, by mode of transport)
 *  - DISTANCE_TRAVELED_USER_MODE = 1 (ratio of distance travelled by the user, by mode of transport)
 *  - CO2_USER_MODE = 2 (ratio of co2 emissions by the user, by mode of transport)
 *  - CALORIES_USER_MODE = 3 (ratio of calories burnt by the user, by mode of transport)
 *
 *  The statistics data is passed through the @param keys.arrayStatsIntentKey
 *
 *  * (C) 2017-2020 - The Woorti app is a research (non-commercial) application that was
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
public class UserStatsActivity extends AppCompatActivity implements View.OnClickListener {

    int mode;

    ArrayList<UserStatsHolder> userStatsHolders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);

        mode = getIntent().getIntExtra(keys.modeToDrawIntentKey, 0);

        try {
            userStatsHolders = (ArrayList<UserStatsHolder>) getIntent().getSerializableExtra(keys.arrayStatsIntentKey);
            UserStatsHolder.removeInvalidModesAndStats(userStatsHolders);

        }catch (Exception e){
            e.printStackTrace();
            userStatsHolders = new ArrayList<>();
        }


        TextView titleTextView = findViewById(R.id.titleTextView);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        switch(mode){

            case keys.TIME_TRAVELED_USER_MODE:
                titleTextView.setText(getString(R.string.Time_Spent_In_Travel));
                break;

            case keys.DISTANCE_TRAVELED_USER_MODE:
                titleTextView.setText(R.string.Travelled_Distance);
                break;

            case keys.CO2_USER_MODE:
                titleTextView.setText(R.string.CO2_Emissions);
                break;

            case keys.CALORIES_USER_MODE:
                titleTextView.setText(getString(R.string.Calories));
                break;

        }

        if(userStatsHolders != null && userStatsHolders.size() > 0) {

            Collections.sort(userStatsHolders, new SortUserStatsHolderByDecValue());

            UserStatsListAdapter userStatsListAdapter = new UserStatsListAdapter(userStatsHolders, getApplicationContext(), mode);

            ExpandableHeightListView statsPerModeUsedListView = findViewById(R.id.statsPerModeUsedListView);
            statsPerModeUsedListView.setExpanded(true);
            statsPerModeUsedListView.setAdapter(userStatsListAdapter);
        }

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

        String modeToDrawIntentKey = "MODE_TO_DRAW_INTENT_KEY";
        String arrayStatsIntentKey = "ARRAY_STATS_INTENT_KEY";


        int TIME_TRAVELED_USER_MODE = 0;
        int DISTANCE_TRAVELED_USER_MODE = 1;
        int CO2_USER_MODE = 2;
        int CALORIES_USER_MODE = 3;

        int TIME_TRAVELED_COMMUNITY_MODE = 4;
        int DISTANCE_TRAVELED_COMMUNITY_MODE = 5;
        int CO2_COMMUNITY_MODE = 6;
        int CALORIES_COMMUNITY_MODE = 7;

    }


}

package inesc_id.pt.motivandroid.mobilityCoach.activities;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.stories.Story;


/**
 * StoryActivity
 *
 *   Activity that allows the user to read a "Story". The story to be shown is passed through the
 *   intent with the name keys.INTENT_STORY.
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

public class StoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        Story story = (Story) getIntent().getSerializableExtra(keys.INTENT_STORY);

        TextView titleTextView = (TextView) findViewById(R.id.storyTitleTextView);
        titleTextView.setText(story.getLessonTitle());

        TextView titleContentTextView = (TextView) findViewById(R.id.storyContentTitleTextView);
        titleContentTextView.setText(story.getContentTitle());

        TextView contentTextView = (TextView) findViewById(R.id.storyContentTextView);
        contentTextView.setText(story.getContentText());

        RoundedImageView storyImage = (RoundedImageView) findViewById(R.id.storyImageView);

        Glide.with(this).load(story.getImageDrawable())
//                    .override(view.getMeasuredWidth(), view.getMeasuredHeight())
                .centerCrop()
                .into(storyImage);

        Button closeButton = findViewById(R.id.closeButton);
        Button doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(buttonListener);
        closeButton.setOnClickListener(buttonListener);
    }

    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.doneButton:
                case R.id.closeButton:
                    finish();
                    break;
            }
        }
    };

    public interface keys{

        String INTENT_STORY = "intent_story";

    }

}

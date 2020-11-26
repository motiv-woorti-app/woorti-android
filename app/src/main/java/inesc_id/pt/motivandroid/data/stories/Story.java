package inesc_id.pt.motivandroid.data.stories;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;

/**
 * Story
 *
 * Data structure holding stories' data
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

public class Story implements Serializable{

    int storyID;
    int imageDrawable;
    String lessonTitle;
    String contentTitle;
    String contentText;

    public Story(int storyID, int imageDrawable, String lessonTitle, String contentTitle, String contentText) {
        this.storyID = storyID;
        this.imageDrawable = imageDrawable;
        this.lessonTitle = lessonTitle;
        this.contentTitle = contentTitle;
        this.contentText = contentText;
    }

    public int getStoryID() {
        return storyID;
    }

    public void setStoryID(int storyID) {
        this.storyID = storyID;
    }

    public int getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(int imageDrawable) {
        this.imageDrawable = imageDrawable;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }


    public static ArrayList<Story> getDummmyStoryList(Context context){

        ArrayList<Story> result = new ArrayList<>();

        result.add(new Story(0,
                R.drawable.story_1_image_chiaras_train_ride,
                context.getString(R.string.Story, 1),
                context.getResources().getString(R.string.story_1_title_chiaras_new_train_ride),
                context.getResources().getString(R.string.story_1_text_chiaras_train_ride)));

        result.add(new Story(1,
                R.drawable.story_2_image_the_best_way_to_get_great_ideas_while_moving_p1,
                context.getString(R.string.Story, 2),
                context.getResources().getString(R.string.story_2_title_the_best_way_to_get_great_ideas_while_moving_p1),
                context.getResources().getString(R.string.story_2_text_the_best_way_to_get_great_ideas_while_moving_p1)));

        result.add(new Story(2,
                R.drawable.story_3_image_the_best_way_to_get_great_ideas_while_moving_p2,
                context.getString(R.string.Story, 3),
                context.getResources().getString(R.string.story_3_title_the_best_way_to_get_great_ideas_while_moving_p2),
                context.getResources().getString(R.string.story_3_text_the_best_way_to_get_great_ideas_while_moving_p2)));

        result.add(new Story(3,
                R.drawable.story_4_image_the_7_shades_of_pedros_cycling_time,
                context.getString(R.string.Story, 4),
                context.getResources().getString(R.string.story_4_title_the_7_shades_of_pedros_cycling_time),
                context.getResources().getString(R.string.story_4_text_the_7_shades_of_pedros_cycling_time)));

        result.add(new Story(4,
                R.drawable.story_5_image_stopping_or_not_stopping_in_a_moving_walkway,
                context.getString(R.string.Story, 5),
                context.getResources().getString(R.string.story_5_title_stopping_or_not_stopping_in_a_moving_walkway),
                context.getResources().getString(R.string.story_5_text_stopping_or_not_stopping_in_a_moving_walkway)));

        result.add(new Story(5,
                R.drawable.story_6_image_virtual_travel_time,
                context.getString(R.string.Story, 6),
                context.getResources().getString(R.string.story_6_title_virtual_travel_time),
                context.getResources().getString(R.string.story_6_text_virtual_travel_time)));

        return result;
    }

    public static Story getStoryById(int id, ArrayList<Story> stories){

        for(Story story : stories){

            if(story.getStoryID() == id){

                return story;

            }

        }

        return null;

    }

}

package inesc_id.pt.motivandroid.data.stories;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Story
 *
 * Class keeping state of stories seen/available to the user
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

public class StoryStateful implements Serializable{

    @Expose
    int storyID;

    @Expose
    boolean read;

    /**
     * 0l if user has already read the story, otherwise reading timestamp
     */
    @Expose
    long readTimestamp;

    /**
     * timestamp at which the story will become available to the user
     */

    @Expose
    long availableTimestamp;

    public StoryStateful(int storyID, boolean read, long readTimestamp, long availableTimestamp) {
        this.storyID = storyID;
        this.read = read;
        this.readTimestamp = readTimestamp;
        this.availableTimestamp = availableTimestamp;
    }

    public int getStoryID() {
        return storyID;
    }

    public void setStoryID(int storyID) {
        this.storyID = storyID;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public long getReadTimestamp() {
        return readTimestamp;
    }

    public void setReadTimestamp(long readTimestamp) {
        this.readTimestamp = readTimestamp;
    }

    public long getAvailableTimestamp() {
        return availableTimestamp;
    }

    public void setAvailableTimestamp(long availableTimestamp) {
        this.availableTimestamp = availableTimestamp;
    }


}

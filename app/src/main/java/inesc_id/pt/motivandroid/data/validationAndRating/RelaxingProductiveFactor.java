package inesc_id.pt.motivandroid.data.validationAndRating;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * RelaxingProductiveFactor
 *
 * Data structure for the productivity and relaxing factors
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
public class RelaxingProductiveFactor implements Serializable{

    @Expose
    @SerializedName("factorText")
    String text;

    @Expose
    @SerializedName("factorCode")
    String code;

    @Expose
    int satisfactionFactor;

    @Expose
    int orderValue;

    public RelaxingProductiveFactor(String text, String code) {
        this.text = text;
        this.code = code;
        this.satisfactionFactor = 50;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getSatisfactionFactor() {
        return satisfactionFactor;
    }

    public void setSatisfactionFactor(int satisfactionFactor) {
        this.satisfactionFactor = satisfactionFactor;
    }

    public int getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(int orderValue) {
        this.orderValue = orderValue;
    }

    public static ArrayList<RelaxingProductiveFactor> getListOfRelaxingFactors(){

        ArrayList<RelaxingProductiveFactor> result = new ArrayList<>();

        result.add(new RelaxingProductiveFactor("Cleanliness inside", "CLEANLINESS_INSIDE"));
        result.add(new RelaxingProductiveFactor("Seating availability (inside)", "SEATING_AVAILABILITY_INSIDE"));
        result.add(new RelaxingProductiveFactor("Air Quality", "AIR_QUALITY"));
        result.add(new RelaxingProductiveFactor("Temperature", "TEMPERATURE"));
        result.add(new RelaxingProductiveFactor("Personal space (crowd level)", "PERSONAL_SPACE"));
        result.add(new RelaxingProductiveFactor("Privacy", "PRIVACY"));
        result.add(new RelaxingProductiveFactor("Jerkiness/ Motion sickness", "JERKINESS"));
        result.add(new RelaxingProductiveFactor("Design/ Architecture", "DESIGN"));
        result.add(new RelaxingProductiveFactor("Maintenance (upkeep/repair)", "MAINTENANCE"));
        result.add(new RelaxingProductiveFactor("Cleanliness Outside", "CLEANLINESS_OUTSIDE"));
        result.add(new RelaxingProductiveFactor("Seating availability (outside)", "SEATING_AVAILABILITY_OUTSIDE"));
        result.add(new RelaxingProductiveFactor("Air pollution", "AIR_POLLUTION"));
        result.add(new RelaxingProductiveFactor("Noise", "NOISE"));
        result.add(new RelaxingProductiveFactor("Traffic (congestion)", "TRAFFIC"));
        result.add(new RelaxingProductiveFactor("Pleasantness", "PLEASANTNESS"));
        result.add(new RelaxingProductiveFactor("Smoothless (pavement/floors)", "SMOOTHLESS"));
        result.add(new RelaxingProductiveFactor("Accessibility (easy to get to or through)", "ACCESSIBILITY"));

        return result;

    }

    public interface keys {



    }
}

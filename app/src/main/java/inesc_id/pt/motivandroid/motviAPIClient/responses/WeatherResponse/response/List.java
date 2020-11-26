package inesc_id.pt.motivandroid.motviAPIClient.responses.WeatherResponse.response;

import java.io.Serializable;
import java.util.Date;

import inesc_id.pt.motivandroid.utils.DateHelper;

/**
 *  List
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
public class List implements Serializable {

    private Long dt;
    private Main main;
    private java.util.List<Weather> weather = null;
    private Clouds clouds;
    private Wind wind;
    private Sys sys;
    private String dtTxt;
    private Rain rain;
    private Snow snow;

    /**
     * No args constructor for use in serialization
     *
     */
    public List() {
    }

    /**
     *
     * @param clouds
     * @param dt
     * @param wind
     * @param sys
     * @param dtTxt
     * @param weather
     * @param rain
     * @param main
     */
    public List(Long dt, Main main, java.util.List<Weather> weather, Clouds clouds, Wind wind, Sys sys, String dtTxt, Rain rain) {
        super();
        this.dt = dt;
        this.main = main;
        this.weather = weather;
        this.clouds = clouds;
        this.wind = wind;
        this.sys = sys;
        this.dtTxt = dtTxt;
        this.rain = rain;
    }

    public Long getDt() {
        return dt;
    }

    public void setDt(Long dt) {
        this.dt = dt;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public java.util.List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(java.util.List<Weather> weather) {
        this.weather = weather;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public String getDtTxt() {
        return dtTxt;
    }

    public void setDtTxt(String dtTxt) {
        this.dtTxt = dtTxt;
    }

    public Rain getRain() {
        return rain;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Date: " + DateHelper.getDateFromTSString(dt*1000) + "\n");

        stringBuilder.append(main.toString());

        for(Weather w : weather){
            stringBuilder.append(w.toString());
        }

        stringBuilder.append(clouds.toString());
        stringBuilder.append(wind.toString());

        return stringBuilder.toString();

    }
}
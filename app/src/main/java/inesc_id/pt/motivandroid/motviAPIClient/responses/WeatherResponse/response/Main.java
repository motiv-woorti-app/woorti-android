package inesc_id.pt.motivandroid.motviAPIClient.responses.WeatherResponse.response;

import java.io.Serializable;

/**
 *  Main
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
public class Main implements Serializable {

    private Double temp;
    private Double temp_min;
    private Double temp_max;
    private Double pressure;
    private Double sea_level;
    private Double grnd_level;
    private Integer humidity;

    /**
     * No args constructor for use in serialization
     *
     */
    public Main() {
    }

    /**
     *
     * @param seaLevel
     * @param humidity
     * @param pressure
     * @param grndLevel
     * @param tempMax
     * @param temp
     * @param tempMin
     */
    public Main(Double temp, Double tempMin, Double tempMax, Double pressure, Double seaLevel, Double grndLevel, Integer humidity) {
        super();
        this.temp = temp;
        this.temp_min = tempMin;
        this.temp_max = tempMax;
        this.pressure = pressure;
        this.sea_level = seaLevel;
        this.grnd_level = grndLevel;
        this.humidity = humidity;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getTempMin() {
        return temp_min;
    }

    public void setTempMin(Double tempMin) {
        this.temp_min = tempMin;
    }

    public Double getTempMax() {
        return temp_max;
    }

    public void setTempMax(Double tempMax) {
        this.temp_max = tempMax;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Double getSeaLevel() {
        return sea_level;
    }

    public void setSeaLevel(Double seaLevel) {
        this.sea_level = seaLevel;
    }

    public Double getGrndLevel() {
        return grnd_level;
    }

    public void setGrndLevel(Double grndLevel) {
        this.grnd_level = grndLevel;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Temperature: " + temp + "cº" + "\n");
        stringBuilder.append("Min Temperature: " + temp_min + "cº" + "\n");
        stringBuilder.append("Max Temperature: " + temp_max + "cº" + "\n");
        stringBuilder.append("Pressure: " + pressure + "\n");
        stringBuilder.append("Sea level: " + sea_level + "\n");
        stringBuilder.append("Ground level: " + grnd_level + "\n");
        stringBuilder.append("Humidity: " + humidity + "%" + "\n");

        return stringBuilder.toString();
    }
}
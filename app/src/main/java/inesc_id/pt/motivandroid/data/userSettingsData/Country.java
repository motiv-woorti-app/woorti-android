package inesc_id.pt.motivandroid.data.userSettingsData;

import android.content.Context;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;

/**
 * Country
 *
 * Data structure representation of a country.
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
public class Country {


    /**
     * ISO code for the country - ISO 3166-1 alpha-3 - three-letter representation
     */
    private String iso;

    /**
     * ISO code for the country - ISO 3166-1 alpha-2 - two-letter representation
     */
    private String code;

    private String name;

    public Country(String iso, String code, String name) {
        this.iso = iso;
        this.code = code;
        this.name = name;
    }

    public String getIso() {
        return iso;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

//    @Override
//    public int compareTo(Object o) {
//        return this.name.compareTo(((Country)o).getName());
//    }

//    @Override
//    public String toString() {
//        return "langs.append(Languages(smartphoneID: \"" + code+"\",  woortiID: \""+iso+"\", name: \""+ name + "\"))";
//    }

//    @Override
//    public String toString() {
//        return "countryList.add(new Country("+iso+", "+code+", "+name+"))";
//    }

    public static Country getCountryIfNotYetISO(String prevCountry){

        if(prevCountry == null){
            return new Country("SVK", "SK", "Slovakia");
        }
        else if (prevCountry.length() == 3){
            return null;
        }else{

            for (Country country : getFullCountryList()){

                if(country.getName().equals(prevCountry)){
                    return country;
                }

            }

        }

        return new Country("SVK", "SK", "Slovakia");

    }


    public static ArrayList<Country> getSelectedCountryList(Context context){

        ArrayList<Country> countryList = new ArrayList<>();

        countryList.add(new Country("PRT", "PT", "Portugal"));
        countryList.add(new Country("SVK", "SK", "Slovakia"));
        countryList.add(new Country("BEL", "BE", "Belgique/België/Belgien"));
        countryList.add(new Country("ESP", "ES", "España"));
        countryList.add(new Country("FIN", "FI", "Suomi"));
        countryList.add(new Country("CHE", "CH", "Suisse/Svizzera/Schweiz"));
        countryList.add(new Country("ITA", "IT", "Italia"));
        countryList.add(new Country("FRA", "FR", "France"));
        countryList.add(new Country("NOR", "NO", "Norge"));
        countryList.add(new Country("HRV", "HR", "Hrvatska"));
        countryList.add(new Country("OTH", "OT", context.getString(R.string.Other)));

        return countryList;
    }

    public static ArrayList<String> getSelectedCountryListDisplayName(Context context){

        ArrayList<String> result = new ArrayList<>();

        for (Country country : getSelectedCountryList(context)){
            result.add(country.getName());
        }

        return result;

    }

    public static ArrayList<String> getFullCountryListDisplayName(){

        ArrayList<String> result = new ArrayList<>();

        for (Country country : getFullCountryList()){
            result.add(country.getName());
        }

        return result;

    }

    public static String getOtherISOCode(){
        return "OTH";
    }

    public static String getDisplayFromISOCode(String isoCode){

        for(Country country: getFullCountryList()){

            if(country.getIso().equals(isoCode)){
                return country.getName();
            }

        }

        return "undefined";

    }

    /**
     * @param context
     * @param iso code of the country
     * @return array of strings with countries' city names
     */
    public static ArrayList<String> getCityListPerCountry(Context context, String iso){
        ArrayList<String> cities = new ArrayList<>();

        switch (iso){
            case "ESP":
                cities.add("Barcelona");
                cities.add("Girona");
                cities.add("Tarragona");
                cities.add("Lleida");
//                cities.add("Zaragoza");
//                cities.add("Málaga");
//                cities.add("Murcia");
//                cities.add("Palma");
//                cities.add("Las Palmas de Gran Canaria");
//                cities.add("Bilbao");
                break;
            case "PRT":
                cities.add("Lisbon");
                cities.add("Porto");
//                cities.add("Sintra");
//                cities.add("Cascais");
//                cities.add("Braga");
//                cities.add("Coimbra");
//                cities.add("Faro");
                break;
            case "SVK":

                cities.add("Žilina");
                cities.add("Bratislava");
                cities.add("Trnava");
                cities.add("Nitra");
                cities.add("Trenčín");
                cities.add("Banská Bystrica");
                cities.add("Košice");
                cities.add("Prešov");

                break;
            case "BEL":
                cities.add("Antwerp");
                cities.add("Brugge");
                cities.add("Brussels");
                cities.add("Charleroi");
                cities.add("Gent");
                cities.add("Leuven");
                break;
            case "FIN":

                cities.add("Helsinki");
                cities.add("Tampere");
                cities.add("Turku");
                cities.add("Oulu");
                cities.add("Etelä-Suomi");
                cities.add("Länsi-Suomi");
                cities.add("Keski-Suomi");
                cities.add("Itä-Suomi");
                cities.add("Pohjois-Suomi");
                break;
            case "CHE":
                cities.add("Lausanne");
                cities.add("Genève");
                cities.add("Montreux");
                cities.add("Fribourg");
                cities.add("Bern");
                cities.add("Basel");
                cities.add("Zurich");
                cities.add("Neuchâtel");
                cities.add("Yverdon-les-Bains");
                break;
            case "ITA":
                cities.add("Milan");
                break;
            case "FRA":
                cities.add("Paris");
                cities.add("Lyon");
                cities.add("Grenoble");
                cities.add("Nevers");
                cities.add("Nantes");
                cities.add("Bordeaux");
                cities.add("Toulouse");
                cities.add("Strasbourg");
                cities.add("Amiens");
                cities.add("Angers");
                cities.add("Lille");
                cities.add("Brest");
                cities.add("Marseille");
                cities.add("Saint Brieuc");
                cities.add("Montpellier");
                break;
            case "NOR":
                cities.add("Oslo");
                cities.add("Bergen");
                cities.add("Trondheim");
                cities.add("Stavager");
                cities.add("Drammen");
                cities.add("Fredrikstad");
                cities.add("Porsgrunn");
                cities.add("Skien");
                cities.add("Kristiansand");
                cities.add("Ålesund");
                cities.add("Tønsberg");
                break;
            case "HRV":
                cities.add("Zagreb");
                cities.add("Velika Gorica");
                cities.add("Samobor");
                cities.add("Zaprešić");
                cities.add("Dugo selo");
                cities.add("Zagrebačka županija");
                cities.add("Split");
                cities.add("Rijeka");
                cities.add("Osijek");
                cities.add("Varaždin");
                cities.add("Zadar");
                break;
            default:
                break;
        }
        cities.add(context.getString(R.string.Other));
        return cities;
    }

    /**
     * @return list of available countries
     */
    public static ArrayList<Country> getFullCountryList(){

        ArrayList<Country> countryList = new ArrayList<>();

        countryList.add(new Country("AFG", "AF", "Afghanistan"));
        countryList.add(new Country("ALB", "AL", "Albania"));
        countryList.add(new Country("DZA", "DZ", "Algeria"));
        countryList.add(new Country("ASM", "AS", "American Samoa"));
        countryList.add(new Country("AND", "AD", "Andorra"));
        countryList.add(new Country("AGO", "AO", "Angola"));
        countryList.add(new Country("AIA", "AI", "Anguilla"));
        countryList.add(new Country("ATA", "AQ", "Antarctica"));
        countryList.add(new Country("ATG", "AG", "Antigua and Barbuda"));
        countryList.add(new Country("ARG", "AR", "Argentina"));
        countryList.add(new Country("ARM", "AM", "Armenia"));
        countryList.add(new Country("ABW", "AW", "Aruba"));
        countryList.add(new Country("AUS", "AU", "Australia"));
        countryList.add(new Country("AUT", "AT", "Austria"));
        countryList.add(new Country("AZE", "AZ", "Azerbaijan"));
        countryList.add(new Country("BHS", "BS", "Bahamas"));
        countryList.add(new Country("BHR", "BH", "Bahrain"));
        countryList.add(new Country("BGD", "BD", "Bangladesh"));
        countryList.add(new Country("BRB", "BB", "Barbados"));
        countryList.add(new Country("BLR", "BY", "Belarus"));
        countryList.add(new Country("BEL", "BE", "Belgique/België/Belgien"));
        countryList.add(new Country("BLZ", "BZ", "Belize"));
        countryList.add(new Country("BEN", "BJ", "Benin"));
        countryList.add(new Country("BMU", "BM", "Bermuda"));
        countryList.add(new Country("BTN", "BT", "Bhutan"));
        countryList.add(new Country("BOL", "BO", "Bolivia"));
        countryList.add(new Country("BES", "BQ", "Bonaire, Sint Eustatius and Saba"));
        countryList.add(new Country("BIH", "BA", "Bosnia and Herzegovina"));
        countryList.add(new Country("BWA", "BW", "Botswana"));
        countryList.add(new Country("BVT", "BV", "Bouvet Island"));
        countryList.add(new Country("BRA", "BR", "Brazil"));
        countryList.add(new Country("IOT", "IO", "British Indian Ocean Territory"));
        countryList.add(new Country("VGB", "VG", "British Virgin Islands"));
        countryList.add(new Country("BRN", "BN", "Brunei"));
        countryList.add(new Country("BGR", "BG", "Bulgaria"));
        countryList.add(new Country("BFA", "BF", "Burkina Faso"));
        countryList.add(new Country("BDI", "BI", "Burundi"));
        countryList.add(new Country("KHM", "KH", "Cambodia"));
        countryList.add(new Country("CMR", "CM", "Cameroon"));
        countryList.add(new Country("CAN", "CA", "Canada"));
        countryList.add(new Country("CPV", "CV", "Cape Verde"));
        countryList.add(new Country("CYM", "KY", "Cayman Islands"));
        countryList.add(new Country("CAF", "CF", "Central African Republic"));
        countryList.add(new Country("TCD", "TD", "Chad"));
        countryList.add(new Country("CHL", "CL", "Chile"));
        countryList.add(new Country("CHN", "CN", "China"));
        countryList.add(new Country("CXR", "CX", "Christmas Island"));
        countryList.add(new Country("CCK", "CC", "Cocos Islands"));
        countryList.add(new Country("COL", "CO", "Colombia"));
        countryList.add(new Country("COM", "KM", "Comoros"));
        countryList.add(new Country("COG", "CG", "Congo"));
        countryList.add(new Country("COK", "CK", "Cook Islands"));
        countryList.add(new Country("CRI", "CR", "Costa Rica"));
        countryList.add(new Country("HRV", "HR", "Hrvatska"));
        countryList.add(new Country("CUB", "CU", "Cuba"));
        countryList.add(new Country("CUW", "CW", "Curaçao"));
        countryList.add(new Country("CYP", "CY", "Cyprus"));
        countryList.add(new Country("CZE", "CZ", "Czech Republic"));
        countryList.add(new Country("CIV", "CI", "Côte d'Ivoire"));
        countryList.add(new Country("DNK", "DK", "Denmark"));
        countryList.add(new Country("DJI", "DJ", "Djibouti"));
        countryList.add(new Country("DMA", "DM", "Dominica"));
        countryList.add(new Country("DOM", "DO", "Dominican Republic"));
        countryList.add(new Country("ECU", "EC", "Ecuador"));
        countryList.add(new Country("EGY", "EG", "Egypt"));
        countryList.add(new Country("SLV", "SV", "El Salvador"));
        countryList.add(new Country("GNQ", "GQ", "Equatorial Guinea"));
        countryList.add(new Country("ERI", "ER", "Eritrea"));
        countryList.add(new Country("EST", "EE", "Estonia"));
        countryList.add(new Country("ETH", "ET", "Ethiopia"));
        countryList.add(new Country("FLK", "FK", "Falkland Islands"));
        countryList.add(new Country("FRO", "FO", "Faroe Islands"));
        countryList.add(new Country("FJI", "FJ", "Fiji"));
        countryList.add(new Country("FIN", "FI", "Suomi"));
        countryList.add(new Country("FRA", "FR", "France"));
        countryList.add(new Country("GUF", "GF", "French Guiana"));
        countryList.add(new Country("PYF", "PF", "French Polynesia"));
        countryList.add(new Country("ATF", "TF", "French Southern Territories"));
        countryList.add(new Country("GAB", "GA", "Gabon"));
        countryList.add(new Country("GMB", "GM", "Gambia"));
        countryList.add(new Country("GEO", "GE", "Georgia"));
        countryList.add(new Country("DEU", "DE", "Germany"));
        countryList.add(new Country("GHA", "GH", "Ghana"));
        countryList.add(new Country("GIB", "GI", "Gibraltar"));
        countryList.add(new Country("GRC", "GR", "Greece"));
        countryList.add(new Country("GRL", "GL", "Greenland"));
        countryList.add(new Country("GRD", "GD", "Grenada"));
        countryList.add(new Country("GLP", "GP", "Guadeloupe"));
        countryList.add(new Country("GUM", "GU", "Guam"));
        countryList.add(new Country("GTM", "GT", "Guatemala"));
        countryList.add(new Country("GGY", "GG", "Guernsey"));
        countryList.add(new Country("GIN", "GN", "Guinea"));
        countryList.add(new Country("GNB", "GW", "Guinea-Bissau"));
        countryList.add(new Country("GUY", "GY", "Guyana"));
        countryList.add(new Country("HTI", "HT", "Haiti"));
        countryList.add(new Country("HMD", "HM", "Heard Island And McDonald Islands"));
        countryList.add(new Country("HND", "HN", "Honduras"));
        countryList.add(new Country("HKG", "HK", "Hong Kong"));
        countryList.add(new Country("HUN", "HU", "Hungary"));
        countryList.add(new Country("ISL", "IS", "Iceland"));
        countryList.add(new Country("IND", "IN", "India"));
        countryList.add(new Country("IDN", "ID", "Indonesia"));
        countryList.add(new Country("IRN", "IR", "Iran"));
        countryList.add(new Country("IRQ", "IQ", "Iraq"));
        countryList.add(new Country("IRL", "IE", "Ireland"));
        countryList.add(new Country("IMN", "IM", "Isle Of Man"));
        countryList.add(new Country("ISR", "IL", "Israel"));
        countryList.add(new Country("ITA", "IT", "Italia"));
        countryList.add(new Country("JAM", "JM", "Jamaica"));
        countryList.add(new Country("JPN", "JP", "Japan"));
        countryList.add(new Country("JEY", "JE", "Jersey"));
        countryList.add(new Country("JOR", "JO", "Jordan"));
        countryList.add(new Country("KAZ", "KZ", "Kazakhstan"));
        countryList.add(new Country("KEN", "KE", "Kenya"));
        countryList.add(new Country("KIR", "KI", "Kiribati"));
        countryList.add(new Country("KWT", "KW", "Kuwait"));
        countryList.add(new Country("KGZ", "KG", "Kyrgyzstan"));
        countryList.add(new Country("LAO", "LA", "Laos"));
        countryList.add(new Country("LVA", "LV", "Latvia"));
        countryList.add(new Country("LBN", "LB", "Lebanon"));
        countryList.add(new Country("LSO", "LS", "Lesotho"));
        countryList.add(new Country("LBR", "LR", "Liberia"));
        countryList.add(new Country("LBY", "LY", "Libya"));
        countryList.add(new Country("LIE", "LI", "Liechtenstein"));
        countryList.add(new Country("LTU", "LT", "Lithuania"));
        countryList.add(new Country("LUX", "LU", "Luxembourg"));
        countryList.add(new Country("MAC", "MO", "Macao"));
        countryList.add(new Country("MKD", "MK", "Macedonia"));
        countryList.add(new Country("MDG", "MG", "Madagascar"));
        countryList.add(new Country("MWI", "MW", "Malawi"));
        countryList.add(new Country("MYS", "MY", "Malaysia"));
        countryList.add(new Country("MDV", "MV", "Maldives"));
        countryList.add(new Country("MLI", "ML", "Mali"));
        countryList.add(new Country("MLT", "MT", "Malta"));
        countryList.add(new Country("MHL", "MH", "Marshall Islands"));
        countryList.add(new Country("MTQ", "MQ", "Martinique"));
        countryList.add(new Country("MRT", "MR", "Mauritania"));
        countryList.add(new Country("MUS", "MU", "Mauritius"));
        countryList.add(new Country("MYT", "YT", "Mayotte"));
        countryList.add(new Country("MEX", "MX", "Mexico"));
        countryList.add(new Country("FSM", "FM", "Micronesia"));
        countryList.add(new Country("MDA", "MD", "Moldova"));
        countryList.add(new Country("MCO", "MC", "Monaco"));
        countryList.add(new Country("MNG", "MN", "Mongolia"));
        countryList.add(new Country("MNE", "ME", "Montenegro"));
        countryList.add(new Country("MSR", "MS", "Montserrat"));
        countryList.add(new Country("MAR", "MA", "Morocco"));
        countryList.add(new Country("MOZ", "MZ", "Mozambique"));
        countryList.add(new Country("MMR", "MM", "Myanmar"));
        countryList.add(new Country("NAM", "NA", "Namibia"));
        countryList.add(new Country("NRU", "NR", "Nauru"));
        countryList.add(new Country("NPL", "NP", "Nepal"));
        countryList.add(new Country("NLD", "NL", "Netherlands"));
        countryList.add(new Country("ANT", "AN", "Netherlands Antilles"));
        countryList.add(new Country("NCL", "NC", "New Caledonia"));
        countryList.add(new Country("NZL", "NZ", "New Zealand"));
        countryList.add(new Country("NIC", "NI", "Nicaragua"));
        countryList.add(new Country("NER", "NE", "Niger"));
        countryList.add(new Country("NGA", "NG", "Nigeria"));
        countryList.add(new Country("NIU", "NU", "Niue"));
        countryList.add(new Country("NFK", "NF", "Norfolk Island"));
        countryList.add(new Country("PRK", "KP", "North Korea"));
        countryList.add(new Country("MNP", "MP", "Northern Mariana Islands"));
        countryList.add(new Country("NOR", "NO", "Norge"));
        countryList.add(new Country("OMN", "OM", "Oman"));
        countryList.add(new Country("PAK", "PK", "Pakistan"));
        countryList.add(new Country("PLW", "PW", "Palau"));
        countryList.add(new Country("PSE", "PS", "Palestine"));
        countryList.add(new Country("PAN", "PA", "Panama"));
        countryList.add(new Country("PNG", "PG", "Papua New Guinea"));
        countryList.add(new Country("PRY", "PY", "Paraguay"));
        countryList.add(new Country("PER", "PE", "Peru"));
        countryList.add(new Country("PHL", "PH", "Philippines"));
        countryList.add(new Country("PCN", "PN", "Pitcairn"));
        countryList.add(new Country("POL", "PL", "Poland"));
        countryList.add(new Country("PRT", "PT", "Portugal"));
        countryList.add(new Country("PRI", "PR", "Puerto Rico"));
        countryList.add(new Country("QAT", "QA", "Qatar"));
        countryList.add(new Country("REU", "RE", "Reunion"));
        countryList.add(new Country("ROU", "RO", "Romania"));
        countryList.add(new Country("RUS", "RU", "Russia"));
        countryList.add(new Country("RWA", "RW", "Rwanda"));
        countryList.add(new Country("BLM", "BL", "Saint Barthélemy"));
        countryList.add(new Country("SHN", "SH", "Saint Helena"));
        countryList.add(new Country("KNA", "KN", "Saint Kitts And Nevis"));
        countryList.add(new Country("LCA", "LC", "Saint Lucia"));
        countryList.add(new Country("MAF", "MF", "Saint Martin"));
        countryList.add(new Country("SPM", "PM", "Saint Pierre And Miquelon"));
        countryList.add(new Country("VCT", "VC", "Saint Vincent And The Grenadines"));
        countryList.add(new Country("WSM", "WS", "Samoa"));
        countryList.add(new Country("SMR", "SM", "San Marino"));
        countryList.add(new Country("STP", "ST", "Sao Tome And Principe"));
        countryList.add(new Country("SAU", "SA", "Saudi Arabia"));
        countryList.add(new Country("SEN", "SN", "Senegal"));
        countryList.add(new Country("SRB", "RS", "Serbia"));
        countryList.add(new Country("SYC", "SC", "Seychelles"));
        countryList.add(new Country("SLE", "SL", "Sierra Leone"));
        countryList.add(new Country("SGP", "SG", "Singapore"));
        countryList.add(new Country("SXM", "SX", "Sint Maarten (Dutch part)"));
        countryList.add(new Country("SVK", "SK", "Slovakia"));
        countryList.add(new Country("SVN", "SI", "Slovenia"));
        countryList.add(new Country("SLB", "SB", "Solomon Islands"));
        countryList.add(new Country("SOM", "SO", "Somalia"));
        countryList.add(new Country("ZAF", "ZA", "South Africa"));
        countryList.add(new Country("SGS", "GS", "South Georgia And The South Sandwich Islands"));
        countryList.add(new Country("KOR", "KR", "South Korea"));
        countryList.add(new Country("SSD", "SS", "South Sudan"));
        countryList.add(new Country("ESP", "ES", "España"));
        countryList.add(new Country("LKA", "LK", "Sri Lanka"));
        countryList.add(new Country("SDN", "SD", "Sudan"));
        countryList.add(new Country("SUR", "SR", "Suriname"));
        countryList.add(new Country("SJM", "SJ", "Svalbard And Jan Mayen"));
        countryList.add(new Country("SWZ", "SZ", "Swaziland"));
        countryList.add(new Country("SWE", "SE", "Sweden"));
        countryList.add(new Country("CHE", "CH", "Suisse/Svizzera/Schweiz"));
        countryList.add(new Country("SYR", "SY", "Syria"));
        countryList.add(new Country("TWN", "TW", "Taiwan"));
        countryList.add(new Country("TJK", "TJ", "Tajikistan"));
        countryList.add(new Country("TZA", "TZ", "Tanzania"));
        countryList.add(new Country("THA", "TH", "Thailand"));
        countryList.add(new Country("COD", "CD", "The Democratic Republic Of Congo"));
        countryList.add(new Country("TLS", "TL", "Timor-Leste"));
        countryList.add(new Country("TGO", "TG", "Togo"));
        countryList.add(new Country("TKL", "TK", "Tokelau"));
        countryList.add(new Country("TON", "TO", "Tonga"));
        countryList.add(new Country("TTO", "TT", "Trinidad and Tobago"));
        countryList.add(new Country("TUN", "TN", "Tunisia"));
        countryList.add(new Country("TUR", "TR", "Turkey"));
        countryList.add(new Country("TKM", "TM", "Turkmenistan"));
        countryList.add(new Country("TCA", "TC", "Turks And Caicos Islands"));
        countryList.add(new Country("TUV", "TV", "Tuvalu"));
        countryList.add(new Country("VIR", "VI", "U.S. Virgin Islands"));
        countryList.add(new Country("UGA", "UG", "Uganda"));
        countryList.add(new Country("UKR", "UA", "Ukraine"));
        countryList.add(new Country("ARE", "AE", "United Arab Emirates"));
        countryList.add(new Country("GBR", "GB", "United Kingdom"));
        countryList.add(new Country("USA", "US", "United States"));
        countryList.add(new Country("UMI", "UM", "United States Minor Outlying Islands"));
        countryList.add(new Country("URY", "UY", "Uruguay"));
        countryList.add(new Country("UZB", "UZ", "Uzbekistan"));
        countryList.add(new Country("VUT", "VU", "Vanuatu"));
        countryList.add(new Country("VAT", "VA", "Vatican"));
        countryList.add(new Country("VEN", "VE", "Venezuela"));
        countryList.add(new Country("VNM", "VN", "Vietnam"));
        countryList.add(new Country("WLF", "WF", "Wallis And Futuna"));
        countryList.add(new Country("ESH", "EH", "Western Sahara"));
        countryList.add(new Country("YEM", "YE", "Yemen"));
        countryList.add(new Country("ZMB", "ZM", "Zambia"));
        countryList.add(new Country("ZWE", "ZW", "Zimbabwe"));
        countryList.add(new Country("ALA", "AX", "Åland Islands"));

        return countryList;

    }

}


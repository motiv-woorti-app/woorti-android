package inesc_id.pt.motivandroid.utils;

import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import java.lang.reflect.Field;
import java.util.Locale;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

/**
 *
 * MiscUtils
 *
 *   Miscellaneous utility functions
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

public class MiscUtils {

    public MiscUtils(){}

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String getOSVersion(){

        /*Field[] fields = Build.VERSION_CODES.class.getFields();


        return fields[Build.VERSION.SDK_INT + 1].getName();*/

        return Build.VERSION.SDK_INT+"";

    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean checkPasswordValiditySignUp(String password) {

        if((password.length() < 6) || (password.length() > 16)){
            return false;
        }else{
            return true;
        }

    }

    public static boolean checkPasswordValidityLogIn(String password) {

        if((password.length() < 6)){
            return false;
        }else{
            return true;
        }

    }

    public  static SpannableStringBuilder emboldenKeywords(final String text,
                                                           final String[] searchKeywords) {
        // searching in the lower case text to make sure we catch all cases
        final String loweredMasterText = text.toLowerCase(Locale.ENGLISH);
        final SpannableStringBuilder span = new SpannableStringBuilder(text);

        // for each keyword
        for (final String keyword : searchKeywords) {
            // lower the keyword to catch both lower and upper case chars
            final String loweredKeyword = keyword.toLowerCase(Locale.ENGLISH);

            // start at the beginning of the master text
            int offset = 0;
            int start;
            final int len = keyword.length(); // let's calculate this outside the 'while'

            while ((start = loweredMasterText.indexOf(loweredKeyword, offset)) >= 0) {
                // make it bold
                span.setSpan(new StyleSpan(Typeface.BOLD), start, start+len, SPAN_INCLUSIVE_INCLUSIVE);
                // move your offset pointer
                offset = start + len;
            }
        }

        // put it in your TextView and smoke it!
        return span;
    }

    public static SpannableStringBuilder styleKeywords(final String text,
                                                           final String[] searchKeywords, Object styleSpan) {
        // searching in the lower case text to make sure we catch all cases
        final String loweredMasterText = text.toLowerCase(Locale.ENGLISH);
        final SpannableStringBuilder span = new SpannableStringBuilder(text);

        // for each keyword
        for (final String keyword : searchKeywords) {
            // lower the keyword to catch both lower and upper case chars
            final String loweredKeyword = keyword.toLowerCase(Locale.ENGLISH);

            // start at the beginning of the master text
            int offset = 0;
            int start;
            final int len = keyword.length(); // let's calculate this outside the 'while'

            while ((start = loweredMasterText.indexOf(loweredKeyword, offset)) >= 0) {
                // make it bold
                span.setSpan(styleSpan, start, start+len, SPAN_INCLUSIVE_INCLUSIVE);
                // move your offset pointer
                offset = start + len;
            }
        }

        // put it in your TextView and smoke it!
        return span;
    }

}

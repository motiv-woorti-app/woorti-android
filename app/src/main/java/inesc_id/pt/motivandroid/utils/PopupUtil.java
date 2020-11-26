package inesc_id.pt.motivandroid.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;

import inesc_id.pt.motivandroid.R;

/**
 *
 * PopupUtil
 *
 *  Utility functions for showing popups
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
public class PopupUtil {

    //Generic method to draw a bubble popup above the provided view viewToDrawPopupAbove
    public static void showBubblePopup(Context context, View viewToDrawPopupAbove, String text){

        BubbleLayout bubbleLayout = (BubbleLayout) LayoutInflater.from(context).inflate(R.layout.bubble_layout_textview, null);
        PopupWindow popupWindow = BubblePopupHelper.create(context, bubbleLayout);

        TextView bubbleTextView = bubbleLayout.findViewById(R.id.bubbleTextView);
        bubbleTextView.setText(text);

        int[] location = new int[2];
        viewToDrawPopupAbove.getLocationInWindow(location);

        bubbleLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

//                Log.e("popup", "x "+ bubbleLayout.getHeight() + " y " + bubbleLayout.getWidth());
        Log.e("popup", "x "+ bubbleLayout.getMeasuredHeight() + " y " + bubbleLayout.getMeasuredWidth());

        viewToDrawPopupAbove.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);


        popupWindow.showAtLocation(viewToDrawPopupAbove, Gravity.NO_GRAVITY, location[0] + (viewToDrawPopupAbove.getMeasuredWidth()/2) - (bubbleLayout.getMeasuredWidth()/2), location[1] - bubbleLayout.getMeasuredHeight());
//                int[] location = new int[2];

    }


    //Generic method to draw Other Option string with title=Other and TextHint=$otherText
    public static void showOtherOptionDialog(final Context context, final CallbackInterface listener, String otherText) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_add_activity_new_mytrips_other_option, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        final EditText otherOptionEditText = mView.findViewById(R.id.otherOptionEditText);
        otherOptionEditText.setHint(otherText);

        Button saveOtherOptionButton = mView.findViewById(R.id.saveOtherButton);

        Button backOtherOptionButton = mView.findViewById(R.id.backButton);

        backOtherOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveOtherOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String option = otherOptionEditText.getText().toString();

                if (option.length() >= 3){
                    listener.saveOtherOption(option);
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, "Must be at least 3 letters long", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public interface CallbackInterface {
        void saveOtherOption(String option);
    }


}

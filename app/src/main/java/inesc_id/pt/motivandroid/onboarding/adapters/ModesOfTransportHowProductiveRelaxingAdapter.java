package inesc_id.pt.motivandroid.onboarding.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.WrappedDrawable;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding6_7Fragment;
import inesc_id.pt.motivandroid.onboarding.wrappers.ModeOfTransportUsed;
import inesc_id.pt.motivandroid.utils.DensityUtil;
import inesc_id.pt.motivandroid.utils.MotivMapUtils;

/**
 *  ModesOfTransportHowProductiveRelaxingAdapter
 *
 *   This adapter draws a list of ModeOfTransportUsed. Each listitem has a switch for the user to
 *   select (multiple selectable)
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
public class ModesOfTransportHowProductiveRelaxingAdapter extends ArrayAdapter<ModeOfTransportUsed>{

    private ArrayList<ModeOfTransportUsed> dataSet = new ArrayList<>();
    private Context mContext;
    private int mode;

    private static class ViewHolder {
        SeekBar seekBar;
        TextView rating;
    }

    public ModesOfTransportHowProductiveRelaxingAdapter(ArrayList<ModeOfTransportUsed> data, Context context, int mode) {
        super(context, R.layout.modes_used_regularly_listitem, data);
        //this.dataSet.addAll(data);

        this.mContext = context;
        this.mode = mode;
        this.dataSet = data;

    }

    @Override
    public int getViewTypeCount() {
        return dataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public Drawable scaleImage (Drawable image, float scaleFactor) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

        image = new BitmapDrawable(getContext().getResources(), bitmapResized);

        return image;

    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final ModeOfTransportUsed dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.how_productive_relaxing_seekbar_listitem, parent, false);

            viewHolder.seekBar = (SeekBar) convertView.findViewById(R.id.seekBar);
            viewHolder.rating = (TextView) convertView.findViewById(R.id.ratingTextView);


            Drawable drawable = mContext.getResources().getDrawable(ActivityDetected.getTransportIconButtonFromInt(dataModel.getModalityIntCode()));

            WrappedDrawable wrappedDrawable = new WrappedDrawable(drawable);
            wrappedDrawable.setBounds(0,0, drawable.getIntrinsicWidth()/3,drawable.getIntrinsicHeight()/3);

            viewHolder.seekBar.setThumb(wrappedDrawable);
            viewHolder.seekBar.getProgressDrawable().setColorFilter(getContext().getResources().getColor(R.color.colorOrangeTripPolyline), PorterDuff.Mode.MULTIPLY);

            viewHolder.seekBar.setMax(10);

            //progress divided by 10 because dataModel has the value in percentage
            if(mode == Onboarding6_7Fragment.keys.PRODUCTIVE)
                viewHolder.seekBar.setProgress(dataModel.getProductiveRating()/10);
            else if( mode == Onboarding6_7Fragment.keys.ENJOYMENT)
                viewHolder.seekBar.setProgress(dataModel.getEnjoymentRating()/10);
            else
                viewHolder.seekBar.setProgress(dataModel.getFitnessRating()/10);

            viewHolder.rating.setText((viewHolder.seekBar.getProgress() * 10) + "%");
            viewHolder.rating.setVisibility(View.VISIBLE);

            viewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChanged = 5;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChanged = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    progressChanged = seekBar.getProgress();

                    viewHolder.rating.setText(progressChanged * 10 + "%");
                    viewHolder.rating.setVisibility(View.VISIBLE);

                    if(mode == Onboarding6_7Fragment.keys.PRODUCTIVE)
                        dataModel.setProductiveRating(progressChanged * 10);
                    else if(mode == Onboarding6_7Fragment.keys.ENJOYMENT)
                        dataModel.setEnjoymentRating(progressChanged * 10);
                    else
                        dataModel.setFitnessRating(progressChanged * 10);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }

    public ArrayList<ModeOfTransportUsed> getModes() {
        return dataSet;
    }

}
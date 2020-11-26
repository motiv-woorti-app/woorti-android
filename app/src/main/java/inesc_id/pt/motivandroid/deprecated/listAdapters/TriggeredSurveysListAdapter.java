package inesc_id.pt.motivandroid.deprecated.listAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import inesc_id.pt.motivandroid.data.surveys.SurveyStateful;

/**
 * Created by INESC-ID on 01/04/2018.
 */

@Deprecated
public class TriggeredSurveysListAdapter extends ArrayAdapter<SurveyStateful> {


        private Context mContext;
        private List<SurveyStateful> moviesList = new ArrayList<>();

        public TriggeredSurveysListAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<SurveyStateful> list) {
            super(context, 0 , list);
            mContext = context;
            moviesList = list;

            ReverseTripTimeOrder customComparator = new ReverseTripTimeOrder();

            Collections.sort(moviesList, customComparator);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if(listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1,parent,false);

                SurveyStateful currentMovie = moviesList.get(position);

                if(currentMovie.isHasBeenAnswered()) {
                    listItem.setBackgroundColor(Color.GREEN);
                }else{
                    listItem.setBackgroundColor(Color.RED);
                }

//            ImageView image = (ImageView)listItem.findViewById(R.id.imageView_poster);
//            image.setImageResource(currentMovie.getmImageDrawable());

                TextView name = (TextView) listItem.findViewById(android.R.id.text1);
                name.setText(currentMovie.toString());
//
//            TextView release = (TextView) listItem.findViewById(R.id.textView_release);
//            release.setText(currentMovie.getmRelease());

            return listItem;
        }

    public class ReverseTripTimeOrder implements Comparator<SurveyStateful> {// may be it would be Model
        @Override
        public int compare(SurveyStateful obj1, SurveyStateful obj2) {
            return Long.compare(obj2.getTriggerTimestamp(),obj1.getTriggerTimestamp());
        }
    }

    }



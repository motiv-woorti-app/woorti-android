package inesc_id.pt.motivandroid.testingCarousel;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetectedWrapper;

public class TestCarouselActivity extends AppCompatActivity implements DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder> {

    DiscreteScrollView scrollView;

    CorrectModalityMergeAdapter adapter;
    ArrayList<ActivityDetectedWrapper> activityDetectedWrappers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_carousel);

        scrollView = findViewById(R.id.picker);

         activityDetectedWrappers = new ArrayList<>();

    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, final int adapterPosition) {

        Log.e("carousel", "pos " + adapterPosition);

        adapter.setSelectedItem(adapterPosition);



    }
}

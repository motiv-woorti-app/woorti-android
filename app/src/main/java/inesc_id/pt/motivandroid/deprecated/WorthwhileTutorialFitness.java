package inesc_id.pt.motivandroid.deprecated;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import inesc_id.pt.motivandroid.R;

@Deprecated
public class WorthwhileTutorialFitness extends ConstraintLayout {

    public WorthwhileTutorialFitness(Context context) {
        super(context);
        init(context);
    }

    public WorthwhileTutorialFitness(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WorthwhileTutorialFitness(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.what_is_worthwhileness_box_5_body, this, true);

        ImageView figure = v.findViewById(R.id.imageView16);

        Glide.with(context).load(R.drawable.onboarding_illustrations_fitness)
                .into(figure);

    }


}

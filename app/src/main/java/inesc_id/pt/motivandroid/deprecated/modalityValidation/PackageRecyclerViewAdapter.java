package inesc_id.pt.motivandroid.deprecated.modalityValidation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;

@Deprecated
//see http://www.zoftino.com/android-recyclerview-radiogroup
public class PackageRecyclerViewAdapter extends
        RecyclerView.Adapter<PackageRecyclerViewAdapter.ViewHolder> {

    private List<LegModel> legList;
    private Context context;

    //private RadioGroup lastCheckedRadioGroup = null;

    public PackageRecyclerViewAdapter(List<LegModel> packageListIn
            , Context ctx) {
        legList = packageListIn;
        context = ctx;
    }

    @Override
    public PackageRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leg_item, parent, false);

        PackageRecyclerViewAdapter.ViewHolder viewHolder =
                new PackageRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position){

        return position;

    }

    @Override
    public void onBindViewHolder(PackageRecyclerViewAdapter.ViewHolder holder,
                                 int position) {

        holder.setIsRecyclable(false);

        LegModel packageModel = legList.get(position);
        holder.legNumber.setText("Leg #"+position);
        holder.wrongLegSwitch.setChecked(!packageModel.getLeg().isWrongLeg());

        String realMod = ActivityDetected.keys.modalities[packageModel.getLeg().getModality()];

        Log.e("mod pos", position + "");
        Log.e("mod", "leg mod " + packageModel.getLeg().getModality());
        Log.e("mod", ActivityDetected.keys.modalities[packageModel.getLeg().getModality()]);
        Log.e("mod", ActivityDetected.getSurveyModalityValue(realMod) + "");

        int id = (position+1)*100;
        int positionInRadioGroup = 0;
        for(String modality : ActivityDetected.keys.surveyModalities){
            RadioButton rb = new RadioButton(PackageRecyclerViewAdapter.this.context);
            rb.setId(id++);
            rb.setText(modality);

            if(packageModel.getLeg().getCorrectedModeOfTransport() ==  -1) {
                if (positionInRadioGroup == ActivityDetected.getSurveyModalityValue(realMod))
                    rb.setChecked(true);
            }else{
                if (positionInRadioGroup == ActivityDetected.getSurveyModalityValue(ActivityDetected.keys.modalities[packageModel.getLeg().getCorrectedModeOfTransport()])) {
                    rb.setChecked(true);
                    //packageModel.getLeg().
                }
            }

            holder.priceGroup.addView(rb);
            positionInRadioGroup++;
        }
    }

    public List<LegModel> getLegList(){
        return legList;
    }

    @Override
    public int getItemCount() {
        return legList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView legNumber;
        public RadioGroup priceGroup;
        public Switch wrongLegSwitch;

        public ViewHolder(View view) {
            super(view);


            legNumber = (TextView) view.findViewById(R.id.leg_number);
            priceGroup = (RadioGroup) view.findViewById(R.id.modality_grp);
            wrongLegSwitch = (Switch) view.findViewById(R.id.isLegSwitch);

            priceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {

                    Log.e("adalter i", i+"");
                    Log.e("adapter", getAdapterPosition()+"");
                    Log.e("adapter pos", (i-((getAdapterPosition()+1)*100)) + "");

                    String surveyModality = ActivityDetected.keys.surveyModalities[i-((getAdapterPosition()+1)*100)];
                    int realModalityIndex = ActivityDetected.getRealModalityValue(surveyModality);

                    legList.get(getAdapterPosition()).setSelectedIndex(realModalityIndex);
                    legList.get(getAdapterPosition()).getLeg().setCorrectedModeOfTransport(realModalityIndex);

                    //since only one package is allowed to be selected
                    //this logic clears previous selection
                    //it checks state of last radiogroup and
                    // clears it if it meets conditions
                    /*if (lastCheckedRadioGroup != null
                            && lastCheckedRadioGroup.getCheckedRadioButtonId()
                            != radioGroup.getCheckedRadioButtonId()
                            && lastCheckedRadioGroup.getCheckedRadioButtonId() != -1) {
                        lastCheckedRadioGroup.clearCheck();

                        Toast.makeText(PackageRecyclerViewAdapter.this.context,
                                "Radio button clicked " + radioGroup.getCheckedRadioButtonId(),
                                Toast.LENGTH_SHORT).show();

                    }
                    lastCheckedRadioGroup = radioGroup;*/

                }
            });

            wrongLegSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    legList.get(getAdapterPosition()).getLeg().setWrongLeg(!isChecked);
                }

            });
        }
    }
}

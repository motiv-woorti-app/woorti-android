package inesc_id.pt.motivandroid.onboarding.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.onboarding.activities.OnboardingActivity;
import inesc_id.pt.motivandroid.utils.PopupUtil;

/**
 * Onboarding4Fragment
 *
 *  Asks the user to answer the question "How important are the travel time worthwhileness elements
 *  for you, when you travel?". This is done with 3 seekbars for each of the terms (productivity,
 *  fitness and enjoyment). There is also one button that opens a popup explaining each of the terms.
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
public class Onboarding4Fragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private final static int ONBOARDING_MODE = 0;
    private final static int SETTINGS_MODE = 1;

    SeekBar seekBarProductivity;
    SeekBar seekBarActivity;
    SeekBar seekBarRelaxing;

    Button nextButton;

    private OnFragmentInteractionListener mListener;

    public Onboarding4Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Onboarding4Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Onboarding4Fragment newInstance() {
        Onboarding4Fragment fragment = new Onboarding4Fragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_what_is_worthwile_trip_onboarding_4, container, false);

        seekBarProductivity = view.findViewById(R.id.seekBarProductivity);
        seekBarRelaxing = view.findViewById(R.id.seekBarRelaxing);
        seekBarActivity = view.findViewById(R.id.seekBarActivity);

        final TextView productivityValueTextView = view.findViewById(R.id.productivityValueTextView);
        final TextView mindValueTextView = view.findViewById(R.id.mindValueTextView);
        final TextView bodyValueTextView = view.findViewById(R.id.bodyValueTextView);

        ImageView prodInfoImageView = view.findViewById(R.id.infoProductivityImageView);
        ImageView mindInfoImageView = view.findViewById(R.id.infoRelaxingImageView);
        ImageView bodyInfoImageView = view.findViewById(R.id.infoActivityImageView);

        prodInfoImageView.setOnClickListener(this);
        mindInfoImageView.setOnClickListener(this);
        bodyInfoImageView.setOnClickListener(this);

        productivityValueTextView.setText(getContext().getString(R.string.Productivity_Percentage,
                ""+ seekBarProductivity.getProgress()));

        mindValueTextView.setText(getContext().getString(R.string.Mind_Percentage,
                ""+ seekBarRelaxing.getProgress()));

        bodyValueTextView.setText(getContext().getString(R.string.Body_Percentage,
                ""+ seekBarActivity.getProgress()));






        seekBarProductivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                int progressChanged = minRange;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("seekbar changed", "prod " + progress);

                productivityValueTextView.setText(getContext().
                        getString(R.string.Productivity_Percentage, ""+ progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarRelaxing.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                int progressChanged = minRange;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("seekbar changed", "prod " + progress);

                mindValueTextView.setText(getContext().getString(R.string.Mind_Percentage,
                        ""+ progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarActivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                int progressChanged = minRange;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("seekbar changed", "prod " + progress);

                bodyValueTextView.setText(getContext().getString(R.string.Body_Percentage,
                        ""+ progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });






        Button backButton = view.findViewById(R.id.backButton);

        nextButton = view.findViewById(R.id.saveButton);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView prod = view.findViewById(R.id.productivityValueTextView);

        prod.setOnClickListener(this);


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.saveButton:
                //todo
                ((OnboardingActivity) getActivity()).saveProductivityRelaxingActivity(seekBarProductivity.getProgress(), seekBarRelaxing.getProgress(), seekBarActivity.getProgress());

                Onboarding5Fragment mfragment = Onboarding5Fragment.newInstance(ONBOARDING_MODE);
                //using Bundle to send data
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.onboarding_main_fragment, mfragment).addToBackStack(null);
                transaction.commit();


                break;
            case R.id.backButton:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            case R.id.infoProductivityImageView:
                PopupUtil.showBubblePopup(getContext(), v, getString(R.string.Productivity_Worthwhile_Description_Score));
                break;

            case R.id.infoRelaxingImageView:
                PopupUtil.showBubblePopup(getContext(), v, getString(R.string.Enjoyment_Worthwhile_Description_Score));
                break;

            case R.id.infoActivityImageView:
                PopupUtil.showBubblePopup(getContext(), v, getString(R.string.Fitness_Worthwhile_Description_Score));
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

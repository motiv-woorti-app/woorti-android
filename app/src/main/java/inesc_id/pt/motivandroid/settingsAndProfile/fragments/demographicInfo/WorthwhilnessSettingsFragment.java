package inesc_id.pt.motivandroid.settingsAndProfile.fragments.demographicInfo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.utils.PopupUtil;

/**
 * WorthwhilnessSettingsFragment
 *
 *  Sub menu for worthwhileness settings. Allows the user to fill in how important are the following
 *  travel time worthwhileness elements, when he travels:
 *  -Productivity
 *  -Enjoyment
 *  -Fitness
 *
 *  The user sets its preference using a seekbar for each element. There's is also a button next to
 *  each element that when pressed shows a popup describing the element
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
public class WorthwhilnessSettingsFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    SeekBar seekBarProductivity;
    SeekBar seekBarActivity;
    SeekBar seekBarRelaxing;

    Button saveButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public WorthwhilnessSettingsFragment() {
        // Required empty public constructor
    }

    UserSettingStateWrapper userSettingStateWrapper;
    boolean validOnboarding = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorthwhilnessSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorthwhilnessSettingsFragment newInstance(String param1, String param2) {
        WorthwhilnessSettingsFragment fragment = new WorthwhilnessSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "notExistent");

        if ((userSettingStateWrapper != null) && (userSettingStateWrapper.getUid().equals(FirebaseAuth.getInstance().getUid()))) {
            Log.e("ProfileAndSettings", "valid");

            validOnboarding = true;

        }else{
            Log.e("ProfileAndSettings", "invalid");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_worthwhilness_settings, container, false);

        final TextView productivityValueTextView = view.findViewById(R.id.productivityValueTextView);
        final TextView mindValueTextView = view.findViewById(R.id.mindValueTextView);
        final TextView bodyValueTextView = view.findViewById(R.id.bodyValueTextView);

        ImageView prodInfoImageView = view.findViewById(R.id.infoProductivityImageView);
        ImageView mindInfoImageView = view.findViewById(R.id.infoRelaxingImageView);
        ImageView bodyInfoImageView = view.findViewById(R.id.infoActivityImageView);

        prodInfoImageView.setOnClickListener(this);
        mindInfoImageView.setOnClickListener(this);
        bodyInfoImageView.setOnClickListener(this);

        seekBarProductivity = view.findViewById(R.id.seekBarProductivity);
        seekBarRelaxing = view.findViewById(R.id.seekBarRelaxing);
        seekBarActivity = view.findViewById(R.id.seekBarActivity);

        if (validOnboarding){

            //if exists, get previously chosen productivity value and update seekbar and textview
            if(userSettingStateWrapper.getUserSettings().getProductivityValue() != -1) {
                seekBarProductivity.setProgress(userSettingStateWrapper.getUserSettings().getProductivityValue());

                productivityValueTextView.setText(getContext().getString(R.string.Productivity_Percentage,
                        ""+ userSettingStateWrapper.getUserSettings().getProductivityValue()));
            }

            //if exists, get previously chosen enjoyment value and update seekbar and textview
            if(userSettingStateWrapper.getUserSettings().getRelaxingValue() != -1) {
                seekBarRelaxing.setProgress(userSettingStateWrapper.getUserSettings().getRelaxingValue());

                mindValueTextView.setText(getContext().getString(R.string.Mind_Percentage, "" + userSettingStateWrapper.getUserSettings().getRelaxingValue()));
            }

            //if exists, get previously chosen fitness value and update seekbar and textview
            if(userSettingStateWrapper.getUserSettings().getActivityValue() != -1) {
                seekBarActivity.setProgress(userSettingStateWrapper.getUserSettings().getActivityValue());

                bodyValueTextView.setText(getContext().getString(R.string.Body_Percentage,
                        ""+userSettingStateWrapper.getUserSettings().getActivityValue()));
            }

            seekBarProductivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.d("seekbar changed", "prod " + progress);

                    //  productivity seekbar thumb moved->update productivity textview progress
                    // indicator
                    productivityValueTextView.setText(getContext().getString(R.string.Productivity_Percentage,
                            ""+ progress));

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            seekBarRelaxing.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.d("seekbar changed", "prod " + progress);
                    //  enjoyment seekbar thumb moved->update enjoyment textview progress
                    // indicator
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

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.e("seekbar changed", "prod " + progress);
                    //  fitness seekbar thumb moved->update fitness textview progress
                    // indicator
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

        }

        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        return view;
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

        switch(v.getId()) {
            case R.id.saveButton:

                if (validOnboarding) {
                    userSettingStateWrapper.getUserSettings().setActivityValue(seekBarActivity.getProgress());
                    userSettingStateWrapper.getUserSettings().setRelaxingValue(seekBarRelaxing.getProgress());
                    userSettingStateWrapper.getUserSettings().setProductivityValue(seekBarProductivity.getProgress());

                    //save worthwhileness setting persistently
                    SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);
                }

                Log.d("savebutton", "savebutton");
                getActivity().onBackPressed();

                break;
            case R.id.infoProductivityImageView:
                //show popup with description of productivity
                PopupUtil.showBubblePopup(getContext(), v, getString(R.string.Productivity_Worthwhile_Description_Score));
                break;

            case R.id.infoRelaxingImageView:
                //show popup with description of enjoyment
                PopupUtil.showBubblePopup(getContext(), v, getString(R.string.Enjoyment_Worthwhile_Description_Score));
                break;

            case R.id.infoActivityImageView:
                //show popup with description of fitness
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

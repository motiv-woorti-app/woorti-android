package inesc_id.pt.motivandroid.settingsAndProfile.fragments.setHomeAndWork;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.userSettingsData.HomeWorkAddress;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.settingsAndProfile.ProfileAndSettingsActivity;

/**
 * SetHomeOrWorkFragment
 *
 *  Sub menu for setting Home or Work adress information. This fragment has both home and work modes
 *  depending on mParam1 passed to the fragment.
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

public class SetHomeOrWorkFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1;

    int mode;

    private OnFragmentInteractionListener mListener;

    public SetHomeOrWorkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1
     * @return A new instance of fragment SetHomeOrWorkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetHomeOrWorkFragment newInstance(int param1) {
        SetHomeOrWorkFragment fragment = new SetHomeOrWorkFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    UserSettingStateWrapper userSettingStateWrapper;
    EditText typeAddressEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);

        }

        //get user settings
        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "");

    }

    Button doneButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_set_home_or_work, container, false);

        TextView subtitle = view.findViewById(R.id.homeOrWorkSubtitleTextView);
        typeAddressEditText = view.findViewById(R.id.typeAddressEditText);

        doneButton = view.findViewById(R.id.doneFinalButton);
        doneButton.setOnClickListener(this);

        //check mParam1 and set to home address mode or work address mode
        switch(mParam1){
            case R.string.Home:
                ((ProfileAndSettingsActivity)getActivity()).setTitleTextView(getString(R.string.Set_Home));
                subtitle.setText(getString(R.string.Define_Home_Location));

                //fill in home address if the user has previously chosen its home address
                if(userSettingStateWrapper.getUserSettings().getHomeAddress() != null){
                    typeAddressEditText.setText(userSettingStateWrapper.getUserSettings().getHomeAddress().getAddress());
                }

                break;

            case R.string.Work:
                ((ProfileAndSettingsActivity)getActivity()).setTitleTextView(getString(R.string.Set_Work));
                subtitle.setText(getString(R.string.Define_Work_Location));

                //fill in work address if the user has previously chosen its work address
                if(userSettingStateWrapper.getUserSettings().getWorkAddress() != null){
                    typeAddressEditText.setText(userSettingStateWrapper.getUserSettings().getWorkAddress().getAddress());
                }

                break;
        }


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

        switch (v.getId()){

            case R.id.doneFinalButton:

                if (typeAddressEditText.getText().length() < 3){
                    Toast.makeText(getContext(), "Address is too short", Toast.LENGTH_SHORT).show();
                }else{

                    switch(mParam1){
                        case R.string.Home:
                            userSettingStateWrapper.getUserSettings().setHomeAddress(new HomeWorkAddress(new inesc_id.pt.motivandroid.data.tripData.LatLng(0.0,0.0), typeAddressEditText.getText().toString()));
                            break;

                        case R.string.Work:
                            userSettingStateWrapper.getUserSettings().setWorkAddress(new HomeWorkAddress(new inesc_id.pt.motivandroid.data.tripData.LatLng(0.0,0.0), typeAddressEditText.getText().toString()));
                            break;
                    }

                    //save home or work address persistently
                    SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);
                    if(getActivity() != null) getActivity().finish();
                }
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

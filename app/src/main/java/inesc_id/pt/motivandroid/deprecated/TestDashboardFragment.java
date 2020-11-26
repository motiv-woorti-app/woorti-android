package inesc_id.pt.motivandroid.deprecated;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.managers.CampaignManager;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TestDashboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TestDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

@Deprecated
public class TestDashboardFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TestDashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestDashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestDashboardFragment newInstance(String param1, String param2) {
        TestDashboardFragment fragment = new TestDashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    UserSettingStateWrapper userSettingStateWrapper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_test_dashboard, container, false);

        Button button = view.findViewById(R.id.buttonYou);

        button.setOnClickListener(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    PopupWindow popupWindow;

    public void showUserCommunityPopup(View viewToDrawPopupAbove){

        LinearLayout outterOptionsLinearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dashboard_outter_options_linear_layout, null, false);

        String country = userSettingStateWrapper.getUserSettings().getCountry();

        String city = userSettingStateWrapper.getUserSettings().getCity();

        ArrayList<Campaign> campaigns = CampaignManager.getInstance(getContext()).getCampaigns();

        drawYouTopOptionSection(outterOptionsLinearLayout);
        drawCityRegionMidOptionSection(outterOptionsLinearLayout, city);
        drawCountryMidOptionSection(outterOptionsLinearLayout, country);
        drawCampaignsMidOptionSection(outterOptionsLinearLayout, campaigns);
        drawGlobalBottomOptionSection(outterOptionsLinearLayout);

        viewToDrawPopupAbove.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        popupWindow = new PopupWindow(outterOptionsLinearLayout, viewToDrawPopupAbove.getMeasuredWidth(), height);

        int[] location = new int[2];
        viewToDrawPopupAbove.getLocationInWindow(location);

        outterOptionsLinearLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(viewToDrawPopupAbove, Gravity.NO_GRAVITY, location[0], location[1] + viewToDrawPopupAbove.getMeasuredHeight());
    }

    public void drawYouTopOptionSection(LinearLayout outterOptionsLinearLayout){

        ConstraintLayout topModuleLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.top_dashboard_you_community_option_layout, null, false);
        outterOptionsLinearLayout.addView(topModuleLayout);

        //lacks onclick
    }

    public void drawCityRegionMidOptionSection(LinearLayout outterOptionsLinearLayout, String city){

        //city region

        ConstraintLayout cityRegionModuleLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.mid_dashboard_you_community_option_layout, null, false);
        TextView cityRegionSectionTitleTextView = cityRegionModuleLayout.findViewById(R.id.sectionTitleTextView);
        cityRegionSectionTitleTextView.setText("City/Region");

        LinearLayout cityRegionLL = cityRegionModuleLayout.findViewById(R.id.innerOptionsLinearLayout);

        ConstraintLayout cityRegionTextOptionLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dashboard_inner_option_text_layout, null, false);
        TextView innerOptionTextView = cityRegionTextOptionLayout.findViewById(R.id.optionTextView);
        innerOptionTextView.setText(city);
        cityRegionLL.addView(cityRegionTextOptionLayout);

        outterOptionsLinearLayout.addView(cityRegionModuleLayout);

        //end city region

        //lacks onclick
    }

    public void drawCountryMidOptionSection(LinearLayout outterOptionsLinearLayout, String country){

        //init country

        ConstraintLayout countryModuleLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.mid_dashboard_you_community_option_layout, null, false);
        TextView countrySectionTitleTextView = countryModuleLayout.findViewById(R.id.sectionTitleTextView);
        countrySectionTitleTextView.setText(country);

        LinearLayout countryLL = countryModuleLayout.findViewById(R.id.innerOptionsLinearLayout);

        ConstraintLayout countryTextOptionLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dashboard_inner_option_text_layout, null, false);
        TextView countryInnerOptionTextView = countryTextOptionLayout.findViewById(R.id.optionTextView);
        countryInnerOptionTextView.setText("Portugal");

        countryLL.addView(countryTextOptionLayout);
        outterOptionsLinearLayout.addView(countryModuleLayout);

        //end country

        //lacks onclick
    }

    public void drawCampaignsMidOptionSection(LinearLayout outterOptionsLinearLayout, ArrayList<Campaign> campaignArrayList){

        //init campaigns

        ConstraintLayout campaignsModuleLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.mid_dashboard_you_community_option_layout, null, false);
        TextView campaignSectionTitleTextView = campaignsModuleLayout.findViewById(R.id.sectionTitleTextView);
        campaignSectionTitleTextView.setText("Campaigns");

        LinearLayout campaignsLL = campaignsModuleLayout.findViewById(R.id.innerOptionsLinearLayout);

        int i = 0;

        for(Campaign campaign : campaignArrayList){

            ConstraintLayout campaignTextOptionLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dashboard_inner_option_text_layout, null, false);
            TextView ptCampaignInnerOptionTextView = campaignTextOptionLayout.findViewById(R.id.optionTextView);
            ptCampaignInnerOptionTextView.setText(campaign.getName());

            campaignsLL.addView(campaignTextOptionLayout);

            i++;

            if(i>4){
                break;
            }

        }

        outterOptionsLinearLayout.addView(campaignsModuleLayout);

        //end campaigns

        //lacks onclick
    }

    public void drawGlobalBottomOptionSection(LinearLayout outterOptionsLinearLayout){

        //init global

        ConstraintLayout globalModuleLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.bottom_dashboard_you_community_option_layout, null, false);
        TextView globalSectionTitleTextView = globalModuleLayout.findViewById(R.id.sectionTitleTextView);
        globalSectionTitleTextView.setText("Global");

        LinearLayout globalLL = globalModuleLayout.findViewById(R.id.innerOptionsLinearLayout);

        ConstraintLayout globalTextOptionLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dashboard_inner_option_text_layout, null, false);
        TextView globalOptionInnerOptionTextView = globalTextOptionLayout.findViewById(R.id.optionTextView);
        globalOptionInnerOptionTextView.setText("All users");

        globalLL.addView(globalTextOptionLayout);

        outterOptionsLinearLayout.addView(globalModuleLayout);

        //end global

        //lacks onclick
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

            case R.id.buttonYou:

                showUserCommunityPopup(v);

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

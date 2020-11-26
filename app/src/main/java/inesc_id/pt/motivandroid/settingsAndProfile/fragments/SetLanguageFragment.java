package inesc_id.pt.motivandroid.settingsAndProfile.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.Locale;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.userSettingsData.Language;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.onboarding.activities.OnboardingActivity;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.settingsAndProfile.ProfileAndSettingsActivity;
import inesc_id.pt.motivandroid.settingsAndProfile.adapters.LanguageListAdapter;

/**
 * SetLanguageFragment
 *
 *  Sub menu for changing the app language. This fragment is used in both the onboarding and profile
 *  and setiings (check ARG_PARAM1)
 *
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
public class SetLanguageFragment extends Fragment implements AdapterCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int ONBOARDING = 0;
    private static final int SETTINGS = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int mode;

    private OnFragmentInteractionListener mListener;

    public SetLanguageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetLanguageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetLanguageFragment newInstance(String param1, String param2) {
        SetLanguageFragment fragment = new SetLanguageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static SetLanguageFragment newInstance(int mode) {
        SetLanguageFragment fragment = new SetLanguageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, mode);
        fragment.setArguments(args);
        return fragment;
    }

    UserSettingStateWrapper userSettingStateWrapper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getInt(ARG_PARAM1);
        }else
            mode = SETTINGS;      //Default mode

        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_set_language, container, false);

        ArrayList<String> languageOptions = new ArrayList<>();

        //get current language
        String currentLocale = getResources().getConfiguration().locale.getLanguage();

        Log.d("--Setlanguage", "currentLocaleFromGetConfiguration " + currentLocale);

        int defaultOp = 0;

        //check current language against the list
        int i = 0;
        for (Language lang : Language.getAllLanguages()){
            languageOptions.add(lang.getName());

            if(currentLocale.equals(new Locale(lang.getSmartphoneID()).getLanguage())){
                defaultOp = i;
            }
            i++;
        }

        //list available languages, and pass the currently used language
        LanguageListAdapter adapter = new LanguageListAdapter(getContext(), languageOptions, this, defaultOp);

        ExpandableHeightListView optionsListView = view.findViewById(R.id._dynamic);

        optionsListView.setAdapter(adapter);
        optionsListView.setExpanded(true);
        optionsListView.setDivider(null);

        if(getActivity() instanceof  ProfileAndSettingsActivity) {
            ((ProfileAndSettingsActivity) getActivity()).setTitleTextView(getString(R.string.App_Language));
        }

        return view;

    }

    /**
     * change app language to language
     *
     * @param language
     */
    private void changeLanguage(Language language) {
        try {
            Log.e("before", getResources().getConfiguration().locale.getLanguage());

            Locale locale = new Locale(language.getSmartphoneID());
            Locale.setDefault(locale);
            Resources res = getActivity().getApplicationContext().getResources();
            Configuration config = new Configuration(res.getConfiguration());
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());

            //workaround to force app language int android version equal or greater to 26
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Resources activityRes = getResources();
                Configuration activityConf = activityRes.getConfiguration();
                activityConf.setLocale(locale);
                activityRes.updateConfiguration(activityConf, activityRes.getDisplayMetrics());
            }

            //profile and settings mode - save persistently
            if(getActivity() instanceof ProfileAndSettingsActivity) {
                userSettingStateWrapper.getUserSettings().setLang(language.getWoortiID());
                SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);
            }

            //onboarding mode - ask activity to save register language
            else if(getActivity() instanceof OnboardingActivity){
                ((OnboardingActivity) getActivity()).saveLanguage(language.getWoortiID());
            }


            Log.e("after", getResources().getConfiguration().locale.getLanguage());
        }catch (Exception e) {
            Log.e("SetLanguage", e.getMessage());
        }
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


    /**
     * lanaguage list adapter callback. called when a language is pressed
     *
     * @param positionWrapper
     */
    @Override
    public void clickedItem(int positionWrapper) {

        changeLanguage(Language.getAllLanguages().get(positionWrapper));

        showChangeLanguageWarningPopup();

    }

    /**
     * show warning popup (change language requires restart to be completed)
     */
    private void showChangeLanguageWarningPopup() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.language_changed_popup_layout, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button closeButton = mView.findViewById(R.id.closePopUpButton);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //call method on fragment to show add activities dialog
            }
        });

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

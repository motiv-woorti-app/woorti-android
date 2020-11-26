package inesc_id.pt.motivandroid.onboarding.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.SetLanguageFragment;

/**
 * Onboarding1Fragment
 *
 *  First fragment of the onboarding process. Allows the user to choose the app language
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
public class Onboarding1Fragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int ONBOARDING = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Button changeLangButton;



    public Onboarding1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Onboarding1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Onboarding1Fragment newInstance() {
        Onboarding1Fragment fragment = new Onboarding1Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.coordinated_onboarding_language, container, false);

//        Glide.with(this).load(R.drawable.onboarding_backgrounds_orange_bg_extended)
//                .into(new SimpleTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
//                        view.setBackground(resource);
//                    }
//                });


        Button nextButton = view.findViewById(R.id.nextOB1Button);
        nextButton.setOnClickListener(this);

        changeLangButton = view.findViewById(R.id.changeLanguageButton);
        changeLangButton.setOnClickListener(this);



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        String lang = getResources().getConfiguration().locale.getLanguage();
        changeLangButton.setText(lang);

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
        FragmentTransaction transaction;

        switch (v.getId()){
            case R.id.nextOB1Button:

                OnboardingFragment1_1 mfragment = OnboardingFragment1_1.newInstance("","");
                //using Bundle to send data
                transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.onboarding_main_fragment, mfragment).addToBackStack(null);
                transaction.commit();


                break;
            case R.id.changeLanguageButton:
                SetLanguageFragment fragment = SetLanguageFragment.newInstance(ONBOARDING);
                //using Bundle to send data
                transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.onboarding_main_fragment, fragment).addToBackStack(null);
                transaction.commit();
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

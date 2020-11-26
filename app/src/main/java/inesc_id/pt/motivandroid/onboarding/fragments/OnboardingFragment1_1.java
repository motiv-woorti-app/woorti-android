package inesc_id.pt.motivandroid.onboarding.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Locale;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.utils.MiscUtils;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

/**
 * OnboardingFragment1_1
 *
 *  Presents the user with "Help research for better transport" (info only and next button)
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
public class OnboardingFragment1_1 extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public OnboardingFragment1_1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnboardingFragment1_1.
     */
    // TODO: Rename and change types and number of parameters
    public static OnboardingFragment1_1 newInstance(String param1, String param2) {
        OnboardingFragment1_1 fragment = new OnboardingFragment1_1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    Button nextOB1_1Button;

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

        View view =inflater.inflate(R.layout.fragment_onboarding_fragment1_1, container, false);

//        Glide.with(this).load(R.drawable.onboarding_backgrounds_orange_bg_extended)
//                .into(new SimpleTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
//                        view.setBackground(resource);
//                    }
//                });

        nextOB1_1Button = view.findViewById(R.id.nextOB1_1Button);
        nextOB1_1Button.setOnClickListener(this);

        String privacyPolicyURL = getString(R.string.Url_Privacy_Policy);

        String[] array = {privacyPolicyURL};

        String ppTotalString = getString(R.string.Second_Onboarding_Screen_Help_Reseach_Check_Out_Site, privacyPolicyURL);

        TextView linkTextView = view.findViewById(R.id.linkTextView);
        linkTextView.setText(MiscUtils.styleKeywords(ppTotalString, array, new UnderlineSpan()));
        linkTextView.setOnClickListener(this);

        return view;
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nextOB1_1Button:

                Onboarding2Fragment mfragment = Onboarding2Fragment.newInstance();
                //using Bundle to send data
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.onboarding_main_fragment, mfragment).addToBackStack(null);
                transaction.commit();

                break;
            case R.id.linkTextView:

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://motivproject.eu/data-collection/data-protection.html"));
                startActivity(browserIntent);

                break;
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

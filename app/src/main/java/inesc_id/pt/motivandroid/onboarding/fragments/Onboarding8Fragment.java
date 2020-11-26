package inesc_id.pt.motivandroid.onboarding.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import inesc_id.pt.motivandroid.R;

/**
 * Onboarding8Fragment
 *
 * This fragment shows the user the privacy and data protection policies. The user has to accept
 * them to continue through the onboarding
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
public class Onboarding8Fragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private OnFragmentInteractionListener mListener;

    private Button next;

    public Onboarding8Fragment() {
        // Required empty public constructor
    }

    CheckBox checkBoxAccepts;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Onboarding8Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Onboarding8Fragment newInstance() {
        Onboarding8Fragment fragment = new Onboarding8Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_data_protection_onboarding_8, container, false);

        Spanned policy = Html.fromHtml(getString(R.string.motiv_policy));
        TextView termsOfUse = view.findViewById(R.id.textView44);
        termsOfUse.setText(policy);
        termsOfUse.setMovementMethod(LinkMovementMethod.getInstance());

        Button backButton = view.findViewById(R.id.backButton);
        next = view.findViewById(R.id.nextOB8Button);
        next.setOnClickListener(this);
        backButton.setOnClickListener(this);

        checkBoxAccepts = view.findViewById(R.id.checkBoxAcceptsGDPR);

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

            case R.id.nextOB8Button:

                if(checkBoxAccepts.isChecked()){

                    Onboarding9Fragment nextFragment = Onboarding9Fragment.newInstance();
                    //using Bundle to send data
                    FragmentTransaction nextTransaction=getFragmentManager().beginTransaction();
                    nextTransaction.replace(R.id.onboarding_main_fragment, nextFragment).addToBackStack(null);
                    nextTransaction.commit();

                }else{
                    Toast.makeText(getContext(), "You must accept Motiv data protection condition.", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.backButton:
                getActivity().getSupportFragmentManager().popBackStack();
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

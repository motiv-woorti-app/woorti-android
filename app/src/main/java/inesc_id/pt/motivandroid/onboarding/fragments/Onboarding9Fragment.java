package inesc_id.pt.motivandroid.onboarding.fragments;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import inesc_id.pt.motivandroid.R;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Onboarding9Fragment
 *
 *  Asks the users to accept the needed permissions (fine location and storage)
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
public class Onboarding9Fragment extends Fragment implements View.OnClickListener, EasyPermissions.PermissionCallbacks
{
    // TODO: Rename parameter arguments, choose names that match

    private OnFragmentInteractionListener mListener;

    private final int FINE_LOCATION_AND_READ_EXTERNAL_STORAGE = 100;

    public Onboarding9Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Onboarding9Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Onboarding9Fragment newInstance() {
        Onboarding9Fragment fragment = new Onboarding9Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_permissions_onboarding_9, container, false);

        Button backButton = view.findViewById(R.id.backButton);
        Button nextButton = view.findViewById(R.id.nextOB9Button);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

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
            case R.id.nextOB9Button:

                askForPermissions();

                break;
            case R.id.backButton:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    @AfterPermissionGranted(FINE_LOCATION_AND_READ_EXTERNAL_STORAGE)
    private void askForPermissions(){
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {

            Onboarding10Fragment nextFragment = Onboarding10Fragment.newInstance();
            //using Bundle to send data
            FragmentTransaction nextTransaction=getFragmentManager().beginTransaction();
            nextTransaction.replace(R.id.onboarding_main_fragment, nextFragment).addToBackStack(null);
            nextTransaction.commit();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getContext().getString(R.string.Onboarding_Dialog_Box_Ask_Location_Storage_Permissions),
                    FINE_LOCATION_AND_READ_EXTERNAL_STORAGE, perms);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        Toast.makeText(getActivity(), getContext().getString(R.string.Onboarding_Warning_Permissions_Denied_Retry), Toast.LENGTH_LONG).show();

        askForPermissions();

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

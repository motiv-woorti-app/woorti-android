package inesc_id.pt.motivandroid.onboarding.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.onboarding.activities.OnboardingActivity;
import inesc_id.pt.motivandroid.onboarding.adapters.CampaignListAdapter;
import inesc_id.pt.motivandroid.onboarding.wrappers.CampaignWrapper;
import inesc_id.pt.motivandroid.utils.LocationUtils;

/**
 * Onboarding13Fragment
 *
 *  Asks the user to choose which campaigns/studies he wants to be enrolled in.
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
public class Onboarding13Fragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private final static int ONBOARDING_MODE = 0;

    // TODO: Rename and change types of parameters
    private String country;
    private String city;

    private ArrayList<Campaign> campaigns;
    ExpandableHeightListView campaignListView;

    CampaignListAdapter adapter;

//    TextView howManyStudiesTextView;

    boolean retrievedCampaigns = false;

    private OnFragmentInteractionListener mListener;

    public Onboarding13Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Onboarding13Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Onboarding13Fragment newInstance(String param1, String param2, ArrayList<Campaign> campaigns) {
        Onboarding13Fragment fragment = new Onboarding13Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putParcelableArrayList(ARG_PARAM3, campaigns);
        fragment.setArguments(args);
        return fragment;
    }

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            country = getArguments().getString(ARG_PARAM1);
            city = getArguments().getString(ARG_PARAM2);
            campaigns = getArguments().getParcelableArrayList(ARG_PARAM3);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        //LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receivingCampaignData);
    }

    @Override
    public void onResume() {
        super.onResume();
        //LocalBroadcastManager.getInstance(getContext()).registerReceiver(
        //        receivingCampaignData, new IntentFilter(MotivAPIClientManager.keys.broadcasteKeyCampaigns));
    }

    public boolean isThereRadiusCampaign(ArrayList<Campaign> campaigns){

        if (campaigns == null) return false;

        if (campaigns.size() == 0) return false;

        Log.e("Campaigns", "campaigns.size()" + campaigns.size());


        for (Campaign campaign : campaigns){

            if (campaign.getCity().equals("Any by radius")){
                return true;
            }

        }
        return false;
    }

    Timer timer;

    Location retrievedLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_a_little_about_you_ongoing_studies_onboarding_13, container, false);

        Button nextButton = view.findViewById(R.id.nextOB13Button);
        nextButton.setOnClickListener(this);

        campaignListView = view.findViewById(R.id.campaignsListView);
        campaignListView.setExpanded(true);
//        updateUI(campaigns);


//        howManyStudiesTextView = view.findViewById(R.id.howManyStudiesTextView);
        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
//
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        timer = new Timer();
        timer.schedule(new RemindTask(), 5000);

         if(isThereRadiusCampaign(campaigns)){
             Log.e("Campaigns", "There's radius campaigns");

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationProviderClient.requestLocationUpdates(buildHighAccuracyLocRequest(), mLocationCallback, null);
                    Log.e("Campaigns", "got permissions -> asking for loction");
                    ((OnboardingActivity) getActivity()).showLoading();
                }else{
                    Log.e("Campaigns", "got NO permissions -> updating ui");
                    updateUI(campaigns);
                    timer.cancel();
                }

         }else{
             Log.e("Campaigns", "There's NO radius campaigns");

             updateUI(campaigns);
             timer.cancel();
         }


        return view;
    }

    public static boolean gotLocation = false;

    class RemindTask extends TimerTask {
        public void run() {

            Log.e("Campaigns", "Time's up!");

            timer.cancel(); //Terminate the timer thread
            ((OnboardingActivity) getActivity()).stopLoading();

            if (!gotLocation){
                Log.e("Campaigns", "Time's up and got NO location - drawing with campaigns!");
                updateUI(campaigns);
            }else{
                Log.e("Campaigns", "Time's up and got location - do nothing!");
            }

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {


                if (location == null){
                    Log.e("Campaigns", "location null - drawing with what we have");
                    updateUI(campaigns);
                    timer.cancel();
                    break;
                }

                Log.e("Campaigns", "Location retrieved " + location.getLatitude() + "," + location.getLongitude());


                ArrayList<Campaign> filteredCampaigns = new ArrayList<>();

                Log.e("Campaigns", "Filtering campaigns");

                for (Campaign campaign : campaigns){
                    Log.e("Campaigns", "Checking " + campaign.getCampaignID() + " name " + campaign.getName());
                    if(campaign.getCity().equals("Any by radius")){
                        if(checkIfCampaignIsInsideRadius(location, campaign)){
                            filteredCampaigns.add(campaign);
                            Log.e("Campaigns", "adding to filtered campaigns " + campaign.getName() + " " + campaign.getCampaignID());
                        }
                    }else{
                        filteredCampaigns.add(campaign);
                    }
                }


                Log.e("Campaigns", "drawing campaigns after filtering");

                updateUI(filteredCampaigns);
                gotLocation = true;

                timer.cancel();

                ((OnboardingActivity) getActivity()).stopLoading();

                break;
            }
        };
    };

    private boolean checkIfCampaignIsInsideRadius(Location location, Campaign campaign) {

        Log.e("Campaigns", "Distance -> " + LocationUtils.meterDistanceBetweenTwoLocations(location, campaign.getLocation()));

        Log.e("Campaigns", "Curr " + location.getLatitude() + "," + location.getLongitude() + " Campaign loc " + campaign.getLocation().getLat() + "," + campaign.getLocation().getLng());

        if(LocationUtils.meterDistanceBetweenTwoLocations(location, campaign.getLocation()) <= campaign.getRadius()){
            return true;
        }

        return false;

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
            case R.id.nextOB13Button:

                ArrayList<String> onCampaigns = new ArrayList<>();

                for (Campaign campaign : adapter.getCampaignsSelected()){
                    onCampaigns.add(campaign.getCampaignID());
                    Log.e("Onboarding13Fragment", "Added campaign " + campaign.getCampaignID());
                }

                 ((OnboardingActivity) getActivity()).saveOnCampaignsSelected(onCampaigns);


                Onboarding14Fragment nextFragment = Onboarding14Fragment.newInstance(ONBOARDING_MODE);
                //using Bundle to send data
                FragmentTransaction nextTransaction=getFragmentManager().beginTransaction();
                nextTransaction.replace(R.id.onboarding_main_fragment, nextFragment).addToBackStack(null);
                nextTransaction.commit();
                break;
            case R.id.backButton:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }

    }

    private LocationRequest buildHighAccuracyLocRequest(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Log.d("onboardingCampaigns","Building high accuracy location request");

        return mLocationRequest;

    }

    @Override
    public void onStop() {
        super.onStop();

        ((OnboardingActivity) getActivity()).stopLoading();
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);

    }


    public void updateUI(ArrayList<Campaign> campaigns){

        if(campaigns.size() == 0) return;

        ArrayList<CampaignWrapper> camapaignsWrapped = new ArrayList<>();

        for(Campaign c : campaigns){
            camapaignsWrapped.add(new CampaignWrapper(c, false));
        }

        adapter = new CampaignListAdapter(camapaignsWrapped, getContext());
        campaignListView.setAdapter(adapter);


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

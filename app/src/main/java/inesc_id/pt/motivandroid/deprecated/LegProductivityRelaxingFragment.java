package inesc_id.pt.motivandroid.deprecated;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.myTrips.adapters.LegProductivityRelaxingRecyclerAdapter;
import inesc_id.pt.motivandroid.myTrips.fragments.OverviewAfterRelaxingProductivityFragment;
import inesc_id.pt.motivandroid.deprecated.relaxingRide.AdapterCallbackRelaxingRating;
import inesc_id.pt.motivandroid.deprecated.relaxingRide.adapters.OrderFactorsRecyclerListAdapter;
import inesc_id.pt.motivandroid.deprecated.relaxingRide.adapters.RelaxingRideFactorsListAdapter;
import inesc_id.pt.motivandroid.deprecated.relaxingRide.adapters.RelaxingRideRateSatisfactonFactorsListAdapter;
import inesc_id.pt.motivandroid.deprecated.relaxingRide.adapters.recyclerDragHelpers.OnStartDragListener;
import inesc_id.pt.motivandroid.deprecated.relaxingRide.adapters.recyclerDragHelpers.SimpleItemTouchHelperCallback;
import inesc_id.pt.motivandroid.data.validationAndRating.RelaxingProductiveFactor;
import inesc_id.pt.motivandroid.persistence.TripKeeper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LegProductivityRelaxingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LegProductivityRelaxingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@Deprecated
public class LegProductivityRelaxingFragment extends Fragment implements View.OnClickListener, AdapterCallbackRelaxingRating, OnStartDragListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FullTrip fullTrip;

    private String fullTripID;

    RecyclerView relaxingProductiveRecyclerView;

    Button confirmAllButton;

    LegProductivityRelaxingRecyclerAdapter adapter;

    //PersistentTripStorage persistentTripStorage;

    TripKeeper tripKeeper;

    private OnFragmentInteractionListener mListener;

    View view;

    private ItemTouchHelper mItemTouchHelper;

    Dialog dialog;

    //select factors
    RelaxingRideFactorsListAdapter selectFactorsAdapter;

    //order factors
    OrderFactorsRecyclerListAdapter recAdapter;

    //rate factors
    private RelaxingRideRateSatisfactonFactorsListAdapter rateFactorsAdapter;

    private int tripPartCurrentlyBeingRated;
    private int mode;

    ScrollView scrollView;
    ExpandableHeightListView factorsListView;
    RecyclerView recyclerView;

    EditText otherFactors;

    public LegProductivityRelaxingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fullTripBeingValidated Parameter 1.
     * @return A new instance of fragment LegProductivityRelaxingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LegProductivityRelaxingFragment newInstance(String fullTripBeingValidated) {
        LegProductivityRelaxingFragment fragment = new LegProductivityRelaxingFragment();
        Bundle args = new Bundle();
        args.putString(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTripBeingValidated);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            fullTripID = getArguments().getString(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED);
        }

        tripKeeper = TripKeeper.getInstance(getContext());
        // persistentTripStorage = new PersistentTripStorage(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_leg_productivity_relaxing, container, false);

        relaxingProductiveRecyclerView = view.findViewById(R.id.productivityRelaxingRecyclerView);

        confirmAllButton = view.findViewById(R.id.confirmAllRelaxingProductiveButton);
        confirmAllButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        fullTrip = tripKeeper.getCurrentFullTrip(fullTripID);
//        fullTrip = persistentTripStorage.getFullTripByDate(fullTripID);

        adapter = new LegProductivityRelaxingRecyclerAdapter(fullTrip, getContext(), this);

        GridLayoutManager recyclerLayoutManager = new GridLayoutManager(getContext(),1);

        relaxingProductiveRecyclerView.setLayoutManager(recyclerLayoutManager);

        relaxingProductiveRecyclerView.setAdapter(adapter);
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
            case R.id.confirmAllRelaxingProductiveButton:

                    fullTrip.updateRelaxingProductivity(adapter.tripPartList);
//                    persistentTripStorage.updateFullTripDataObject(fullTrip,fullTrip.getDateId());

                tripKeeper.setCurrentFullTrip(fullTrip);

                OverviewAfterRelaxingProductivityFragment mfragment = OverviewAfterRelaxingProductivityFragment.newInstance(fullTrip.getDateId());
                //using Bundle to send data
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.my_trips_main_fragment, mfragment).addToBackStack(null);
                transaction.commit();

                break;

            case R.id.doneChoosingFactorsButton:

                ArrayList<RelaxingProductiveFactor> selectedFactors = selectFactorsAdapter.getSelectedFactors();

                if(selectedFactors.size() == 0){
                    dialog.dismiss();
                }else{
                    renderOrderFactorsRecyclerView(selectedFactors);
                }



                break;

            case R.id.doneOrderingFactorsButton:

                ArrayList<RelaxingProductiveFactor> orderedFactors = recAdapter.getOrderedFactors();
                renderFactorsSatisfactionListView(orderedFactors);

                break;

            case R.id.doneRatingFactorsButton:

                ArrayList<RelaxingProductiveFactor> ratedFactors = rateFactorsAdapter.getRatedFactors();

                int i = 0;
                for(RelaxingProductiveFactor relaxingProductiveFactor : ratedFactors){
                    relaxingProductiveFactor.setOrderValue(i);
                    i++;
                }

                if(mode == keys.PRODUCTIVE_FACTORS){
                    fullTrip.getTripList().get(tripPartCurrentlyBeingRated).setProductiveFactorsArrayList(ratedFactors);
                }else{
                    fullTrip.getTripList().get(tripPartCurrentlyBeingRated).setRelaxingFactorsArrayList(ratedFactors);
                }

                renderOtherFactorsLayout();
                //tripKeeper.setCurrentFullTrip(fullTrip);

                break;
            case R.id.doneOtherFactorsButton:

                if(mode == keys.PRODUCTIVE_FACTORS){
                    fullTrip.getTripList().get(tripPartCurrentlyBeingRated).setProductiveFactorsText(otherFactors.getText().toString());
                }else{
                    fullTrip.getTripList().get(tripPartCurrentlyBeingRated).setRelaxingFactorsText(otherFactors.getText().toString());
                }

                dialog.dismiss();

                break;

            case R.id.dismissDialogButton:

                dialog.dismiss();

                break;

        }

    }

    private void renderOtherFactorsLayout() {

        Button doneOtherFactorsButton = dialog.findViewById(R.id.doneOtherFactorsButton);
        doneOtherFactorsButton.setOnClickListener(this);

        otherFactors = dialog.findViewById(R.id.editTextAddSatisfactionFactors);

        ConstraintLayout otherFactorsLayout = dialog.findViewById(R.id.otherFactorSatisfactionConstraintLayout);
        otherFactorsLayout.setVisibility(View.VISIBLE);

        focusOnBottomScroll();


    }

    private final void focusOnTopScrollView(){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                Log.d("Scroll", "trying to top");
                scrollView.scrollTo(0, scrollView.getTop());
            }
        });
    }

    private final void focusOnBottomScroll(){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void renderFactorSelectionListView() {

        //select factors part first


        factorsListView = dialog.findViewById(R.id.factorsListView);

        ArrayList<RelaxingProductiveFactor> factors = RelaxingProductiveFactor.getListOfRelaxingFactors();
        selectFactorsAdapter = new RelaxingRideFactorsListAdapter(factors, getContext());

        factorsListView.setDivider(null);
        factorsListView.setAdapter(selectFactorsAdapter);
        factorsListView.setExpanded(true);


        scrollView = dialog.findViewById(R.id.scrollView);

        Button doneSelectingFactorsButton = dialog.findViewById(R.id.doneChoosingFactorsButton);
        doneSelectingFactorsButton.setOnClickListener(this);

        ConstraintLayout orderFactorsLayout = dialog.findViewById(R.id.orderFactorsConstraintLayout);
        orderFactorsLayout.setVisibility(View.GONE);

        ConstraintLayout rateFactorsLayout = dialog.findViewById(R.id.rateFactorSatisfactionConstraintLayout);
        rateFactorsLayout.setVisibility(View.GONE);

        ConstraintLayout otherFactorsLayout = dialog.findViewById(R.id.otherFactorSatisfactionConstraintLayout);
        otherFactorsLayout.setVisibility(View.GONE);

        focusOnTopScrollView();

    }

    private void renderOrderFactorsRecyclerView(ArrayList<RelaxingProductiveFactor> selectedFactors) {

        //order factors then

        recyclerView = dialog.findViewById(R.id.orderFactorsRecyclerView);

        recyclerView.setHasFixedSize(true);

        recAdapter = new OrderFactorsRecyclerListAdapter(getActivity(), this, selectedFactors);

        recyclerView.setAdapter(recAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(recAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        ConstraintLayout orderFactorsLayout = dialog.findViewById(R.id.orderFactorsConstraintLayout);
        orderFactorsLayout.setVisibility(View.VISIBLE);

        Button doneOrderingFactorsButton = dialog.findViewById(R.id.doneOrderingFactorsButton);
        doneOrderingFactorsButton.setOnClickListener(this);

        focusOnBottomScroll();

    }

    private void renderFactorsSatisfactionListView(ArrayList<RelaxingProductiveFactor> orderedFactors) {

        ExpandableHeightListView rateFactorsListView;
        rateFactorsListView = dialog.findViewById(R.id.rateFactorsListView);

        rateFactorsAdapter = new RelaxingRideRateSatisfactonFactorsListAdapter(orderedFactors, getActivity());

        rateFactorsListView.setDivider(null);
        rateFactorsListView.setAdapter(rateFactorsAdapter);
        rateFactorsListView.setExpanded(true);

        Button doneRatingFactorsButton = dialog.findViewById(R.id.doneRatingFactorsButton);
        doneRatingFactorsButton.setOnClickListener(this);

        ConstraintLayout rateFactorsLayout = dialog.findViewById(R.id.rateFactorSatisfactionConstraintLayout);
        rateFactorsLayout.setVisibility(View.VISIBLE);

        focusOnBottomScroll();

    }

    @Override
    public void openRelaxingDialog(int index, int mode) {

        this.tripPartCurrentlyBeingRated = index;
        this.mode = mode;

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_relaxing_ride_factores, null);
        dialog = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        dialog.setContentView(dialogView);

        TextView title = dialog.findViewById(R.id.relaxingRideSubTitleTextView);
        TextView description = dialog.findViewById(R.id.relaxingRideDescriptionTextView);

        if (mode == keys.RELAXING_FACTORS){
            title.setText("Relaxing Bus ride");
            description.setText("Help us understand what made this bus ride more relaxing than usual");
        }else{
            title.setText("Productive Bus ride");
            description.setText("Help us understand what made this bus ride more productive than usual");
        }

        renderFactorSelectionListView();
        dialog.show();

    }



    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);

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

    public interface keys{

        int RELAXING_FACTORS = 0;
        int PRODUCTIVE_FACTORS = 1;

    }

}

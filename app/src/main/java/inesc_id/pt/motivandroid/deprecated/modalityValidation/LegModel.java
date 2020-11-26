package inesc_id.pt.motivandroid.deprecated.modalityValidation;

import inesc_id.pt.motivandroid.data.tripData.Trip;

@Deprecated
public class LegModel {

    private Trip leg;
    private int tripPartIndex;

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    private int selectedIndex;

    public boolean isWrongLeg() {
        return isWrongLeg;
    }

    public void setWrongLeg(boolean wrongLeg) {
        isWrongLeg = wrongLeg;
    }

    private boolean isWrongLeg;

    public LegModel(Trip leg, int index){
        this.leg = leg;
        this.tripPartIndex = index;
        this.selectedIndex = leg.getModality();
        this.isWrongLeg = leg.isWrongLeg();
    }

    public Trip getLeg() {
        return leg;
    }

    public void setLeg(Trip leg) {
        this.leg = leg;
    }

    public int getTripPartIndex() {
        return tripPartIndex;
    }

    public void setTripPartIndex(int index) {
        this.tripPartIndex = index;
    }





}

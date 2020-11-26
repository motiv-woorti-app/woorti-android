package inesc_id.pt.motivandroid.deprecated.modalityValidation;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

@Deprecated
public class LegMarkerPolylineHolder {

    Marker legMarker;
    Polyline legPolyline;
    //to know what index this leg corresponds on the full trip part list (have both legs and waiting events)
    int fullTripPartIndex;
    //to check if if there is the possibility of joining the leg with the next
    boolean isNextPartTrip;
    int selfIndex;

    public LegMarkerPolylineHolder(){}

    public LegMarkerPolylineHolder(Marker legMarker, Polyline legPolyline, int fullTripPartIndex, boolean isNextPartTrip, int selfIndex) {
        this.legMarker = legMarker;
        this.legPolyline = legPolyline;
        this.fullTripPartIndex = fullTripPartIndex;
        this.isNextPartTrip = isNextPartTrip;
        this.selfIndex = selfIndex;
    }

    public Marker getLegMarker() {
        return legMarker;
    }

    public void setLegMarker(Marker legMarker) {
        this.legMarker = legMarker;
    }

    public Polyline getLegPolyline() {
        return legPolyline;
    }

    public void setLegPolyline(Polyline legPolyline) {
        this.legPolyline = legPolyline;
    }

    public int getFullTripPartIndex() {
        return fullTripPartIndex;
    }

    public void setFullTripPartIndex(int fullTripPartIndex) {
        this.fullTripPartIndex = fullTripPartIndex;
    }

    public boolean isNextPartTrip() {
        return isNextPartTrip;
    }

    public void setNextPartTrip(boolean nextPartTrip) {
        isNextPartTrip = nextPartTrip;
    }

    public String toString(){
        return selfIndex + "";
    }

}

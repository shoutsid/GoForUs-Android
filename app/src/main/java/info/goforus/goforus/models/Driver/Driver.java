package info.goforus.goforus.models.driver;


import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.List;

import info.goforus.goforus.R;
import us.monoid.json.JSONObject;

public class Driver extends SugarRecord implements Comparable<Driver> {
    private static final String TAG = "Driver";
    public Integer externalId;
    public String name;
    public String email;
    public double lat;
    public double lng;
    public String short_bio;
    public String mobile_number;
    public Integer rating = 5;


    public DriverIndicator indicator;
    public Marker marker;
    public GoogleMap map;

    public Driver(JSONObject driverObject) {
        try {
            this.externalId = Integer.parseInt(driverObject.get("id").toString());
            this.name = driverObject.get("name").toString();
            this.email = driverObject.get("email").toString();
            this.lat = Double.parseDouble(driverObject.get("lat").toString());
            this.lng = Double.parseDouble(driverObject.get("lng").toString());
            this.mobile_number = driverObject.get("mobile_number").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public LatLng location() {
        return new LatLng(lat, lng);
    }

    public String toString() {
        return name;
    }

    public void addToMap(GoogleMap map) {
        marker = map.addMarker(new MarkerOptions()
                        .position(location())
                        .visible(true)
                        .anchor(0.5f, 0.5f)
                        .title(name)
                        .snippet(short_bio)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
        );
        this.map = map;
    }

    public void updatePositionOnMap(){
        if(map != null){
            marker.setPosition(location());
            if (marker.isInfoWindowShown()) {
                goTo();
            }
        }
    }

    public void goTo() {
        if (marker != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15);
            map.animateCamera(cameraUpdate, 1, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                }

                @Override
                public void onCancel() {
                }
            });
            marker.showInfoWindow();
        }
    }

    /* ================== Class Conversions  =======================*/
    public DriverInformation toDriverInformation(){
        return new DriverInformation(this);
    }

    /* =================== Custom Lookups/Comparators =============== */
    public static boolean containsId(List<Driver> list, long id) {
        for (Driver object : list) {
            if (object.externalId == id) {
                return true;
            }
        }
        return false;
    }

    public static Driver findByDriverMarker(List<Driver> list, Marker marker) {
        for (Driver object : list) {
            if (object.marker != null){
                if (object.marker.equals(marker)) {
                    return object;
                }
            }
        }
        return null;
    }

    public static Integer getIndexOfDriverFrom(List<Driver> list, long id) {
        for (Driver object : list) {
            if (object.externalId == id) {
                return list.indexOf(object);
            }
        }
        return null;
    }


    @Override
    public int compareTo(@NonNull Driver another) {
        return this.externalId.compareTo(another.externalId);

    }
}
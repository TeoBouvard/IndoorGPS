package com.example.teobouvard.projetinfo;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;


public class PositionPas {

    Location mCurrentLocation;

    public PositionPas(LatLng loc){
        mCurrentLocation = new Location("Start");
        this.mCurrentLocation.setLatitude(loc.latitude);
        this.mCurrentLocation.setLongitude(loc.longitude);
    }

    public Location computeNextStep(float stepSize, float bearing){

        final float R = (float) 6378137.0;
        float dist = stepSize/R;

        Location mNewLocation = mCurrentLocation;
        double lat1 = Math.toRadians(mCurrentLocation.getLatitude());
        double lon1 = Math.toRadians(mCurrentLocation.getLongitude());

        double lat2 = Math.asin(Math.sin(lat1)*Math.cos(dist) + Math.cos(lat1)*Math.sin(dist)*Math.cos(bearing));
        double lon2 = lon1 + Math.atan2(Math.sin(bearing)*Math.sin(dist)*Math.cos(lat1), Math.cos(dist)-Math.sin(lat1)*Math.sin(lat2));

        mNewLocation.setLatitude(Math.toDegrees(lat2));
        mNewLocation.setLongitude(Math.toDegrees(lon2));

        return mNewLocation;
    }

    public Location getmCurrentLocation() {
        return mCurrentLocation;
    }

    public void setmCurrentLocation(Location mLocation) {
        this.mCurrentLocation = mLocation;
    }

}

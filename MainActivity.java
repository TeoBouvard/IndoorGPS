package com.example.teobouvard.projetinfo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.teobouvard.projetinfo.R.id.map;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback  {

    private final static LatLng insa = new LatLng(45.783635,4.881821);
    private final static int meanStep = 70;
    private final static double meanAccel = 300;
    private final static float max = 100f;
    private final static int init = 1;

    private TextView nbPas, angleNord, stepDistance, accelText;
    private SeekBar taillePas, seuilAccel;
    private SensorManager sensorManager;
    private DetectionPas mStepCount;
    private OrientationSupport mOrientation;
    private PositionPas mStepPosition;
    private SupportMapFragment mapFragment;
    private Marker marker;
    private RadioButton cls, sat;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mStepCount = new DetectionPas(sensorManager);
        mStepCount.setStepListener(mStepListener);
        mOrientation = new OrientationSupport(sensorManager);
        mStepPosition = new PositionPas(insa);

        nbPas = (TextView) findViewById(R.id.nbPasText);
        angleNord = (TextView) findViewById(R.id.yawText);
        stepDistance = (TextView) findViewById(R.id.stepDistanceText);
        accelText = (TextView) findViewById(R.id.accelText);
        taillePas = (SeekBar) findViewById(R.id.taillePasBar);
        seuilAccel = (SeekBar) findViewById(R.id.seuilBar);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
        cls = (RadioButton) findViewById(R.id.cls);
        sat = (RadioButton) findViewById(R.id.sat);

        cls.setOnCheckedChangeListener(onCheckedChangeListener);
        sat.setOnCheckedChangeListener(onCheckedChangeListener);

        taillePas.setProgress(meanStep);
        seuilAccel.setProgress((int) meanAccel);
        mStepCount.setAcc(meanAccel / 100);
        accelText.setText(String.valueOf(meanAccel / 100));
        stepDistance.setText((float) meanStep / max + " m");

        taillePas.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                stepDistance.setText(String.valueOf((float) taillePas.getProgress()/100.0) + " m");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seuilAccel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mStepCount.setAcc(seuilAccel.getProgress() / 100);
                accelText.setText(String.valueOf((double) seuilAccel.getProgress() / 100));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (cls.isChecked()) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            } else {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mStepCount.start();
        mOrientation.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStepCount.stop();
        mOrientation.stop();
    }

    public void resetPas(View view){
        mStepCount.setPas(init);
        nbPas.setText("0");
        angleNord.setText("");
        marker.setPosition(insa);
        taillePas.setProgress(meanStep);
        seuilAccel.setProgress((int) meanAccel);
    }

    private DetectionPas.StepListener mStepListener = new DetectionPas.StepListener() {

        @Override
        public void onStepDetected() {
            nbPas.setText(String.valueOf(mStepCount.getPas()));
            float[] vals = mOrientation.getmOrientationVals();
            float yaw = vals[0];
            angleNord.setText(String.valueOf((int)Math.toDegrees(yaw)) + "°");

            mStepPosition.setmCurrentLocation(mStepPosition.computeNextStep((float)(taillePas.getProgress()/max), yaw));

            double lat = mStepPosition.getmCurrentLocation().getLatitude();
            double lon = mStepPosition.getmCurrentLocation().getLongitude();
            marker.setPosition(new LatLng(lat,lon));

        }
    };

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        marker = map.addMarker(new MarkerOptions().position(insa).title("Marker"));
        marker.setDraggable(true);
        map.setOnMarkerDragListener (dragListener);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(marker.getPosition()).zoom(13.0f).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.moveCamera(cameraUpdate);
    }

    GoogleMap.OnMarkerDragListener dragListener = new GoogleMap.OnMarkerDragListener() {

        @Override
        public void onMarkerDragStart(Marker marker) {
        }

        @Override
        public void onMarkerDrag(Marker marker) {
            Location currentLocation = new Location ("Me");
            currentLocation.setLatitude(marker.getPosition().latitude);
            currentLocation.setLongitude(marker.getPosition().longitude);
            mStepPosition.setmCurrentLocation(currentLocation);
        }

        @Override
        public void onMarkerDragEnd(Marker marker) {

        }
    };

}

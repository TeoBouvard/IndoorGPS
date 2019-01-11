package com.example.teobouvard.projetinfo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;

import static android.hardware.SensorManager.SENSOR_DELAY_UI;

public class OrientationSupport implements SensorEventListener{

    private float[] mOrientationVals = new float[3];
    private float[] mRotationMatrixMagnetic = new float[16];
    private float[] getmRotationMatrixMagneticToTrue = new float[16];
    private float[] mRotationMatrix = new float[16];

    private SensorManager mSensorManager;
    private Sensor mSensor;

    public OrientationSupport(SensorManager sensorManager){
        this.mSensorManager = sensorManager;
        this.mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void start(){
        mSensorManager.registerListener(this, mSensor, SENSOR_DELAY_UI);
    }

    public void stop(){
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR){
            return;
        }
        else {
            SensorManager.getRotationMatrixFromVector(mRotationMatrixMagnetic, event.values);
            Matrix.setRotateM(getmRotationMatrixMagneticToTrue, 0, -1.83f, 0, 0, 1);
            Matrix.multiplyMM(mRotationMatrix, 0, mRotationMatrixMagnetic, 0, getmRotationMatrixMagneticToTrue, 0);
            SensorManager.getOrientation(mRotationMatrix, mOrientationVals);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public float[] getmOrientationVals() {
        return mOrientationVals;
    }
}

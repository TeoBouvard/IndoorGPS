package com.example.teobouvard.projetinfo;


import android.hardware.Sensor;
import android.hardware.SensorManager;

public class DetectionPasCapteur {

    private int pas;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    public DetectionPasCapteur(SensorManager sensorManager){
        this.pas = 1;
        this.mSensorManager = sensorManager;
        this.mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }



}

package com.example.teobouvard.projetinfo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;

import static android.hardware.SensorManager.GRAVITY_EARTH;
import static android.hardware.SensorManager.SENSOR_DELAY_UI;

public class DetectionPas implements SensorEventListener {

    private final static int TIMER = 500;
    private final static int INIT = 1;
    private final static int ACC = 3;

    private int pas;
    private double acc;
    private StepListener mStepListener;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean timer;

    public DetectionPas(SensorManager sensorManager){
        this.acc = ACC;
        this.timer = true;
        this.pas = INIT;
        this.mSensorManager = sensorManager;
        this.mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void start(){
        mSensorManager.registerListener(this, mSensor, SENSOR_DELAY_UI);
    }

    public void stop(){
        mSensorManager.unregisterListener(this);
    }

    public float calculAcceleration(float a, float b, float c){
        float acceleration = (float) Math.pow((Math.pow(a, 2) + Math.pow(b, 2) + Math.pow(c, 2)), 0.5);
        return acceleration - GRAVITY_EARTH;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        float acceleration = calculAcceleration(values[0], values[1], values[2]);

        if (acceleration > acc && timer){
            mStepListener.onStepDetected();
            pas++;
            timerLastStep();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void setStepListener(StepListener stepListener){
        mStepListener = stepListener;
    }

    public interface StepListener {
        void onStepDetected();
    }

    public int getPas() {
        return pas;
    }

    public void setPas(int pas){
        this.pas = pas;
    }

    public void setAcc(double acc) {
        this.acc = acc;
    }

    //compte à rebours afin de ne pas détecter de pas dans la 0.5s suivant un pas
    public void timerLastStep(){
        timer = false;
        new CountDownTimer(TIMER, TIMER){

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                timer = true;
            }
        }.start();
    }

}

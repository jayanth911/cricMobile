package com.example.sender;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import okhttp3.WebSocket;
public class SensorService {
    private final SensorManager sensorManager;
    private final WebSocket webSocket;
    private final SensorEventListener sensorEventListener;
    private TextView tvSensorData;
    public SensorService(Context context, WebSocket webSocket, TextView tvSensorData) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.webSocket = webSocket;
        this.tvSensorData=tvSensorData;
        this.sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float ax = event.values[0];
                    float ay = event.values[1];
                    float az = event.values[2];
                    String accelerometerData = "Accelerometer: x=" + ax + ", y=" + ay + ", z=" + az;
                    Log.d("SensorService", accelerometerData);  // Log the data for debugging
                    tvSensorData.setText(accelerometerData);
                    webSocket.send(accelerometerData);          // Send the data to server
                } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    float gx = event.values[0];
                    float gy = event.values[1];
                    float gz = event.values[2];
                    String gyroscopeData = "Gyroscope: x=" + gx + ", y=" + gy + ", z=" + gz;
                    Log.d("SensorService", gyroscopeData);
                    // Log the data for debugging
                    tvSensorData.setText(gyroscopeData);
                    webSocket.send(gyroscopeData);              // Send the data to server
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Handle changes in sensor accuracy, if necessary
            }
        };
    }
    public void start() {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void stop() {
        sensorManager.unregisterListener(sensorEventListener);
    }
}

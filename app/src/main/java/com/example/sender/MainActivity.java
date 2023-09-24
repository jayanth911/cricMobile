package com.example.sender;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SensorService sensorService;
    private WebSocket webSocket;

    private TextView tvSensorData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvSensorData = findViewById(R.id.dataTextView);
        // Initialize the WebSocket
        initializeWebSocket();
    }

    private void initializeWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("ws://192.168.29.193:8765")  // Your WebSocket server URL
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                super.onOpen(webSocket, response);
                Log.d("MainActivity", "WebSocket opened");
                startSensorService();  // Start the SensorService after WebSocket connection is established
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                Log.d("MainActivity", "Received message from server: " + text);
            }

            // ... (Implement other callback methods as necessary, e.g., onClose, onFailure)
        });
    }

    private void startSensorService() {
        sensorService = new SensorService(this, webSocket,tvSensorData);
        sensorService.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorService != null) {
            sensorService.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorService != null) {
            sensorService.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "App destroyed");  // Close the WebSocket with a reason
        }
    }
}

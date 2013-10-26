package org.thomasamsler.android.sensor.light;

import java.net.MalformedURLException;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mLight;

	private TextView luxTextView;
	private SocketIO socket;
	private float lux = 0.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupSocketIo();

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

		luxTextView = (TextView) findViewById(R.id.tv_lux);
	}

	// SensorEventListener method
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

		Log.i("Light Sensor", "Sensor : " + sensor.getName() + " : accuracy : "
				+ accuracy);
	}

	// SensorEventListener method
	@Override
	public void onSensorChanged(SensorEvent event) {

		lux = event.values[0];
		Log.i("Light Sensor", "Lux : " + lux + " lx");
		luxTextView.setText(lux + " lx");
		
		JSONObject json = new JSONObject();
		
		try {
			
			json.put("src", "B");
			json.put("lux", lux);
			
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		socket.emit("sensor", json);
	}

	@Override
	protected void onResume() {

		super.onResume();
		mSensorManager.registerListener(this, mLight,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {

		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	private void setupSocketIo() {
		
		try {
			
			socket = new SocketIO("http://192.168.43.22:8080/");
			
		}
		catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		socket.connect(new IOCallback() {
			
			@Override
			public void onMessage(JSONObject json, IOAcknowledge ack) {
				try {
					System.out.println("Server said:" + json.toString(2));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onMessage(String data, IOAcknowledge ack) {
				System.out.println("Server said: " + data);
			}

			@Override
			public void onError(SocketIOException socketIOException) {
				System.out.println("an Error occured");
				socketIOException.printStackTrace();
			}

			@Override
			public void onDisconnect() {
				System.out.println("Connection terminated.");
			}

			@Override
			public void onConnect() {
				System.out.println("Connection established XXXXX");
			}

			@Override
			public void on(String event, IOAcknowledge ack, Object... args) {
				System.out.println("Server triggered event '" + event + "'");
			}
		});

		// This line is cached until the connection is establisched.
		//socket.send("Hello Server!");
	}
}

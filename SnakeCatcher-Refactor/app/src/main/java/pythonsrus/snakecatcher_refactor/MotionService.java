package pythonsrus.snakecatcher_refactor;

/**
 * Created by Justin Liu on 12/8/17.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class MotionService extends Service implements SensorEventListener {
    private static final String TAG = "MotionService";

    private long first_accel_time;
    private long T0 = System.currentTimeMillis();

    private SensorManager mSensorManager;

    private MyBinder binder = new MyBinder();
    private PowerManager.WakeLock wakeLock;

    public MotionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 100);

        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

        return super.onStartCommand(intent, flags, startId);
    }

    public void clear() {
        T0 = System.currentTimeMillis();
        first_accel_time = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //Start after 30s
            if (System.currentTimeMillis() - T0 >= 5000) {
                // When phone is settled it still has x&y acceleration，So I set this value to be
                // >0.5 to avoid errors.
                /**
                 * The app tells you if the phone was moved. When you start the app, it waits 30 seconds
                 before starting to detect motion. Then, it remembers if someone has moved the phone.
                 */
                if (first_accel_time == 0 && (Math.abs(values[0]) >= 0.5 || Math.abs(values[1]) >= 0.5)) {
                    first_accel_time = System.currentTimeMillis();
                }
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public class MyBinder extends Binder {
        public MotionService getService() {
            return MotionService.this;
        }
    }

    /**
     * The service has a method, that can be called from the activity. The method is used to check
     * if someone moved the phone at least 30 seconds ago, to give you time to pick the phone up
     * and check.
     *
     * @return
     */
    public boolean didItMove() {
        long d = System.currentTimeMillis();
        boolean move = false;
        synchronized (binder) {
            //30s ago is other people moved your phone
            if (first_accel_time != 0) {
                Log.e(TAG, "didItMove: 30s" + (d - first_accel_time));
                move = true;
            }
        }
        return move;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //wakeLock.release();
    }
}

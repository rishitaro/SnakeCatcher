package pythonsrus.snakecatcher_refactor;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MotionDetection extends Activity {
    private static final String TAG = "MotionDetection";
    private ServiceConnection connection;
    private MotionService service;
    public MotionItem item;
    private TextView textView;
    private DatabaseReference databaseReference;
    String uid = "";
    String email = "";
    double latitude;
    double longitude;
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_detection);
        uid = getIntent().getStringExtra("uid");
        email = getIntent().getStringExtra("email");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        initView();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void writeNewMotionItem(String uid, MotionItem item){
        databaseReference
                .child(uid)
                .child("motion activity")
                .child(item.getDatetime().toString())
                .setValue(item);
    }

    private void initView() {
        textView = (TextView) findViewById(R.id.textView);
        final ServiceTask[] task = {new ServiceTask()};

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("Click STOP to end motion detection session");
                locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                initService();
                item = new MotionItem();
                task[0].execute();
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                item.endMotion(service.didItMove());
                writeNewMotionItem(uid, item);
                task[0].cancel(true);

                if (service.didItMove()){

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                GMAILSender sender = new GMAILSender("snakecatcherapp@gmail.com", "cmps115struggle");
                                sender.sendMail("Motion Detected!",
                                        "Hello Friend, \n \nThis is an email notification to let you know that your phone was moved during an active motion detecting session. \n" +
                                                "The following is a link to where the device was when the session ended:" +
                                                "\nhttps://www.google.com/maps/search/?api=1&query="+latitude + "," +longitude+ "\n\n" +
                                                "Best, \nSnakeCatcher ",
                                        "snakecatcherapp@gmail.com",
                                        email);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                            }
                        }
                    }).start();

                }

                task[0] = new ServiceTask();
                textView.setText("Click START to begin another session");
                unbindService(connection);
                service.onDestroy();
                stopService(new Intent(MotionDetection.this, MotionService.class));
            }
        });
    }

    private void initService() {
        Log.v(TAG, "in initService()");
        Intent intent = new Intent(this, MotionService.class);
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                service = ((MotionService.MyBinder) iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    class ServiceTask extends AsyncTask<Void, Void, Void> {
        boolean isTaskCancelled;
        int count = 0;

        public ServiceTask(){
            isTaskCancelled = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.e(TAG, "doInBackground: " +(service!=null));
            while(!isCancelled()){
                //Start monitoring
                if (service!=null && service.didItMove()) {
                    Log.e(TAG, "doInBackground: has move");

                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("The phone moved!");
                        }
                    });
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                count++;
            }
            return null;
        }

        public int getCount(){
            return count;
        }
    }

    void getLocation() {
        Log.v(TAG, "inGetLocation");

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Log.v(TAG, "inGetLocation");

            if (location != null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.v(TAG, "inGetLocation != null");
                Log.v(TAG, "latitude: " + latitude);
                Log.v(TAG, "longitude: " + longitude);

            } else {
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_motion:
                    return true;
                case R.id.navigation_history:
                    startActivity(new Intent(MotionDetection.this, HistoryView.class));
                    return true;
                case R.id.navigation_settings:
                    startActivity(new Intent(MotionDetection.this, Settings.class));
                    return true;
            }
            return false;
        }
    };
}
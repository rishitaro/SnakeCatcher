package pythonsrus.snakecatcher_refactor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Date;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class HistoryView extends AppCompatActivity {

    private TextView mTextMessage;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_camera:
                    //mTextMessage.setText(R.string.title_camera);
                    startActivity(new Intent(HistoryView.this, Camera.class));
                    return true;
                case R.id.navigation_history:

                    return true;
                case R.id.navigation_settings:
                    startActivity(new Intent(HistoryView.this, Settings.class));
                    //mTextMessage.setText(R.string.title_settings);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously();



        databaseReference = FirebaseDatabase.getInstance().getReference();

        writeNewHistoryItem("test", "www.google.com");

        //Click Listere


    }

    private void writeNewHistoryItem(String uid, String uri){
        HistoryItem item = new HistoryItem(uid, uri);
        //Date time = new Date();
        databaseReference.child(uid).child(item.passHumanTime()).setValue(item);
    }

}

package pythonsrus.snakecatcher_refactor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import butterknife.BindView;


public class HistoryView extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    //private TextView mTextMessage;
    private DatabaseReference databaseReference;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseRecyclerAdapter mAdapter;
    private String TAG = "HistoryView";
    private String uid = "";
    private String email = "";

    @BindView(R.id.db)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if(opr.isDone()){
            GoogleSignInResult result = opr.get();
            uid = result.getSignInAccount().getId();
            email = result.getSignInAccount().getEmail();
            Log.v(TAG, "uid = " + uid);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        writeNewHistoryItem(uid, "www.google.com");

        // Recycler View

        recyclerView = (RecyclerView) findViewById(R.id.db);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(uid)
                .child("motion activity")
                .limitToLast(50);

        FirebaseRecyclerOptions<MotionItem> options =
                new FirebaseRecyclerOptions.Builder<MotionItem>()
                    .setQuery(query, MotionItem.class)
                    .build();

        mAdapter = new FirebaseRecyclerAdapter<MotionItem, MotionItemView>(options){

            @Override
            public MotionItemView onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_motion_item_view, parent, false);
                return new MotionItemView(view);
            }

            @Override
            protected void onBindViewHolder(MotionItemView holder, int position, MotionItem model) {
                holder.bind(model);
            }

            @Override
            public void onDataChanged(){
                Log.v(TAG , "In onDataChanged");
            }
        };

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(mAdapter);

    }


    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
        mAdapter.startListening();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void writeNewHistoryItem(String uid, String uri){
        HistoryItem item = new HistoryItem(uid, uri);
        databaseReference.child(uid).child("login activity").child(item.getDatetime().toString()).setValue(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_motion:
                    Intent i = new Intent(getApplicationContext(), MotionDetection.class);
                    i.putExtra("uid", uid);
                    i.putExtra("email", email);
                    startActivity(i);
                    return true;
                case R.id.navigation_history:
                    return true;
                case R.id.navigation_settings:
                    startActivity(new Intent(HistoryView.this, Settings.class));
                    return true;
            }
            return false;
        }
    };



}

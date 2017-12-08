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

    // Fields
    private DatabaseReference databaseReference;
    private GoogleApiClient mGoogleApiClient;
    FirebaseRecyclerAdapter mAdapter;
    private String TAG = "HistoryView";
    private String uid = "";
    private String email = "";

    // Bind Views
    @BindView(R.id.db)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Views
        setContentView(R.layout.activity_history_view);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        recyclerView = (RecyclerView) findViewById(R.id.db);

        // Google Sign In (for email + uid passthrough)
        googleSignIn();

        // Initialize Database + Write login activity
        databaseReference = FirebaseDatabase.getInstance().getReference();
        writeNewLogInItem(uid, "SignIn Activity");


        // Create options for recycler view and set adapter
        FirebaseRecyclerOptions<MotionItem> options = setRecyclerOptions();
        mAdapter = setFirebaseRecyclerAdapter(options);

        // Create linear layout manager
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        // Set recyclerview linear layout manager and adapter
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(mAdapter);

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

    private void googleSignIn(){
        Log.v(TAG, "In googleSignIn()");

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if(opr.isDone()){
            Log.v(TAG, "Setting uid and emails");
            GoogleSignInResult result = opr.get();
            uid = result.getSignInAccount().getId();
            email = result.getSignInAccount().getEmail();
            Log.v(TAG, "uid = " + uid);
            Log.v(TAG, "email = " + email);
        }
    }

    private FirebaseRecyclerOptions<MotionItem> setRecyclerOptions(){
        Log.v(TAG, "In setRecyclerOptions()");

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(uid)
                .child("motion activity")
                .limitToLast(50);

        FirebaseRecyclerOptions<MotionItem> options =
                new FirebaseRecyclerOptions.Builder<MotionItem>()
                        .setQuery(query, MotionItem.class)
                        .build();
        return options;
    }

    private FirebaseRecyclerAdapter<MotionItem, MotionItemView> setFirebaseRecyclerAdapter(FirebaseRecyclerOptions options){
        Log.v(TAG, "In setFirebaseRecyclerAdapter()");

        FirebaseRecyclerAdapter<MotionItem, MotionItemView> adapter = new FirebaseRecyclerAdapter<MotionItem, MotionItemView>(options){

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
            }
        };

        return adapter;
    }




    private void writeNewLogInItem(String uid, String uri){
        Log.v(TAG, "In writeNewLogInItem()");
        HistoryItem item = new HistoryItem(uid, uri);
        databaseReference.child(uid).child("login activity").child(item.getDatetime().toString()).setValue(item);
    }



}

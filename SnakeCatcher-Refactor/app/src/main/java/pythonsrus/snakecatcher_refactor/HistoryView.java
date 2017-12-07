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

import com.firebase.ui.database.FirebaseListAdapter;
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

import org.w3c.dom.Text;
import java.util.List;


public class HistoryView extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private TextView mTextMessage;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseRecyclerAdapter mAdapter;

    @BindView(R.id.db)
    RecyclerView recyclerView;



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
                    Bundle extras = getIntent().getExtras();
                    String email, name = "";

                    if(extras != null){
                        email = extras.getString("email");
                        name = extras.getString("name");
                        Log.v("history", "name: " + name);
                        Log.v("history", "email: " + email);

                        Intent settings = new Intent(getApplicationContext(), Settings.class);
                        settings.putExtra("email", email);
                        settings.putExtra("email", email);
                        settings.putExtra("name", name);
                        startActivity(settings);
                    }else{
                        startActivity(new Intent(HistoryView.this, Settings.class));
                    }

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

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        String uid = "";
        if(opr.isDone()){
            GoogleSignInResult result = opr.get();
            uid = result.getSignInAccount().getId();
            Log.v("HistoryView", "uid = " + uid);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        writeNewHistoryItem(uid, "www.google.com");

        // Recycler View

        recyclerView = (RecyclerView) findViewById(R.id.db);


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(uid)
                .limitToLast(50);

        FirebaseRecyclerOptions<HistoryItem> options =
                new FirebaseRecyclerOptions.Builder<HistoryItem>()
                    .setQuery(query, HistoryItem.class)
                    .build();

        mAdapter = new FirebaseRecyclerAdapter<HistoryItem, HistoryItemView>(options){

            @Override
            public HistoryItemView onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_item_view, parent, false);
                return new HistoryItemView(view);
            }

            @Override
            protected void onBindViewHolder(HistoryItemView holder, int position, HistoryItem model) {
                holder.bind(model);
            }

            @Override
            public void onDataChanged(){
                Log.v("historyview" , "what the fuck man");
            }
        };

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(mAdapter);

    }


    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
        mAdapter.startListening();
    }

    private void writeNewHistoryItem(String uid, String uri){
        HistoryItem item = new HistoryItem(uid, uri);
        databaseReference.child(uid).child(item.getDatetime().toString()).setValue(item);
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


}

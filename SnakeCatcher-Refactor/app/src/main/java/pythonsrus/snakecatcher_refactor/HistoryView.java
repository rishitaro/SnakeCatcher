package pythonsrus.snakecatcher_refactor;

import android.content.ClipData;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.firebase.ui.database.*;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import org.w3c.dom.Text;

import java.util.List;


public class HistoryView extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private TextView mTextMessage;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private GoogleApiClient mGoogleApiClient;
    private RecyclerView recyclerView;

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
        recyclerView = (RecyclerView) findViewById(R.id.db_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(uid)
                .limitToLast(50);

        FirebaseRecyclerOptions<HistoryItem> options =
                new FirebaseRecyclerOptions.Builder<HistoryItem>()
                        .setQuery(query, HistoryItem.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<HistoryItem, HistoryItemView>(options) {
            @Override
            public HistoryItemView onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_history_view, parent, false);

                return new HistoryItemView(view);
            }

            @Override
            protected void onBindViewHolder(HistoryItemView holder, int position, HistoryItem model) {
                holder.bind(model);
                Log.v("HistoryView", model.datetime_human);
            }
        };

        recyclerView.setAdapter(adapter);


    }

    public class ItemHolder extends RecyclerView.ViewHolder{

        private final ImageView image;
        private final TextView date;

        public ItemHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            date = (TextView) itemView.findViewById(R.id.date);
        }

        public void setDate(String d){
            date.setText(d);
        }


    }


    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    private void writeNewHistoryItem(String uid, String uri){
        HistoryItem item = new HistoryItem(uid, uri);
        databaseReference.child(uid).child(item.passHumanTime()).setValue(item);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

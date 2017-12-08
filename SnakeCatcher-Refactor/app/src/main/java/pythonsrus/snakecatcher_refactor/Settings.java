package pythonsrus.snakecatcher_refactor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;


public class Settings extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{
    private Button SignOut;
    private TextView Name,Email;
    private GoogleApiClient mGoogleApiClient;
    String uid = "";
    String email = "";

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bn_logout:
                signOut();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SignOut = (Button)findViewById(R.id.bn_logout);
        Name = (TextView)findViewById(R.id.name);
        Email = (TextView)findViewById(R.id.email);

        SignOut.setOnClickListener(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        String name = "";

        if(opr.isDone()){
            GoogleSignInResult result = opr.get();
            name = result.getSignInAccount().getDisplayName();
            email = result.getSignInAccount().getEmail();
            uid = result.getSignInAccount().getId();
        }

        Name.setText(name);
        Email.setText(email);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    private void signOut(){
        if(mGoogleApiClient.isConnected()){
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();

            Intent i = new Intent(Settings.this, SignIn.class);
            Settings.this.startActivity(i);
            Settings.this.finish();
        }

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
                    startActivity(new Intent(Settings.this, HistoryView.class));
                    return true;
                case R.id.navigation_settings:

                    return true;
            }
            return false;
        }
    };

}

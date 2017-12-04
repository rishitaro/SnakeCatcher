package pythonsrus.snakecatcher_refactor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


public class Settings extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{
    private Button SignOut;
    private TextView Name,Email;
    private GoogleApiClient mGoogleApiClient;

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

        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        Name.setText(name);
        Email.setText(email);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();
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

}

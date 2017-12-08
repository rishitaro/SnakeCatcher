package pythonsrus.snakecatcher_refactor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class SignIn extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{

    private LinearLayout Prof_Section;
    private Button SignOut;
    private SignInButton SignIn;
    private TextView Name,Email;
    private String email;
    //private ImageView Prof_pic;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;
    private ProgressDialog mProgressDialog;
    private String TAG = "SignIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Prof_Section = (LinearLayout)findViewById(R.id.prof_section);
        SignOut = (Button)findViewById(R.id.bn_logout);
        SignIn = (SignInButton)findViewById(R.id.bn_login);
        Name = (TextView)findViewById(R.id.name);
        Email = (TextView)findViewById(R.id.email);
        //Prof_pic = (ImageView)findViewById(R.id.prof_pic);
        SignIn.setOnClickListener(this);
        SignOut.setOnClickListener(this);
        Prof_Section.setVisibility(View.GONE);
        SignIn.setVisibility(View.GONE);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

//        String method = getIntent().getStringExtra("method");
//        if(method != null){
//            if(method.equals("signOut")){
//                signOut();
//            }
//        }
    }

    @Override
    public void onStart(){
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.

            GoogleSignInResult result = opr.get();
            handleResult(result);


        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

            showProgressDialog();
            SignIn.setVisibility(View.VISIBLE);
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    SignIn.setVisibility(View.VISIBLE);
                    hideProgressDialog();
                    handleResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }


    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.bn_login:
                signIn();
                break;
            case R.id.bn_logout:
                signOut();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn()
    {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }


    private void signOut()
    {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });

    }



    private void handleResult(GoogleSignInResult result)
    {
        if(result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            String name = account.getDisplayName();
            String id_token = account.getIdToken();
            email = account.getEmail();
            //String imG_url = account.getPhotoUrl().toString();
            Name.setText(name);
            Email.setText(email);
            //Glide.with(this).load(imG_url).into(Prof_pic);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        GMAILSender sender = new GMAILSender("snakecatcherapp@gmail.com", "cmps115struggle");
                        //sender.sendMail("Test mail", "This mail has been sent from android app", "snakecatcherapp@gmail.com", email);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                    }
                }
            }).start();



            Intent i = new Intent(getApplicationContext(), HistoryView.class);

            Log.v(TAG, "name: " + name);
            Log.v(TAG, "email: " + email);
            i.putExtra("email", email);
            i.putExtra("name", name);
            startActivity(i);
            //updateUI(true);
        } else {
            updateUI(false);
        }
    }


    private void updateUI(boolean isLogin)
    {
        if(isLogin) {
//            Prof_Section.setVisibility(View.VISIBLE);
//            SignIn.setVisibility(View.GONE);
            startActivity(new Intent(SignIn.this, HistoryView.class));
        } else {
            Prof_Section.setVisibility(View.GONE);
            SignIn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);

        }
    }
}


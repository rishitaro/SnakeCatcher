package pythonsrus.snakecatcher_refactor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

public class Settings extends AppCompatActivity {
    private Button SignOut;
    private TextView Name,Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SignOut = (Button)findViewById(R.id.bn_logout);
        Name = (TextView)findViewById(R.id.name);
        Email = (TextView)findViewById(R.id.email);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String name = extras.getString("name");
            String email = extras.getString("email");
            Name.setText(name);
            Email.setText(email);
        }

    }
}

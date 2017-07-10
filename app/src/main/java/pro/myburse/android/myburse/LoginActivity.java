package pro.myburse.android.myburse;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by alexey on 10.07.17.
 */

public class LoginActivity extends AppCompatActivity {

    private Button btnFB;
    private Button btnVK;
    private Button btnOK;
    private Button btnEmail;
    private Button btnRegister;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnFB = (Button) findViewById(R.id.btnFB);
        btnFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "Facebook", Toast.LENGTH_SHORT).show();
            }
        });

        btnVK = (Button) findViewById(R.id.btnVK);
        btnVK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "VK", Toast.LENGTH_SHORT).show();
            }
        });

        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        });

        btnEmail = (Button) findViewById(R.id.btnMyBurse);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "MyBurse", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "MyBurse регистрация", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

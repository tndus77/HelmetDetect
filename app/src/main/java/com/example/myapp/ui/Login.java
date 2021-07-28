package com.example.myapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.myapp.MapsActivity;
import com.example.myapp.R;

public class Login extends Activity {
    private EditText login_email, login_password;
    private Button login_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);

        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }
}

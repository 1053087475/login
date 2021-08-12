package com.example.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initview();
    }

    public void initview(){
        Button login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dologin();
            }
        });

    }

    public void dologin(){
        String code_url = GitHubConstants.CODE_URL.replace("CLIENT_ID",GitHubConstants.CLIENT_ID)
                .replace("CALLBACK",GitHubConstants.CALLBACK);
        Uri uri = Uri.parse(code_url);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(i);

    }
}
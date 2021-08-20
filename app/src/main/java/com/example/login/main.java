package com.example.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class main extends AppCompatActivity {
    String code;
    String githubInfoResult;
    private String info;
    Button login;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            System.out.println("正在message!!!");
            super.handleMessage(msg);
            Intent intent = new Intent(main.this,index.class);
            intent.putExtra("githubInfoResult",githubInfoResult);
            startActivity(intent);
        }
    };
    private void saveInfo(String info){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                githubInfoResult=info;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.login);
        initview();

        Intent intent = getIntent();
        String action = intent.getAction();
        if(Intent.ACTION_VIEW.equals(action)){
            login.setText("登陆中……");
            Uri code_uri = intent.getData();
            if(code_uri != null){
                code = code_uri.getQueryParameter("code");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getToken(code);
                        Message message = new Message();
                        handler.sendEmptyMessage(1);
                    }
                }).start();
            }
        }
    }

    public void initview(){


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


    public void getToken(String code){
        String token_url = GitHubConstants.TOKEN_URL.replace("CLIENT_ID", GitHubConstants.CLIENT_ID)
                .replace("CLIENT_SECRET", GitHubConstants.CLIENT_SECRET)
                .replace("CALLBACK", GitHubConstants.CALLBACK)
                .replace("CODE", code);
        System.out.println("用户信息数据"+token_url);//这个里面有我们想要的用户信息数据
        String githubAccessTokenResult = HttpUtil.getAccess_token(token_url);
        System.out.println("==>githubAccessTokenResult: " + githubAccessTokenResult);

        String[] githubResultList = githubAccessTokenResult.split("&");
        List<String> params = new ArrayList<>();
        for (String paramMap : githubResultList) { if (!"scope".equals(paramMap.split("=")[0])){
            // 再以 = 为分割字符分割, 并加到 params 中
            params.add(paramMap.split("=")[1]);
        }
        }
        //此时 params.get(0) 为 access_token;  params.get(1) 为 token_type
        // Step2：通过 access_token 获取用户信息
        String access_token = params.get(0);
        String githubInfoResult = HttpUtil.getUser_info(GitHubConstants.USER_INFO_URL,access_token);

        //给全局变量赋值
        saveInfo(githubInfoResult);
        Map<String, String> userinfo = HttpUtil.jsonToMap(githubInfoResult);
        System.out.println("登录用户信息:"+userinfo);//responseMap里面保存着用户登录信息
        System.out.println("获取登录用户的用户名:"+userinfo.get("login"));

    }
}
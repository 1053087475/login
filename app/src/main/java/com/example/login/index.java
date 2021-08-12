package com.example.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class index extends AppCompatActivity {
    String code;
    private String info;
    TextView show_login,show_name,show_repos_url;
    Map<String, String> userinfo;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            System.out.println("正在message!!!");
            super.handleMessage(msg);
            update(userinfo);
        }
    };
    private void saveInfo(Map<String, String> info){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userinfo=info;
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        show_login = findViewById(R.id.show_login);
        show_name = findViewById(R.id.show_name);
        show_repos_url = findViewById(R.id.show_repos_url);



        Intent intent = getIntent();
        String action = intent.getAction();
        if(Intent.ACTION_VIEW.equals(action)){
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


    public void update(Map<String, String> userinfo){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                show_login.setText(userinfo.get("login"));
                show_name.setText(userinfo.get("name"));
                show_repos_url.setText(userinfo.get("repos_url"));
            }
        });
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

        Map<String, String> userinfo = HttpUtil.jsonToMap(githubInfoResult);
        System.out.println("登录用户信息:"+userinfo);//responseMap里面保存着用户登录信息
        System.out.println("获取登录用户的用户名:"+userinfo.get("login"));
        //给全局变量赋值
        saveInfo(userinfo);
    }
}
package com.example.login;

public class GitHubConstants {
    public static final String CLIENT_ID = "1165a26d174a11d99fc4"; // TODO 修改成自己的
    public static final String CLIENT_SECRET = "d8af3957032ddb2c089c185d56e77ad08a62859d";  // TODO 修改成自己的
    public static final String CALLBACK = "http://oauth/callback";  // TODO 修改成自己的  [注意：callback要和注册的回调路径保持一致  否则登录授权之后会报NullPointerException]

    //获取code的url
    public static final String CODE_URL = "https://github.com/login/oauth/authorize?client_id=CLIENT_ID&redirect_uri=CALLBACK";
    //获取token的url
    public static final String TOKEN_URL = "https://github.com/login/oauth/access_token?client_id=CLIENT_ID&client_secret=CLIENT_SECRET&code=CODE&redirect_uri=CALLBACK";
    //获取用户信息的url
    public static final String USER_INFO_URL = "https://api.github.com/user";
}

package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hancher.contribution.ContributionConfig;
import com.hancher.contribution.ContributionItem;
import com.hancher.contribution.ContributionView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class index extends AppCompatActivity {

    TextView show_login,show_name,show_repos_url;
    Map<String, String> userinfo,contribution;
    ContributionView contributionView;
    private void saveContribution(Map<String, String> map){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contribution=map;
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
        contributionView = findViewById(R.id.contributionView);
        Intent intent = getIntent();
        String githubInfoResult = intent.getStringExtra("githubInfoResult");
        userinfo = HttpUtil.jsonToMap(githubInfoResult);
        update(userinfo);
        //
        initview();


    }

    private void initview(){
        //贡献图点击事件
        contributionView.setOnItemClick(new ContributionView.OnItemClickListener() {
            @Override
            public void onClick(int position, ContributionItem item) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(item.getNumber()).append(" contributions on ").append(transformatDate(item.getTime()));
                Toast.makeText(index.this, stringBuffer, Toast.LENGTH_SHORT).show();
            }
        });
        //获取贡献信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                getContributions();
            }
        }).start();

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

    public void updateC(Date startDate,List<ContributionItem> data){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contributionView.setData(startDate, data);
                contributionView.setConfig(configContribution());
            }
        });
    }

    public void getContributions(){
        try {
            URL url = new URL("https://github.com/users/"+ userinfo.get("login")+"/contributions");
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            InputStream inputStream = httpsURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream,"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            String temp = null,datacount,datadata;
            Map<String,String> map = new HashMap<String,String>();
            while ((temp = bufferedReader.readLine()) != null){
                if(temp.contains("data-count")){
                    datacount = getDatacount(temp);
                    if (!datacount.equals("0")){
                        datadata = getDatadata(temp);
                        map.put(datadata,datacount);
                    }
                }
            }
            bufferedReader.close();
            reader.close();
            inputStream.close();
            saveContribution(map);
            setContributionView();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setContributionView(){
        int day=0,digit=146;
        List<ContributionItem> data = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.DAY_OF_MONTH,-digit);
        while(calendar.get(Calendar.DAY_OF_WEEK)!=1){
            calendar.add(Calendar.DAY_OF_MONTH,-1);
            day++;
        }

        Date startDate = calendar.getTime();
        for (int i = 0; i <= digit + day; i++) {
            String time = getTime(calendar);
            if(contribution.get(time) != null){
                data.add(new ContributionItem(calendar.getTime(),Integer.valueOf(contribution.get(time))));

            }else{
                data.add(new ContributionItem(calendar.getTime(),0));
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        updateC(startDate,data);
    }
    private String getTime(Calendar calendar){
        String MM,dd,time;
        if(calendar.get(Calendar.MONTH)+1 < 10){

            MM = "0" + (calendar.get(Calendar.MONTH) + 1);
        }else{
            MM = (calendar.get(Calendar.MONTH) + 1) + "";
        }
        if(calendar.get(Calendar.DAY_OF_MONTH) < 10){
            dd = "0" + calendar.get(Calendar.DAY_OF_MONTH);
        }else{
            dd = calendar.get(Calendar.DAY_OF_MONTH) + "";
        }
        time = calendar.get(Calendar.YEAR) + "-" + MM + "-" + dd;
        return time;
    }
    public String transformatDate(Date date) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String transformDate=simpleDateFormat.format(date);
        return transformDate;
    }

    private String getDatacount(String line){
        String[] str = line.split("data-count=\"");
        String result = str[1].split("\"")[0];
        return  result;
    }

    private String getDatadata(String line){
        String[] str = line.split("data-date=\"");
        String result = str[1].split("\"")[0];
        return  result;
    }
    private ContributionConfig configContribution(){
        ContributionConfig config = new ContributionConfig()
                .setBorderWidth(2)//边框宽度
                .setBorderColor(0xFF9E9E9E)//边框颜色
                .setItemRound(5)//圆角矩形圆半径
                .setMonths(new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"})//月份字符串
                .setPadding(4)//单个框宽度
                .setRank(new int[]{0,2,5,8,10})//颜色等级范围，大于等于2小于5则为第二个颜色范围
                .setRankColor(new int[]{0xFFEBEDF0, 0xFF9BE9A8, 0xFF40C463, 0xFF30A14E, 0xFF216E39})//填充的等级颜色
                .setWeeks(new String[]{"", "周一", "", "周三", "", "周五", ""})//周名称
                .setStartOfWeek(Calendar.SUNDAY)//配合setWeeks一起使用，可以实现第一行为周日，默认第一行周一
                .setTxtColor(0xFF9E9E9E);//设置文字颜色
        return config;
    }
}
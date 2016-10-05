package com.example.timetable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.*;

public class LoginActivity extends AppCompatActivity {


    public Bitmap bitmap=null;
    public String cookieVal=null;
    public String UserName=null;
    public String PassWord=null;
    public String Code=null;
    public String csrftoken=null;

    public StringBuilder Index_response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //获取验证码的线程
        new MyThread_getCodeImg().start();


        Button But_ChangeCode=(Button)findViewById(R.id.ChangeCode);
        But_ChangeCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取验证码的线程
                new MyThread_getCodeImg().start();
            }
        });



        Button But_Login=(Button)findViewById(R.id.Login);
        But_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入的数据
                EditText EV_UserName = (EditText) findViewById(R.id.Account);
                EditText EV_PassWord = (EditText) findViewById(R.id.Password);
                EditText EV_Code = (EditText) findViewById(R.id.getCode);

                UserName = EV_UserName.getText().toString();
                PassWord = EV_PassWord.getText().toString();
                Code = EV_Code.getText().toString();
                //登陆线程
                new MyThread_Login().start();

            }
        });
    }




    //从CourseActivity返回的时候直接退出
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        switch (requestCode)
        {
            case 1:
                finish();
                break;
        }
    }








    class MyThread_getCodeImg extends Thread{
        public void run(){
            try
            {
                URL url=new URL("http://210.42.121.132//servlet/GenImg");
                HttpURLConnection HUCconn_Img=(HttpURLConnection)url.openConnection();
                //设置头信息
                HUCconn_Img.setConnectTimeout(3000);
                HUCconn_Img.setReadTimeout(3000);
                HUCconn_Img.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
                HUCconn_Img.addRequestProperty("Host","210.42.121.132");
                HUCconn_Img.addRequestProperty("Connection","keep-alive");
                HUCconn_Img.addRequestProperty("Accept","image/webp,*/*;q=0.8");
                HUCconn_Img.addRequestProperty("Accept-Encoding"," gzip,deflate,sdch");
                HUCconn_Img.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");

                HUCconn_Img.connect();

                //获取验证码图片
                InputStream is = HUCconn_Img.getInputStream();

                //保存随验证码发来的COOKIE，以后用
                cookieVal = HUCconn_Img.getHeaderField("Set-Cookie");
                cookieVal=cookieVal.substring(0, cookieVal.length() - 8);
                bitmap = BitmapFactory.decodeStream(is);
                HUCconn_Img.disconnect();

                //更新UI
                runOnUiThread(new Runnable() {
                    public void run() {
                        ImageView imageView = (ImageView) findViewById(R.id.Code);
                        imageView.setImageBitmap(bitmap);

                        //测试使用
                        TextView TV_Cookie = (TextView) findViewById(R.id.Cookie);
                        TV_Cookie.setTextColor(0x00ff00ff);//设置字为透明
                        TV_Cookie.setText(cookieVal);

                    }
                });

            }
            catch(Exception e)
            {
                e.printStackTrace();
                //Log.d("MyThread_getCodeImg","网络超时");
                new MyThread_InterErr().start();
            }
        }
    }


    //登录线程
    class MyThread_Login extends Thread
    {
        public void run()
        {
            try
            {

                URL url=new URL("http://210.42.121.132/servlet/Login");
                HttpURLConnection con=(HttpURLConnection)url.openConnection();

                con.setRequestMethod("POST");
                con.setConnectTimeout(3000);
                con.setReadTimeout(3000);
                //设置头信息
                con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
                con.setRequestProperty("Cookie", cookieVal);
                con.setRequestProperty("Host", "210.42.121.132");
                con.setRequestProperty("Connection", "keep-alive");
                // con.setRequestProperty("Content-Length", "64");  //此处细节有坑
                con.setRequestProperty("Cache-Control", "max-age=0");
                con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                con.setRequestProperty("Origin", "http://210.42.121.132");
                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Referer", "http://210.42.121.132/stu/stu_index.jsp");
                con.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
                con.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");

                con.setDoOutput(true);
                con.setDoInput(true);
                con.connect();

                //生成body
                String Body="id="+UserName+"&pwd="+ MD5.GetMD5Code(PassWord)+"&xdvfb="+Code;
                OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
                out.write(Body);
//              out.write("id=2015301500200&pwd=2e9b71a4cc16e9da6963231807a35b6a&xdvfb=aaaa");
                out.flush();
                InputStream in=con.getInputStream();//此处细节有坑 /(ㄒoㄒ)/~~
//                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
//                response=new StringBuilder();
//                String line;
//                while ((line=reader.readLine())!=null)
//                {
//                    response.append(line);
//                }
                in.close();
                out.close();
                con.disconnect();


                //获取图表式课程表
                URL url_Index=new URL("http://210.42.121.132/stu/stu_index.jsp");
                HttpURLConnection HUCCon_Index= (HttpURLConnection) url_Index.openConnection();
                HUCCon_Index.setConnectTimeout(3000);
                HUCCon_Index.setReadTimeout(3000);
                HUCCon_Index.setRequestProperty("Cookie", cookieVal);
                HUCCon_Index.connect();

                //读取数据
                InputStream inputStream_Index = HUCCon_Index.getInputStream();
                InputStreamReader InputStreamReader_Index = new InputStreamReader(inputStream_Index, "GBK");//添加这一句话设置相应编码格式
                BufferedReader BufferedReader_Index=new BufferedReader(InputStreamReader_Index);


                String line;
                Index_response=new StringBuilder();
                while ((line=BufferedReader_Index.readLine())!=null)
                {
                    Index_response.append(line);
                    Index_response.append("\n");
                }

                BufferedReader_Index.close();
                InputStreamReader_Index.close();
                inputStream_Index.close();
                HUCCon_Index.disconnect();

                //使用正则表达式寻找 课程表列表模式 的链接
                Pattern pattern=Pattern.compile("&csrftoken=.*','calendarRight");
                Matcher matcher=pattern.matcher(Index_response.toString());
                if( matcher.find() )
                {
                    csrftoken=matcher.group();
                }



                runOnUiThread(new Runnable() {
                    public void run() {

                        TextView TV_Cookie = (TextView) findViewById(R.id.Cookie);

                        //利用csrftoken判断登录情况
                        if(csrftoken==null)//登录失败
                        {
                            Toast.makeText(LoginActivity.this,"登录信息有误，请重新登陆。",Toast.LENGTH_LONG).show();
                            //换验证码
                            new MyThread_getCodeImg().start();
                            //清除输入
                            EditText UserName=(EditText)findViewById(R.id.Account);
                            EditText PassWord=(EditText)findViewById(R.id.Password);
                            EditText Code=(EditText)findViewById(R.id.getCode);
                            UserName.setText(null);
                            PassWord.setText(null);
                            Code.setText(null);
                        }else//登陆成功  开新活动
                        {
                            csrftoken=csrftoken.substring(1,47);
                            TV_Cookie.setText(csrftoken);
                            Intent intent=new Intent(LoginActivity.this,ShowCourseMessage.class);
                            intent.putExtra("csrftoken",csrftoken);
                            intent.putExtra("Cookie",cookieVal);
                            startActivityForResult(intent, 1);
                        }
                    }
                });

            }catch(Exception e)
            {
                e.printStackTrace();
                new MyThread_InterErr().start();
            }
        }
    }


    //网络环境出错提醒
    class MyThread_InterErr extends Thread
    {
        public void run()
        {
            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    Toast.makeText(LoginActivity.this,"网络环境较差，请确认网络连接情况或过一会再登录。",Toast.LENGTH_LONG).show();
                }
            });
        }
    }



    }























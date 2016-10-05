package com.example.timetable;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowCourseMessage extends Activity {

    public static List<Map<String,String>> CourseMessage;
    public String csrftoken=null;
    public String Cookie=null;
    public StringBuilder SB_CourseMessage;

    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_course_message);


        Intent Intent_get=getIntent();
        csrftoken=Intent_get.getStringExtra("csrftoken");
        Cookie=Intent_get.getStringExtra("Cookie");



        MyThread_DownCourageMes downData= new MyThread_DownCourageMes();
        downData.start();
        try
        {
            downData.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        CourseMessage=CourseMessageAnalysis.getCourseMessage(SB_CourseMessage.toString());


        for(int i=0;i< CourseMessage.size();i++)
        {
            System.out.println( CourseMessage.get(i));
        }






        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new MyAdapter());



    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        //private List<String> data;

//        public MyAdapter(List<String> data) {
//            this.data = data;
//        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            // 加载Item的布局.布局中用到的真正的CardView.
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
            // ViewHolder参数一定要是Item的Root节点.
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.TitleName.setText(CourseMessageAnalysis.CourseMesHead[1]);
            viewHolder.Name.setText(CourseMessage.get(i).get(CourseMessageAnalysis.CourseMesHead[1]));
            viewHolder.TitleTime.setText(CourseMessageAnalysis.CourseMesHead[9]);
            viewHolder.Time.setText(CourseMessage.get(i).get(CourseMessageAnalysis.CourseMesHead[9]));
            viewHolder.TitlePlace.setText(CourseMessageAnalysis.CourseMesHead[10]);
            viewHolder.Place.setText(CourseMessage.get(i).get(CourseMessageAnalysis.CourseMesHead[10]));
        }

        @Override
        public int getItemCount() {
            return CourseMessage.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView TitleName;
            TextView Name;
            TextView TitleTime;
            TextView Time;
            TextView TitlePlace;
            TextView Place;

            public ViewHolder(View itemView) {
                // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
                super(itemView);
                TitleName = (TextView) itemView.findViewById(R.id.CourseNameTitle);
                Name = (TextView) itemView.findViewById(R.id.CourseName);
                TitleTime = (TextView) itemView.findViewById(R.id.CourseTimeTitle);
                Time = (TextView) itemView.findViewById(R.id.CourseTime);
                TitlePlace = (TextView) itemView.findViewById(R.id.CoursePlaceTitle);
                Place = (TextView) itemView.findViewById(R.id.CoursePlace);
            }
        }
    }

    class MyThread_DownCourageMes extends Thread{
        public void run(){
            try{

                //链接字符串
                String Str_url=csrftoken+"&action=normalLsn&year=2016&term=%C9%CF&state=";
                Str_url="http://210.42.121.132/servlet/Svlt_QueryStuLsn?"+Str_url;
                URL url=new URL(Str_url);
                HttpURLConnection con=(HttpURLConnection)url.openConnection();

                //头信息
                con.setRequestProperty("Host","210.42.121.132");
                con.setRequestProperty("Connection","keep-alive");
                con.setRequestProperty("Accept",": text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                con.setRequestProperty("User-Agent",": Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
                con.setRequestProperty("Accept-Encoding","deflate,sdch");
                con.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
                con.setRequestProperty("Cookie", Cookie);
                con.connect();

                //拿数据
                SB_CourseMessage=new StringBuilder();
                String part;
                DataInputStream DataIn_Coruse= new DataInputStream(con.getInputStream());
                byte[] buffer_Coruse = new byte[1024];
                int length_buffer_Coruse;
                while((length_buffer_Coruse = DataIn_Coruse.read(buffer_Coruse))>0)
                {
                    part=new String(buffer_Coruse, "GB2312");
                    //Log.d("Course",part);
                    SB_CourseMessage.append(part);
                }


//                runOnUiThread(new Runnable(){
//                    public void run(){
//                        CourseMessage=CourseMessageAnalysis.getCourseMessage(SB_CourseMessage.toString());
//
//                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
////                        recyclerView.setHasFixedSize(true);
////                        layoutManager = new LinearLayoutManager(ShowCourseMessage.this);
////                        recyclerView.setLayoutManager(layoutManager);
//                        recyclerView.setAdapter(new MyAdapter());
//
//
//                    }
//                });



            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }





}

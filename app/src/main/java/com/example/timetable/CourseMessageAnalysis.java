package com.example.timetable;
import java.util.*;
import java.util.regex.*;

public class CourseMessageAnalysis {

    //一共12条课头信息
    public static String[] CourseMesHead={"课头号","课  程  名","课程类型","学习类型"
            ,"授课学院","教师","专业","学分","学时","上课时间","备        注","状态"};//0--11
    //总容器
    public static List<Map<String,String>> CourseMessage=new ArrayList<>();


    public static String getStrNoBlank(String StrWithBlank)
    {
        Pattern patternMes=Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = patternMes.matcher(StrWithBlank);
        return m.replaceAll("");
    }

    public static Map<String,String> MapDeepCopy(Map<String,String> s)
    {
        Map<String,String> newMap=new HashMap<>();

        for(Map.Entry<String, String> entry: s.entrySet())
        {
            String key=entry.getKey();
            String value=entry.getValue();

            newMap.put(key, value);
        }
        return newMap;
    }


    public static List<Map<String,String>> getCourseMessage(String HTMLData)
    {
        //除去空行和换行符
        String dataNoBlank=getStrNoBlank(HTMLData);
        //找出每一门课程的html
        Pattern pfindCourse=Pattern.compile("<tr>.*?</tr>");
        Matcher mfindCourse = pfindCourse.matcher(dataNoBlank);

        //记录有多少个匹配项 在匹配项除了第一个都是课程信息，第一个是课头信息
        int numOfCourse=0;
        while(mfindCourse.find())
        {
            numOfCourse++;
            if(numOfCourse==1)continue;//第一个课头信息不处理

            //存放一门课程的信息
            Map<String,String> saveOneCourse=new HashMap<>();

            //存放一门课程的html
            String singleCourse=mfindCourse.group();
            singleCourse=singleCourse.substring(4, singleCourse.length()-8);
            System.out.printf("\n\n第%d个课程的html信息：         %s\n（删去头尾）\n",numOfCourse-1,singleCourse);

            //找出singleCourse里的每一项信息
            Pattern pfindCourseBody=Pattern.compile("<td.*?</td>");
            Matcher mfindCourseBody = pfindCourseBody.matcher(singleCourse);


            //“每一项信息”  项数       范围是[1,13]  13  最后一个是无用信息
            int numOfCourseBodyFind=0;
            while(mfindCourseBody.find())
            {
                numOfCourseBodyFind++;
//				System.out.println();
//				System.out.println(numOfCourseBodyFind);

                if(numOfCourseBodyFind<=12)//
                {
                    String CourseBody=mfindCourseBody.group();
                    //System.out.printf("\n%d:%s\n",numOfCourseBodyFind-1,CourseBody);


                    //找到形如  >message<  的东西
                    Pattern pfindFinalCourseMes=Pattern.compile(">.+?<");
                    Matcher mfindFinalCourseMes = pfindFinalCourseMes.matcher(CourseBody);

                    String finalCourseMes=null;
                    if(mfindFinalCourseMes.find())
                    {
                        finalCourseMes=mfindFinalCourseMes.group();
                        //System.out.println(finalCourseMes);
                        finalCourseMes=finalCourseMes.substring(1, finalCourseMes.length()-1);
                        //System.out.println(finalCourseMes);



                        //当上课时间未定时的处理
                        if(numOfCourseBodyFind==10 && finalCourseMes.contains("div"))
                        {
                            finalCourseMes=null;
                        }
                        saveOneCourse.put(CourseMesHead[numOfCourseBodyFind-1], finalCourseMes);


                    }



                }

            }

            CourseMessage.add(saveOneCourse);
        }

        return CourseMessage;
    }

}
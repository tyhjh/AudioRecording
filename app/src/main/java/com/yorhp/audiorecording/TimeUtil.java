package com.yorhp.audiorecording;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeUtil {


    //时间描述
    public static String getTimeTxt(long time) {
        Date date = new Date(time);
        SimpleDateFormat daydf = new SimpleDateFormat("MM月dd日");
        SimpleDateFormat hourDf = new SimpleDateFormat("HH时mm分");
        SimpleDateFormat yeardf = new SimpleDateFormat("yyyy年");

        long minute = (System.currentTimeMillis() - time) / (1000 * 60);
        long hour = (System.currentTimeMillis() - time) / (1000 * 3600);
        String day = daydf.format(date);
        String year = yeardf.format(date);
        String hourString = hourDf.format(date);

        switch ((int) hour) {
            case 0:
                if (minute < 1) {
                    return "刚刚";
                } else {
                    return minute + "分钟前";
                }
            case 1:
                return "一个小时前";
            case 2:
                return "两个小时前";
            default:
                break;
        }


        if (year.equals(getNowYearTime())) {
            if (day.equals(getNowTime())) {
                return "今天" + hourString;
            } else if (day.equals(getLastTime())) {
                return "昨天" + hourString;
            }
            return day + hourString;
        } else {
            return year + day + hourString;
        }


    }


    public static boolean isToday(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

    public static String getTimeC(Long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        String str = format.format(date);
        return str;
    }

    public static String getTimeE(Long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }


    public static String getTimeSpend(long startTime, long endTime) {
        String timeSt = "";
        long spend = endTime - startTime;


        int year = (int) (spend / (1000 * 60 * 60 * 24 * 365));
        if (year > 0)
            timeSt = timeSt + year + "年";
        int month = (int) ((spend % (1000 * 60 * 60 * 24 * 365)) / (1000 * 60 * 60 * 24 * 30));
        if (timeSt.length() > 0 || month > 0) {
            timeSt = timeSt + month + "月";
        }
        int day = (int) ((spend % (1000 * 60 * 60 * 24 * 365)) % (1000 * 60 * 60 * 24 * 30) / (1000 * 60 * 60 * 24));
        if (timeSt.length() > 0 || day > 0) {
            timeSt = timeSt + day + "天";
        }
        int hour = (int) ((spend % (1000 * 60 * 60 * 24 * 365)) % (1000 * 60 * 60 * 24 * 30) % (1000 * 60 * 60 * 24) / (1000 * 60 * 60));
        if (timeSt.length() > 0 || hour > 0) {
            timeSt = timeSt + hour + "时";
        }
        int minute = (int) ((spend % (1000 * 60 * 60 * 24 * 365)) % (1000 * 60 * 60 * 24 * 30) % (1000 * 60 * 60 * 24) % (1000 * 60 * 60) / (1000 * 60));
        if (timeSt.length() > 0 || minute > 0) {
            timeSt = timeSt + minute + "分";
        }
        int second = (int) ((spend % (1000 * 60 * 60 * 24 * 365)) % (1000 * 60 * 60 * 24 * 30) % (1000 * 60 * 60 * 24) % (1000 * 60 * 60) % (1000 * 60) / 1000);
        if (timeSt.length() > 0 || second > 0) {
            timeSt = timeSt + second + "秒";
        }
        return timeSt;
    }

    //获取当年时间
    public static String getNowYearTime() {
        String time;
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy年");
        time = df.format(now);
        return time;
    }

    //获取当天时间
    public static String getNowTime() {
        String time;
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
        time = df.format(now);
        return time;
    }

    //明天的时间
    public static String getLastTime() {
        String time;
        Date next = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(next);
        calendar.add(calendar.DATE, -1);//把日期往后增加一天.整数往后推,负数往前移动
        next = calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
        time = df.format(next);
        return time;
    }

}

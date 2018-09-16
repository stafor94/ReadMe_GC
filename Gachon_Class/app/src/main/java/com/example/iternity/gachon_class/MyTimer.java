package com.example.iternity.gachon_class;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyTimer {
    // 요일을 리턴해주는 메소드
    public String getDayOfWeek() {
        Calendar cal = Calendar.getInstance();

        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (nWeek == 1) return "일";
        else if (nWeek == 2) return "월";
        else if (nWeek == 3) return "화";
        else if (nWeek == 4) return "수";
        else if (nWeek == 5) return "목";
        else if (nWeek == 6) return "금";
        else return "토";
    }

    // 챗봇에서 입력받은 요일을 리턴해주는 메소드
    public String getChatDow(String day) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(day);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (nWeek == 1) return "일";
        else if (nWeek == 2) return "월";
        else if (nWeek == 3) return "화";
        else if (nWeek == 4) return "수";
        else if (nWeek == 5) return "목";
        else if (nWeek == 6) return "금";
        else return "토";
    }

    // 현재 시간을 int형으로 리턴해주는 메소드
    public int getCurrentTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
        String formatDate = sdfNow.format(date);
        String[] times = formatDate.split(":");
        int convertTime = (Integer.parseInt(times[0]) * 100) + Integer.parseInt(times[1]);  // int형 데이터 HHmm으로 환산

        return convertTime;
    }

    // 챗봇에서 입력받은 시간을 int형으로 리턴해주는 메소드
    public int getChatTime(String time) {
        String[] times = time.split(":");
        int chatTime = (Integer.parseInt(times[0]) * 100) + Integer.parseInt(times[1]);

        return chatTime;
    }

    // 입력된 int형 시간이 해당하는 시간을 리턴해주는 메소드
    public String matchTime(String sTime) {
        int time = getChatTime(sTime);
        String result = "";

        if (time >= 900 && time < 1000) result += "1/";
        if (time >= 930 && time < 1100) result += "A/";
        if (time >= 1000 && time < 1100) result += "2/";
        if (time >= 1100 && time < 1200) result += "3/";
        if (time >= 1100 && time < 1230) result += "B/";
        if (time >= 1200 && time < 1300) result += "4/";
        if (time >= 1230 && time < 1400) result += "C/";
        if (time >= 1300 && time < 1400) result += "5/";
        if (time >= 1400 && time < 1500) result += "6/";
        if (time >= 1400 && time < 1530) result += "D/";
        if (time >= 1500 && time < 1600) result += "7/";
        if (time >= 1530 && time < 1700) result += "E/";
        if (time >= 1600 && time < 1700) result += "8";
        if (time >= 1730 && time < 1825) result += "9";
        if (time >= 1825 && time < 1920) result += "10";
        if (time >= 1920 && time < 2015) result += "11";
        if (time >= 2015 && time < 2110) result += "12";
        if (time >= 2110 && time < 2205) result += "13";
        if (time >= 2205 && time < 2255) result += "14";

        if (result.equals("")) {    // 위 시간표에 해당하지 않으면
            result += "x";
        }

        if (result.length() > 0 && result.charAt(result.length() - 1) == '/') {    // 마지막이 '/'로 끝나면
            result = result.substring(0, result.length() - 1);  // '/'를 제거한다
        }

        return result;
    }

    // 수업 시작시간과 종료시간을 환산해주는 메소드
    public int[] convertTime(String time) {
        String[] times = time.split(" ");   // 공백으로 각 시간을 나눔
        String[] eTimes = {times[0].substring(1), times[times.length - 1].substring(1)};    // 시작시간, 종료시간
        int[] cTimes = {getStartTime(eTimes[0]), getEndTime(eTimes[1])};

        return cTimes;
    }

    // 시작시간 환산
    public int getStartTime(String time) {
        switch (time) {
            case "1": return 900;
            case "2": return 1000;
            case "3": return 1100;
            case "4": return 1200;
            case "5": return 1300;
            case "6": return 1400;
            case "7": return 1500;
            case "8": return 1600;
            case "9": return 1730;
            case "10": return 1825;
            case "11": return 1920;
            case "12": return 2015;
            case "13": return 2110;
            case "14": return 2205;
            case "A": return 930;
            case "B": return 1100;
            case "C": return 1230;
            case "D": return 1400;
            case "E": return 1530;
            default: return 0;
        }
    }

    // 종료시간 환산
    public int getEndTime(String time) {
        switch (time) {
            case "1": return 1000;
            case "2": return 1100;
            case "3": return 1200;
            case "4": return 1300;
            case "5": return 1400;
            case "6": return 1500;
            case "7": return 1600;
            case "8": return 1700;
            case "9": return 1825;
            case "10": return 1920;
            case "11": return 2015;
            case "12": return 2110;
            case "13": return 2205;
            case "14": return 2255;
            case "A": return 1100;
            case "B": return 1230;
            case "C": return 1400;
            case "D": return 1530;
            case "E": return 1700;
            default: return 0;
        }
    }
}

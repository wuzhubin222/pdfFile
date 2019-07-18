package utils;

import excption.BusinessException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static Date getFormatDate(String format,String date){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date resDate;
        try {
            resDate = sdf.parse(date);
        } catch (ParseException e) {
            throw new BusinessException(3046,"日期转换失败:"+e.toString());
        }
        return resDate;
    }

    public static String getFormatString(String format,Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static void main(String[] args) throws ParseException {
        String s = "2017年09月23日";
        Date date = DateUtils.getFormatDate("yyyy年MM月dd日", s);
        System.out.println(date.toString()+date);


        SimpleDateFormat dateFrom=new SimpleDateFormat("yyyy-MM-dd");
        Date dates=new Date();

        System.out.println(dateFrom.format(dates));

        System.out.println(dateFrom.parse(dateFrom.format(dates)));


    }

}

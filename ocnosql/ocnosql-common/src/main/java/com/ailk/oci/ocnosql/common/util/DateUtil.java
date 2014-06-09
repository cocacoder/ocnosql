package com.ailk.oci.ocnosql.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.*;
import org.junit.*;


import java.text.*;
import java.util.*;

public final class DateUtil {

	private final static String format_1 = "yyyy-MM-dd HH:mm:ss";
	private final static String format_2 = "yyyy/MM/dd HH:mm:ss";
	
	private final static String format_3 = "yyyy-MM-dd";
	private final static String format_4 = "yyyy/MM/dd";
	private final static String format_5 = "yyyy.MM.dd HH:mm:ss";
	private final static String format_6 = "yyyy.MM.dd";
	/**
	 * 将String类型日期转化成java.utl.Date类型"2003-01-01"格式
	 * 
	 * @param str
	 *            String 要格式化的字符串
	 * @param format
	 *            String
	 * @return Date
	 */
	public static Date stringToUtilDate(String str, String format) {
		Assert.assertNotNull(str, "param [str] not be null");
		Assert.assertNotNull(str,"param [format] not be null");
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(str);
		} 
		catch (Exception e) {
		}
		return date;
	}

	/**
	 * 获得间隔时间
	 * 
	 * @param starttime
	 * @param endtime
	 * @param formatDate
	 * @return
	 */
	public static List<String> getIntervalTime(Date starttime, Date endtime, String formatDate) {
		List<String> result = new ArrayList<String>();

		SimpleDateFormat format = new SimpleDateFormat(formatDate);

		Calendar start = new GregorianCalendar();
		start.setTime(starttime);
		Calendar end = new GregorianCalendar();
		end.setTime(endtime);
		int endDay = end.get(Calendar.DAY_OF_YEAR);
       
		if (start.get(Calendar.DAY_OF_YEAR) != endDay) {

			start = (Calendar) start.clone();
			do {
				String timestamp = format.format(start.getTime());
				String tableName = timestamp;
				result.add(tableName);
				start.add(Calendar.DATE, 1);
			} 
			while (start.get(Calendar.DAY_OF_YEAR) != endDay);
		}
		return result;
	}

	public static Date getNextDate(Date date) {
		Assert.assertNotNull("The date must not be null", date);
		return DateUtils.addDays(date, 1);
	}

	/**
	 * 返回当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTimestamp() {
		return getCurrentTimestamp(format_1);
	}
	public static String getCurrentTimestamp(String format) {
		return new SimpleDateFormat(format).format(Calendar.getInstance()
				.getTime());
	}
	public static String getCurrentMonth() {
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		month = month + 1;
		String strMonth = null;
		if (month < 10) {
			strMonth = "0" + month;
		} 
		else {
			strMonth = String.valueOf(month);
		}
		return year + strMonth;
	}

	/**
	 * @return 返回上一个月月份，格式为 yyyymm
	 */
	public static String getLastMonth() {
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		if (month == 0) {
			month = 12;
			year--;
		}
		String strMonth = null;
		if (month < 10) {
			strMonth = "0" + month;
		} else {
			strMonth = String.valueOf(month);
		}
		return year + strMonth;
	}
	/**
	 * @return 返回上一个月月份，格式为 yyyymm
	 */
	public static String getLastMonth(String time,String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(stringToUtilDate(time,format));
		lastDate.add(Calendar.MONTH, -1);
		return sdf.format(lastDate.getTime());
	}
	/**
	 * @return 返回上一个月月份，格式为 yyyymm
	 */
	public static String getNextMonth(String time,String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(stringToUtilDate(time,format));
		lastDate.add(Calendar.MONTH, 1);
		return sdf.format(lastDate.getTime());
	}
    /**
     * 格式化日期
     * @param parsetime
     * @param format 传入的日期格式，如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String parseDate(String parsetime,String format){
    	if(parsetime==null){
    		return null;
    	}
    	int limit = parsetime.length();
    	int i = 0;
    	int cursor=0;
    	char c= 0;
    	String retTime = "";
    	char formatDay = 0;
    	char formatTime = 0;
    	if(format.equals(format_1)||format.equals(format_3)){
    		formatDay='-';
    		formatTime = ':';
    	}
    	else if(format.equals(format_2)||format.equals(format_4)){
    		formatDay='/';
    		formatTime = ':';
    	}
    	else if(format.equals(format_5)||format.equals(format_6)){
    		formatDay='.';
    		formatTime = ':';
    	}
        while (cursor < limit) {
            if(i==4||i==7){
            	retTime+=formatDay;
            }
            else if(i==10){
            	retTime+=" ";
            }
            else if(i==13||i==16){
            	retTime+=formatTime;
            }
            else{
            	 c = parsetime.charAt(cursor);
            	 retTime+=c;
            	 cursor++;
            }
            i++;
        }
    	return retTime;
    }
    public static String parse(String parsetime,String oriFormat,String toFormat){
    	return dateToString(stringToUtilDate(parsetime,oriFormat),toFormat);
    }
    /**
	 * 将Date类型日期转化成String类型"任意"格式
	 * java.sql.Date,java.sql.Timestamp类型是java.util.Date类型的子类
	 * 
	 * @param date
	 *            Date
	 * @param format
	 *            String "2003-01-01"格式 "yyyy年M月d日" "yyyy-MM-dd HH:mm:ss"格式
	 * @return String
	 */
	public static String dateToString(Date date, String format) {
		if (date == null || format == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String str = sdf.format(date);
		return str;
	}

	/**
	 * 获取当月最后一天
	 * @param format 日期格式
	 * @return
	 */
	public static String getLastDayOfMonth(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号
		lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天
		return sdf.format(lastDate.getTime());
	}
	/**
	 * 获取传入日期所属月份的最后一天。
	 * @param time 传入日期
	 * @param format 日期格式
	 * @return
	 */
	public static String getLastDayOfMonth(String time,String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(stringToUtilDate(time,format));
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号
		lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天
		return sdf.format(lastDate.getTime());
	}
    /**
     * 判断sCompareDate时间是否在toCompareDate时间之后
     * @param sCompareDate
     * @param toCompareDate
     * @param dateFormat
     * @return
     */
    public static boolean after(String sCompareDate,String toCompareDate,String dateFormat){
    	Date sourceDate = stringToUtilDate(sCompareDate,dateFormat);
    	Date toCompare = stringToUtilDate(toCompareDate,dateFormat);
    	return sourceDate.after(toCompare);
    }
    public static String getLastDayOfMonth() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号
		lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天
		return sdf.format(lastDate.getTime());
	}
    public static String addTime(String time,String dateFormat,String addValue,String addType) {
    	Date date = stringToUtilDate(time,dateFormat);
    	if(addType.equals("sec")){
    		date = DateUtils.addSeconds(date, Integer.parseInt(addValue));
    	}
    	else if(addType.equals("min")){
    		date = DateUtils.addMinutes(date, Integer.parseInt(addValue));
    	}
    	else if(addType.equals("day")){
    		date = DateUtils.addDays(date, Integer.parseInt(addValue));
    	}
    	
    	return dateToString(date,dateFormat);
    }
    public static boolean checkContinuousTimeViaSec(String startTime,String endTime,String timeFormat,String continuousTime){
    	String toCheckEndTime  = addTime(startTime,timeFormat,continuousTime,"sec");
    	return StringUtils.equals(toCheckEndTime, endTime);
    }
    public static long calculateTimedifference(String startTimeString,String endTimeString){
    	Date start = stringToUtilDate(startTimeString,"yyyyMMddhhmmss");
    	Date end = stringToUtilDate(endTimeString,"yyyyMMddhhmmss");
    	long timedifference = end.getTime()-start.getTime();
    	return timedifference/1000;
    }
    public static boolean validateDateFormat(String dateStr,String format){
    	Assert.assertNotNull("param [dateStr] not be null",dateStr);
		Assert.assertNotNull("param [format] not be null",format);
        if(dateStr.length()!=format.length()){
        	return false;
        }
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			sdf.setLenient(false);
			sdf.parse(dateStr);
		} 
		catch (Exception e) {
			return false;
		}   
		return true;
    }
}

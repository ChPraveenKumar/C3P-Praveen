package com.techm.orion.utility;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class WAFADateUtil {
	private static final Logger logger = LogManager.getLogger(WAFADateUtil.class);

	public static boolean isOnSameDay(Timestamp... dates) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		String date1 = fmt.format(dates[0]);
		for (Timestamp date : dates) {
			if (!fmt.format(date).equals(date1)) {
				return false;
			}
		}
		return true;
	}

	public static String dbToUI(Timestamp dateProcessed) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date newDateProcessed = format.parse(dateProcessed.toString());
			format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
			return format.format(newDateProcessed);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean inBetweenTime(String mainDateString, String time1String, String time2String) {
		try {
			Date mainDate = new SimpleDateFormat("HH:mm").parse(mainDateString);
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(mainDate);

			Date time1 = null;
			if (WAFADateUtil.isValidFormat("HH:mm:ss", time1String))
				time1 = new SimpleDateFormat("HH:mm:ss").parse(time1String);
			else
				time1 = new SimpleDateFormat("HH:mm").parse(time1String);

			// Date time1 = new SimpleDateFormat("HH:mm:ss").parse(time1String);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(time1);
			// calendar2.add(Calendar.DATE, 1);

			Date time2 = null;
			if (WAFADateUtil.isValidFormat("HH:mm:ss", time2String))
				time2 = new SimpleDateFormat("HH:mm:ss").parse(time2String);
			else
				time2 = new SimpleDateFormat("HH:mm").parse(time2String);

			Calendar calendar3 = Calendar.getInstance();
			calendar3.setTime(time2);
			// calendar3.add(Calendar.DATE, 1);

			Date x = calendar1.getTime();
			if (x.after(calendar2.getTime()) && x.before(calendar3.getTime())) {
				// checkes whether the current time is between 14:49:00 and 20:11:13.
				logger.info(true);
				return true;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean beforeTime(String mainDateString, String time1String) {
		try {
			Date mainDate = new SimpleDateFormat("HH:mm").parse(mainDateString);
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(mainDate);

			Date time1 = null;
			if (WAFADateUtil.isValidFormat("HH:mm:ss", time1String))
				time1 = new SimpleDateFormat("HH:mm:ss").parse(time1String);
			else
				time1 = new SimpleDateFormat("HH:mm").parse(time1String);

			// Date time1 = new SimpleDateFormat("HH:mm:ss").parse(time1String);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(time1);
			// calendar2.add(Calendar.DATE, 1);

			Date x = calendar1.getTime();
			if (x.before(calendar2.getTime())) {
				// checkes whether the current time is between 14:49:00 and 20:11:13.
				logger.info(true);
				return true;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean afterTime(String mainDateString, String time1String) {
		try {
			Date mainDate = new SimpleDateFormat("HH:mm").parse(mainDateString);
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(mainDate);

			Date time1 = null;
			if (WAFADateUtil.isValidFormat("HH:mm:ss", time1String))
				time1 = new SimpleDateFormat("HH:mm:ss").parse(time1String);
			else
				time1 = new SimpleDateFormat("HH:mm").parse(time1String);

			// Date time1 = new SimpleDateFormat("HH:mm:ss").parse(time1String);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(time1);
			// calendar2.add(Calendar.DATE, 1);

			Date x = calendar1.getTime();
			if (x.after(calendar2.getTime())) {
				// checkes whether the current time is between 14:49:00 and 20:11:13.
				logger.info(true);
				return true;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isValidFormat(String format, String value) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(value);
			if (!value.equals(sdf.format(date))) {
				date = null;
			}
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return date != null;
	}

	public String currentDateTime()
	{
		String response=null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss");
		LocalDateTime datetime = LocalDateTime.now();
		response = datetime.format(formatter);
		return response;
		
	}
	
	public String currentDateTimeFromUserTimeZoneToServerTimzeZone(String timezone)
	{
		String response=null;
		// Current date and time using now()
		ZonedDateTime currentDateTime = ZonedDateTime.now();
		ZoneId applicationServerZoneId = ZoneId.of(TSALabels.C3P_APPLICATION_SERVER_TIMEZONE.getValue());
		ZoneId clientZoneID = ZoneId.of(timezone);
		ZonedDateTime clientDateTime = currentDateTime.withZoneSameInstant(clientZoneID);
		ZonedDateTime applicationDateTime = clientDateTime.withZoneSameInstant(applicationServerZoneId);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss");
		response = applicationDateTime.format(formatter);
		return response;
	}
	
	public String dateTimeInAppFormat(String time)
	{
		String response=null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss");
		LocalDateTime dateTime= Timestamp.valueOf(time).toLocalDateTime();
		//LocalDateTime dateTime = LocalDateTime.parse(time);
		response = dateTime.format(formatter);
		return response;
	}
	
	public Timestamp convertStringToTimeStamp(String time)
	{
		Timestamp timestamp=null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
		    Date parsedDate = dateFormat.parse(time);
		     timestamp = new java.sql.Timestamp(parsedDate.getTime());;
		   
		} catch (ParseException e) {
		    e.printStackTrace();
		}
		return timestamp;
	}
	public String convertTimeStampfromDBToParsableDate(String timestampin)
	{
		String response=null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		Date parsedDate;
		try {
		parsedDate = dateFormat.parse(timestampin);
		Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		Date parsedDate1 = dateFormat1.parse(timestamp.toString());
		Timestamp timestamp1 = new java.sql.Timestamp(parsedDate1.getTime());
		response= timestamp1.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
}

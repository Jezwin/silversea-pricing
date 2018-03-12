package com.silversea.aem.helper;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;

/**
 * Doc for format date
 * https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
 */
public class DateHelper extends WCMUsePojo {

	public String value;

	private Calendar calendar = Calendar.getInstance();

	@Override
	public void activate() throws Exception {
		Calendar date = get("date", Calendar.class);
		String format = get("format", String.class);
		String month = get("month", String.class);
		String localeParam = get("locale", String.class);
		String time = get("time", String.class);

		Locale locale = StringUtils.isNotBlank(localeParam) ? new Locale(localeParam)
				: getCurrentPage().getLanguage(false);
		SimpleDateFormat formatter;

		if (date != null && format != null) {
			formatter = new SimpleDateFormat(format, locale);
			formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
			value = formatter.format(date.getTime());
		}

		if (month != null) {
			DateFormatSymbols symbols = new DateFormatSymbols(locale);
			String[] monthNames = symbols.getMonths();
			value = monthNames[Integer.parseInt(month) - 1];
		}

		value = convertTime(time, locale.getLanguage());
	}

	private String convertTime(String time, String language) {
		String result = null;
		if (time != null && !time.equalsIgnoreCase("")) {
			if (language.equalsIgnoreCase("en")) {
				String closingTime = time.replace(":", "");
				calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(closingTime.substring(0, 2)));
				// For display purposes only We could just return the last two substring or
				// format Calender.MINUTE as shown below
				calendar.set(Calendar.MINUTE, Integer.parseInt(closingTime.substring(2, 4)));
				int hour = calendar.get(Calendar.HOUR);
				String minute = String.format("%02d", calendar.get(Calendar.MINUTE));

				String AM_PM = calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM";
				// workournd from 00:00pm to 12:00pm
				if (hour == 00 && AM_PM.equalsIgnoreCase("PM")) {
					hour = 12;
				}
				result = hour + ":" + minute + " " + AM_PM;
			} else {
				result = time;
			}
		}
		return result;
	}

	public String getValue() {
		return value;
	}

	public static void main(String args[]) {
		DateHelper dateHelper = new DateHelper();
		String value = dateHelper.convertTime("12:00", "en");
		System.out.println(value);
		value = dateHelper.convertTime("13:00", "fr");
		System.out.println(value);
		value = dateHelper.convertTime("13:00", "en");
		System.out.println(value);
	}
}
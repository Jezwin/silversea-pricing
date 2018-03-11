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
	
	private Calendar calendar  = Calendar.getInstance();

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

		if (time != null && !time.equalsIgnoreCase("")) { 
			if (locale.getLanguage().equalsIgnoreCase("en")) {
				String closingTime = time.replace(":", "");
				calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(closingTime.substring(0, 2)));
				// For display purposes only We could just return the last two substring or format Calender.MINUTE as shown below
				calendar.set(Calendar.MINUTE, Integer.parseInt(closingTime.substring(2, 4)));
				
				String minute = String.format("%02d", calendar.get(Calendar.MINUTE));

				String AM_PM = calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM";
				//workournd from 00:00pm to 12:00pm 
				if (AM_PM.) {
					
				}
				value = calendar.get(Calendar.HOUR) + ":" + minute + " " + AM_PM;
			} else {
				value = time;
			}
		}
	}

	public String getValue() {
		return value;
	}
}
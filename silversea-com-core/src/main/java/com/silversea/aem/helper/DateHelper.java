package com.silversea.aem.helper;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

		if (time != null) {
			if (locale.getLanguage().equalsIgnoreCase("en")) {
				formatter = new SimpleDateFormat("HH:mm");
				try {
					Date dateParse = formatter.parse(time);
					formatter = new SimpleDateFormat("HH:mm aa");
					value = formatter.format(dateParse);
				} catch (ParseException e) {
					value = time;
				}
			}
		}
	}

	public String getValue() {
		return value;
	}
}
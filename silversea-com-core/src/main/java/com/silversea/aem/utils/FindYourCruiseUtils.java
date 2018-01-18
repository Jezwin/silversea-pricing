package com.silversea.aem.utils;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class FindYourCruiseUtils {
	

	/**
	 * Conversion to fix problem with time zone (Find your cruise departure date
	 * issue) Add the timeZone to the startDate: 2017-11-30T19:00:00.000-05:00
	 * in crx/de startDate from API: 2017-12-01T00:00:00.000Z calWithTimeZone
	 * will be 2017-11-30T19:00:00.000 + (05:00) = 2017-12-01T00:00:00.000Z
	 * 
	 * @param cruise
	 * @return YearMonth (with timezone) to compare
	 */
	public static YearMonth getYearMonthWithTimeZone(Calendar cruiseDate) {
		Long timeMillsWithTimeZone = cruiseDate.getTimeInMillis() + Math.abs(cruiseDate.getTimeZone().getRawOffset());

		Calendar calWithTimeZone = new GregorianCalendar();
		calWithTimeZone.setTime(new Date(timeMillsWithTimeZone));

		return YearMonth.of(calWithTimeZone.get(Calendar.YEAR), calWithTimeZone.get(Calendar.MONTH) + 1);
	}
}

package com.silversea.aem.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DateUtils {

    static final private Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);

    /**
     * Returns the XMLGregorianCalendar version of a date string with the dateFormat
     */
    public static XMLGregorianCalendar getXmlGregorianCalendar(String dateString, String dateFormat) {
        XMLGregorianCalendar xmlDate = null;
        try {
            DateFormat df = new SimpleDateFormat(dateFormat);
            Date date = df.parse(dateString);
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(date);
            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return xmlDate;
    }
    
    /**
     * Format date with a specific format
     * @param format: date's format
     * @param date: date to format
     * @return: formatted date
     */
    public static String formatDate(String format,Date date){
        String formattedDate = null;
        if (date != null && StringUtils.isNoneEmpty(format)) {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            formattedDate = formatter.format(date);
        }
        return formattedDate;
    }
    
    /**
     * Parse String date
     * @param format: date's format
     * @param date: date to parse
     * @return date object
     */
    public static Date parseDate(String format,String date) {
        Date parsedDate = null;
        if (StringUtils.isNoneEmpty(date) && StringUtils.isNoneEmpty(format)) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(format);
                parsedDate = formatter.parse(date);
            } catch (ParseException e) {
                LOGGER.error("Date parse exception ",e);
            }
        }
        return parsedDate;
    }
    
    /**
     * parse date with a specific format by locale
     * @param format: date's format
     * @param date: date to format
     * @param locale: date's locale
     * @return: parsed date
     */
    public static String formatDateByLocale(String format,String date,String locale){
        String formattedDate = null;
        if (date != null && StringUtils.isNoneEmpty(format) 
                && StringUtils.isNoneEmpty(locale)) {
            SimpleDateFormat formatter = new SimpleDateFormat(format,new Locale(locale,StringUtils.upperCase(locale)));
            Date parsedDate = parseDate(format,date);
            formattedDate = formatter.format(parsedDate);
        }
        return formattedDate;
    }
}
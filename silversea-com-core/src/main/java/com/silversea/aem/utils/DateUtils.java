package com.silversea.aem.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

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
}
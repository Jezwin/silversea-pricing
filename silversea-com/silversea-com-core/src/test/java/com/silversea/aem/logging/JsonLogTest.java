package com.silversea.aem.logging;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.silversea.aem.logging.JsonLog.jsonLog;
import static com.silversea.aem.logging.JsonLog.nested;
import static org.junit.Assert.assertEquals;


public class JsonLogTest {

    @Test
    public void eventIsRequiredForLogs() {
        JsonLog log = jsonLog("abc");
        assertEquals("{\"event\":\"abc\"}", log.toString());
    }

    @Test
    public void canAddStringProperties() {
        JsonLog log = jsonLog("stringTest").with("stringkey", "stringvalue");
        assertEquals("{\"event\":\"stringTest\",\"stringkey\":\"stringvalue\"}", log.toString());
    }

    @Test
    public void canAddLongProperties() {
        JsonLog log = jsonLog("longTest").with("longkey", (long)444558897);
        assertEquals("{\"event\":\"longTest\",\"longkey\":444558897}", log.toString());
    }

    @Test
    public void canAddDecimalProperties() {
        JsonLog log = jsonLog("decimalTest").with("decimalkey", new BigDecimal(444.01).setScale(2, RoundingMode.CEILING));
        assertEquals("{\"event\":\"decimalTest\",\"decimalkey\":444.01}", log.toString());
    }

    @Test
    public void canAddStringArrays() {
        JsonLog log = jsonLog("stringArrayTest").with("stringArrayKey", new String[]{ "aaa", "bbb", "ccc" });
        assertEquals("{\"event\":\"stringArrayTest\",\"stringArrayKey\":[\"aaa\",\"bbb\",\"ccc\"]}", log.toString());
    }

    @Test
    public void canAddNestedJsonProperties() {
        JsonLog log = jsonLog("nestedTest").with("nestedKey", nested().with("a", 22).with("b", "blah"));
        assertEquals("{\"event\":\"nestedTest\",\"nestedKey\":{\"a\":22,\"b\":\"blah\"}}", log.toString());
    }

    @Test
    public void canLogExceptionWithCause() {
        JsonLog log = jsonLog("exceptionTest").with(new Exception("abc123", new Exception("nestedError")));
        assertEquals("{\"event\":\"exceptionTest\",\"exception\":{\"type\":\"java.lang.Exception\",\"message\":\"abc123\",\"cause\":\"nestedError\"}}", log.toString());
    }

    @Test
    public void canLogExceptionWithouCause() {
        JsonLog log = jsonLog("exceptionTest").with(new Exception("zzzaaawww"));
        assertEquals("{\"event\":\"exceptionTest\",\"exception\":{\"type\":\"java.lang.Exception\",\"message\":\"zzzaaawww\"}}", log.toString());
    }
}

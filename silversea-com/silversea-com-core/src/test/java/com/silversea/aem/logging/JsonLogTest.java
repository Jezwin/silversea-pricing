package com.silversea.aem.logging;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.silversea.aem.logging.JsonLog.jsonLog;
import static com.silversea.aem.logging.JsonLog.nested;
import static org.junit.Assert.assertEquals;


public class JsonLogTest {

    @Test public void eventIsRequiredForLogs() {
        JsonLog log = jsonLog("abc");
        String json = log.underlying().toString();
        assertEquals("{\"event\":\"abc\"}", json);
    }

    @Test public void canAddStringProperties() {
        JsonLog log = jsonLog("stringTest").with("stringkey", "stringvalue");
        String json = log.underlying().toString();
        assertEquals("{\"event\":\"stringTest\",\"stringkey\":\"stringvalue\"}", json);
    }

    @Test public void canAddLongProperties() {
        JsonLog log = jsonLog("longTest").with("longkey", (long)444558897);
        String json = log.underlying().toString();
        assertEquals("{\"event\":\"longTest\",\"longkey\":444558897}", json);
    }

    @Test public void canAddDecimalProperties() {
        JsonLog log = jsonLog("decimalTest").with("decimalkey", new BigDecimal(444.01).setScale(2, RoundingMode.CEILING));
        String json = log.underlying().toString();
        assertEquals("{\"event\":\"decimalTest\",\"decimalkey\":444.01}", json);
    }

    @Test public void canAddStringArrays() {
        JsonLog log = jsonLog("stringArrayTest").with("stringArrayKey", new String[]{ "aaa", "bbb", "ccc" });
        String json = log.underlying().toString();
        assertEquals("{\"event\":\"stringArrayTest\",\"stringArrayKey\":[\"aaa\",\"bbb\",\"ccc\"]}", json);
    }

    @Test public void canAddNestedJsonProperties() {
        JsonLog log = jsonLog("nestedTest").with("nestedKey", nested().with("a", 22).with("b", "blah"));
        String json = log.underlying().toString();
        assertEquals("{\"event\":\"nestedTest\",\"nestedKey\":{\"a\":22,\"b\":\"blah\"}}", json);
    }
}

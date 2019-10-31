package models;

import com.silversea.aem.models.ItineraryModel;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DedicatedShorexPolicyTest {
    @Test
    public void itShouldHaveShorexIfNotExpeditionAndLessThan180Days() {
        Calendar calendar = Calendar.getInstance();
        DateTime futureDate = DateTime.now().plusDays(179);
        calendar.setTime(futureDate.toDate());
        Boolean isExpedition = false;
        Boolean hasShorex = ItineraryModel.DedicatedShorexPolicy.hasDedicatedShorex(calendar,isExpedition);
        assertTrue(hasShorex);
    }
    @Test
    public void itShouldNotHaveShorexIfExpeditionAndLessThan180Days() {
        Calendar calendar = Calendar.getInstance();
        DateTime futureDate = DateTime.now().plusDays(179);
        calendar.setTime(futureDate.toDate());
        Boolean isExpedition = true;
        Boolean hasShorex = ItineraryModel.DedicatedShorexPolicy.hasDedicatedShorex(calendar,isExpedition);
        assertFalse(hasShorex);
    }
    @Test
    public void itShouldNotHaveShorexIfExpeditionAndMoreThan180Days() {
        Calendar calendar = Calendar.getInstance();
        DateTime futureDate = DateTime.now().plusDays(181);
        calendar.setTime(futureDate.toDate());
        Boolean isExpedition = true;
        Boolean hasShorex = ItineraryModel.DedicatedShorexPolicy.hasDedicatedShorex(calendar,isExpedition);
        assertFalse(hasShorex);
    }
    @Test
    public void itShouldNotHaveShorexIfNotExpeditionAndMoreThan180Days() {
        Calendar calendar = Calendar.getInstance();
        DateTime futureDate = DateTime.now().plusDays(181);
        calendar.setTime(futureDate.toDate());
        Boolean isExpedition = false;
        Boolean hasShorex = ItineraryModel.DedicatedShorexPolicy.hasDedicatedShorex(calendar,isExpedition);
        assertFalse(hasShorex);
    }
}

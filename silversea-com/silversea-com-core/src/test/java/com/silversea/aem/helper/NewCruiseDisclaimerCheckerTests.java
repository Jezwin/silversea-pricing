package com.silversea.aem.helper;

import org.junit.Assert;
import org.junit.Test;

public class NewCruiseDisclaimerCheckerTests {

    @Test
    public void returnsTrueForSingleMatch() throws Exception {
        Assert.assertTrue(getResult("1", "1"));
    }

    @Test
    public void returnsFalseForNonMatch() throws Exception {
        Assert.assertFalse(getResult("1", "2"));
    }

    @Test
    public void returnsTrueForMatchInList() throws Exception {
        Assert.assertTrue(getResult("1,2", "2"));
    }

    @Test
    public void returnsTrueWhenCrxListContainsSpaces() throws Exception {
        Assert.assertTrue(getResult("1, 2,  3,4 ,5 ,", "4"));
    }

    @Test
    public void returnsTrueWhenCrxListContainsSpacesAtTheEnd() throws Exception {
        Assert.assertTrue(getResult("1, 2,  3,4 ,5 , 6 ", "6"));
    }

    @Test
    public void returnsTrueForCaseInsensitiveMatch() throws Exception {
        Assert.assertTrue(getResult("a1", "A1"));
    }

    private boolean getResult(String codesInCrx, String cruiseCodeToCheck) throws Exception {
        FakeCrxQuerier crxQuerier = new FakeCrxQuerier();
        NewCruiseDisclaimerNode node = new NewCruiseDisclaimerNode(codesInCrx);
        crxQuerier.addNode(NewCruiseDisclaimerChecker.CRX_NODE_PATH, node);
        NewCruiseDisclaimerChecker checker = new NewCruiseDisclaimerChecker(crxQuerier);
        return checker.needsDisclaimer(cruiseCodeToCheck);
    }
}
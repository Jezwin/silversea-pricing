package com.silversea.aem.helper;

import org.hamcrest.core.IsAnything;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NewCruiseDisclaimerHelperTest {

//    private NewCruiseDisclaimerHelper mockNewCruiseDisclaimerHelper;
//
//    public NewCruiseDisclaimerHelperTest() throws Exception {
//        mockNewCruiseDisclaimerHelper = mock(NewCruiseDisclaimerHelper.class);
//
//       when(mockNewCruiseDisclaimerHelper.get(Mockito.any(), Mockito.any())).thenReturn("Random stuff");
//
//        // Set to call the real activate method in the class being tested. For void return types.
//        doCallRealMethod().when(mockNewCruiseDisclaimerHelper).activate();
//    }

    @Test
    public void parseCommaSeparatedCruiseCodesTest() {
        String codesFromCrx = "1, 2,  3,4 ,5 ,";
        String[] expected = new String[]{"1", "2", "3", "4", "5"};

        NewCruiseDisclaimerHelper newCruiseDisclaimerHelper = new NewCruiseDisclaimerHelper();

        String[] actual = newCruiseDisclaimerHelper.parseCodes(codesFromCrx);
        Assert.assertArrayEquals(expected, actual);
    }

//    @Test
//    public void makeThisTestWork() throws Exception {
//        mockNewCruiseDisclaimerHelper.activate();
//        Assert.assertTrue(mockNewCruiseDisclaimerHelper.getShowDisclaimer());
//    }
}
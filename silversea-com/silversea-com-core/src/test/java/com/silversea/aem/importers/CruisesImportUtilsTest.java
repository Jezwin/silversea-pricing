package com.silversea.aem.importers;

import com.silversea.aem.importers.utils.CruisesImportUtils;
import io.swagger.client.model.Price;
import io.swagger.client.model.VoyagePriceMarket;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CruisesImportUtilsTest {

    @Test
    public void missingPriorityFare(){
        Price p1 = createPrice("03", "OK");
        VoyagePriceMarket vpm = new VoyagePriceMarket();
        vpm.setPriorityFare(null);
        vpm.setCruiseOnlyPrices(Arrays.asList(p1));

        List<Price> result = CruisesImportUtils.filterPricesByPriorityFare(vpm);

        assertThat(result, equalTo(Arrays.asList(p1)));
    }

    @Test
    public void withPriorityFare(){
        Price p1 = createPrice("03", "OK");
        Price p2 = createPrice("04", "KO");
        VoyagePriceMarket vpm = new VoyagePriceMarket();
        vpm.setPriorityFare("03");
        vpm.setCruiseOnlyPrices(Arrays.asList(p1,p2));

        List<Price> result = CruisesImportUtils.filterPricesByPriorityFare(vpm);

        assertThat(result, equalTo(Arrays.asList(p1)));
    }

    private Price createPrice(String fare, String categoryCode) {
        Price p1 = new Price();
        p1.setFare(fare);
        p1.setSuiteCategoryCod(categoryCode);
        return p1;
    }

}

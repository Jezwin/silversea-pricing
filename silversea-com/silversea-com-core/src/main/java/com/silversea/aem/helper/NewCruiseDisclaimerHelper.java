package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.helper.crx.CrxQuerierImpl;

public class NewCruiseDisclaimerHelper extends WCMUsePojo {

    private Boolean showDisclaimer;

    @Override
    public void activate() throws Exception {
        String cruiseCode = super.get("cruiseCode", String.class);
        NewCruiseDisclaimerChecker checker = new NewCruiseDisclaimerChecker(buildCrxQuerier());
        this.showDisclaimer = checker.needsDisclaimer(cruiseCode);
    }

    private CrxQuerierImpl buildCrxQuerier() {
        return new CrxQuerierImpl(super.getResourceResolver());
    }

    public Boolean getShowDisclaimer() {
        return this.showDisclaimer;
    }
}
package com.silversea.aem.components.external;

public class ExternalPageDefs {

    // Add any new External UI (React) page replacements to this array :)
    public static final ExternalPageDef[] All = {
            new ExternalPageDef(
                    "/terms-and-conditions",
                    "/termsandconditions/termsandconditions.html",
                    x -> x.isTermsAndConditionsExternalUiEnabled()),

            new ExternalPageDef(
                    "/cruise/cruise-results",
                    "/findyourcruise/findyourcruise.html",
                    x -> x.isFindYourCruiseExternalUiEnabled())
    };

}

package com.silversea.aem.components.external;

import java.net.URL;

public class ExternalPageDefs {

    // Add any new External UI (React) page replacements to this array :)
    public static final ExternalPageDef[] All = {

            new StaticHtmlExternalPageDef(
                    "/destinations/antarctica-cruise",
                    x -> x.getAntarcticaExperimentStaticExternalUiPath(),
                    x -> x.isAntarcticaExperimentStaticExternalUiEnabled(),
                    x -> x.getRequestParameterMap()
                            .getValue("version")
                            .getString()
                            .equalsIgnoreCase("2")
            ),

            new ReactExternalPageDef(
                    "/terms-and-conditions",
                    "/termsandconditions/termsandconditions.html",
                    x -> x.isTermsAndConditionsExternalUiEnabled(),
                    null,
                    ExternalPageAemContentOption.RenderAsFallback),

            new ReactExternalPageDef(
                    "/destinations/antarctica-cruise",
                    "/antarcticaexperiment/antarcticaexperiment.html",
                    x -> x.isAntarcticaExperimentExternalUiEnabled(),
                    x -> x.getRequestParameterMap()
                            .getValue("version")
                            .getString()
                            .equalsIgnoreCase("2"),
                    ExternalPageAemContentOption.RenderAsFallback),
    };

}

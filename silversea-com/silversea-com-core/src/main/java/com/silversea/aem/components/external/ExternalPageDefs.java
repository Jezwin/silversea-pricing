package com.silversea.aem.components.external;

import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;

public class ExternalPageDefs {

    // Add any new External UI (React) page replacements to this array :)
    public static final ExternalPageDef[] All = {

            new ExternalPageDef(
                    "/terms-and-conditions",
                    "/termsandconditions/termsandconditions.html",
                    x -> x.isTermsAndConditionsExternalUiEnabled(),
                    null,
                    ExternalPageAemContentOption.RenderAsFallback),

            new ExternalPageDef(
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

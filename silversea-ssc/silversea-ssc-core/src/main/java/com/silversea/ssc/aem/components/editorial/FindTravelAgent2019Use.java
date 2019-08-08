package com.silversea.ssc.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRow;
import com.silversea.aem.models.TravelAgencyModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class FindTravelAgent2019Use extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindTravelAgent2019Use.class);
    private List<TravelAgencyModel> travelAgents;

    @Override
    public void activate() throws Exception {
        String reference = getProperties().get("reference", String.class);
        travelAgents = retrieveTravelAgent(reference);
    }

    private List<TravelAgencyModel> retrieveTravelAgent(String path) {
        List<TravelAgencyModel> travelAgents = new ArrayList<>();
        if (StringUtils.isNotEmpty(path)) {
            Resource res = getResourceResolver().resolve(path);
            if (res != null) {
                Page rootPage = res.adaptTo(Page.class);
                if (rootPage != null) {
                    Iterator<Page> it = rootPage.listChildren(new PageFilter());
                    while (it.hasNext()) {
                        Page currentPage = it.next();
                        Iterator<Page> itChild = currentPage.listChildren(new PageFilter());
                        while (itChild.hasNext()) {
                            TravelAgencyModel travelAgent = itChild.next().adaptTo(TravelAgencyModel.class);
                            travelAgents.add(travelAgent);
                        }
                    }
                }
            }
        }
        return travelAgents.stream().sorted(Comparator.comparing(TravelAgencyModel::getTitle)).collect(Collectors.toList());
    }

    public String getTravelAgents() {
        return toJsonArray(travelAgents).toString();
    }


    public Integer getTotTravelAgents() {
        return travelAgents != null ? travelAgents.size() : 0;
    }

    private JsonElement toJson(TravelAgencyModel agent) {
        JsonObject json = new JsonObject();
        json.addProperty("title", agent.getTitle());
        json.addProperty("agencyId", agent.getAgencyId());
        json.addProperty("address", agent.getAddress());
        json.addProperty("city", agent.getCity());
        json.addProperty("zip", agent.getZip());
        json.addProperty("country", agent.getCountry());
        json.addProperty("phone", agent.getPhone());
        json.addProperty("latitude", agent.getLatitude());
        json.addProperty("longitude", agent.getLongitude());
        json.addProperty("stateCode", agent.getStateCode());
        return json;
    }

    public JsonElement toJsonArray(List<TravelAgencyModel> travelAgents) {
        JsonArray array = new JsonArray();
        if (travelAgents != null) {
            travelAgents.forEach(row -> {
                array.add(toJson(row));
            });
        }
        return array;
    }
}

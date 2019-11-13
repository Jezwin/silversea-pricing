package com.silversea.aem.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.EoBean;
import com.silversea.aem.components.beans.EoConfigurationBean;
import com.silversea.aem.components.beans.ValueTypeBean;
import com.silversea.aem.config.ConfigurationManager;
import com.silversea.aem.config.CoreConfig;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.content.CrxContentLoader;
import com.silversea.aem.importers.services.StyleCache;
import com.silversea.aem.logging.LogzLoggerFactory;
import com.silversea.aem.models.AppSettingsModel;
import com.silversea.aem.models.ExclusiveOfferFareModel;
import com.silversea.aem.models.ExclusiveOfferModel;
import com.silversea.aem.proxies.ExclusiveOfferProxy;
import com.silversea.aem.proxies.OkHttpClientWrapper;
import com.silversea.aem.services.ExclusiveOffer;
import com.silversea.aem.utils.AwsSecretsManager;
import com.silversea.aem.utils.AwsSecretsManagerClientWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

import java.text.SimpleDateFormat;
import java.util.*;

public class EoHelper extends AbstractGeolocationAwareUse {

    private StyleCache styleCache;
    private Gson gson;
    private ExclusiveOfferProxy exclusiveOfferProxy;
    private ExclusiveOffer exclusiveOffer;
    private LogzLoggerFactory sscLogFactory;
    private AppSettingsModel appSettings;

    @Override
    public void activate() throws Exception {
        super.activate();
        styleCache = getSlingScriptHelper().getService(StyleCache.class);
        sscLogFactory = getSlingScriptHelper().getService(LogzLoggerFactory.class);
        gson = new GsonBuilder().create();

        ResourceResolver resourceResolver = super.getResourceResolver();
        CrxContentLoader contentLoader = new CrxContentLoader(resourceResolver);
        ConfigurationManager configurationManager = new ConfigurationManager(contentLoader);
        this.appSettings = configurationManager.getAppSettings();

        CoreConfig config = getSlingScriptHelper().getService(CoreConfig.class);
        AwsSecretsManager awsSecretsManager = new AwsSecretsManagerClientWrapper(config.getAwsRegion(), config.getAwsSecretName());

        exclusiveOfferProxy = new ExclusiveOfferProxy(new OkHttpClientWrapper(awsSecretsManager), this.appSettings.getBffApiBaseUrl());
        exclusiveOffer = new ExclusiveOffer(exclusiveOfferProxy, sscLogFactory.getLogger(ExclusiveOffer.class));
    }

    public EoBean parseExclusiveOffer(EoConfigurationBean eoConfig, ExclusiveOfferModel eoModel) {
        EoBean eoBean = null;

        if (eoConfig != null && eoConfig.isActiveSystem() && eoModel != null) {
            eoBean = new EoBean();
            String title = null, description = null, shortDescription = null, mapOverhead = null, footnote = null,
                    shortTitle = null, eofootnotes = null, iconVoyage = null, postPrice = null;
            Integer priorityWeight = 0;

            ExclusiveOfferFareModel[] cruiseFares = null;
            Map<String, ValueTypeBean> styles = styleCache.getStyles();

            Map<String, ValueTypeBean> tokensAndStyle =
                    getTokensByBesthMatchTag(eoModel.getCustomTokenValuesSettings());

            String cruiseCode = (String) getCurrentPage().getProperties().get("cruiseCode");

            if (appSettings.isExclusiveOffersExternalBffEnabled() && cruiseCode != null) {
                Locale locale = new Locale(getCurrentPage().getLanguage().getLanguage(), countryCode);
                exclusiveOffer.ResolveExclusiveOfferTokens(tokensAndStyle, currency, cruiseCode, locale);
            }

            ValueTypeBean eoValue = null;
            if (eoModel.getExpirationDate() != null) {
                Date expirationDate = eoModel.getExpirationDate();
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy", getCurrentPage().getLanguage(false));
                if (getCurrentPage().getLanguage(false).getLanguage().equals("de")) {
                    formatter = new SimpleDateFormat("dd. MMMM yyyy", getCurrentPage().getLanguage(false));
                }
                formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                eoValue = new ValueTypeBean(formatter.format(expirationDate.getTime()), "token");
                tokensAndStyle.put("expiration_date", eoValue);

                SimpleDateFormat formatterShort = new SimpleDateFormat("dd MMMM", getCurrentPage().getLanguage(false));
                if (getCurrentPage().getLanguage(false).getLanguage().equals("de")) {
                    formatter = new SimpleDateFormat("dd. MMMM", getCurrentPage().getLanguage(false));
                }
                formatterShort.setTimeZone(TimeZone.getTimeZone("GMT"));
                eoValue = new ValueTypeBean(formatterShort.format(expirationDate.getTime()), "token");
                tokensAndStyle.put("expiration_date_short", eoValue);


            }

            if (styles != null && !styles.isEmpty()) {
                tokensAndStyle.putAll(styles);
            }

            if (eoModel.getGeomarkets() != null && eoModel.getGeomarkets().contains(geomarket)) {
                eoBean.setAvailable(true);
            }

            if (eoConfig.isTitleMain()) {
                title = getValueByBesthMatchTag(eoModel.getCustomMainSettings(), "title", eoModel.getDefaultTitle());
            }

            if (eoConfig.isShortTitleMain()) {
                shortTitle = getValueByBesthMatchTag(eoModel.getCustomMainSettings(), "shortTitle",
                        eoModel.getDefaultShortTitle());
            }

            if (eoConfig.isDescriptionMain()) {
                description = getValueByBesthMatchTag(eoModel.getCustomMainSettings(), "description",
                        eoModel.getDefaultDescription());
            }
            if (eoConfig.isShortDescriptionMain()) {
                shortDescription = getValueByBesthMatchTag(eoModel.getCustomMainSettings(), "shortDescription",
                        eoModel.getDefaultShortDescription());
            }
            if (eoConfig.isFootnotesMain()) {
                eofootnotes = getValueByBesthMatchTag(eoModel.getCustomMainSettings(), "footnotes",
                        eoModel.getDefaultEoFootnotes());
            }
            //-----------------------------------------
            if (eoConfig.isDescriptionTnC()) {
                description = getValueByBesthMatchTag(eoModel.getCustomTnCSettings(), "description",
                        eoModel.getDefaultDescriptionTnC());
            }
            //-----------------------------------------
            if (eoConfig.isTitleVoyage()) {
                title = getValueByBesthMatchTag(eoModel.getCustomVoyageSettings(), "shortTitle",
                        eoModel.getCustomMainSettings(), eoModel.getDefaultShortTitle());
            }

            if (eoConfig.isPostPrice()) {
                postPrice = getValueByBesthMatchTag(eoModel.getCustomVoyageSettings(), "postPrice", eoModel.getDefaultPostPrice());
            }

            if (eoConfig.isPriorityWeight()) {
                priorityWeight = Integer.parseInt(
                        getValueByBesthMatchTag(eoModel.getCustomVoyageSettings(), "priorityWeight", eoModel.getDefaultPriorityWeight().toString()));

            }
            if (eoConfig.isIconVoyage()) {
                iconVoyage = getValueByBesthMatchTag(eoModel.getCustomVoyageSettings(), "icon",
                        eoModel.getDefaultVoyageIcon());
            }

            if (eoConfig.isShortDescriptionVoyage()) {
                shortDescription = getValueByBesthMatchTag(eoModel.getCustomVoyageSettings(), "shortDescription",
                        eoModel.getCustomMainSettings(), eoModel.getDefaultShortDescription());
            }
            if (eoConfig.isMapOverheadVoyage()) {
                mapOverhead = getValueByBesthMatchTag(eoModel.getCustomVoyageSettings(), "mapOverhead",
                        eoModel.getCustomMainSettings(), eoModel.getDefaultMapOverhead());
            }
            if (eoConfig.isFootnoteVoyage()) {
                footnote = getValueByBesthMatchTag(eoModel.getCustomVoyageSettings(), "footnote",
                        eoModel.getCustomMainSettings(), eoModel.getDefaultFootnote());
            }
            if (eoConfig.isCruiseFareVoyage()) {
                cruiseFares = getCruiseFaresValuesByBesthMatchTag(eoModel.getCustomVoyageFaresSettings());
            }
            //-----------------------------------------
            if (eoConfig.isTitleLigthbox()) {
                title = getValueByBesthMatchTag(eoModel.getCustomLBSettings(), "title", eoModel.getCustomMainSettings(),
                        eoModel.getDefaultTitle());
            }
            if (eoConfig.isDescriptionLigthbox()) {
                description = getValueByBesthMatchTag(eoModel.getCustomLBSettings(), "description",
                        eoModel.getCustomMainSettings(), eoModel.getDefaultDescription());
            }
            if (eoConfig.isActiveGreysSystem()) {
                eoBean.setGreyBoxesSystem(eoModel.getActiveGreysBoxes());
            }
            if (eoConfig.isImageLightbox()) {
                eoBean.setImage(eoModel.getPathImageLB());
            }

            //replace token and style
            for (String key : tokensAndStyle.keySet()) {
                eoValue = tokensAndStyle.get(key);
                String valueToReplace = null,
                        keyToReplace = null,
                        endTag = null;
                if (eoValue.getType().equalsIgnoreCase("token")) {
                    keyToReplace = "#" + key + "#";
                    valueToReplace = eoValue.getValue();
                } else if (eoValue.getType().equalsIgnoreCase("style")) {
                    keyToReplace = "<" + key + ">";
                    endTag = "</" + key + ">";
                    valueToReplace = "<span style='" + eoValue.getValue() + "'>";
                }
                if (StringUtils.isNotEmpty(title)) {
                    title = title.replace(keyToReplace, valueToReplace);
                    title = title.replace("\n", "<br>");
                    if (eoValue.getType().equalsIgnoreCase("style")) {
                        title = title.replace(endTag, "</span>");
                    }
                }
                if (StringUtils.isNotEmpty(shortTitle)) {
                    shortTitle = shortTitle.replace(keyToReplace, valueToReplace);
                    shortTitle = shortTitle.replace("\n", "<br>");
                    if (eoValue.getType().equalsIgnoreCase("style")) {
                        shortTitle = shortTitle.replace(endTag, "</span>");
                    }
                }
                if (StringUtils.isNotEmpty(description)) {
                    description = description.replace(keyToReplace, valueToReplace);
                    description = description.replace("\n", "<br>");
                    if (eoValue.getType().equalsIgnoreCase("style")) {
                        description = description.replace(endTag, "</span>");
                    }
                }
                if (StringUtils.isNotEmpty(shortDescription)) {
                    shortDescription = shortDescription.replace(keyToReplace, valueToReplace);
                    shortDescription = shortDescription.replace("\n", "<br>");
                    if (eoValue.getType().equalsIgnoreCase("style")) {
                        shortDescription = shortDescription.replace(endTag, "</span>");
                    }
                }
                if (StringUtils.isNotEmpty(eofootnotes)) {
                    eofootnotes = eofootnotes.replace(keyToReplace, valueToReplace);
                    eofootnotes = eofootnotes.replace("\n", "<br>");
                    if (eoValue.getType().equalsIgnoreCase("style")) {
                        eofootnotes = eofootnotes.replace(endTag, "</span>");
                    }
                }
                if (StringUtils.isNotEmpty(mapOverhead)) {
                    mapOverhead = mapOverhead.replace(keyToReplace, valueToReplace);
                    mapOverhead = mapOverhead.replace("\n", "<br>");
                    if (eoValue.getType().equalsIgnoreCase("style")) {
                        mapOverhead = mapOverhead.replace(endTag, "</span>");
                    }
                }
                if (StringUtils.isNotEmpty(footnote)) {
                    footnote = footnote.replace(keyToReplace, valueToReplace);
                    footnote = footnote.replace("\n", "<br>");
                    if (eoValue.getType().equalsIgnoreCase("style")) {
                        footnote = footnote.replace(endTag, "</span>");
                    }
                }
                if (cruiseFares != null && cruiseFares.length > 0) {
                    for (int i = 0; i < cruiseFares.length; i++) {
                        if (StringUtils.isNotEmpty(cruiseFares[i].additionalFare)) {
                            cruiseFares[i].additionalFare =
                                    cruiseFares[i].additionalFare.replace(keyToReplace, valueToReplace);
                            if (eoValue.getType().equalsIgnoreCase("style")) {
                                cruiseFares[i].additionalFare =
                                        cruiseFares[i].additionalFare.replace(endTag, "</span>");
                            }
                        }

                        if (StringUtils.isNotEmpty(cruiseFares[i].footnote)) {
                            cruiseFares[i].footnote = cruiseFares[i].footnote.replace(keyToReplace, valueToReplace);
                            if (eoValue.getType().equalsIgnoreCase("style")) {
                                cruiseFares[i].footnote = cruiseFares[i].footnote.replace(endTag, "</span>");
                            }
                        }
                    }
                }
            }
            if (StringUtils.isNotEmpty(title)) {
                eoBean.setTitle(title);
            }
            if (StringUtils.isNotEmpty(shortTitle)) {
                eoBean.setShortTitle(shortTitle);
            }
            if (StringUtils.isNotEmpty(description)) {
                eoBean.setDescription(description);
            }
            if (StringUtils.isNotEmpty(shortDescription)) {
                eoBean.setShortDescription(shortDescription);
            }
            if (StringUtils.isNotEmpty(eofootnotes)) {
                eoBean.setEoFootnotes(eofootnotes);
            }
            if (StringUtils.isNotEmpty(mapOverhead)) {
                eoBean.setMapOverhead(mapOverhead);
            }
            if (StringUtils.isNotEmpty(footnote)) {
                eoBean.setFootnote(footnote);
            }
            if (StringUtils.isNotEmpty(iconVoyage)) {
                eoBean.setIcon(iconVoyage);
            }
            if (StringUtils.isNotEmpty(postPrice)) {
                eoBean.setPostPrice(postPrice);
            }
            if (cruiseFares != null && cruiseFares.length > 0) {
                eoBean.setCruiseFares(cruiseFares);
            }
            eoBean.setPriorityWeight(priorityWeight);

        }

        return eoBean;
    }

    protected Map<String, ValueTypeBean> getTokenAnsStyleByTag(ExclusiveOfferModel eoModel) {
        Map<String, ValueTypeBean> tokensAndStyle = getTokensByBesthMatchTag(eoModel.getCustomTokenValuesSettings());
        Map<String, ValueTypeBean> styles = styleCache.getStyles();

        ValueTypeBean eoValue = null;
        if (eoModel.getExpirationDate() != null) {
            Date expirationDate = eoModel.getExpirationDate();
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy", getCurrentPage().getLanguage(false));
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            eoValue = new ValueTypeBean(formatter.format(expirationDate.getTime()), "token");
            tokensAndStyle.put("expiration_date", eoValue);

            SimpleDateFormat formatterShort = new SimpleDateFormat("dd MMMM", getCurrentPage().getLanguage(false));
            formatterShort.setTimeZone(TimeZone.getTimeZone("GMT"));
            eoValue = new ValueTypeBean(formatterShort.format(expirationDate.getTime()), "token");
            tokensAndStyle.put("expiration_date_short", eoValue);
        }

        if (styles != null && !styles.isEmpty()) {
            tokensAndStyle.putAll(styles);
        }

        return tokensAndStyle;
    }


    private Map<String, ValueTypeBean> getTokensByBesthMatchTag(String[] customTokens) {
        Map<String, ValueTypeBean> tokenByTag = new HashMap<String, ValueTypeBean>();
        if (customTokens != null) {
            JsonObject eoSettings = null;
            String value = null, token = null;
            for (int i = 0; i < customTokens.length; i++) {
                eoSettings = gson.fromJson(customTokens[i], JsonObject.class);
                if (eoSettings != null) {
                    if (eoSettings.get("tags") != null) {
                        String[] tags = eoSettings.get("tags").getAsString().split(",");
                        for (String tag : tags) {
                            tag = tag.replaceAll(WcmConstants.GEOLOCATION_TAGS_PREFIX, "");
                            if (super.isBestMatch(tag)) {
                                value = (eoSettings.get("value") != null) ? eoSettings.get("value").getAsString() :
                                        null;
                                token = (eoSettings.get("token") != null) ? eoSettings.get("token").getAsString() :
                                        null;
                                ValueTypeBean eoValue = new ValueTypeBean(value, "token");
                                if (!tokenByTag.containsKey(token)) {
                                    tokenByTag.put(token, eoValue);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return tokenByTag;
    }

    private String getValueByBesthMatchTag(String[] customSettings, String type, String[] defaultValueLevel1,
                                           String defaultValueLevel2) {
        String value = getValueByBesthMatchTag(customSettings, type);
        if (StringUtils.isEmpty(value)) {
            return getValueByBesthMatchTag(defaultValueLevel1, type, defaultValueLevel2);
        }
        return value;
    }

    private String getValueByBesthMatchTag(String[] customSettings, String type, String defaultValue) {
        String value = getValueByBesthMatchTag(customSettings, type);
        if (StringUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    private String getValueByBesthMatchTag(String[] customSettings, String type) {
        String value = null;
        if (customSettings != null) {
            JsonObject eoSettings = null;
            for (int i = 0; i < customSettings.length && (value == null); i++) {
                eoSettings = gson.fromJson(customSettings[i], JsonObject.class);

                if (eoSettings != null) {
                    boolean isActive = (eoSettings.get("active") != null) ? Boolean.valueOf(eoSettings.get("active").getAsString()) : false;

                    if (isActive) {
                        boolean typeIsTitle = (eoSettings.get("type") != null) ? eoSettings.get("type").getAsString().equalsIgnoreCase(type) : false;
                        String propertyNameValue = type.equalsIgnoreCase("icon") ? "valueIcon" : "value";
                        if (typeIsTitle) {
                            if (eoSettings.get("tags") != null) {
                                String[] tags = eoSettings.get("tags").getAsString().split(",");
                                for (String tag : tags) {
                                    tag = tag.replaceAll(WcmConstants.GEOLOCATION_TAGS_PREFIX, "");
                                    if (super.isBestMatch(tag)) {
                                        value = (eoSettings.get(propertyNameValue) != null) ? eoSettings.get(propertyNameValue).getAsString()
                                                : null;
                                        break;
                                    }
                                }
                            } else {
                                value = (eoSettings.get(propertyNameValue) != null) ? eoSettings.get(propertyNameValue).getAsString() : null;
                            }
                        }
                    }
                }
            }
        }
        return value;
    }


    private ExclusiveOfferFareModel[] getCruiseFaresValuesByBesthMatchTag(String[] customSettings) {
        List<ExclusiveOfferFareModel> value = null;
        if (customSettings != null) {
            JsonObject eoSettings = null;
            for (int i = 0; i < customSettings.length; i++) {
                eoSettings = gson.fromJson(customSettings[i], JsonObject.class);

                if (eoSettings != null) {
                    boolean isActive = (eoSettings.get("active") != null) ?
                            Boolean.valueOf(eoSettings.get("active").getAsString()) : false;

                    if (isActive) {

                        if (eoSettings.get("tags") != null) {
                            String[] tags = eoSettings.get("tags").getAsString().split(",");
                            for (String tag : tags) {
                                tag = tag.replaceAll(WcmConstants.GEOLOCATION_TAGS_PREFIX, "");
                                if (super.isBestMatch(tag)) {
                                    ExclusiveOfferFareModel newFare = new ExclusiveOfferFareModel();
                                    if ((eoSettings.get("cruisefare") != null)) {

                                        newFare.additionalFare = eoSettings.get("cruisefare").getAsString();
                                    }
                                    if ((eoSettings.get("cruisefarefootnote") != null)) {
                                        newFare.footnote = eoSettings.get("cruisefarefootnote").getAsString();
                                    }
                                    if (value == null) {
                                        value = new ArrayList<ExclusiveOfferFareModel>();
                                    }
                                    value.add(newFare);
                                    break;
                                }
                            }
                        }

                    }
                }
            }
        }
        if (value != null) {
            return value.toArray(new ExclusiveOfferFareModel[value.size()]);
        } else {
            return null;
        }
    }

}

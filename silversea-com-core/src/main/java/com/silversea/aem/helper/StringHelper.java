package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.jcr.JcrUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.xss.XSSAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper extends WCMUsePojo {


    private String textTruncate;
    private String textValidName;
    private String textRest;

    @Override
    public void activate() throws Exception {
        String text = get("text", String.class);
        Integer limit = get("limit", Integer.class);
        String tagsToKeep = get("tagsToKeep", String.class);

        if (StringUtils.isNotEmpty(text) && limit != null) {

            // Clean / encode for HTML
            XSSAPI xssAPI = getResourceResolver().adaptTo(XSSAPI.class);
            text = xssAPI.filterHTML(text);
            String textHtmlStrip = retrieveTextHtmlStrip(text, tagsToKeep);
            textTruncate = (textHtmlStrip.length() > limit) ? textHtmlStrip.substring(0, limit).trim() : textHtmlStrip;

            if (textHtmlStrip.length() > limit) {
                int cutPosition = retrieveLastGoodPointPosition(tagsToKeep, textHtmlStrip, limit);
                textTruncate = retrieveTruncateText(textHtmlStrip, cutPosition);
                textRest = retrieveRestOfText(textHtmlStrip, cutPosition);
            } else {
                textTruncate = textHtmlStrip;
            }
        }

        if (StringUtils.isNotEmpty(text)) {
            textValidName = JcrUtil.createValidName(text, JcrUtil.HYPHEN_LABEL_CHAR_MAPPING);
        }
    }

    public List<String> retrieveListTagsNotValid(String tag, String originalText, int cutPosition) {
        String textLeft = retrieveTruncateText(originalText, cutPosition);
        String textRight = retrieveRestOfText(originalText, cutPosition);
        final Pattern pattern = Pattern.compile("<" + tag + "(.+?)>(.+?)</" + tag + ">", Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(originalText);
        List<String> tagsList = new ArrayList<String>();
        while (matcher.find()) {
            String tagToCheck = matcher.group();
            boolean notLeft = !textLeft.contains(tagToCheck),
                    notRight = !textRight.contains(tagToCheck);
            if (notLeft && notRight) {
                tagsList.add(tagToCheck);
            }
        }
        return tagsList;
    }


    public int retrieveLastGoodPointPosition(String tags, String text, int limit) {
        int cutPosition = limit + 1;
        String textTruncated = text.substring(0, cutPosition);
        if (text.charAt(limit) != '.') {
            cutPosition = textTruncated.lastIndexOf(".") + 1;
        }
        if (StringUtils.isNotEmpty(tags)) {
            List<String> tagNotValid = retrieveListTagsNotValid(tags, text, cutPosition);
            for (String tag : tagNotValid) {
                cutPosition += tag.length();
            }
        }
        if (cutPosition > text.length()) {
            cutPosition = text.length();
        }
        return cutPosition;
    }

    public String retrieveTextHtmlStrip(String text, String tagsToKeep) {
        String regex = retrieveRegexTags(tagsToKeep);
        return text.replaceAll(regex, "");
    }

    public String retrieveTruncateText(String textHtmlStrip, Integer cutPosition) {
        return textHtmlStrip.substring(0, cutPosition);
    }

    public String retrieveRestOfText(String textHtmlStrip, Integer cutPosition) {
        return textHtmlStrip.substring(cutPosition);
    }

    public String retrieveRegexTags(String tagsToKeep) {
        String regex;
        if (tagsToKeep != null) {
            StringBuilder regexForAllTags = new StringBuilder("\\<[^>");
            String[] splittedTagsToKeep = tagsToKeep.split(",");
            for (String tag : splittedTagsToKeep) {
                regexForAllTags.append(",^");
                regexForAllTags.append(tag);
            }
            regexForAllTags.append("]*>");
            regex = regexForAllTags.toString();
        } else {
            // Remove HTML tag
            regex = "\\<[^>]*>";
        }
        return regex;
    }

    /**
     * @return the textTruncate
     */
    public String getTextTruncate() {
        return textTruncate;
    }

    public String getTextRest() {
        return textRest;
    }

    /**
     * @return the textValidName
     */
    public String getTextValidName() {
        return textValidName;
    }
}
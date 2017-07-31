package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * Helper class to deal with languages in SilverSea context.
 * <p>
 * Main usage are :
 * - tagging of asset with language tag,
 * - dealing with language selectors in url, for brochures for example.
 */
public class LanguageHelper extends WCMUsePojo {

    public static final String SELECTOR_LANGUAGE_PREFIX = "language_";

    public static final String ENGLISH = "en";

    private String language;

    @Override
    public void activate() throws Exception {
        language = get("language", String.class);
    }

    public String getSelector() {
        return SELECTOR_LANGUAGE_PREFIX + language;
    }

    /**
     * Get language from page, by getting the name of the 2nd level page in the tree
     *
     * @param page
     * @return SilverSea language code
     */
    public static String getLanguage(final Page page) {
        Page languageAncestor = page.getAbsoluteParent(2);

        if (languageAncestor != null) {
            return languageAncestor.getName();
        }

        return ENGLISH;
    }

    /**
     * Get language from the request by reading the selectors
     *
     * @param request
     * @return SilverSea language code
     */
    public static String getLanguage(final SlingHttpServletRequest request) {
        String[] selectors = request.getRequestPathInfo().getSelectors();

        for (String selector : selectors) {
            if (selector.startsWith(SELECTOR_LANGUAGE_PREFIX)) {
                return selector.replace(SELECTOR_LANGUAGE_PREFIX, "");
            }
        }

        return null;
    }
}

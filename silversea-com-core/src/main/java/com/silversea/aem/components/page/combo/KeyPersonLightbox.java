package com.silversea.aem.components.page.combo;

import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.editorial.cardSliderLightbox.LightboxUse;
import com.silversea.aem.components.page.KeyPerson;
import com.silversea.aem.models.CardLightbox;
import org.apache.sling.api.request.RequestPathInfo;

import java.util.List;

import static com.silversea.aem.utils.MultiFieldUtils.retrieveMultiField;
import static java.util.stream.Collectors.toList;

public class KeyPersonLightbox extends LightboxUse {

    @Override
    public void activate() throws Exception {
        RequestPathInfo requestPathInfo = getRequest().getRequestPathInfo();
        String[] selectors = requestPathInfo.getSelectors();

        List<? extends CardLightbox> keyPeople = retrieveMultiField(getResource(), "keyPeople", resource -> resource.getChild("path"))
                .map(path -> path.adaptTo(String.class))
                .map(getPageManager()::getPage)
                .map((Page page) -> new KeyPerson(page, getResourceResolver()))
                .collect(toList());
        init(requestPathInfo.getResourcePath(), selectors[0]+"."+selectors[1], retrieveCurrentIndex(selectors), keyPeople);
    }
}

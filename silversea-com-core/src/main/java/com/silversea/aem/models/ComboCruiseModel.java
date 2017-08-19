package com.silversea.aem.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = Page.class)
public class ComboCruiseModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ComboCruiseModel.class);

    @Inject @Self
    private Page page;

    @PostConstruct
    private void init() {
    }
}
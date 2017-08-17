package com.silversea.aem.models;

import com.day.cq.tagging.Tag;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Tag.class)
public class FeatureModel {

    private String id;

    private String title;

    private String icon;

    private String description;
}

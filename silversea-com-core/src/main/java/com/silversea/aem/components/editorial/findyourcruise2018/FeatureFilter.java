package com.silversea.aem.components.editorial.findyourcruise2018;

import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.FeatureModel;
import com.silversea.aem.models.FeatureModelLight;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.ENABLED;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

class FeatureFilter extends AbstractFilter<FeatureModelLight> {
    public FeatureFilter() {
        super("feature");
    }

    @Override
    protected Stream<FilterRow<FeatureModelLight>> projection(CruiseModelLight cruise) {
        return cruise.getFeatures().stream().filter(feature -> feature.getTitle() != null)
                .map(feature -> new FilterRow<>(feature, feature.getTitle(), ENABLED));
    }

    @Override
    protected Set<FilterRow<FeatureModelLight>> retrieveAllValues(FindYourCruise2018Use use, List<CruiseModelLight> cruises) {
        TagManager tagManager = use.getTagManager();
        if (tagManager == null) {
            return emptySet();
        }
        return ofNullable(use.getCurrentStyle().get(TagConstants.PN_TAGS, String[].class)).map(Stream::of)
                .map(tags -> tags
                        .map(tagManager::resolve)
                        .filter(Objects::nonNull)
                        .map(tag -> tag.adaptTo(FeatureModel.class))
                        .filter(Objects::nonNull)
                        .map(feature -> new FilterRow<>(new FeatureModelLight(feature), FeatureModelLight::getName,
                                feature.getFeatureId(), ENABLED))
                        .collect(toSet()))
                .orElse(emptySet());
    }
}

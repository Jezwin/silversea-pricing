package com.silversea.aem.components.editorial.findyourcruise2018;

import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.FeatureModel;
import com.silversea.aem.models.FeatureModelLight;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.CHOSEN;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.ENABLED;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;

class FeatureFilter extends AbstractFilter<FeatureModelLight> {
    public FeatureFilter() {
        super("feature", Comparator.comparing(cruise -> ""), Sorting.HIDDEN);
    }

    @Override
    protected Stream<FilterRow<FeatureModelLight>> projection(CruiseModelLight cruise) {
        return cruise.getFeatures().stream().filter(feature -> feature.getTitle() != null)
                .map(feature -> new FilterRow<>(feature, FeatureModelLight::getTitle, feature.getFeatureId(), ENABLED));

    }

    @Override
    protected Set<FilterRow<FeatureModelLight>> retrieveAllValues(FindYourCruise2018Use use,
                                                                  String[] selectedKeys,
                                                                  Consumer<FilterRow<FeatureModelLight>> addToChosen,
                                                                  Collection<CruiseModelLight> allCruises) {
        TagManager tagManager = use.getTagManager();
        if (tagManager == null) {
            return emptySet();
        }
        Set<String> selected = new HashSet<>(Arrays.asList(selectedKeys));
        List<String> availableFeatures = allCruises.stream().flatMap(this::projection).distinct().map(FilterRow::getKey).collect(Collectors.toList());
        return ofNullable(use.getCurrentStyle().get(TagConstants.PN_TAGS, String[].class)).map(Stream::of)
                .map(tags -> tags
                        .map(tagManager::resolve)
                        .filter(Objects::nonNull)
                        .map(tag -> tag.adaptTo(FeatureModel.class))
                        .filter(Objects::nonNull)
                        .filter(feature -> feature.getFeatureId() != null)
                        .filter(feature -> availableFeatures.contains(feature.getFeatureId()))
                        .map(feature -> {
                            if (selected.contains(feature.getFeatureId())) {
                                FeatureFilterRow featureFilterRow = new FeatureFilterRow(feature, CHOSEN);
                                addToChosen.accept(featureFilterRow);
                                return featureFilterRow;
                            } else {
                                return (FilterRow<FeatureModelLight>) new FeatureFilterRow(feature, ENABLED);
                            }
                        })
                        .collect(toCollection(() -> (Set<FilterRow<FeatureModelLight>>) new TreeSet<>(comparator()))))
                .orElse(emptySet());
    }

    public static class FeatureFilterRow extends FilterRow<FeatureModelLight> {
        private final String icon;

        FeatureFilterRow(FeatureModel feature, FilterRowState state) {
            super(new FeatureModelLight(feature), FeatureModelLight::getTitle, feature.getFeatureId(), state);
            icon = feature.getIcon();
        }

        public String getIcon() {
            return icon;
        }
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && !getRows().isEmpty();
    }
}

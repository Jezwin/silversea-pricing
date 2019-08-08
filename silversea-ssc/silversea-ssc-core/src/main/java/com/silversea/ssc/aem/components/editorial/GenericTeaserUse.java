package com.silversea.ssc.aem.components.editorial;

import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.EoConfigurationBean;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.models.DestinationModel;
import com.silversea.aem.models.ExclusiveOfferModel;
import com.silversea.aem.models.ExclusiveOfferVariedModel;
import com.silversea.aem.models.ShipModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public class GenericTeaserUse extends EoHelper {

    static final private Logger LOGGER = LoggerFactory.getLogger(GenericTeaserUse.class);
    private static final String[] LOCALES = new String[]{"ft", "as", "eu", "uk"};


    /**
     * List of exclusive offers paths, based on geolocation
     */
    private List<ItemBean> items;
    private boolean useExcerpt;


    @Override
    public void activate() throws Exception {
        super.activate();
        useExcerpt = Optional.ofNullable(getProperties().get("useExcerpt", Boolean.class)).orElse(false);
        final String listPath = getProperties().get("folderReference", "");
        String[] categoryEO = getProperties().get("categoryEO", String[].class);
        LOGGER.debug("Searching EOs with market: {}", geomarket);
        List<String> categories = categoryEO == null ? emptyList() : Arrays.asList(categoryEO);
        items = sort(stream(((Iterable<Page>) getPageManager().getPage(listPath)::listChildren).spliterator(), false)
                .filter(page -> !isExclusiveOffer(page) || (isExclusiveOfferInCategory(page, categories) && isExclusiveOfferAvailableAndNotGhost(page)))
                .map(page -> toVariation(page).orElseGet(() -> ItemBean.fromPage(page)))
                .collect(Collectors.toList()));
        if (getProperties().get("activatePathList") != null) {
            if (getProperties().get("activatePathList", Boolean.class)) {
                items.clear();
                if (getResource().hasChildren()) {
                    Resource child = getResource().getChild("pathList");
                    if (child != null) {
                        Iterable<Resource> it = child.getChildren();
                        for (Resource rsr : it) {
                            if (rsr != null) {
                                String pathPage = rsr.getValueMap().get("itemPath", String.class);
                                if (StringUtils.isNotEmpty(pathPage)) {
                                    Page p = getResourceResolver().getResource(pathPage).adaptTo(Page.class);
                                    if (p != null) {
                                        items.add(ItemBean.fromPage(p));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private List<ItemBean> sort(List<ItemBean> unsorted) {
        List<String> order = Stream.of(LOCALES).filter(this::isBestMatch).map(this::getSorting).findFirst().orElseGet(Collections::emptyList);
        List<String> manualOrder = Optional.ofNullable(getProperties().get("offerOrderManual", String[].class)).map(Arrays::asList).orElseGet(Collections::emptyList);
        int size = unsorted.size();
        Comparator<ItemBean> comparator = comparingInt(page -> {
            if (manualOrder.contains(page.getPath())) {
                return manualOrder.indexOf(page.getPath());
            } else if (order.contains(page.getPath())) {
                return size + order.indexOf(page.getPath());
            } else {
                return (2 * size) + unsorted.indexOf(page);
            }
        });
        return unsorted.stream().sorted(comparator).collect(Collectors.toList());//if not in the list put in the end as they are .collect(toList());
    }

    private List<String> getSorting(String locale) {
        String key = "offerOrder" + locale.toUpperCase();
        String[] order = getProperties().get(key, String[].class);
        if (order == null) {
            order = getPageProperties().get(key, String[].class);
        }
        return order == null ? Collections.emptyList() : Arrays.asList(order);

    }


    public List<ItemBean> getItems() {
        return items;
    }


    private boolean isExclusiveOffer(Page page) {
        return "/apps/silversea/silversea-com/templates/exclusiveoffer".equals(page.getProperties().get("cq:template", ""));
    }


    private boolean isExclusiveOfferAvailableAndNotGhost(Page page) {
        return Optional.ofNullable(page).map(eo -> eo.adaptTo(ExclusiveOfferVariedModel.class)).filter(exclusiveOffer -> exclusiveOffer.getGeomarkets().contains(geomarket)).filter(exclusiveOffer -> !exclusiveOffer.isGhostOffer()).isPresent();
    }

    private boolean isExclusiveOfferInCategory(Page page, List<String> categories) {
        return categories.isEmpty() || Optional.ofNullable(page)
                .map(eo -> eo.adaptTo(ExclusiveOfferVariedModel.class))
                .map(ExclusiveOfferModel::getCategoryEO)
                .map(Stream::of)
                .filter(eoCategories -> eoCategories.anyMatch(categories::contains))
                .isPresent();

    }

    private Optional<ItemBean> toVariation(Page page) {
        return Optional.ofNullable(page)
                .filter(this::isExclusiveOffer)
                .map(that -> that.adaptTo(ExclusiveOfferVariedModel.class))
                .map(exclusiveOfferModel -> {
                    if (!exclusiveOfferModel.getActiveSystem()) { //Still rely on the good old system
                        //Check if there is a variation that apply
                        List<ExclusiveOfferModel> availableEOVariation = exclusiveOfferModel.getVariations();
                        for (ExclusiveOfferModel eoVar : availableEOVariation) {
                            String fullGeo = String.join("||", eoVar.getGeomarkets());
                            if (fullGeo.contains("/" + countryCode)) {
                                if (!eoVar.getDescription().isEmpty()) {
                                    exclusiveOfferModel.setVariedDescription(eoVar.getDescription());
                                    exclusiveOfferModel.setVariedTitle(eoVar.getTitle());
                                }
                            }
                        }
                    } else { //rely on the brand new system
                        EoConfigurationBean eoConfig = new EoConfigurationBean();
                        eoConfig.setShortTitleMain(true);
                        eoConfig.setShortDescriptionMain(true);
                        eoConfig.setActiveSystem(exclusiveOfferModel.getActiveSystem());
                        Optional.ofNullable(parseExclusiveOffer(eoConfig, exclusiveOfferModel)).ifPresent(tncEntry -> {
                            exclusiveOfferModel.setVariedDescription(tncEntry.getShortDescription());
                            exclusiveOfferModel.setVariedTitle(tncEntry.getShortTitle());
                        });
                    }
                    return ItemBean.fromEo(exclusiveOfferModel);
                });
    }

    public static class ItemBean {
        private final String title;
        private final String description;
        private final String excerpt;
        private final String path;
        private final String lastSplit;

        static ItemBean fromEo(ExclusiveOfferVariedModel exclusiveOffer) {
            return new ItemBean(exclusiveOffer.getVariedTitle(), exclusiveOffer.getVariedDescription(), exclusiveOffer.getDescription(), exclusiveOffer.getPath());
        }

        static ItemBean fromDestination(DestinationModel destinationModel) {
            return new ItemBean(destinationModel.getTitle(), destinationModel.getDescription(), destinationModel.getExcerpt(), destinationModel.getPath());
        }

        static ItemBean fromShip(ShipModel shipModel) {
            return new ItemBean(shipModel.getTitle(), shipModel.getLongDescription(), shipModel.getExcerpt(), shipModel.getPath());
        }

        static ItemBean fromPage(Page page) {
            ShipModel shipModel = page.adaptTo(ShipModel.class);
            DestinationModel destinationModel = page.adaptTo(DestinationModel.class);
            ExclusiveOfferVariedModel exclusiveOffer = page.adaptTo(ExclusiveOfferVariedModel.class);
            if (shipModel != null) {
                return fromShip(shipModel);
            } else if (destinationModel != null) {
                return fromDestination(destinationModel);
            } else if (exclusiveOffer != null) {
                return fromEo(exclusiveOffer);
            } else {
                return new ItemBean("", "", "", "");
            }
        }

        private ItemBean(String title, String description, String excerpt, String path) {
            this.title = title;
            this.description = description;
            this.path = path;
            if (StringUtils.isNotEmpty(excerpt)) {
                this.excerpt = excerpt;
            } else {
                this.excerpt = description;
            }
            String[] split = this.path.split("/");
            this.lastSplit = split[split.length - 1];
        }


        public String getExcerpt() {
            return excerpt;
        }

        public String getTitle() {
            return title;
        }


        public String getDescription() {
            return description;
        }


        public String getPath() {
            return path;
        }


        public String getLastSplit() {
            return lastSplit;
        }

    }
}
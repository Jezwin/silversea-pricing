package com.silversea.ssc.aem.components.editorial;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.models.DestinationModel;

public class DestinationSliderUse extends AbstractGeolocationAwareUse {

    private static final Logger log = LoggerFactory.getLogger(DestinationSliderUse.class);

    private List<SingleItem> itemList = new LinkedList<SingleItem>();

    private String itemsOrder;

    private Boolean isShip = false;

    private Boolean isDestination = false;

    @Override
    public void activate() throws Exception {
        super.activate();
        String itemPageRoot = (String) getProperties().get("itemsReference", String.class);
        /*
        Use for Destination Sider inside Mozaic Slider AB Test
        TODO: delete after ab test
        */
        if (StringUtils.isEmpty(itemPageRoot)) {
            itemPageRoot = (String) getProperties().get("destinationsReference", String.class);
        }

        if (itemPageRoot != null) {
            Resource res = getResourceResolver().resolve(itemPageRoot);
            // Retrieve all destinations
            if (res != null) {
                Page rootPage = res.adaptTo(Page.class);
                Iterator<Page> it = rootPage.listChildren(null, false);

                while (it.hasNext()) {
                    Page currentPage = it.next();
                    String currentTemplate = currentPage.getProperties().get("cq:template", "");
                    if (currentTemplate.equals("/apps/silversea/silversea-com/templates/destination")) {
                        SingleItem sItem = new SingleItem(currentPage);
                        itemList.add(sItem);
                        isDestination = true;
                    } else if (currentTemplate.equals("/apps/silversea/silversea-com/templates/ship")) {
                        SingleItem sItem = new SingleItem(currentPage);
                        itemList.add(sItem);
                        isShip = true;
                    }
                }
            }

            if (isDestination) {
                itemsOrder = selectItemsOrderByMarket("destinationsOrder");
            } else if (isShip) {
                itemsOrder = selectItemsOrderByMarket("shipsOrder");
            }

            if (StringUtils.isNotEmpty(itemsOrder)) {
                itemList = applyCustomOrder(this.itemList, itemsOrder);
            }

            log.debug("Found {} destinations", itemList.size());
        } else if (getProperties().get("activatePathList") != null && getProperties().get("activatePathList", Boolean.class)) {
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
                                    itemList.add(new SingleItem(p));
                                }
                            }


                        }
                    }
                }
            }
        }
    }

    /**
     * @return destinationsOrder: user destinations order by market
     */
    private String selectItemsOrderByMarket(String DefaultName) {
        String itemsOrder = null;
        String keyItemByMarket = DefaultName;

        if (geomarket != null) {
            switch (geomarket) {
                case "eu":
                    keyItemByMarket = DefaultName + "EU";
                    break;
                case "as":
                    keyItemByMarket = DefaultName + "AS";
                    break;
                case "uk":
                    keyItemByMarket = DefaultName + "UK";
                    break;
                default:
                    keyItemByMarket = DefaultName;
                    break;
            }
        }

        if (getProperties().get(keyItemByMarket, String.class) != null) {
            //specific component order
            itemsOrder = getProperties().get(keyItemByMarket, String.class);
        } else {
            //global order
            itemsOrder = getCurrentStyle().get(keyItemByMarket, String.class);
        }

        return itemsOrder;
    }

    /**
     * @param originalList List to order
     * @param orderToApply Custom order to apply
     * @return sorted destination list
     */
    private List<SingleItem> applyCustomOrder(List<SingleItem> originalList, String orderToApply) {
        log.info("Processing user order...");

        List<SingleItem> destinationsWithOrder = new LinkedList<SingleItem>();
        Map<Integer, SingleItem> destinationTmp = new HashMap<Integer, SingleItem>();
        // initialization needed to start with 0 in the list (incrementAndGet)
        AtomicInteger counter = new AtomicInteger(-1);

        /*
         * Parsing custom order to create a Map. Key: destination id, value:
         * position
         */
        String[] keys = orderToApply.trim().split(";");
        Map<String, Integer> positionMap = Arrays.asList(keys).stream()
                .collect(Collectors.toMap((c) -> c, (c) -> counter.incrementAndGet()));

        /*
         * Create a Map that represent the sorted destination list. Not use
         * classic List for IndexOutOfBoundsException exception
         */
        originalList.forEach((item) -> {
            String destId = item.getSingleItemId();

            if (positionMap != null && positionMap.containsKey(destId)) {
                int key = positionMap.get(destId);
                destinationTmp.put(key, item);
            }
        });

        // Create the result sorted list
        destinationTmp.forEach((k, v) -> {
            destinationsWithOrder.add(v);
        });

        return destinationsWithOrder;
    }

    /**
     * @return destinations available for all cruises for this lang
     */
    public List<SingleItem> getItemList() {
        return itemList;
    }

    public class SingleItem {
        private Page page;

        private String title;

        private String navigationTitle;

        private String excerpt;

        private String SingleItemId;

        private String description;

        private String path;

        public SingleItem() {

        }

        public SingleItem(Page page) {
            String currentTemplate = page.getProperties().get("cq:template", "");
            if (currentTemplate.equals("/apps/silversea/silversea-com/templates/destination")) {
                this.page = page;
                this.path = page.getPath();
                ValueMap currentVal = page.getProperties();
                this.title = currentVal.get("jcr:title", String.class);
                this.navigationTitle = currentVal.get("navTitle", String.class);
                this.excerpt = currentVal.get("excerpt", String.class);
                this.SingleItemId = currentVal.get("destinationId", String.class);
                this.description = currentVal.get("jcr:description", String.class);
            } else if (currentTemplate.equals("/apps/silversea/silversea-com/templates/ship")) {
                this.page = page;
                this.path = page.getPath();
                ValueMap currentVal = page.getProperties();
                this.title = currentVal.get("jcr:title", String.class);
                this.navigationTitle = currentVal.get("navTitle", String.class);
                this.excerpt = currentVal.get("excerpt", String.class);
                this.SingleItemId = currentVal.get("shipId", String.class);
                this.description = currentVal.get("jcr:description", String.class);
            } else {
                this.page = page;
                this.path = page.getPath();
                ValueMap currentVal = page.getProperties();
                this.title = currentVal.get("jcr:title", String.class);
                this.navigationTitle = currentVal.get("navTitle", String.class);
                this.excerpt = currentVal.get("excerpt", String.class);
                this.SingleItemId = "0";
                this.description = currentVal.get("jcr:description", String.class);
            }
        }

        public Page getPage() {
            return page;
        }

        public String getTitle() {
            return title;
        }

        public String getNavigationTitle() {
            return navigationTitle;
        }

        public String getExcerpt() {
            return excerpt;
        }

        public String getSingleItemId() {
            return SingleItemId;
        }

        public String getDescription() {
            return description;
        }

        public String getPath() {
            return path;
        }
    }
}

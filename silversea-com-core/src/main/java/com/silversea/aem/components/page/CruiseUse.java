package com.silversea.aem.components.page;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.day.cq.commons.Externalizer;
import com.day.cq.dam.api.Asset;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.EoBean;
import com.silversea.aem.components.beans.EoConfigurationBean;
import com.silversea.aem.components.beans.ExclusiveOfferItem;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.DiningModel;
import com.silversea.aem.models.ExclusiveOfferModel;
import com.silversea.aem.models.FeatureModel;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.models.PortModel;
import com.silversea.aem.models.PriceModel;
import com.silversea.aem.models.SilverseaAsset;
import com.silversea.aem.models.SuiteModel;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.utils.AssetUtils;
import com.silversea.aem.utils.PathUtils;

public class CruiseUse extends EoHelper {

	private CruiseModel cruiseModel;

	private String previous;

	private String next;

	private int excursionsNumber = 0;

	private int hotelsNumber = 0;

	private int landProgramsNumber = 0;

	private List<SilverseaAsset> suitesAssetsList = new ArrayList<>();

	private List<SilverseaAsset> diningsAssetsList = new ArrayList<>();

	private List<SilverseaAsset> publicAreasAssetsList = new ArrayList<>();

	private List<SilverseaAsset> itinerariesAssetsList = new ArrayList<>();
	
	private List<SilverseaAsset> virtualTourAssetsList = new ArrayList<>();

	private List<SuitePrice> prices = new ArrayList<>();

	private PriceModel lowestPrice = null;

	private boolean isWaitList = true;
	
	private boolean isVS = false;

	private List<FeatureModel> enrichmentsFeatures = new ArrayList<>();

	private List<ExclusiveOfferItem> exclusiveOffers = new ArrayList<>();

	private Locale locale;

	private String currentPath;

	private String ccptCode;
	
	private String taCode;

	@Override
	public void activate() throws Exception {
		super.activate();

		locale = getCurrentPage().getLanguage(false);

		setCurrentPath(getSlingScriptHelper().getService(Externalizer.class).publishLink(getResourceResolver(),
				getCurrentPage().getPath()));

		String[] selectors = getRequest().getRequestPathInfo().getSelectors();
		taCode = "";
		for (String selectorInfo : selectors) {
			if (selectorInfo.contains("ccpt_")) {
				setCcptCode(selectorInfo.replace("ccpt_", "."));
			}
			if (selectorInfo.contains("ta_")) {
				setTaCode(selectorInfo.replace("ta_", "."));
			}
		}

		// init cruise model from current page
		if (getRequest().getAttribute("cruiseModel") != null) {
			cruiseModel = (CruiseModel) getRequest().getAttribute("cruiseModel");
		} else {
			cruiseModel = getCurrentPage().adaptTo(CruiseModel.class);
			getRequest().setAttribute("cruiseModel", cruiseModel);
		}

		if (cruiseModel == null) {
			throw new Exception("Cannot get cruise model");
		}

		String shipName = (cruiseModel.getShip() != null) ? cruiseModel.getShip().getName() : null;
		searchPreviousAndNextCruise(shipName);
		
		Map<String, List<SilverseaAsset>> mapAsset = mapAsset =  AssetUtils.addAllShipAreaAssets(getResourceResolver(), cruiseModel.getShip().getSuites());
		virtualTourAssetsList = mapAsset.get("assetsVirtualTour");
		
		if (cruiseModel.getShip() != null && StringUtils.isNotEmpty(cruiseModel.getShip().getPhotoVideoSuiteSelectionReference())) {
			suitesAssetsList = AssetUtils.buildSilverseaAssetList(cruiseModel.getShip().getPhotoVideoSuiteSelectionReference(), getResourceResolver(), null);
		} else {
			suitesAssetsList = mapAsset.get("assets");
		}
			
		// init assets from ship areas
		mapAsset = AssetUtils.addAllShipAreaAssets(getResourceResolver(), cruiseModel.getShip().getDinings());
		diningsAssetsList = mapAsset.get("assets");
		virtualTourAssetsList.addAll(mapAsset.get("assetsVirtualTour"));
		mapAsset = AssetUtils.addAllShipAreaAssets(getResourceResolver(), cruiseModel.getShip()
				.getPublicAreas());
		publicAreasAssetsList = mapAsset.get("assets");
		virtualTourAssetsList.addAll(mapAsset.get("assetsVirtualTour"));
		List<Asset> itinerariesAssets = new ArrayList<>();
		
		if (StringUtils.isNotBlank(cruiseModel.getAssetSelectionReference())) {
			itinerariesAssets
					.addAll(AssetUtils.buildAssetList(cruiseModel.getAssetSelectionReference(), getResourceResolver()));
		}

		for (Asset asset : itinerariesAssets) {
			SilverseaAsset sscAsset = new SilverseaAsset();
			sscAsset.setPath(asset.getPath());
			sscAsset.setName(asset.getName());
			itinerariesAssetsList.add(sscAsset);
		}

		// init assets from itinerary and cruise itself
		for (ItineraryModel itinerary : cruiseModel.getCompactedItineraries()) {
			PortModel portModel = itinerary.getPort();
			
			if (itinerary.getDepartDate() != null && itinerary.getDate() != null) {
				int numberDays = itinerary.getDepartDate().get(Calendar.DATE) - itinerary.getDate().get(Calendar.DATE);
				itinerary.setNumberDays(numberDays);
			}
			
			
			
			if (portModel != null) {
				String assetSelectionReference = portModel.getAssetSelectionReference();

				if (StringUtils.isNotBlank(assetSelectionReference)) {
					itinerariesAssets = AssetUtils.buildAssetList(assetSelectionReference, getResourceResolver());
					for (Asset asset : itinerariesAssets) {
						SilverseaAsset sscAsset = new SilverseaAsset();
						sscAsset.setPath(asset.getPath());
						sscAsset.setName(asset.getName());
						sscAsset.setLabel(portModel.getTitle());
						itinerariesAssetsList.add(sscAsset);
					}
				}
			}
		}

		// init number of elements (excursions, hotels, land programs)
		for (ItineraryModel itinerary : cruiseModel.getCompactedItineraries()) {
			excursionsNumber += itinerary.getHasDedicatedShorex() ? itinerary.getExcursions().size()
					: itinerary.getPort().getExcursions().size();
			hotelsNumber += itinerary.getHotels().size();
			landProgramsNumber += itinerary.getLandPrograms().size();
		}

		// init prices based on geolocation
		for (PriceModel priceModel : cruiseModel.getPrices()) {
			if (priceModel.getGeomarket() != null && priceModel.getGeomarket().equals(geomarket)
					&& priceModel.getCurrency().equals(currency)) {
				// Adding price to suites/prices mapping
				boolean added = false;

				for (SuitePrice price : prices) {
					if (price.getSuite().equals(priceModel.getSuite())) {
						price.add(priceModel);

						added = true;
					}
				}

				if (!added) {
					prices.add(new SuitePrice(priceModel.getSuite(), priceModel, locale, priceModel.getSuiteCategory()));
				}

				// Init lowest price
				if (!priceModel.isWaitList()) {
					if (lowestPrice == null) {
						lowestPrice = priceModel;
					} else if (lowestPrice.getComputedPrice() > priceModel.getComputedPrice()) {
						lowestPrice = priceModel;
					}
					// Init wait list
					isWaitList = false;
				}
			}
		}

		// init enrichments features
		for (FeatureModel feature : cruiseModel.getFeatures()) {
			if (feature.getFeatureCode() != null
					&& !feature.getFeatureCode().equals(WcmConstants.FEATURE_CODE_VENETIAN_SOCIETY)) {
				enrichmentsFeatures.add(feature);
			}else if(feature.getFeatureCode() != null
					&&feature.getFeatureCode().equals(WcmConstants.FEATURE_CODE_VENETIAN_SOCIETY)){
				isVS = true;
			}
		}

		// init exclusive offers based on geolocation
    	EoConfigurationBean eoConfig = new EoConfigurationBean();
		eoConfig.setTitleVoyage(true);
		eoConfig.setShortDescriptionVoyage(true);
		eoConfig.setDescriptionMain(true);
		eoConfig.setFootnoteVoyage(true);
		eoConfig.setMapOverheadVoyage(true);
		eoConfig.setCruiseFareVoyage(true);
		for (ExclusiveOfferModel exclusiveOffer : cruiseModel.getExclusiveOffers()) {
			if (exclusiveOffer.getGeomarkets() != null && exclusiveOffer.getGeomarkets().contains(geomarket)) {
				try{
					eoConfig.setActiveSystem(exclusiveOffer.getActiveSystem());
						
					EoBean result = super.parseExclusiveOffer(eoConfig, exclusiveOffer);
					exclusiveOffers.add(
							new ExclusiveOfferItem(exclusiveOffer, countryCode, cruiseModel.getDestination().getPath(), result));
			
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
	}

	/**
	 * Search previous and next cruise to show link PREVIOUS VOYAGE | NEXT VOYAGE
	 * based on ship
	 * 
	 * @param shipName
	 *            Ship name of the current cruise to show
	 */
	private void searchPreviousAndNextCruise(String shipName) {
		final String lang = LanguageHelper.getLanguage(getCurrentPage());
		final CruisesCacheService cruisesCacheService = getSlingScriptHelper().getService(CruisesCacheService.class);

		List<CruiseModelLight> allCruises = (cruisesCacheService != null) ? cruisesCacheService.getCruises(lang) : null;

		if (allCruises != null) {
			// Security adaptation: If voyage is in the past-do not display them
			List<CruiseModelLight> newAllCruises = new ArrayList<>();
			for (CruiseModelLight cruise : allCruises) {
				if (cruise.getStartDate().after(Calendar.getInstance()) && cruise.isVisible()) {
					newAllCruises.add(cruise);
				}
			}
			allCruises = newAllCruises;

			// needed inside filter
			AtomicInteger indexCurrentCruise = new AtomicInteger(-1);
			AtomicInteger indexLoop = new AtomicInteger(-1);
			/*
			 * Sort all cruise based on departed date before filter by ship becuase we will
			 * save the index of the current voyage inside the list
			 */
			allCruises.sort((Comparator.comparing(CruiseModelLight::getStartDate)));

			/*
			 * We filter by ship based on current cruise and we save the index of current
			 * cruise inside the list (to get previous and next)
			 */
			List<CruiseModelLight> listCruiseFilterByShip = allCruises.stream().filter(cruise -> {
				String shipNameElement = cruise.getShip().getName();
				boolean isToInsert = shipName.equalsIgnoreCase(shipNameElement);
				boolean isCurrentCruise = cruise.getCruiseCode().equalsIgnoreCase(cruiseModel.getCruiseCode());
				if (isToInsert) {
					indexLoop.incrementAndGet();
					if (isCurrentCruise && indexCurrentCruise.get() < 0) {
						indexCurrentCruise.set(indexLoop.get());
					}
				}
				return isToInsert;
			}).collect(Collectors.toList());

			// Get Previous and Next Cruise
			if (indexCurrentCruise.get() > -1) {
				Integer previousCruiseIndex = indexCurrentCruise.get() - 1;
				Integer nextCruiseIndex = indexCurrentCruise.get() + 1;
				this.previous = (previousCruiseIndex >= 0) ? listCruiseFilterByShip.get(previousCruiseIndex).getPath()
						: null;
				this.next = (nextCruiseIndex < listCruiseFilterByShip.size())
						? listCruiseFilterByShip.get(nextCruiseIndex).getPath()
						: null;
			}
		}
	}

	/**
	 * @return cruise model
	 */
	public CruiseModel getCruiseModel() {
		return cruiseModel;
	}

	/**
	 * @return true if at least on itinerary have an excursion, land program or
	 *         hotels
	 */
	public boolean getItinerariesHasElements() {
		for (ItineraryModel itinerary : cruiseModel.getItineraries()) {
			if (itinerary.getExcursions().size() > 0 || itinerary.getLandPrograms().size() > 0
					|| itinerary.getHotels().size() > 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return true if at least on itinerary have an excursion
	 */
	public boolean getItinerariesHasExcursions() {
		for (ItineraryModel itinerary : cruiseModel.getItineraries()) {
			if (itinerary.getExcursions().size() > 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return true if at least on itinerary have an hotels
	 */
	public boolean getItinerariesHasHotels() {
		for (ItineraryModel itinerary : cruiseModel.getItineraries()) {
			if (itinerary.getHotels().size() > 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return true if at least on itinerary have a land program
	 */
	public boolean getItinerariesHasLandPrograms() {
		for (ItineraryModel itinerary : cruiseModel.getItineraries()) {
			if (itinerary.getLandPrograms().size() > 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return the number of excursions, of the itineraries or the attached ports
	 */
	public int getExcursionsNumber() {
		return excursionsNumber;
	}

	/**
	 * @return the number of hotels, of the itineraries or the attached ports
	 */
	public int getHotelsNumber() {
		return hotelsNumber;
	}

	/**
	 * @return the number of land programs, of the itineraries or the attached ports
	 */
	public int getLandProgramsNumber() {
		return landProgramsNumber;
	}

	/**
	 * @return get previous cruise in the destination
	 */
	public String getPrevious() {
		return previous;
	}

	/**
	 * @return get next cruise in the destination
	 */
	public String getNext() {
		return next;
	}

	/**
	 * @return assets from suites of the ship
	 */
	public List<SilverseaAsset> getAllAssetForSuite() {
		return suitesAssetsList;
	}

	/**
	 * @return assets from dinings of the ship
	 */
	public List<SilverseaAsset> getAllAssetForDinning() {
		return diningsAssetsList;
	}

	/**
	 * @return assets from public areas of the ship
	 */
	public List<SilverseaAsset> getAllAssetForPublicArea() {
		return publicAreasAssetsList;
	}

	/**
	 * @return assets form itinerary (cities) and the cruise itself
	 */
	public List<SilverseaAsset> getAllAssetForItinerary() {
		return itinerariesAssetsList;
	}

	/**
	 * @return assets form itinerary and the cruise itself and itinerary map
	 */
	public List<SilverseaAsset> getAllAssetWithItinerary() {
		List<SilverseaAsset> listAssetWithMap = new ArrayList<SilverseaAsset>(itinerariesAssetsList);
		String itineraryMap = (getCruiseModel() != null) ? getCruiseModel().getItinerary() : null;
		final Resource members = (itineraryMap != null) ? getResourceResolver().getResource(itineraryMap) : null;
		if (members != null) {
			Asset asset = members.adaptTo(Asset.class);
			if (asset != null) {
				SilverseaAsset sscAsset = new SilverseaAsset();
				sscAsset.setName(asset.getName());
				sscAsset.setPath(asset.getPath());
				listAssetWithMap.add(0, sscAsset);
			}
		}
		return listAssetWithMap;
	}

	/**
	 * @return the language of the current page
	 */
	public String getPageLanguage() {
		return getCurrentPage().getLanguage(false).getLanguage();
	}

	/**
	 * @return the request quote page
	 */
	public String getRequestQuotePagePath() {
		return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage());
	}

	/**
	 * @return common list for dinings and public areas
	 */
	public List<SilverseaAsset> getAllAssetForDinningNPublicAreas() {
		return Stream.concat(getAllAssetForDinning().stream(), getAllAssetForPublicArea().stream())
				.collect(Collectors.toList());
	}

	/**
	 * @return all the assets of the gallery
	 */
	public LinkedHashMap<String, List<SilverseaAsset>> getCruiseGallery() {
		LinkedHashMap<String, List<SilverseaAsset>> gallery;
		gallery = new LinkedHashMap<>();

		if (getAllAssetForItinerary() != null && !getAllAssetForItinerary().isEmpty()) {
			gallery.put("voyage", getAllAssetForItinerary());
		}
		if (getAllAssetForSuite() != null && !getAllAssetForSuite().isEmpty()) {
			gallery.put("suites", getAllAssetForSuite());
		}
		if (getAllAssetForDinning() != null && !getAllAssetForDinning().isEmpty()) {
			gallery.put("dinings", getAllAssetForDinning());
		}
		if (getAllAssetForPublicArea() != null && !getAllAssetForPublicArea().isEmpty()) {
			gallery.put("public-areas", getAllAssetForPublicArea());
		}
		if (getVirtualTourAssetsList() != null && !getVirtualTourAssetsList().isEmpty()) {
			gallery.put("virtual-tour", getVirtualTourAssetsList());
		}
		// TODO : gallery.put("ship-exteriors", value);

		return gallery;
	}

	/**
	 * Put the cruise itinerary map inside voyages gallery. The cruise itinerary map
	 * will be the first element of the list
	 * 
	 * @return gallery
	 */
	public LinkedHashMap<String, List<SilverseaAsset>> getCruiseOverviewGallery() {
		LinkedHashMap<String, List<SilverseaAsset>> gallery = getCruiseGallery();
		String itineraryMap = (getCruiseModel() != null) ? getCruiseModel().getItinerary() : null;
		final Resource members = (itineraryMap != null) ? getResourceResolver().getResource(itineraryMap) : null;
		if (members != null) {
			Asset asset = members.adaptTo(Asset.class);
			List<SilverseaAsset> voyages = gallery.get("voyage");
			if (asset != null && !voyages.isEmpty()) {
				SilverseaAsset sscAsset = new SilverseaAsset();
				sscAsset.setName(asset.getName());
				sscAsset.setPath(asset.getPath());
				voyages.add(0, sscAsset);
				gallery.put("voyage", voyages);
			}
		}

		return gallery;
	}

	/**
	 * @return return prices corresponding to the current geolocation
	 */
	public List<SuitePrice> getPrices() {
		return prices;
	}

	/**
	 * @return the lowest price for this cruise
	 */
	public PriceModel getLowestPrice() {
		return lowestPrice;
	}

	/**
	 * @return the computed price, formatted with the locale according to the
	 *         geolocation
	 */
	public String getComputedPriceFormated() {
		return PriceHelper.getValue(locale, getLowestPrice().getComputedPrice());
	}

	/**
	 * @return true is the cruise is on wait list
	 */
	public boolean isWaitList() {
		return isWaitList;
	}
	
	public boolean isVS() {
		return isVS;
	}

	/**
	 * @return get the enrichments features (without venitian society)
	 */
	public List<FeatureModel> getEnrichmentsFeatures() {
		return enrichmentsFeatures;
	}

	/**
	 * @return exclusive offers of this cruise
	 */
	public List<ExclusiveOfferItem> getExclusiveOffers() {
		return exclusiveOffers;
	}

	/**
	 * @return cruise fare additions of all exclusive offers of this cruise
	 */
	public List<String> getExclusiveOffersCruiseFareAdditions() {
		final List<String> cruiseFareAdditions = new ArrayList<>();

		for (final ExclusiveOfferItem exclusiveOffer : exclusiveOffers) {
			cruiseFareAdditions.addAll(exclusiveOffer.getCruiseFareAdditions());
		}

		return cruiseFareAdditions;
	}

	/**
	 * @return the total number of cruise fares additions
	 */
	public int getCruiseFareAdditionsSize() {
		return getExclusiveOffersCruiseFareAdditions().size() + cruiseModel.getCruiseFareAdditions().size();
	}

	/**
	 * @return first map overhead
	 */
	public String getMapOverHead() {
		for (final ExclusiveOfferItem exclusiveOffer : exclusiveOffers) {
			if (exclusiveOffer.getMapOverHead() != null) {
				return exclusiveOffer.getMapOverHead();
			}
		}

		return null;
	}

	/**
	 * @return the price prefix from the first exclusive offer
	 */
	public String getPricePrefix() {
		for (ExclusiveOfferItem exclusiveOffer : exclusiveOffers) {
			if (exclusiveOffer.getPricePrefix() != null) {
				return exclusiveOffer.getPricePrefix();
			}
		}

		return null;
	}

	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}
	
	public List<SilverseaAsset> getVirtualTourAssetsList() {
		return virtualTourAssetsList;
	}

	public String getCcptCode() {
		return ccptCode;
	}
	
	public String getTaCode() {
		return taCode;
	}

	public void setCcptCode(String ccptCode) {
		this.ccptCode = ccptCode;
	}
	
	public void setTaCode(String taCode) {
		this.taCode = taCode;
	}
}
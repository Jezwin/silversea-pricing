package com.silversea.aem.importers.servlets;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.services.BrochuresImporter;
import com.silversea.aem.importers.services.CcptImporter;
import com.silversea.aem.importers.services.CitiesImporter;
import com.silversea.aem.importers.services.ComboCruisesImporter;
import com.silversea.aem.importers.services.CountriesImporter;
import com.silversea.aem.importers.services.CruisesExclusiveOffersImporter;
import com.silversea.aem.importers.services.CruisesImporter;
import com.silversea.aem.importers.services.CruisesItinerariesExcursionsImporter;
import com.silversea.aem.importers.services.CruisesItinerariesHotelsImporter;
import com.silversea.aem.importers.services.CruisesItinerariesImporter;
import com.silversea.aem.importers.services.CruisesItinerariesLandProgramsImporter;
import com.silversea.aem.importers.services.CruisesPricesImporter;
import com.silversea.aem.importers.services.ExclusiveOffersImporter;
import com.silversea.aem.importers.services.FeaturesImporter;
import com.silversea.aem.importers.services.HotelsImporter;
import com.silversea.aem.importers.services.LandProgramsImporter;
import com.silversea.aem.importers.services.MultiCruisesImporter;
import com.silversea.aem.importers.services.MultiCruisesItinerariesExcursionsImporter;
import com.silversea.aem.importers.services.MultiCruisesItinerariesHotelsImporter;
import com.silversea.aem.importers.services.MultiCruisesItinerariesImporter;
import com.silversea.aem.importers.services.MultiCruisesItinerariesLandProgramsImporter;
import com.silversea.aem.importers.services.MultiCruisesPricesImporter;
import com.silversea.aem.importers.services.ShoreExcursionsImporter;
import com.silversea.aem.importers.services.StyleCache;

@SlingServlet(paths = "/bin/api-import-diff")
public class UpdateImportServlet extends SlingSafeMethodsServlet {

	static final private Logger LOGGER = LoggerFactory.getLogger(UpdateImportServlet.class);

	private enum Mode {
		cities, excursions, hotels, landprograms,

		countries, features, brochures,

		exclusiveoffers,

		cruises, itineraries, itinerarieshotels, itinerariesexcursions, itinerarieslandprograms, prices, cruisesexclusiveoffers,
		
		multicruises, multicruisesitineraries, multicruisesitinerarieshotels, multicruisesitinerariesexcursions, multicruisesitinerarieslandprograms, multicruisesprices,

		combocruises, combocruisessegmentsactivation,

		ccptgeneration,

		stylesconfiguration,
		
		excursionsDisactive, landProgramsDisactive,hotelsDisactive, citiesDisactive,
		
		importAllPortImages
	}

	@Reference
	private CitiesImporter citiesImporter;

	@Reference
	private HotelsImporter hotelsImporter;

	@Reference
	private LandProgramsImporter landProgramsImporter;

	@Reference
	private ShoreExcursionsImporter shoreExcursionsImporter;

	@Reference
	private CountriesImporter countriesImporter;

	@Reference
	private CruisesImporter cruisesImporter;

	@Reference
	private CruisesItinerariesHotelsImporter cruisesItinerariesHotelsImporter;

	@Reference
	private CruisesItinerariesLandProgramsImporter cruisesItinerariesLandProgramsImporter;

	@Reference
	private CruisesItinerariesExcursionsImporter cruisesItinerariesExcursionsImporter;

	@Reference
	private CruisesItinerariesImporter cruisesItinerariesImporter;

	@Reference
	private CruisesPricesImporter cruisesPricesImporter;

	@Reference
	private CruisesExclusiveOffersImporter cruisesExclusiveOffersImporter;
	
	@Reference
    private MultiCruisesImporter multiCruisesImporter;

    @Reference
    private MultiCruisesItinerariesImporter multiCruisesItinerariesImporter;

    @Reference
    private MultiCruisesPricesImporter multiCruisesPricesImporter;

    @Reference
    private MultiCruisesItinerariesHotelsImporter multiCruisesItinerariesHotelsImporter;

    @Reference
    private MultiCruisesItinerariesLandProgramsImporter multiCruisesItinerariesLandProgramsImporter;

    @Reference
    private MultiCruisesItinerariesExcursionsImporter multiCruisesItinerariesExcursionsImporter;

	@Reference
	private ComboCruisesImporter comboCruisesImporter;

	@Reference
	private ExclusiveOffersImporter exclusiveOffersImporter;

	@Reference
	private FeaturesImporter featuresImporter;

	@Reference
	private BrochuresImporter brochuresImporter;

	@Reference
	private CcptImporter ccptImporter;

	@Reference
	private StyleCache styleCache;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		final String modeParam = request.getParameter("mode");
		if (modeParam == null) {
			throw new ServletException(
					"the mode parameter must be among the values : " + StringUtils.join(Mode.values(), ", "));
		}

		final Mode mode;
		try {
			mode = Mode.valueOf(modeParam);
		} catch (IllegalArgumentException e) {
			throw new ServletException(
					"the mode parameter must be among the values : " + StringUtils.join(Mode.values(), ", "));
		}

		try {
			if (mode.equals(Mode.cities)) {
				citiesImporter.updateItems();
			} else if (mode.equals(Mode.citiesDisactive)) {
				citiesImporter.DesactivateUselessPort();
			} else if (mode.equals(Mode.hotels)) {
				hotelsImporter.updateHotels();
			} else if (mode.equals(Mode.excursions)) {
				shoreExcursionsImporter.updateShoreExcursions();
			} else if (mode.equals(Mode.landprograms)) {
				landProgramsImporter.updateLandPrograms();
			} else if (mode.equals(Mode.countries)) {
				countriesImporter.importData(false);
			} else if (mode.equals(Mode.exclusiveoffers)) {
				exclusiveOffersImporter.importAllItems();
			} else if (mode.equals(Mode.features)) {
				featuresImporter.updateFeatures();
			} else if (mode.equals(Mode.cruises)) {
				cruisesImporter.updateItems();
			} else if (mode.equals(Mode.itineraries)) {
				cruisesItinerariesImporter.importAllItems(true);
			} else if (mode.equals(Mode.prices)) {
				cruisesPricesImporter.importAllItems(true);
			} else if (mode.equals(Mode.itinerarieshotels)) {
				cruisesItinerariesHotelsImporter.importAllItems(true);
			} else if (mode.equals(Mode.itinerarieslandprograms)) {
				cruisesItinerariesLandProgramsImporter.importAllItems(true);
			} else if (mode.equals(Mode.itinerariesexcursions)) {
				cruisesItinerariesExcursionsImporter.importAllItems(true);
			} else if (mode.equals(Mode.cruisesexclusiveoffers)) {
				cruisesExclusiveOffersImporter.importAllItems();
			}else if (mode.equals(Mode.multicruises)) {
				multiCruisesImporter.updateItems();
			} else if (mode.equals(Mode.multicruisesitineraries)) {
				multiCruisesItinerariesImporter.importAllItems();
			} else if (mode.equals(Mode.multicruisesprices)) {
				multiCruisesPricesImporter.importAllItems();
			} else if (mode.equals(Mode.multicruisesitinerarieshotels)) {
				multiCruisesItinerariesHotelsImporter.importAllItems();
			} else if (mode.equals(Mode.multicruisesitinerarieslandprograms)) {
				multiCruisesItinerariesLandProgramsImporter.importAllItems();
			} else if (mode.equals(Mode.multicruisesitinerariesexcursions)) {
				multiCruisesItinerariesExcursionsImporter.importAllItems();
			} else if (mode.equals(Mode.combocruises)) {
				// comboCruisesImporter.importData(true);
			} else if (mode.equals(Mode.brochures)) {
				brochuresImporter.updateBrochures();
			} else if (mode.equals(Mode.combocruisessegmentsactivation)) {
				comboCruisesImporter.markSegmentsForActivation();
			} else if (mode.equals(Mode.ccptgeneration)) {
				ccptImporter.importAllItems();
			} else if (mode.equals(Mode.stylesconfiguration)) {
				styleCache.buildCache();
			} else if (mode.equals(Mode.excursionsDisactive)) {
				shoreExcursionsImporter.disactiveAllItemDeltaByAPI();
			} else if (mode.equals(Mode.landProgramsDisactive)) {
				landProgramsImporter.disactiveAllItemDeltaByAPI();
			}  else if (mode.equals(Mode.hotelsDisactive)) {
				hotelsImporter.disactiveAllItemDeltaByAPI();
			} else if (mode.equals(Mode.importAllPortImages)) {
				citiesImporter.importAllPortImages();
			}

		} catch (ImporterException e) {
			throw new ServletException(e);
		}

		response.setContentType("text/html");
	}
}

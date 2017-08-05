package com.silversea.aem.importers.servlets;

import com.silversea.aem.importers.services.*;
import com.silversea.aem.importers.services.impl.ImportResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

@Deprecated
@SuppressWarnings("serial")
@SlingServlet(paths = "/bin/api-update-import")
public class UpdateImportServlet extends SlingSafeMethodsServlet {

    static final private Logger LOGGER = LoggerFactory.getLogger(UpdateImportServlet.class);

    private boolean isAllRuning = false;

    private int nbrError = 0;

    private int nbrSucces = 0;

    StopWatch watch = new StopWatch();
    String time = "";

    StopWatch watchAll = new StopWatch();
    String timeAll = "";

    @Reference
    private CitiesImporter updateImportCities;

    @Reference
    private HotelsImporter updateImportHotel;

    @Reference
    private LandProgramsImporter updateImportLandProgram;

    @Reference
    private ShoreExcursionsImporter updateImportShoreExcursion;

    @Reference
    private CruisesImporter cruisesImporter;

    @Reference
    private TravelAgenciesUpdateImporter updateTravalAgencies;

    @Reference
    private ExclusiveOffersUpdateImporter updateExclusiveOffers;

    @Reference
    private FeaturesImporter updateFeatures;

    @Reference
    ComboCruisesImporter ComboCruisesImporter;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        try {
            response.setContentType("text/html");
            prepareDoc(response.getWriter());

            // To Extract
            String modeParam = request.getParameter("mode");
            Boolean all = modeParam == null || (modeParam != null && ("".equals(modeParam) || "ALL".equals(modeParam)));
            Mode mode = null;
            if (!all) {
                try {
                    mode = Mode.valueOf(modeParam);
                } catch (IllegalArgumentException e) {
                    LOGGER.error("the mode parameter must be among the values : {}",
                            StringUtils.join(Mode.values(), ", "));
                    response.getWriter().write(
                            "the mode parameter must be among the values : " + StringUtils.join(Mode.values(), ", "));
                    response.getWriter().flush();
                    response.getWriter().close();
                    return;
                }
            } else {
                watchAll.reset();
                watchAll.start();
            }
            // End To Extract
            if (!isAllRuning) {
                isAllRuning = true;
                if ((all || mode.equals(Mode.cities))) {
                    response.getWriter().write("Init update import of cities ...<br/>");
                    response.getWriter().flush();
                    watch.reset();
                    watch.start();
                    final ImportResult importResult = updateImportCities.updateCities();
                    response.getWriter().write("Cities import failure number : <p>" + importResult.getErrorNumber() + "</p>");
                    response.getWriter().write("<br/>");
                    response.getWriter().write("Cities import succes number : <p>" + importResult.getSuccessNumber() + "</p>");
                    response.getWriter().write("<br/>");
                    response.getWriter().write("cities import Done<br/>");
                    watch.stop();
                    time = watch.toString();
                    response.getWriter().write("Time :<br/>" + time);
                    response.getWriter().write("<br/> ---------------- <br />");
                    response.getWriter().flush();
                }

                if (all || mode.equals(Mode.hotels)) {
                    response.getWriter().write("Init update import of hotels ...<br/>");
                    response.getWriter().flush();
                    watch.reset();
                    watch.start();
                    final ImportResult importResult = updateImportHotel.updateHotels();
                    response.getWriter().write("Hotels import failure number : <p>" + importResult.getErrorNumber() + "</p>");
                    response.getWriter().write("<br/>");
                    response.getWriter().write("hotels import succes number : <p>" + importResult.getSuccessNumber() + "</p>");
                    response.getWriter().write("<br/>");
                    response.getWriter().write("Hotels import Done<br/>");
                    watch.stop();
                    time = watch.toString();
                    response.getWriter().write("Time :<br/>" + time);
                    response.getWriter().write("<br/> ---------------- <br />");

                    response.getWriter().flush();
                }

                if (all || mode.equals(Mode.ex)) {
                    response.getWriter().write("Init update import of shorex ...<br/>");
                    response.getWriter().flush();
                    watch.reset();
                    watch.start();
                    final ImportResult importResult = updateImportShoreExcursion.updateShoreExcursions();
                    response.getWriter().write("Shorex import failure number : <p>" + importResult.getErrorNumber() + "</p>");
                    response.getWriter().write("<br/>");
                    response.getWriter().write("Shorex import succes number : <p>" + importResult.getSuccessNumber() + "</p>");
                    response.getWriter().write("<br/>");
                    response.getWriter().write("ShoreExcursions import Done<br/>");
                    watch.stop();
                    time = watch.toString();
                    response.getWriter().write("Time :<br/>" + time);
                    response.getWriter().write("<br/> ---------------- <br />");

                    response.getWriter().flush();
                }

                if (all || mode.equals(Mode.lp)) {
                    response.getWriter().write("Init import of lands programs ...<br/>");
                    response.getWriter().flush();
                    watch.reset();
                    watch.start();

                    final ImportResult importResult = updateImportLandProgram.updateLandPrograms();
                    response.getWriter().write("Land program import failure number : <p>" + importResult.getErrorNumber() + "</p>");
                    response.getWriter().write("<br/>");
                    response.getWriter().write("land program import succes number : <p>" + importResult.getSuccessNumber() + "</p>");
                    response.getWriter().write("<br/>");
                    response.getWriter().write("LandPrograms import Done<br/>");
                    watch.stop();
                    time = watch.toString();
                    response.getWriter().write("Time :<br/>" + time);
                    response.getWriter().write("<br/> ---------------- <br />");

                    response.getWriter().flush();
                }

                // TODO
                if (all || mode.equals(Mode.ta)) {
                    response.getWriter().write("Init import of travel agencies...<br/>");
                    response.getWriter().flush();
                    watch.reset();
                    watch.start();
                    updateTravalAgencies.updateImporData();
                    nbrError = updateTravalAgencies.updateImporData().getErrorNumber();
                    nbrSucces = updateTravalAgencies.updateImporData().getSuccesNumber();
                    response.getWriter().write("Travel agencies import failure number : <p>" + nbrError + "</p>");
                    response.getWriter().write("<br/>");
                    response.getWriter().write("Travel agencies import succes number : <p>" + nbrSucces + "</p>");
                    response.getWriter().write("<br/>");
                    response.getWriter().write("TravelAgencies import Done<br/>");
                    watch.stop();
                    time = watch.toString();
                    LOGGER.debug("total time to execute import off travel agency : {}", time);
                    response.getWriter().write("Time :<br/>" + time);
                    response.getWriter().write("<br/> ---------------- <br />");
                    response.getWriter().flush();
                }

                if (all || mode.equals(Mode.eo)) {
                    response.getWriter().write("Init import of exclusives offers ...<br/>");
                    response.getWriter().flush();
                    watch.reset();
                    watch.start();
                    updateExclusiveOffers.updateImporData();
                    nbrError = updateExclusiveOffers.updateImporData().getErrorNumber();
                    nbrSucces = updateExclusiveOffers.updateImporData().getSuccesNumber();
                    response.getWriter().write("Exclusive offers import failure number : <p>" + nbrError + "</p>");
                    response.getWriter().write("<br/>");
                    response.getWriter().write("Exclusives offers import succes number : <p>" + nbrSucces + "</p>");
                    response.getWriter().write("<br/>");
                    response.getWriter().write("ExclusiveOffers import Done<br/>");
                    watch.stop();
                    time = watch.toString();
                    response.getWriter().write("Time :<br/>" + time);
                    response.getWriter().write("<br/> ---------------- <br />");
                    response.getWriter().flush();
                }
                //
                // if (all || mode.equals(Mode.countries)) {
                // countriesImporter.importAllCities();
                // response.getWriter().write("Countries import Done<br/>");
                // response.getWriter().flush();
                // }
                //
                if (all || mode.equals(Mode.ft)) {
                    updateFeatures.updateFeatures();
                    response.getWriter().write("Feature import Done<br/>");
                    response.getWriter().flush();
                }
                //
                // if (all || mode.equals(Mode.brochures)) {
                // brochuresImporter.importAllBrochures();
                // response.getWriter().write("Brochures import Done<br/>");
                // response.getWriter().flush();
                // }
                //
                if (all || mode.equals(Mode.cruises)) {
                    cruisesImporter.importData(true);
                    response.getWriter().write("Cruises import Done<br/>");
                    response.getWriter().flush();
                }
                if (all || mode.equals(Mode.cc)) {
                    ComboCruisesImporter.importData(true);
                    response.getWriter().write("Cruises import Done<br/>");
                    response.getWriter().flush();
                }

            } else {
                response.getWriter().write("<br/>an other import is aleready run<br />");
                response.getWriter().flush();
            }

            if (all) {
                watchAll.stop();
                timeAll = watchAll.toString();
                response.getWriter().write("<br/> ---------------- <br />");
                response.getWriter().write("Time Global:<br/>" + timeAll);
                response.getWriter().write("<br/> ---------------- <br />");
                response.getWriter().flush();
            }
            closeDocument(response.getWriter());
        } catch (RuntimeException e) {
            // watchAll.stop();
            timeAll = watchAll.toString();
            response.getWriter().write("<br/> ---------------- <br />");
            response.getWriter().write("Time Global:<br/>" + timeAll);
            response.getWriter().write("Finished With Error : " + ExceptionUtils.getStackTrace(e));
            response.getWriter().write("<br/> ---------------- <br />");
            response.getWriter().flush();
        } finally {
            isAllRuning = false;
        }
    }

    private void prepareDoc(PrintWriter writer) {
        writer.write("<html><head><title>Launcher Test Importer</title></head>"
                + "<body><H1>Initial import...</H1><div class=\'content\'>");
        writer.flush();
    }

    private void closeDocument(PrintWriter writer) {

        writer.write("<H2>All done.</H2></div></body></html>");
        writer.close();
    }

    enum Mode {
        cities, ex, hotels, lp, ta, eo, ships, countries, ft, brochures, cruises, cc;

    }
}

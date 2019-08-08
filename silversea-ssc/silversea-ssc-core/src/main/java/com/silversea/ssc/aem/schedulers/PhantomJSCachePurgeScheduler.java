package com.silversea.ssc.aem.schedulers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.discovery.InstanceDescription;
import org.apache.sling.discovery.TopologyEvent;
import org.apache.sling.discovery.TopologyEventListener;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.ssc.aem.factory.PDFConfigFactory;

/**
 * Scheduler service to clean the phantom js floder where the PDF is temporarily
 * generated. This CRON JOB would run on scheduled time for purging the full
 * directory.
 * 
 * <p>
 * Below are the properties defined for the service which will be used to
 * configure the CRON expression,concurrent execution of the service and
 * description of the vendor information.
 * </p>
 * 
 * @author nikhil
 *
 */
@Component(label = "77Agency - Phantom Cache Purger", description = "77Agency - Phantom Cache Purger", immediate = true, metatype = true)
@Properties({
		@Property(label = "Cron expression", description = "Cron expression defining when this scheduled service will run", name = "scheduler.expression", value = ""),
		@Property(label = "Allow concurrent executions", description = "Allow concurrent executions of this scheduled service", name = "scheduler.concurrent", boolValue = false),
		@Property(label = "Vendor", name = Constants.SERVICE_VENDOR, value = "77Agency", propertyPrivate = true) })
@Service
/**
 * The runnable is implemented to override the run() method ,which will be
 * executed whenever the scheduler runs as per the information provided inside
 * cron expression.
 *
 */
public class PhantomJSCachePurgeScheduler implements Runnable, TopologyEventListener {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PhantomJSCachePurgeScheduler.class);

	/** Is the instance master. */
	private boolean isMaster = true;

	/**
	 * Injecting PDF Configuration Service into this service to fetch Phantom
	 * env specific properties
	 */
	@Reference
	private PDFConfigFactory pdfConfService;
	@Reference
	private ResourceResolverFactory rFactory;

	/**
	 * The sling settings service reference.
	 */
	@Reference
	private SlingSettingsService slingSettingsService;

	/** String to destination PDF file location. */
	private String destPdfLocation;

	/**
	 * Constant for Dest Pdf Location
	 */
	public static final String DESTINATION_PDF_LOCATION = "pdf.destination.path";

	/**
	 * The run modes collection. Required to check the value of custom set run
	 * modes.
	 */
	public Set<String> runModes = new HashSet<String>();

	/**
	 * This method is used to set the componentContext and the runmodes required
	 * in run() method.
	 * 
	 * @param ctx
	 *            The osgi component context.
	 * 
	 * @throws Exception
	 *             Any exception occurring during start/stop or during
	 *             execution.
	 */
	@Activate
	public void activate(ComponentContext ctx) {
		LOGGER.debug("Start of :- Activate method ()");
		runModes = slingSettingsService.getRunModes();
		LOGGER.debug("End of :- Activate method ()");
	}

	/**
	 * This method executes by default when the scheduler thread runs based on
	 * the CRON expression provided inside the #PhantomJSCachePurgeScheduler
	 * configurations.
	 * <p>
	 * The site {@link www.cronmaker.com} can be used to create one such CRON
	 * expression.
	 * </p>
	 *
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if (slingSettingsService.getRunModes().contains("publish")) {
			ResourceResolver resolver = null;
			try {
				resolver = rFactory.getServiceResourceResolver(null);
				this.destPdfLocation = pdfConfService.getPropertyValue(DESTINATION_PDF_LOCATION);
				LOGGER.debug("Dest Pdf Location::::" + destPdfLocation);
				if (StringUtils.isNotEmpty(destPdfLocation)) {
					/*The command wasn't taking wild cards, so had to pass it via shell*/
					String[] cmdString = new String[] { "/bin/sh", "-c", "rm -rf " + destPdfLocation + "/*" };
					LOGGER.debug("Executing command {}", cmdString[2]);
					Process process = Runtime.getRuntime().exec(cmdString);
					final Integer exitStatus = process.waitFor();
					LOGGER.info("exit status : ", exitStatus.toString());
				}

			} catch (LoginException e) {
				LOGGER.error("Error while taking the resolver login. {} {}", e, e.getMessage());
			} catch (IOException e) {
				LOGGER.error("Error while executing the command {} {}", e, e.getMessage());
			} catch (InterruptedException e) {
				LOGGER.error("The process thread interuppted. {} {}", e, e.getMessage());
				Thread.currentThread().interrupt();
			} finally {
				if (null != resolver && resolver.isLive()) {
					resolver.close();
				}
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.sling.discovery.TopologyEventListener#handleTopologyEvent(org.
	 * apache.sling.discovery.TopologyEvent)
	 */
	@Override
	public void handleTopologyEvent(TopologyEvent topologyEvent) {
		InstanceDescription instances = topologyEvent.getNewView().getLocalInstance();
		LOGGER.debug("Entry :- handleTopologyEvent method.");
		LOGGER.debug("The value of master flag is :- {} {}", isMaster, instances.isLeader());
		LOGGER.debug("Exit :- handleTopologyEvent method.");
	}
}
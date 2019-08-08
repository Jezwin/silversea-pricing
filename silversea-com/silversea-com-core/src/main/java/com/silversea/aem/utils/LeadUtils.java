package com.silversea.aem.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.DamConstants;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.servlets.LeadServlet;
import com.silversea.aem.servlets.LeadServlet.JsonMapper;

/**
 * This is the utility clas to hold th lead related methods.
 * 
 * @author nikhil
 *
 */
public class LeadUtils {
	private final static String LEAD_DATA_PATH = "/var/leadservicedata";
	private static final String CONTENT_APPLICATION_JSON = "application/json";
	private static final String UTF8_ENCODING = "utf-8";
	private static final Logger LOGGER = LoggerFactory.getLogger(LeadUtils.class);

	private LeadUtils() {

	}

	/**
	 * Serialize object to json and send it in the response
	 * 
	 * @param response
	 *            Http response
	 * @param domainObject
	 *            Object to send in the response
	 */
	public static <T> void writeDomainObject(SlingHttpServletResponse response, T domainObject) {
		response.setContentType(CONTENT_APPLICATION_JSON);
		response.setCharacterEncoding(UTF8_ENCODING);
		String json = JsonMapper.getJson(domainObject);

		try {
			response.getWriter().write(json);
		} catch (IOException e) {
			LOGGER.error("Error while writing json in the response", e);
		}
	}

	/**
	 * Method to create a random digest.
	 * 
	 * @param email
	 *            The set email id of the user.
	 * @param formatDate
	 *            The current date time in millis.
	 * @return The unique digest.
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String generateFileName(String email, String formatDate)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String digest = StringUtils.EMPTY;
		if (StringUtils.isEmpty(email)) {
			LOGGER.debug("Email on this request is null. Creating a random shuffle from date.");
			email = shuffle(formatDate.concat(formatDate));
		}
		MessageDigest algorithm = MessageDigest.getInstance("SHA-1");
		byte[] hashedBytes = algorithm.digest(email.getBytes("UTF-8"));
		digest = convertByteArrayToHexString(hashedBytes).concat(shuffle(formatDate));
		LOGGER.debug("The digested string temporary id looks like: - {}", digest);
		return digest.concat(".txt");
	}

	/**
	 * This method is used ot generate the lead data file under /var.
	 * 
	 * @param request
	 *            The obtained request parameter.
	 * @param body
	 *            The json body.
	 * @param email
	 *            The email id for generating hash.
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public static String generateLeadDataFile(final ResourceResolverFactory resourceResolverFactory,
			final SlingHttpServletRequest request, String body, String email)
					throws NoSuchAlgorithmException, UnsupportedEncodingException {
		LOGGER.debug("Starting file generation");
		final Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		String fileName = generateFileName(email, DateUtils.formatDate("yyyyMMddHHmmss", new Date()));
		try (final ResourceResolver adminResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			final Session adminSession = adminResolver.adaptTo(Session.class);
			if (adminSession == null) {
				throw new Exception("Cannot initialize session");
			}
			String path = LEAD_DATA_PATH + "/" + fileName;
			LOGGER.debug("Generating file : - {} with data :- {}", path, body);

			InputStream inputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
			LOGGER.debug("Setting file metadata..");
			if (adminSession.nodeExists(path + "/" + JcrConstants.JCR_CONTENT)) {
				Node node = adminSession.getNode(path + "/" + JcrConstants.JCR_CONTENT);
				node.setProperty(JcrConstants.JCR_DATA, adminSession.getValueFactory().createBinary(inputStream));
				node.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
				node.setProperty(JcrConstants.JCR_LAST_MODIFIED_BY, node.getSession().getUserID());
			} else {
				LOGGER.debug("jcr:content not present. Creating it...");
				Node feedNode = JcrUtil.createPath(path, true, DamConstants.NT_SLING_ORDEREDFOLDER,
						JcrConstants.NT_FILE, adminSession, false);
				Node dataNode = feedNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
				dataNode.setProperty(JcrConstants.JCR_MIMETYPE, "text/plain");
				dataNode.setProperty(JcrConstants.JCR_ENCODING, UTF8_ENCODING);
				dataNode.setProperty(JcrConstants.JCR_DATA, adminSession.getValueFactory().createBinary(inputStream));
			}
			if (adminSession.isLive() || adminSession.hasPendingChanges()) {
				adminSession.save();
			}
		} catch (LoginException loginException) {
			LOGGER.error("Exception while retrieving admin access for Resolver: {}", loginException.getMessage());
		} catch (RepositoryException repositoryException) {
			LOGGER.error("Repository Exception: {}", repositoryException);
		} catch (Exception e) {
			LOGGER.error("Exception ", e.getMessage());
		}
		return fileName.substring(0, fileName.length() - 4);
	}

	/**
	 * Logic to shuffle the items internally in a randomized way.
	 * 
	 * @param input
	 *            The input to be randomized.
	 * @return The randomly created string.
	 */
	public static String shuffle(String input) {
		List<Character> characters = new ArrayList<Character>();
		for (char c : input.toCharArray()) {
			characters.add(c);
		}
		StringBuilder output = new StringBuilder(input.length());
		while (characters.size() != 0) {
			int randPicker = (int) (Math.random() * characters.size());
			output.append(characters.remove(randPicker));
		}
		return output.toString();
	}

	/**
	 * The byte array to hext string converter for the digest.
	 * 
	 * @param arrayBytes
	 *            The array of bytes.
	 * @return The string representation of the byte array.
	 */
	public static String convertByteArrayToHexString(byte[] arrayBytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < arrayBytes.length; i++) {
			stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return stringBuffer.toString();
	}
}

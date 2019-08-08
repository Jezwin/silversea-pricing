
package com.silversea.ssc.aem.replication;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.ProviderType;

/**
 * 
 * This class converts the Service properties in the map format for their usage
 * in respective calling files.
 * 
 */
@ProviderType
public final class ParameterUtil {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ParameterUtil.class);

	/**
	 * Default private constructor. For preventing class instantiation.
	 */
	private ParameterUtil() {

	}

	/**
	 * A wrapper overloaded method, to convert the passed in array into map. The
	 * valueless key would be ignored.
	 * 
	 * @param values
	 *            The array of values
	 * @param separator
	 *            The separator.
	 * 
	 * @return The map from array.
	 */
	public static Map<String, String> convertToMap(final String[] values, final String separator) {
		return convertToMap(values, separator, false, null);
	}

	/**
	 * A wrapper overloaded method, to convert the passed in array into map. The
	 * valueless key would be ignored.
	 * 
	 * @param values
	 *            The array of values
	 * @param separator
	 *            The separator.
	 * @param allowValuelessKeys
	 *            Flag to disallow/allow valueless keys.
	 * @param defaultValue
	 *            For default values supply, if at all needed.
	 * @return The map from array.
	 * 
	 */
	public static Map<String, String> convertToMap(final String[] values, final String separator,
			final boolean allowValuelessKeys, final String defaultValue) {
		return convertToMap(values, separator, allowValuelessKeys, defaultValue, false);
	}

	/**
	 * A method, to convert the passed in array into map. The valueless key
	 * would be ignored.
	 * 
	 * @param values
	 *            The array of values
	 * @param separator
	 *            The separator.
	 * @param allowValuelessKeys
	 *            Flag to disallow/allow valueless keys.
	 * @param defaultValue
	 *            For default values supply, if at all needed.
	 * @param allowMultipleSeparators
	 *            For allowing/disallowing multiple separators.
	 * @return The map from array.
	 * 
	 */
	public static Map<String, String> convertToMap(final String[] values, final String separator,
			final boolean allowValuelessKeys, final String defaultValue, final boolean allowMultipleSeparators) {

		final Map<String, String> pairsMap = new LinkedHashMap<String, String>();

		if (values == null || values.length < 1) {
			return pairsMap;
		}

		for (final String value : values) {
			final String[] tmp = StringUtils.split(value, separator, allowMultipleSeparators ? 2 : -1);

			if (tmp.length == 1 && allowValuelessKeys) {
				if (StringUtils.startsWith(value, separator)) {
					continue;
				}

				pairsMap.put(tmp[0], defaultValue);
			} else if (tmp.length == 2) {
				pairsMap.put(tmp[0], tmp[1]);
			}
		}

		return pairsMap;
	}

}

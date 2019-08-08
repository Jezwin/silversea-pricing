package com.silversea.ssc.aem.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.ssc.aem.bean.SummaryBean;
import com.silversea.ssc.aem.constants.SSCConstants;

public class SSCUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(SSCUtils.class);

	// to prohibit instantiation
	private SSCUtils() {

	}

	/**
	 * This method splits the object string based on passed limit, the limit
	 * here is put to the number of words. There is a look-ahead here where the
	 * first part would always be semantically complete assuming the sentences
	 * ends with a DOT(.). Assumption is the text provided would always normally
	 * or with a 'p' tag. The logic shall remove the tag, preserve it and remove
	 * the aforesaid words and then re-attach the tag along with the overflow
	 * tag to the remaining part. The code also assumes that the first part will
	 * not open any further HTML tags in the given limit
	 * 
	 * 
	 * 
	 * @param input
	 *            The passed input string with/without the HTML tags.
	 * @param limit
	 *            The integer limit applied for truncation.
	 * 
	 * @return bean containing the parts of object string.
	 */

	public static SummaryBean getExcerpt(String input, int limit) {
		SummaryBean sBean = new SummaryBean();
		String startingPTagPattern = "(^<p[^>]*>)";
		Pattern patternPTag = Pattern.compile(startingPTagPattern);
		Matcher matcherPTag = patternPTag.matcher(input);
		String pTag = StringUtils.EMPTY;
		if (matcherPTag.find()) {
			pTag = matcherPTag.group(1);
		}
		String textWithoutP = input.replaceAll(startingPTagPattern, " ").replaceAll("\\s+", " ").trim();
		// String noHTMLText = input.replaceAll("(?i)<[^>]*>", "
		// ").replaceAll("\\s+", SSCConstants.SPACE).trim();
		String[] splitArray = textWithoutP.split("\\s+");
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		for (int i = 0; i < splitArray.length; i++) {
			if (i < limit) {
				sb1.append(SSCConstants.SPACE).append(splitArray[i]);
			} else {
				sb2.append(SSCConstants.SPACE).append(splitArray[i]);
			}
		}
		String subString1 = StringUtils.removeStart(sb1.toString(), SSCConstants.SPACE);
		String subString2 = sb2.toString();
		String patternString = "(.*\\.)([^.]*)";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(subString1);
		if (matcher.find()) {
			LOGGER.debug("Truncated first part looks like: - {}", matcher.group(1));
			LOGGER.debug("Merged overflown text back to sub string {}",
					StringUtils.join(pTag, matcher.group(2), subString2));
			LOGGER.debug("Full looks like: - {}",
					StringUtils.join(matcher.group(1), pTag, matcher.group(2), subString2));
			sBean.setFirstPart(matcher.group(1));
			sBean.setRemainingPart(StringUtils.join(pTag, matcher.group(2), subString2));

		}
		return sBean;
	}

	/**
	 * The function removes the additional selectors and just keep the relevant
	 * ones separated by underscore
	 * 
	 * @param requestURI
	 *            The request URL
	 * @return The sanitized URL.
	 */
	public static String getSanitizedFilename(String requestURI) {
		String fileName = StringUtils.substring(requestURI, StringUtils.lastIndexOf(requestURI, "/") + 1);
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		String name = fileName.substring(0, fileName.lastIndexOf("."));
		String[] split = name.split("\\.");
		StringBuffer sb = new StringBuffer();

		for (int index = 0; index < split.length; index++) {

			if (!("rendition").equalsIgnoreCase(split[index]) && !("download").equalsIgnoreCase(split[index])
					&& !("print").equalsIgnoreCase(split[index])) {
				sb.append(split[index]);
				if (index < split.length - 1) {
					sb.append(SSCConstants.UNDERSCORE);
				}
			}
		}
		fileName = sb.append(SSCConstants.DOT).append(extension).toString();
		return fileName;
	}

	/**
	 * Check the presence of the CCPT code.
	 * 
	 * @param ccptCode
	 *            The ccot code from the selector
	 * @return The resulting modified selector string.
	 */
	public static String isPresentCCPTCode(String ccptCode) {

		return StringUtils.isNotEmpty(ccptCode)
				? SSCConstants.DOT + SSCConstants.CCPT_PREFIX + ccptCode + SSCConstants.DOT + SSCConstants.HTML_EXTN
				: SSCConstants.DOT + SSCConstants.HTML_EXTN;
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
	    List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
	    Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
	        @Override
	        public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
	            return (o2.getValue()).compareTo(o1.getValue());
	        }
	    });
	
	    Map<K, V> result = new LinkedHashMap<>();
	    for (Map.Entry<K, V> entry : list) {
	        result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
	}
}

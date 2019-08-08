package com.silversea.ssc.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.sling.api.resource.ValueMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableMap.of;


/**
 * Convention over configuration. Properties should have the following schema.
 * Using 'height' as an example.
 *
 * heightDesktopPixel=150 heightDesktopViewport=50 heightDesktopMethod=Pixel
 * heightTabletPixel=100 heightTabletViewport=40 heightTabletMethod=Viewport ...
 *
 * resulting in
 *
 * heightDesktop150 heightTablet40Viewport
 *
 *
 */
public class FrameworkHelper extends WCMUsePojo {
	private static final String[] DEVICES = new String[] { "Desktop", "Tablet", "Mobile" };
	private static final Map<String, String> FW_METHOD_SUFFIXES = of("Pixel", "", "Viewport", "view", "", "");

	private String[] cssAttributes;
	private ValueMap componentProperties;

	@Override
	public void activate() throws Exception {
		cssAttributes = get("property", String.class).split(",");
		componentProperties = getProperties();
	}

	public String getAllDevices() {
		return forEach(cssAttributes, attribute -> forEach(DEVICES,
				device -> frameworkClassName(attribute, device, getMethod(attribute, device))));
	}

	public String getAllDevicesViewport() {
		return forEach(cssAttributes,
				attribute -> forEach(DEVICES, device -> frameworkClassName(attribute, device, "Viewport")));
	}

	public String getAllDevicesPixel() {
		return forEach(cssAttributes,
				attribute -> forEach(DEVICES, device -> frameworkClassName(attribute, device, "Pixel")));
	}

	public String getNoDevices() {
		return forEach(cssAttributes, atttribute -> frameworkClassName(atttribute, "", ""));

	}

	private String getString(String key) {
		return componentProperties.get(key, String.class);
	}

	private <T> String forEach(T[] elements, Function<T, String> function) {
		StringBuilder builder = new StringBuilder();
		for (T element : elements) {
			builder.append(function.apply(element)).append(" ");
		}
		return builder.toString();
	}

	private String frameworkClassName(String property, String device, String method) {
		String value = getString(property + device + method);
		String returnValue = FW_METHOD_SUFFIXES.get(method) + property + device + value;
		returnValue = returnValue.replace("viewheight", "viewHeight");
		returnValue = returnValue.replace("heightby1", "height");
		returnValue = returnValue.replace("heightby2", "height");
		returnValue = returnValue.replace("heightcontent", "height");
		return returnValue;
	}

	private String getMethod(String property, String device) {
		return getString(property + device + "Method");
	}
}
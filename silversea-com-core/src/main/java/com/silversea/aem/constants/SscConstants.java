package com.silversea.aem.constants;

public enum SscConstants {
	/* P */
	PAGE_CATEGORY("pageCategory1"),
	/* S */
	SINGLE_DESTINATION_SUFFIX("sd"), SINGLE_SHIP_SUFFIX("ss"), SINGLE_DESTINATION_ID("destinationId"), SINGLE_SHIP_ID(
			"shipId");

	private String key;

	SscConstants(String key) {
		this.key = key;
	}

	public String toString() {
		return key;
	}
}
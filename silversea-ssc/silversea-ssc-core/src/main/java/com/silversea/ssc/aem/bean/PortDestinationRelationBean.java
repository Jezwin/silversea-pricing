package com.silversea.ssc.aem.bean;

import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PortDestinationRelationBean {

	@SerializedName("port_name")
	@Expose
	private String portName;
	@SerializedName("port_path")
	@Expose
	private String portPath;
	@SerializedName("port_node_id")
	@Expose
	private String portNodeId;
	@SerializedName("port_city_id")
	@Expose
	private String portCityId;
	@SerializedName("related_destinations")
	@Expose
	private List<RelatedDestination> relatedDestinations = null;

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getPortPath() {
		return portPath;
	}

	public void setPortPath(String portPath) {
		this.portPath = portPath;
	}

	public List<RelatedDestination> getRelatedDestinations() {
		return relatedDestinations;
	}

	public void setRelatedDestinations(List<RelatedDestination> relatedDestinations) {
		this.relatedDestinations = relatedDestinations;
	}

	public String getPortCityId() {
		return portCityId;
	}

	public void setPortCityId(String portCityId) {
		this.portCityId = portCityId;
	}

	public String getPortNodeId() {
		return portNodeId;
	}

	public void setPortNodeId(String portNodeId) {
		this.portNodeId = portNodeId;
	}

}

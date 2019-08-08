package com.silversea.ssc.aem.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RelatedDestination {

	@SerializedName("dest_name")
	@Expose
	private String destName;
	@SerializedName("dest_node_id")
	@Expose
	private String destNodeId;
	@SerializedName("dest_id")
	@Expose
	private String destId;

	public RelatedDestination(String destName, String destNodeId, String destId) {
		this.destName = destName;
		this.destId = destId;
		this.destNodeId = destNodeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destId == null) ? 0 : destId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RelatedDestination other = (RelatedDestination) obj;
		if (destId == null) {
			if (other.destId != null)
				return false;
		} else if (!destId.equals(other.destId))
			return false;
		return true;
	}

	public String getDestName() {
		return destName;
	}

	public void setDestName(String destName) {
		this.destName = destName;
	}

	public String getDestId() {
		return destId;
	}

	public void setDestId(String destId) {
		this.destId = destId;
	}
}

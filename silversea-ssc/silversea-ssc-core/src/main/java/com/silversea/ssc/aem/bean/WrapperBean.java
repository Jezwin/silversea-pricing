package com.silversea.ssc.aem.bean;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WrapperBean {

	@SerializedName("data")
	@Expose
	private List<Object> data = null;

	public List<Object> getData() {
		return data;
	}

	public void setData(List<Object> data) {
		this.data = data;
	}

}

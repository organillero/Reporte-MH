package com.aureacode.reportemh.pojos;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Report {
	
	public String username;
	public String id;
	public String uid;
	public String type;
	public String status;
	public Date date;
	public String lat;
	public String lon;
	
	@SerializedName("mh_location")
	public String mhLocation;
	
	public String comment;
	public String address;
	public String distance;
	public String supports;

}

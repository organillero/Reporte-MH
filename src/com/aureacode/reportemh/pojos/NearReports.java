package com.aureacode.reportemh.pojos;

import java.util.List;

public class NearReports {
	
	public String service;
	public Integer status;
	public Data data;
	
	public class Data{
		public List<Report> reports;
	}

}

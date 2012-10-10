package com.aureacode.reportemh.pojos;

import com.google.android.maps.GeoPoint;

public class MyGeoPoint extends GeoPoint {

	private Boolean penalizado;

	public MyGeoPoint(int arg0, int arg1) {
		super(arg0, arg1);
	}

	public Boolean isPenalizado() {
		return penalizado;
	}

	public void setPenalizado(String penalizado) {

		if (penalizado.equals("0"))
			this.penalizado = false;	
		else 
			this.penalizado = true;
	}



}

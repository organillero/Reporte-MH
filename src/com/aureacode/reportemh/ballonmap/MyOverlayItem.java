package com.aureacode.reportemh.ballonmap;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem {

	private Object object = null;

	public MyOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}

	public Object getObject() {
		return this.object;
	}

	public void setObject(Object o) {
		this.object = o;
	}

}

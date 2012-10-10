package com.aureacode.reportemh;

import static com.aureacode.reportemh.recursos.Recursos.EMAIL;
import static com.aureacode.reportemh.recursos.Recursos.URL_SERVER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mx.ferreyra.Net;
import mx.ferreyra.exceptions.OfflineException;
import mx.ferreyra.persistencia.Preferencias;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.aureacode.reportemh.ballonmap.MyItemizedOverlay;
import com.aureacode.reportemh.ballonmap.MyOverlayItem;
import com.aureacode.reportemh.pojos.NearReports;
import com.aureacode.reportemh.pojos.Report;
import com.aureacode.reportemh.recursos.Recursos.AnsType;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity
public class MainActivity extends MapActivity {


	private MyLocationOverlay myLocationOverlay;
	private LocationManager locationManager;
	private GeoUpdateHandler locationListener;

	private MyItemizedOverlay itemizedOverlay;
	private Drawable drawable;

	private Context context;

	private Preferencias prefs;

	//@ViewById
	MapView mapView;

	@ViewById
	Button btNew;

	@ViewById
	Button btProfile;

	@ViewById
	Button btReports;
	


	MapController mc;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this;

		prefs = App.getInstance().getPrefs();

		this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		this.locationListener = new GeoUpdateHandler();

		setContentView( R.layout.map);
		mapView = (MapView) findViewById(R.id.mapView);
		mc = mapView.getController();

		GeoPoint geoPoint = new GeoPoint(19426900, -99182200);
		mc.animateTo(geoPoint);
		mc.setZoom(10);
		mapView.invalidate();

		this.myLocationOverlay = new MyLocationOverlay(this, mapView);

		drawable = context.getResources().getDrawable(R.drawable.map_pin_other);
		getReports();

	}


	@Override
	protected void onResume() {
		super.onResume();
		// when our activity resumes, we want to register for location updates
		myLocationOverlay.enableMyLocation();
		this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10*60000, 10, this.locationListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// when our activity pauses, we want to remove listening for location updates
		myLocationOverlay.disableMyLocation();
		this.locationManager.removeUpdates(this.locationListener);
	}


	@AfterViews
	void afterView(){


		if (prefs.loadData(EMAIL) == null){
			btProfile.setVisibility(View.INVISIBLE);
			btReports.setVisibility(View.INVISIBLE);
		}
		else{
			btProfile.setVisibility(View.VISIBLE);
			btReports.setVisibility(View.VISIBLE);
		}

		return;
	}
	
	@Click(R.id.bt_new)
	void newReport(View clickedView){
		
		Intent intent = new Intent(this, LoginAct_.class) ; 
		//Intent intent = prefs.loadData(EMAIL) == null ? new Intent(this, Login_.class) :  new Intent(this, NewReport_.class);
		startActivity(intent);
	}
	
	

	@Background
	void  getReports(){

		AnsType ansType = AnsType.UNKNOW;

		NearReports nearReports = null;

		final String url = URL_SERVER + "get_reports_near_by/";

		String bestProvider;
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		bestProvider = locationManager.getBestProvider(criteria, true);		 
		Location location = locationManager.getLastKnownLocation(bestProvider);


		List<NameValuePair> postData = new ArrayList<NameValuePair>(2);

		postData.add(new BasicNameValuePair("lat", Double.valueOf(location.getLatitude()).toString()));
		postData.add(new BasicNameValuePair("lon", Double.valueOf(location.getLongitude()).toString()));
		postData.add(new BasicNameValuePair("dist", "20"));

		try{
			nearReports = (NearReports) Net.sendDataAndGetObject(url, NearReports.class, postData);
			ansType = AnsType.OK;
		} catch (IOException e) {
			ansType = AnsType.NETWORK_PROBLEM;
			e.printStackTrace();
		} catch (OfflineException e) {
			ansType = AnsType.OFFLINE;
			e.printStackTrace();
		} catch (Exception e) {
			ansType = AnsType.UNKNOW;
			e.printStackTrace();
		}


		displayReports(nearReports,ansType);
		return;
	}

	@UiThread
	void displayReports(NearReports nearReports, AnsType ansType){

		if (nearReports == null)
			return;

		List<Report> reports = new ArrayList<Report>();

		reports = nearReports.data.reports;


		if(reports != null && reports.size() > 0){		

			itemizedOverlay = new MyItemizedOverlay(drawable, mapView);

			for ( Report report : reports){
				Double lat = Double.valueOf(report.lat)*1E6;
				Double lng = Double.valueOf(report.lon)*1E6;
				MyOverlayItem overlayItem = new MyOverlayItem(new GeoPoint(lat.intValue(), lng.intValue()), "Reporte " , "Inicio");
				overlayItem.setObject(report);

				itemizedOverlay.addOverlay(overlayItem);
			}

			List<Overlay> mapOverlays = mapView.getOverlays();
			mapOverlays.add(itemizedOverlay);
			mapView.invalidate();
		}



	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}




	public class GeoUpdateHandler implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			mc.animateTo(point);
			mapView.invalidate();
		}

		@Override
		public void onProviderDisabled(String provider) {
			/**/
		}

		@Override
		public void onProviderEnabled(String provider) {
			/**/
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			/**/
		}
	}

}

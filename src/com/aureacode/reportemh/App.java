package com.aureacode.reportemh;

import mx.ferreyra.persistencia.ObjectMemoryCache;
import mx.ferreyra.persistencia.Preferencias;
import android.app.Application;



public class App extends Application{

	private static App m_singleton;
	private Preferencias prefs;
	private ObjectMemoryCache strMemoryCache;

	@Override
	public final void onCreate()
	{
		super.onCreate();
		m_singleton = this;

		this.prefs=  new Preferencias(this);
		this.strMemoryCache = new ObjectMemoryCache();
	}

	public static App getInstance(){
		return m_singleton;
	}

	public Preferencias getPrefs() {
		return this.prefs;
	}

	public ObjectMemoryCache getStringMemoryCache(){
		return this.strMemoryCache;
	}

}

package mx.ferreyra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

import mx.ferreyra.exceptions.OfflineException;
import mx.ferreyra.persistencia.FileCache;
import mx.ferreyra.persistencia.ObjectMemoryCache;
import mx.ferreyra.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.aureacode.reportemh.App;
import com.google.gson.GsonBuilder;

public class Net {

	private static Context context;
	private static FileCache fileCache;
	private static ObjectMemoryCache objMemoryCache;

	static{
		context = App.getInstance().getApplicationContext();
		fileCache=new FileCache(context);
		objMemoryCache =  App.getInstance().getStringMemoryCache();
	}


	public  static Object sendDataAndGetObjectCache (String url, Type type, List<NameValuePair> nameValuePairs) throws Exception{

		//Object ansHttpGet = null;
		Object o=null;

		//memoria cache
		o =  objMemoryCache.get(Utils.getSHA1(url));

		if (o != null)
			return o;

		//archivo cache
		File f=fileCache.getFile(url);

		// && new FileInputStream(f).available()>0
		if (f.exists()) {

			ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
			o =  in.readObject();
			in.close();
		}
		else{ 

			//web
			if (isOnline() == false )
				throw new OfflineException();


			o = sendDataAndGetObject(url, type, nameValuePairs );

			if (o != null){
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
				out.writeObject(o);
				out.close();
			}

		}


		objMemoryCache.put(Utils.getSHA1(url), o);
		return o;
	}

	public static Object sendDataAndGetObject  (String url, Type type, List<NameValuePair> nameValuePairs) throws OfflineException,  IOException, Exception {

		Object o = null;
		InputStream is = sendData( url, nameValuePairs);

		if (is != null && is instanceof InputStream){
			String ansUrl = inputStreamToString (is);

			try {
				o = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create().
				fromJson(ansUrl, type);
			} catch (Exception e) {
				o=null;
			}



		}
		return o;
	}

	public static InputStream sendData(String url, List<NameValuePair> nameValuePairs) throws  OfflineException, IOException, Exception {

		if (isOnline()== false){
			throw new OfflineException();
		}

		HttpParams httpParameters = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
		HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);

		HttpRequestBase httpRequest;


		//POST 
		if (nameValuePairs != null){

			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			for(int index=0; index < nameValuePairs.size(); index++) {
				if(nameValuePairs.get(index).getName().equalsIgnoreCase("image")) {
					// If the key equals to "image", we use FileBody to transfer the data
					entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
				} else {
					// Normal string data
					entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
				}
			}

			httpRequest = new HttpPost(url);
			((HttpPost) httpRequest).setEntity(entity);
		}
		//GET
		else{
			httpRequest = new HttpGet(url);
		}

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse response = httpClient.execute(httpRequest);


		return  response.getEntity().getContent();

	}

	public static String addParameter(String url, String name, String value)
	{
		if  (name != null && value != null){
			int qpos = url.indexOf('?');
			int hpos = url.indexOf('#');
			char sep = qpos == -1 ? '?' : '&';
			String seg = sep + encodeUrl(name) + '=' + encodeUrl(value);
			return hpos == -1 ? url + seg : url.substring(0, hpos) + seg+ url.substring(hpos);
		}
		return url;
	}

	public  static boolean isOnline() {		
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean ans;
		final NetworkInfo network_info = cm.getActiveNetworkInfo();

		ans = network_info != null && network_info.isConnected() ? true : false; 
		return ans;
	}

	public static String inputStreamToString (InputStream in)  {		
		StringBuilder out = new StringBuilder();

		if (in == null) return null;

		byte[] b = new byte[4096];
		try {

			for (int n; (n = in.read(b)) != -1;) {
				out.append(new String(b, 0, n));
			}
			return out.toString();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String encodeUrl(String url)	{
		try{
			return URLEncoder.encode(url, "UTF-8");		}
		catch (UnsupportedEncodingException uee){
			throw new IllegalArgumentException(uee);	}
	}
}

package com.aureacode.reportemh;

import static com.aureacode.reportemh.recursos.Recursos.URL_SERVER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mx.ferreyra.Net;
import mx.ferreyra.exceptions.OfflineException;
import mx.ferreyra.persistencia.Preferencias;
import mx.ferreyra.utils.UI;
import mx.ferreyra.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.aureacode.reportemh.pojos.Login;
import com.aureacode.reportemh.recursos.Recursos;
import com.aureacode.reportemh.recursos.Recursos.AnsType;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;




@EActivity(R.layout.signup)
public class SignUp extends Activity {


	Context context;

	@ViewById
	View activityRootView;

	@ViewById
	EditText edUserSignUp;

	@ViewById
	EditText edEmailSignUp;

	@ViewById
	EditText edPassSignUp;

	@ViewById
	EditText edConfirmpassSignUp;


	private Preferencias prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		prefs = App.getInstance().getPrefs();
	}


	@Click(R.id.bt_signup)
	void sigup (){

		if (edUserSignUp.getText().length()== 0 || edEmailSignUp.getText().length()== 0 || edPassSignUp.getText().length()== 0 || edConfirmpassSignUp.getText().length()== 0){
			UI.showAlertDialog("Upps!", "Por favor introduzca todos los campos", getString(android.R.string.ok), context, null);
		}
		else if (edUserSignUp.getText().length()<4 ){
			UI.showAlertDialog("Upps!", "El usuario debe tener al menos 4 caracteres", getString(android.R.string.ok), context, null);
		}

		else if (Utils.isValidEmailAddress(edEmailSignUp.getText().toString())){
			UI.showAlertDialog("Upps!", "Por favor introduzca un correo valido", getString(android.R.string.ok), context, null);
		}
		else if (edPassSignUp.getText().length()< 4 || edConfirmpassSignUp.getText().length()< 4  || edPassSignUp.getText().toString().equals(edConfirmpassSignUp.getText().toString()) == false){
			UI.showAlertDialog("Upps!", "Por favor introduzca verique que las contrase–as coninciden y tienen al menos 4 caracteres.", getString(android.R.string.ok), context, null);
		}
		else if (edUserSignUp.getText().length()>32 || edPassSignUp.getText().length()> 32){
			UI.showAlertDialog("Upps!", "El usuario y la contrase–a debe tener menos de 32 caracteres", getString(android.R.string.ok), context, null);
		}

		else{

			sigUpBack(edUserSignUp.getText().toString() , edEmailSignUp.getText().toString(), edPassSignUp.toString());
		}


	}


	@Background
	void sigUpBack(String user, String email, String pass) {

		AnsType ansType = AnsType.UNKNOW;

		final String url = URL_SERVER + "signup/";

		List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
		Login login = null;

		postData.add(new BasicNameValuePair("username", user));
		postData.add(new BasicNameValuePair("email", email));
		postData.add(new BasicNameValuePair("password", pass));

		try {
			login = (Login) Net.sendDataAndGetObject(url, Login.class, postData);
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

		doInUiThread(login, ansType);
	}


	@UiThread
	void doInUiThread(Login login, AnsType ansType) {

		if (login == null)
			return;

		Iterator<Entry<String, String>> it;
		if (login.data.error != null){

			it = login.data.error_message.entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();
				//System.out.println(e.getKey() + " " + e.getValue());
				UI.showAlertDialog(e.getKey().toString(), e.getValue().toString(), getString(android.R.string.ok), context, null);
				break;

			}
		}
		else {
			prefs.saveData(Recursos.UDID, login.data.uid);
			prefs.saveData(Recursos.EMAIL, login.data.email);
			prefs.saveData(Recursos.TOKEN, login.data.token);
		}

	}


	public void checkAndHideKeyboard (View view){

		if(getResources().getConfiguration().keyboardHidden == Configuration.KEYBOARDHIDDEN_NO){
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(activityRootView.getWindowToken(), 0);
		}

		return;
	}

}

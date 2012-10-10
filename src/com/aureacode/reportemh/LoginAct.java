package com.aureacode.reportemh;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

@EActivity(R.layout.login)
public class LoginAct extends Activity {

	@ViewById
	View activityRootView;

	public void checkAndHideKeyboard (View view){

		if(getResources().getConfiguration().keyboardHidden == Configuration.KEYBOARDHIDDEN_NO){
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(activityRootView.getWindowToken(), 0);
		}

		return;
	}

	
	@Click(R.id.recuperar_pass)
	void recuperarPass(){
		startActivity(new Intent(this, Recover_.class));
	}
	
	@Click(R.id.sign_up)
	void signUp(){
		startActivity(new Intent(this, SignUp_.class));
	}

}

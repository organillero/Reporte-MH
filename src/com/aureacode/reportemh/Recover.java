package com.aureacode.reportemh;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


@EActivity(R.layout.recover_pass)
public class Recover extends Activity {

	@ViewById
	View activityRootView;
	
	public void checkAndHideKeyboard (View view){

		if(getResources().getConfiguration().keyboardHidden == Configuration.KEYBOARDHIDDEN_NO){
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(activityRootView.getWindowToken(), 0);
		}

		return;
	}

}

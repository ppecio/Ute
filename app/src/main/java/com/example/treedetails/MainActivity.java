package com.example.treedetails;

/*
	Paweł Pecio
	Piotr Godlewski
*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStore;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.net.UnknownHostException;
import java.security.KeyManagementException;


import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.ImageButton;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.MenuItem;
import android.telephony.TelephonyManager;


public class MainActivity extends AppCompatActivity {
	String data = "";
	// double latit = 52.27683789;
	// double longi = 20.92454726;
	String latitBankomat;
	String longiBankomat;
	String numerGlobal = "";
	String dostepGlobal;
	Button b1;
	Button b2;
	TextView wspX;
	TextView wspY;
	TextView info;
	TextView test;
	TextView test1;
	EditText promien;
	EditText numer;
	String getSimSerialNumber = "";
	TextView pokazNaMapieButton;
	ImageButton orangeButton;
	ImageButton policjaButton;
	ImageButton bankomatButton;
	ImageButton mapaButton;
	String czegoSzukam = "null";
	boolean go = false;
	boolean wawaAPI = true;
	boolean echo01 = true;
	boolean echo02 = true;
	

	boolean mozeszPokazacMape = false;
	public String zapytanie;
	public String wynikAPI = "start";
	public String request = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		getSimSerialNumber = telemamanger.getSimSerialNumber();

		b1 = (Button) findViewById(R.id.button1);
		// b2 = (Button) findViewById(R.id.button2);

		pokazNaMapieButton = (Button) findViewById(R.id.button3);
		wspX = (TextView) findViewById(R.id.textView1);
		wspY = (TextView) findViewById(R.id.textView2);
		info = (TextView) findViewById(R.id.textView3);
		test = (TextView) findViewById(R.id.textView4);
		test1 = (TextView) findViewById(R.id.textView6);
		promien = (EditText) findViewById(R.id.editText1);
		orangeButton = (ImageButton) findViewById(R.id.imageButton1);

		policjaButton = (ImageButton) findViewById(R.id.imageButton2);
		policjaButton.setEnabled(false);
		bankomatButton = (ImageButton) findViewById(R.id.imageButton3);
		bankomatButton.setEnabled(false);
		mapaButton = (ImageButton) findViewById(R.id.imageButton4);
		mapaButton.setEnabled(false);
		numer = (EditText) findViewById(R.id.editText2);
		test1.setText("SIM: " + getSimSerialNumber);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				LocationManager mlocManager = null;
				LocationListener mlocListener;
				mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				mlocListener = new gpsMyCordinate();
				mlocManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

				if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					if (gpsMyCordinate.latitude > 0) {
						wspX.setText("Latitude:- " + gpsMyCordinate.latitude);
						wspY.setText("Longitude:- " + gpsMyCordinate.longitude);

						policjaButton.setEnabled(true);
						bankomatButton.setEnabled(true);

					} else {
						info.setText("Wait");

						info.setText("Szukam GPS...");

					}
				} else {
					info.setText("Uruchom GPS aby zlokalizowa� swoj� pozycje.");
				}

			}
		});
		bankomatButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				wawaAPI = true;
				if (promien.getText().toString().length() > 0) {
					int r = Integer.parseInt(promien.getText().toString());
					// info.setText("Szukam w promieniu " +r
					// +" metr�w bankomatu.");
					Toast.makeText(MainActivity.this,
							"Szukam w promieniu " + r + " metr�w bankomatu.",
							Toast.LENGTH_SHORT).show();
					// sklejnie zapytania na podstawie po�o�enia i promienia
					// poszukiwa�, wykorzystano filtr "circle"
					try {
						/*
						zapytanie = new String(
								"https://api.bihapi.pl/wfs/warszawa/cashMachines?maxFeatures=1&circle="
										+ gpsMyCordinate.latitude + ","
										+ gpsMyCordinate.longitude + "," + r);
										*/
						zapytanie = new String(
								"https://api.bihapi.pl/wfs/warszawa/cashMachines?maxFeatures=1&circle="
										+ gpsMyCordinate.longitude + ","
										+ gpsMyCordinate.latitude + "," + r);
						request = zapytanie;
						czegoSzukam = "bankomat";
						new LongOperation().execute("");
						// test.setText("Najblizszy bankomat: \nLatitude: "+
						// latitBankomat +"\n Longitude: "+ longiBankomat
						// +"\nDost�p: " +dostepGlobal );

					} catch (Exception e) {
						Toast.makeText(MainActivity.this,
								"B��d - zg�o� na lukasz@aleksandrowski.com",
								Toast.LENGTH_SHORT).show();
					}

					// info.setText("Wys�ano request: " +zapytanie);
				//	Toast.makeText(MainActivity.this,
				//			"Wys�ano request: " + zapytanie, Toast.LENGTH_SHORT)
				//			.show();

					// latitBankomat //= gpsMyCordinate.latitude - 0.000001;
					// longiBankomat //= gpsMyCordinate.longitude - 0.000001;

					// test.setText(wynikAPI);
					mozeszPokazacMape = true;
					if (go) {

						Log.e("WYNIK", "LATI " + latitBankomat);
						Log.e("WYNIK", "LONG " + longiBankomat);
						Log.e("WYNIK", "DOSTEP " + dostepGlobal);
					}
					// test.setText("Najblizszy bankomat: \nLatitude: "+
					// latitBankomat +"\n Longitude: "+ longiBankomat
					// +"\nDost�p: " +dostepGlobal );
				} else {
					Toast.makeText(MainActivity.this,
							"Podaj promie� poszukiwa�", Toast.LENGTH_SHORT)
							.show();
				}
			
			}
		});

		policjaButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				wawaAPI = true;
				if (promien.getText().toString().length() > 0) {
					int r = Integer.parseInt(promien.getText().toString());
					// info.setText("Szukam w promieniu " +r
					// +" metr�w bankomatu.");
					Toast.makeText(
							MainActivity.this,
							"Szukam w promieniu " + r
									+ " metr�w posterunku Policji.",
							Toast.LENGTH_SHORT).show();
					// sklejnie zapytania na podstawie po�o�enia i promienia
					// poszukiwa�, wykorzystano filtr "circle"
					try {
						zapytanie = new String(
								"https://api.bihapi.pl/wfs/warszawa/policeOffices?maxFeatures=1&circle="
										+ gpsMyCordinate.longitude + ","
										+ gpsMyCordinate.latitude + "," + r);
						request = zapytanie;
						czegoSzukam = "policja";
						new LongOperation().execute("");
						// test.setText("Najblizszy bankomat: \nLatitude: "+
						// latitBankomat +"\n Longitude: "+ longiBankomat
						// +"\nDost�p: " +dostepGlobal );

					} catch (Exception e) {
						Toast.makeText(MainActivity.this,
								"B��d - zg�o� na lukasz@aleksandrowski.com",
								Toast.LENGTH_SHORT).show();
					}

					// info.setText("Wys�ano request: " +zapytanie);
					//Toast.makeText(MainActivity.this,
					//		"Wys�ano request: " + zapytanie, Toast.LENGTH_SHORT)
					//		.show();

					// latitBankomat //= gpsMyCordinate.latitude - 0.000001;
					// longiBankomat //= gpsMyCordinate.longitude - 0.000001;

					// test.setText(wynikAPI);
					mozeszPokazacMape = true;
					if (go) {

						Log.e("WYNIK", "LATI " + latitBankomat);
						Log.e("WYNIK", "LONG " + longiBankomat);
						Log.e("WYNIK", "DOSTEP " + dostepGlobal);
					}
					// test.setText("Najblizszy bankomat: \nLatitude: "+
					// latitBankomat +"\n Longitude: "+ longiBankomat
					// +"\nDost�p: " +dostepGlobal );
				} else {
					Toast.makeText(MainActivity.this,
							"Podaj promie� poszukiwa�", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		mapaButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (mozeszPokazacMape) {
					String uri = "geo:" + latitBankomat + "," + longiBankomat
							+ "?q=" + latitBankomat + "," + longiBankomat;
					startActivity(new Intent(
							android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
				} else {
					// elo();
					//Toast.makeText(
					//		MainActivity.this,
					//		"Najpierw wyszukaj poprawnie bankomat lub posterunek Policji!",
					//		Toast.LENGTH_SHORT).show();

				}
			}
		});

		orangeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				wawaAPI = false;
				if (numer.getText().toString().length() == 11) {

					TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String getSimSerialNumber = telemamanger
							.getSimSerialNumber();
					String getSimNumber = telemamanger.getLine1Number();
					numerGlobal = numer.getText().toString();
					String reset = new String(
							"https://api.bihapi.pl/orange/oracle/resetlocation?msisdn="
									+ numer.getText().toString());
					//Toast.makeText(MainActivity.this,
					//		"Wys�ano request " + reset, Toast.LENGTH_SHORT)
					//		.show();
					zapytanie = reset;
					new LongOperation().execute("");
					
				} else {
					Toast.makeText(MainActivity.this,
							"Podaj numer telefonu (11 cyfr)",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// always verify the host - dont check for certificate
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String createBihapiConnection(String path) {
		String result = "";
		Context context;
		
		   

		
		
		
		try {
			HttpClient httpclient = getNewHttpClient();
			HttpGet httppost = new HttpGet(path);
			httppost.addHeader(BasicScheme.authenticate(
					new UsernamePasswordCredentials("48514168444",
							"t55fg55m44e35GBvgCTnhmnhmc"), "UTF-8", false));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");

			}
			is.close();
			result = sb.toString();
			Log.e("REQUEST", "ELOOOOO " + request);
			Log.e("WYNIK", "ELOOOOO " + result);
			if(wawaAPI){
			String testujemy = result;
			testujemy = testujemy.substring(3, 11);
			Log.e("WYNIK", "TEST " + testujemy);
			String blad = "ataError";
			odblokowuje();
			if (testujemy.equals("ataError")) {
				Log.e("WYNIK", "ZATRZYMAJ_TO_TUTAJ " + testujemy);
				blokuje();
				return "";

			}
			// if()
			// lacze.join();
			// Log.e("log_tag", "Konczymy BALLL2" );
			String latBank = null;
			String lonBank;
			String dostep;
			latBank = (result.substring(result.lastIndexOf("lat") + 6));
			latBank = latBank.substring(0, 9);
			lonBank = (result.substring(result.lastIndexOf("lon") + 6));
			lonBank = lonBank.substring(0, 9);
			dostep = (result.substring(result.lastIndexOf("DOSTEP") + 17));
			dostep = dostep.substring(0, 3);
			if(!dostep.equals("24h")){
				dostep="brak danych";
			}
			Log.e("WYNIK", "LATI " + latBank);
			Log.e("WYNIK", "LONG " + lonBank);
			Log.e("WYNIK", "DOSTEP " + dostep);
			// test.setText("Najblizszy bankomat: \nLatitude: "+ latitBankomat
			// +"\n Longitude: "+ longiBankomat +"\nDost�p: " +dostepGlobal );
			latitBankomat = latBank;
			longiBankomat = lonBank;
			dostepGlobal = dostep;
			wynikAPI = result;
			if (latBank != null) {
				go = true;
			} else {
				go = false;
				mapaButton.setEnabled(false);
			}
			}
			else{
				String orangeW;
				orangeW = result;
				orangeW = orangeW.substring(3, 11);
				Log.e("log_tag", "yyyyyyyyyyyyyy" +orangeW);
				if(orangeW.equals("ataError")){
					Log.e("log_tag", "BLAD ORANGE" +orangeW);
					echo01 = false;
					
				}
				else{
					Log.e("log_tag", "SUKCES ZIOM");
					echo01 = true;
					
				}
				//toast(boolean sukces, String numer)
				
				// api orange
				
				
			}
			echo02 = true;
		} catch (Exception e) {
			echo02 = false;
			Log.e("log_tag", "Error in http connection " + e.toString());
			Toast.makeText(MainActivity.this, "ERRRORR", Toast.LENGTH_SHORT)
					.show();
		}
		return result;
		
	}

	public HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	private class LongOperation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			createBihapiConnection(zapytanie);
			if(!echo02){
				Toast.makeText(MainActivity.this,
						"Blad polaczenia sprawdz internet",Toast.LENGTH_SHORT).show();
			}

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {
			if(!echo02){
			wyswietl();
			if(!wawaAPI){
			toast();
			}
			// info.setText("Executed"); // txt.setText(result);
			// might want to change "executed" for the returned string passed
			// into onPostExecute() but that is upto you
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	public void wyswietl() {
		mapaButton.setEnabled(true);
		if (czegoSzukam == "bankomat") {
			if (mozeszPokazacMape) {
				test.setText("Najblizszy bankomat: \nLatitude: "
						+ latitBankomat + "\nLongitude: " + longiBankomat
						+ "\nDost�p: " + dostepGlobal);
			}
			else{
				test.setText("B��d API! (2) lub zwi�ksz promie� wyszukiwania");
			}
		} else {
			if (mozeszPokazacMape) {
				test.setText("Najblizszy komisariat Policji: \nLatitude: "
						+ latitBankomat + "\nLongitude: " + longiBankomat);
			}
			else{
				if(wawaAPI){
				test.setText("B��d API! (3) lub zwi�ksz promie� wyszukiwania");
				}
				else{
					test.setText("");
				}
			}
		}
	}

	public void blokuje() {
		// test.setText("B��d API (500)");
		// mapaButton.setEnabled(false);
		mozeszPokazacMape = false;
	}

	public void odblokowuje() {
		// mapaButton.setEnabled(true);
		mozeszPokazacMape = true;
		// test.setText("");

	}
	
	public void toast(){
		if(echo01){
			//Log.e("log", "lipa orange " +numer);
			//test.setText("Usuni�to dane z VLR i SGSN dla numeru +" +numerGlobal);
		Toast.makeText(MainActivity.this,
				"Usuni�to dane z VLR i SGSN dla numeru +" +numerGlobal,Toast.LENGTH_SHORT).show();
		}
		else{
			//test.setText("B��d - Value " +numerGlobal +" for parameter arg0.msisdn was denied by SLA!");
		//	Log.e("log", "sukces orange " +numer);
		Toast.makeText(MainActivity.this,
					"B��d - Value "+numerGlobal +" for parameter arg0.msisdn was denied by SLA!",
					Toast.LENGTH_SHORT).show();
		}
	}

	public class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

	}

}

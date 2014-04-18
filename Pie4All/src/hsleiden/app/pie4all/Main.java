package hsleiden.app.pie4all;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import hsleiden.app.pie4all.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Main extends FragmentActivity implements OnItemSelectedListener,OnItemClickListener {

	//Variabelen die we later nodig hebben
	static Spinner categorie_Spinner;
	static ListView producten_Lijst;	
	static ArrayList<String> categorieLijst;
	private SimpleAdapter simAdapter;
	static ArrayList<HashMap<String, String>> product_Gegevens;
	SharedPreferences preferences;
	int categorie;
	public static String list_prijs = "€7";
	private static MainActivity activity;
	public ServerCommunicator serverCommunicator;

	@Override
	// Voert de volgende dingen uit tijdens creeren van pagina
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		categorie_Spinner = (Spinner) findViewById(R.id.categorie_spinner);
		categorieLijst = getCategories();


		preferences = this.getSharedPreferences("spinnerSelection", 0);
		categorie_Spinner.setSelection(preferences.getInt("spinnerSelection", 0));

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item, categorieLijst) {
		};
		producten_Lijst = (ListView) findViewById(R.id.producten_listview);
		producten_Lijst.setOnItemClickListener(this);
		categorie_Spinner.setAdapter(adapter);
		categorie_Spinner.setOnItemSelectedListener(this);


	}

	// @Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
		this.categorie = position;

		// De gekozen categorie uit de spinner wordt opgeslagen in de preferences
		Editor editor = preferences.edit();
		int selectedPosition = categorie_Spinner.getSelectedItemPosition();
		editor.putInt("spinnerSelection", selectedPosition);
		editor.commit();

		ArrayList<HashMap<String, String>> lijst = getProductData(categorie);

		// De gegevens die bij de categorie horen worden getoont in een listview
		simAdapter = new SimpleAdapter(this, lijst, R.layout.list_item,
				new String[] { "naam", "list_prijs" }, new int[] {
				R.id.list_product, R.id.list_prijs }) {

			@Override
			public View getView(int pos, View convertView, ViewGroup parent) {
				View v = convertView;
				if (v == null) {
					LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = vi.inflate(R.layout.list_item, null);
				}

				TextView tv = (TextView) v.findViewById(R.id.list_product);
				tv.setText(product_Gegevens.get(pos).get("naam"));

				TextView tvs = (TextView) v.findViewById(R.id.list_prijs);
				tvs.setText(product_Gegevens.get(pos).get("prijs"));
				return v;
			}
		};
		// Geeft adapter mee aan producten_lijst
		producten_Lijst.setAdapter(simAdapter);
	}

	@Override
	// Gaat naar product scherm
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {


		Intent intent = null;
		intent = new Intent(getApplicationContext(), ProductScherm.class);

		startActivity(intent);


	}

	// Haalt categorien van de server op
	static ArrayList<String> getCategories() {
		categorieLijst = new ArrayList<String>();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("categorielijst", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String response = null;
		try {
			try {
				// Dit IP adres moet IP adres van server zijn.
				response = new ServerCommunicator(activity, "192.168.1.12",
						4444, jsonObject.toString()).execute().get();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		// Haal de null naam weg van de JSONArray (Voorkomt error)
		String jsonFix = response.replace("null", "");

		JSONArray JArray = null;
		try {
			JArray = new JSONArray(jsonFix);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONObject jObject = null;
		String value = null;
		categorieLijst = new ArrayList<String>();

		for (int i = 0; i < JArray.length(); i++) {
			try {
				jObject = (JSONObject) JArray.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				value = jObject.getString("naam");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			categorieLijst.add(value);
		}

		return categorieLijst;
	}
	// Haalt producten op van server
	public static ArrayList<HashMap<String, String>> getProductData(
			int gekozenCategorie) {
		product_Gegevens = new ArrayList<HashMap<String, String>>();

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("productenlijst",
					categorieLijst.get(gekozenCategorie));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String response = null;
		try {
			try { // Dit IP adres moet IP adres van server zijn.
				response = new ServerCommunicator(activity, "192.168.1.12",
						4444, jsonObject.toString()).execute().get();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		;

		String jsonFix = response.replace("null", "");

		JSONArray productarray = null;
		try {
			productarray = new JSONArray(jsonFix);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONObject productobject = null;
		ArrayList<HashMap<String, String>> productlist = new ArrayList<HashMap<String, String>>();
		// Hashmap zorgt ervoord at producten_lijst waarden kunnen worden terugegeven
		for (int i = 0; i < productarray.length(); i++) {
			try {
				productobject = productarray.getJSONObject(i);

				HashMap<String, String> products = new HashMap<String, String>();
				products = new HashMap<String, String>();
				products.put("naam", productobject.getString("naam"));
				products.put("list_prijs", list_prijs);
				product_Gegevens.add(products);
				productlist.add(products);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return productlist;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	{
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Haalt de gegevens op uit de server
	public static String getProducts(int gekozenCategorie, int productposition) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("productenlijst",
					categorieLijst.get(gekozenCategorie));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String response = null;
		try {
			try { // Dit IP adres moet IP adres van server zijn.
				response = new ServerCommunicator(activity, "192.168.1.12",
						4444, jsonObject.toString()).execute().get();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String jsonFix = response.replace("null", "");

		JSONArray JArray = null;
		try {
			JArray = new JSONArray(jsonFix);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONObject jObject = null;
		ArrayList<String> productlist = new ArrayList<String>();

		for (int i = 0; i < JArray.length(); i++) {
			try {
				jObject = JArray.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				productlist.add(jObject.getString("naam"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		String naamProduct = productlist.get(productposition);
		return naamProduct;
	}

	public static String getProductInfo(String product) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("productinfo", product);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String response = null;
		try {
			try { // Dit IP adres moet IP adres van server zijn.
				response = new ServerCommunicator(activity, "192.168.1.12",
						4444, jsonObject.toString()).execute().get();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String jsonFix = response.replace("null", "");

		JSONArray JArray = null;
		try {
			JArray = new JSONArray(jsonFix);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONObject jObject = null;
		try {
			jObject = JArray.getJSONObject(0);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String beschrijving = null;
		try {
			beschrijving = jObject.getString("naam");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return beschrijving;
	}


}
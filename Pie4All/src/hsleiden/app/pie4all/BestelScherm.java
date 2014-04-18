package hsleiden.app.pie4all;

import org.json.JSONArray;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;


public class BestelScherm extends Activity
{
	ProductScherm productscherm;
	Button Bestel_Bestelknop;
	Button Bestel_Annuleerknop;
	SharedPreferences preferences;
	public String productAantal;
	public String productNaam;
	public String productPrijs;
	private String naam;
	private String adres;
	private String telefoonnummer;
	private String email;

	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bestel_scherm);
		buttonListener();

		TextView product = (TextView) findViewById(R.id.text_product);
		TextView totaal = (TextView) findViewById(R.id.text_totaal);
		TextView naam = (TextView) findViewById(R.id.text_naam);
		TextView adres = (TextView) findViewById(R.id.text_adres);
		TextView telefoon = (TextView) findViewById(R.id.text_telefoon);
		TextView email = (TextView) findViewById(R.id.text_email);

		//De preferences worden ingeladen 
		//De standaardwaarden van de invoervelden worden opgehaald
		preferences = this.getSharedPreferences("gegevens", 0);
		this.naam = preferences.getString("Naam", " ");
		this.adres = preferences.getString("Adres", " ");
		this.telefoonnummer = preferences.getString("Telefoonnummer", " ");
		this.email = preferences.getString("Email", " ");
		this.productAantal = preferences.getString("Aantal", " ");
		this.productNaam = preferences.getString("Product", " ");
		this.productPrijs = preferences.getString("Prijs", Main.list_prijs);
		setString();

		//Controleert of er een internet verbinding is.
		final ConnectivityManager conMgr =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) 
		{
			//er is en internet verbinding
		} 
		else 
		{
			//er is geen internet verbinding, geeft de gebruiker een melding en zorgt ervoor dat de bestelknop niet werkt
			Toast.makeText(getApplicationContext(), "." + "\n" +  "Bestelling kan niet geplaatst worden", Toast.LENGTH_LONG).show();
			Bestel_Bestelknop.setEnabled(false);
		} 
	}

	//De methode setString zet de standaardwaarden in de bijbehorende invoervelden
	public void setString()
	{

		TextView product = (TextView) findViewById(R.id.product);
		product.setText(this.productNaam);
		TextView prijs = (TextView) findViewById(R.id.prijs);
		prijs.setText(this.productPrijs);
		TextView totaalprijs = (TextView) findViewById(R.id.totaalprijs);
		int totaalprijsAantal = Integer.parseInt(this.productAantal);
		int totaalprijsPrijs = Integer.parseInt("7");
		totaalprijs.setText("€" + (totaalprijsAantal * totaalprijsPrijs));
		EditText naamEditTekst = (EditText) findViewById( R.id.edit_naam);
		naamEditTekst.setText(this.naam );
		EditText adresEditTekst = (EditText) findViewById( R.id.edit_adres );
		adresEditTekst.setText(this.adres );
		EditText telefoonEditTekst = (EditText) findViewById( R.id.edit_telefoon );
		telefoonEditTekst.setText(this.telefoonnummer );
		EditText emailEditTekst = (EditText) findViewById( R.id.edit_email );
		emailEditTekst.setText(this.email );

	}
	// Tekst velden worden opgehaald en in string gezet.
	public void setGegevens()
	{
		EditText naamEditTekst = (EditText) this.findViewById(R.id.edit_naam);
		this.naam = naamEditTekst.getText().toString();

		EditText adresEditTekst = (EditText) this.findViewById(R.id.edit_adres);
		this.adres = adresEditTekst.getText().toString();

		EditText telefoonEditTekst = (EditText) this.findViewById(R.id.edit_telefoon);
		this.telefoonnummer = telefoonEditTekst.getText().toString();

		EditText emailEditTekst = (EditText) this.findViewById(R.id.edit_email);
		this.email = emailEditTekst.getText().toString();

		Editor editor = preferences.edit();
		editor.putString("Naam", naam);
		editor.putString("Adres", adres);
		editor.putString("Telefoonnummer", telefoonnummer);
		editor.putString("Email", email);
		editor.commit();	
	}

	public void buttonListener()  
	{
		final Context context = this;
		//Knop om de bestelling te bevestigen
		Bestel_Bestelknop = (Button) findViewById(R.id.Bestel_Bestelknop);
		Bestel_Bestelknop.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				setGegevens();
				JSONObject bestelling = new JSONObject();
				JSONObject bestellingsgegevens = new JSONObject();
				JSONObject klantgegevens = new JSONObject();
				JSONArray bestelInfo = new JSONArray();
				try 
				{
					//Gegevens van de bestelling worden in een object gezet
					bestellingsgegevens.put("Product:", productNaam);
					bestellingsgegevens.put("Aantal:", productAantal);
					klantgegevens.put("Naam:", naam);
					klantgegevens.put("Adres:", adres ); 
					klantgegevens.put("Telnr:", telefoonnummer );
					klantgegevens.put("Email:", email);

					//alle gegevens in een JSONArray 
					bestelInfo.put(bestellingsgegevens);
					bestelInfo.put(klantgegevens);

					//JSONArray wordt weer teruggestopt in een JSONObject
					bestelling.put("bestel", bestelInfo);

				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
				//er wordt verbinding gemaakt met de server en het JSONObject met het
				//JSONObject wordt naar de server verzonden
				new ServerCommunicator(v.getContext(), "192.168.1.12", 4444, bestelling.toString()).execute();				

			}
		});
		
		// annuleert de bestelling en gaat terug naar vorige scherm
		Bestel_Annuleerknop = (Button) findViewById(R.id.Bestel_Annuleerknop);
		Bestel_Annuleerknop.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				Intent intent = new Intent(context, ProductScherm.class);
				startActivity(intent); 
			}
		});
	}
}
package hsleiden.app.pie4all;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ProductScherm extends Activity {

	//Variabelen die we later nodig hebben
	Button BestelButton;
	Button AnnuleerButton;
	public String aantalProducten;
	SharedPreferences preferences;
	private String productnaam;
	private int categorie, productposition;

	// Voert de volgende dingen uit tijdens creëren van pagina
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_scherm);
		buttonListener();

		// Haalt product informatie op
		TextView naam = (TextView) findViewById(R.id.Product_Naam);
		productnaam = Main.getProducts(categorie, productposition);
		naam.setText(productnaam);
		TextView aantal = (TextView) findViewById(R.id.Product_Aantal);
		TextView list_prijs = (TextView) findViewById(R.id.Product_Prijs);
		list_prijs.setText(Main.list_prijs);
		TextView beschrijving = (TextView) findViewById(R.id.Product_Beschrijving);
		beschrijving.setText(Main.getProductInfo(productnaam));

		

		//Haal sharedPrefences op
		preferences = this.getSharedPreferences("gegevens", 0);
		this.aantalProducten = preferences.getString("Aantal", "0");
		setString();
	}

	public void setString() {

		EditText aantalEditTekst = (EditText) this
				.findViewById(R.id.Product_Aantal_Edit);
		aantalEditTekst.setText(this.aantalProducten);

	}
	
	//Aantal opslaan
	public void setGegevens() {
		EditText aantalEditTekst = (EditText) this
				.findViewById(R.id.Product_Aantal_Edit);
		this.aantalProducten = aantalEditTekst.getText().toString();

		Editor editor = preferences.edit();
		editor.putString("Aantal", aantalProducten);
		editor.commit();
	}

	public void buttonListener() {
		final Context context = this;
		BestelButton = (Button) findViewById(R.id.Product_BestelKnop);
		BestelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setGegevens();

				//Check of er een aantal is ingevoerd
				if (aantalProducten.matches("")) {
					Toast.makeText(getApplicationContext(),
							"Hoeveel wilt u er?", Toast.LENGTH_LONG)
							.show();
				} else {
					// Gaat naar bestelscherm
					Intent intent = new Intent(context, BestelScherm.class);
					startActivity(intent);
				}
			}
		});
		//Ga terug naar het Mainscherm(hoofdscherm)
		AnnuleerButton = (Button) findViewById(R.id.Product_AnnuleerKnop);
		AnnuleerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, Main.class);
				startActivity(intent);
			}
		});
	}


}
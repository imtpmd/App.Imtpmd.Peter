package application;

import java.util.HashMap;

public class PieData
{
	//singleton
	private static PieData _instance;
	public static PieData getInstance()
	{
		if( _instance == null )
			_instance = new PieData();
		
		return _instance;
	}
	


	public String[] categories = {"Vlaaien", "Cakes", "Bruidstaarten", "Verjaardagstaarten"};
	public HashMap<String, String[]> products;
	private HashMap<String, String> productInfo;
	
	private PieData()
	{	
		products = new HashMap<String,String[]>();
		productInfo = new HashMap<String,String>();
		
		String[] vlaaien = { "Kersenvlaai", "Perzikvlaai" };
		products.put("Vlaaien", vlaaien );

		productInfo.put( "Kersenvlaai", "Heerlijk verse kersen zonder pit" );
		productInfo.put( "Perzikvlaai", "Mierzoet en extra plakkerig" );
		

		String[] cakes = { "Boerencake", "Chocoladecake" };
		products.put("Cakes", cakes );

		productInfo.put("Boerencake", "Rechtstreeks van het platteland" );
		productInfo.put("Chocoladecake", "Met hele stukken pure chocolade" );
		
		
		String[] bruidstaarten = { "Chocolade bruidstaart", "Aardbei bruidstaart" };
		products.put("Bruidstaarten", bruidstaarten );
		
		productInfo.put("Chocolade bruidstaart", "Drie lagen vloeibare chocola" );
		productInfo.put("Aardbei bruidstaart", "Vier lagen alléén maar aardbei" );
		
		
		String[] verjaardagstaarten = { "Slagroomtaart", "Kwarktaart" };
		products.put("Verjaardagstaarten", verjaardagstaarten );
		
		productInfo.put("Slagroomtaart", "Meer slagroom dan taart" );
		productInfo.put("Kwarktaart", "Met citroenzure smaak" );
	}
	
	public String[] getCategories()
	{
		return categories;
	}
	
	public String[] getProducts( String category )
	{
		return products.get( category );
	}
	
	public String getProductInfo( String product )
	{
		return productInfo.get( product );
	}
}

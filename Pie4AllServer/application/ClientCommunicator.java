package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.JTextArea;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ClientCommunicator implements Runnable
{
	private Socket clientSocket;
	private JTextArea textArea;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public ClientCommunicator( JTextArea textArea, Socket clientSocket )
	{
		this.textArea = textArea;
		this.clientSocket = clientSocket;
		
		Thread thread = new Thread( this );
		thread.start();
	}
	
	
	@Override
	public void run()
	{
		try
		{
			this.connect();
			this.converse();
		}
		
		catch( IOException e ) { e.printStackTrace(); }

		this.close();
	}
	

	public void connect() throws IOException
	{
		this.writer = new PrintWriter(
			this.clientSocket.getOutputStream(), true
		);

		this.reader = new BufferedReader(
			new InputStreamReader(
				this.clientSocket.getInputStream()
			)
		);
	}
	
	private String processInput( String input )
	{
		String answer = "Could not parse sent json string";
		
		JSONObject jsonObject = (JSONObject) JSONValue.parse( input );
		
		if( jsonObject != null )
		{
			if( jsonObject.containsKey("categorielijst") )
			{	
				answer = getCategories();
			}

			if( jsonObject.containsKey("productenlijst") )
			{	
				answer = getProducts( jsonObject.get("productenlijst").toString() );
			}

			if( jsonObject.containsKey("productinfo") )
			{	
				answer = getProductInfo( jsonObject.get("productinfo").toString() );
			}

			if( jsonObject.containsKey("bestel") )
			{	
				answer = bestel( (JSONArray)jsonObject.get("bestel") );
			}
		}
		
		return answer;
	}

	private void converse() throws IOException
	{
		String line = null;
        String outputLine = null;
        writer.println(outputLine);
	    
        line = reader.readLine();
	    if (line != null) {
	        textArea.append( line );
	        outputLine = processInput( line );
	        writer.println(outputLine);
	    }
	    
	    this.close();
	}

	
	private void close()
	{
		try
		{
			this.writer.close();
			this.reader.close();
			this.clientSocket.close();
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	
	
	
	//data
	
	private String getCategories()
	{
		PieData pies = PieData.getInstance();
		
		JSONArray array = new JSONArray();
		
		for( int i = 0; i < pies.getCategories().length; i++ )
		{
			String category = pies.getCategories()[i];
			JSONObject obj = new JSONObject();
			obj.put( "naam", category );
			array.add( obj );
		}
		
		return array.toJSONString();
	}
	

	private String getProducts( String category )
	{
		PieData pies = PieData.getInstance();
		
		JSONArray array = new JSONArray();
		
		for( int i = 0; i < pies.getProducts(category).length; i++ )
		{
			String product = pies.getProducts(category)[i];
			JSONObject obj = new JSONObject();
			obj.put( "naam", product );
			array.add( obj );
		}
		
		return array.toJSONString();
	}


	private String getProductInfo( String product )
	{
		PieData pies = PieData.getInstance();
	
		JSONArray array = new JSONArray();
		String productInfo = pies.getProductInfo(product);
		JSONObject obj = new JSONObject();
		obj.put( "naam", productInfo );
		array.add( obj );
			
		return array.toJSONString();
	}


	private String bestel( JSONArray bestelInfo )
	{
		JSONObject bestelling = (JSONObject) bestelInfo.get(0);
		JSONObject koper = (JSONObject) bestelInfo.get(1);

		String productNaam = bestelling.get( "productnaam" ).toString();
		String productAantal = bestelling.get( "productaantal" ).toString();
		
		String koperNaam = koper.get( "kopernaam" ).toString();
		String koperAdres = koper.get( "koperadres" ).toString();
		String koperTelNr = koper.get( "kopertelnr" ).toString();
		String koperEmail = koper.get( "koperemail" ).toString();
		
		textArea.append("\n");
		textArea.append( "Nieuwe bestelling: " + productAantal + " " + productNaam + " voor " +
						 koperNaam + "\n" );
		textArea.append( koperAdres + "\n" );
		textArea.append( koperTelNr + "\n" );
		textArea.append( koperEmail + "\n" );
		
		return "Bestelling ontvangen, bedankt!";
	}
}

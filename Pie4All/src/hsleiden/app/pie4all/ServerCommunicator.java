package hsleiden.app.pie4all;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class ServerCommunicator implements Runnable
{
	private Thread thread;
	private String message, ip;
	private int port;
	private Context context;

	public ServerCommunicator(Context c, String ip, int port, String message )
	{	
		//gegevens om naar de server te verbinden en een message te sturen
		this.context = c;
		this.message = message;
		this.ip = ip;
		this.port = port;

		//de nieuwe thread kan tekst verzenden en ontvangen van en naar een server
		this.thread = new Thread(this);
		thread.start();
	}


	//dit is een methode die niet op de UI thread wordt aangeroepen, maar door onze eigen nieuwe thread
	//we kunnen dus niet zomaar ontvangen berichten in een userinterface object stoppen m.b.v. view.setText( message )
	//hier gebruiken we de activity voor: activity.runOnUiThread( activity )
	@Override
	public void run()
	{
		try
		{
			Socket serverSocket = new Socket();
			serverSocket.connect( new InetSocketAddress( this.ip, this.port ), 4000 );

			//verzend een bericht naar de server
			this.sendMessage( message, serverSocket );

			String response = waitForResponse(serverSocket);

			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = settings.edit();
			editor.putString("response", response);
			editor.commit();

			//gebruik de volgende twee methoden van de activity om informatie naar de UI thread (de activity) te sturen

		}

		catch( UnknownHostException e )
		{
			Log.d("debug", "can't find host");
		}

		catch( SocketTimeoutException e )
		{
			Log.d("debug", "time-out");
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	//ook deze methoden kunnen niet naar de UI direct communiceren, hou hier rekening mee
	private void sendMessage( String message, Socket serverSocket )
	{
		OutputStreamWriter outputStreamWriter = null;

		try
		{
			outputStreamWriter = new OutputStreamWriter(serverSocket.getOutputStream()) ;
		}

		catch (IOException e2)
		{
			e2.printStackTrace();
		}

		if( outputStreamWriter != null )
		{
			BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
			PrintWriter writer = new PrintWriter( bufferedWriter, true );

			writer.println(message);
		}
	}


	//wacht op server bericht (na versturen)
	private String waitForResponse(Socket serverSocket)
	{
		String response = null;
		InputStream input;

		try {
			input = serverSocket.getInputStream();
			BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(input));
			String line = "";
			StringBuilder stringBuilder = new StringBuilder();
			while ((line = responseStreamReader.readLine()) != null) {
			    stringBuilder.append(line);
			}
			responseStreamReader.close();

			response = stringBuilder.toString();	

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;

	}
}
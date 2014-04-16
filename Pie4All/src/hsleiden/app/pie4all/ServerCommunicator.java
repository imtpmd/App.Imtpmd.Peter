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
import android.os.AsyncTask;
import android.util.Log;

public class ServerCommunicator extends AsyncTask<Void, Void, String> {
	private Context context;
	private String message;
	private String ip;
	private int port;
	private String response = null;

	public ServerCommunicator(Context context, String ip, int port,
			String message) {
		super();
		// gegevens om met de server te verbinden en een message te sturen
		this.context = context;
		this.message = message;
		this.ip = ip;
		this.port = port;
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			Socket serverSocket = new Socket();
			serverSocket.connect(new InetSocketAddress(this.ip, this.port),
					4444);

			// verzend een bericht naar de server
			this.sendMessage(message, serverSocket);

			InputStream input;

			try {
				input = serverSocket.getInputStream();
				BufferedReader responseStreamReader = new BufferedReader(
						new InputStreamReader(input));
				String line = "";
				StringBuilder stringBuilder = new StringBuilder();
				while ((line = responseStreamReader.readLine()) != null) {
					stringBuilder.append(line);
				}
				responseStreamReader.close();

				this.response = stringBuilder.toString();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("Response: " + response);

			// gebruikt de volgende twee methoden van de activity om informatie
			// naar de UI thread (de activity) te sturen
		}

		catch (UnknownHostException e) {
			Log.d("debug", "can't find host");
		}

		catch (SocketTimeoutException e) {
			Log.d("debug", "time-out");
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		return response;
	}

	// Deze methoden kunnen niet direct met de UI communiceren
	private void sendMessage(String message, Socket serverSocket) {
		OutputStreamWriter outputStreamWriter = null;

		try {
			outputStreamWriter = new OutputStreamWriter(
					serverSocket.getOutputStream());
		}

		catch (IOException e2) {
			e2.printStackTrace();
		}

		if (outputStreamWriter != null) {
			BufferedWriter bufferedWriter = new BufferedWriter(
					outputStreamWriter);
			PrintWriter writer = new PrintWriter(bufferedWriter, true);

			writer.println(message);
		}
	}

}
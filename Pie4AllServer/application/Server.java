package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JTextArea;


public class Server implements Runnable
{
	private ServerSocket serverSocket;
	private Thread thread;
	private boolean stop;
	private JTextArea textArea;
	
	public Server( JTextArea textArea )
	{
		this.textArea = textArea;
		this.textArea.setEditable( false );
		this.stop = false;
		
		try
		{
			this.serverSocket = new ServerSocket( 4444 );
			this.thread = new Thread( this );
			this.thread.start();
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void requestStop()
	{
		this.stop = true;
	}
	
	@Override
	public void run()
	{
		this.textArea.append( "Waiting for clients\n" );
		
		while( this.stop == false )
		{
			try
			{
				Socket clientSocket = this.serverSocket.accept();
				new ClientCommunicator( this.textArea, clientSocket );
			}
			
			catch( IOException e )
			{
				this.textArea.append( "Failed to continue listening to clients\n" );
				e.printStackTrace();
			}
		}
	}

}

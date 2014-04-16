package application;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Pie4AllServer extends JFrame
{
	private Server server;
	
	
	
	public Pie4AllServer()
	{
		this.setDefaultCloseOperation( EXIT_ON_CLOSE );
		this.setSize( 400, 400 );
		this.setLocationRelativeTo( null );
		
		JTextArea textArea = new JTextArea();
		textArea.setWrapStyleWord( true );
		textArea.setEditable( false );
		JScrollPane scrollPane = new JScrollPane( textArea );
		this.add( scrollPane );
		
		this.setVisible( true );
		
		server = new Server( textArea );
		
		InetAddress ip;
	    try {
	     
	   ip = InetAddress.getLocalHost();
	   System.out.println("Current IP address : " + ip.getHostAddress());
	  
	    } catch (UnknownHostException e) {
	  
	   e.printStackTrace();
	  
	    }
	}


	@Override
	public void finalize()
	{
		server.requestStop();
	}


	public static void main( String[] args )
	{
		new Pie4AllServer();
	}
}

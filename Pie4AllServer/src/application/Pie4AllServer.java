package application;

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

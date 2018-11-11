package connection;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class ServerDriver extends JFrame implements ActionListener {
	private JButton btnStart;
	private JButton btnStop;
	private Server server;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			new ServerDriver();
		});
	}

	public ServerDriver(){
		super("NBG Tele-Banking Server");
		setSize(200, 200);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		add(btnStart = new JButton("Start server"));
		add(btnStop = new JButton("Stop server"));
		
		btnStart.addActionListener(this);
		btnStop.addActionListener(this);
		
		btnStop.setEnabled(false);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent event) {
		Thread t1 = null;
		if(event.getSource().equals(btnStart)) {
			server = new Server();
			t1 = new Thread(() -> {
				server.waitForRequests();
			});
			
			t1.start();
			
			btnStart.setEnabled(false);
			btnStop.setEnabled(true);
		}else if(event.getSource().equals(btnStop)) {
			if(server != null) {
				server.stop();
				
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
			}
		}
	}
}

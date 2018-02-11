import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ClintGUI extends JFrame {
	private String sendContent=null;
	private String currentContent="";
	private String userName;
	private Socket s;
	static JTextArea topT;
	public ClintGUI() throws  IOException
	{
		//Set userName
		String userName = JOptionPane.showInputDialog(null, "Input your name", "User input", JOptionPane.QUESTION_MESSAGE);
		this.userName=userName;
		//Set socket
		try {
		s=new Socket("192.168.1.13",30000);
		PrintStream ps=new PrintStream(s.getOutputStream());
		ps.println("Name:"+userName);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "Sorry, Connection fails", "Adding failed",
					JOptionPane.WARNING_MESSAGE, null);
			return;
		}
		

		topT=new JTextArea(100,50);
		 topT.setEditable(false);
		 JScrollPane top=new JScrollPane(topT);
		 
		JPanel bot=new JPanel();
		JTextArea botT=new JTextArea(100,50);
		
		JButton Send=new JButton("Send");	
			Send.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					sendContent=botT.getText();
					botT.setText("");
					BufferedWriter br;
					try {
						PrintStream ps=new PrintStream(s.getOutputStream());
						ps.println("Content:"+sendContent);
				
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			});
			//Input after type ENTER
			botT.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
					
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()==KeyEvent.VK_ENTER)
					{
						sendContent=botT.getText();
						BufferedWriter br;
						try {
							PrintStream ps=new PrintStream(s.getOutputStream());
							ps.println("Content:"+sendContent);
							botT.setCaretPosition(0);
					
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				}

				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
					if(e.getKeyCode()==KeyEvent.VK_ENTER)
					{
						botT.setText("");
					}
				}
				
			});
			new Thread(new updateThread(s)).start();
			
		bot.setLayout(new BorderLayout(0,2));
		bot.add(botT,BorderLayout.CENTER);
		bot.add(Send,BorderLayout.SOUTH);
		
		setLayout(new GridLayout(2,1,1,1));
		add(top);
		add(bot);
		
		setSize(900,600);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(userName);
		setVisible(true);	
		
	}
	class updateThread implements Runnable
	{
		private Socket s;
		private BufferedReader br=null;
		String content=null;
		public updateThread(Socket s) throws IOException
		{
			this.s=s;
			br=new BufferedReader(new InputStreamReader(s.getInputStream()));
		}
		@Override
		public void run() {
			try {
				while((content=br.readLine())!=null)
				{
					StringBuilder s=new StringBuilder(currentContent);
					s.append("\n"+content);
					currentContent=s.toString();
					topT.setText(currentContent);
					topT.setCaretPosition(topT.getText().length());
//					System.out.print(content);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}
	
}

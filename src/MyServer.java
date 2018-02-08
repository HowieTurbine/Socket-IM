import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MyServer {
	static List<Socket> socketlist=Collections.synchronizedList(new ArrayList<>());
	static ServerSocket ss;
	 public static void main(String[] args) throws IOException
	 {
		 ss=new ServerSocket(30000);
			while(true)
			{
				Socket s=ss.accept();
				System.out.println("有新的链接");
				socketlist.add(s);
				new Thread(new ServerThread(s)).start();
			}
	 }

	
		
}
class ServerThread implements Runnable
{
	Socket s= null;
	BufferedReader br=null;
	String UserName;
	ServerThread(Socket s) throws IOException
	{
		this.s=s;
		br=new BufferedReader(new InputStreamReader(s.getInputStream()));
	}
	@Override
	public void run() {
		String content=null;
		while((content=readFromClient())!=null)
		{
			if(content.startsWith("Name:"))
			{
				UserName=content.substring(5);
			}
			if(content.startsWith("Content:"))
			{
				String realContent=UserName+" at "+new Date().toString()+" said : \n"+content.substring(8)+"\n";
			
			System.out.println(realContent);
			for(Socket s : MyServer.socketlist)
			{
				PrintStream ps;
				try {
				ps = new PrintStream(s.getOutputStream());
					ps.println(realContent);
					System.out.println("Send out content: "+realContent);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}
		}
		
	}
	private String readFromClient()
	{
		try
		{
			return br.readLine();
		}
		catch(IOException e)//If catch exception, it means that the client has been closed
		{
			MyServer.socketlist.remove(s);
		}
		return null;
		
	}
}

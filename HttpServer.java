import java.net.*;
import java.io.*;
import java.util.*;

//http://localhost:8080/test.txt

class HttpServer
{
	//main method fo HttpServer
	public static void main(String args[])
	{
		System.out.println("Web Server Starting");
		try
		{
			ServerSocket ss = new ServerSocket(8080);
			//sessions = new ArrayList<HttpServerSession>();
		
			while(true)
			{
				Socket s = ss.accept();
				HttpServerSession session = new HttpServerSession(s);
				//synchronized(sessions) 
				//{
				//	sessions.add(session);
				//}
				//Start the new sessions
				session.start();
				//Print connection made and IP address
				System.out.println("Connection Made");
				InetAddress ip = s.getInetAddress();
				System.out.println("IP: " + ip.getHostAddress());
			}
			
		}
		catch(Exception ex)
		{
			System.err.println(ex.getMessage());
		}
	}
}


class HttpServerSession extends Thread
{
	//Private class variables
	private Socket client;
	private BufferedOutputStream bos;

	HttpServerSession(Socket s)
	{
		client = s;
	}

	//The HttpServerSession run method
	public void run()
	{
		try
		{
			//Makes a new buffered reader and buffered output stream listening to the client
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

			bos = new BufferedOutputStream(client.getOutputStream());
			
			byte[] byteArray = new byte[1024];

			String filename = "";
			//While the line is not empty
			while(true)
			{
				String line = reader.readLine();
				if(line == null || line.compareTo("") == 0)
				{
					break;
				}
				String parts[] = line.split(" ");
				if((parts.length == 3 && (parts[0].compareTo("GET") == 0)))
				{
					filename = parts[1].substring(1);
					if(!(filename.equals("")))	
						System.out.println("Request: " + filename);
					else 
						filename = "index.html";
				}
			}

			//Makes a new file with the filename
			File file = new File(filename);
			FileInputStream fis = new FileInputStream(file);

			println(bos, "HTTP/1.1 200 OK");
			println(bos, "");

			int r = 0;
			System.out.println(filename);

			//while there are still more bytes to read
			while((r = fis.read(byteArray)) != -1)
			{
				bos.write(byteArray, 0, r);
				sleep(1000);
			}
			//flush the bos
			bos.flush();
			System.out.println("PRINTED");
		}
		catch(FileNotFoundException ex)
		{
			try
			{
				println(bos, "HTTP/1.1 404 FILE NOT FOUND");
				System.err.println("404 Error: " + ex.getMessage());
			}
			catch(IOException e)
			{
				System.err.println("Error: " + e.getMessage());
 			}
		}
		catch(Exception ex)
		{
			System.err.println("Error: " + ex.getMessage());
		}
		finally
		{
			try { client.close(); }
			catch(IOException e) { System.err.println("Error"); }
		}
	}

	private void println(BufferedOutputStream bos, String s)
		throws IOException
	{
		String news = s + "\r\n";
		byte[] array = news.getBytes();
		for(int i=0; i<array.length; i++)
		{
			bos.write(array[i]);
		}
		return;
	}

	
}

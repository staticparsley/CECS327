import java.net.*;
import java.io.*;
import java.util.*;

public class CLIENT_PROGRAM {
	public static void main(String[] args) {
		DatagramSocket aSocket = null;
		
		try {
			aSocket = new DatagramSocket();
			byte[] m = args[0].getBytes();
			
			InetAddress aHost = InetAddress.getByName(args[1]);
			
			int portNum = 32710;
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, portNum);
			aSocket.send(request);
			
			byte[] buffer = new byte[100];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receve(reply);
			
			System.out.println("Reply: " + new String(reply.getData()) + "\n");
		}
		catch (SocketException e){
			System.out.println("Socket: "+e.getMessage());
		}
		catch (IOException e) {
			System.out.println("IO: "+e.getMessage());			
		}
		finally{
			if(aSocket != null){
				aSocket.close();
			}
		}
	}
}
import java.net.*; 
import java.io.*;
import java.util.*;

public class assn10{
	public static void main(String[] args) {
		ArrayList<Integer> hopCount = new ArrayList<Integer>();
		for(int i = 0; i < 1000; i++) {
			int rtnHop = findAddr();
			if(rtnHop == -1) {
				i--;
			}
			else {
				hopCount.add(rtnHop);
			}
		}
		
		HashMap<Integer,Integer> countMap = new HashMap<Integer,Integer>();
		for(int elem : hopCount) {
			if(countMap.containsKey(elem)) {
				countMap.put(elem,countMap.get(elem)+1);
			}
			else {
				countMap.put(elem,1);
			}
		}
		System.out.println("\n" + countMap);
	}
	
	
	public static String createPastry() {
		Random r = new Random();
		String id = "";
		for(int i = 0; i < 4; i++) {
			int num = r.nextInt(3);
			id += num;
		}
		return id;
	}
	
	
	
	public static int findAddr() {
		DatagramSocket aSocket = null;
		int count = 1;
		
		try {
			String pastry = createPastry();
			System.out.println("\n***************************\n\nInput: "+pastry );
			String ip = "54.213.89.200";
			boolean found = false;
			ArrayList<String> pastryContainer = new ArrayList<String>();
			while(!found) {
				aSocket = new DatagramSocket();
				
				byte[] m = pastry.getBytes();
				InetAddress aHost = InetAddress.getByName(ip);
				int portNum = 32710;
				DatagramPacket req = new DatagramPacket(m , m.length, aHost, portNum);
				aSocket.setSoTimeout(500);
				aSocket.send(req);
				byte[] buff = new byte [1000];
				DatagramPacket resp = new DatagramPacket(buff, buff.length);
				aSocket.receive(resp);
				System.out.println(count+"  Returned\t"+new String(resp.getData())+"\n");
				String [] val = new String(resp.getData()).split(":");
				if(val[0].equals(pastry) ) {
					System.out.println("Found");
					found = true;
				}  
				else if(val[0].trim().equalsIgnoreCase("null") || val[1].trim().equalsIgnoreCase("null")) { 
					System.out.println("Can not be found");
					found = true; 
				} 
				else if(pastryContainer.contains(val[0].trim())) {
					System.out.println("Excluded");
					return -1;
				} 
				else {
					ip = val[1];
					pastryContainer.add(val[0].trim());
					count++;
				}
			}
		}
		catch (SocketException e) {
			System.out.println("Socket Timeout: "+e.getMessage());
			System.out.println("Excluded");
			
			return -1;
		}
		catch (IOException e) {
			System.out.println("IO: "+e.getMessage());
			System.out.println("Excluded");
			
			return -1;
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
			System.out.println("Excluded");
			
			return -1;
		}
		finally {
			if(aSocket != null) {
				aSocket.close();
			}
		}
		return count;
	}	
}
import java.rmi.*;
import java.rmi.server.*;   
 
public class Assn6Server {
	   public static void main (String[] argv) {
			try {
				Method localMethods = new Method();
				Naming.rebind("rmi://" + argv[0] + "/cecs327", localMethods);
				System.out.println("Server is ready...");
			}catch(Exception e) {
				System.out.println("Error " + e);
			}		
		}
}
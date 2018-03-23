import java.rmi.*;
import java.util.*;

public class Assn6Client {
	public static void main(String[] args) {
		try {

			MethodInterface serverMethods = (MethodInterface)Naming.lookup(args[0]);

			int input = Integer.parseInt(args[2]);
			int result = -1;

			switch(args[1]){
				case "fibonacci":
					result = serverMethods.fibonacci(input);
					System.out.println("The fibonacci of "+args[2]+" is "+result);
					break;
				case "factorial":
					result = serverMethods.factorial(input);
					System.out.println("The factorial of "+args[2]+" is "+result);
					break;
				default:
					System.out.println("Err");
			}
		}catch (Exception e) {
			System.out.println(e);
		}
	}
}
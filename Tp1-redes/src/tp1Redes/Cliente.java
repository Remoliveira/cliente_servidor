package tp1Redes;
import java.net.*;
import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.Scanner;



public class Cliente {
	


	public static void main(String[] agrs)throws UnknownHostException, IOException {
		
		String url;
		
		System.out.println("Url:");
		
		Scanner s  = new Scanner(System.in);
		url = s.nextLine();
		
		
		String novourl = url.replace("https://","").replace("httlp://","");
		
		
		Socket cliente = new Socket(novourl,8080);
		
		if(cliente.isConnected()) {
			
			System.out.println("Conectado a " + cliente.getInetAddress());
			
			
			//Scanner scan = new Scanner(System.in);
	        //PrintStream saida = new PrintStream(cliente.getOutputStream());
	        
	        OutputStream envio = cliente.getOutputStream();
	        
	        
	       Scanner scan  = new Scanner(cliente.getInputStream());
	       
	        
		}    
        
   
        cliente.close();

	}
	
}

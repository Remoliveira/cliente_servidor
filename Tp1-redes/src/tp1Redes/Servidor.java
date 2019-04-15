

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Scanner;

public class Servidor implements Runnable {

	public static String url;
	public static String navegador;
	public static String caminho;
	public static Socket cliente;
	static String door;


	public Servidor(Socket cliente) {
		this.cliente = cliente;
	}

	public static void main(String[] agrs) throws IOException {

		try {

			Scanner s = new Scanner(System.in);
			System.out.println("Digite a url" + "\n");
			caminho = s.nextLine();

			ServerSocket servidor = new ServerSocket(8080);
			System.out.println("Servidor iniciado na porta " + servidor.getLocalPort() + "\n");

			while (true) {

				Socket cliente = servidor.accept();
				new Thread(new Servidor(cliente)).start();
			}

			// cliente.close();
			// servidor.close();

		} catch (Exception e) {

			System.out.println("Erro: " + e.getMessage());
		}

	}

	public void run() {

		System.out.println("Nova conexão com o cliente " + cliente.getInetAddress().getHostAddress() + "\n");

		try {

			BufferedWriter resposta;

			resposta = new BufferedWriter(
					new OutputStreamWriter(new BufferedOutputStream(cliente.getOutputStream()), "UTF-8"));

			Scanner recebido = new Scanner(cliente.getInputStream());

			while (recebido.hasNextLine()) {
				String requisicao = recebido.nextLine();
				System.out.println(requisicao);
				String[] parts = requisicao.split(" ");
				if (parts.length == 3) {
					navegador = parts[0];
					url = parts[1];
					door = parts[2];
				} else if (parts.length == 2) {
					navegador = parts[0];
					url = parts[1];
					door = "8080";
				} else {
					System.out.println("Você digitou errado!");
				}
				break;
			}
			GET();
			resposta.flush();
			resposta.close();
			cliente.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public static void GET() throws IOException {
		
		System.out.println("caminho: " + caminho);
		System.out.println("url: " + url);
		String arquivo = caminho+url;
		
		File file = new File(arquivo);
		// System.out.println("--->" + arquivo);

		if (file.isDirectory()) {

			OutputStream stream = cliente.getOutputStream();
			String arquivos[] = file.list();
			String mensagem = "Lista de arquivos: ";
			stream.write(mensagem.getBytes(Charset.forName("UTF-8")));

			String espaco = "\n";
			for (int i = 0; i < arquivos.length; i++) {

				//System.out.println(arquivos[i]);
				stream.write(espaco.getBytes(Charset.forName("UTF-8")));
				stream.write(arquivos[i].getBytes(Charset.forName("UTF-8")));
				stream.write(espaco.getBytes(Charset.forName("UTF-8")));

			}
			stream.flush();

		} else if (file.exists()) {

			String imita = Files.probeContentType(file.toPath());
			System.out.println("Content Type: " + imita);
			OutputStream stream = cliente.getOutputStream();

			String send = "HTTP/1.1 200 OK\r\nContent-Type: " + imita + "\r\n";
			stream.write(send.getBytes(Charset.forName("UTF-8")));

			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);

			stream.write(("Content-Length: " + String.valueOf(file.length()) + "\r\n\r\n").getBytes());

			long tam = file.length();
			int valor = 0;
			byte[] contents;
			while (tam > 0) {

				if (tam >= 1) {
					tam = tam - 1;
					valor = 1;
				} else if (tam < 1) {
					valor = (int) tam;
					tam = 0;
				}

				contents = new byte[valor];
				bis.read(contents, 0, valor);
				stream.write(contents);

			}
			stream.flush();
			fis.close();
			bis.close();

		}
	}
}

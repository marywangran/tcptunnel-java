import java.io.*;
import java.net.*;

class Relay implements Runnable {
	InputStream input;
	OutputStream output;
	public Relay(InputStream input, OutputStream output) {
		this.input = input;
		this.output = output;
	}

	public void run() {
		int n = 0, m = 0;
		byte[] data = new byte[2049];
		try {
			while (true) {
				n = input.read(data);
				output.write(data, 0, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
class ServerThread implements Runnable {

	Socket front_socket = null;
	Socket back_socket = null;
	int port;
	public ServerThread(Socket socket, int port) {
		this.front_socket = socket;
		this.port = port;
	}

	public void run() {
		InputStream infront = null;
		InputStream inback = null;
		OutputStream outfront = null;
		OutputStream outback = null;
		try {
            		ServerSocket server = new ServerSocket(port);
                	back_socket = server.accept();

			infront = front_socket.getInputStream();
			outfront = front_socket.getOutputStream();
			inback = back_socket.getInputStream();
			outback = back_socket.getOutputStream();
                	new Thread(new Relay(infront, outback)).start();
                	new Thread(new Relay(inback, outfront)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

public class Server {
    public static void main(String[] args) {
	    int port = 1234;
        try {
            ServerSocket server = new ServerSocket(port);
            while (true) {
                Socket socket = server.accept();
		port = port + 1;
                Thread thread = new Thread(new ServerThread(socket, port));
                thread.start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

import java.io.*;
import java.net.*;
import java.math.*;

class Net2tun implements Runnable {

	DataInputStream input = null;
	FileOutputStream fout = null;
	public Net2tun(InputStream input, FileOutputStream fout) {
		this.input = new DataInputStream(input);
		this.fout = fout;
	}

	public void run() {
		byte[] data = new byte[2048];
		byte[] lenbytes = new byte[4];
		int len, len2 = 0;
		try {
			while (true) {
				input.readFully(lenbytes, 0, 4);
				len = new BigInteger(lenbytes).intValue();
				//len2 = new LittleInteger(lenbytes).intValue();
				input.readFully(data, 0, len);
				fout.write(data, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Tun2net implements Runnable {

	OutputStream output = null;
	FileInputStream fin = null;
	public Tun2net(FileInputStream fin, OutputStream output) {
		this.output = output;
		this.fin = fin;
	}

	public void run() {
		byte[] data = new byte[2048];
		int len;
		try {
			while (true) {
				len = fin.read(data);
				if (len > 0) {
					output.write(BigInteger.valueOf(len).toByteArray(), 0, 4);
					output.write(data, 0, len);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

public class Edge {
	public static void main(String[] args) {
		String addr = args[0];
		int port = Integer.parseInt(args[1]);
		try {
            		Socket socket = new Socket(addr, port);
			OutputStream output = socket.getOutputStream();
			InputStream input = socket.getInputStream();
			File file = new File("/dev/net/edge2");
      // 这里需要用native方法打开tun设备
			FileOutputStream fout = new FileOutputStream(file);
			FileInputStream fin = new FileInputStream(file);
			new Thread(new Tun2net(fin, output)).start();
			new Thread(new Net2tun(input, fout)).start();
			System.out.println("aaaaa");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}

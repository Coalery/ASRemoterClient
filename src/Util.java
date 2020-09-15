import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;

import VoiceChatClient.Client;

public class Util {
	private Robot r;
	private Socket s;
	private Client c;
	
	public Util(Socket s) throws AWTException {
		r = new Robot(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
		this.s  = s;
	}
	
	public void sendDefaultInfo() throws IOException {
		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		
		out.writeUTF(Main.name);
		out.write(Main.service);
		out.writeUTF(Main.address);
		out.writeUTF(Main.phonenum);
	}
	
	public void process() throws IOException, EOFException {
		int command = s.getInputStream().read();
		
		if(command == 0)
			mouseMove();
		else if(command == 1)
			mouseClick();
		else if(command == 2)
			mouseRelease();
		else if(command == 3)
			mouseWheel();
		else if(command == 4)
			keyPressed();
		else if(command == 5)
			keyReleased();
		else if(command == 6)
			showMessage();
		else if(command == 7)
			setSize();
		else if(command == 8)
			fullScreen();
		else if(command == 9)
			recieveFile();
		else if(command == 10)
			System.exit(0);
		else if(command == 11) {
			c = new Client("127.0.0.1", 1049);
			c.start();
		} else if(command == 12) {
			c.killClient();
		}
	}
	
	public void mouseMove() throws IOException {
		DataInputStream in = new DataInputStream(s.getInputStream());
		
		int x = in.readInt();
		int y = in.readInt();
		
		r.mouseMove(x, y);
	}
	
	public void mouseClick() throws IOException {
		DataInputStream in = new DataInputStream(s.getInputStream());
		
		int mask = in.readInt();
		
		try {r.mousePress(mask);} catch (IllegalArgumentException e) {}
	}
	
	public void mouseRelease() throws IOException {
		DataInputStream in = new DataInputStream(s.getInputStream());
		
		int mask = in.readInt();
		
		try {r.mouseRelease(mask);} catch (IllegalArgumentException e) {}
	}
	
	public void mouseWheel() throws IOException {
		DataInputStream in = new DataInputStream(s.getInputStream());
		
		int wheel = in.readInt();
		
		r.mouseWheel(wheel);
	}
	
	public void keyPressed() throws IOException {
		DataInputStream in = new DataInputStream(s.getInputStream());
		
		int keycode = in.readInt();
		
		r.keyPress(keycode);
	}
	
	public void keyReleased() throws IOException {
		DataInputStream in = new DataInputStream(s.getInputStream());
		
		int keycode = in.readInt();
		
		r.keyRelease(keycode);
	}
	
	public void showMessage() throws IOException {
		DataInputStream in = new DataInputStream(s.getInputStream());
		
		JFrame tmp = new JFrame("¸Þ¼¼Áö µµÂø");
		JLabel l = new JLabel("", JLabel.CENTER);
		
		l.setText(in.readUTF());
		
		tmp.setSize(300, 200);
		tmp.add(l, "Center");
		
		tmp.addWindowListener(new WindowAdapter() {public void windowClosing(WindowEvent event) {
			tmp.setVisible(false);
			tmp.dispose();
		}});
		
		Thread t = new Thread() {
			@Override
			public void run() {
				try {Thread.sleep(5000);} catch (InterruptedException e) {}
				
				tmp.setVisible(false);
				tmp.dispose();
			}
		};
		t.start();
		
		tmp.setResizable(false);
		tmp.setVisible(true);
	}
	
	public void setSize() throws IOException {
		DataInputStream in = new DataInputStream(s.getInputStream());
		Threads.setC(in.readInt(), in.readInt(), in.readInt(), in.readInt());
	}
	
	public void fullScreen() {
		Threads.setC(0, 0, Toolkit.getDefaultToolkit().getScreenSize());
	}
	
	public void recieveFile() throws IOException {
		String fileName = "";
		
		DataInputStream dis = new DataInputStream(s.getInputStream());
		
		fileName = dis.readUTF();
		FileOutputStream fos = new FileOutputStream(Main.FILE_RECIEVE_PATH + "\\" + fileName);
		
		byte[] buffer = new byte[4096]; 
		
		long filesize = dis.readLong();
		int read =0 ;
		int remaining = (int)(filesize > 4096 ? 4096 : filesize);
		
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			filesize -= read;
			remaining = (int)(filesize > 4096 ? 4096 : filesize);
			fos.write(buffer, 0, read);
		}
		fos.close();
		
		s = null;
		s = new Socket(Main.IP, Main.port);
	}
}

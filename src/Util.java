import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;

import VoiceChatClient.Client;

public class Util {
	private Robot r;
	private Client c;
	
	private InputStream dataInputStream;
	private OutputStream dataOutputStream;
	
	private JFrame callingIndicator;
	private JLabel callingTimeLabel;
	private boolean isCalling;
	
	private int callingSec;
	
	public Util(Socket dataSocket){
		isCalling = false;
		
		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			return;
		}
		
		try {
			dataInputStream = dataSocket.getInputStream();
			dataOutputStream = dataSocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void sendDefaultInfo() throws IOException {
		DataOutputStream out = new DataOutputStream(dataOutputStream);
		
		out.writeUTF(Main.name);
		out.write(Main.service);
		out.writeUTF(Main.address);
		out.writeUTF(Main.phonenum);
	}
	
	public void process() throws IOException, EOFException {
		int command = dataInputStream.read();
		
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
			callingIndicator = new JFrame("통화중");
			callingIndicator.setSize(200, 100);
			callingIndicator.setLayout(new BorderLayout());
			callingIndicator.getContentPane().setBackground(new Color(192, 0, 0));
			
			callingTimeLabel = new JLabel("통화 중 (00:00:00)", JLabel.CENTER);
			callingTimeLabel.setForeground(Color.WHITE);
			callingIndicator.add(callingTimeLabel, BorderLayout.CENTER);
			
			c = new Client(Main.IP, 1049);
			c.start();
			isCalling = true;
			
			new Thread() {
				@Override
				public void run() {
					while(true) {
						if(!isCalling) break;
						try { Thread.sleep(1000); } catch(InterruptedException e) {}
						callingSec++;
						
						int sec = callingSec;
						int minute = sec / 60;
						sec = sec % 60;
						int hour = minute / 60;
						minute = minute % 60;
						
						String timerStr = String.format("통화 중 (%02d:%02d:%02d)", hour, minute, sec);
						callingTimeLabel.setText(timerStr);
					}
				}
			}.start();
			
			callingIndicator.setVisible(true);
			callingIndicator.setResizable(false);
		} else if(command == 12) {
			callingIndicator.setVisible(false);
			callingIndicator.dispose();
			isCalling = false;
		}
	}
	
	public void mouseMove() throws IOException {
		DataInputStream in = new DataInputStream(dataInputStream);
		
		int x = in.readInt();
		int y = in.readInt();
		
		r.mouseMove(x, y);
	}
	
	public void mouseClick() throws IOException {
		DataInputStream in = new DataInputStream(dataInputStream);
		
		int mask = in.readInt();
		
		try {r.mousePress(mask);} catch (IllegalArgumentException e) {}
	}
	
	public void mouseRelease() throws IOException {
		DataInputStream in = new DataInputStream(dataInputStream);
		
		int mask = in.readInt();
		
		try {r.mouseRelease(mask);} catch (IllegalArgumentException e) {}
	}
	
	public void mouseWheel() throws IOException {
		DataInputStream in = new DataInputStream(dataInputStream);
		
		int wheel = in.readInt();
		
		r.mouseWheel(wheel);
	}
	
	public void keyPressed() throws IOException {
		DataInputStream in = new DataInputStream(dataInputStream);
		
		int keycode = in.readInt();
		
		r.keyPress(keycode);
	}
	
	public void keyReleased() throws IOException {
		DataInputStream in = new DataInputStream(dataInputStream);
		
		int keycode = in.readInt();
		
		r.keyRelease(keycode);
	}
	
	public void showMessage() throws IOException {
		DataInputStream in = new DataInputStream(dataInputStream);
		
		JFrame tmp = new JFrame("메세지 받음");
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
		DataInputStream in = new DataInputStream(dataInputStream);
		Threads.setC(in.readInt(), in.readInt(), in.readInt(), in.readInt());
	}
	
	public void fullScreen() {
		Threads.setC(0, 0, Toolkit.getDefaultToolkit().getScreenSize());
	}
	
	public void recieveFile() throws IOException {
		String fileName = "";
		
		DataInputStream dis = new DataInputStream(dataInputStream);
		
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
	}
}

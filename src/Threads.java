import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import javax.imageio.ImageIO;

public class Threads {
	
	private static int captureLocX, captureLocY, captureWidth, captureHeight;
	
	public static int getCX() {return captureLocX;}
	public static int getCY() {return captureLocY;}
	public static int getCWidth() {return captureWidth;}
	public static int getCHeight() {return captureHeight;}
	
	public static void setCX(int x) {captureLocX = x;}
	public static void setCY(int y) {captureLocY = y;}
	public static void setCWidth(int width) {captureWidth = width;}
	public static void setCHeight(int height) {captureHeight = height;}
	
	public static void setC(int x, int y, int width, int height) {
		captureLocX = x;
		captureLocY = y;
		captureWidth = width;
		captureHeight = height;
	}
	
	public static void setC(Point p, int width, int height) {
		captureLocX = (int)p.getX();
		captureLocY = (int)p.getY();
		captureWidth = width;
		captureHeight = height;
	}
	
	public static void setC(int x, int y, Dimension wh) {
		captureLocX = x;
		captureLocY = y;
		captureWidth = (int)wh.getWidth();
		captureHeight = (int)wh.getHeight();
	}
	
	public static void setC(Point p, Dimension wh) {
		captureLocX = (int)p.getX();
		captureLocY = (int)p.getY();
		captureWidth = (int)wh.getWidth();
		captureHeight = (int)wh.getHeight();
	}
	
	public static class SendScreenThread extends Thread {
		
		private Socket s;
		
		public SendScreenThread(Socket s) {
			this.s = s;
		}
		
		public void sendScreen() {
			try {
			BufferedImage bi = new Robot().createScreenCapture(new Rectangle(captureLocX, captureLocY, captureWidth, captureHeight));
			ImageIO.write(bi, "png", s.getOutputStream());
			} catch (IOException | AWTException e) {}
		}
		
		@Override
		public void run() {
			int counter = 0;
			
			try {
			while(true) {
				sendScreen();
				
				if(counter++ > 5) {
					s.close();
					s = null;
					s = new Socket(Main.IP, Main.port+1);
					counter = 0;
				}
				
				try { Thread.sleep(500); } catch (InterruptedException e) {e.printStackTrace();}
			}
			} catch (Exception e) {e.printStackTrace();}
			System.out.println("Thread Stop.");
		}
	}
	
	public static class Answer extends Thread {
		Util u;
		
		public Answer(Socket s) {
			try {u = new Util(s);} catch (AWTException e) {e.printStackTrace();}
			try {u.sendDefaultInfo();} catch (IOException e) {e.printStackTrace();}
		}
		
		@Override
		public void run() {
			try {while(true) {try {
				u.process();
			} catch (EOFException eo) {eo.printStackTrace();}}} catch (IOException e) {System.exit(0);} 
		}
	}
	
	
}

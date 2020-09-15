import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
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
		private Socket scrSocket;
		private OutputStream scrOutputStream;
		
		public SendScreenThread(Socket scrSocket) {
			this.scrSocket = scrSocket;
			
			try {
				scrOutputStream = scrSocket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void sendScreen() {
			try {
				BufferedImage bi = new Robot().createScreenCapture(new Rectangle(captureLocX, captureLocY, captureWidth, captureHeight));
				ImageIO.write(bi, "png", scrOutputStream);
			} catch (IOException | AWTException e) {}
		}
		
		@Override
		public void run() {
			try {
				while(true) {
					sendScreen();
					try { Thread.sleep(250); } catch (InterruptedException e) {e.printStackTrace();}
				}
			} catch (Exception e) {e.printStackTrace();}
			
			if(scrSocket != null && !scrSocket.isClosed()) {
				try { scrSocket.close(); } catch(IOException e) { e.printStackTrace(); }			
			}
		}
	}
	
	public static class Answer extends Thread {
		Util util;
		
		public Answer(Socket s) {
			util = new Util(s);
			try {util.sendDefaultInfo();} catch (IOException e) {e.printStackTrace();}
		}
		
		@Override
		public void run() {
			try {while(true) {try {
				util.process();
			} catch (EOFException eo) {eo.printStackTrace();}}} catch (IOException e) {System.exit(0);} 
		}
	}
	
	
}

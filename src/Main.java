import java.awt.Choice;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Main {
	
	public static final String FILE_RECIEVE_PATH = "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop";
	public static final String IP = "192.168.0.106";
	public static final int port = 5678;
	
	public static String name = "";
	public static int service = 0;
	public static String address = "";
	public static String phonenum = "";
	
	public static void main(String[] args){
		new Main().data();
	}
	
	public void data() {
		JFrame dataFrame = new JFrame("데이터를 입력하세요...");
		dataFrame.addWindowListener(new WindowAdapter() {public void windowClosing(WindowEvent event) {System.exit(0);}});
		
		dataFrame.setBounds(100, 100, 225, 345);
		dataFrame.getContentPane().setLayout(null);
		
		
		JLabel nameLabel = new JLabel("이름 입력", JLabel.CENTER);
		nameLabel.setBounds(12, 10, 185, 15);
		dataFrame.getContentPane().add(nameLabel);
		
		JTextField nameTextField = new JTextField();
		nameTextField.setBounds(12, 29, 185, 31);
		dataFrame.getContentPane().add(nameTextField);
		
		
		JLabel serviceLabel = new JLabel("받을 서비스 입력", JLabel.CENTER);
		serviceLabel.setBounds(12, 70, 185, 20);
		dataFrame.getContentPane().add(serviceLabel);
		
		Choice serviceChoice = new Choice();
		serviceChoice.setBounds(12, 89, 185, 31);
		dataFrame.getContentPane().add(serviceChoice);
		
		serviceChoice.add("[ Service Select ]");
		serviceChoice.add("Optimizing Computer");
		serviceChoice.add("Virus");
		serviceChoice.add("Fix Program");
		serviceChoice.add("Etc.");
		
		
		JLabel addressLabel = new JLabel("주소 입력", JLabel.CENTER);
		addressLabel.setBounds(12, 127, 185, 20);
		dataFrame.getContentPane().add(addressLabel);
		
		JTextField addressTextField = new JTextField();
		addressTextField.setBounds(12, 150, 185, 31);
		dataFrame.getContentPane().add(addressTextField);
		
		
		JLabel phoneLabel = new JLabel("전화번호 입력", SwingConstants.CENTER);
		phoneLabel.setBounds(12, 191, 185, 20);
		dataFrame.getContentPane().add(phoneLabel);
		
		JTextField phoneTextField = new JTextField();
		phoneTextField.setBounds(12, 214, 185, 31);
		dataFrame.getContentPane().add(phoneTextField);
		
		
		JButton btnComplete = new JButton("입력 완료");
		btnComplete.setBounds(61, 262, 97, 31);
		dataFrame.getContentPane().add(btnComplete);
		
		btnComplete.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent event) {
			nameLabel.setForeground(nameTextField.getText().length() == 0 ? Color.RED : Color.BLACK);
			serviceLabel.setForeground(serviceChoice.getSelectedIndex() == 0 ? Color.RED : Color.BLACK);
			addressLabel.setForeground(addressTextField.getText().length() == 0 ? Color.RED : Color.BLACK);
			phoneLabel.setForeground(phoneTextField.getText().length() == 0 ? Color.RED : Color.BLACK);
			
			if(nameTextField.getForeground() == Color.RED || serviceLabel.getForeground() == Color.RED || addressLabel.getForeground() == Color.RED || phoneLabel.getForeground() == Color.RED) {
				Toolkit.getDefaultToolkit().beep();
				return;
			} // Invalid Value
			
			name = nameTextField.getText();
			service = serviceChoice.getSelectedIndex();
			address = addressTextField.getText();
			phonenum = phoneTextField.getText();
			
			dataFrame.setVisible(false);
			dataFrame.dispose();
			
			start();
		}});
		
		dataFrame.setResizable(false);
		dataFrame.setVisible(true);
	}
	
	public void start() {
		try {
			Socket s = new Socket(IP, port);
			Socket s2 = new Socket(IP, port+1);
			Threads.setC(0, 0, Toolkit.getDefaultToolkit().getScreenSize());
			
			new Threads.Answer(s).start();
			new Threads.SendScreenThread(s2).start();
			
		} catch (IOException e) {e.printStackTrace();}
	}
	
}

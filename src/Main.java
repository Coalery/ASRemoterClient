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
	public static final String IP = "192.168.200.180";
	public static final int port = 5678;
	
	public static String name = "";
	public static int service = 0;
	public static String address = "";
	public static String phonenum = "";
	
	public static void main(String[] args){
		new Main().data();
	}
	
	public void data() {
		JFrame frame = new JFrame("데이터를 입력하세요...");
		frame.addWindowListener(new WindowAdapter() {public void windowClosing(WindowEvent event) {System.exit(0);}});
		
		frame.setBounds(100, 100, 225, 345);
		frame.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("\uC774\uB984 \uC785\uB825", JLabel.CENTER);
		label.setBounds(12, 10, 185, 15);
		frame.getContentPane().add(label);
		
		JTextField tf = new JTextField();
		tf.setBounds(12, 29, 185, 31);
		frame.getContentPane().add(tf);
		
		JLabel lblNewLabel = new JLabel("\uBC1B\uC744 \uC11C\uBE44\uC2A4 \uC785\uB825", JLabel.CENTER);
		lblNewLabel.setBounds(12, 70, 185, 20);
		frame.getContentPane().add(lblNewLabel);
		
		Choice tf_1 = new Choice();
		tf_1.setBounds(12, 89, 185, 31);
		frame.getContentPane().add(tf_1);
		
		tf_1.add("[ 서비스 선택 ]");
		tf_1.add("컴퓨터 최적화");
		tf_1.add("바이러스 치료");
		tf_1.add("프로그램 고장 지원");
		tf_1.add("기타");
		
		JLabel label_1 = new JLabel("\uC8FC\uC18C \uC785\uB825", JLabel.CENTER);
		label_1.setBounds(12, 127, 185, 20);
		frame.getContentPane().add(label_1);
		
		JTextField tf_2 = new JTextField();
		tf_2.setBounds(12, 150, 185, 31);
		frame.getContentPane().add(tf_2);
		
		JButton btnNewButton = new JButton("\uC785\uB825 \uC644\uB8CC");
		btnNewButton.setBounds(61, 262, 97, 31);
		frame.getContentPane().add(btnNewButton);
		
		JLabel label_2 = new JLabel("\uC804\uD654\uBC88\uD638 \uC785\uB825", SwingConstants.CENTER);
		label_2.setBounds(12, 191, 185, 20);
		frame.getContentPane().add(label_2);
		
		JTextField tf_3 = new JTextField();
		tf_3.setBounds(12, 214, 185, 31);
		frame.getContentPane().add(tf_3);
		
		btnNewButton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent event) {
			if(tf_3.getText().length() == 0)
				label_2.setForeground(Color.RED);
			else
				label_2.setForeground(Color.BLACK);
			if(tf_2.getText().length() == 0)
				label_1.setForeground(Color.RED);
			else
				label_1.setForeground(Color.BLACK);
			if(tf_1.getSelectedItem().equals("[ 서비스 선택 ]"))
				lblNewLabel.setForeground(Color.RED);
			else
				lblNewLabel.setForeground(Color.BLACK);
			if(tf.getText().length() == 0)
				label.setForeground(Color.RED);
			else
				label.setForeground(Color.BLACK);
			if(label.getForeground() == Color.RED || lblNewLabel.getForeground() == Color.RED || label_1.getForeground() == Color.RED || label_2.getForeground() == Color.RED) {
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			name = tf.getText();
			service = tf_1.getSelectedIndex();
			address = tf_2.getText();
			phonenum = tf_3.getText();
			
			frame.setVisible(false);
			frame.dispose();
			
			start();
		}});
		
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	public void start() {
		try {
//			String IP = JOptionPane.showInputDialog("아이피를 입력하세요");
			
			Socket s = new Socket(IP, port);
			Socket s2 = new Socket(IP, port+1);
			Threads.setC(0, 0, Toolkit.getDefaultToolkit().getScreenSize());
			
			new Threads.Answer(s).start();
			new Threads.SendScreenThread(s2).start();
			
		} catch (IOException e) {e.printStackTrace();}
	}
	
}

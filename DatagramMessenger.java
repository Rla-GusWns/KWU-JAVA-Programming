//데이터그램 방식 
// 둘만 채팅 할 수 있는 방식

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class MessengerA {
	public JTextField txtField;
	public JTextArea txtArea;

	DatagramSocket socket;
	DatagramPacket packet;

	InetAddress add = null;

	// 바뀌면 안돼!
	final int myPort = 5000; // 수신용
	final int otherPort = 6000; // 송신용

	public MessengerA() throws IOException {
		MyFrameA f = new MyFrameA();
		add = InetAddress.getByName("192.168.1.10");
		socket = new DatagramSocket(myPort);
	}

	//패킷을 받아서 텍스트에 뿌리기
	public void process() {
		//패킷은 소켓처럼 연결하는게 아니라 생성할 때 마다 보내~
		while(true) {
			try {
				byte[] buf = new byte[256];
				
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet); //송신측에서 보낸 패킷 받기
				
				//패킷 출력
				txtArea.append("RECIEVE: " + new String(buf) + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	class MyFrameA extends JFrame implements ActionListener {

		public MyFrameA() {
			super("DJ 채팅방");
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			txtField = new JTextField(30);
			txtField.addActionListener(this);

			txtArea = new JTextArea(10, 30);

			this.add(txtField, BorderLayout.PAGE_END);
			this.add(txtArea, BorderLayout.CENTER);

			this.pack();
			this.setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String s = txtField.getText();
			
			//데이터 그램 방식 = 바이트
			byte[] buf = s.getBytes();
			
			//데이터그램은 패킷 단위로 보내줘~
			DatagramPacket packet;
			
			//패킷 생성
			packet = new DatagramPacket(buf, buf.length, add, otherPort);
			
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			txtArea.append("SEND: " + s + "\n");
//			txtField.selectAll();//전체 선택
			txtField.setText("");
			
			//채팅창의 커서위치
			txtArea.setCaretPosition(txtArea.getDocument().getLength()); 
		}
	}
}

class MessengerB {
	public JTextField txtField;
	public JTextArea txtArea;

	DatagramSocket socket;
	DatagramPacket packet;

	InetAddress add = null;

	// 바뀌면 안돼!
	final int myPort = 6000; // 수신용
	final int otherPort = 5000; // 송신용

	public MessengerB() throws IOException {
		MyFrameB f = new MyFrameB();
		add = InetAddress.getByName("127.0.0.1");
		socket = new DatagramSocket(myPort);
	}

	//패킷을 받아서 텍스트에 뿌리기
	public void process() {
		//패킷은 소켓처럼 연결하는게 아니라 생성할 때 마다 보내~
		while(true) {
			try {
				byte[] buf = new byte[256];
				
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet); //송신측에서 보낸 패킷 받기
				
				//패킷 출력
				txtArea.append("RECIEVE: " + new String(buf) + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	class MyFrameB extends JFrame implements ActionListener {

		public MyFrameB() {
			super("DJ 채팅방");
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			txtField = new JTextField(30);
			txtField.addActionListener(this);

			txtArea = new JTextArea(10, 30);

			this.add(txtField, BorderLayout.PAGE_END);
			this.add(txtArea, BorderLayout.CENTER);

			this.pack();
			this.setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String s = txtField.getText();
			
			//데이터 그램 방식 = 바이트
			byte[] buf = s.getBytes();
			
			//데이터그램은 패킷 단위로 보내줘~
			DatagramPacket packet;
			
			//패킷 생성
			packet = new DatagramPacket(buf, buf.length, add, otherPort);
			
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			txtArea.append("SEND: " + s + "\n");
//			txtField.selectAll();//전체 선택
			txtField.setText("");
			
			//채팅창의 커서위치
			txtArea.setCaretPosition(txtArea.getDocument().getLength()); 
		}
	}
}

public class DatagramMessenger {
	public static void main(String[] args) throws IOException {
//		MessengerA ma = new MessengerA();
//		ma.process();
		
		MessengerB mb = new MessengerB();
		mb.process();
	}

}


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class ChatClient implements ActionListener, Runnable {

	private String ip;
	private String id;
	private Socket socket;
	private BufferedReader inMsg = null;
	private PrintWriter outMsg = null;

	// 로그인 패널
	private JPanel loginPanel;
	private JButton btnLogin;
	private JLabel label1;
	private JTextField idInput;

	private JPanel logoutPanel;
	private JButton btnLogout;
	private JLabel label2;

	// 입력 패널
	private JPanel msgPanel;
	private JTextField msgInput;
	private JButton btnExit;

	// 채팅창
	private JFrame jframe;
	private JTextArea msgOut;

	// 카드 레이아웃 - 로그인/로그아웃
	private Container tab;
	private CardLayout clayout;
	private Thread thread;

	// 상태 플래그
	boolean status;

	public ChatClient(String ip) {
		this.ip = ip;

		// 로그인 패널
		loginPanel = new JPanel();
		loginPanel.setLayout(new BorderLayout());

		idInput = new JTextField(15);
		btnLogin = new JButton("로그인");
		btnLogin.addActionListener(this);
		label1 = new JLabel("대화명");

		loginPanel.add(label1, BorderLayout.WEST);
		loginPanel.add(idInput, BorderLayout.CENTER);
		loginPanel.add(btnLogin, BorderLayout.EAST);

		// 로그아웃 패널
		logoutPanel = new JPanel();
		logoutPanel.setLayout(new BorderLayout());

		btnLogout = new JButton("로그아웃");
		btnLogout.addActionListener(this);
		label2 = new JLabel("대화명");

		logoutPanel.add(label2, BorderLayout.CENTER);
		logoutPanel.add(btnLogout, BorderLayout.EAST);

		// 메세지 패널
		msgPanel = new JPanel();
		msgPanel.setLayout(new BorderLayout());

		msgInput = new JTextField(30);
		msgInput.addActionListener(this);
		btnExit = new JButton("종료");
		btnExit.addActionListener(this);

		msgPanel.add(msgInput, BorderLayout.CENTER);
		msgPanel.add(btnExit, BorderLayout.EAST);

		// 카드 레이아웃 변환(로그인/로그아웃)
		tab = new JPanel();
		clayout = new CardLayout();

		tab.setLayout(clayout);
		tab.add(loginPanel, "login");
		tab.add(logoutPanel, "logout");

		// 메인 프레임 구성
		jframe = new JFrame("::멀티채팅::");
		msgOut = new JTextArea("", 10, 30);
		msgOut.setEditable(false); // 글씨쓰지 못하도록 막아놓음

		// verticalScrollBar는 항상 나타내고, horizontalScrollBar는 필요시만
		JScrollPane jsp = new JScrollPane(msgOut,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jframe.add(tab, BorderLayout.NORTH);
		jframe.add(jsp, BorderLayout.CENTER);
		jframe.add(msgPanel, BorderLayout.SOUTH);

		// 로그인패널을 가장 먼저 표시
		clayout.show(tab, "login");

		jframe.pack();
		jframe.setResizable(false);// 사이즈 조정 금지

		jframe.setVisible(true);
	}

	public void connServer() {
		try {
			socket = new Socket("127.0.0.1", 8000);

			outMsg = new PrintWriter(socket.getOutputStream(), true);
			inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outMsg.println(id + "/" + "login");

			thread = new Thread(this);
			thread.start();
			System.out.println("서버 연결 성공");
			
		} catch (Exception e) {
			System.out.println("서버 연결 예외 발생");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();

		if (obj == btnExit) {
			System.exit(0);
		} else if (obj == btnLogin) {
			id = idInput.getText();
			
			label1.setText("대화명: " + id);
			clayout.show(tab, "logout");
			connServer();
		} else if (obj == btnLogout) {
			outMsg.println(id + "/" + "logout");

			msgOut.setText("");

			clayout.show(tab, "login");
			outMsg.close();

			try {
				inMsg.close();
				outMsg.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			status = false;
		} else if (obj == msgInput) {
			outMsg.println(id + "/" + msgInput.getText());
			msgInput.setText("");
		}
	}

	@Override
	public void run() {
		// 메세지 송수신

		// 수신 메세지를 처리하는 변수
		String msg;
		String[] rmsg;

		status = true;

		while (status) {
			// 메세지 수신,파싱
			try {
				msg = inMsg.readLine();// 서버로부터 메세지가 옴
				rmsg = msg.split("/"); // 문자열에서 특정 문자 제거

				msgOut.append(rmsg[0] + " > " + rmsg[1] + "\n"); // 아이디

				// 대화 메시지 표시
				msgOut.setCaretPosition(msgOut.getDocument().getLength());

			} catch (IOException e) {
				e.printStackTrace();
				status = false;
			}
		}
		System.out.println(thread.getName() + "종료됨");
	}
}




class ChatServer{
	private ServerSocket ss = null;
	private Socket s = null;

	ArrayList<ChatThread> chatlist = new ArrayList<ChatThread>();// 자료형 지정

	public void start() {
		try {
			ss = new ServerSocket(8000);
			System.out.println("서버 시작");

			while (true) {
				s = ss.accept();// 클라이언트 연결

				ChatThread chat = new ChatThread();

				chatlist.add(chat);

				chat.start();
			}
		} catch (Exception e) {
			System.out.println("채팅 서버 시작 예외 발생 ");
		}
	}

	public void msgSendAll(String msg) {
		for (ChatThread ct : chatlist) {
			ct.outMsg.println(msg);
		}
	}

	class ChatThread extends Thread {
		// 수신 메시지 파싱처리

		String msg;
		String[] rmsg;

		PrintWriter outMsg = null;
		BufferedReader inMsg = null;

		@Override
		public void run() {
			boolean status = true;

			System.out.println("스레드 시작");

			try {
				outMsg = new PrintWriter(s.getOutputStream(), true);
				inMsg = new BufferedReader(
						new InputStreamReader(s.getInputStream()));

				// 상태 정보가 true면 사용자한테 수신된 메세지를 보냄
				while (status) {
					msg = inMsg.readLine();
					rmsg = msg.split("/");

					if (rmsg[1].equals("logout")) {
						chatlist.remove(this);

						// 메세지 보내기 처리
						msgSendAll("서버/" + rmsg[0] + "님이 퇴장");
						status = false;
					} else if (rmsg[1].equals("login")) {
						// 메세지 보내는 처리
						msgSendAll("서버/" + rmsg[0] + "님이 입장");
					} else {
						msgSendAll(msg);
					}
				}// 클라이언트 연결 종료 -> 스레드 인터럽트
				this.interrupt();
				System.out.println(this.getName() + " 멈춤 ");

			} catch (IOException e) {
				chatlist.remove(this);
				System.out.println("예외처리 발생");
			}
		}
	}
}
public class MultiChat {
	public static void main(String[] args) {
                        //1. 서버 먼저 실행 후, 코드 주석
                        //2. 그리고 클라이언트 여러개 실행

                        // ChatServer server = new ChatServer();
                        //  server.start();
	
                	new ChatClient("127.0.0.1");   
	}
}

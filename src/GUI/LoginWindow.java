package GUI;

import javax.swing.JFrame;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.awt.Color;
import java.awt.EventQueue;

import project.ClientRequest;
import project.Result;
import project.ServerResponse;
import project.Student;
import project.SubjectDetails;

import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.ItemEvent;
import javax.swing.JPasswordField;

/*
100 : close connection
101 : send PRN to get student details after login
102 : send Student registration details
103 : fetch Questions
104 : send Exam Result(PRN,result)
105 : Fetch Students Result Record
107 : Provide Feedback
108 : request to mail Responses
110 : logout
*/

class LoginErr extends Exception{  
	 LoginErr(String s){  
	  super(s);  }	
  }  
class RegisErr extends Exception{  
	 RegisErr(String s){  
	  super(s);  }  }

public class LoginWindow extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JTextField textField1;
	private JPasswordField PasswordField2;
	private JTextField textField3;
	private JTextField textField4;
	private JTextField textField6;
	private JPasswordField textField8;
	private JPasswordField textField9;
	private JTextField textField5;
	private JButton btnLogin, btnRegister;
	private JComboBox<String> comboBox;
	private JPanel loginPanel, registerPanel;
	private JLabel loginErrLabel, registerErrLabel;
	public volatile int operation = 0;
	
	static protected  Student studObj;
	static protected  SubjectDetails []subDetails;// = new SubjectDetails[5];
	static String courseList[] = {"Computer Engineering","Information Technology", "Electronics and Telecommunication"};
	static String coursename;
	static Result resObj;

	static Scanner in = new Scanner(System.in);
    static ObjectInputStream din;
    static ObjectOutputStream dout;

    static boolean loggedin = false;
    static boolean attempted = false;
    static boolean apprun = true;  //if 'apprun' is false the program exits

    final static String invalidInputMsg = "Enter valid input";
    final static String loginErr = "Login Error. Try Again";
    private JLabel examImage_1;
    
    void logout() {
    	PasswordField2.setText(" ");
    	ClientRequest CR0 = new ClientRequest(110);
        try {
			dout.writeObject(CR0);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private int getCourse(){
//	  Values not in database. Only 1003 is.
//    switch(comboBox.getSelectedIndex()) {
//    case 1:return 1003;
//    case 2:return 2003;
//    case 3:return 3003;
//    }
    return 1003;
    }
    
    public LoginWindow() {

    LoginWindow.studObj=new Student();
	LoginWindow.subDetails = new SubjectDetails[5];
	LoginWindow.resObj = new Result();
	
	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	
	this.addWindowListener(new java.awt.event.WindowAdapter() {
	    @Override
	    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
	        if (JOptionPane.showConfirmDialog(null, 
	            "Exit Application?", "Close Window",
	            JOptionPane.YES_NO_OPTION,
	            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
	            logout();
	            System.exit(0);
	        }
	    }
	});
	
	setBounds(100, 100, 800, 600);
	setTitle("Exam Portal");
	setResizable(false);			
	
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);
	
	JLabel examImage = new JLabel("imgExam");
	examImage.setBounds(24, 45, 335, 491);
	BufferedImage examPic;
	try {
		examPic = ImageIO.read(getClass().getResource("/resources/exam.jpg"));
		examImage_1 = new JLabel(new ImageIcon(examPic));
		examImage_1.setLocation(20, 25);
		examImage_1.setSize(318,510);
	} catch (IOException e1) {
		examImage.setText(e1.getMessage());
	}
	contentPane.add(examImage_1);
	
	JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	tabbedPane.setBounds(356, 20, 415, 520);
	contentPane.add(tabbedPane);
	
	loginPanel = new JPanel();
	tabbedPane.addTab("Login", null, loginPanel, null);
	loginPanel.setLayout(null);
	
	JLabel label1 = new JLabel("PRN");
	label1.setBounds(22, 80, 119, 38);
	loginPanel.add(label1);
	
	textField1 = new JTextField();
	label1.setLabelFor(textField1);
	textField1.setBounds(22, 110, 225, 30);
	loginPanel.add(textField1);
	textField1.setColumns(10);
	
	JLabel label2 = new JLabel("Password");
	label2.setBounds(26, 140, 119, 38);
	loginPanel.add(label2);
	
	PasswordField2 = new JPasswordField();
	label2.setLabelFor(PasswordField2);
	PasswordField2.setBounds(22, 170, 225, 30);
	loginPanel.add(PasswordField2);
	PasswordField2.setColumns(10);
	PasswordField2.setToolTipText("Password should be minimun 4 characters and should conatin atleast 1 character and atleast 1 number.");
	
	btnLogin = new JButton("Login");
	btnLogin.setBounds(22, 230, 117, 25);
	loginPanel.add(btnLogin);
	btnLogin.addActionListener(this);

	JLabel labelx = new JLabel("Not Registered? Register Here.");
	labelx.setBounds(22, 270, 268, 30);
	loginPanel.add(labelx);
	
	loginErrLabel = new JLabel("");
	loginErrLabel.setForeground(Color.RED);
	loginErrLabel.setBounds(22, 301, 364, 87);
	loginPanel.add(loginErrLabel);
	
	registerPanel = new JPanel();
	tabbedPane.addTab("Register", null, registerPanel, null);
	registerPanel.setLayout(null);
	
	JLabel label3 = new JLabel("PRN");
	label3.setBounds(23, 50, 94, 40);
	registerPanel.add(label3);
	
	textField3 = new JTextField();
	textField3.setColumns(10);
	textField3.setBounds(23, 80, 180, 30);
	registerPanel.add(textField3);
	
	JLabel label4 = new JLabel("Firstname");
	label4.setBounds(23, 110, 94, 40);
	registerPanel.add(label4);
	
	textField4 = new JTextField();
	textField4.setColumns(10);
	textField4.setBounds(23, 140, 180, 30);
	registerPanel.add(textField4);
	
	JLabel label5 = new JLabel("Lastname");
	label5.setBounds(244, 110, 94, 40);
	registerPanel.add(label5);
	
	textField5 = new JTextField();
	textField5.setColumns(10);
	textField5.setBounds(212, 140, 180, 30);
	registerPanel.add(textField5);
	
	JLabel label6 = new JLabel("Email");
	label6.setBounds(23, 170, 94, 40);
	registerPanel.add(label6);
	
	textField6 = new JTextField();
	textField6.setColumns(10);
	textField6.setBounds(23, 200, 300, 30);
	registerPanel.add(textField6);
	
	JLabel label7 = new JLabel("Course");
	label7.setBounds(23, 230, 94, 40);
	registerPanel.add(label7);
	int c=1003;
	comboBox = new JComboBox();
	comboBox.setModel(new DefaultComboBoxModel<String>(LoginWindow.courseList));
//	comboBox.addItemListener(new ItemListener() {
//		public void itemStateChanged(ItemEvent e) {
//			String crs = comboBox.getSelectedItem().toString();
//		}
//	});
	comboBox.setBounds(23, 260, 300, 30);
	registerPanel.add(comboBox);
	
	JLabel label8 = new JLabel("Password");
	label8.setBounds(23, 290, 94, 40);
	registerPanel.add(label8);
	
	textField8 = new JPasswordField();
	textField8.setColumns(10);
	textField8.setBounds(23, 320, 180, 30);
	registerPanel.add(textField8);
	
	JLabel label9 = new JLabel("Confirm Password");
	label9.setBounds(212, 290, 180, 40);
	registerPanel.add(label9);
	
	textField9 = new JPasswordField();
	textField9.setColumns(10);
	textField9.setBounds(212, 320, 180, 30);
	registerPanel.add(textField9);
	
	btnRegister = new JButton("Register");
	btnRegister.setBounds(22, 370, 117, 25);
	registerPanel.add(btnRegister);
	
	registerErrLabel = new JLabel("");
	registerErrLabel.setForeground(Color.RED);
	registerErrLabel.setBounds(22, 406, 376, 68);
	registerPanel.add(registerErrLabel);
	btnRegister.addActionListener(this);	
    }

	private boolean checkPRN(String PRN) {
		try {
			Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/PythonDB?useSSL=false", "root", "S@mik8161");
			PreparedStatement checkstmt = con.prepareStatement("select count(*) from student where prn = ? ");
			checkstmt.setString(1, PRN);
			ResultSet result = checkstmt.executeQuery();
			result.next();
			if(result.getInt(1)!=0) return true;    //PRN Found in database 
		}
		catch(Exception e){ 
			System.out.println(e);
		}
		return false;
	}
	    
	private boolean checkEmail(String email) { 
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ 
                            "[a-zA-Z0-9_+&*-]+)*@" + 
                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" + 
                            "A-Z]{2,7}$";                          
        Pattern pat = Pattern.compile(emailRegex); 
        if (email == null) 
            return false; 
        return pat.matcher(email).matches();
    } 
	    
//    boolean startExamSession(String PRN, ObjectInputStream din, ObjectOutputStream dout) {
//        try{
//            int subjectId;
//            System.out.print("Subject Code : ");
//            subjectId=in.nextInt();
//            dout.writeObject(subjectId);
//            StudentSession s1 = new StudentSession(PRN,subjectId); 
//            int result = s1.startSession(din);
//            dout.writeObject(PRN);
//            dout.writeObject(result);
//        }catch (Exception e){System.out.println(e);}
//        return true;
//    }//go to other window it says
	
	public void actionPerformed(ActionEvent event) {
		
		if(event.getSource()==btnLogin) {
			try {
				operation = 1;
				loginErrLabel.setText("");
				String prn,pwd;
				prn = textField1.getText();
				pwd = String.valueOf(PasswordField2.getPassword());
				if(prn.equals(""))
					throw new LoginErr("PRN cannot be empty");
				else if(pwd.equals(""))
					throw new LoginErr("\nPassword cannot be empty\n");
				else {
					studObj = new Student(prn,pwd);
                    ClientRequest CR = new ClientRequest(101);
            		dout.writeObject(CR);
            		dout.writeObject(studObj);
            		ServerResponse SR = (ServerResponse)din.readObject();
                    if(SR.getSRN()==400) {
                        Student studObj1 = new Student();
                        studObj1 = (Student)din.readObject();
                        LoginWindow.coursename = din.readUTF();
                        for(int subno=0;subno<5;subno++)
                        LoginWindow.subDetails[subno] = (SubjectDetails)din.readObject();
                        loggedin = true;
                        System.out.println("Logged in successfully!");
                        studObj=studObj1;
                        setVisible(false);
                        Dashboard dashboard = new Dashboard(this,din,dout);
                		dashboard.setVisible(true);
                        }
                    else if(SR.getSRN()==401||SR.getSRN()==403)
    				JOptionPane.showMessageDialog(this, SR.getMessage(),"Login Error",JOptionPane.WARNING_MESSAGE);
                    else 
                    	JOptionPane.showMessageDialog(this,loginErr,"Error",JOptionPane.WARNING_MESSAGE);
				}
			}
			catch(LoginErr e) {
				loginErrLabel.setText(e.getMessage());
			}
			catch(Exception e) {
				System.out.println( e.getMessage());
			}
		}
		else if (event.getSource()==btnRegister) {
			try {
				String pwd = String.valueOf(textField8.getPassword());
				String pwd2 = String.valueOf(textField9.getPassword());
				registerErrLabel.setText("");
				if(textField3.getText().equals(""))
					throw new RegisErr("PRN cannot be empty");
				if(textField4.getText().equals(""))
					throw new RegisErr("Firstname cannot be empty");
				if(textField6.getText().equals(""))
					throw new RegisErr("Email cannot be empty");
				if(pwd.equals(""))
					throw new RegisErr("Password cannot be empty");
				if(pwd.equals(pwd2)==false)
					throw new RegisErr("Passwords Do not Match");
				comboBox.getSelectedObjects();
				if(!checkPRN(textField3.getText()))
					throw new RegisErr("Invalid PRN");
				if(!checkEmail(textField6.getText()))
					throw new RegisErr("Invalid email. Email should be like x@y.com");
				Student studObj2 = new Student(textField3.getText(),textField4.getText(),textField5.getText(),textField6.getText(),
						pwd,getCourse());
                operation=2;
                ClientRequest CR2 = new ClientRequest(102);
        		dout.writeObject(CR2);
        		dout.writeObject(studObj2);
        		ServerResponse SR2 = (ServerResponse)din.readObject();
                if(SR2.getSRN()==402)
    				JOptionPane.showMessageDialog(this,SR2.getMessage(),"Error",JOptionPane.WARNING_MESSAGE);
                else
    				JOptionPane.showMessageDialog(this,"Registered Successfully!");        
			}
			catch(RegisErr e) {
				registerErrLabel.setText(e.getMessage());
			}
			catch(Exception e) {
				JOptionPane.showMessageDialog(null, e);
			}
		}
	}
	
	public static void main(String args[]){
		
        Socket socket;
        int connectFlag=0;
        try{
            do{
            try{
            socket = new Socket("localhost",9999);connectFlag=1;
            dout = new ObjectOutputStream(socket.getOutputStream());
            din = new ObjectInputStream(socket.getInputStream());
            }
            catch(Exception e){ Thread.sleep(1000);}
            }while(connectFlag!=1);

    		EventQueue.invokeLater(new Runnable() {
    			public void run() {
    				LoginWindow loginwin = new LoginWindow();
    				loginwin.setVisible(true);
    			}
    		});
            while(apprun){
//                loginwin.setVisible(true);
                if(loggedin==true){
                    }
            } //end of while loop
        }
        catch(Exception e){
        System.out.println(e); 
        }
    }
}
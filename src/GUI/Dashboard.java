package GUI;

import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import project.ClientRequest;
import project.Question;
import project.Result;
import project.ServerResponse;
import project.SubjectDetails;
import javax.swing.JTextArea;
/*
 *  switch(operation){
 */
//                        case 1: ClientRequest CR = new ClientRequest(103);
//                                dout.writeObject(CR);
//                                //boolean stat = startExamSession(studObj.getPRN(),din,dout);
//                                if(stat==true)
//                                    attempted=true;
//                        case 4: //send Feedback (chatbox)
//                    }*/
public class Dashboard extends JFrame {

	private JPanel contentPane;
	private JTextField textField_1;
	private JTextField textField;
	
	static ObjectInputStream din;
    static ObjectOutputStream dout;

	public Dashboard(LoginWindow loginwin, ObjectInputStream din, ObjectOutputStream dout) {

		Dashboard.din=din;
		Dashboard.dout=dout;
		
		this.setVisible(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(null, 
		        		"You will be logged out. Continue?", "Close Window", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		        	loginwin.logout();
//		            ClientRequest CR0 = new ClientRequest(110);
//	                try {
//						dout.writeObject(CR0);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
		            System.exit(0);
		        }
		    }
		});
		
		try {
			ClientRequest CR2 = new ClientRequest(105);
			//Fetch Students Result Record
			dout.writeObject(CR2);
			ServerResponse SR2 = (ServerResponse)din.readObject();
			if(SR2.getSRN()==400) {
				LoginWindow.resObj = (Result)din.readObject();
			}
			else
				System.out.println(SR2.getMessage());  
		}catch(Exception ex) {
			System.out.println(ex.getMessage());  
			}
		
		setBounds(100, 100, 800, 600);
		setTitle("Exam Portal");
		setResizable(false);	
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblPrn = new JLabel("PRN :"+LoginWindow.studObj.getPRN());
		lblPrn.setBounds(30, 10, 232, 49);
		contentPane.add(lblPrn);
		
		JButton btnLogout = new JButton("Log out");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ClientRequest CR0 = new ClientRequest(110);
                try {
					dout.writeObject(CR0);
				} catch (IOException e) {
					e.printStackTrace();
				}
                setVisible(false);
        		loginwin.setVisible(true);
			}
		});
		btnLogout.setBounds(650, 20, 117, 25);
		contentPane.add(btnLogout);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(22, 52, 757, 498);
		contentPane.add(tabbedPane);
		
		JPanel studPanel = new JPanel();
		tabbedPane.addTab("Student Details", null, studPanel, null);
		tabbedPane.setEnabledAt(0, true);
		studPanel.setLayout(null);
		
		JLabel lblFirstname = new JLabel("Name");
		lblFirstname.setBounds(45, 40, 153, 32);
		studPanel.add(lblFirstname);
		JLabel lblStudname = new JLabel(": "+LoginWindow.studObj.getName());
		lblStudname.setBounds(201, 40, 144, 32);
		studPanel.add(lblStudname);
		
		JLabel lblCourseId = new JLabel("Course Id");
		lblCourseId.setBounds(45, 80, 153, 32);
		studPanel.add(lblCourseId);
		JLabel lblStudcourseid = new JLabel(": "+LoginWindow.studObj.getCourseId());
		lblStudcourseid.setBounds(201, 80, 144, 32);
		studPanel.add(lblStudcourseid);
		
		
		JLabel lblCourse = new JLabel("Course Name");
		lblCourse.setBounds(45, 120, 153, 33);
		studPanel.add(lblCourse);
		JLabel lblStudcrs = new JLabel(": "+LoginWindow.coursename);
		lblStudcrs.setBounds(201, 120, 144, 32);
		studPanel.add(lblStudcrs);
		
		JLabel lblEmail = new JLabel("Email Id			:");
		lblEmail.setBounds(45, 160, 153, 32);
		studPanel.add(lblEmail);
		
		textField_1 = new JTextField(LoginWindow.studObj.getEmail());
		textField_1.setBounds(201, 165, 234, 25);
		studPanel.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(45, 200, 153, 32);
		studPanel.add(lblPassword);
		
		textField = new JTextField(LoginWindow.studObj.getPassword());
		textField.setColumns(10);
		textField.setBounds(201, 205, 234, 25);
		studPanel.add(textField);
		
		JPanel coursePanel = new JPanel();
		coursePanel.setLayout(null);
		tabbedPane.addTab("Course Details", null, coursePanel, null);	
		
		String courseTitle[] = {"Subject No.","Subject Name","Date of Exam","Maximum Marks"};
		
		String courseData[][]=new String[5][4];
		int i=0;
		for(SubjectDetails s:LoginWindow.subDetails) {
			courseData[i][0]=""+s.subjectId;//,s.subjectName,s.dateOfExam,10};
			courseData[i][1]=s.subjectName;
			courseData[i][2]=s.dateOfExam;
			courseData[i][3]="50";
			i++;
		}
		
		JTable subtable = new JTable(courseData,courseTitle);
		JScrollPane scrollPane = new JScrollPane(subtable);
		subtable.setFillsViewportHeight(true);
		subtable.setPreferredScrollableViewportSize(subtable.getPreferredSize());
		scrollPane.setBounds(52, 78, 645, 102);
		coursePanel.add(scrollPane);
		
		JPanel resultPanel = new JPanel();
//		resultPanel.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				
//			}
//		});
		resultPanel.setLayout(null);
		tabbedPane.addTab("Results", null, resultPanel, null);

		String resTitle[] = {"Subject No.","Subject Name","Marks Obtained","Maximum Marks"};
		
		String resData[][]=new String[5][4];
		i=0;
		for(SubjectDetails s:LoginWindow.subDetails) {
			resData[i][0]=""+s.subjectId;
			resData[i][1]=s.subjectName;
			resData[i][2]=""+LoginWindow.resObj.getRes(i);
			resData[i][3]="50";
			i++;
		}
		
		JTable restable = new JTable(resData,resTitle);
		JScrollPane scrollPane2 = new JScrollPane(restable);
//		restable.setFillsViewportHeight(true);
		restable.setPreferredScrollableViewportSize(restable.getPreferredSize());
		scrollPane2.setBounds(52, 78, 645, 102);
		resultPanel.add(scrollPane2);
		
		JPanel examPanel = new JPanel();
		tabbedPane.addTab("Exam", null, examPanel, null);
		examPanel.setLayout(null);
		
		JLabel label = new JLabel("Instructions");
		label.setBounds(322, 12, 119, 34);
		examPanel.add(label);
		
		String instructions ="\n  Maximun marks = 50\n  All questions are multiple choice questions.";
		JTextArea instructionText = new JTextArea(instructions); //instructions
		instructionText.setBounds(119, 43, 527, 306);
		examPanel.add(instructionText);
		
		JButton btnExam = new JButton("Start Exam");
		btnExam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actn) {
				startExamSession();
			}
		});
		
		btnExam.setBounds(322, 361, 117, 25);
		examPanel.add(btnExam);
		
		
		JPanel helpPanel = new JPanel();
		tabbedPane.addTab("Help", null, helpPanel, null);
		helpPanel.setLayout(null);
	}
	
	void startExamSession() {
		//get questions from database
		try {  
			ClientRequest CR = new ClientRequest(103);
			dout.writeObject(CR);
			int subjectId=304;
			dout.writeObject(subjectId);
			Question q= new Question();
			System.out.println("Fetched questions from DB");
		    ExamSession.board = new ArrayList<QuestionResponses>();
	        for(int i=0;i<ExamSession.NoOfQuestions;i++) {//ExamSession.NoOfQuestions
	            q= (Question)din.readObject();
	            QuestionResponses q2=new QuestionResponses(q);
	            ExamSession.board.add(q2);
	        }
    		setVisible(false);
    		ExamSession examwin = new ExamSession(this,din,dout);
    		examwin.setVisible(true);
    		System.out.println("Back to dashboard");
    		
		}catch(Exception e){
	    System.out.println(e);
		}
		
	}
}



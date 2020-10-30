package GUI;

import java.io.*;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

import project.ClientRequest;
import project.Question;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class testQuestionResponses extends Question{
	boolean attempted;
    String response;
    boolean marked;
    boolean feedback;

    public testQuestionResponses() {
        
    }

    public testQuestionResponses(int quesId, String quesDesc, String opt1, String opt2, String opt3, String opt4, String answer) {//, int dlevel
    super(quesId,quesDesc,opt1,opt2,opt3,opt4,answer);//,dlevel
    attempted=false;
    response=" ";
    marked=false;
    feedback=false;
    }

    public testQuestionResponses(Question q){
    super(q);
    attempted=false;
    response="";
    marked=false;
    feedback=false;
    }
    
    void addResponse(String opt) {
        boolean valid=true;
        
        switch(opt) {
        case "a": response = getOpt1(); break;
        case "b": response = getOpt2(); break;
        case "c": response = getOpt3(); break;
        case "d": response = getOpt4(); break;
        default : valid=false;
    }
    if(valid) {
        attempted=true;
        System.out.println("Response Added");
    }
    else System.out.println("Invalid Response");
    }

    public String getAnswer() {return answer;}
    
}//Class Question Response ends here

public class testExamSession extends JFrame {
	
	//ExamSession JFrame is divided into 5 Panels: Title,Score,Question,Options,Buttons of which 2,3,4 are private.
	private JPanel contentPane;
	private JPanel scorePanel;
	private JLabel maxLabel, attemptLabel;
	private JPanel quesPanel,optionPanel;
	private JTextArea questionArea;
	private JRadioButton radioOpt1,radioOpt2,radioOpt3,radioOpt4;

	static ObjectInputStream din;
    static ObjectOutputStream dout;
    
    int currentqno=1;
	int totMarks, marksObt;
    public static ArrayList<QuestionResponses> board;
    public static final int NoOfQuestions = 8;
    
    private int calculateScore(){
        marksObt = 0;
        for(QuestionResponses ques : board) {
            totMarks++;
            if(ques.response.equals(ques.getAnswer()))
                marksObt++;
        }
        return marksObt;
    }

	void submitScores() {
		try {
			ClientRequest CR = new ClientRequest(104);
			dout.writeObject(CR);
			dout.writeObject(LoginWindow.studObj.getPRN());
			dout.writeObject(marksObt);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	void closeExamSession() {
		setVisible(false);
	}
	
	void setBtnState(JButton defaultbtn, int actn) {
		switch(actn) {
		case 1: defaultbtn.setBackground(new Color(0, 255, 153)); //GREEN FOR ATTEMPTED
		break;
		case 2: defaultbtn.setBackground(new Color(204, 153, 255));	//PURPLE FOR REVIEW
		break;
		case 3: defaultbtn.setBackground(new Color(255, 153, 153)); //RED FOR NOT ATTEMPTED
		break;
		case 4: defaultbtn.setBackground(new Color(102, 153, 255));	//BLUE FOR CURRENT
		break;
		default: defaultbtn.setBackground(null);
		}
	}
	
	public testExamSession() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setBounds(100, 100, 800, 600);
		setTitle("Exam Portal");
		setResizable(false);	
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//---------title Panel---------------
		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(UIManager.getBorder("DesktopIcon.border"));
		titlePanel.setBounds(20, 5, 760, 42);
		contentPane.add(titlePanel);
		titlePanel.setLayout(null);
		
		JLabel lblPrn = new JLabel("PRN : ");
		lblPrn.setBounds(12, 0, 187, 42);
		titlePanel.add(lblPrn);
		
		JLabel lblSubject = new JLabel("Subject:");
		lblSubject.setBounds(543, 0, 198, 42);
		titlePanel.add(lblSubject);
		
		//---------score Panel---------------
		scorePanel = new JPanel();
		scorePanel.setBorder(UIManager.getBorder("DesktopIcon.border"));
		scorePanel.setBounds(15, 55, 212, 500);
		contentPane.add(scorePanel);
		scorePanel.setLayout(null);
		
		maxLabel = new JLabel("Maximum Marks :");
		maxLabel.setBounds(10, 10, 180, 20);
		scorePanel.add(maxLabel);
		
		attemptLabel = new JLabel("Total Attempted : ");
		attemptLabel.setBounds(10, 30, 180, 20);
		scorePanel.add(attemptLabel);
		
		//--------traverse Panel is nested within score Panel-------------
		JPanel traversePanel = new JPanel();
		traversePanel.setBorder(new LineBorder(UIManager.getColor("TabbedPane.unselectedBackground")));
		traversePanel.setBounds(10, 62, 198, 140);
		scorePanel.add(traversePanel);
		traversePanel.setLayout(new GridLayout(3, 5, 0, 0));	
		JPanel line1 = new JPanel();
		line1.setBorder(new LineBorder(UIManager.getColor("TabbedPane.unselectedBackground")));
		traversePanel.add(line1);
		line1.setLayout(new GridLayout(1, 0, 5, 0));

		JButton Qbtn[] = new JButton[NoOfQuestions+1];

		Qbtn[0] = new JButton(String.valueOf(0));
		for(int i=1;i<5;i++) {
			Qbtn[i] = new JButton(String.valueOf(i));
			line1.add(Qbtn[i]);
			}

		setBtnState(Qbtn[currentqno],4);
		JPanel line2 = new JPanel();
		traversePanel.add(line2);
		line2.setBorder(new LineBorder(UIManager.getColor("TabbedPane.unselectedBackground")));
		line2.setLayout(new GridLayout(1, 0, 5, 0));
		
		for(int i=5;i<9;i++) {
		Qbtn[i] = new JButton(String.valueOf(i));
		line2.add(Qbtn[i]);
		}
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//calculateScore()
				submitScores();
				closeExamSession();
			}
		});
		
		btnSubmit.setBounds(37, 445, 117, 25);
		scorePanel.add(btnSubmit);
		
		//---------question Panel---------------
		quesPanel = new JPanel();
		quesPanel.setBorder(UIManager.getBorder("DesktopIcon.border"));
		quesPanel.setBounds(236, 55, 544, 230);
		contentPane.add(quesPanel);
		quesPanel.setLayout(null);
		
		//---------question description----------
		questionArea = new JTextArea();
		questionArea.setEditable(false);
		questionArea.setText(board.get(currentqno-1).displayQuestion());
		questionArea.setFont(new Font("Dialog", Font.BOLD, 12));
		questionArea.setBackground(UIManager.getColor("TabbedPane.unselectedBackground"));
		questionArea.setBounds(12, 31, 520, 185);
		quesPanel.add(questionArea);
		
		//---------options Panel---------------
		optionPanel = new JPanel();
		optionPanel.setBounds(236, 273, 544, 240);
		contentPane.add(optionPanel);
		optionPanel.setBorder(UIManager.getBorder("DesktopIcon.border"));
		optionPanel.setLayout(new GridLayout(4, 0, 0, 0));
		
		radioOpt1 = new JRadioButton("a) ");
		optionPanel.add(radioOpt1);
		
		radioOpt2 = new JRadioButton("b) ");
		optionPanel.add(radioOpt2);
		
		radioOpt3 = new JRadioButton("c) ");
		optionPanel.add(radioOpt3);
		
		radioOpt4 = new JRadioButton("d) ");
		optionPanel.add(radioOpt4);
		
		JPanel btnPanel = new JPanel();
		btnPanel.setBorder(UIManager.getBorder("DesktopIcon.border"));
		btnPanel.setBounds(236, 512, 544, 45);
		contentPane.add(btnPanel);
		btnPanel.setLayout(null);
		

		JButton btnReview = new JButton("Review");
		btnReview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setBtnState(Qbtn[currentqno],2);
			}
		});
		btnReview.setBounds(235, 12, 117, 25);
		btnPanel.add(btnReview);
		
		JButton btnPrev = new JButton("Previous");
		btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setBtnState(Qbtn[currentqno],0);
				if(currentqno!=1) currentqno--;
				setBtnState(Qbtn[currentqno],4);
			}
		});
		btnPrev.setHorizontalAlignment(SwingConstants.TRAILING);
		btnPrev.setBounds(360, 12, 95, 25);
		btnPanel.add(btnPrev);
		
		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setBtnState(Qbtn[currentqno],0);
				if(currentqno!=NoOfQuestions) currentqno++;
				setBtnState(Qbtn[currentqno],4);
			}
		});
		btnNext.setHorizontalAlignment(SwingConstants.RIGHT);
		btnNext.setBounds(464, 12, 66, 25);
		btnPanel.add(btnNext);

		System.out.println("Boom! Yes, Frame created");
		setVisible(true);
	}	
	
	public static void main(String args[]) {
		testExamSession examwin = new testExamSession();
	}
}

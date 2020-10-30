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
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

import project.ClientRequest;
import project.Question;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//Remember for board index starts with 0
//		   for Questions in the traverse panel start with 1
// 		   the first qBtn is 0 and not included in our panel
class QuestionResponses extends Question{
    int attempted;
    String response;
    boolean marked;
    boolean feedback;

    public QuestionResponses() {
   
    }

    public QuestionResponses(int quesId, String quesDesc, String opt1, String opt2, String opt3, String opt4, String answer) {//, int dlevel
    super(quesId,quesDesc,opt1,opt2,opt3,opt4,answer);//dlevel
    attempted=0;
    response=" ";
    marked=false;
    feedback=false;
    }

    public QuestionResponses(Question q){
    super(q);
    attempted=0;
    response="";
    marked=false;
    feedback=false;
    }
    
    void addResponse(int opt) {
        switch(opt) {
        case 1: response = getOpt1(); attempted = 1; break;
        case 2: response = getOpt2(); attempted = 2;  break;
        case 3: response = getOpt3(); attempted = 3;  break;
        case 4: response = getOpt4(); attempted = 4;  break;
        default : attempted = 0;
    }
    }

    public String getAnswer() {return answer;}
    
}//Class Question Response ends here

public class ExamSession extends JFrame {
	
	//ExamSession JFrame is divided into 5 Panels: Title,Score,Question,Options,Buttons of which 2,3,4 are private.
	private JPanel contentPane;
	private JPanel scorePanel;
	private JLabel maxLabel, attemptLabel;
	private JButton Qbtn[];
	private JPanel quesPanel,optionPanel;
	private JTextArea questionArea;
	private ButtonGroup optionsGroup;
	private JRadioButton radioOpt[];//,radioOpt1,radioOpt2,radioOpt3,radioOpt4,;
    private JButton btnReview;
	
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
		default:if(board.get(currentqno-1).marked==true)
					defaultbtn.setBackground(new Color(204, 153, 255));
				else if(board.get(currentqno-1).attempted!=0)
					defaultbtn.setBackground(new Color(0, 255, 153));
				else
					 defaultbtn.setBackground(null);
		}
	}
	
	void displayCurrentQuestion() {
		int quesIndex=currentqno-1;
		questionArea.setText(board.get(quesIndex).displayQuestion());
		optionsGroup.clearSelection();
		radioOpt[0].setText(board.get(quesIndex).getOpt1());
		radioOpt[1].setText(board.get(quesIndex).getOpt2());
		radioOpt[2].setText(board.get(quesIndex).getOpt3());
		radioOpt[3].setText(board.get(quesIndex).getOpt4());
		if(board.get(quesIndex).attempted!=0)
			radioOpt[board.get(quesIndex).attempted-1].setSelected(true);
		if(board.get(quesIndex).marked==true) {
			setBtnState(Qbtn[currentqno], 2);
			btnReview.setText("Unmark");
		}
		else {
			setBtnState(Qbtn[currentqno], 0);
			btnReview.setText("Mark");
		}
		setBtnState(Qbtn[currentqno], 4);
	}
	
	void showUnattempted() {
		for(int i=1;i<=NoOfQuestions;i++) {
			if(board.get(i).attempted==0)
				setBtnState(Qbtn[i+1],3);
		}
	}
	
	public ExamSession(Dashboard dashboard, ObjectInputStream din, ObjectOutputStream dout) {
		
		currentqno =1;
		ExamSession.din=din;
		ExamSession.dout=dout;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(null, 
		        		"Submit Exam?", "Exit", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		        	calculateScore();
		        	submitScores();
		        	setVisible(false);
	        		dashboard.setVisible(true);
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
		
		//---------title Panel---------------
		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(UIManager.getBorder("DesktopIcon.border"));
		titlePanel.setBounds(20, 5, 760, 42);
		contentPane.add(titlePanel);
		titlePanel.setLayout(null);
		
		JLabel lblPrn = new JLabel("PRN : "+LoginWindow.studObj.getPRN());
		lblPrn.setBounds(12, 0, 187, 42);
		titlePanel.add(lblPrn);
		
		JLabel lblSubject = new JLabel("Subject:"+LoginWindow.subDetails[1].subjectName);
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
		
		Qbtn = new JButton[NoOfQuestions+1];
		for(int i=1;i<5;i++) {
			Qbtn[i] = new JButton(String.valueOf(i));
			line1.add(Qbtn[i]);
			Qbtn[i].setActionCommand(String.valueOf(i));
			}
		setBtnState(Qbtn[currentqno],4);
		
		JPanel line2 = new JPanel();
		traversePanel.add(line2);
		line2.setBorder(new LineBorder(UIManager.getColor("TabbedPane.unselectedBackground")));
		line2.setLayout(new GridLayout(1, 0, 5, 0));
		
		for(int i=5;i<9;i++) {
		Qbtn[i] = new JButton(String.valueOf(i));
		line2.add(Qbtn[i]);
		Qbtn[i].setActionCommand(String.valueOf(i));
		}
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				calculateScore();
				submitScores();
				closeExamSession();
				dashboard.setVisible(true);
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
		
		radioOpt = new JRadioButton[4];
		optionsGroup = new ButtonGroup();
		for(int i=0;i<4;i++) {
			String name = "option";
			radioOpt[i]=new JRadioButton(name);
			optionPanel.add(radioOpt[i]);
			optionsGroup.add(radioOpt[i]);
		}
		
		radioOpt[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ExamSession.board.get(currentqno-1).addResponse(1);
				setBtnState(Qbtn[currentqno], 1);
				}
			});
		radioOpt[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ExamSession.board.get(currentqno-1).addResponse(2);
				setBtnState(Qbtn[currentqno], 1);
				}
			});
		radioOpt[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ExamSession.board.get(currentqno-1).addResponse(3);
				setBtnState(Qbtn[currentqno], 1);
				}
			});
		radioOpt[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ExamSession.board.get(currentqno-1).addResponse(4);
				setBtnState(Qbtn[currentqno], 1);
				}
			});
//		radioOpt1 = new JRadioButton("option1");
//		optionPanel.add(radioOpt1);
//		
//		radioOpt2 = new JRadioButton("option2");
//		optionPanel.add(radioOpt2);
//		
//		radioOpt3 = new JRadioButton("option3");
//		optionPanel.add(radioOpt3);
//		
//		radioOpt4 = new JRadioButton("option4");
//		optionPanel.add(radioOpt4);
		JPanel btnPanel = new JPanel();
		btnPanel.setBorder(UIManager.getBorder("DesktopIcon.border"));
		btnPanel.setBounds(236, 512, 544, 45);
		contentPane.add(btnPanel);
		btnPanel.setLayout(null);

		//------------button Panel----------------
		btnReview = new JButton("Mark");
		btnReview.setBounds(235, 12, 117, 25);
		btnPanel.add(btnReview);
		btnReview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(board.get(currentqno-1).marked==false) {
					setBtnState(Qbtn[currentqno],2);
					board.get(currentqno-1).marked=true;
					btnReview.setText("Unmark");
				}
				else if(board.get(currentqno-1).attempted!=0) {
					setBtnState(Qbtn[currentqno],1);
					board.get(currentqno-1).marked=false;
					btnReview.setText("Mark");
				}
			}
		});
		
		JButton btnPrev = new JButton("Previous");
		btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setBtnState(Qbtn[currentqno],0);
				if(currentqno>1) currentqno--;
				displayCurrentQuestion();
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
				displayCurrentQuestion();
				setBtnState(Qbtn[currentqno],4);
			}
		});
		btnNext.setHorizontalAlignment(SwingConstants.RIGHT);
		btnNext.setBounds(464, 12, 66, 25);
		btnPanel.add(btnNext);

		displayCurrentQuestion();
		System.out.println("Flex! coz the Frame's created");
		
		//------------------working code-----------------------
		class goToQuestionListener implements ActionListener {
		      public void actionPerformed(ActionEvent actionEvent) {
			        setBtnState(Qbtn[currentqno],0);
			        String newqno = actionEvent.getActionCommand();
			        currentqno = Integer.parseInt(newqno);
			        displayCurrentQuestion();
					setBtnState(Qbtn[currentqno],4);
			      }
			    }
		ActionListener gotoQ = new goToQuestionListener();
		for(int i=1;i<=NoOfQuestions;i++) {
			Qbtn[i].addActionListener(gotoQ);
		}
		//------------------working code ends here--------------
	}
}

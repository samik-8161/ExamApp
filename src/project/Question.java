package project;

import java.io.Serializable;

public class Question implements Serializable{
    private int quesId;
    private String quesDesc, opt1, opt2, opt3, opt4;
	protected String answer;
    private int dlevel;
    //private int corrAttempts, totAttempts; in db only
    
    public Question(){

    }

    public Question(int quesId, String quesDesc, String opt1, String opt2, String opt3, String opt4, String answer){//, int dlevel
        this.quesId=quesId;
        this.quesDesc=quesDesc;
        this.opt1=opt1;
        this.opt2=opt2;
        this.opt3=opt3;
        this.opt4=opt4;
        this.answer=answer;
        this.dlevel=1; //dlevel;
        //this.corrAttempts=this.totAttempts=0;
    }
    public Question(Question q){
        this.quesId=q.quesId;
        this.quesDesc=q.quesDesc;
        this.opt1=q.opt1;
        this.opt2=q.opt2;
        this.opt3=q.opt3;
        this.opt4=q.opt4;
        this.answer=q.answer;
        this.dlevel=1; //q.dlevel;
        //this.corrAttempts=this.totAttempts=0;
    }

    int getQuesId() {return quesId;}

    public String getOpt1() {return opt1;}

    public String getOpt2() {return opt2;}

    public String getOpt3() {return opt3;}

    public String getOpt4() {return opt4;}

    public String getAnswer() {return answer;}

    public String displayQuestion() {
    return quesDesc;
    }
}

/*each entity of question class represents a single question with properties as described above.
the properties corrAttempts and totAttempts is to check how many students could attempt the particular question.
*/
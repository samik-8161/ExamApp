package project;

import java.io.Serializable;

public class SubjectDetails implements Serializable{
    public int subjectId;
    public String subjectName;
    public String dateOfExam;

    public SubjectDetails(){
    }
    
    public SubjectDetails(int subjectId, String subjectName, String dateOfExam) {
        this.subjectId=subjectId;
        this.subjectName=subjectName;
        this.dateOfExam=dateOfExam;
    }
}

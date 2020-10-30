package project;

import java.io.Serializable;

public class ClientRequest implements Serializable{
    int requestNum=100;
    public ClientRequest(){}
    public ClientRequest(int rno) {requestNum=rno;}
    // public void setCRN(int crn) {requestNum = crn;}
    public int  getCRN() {return requestNum;}
    /*
    100 : Close Connection
    101 : send PRN to get student,course details after login
    102 : send Student registration details
    103 : fetch Questions,send Exam Result (PRN,subjectId,result)
    104 : Fetch Students Result Record
    107 : Provide Feedback
    108 : Mail Responses and Result
    110 : Logout
    */
}
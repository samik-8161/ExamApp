package project;

import java.io.Serializable;

public class ServerResponse implements Serializable{
	private int responseNum=0;
    private String message;
    public ServerResponse(){}
    public ServerResponse(int rno) {
        responseNum=rno;
        setMessage();
    }
    public void setSRN(int rno) {
        responseNum = rno;
        setMessage();
    }
    public int getSRN() {return responseNum;}
    public String getMessage() {return message;}
    
    public void setMessage() {
        switch(responseNum) {
            case 400: message="Success"; break;
            case 401: message="Invalid Login Credentials"; break;
            case 402: message="PRN Already Registered"; break;
            case 403: message="PRN Already Logged-in"; break;
            case 404: message="Record Not found"; break;
            case 405: message=" "; break;
            case 406: message=" "; break;
            case 407: message=" "; break;
            default : message="null";
        }
    }
}
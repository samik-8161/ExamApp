package project;

import java.io.*;  
import java.net.*; 
import java.util.*;
import java.sql.*;
import java.text.DateFormat; 
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;

import project.*;

/*
100 : accept close Connection request
101 : accept PRN, send student,course details
102 : accept Student registration details
103 : send Questions
104 : fetch Exam Result (PRN,result)
105 : send Students Result Record
107 : fetch Feedback
108 : Mail Responses and Result
110 : acknowledge logout request
*/

class ExamClientHandler extends Thread {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  

    final String cname;
    final Socket socket;
    final ObjectInputStream din;
    final ObjectOutputStream dout;
    final Connection con;

    public ExamClientHandler(Socket socket, ObjectInputStream din, ObjectOutputStream dout, Connection con,int cno) {
        this.cname="client" + String.valueOf(cno);
        this.socket = socket;
        this.din = din;
        this.dout = dout;
        this.con = con;
    }

    public void run() {
        System.out.println(cname+": connected at "+ dtf.format(LocalDateTime.now()));
        
        Student studObj = new Student();
        ClientRequest CR = new ClientRequest(100);
        Integer examSubject = 0;
        boolean clientLoggedin =  false;
        while(socket.isConnected()){
            try{
               CR = (ClientRequest)din.readObject();
                if(CR.requestNum==100) {
                	if (clientLoggedin && ServerProgram.loginTable.containsKey(studObj.PRN)) {
                		ServerProgram.loginTable.remove(studObj.PRN); //remove key from hashtable
                        System.out.println(cname+": logged out at "+dtf.format(LocalDateTime.now()));
                        }
                    System.out.println(cname +": closed connection ");
                    socket.close();
                    return;
                } else
                switch(CR.requestNum) {
                    case 101:   //accept PRN, send Stud Details (if Present)
                                System.out.println(cname+": (CRN:"+CR.getCRN()+")"+" requested for login");
                                studObj = (Student)din.readObject();
                                String query = "select * from Student where prn = ? and password = ?";
                                PreparedStatement sql = con.prepareStatement(query);
                                sql.setString(1, studObj.PRN);
                                sql.setString(2, studObj.password);
                                ResultSet rs = sql.executeQuery();
                                ServerResponse SR;
                                if(rs.next()==false) {
                                    SR = new ServerResponse(401);
                                    dout.writeObject(SR);
                                    System.out.println(cname+": login denied");
                                }
                                else if (ServerProgram.loginTable.containsKey(studObj.PRN)){
                                    SR = new ServerResponse(403);
                                    dout.writeObject(SR);
                                    System.out.println(cname+": login denied");
                                }
                                else {
                                    SR = new ServerResponse(400);
                                    dout.writeObject(SR);
                                    Student newStudObj = new Student(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getInt(6));
                                    dout.writeObject(newStudObj);
                                    studObj = newStudObj;
                                    CallableStatement csql = con.prepareCall("{call getSubjects(?)}");
                                    csql.setString(1, studObj.PRN);
                                    boolean hasresult = csql.execute();
                                    ResultSet crs = csql.getResultSet();
                                    crs.next();
                                    dout.writeUTF(crs.getString(1));
                                    csql.getMoreResults();
                                    ResultSet crs1 = csql.getResultSet();
                                    DateFormat df = new SimpleDateFormat("dd/mm/yy"); 
                                    while(crs1.next()){
                                        SubjectDetails subDetails = new SubjectDetails(crs1.getInt(1),crs1.getString(2),df.format(crs1.getDate(3)));
                                        dout.writeObject(subDetails);
                                    }
                                    ServerProgram.loginTable.put(studObj.PRN,LocalDateTime.now());//add student detail to loginTable if successfully logged in 
                                    System.out.println(cname+": logged in at "+ dtf.format(LocalDateTime.now()));
                                    clientLoggedin = true;
                                }     
                    break;

                    case 102://accept Student registration details
                                System.out.println(cname+": (CRN:"+CR.getCRN()+")"+" resuested for Register");
                                Student studObj2 = (Student)din.readObject();
                                String query2 = "select * from Student where prn = ?";
                                PreparedStatement sql2 = con.prepareStatement(query2);
                                sql2.setString(1, studObj2.PRN);
                                ResultSet rs2 = sql2.executeQuery();
                                ServerResponse SR2;
                                if(rs2.next()==false) { //record not found means not already registered
                                    query2 = "insert into Student values (?,?,?,?,?,?)";
                                    sql2 = con.prepareStatement(query2);
                                    sql2.setString(1, studObj2.PRN);
                                    sql2.setString(2, studObj2.firstname);
                                    sql2.setString(3, studObj2.lastname);
                                    sql2.setString(4, studObj2.email);
                                    sql2.setString(5, studObj2.password);
                                    sql2.setInt(6, studObj2.courseId);
                                    sql2.executeUpdate();
                                    System.out.println(cname+": registered at "+dtf.format(LocalDateTime.now()));
                                    SR2 = new ServerResponse(400);
                                }
                                else {
                                    System.out.println(cname+": registeration cancelled");
                                    SR2 = new ServerResponse(402);
                                }
                                dout.writeObject(SR2);
                    break;

                    case 103:// send Questions
                                System.out.println(cname+": (CRN:"+CR.getCRN()+")"+" started Exam at "+dtf.format(LocalDateTime.now()));
                                examSubject = (Integer)din.readObject();
                                String query3 = "select * from Question limit 8";
                                Statement sql3 = con.createStatement();
                                ResultSet rs3 = sql3.executeQuery(query3);
                                while(rs3.next()) {
                                    Question q=new Question(rs3.getInt(1),rs3.getString(2),rs3.getString(3),rs3.getString(4),rs3.getString(5),rs3.getString(6),rs3.getString(7));
                                    dout.writeObject(q);
                                    }
//                                
                    break;
                    case 104:	String prn=(String)din.readObject();
                    			Integer result = (Integer)din.readObject();
                    			System.out.println(prn +" scored " +result);
                    			System.out.println(cname+": submitted Exam at "+dtf.format(LocalDateTime.now()));
                    			CallableStatement csql3 = con.prepareCall("{call setResult(?,?,?)}");
                    			csql3.setString(1,prn);
                    			csql3.setInt(2, examSubject);
                    			csql3.setInt(3, result);
                    			csql3.execute();
                    
                    break;
                    case 105://send Students Result Record
                                System.out.println(cname+": (CRN:"+CR.getCRN()+")"+" requested for Results ");
                                String query4 = "select * from Results where prn = ?";
                                PreparedStatement sql4 = con.prepareStatement(query4);
                                sql4.setString(1, studObj.PRN);
                                ResultSet rs4 = sql4.executeQuery();
                                ServerResponse SR4;
                                if(rs4.next()==false) {
                                    SR4 = new ServerResponse(404);
                                    dout.writeObject(SR4);
                                }
                                else {
                                    SR4 = new ServerResponse(400);
                                    Result resObj = new Result(rs4.getString(1),rs4.getInt(2),rs4.getInt(3),rs4.getInt(4),rs4.getInt(5),rs4.getInt(6));
                                    dout.writeObject(SR4);
                                    dout.writeObject(resObj);
                                }
                    break;

                    case 107://fetch Feedback (chatbox)
                    break;

                    case 108:// Mail Responses
                    break;
                    	
                    case 110://log out
                            ServerProgram.loginTable.remove(studObj.PRN); //remove key from hashtable
                            System.out.println(cname+": logged out at "+dtf.format(LocalDateTime.now()));        
                    break;
                    
                    default: System.out.println( );
                }
            }
            catch(Exception e) {
            	try {
                    System.out.println("Exception Occured :"+e.getMessage());
                    din.close(); 
                    dout.close();
                	socket.close();
                    System.out.println(cname +": closed connection ");
                    return;
                } 
                catch(IOException err){ 
                    System.out.println(err.getMessage()); 
                } 
            }
        }
        try {// closing resources 
        	socket.close();
            din.close(); 
            dout.close();
        } 
//        catch(IOException e){ 
//        	if(ServerProgram.loginTable.containsKey(studObj.PRN)) {
//    		ServerProgram.loginTable.remove(studObj.PRN); //remove key from hashtable
//            }
//            System.out.println(e.getMessage()); 
//        } 
        catch(Exception ex) {
        	System.out.println(ex);
        }
    }
};

class CloseServerInterrupt extends Thread{
	final String closeCommand = "CLOSE_SERVER";
	public void run() {
	    Scanner in = new Scanner(System.in);
	    String cmd = in.next();
	    if(cmd.equalsIgnoreCase(closeCommand))
	    	{
	        System.out.println("Closing Server.");
	    	System.exit(0);
	    	}
	}
};

public class ServerProgram {
    static int cno=1;
    static Hashtable<String,LocalDateTime> loginTable; //<PRN,TimeOfLgin>

    public static void main(String args[]) throws IOException{
        loginTable = new Hashtable<String,LocalDateTime>();
        ServerSocket server = new ServerSocket(9999); //for now port number = 9999
        System.out.println("Server started");
        System.out.println("Waiting for the client...");
        Thread interruptthread = new CloseServerInterrupt();
        interruptthread.start();
        while(true){
//            Socket socket = null;
//            try{ 
                Socket socket = server.accept();
                
				try {
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaDB?useSSL=false", "root", "S@mik8161");
				      
                ObjectInputStream din = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream dout = new ObjectOutputStream(socket.getOutputStream());
                Thread thread = new ExamClientHandler(socket,din,dout,con,cno++);
                thread.start();
                } catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
        }   //end of outer while
        
    }
}

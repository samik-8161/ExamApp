package project;

import java.io.Serializable;
import java.util.regex.Matcher; 
import java.util.regex.Pattern;

public class Student implements Serializable{
    String PRN;
    String firstname, lastname, email, password;
    int courseId;

    static final String address = "localhost";
    static final int port = 9999;

    public Student(){
    }

    public Student(String PRN, String firstname, String lastname, String email, String password,int courseId) {
        this.PRN=PRN;
        this.firstname=firstname;
        this.lastname=lastname;
        this.email=email;
        this.password=password;
        this.courseId = courseId;
    }

    public Student(String PRN,String password) {
        this.PRN=PRN;
        this.firstname=" ";
        this.lastname=" ";
        this.email=" ";
        this.password=password;
        this.courseId =0;
    }
    // public Student(Student s) {
    //     this.PRN=s.PRN;
    //     this.firstname=s.firstname;
    //     this.lastname=s.lastname;
    //     this.email=s.email;
    //     this.password=s.password;
    //     this.courseId = s.courseId; 
    // }
    public String getPRN() {
        return this.PRN;
    }
    public String getName() {
        return this.firstname +" " + this.lastname;
    }
    
    public int getCourseId() {
		return courseId;
	}
    
    public String getPassword() {
		return password;
	}
    
    public String getEmail() {
		return email;
	}
    
    //gotta add setters for changing info if student wants

    public void displayStudentDetails() {
    System.out.println("PRN : "+PRN);
    System.out.println("Student Name : "+firstname+" "+lastname);
    System.out.println("Email : "+email);
    System.out.println("Course : "+courseId);
    } 

    public void showCourseDetails(SubjectDetails [] sub){
        for(int subno=0;subno<5;subno++)
        System.out.println(sub[subno].subjectId+"\t"+sub[subno].subjectName+"\t"+sub[subno].dateOfExam);
    }
}
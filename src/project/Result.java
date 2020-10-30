package project;
import java.io.Serializable;

public class Result implements Serializable{
    private String PRN;
    private int sub1;
    private int sub2;
    private int sub3;
    private int sub4;
    private int sub5;

    public Result() {
    }
    
    public Result(String PRN, int sub1, int sub2, int sub3, int sub4, int sub5) {
        this.PRN = PRN;
        this.sub1 = sub1;
        this.sub2 = sub2;
        this.sub3 = sub3;
        this.sub4 = sub4;
        this.sub5 = sub5;
    }
    
    public int getRes(int i) {
    	switch(i) {
    	case 1:return sub1;case 2:return sub2;case 3:return sub3;case 4:return sub4;case 5:return sub5;default:return 0;
    	}
    }

    public void displayResults(){
        System.out.println(PRN+ "("+sub1+","+sub2+","+sub3+","+sub4+","+sub5+")");
    }
    
}   
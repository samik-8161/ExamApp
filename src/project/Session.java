package project;
import java.io.*;  
import java.net.*; 
import java.util.*;
import java.sql.*; 

import project.Question;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    

public class Session {//implements Serializable
    static int sNo=1;
    private String sessionId;
    private int subjectId;
    private Set<Integer> questionSet;
    public Queue<Question> questionList;
    static Set<Integer> theBigSet;
    static final int NoOfQuestions = 5;

    //for Socket Programming
    private static Socket socket=null;
    private static ServerSocket server = null;
    ObjectOutputStream output = null;
    ObjectInputStream input = null;

    public Session(int subjectId, int SessionNumber){
    questionSet = new HashSet<>();
    questionList = new LinkedList<>();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss-");
    LocalDateTime now = LocalDateTime.now();
    sessionId = subjectId+dtf.format(now).concat(Integer.toString(SessionNumber));
    createSession(NoOfQuestions);
    }

    public void createSession(int totalMarks) {
    int qno=0;
    int marks=0;     
    while(marks<totalMarks) {
        qno=randomQuestion();
        if(!theBigSet.contains(qno)){
            questionSet.add(qno);
            marks++;
            }
        }
    System.out.println("\nSession created Successfully");
    System.out.println("SessionID : "+sessionId);
    System.out.print("Questions in session : ");
    for(int q:questionSet)
        System.out.print(q + " ");
    System.out.println("");
    }

    int randomQuestion(){
        Random rand = new Random();
        int quesNo;
        quesNo=100+rand.nextInt(100);
        return quesNo;
    }

    public void addToTheBigSet(){
        for(int q:questionSet)
            theBigSet.add(q);
    }

    public static void displayTheBigSet(){
    System.out.print("\nBig Set Already has : \n");
    for(int q:theBigSet)
            System.out.print(q + " ");
    }

    private static void loadPreviousSetQuestions(){
        theBigSet.clear();
        File file=new File("thebigset.txt");
        try(Scanner scanner = new Scanner(file))    {
        while(scanner.hasNextInt())
            theBigSet.add(scanner.nextInt());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveTheBigSet(){
        System.out.println("\nSaving...");
        try{
            FileWriter writer = new FileWriter("thebigset.txt");
            BufferedWriter out = new BufferedWriter(writer);
            for(int q:theBigSet)
                    out.write(q+" ");
            out.newLine();
            out.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println("Successfully Saved the file.");
    }

    

    // private void sendQuestionsToClient() {
    //     try{
    //         server = new ServerSocket(9999); //for now port number = 9999
    //         System.out.println("Server started");
    //         System.out.println("Waiting for the client...");
    //         socket = server.accept();
    //         System.out.println("Client connected");
    //         output = new ObjectOutputStream(socket.getOutputStream());
    //         for(Question q :questionList) {
    //             output.writeObject(q);
    //         }
    //     }
    //     catch(IOException i) {
    //         System.out.println(i);
    //     }
    // }

    public static void main(String args[]){
        
        Session.theBigSet = new HashSet<>();
        // loginTable = new Hashtable<>();
        loadPreviousSetQuestions();

        Scanner obj = new Scanner(System.in);
        Vector<Session> SV = new Vector<Session>(); //SV= Vector of Sessions
        Session.displayTheBigSet();
        int loopval=100,op;
        String menu1="\n\n1. Create session\n2. Use Session \n3. Show All Previous Questions \n4. Add Session Qno to theBigSet\nPress 0 to Exit.\nInput : ";
        String menu2="\n\n1. Create session\n2. Use Session \n3. Show All Previous Questions \n4. Add Session Qno to theBigSet\n5. Login\nPress 0 to Exit.\nInput : ";
        while(loopval--!=0) {
            if(SV.isEmpty())
                System.out.print(menu1);
            else
                System.out.print(menu2);
            op = obj.nextInt();
            switch(op) {
                case 1: Session S = new Session(101,sNo++);
                        SV.add(S);
                        break;
                case 2: int sno=0;
                        System.out.print("Enter Session No. : ");
                        sno = obj.nextInt();
                        sno=sno-1;
                        if(sno>=SV.size())
                            System.out.println("Invalid Session!\n");
                        break;
                case 3: displayTheBigSet();
                        break;
                case 4: for(Session s:SV)
                            s.addToTheBigSet();
                        saveTheBigSet();
                        displayTheBigSet();
                        break;
                case 5: String prn;int subid;
                        System.out.println("Enter Login Details : ");
                        System.out.print("PRN Number : ");
                        prn= obj.next();
                        System.out.print("Subject Code : ");
                        subid=obj.nextInt();
                        // boolean retVal = Login(prn,subid);
                        // if(retVal) System.out.println("Logged In Successfully!");
                        // else System.out.println("Number of logins Exceeded");
                        break;
                case 0: return;
                default: System.out.println("Enter Valid Number");
            }
            }
    }
}

class Subject {
    private int subjectId;
    private LocalDateTime dat;

    public Subject(int subjectId,LocalDateTime dat) {
        this.subjectId=subjectId;
        this.dat=dat;
    }
}
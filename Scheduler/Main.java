/*
* Main file for Preemptive scheduler which reads in input file and passes 
* to chosen scheduler object
* Java 8 lambdas are used for this set of schedulers.
* 
* input file --> [Main] --> List of [Process] --> [Scheduling algorithm]
*
* Scheduler manages queues (array lists) for access to CPU and IO based 
* on the method chosen by the user. The collection of processes on the queues 
* are sorted using comparators so that the desired process is located at index 0.
* The ID of this process is then printed as a string in the CPU or IO array and the process removed from queue.
* 
* 
* -- Candice Withrow 4/15 --
*
* 
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {

  
    static List<Process> processList; //created from input then passed to scheduler which passes to algorithm
    static BufferedReader br;
    static int numProc;
    
    
    public static void main(String[] args) 
    {
        
      //take input  
        Scanner in = new Scanner(System.in);
        String filename = "testing.txt";
        int run;
        run = 2;
    System.out.println("===============================================================================");
	System.out.println("~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~");
	System.out.println("-------------------------------------------------------------------------------");
	System.out.println("~ ~ ~ ~ ~ __ ~ ~__ ~  __ ~ _|_~ _|_ ~ ~  ~ ~ ~ ~ ~ ~ ~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~");
	System.out.println("---------|__)--|--)--|__|---|----|---|--|--------------------------------------");
	System.out.println("~ ~ ~ ~ ~| ~ ~ | ~ ~ (___~ ~|~ ~ | ~ (__| ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~");
	System.out.println("-------(_|------------------------------|--------------------------------------");
	System.out.println("~ ~ ~ ~ ~ ~ ~ ~ ~ __ ~ ~ ~ ~ ~ ~ ~ ~ ~__) ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~");
	System.out.println("-----------------|  )---------------------------------|------------------------");
	System.out.println("~ ~ ~ ~ ~ ~ ~ ~ ~(___ ~ ~__~ ~|~ ~ ~ __~ ~ ~ |~ ~ ~ ~ | ~ __~ ~ __~ ~ ~ ~ ~ ~ ~");
	System.out.println("-------------------- |--|  )--|__---|__|---__|--|--|--|--|__|--|--)------------");
	System.out.println("~ ~ ~ ~ ~ ~ ~ ~ ~(___) ~(___ ~|~ | ~(___ ~(__| ~|__(~ ( ~(___~ |~ ~ ~ ~ ~ ~ ~ ~");
	System.out.println("-------------------------------------------------------------------------------");
	System.out.println("~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~");
	System.out.println("===============================================================================");		
    System.out.println();
      
      
      while (run !=0)
       {
		   
		if (run == 2)
		{	System.out.print("Enter a path for the file containing processes to be scheduled: ");
			filename = in.next(); }
        
 //       filename = "testing1.txt";
  
        BufferedReader br = null;
       
        try
         {
            
            String sCurrentLine;     
            br = new BufferedReader(new FileReader(filename));
   
            List<Process> processList = new ArrayList<Process>();
            String numP = br.readLine();
            numProc = Integer.parseInt(numP);
            
            for (int i=0; i < (numProc); i++)
             {
             
              // String ID =  String.valueOf((char)(i + 65));  //only up to 26 process :(
  //----to label each process, up to 100
              String ID = "ID";
				if ( i<26)
				{ ID =  String.valueOf((char)(i + 65));}  //only up to 26 process :(
				else if (i>25 && i < 52)
				{ ID =  String.valueOf((char)((i%26) + 65)) + Integer.toString(1) ;}
				else if (i>51 && i < 78)
				{ ID =  String.valueOf((char)(i%26 + 65)) + Integer.toString(2) ;}
				else if (i>77 && i < 101)
				{ ID =  String.valueOf((char)(i%26 + 65)) + Integer.toString(3) ;}
				else 
				{ System.out.println("Error, scheduler limited to 100 processes.");  }
       
                sCurrentLine = br.readLine();
                String a[] = sCurrentLine.split(" ");
                
				int arrivalTime = new Integer(a[0]);
				int priorLvl = new Integer(a[1]);
				int procTime = new Integer(a[2]);
				int numBursts = new Integer(a[3]);
			    
			   
                Process proc = new Process(ID,arrivalTime,priorLvl,procTime,numBursts);
                
                processList.add(proc);   
             
		     }
        
			System.out.println("Processes to be scheduled : ");
		
			for (Process p : processList) 
			{   System.out.println("\t" + p);	}
		

			int choice;
			int debugOpt;
			int debug = 0;
			int q;
			
			System.out.println("Select an algorithm: ");
			System.out.println("1. Preemptive Priority");
			System.out.println("2. SJRN");
			System.out.println("3. Roundrobin");
			System.out.println("4. PrePriority w/ RR");
			
			choice = Integer.parseInt(in.next());
			System.out.println("Select '1' to run in debug mode, or 2 to continue in normal mode.");
			debugOpt = Integer.parseInt(in.next());
			if(debugOpt ==1) 
			{debug = 1;}
        
			switch(choice)
			{	
				case 1:
					System.out.println("You have selected pre-priority scheduling, with secondary criteria of FCFS. ");
					System.out.println("-------------------------------------------------------------------------------");
					PrePriority ppsched = new PrePriority(processList,numProc,debug);
                    ppsched.run();
					
					break;
				case 2:
					System.out.println("You have selected shortest job remaining next (SJRN).");
					System.out.println("-------------------------------------------------------------------------------");
					SJRNSched sched = new SJRNSched(processList,numProc,debug);
                    sched.run();
					break;
				case 3:
					System.out.println("You have selected round robin; enter the desired quantum: ");
					q = Integer.parseInt(in.next());
					System.out.println("-------------------------------------------------------------------------------");
					RoundRobin rrsched = new RoundRobin(processList,numProc,debug,q);
                    rrsched.run();
					break;
				case 4:
					System.out.println("You have selected priority with secondary criteria round robin. Enter the desired quantum: ");
				    q = Integer.parseInt(in.next());
				    System.out.println("-------------------------------------------------------------------------------");
				    PrePriorRR pprrsched = new PrePriorRR(processList,numProc,debug,q);
                    pprrsched.run();
					break;

				default:
					System.out.println("Error in choice, please try again:");
			}
		   
		   	System.out.println("===============================================================================");
		    System.out.println();
		    System.out.println("Enter '1' to run the scheduler on these processes again, '2' to enter a new filepath or '0' to exit. ");
		    run = Integer.parseInt(in.next());;
          } //end try
	   
          catch (IOException e) {e.printStackTrace(); } 
         finally 
            {
            try {if (br != null)br.close();
                } catch (IOException ex) {ex.printStackTrace();}
			}

		} //end while
		
	System.out.println("===============================================================================");
	System.out.println("~ ~ ~ ~ ~ ~ ~ ~ __~ ~_~ ~_~ ~_| |_ ~ ~ ~ _~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~");
	System.out.println("---------------(__|-(_)-(_)-(_|-|_)-(_|-|_)------------------------------------");
	System.out.println("~ ~ ~ ~ ~ ~ ~ ~ __) ~ ~ ~ ~ ~ ~ ~ ~ ~ | (__~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ");
	System.out.println("------------------------------------(_|----------------------------------------");
	System.out.println("===============================================================================");
	
	}
	

	
}


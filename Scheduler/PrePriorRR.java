/* 
 * PrePriorRR.java  
 * 
 * Preemptive Priority with Round Robin as second criteria:
 * Allows high priority processes to take CPU or IO resources 
 * from in-progress lower priority ones- if two processes 
 * have equal priority the one ahead in the round robin list, which 
 * regularly rotates position based on a time quantum, gets priority
 * 
 * Schedules processes into Gantt chart
 * - taking items from CpuQueue and IOQueue
 * - sorting by method
 * - inserting into CPUARRAY, and IOARRAY
 *  
 *  -- Candice Withrow 4/15 --
*/



import java.util.*;
import java.text.DecimalFormat;
import java.io.BufferedReader;


public class PrePriorRR{
	
    protected static List<Process> processList, cpuQueue, IOQueue;
	protected static ArrayList<Process> arrivalList, RRlist;  


	static ArrayList<String> CPUARRAY, IOARRAY; 
	
	static int currTime;
	
	static Process currProc, currIOProc;
	static String currID;
	static int IOTimeMax = 4;
    static int numProcsComp = 0;
    static int numProc;
	static int debug;
	static int contextSwitches = 0;
	static float throughput = 0;
	static int completionTime = 0;
	static int cpuIdle = 0;
	static int utiliz = 0;
	static boolean IOrunning = false;  //tells adder if process is running
	static boolean nextTurn;  //tells adder if process is running
	static int q; //quantum
	static int runCnt;  //time lapsed for process running
	DecimalFormat df;
	
	public PrePriorRR(List<Process> processes, int numProc, int de, int q)
	{
		this.processList = processes;
		this.numProc = numProc;
		this.debug = de;
		this.q = q;
	}

	public static void run()
    {
      CPUARRAY = new ArrayList<String>();
	  IOARRAY = new ArrayList<String>();
      schedule();
     }
    
 
	public static void schedule() //throws NumberFormatException, IOException  
	{
		
		cpuQueue =  new ArrayList<Process>();
		IOQueue = new ArrayList<Process>();
		arrivalList = new ArrayList<Process>(processList);
	    RRlist = new ArrayList<Process>();
		
		int numProcsComp = 0;
		int currPriorLvl = 1000;
		currTime = 0;
		nextTurn = true;
		runCnt = 0;

		while( numProcsComp < numProc)                  //numProcsComp < numProc)
		{
				if (debug == 1)
				{	System.out.println( "-----------------------------------------------");
					System.out.println( "at time t = " + currTime);}

//----load CPU queue----------
			for (int i=0; i < arrivalList.size(); i++) //cycle through positions in arrivalList
			{       
			   Process tempProc = arrivalList.get(i); 
			   int procAT = tempProc.currArrivTime;
			    if (procAT <= currTime)
			     {	 cpuQueue.add(tempProc);
					 tempProc.firstRequest = currTime;	
					 arrivalList.remove(i);
					 arrivalList.trimToSize();	
				 } //add to cpuQueue
			 }	
				if (debug == 1)
				{	printQueues();	}
				 
//------- add to CPU array---------		
			
			sortCPUQueue();
			
//----select process to add	to CPU array	

			if (cpuQueue.size() < 1)
			   {
				CPUARRAY.add(currTime,"_"); //CPU idle
				cpuIdle++;	
				}  
			
			else 
			
			{
				currProc = cpuQueue.get(0);
				RRlist.clear();
				
				if (currProc.getPriorLvl() != currPriorLvl) //new list
					{ //RRlist.removeAll();
					  currPriorLvl = currProc.getPriorLvl(); 
					  runCnt = 0;				//set to zero every time different priority lvl
					  nextTurn = true;
					  }
				
				if (cpuQueue.size() > 1) //make RRlist
					{  // check for same priorlvl, add to new list

						for (Process p : cpuQueue) 
						{   currPriorLvl = currProc.getPriorLvl(); 
							if (p.getPriorLvl() == currPriorLvl)
							{ RRlist.add(p);}
						 }
				 
						if (RRlist.size() > 0)   //only if something else with same priority
						{	Collections.sort( RRlist, Comparator.comparing((Process p1) -> p1.currArrivTime));
							currProc = RRlist.get(0);   //get top of RR list
							nextTurn = false;
							runCnt++;
							if (debug == 1)
							{	System.out.print(" RRlist: [" );
								for (Process r : RRlist) 
								{   System.out.print(r.getID() +  " ");	}
								System.out.println("]" );
							}	
						}
					}
				
					
				else if  ((RRlist.size() > 1) && nextTurn == true)	//change to next process in RRlist
					
					{
					   if (debug == 1)
						{	System.out.print(" RRlist: [" );
							for (Process r : RRlist) 
							{   System.out.print(r.getID() +  " ");	}
							System.out.println("]" );
						} 
					   runCnt = 0;
					   Collections.sort( RRlist, Comparator.comparing((Process p1) -> p1.currArrivTime));
					   currProc = RRlist.get(0);   //get top of RR list
					  
					 
					}
				
						
	//---- actually add to CPUARRAY	
				if (CPUARRAY.contains(currProc.getID()) == false)
					{ currProc.firstResponse = currTime ; }
				
				CPUARRAY.add(currTime,currProc.getID()); 
					if (debug == 1 && currProc.procTimeCnt < 1 )
					{	System.out.println("Process " + currProc.getID() + " accessed the CPU at time t = " + currTime + "s.");	}

				currProc.procTimeCnt ++; //increase procTimeCnt tally
				
				//set runCnt and flag for roundrobin
				
				if (RRlist.size() > 1)
			{	runCnt++;  	
				if (runCnt >= q)
					{ nextTurn = true;	
					 
					 if (RRlist.contains(currProc))
						{ /*Process tempProc;
							tempProc = currProc;
							RRlist.remove(currProc);
							RRlist.add(tempProc);*/
					  currProc.currArrivTime = (currTime + 1);  //add to RRlist  with updated ready time (adds to end once sorted by time)
						}
					}	
				else
					{ nextTurn = false;}
				}
				
					if (currTime >0)
							{ if (currProc.getID() != CPUARRAY.get(currTime-1))
								{ contextSwitches++; }
							}

			}


//---------	 add to IO array---------
		    sortIOQueue();
			
			
			if (IOQueue.size()< 1)
				{	IOARRAY.add(currTime,"_");	//IO idle	
				}

			else 
			   {
					if (IOrunning == false) 
					{
					currIOProc = IOQueue.get(0);
					}
	
					IOARRAY.add(currTime,currIOProc.getID());
					currIOProc.IOTimeCnt ++;
					IOrunning = true;
						if (debug == 1&& currIOProc.IOTimeCnt < 1)
						{System.out.println("Process " + currIOProc.getID() + " entered IO at time t = " + currTime + "s.");  }
				
		       }
			    
				if (debug == 1)
				{	printArrays();	} 
				   
//-----------readjust queues after cpuProc Cnts change-------
				    		
			if ((currProc!= null) && (currProc.procTimeCnt == currProc.getProcTime()))  //burst finished
			   {	
				nextTurn= true;
				 currProc.burstCnt ++;   //increase burst cnt if processing time done
				 currProc.procTimeCnt = 0;	
					if (debug == 1 && currProc.burstCnt < currProc.getNumBursts())
					{System.out.println("CPU burst " + currProc.burstCnt + " for process " + currProc.getID() +  " completed at time t = " + currTime); }
				 
				 if ((currProc.burstCnt)==(currProc.getNumBursts()))  // all bursts complete
					{     
						if (debug == 1)
						{System.out.println("All bursts for process " + currProc.getID() +  " completed at time t = " + currTime);
						 System.out.println();  }
					  currProc.finishTime = currTime;
					  RRlist.remove(currProc);
					  cpuQueue.remove(currProc); //remove from head
					 
					  currProc.procComp = true;
					  numProcsComp ++;		
										  }
					
				else if (currProc.burstCnt < (currProc.getNumBursts()-1))  // enough bursts rmg to do IO
					{
						//add to IOQ     	
					  currProc.currArrivTime = currTime;  //add to q with updated arrival time
					  IOQueue.add(currProc);
					  RRlist.remove(currProc);
					  cpuQueue.remove(currProc);
					  
					
					}
					
				else 			//bursts remain for cpu only
					{	
					  Process tempProc;
					  tempProc = currProc;
					  RRlist.remove(currProc);
					  cpuQueue.remove(currProc);
					  currProc.currArrivTime = currTime;  //add to q with updated arrival time
					  cpuQueue.add(tempProc);
					  RRlist.add(tempProc);
					}		
				//System.out.println("Process: " + currProc.ID + " has procTimeCnt: "+(currProc.procTimeCnt));	
				}
			
//------- readjust queues after IOProc Cnts change---------	
		
		
			if ((currIOProc != null)&&(currIOProc.IOTimeCnt == 4))     //add to cpuQueue if IO complete
				{
					IOrunning = false;
						if (debug == 1)
						{System.out.println("IO burst for process " + currIOProc.getID() +  " completed at time t = " + currTime);}	
					currIOProc.currArrivTime = currTime;    //set Q arrival time
					cpuQueue.add(currIOProc);	
					currIOProc.burstCnt ++;
					currIOProc.IOTimeCnt = 0;
					IOQueue.remove(currIOProc);
					
				}
			
	
			
			
			currTime++; //move to next time slot
	
		} //end while

			
	 printOutput();
	   
	   
	   
	   /*if (debug==1)
			{ printDebug();}
		*/
	
		
    }//end schedule

	
	
 
	private static void printQueues()
	{	

      System.out.print("(cpuQueue: [" );
        for (Process p : cpuQueue) 
		{   
			 System.out.print(p.getID() +  " ");	}	
	   System.out.print("]\t" );
	   System.out.print(" IOQueue: [" );
        for (Process q : IOQueue) 
		{   System.out.print(q.getID() +  " ");	}
		System.out.println("]" );
	 	  
	 }
	
	
	private static void printArrays()
	{	
	  String cpuString = CPUARRAY.toString().replace(", ", ""); 
	  String IOString = IOARRAY.toString().replace(", ", "");
	  System.out.println("CPU: " + cpuString );
	  System.out.println("IO:  " +  IOString);
	 }
	
	public static void sortCPUQueue()
	{
		Collections.sort( cpuQueue, Comparator.comparing((Process p1) -> p1.getPriorLvl())
	.thenComparing(p1 -> p1.currArrivTime)
	);
	}
	
	public static void sortIOQueue()  //sorts with priority but (IO) current arrival time first so that no preemption occurs
	{
		Collections.sort( cpuQueue, Comparator.comparing((Process p1) -> p1.getPriorLvl())
	.thenComparing(p1 -> p1.currArrivTime)
	);
	}
		
	
	private static void printOutput()
	{
	  
	   DecimalFormat df = new DecimalFormat("0.00");
	   completionTime = currTime;
	   throughput = ((float)numProc / completionTime);
	   utiliz = ((100*completionTime - cpuIdle)/(completionTime));
	   
	   System.out.println("All jobs completed.");
	   printArrays();	
	   
	   System.out.println("Context switches: " + contextSwitches );
	   System.out.println("Throughput: " + df.format(throughput) + " proc/s ");
	   System.out.println("Cpu utilization: " + utiliz + "%");
	   
//---process specific output---	   
	   for (Process p : processList) 
		{   int responseTime = p.firstResponse-p.firstRequest;
			int turnaroundtime = (p.finishTime-p.getInitArrivTime());
			System.out.println("Process " + p.getID() + " Analysis:\t response time : "
			+ responseTime + "\t turnaround time: " + turnaroundtime ) ;
			
		}	
			
	}//end output
	
	
	private static void printDebug()
	{
		//page faults??
	}

	/*private static void resetAT()
	{
		for (Process p : processList) 
		{    p.currArrivTime = p.getInitArrivTime;}
	}	*/			
} //end Scheduler.java	
	

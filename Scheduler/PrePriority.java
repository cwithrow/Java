/* 
 * PrePriority.java  
 * 
 * Preemptive priority: allows high priority processes to take CPU 
 * or IO resources from in progress lower priority ones. 
 * (First come fist serve as secondary criteria)
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


public class PrePriority{
	
    protected static List<Process> processList, cpuQueue, IOQueue;
	protected static ArrayList<Process> arrivalList;  
	
	protected static ArrayList<Process> queue;
	
	
    
	
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
	
	DecimalFormat df;
	
	public PrePriority(List<Process> processes, int numProc, int de)
	{
		this.processList = processes;
		this.numProc = numProc;
		this.debug = de;
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
		
		int numProcsComp = 0;

		currTime = 0;
	
//---using PriorityQueue----
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
			
		
			if (cpuQueue.size() < 1)
			   {
				CPUARRAY.add(currTime,"_"); //CPU idle
				cpuIdle++;	
				}  
			
			else 
			
			{
				currProc = cpuQueue.get(0);
				//addto CPUARRAY	
				if (CPUARRAY.contains(currProc.getID()) == false)
					{ currProc.firstResponse = currTime ; }
				
				CPUARRAY.add(currTime,currProc.getID()); 
					if (debug == 1 && currProc.procTimeCnt < 1 )
					{	System.out.println("Process " + currProc.getID() + " accessed the CPU at time t = " + currTime + "s.");	}

				currProc.procTimeCnt ++; //increase procTimeCnt tally
				//running = true;	
				
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
			
			else 	//add to IOARRAY
				{
					currIOProc = IOQueue.get(0);
					IOARRAY.add(currTime,currIOProc.getID());
				    currIOProc.IOTimeCnt ++;
						if (debug == 1&& currIOProc.IOTimeCnt < 1)
						{System.out.println("Process " + currIOProc.getID() + " entered IO at time t = " + currTime + "s.");  }
				}
			    
				if (debug == 1)
				{	printArrays();	} 
				   
//-----------readjust queues after cpuProc Cnts change-------
				    		
			if ((currProc!= null) && (currProc.procTimeCnt == currProc.getProcTime()))  //burst finished
			   {	
				// running = false;
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
					  cpuQueue.remove(currProc); //remove from head
					  currProc.procComp = true;
					  numProcsComp ++;		
										  }
					
				else if (currProc.burstCnt < (currProc.getNumBursts()-1))  // enough bursts rmg to do IO
					{
						//add to IOQ     	
					  currProc.currArrivTime = currTime;  //add to q with updated arrival time
					  IOQueue.add(currProc);
					  cpuQueue.remove(currProc);
					
					}
					
				else 			//bursts remain for cpu only
					{	
					  Process tempProc;
					  tempProc = currProc;
					  cpuQueue.remove(currProc);
					  currProc.currArrivTime = currTime;  //add to q with updated arrival time
					  cpuQueue.add(tempProc);
					}		
				//System.out.println("Process: " + currProc.ID + " has procTimeCnt: "+(currProc.procTimeCnt));	
				}
			
//------- readjust queues after IOProc Cnts change---------	
		
		
			if ((currIOProc != null)&&(currIOProc.IOTimeCnt == 4))     //add to cpuQueue if IO complete
				{
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
		Collections.sort( cpuQueue, Comparator.comparing((Process p1) -> p1.currArrivTime)
	.thenComparing(p1 -> p1.getPriorLvl())
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
	

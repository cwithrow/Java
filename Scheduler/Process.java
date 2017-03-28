/* Process record to store information about each process
 *  

 * ---Candice Withrow 4/6/15---
 * 
*/
 
 
 
 public class Process {


	private String ID;			
	//These are from input file
	private Integer initArrivTime;     //time arrives on scheduler
	private Integer priorLvl;        //priority level
	private Integer procTime;     //CPU process time
	private Integer numBursts;		//number of CPU bursts

	boolean procComp; 
	int burstCnt, procTimeCnt, IOTimeCnt = 0;
	Integer procTimeRmg; 	//process time remaining (for SJRN)
	Integer currArrivTime; //time arrives on queues  
	int firstFinish;        //first CPU process finishes
	int firstRequest;       //first arrival on queue
	int firstResponse;      //firstFinish - firstRequest
	int finishTime;			//job fully completed
   
	 
	 //need to add other times for calculations later
	public Process() {}
	public Process(String ID, int arrivalTime, int priorLvl, int procTime, int numBursts)
	{
		this.ID = ID;
		this.initArrivTime = arrivalTime;
		this.priorLvl = priorLvl;
		this.procTime = procTime;
		this.numBursts = numBursts;
		this.currArrivTime = arrivalTime;
		this.procTimeRmg = procTime;
	 }
	 
    public String getID()
	 {	return ID;	}
	 
	public Integer getInitArrivTime()
	{	return initArrivTime;	}
	 
	public Integer getPriorLvl()
	{	return priorLvl;	}
	 
	public Integer getProcTime()
	{	return procTime;	}
	 
	public Integer getNumBursts()
	{	return numBursts;	}
	


	public String toString() 
	{	return "" + ID + ", " + initArrivTime + ", " + priorLvl + ", " + procTime + ", " + numBursts; }

 
	
 
	 
	 
}
	 

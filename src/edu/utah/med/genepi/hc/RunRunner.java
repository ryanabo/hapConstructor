package edu.utah.med.genepi.hc;

import java.io.IOException;

import edu.utah.med.genepi.app.rgen.PedGenie;
import edu.utah.med.genepi.stat.CCAnalysis;
import edu.utah.med.genepi.util.GEException;

//RunRunner is a class that implements Runnable to create a new thread.
public class RunRunner implements Runnable{

  private CCAnalysis[] 		ca;
  public  Thread 			t;
  private int 				sigrun;
  private int 				icycle;
  private PedGenie 			pedgenie;
  private int 				evalCycles;
  private int				cyclePt;
  private int				step;
  private compressGtype     cGtype;
  
  //RunRunner is used to split the number of calls to update() in PedGenie.java (which is the number of simulated cycles) to two threads.
  //Currently not being utilized since it does not seem to gain anything in performance.
  public RunRunner(int significancerun, int startpt, int stp, int totalNumCycles, PedGenie genie, CCAnalysis[] cA){
    pedgenie = genie;
    //cGtype = compressGT;
    sigrun = significancerun;
    ca = cA;
    //No. of simulations to do.
    evalCycles = totalNumCycles;
    //Start location in array
    cyclePt = startpt;
    step = stp;
    //Start a new thread to run an update.
    t = new Thread(this);
    //Call start() which calls run()
    t.start();
  }
  
  public void run(){
	//Determine what is half of total number of cycles.
	int half = evalCycles / 2;
	int quarter = evalCycles / 4;
	int other_half = evalCycles - half;
	//Set default start and iter to 0 and half.
	int iter = half;
	int start = 0;
	//Determine if starting cycle is halfway or at 0.
	if(cyclePt != 0){
	  start = half;
	  iter = start + other_half;
	}

    //Call update totalCycles / 2 number of times.
	//for(int i=start; i< iter;i++){
	for(int i=cyclePt; i< cyclePt
	+ quarter;i++){
	  System.out.println("Thread: " + t + " Iter:" + i + " priority " + t.getPriority());
	  
      try {
        boolean flg = false;
        if(sigrun > -1 && sigrun != icycle){
          flg = true;
        }
        else if(sigrun == -1){
          flg = true;
        }
        if(flg){
          pedgenie.update(sigrun, step, i);
        }
	  } catch (GEException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
	}	
  }
}



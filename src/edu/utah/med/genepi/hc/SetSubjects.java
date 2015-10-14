package edu.utah.med.genepi.hc;

import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.stat.CCAnalysis;

public class SetSubjects implements Runnable {
	
	private CCAnalysis[] ccAnalysis;
	public Thread t;
	private int index;
	private Study[] stdy;

	public SetSubjects(int start,Study[] study, CCAnalysis[] ccA) {
		stdy = study;
		ccAnalysis = ccA;
		index = start;
		t = new Thread(this);
		t.start();
	}

	public void run() {
		System.out.println("Call:" + t.getId());
		int len = ccAnalysis.length;
		int half = len / 2;
		int other_half = len - half;
		int iter = half;
		int start = 0;
		if (index != 0) {
			start = half;
			iter = start + other_half;
		}
		for (int i = start; i < iter; i++) {
		  ccAnalysis[i].setStudySubjects(stdy);
		}
		System.out.println("Thread: " + t.getId() + " ended");
	}
}


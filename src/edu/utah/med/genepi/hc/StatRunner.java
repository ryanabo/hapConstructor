package edu.utah.med.genepi.hc;

import edu.utah.med.genepi.stat.CCAnalysis;

public class StatRunner implements Runnable {
	
	private CCAnalysis[] ccAnalysis;
	public Thread t;
	private int index;
	private int icycle;
    private compressGtype cGtype;
	private String mode;
	private int sigrun;

	public StatRunner(CCAnalysis[] ccA, int indx, int cycle, String code,
			int significancerun, compressGtype genotypes) {
		
		ccAnalysis = ccA;
		sigrun = significancerun;
		mode = code;
		index = indx;
		cGtype = genotypes;
		icycle = cycle;
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
			if (mode == "stat") {
				// synchronized(ccAnalysis){
				ccAnalysis[i].startObsRuns(sigrun,icycle,cGtype,i);
				//ccAnalysis[i].startStatRuns(icycle, t, i, proc);
				// }
			} else {
				if (sigrun > -1) {
					if (i != icycle) {
						ccAnalysis[i].startsimRuns(sigrun,icycle,cGtype,i);
					}
				} else {
					//ccAnalysis[i].updateStatRuns(icycle, t, i, proc);
					ccAnalysis[i].startsimRuns(sigrun,icycle,cGtype,i);
				}
			}
		}
		System.out.println("Thread: " + t.getId() + " ended");
	}
}

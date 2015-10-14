package edu.utah.med.genepi.hapconstructor;

public class hapCAnalysisStep {

	private int step = 1;
	// 1 = back, 0 = forward
	// col 0 = previous direction, col 1 = current direction
	private int[] direction = new int[]{0,0};
	// Tracks the index for constructionWide signficance testing, -1 = observed data, > -1 = sim index
	private int sigTestingIndex = -1;
	
	public hapCAnalysisStep()
	{ }
	
	//----------------------------------------------------------------------------
	public int getStep(){ return step; }
	
	//----------------------------------------------------------------------------
	public int getSigTestingIndex(){ return sigTestingIndex; }
	
	//----------------------------------------------------------------------------
	public void incrementSigTestingIndex(){ sigTestingIndex++; } 
	
	//----------------------------------------------------------------------------
	public void setStep( int s ){ step = s; }
	
	//----------------------------------------------------------------------------
	public void incrementStep( boolean useBackSets )
	{ 		
		if ( useBackSets && step > 1 )
		{
			// Going from forward-forward to forward-backward 
			if ( direction[0] == 0 && direction[1] == 0 )
			{
				direction[0] = 0;
				direction[1] = 1;
				step++;
			}
			else if ( direction[0] == 0 && direction[1] == 1 )
			{
				direction[0] = 1;
				direction[1] = 0;
				step--;
			}
			else if ( direction[0] == 1 && direction[1] == 0 )
			{
				direction[0] = 0;
				direction[1] = 0;
				step++;
			}
		}
		else step++;
	}
	
	//----------------------------------------------------------------------------
	public void reset()
	{
		step = 1;
		direction[0] = 0;
		direction[1] = 0;
	}
	
	//----------------------------------------------------------------------------
	public int getDirection(){ return direction[1]; }
}

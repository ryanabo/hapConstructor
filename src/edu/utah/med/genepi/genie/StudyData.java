package edu.utah.med.genepi.genie;

import java.io.File;
import java.io.IOException;

import alun.genapps.GeneDropper;
import alun.linkage.LinkageDataSet;
import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;
import edu.utah.med.genepi.phase.GCHapExt;
import edu.utah.med.genepi.phase.PedPhase;
import edu.utah.med.genepi.phase.Phase;

interface Col {
    int PED_ID  = 0;
    int IND_ID  = 1;
    int DAD_ID  = 2;
    int MOM_ID  = 3;
    int SEX_ID  = 4;
    int PHEN    = 5;
    int LIAB    = 6;
    int MARKER1 = 7;
  }

public class StudyData {

	private String studyName = null;
	private File genotypeFile = null;
	private File quantitativeFile = null;
	//private File haplotypeFile = null;
	//private File linkageParFile = null;
	private DataInterface di = null;
	
	public StudyData( String[] fileNames, GenieParameterData gp, int studyIndex ) throws IOException
	{
    	studyName = fileNames[0];
    	genotypeFile = new File(fileNames[1]);
    	if ( fileNames[3] != null ) quantitativeFile = new File(fileNames[3]);
    	//if ( fileNames[2] != null ) haplotypeFile = new File(fileNames[2]);
    	//if ( fileNames[4] != null ) linkageParFile = new File(fileNames[4]);
    	loadData(gp,studyIndex);
	}

	//--------------------------------------------------------------------------------
	public String getName(){ return studyName; }
	
	//--------------------------------------------------------------------------------
	public String getDataSourceName(){ return genotypeFile.getPath(); }
	
	//--------------------------------------------------------------------------------
	public boolean hasRelatedSubjects(){ return di.hasRelatedSubjects(); }
	
	//--------------------------------------------------------------------------------
	public AnalysisTable getTable( int[] simIndices, String table, ColumnManager cm, GenieParameterData gp )
	{ 
		return di.getTable(simIndices,table,cm,gp); 
	}
	
	//--------------------------------------------------------------------------------
	public void setIndShuffle(){ di.setIndShuffle(); }
	
	//--------------------------------------------------------------------------------
	private void loadData( GenieParameterData gp, int studyIndex ) throws IOException
	{
		System.out.println("Loading data from '" + genotypeFile.getName() + "'...");
		if ( quantitativeFile != null ) System.out.println("Loading data from '" + quantitativeFile.getName() + "'...");

		DataFileProcessor dfp = new DataFileProcessor(genotypeFile,quantitativeFile,gp,studyIndex);
	    di = new DataInterface(dfp,gp,studyIndex);
	}
	
	//--------------------------------------------------------------------------------
	public void loadNullData(){ di.loadNullData(studyName); }
	
	//--------------------------------------------------------------------------------
	public void simNullData( GenieParameterData gp, boolean simulateAll )
	{ 
//		if ( di.hasRelatedSubjects() )
//		{
			LinkageDataSet linkageDataSet = di.getLinkageDataSet(gp,"all",0);
			GeneDropper g = new GeneDropper(linkageDataSet,di.getLDModel());
			if ( simulateAll )
			{
				for ( int i=1; i < (gp.getNSims()+1); i++ )
				{
					if ( gp.logNullData() ) System.out.println("Sim " + (i + gp.startNullLogIndex()));
					//System.out.println("Gene drop");
					g.geneDrop(false);
					if (gp.phaseData())
					{
						phase(gp,i);
					}
					else di.storeLinkageData(i);
				}
			}
			else
			{
				// PedGenie and HapMC simulations for related subjects
				// store data in sim index 1
				g.geneDrop(false);
				if (gp.phaseData()) phase(gp,1);
				else di.storeLinkageData(1);
			}
//		}
//		else
//		{
//			di.permuteValues();
//		}
	}
	
	//--------------------------------------------------------------------------------
	public void phase( GenieParameterData gp, int simIndex )
	{ 	
		Phase phaser = new GCHapExt(gp,di.getGCHapDataSource(gp,simIndex));
		if ( gp.getPhaser().equals("pedphase") ) phaser = new PedPhase(gp,di);
		phaser.phase();
		if ( simIndex > -1 )
		{
			if ( gp.logNullData() && simIndex > 0 )
			{
				if ( gp.getAppId().equals("simnull") )
				{
					phaser.storeData(gp,di,1);
				}
				else if ( gp.getAppId().equals("hapconstructor") )
				{
					phaser.storeData(gp,di,simIndex);
				}
				di.logNullData(simIndex,studyName,gp.startNullLogIndex());
			}
			else phaser.storeData(gp,di,simIndex);
		}
		else phaser.output();
	}
}

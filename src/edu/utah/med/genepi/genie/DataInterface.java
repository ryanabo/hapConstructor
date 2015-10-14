package edu.utah.med.genepi.genie;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import alun.linkage.LinkageDataSet;
import alun.linkage.LinkageIndividual;
import alun.linkage.LinkageInterface;
import alun.linkage.LinkagePedigreeData;
import alun.linkage.LinkagePhenotype;
import alun.linkage.NumberedAllelePhenotype;
import alun.mcld.LDModel;
import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.gm.GenotypeDataInterface;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.gm.Qdata;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;
import edu.utah.med.genepi.linkageext.GCHapDataSource;
import edu.utah.med.genepi.linkageext.LinkageIndividualExt;
import edu.utah.med.genepi.linkageext.LinkageParameterDataExt;
import edu.utah.med.genepi.ped.Individual;
import edu.utah.med.genepi.ped.Pedigree2;
import edu.utah.med.genepi.stat.ContingencyTallier;
import edu.utah.med.genepi.stat.IntxLDTallier;
import edu.utah.med.genepi.stat.QuantitativeTallier;
import edu.utah.med.genepi.util.Trio;
import edu.utah.med.genepi.util.Ut;

/** This class is owned by the StudyData class and contains the functions
 * and variables necessary to access all the pedigree, genotype, phenotype, 
 * and quantitative data for any type of analysis performed through Genie.
 * 
 * @author Ryan Abo Jan 30, 2010
 * @version 1.0
 * 
 */
public class DataInterface {

	private Pedigree2[] peds = null;
	//private Individual[] inds = null;
	private IndividualManager indivManager = null;
	private Phenotype[] phens = null;
	private Qdata[] quants = null;
	private GenotypeDataInterface gtDataInterface = null;
	private int[] caseIndices = null;
	private int studyIndex = 0;
	private LinkageDataSet linkageDataSet = null;
	private LDModel ldmodel = null;

	/** Constructor for DataInterface object which sets the pedigree, quantitative and 
	 * genotype data into permanent, compact storage data structures.
	 * 
	 * @param dfp	DataFileProcessor object containing structured input data.
	 * @param gp	Parameter data object to access all rgen parameters
	 * @param index	The integer index of the study.
	 */
	public DataInterface( DataFileProcessor dfp, GenieParameterData gp, int index )
	{
		setPeds(dfp);
		setData(dfp,gp);
		studyIndex = index;

		if ( peds != null ) linkageDataSet = getLinkageDataSet(gp,"all",0);
		else linkageDataSet = getLinkageDataSet(gp,"gt",0);
		ldmodel = new LDModel(new LinkageInterface(linkageDataSet));
	}

	//----------------------------------------------------------------------------
	public LDModel getLDModel(){ return ldmodel; }

	//----------------------------------------------------------------------------
	public void storeLinkageData( int dataSourceIndex )
	{
		LinkagePedigreeData lpd = linkageDataSet.getPedigreeData();
		LinkageIndividual[] li = lpd.getIndividuals();
		Individual[] allInds = indivManager.getAllInds();
		for ( int i=0; i < li.length; i++ )
		{
			LinkageIndividualExt lext = (LinkageIndividualExt) li[i];
			if ( !lext.isMissing() )
			{
				LinkagePhenotype[] phenos = lext.getPhenotypes();
				for (int j=0; j < phenos.length; j++)
				{
					NumberedAllelePhenotype pheno = (NumberedAllelePhenotype) phenos[j];
					gtDataInterface.storeData(dataSourceIndex,allInds[li[i].id].getDataIndex(),j,pheno.a1,pheno.a2);
				}
			}
		}
	}

	/** Takes structured data from individuals that are stored and creates LinkageIndividuals.
	 * 
	 * @param lpar LinkageParameterData object which contains all the locus information
	 * @param dataSourceIndex This determines where to pull the genotype data from. If the index
	 * is more than -1, then it will pull simulated data.
	 * @return Vector of LinkageIndividuals necessary to create a LinkagePedigreeData object
	 * for LinkageDataSet object which is required for GeneDrops and GCHap phasing.
	 * 
	 */
	//----------------------------------------------------------------------------
	public Vector<LinkageIndividual> getLinkageInds( LinkageParameterDataExt lpar, int dataSourceIndex, String inds )
	{

		Individual[] allInds = indivManager.getAllInds();
		int[] dataInds = indivManager.getDataInds();
		Vector<LinkageIndividual> linkageInds = new Vector<LinkageIndividual>();
		if ( inds.equals("all") )
		{
			for ( int i=0; i < allInds.length; i++ )
			{
				linkageInds.add(new LinkageIndividualExt(gtDataInterface,lpar,allInds[i],dataSourceIndex));
			}
		}
		else
		{
			for ( int i=0; i < dataInds.length; i++ )
			{
				linkageInds.add(new LinkageIndividualExt(gtDataInterface,lpar,allInds[dataInds[i]],dataSourceIndex));
			}
		}
		return linkageInds;
	}

	//----------------------------------------------------------------------------
	public boolean hasRelatedSubjects(){ return peds != null; }

	//----------------------------------------------------------------------------
	public AnalysisTable getTable( int[] simIndices, String table, ColumnManager cm, GenieParameterData gp )
	{
		AnalysisTable at = null;
		if ( table.equals("contingency") )
		{
			at = getContingencyTable(simIndices,cm,gp);
		}
		//		else if ( table.equals("pseudo") )
		//		{
		//			at = getPseudoControlTable(simIndex,cm);
		//		}
		else if ( table.equals("quantitative") )
		{
			at = getQuantitativeTable(simIndices,cm,gp);
		}
		else if ( table.equals("interactionld") )
		{
			at = getLDTable(simIndices,cm,gp);
		}
		return at;
	}

	//----------------------------------------------------------------------------
	private void setPeds( DataFileProcessor dfp )
	{
		HashMap<String,String[]> pedDict = dfp.getPedDict();
		if ( pedDict != null )
		{
			HashMap<String,IndividualBuffer> indBuffer = dfp.getIndividualBuffer();
			LinkedHashMap<String,Pedigree2> tempPeds = new LinkedHashMap<String,Pedigree2>();

			if ( pedDict.keySet().size() > 0 )
			{
				int pedIter = 0;
				for ( Iterator<String> it = pedDict.keySet().iterator(); it.hasNext(); )
				{
					int paIndex = -1;
					int maIndex = -1;
					int indIndex = -1;

					String key = it.next();
					String[] keySplit = key.split(":");
					String pedid = keySplit[0];

					IndividualBuffer indBuff = indBuffer.get(key);
					indIndex = indBuff.getStorageIndex();
					String paId = indBuff.getPaId();
					String maId = indBuff.getMaId();
					IndividualBuffer paBuff = null;
					IndividualBuffer maBuff = null;
					if ( !paId.equals("0") )
					{
						paBuff = indBuffer.get(Ut.strAppend(pedid,paId,":"));
						paIndex = paBuff.getStorageIndex();
						indBuff.setPaIndex(paIndex);
					}
					if ( !maId.equals("0") )
					{
						maBuff = indBuffer.get(Ut.strAppend(pedid,maId,":"));
						maIndex = maBuff.getStorageIndex();
						indBuff.setMaIndex(maIndex);
					}

					if ( !tempPeds.containsKey(pedid) )
					{
						Pedigree2 ped = new Pedigree2(pedid,pedIter);
						tempPeds.put(pedid,ped);
						pedIter++;
					}

					Pedigree2 ped = tempPeds.get(pedid);
					int pIndex = ped.getPedIndex();
					indBuff.setPedIndex(pIndex);
					if ( paBuff != null ) paBuff.setPedIndex(pIndex);
					if ( maBuff != null ) maBuff.setPedIndex(pIndex);
					ped.addTrio(new Trio(indIndex,paIndex,maIndex));
					tempPeds.put(pedid,ped);
				}

				int nPeds = tempPeds.keySet().size();
				peds = new Pedigree2[nPeds];
				int iter = 0;
				for ( Pedigree2 ped : tempPeds.values() )
				{
					peds[iter] = ped;
					iter++;
				}
			}
		}
	}

	//----------------------------------------------------------------------------
	private void setData( DataFileProcessor dfp, GenieParameterData gp )
	{
		LinkedHashMap<String,IndividualBuffer> indBuffer = dfp.getIndividualBuffer();				
		int totalInds = indBuffer.keySet().size();
		//int nDataInds = dfp.getNGenotyped();
		int nGtCases = dfp.getNGenotypedCases();
		int nGtControls = dfp.getNGenotypedControls();
		int nAnalysisInds = dfp.getNPhenotypedGenotyped();
		int nGenoInds = dfp.getNGenotyped();

		indivManager = new IndividualManager(totalInds,nAnalysisInds,nGenoInds,nGtCases,nGtControls);
		phens = new Phenotype[nGenoInds];
		quants = new Qdata[nGenoInds];

		boolean storeSims = true;
		//if ( dfp.getPedDict() != null ) storeSims = true;

		gtDataInterface = new GenotypeDataInterface(nGenoInds,storeSims,gp);
		int allIter = 0;
		int dataIter = 0;
		int analysisIter = 0;
		for( Iterator<String> it = indBuffer.keySet().iterator(); it.hasNext(); )
		{
			String key = it.next();			
			IndividualBuffer indBuff = indBuffer.get(key);

			if ( indBuff.getPedIndex() == -1 )
			{
				int pedIndex = indBuff.getIndIndex();
				if ( peds != null ) pedIndex += peds.length;
				indBuff.setPedIndex(pedIndex);
			}

			Individual ind = indBuff.getIndividual();
			indivManager.setInd(allIter,ind);
			if ( indBuff.hasData() )
			{
				Phenotype indPhen = indBuff.getPhenotype();
				phens[dataIter] = indPhen;
				quants[dataIter] = indBuff.getQdata();
				indivManager.setDataIndex(dataIter,allIter);
				if ( indBuff.isPhenoGenotyped() ){
					indivManager.setAnalysisIndex(analysisIter, dataIter);
					analysisIter++;
				}
				gtDataInterface.setGtData(dfp.getStudyIndex(),dataIter,indBuff.getGtData(),gp);
				dataIter++;
			}
			allIter++;
		}
		caseIndices = indivManager.getCaseIndices(phens);
	}

	/** Pass phased haplotype alleles to be stored in the genotype data structure. If simIndex
	 * is 0, then overwrite the observed data and any missing values that were inferred. Otherwise
	 * write simulated data to proper location.
	 * 
	 * @param dataSourceIndex Denotes which location in the genotype and missing to store values.
	 * @param indIndex Individual index in genotype array.
	 * @param locusIndex Locus index.
	 * @param allele1 Value of allele1.
	 * @param allele2 Value of allele2.
	 */
	//----------------------------------------------------------------------------
	public void setPhasedData( int dataSourceIndex, int indIndex, int locusIndex, int allele1, int allele2 )
	{
		gtDataInterface.storeData(dataSourceIndex,indIndex,locusIndex,allele1,allele2);
	}

	//----------------------------------------------------------------------------
	public Integer[] getCaseIndices()
	{
		int[] gtInds = indivManager.getDataInds();
		List<Integer> cIndices = new ArrayList<Integer>();
		for ( int i=0; i < gtInds.length; i++ )
		{
			if ( phens[i].getPhenotype() == 2 )	cIndices.add(i);
		}
		return cIndices.toArray(new Integer[0]);
	}

	//----------------------------------------------------------------------------
	public void setIndShuffle()
	{
		caseIndices = (int[]) permuteCaseIndices(caseIndices);		
	}

	//----------------------------------------------------------------------------
	public void permuteValues()
	{
		phens = (Phenotype[]) permutePhens(phens);
		if ( quants != null ) quants = (Qdata[]) permuteQuants(quants); 
	}

	//----------------------------------------------------------------------------
	public static int[] permuteCaseIndices( int[] c )
	{
		int nobjs = c.length;
		List<Integer> shuffledIndices = new ArrayList<Integer>();
		for ( int i=0; i < nobjs; i++ ) shuffledIndices.add(i);

		Collections.shuffle(shuffledIndices);

		int[] shuffledCases = new int[nobjs];

		for (int i=0; i< nobjs; i++){ shuffledCases[i] = c[shuffledIndices.get(i)]; }
		return shuffledCases;
	}

	//----------------------------------------------------------------------------
	public static Phenotype[] permutePhens( Phenotype[] p )
	{
		int nobjs = p.length;
		List<Integer> shuffledIndices = new ArrayList<Integer>();
		for ( int i=0; i < nobjs; i++ ) shuffledIndices.add(i);

		Collections.shuffle(shuffledIndices);

		Phenotype[] shuffledPhens = new Phenotype[nobjs];

		for (int i=0; i< nobjs; i++){ shuffledPhens[i] = p[shuffledIndices.get(i)]; }
		return shuffledPhens;
	}

	//----------------------------------------------------------------------------
	public static Qdata[] permuteQuants( Qdata[] q )
	{
		int nobjs = q.length;
		List<Integer> shuffledIndices = new ArrayList<Integer>();
		for ( int i=0; i < nobjs; i++ ) shuffledIndices.add(i);

		Collections.shuffle(shuffledIndices);

		Qdata[] shuffledQuants = new Qdata[nobjs];

		for (int i=0; i< nobjs; i++){ shuffledQuants[i] = q[shuffledIndices.get(i)]; }
		return shuffledQuants;
	}

	//----------------------------------------------------------------------------
	public LinkageDataSet getLinkageDataSet( GenieParameterData gp, String inds, int simIndex )
	{ 
		// Initialize if necessary or pull out proper inds from existing linkageDataSet.
		if ( linkageDataSet == null )
		{
			LinkageParameterDataExt lpar = new LinkageParameterDataExt(gp,studyIndex);
			LinkagePedigreeData lped = new LinkagePedigreeData(getLinkageInds(lpar,0,inds));
			linkageDataSet = new LinkageDataSet(lpar,lped);		
		}
		//		else
		//		{
		//			LinkageIndividual[] individuals = linkageDataSet.getPedigreeData().getIndividuals();
		//			if ( inds.equals("all") && (individuals.length != indivManager.getAllInds().length) )
		//			{
		//				// This occurs when going from phasing using only gt inds to genedrop for all inds.
		//				LinkagePedigreeData lped = new LinkagePedigreeData(getLinkageInds((LinkageParameterDataExt)linkageDataSet.getParameterData(),0,inds));
		//				linkageDataSet = new LinkageDataSet(linkageDataSet.getParameterData(),lped);
		//			}
		//			else if ( inds.equals("gt") && (individuals.length != indivManager.getDataInds().length) )
		//			{
		//				// This situation occurs when going from genedrop to phasing
		//				Vector<LinkageIndividual> indivs = new Vector<LinkageIndividual>();
		//				int[] gtIndices = indivManager.getDataInds();
		//				for ( int index : gtIndices ) indivs.add(individuals[index]);
		//				LinkagePedigreeData lped = new LinkagePedigreeData(indivs);
		//				linkageDataSet = new LinkageDataSet(linkageDataSet.getParameterData(),lped);
		//			}
		//		}
		return linkageDataSet; 
	}

	//----------------------------------------------------------------------------
	public GCHapDataSource getGCHapDataSource( GenieParameterData gp, int simIndex )
	{
		int[] gtIndices = indivManager.getDataInds();
		LinkageIndividual[] indivs = new LinkageIndividual[gtIndices.length];
		//		if ( linkageDataSet != null )
		//		{
		// Related data set
		LinkageIndividual[] linkageInds = linkageDataSet.getPedigreeData().getIndividuals();
		if ( indivs.length < linkageInds.length )
		{
			for ( int i=0; i < gtIndices.length; i++ ) indivs[i] = linkageInds[gtIndices[i]];
		}
		else indivs = linkageInds;
		//		}
		//		else
		//		{
		//			// Independent subjects
		//			LinkageParameterDataExt lpar = new LinkageParameterDataExt(gp,studyIndex);
		//			Vector<LinkageIndividual> linds = getLinkageInds(lpar,simIndex,"gt");
		//			indivs = linds.toArray(new LinkageIndividual[0]);
		//			
		//		}
		return new GCHapDataSource(linkageDataSet.getParameterData(),indivs);
	}

	//----------------------------------------------------------------------------
	private AnalysisTable getContingencyTable( int[] simIndices, ColumnManager columnmanager, GenieParameterData gp )
	{		
		Phenotype[] phenotypes = phens;

		int dataIndex = 0;
		if ( simIndices[0] > 0 )
		{
			// Assessing significance of observed result.
			//if (!hasRelatedSubjects()) phenotypes = (Phenotype[]) permutePhens(phens);
			//else dataIndex = simIndices[0];			
			dataIndex = simIndices[0];
		}
		else
		{
			// Generating observed statistic.
			//if (hasRelatedSubjects() && simIndices[1] > -1) dataIndex = simIndices[1]+1;
			if (simIndices[1] > -1) dataIndex = simIndices[1]+1;
		}

		ContingencyTallier ctallier = new ContingencyTallier(new Ptype[]{Ptype.CASE,Ptype.CONTROL},columnmanager,phenotypes);

		gtDataInterface.tallyData(ctallier,dataIndex,indivManager,null);
		AnalysisTable table = ctallier.extractTable();
		return table;
	}

	//----------------------------------------------------------------------------
	private AnalysisTable getQuantitativeTable( int[] simIndices, ColumnManager columnmanager, GenieParameterData gp )
	{
		Qdata[] quantitativeTraits = quants;

		int dataIndex = 0;
		if ( simIndices[0] > 0 )
		{
			// Assessing significance of observed result.
			if (!hasRelatedSubjects()) quantitativeTraits = (Qdata[]) permuteQuants(quants);
			else dataIndex = simIndices[0];			
		}
		else
		{
			// Generating observed statistic.
			if (hasRelatedSubjects() && simIndices[1] > -1) dataIndex = simIndices[1]+1;
		}

		QuantitativeTallier qtallier = new QuantitativeTallier(gp.getCovarIDs(),columnmanager,quantitativeTraits); 
		gtDataInterface.tallyData(qtallier,dataIndex,indivManager,null);
		return qtallier.extractTable();
	}

	//----------------------------------------------------------------------------
	public List<Integer> getNonMissingIndices()
	{
		int[] gtInds = indivManager.getDataInds();
		List<Integer> indices = new ArrayList<Integer>();
		for ( int i=0; i < gtInds.length; i++ )
		{
			indices.add(i);
		}
		return indices;
	}

	//----------------------------------------------------------------------------
	public void logNullData( int simIndex, String studyName, int offsetIndex )
	{
		//System.out.println("Logging null for sim " + simIndex);
		byte[][] simDataSet = gtDataInterface.getSim(1);
		BufferedOutputStream dataOut = null;
		try 
		{
			StringBuffer fname = new StringBuffer();
			fname.append(studyName);
			fname.append(".");
			fname.append(offsetIndex);
			fname.append(".null.log");
			FileOutputStream fstream = null;
			File freport = new File(fname.toString());
			if ( freport.exists() ) 
				fstream = new FileOutputStream(fname.toString(),true);
			else
				fstream = new FileOutputStream(fname.toString());
			dataOut = new BufferedOutputStream(fstream);
			for ( int i = 0; i < simDataSet.length; i++ )
			{
				byte[] row = simDataSet[i];
//				StringBuffer outstr = new StringBuffer();
//				if(i == 0) 
//				{
//					outstr.append("Simulation:");
//					outstr.append(simIndex+offsetIndex);
//					outstr.append("\n");
//					outstr.append(simDataSet[i][0]);
//				}
//				else outstr.append(simDataSet[i][0]);
                dataOut.write(row);
//				for ( int k = 0; k < row.length; k++ )
//				{
//					System.out.print("Row " + i + " col " + k + " value " + row[k]);
//					dataOut.write(row[k]);
//					outstr.append(",");
//					outstr.append(simDataSet[i][k]);
//				}

//				write_sims(outstr+"\n",fname.toString());
			}
		}
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
    	finally 
    	{
        
    		try 
    		{
    			if (dataOut != null) 
    			{
    				dataOut.flush();
    				dataOut.close();
    			}
    		} 
    		catch (IOException ex) 
    		{
    			ex.printStackTrace();
    		}
    	}
	}

	//---------------------------------------------------------------------------  
//	private void write_sims ( String outstr, String fname )
//	{
//		File freport = new File(fname + ".null.log");
//
//
//		if ( freport.exists() )
//		{
//			String filename = fname + ".null.log";
//			boolean append = true;
//			FileWriter fw;
//			try {
//				fw = new FileWriter(filename,append);
//				fw.write(outstr);
//				fw.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}	 
//		}
//		else
//		{
//			freport  = Ut.fExtended(fname,"null.log");
//			PrintWriter pwReport;
//			try {
//				pwReport = new PrintWriter(new BufferedWriter(new FileWriter(freport)), true);
//				pwReport.print(outstr);
//				pwReport.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}  

	//----------------------------------------------------------------------------
	public void loadNullData( String studyName )
	{
		String fileName = studyName + ".null.log";
		try
		{
			DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
			gtDataInterface.loadNullData(input);
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex){
			ex.printStackTrace();
		}	  	
	}

	//----------------------------------------------------------------------------
	private AnalysisTable getLDTable( int[] simIndices, ColumnManager columnmanager, GenieParameterData gp )
	{
		boolean casesOnly = gp.getCaseOnlyIntx();

		int dataIndex = 0;
		int[] indIndices = (int[]) caseIndices;
		//List<Integer> indIndices = Arrays.asList(caseIndices);
		//if ( !casesOnly ) indIndices = getNonMissingIndices();
		if ( !casesOnly ) indIndices = indivManager.getDataInds();
		Phenotype[] phenotypes = phens;
		if ( simIndices[0] > 0 )
		{
			// Assessing significance of observed result.
			//if ( hasRelatedSubjects() ) dataIndex = simIndices[0];
			//else phenotypes = (Phenotype[]) permutePhens(phens);
			dataIndex = simIndices[0];
		}
		else
		{
			// Generating observed statistic.
			//if ( hasRelatedSubjects() && simIndices[1] > -1 ) dataIndex = simIndices[1]+1;
			dataIndex = simIndices[1]+1;
		}

		IntxLDTallier ldTallier = new IntxLDTallier(columnmanager,indIndices,casesOnly,phenotypes,simIndices); 
		//System.out.println(indIndices.toString());
		gtDataInterface.tallyData(ldTallier,dataIndex,indivManager,indIndices);
		return ldTallier.extractTable();
	}

	//----------------------------------------------------------------------------
	public int[] getDataInds(){ return indivManager.getDataInds(); }

	//----------------------------------------------------------------------------
	public Individual[] getAllInds(){ return indivManager.getAllInds(); }
	
	//----------------------------------------------------------------------------
	public int[] getAnalysisInds(){ return indivManager.getAnalysisIndices(); }
}

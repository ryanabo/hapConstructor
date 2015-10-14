package edu.utah.med.genepi.genie;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;

import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.analysis.ExpandedAnalysis;
import edu.utah.med.genepi.analysis.RgenAnalysis;
import edu.utah.med.genepi.analysis.RgenColumn;
import edu.utah.med.genepi.gm.AlleleFormat;
import edu.utah.med.genepi.gm.Gene;
import edu.utah.med.genepi.gm.GtypeMatcher;
import edu.utah.med.genepi.gm.Loci;
import edu.utah.med.genepi.gm.Locus;
import edu.utah.med.genepi.gm.MarkerManager;
import edu.utah.med.genepi.gm.Repeat;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable.Column;
import edu.utah.med.genepi.io.ResourceResolver;
import edu.utah.med.genepi.io.XLoader;
import edu.utah.med.genepi.io.XUt;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedData.Printer;
import edu.utah.med.genepi.sim.GSimulator;
import edu.utah.med.genepi.sim.GSimulator.Drop;
import edu.utah.med.genepi.sim.GSimulator.Top;
import edu.utah.med.genepi.sim.XDropSim;
import edu.utah.med.genepi.sim.XTopSim;
import edu.utah.med.genepi.stat.CCStat;
import edu.utah.med.genepi.stat.StatInterface;
import edu.utah.med.genepi.util.GEException;
import edu.utah.med.genepi.util.Randy;
import edu.utah.med.genepi.util.Ut;

public class RgenProcessor implements Tags, Atts, Apps{
	
	private static final EntityResolver DTD_RESOLVER = new ResourceResolver("ge-rgen.dtd");
	private final Map<String, String> mParams = new TreeMap<String, String>();
	private static final String SPKG_GM = Ut.pkgOf(Repeat.class);
	private static final String SPKG_SIM = Ut.pkgOf(GSimulator.class);
	private static final String SPKG_STAT = Ut.pkgOf(CCStat.class);
	private static final String SPKG_IO = Ut.pkgOf(XLoader.class);
	private static final String OPT_DISABLER = ".";
	private File rgenFile = null;
	private Element edoc = null;
	
	public RgenProcessor( File f ) throws GEException
	{
		rgenFile = f;
	    edoc = new XLoader().loadDocumentElement(f,DTD_RESOLVER);
	    String nsURI = edoc.getNamespaceURI();

	    mParams.putAll(getGlobalAttributes(edoc));
	    mParams.putAll(getOptionalParameters(kidsOf(edoc,Tags.PARAM,nsURI)));
	    mParams.putAll(getLocusParameters(kidsOf(edoc,Tags.LOCUS,nsURI)));
	}
	
	//----------------------------------------------------------------------------
	public boolean loadNullData()
	{
		boolean loadNullData = false;
		if ( mParams.containsKey("loadNullData"))
		{
			if ( mParams.get("loadNullData").equals("true") ) loadNullData = true;
		}
		return loadNullData;
	}
	
	//----------------------------------------------------------------------------
	public boolean getPhaseTogether()
	{
		boolean together = false;
		if ( mParams.containsKey("phaseTogether") )
		{
			if ( mParams.get("phaseTogether").equals("true") ) together = true;
		}
		return together;
	}
	
	//----------------------------------------------------------------------------
	public boolean getCaseOnlyIntx()
	{
		boolean caseOnlyIntx = false;
		if ( mParams.containsKey("caseOnlyIntx"))
		{
			if ( mParams.get("caseOnlyIntx").equals("true") ) caseOnlyIntx = true;
		}
		return caseOnlyIntx;
	}
	
	//----------------------------------------------------------------------------
	public boolean getLogNullData()
	{
		boolean logNullData = false;
		if ( mParams.containsKey("logNullData"))
		{
			if ( mParams.get("logNullData").equals("true") ) logNullData = true;
		}
		return logNullData;		
	}
	
	//----------------------------------------------------------------------------
	public int getStartLogNullIndex()
	{
		int startIndex = 1;
		if ( mParams.containsKey("logNullStartIndex"))
		{
			startIndex = Integer.parseInt(mParams.get("logNullStartIndex"));
		}
		return startIndex;				
	}
	
	//----------------------------------------------------------------------------
	public String getPhaser()
	{ 
		String phaser = "gchap";
		if ( mParams.containsKey("phaser") ) phaser = mParams.get("phaser");
		return phaser;
	}
	
	//----------------------------------------------------------------------------
	public String getReportType(){ return mParams.get("report"); }
	
	//----------------------------------------------------------------------------
	public String getRgenFileName(){ return rgenFile.getPath(); }
	
	//----------------------------------------------------------------------------
	public String getoutpathStem(){ return Ut.stemOf(rgenFile); }
	
	//----------------------------------------------------------------------------
	public String getTopSample(){ return mParams.get("top-sample"); }
	
	//----------------------------------------------------------------------------	
	public String getRgenFilePath(){ return rgenFile.getPath(); }
	
	//----------------------------------------------------------------------------
	public int getNSims()
	{
		int nsims = Integer.parseInt(mParams.get(Atts.NSIMS));
		if ( getDistribution().equals("weightedindex") ) nsims = 0;
		return nsims;  
	}
	
	//----------------------------------------------------------------------------
	public AlleleFormat getAlleleFormat() throws GEException
	{
		AlleleFormat alleleformat = (AlleleFormat) Ut.newModule(SPKG_GM, "Allelebyte");;
		if ( mParams.containsKey(Atts.ALLELEFORMAT) )
		{
			String af = mParams.get(Atts.ALLELEFORMAT);
			if (!af.equals("numeric")) alleleformat = (AlleleFormat) Ut.newModule(SPKG_GM, "Allelechar");
		}		
		return alleleformat;
	}
	
	//----------------------------------------------------------------------------
	public int getNWeightedCycles()
	{
		int nsims = Integer.parseInt(mParams.get(Atts.NSIMS));
		return nsims;  
	}
	
	//----------------------------------------------------------------------------
	public int getRSeed() throws GEException
	{
		int newSeed = 0;
	    String rseed = mParams.get(Atts.RSEED);
	    if ( "random".equals(rseed.toLowerCase()) ) 
	      newSeed = new Random().nextInt(getNSims()*100);
	    else
	      newSeed = Integer.parseInt(rseed);

		Randy.create((long) newSeed);
	    return newSeed;
	}
	
	//----------------------------------------------------------------------------	
	public GSimulator.Top getTopSim() throws GEException
	{
		GSimulator.Top tSim = null;
		if( mParams.containsKey(Atts.TOP) ) tSim = (Top) Ut.newModule(SPKG_SIM, mParams.get(Atts.TOP));		
		if( getDistribution().equals("weightedindex")) tSim = (Top) Ut.newModule(SPKG_SIM,"IndivWtTopSim");
		return tSim;	
	}
	
	//----------------------------------------------------------------------------	
	public boolean phaseData()
	{
		boolean phase = false;
		if( mParams.containsKey(Atts.TOP) )
		{
			String tSim = mParams.get(Atts.TOP);
			if (tSim.contains("Hap") ) phase = true;
		}
		return phase;
	}
	
	//----------------------------------------------------------------------------	
	public GSimulator.Drop getDropSim() throws GEException
	{
		GSimulator.Drop dSim = null;
		if( mParams.containsKey(Atts.TOP) ) dSim = (Drop) Ut.newModule(SPKG_SIM, mParams.get(Atts.DROP));
		if ( getDistribution().equals("weightedindex")) dSim = (Drop) Ut.newModule(SPKG_SIM,"IndivWtDropSim");
		return dSim;	
	}
	
	//----------------------------------------------------------------------------
	public PedData.Printer getDataDumper() throws GEException
	{
		PedData.Printer dumper = null;
		if (mParams.containsKey(Apps.DUMPER)) dumper = (Printer) Ut.newModule(SPKG_IO, mParams.get(Apps.DUMPER));		
		return dumper;
	}
	
	//----------------------------------------------------------------------------	
	public String[][] getDataFiles() throws GEException
	{
		String nsURI = edoc.getNamespaceURI();
		NodeList dataFileList = kidsOf(edoc,Tags.DATAFILE,nsURI);
		// 0=stdname, 1=genodata, 2=hap, 3=quant, 4=par
		String[][] fileNames = new String[dataFileList.getLength()][5];
		for ( int i = 0; i < dataFileList.getLength(); i++ )
		{
			Element edatafile = (Element) dataFileList.item(i);
			fileNames[i][0] = XUt.stringAtt(edatafile, Atts.STUDYNAME);
			fileNames[i][1] = XUt.stringAtt(edatafile, Atts.GENOTYPEDATA);
			fileNames[i][2] = XUt.stringAtt(edatafile, Atts.HAPLOTYPE);
			fileNames[i][3] = XUt.stringAtt(edatafile, Atts.QUANTITATIVE);
			fileNames[i][4] = XUt.stringAtt(edatafile, Atts.LINKAGEPARAMETER);
			if ( fileNames[i][1] == null ) new GEException ("Pedigree file attribute is missing in .rgen file");
		}
		return fileNames;
	}
	
	//----------------------------------------------------------------------------
	public Gene[] getLoci() throws GEException
	{
		String nsURI = edoc.getNamespaceURI();
		NodeList nlloci = kidsOf(edoc,Tags.LOCUS,nsURI);
		Loci l = new Loci(nlloci.getLength());
		Map<Integer,List<Locus>> geneBuffer = new HashMap<Integer,List<Locus>>();
		
		for (int i = 0; i < nlloci.getLength(); ++i)
		{
			Element elocus = (Element) nlloci.item(i);
			//int id, String marker, double distance, 
            //boolean ordered, int gene
			int index = XUt.intAtt(elocus,Atts.ID);
			String marker = XUt.stringAtt(elocus,Atts.MARKER);
			double distance = XUt.doubleAtt(elocus,Atts.DIST);
			boolean ordered = true;
			int gene = XUt.intAtt(elocus,Atts.GENE);
			
			if ( !geneBuffer.containsKey(gene) ) geneBuffer.put(gene,new ArrayList<Locus>());
			List<Locus> locuslst = geneBuffer.get(gene);
			locuslst.add(new Locus(i,index,marker,distance,ordered,gene));
			geneBuffer.put(gene,locuslst);
		}
		
		int nGenes = geneBuffer.keySet().size();
		Gene[] genes = new Gene[nGenes];
		int iter = 0;
		for ( Iterator<Integer> it = geneBuffer.keySet().iterator(); it.hasNext(); )
		{
			int geneId = it.next();
			List<Locus> locuslst = geneBuffer.get(geneId);
			Locus[] markers = locuslst.toArray(new Locus[0]);
			genes[iter] = new Gene(geneId,markers);
			iter++;
		}
		return genes;
	}
	
	//----------------------------------------------------------------------------
	public String getDistSuffix()
	{
		String suffix = "";
		if ( getDistribution().equals("weightedindex") ) suffix = "Wt";
		return suffix;
	}
	
	//----------------------------------------------------------------------------
	public StatInterface getStats() throws GEException
	{
		List<String> sstats = scan_params(Apps.CCSTAT);
		List<String> mstats = scan_params(Apps.METASTAT);
		
		CCStat[] ccstats = new CCStat[sstats.size()];
		CCStat[] metastats = new CCStat[mstats.size()];
		
		for ( int i=0; i < ccstats.length; i++ ) ccstats[i] = (CCStat) Ut.newModule(SPKG_STAT,sstats.get(i),getDistSuffix());
		for ( int i=0; i < metastats.length; i++ ) metastats[i] = (CCStat) Ut.newModule(SPKG_STAT,mstats.get(i),getDistSuffix());
		
		StatInterface si = new StatInterface(ccstats,metastats);
		return si;
	}
	
	//----------------------------------------------------------------------------
	public Map<String,String> getHapConstructorParameters()
	{
		Map<String,String> mparams = new HashMap<String,String>();
		String key = Apps.HAPC;
				
		for ( Iterator<String> it = mParams.keySet().iterator(); it.hasNext(); )
		{
			String keyValue = it.next();
			if ( keyValue.split(key).length > 1 ) 
			{
				mparams.put(keyValue,mParams.get(keyValue));
			}
		}
		String[] startModels = getTableModels();
		if ( startModels.length > 0 ) mparams.put("hapc_startmodels",Ut.array2str(startModels,","));
		return mparams;		
	}
	
	//----------------------------------------------------------------------------	
	public String[] getTableModels()
	{
		String nsURI = edoc.getNamespaceURI();
		NodeList nltables = kidsOf(edoc,Tags.CCTABLE,nsURI);
		String[] models = new String[nltables.getLength()];
		for (int itable = 0; itable < nltables.getLength(); ++itable) 
		{
			Element etable = (Element) nltables.item(itable);
			models[itable] = etable.getAttribute(Atts.MODEL);
		}
		return models;
	}
	
	//----------------------------------------------------------------------------	
	public ExpandedAnalysis[] getRgenAnalyses( StatInterface statinterface, MarkerManager markermanager ) throws GEException
	{
		String nsURI = edoc.getNamespaceURI();
		NodeList nltables = kidsOf(edoc,Tags.CCTABLE,nsURI);
		CCStat[] stats = statinterface.getStats();
		CCStat[] metas = statinterface.getMetas();
		int nstats = stats.length;
		int nmetas = metas.length;
		int nloci = markermanager.getLociCount(-1);
		String[] tabletypes = getTableTypes();
		int ntabletype = tabletypes.length;

		RgenAnalysis[] rgenAnalyses = new RgenAnalysis[nltables.getLength()];
		
		for (int itable = 0; itable < nltables.getLength(); ++itable)
		{
			Element etable = (Element) nltables.item(itable);
			int[] itabletype = parseRefs(etable, Atts.TABLES, ntabletype);
			int[] iloci = parseRefs(etable, Atts.LOCI, nloci);
			int[] istats = parseRefs(etable, Atts.STATS, nstats);
			int[] imetas = parseRefs(etable, Atts.METAS, nmetas);
			
			String imodel = etable.getAttribute(Atts.MODEL);
			String itype = etable.getAttribute(Atts.TYPE).toLowerCase();
			NodeList nlcols = kidsOf(etable,Tags.COL,nsURI);
			int ncols = nlcols.getLength();
			
			AnalysisTable.Column[] cdefs = new AnalysisTable.Column[ncols];
			
			String irepeat = etable.getAttribute(Atts.REPEAT);
			int[][] repeatGroup;

			if ( itype.length() == 0 ) itype = "genotype";

			//check # loci in the table
			Element  colAtt = (Element) nlcols.item(0);
			NodeList gAtt = kidsOf(colAtt,Tags.G,nsURI);
			NodeList aAtt = kidsOf((Element) gAtt.item(0),Tags.A,nsURI);
			
			int groupSize = aAtt.getLength(); 
			Object repeat = Ut.newModule(SPKG_GM, irepeat);
			Repeat rp = (Repeat) repeat;
			repeatGroup = rp.getGroup(iloci,groupSize);
			iloci = repeatGroup[0];

			for (int icol = 0; icol < ncols; ++icol)
			{
				Element ecol = (Element) nlcols.item(icol);
				int wt = XUt.intAtt(ecol,Atts.WT);
				NodeList nlg = kidsOf(ecol,Tags.G,nsURI);
				List<GtypeMatcher> lgtmatchers = new ArrayList<GtypeMatcher>();

				for (int ig = 0, ng = nlg.getLength(); ig < ng; ++ig) {
					NodeList nla = kidsOf((Element) nlg.item(ig),Tags.A,nsURI);
					GtypeMatcher gtm = new GtypeMatcher(iloci);

					if (nla.getLength() != iloci.length)
						throw new GEException(
								elCountMsg(new String[] { Tags.CCTABLE, Tags.COL,
										Tags.G, Tags.A }, new int[] { itable,
										icol, ig, nla.getLength() },
										iloci.length));

					for (int ia = 0; ia < iloci.length; ++ia)
					{
						gtm.setRegex(ia,nla.item(ia).getFirstChild().getNodeValue().trim(),getAlleleFormat());
					}
					lgtmatchers.add(gtm);
				}
				cdefs[icol] = (Column) new RgenColumn(lgtmatchers, wt);
			}
			
			List<String> newTabletype = new ArrayList<String>();
			for ( int iitabletype = 0; iitabletype < itabletype.length; iitabletype++)
				newTabletype.add(tabletypes[itabletype[iitabletype]]);
			String[] selected_tabletype = newTabletype.toArray(new String[0]);
 
			ColumnManager cm = new ColumnManager(cdefs,itype);
			RgenAnalysis rA = new RgenAnalysis(cm,imodel,istats,imetas,repeatGroup,selected_tabletype);
			
			rgenAnalyses[itable] = rA;
		}
		return rgenAnalyses;		
	}
	
	//----------------------------------------------------------------------------	
	public String getMissingDataValue()
	{
		String missingDataValue = "0";
		if ( mParams.containsKey(Atts.MISSINGDATA)) missingDataValue = mParams.get(Atts.MISSINGDATA);
		return missingDataValue;
	}
	
	//----------------------------------------------------------------------------	
	public double getPercent(){ return Double.parseDouble(mParams.get(Atts.PERCENT)); }
	
	//----------------------------------------------------------------------------
	private NodeList kidsOf( Element e, String kid_elements_name, String nsURI )
	{
		return XUt.kidsOf(e,kid_elements_name,nsURI);
	}
	
	//----------------------------------------------------------------------------
	private Map<String, String> getGlobalAttributes(Element doc_elem)
	{
		Map<String, String> m = new HashMap<String, String>();
		NamedNodeMap atts = doc_elem.getAttributes();

		for (int i = 0; i < atts.getLength(); ++i)
		{
			m.put(atts.item(i).getNodeName(), atts.item(i).getNodeValue());
		}
		m.remove("xmlns" + ":" + doc_elem.getPrefix());

		return m;
	}

	//----------------------------------------------------------------------------
	private Map<String, String> getOptionalParameters(NodeList nlparams)
	{
		Map<String, String> m = new HashMap<String, String>();
		for (int i = 0; i < nlparams.getLength(); ++i)
		{
			Element e = (Element) nlparams.item(i);
			String  value = e.getFirstChild().getNodeValue().trim();
			if (!value.startsWith(OPT_DISABLER))
				m.put(e.getAttribute(Atts.NAME), value);
		}
		return m;
	}

	//----------------------------------------------------------------------------
//	private int[] getCovarParameters(NodeList covarparams)
//	{
//		int[] covars = new int[covarparams.getLength()];
//		for ( int i = 0 ; i < covarparams.getLength(); i++ )
//		{ 
//			Element e = (Element) covarparams.item(i);
//			covars[i] = Integer.parseInt(e.getAttribute(Atts.ID));
//		}
//		return covars;
//	}
	
	//----------------------------------------------------------------------------
	public int[] getCovarIDs()
	{
		List<String> covarIDs = scan_params(Apps.COVAR);
		int[] cids = new int[covarIDs.size()];
		for ( int i=0; i < cids.length; i++ ) cids[i] = Integer.parseInt(covarIDs.get(i));
		return cids;
	}
	
	//----------------------------------------------------------------------------
	public String[] getTableTypes() throws GEException
	{
		List<String> tableTypes = scan_params(Apps.TABLETYPE);
		if ( (getTopSim() instanceof XTopSim) && (getDropSim() instanceof XDropSim ))
		{
			if ( !tableTypes.contains("x-chr") ) tableTypes.add("x-chr"); 
		}
		if ( tableTypes.size() == 0 ) tableTypes.add("original");
		String[] tts = tableTypes.toArray(new String[0]);
		return tts;
	}
	
	//----------------------------------------------------------------------------
	private List<String> scan_params( String key )
	{
		// Get values in mParams dictionary that have keys that contain value passed in.
		List<String> lparams = new ArrayList<String>();
		for ( Iterator<String> it = mParams.keySet().iterator(); it.hasNext(); )
		{
			String keyValue = it.next();
			if ( keyValue.split(key).length > 1 ) lparams.add(mParams.get(keyValue));
		}
		return lparams;
	}
	
	//----------------------------------------------------------------------------
	public String getDistribution()
	{
		String distribution = null;
		if ( mParams.containsKey(Atts.DISTRIBUTION) ) distribution = mParams.get(Atts.DISTRIBUTION).toLowerCase();
		return distribution;
	}

	//----------------------------------------------------------------------------
	private Map<String, String> getLocusParameters(NodeList nlparams)
	{
		Map<String, String> m = new HashMap<String, String>();
		for (int i = 0; i < nlparams.getLength(); ++i)
		{
			Element e = (Element) nlparams.item(i);
			if ( e.getAttribute(Atts.MARKER).length() == 0 )
				m.put("locus "+(i+1), e.getAttribute(Atts.ID));
			else
				m.put("locus "+(i+1), e.getAttribute(Atts.MARKER));
		}
		return m;
	}
	
	//----------------------------------------------------------------------------
	private int[] parseRefs(Element e, String attr_name, int nobjs)
	throws GEException
	{
		int[] ordinals = XUt.intsAtt(e, attr_name);
		if (ordinals != null) // if attribute specifying object subset present
			return Ut.mapAdd(ordinals, -1); // change indexing base from 1 to 0
		return Ut.identityArray(nobjs);   // otherwise reference all objects
	}
	
	//----------------------------------------------------------------------------
	private String elCountMsg(String[] names, int[] indices, int expect)
	{
		List<String> l = new ArrayList<String>();
		for (int i = 0; i < names.length; ++i)
			l.add(names[i] + "[" + (indices[i] + 1));
		return expect + " elements expected ( " + Ut.join(l, " > ") + " )";
	}
}

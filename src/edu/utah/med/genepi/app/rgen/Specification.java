//******************************************************************************
// Specification.java
//******************************************************************************
package edu.utah.med.genepi.app.rgen;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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

import edu.utah.med.genepi.gm.AlleleFormat;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.gm.GDefBuilder;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.GtypeMatcher;
import edu.utah.med.genepi.gm.Repeat;
import edu.utah.med.genepi.io.ResourceResolver;
import edu.utah.med.genepi.io.XLoader;
import edu.utah.med.genepi.io.XUt;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.sim.GSimulator;
import edu.utah.med.genepi.sim.XDropSim;
import edu.utah.med.genepi.sim.XTopSim;
import edu.utah.med.genepi.stat.CCAnalysis;
import edu.utah.med.genepi.stat.CCStat;
import edu.utah.med.genepi.util.GEException;
import edu.utah.med.genepi.util.Randy;
import edu.utah.med.genepi.util.Ut;
//d

//==============================================================================
/** Encapsulates the contents of an *.rgen specification file (an XML
 *  application).  It creates the objects needed by {@link PedGenie},
 *  according to the info found in the file.
 */
public class Specification {

	interface Tag 
	{
		String DATAFILE = "datafile";
		String LOCUS    = "locus";
		String PARAM    = "param";
		String CCTABLE  = "cctable";
		String COL      = "col";
		String G        = "g";
		String A        = "a";
	}
	interface Att {
		String NS               = "xmlns:ge";
		String NSIMS            = "nsims";
		String RSEED            = "rseed";
		String TOP              = "top";
		String DROP             = "drop";
		String REPORT           = "report";
		String PERCENT          = "percent";
		String NAME             = "name";
		String ID               = "id";
		String DIST             = "dist";
		String MARKER           = "marker";
		String ORDERED          = "ordered";
		String GENE		    = "gene";
		String LOCI             = "loci";
		String REPEAT           = "repeat";
		String STATS            = "stats";
		String METAS            = "metas";
		String MODEL            = "model";
		String TABLES	    	= "tables";
		String TYPE             = "type";
		String WT               = "wt";
		String STUDYNAME	    = "studyname";
		String GENOTYPEDATA     = "genotypedata";
		String QUANTITATIVE     = "quantitative";
		String HAPLOTYPE        = "haplotype";
		String LINKAGEPARAMETER = "linkageparameter";
		String ALLELEFORMAT	    = "alleleformat";
		String MISSINGDATA	    = "missingdata";
		String DISTRIBUTION	    = "distribution";
	}
	interface PName {
		String CCSTAT    = "ccstat";
		String DUMPER    = "dumper";
		String METASTAT  = "metastat";
		//String QUANTFILE = "quantfile";
		String COVAR     = "covar";
		String TABLETYPE = "tabletype";
		String HAPC	     = "hapc";
		String INTX = "interaction";
	}

	private static class ColumnImp implements CCAnalysis.Table.Column {
		private final GtypeMatcher[] gtMatchers;
		private final int            theWt;
		//private final String 	 theType;
		private ColumnImp( List<GtypeMatcher> gt_matchers,  int weight )
		{
			gtMatchers = gt_matchers.toArray(new GtypeMatcher[0]);
			theWt = weight;
		}
		// pass each allele separately for type = Allele
		public int subsumesAtype(Gtype gt, boolean first) 
		{
			for ( int j = 0; j < gtMatchers.length; j++ )
			{
				if ( gtMatchers[j].matchesGtype(gt, first) )
					return 1;
			}
			return 0;
		}

		public int subsumesGtype(Gtype gt)
		{
			for (int j = 0; j < gtMatchers.length; ++j)
			{
				if ( gtMatchers[j].matchesGtype(gt) )
					return 1;
			}
			return 0;
		}

		public int getWeight() { return theWt; }
		public GtypeMatcher[] getGtypeMatcher() { return gtMatchers; }
		//public String getType() { return theType; }
	}

	private static final EntityResolver DTD_RESOLVER = new ResourceResolver(
			"ge-rgen.dtd"
	);
	private static final String SPKG_GM = Ut.pkgOf(Repeat.class);
	private static final String SPKG_SIM = Ut.pkgOf(GSimulator.class);
	private static final String SPKG_STAT = Ut.pkgOf(CCStat.class);
	private static final String SPKG_IO = Ut.pkgOf(XLoader.class);
	private static final String OPT_DISABLER = ".";

	private final String              nsURI;
	private final String              specName;
	private final Map<String, String> mParams    = new TreeMap<String, String>();
	private final GSimulator.Top      topSim;
	private final GSimulator.Drop     dropSim;
	private final GDef                gDef;
	private final AlleleFormat	    alleleformat;
	private final List<Object>        lStats     = new ArrayList<Object>();
	private final List<Object>        lMetas     = new ArrayList<Object>();
	private List<CCAnalysis>          lAnalyses  = new ArrayList<CCAnalysis>();
	private final List<String>        lcovarIDs  = new ArrayList<String>();
	private final List<String>        ltabletype = new ArrayList<String>();
	private       Study[]             study;
	private final PedData.Printer     dataDumper;
	public  int[]     	            covarIDs  = null;
	public  String[]     	            tabletype  = null;
	public  int 		            newSeed;
	public  final int                 percent = 50;
	private final String 	            app_id;
	private Map<String, Object>       hapC_params = new HashMap<String, Object>();
	private Map<String,String> intx_params = new HashMap<String,String>();
	//public static File   	    quantfile; 
	private boolean		    header = true;
	private String      		    missingdata;
	private String 		    distribution;
	private String		    distSuffix;
	private int 			    nSims;
	private int 			    nWeightedCycle = 0;

	//----------------------------------------------------------------------------
	Specification(File f, String appid, Map<String, String> prior_globals)
	throws GEException
	{
		app_id = appid;
		specName = f.getPath();

		//Ryan added to setup default params for hapBuilder
		hapC_params.put("hapc_sigtesting", false);
		hapC_params.put("hapc_sigtesting_only", "false,0");
		hapC_params.put("hapc_loadnulls", false);
		hapC_params.put("hapc_backsets", true);
		//hapC_params.put("hapc_threshold", "0.1,0.05,0.01,0.005,0.001");
		hapC_params.put("hapc_threshold", "0.1,0.05,0.005,0.0005");
		hapC_params.put("hapc_models", "HAdd,HDom,HRec,MG");
		hapC_params.put("hapc_check_mostsignificant", "true");

		Element edoc = new XLoader().loadDocumentElement(f, DTD_RESOLVER);
		nsURI = edoc.getNamespaceURI();

		mParams.putAll(getGlobalAttributes(edoc));
		mParams.putAll(getOptionalParameters(kidsOf(edoc, Tag.PARAM)));
		mParams.putAll(getLocusParameters(kidsOf(edoc, Tag.LOCUS)));
		mParams.putAll(prior_globals);

		nSims = Integer.parseInt(mParams.get(Att.NSIMS));
		// modified for random seed 
		String rseed = mParams.get(Att.RSEED);
		if ( "random".equals(rseed.toLowerCase()) )  
			newSeed = new Random().nextInt(nSims * 100);
		else
			newSeed = Integer.parseInt(rseed);
		Randy.create((long) newSeed);

		distribution = mParams.get(Att.DISTRIBUTION).toLowerCase();
		nWeightedCycle = nSims;
		if ( distribution.equals("weightedindex") )
		{
			distSuffix = "Wt";
			nSims = 0;
			mParams.put(Att.TOP, "IndivWtTopSim");
			mParams.put(Att.DROP, "IndivWtDropSim");
		}

		Object top = null, drop = null, dumper = null, 
		topAtt = null, alformat = null;
		int percent = 0;

		for (Iterator i = mParams.keySet().iterator(); i.hasNext();)
		{
			String name = ((String) i.next()).toLowerCase();
			//if (name.equals(Att.TOP) && !(app_id.toLowerCase().equals("hapmc")))
			if (name.equals(Att.TOP))
				top = Ut.newModule(SPKG_SIM, mParams.get(name));
			//else if (name.equals(Att.DROP) && !(app_id.toLowerCase().equals("hapmc")))
			else if (name.equals(Att.DROP))
			{
				//System.out.println("drop : " + mParams.get(name));
				drop = Ut.newModule(SPKG_SIM, mParams.get(name));
			}
			else if (name.equals(Att.PERCENT))
				percent = new Integer(mParams.get(name)).intValue();
			// attach ResultImp to each statistic and metastat
			else if (name.startsWith(PName.CCSTAT))
				lStats.add(Ut.newModule(SPKG_STAT, mParams.get(name), distSuffix));
			else if (name.startsWith(PName.METASTAT))
				lMetas.add(Ut.newModule(SPKG_STAT, mParams.get(name), distSuffix));
			else if (name.equals(PName.DUMPER))
				dumper = Ut.newModule(SPKG_IO, mParams.get(name));
			else if (name.equals(Att.ALLELEFORMAT))
			{
				String af = mParams.get(name);
				if (af.equals("numeric"))
					alformat = Ut.newModule(SPKG_GM, "Allelebyte");
				else 
					alformat = Ut.newModule(SPKG_GM, "Allelechar"); 
			}
			else if (name.equals(Att.MISSINGDATA))
				missingdata = mParams.get(name);
			//Ryan added 08-16-07 to extract parameters for HapBuilder
			//1. Boolean value to do complete significant testing - default is no (0).
			//2. Boolean value to perform backsets - default is yes (1).
			//3. Threshold list - default is 0.1
			else if (name.startsWith(PName.HAPC)){
				if(name.equals("hapc_threshold")){
					hapC_params.put(name, mParams.get(name));
				}
				else{
					hapC_params.put(name, mParams.get(name));
				}
			}
			else if (name.startsWith(PName.INTX)) 
			{
				intx_params.put(name,mParams.get(name));
			}
			else if (name.startsWith(PName.COVAR))
			{
				lcovarIDs.add(mParams.get(name));
			}
			else if (name.startsWith(PName.TABLETYPE))
				ltabletype.add(mParams.get(name));
		}

		topSim = (GSimulator.Top) top;
		dropSim = (GSimulator.Drop) drop;
		alleleformat = (AlleleFormat) alformat;
		dataDumper = (PedData.Printer) dumper;

		String[] covarids = lcovarIDs.toArray(new String[0]);
		covarIDs = new int[covarids.length];
		for ( int i = 0; i < covarids.length; i++)
			covarIDs[i] = Integer.parseInt(covarids[i]);

		if ( (topSim instanceof XTopSim) && (dropSim instanceof XDropSim ))
		{
			if ( !ltabletype.contains("x-chr") )
				ltabletype.add("x-chr");
		}
		if ( ltabletype.size() > 0 )
			tabletype = ltabletype.toArray(new String[0]);
		else
		{
			tabletype = new String[1];
			tabletype[0] = new String("original");
		}
		//covarIDs = getCovarParameters(kidsOf(edoc, Tag.COVAR));
		NodeList datafilelist = kidsOf(edoc, Tag.DATAFILE);
		buildStudy(datafilelist);
		gDef = buildGDef(kidsOf(edoc, Tag.LOCUS));
		gDef.setAlleleFormat(alleleformat);
		alleleformat.setMissingData(missingdata);
		buildAnalysisDefs(kidsOf(edoc, Tag.CCTABLE));
	}

	//----------------------------------------------------------------------------
	String getName() { return specName; }

	//----------------------------------------------------------------------------
	public GSimulator.Top getTopSim() { return topSim; }

	//----------------------------------------------------------------------------
	public GSimulator.Drop getDropSim() { return dropSim; }

	//----------------------------------------------------------------------------
	public GDef getGDef() { return gDef; }

	//----------------------------------------------------------------------------
	CCAnalysis[] getCCAnalyses()
	{
		return lAnalyses.toArray(new CCAnalysis[0]);
	}

	//----------------------------------------------------------------------------
	PedData.Printer getDataDumper() { return dataDumper; }

	//----------------------------------------------------------------------------
	public int getNumberOfSimulations()
	{
		//return Integer.parseInt(mParams.get(Att.NSIMS));
		return nSims;
	}

	//----------------------------------------------------------------------------
	public Map getAllGlobalParameters()
	{
		return Collections.unmodifiableMap(mParams);
	}

	//----------------------------------------------------------------------------
	//Ryan 06-12-07 changed to public for analysisSet to access this method.
	public CCStat[] getCCStats()
	{
		return lStats.toArray(new CCStat[0]);
	}

	//----------------------------------------------------------------------------
	//Ryan 1-04-08 changed to public for analysisSet to access this method.
	public String[] gethapB_models(){
		String[] models = ((String) hapC_params.get("hapc_models")).split(",");
		return models;
	}

	//----------------------------------------------------------------------------
	public CCStat[] getMetaStats()
	{
		return lMetas.toArray(new CCStat[0]);
	}

	//----------------------------------------------------------------------------
	public int getRseed()
	{
		return newSeed;
	}

	//----------------------------------------------------------------------------
	private Map<String, String> getGlobalAttributes(Element doc_elem)
	{
		Map<String, String>          m = new HashMap<String, String>();
		NamedNodeMap atts = doc_elem.getAttributes();

		for (int i = 0; i < atts.getLength(); ++i)
			m.put(atts.item(i).getNodeName(), atts.item(i).getNodeValue());
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
				m.put(e.getAttribute(Att.NAME), value);
		}
		return m;
	}

	//----------------------------------------------------------------------------
	private int[] getCovarParameters(NodeList covarparams)
	{
		int[] covars = new int[covarparams.getLength()];
		for ( int i = 0 ; i < covarparams.getLength(); i++ )
		{ 
			Element e = (Element) covarparams.item(i);
			covars[i] = Integer.parseInt(e.getAttribute(Att.ID));
		}
		return covars;
	}

	//----------------------------------------------------------------------------
	private Map<String, String> getLocusParameters(NodeList nlparams)
	{
		Map<String, String> m = new HashMap<String, String>();
		for (int i = 0; i < nlparams.getLength(); ++i)
		{
			Element e = (Element) nlparams.item(i);
			if ( e.getAttribute(Att.MARKER).length() == 0 )
				m.put("locus "+(i+1), e.getAttribute(Att.ID));
			else
				m.put("locus "+(i+1), e.getAttribute(Att.MARKER));
		}
		return m;
	}

	//----------------------------------------------------------------------------
	private void buildStudy(NodeList ndatafile) throws GEException
	{
		study = new Study[ndatafile.getLength()];
		for ( int i = 0; i < ndatafile.getLength(); i++ )
		{
			String stdname, genodata, hap, quant, par = null;
			Element edatafile = (Element) ndatafile.item(i);
			stdname  = XUt.stringAtt(edatafile, Att.STUDYNAME);
			genodata = XUt.stringAtt(edatafile, Att.GENOTYPEDATA);
			hap      = XUt.stringAtt(edatafile, Att.HAPLOTYPE);
			quant    = XUt.stringAtt(edatafile, Att.QUANTITATIVE);
			par      = XUt.stringAtt(edatafile, Att.LINKAGEPARAMETER);
			if ( genodata == null )
				new GEException ("Pedigree file attribute is missing in .rgen file");
			study[i] = new Study(stdname, genodata, quant, hap, par);
		}
	}
	//----------------------------------------------------------------------------
	private GDef buildGDef(NodeList nlloci) throws GEException
	{
		GDefBuilder gdb = new GDefBuilder();
		for (int i = 0; i < nlloci.getLength(); ++i)
		{
			Element elocus = (Element) nlloci.item(i);
			//  XUt.intAtt(elocus, Att.ID), XUt.doubleAtt(elocus, Att.DIST), true
			gdb.addLocus(
					XUt.intAtt(elocus, Att.ID), XUt.stringAtt(elocus, Att.MARKER), 
					XUt.doubleAtt(elocus, Att.DIST), true,
					XUt.intAtt(elocus, Att.GENE)
			);
		}

		return gdb.build();
	}

	//----------------------------------------------------------------------------
	private void buildAnalysisDefs(NodeList nltables) throws GEException
	{
		CCStat[] stats = getCCStats();
		CCStat[] metas = getMetaStats();
		int      nstats = stats.length;
		int      nmetas = metas.length;
		GDef     gdef   = getGDef();
		int      nloci  = gdef.getLocusCount();
		int      ntabletype = (getTableType()).length;

		for (int itable = 0; itable < nltables.getLength(); ++itable)
		{
			Element                   etable = (Element) nltables.item(itable);
			int[]                     itabletype = parseRefs(etable, Att.TABLES, ntabletype);
			int[]                     iloci = parseRefs(etable, Att.LOCI, nloci);
			int[]                     istats = parseRefs(etable, Att.STATS, nstats);
			int[]                     imetas = parseRefs(etable, Att.METAS, nmetas);
			String 		        imodel = etable.getAttribute(Att.MODEL);
			String 		        itype  = etable.getAttribute(Att.TYPE).toLowerCase();
			NodeList                  nlcols = kidsOf(etable, Tag.COL);
			int                       ncols = nlcols.getLength();
			CCAnalysis.Table.Column[] cdefs = new CCAnalysis.Table.Column[ncols];
			//List<String>        	lmarker = new ArrayList<String>();
			String			irepeat = etable.getAttribute(Att.REPEAT); 
			int[][] 			repeatGroup;

			if ( itype.length() == 0 )
				itype = "genotype";

			//check # loci in the table
			Element  colAtt = (Element) nlcols.item(0);
			NodeList gAtt = kidsOf(colAtt, Tag.G);
			NodeList aAtt = kidsOf((Element) gAtt.item(0), Tag.A);
			int groupSize = aAtt.getLength(); 
			Object repeat = Ut.newModule(SPKG_GM, irepeat);
			//RepeatLoci rp = new RepeatLoci();
			Repeat rp = (Repeat) repeat;
			repeatGroup = rp.getGroup(iloci, groupSize);
			iloci = repeatGroup[0];

			for (int icol = 0; icol < ncols; ++icol)
			{
				Element                   ecol = (Element) nlcols.item(icol);
				int                         wt = XUt.intAtt(ecol, Att.WT);
				NodeList                   nlg = kidsOf(ecol, Tag.G);
				List<GtypeMatcher> lgtmatchers = new ArrayList<GtypeMatcher>();

				for (int ig = 0, ng = nlg.getLength(); ig < ng; ++ig)
				{
					NodeList     nla = kidsOf((Element) nlg.item(ig), Tag.A);
					GtypeMatcher gtm = new GtypeMatcher(iloci);

					if (nla.getLength() != iloci.length)
						throw new GEException(
								elCountMsg(
										new String[] {Tag.CCTABLE, Tag.COL, Tag.G, Tag.A},
										new int[] {itable, icol, ig, nla.getLength()},
										iloci.length
								)
						);

					for (int ia = 0; ia < iloci.length; ++ia)
					{
						gtm.setRegex( ia,
								nla.item(ia).getFirstChild().getNodeValue().trim(),
								alleleformat );
					}

					lgtmatchers.add(gtm);
				}

				//cdefs[icol] = new ColumnImp(lgtmatchers, wt, itype);
				cdefs[icol] = new ColumnImp(lgtmatchers, wt);
			}

			List<CCStat>	newStats = new ArrayList<CCStat>();
			for (int iistat = 0; iistat < istats.length; ++iistat)
				newStats.add(stats[istats[iistat]]);
			CCStat[] selected_stats = newStats.toArray(new CCStat[0]);

			List<CCStat>      newMetas = new ArrayList<CCStat>();
			for (int iimeta = 0; iimeta < imetas.length; ++iimeta)
				newMetas.add(metas[imetas[iimeta]]);
			CCStat[] selected_metas = newMetas.toArray(new CCStat[0]);

			List<String>	newTabletype = new ArrayList<String>();
			for ( int iitabletype = 0; iitabletype < itabletype.length; iitabletype++)
				newTabletype.add(tabletype[itabletype[iitabletype]]);
			String[] selected_tabletype = newTabletype.toArray(new String[0]);

			//lAnalyses.add(new CCAnalysis(itable, cdefs, markers, imodel, 
			//lAnalyses.add(new CCAnalysis(cdefs, markers, imodel, selected_stats,
			//11-28-07 removed markers, no fix markername for repeat process
			lAnalyses.add(new CCAnalysis(cdefs, imodel, selected_stats,
					selected_metas, itype, covarIDs,
					percent, repeatGroup, selected_tabletype,
					app_id));
		}
	}

	//----------------------------------------------------------------------------
	public void updateAnalyses(List<CCAnalysis> newAnalyses){
		lAnalyses.clear();
		for(int i=0;i<newAnalyses.size();i++)
		{
			lAnalyses.add(newAnalyses.get(i));
		}
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
	//moved this method to util.Ut
	//private static Object newModule(String sdefpkg, String classname)
	//public static Object newModule(String sdefpkg, String classname)
	//throws GEException
	// {
	//  try {
	//    return Class.forName(
	//      classname.indexOf('.') == -1 ? (sdefpkg + "." + classname) : classname
	//    ).newInstance();
	//  } catch (Exception e) {
	//    throw new GEException("Can't get class instance: ", e);
	//  }
	//}

	//----------------------------------------------------------------------------
	private String elCountMsg(String[] names, int[] indices, int expect)
	{
		List<String> l = new ArrayList<String>();
		for (int i = 0; i < names.length; ++i)
			l.add(names[i] + "[" + (indices[i] + 1));
		return expect + " elements expected ( " + Ut.join(l, " > ") + " )";
	}

	//----------------------------------------------------------------------------
	private NodeList kidsOf(Element e, String kid_elements_name)
	{
		return XUt.kidsOf(e, kid_elements_name, nsURI);
	}
	//----------------------------------------------------------------------------
	//public File getQuantfile ()
	//{
	//   return quantfile;
	//}
	//----------------------------------------------------------------------------
	public Study[] getStudy()
	{
		if ( study.length > 0 )
			return study;
		else return null;
	}

	//----------------------------------------------------------------------------
	public int[] getCovarIds()
	{ return covarIDs; }

	//----------------------------------------------------------------------------
	public String[] getTableType()
	{ return tabletype; }

	//----------------------------------------------------------------------------
	public boolean hasHeader()
	{ return header; }

	//----------------------------------------------------------------------------
	public String getDistribution()
	{ return distribution; }

	//----------------------------------------------------------------------------
	public int getWeightedCycle()
	{ return nWeightedCycle; }

	//----------------------------------------------------------------------------
	//Ryan 06-12-07 created to create a new table column in analysisSet class
	//public ColumnImp createNewColumn(List<GtypeMatcher> lgtmatchers, int wt, String itype){
	//return new ColumnImp(lgtmatchers, wt, itype);
	public ColumnImp createNewColumn(List<GtypeMatcher> lgtmatchers, int wt){
		return new ColumnImp(lgtmatchers, wt);
	}

	//----------------------------------------------------------------------------
	//Ryan 06-12-07 created to obtain percent variable for analysisSet class
	public int getPercent(){
		return percent;
	}

	//----------------------------------------------------------------------------
	//Ryan 08-16-07, added gethapB functions to allow user specified parameters
	//for hapBuilder package. The parameters are:
	//1. significance testing - establish the significance of the loci sets from hapBuilder
	//2. backset testing - go foward and backward or just forward
	//3. threshold - the different thresholds for the different steps.
	public boolean gethapC_sigtesting(){
		if(hapC_params.get("hapc_sigtesting").equals("true"))
			return true;
		else
			return false;
	}

	//----------------------------------------------------------------------------
	public boolean gethapC_backsets(){
		if(hapC_params.get("hapc_backsets").equals("true"))
			return true;
		else
			return false;
	}

	//----------------------------------------------------------------------------
	public double[] gethapC_threshold(){
		String[] thold = ((String) hapC_params.get("hapc_threshold")).split(",");
		double[] m = {};
		for(int i=0; i < thold.length; i++){
			m[i] = Double.parseDouble(thold[i]);
		}
		return m;
	}

	//----------------------------------------------------------------------------
	// added 2-25-08
	public double[] gethapC_screenthreshold()
	{
		String[] thold = ((String) hapC_params.get("hapc_screenthreshold")).split(",");
		double[] m = {};
		for(int i=0; i < thold.length; i++){
			m[i] = Double.parseDouble(thold[i]);
		}
		return m;
	}
	//----------------------------------------------------------------------------
	public String[] gethapC_models(){
		String[] models = ((String) hapC_params.get("hapc_models")).split(",");
		return models;
	}

	//----------------------------------------------------------------------------
	public int gethapC_screen()
	{
		if (hapC_params.containsKey("hapc_screen"))
		{
			if ( hapC_params.get("hapc_screen").equals("true") )
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return 0;
		}
	}

	//----------------------------------------------------------------------------
	public boolean gethapC_sigtesting_only(){
		if(((String)hapC_params.get("hapc_sigtesting_only")).split(",")[0].equals("true"))
			return true;
		else
			return false;
	}

	//----------------------------------------------------------------------------
	public int gethapC_sigtesting_start(){
		int sigrun = 0;
		sigrun = (int) Integer.parseInt(((String) hapC_params.get("hapc_sigtesting_only")).split(",")[1]);
		return sigrun;
	}  

	//----------------------------------------------------------------------------
	public boolean gethapC_loadnulls(){
		if(hapC_params.get("hapc_loadnulls").equals("true"))
			return true;
		else
			return false;
	} 

	//----------------------------------------------------------------------------
	public boolean gethapC_check_mostsignificant()
	{
		if(hapC_params.get("hapc_check_mostsignificant").equals("true"))
			return true;
		else
			return false;
	}

	// ----------------------------------------------------------------------------
	public Map<String,String> get_intx_params(){ return intx_params; }
}


package edu.utah.med.genepi.ped;

public class Individual {

	private String pedid,indid,paid,maid;
	private int ipedid = -1;
	private int iindid = -1;
	private int ipaid = 0;
	private int imaid = 0;
	private int sex = -1;
	private int storageAddress = -1;
	private int dataIndex = -1;
	private int analysisIndex = -1;
	
	public Individual( String pid, String iid, String father_id, String mother_id, int s, int liab, int loc )
	{
		pedid = pid;
		indid = iid;
		paid = father_id;
		maid = mother_id;
		storageAddress = loc;
		iindid = storageAddress;
		sex = s;
	}
	
	//----------------------------------------------------------------------------
	public String getIndId(){ return indid; }
	
	//----------------------------------------------------------------------------
	public void setDataIndex( int index ){ dataIndex = index; }
	
	//----------------------------------------------------------------------------
	public void setAnalysisIndex( int index ){ analysisIndex = index; }
	
	//----------------------------------------------------------------------------
	public int getDataIndex(){ return dataIndex; }
	
	//----------------------------------------------------------------------------
	public int getIndIndex(){ return iindid; }
	
	//----------------------------------------------------------------------------
	public int getPedIndex(){ return ipedid; }
	
	//----------------------------------------------------------------------------
	public void setPedIndex( int pedIndex ){ ipedid = pedIndex; }
	
	//----------------------------------------------------------------------------
	public int getPaIndex(){ return ipaid; }
	
	//----------------------------------------------------------------------------
	public int getMaIndex(){ return imaid; }
	
	//----------------------------------------------------------------------------
	public int getSex(){ return sex; }
	
	//----------------------------------------------------------------------------
	public void setPaIndex( int paIndex ){ ipaid = paIndex; }
	
	//----------------------------------------------------------------------------
	public void setMaIndex( int maIndex ){ imaid = maIndex; }
	
	//----------------------------------------------------------------------------
	public boolean isFounder()
	{
		if ( paid.equals("0") && maid.equals("0") ) return true;
		else return false;
	}
	
	//----------------------------------------------------------------------------
	public int getStorageIndex(){ return storageAddress; }
	
	//----------------------------------------------------------------------------
	public String getPedId(){ return pedid; }
	
	//----------------------------------------------------------------------------
	public String getPaId(){ return paid; }
	
	//----------------------------------------------------------------------------
	public String getMaId(){ return maid; }
}

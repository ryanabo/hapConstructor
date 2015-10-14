package edu.utah.med.genepi.stat;

public class StatInterface {
	
	CCStat[] stats = null;
	CCStat[] metas = null;
	
	public StatInterface( CCStat[] cs, CCStat[] ms )
	{
		stats = cs;
		metas = ms;
	}
	
	public CCStat[] getStats(){ return stats; }
	
	public CCStat[] getMetas(){ return metas; }
	
	public CCStat getStat( int statIndex, boolean isMeta )
	{
		return isMeta ? metas[statIndex] : stats[statIndex];
	}
}

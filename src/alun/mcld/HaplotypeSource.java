package alun.mcld;

import java.util.Collection;

public interface HaplotypeSource
{
	public Collection<Locus> getLoci();
	public int nLoci();
	public int nAlleles(int j);
	public boolean update(LDModel m);
	public boolean maximize(LDModel m);
}

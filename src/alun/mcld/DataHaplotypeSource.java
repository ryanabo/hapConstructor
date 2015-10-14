package alun.mcld;

public interface DataHaplotypeSource extends HaplotypeSource
{
	public int nHaplotypes();
	public int[] getHaplotype(int i);
	public int getAllele(int i, int j);
}

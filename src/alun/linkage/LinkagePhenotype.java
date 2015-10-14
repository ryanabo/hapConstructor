package alun.linkage;

import alun.util.StringFormatter;

/**
 This is a base class from with the linkage phenotype classes
 are derived.
*/
abstract public class LinkagePhenotype
{
/**
 Sets the locus associated with this phenotype.
*/
	public void setLocus(LinkageLocus l)
	{
		this.l = l;
	}

/**
 Gets the locus associated with this phenotype.
*/
	public LinkageLocus getLocus()
	{
		return l;
	}

	abstract public LinkagePhenotype nullCopy();

// Protected data.

	protected static final StringFormatter f = new StringFormatter();

// Private data.

	private LinkageLocus l = null;
}

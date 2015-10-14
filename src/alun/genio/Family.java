package alun.genio;

import java.util.LinkedHashSet;
import java.util.Set;

public class Family
{
	public Family()
	{
		k = new LinkedHashSet<Object>();
	}

	public Object getPa()
	{
		return p;
	}

	public Object getMa()
	{
		return m;
	}

	public Object[] getKids()
	{
		return k.toArray();	
	}

	public int nKids()
	{
		return k.size();
	}

	public void setPa(Object a)
	{
		p = a;
	}

	public void setMa(Object a)
	{
		m = a;
	}

	public void addKid(Object a)
	{
		k.add(a);
	}

	public void removeKid(Object a)
	{
		k.remove(a);
	}

	private Object p = null;
	private Object m = null;
	private Set<Object> k = null;
}

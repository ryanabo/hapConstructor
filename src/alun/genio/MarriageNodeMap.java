package alun.genio;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import alun.graph.DirectedNetwork;
import alun.viewgraph.Arrow;
import alun.viewgraph.LabelledBlob;
import alun.viewgraph.Line;
import alun.viewgraph.Mappable;

public class MarriageNodeMap extends DirectedNetwork<Mappable,Line>
{
	public MarriageNodeMap(Pedigree p)
	{
		Map<Object,Mappable> h = new LinkedHashMap<Object,Mappable>();
		Set<Object> males = new LinkedHashSet<Object>();
		Set<Object> females = new LinkedHashSet<Object>();
		Set<Object> unk = new LinkedHashSet<Object>();

		Family[] f = p.nuclearFamilies();


		for (Object x : p.individuals())
			unk.add(x);
/*
		for (int i=0; i<f.length; i++)
		{
			Object[] kids = f[i].getKids();
			for (int j=0; j<kids.length; j++)
				unk.add(kids[j]);
		}
*/

		for (int i=0; i<f.length; i++)
		{
			unk.remove(f[i].getPa());
			unk.remove(f[i].getMa());
			males.add(f[i].getPa());
			females.add(f[i].getMa());
		}

		for (Iterator<Object> i = males.iterator(); i.hasNext(); )
		{
			Object x = i.next();
			h.put(x,male(x));
		}
		
		for (Iterator<Object> i = females.iterator(); i.hasNext(); )
		{
			Object x = i.next();
			h.put(x,female(x));
		}

		for (Iterator<Object> i = unk.iterator(); i.hasNext(); )
		{
			Object x = i.next();
			h.put(x,unknown(x));
		} 

		for (Object x : unk)
			add(h.get(x));

		for (int i=0; i<f.length; i++)
		{
			Mappable mar = marriage();
			connect(h.get(f[i].getPa()),mar,new Arrow(h.get(f[i].getPa()),mar));
			connect(h.get(f[i].getMa()),mar, new Arrow(h.get(f[i].getMa()),mar));
			Object[] kids = f[i].getKids();
			for (int j=0; j<kids.length; j++)
				connect(mar,h.get(kids[j]), new Arrow(mar,h.get(kids[j])));
			
		}
	}

	public Mappable unknown(Object c)
	{
		LabelledBlob b = new LabelledBlob(c.toString());
		b.setShowText(true);
		b.setColor(Color.orange);
		b.setTextColor(Color.black);
		b.setShape(LabelledBlob.DIAMOND);
		b.setSize(12,12);
		return b;
	}

	public Mappable male(Object c)
	{
		LabelledBlob b = new LabelledBlob(c.toString());
		b.setShowText(true);
		b.setColor(Color.cyan);
		b.setTextColor(Color.black);
		b.setShape(LabelledBlob.RECTANGLE);
		b.setSize(12,12);
		return b;
	}

	public Mappable female(Object c)
	{
		LabelledBlob b = new LabelledBlob(c.toString());
		b.setShowText(true);
		b.setColor(Color.yellow);
		b.setTextColor(Color.black);
		b.setShape(LabelledBlob.OVAL);
		b.setSize(12,12);
		return b;
	}

	public Mappable marriage()
	{
		LabelledBlob b = new LabelledBlob();
		b.setShowText(false);
		b.setColor(Color.white);
		b.setTextColor(Color.black);
		b.setShape(LabelledBlob.OVAL);
		b.setSize(8,8);
		return b;
	}
}

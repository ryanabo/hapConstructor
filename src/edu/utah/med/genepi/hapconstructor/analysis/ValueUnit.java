package edu.utah.med.genepi.hapconstructor.analysis;


public class ValueUnit {

	private String type = null;
	private byte[][] values = null;
	
	public ValueUnit( byte[][] v, String t )
	{
		values = v;
		type = t;
	}
	
	public byte[] getAllele( int index )
	{
		if ( (values.length-1) < index )
		{
			System.out.println("error");
		}
		return values[index]; 
	}
	
	public byte[][] getValues(){ return values; }
	
	public String getType(){ return type; }
}

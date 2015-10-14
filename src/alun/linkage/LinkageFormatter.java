package alun.linkage;

import java.io.BufferedReader;
import java.io.IOException;

import alun.util.InputFormatter;

public class LinkageFormatter extends InputFormatter
{
	public LinkageFormatter(BufferedReader br, String f) throws IOException
	{
		super(br);
		file = f;
	}

	public void readLine() throws IOException
	{
		if (!newLine())
			crash("is missing");
	}

	public int readInt(String name, int def, boolean used, boolean crash)
	{
		if (newToken() && nextIsInt())
			return getInt();
		
		if (crash)
		{
			crash("Can't read integer "+name+" from string \""+getString()+"\".");
		}
		else
		{
			warn("Can't read integer "+name+
				" from string \""+getString()+"\"."+
				"\n\tAssumed to be "+def+"."
				+ (!used ? "\n\tThis parameter is not used by these programs" : "")
				);
		}
		return def;
	}

	public double readDouble(String name, double def, boolean used, boolean crash)
	{
		if (newToken() && nextIsDouble())
			return getDouble();
		
		if (crash)
		{
			crash("Can't read double "+name+" from string \""+getString()+"\".");
		}
		else
		{
			warn("Can't read double "+name+
				" from string \""+getString()+"\"."+
				"\n\tAssumed to be "+def+"."
				+ (!used ? "\n\tThis parameter is not used by these programs" : "")
				);
		}
		return def;
	}

	public void crash(String s)
	{
		System.err.println("\n"+file+", line "+lastLine()+", item "+lastToken()+":\n\t"+s);
		System.err.println("\tCan't continue");
		System.exit(1);
	}

	public void warn(String s)
	{
		System.err.println("\n"+file+", line "+lastLine()+", item "+lastToken()+":\n\t"+s);
	}

	private String file = null;
}

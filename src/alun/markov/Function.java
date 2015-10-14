package alun.markov;

import java.util.Set;

/** 
 A Function is a mapping from the states of a set of variables to a double.
*/
public interface Function
{
/**
 Returns the current value of the Function.
*/
	public double getValue();
/**
 Returns the Set of Variables that are arguments to the function.
*/
	public Set<Variable> getVariables();
}

package alun.markov;

/**
 A Variable is an object that can be in some finite number of states.
*/
public interface Variable extends Iterable
{
/**
 Returns the value of the current state of the Variable.
*/
	public int getState();
/**
 Sets the current state to the given integer.
 Returns true if the given state is valid. 
*/
	public boolean setState(int s);
/**
 Restricts the set of values that the variable can take to those
 among the current set for which the given function has positive value.
*/
	public void setStates(Function f);

	public void setStates(int[] a);

	public int[] getStates();
	
/**
 Returns the current number of possible states.
*/
	public int getNStates();

}

import java.io.Serializable;


public class Pair<K,V>  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5758037073498392058L;
	private K elementOne;
	private V elementTwo;
	public Pair(K elementOne, V elementTwo){
		this.elementOne = elementOne;
		this.elementTwo = elementTwo;
	}
	public K getElementOne() {
		return elementOne;
	}
	public V getElementTwo() {
		return elementTwo;
	}
	public void setElementOne(K elementOne) {
		this.elementOne = elementOne;
	}
	public void setElementTwo(V elementTwo) {
		this.elementTwo = elementTwo;
	}

}
class PriorityPair implements Comparable<PriorityPair>{
	private Item elementOne;
	private int elementTwo;
	public PriorityPair(Item elementOne, int elementTwo){
		this.elementOne = elementOne;
		this.elementTwo = elementTwo;
	}
	public Item getItem() {
		return elementOne;
	}
	public int getInteger() {
		return elementTwo;
	}
	public void setItem(Item elementOne) {
		this.elementOne = elementOne;
	}
	public void setInteger(int elementTwo) {
		this.elementTwo = elementTwo;
	}
	public void increment(){
		elementTwo++;
	}
	@Override
	public int compareTo(PriorityPair o) {
		if(o.getInteger() <this.elementTwo)
			return -1;
		else if( o.getInteger() >this.elementTwo){
			return 1;
		}
		return 0;
	}
}

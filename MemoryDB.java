import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class MemoryDB {
	/*ValueNum Map tracking all Value and its numbers*/
	final static Map<Integer, Integer> valueNum = new HashMap<Integer, Integer>();
	/*stack transaction tracking each transaction affect variable*/
	final static Stack<Map<String, Integer>> transactions = new Stack<Map<String, Integer>>();
	static {
		transactions.push(new HashMap<String, Integer>());
	}
	
	public void equalTo(int value) {
		Integer num = valueNum.get(value);
		System.out.println(num == null ? 0 : num);
	}
	
	public void set(String variable, int value) {
		decreaseOne(getValue(variable));
		transactions.peek().put(variable, value);
		increaseOne(value);
	}
	
	private Integer getValue(String varaible) {
		for (int i = transactions.size() - 1; i >= 0; i--) {
			if (transactions.elementAt(i).containsKey(varaible)) {
				return transactions.elementAt(i).get(varaible);
			}
		}
		return null;
	}
	
	public void get(String variable) {
		Integer value = getValue(variable);
		System.out.println(value == null ? "null" : value);
	}
	
	public void unSet(String variable) {
		Integer oldValue = getValue(variable);
		if (transactions.size() == 1) {
			transactions.peek().remove(variable);
		} else {
			transactions.peek().put(variable, null);
		}
		decreaseOne(oldValue);
	}
	
	public void startTransaction() {
		transactions.push(new HashMap<String, Integer>());
	}
	
	public void commitTransaction() {
		if (transactions.size() == 1) throw new IllegalStateException();
		for(Map.Entry<String, Integer> item : transactions.pop().entrySet()) {
			transactions.peek().put(item.getKey(), item.getValue());
			if (transactions.size() == 1 && item.getValue() == null) {
				transactions.peek().remove(item.getKey());
			}
		}
	}
	
	public void rollbackTransaction() {
		if (transactions.size() == 1) throw new IllegalStateException();
		for(Map.Entry<String, Integer> item : transactions.pop().entrySet()) {
			decreaseOne(item.getValue());
			increaseOne(getValue(item.getKey()));
		}
	}
		
	private void decreaseOne(Integer value) {
		if (value == null) return;
		Integer num = valueNum.get(value);
		if (num == 1) valueNum.remove(value);
		else valueNum.put(value, num - 1);
	}
	
	public void increaseOne(Integer value) {
		if (value  == null) return;
		Integer num = valueNum.get(value);
		valueNum.put(value, (num == null ? 1 : num + 1));
	}

}

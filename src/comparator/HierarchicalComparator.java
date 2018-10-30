package comparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HierarchicalComparator<T> implements Comparator<T> {

	private final List<Comparator<T>> comparators;
	
	public HierarchicalComparator(List<Comparator<T>> comparators) {
		
		if (comparators == null || comparators.isEmpty()) 
			throw new IllegalArgumentException("Comparators cannot be null or empty.");
		
		this.comparators = new ArrayList<Comparator<T>>(comparators);
	}
	
	@Override
	public int compare(T o1, T o2) {
		for (Comparator<T> c : comparators) {
			final int result = c.compare(o1, o2);
			if (result != 0) {
				return result;
			}
		}
		
		return 0;
	}

}

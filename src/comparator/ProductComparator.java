package comparator;

import java.util.Collections;
import java.util.Comparator;

import model.Product;

public enum ProductComparator implements Comparator<Product> {

	byPrice() {
		public int compare(Product p, Product o) {
			return p.getPrice().compareTo(o.getPrice());
		}
	},
	
	byName() {
		public int compare(Product p, Product o) {
			return p.getName().compareTo(o.getName());
		}
	},
	
	byVolume() {
		public int compare(Product p, Product o) {
			return p.getVolume().compareTo(o.getVolume());
		}
	},
	
	byCategoryName() {
		public int compare(Product p, Product o) {
			return p.getCategory().getName().compareTo(o.getCategory().getName());
		}
	};
	
	public Comparator<Product> asc() {
		return this;
	}
	
	public Comparator<Product> desc() {
		return Collections.reverseOrder(this);
	}
	
}

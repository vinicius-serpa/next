package comparator;

import java.util.Collections;
import java.util.Comparator;

import model.Product;

public enum ProductComparator implements Comparator<Product> {

	PRICE() {
		public int compare(Product p, Product o) {
			return p.getPrice().compareTo(o.getPrice());
		}
	},
	
	NAME() {
		public int compare(Product p, Product o) {
			return p.getName().compareTo(o.getName());
		}
	},
	
	VOLUME() {
		public int compare(Product p, Product o) {
			return p.getVolume().compareTo(o.getVolume());
		}
	},
	
	CATEGORY_NAME() {
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

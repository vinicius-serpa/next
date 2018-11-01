package run;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import comparator.ProductComparator;
import model.Category;
import model.Product;

public class Run {

	public static void main(String[] args) {
		
		Category c1 = new Category("Common");
		Category c2 = new Category("Premium");
		
		Product p1 = new Product("Fandang", 80, 8.50, c1);
		Product p2 = new Product("Cheetos", 110, 3.00, c1);
		Product p3 = new Product("Ruffles", 95, 5.50, c2);		
		Product p4 = new Product("Ruffles", 80, 8.50, c2);
		Product p5 = new Product("Ruffles", 110, 12.00, c2);
		Product p6 = new Product("Ruffles", 90, 5.50, c2);
		
		List<Product> l = Arrays.asList(p1, p2, p3, p4, p5, p6);						
		
		Collections.sort(l, ProductComparator.CATEGORY_NAME.desc());
		System.out.println(l);
		
		Collections.sort(l);
		System.out.println(l);
	}
	
}

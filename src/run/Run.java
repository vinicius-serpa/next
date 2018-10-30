package run;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comparator.ProductComparator;
import model.Category;
import model.Product;

public class Run {

	public static void main(String[] args) {
		
		Category c1 = new Category("Common");
		Category c2 = new Category("Premium");
		
		Product p1 = new Product("Ruffles", 80, 8.50, c2);
		Product p2 = new Product("Cheetos", 110, 3.00, c1);
		Product p3 = new Product("Fandangos", 90, 5.50, c1);
		
		List<Product> l = new ArrayList<Product>();
		
		l.add(p1);
		l.add(p2);
		l.add(p3);				
		
		Collections.sort(l, ProductComparator.byCategoryName.desc());
		
		System.out.println(l);
	}
	
}

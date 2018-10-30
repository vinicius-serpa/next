package model;

import java.util.Arrays;
import java.util.Comparator;

import comparator.ProductComparator;
import comparator.HierarchicalComparator;

public class Product implements Comparable<Product> {

	private static final Comparator<Product> comparator;

	static {
		comparator = new HierarchicalComparator<Product>(
				Arrays.<Comparator<Product>> asList(
						ProductComparator.NAME,
						ProductComparator.PRICE.desc(),
						ProductComparator.VOLUME,
						ProductComparator.CATEGORY_NAME));
	}
	
	private String name;
	private Integer volume;
	private Double price;
	
	private Category category;	
	
	public Product(String name, Integer volume, Double price, Category category) {
		
		if (name == null || name.isEmpty()) 
			throw new IllegalArgumentException("Name cannot be null or empty.");

		if (volume <= 0) 
			throw new IllegalArgumentException("Volume must be positive.");
		
		if (price <= 0.0) 
			throw new IllegalArgumentException("Price must be positive.");

		if (category == null) 
			throw new IllegalArgumentException("Product category cannot be null.");
		
		this.name = name;
		this.volume = volume;
		this.price = price;
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getVolume() {
		return volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "name:" + name + "\t\tvolume:" + volume + "\n";
	}

	@Override
	public int compareTo(Product o) {
		return comparator.compare(this, o);
	}	
}

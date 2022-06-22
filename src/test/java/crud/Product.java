package crud;

public class Product {
	private String productName;
	private int productUnits;
	private double productPrice;
	private int productId;
	
	
	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}


	public int getProductUnits() {
		return productUnits;
	}


	public void setProductUnits(int productUnits) {
		this.productUnits = productUnits;
	}


	public double getProductPrice() {
		return productPrice;
	}


	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}


	public int getProductId() {
		return productId;
	}


	public void setProductId(int productId) {
		this.productId = productId;
	}


	public Product(String productName, int productUnits, double productPrice, int productId) {
		this.productName = productName;
		this.productUnits = productUnits;
		this.productPrice = productPrice;
		this.productId = productId;
	}
	
	
	
	
}

package crudTest;

import java.util.concurrent.TimeUnit;import javax.print.attribute.standard.Media;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import crud.Product;
import crud.ProductsPage;

public class ProductTest {

	private WebDriver driver;
	private ProductsPage productsPage;
	
	@BeforeTest
	public void setup() {
		System.setProperty("webdriver.chrome.driver","C:\\Users\\shapira\\Desktop\\natan\\seleniumProject\\drivers\\chromedriver.exe"); 
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		
		//driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		String url = "https://demos.telerik.com/aspnet-ajax/grid/examples/data-editing/manual-crud-operations/defaultcs.aspx";
		driver.get(url);
		
		productsPage = new ProductsPage(driver);
		
		//close accept cookies
		//productsPage.acceptCookies();
	}
	
	@Test
	public void testAddProduct() {
		Product expectedProduct = new Product("item3", 2, 5.5, 0);
		
		productsPage.addProduct(expectedProduct);
		
		Product resultProduct = productsPage.getLastProduct();
		assertProductsMatch(expectedProduct, resultProduct);
	}
	
	@Test
	public void testUpdateProduct() throws InterruptedException {
		Product expectedProduct = new Product("item updated", 4, 7.9, 17);
		productsPage.updateProduct(expectedProduct);
		
		Product resultProduct = productsPage.findAndGetProduct(expectedProduct.getProductId());
		assertProductsChange(expectedProduct, resultProduct);
	}
	
	@Test
	public void testGetProduct() {
		Product product = productsPage.findAndGetProduct(16);
		System.out.println("product name: " + product.getProductName()
		 + "\nproduct units: " + product.getProductUnits()
		 + "\nproduct price: " + product.getProductPrice()
		 + "\nproduct id: " + product.getProductId());
	}
	
	@Test
	public void testDeleteProduct() {
		Product addedProduct = new Product("item3", 2, 5.5, 0);
		
		productsPage.addProduct(addedProduct);
		
		int lastIdBefore = productsPage.getLastId();
		
		productsPage.deleteProduct(addedProduct.getProductId());
		
		int lastIdAfter = productsPage.getLastId();
		
		Assert.assertNotEquals(lastIdAfter, lastIdBefore);
	}
	
	@AfterTest
	public void finish() throws InterruptedException {
		Thread.sleep(20000);
		driver.quit();
	}
	
	//private void assertProductDeleted(int id) {
	//	Assert.assertEquals(null, productsPage.findAndGetProduct(id));
	//}
	
	private void assertProductsMatch(Product expProduct, Product resultProduct) {
		Assert.assertEquals(expProduct.getProductName(), resultProduct.getProductName());
		Assert.assertEquals(expProduct.getProductUnits(), resultProduct.getProductUnits());
		Assert.assertEquals(expProduct.getProductPrice(), resultProduct.getProductPrice());
	}
	
	private void assertProductsChange(Product expProduct, Product resultProduct) {
		if(expProduct.getProductName() == resultProduct.getProductName()
				&& expProduct.getProductUnits() == resultProduct.getProductUnits()
				&& expProduct.getProductPrice() == resultProduct.getProductPrice()) {
			Assert.fail("No change occurred");
		}
	}
}

package crud;

import java.sql.Driver;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class ProductsPage {

	private WebDriver driver;
	private WebDriverWait waiter;
	JavascriptExecutor js;
	
	public ProductsPage(WebDriver driver) {
		this.driver = driver;
		waiter = new WebDriverWait(driver, Duration.ofSeconds(20));
		js = (JavascriptExecutor)driver;
	}
	
	private void clickAddProduct() {
		By newProductLink = By.className("rgAdd");
		WebElement addBtn = driver.findElement(newProductLink);
		addBtn.click();
	}
	
	private void fillEditProdute(Product product) {
		
		By productNameLocator = By.xpath("//div[@class='rgEditForm']/table/tbody/tr[1]/td[2]/input");
		//By productNameLocator = By.xpath("//div[@class='rgEditForm']/table/tbody/tr[0]/td[2]/input");
		
		By productUnitsLocator = By.xpath("//div[@class='rgEditForm']/table/tbody/tr[2]/td[2]/input");
		//By productUnitsLocator = By.xpath("//div[@class='rgEditForm']/table/tbody/tr[1]/td[2]/input");
		
		By productPriceLocator = By.xpath("//div[@class='rgEditForm']/table/tbody/tr[3]/td[2]/input");
		//By productPriceLocator = By.xpath("//div[@class='rgEditForm']/table/tbody/tr[2]/td[2]/input");
		
		By submitBtnLocator = By.xpath("//div[@class='rgEditForm']/button");
		
		String inputValue = product.getProductName();
		if(inputValue != null && !inputValue.isEmpty() && !inputValue.isBlank()) {
			WebElement nameInput = waiter.until(ExpectedConditions.visibilityOfElementLocated(productNameLocator));
			nameInput.clear();
			nameInput.sendKeys(inputValue);
		}
		inputValue = String.valueOf(product.getProductUnits());
		if(inputValue != null && !inputValue.isEmpty() && !inputValue.isBlank()) {
			WebElement unitsInput = waiter.until(ExpectedConditions.visibilityOfElementLocated(productUnitsLocator));
			unitsInput.clear();
			unitsInput.sendKeys(inputValue);
		}
		
		inputValue = String.valueOf(product.getProductPrice());
		if(inputValue != null && !inputValue.isEmpty() && !inputValue.isBlank()) {
			WebElement priceInput = waiter.until(ExpectedConditions.visibilityOfElementLocated(productPriceLocator));
			priceInput.clear();
			priceInput.sendKeys(inputValue);
		}
		
		WebElement submitBtn = waiter.until(ExpectedConditions.visibilityOfElementLocated(submitBtnLocator));
		
		js.executeScript("arguments[0].click()", submitBtn);
		//submitBtn.click();
	}
	
	private void fillNewProdute(Product product){
		By productName = By.cssSelector("input[name='ctl00$ContentPlaceholder1$RadGrid1$ctl00$ctl02$ctl03$TB_ProductName']");
		By productUnits = By.cssSelector("input[id='ctl00_ContentPlaceholder1_RadGrid1_ctl00_ctl02_ctl03_TB_UnitsInStock']");
		By productPrice = By.cssSelector("input[id='ctl00_ContentPlaceholder1_RadGrid1_ctl00_ctl02_ctl03_TB_UnitPrice']");
		
		fillBlankForm(productName, productUnits, productPrice, product);
	}
	
	private void fillBlankForm(By pName, By pUnits, By pPrice, Product product) {
		//WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		
		WebElement nameInput = waiter.until(ExpectedConditions.visibilityOfElementLocated(pName));
		nameInput.sendKeys(product.getProductName());
		
		WebElement unitsInput = waiter.until(ExpectedConditions.visibilityOfElementLocated(pUnits));
		unitsInput.sendKeys(String.valueOf(product.getProductUnits()));
		
		WebElement priceInput = waiter.until(ExpectedConditions.visibilityOfElementLocated(pPrice));
		priceInput.sendKeys(String.valueOf(product.getProductPrice()));
	}
	
	public int getLastId() {
		goToLastPage();
		
		By lastRowIdLocator = By.xpath("//table[@class='rgMasterTable rfdOptionList']/tbody/tr[last()]/td[2]");
		WebElement lastRowId = waiter.until(ExpectedConditions.visibilityOfElementLocated(lastRowIdLocator));
		
		String strId = lastRowId.getText();
		return Integer.parseInt(strId);
	}
	
	public Product getLastProduct() {
		
		goToLastPage();
		
		//wait until page updated    
		
		By lastPageNumLocator = By.xpath("//div[@class='rgWrap rgNumPart']/a[last()]");
		waiter.until(ExpectedConditions.attributeContains(lastPageNumLocator, "class", "rgCurrentPage"));
		
		By lastRowNameLocator = By.xpath("//table[@class='rgMasterTable rfdOptionList']/tbody/tr[last()]/td[3]");
		By lastRowUnitsLocator = By.xpath("//table[@class='rgMasterTable rfdOptionList']/tbody/tr[last()]/td[4]");
		By lastRowPriceLocator = By.xpath("//table[@class='rgMasterTable rfdOptionList']/tbody/tr[last()]/td[5]");
		
		WebElement rowElement = driver.findElement(lastRowNameLocator);
		String productName = rowElement.getText();
		//test
		System.out.println("product name:" + productName);
		
		rowElement = driver.findElement(lastRowUnitsLocator);
		int productUnits = Integer.parseInt(rowElement.getText());
		//test
		System.out.println("units: " + productUnits);
		
		rowElement = driver.findElement(lastRowPriceLocator);
		double productPrice = Double.parseDouble(rowElement.getText().substring(1));
		//test
		System.out.println("price: " + productPrice);
		
		Product product = new Product(productName, productUnits, productPrice, 0);
		
		return product;
	}
	
	public void addProduct(Product product) {
		
		//find the list size
		int numBefore = getProductsNum();
		
		clickAddProduct();
		
		fillNewProdute(product);
		
		By submitBtn = By.cssSelector("button[id='ctl00_ContentPlaceholder1_RadGrid1_ctl00_ctl02_ctl03_PerformInsertButton']");
		WebElement submit = driver.findElement(submitBtn);
		submit.click();
		
		
		//wait the item to be added
		By itemsNumLocator = By.xpath("//*[@id=\'ctl00_ContentPlaceholder1_RadGrid1_ctl00\']/tfoot/tr/td/div/div[5]/strong[1]");
		String expectedNum = String.valueOf(numBefore + 1);
		waiter.until(ExpectedConditions.textToBe(itemsNumLocator, expectedNum));
		
		/*
		Product testProduct = getLastProduct();
		
		testProductsMatch(product, testProduct);
		*/
	}
	
	public boolean findPage(String id) {		
		int productsNum = getProductsNum();

		By pageSizeLocator = By.xpath("//div[@class='rgWrap rgAdvPart']/div/span/input");
		WebElement pageSize = waiter.until(ExpectedConditions.visibilityOfElementLocated(pageSizeLocator));
		//int pageProductsNum = Integer.parseInt(pageSize.getCssValue("value"));
		int pageProductsNum = Integer.parseInt(pageSize.getAttribute("value"));
		
		int nextPageNum = productsNum/pageProductsNum;
		
		if(productsNum % pageProductsNum == 0) {
			nextPageNum--;
		}
		
		//test
		System.out.println("all items: " + productsNum + "\nitems per page: " + pageProductsNum + "\npages number: " + (nextPageNum + 1) + "\nleft: " + productsNum % pageProductsNum);
				
		//List<WebElement> productsList;
		WebElement product;
		By productLocator = By.xpath("//td[text()='" + id + "']");
		
		goToFirstPage();
		
		int i = 0;
		while(i < nextPageNum) {
			try {
				//productsList = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(productLocator));
				product = waiter.until(ExpectedConditions.visibilityOfElementLocated(productLocator));
				return true;
			}catch (Exception e) {
				
				//test
				System.out.println("page: " + (i + 1));
				
				if(i < nextPageNum) {
					i++;
					goToNextPage();
				}
				else {
					//System.out.println("product not found");
					return false;
				}
			}
		}
		return false;
	}
	
	private void goToNextPage() {
		/*
		 * //wait.until(ExpectedConditions.elementToBeClickable(lastPageBtn));
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].click()", lastPageBtn);
		
		//lastPageBtn.click();
		 * */
		
		//WebElement nextPageElemaent = driver.findElement(By.className("rgPageNext"));
		By nextPageBtnLocator = By.className("rgPageNext");
		WebElement nextPageBtn = waiter.until(ExpectedConditions.elementToBeClickable(nextPageBtnLocator));
		//nextPageBtn.click();
		
		js.executeScript("arguments[0].click()", nextPageBtn);
	}
	
	public void updateProduct(Product product) throws InterruptedException {
		String strId = String.valueOf(product.getProductId());
		
		if(findPage(strId)) {
			By editBtnocator = By.xpath("//td[text()='" + strId + "']/../td[1]/button");
			//WebElement editBtn = driver.findElement(By.xpath("//td[text()='" + strId + "']/../td[1]/button"));
			
			//WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(editBtnocator));
			//editBtn.click();
			
			WebElement editBtn = waiter.until(ExpectedConditions.visibilityOfElementLocated(editBtnocator));
			//JavascriptExecutor js = (JavascriptExecutor)driver;
			js.executeScript("arguments[0].click()", editBtn);
			
			fillEditProdute(product);
			
			//wait
			//waiter.wait(5000);
		}
		
		
		
		/*
		//way 2
		List<WebElement> rows = driver.findElements(By.tagName("tr"));
		//System.out.println("rows num: "+rows.size()+"\ncolumns:\n");
		
		for (WebElement row : rows) {
			List<WebElement> columns = row.findElements(By.tagName("td"));
			System.out.println(columns.size());
			//if(columns.get(1).getText()=="mytext") {
			//	columns.get(0).findElement(By.tagName("button")).click();
			//	break;
			//}
		}
		*/
	}
	
	public Product findAndGetProduct(int id) {
		if(findPage(String.valueOf(id))) {
			return getProductById(id);
		}
		return null;
	}
	
	private Product getProductById(int id) {
		
		String strId = String.valueOf(id);
		
		By nameLocator = By.xpath("//td[text()='" + strId + "']/../td[3]");
		By unitsLocator = By.xpath("//td[text()='" + strId + "']/../td[4]");
		By priceLocator = By.xpath("//td[text()='" + strId + "']/../td[5]");
		
		WebElement rowElement = driver.findElement(nameLocator);
		String productName = rowElement.getText();
		//test
		System.out.println("product name:" + productName);
		
		rowElement = driver.findElement(unitsLocator);
		int productUnits = Integer.parseInt(rowElement.getText());
		//test
		System.out.println("units: " + productUnits);
		
		rowElement = driver.findElement(priceLocator);
		double productPrice = Double.parseDouble(rowElement.getText().substring(1));
		//test
		System.out.println("price: " + productPrice);
		
		Product product = new Product(productName, productUnits, productPrice, id);
		
		return product;
	}
	
	public void deleteProduct(int id) {
		
		int numBefore = getProductsNum();
		
		String strId = String.valueOf(id);
		if(findPage(strId)) {
			By deleteBtnLocator = By.xpath("//td[text()='" + strId + "']/../td[6]/button");
			//WebElement deleteButton = wait.until(ExpectedConditions.visibilityOfElementLocated(deleteBtnLocator));
			WebElement deleteButton =  waiter.until(ExpectedConditions.elementToBeClickable(deleteBtnLocator));
			deleteButton.click();
			
			By okBtnLocator = By.className("rwOkBtn");
			WebElement okBtn = waiter.until(ExpectedConditions.visibilityOfElementLocated(okBtnLocator));
			okBtn.click();
		}else {
			Assert.fail("id not found");
		}
		
		/*
		//validation 1
		try {
			By itemsNumLocator = By.xpath("//*[@id=\'ctl00_ContentPlaceholder1_RadGrid1_ctl00\']/tfoot/tr/td/div/div[5]/strong[1]");
			String expectedNum = String.valueOf(numBefore - 1);
			wait.until(ExpectedConditions.textToBe(itemsNumLocator, expectedNum));
		}catch(Exception e){
			//assert... changes not saved
			Assert.fail("changes not saved");
		}
		
		//validation 2
		By itemsNumLocator = By.xpath("//*[@id=\'ctl00_ContentPlaceholder1_RadGrid1_ctl00\']/tfoot/tr/td/div/div[5]/strong[1]");
		WebElement itemsNum = wait.until(ExpectedConditions.visibilityOfElementLocated(itemsNumLocator));
		String expectedNum = String.valueOf(numBefore - 1);
		wait.until(ExpectedConditions.textToBe(itemsNumLocator, expectedNum));
		Assert.assertEquals(itemsNum.getText(), expectedNum);
		
		
		//validation 3
		//wait 5 sec...
		//Assert.assertEquals(getProductsNum(), numBefore - 1);
		
		
		//validation 4
		//Assert.assertEquals(false, findPage(strId));
		
		//By deleteBtnLocator = By.xpath("//td[text()='" + strId + "']//following-sibling::td/button[class='rgDel']");
		*/
	}
	
	private int getProductsNum() {
		WebElement productsNumElement = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceholder1_RadGrid1_ctl00\']/tfoot/tr/td/div/div[5]/strong[1]"));
		int num = Integer.parseInt(productsNumElement.getText());
		return num;
	}
	
	private void goToLastPage() {
		/*
		//WebElement lastPageElemaent = driver.findElement(By.cssSelector('button .rgPageLast'));
		WebElement lastPageElemaent = driver.findElement(By.className("rgPageLast"));
		*/
		//wait to be clickable
		
		By lastPageBtnLocator = By.className("rgPageLast");
		waiter.until(ExpectedConditions.elementToBeClickable(lastPageBtnLocator));
		WebElement lastPageBtn = waiter.until(ExpectedConditions.visibilityOfElementLocated(lastPageBtnLocator));
		//wait.until(ExpectedConditions.elementToBeClickable(lastPageBtn));
		
		js.executeScript("arguments[0].click()", lastPageBtn);
		
		//lastPageBtn.click();
	}
	
	public void goToFirstPage() {
		/*
		//WebElement lastPageElemaent = driver.findElement(By.cssSelector('button .rgPageLast'));
		WebElement lastPageElemaent = driver.findElement(By.className("rgPageLast"));
		*/
		//wait to be clickable
		
		By firstPageBtnLocator = By.className("rgPageFirst");
		waiter.until(ExpectedConditions.elementToBeClickable(firstPageBtnLocator));
		WebElement firstPageBtn = waiter.until(ExpectedConditions.visibilityOfElementLocated(firstPageBtnLocator));
		//wait.until(ExpectedConditions.elementToBeClickable(lastPageBtn));
		
		js.executeScript("arguments[0].click()", firstPageBtn);
		
		//lastPageBtn.click();
	}
	
	public void acceptCookies() {
		By acceptCookieBtnLocator = By.id("onetrust-accept-btn-handler");
		//By acceptCookieBtnLocator = By.cssSelector("#onetrust-accept-btn-handler");
		//WebElement acceptCookieBtn = waiter.until(ExpectedConditions.visibilityOfElementLocated(acceptCookieBtnLocator));
		WebElement acceptCookieBtn = waiter.until(ExpectedConditions.elementToBeClickable(acceptCookieBtnLocator));
		acceptCookieBtn.click();
	}
}

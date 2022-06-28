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
		//WebElement addBtn = driver.findElement(newProductLink);
		//addBtn.click();
		WebElement addBtn = waiter.until(ExpectedConditions.visibilityOfElementLocated(newProductLink));
		js.executeScript("arguments[0].click()", addBtn);
	}
	
	private void fillEditProdute(Product product) {
		By productNameLocator = By.xpath("//div[@class='rgEditForm']/table/tbody/tr[1]/td[2]/input");
		By productUnitsLocator = By.xpath("//div[@class='rgEditForm']/table/tbody/tr[2]/td[2]/input");
		By productPriceLocator = By.xpath("//div[@class='rgEditForm']/table/tbody/tr[3]/td[2]/input");
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
		//test
		System.out.println("get last product");
		
		goToLastPage();
		
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
	
	public int addProduct(Product product) {
		
		//find the list size
		int numBefore = getProductsNum();
		
		clickAddProduct();
		
		fillNewProdute(product);
		
		By submitBtn = By.cssSelector("button[id='ctl00_ContentPlaceholder1_RadGrid1_ctl00_ctl02_ctl03_PerformInsertButton']");
		//WebElement submit = driver.findElement(submitBtn);
		WebElement submit = waiter.until(ExpectedConditions.elementToBeClickable(submitBtn));
		js.executeScript("arguments[0].click()", submit);
		//submit.click();
		
		//wait the item to be added
		By itemsNumLocator = By.xpath("//*[@id=\'ctl00_ContentPlaceholder1_RadGrid1_ctl00\']/tfoot/tr/td/div/div[5]/strong[1]");
		String expectedNum = String.valueOf(numBefore + 1);
		waiter.until(ExpectedConditions.textToBe(itemsNumLocator, expectedNum));
		
		return getLastId();
	}
	
	public boolean findPage(String id) throws InterruptedException {
		//test
		System.out.println("find page, seeking for product: " + id);
		
		int productsNum = getProductsNum();

		By pageSizeLocator = By.xpath("//div[@class='rgWrap rgAdvPart']/div/span/input");
		WebElement pageSize = waiter.until(ExpectedConditions.visibilityOfElementLocated(pageSizeLocator));
		
		int pageProductsNum = Integer.parseInt(pageSize.getAttribute("value"));
		
		int nextPageNum = productsNum/pageProductsNum;
		
		if(productsNum % pageProductsNum == 0) {
			nextPageNum--;
		}
		
		//test
		System.out.println("all items: " + productsNum + "\nitems per page: " + pageProductsNum + "\npages number: " + (nextPageNum + 1) + "\nrest: " + productsNum % pageProductsNum);
				
		By productLocator = By.xpath("//td[position() = 2 and text()='" + id + "']");
		
		return BinarySearchId(id, nextPageNum + 1, productLocator);
		
		//goToFirstPage();
		//return linearSearchId(id, nextPageNum, productLocator);
	}
	
	private void goToNumPage(String num) throws InterruptedException {
		By pageNumLocator = By.xpath("//div[@class='rgWrap rgNumPart']/a[position() = '" + num + "']");
		WebElement pageNumBtn = waiter.until(ExpectedConditions.visibilityOfElementLocated(pageNumLocator));
		js.executeScript("arguments[0].click()", pageNumBtn);
		
		/*
		synchronized (waiter)
		{
			waiter.wait(1000);
		}
		*/
		
		waiter.until(ExpectedConditions.stalenessOf(pageNumBtn));
		
		waiter.until(ExpectedConditions.attributeContains(pageNumLocator, "class", "rgCurrentPage"));
		System.out.println("page: " + num);
	}
	
	private int getFirstPageId() {
		By firstRowIdLocator = By.xpath("//table[@class='rgMasterTable rfdOptionList']/tbody/tr[position() = 1]/td[2]");
		WebElement firstRowId = waiter.until(ExpectedConditions.visibilityOfElementLocated(firstRowIdLocator));
		
		String strId = firstRowId.getText();
		return Integer.parseInt(strId);
	}
	
	private int getLastPageId() {
		By lastRowIdLocator = By.xpath("//table[@class='rgMasterTable rfdOptionList']/tbody/tr[last()]/td[2]");
		WebElement lastRowId = waiter.until(ExpectedConditions.visibilityOfElementLocated(lastRowIdLocator));
		
		String strId = lastRowId.getText();
		return Integer.parseInt(strId);
	}
	
	private boolean BinarySearchId(String id, int nextPageNum, By productLocator) throws InterruptedException {
		
		//test
		System.out.println("pages num: " + nextPageNum);
		
		int currentId = Integer.parseInt(id);
		int low = 1;
		int high = nextPageNum;
		int currentPage;// = (low + high) / 2;
		//goToNumPage(String.valueOf(currentPage));
		
		while(low <= high) {
			currentPage = (low + high) / 2;
			goToNumPage(String.valueOf(currentPage));
			int firstId = getFirstPageId();
			int lastId = getLastPageId();
			
			if(firstId <= currentId && lastId >= currentId) {
				try {
					WebElement product = waiter.until(ExpectedConditions.visibilityOfElementLocated(productLocator));
					System.out.println("product id: " + id + " found in page: " + currentPage);
					return true;
				}catch (Exception e) {
					return false;
				}
			}else {
				if(firstId > currentId) {
					//go prev
					high = currentPage - 1;
				}else {
					//go next
					low = currentPage + 1;
				}
			}
		}
		return false;
	}
	
	private boolean linearSearchId(String id, int nextPageNum, By productLocator) {
		for(int i = 0; i <= nextPageNum; i++ ) {
			System.out.println("page: " + (i + 1));
			try {
				WebElement product = waiter.until(ExpectedConditions.visibilityOfElementLocated(productLocator));
				System.out.println("product id: " + id + " found in page: " + (i + 1));
				return true;
			}catch (Exception e) {
				if(i < nextPageNum) {
					goToNextPage();
				}else {
					return false;
				}
			}
		}
		return false;
	}
	
	private void goToNextPage() {
		By nextPageBtnLocator = By.className("rgPageNext");
		WebElement nextPageBtn = waiter.until(ExpectedConditions.elementToBeClickable(nextPageBtnLocator));
		//nextPageBtn.click();
		
		js.executeScript("arguments[0].click()", nextPageBtn);
	}
	
	public void updateProduct(Product product) throws InterruptedException {
		String strId = String.valueOf(product.getProductId());
		
		if(findPage(strId)) {
			//By editBtnocator = By.xpath("//td[2 and text()='" + strId + "']/../td[1]/button");
			//By editBtnocator = By.xpath("(//td[text()='" + strId + "'])[2]/../td[1]/button");
			By editBtnocator = By.xpath("//td[position() = 2 and text()='" + strId + "']/../td[1]/button");
			
			WebElement editBtn = waiter.until(ExpectedConditions.visibilityOfElementLocated(editBtnocator));
			//JavascriptExecutor js = (JavascriptExecutor)driver;
			js.executeScript("arguments[0].click()", editBtn);
			
			fillEditProdute(product);
			
			//waiter.wait(5000);
		}else {
			System.out.println("id not found");
		}
		
	}
	
	public Product findAndGetProduct(int id) throws InterruptedException {
		if(findPage(String.valueOf(id))) {
			return getProductById(id);
		}
		return null;
	}
	
	private Product getProductById(int id) {
		
		String strId = String.valueOf(id);
		
		//By nameLocator = By.xpath("//td[2 and text()='" + strId + "']/../td[3]");
		//By nameLocator = By.xpath("(//td[text()='" + strId + "'])[2]/../td[3]");
		By nameLocator = By.xpath("//td[position() = 2 and text()='" + strId + "']/../td[3]");
		
		//By unitsLocator = By.xpath("//td[2 and text()='" + strId + "']/../td[4]");
		//By unitsLocator = By.xpath("(//td[text()='" + strId + "'])[2]/../td[4]");
		By unitsLocator = By.xpath("//td[position() = 2 and text()='" + strId + "']/../td[4]");
		
		//By priceLocator = By.xpath("//td[2 and text()='" + strId + "']/../td[5]");
		//By priceLocator = By.xpath("(//td[text()='" + strId + "'])[2]/../td[5]");
		By priceLocator = By.xpath("//td[position() = 2 and text()='" + strId + "']/../td[5]");
		
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
	
	public void deleteProduct(int id) throws InterruptedException {
		//test
		System.out.println("deleting item: " + id);
		
		int numBefore = getProductsNum();
		
		String strId = String.valueOf(id);
		if(findPage(strId)) {
			System.out.println("deleting product now");
			//By deleteBtnLocator = By.xpath("//td[text()='" + strId + "']/../td[6]/button");
			//By deleteBtnLocator = By.xpath("(//td[text()='" + strId + "'])[2]/../td[6]/button");
			By deleteBtnLocator = By.xpath("//td[position() = 2 and text()='" + strId + "']/../td[6]/button");
			WebElement deleteButton = waiter.until(ExpectedConditions.visibilityOfElementLocated(deleteBtnLocator));
			//WebElement deleteButton =  waiter.until(ExpectedConditions.elementToBeClickable(deleteBtnLocator));
			js.executeScript("arguments[0].click()", deleteButton);
			
			//deleteButton.click();
			
			By okBtnLocator = By.className("rwOkBtn");
			WebElement okBtn = waiter.until(ExpectedConditions.visibilityOfElementLocated(okBtnLocator));
			okBtn.click();
		}else {
			System.out.println("id not found");
		}
		
		
		//validation 1
		try {
			By itemsNumLocator = By.xpath("//*[@id=\'ctl00_ContentPlaceholder1_RadGrid1_ctl00\']/tfoot/tr/td/div/div[5]/strong[1]");
			String expectedNum = String.valueOf(numBefore - 1);
			waiter.until(ExpectedConditions.textToBe(itemsNumLocator, expectedNum));
		}catch(Exception e){
			System.out.println("changes not saved");
		}
		
		/*
		//validation 2
		By itemsNumLocator = By.xpath("//*[@id=\'ctl00_ContentPlaceholder1_RadGrid1_ctl00\']/tfoot/tr/td/div/div[5]/strong[1]");
		WebElement itemsNum = wait.until(ExpectedConditions.visibilityOfElementLocated(itemsNumLocator));
		String expectedNum = String.valueOf(numBefore - 1);
		wait.until(ExpectedConditions.textToBe(itemsNumLocator, expectedNum));
		Assert.assertEquals(itemsNum.getText(), expectedNum);
		*/
	}
	
	private int getProductsNum() {
		WebElement productsNumElement = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceholder1_RadGrid1_ctl00\']/tfoot/tr/td/div/div[5]/strong[1]"));
		int num = Integer.parseInt(productsNumElement.getText());
		return num;
	}
	
	private void goToLastPage() {
		By lastPageBtnLocator = By.className("rgPageLast");
		//waiter.until(ExpectedConditions.elementToBeClickable(lastPageBtnLocator));
		WebElement lastPageBtn = waiter.until(ExpectedConditions.visibilityOfElementLocated(lastPageBtnLocator));
		//wait.until(ExpectedConditions.elementToBeClickable(lastPageBtn));
		
		js.executeScript("arguments[0].click()", lastPageBtn);
		
		//lastPageBtn.click();
		
		By lastPageNumLocator = By.xpath("//div[@class='rgWrap rgNumPart']/a[last()]");
		waiter.until(ExpectedConditions.attributeContains(lastPageNumLocator, "class", "rgCurrentPage"));
	}
	
	public void goToFirstPage() {
		By firstPageBtnLocator = By.className("rgPageFirst");
		//waiter.until(ExpectedConditions.elementToBeClickable(firstPageBtnLocator));
		//WebElement firstPageBtn = waiter.until(ExpectedConditions.visibilityOfElementLocated(firstPageBtnLocator));
		//wait.until(ExpectedConditions.elementToBeClickable(lastPageBtn));
		
		WebElement firstPageBtn = waiter.until(ExpectedConditions.visibilityOfElementLocated(firstPageBtnLocator));
		
		js.executeScript("arguments[0].click()", firstPageBtn);
		
		//lastPageBtn.click();
		
		By firstPageNumLocator = By.xpath("//div[@class='rgWrap rgNumPart']/a[1]");
		waiter.until(ExpectedConditions.attributeContains(firstPageNumLocator, "class", "rgCurrentPage"));
	}
	
	public int randomId() {
		int lastId = getLastId();
		return (int) (Math.random() * lastId);
	}
	
	public void acceptCookies() {
		By acceptCookieBtnLocator = By.id("onetrust-accept-btn-handler");
		WebElement acceptCookieBtn = waiter.until(ExpectedConditions.elementToBeClickable(acceptCookieBtnLocator));
		acceptCookieBtn.click();
	}
}

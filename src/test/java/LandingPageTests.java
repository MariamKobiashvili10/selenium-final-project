import ge.Constants;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.openqa.selenium.support.Color;


public class LandingPageTests {
    WebDriver driver;
    JavascriptExecutor js ;
    Actions actions;
    @BeforeClass
    @Parameters("browser")
    public void setUp(String browser) {
        if (browser.equalsIgnoreCase("edge")) {
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
        } else if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        }
        else if (browser.equalsIgnoreCase("firefox")){
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        }
        driver.manage().window().maximize();

        js = (JavascriptExecutor) driver;
        actions = new Actions(driver);
        driver.manage().window().maximize();
        driver.get(Constants.SWOOP_URL);
    }

    @AfterClass
    public void tearDown() {

        driver.close();
    }
    @Test
    public void activeCategoryTest() {
        WebElement cookieButton = driver.findElement(By.xpath("//div[@class = 'cookieButton']"));
        cookieButton.click();
        driver.findElement(By.xpath("//div[@class= 'NewCategories newcat']")).click();
        actions.moveToElement(driver.findElement(By.xpath("//li[@cat_id='CatId-6']"))).perform();
        driver.findElement(By.xpath("//li/a[contains(text(), 'კარტინგი')]")).click();
        String currentURL = js.executeScript("return document.URL;").toString();
        Assert.assertEquals(currentURL, Constants.URL_FOR_ACTIVECATEGORYTEST);
        String color = driver.findElement(By.xpath("//div[@class = 'category-filter-desk']//span[contains(text(),'კარტინგი')]")).getCssValue("color");
        Color colorFromRGBA = Color.fromString(color);
        String hexFromColor = colorFromRGBA.asHex().toUpperCase();
        Assert.assertEquals(hexFromColor, Constants.KARTINGI_COLOR);
    }
    @Test
    public void landingTest(){
        driver.get(Constants.SWOOP_URL);
        driver.findElement(By.xpath(Constants.DASVENEBA_XPATH)).click();
        driver.findElement(By.xpath("//header//a[@class = 'Newlogo']")).click();
        Assert.assertEquals(Constants.SWOOP_URL, driver.getCurrentUrl());

    }
}

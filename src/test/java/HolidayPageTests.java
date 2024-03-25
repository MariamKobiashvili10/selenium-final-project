import ge.Constants;
import ge.FunctionsForHolidayPageTests;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.*;

public class HolidayPageTests {
    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;
    SoftAssert softAssert;
    Actions actions;
    Select select;

    @BeforeClass
    @Parameters("browser")
    public void setUp(String browser) {
        if (browser.equalsIgnoreCase("edge")) {
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
        } else if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        }
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, 10);
        js = (JavascriptExecutor) driver;
        softAssert = new SoftAssert();
        actions = new Actions(driver);
        driver.get(Constants.SWOOP_URL);
        //ვინაიდან ყველა ტესტს სვუპზე ვუშვებთ, აქვე მაქვს გაწერილი
    }


    @AfterTest
    public void tearDown() {
        driver.close();
    }


    @Test(priority = 1)
    public void descendingOrderTest() {
        driver.findElement(By.xpath("//div[@class = 'cookieButton']")).click();
        driver.findElement(By.xpath(Constants.DASVENEBA_XPATH)).click();
        //ვიძახებთ წინასწარ შექმნილ ფუნქციას რომელსაც ყველა ფეიჯიდან მოაქვს ფასები და ინტეჯერ ტიპის ლისტად აბრუნებს
        // შემდეგ კი პირობის თანახმად ვაგრძელებთ წერას
        List<Integer> prices = FunctionsForHolidayPageTests.collectPrices(driver);
        Integer maxPrice = Collections.max(prices);

        WebElement sortDropDown = driver.findElement(By.id("sort"));
        select = new Select(sortDropDown);
        select.selectByVisibleText(Constants.PRICE_DECREASE);

        wait.until(ExpectedConditions.urlContains(Constants.URL_ENDPOINT_PRICE_DECREASING));

        WebElement firstElementMaxPrice = driver.findElement(By.xpath("//div[@class = 'special-offer']//div[@class = 'discounted-prices']//p[1]"));
        //ვინაიდან ფასები სტრინგადაა გაწერილი  საიტზე (მაგ : 20₾) ფუნქციით გადაგვყავს პირველი ელემენტის ფასი ინტეჯერში და შემდეგ ვადარებთ
        Integer priceInString = FunctionsForHolidayPageTests.extractFirstPrice(firstElementMaxPrice);
        Assert.assertEquals(priceInString, maxPrice);

    }

    //
    @Test(priority = 2)
    public void ascendingOrderTest() {
        //ვინაიდან ფუნქცია ისეა დაწერილი რომ მოვარგე ოთხივე ტესტს, ვიძახებ ამ ტესტ მეთოდშიც
        driver.findElement(By.xpath(Constants.DASVENEBA_XPATH)).click();
        List<Integer> prices = FunctionsForHolidayPageTests.collectPrices(driver);
        Integer minPrice = Collections.min(prices);

        WebElement sortDropDown = driver.findElement(By.id("sort"));
        select = new Select(sortDropDown);
        select.selectByVisibleText(Constants.PRICE_INCREASE);

        wait.until(ExpectedConditions.urlContains(Constants.URL_ENDPOINT_PRICE_INCREASING));

        WebElement firstElementMinPrice = driver.findElement(By.xpath("//div[@class = 'special-offer']//div[@class = 'discounted-prices']//p[1]"));
        Integer priceInString = FunctionsForHolidayPageTests.extractFirstPrice(firstElementMinPrice);
        Assert.assertEquals(priceInString, minPrice, "სორტის ხარვეზია");
    }
    //ხარვეზი საიტის სორტშია და ერორს ისვრის ამ ეტაპზე

    @Test(priority = 3)
    public void filterTest() {
        WebElement kotejiCheckbox = driver.findElement(By.xpath("//div[@class = 'sidebar__inner']//label[contains(text(), 'კოტეჯი')]"));
        wait.until(ExpectedConditions.elementToBeClickable(kotejiCheckbox));
        js.executeScript("arguments[0].click();", kotejiCheckbox);
        wait.until(ExpectedConditions.urlContains("arrangements"));
        List<String> descriptionsInString = new ArrayList<>();
        try {
            WebElement nextButton = driver.findElement(By.xpath("//img[@src = '/Images/NewDesigneImg/categoryIn/arrow-01.png']/parent::a"));
            while (nextButton.getAttribute("style").contains("auto")) {
                List<WebElement> descriptions = driver.findElements(By.xpath("//div[@class = 'deal-description']"));
                for (WebElement description : descriptions) {
                    String descriptionInString = description.getText();
                    descriptionsInString.add(descriptionInString);
                }
                nextButton.click();
                nextButton = driver.findElement(By.xpath("//img[@src = '/Images/NewDesigneImg/categoryIn/arrow-01.png']/parent::a"));
            }
        } catch (Exception e) {
            List<WebElement> descriptions = driver.findElements(By.xpath("//div[@class = 'deal-description']"));
            for (WebElement description : descriptions) {
                String descriptionInString = description.getText();
                descriptionsInString.add(descriptionInString);
            }
        }
        List<WebElement> lastPageDescriptions = driver.findElements(By.xpath("//div[@class = 'deal-description']"));
        for (WebElement description : lastPageDescriptions) {
            String descriptionInString = description.getText();
            descriptionsInString.add(descriptionInString);
        }
        for (String descriptionInString : descriptionsInString) {
            softAssert.assertTrue(descriptionInString.contains(Constants.KOTEJI), "არ შეიცავს სიტყვა კოტეჯს");
        } //დაფეილდება ვინაიდან ყველა ოფერი არ შეიცავს სიტყვა კოტეჯს და აქ ვიყენებთ სოფტ ასერტს რათა არ გაჩერდეს პროცესი

        driver.findElement(By.xpath("//div[@class = 'pagination']//div[1]")).click();


        List<Integer> prices = FunctionsForHolidayPageTests.collectPrices(driver);
        Integer minPrice = Collections.min(prices);

        WebElement sortDropDown = driver.findElement(By.id("sort"));
        select = new Select(sortDropDown);
        select.selectByVisibleText(Constants.PRICE_INCREASE);

        wait.until(ExpectedConditions.urlContains(Constants.URL_ENDPOINT_PRICE_INCREASING));
        WebElement firstElementMinPrice = driver.findElement(By.xpath("//div[@class = 'special-offer']//div[@class = 'discounted-prices']//p[1]"));
        Integer priceInString = FunctionsForHolidayPageTests.extractFirstPrice(firstElementMinPrice);
        Assert.assertEquals(minPrice, priceInString);
        softAssert.assertAll();
    }


    @Test(priority = 4)
    public void priceRangeTest() {
        WebElement dasveneba = driver.findElement(By.xpath(Constants.DASVENEBA_XPATH));
        wait.until(ExpectedConditions.elementToBeClickable(dasveneba));
        dasveneba.click();
        driver.findElement(By.xpath("//div[@class = 'category-filter-desk']//div[@class = 'price-inputs']//input[1]")).sendKeys("" + Constants.MY_SPECIFIED_RANGE_MIN + "");
        driver.findElement(By.xpath("//div[@class = 'category-filter-desk']//div[@class = 'price-inputs']//input[2]")).sendKeys("" + Constants.MY_SPECIFIED_RANGE_MAX + "");
        js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//div[@id ='sidebar-container']//div[@class = 'submit-button']")));
        wait.until(ExpectedConditions.urlContains(Constants.URl_CONTAINS_FOR_PRICE_RANGE));
        List<Integer> prices = FunctionsForHolidayPageTests.collectPrices(driver);
        Assert.assertTrue(Collections.max(prices) <= Integer.parseInt(Constants.MY_SPECIFIED_RANGE_MAX)
                && (Collections.min(prices) >= Integer.parseInt(Constants.MY_SPECIFIED_RANGE_MIN)), "საიტს რეინჯი არასწორად გამოაქვს");
        //აქაც ერორს დაარტყავს ვინაიდან სწორ რეინჯს არ გვიბრუნებს ვებსაიტი.
    }
}

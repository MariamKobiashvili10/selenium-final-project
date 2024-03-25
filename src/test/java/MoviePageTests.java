import ge.Constants;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;

public class MoviePageTests {
    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;
    WebElement cookieButton;
    Actions actions;
    SoftAssert softAssert;

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
        actions = new Actions(driver);
        js = (JavascriptExecutor) driver;
        softAssert = new SoftAssert();
    }

    @AfterClass
    public void tearDown() {

        driver.close();
    }

    @Test
    public void movieTest() {
        driver.get(Constants.SWOOP_URL);
        cookieButton = driver.findElement(By.xpath("//div[@class = 'cookieButton']"));
        cookieButton.click();
        driver.findElement(By.xpath("//div[@class = 'HeaderMenu']//a[@href = '/events']")).click();
        //წინასწარ მოგვაქვს პირველი ფილმის სახელი და გადაგვყავს სტრინგად
        WebElement movieName = driver.findElement(By.xpath("//div[@class = 'movie-name']"));
        String movieNameString = movieName.getText();
        //ვდგებით პირველ ფილმზე და ვირჩევთ ყიდვას
        actions.moveToElement(driver.findElement(By.xpath("//div[@class = 'movie-main-img']"))).perform();
        driver.findElement(By.xpath("//div[@class = 'info-cinema-ticket']")).click();
        //ვირჩევთ კავეა ისტ ფოინთს
        WebElement caveaEastPoint = driver.findElement(By.xpath("//a[@id = 'ui-id-5']"));
        caveaEastPoint.click();
        //ვინაიდან ჰტმლ-ში ჩამონათვალში იყო ყველა კინოთეატრის მონაცემი და თარიღი, რომელშიც ეს ფილმი გადიოდა, წამოვიღეთ ისინი და შემდეგში გავფილტრეთ
        List<WebElement> allTicketOptions = driver.findElements(By.xpath("//div[contains(@class,'calendar-tabs')]//div[contains(@id,'day-choose')]"));
        List<WebElement> dates = driver.findElements(By.xpath("//ul[@class = 'tabs ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all']//li//a"));
        //ვქმნით ერეი ლისტს რომელშიც მხოლოდ კავეას ბილეთების ოფშენებს დავამატებთ
        List<WebElement> caveaEastPointTicketOptions = new ArrayList<>();
        String lastDateString = "";
        //და ვაკლიკებთ ბოლო დეითამდე რომელიც დისფლეიდ არის
        for (WebElement date : dates) {
            if (date.isDisplayed()) {
                date.click();
                // და კავეას ბილეთების ლისტში ვამატებთ მხოლოდ იმ ბილეთებს რომლებიც დისფლეიდ არის, შემდეგ გეთ ტექსტს ვაკეთებთ და ვამოწმებთ
                for (WebElement ticketOption : allTicketOptions) {
                    if (ticketOption.isDisplayed()) {
                        caveaEastPointTicketOptions.add(ticketOption);
                        String ticketOptionsText = ticketOption.getText();
                        softAssert.assertTrue(ticketOptionsText.contains(caveaEastPoint.getText()));
                    }
                }
                //ბოლო დეითიდან მოგვაქვს ტექსტი რათა თარიღი შევამოწმოთ
                lastDateString = date.getText();
            }

        }
        //ვიღებთ ციფრებს თარიღის შესამოწმებლად
        String dateDigitsOnly = lastDateString.replaceAll("[^0-9]", "");

        //ბოლო ელემენტს ვიღებთ ლისტში ბოლოს დამატებული ელემენტის საშუალებით
        WebElement lastOption = caveaEastPointTicketOptions.get(caveaEastPointTicketOptions.size() - 1);
        //ვიღებთ ბოლო ოფშენიდან საათს
        WebElement hour = lastOption.findElement(By.xpath("//div[contains(@class,'calendar-tabs')]//div[contains(@id,'day-choose')]//p[1]"));

        lastOption.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'content-header']")));
        //ფოფ აფ მენიუში გახსნილი ტექსტი მოგვაქვს სადაც წერია დრო სახელი და კინოთეატრი და ვაასერტებთ
        String lastOptionFeatures = driver.findElement(By.xpath("//div[@class = 'content-header']")).getText();
        softAssert.assertTrue(lastOptionFeatures.contains(caveaEastPoint.getText()) &&
                lastOptionFeatures.contains(movieNameString) &&
                lastOptionFeatures.contains(dateDigitsOnly) &&
                lastOptionFeatures.contains(hour.getText()));

        //იმ შემთხვევაში თუ ადგილს ზუსტად იმ დროს ჩვენთან ერთად ვინმე ირჩევს და დაგვასწრო, იფრიზება ეს ადგილი და თრაი ქეჩში მაქვს მოქცეული.
        List<WebElement> freeSeats = driver.findElements(By.xpath("//div[@class = 'seat free']//div"));
        for (WebElement freeSeat : freeSeats) {
            try {
                freeSeat.click();
                break;
            } catch (Exception e) {
            }
        }

        WebElement createAccount = driver.findElement(By.xpath("//div[@class = 'create pl-2 ']"));
        wait.until(ExpectedConditions.visibilityOf(createAccount));
        createAccount.click();
        driver.findElement(By.id("email")).sendKeys(Constants.EMAIL);
        driver.findElement(By.id("password")).sendKeys(Constants.PASSWORD);
        driver.findElement(By.id("PasswordRetype")).sendKeys(Constants.PASSWORD);
        driver.findElement(By.xpath("//span[contains(text(),'მდედრობითი')]")).click();
        driver.findElement(By.id("name")).sendKeys(Constants.NAME);
        driver.findElement(By.id("surname")).sendKeys(Constants.SURNAME);
        WebElement birthYear = driver.findElement(By.xpath("//span[contains(text(), 'წელი')]"));
        actions.moveToElement(birthYear).click().perform();
        driver.findElement(By.xpath("//ul[@class ='select2-results__options']//li[21]")).click();
        driver.findElement(By.id("Phone")).sendKeys(Constants.PHONE_NUMBER);
        driver.findElement(By.id("PhoneCode")).sendKeys(Constants.PHONE_CODE);
        WebElement accept = driver.findElement(By.xpath("//div[@class='forms-group check-radio']//span[@class = 'checkmark']"));
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        wait.until(ExpectedConditions.visibilityOf(accept));
        js.executeScript("arguments[0].click();", accept);

        WebElement submitButton = driver.findElement(By.xpath("//button[@type = 'submit']"));
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        js.executeScript("arguments[0].click();", submitButton);
        Assert.assertEquals(driver.findElement(By.id("input-error-email")).getText(), Constants.ERROR_MESSAGE);
        softAssert.assertAll();
    }
}

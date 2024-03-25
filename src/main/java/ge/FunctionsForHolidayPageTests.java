package ge;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunctionsForHolidayPageTests {
    //ამ კოდში ვახდენთ ნექსთ ბათონის საშუალებით ყოველ გვერდზე გადასვლას, თუ იპოვის, სხვა შემთხვევაში კი პირველივე გვერდზე ვეძებთ ფასებს
    public static List<Integer> collectPrices(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        List<String> allPrices = new ArrayList<>();
        try {
            WebElement nextButton = driver.findElement(By.xpath("//img[@src = '/Images/NewDesigneImg/categoryIn/arrow-01.png']/parent::a"));
                while (nextButton.getAttribute("style").contains("auto")) {
                    List<WebElement> prices = driver.findElements(By.xpath("//div[@class = 'discounted-prices']//p[@class = 'deal-voucher-price'][1]"));
                    for (WebElement price : prices) {
                        String priceInString = price.getText();
                        allPrices.add(priceInString);
                    }
                    wait.until(ExpectedConditions.elementToBeClickable(nextButton));
                    nextButton.click();
                    nextButton = driver.findElement(By.xpath("//img[@src = '/Images/NewDesigneImg/categoryIn/arrow-01.png']/parent::a"));
                }
                //ციკლის შემდეგ ბოლო გვერდზე ვხვდდებით რომლიდანაც ასევე ვიღებთ ფასებს
                List<WebElement> lastPageElements = driver.findElements(By.xpath("//div[@class = 'discounted-prices']/p[@class = 'deal-voucher-price'][1]"));
                for (WebElement price : lastPageElements) {
                    String priceInString = price.getText();
                    allPrices.add(priceInString);
                }
                //გადაგვყავს ინტეჯერ ტიპად  და ვამატებთ ლისტში რომ შემდეგ გავაკეთოთ მოქმედებები
                List<Integer> listForIntPrices = new ArrayList<>();
                for (String priceToInt : allPrices) {
                    priceToInt = priceToInt.replace("₾", "").trim();
                    listForIntPrices.add(Integer.valueOf(priceToInt));
                    Collections.sort(listForIntPrices);
                }
                //ვუბრუნდებით პირველ გვერდს
                WebElement toFirstPage = driver.findElement(By.xpath("//div[@class = 'pagination']//div[1]"));
                toFirstPage.click();

                return listForIntPrices;

        } catch (Exception e) {
            //იმ შემთხვევაში თუ ნექსთ ბათონი არაა, მხოლოდ ერთ გვერდზე მოვახდენთ მოქმედებას
            List<WebElement> prices = driver.findElements(By.xpath("//div[@class = 'discounted-prices']//p[@class = 'deal-voucher-price'][1]"));
            for (WebElement price : prices) {
                String priceInString = price.getText();
                allPrices.add(priceInString);
            }
            List<Integer> listForIntPrices = new ArrayList<>();
            for (String priceToInt : allPrices) {
                priceToInt = priceToInt.replace("₾", "").trim();
                listForIntPrices.add(Integer.valueOf(priceToInt));
                Collections.sort(listForIntPrices);
            }

            return listForIntPrices;
        }
}
 // ამ ფუნქიას ვიყენებთ დასორტვის შემდეგ პირველი ელემენტის ფასის ამოსაღებად, რათა შემდეგ შევადაროთ
    public static Integer extractFirstPrice(WebElement priceElement) {
        String priceInString = priceElement.getText();
        priceInString = priceInString.replace("₾", "").trim();
        return Integer.valueOf(priceInString);
    }

    }

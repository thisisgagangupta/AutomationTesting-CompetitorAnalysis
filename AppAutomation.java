import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppAutomation {

    static AppiumDriver driver;

    public static void main(String[] args) throws MalformedURLException {
        openMobileApp();
    }

    public static void openMobileApp() throws MalformedURLException {
        DesiredCapabilities cap = new DesiredCapabilities();

        cap.setCapability("deviceName", "");
        cap.setCapability("udid", "");
        cap.setCapability("platformName", "Android");
        cap.setCapability("platformVersion", "13");
        cap.setCapability("automationName", "uiAutomator2");
        cap.setCapability("appPackage", "");
        cap.setCapability("appActivity", "");

        URL url = new URL("");  //http://127.0.0.1:4723/
        driver = new AppiumDriver(url, cap);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Navigate through the app as before
        WebElement enterButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                new AppiumBy.ByAndroidUIAutomator("new UiSelector().description(\"Login via password\")")));
        enterButton.click();

        driver.findElement(new AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(5)")).click();
        driver.findElement(new AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(5)")).sendKeys("");

        driver.findElement(new AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(6)")).click();
        driver.findElement(new AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(6)")).sendKeys("");

        driver.findElement(new AppiumBy.ByAndroidUIAutomator("new UiSelector().description(\"Login\")")).click();

        // Location access
        WebElement locationAccess = wait.until(ExpectedConditions.visibilityOfElementLocated(
                new AppiumBy.ByAndroidUIAutomator("new UiSelector().resourceId(\"com.android.permissioncontroller:id/permission_allow_foreground_only_button\")")));
        locationAccess.click();

        // Go to Tournaments page
        WebElement checkTournaments = wait.until(ExpectedConditions.visibilityOfElementLocated(
                new AppiumBy.ByAndroidUIAutomator("new UiSelector().description(\"TOURNAMENTS\")")));
        checkTournaments.click();

        // Extract text from the view with scrolling
        Set<String> descriptions = new HashSet<>();
        boolean morePages = true;

        while (morePages) {
            WebElement parentElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    new AppiumBy.ByAndroidUIAutomator("new UiSelector().className(\"android.view.View\").instance(15)")));

            List<WebElement> childElements = parentElement.findElements(AppiumBy.className("android.view.View"));

            boolean foundNewText = false;

            for (WebElement element : childElements) {
                String description = element.getAttribute("contentDescription");
                if (description != null && !description.isEmpty() && descriptions.add(description)) {
                    foundNewText = true;
                }
            }

            // Attempt to scroll down
            try {
                driver.findElement(new AppiumBy.ByAndroidUIAutomator(
                        "new UiScrollable(new UiSelector()).scrollForward()"));
                // If scrolling was successful and new text was found, continue
                if (!foundNewText) {
                    morePages = false;
                }
            } catch (Exception e) {
                // If scrolling fails, assume we reached the end of the list
                morePages = false;
            }
        }

        // Print all collected descriptions
        for (String description : descriptions) {
            System.out.println(description);
            System.out.println("--------------------------------------------------"); // Separator line
        }

        System.out.println("Text extraction completed.");
    }
}

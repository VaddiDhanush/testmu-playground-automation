import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PlaygroundTests {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() throws Exception {
        String username = System.getenv("LT_USERNAME");
        String accessKey = System.getenv("LT_ACCESS_KEY");

        if (username == null || username.isBlank() || accessKey == null || accessKey.isBlank()) {
            throw new IllegalStateException(
                "Configure LT_USERNAME and LT_ACCESS_KEY in your run configuration. Do not hard-code credentials.");
        }

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", "Chrome");
        caps.setCapability("browserVersion", "latest");

        Map<String, Object> options = new HashMap<>();
        options.put("platform", "Windows 11");
        options.put("build", "TestMu Assignment");
        options.put("name", "Playground scenarios");
        options.put("user", username);
        options.put("accessKey", accessKey);
        options.put("network", true);
        options.put("video", true);
        options.put("console", true);
        options.put("visual", true);
        caps.setCapability("LT:Options", options);

        driver = new RemoteWebDriver(new URL("https://hub.lambdatest.com/wd/hub"), caps);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void openPlaygroundAndClick(String linkText) {
        driver.get("https://www.testmuai.com/selenium-playground/");
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText(linkText))).click();
    }

    @Test
    public void simpleFormDemo() {
        openPlaygroundAndClick("Simple Form Demo");
        wait.until(ExpectedConditions.urlContains("simple-form-demo"));

        String message = "Welcome to TestMu AI";
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-message")));
        input.clear();
        input.sendKeys(message);
        driver.findElement(By.id("showInput")).click();

        WebElement result = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        Assert.assertEquals(result.getText().trim(), message, "Displayed message should match entered message.");
    }

    @Test
    public void dragDropSlider() {
        openPlaygroundAndClick("Drag & Drop Sliders");

        // Select the range slider whose initial value is 15, then use keyboard arrows.
        WebElement slider = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("input[type='range'][value='15']")));
        slider.click();

        int current = Integer.parseInt(slider.getDomProperty("value"));
        while (current < 95) {
            slider.sendKeys(Keys.ARROW_RIGHT);
            current = Integer.parseInt(slider.getDomProperty("value"));
        }
        while (current > 95) {
            slider.sendKeys(Keys.ARROW_LEFT);
            current = Integer.parseInt(slider.getDomProperty("value"));
        }

        Assert.assertEquals(slider.getDomProperty("value"), "95",
            "Slider value should be 95.");
    }

    @Test
    public void inputFormSubmit() {
        openPlaygroundAndClick("Input Form Submit");

        // Verify the empty form is blocked by required-field validation.
        WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button[type='submit']")));
        submit.click();

        WebElement nameField = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("input[name='name']")));
        Assert.assertTrue(nameField.getAttribute("required") != null
                || "true".equalsIgnoreCase(nameField.getDomProperty("required")),
            "The name field should be required when the form is submitted empty.");

        // Fill the form. Selectors are based on the standard Selenium Playground form;
        // verify them against the current page if the site markup changes.
        fillFirstAvailable(new By[] { By.name("name"), By.id("name") }, "Test User");
        fillFirstAvailable(new By[] { By.name("email"), By.id("inputEmail4") }, "test.user@example.com");
        fillFirstAvailable(new By[] { By.name("phone"), By.id("inputPhone") }, "9876543210");
        fillFirstAvailable(new By[] { By.name("company"), By.id("company") }, "Example Company");
        fillFirstAvailable(new By[] { By.name("website"), By.id("websitename") }, "example.com");

        WebElement country = findFirstAvailable(new By[] { By.name("country"), By.id("inputState") });
        new Select(country).selectByVisibleText("United States");

        fillFirstAvailable(new By[] { By.name("city"), By.id("inputCity") }, "Austin");
        fillFirstAvailable(new By[] { By.name("address"), By.id("address") }, "123 Main Street");
        fillFirstAvailable(new By[] { By.name("state"), By.id("state") }, "Texas");
        fillFirstAvailable(new By[] { By.name("zip"), By.id("zip") }, "78701");
        fillFirstAvailable(new By[] { By.name("message"), By.cssSelector("textarea") },
            "This is a TestMu AI assignment test.");

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))).click();

        String expected = "Thanks for contacting us, we will get back to you shortly.";
        wait.until(ExpectedConditions.or(
            ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), expected),
            ExpectedConditions.urlContains("input-form-demo")
        ));
        Assert.assertTrue(driver.findElement(By.tagName("body")).getText().contains(expected),
            "Expected successful form submission message.");
    }

    private void fillFirstAvailable(By[] locators, String value) {
        WebElement element = findFirstAvailable(locators);
        element.clear();
        element.sendKeys(value);
    }

    private WebElement findFirstAvailable(By[] locators) {
        for (By locator : locators) {
            try {
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed() && element.isEnabled()) {
                    return element;
                }
            } catch (Exception ignored) {
                // Try the next candidate locator.
            }
        }
        throw new org.openqa.selenium.NoSuchElementException("Could not find any candidate locator: " + java.util.Arrays.toString(locators));
    }
}

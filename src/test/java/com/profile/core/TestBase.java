package com.profile.core;
 
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;
 
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
 
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
 
public class TestBase {
 
    protected WebDriver driver;
    protected WebDriverWait wait;
 
    // base URL
    protected String baseUrl = "https://cosmic-tarsier-245fc5.netlify.app/";
 
    @Parameters({"baseUrl"})
    @BeforeClass(alwaysRun = true)
    public void setUp(@Optional("") String baseUrlParam) {
 
        String sys = System.getProperty("baseUrl");
        if (sys != null && !sys.trim().isEmpty()) {
            this.baseUrl = sys.trim();
        } else if (baseUrlParam != null && !baseUrlParam.trim().isEmpty()) {
            this.baseUrl = baseUrlParam.trim();
        }
 
        WebDriverManager.chromedriver().setup();
 
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
 
        driver = new ChromeDriver(options);
        wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
 
        driver.get(this.baseUrl);
    }
 
    @BeforeMethod(alwaysRun = true)
    public void goHome() {
        if (driver != null) {
            driver.get(this.baseUrl);
        }
    }
 
    /**
     * TAKE SCREENSHOT AFTER EVERY ITERATION
     * PASS OR FAIL
     * Folder auto-created: target/screenshots/
     */
    @AfterMethod(alwaysRun = true)
    public void takeScreenshot(ITestResult result) {
 
        if (driver == null) return;
 
        try {
            // Create folder if needed
            Path folder = Paths.get("target", "screenshots");
            Files.createDirectories(folder);
 
            String methodName = result.getMethod().getMethodName();
            String status     = (result.getStatus() == ITestResult.SUCCESS) ? "PASS" : "FAIL";
            String timestamp  = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
 
            String fileName = methodName + "_" + status + "_" + timestamp + ".png";
            Path filePath = folder.resolve(fileName);
 
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(filePath, screenshot);
 
            System.out.println("📸 Screenshot saved: " + filePath.toAbsolutePath());
 
        } catch (Exception e) {
            System.out.println("❌ Screenshot error: " + e.getMessage());
        }
    }
 
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
 
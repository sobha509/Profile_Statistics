package com.profile.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
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

    protected String baseUrl = "https://cosmic-tarsier-245fc5.netlify.app/";

    @Parameters({"baseUrl", "browser"})
    @BeforeClass(alwaysRun = true)
    public void setUp(@Optional("") String baseUrlParam,
                      @Optional("chrome") String browser) {

        // Allow override from XML
        if (baseUrlParam != null && !baseUrlParam.trim().isEmpty()) {
            this.baseUrl = baseUrlParam.trim();
        }

        // -----------------------------------------------------------
        // JENKINS CHROME FIX (Chrome version 145)
        // -----------------------------------------------------------
        if (browser.equalsIgnoreCase("chrome")) {

            // Force ChromeDriver 145 to match Jenkins Chrome
            WebDriverManager.chromedriver().driverVersion("145.0.0").setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.setPageLoadStrategy(PageLoadStrategy.EAGER);

            driver = new ChromeDriver(options);
        }
        // Edge support (optional)
        else if (browser.equalsIgnoreCase("edge")) {

            WebDriverManager.edgedriver().setup();

            EdgeOptions options = new EdgeOptions();
            options.addArguments("--start-maximized");
            options.setPageLoadStrategy(PageLoadStrategy.EAGER);

            driver = new EdgeDriver(options);
        }
        else {
            throw new RuntimeException("Unsupported browser: " + browser);
        }

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get(this.baseUrl);
    }

    @BeforeMethod(alwaysRun = true)
    public void goHome() {
        driver.get(this.baseUrl);
    }

    // -----------------------------------------------------------
    // SCREENSHOT ONLY FOR PASSED TEST CASES (your requirement)
    // -----------------------------------------------------------
    @AfterMethod(alwaysRun = true)
    public void takeScreenshot(ITestResult result) {

        if (driver == null) return;

        try {
            // Only PASS screenshots
            if (result.getStatus() != ITestResult.SUCCESS) {
                return;
            }

            Path folder = Paths.get("target", "screenshots");
            Files.createDirectories(folder);

            String methodName = result.getMethod().getMethodName();
            String timestamp =
                    new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());

            String fileName = methodName + "_PASS_" + timestamp + ".png";
            Path filePath = folder.resolve(fileName);

            byte[] bytes =
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            Files.write(filePath, bytes);

            System.out.println("📸 PASS Screenshot saved: " + filePath.toAbsolutePath());

        } catch (Exception e) {
            System.out.println("❌ Screenshot error: " + e.getMessage());
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
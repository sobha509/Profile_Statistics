package com.profile.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;

public class TestBase {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // Default base URL; can be overridden by -DbaseUrl=... or @Parameters
    protected String baseUrl = "https://cosmic-tarsier-245fc5.netlify.app/";

    @Parameters({"baseUrl"})
    @BeforeClass(alwaysRun = true)
    public void setUp(@Optional("") String baseUrlParam) {
        // Resolve base URL (System property > TestNG parameter > default)
        String sys = System.getProperty("baseUrl");
        if (sys != null && !sys.trim().isEmpty()) {
            this.baseUrl = sys.trim();
        } else if (baseUrlParam != null && !baseUrlParam.trim().isEmpty()) {
            this.baseUrl = baseUrlParam.trim();
        }

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER); // proceed after DOMContentLoaded

        driver = new ChromeDriver(options);
        wait   = new WebDriverWait(driver, Duration.ofSeconds(20));

        // First navigation
        driver.get(this.baseUrl);
    }

    /** Ensure every data-driven iteration starts fresh from the landing page */
    @BeforeMethod(alwaysRun = true)
    public void goHome() {
        if (driver != null) {
            driver.get(this.baseUrl);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
package com.profile.tests;

import java.util.Arrays;
import java.util.Iterator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.profile.core.TestBase;
import com.profile.pages.InputFormPage;
import com.profile.pages.LandingPage;
import com.profile.pages.ResultsPage;
import com.profile.utils.ExcelReader;

public class ProfileTest extends TestBase {

    @DataProvider(name = "fourUsers", parallel = false)
    public Iterator<Object[]> fourUsers() {
        try {
            Object[][] data = ExcelReader.readExactlyFourRows(
                "testdata/PersonData_Four_corrected.xlsx",
                "Person"
            );

            if (data == null || data.length != 4) {
                throw new SkipException("Expected exactly 4 rows, got " +
                        (data == null ? 0 : data.length));
            }
            return Arrays.asList(data).iterator();

        } catch (SkipException se) {
            throw se;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SkipException("DataProvider failed: " + e.getMessage(), e);
        }
    }

    // ---------------------------------------------------------
    // POSITIVE DATA-DRIVEN TEST
    // ---------------------------------------------------------

    @Test(dataProvider = "fourUsers")
    public void testProfileStatus_Four(String years,
                                       int websites,
                                       int apps,
                                       String expectedStatus,
                                       String expectedPersona) {

        System.out.printf("RUN => years=%s, websites=%d, apps=%d, expected=%s%n",
                years, websites, apps, expectedStatus);

        LandingPage landing = new LandingPage(driver, wait);
        InputFormPage form  = landing.clickGetInsights();

        form.selectYears(years);
        form.enterWebsites(websites);
        form.enterApps(apps);

        ResultsPage results = form.clickMeasure();
        String actualStatus = results.getStatus();
        System.out.println("STATUS = " + actualStatus);

        Assert.assertTrue(
            actualStatus.toLowerCase().contains(expectedStatus.toLowerCase()),
            "Expected Status: " + expectedStatus + " | Actual: " + actualStatus
        );
    }

    // ---------------------------------------------------------
    // NEGATIVE TEST 1 — Invalid Years (NO selection)
    // ---------------------------------------------------------
    @Test
    public void testInvalidYears() {

        LandingPage landing = new LandingPage(driver, wait);
        InputFormPage form  = landing.clickGetInsights();

        // Don't select years
        form.enterWebsites(5);
        form.enterApps(2);

        form.clickMeasure();

        Assert.assertTrue(form.isErrorVisible(),
                "Error should appear when Years is not selected.");
    }


    // ---------------------------------------------------------
    // NEGATIVE TEST 2 — Non-numeric Websites
    // ---------------------------------------------------------
    @Test
    public void testNonNumericWebsites() {

        LandingPage landing = new LandingPage(driver, wait);
        InputFormPage form  = landing.clickGetInsights();

        form.selectYears("3 Years");

        // Force non-numeric input
        WebElement siteInput = driver.findElement(By.xpath("(//input[@type='number'])[1]"));
        siteInput.clear();
        siteInput.sendKeys("abc");

        form.enterApps(3);
        form.clickMeasure();

        Assert.assertTrue(form.isErrorVisible(),
                "Error should appear for non-numeric websites.");
    }

    // ---------------------------------------------------------
    // NEGATIVE TEST 3 — Zero Websites
    // ---------------------------------------------------------
    @Test
    public void testZeroWebsites() {

        LandingPage landing = new LandingPage(driver, wait);
        InputFormPage form  = landing.clickGetInsights();

        form.selectYears("3 Years");
        form.enterWebsites(0);
        form.enterApps(3);

        form.clickMeasure();

        Assert.assertTrue(form.isErrorVisible(),
                "Error should appear when Websites = 0.");
    }

    // ---------------------------------------------------------
    // NEGATIVE TEST 4 — Zero Apps
    // ---------------------------------------------------------
    @Test
    public void testZeroApps() {

        LandingPage landing = new LandingPage(driver, wait);
        InputFormPage form  = landing.clickGetInsights();

        form.selectYears("3 Years");
        form.enterWebsites(5);
        form.enterApps(0);

        form.clickMeasure();

        Assert.assertTrue(form.isErrorVisible(),
                "Error should appear when Apps = 0.");
    }
}
package com.profile.tests;

import java.util.Arrays;
import java.util.Iterator;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.profile.core.TestBase;
import com.profile.pages.InputFormPage;
import com.profile.pages.LandingPage;
import com.profile.pages.ResultsPage;
import com.profile.utils.SingleRowExcelReader;

/**
 * Runs once per row in Corrected_PersonData_FourUsers.xlsx (4 users).
 * Excel must be at: src/test/resources/testdata/Corrected_PersonData_FourUsers.xlsx
 * Sheet: Person
 */
public class ProfileTest extends TestBase {

    @DataProvider(name = "fourUsers", parallel = false)
    public Iterator<Object[]> fourUsers() {
        try {
            Object[][] data = SingleRowExcelReader.readExactlyFourRows(
                    "testdata/PersonData_Four_corrected.xlsx", "Person"
            );

            if (data == null || data.length != 4) {
                throw new SkipException("Expected exactly 4 rows, got " + (data == null ? 0 : data.length));
            }
            return Arrays.asList(data).iterator();

        } catch (SkipException se) {
            throw se;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SkipException("DataProvider failed: " + e.getMessage(), e);
        }
    }

    @Test(dataProvider = "fourUsers", description = "Validate status for four Excel rows")
    public void testProfileStatus_Four(String years,
                                       int websites,
                                       int apps,
                                       String expectedStatus,
                                       String expectedPersona /* optional */) {

        System.out.printf("RUN => years=%s, websites=%d, apps=%d, expected=%s%n",
                years, websites, apps, expectedStatus);

        // 1) Navigate to input form (your POM)
        LandingPage landing = new LandingPage(driver, wait);
        InputFormPage form  = landing.clickGetInsights();

        // 2) Fill and submit
        form.selectYears(years);
        form.enterWebsites(websites);
        form.enterApps(apps);

        ResultsPage results = form.clickMeasure();

        // 3) Assert
        String actualStatus = results.getStatus();
        System.out.println("STATUS = " + actualStatus);

        Assert.assertTrue(
            actualStatus.toLowerCase().contains(expectedStatus.toLowerCase()),
            "Expected Status: " + expectedStatus + " | Actual: " + actualStatus
        );

        // OPTIONAL persona assert (enable if you added ResultsPage.getPersona())
        // String actualPersona = results.getPersona();
        // Assert.assertTrue(
        //     actualPersona.toLowerCase().contains(expectedPersona.toLowerCase()),
        //     "Expected Persona: " + expectedPersona + " | Actual: " + actualPersona
        // );
    }
}
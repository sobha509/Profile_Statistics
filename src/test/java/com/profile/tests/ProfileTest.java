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
 * Reads EXACTLY two rows and runs the flow once per row.
 * File must be at: src/test/resources/PersonData_Two.xlsx
 * Sheet name: Person
 */
public class ProfileTest extends TestBase {

    @DataProvider(name = "twoUsers", parallel = false)
    public Iterator<Object[]> twoUsers() {
        try {
            String file = "src/test/resources/PersonData_Two.xlsx"; // ensure this exists
            Object[][] data = SingleRowExcelReader.readExactlyTwoRows(file, "Person");

            if (data == null || data.length != 2) {
                throw new SkipException("Expected exactly 2 rows, got " + (data == null ? 0 : data.length));
            }
            // Convert Object[][] to Iterator<Object[]>
            return Arrays.asList(data).iterator();

        } catch (SkipException se) {
            throw se;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SkipException("DataProvider failed: " + e.getMessage(), e);
        }
    }

    /**
     * Each invocation uses: years, websites, apps, expectedStatus, expectedPersona
     */
    @Test(dataProvider = "twoUsers", description = "Validate status for exactly two Excel rows")
    public void testProfileStatus_ForTwoUsers(String years,
                                              int websites,
                                              int apps,
                                              String expectedStatus,
                                              String expectedPersona /* optional */) {

        System.out.printf("RUN => years=%s, websites=%d, apps=%d, expected=%s%n",
                years, websites, apps, expectedStatus);

        // 1) POM flow
        LandingPage landing = new LandingPage(driver, wait);
        InputFormPage form  = landing.clickGetInsights(); // ensure your POM waits for form load

        form.selectYears(years);
        form.enterWebsites(websites);
        form.enterApps(apps);

        ResultsPage result = form.clickMeasure();
        String actualStatus = result.getStatus();
        System.out.println("STATUS = " + actualStatus);

        Assert.assertTrue(
            actualStatus.toLowerCase().contains(expectedStatus.toLowerCase()),
            "Expected Status: " + expectedStatus + " | Actual: " + actualStatus
        );

        // OPTIONAL: If you have getPersona() in ResultsPage:
        // String actualPersona = result.getPersona();
        // Assert.assertTrue(
        //     actualPersona.toLowerCase().contains(expectedPersona.toLowerCase()),
        //     "Expected Persona: " + expectedPersona + " | Actual: " + actualPersona
        // );
    }
}
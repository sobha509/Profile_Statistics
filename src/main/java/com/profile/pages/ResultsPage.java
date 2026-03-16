package com.profile.pages;

import com.profile.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Results page object.
 * Reads the profile status after waiting for it to become visible.
 */
public class ResultsPage extends BasePage {

    // Matches any of the five statuses; normalize-space for resilience
    private final By statusLocator = By.xpath(
        "//*[contains(normalize-space(.),'Emerging') " +
        " or contains(normalize-space(.),'Growing') " +
        " or contains(normalize-space(.),'Proficient') " +
        " or contains(normalize-space(.),'Advanced') " +
        " or contains(normalize-space(.),'Expert')]"
    );

    public ResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    /** Blocks until the status element is visible */
    public ResultsPage waitUntilResultsLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(statusLocator));
        return this;
    }

    /** Returns the status text after ensuring visibility */
    public String getStatus() {
        return waitUntilResultsLoaded()
               .waitFor(statusLocator)
               .getText();
    }
}
package com.profile.pages;

import com.profile.core.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ResultsPage extends BasePage {

    private final By statusLabel = By.xpath(
        "//*[contains(text(),'Emerging') or contains(text(),'Growing') or contains(text(),'Proficient') or contains(text(),'Advanced') or contains(text(),'Expert')]"
    );

    public ResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public String getStatus() {
        return waitFor(statusLabel).getText();
    }
}

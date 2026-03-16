package com.profile.pages;

import com.profile.core.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

public class InputFormPage extends BasePage {

    private final By yearsSelect = By.xpath("//select");
    private final By websitesInput = By.xpath("(//input[@type='number'])[1]");
    private final By appsInput = By.xpath("(//input[@type='number'])[2]");
    private final By measureBtn = By.xpath("//button[contains(., 'Measure Profile')]");

    public InputFormPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void selectYears(String years) {
        new Select(waitFor(yearsSelect)).selectByVisibleText(years);
    }

    public void enterWebsites(int sites) {
        type(websitesInput, String.valueOf(sites));
    }

    public void enterApps(int apps) {
        type(appsInput, String.valueOf(apps));
    }

    public ResultsPage clickMeasure() {
        click(measureBtn);
        return new ResultsPage(driver, wait);
    }
}
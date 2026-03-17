package com.profile.pages;

import com.profile.core.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class InputFormPage extends BasePage {

    private final By yearsLabel  = By.xpath("//*[contains(normalize-space(.),'Years of Experience')]");
    private final By measureBtn  = By.xpath("//button[contains(normalize-space(.),'Measure Profile')]");

    private final By yearsSelect = By.xpath(
        "//label[contains(normalize-space(.),'Years of Experience')]/following::*[self::select][1]"
    );

    private final By yearsCustomTrigger = By.xpath(
        "((//label[contains(normalize-space(.),'Years of Experience')])[1]/following::*" +
        "[(self::div or self::button or self::span) and " +
        "(contains(@role,'combobox') or contains(@class,'select') or contains(@class,'dropdown'))][1])"
    );

    private By customOption(String text) {
        return By.xpath(
            "//li[normalize-space(.)='" + text + "']" +
            " | //div[normalize-space(.)='" + text + "']" +
            " | //span[normalize-space(.)='" + text + "']"
        );
    }

    private final By websitesInput = By.xpath("(//input[@type='number'])[1]");
    private final By appsInput     = By.xpath("(//input[@type='number'])[2]");

    public InputFormPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public InputFormPage waitUntilLoaded() {
        wait.until(ExpectedConditions.or(
            ExpectedConditions.visibilityOfElementLocated(yearsLabel),
            ExpectedConditions.visibilityOfElementLocated(measureBtn)
        ));
        return this;
    }

    public void selectYears(String yearsText) {
        if (!driver.findElements(yearsSelect).isEmpty()) {
            new Select(waitFor(yearsSelect)).selectByVisibleText(yearsText);
            return;
        }
        WebElement trigger = waitClickable(yearsCustomTrigger);
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block:'center'});", trigger);
        trigger.click();

        By option = customOption(yearsText);
        waitClickable(option).click();
    }

    public void enterWebsites(int sites) {
        WebElement el = waitFor(websitesInput);
        el.click();
        el.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        el.sendKeys(String.valueOf(sites));
    }

    public void enterApps(int apps) {
        WebElement el = waitFor(appsInput);
        el.click();
        el.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        el.sendKeys(String.valueOf(apps));
    }

    public ResultsPage clickMeasure() {
        try {
            waitClickable(measureBtn).click();
        } catch (Exception e) {
            click(measureBtn);
        }
        return new ResultsPage(driver, wait);
    }

    // ---------------- NEGATIVE TEST SUPPORT ----------------

    private WebElement waitClickable(By measureBtn2) {
		// TODO Auto-generated method stub
		return null;
	}

	private final By errorMessageLocator = By.xpath(
        "//*[contains(@class,'error') or contains(@class,'invalid') or contains(@id,'error')]"
    );

    public boolean isErrorVisible() {
        return !driver.findElements(errorMessageLocator).isEmpty();
    }

    public String getErrorText() {
        if (isErrorVisible()) {
            return driver.findElement(errorMessageLocator).getText().trim();
        }
        return "";
    }
}
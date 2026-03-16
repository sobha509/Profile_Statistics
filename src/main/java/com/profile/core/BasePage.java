package com.profile.core;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    protected WebElement waitFor(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        waitFor(locator).click();
    }

    protected void type(By locator, String value) {
        WebElement el = waitFor(locator);
        el.clear();
        el.sendKeys(value);
    }
}
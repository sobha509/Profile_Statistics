package com.profile.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.profile.core.BasePage;

public class LandingPage extends BasePage {

    private final By getInsightsBtn = By.xpath("//div[text()='Get Insights']");

    public LandingPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public InputFormPage clickGetInsights() {
        try {
            click(getInsightsBtn);
        } catch (Exception e) {
            click(getInsightsBtn); // fallback in case of animation overlay
        }
        return new InputFormPage(driver, wait);
    }
}
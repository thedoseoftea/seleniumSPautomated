package cz.fedorcak.rukovoditel.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DATA {
    public String rukovoditelUrl = "https://digitalnizena.cz/rukovoditel/";
    public String userName = "rukovoditel";
    public String validPassword = "vse456ru";
    public String invalidPassword = "tra-la-la";

    public void shouldLoginUsingValidCredentials(ChromeDriver driver) {
        // Given
        driver.get(rukovoditelUrl);

        // When
        WebElement usernameInput = driver.findElement(By.name("username"));
        usernameInput.sendKeys(userName);
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys(validPassword);
        WebElement loginButton = driver.findElement(By.className("btn-info"));
        loginButton.click();
    }


    public void dealWithPossibleUnresetedSearch(ChromeDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='table table-striped table-bordered table-hover']")));
        if (driver.findElements(By.xpath("//span[contains(text(),'Reset Search')]")).size() != 0) {
            System.out.println("Resetting search");
            WebElement resetSearchSpan = driver.findElement(By.xpath("//span[contains(text(),'Reset Search')]"));
            resetSearchSpan.click();
        }
    }
}

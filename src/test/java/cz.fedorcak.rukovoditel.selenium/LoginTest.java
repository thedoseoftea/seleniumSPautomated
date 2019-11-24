package cz.fedorcak.rukovoditel.selenium;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
public class LoginTest {
    private ChromeDriver driver;
    private DATA data = new DATA();

    @Before
    public void init() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @After
    public void tearDown() {
        driver.close();
    }

    @Test
    public void shouldLoginUsingValidCredentials() {
        // Given
        driver.get(data.rukovoditelUrl);

        // When
        WebElement usernameInput = driver.findElement(By.name("username"));
        usernameInput.sendKeys(data.userName);
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys(data.validPassword);
        WebElement loginButton = driver.findElement(By.className("btn-info"));
        loginButton.click();

        // Then
        Assert.assertTrue(driver.getTitle().startsWith("Rukovoditel | Dashboard"));
    }

    @Test
    public void shouldNotLoginUsingInvalidCredentials() {
        // Given
        driver.get(data.rukovoditelUrl);

        // When
        WebElement usernameInput = driver.findElement(By.name("username"));
        usernameInput.sendKeys(data.userName);
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys(data.invalidPassword);
        WebElement loginButton = driver.findElement(By.className("btn-info"));
        loginButton.click();

        // Then
        WebElement errorMessageDiv = driver.findElement(By.className("alert-danger"));
        Assert.assertTrue(errorMessageDiv.getText().contains("No match for Username and/or Password."));
    }

    @Test
    public void shouldBeAbleToLogOff() {
        // Given
        shouldLoginUsingValidCredentials();

        // When
        WebElement userDropdownArrow = driver.findElement(By.xpath("//li[@class='dropdown user']//a[@class='dropdown-toggle']"));
        userDropdownArrow.click();
        WebElement logOffButton = driver.findElement(By.xpath("//a[@href='https://digitalnizena.cz/rukovoditel/index.php?module=users/login&action=logoff']"));
        logOffButton.click();

        // Then
        Assert.assertTrue(driver.getTitle().equals("Rukovoditel")); // We get back to the main page
    }
}

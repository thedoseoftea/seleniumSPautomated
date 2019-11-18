package cz.fedorcak.rukovoditel.selenium;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.UUID;

public class ProjectsTest {

    private ChromeDriver driver;
    private String rukovoditelUrl = "https://digitalnizena.cz/rukovoditel/";
    private String userName = "rukovoditel";
    private String validPassword = "vse456ru";

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
    public void shouldNotBeAbleToCreateAProjectWithoutName() {
        // Given
        shouldLoginUsingValidCredentials();

        // When
        WebElement projectsSpan = driver.findElement(By.xpath("//a//span[contains(text(),'Projects')]"));
        projectsSpan.click();
        WebElement addProjectButton = driver.findElement(By.xpath("//button[contains(text(),'Add Project')]"));
        addProjectButton.click();
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Save')]")));
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),'Save')]"));
        saveButton.click();

        // Then
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(text(),'This field is required!')]")));
    }

    @Test
    public void shouldBeAbleToCreateANewProjectWithValidData() {
        // Given
        shouldLoginUsingValidCredentials();

        // When
        WebElement projectsSpan = driver.findElement(By.xpath("//a//span[contains(text(),'Projects')]"));
        projectsSpan.click();
        WebElement addProjectButton = driver.findElement(By.xpath("//button[contains(text(),'Add Project')]"));
        addProjectButton.click();
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Save')]")));

        WebElement prioritySelectElement = driver.findElement(By.name("fields[156]"));
        Select prioritySelect = new Select(prioritySelectElement);
        prioritySelect.selectByVisibleText("High");
        // Status is set to New by default
        WebElement nameInput = driver.findElement(By.name("fields[158]"));
        String uuid = UUID.randomUUID().toString();
        nameInput.sendKeys("fedj00" + uuid);

        WebElement calendarButton = driver.findElement(By.className("date-set"));
        calendarButton.click();
        WebElement activeDayTd = driver.findElement(By.xpath("//td[@class='active day']"));
        activeDayTd.click();
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),'Save')]"));
        saveButton.click();

        // Then
        // We verify the existence of the project
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a//span[contains(text(),'Projects')]")));
        WebElement projectsSpan2 = driver.findElement(By.xpath("//a//span[contains(text(),'Projects')]"));
        projectsSpan2.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Search']")));
        dealWithPossibleUnresetedSearch();
        WebElement searchInput = driver.findElement(By.xpath("//input[@placeholder='Search']"));
        searchInput.sendKeys("fedj00" + uuid);
        WebElement magnifyingGlassButton = driver.findElement(By.xpath("//button[@title='Search']//i[@class='fa fa-search']"));
        magnifyingGlassButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Reset Search')]")));
        List<WebElement> elements = driver.findElements(By.cssSelector("table tr"));
        WebElement projectTableRow = elements.get(1);
        Assert.assertTrue(projectTableRow.getText().contains(uuid));

        // Cleanup
        WebElement trashCanButton = driver.findElement(By.xpath("//a[@title='Delete']//i[@class='fa fa-trash-o']"));
        trashCanButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Delete') and @type='submit']")));

        if (driver.findElements( By.id("delete_confirm") ).size() != 0) {
            WebElement deleteConfirmInput = driver.findElement(By.id("delete_confirm"));
            deleteConfirmInput.click();
        }

        WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete') and @type='submit']"));
        deleteButton.click();
        dealWithPossibleUnresetedSearch();
    }




    public void shouldLoginUsingValidCredentials() {
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

    private void dealWithPossibleUnresetedSearch() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='table table-striped table-bordered table-hover']")));
        if (driver.findElements(By.xpath("//span[contains(text(),'Reset Search')]")).size() != 0) {
            System.out.println("Resetting search");
            WebElement resetSearchSpan = driver.findElement(By.xpath("//span[contains(text(),'Reset Search')]"));
            resetSearchSpan.click();
        }
    }
}

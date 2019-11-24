package cz.fedorcak.rukovoditel.selenium;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
public class TaskTest {

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
    public void shouldBeAbleToCreateTask() {
        // Given
        shouldLoginUsingValidCredentials();

        // When
        WebElement projectsSpan = driver.findElement(By.xpath("//a//span[contains(text(),'Projects')]"));
        projectsSpan.click();

        dealWithPossibleUnresetedSearch();
        WebElement searchInput = driver.findElement(By.xpath("//input[@placeholder='Search']"));
        searchInput.sendKeys("fedorcak");
        WebElement magnifyingGlassButton = driver.findElement(By.xpath("//button[@title='Search']//i[@class='fa fa-search']"));
        magnifyingGlassButton.click();
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Reset Search')]")));
        WebElement fedorcakA = driver.findElement(By.xpath("//a[contains(text(),'fedorcak')]"));
        fedorcakA.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Add Task')]")));
        WebElement addTaskButton = driver.findElement(By.xpath("//button[contains(text(),'Add Task')]"));
        addTaskButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Save')]")));
        WebElement nameInput = driver.findElement(By.name("fields[168]"));
        String uuid = UUID.randomUUID().toString();
        nameInput.sendKeys("fedj00" + uuid);
        // Status is set to New by default
        // Priority is set to Medium by default
        WebElement descriptionIframe = driver.findElement(By.xpath("//iframe[@title='Rich Text Editor, fields_172']"));
        driver.switchTo().frame(descriptionIframe);
        WebElement body = driver.findElement(By.xpath("//body"));
        body.click();
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("arguments[0].innerHTML = '"+ "I wish I was in Reykjavík looking for Swans"+"'", body);
        driver.switchTo().defaultContent();
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),'Save')]"));
        saveButton.click();

        // Then
        // Verify if it exists
        dealWithPossibleUnresetedSearch();
        WebElement searchInput2 = driver.findElement(By.xpath("//input[@placeholder='Search']"));
        searchInput2.sendKeys("fedj00" + uuid);
        WebElement magnifyingGlassButton2 = driver.findElement(By.xpath("//button[@title='Search']//i[@class='fa fa-search']"));
        magnifyingGlassButton2.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Reset Search')]")));

        WebElement infoButton = driver.findElement(By.xpath("//a[@title='Info']//i[@class='fa fa-info']"));
        infoButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='caption']")));
        WebElement taskTr = driver.findElement(By.className("form-group-167"));
        Assert.assertTrue(taskTr.getText().contains("Task"));
        WebElement statusTr = driver.findElement(By.className("form-group-169"));
        Assert.assertTrue(statusTr.getText().contains("New"));
        WebElement priorityTr = driver.findElement(By.className("form-group-170"));
        Assert.assertTrue(priorityTr.getText().contains("Medium"));
        WebElement captionDiv = driver.findElement((By.xpath("//div[@class='caption']")));
        Assert.assertEquals(captionDiv.getText(), "fedj00" + uuid);
        WebElement descriptionDiv = driver.findElement((By.className("form-group-172")));
        Assert.assertTrue(descriptionDiv.getText().contains("I wish I was in Reykjavík looking for Swans"));

        // Cleanup
        WebElement moreActionsButton = driver.findElement(By.xpath("//button[@class='btn btn-default btn-sm dropdown-toggle']"));
        moreActionsButton.click();
        WebElement deleteButton = driver.findElement(By.xpath("//a//i[@class='fa fa-trash-o']"));
        deleteButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='ajax-modal' and @aria-hidden='false']")));
        WebElement confirmDeleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')]"));
        confirmDeleteButton.click();


        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a//span[contains(text(),'Projects')]")));
        WebElement projectsSpan3 = driver.findElement(By.xpath("//a//span[contains(text(),'Projects')]"));
        projectsSpan3.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'fedorcak')]")));
        WebElement fedorcakA2 = driver.findElement(By.xpath("//a[contains(text(),'fedorcak')]"));
        fedorcakA2.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[contains(text(),'Tasks')]")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Reset Search')]")));
        WebElement resetSearchSpan = driver.findElement(By.xpath("//span[contains(text(),'Reset Search')]"));
        resetSearchSpan.click();

        WebElement projectsSpan4 = driver.findElement(By.xpath("//a//span[contains(text(),'Projects')]"));
        projectsSpan4.click();
        dealWithPossibleUnresetedSearch();
    }

    @Test
    public void shouldFilterTasksCorrectly() {
        // Given
        shouldLoginUsingValidCredentials();

        // When
        // Locate the fedorcak project
        WebElement projectsSpan = driver.findElement(By.xpath("//a//span[contains(text(),'Projects')]"));
        projectsSpan.click();
        dealWithPossibleUnresetedSearch();
        WebElement searchInput = driver.findElement(By.xpath("//input[@placeholder='Search']"));
        searchInput.sendKeys("fedorcak");
        WebElement magnifyingGlassButton = driver.findElement(By.xpath("//button[@title='Search']//i[@class='fa fa-search']"));
        magnifyingGlassButton.click();
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Reset Search')]")));
        WebElement fedorcakA = driver.findElement(By.xpath("//a[contains(text(),'fedorcak')]"));
        fedorcakA.click();

        createTask("New");
        createTask("Open");
        createTask("Waiting");
        createTask("Done");
        createTask("Closed");
        createTask("Paid");
        createTask("Canceled");

        // Setup the default filter
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Add Task')]")));
        WebElement filterArrowButton = driver.findElement(By.xpath("//button[@class='btn dropdown-toggle btn-users-filters' and not(@title)]"));
        filterArrowButton.click();
        WebElement defaultFiltersA = driver.findElement(By.xpath("//a[contains(text(),'Default Filters')]"));
        defaultFiltersA.click();


        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Add Task')]")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='table table-striped table-bordered table-hover']")));
        // Testing default filter
        // Then
        Assert.assertEquals(driver.findElements( By.xpath("//tr") ).size(),4); // Expect 3 results in the table + 1 row in the header

        // When
        // Changing the filter
        WebElement filtersSpan = driver.findElement(By.xpath("//span[@class='filters-preview-box-heading']"));
        filtersSpan.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='ajax-modal' and @aria-hidden='false']"))); // Wait for modal to show up
        WebElement openA = driver.findElement(By.xpath("//li[@class='search-choice']//span[contains(text(),'Open')]/following-sibling::a")); // Find search choice whose sibling is a span with text "Open"
        openA.click();
        WebElement saveButton2 = driver.findElement(By.xpath("//button[contains(text(),'Save')]"));
        saveButton2.click();
        // Then
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='table table-striped table-bordered table-hover']")));
        Assert.assertEquals(driver.findElements( By.xpath("//tr") ).size(),3); // Expect 2 results in the table + 1 row in the header
        WebElement resultTable = driver.findElement(By.xpath("//table[@class='table table-striped table-bordered table-hover']"));
        Assert.assertTrue(resultTable.getText().contains("New"));
        Assert.assertTrue(resultTable.getText().contains("Waiting"));
        Assert.assertFalse(resultTable.getText().contains("Open"));
        Assert.assertFalse(resultTable.getText().contains("Done"));
        Assert.assertFalse(resultTable.getText().contains("Closed"));
        Assert.assertFalse(resultTable.getText().contains("Paid"));
        Assert.assertFalse(resultTable.getText().contains("Canceled"));

        // When
        WebElement deleteFiltersA = driver.findElement(By.xpath("//a[@href]/child::i[@class='fa fa-trash-o' and @title='Remove Filter']"));
        deleteFiltersA.click();
        // Then
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//span[@class='filters-preview-condition-include']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='table table-striped table-bordered table-hover']")));
        Assert.assertEquals(driver.findElements( By.xpath("//tr") ).size(),8); // Expect 7 results in the table + 1 row in the header
        WebElement resultTable2 = driver.findElement(By.xpath("//table[@class='table table-striped table-bordered table-hover']"));
        Assert.assertTrue(resultTable2.getText().contains("New"));
        Assert.assertTrue(resultTable2.getText().contains("Waiting"));
        Assert.assertTrue(resultTable2.getText().contains("Open"));
        Assert.assertTrue(resultTable2.getText().contains("Done"));
        Assert.assertTrue(resultTable2.getText().contains("Closed"));
        Assert.assertTrue(resultTable2.getText().contains("Paid"));
        Assert.assertTrue(resultTable2.getText().contains("Canceled"));


        // Batch delete:
        WebElement selectAllItemsInput = driver.findElement(By.name("select_all_items"));
        selectAllItemsInput.click();
        WebElement withSelected = driver.findElement(By.xpath("//button[@class='btn btn-default dropdown-toggle' and contains(text(),'With Selected')]"));
        withSelected.click();
        WebElement deleteSelected = driver.findElement(By.xpath("//a[@class=' link-to-modalbox']//i[@class='fa fa-trash-o']"));
        deleteSelected.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='ajax-modal' and @aria-hidden='false']")));
        WebElement deleteButton = driver.findElement(By.xpath("//button[@type='submit' and @class='btn btn-primary btn-primary-modal-action']"));
        deleteButton.click();


        // Cleanup
        WebElement projectsSpan4 = driver.findElement(By.xpath("//a//span[contains(text(),'Projects')]"));
        projectsSpan4.click();
        dealWithPossibleUnresetedSearch();
    }

    private void createTask(String status) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Add Task')]")));
        WebElement addTaskButton = driver.findElement(By.xpath("//button[contains(text(),'Add Task')]"));
        addTaskButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Save')]")));
        WebElement nameInput = driver.findElement(By.name("fields[168]"));
        String uuid = UUID.randomUUID().toString();
        nameInput.sendKeys("fedj00" + uuid);

        // Status is set to New by default
        if (!status.equals("New")) {
            WebElement statusSelectElement = driver.findElement(By.name("fields[169]"));
            Select statusSelect = new Select(statusSelectElement);
            statusSelect.selectByVisibleText(status);
        }
        // Priority is set to Medium by default
        WebElement descriptionIframe = driver.findElement(By.xpath("//iframe[@title='Rich Text Editor, fields_172']"));
        driver.switchTo().frame(descriptionIframe);
        WebElement body = driver.findElement(By.xpath("//body"));
        body.click();
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("arguments[0].innerHTML = '"+ "I wish I was in Reykjavík looking for Swans"+"'", body);
        driver.switchTo().defaultContent();
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),'Save')]"));
        saveButton.click();
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

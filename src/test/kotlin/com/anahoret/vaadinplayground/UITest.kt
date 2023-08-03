package com.anahoret.vaadinplayground

import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import java.time.Duration.ofSeconds


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UITest {

    @LocalServerPort
    private lateinit var port: Integer

    @Test
    fun notificationTest() {
        WebDriverManager.chromedriver().setup()
        val driver = ChromeDriver()
        try {
            driver.get("http://localhost:$port")
            WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(titleIs("Main"))
            driver.findElement(By.id("name-input")).sendKeys("Alex")
            driver.findElement(By.id("submit-button")).click()
            WebDriverWait(driver, ofSeconds(5), ofSeconds(1))
                .until(presenceOfElementLocated(By.xpath("//*[text()='Hello Alex']")))
        } finally {
            driver.quit()
        }
    }

}

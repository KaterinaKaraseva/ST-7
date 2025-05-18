package com.mycompany.app;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Task3 {
    public static void execute() {
        System.setProperty("webdriver.chrome.driver", "C:\\Dev\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        try {
            String url = "https://api.open-meteo.com/v1/forecast?latitude=56&longitude=44&hourly=temperature_2m,rain&current=cloud_cover&timezone=Europe%2FMoscow&forecast_days=1&wind_speed_unit=ms";
            driver.get(url);
            WebElement pre = driver.findElement(By.tagName("pre"));
            String jsonStr = pre.getText();

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonStr);
            JSONObject hourly = (JSONObject) json.get("hourly");
            JSONArray time = (JSONArray) hourly.get("time");
            JSONArray temperature = (JSONArray) hourly.get("temperature_2m");
            JSONArray rain = (JSONArray) hourly.get("rain");

            // Создаем папку result, если её нет
            File resultDir = new File("result");
            if (!resultDir.exists()) {
                resultDir.mkdir();
            }

            // Формируем таблицу
            StringBuilder sb = new StringBuilder();
            sb.append("№\tДата/время\tТемпература\tОсадки (мм)\n");
            for (int i = 0; i < time.size(); i++) {
                String row = String.format("%d\t%s\t%.1f°C\t%.2f мм\n",
                        i + 1,
                        time.get(i),
                        ((Number) temperature.get(i)).doubleValue(),
                        ((Number) rain.get(i)).doubleValue());
                sb.append(row);
            }

            // Записываем в файл
            try (FileWriter file = new FileWriter("result/forecast.txt")) {
                file.write(sb.toString());
                System.out.println("Прогноз сохранен в result/forecast.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
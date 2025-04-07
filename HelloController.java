package com.example.sales;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class HelloController {
    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private ComboBox<Integer> yearComboBox;

    private final Map<Integer, Map<Integer, Double>> yearToMonthProfit = new HashMap<>();

    @FXML
    protected void onLoadExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите Excel-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            readExcelFile(file);
            yearComboBox.setItems(FXCollections.observableArrayList(yearToMonthProfit.keySet()));
        }
    }

    private void readExcelFile(File file) {
        yearToMonthProfit.clear();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Пропустить заголовок

                Cell totalSaleCell = row.getCell(4); // Итоговая продажа
                Cell dateCell = row.getCell(5);      // Дата продажи

                if (totalSaleCell == null || dateCell == null) continue;

                double profit = totalSaleCell.getNumericCellValue();

                Date date = dateCell.getDateCellValue();
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                int year = localDate.getYear();
                int month = localDate.getMonthValue();

                yearToMonthProfit
                        .computeIfAbsent(year, y -> new HashMap<>())
                        .merge(month, profit, Double::sum);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onYearSelected() {
        Integer selectedYear = yearComboBox.getValue();
        if (selectedYear == null) return;

        lineChart.getData().clear();

        Map<Integer, Double> monthProfit = yearToMonthProfit.getOrDefault(selectedYear, new HashMap<>());
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Год " + selectedYear);

        for (int month = 1; month <= 12; month++) {
            double profit = monthProfit.getOrDefault(month, 0.0);
            series.getData().add(new XYChart.Data<>(monthName(month), profit));
        }

        lineChart.getData().add(series);
    }

    private String monthName(int month) {
        return switch (month) {
            case 1 -> "Янв";
            case 2 -> "Фев";
            case 3 -> "Мар";
            case 4 -> "Апр";
            case 5 -> "Май";
            case 6 -> "Июн";
            case 7 -> "Июл";
            case 8 -> "Авг";
            case 9 -> "Сен";
            case 10 -> "Окт";
            case 11 -> "Ноя";
            case 12 -> "Дек";
            default -> "";
        };
    }
}
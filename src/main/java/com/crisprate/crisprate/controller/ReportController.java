package com.crisprate.crisprate.controller;

import com.crisprate.crisprate.dto.ReportRow;
import com.crisprate.crisprate.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports")
    public String viewReport(
            @RequestParam(required = false) String city,
            Model model) {
        List<ReportRow> rows = reportService.generateCrispinessReport(city);

        model.addAttribute("rows", rows);
        model.addAttribute("cityFilter", city);
        model.addAttribute("generatedAt", LocalDateTime.now());

        return "report";
    }

    @GetMapping("/reports/export")
    public void exportReportCsv(
            @RequestParam(required = false) String city,
            HttpServletResponse response) throws java.io.IOException {
        List<ReportRow> rows = reportService.generateCrispinessReport(city);
        LocalDateTime generatedAt = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"crisprate_report_" + generatedAt.format(formatter) + ".csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("CrispRate Crispiness Report");
        writer.println("Generated: " + generatedAt.format(DateTimeFormatter.ofPattern("MMM d yyyy h:mm a")));
        writer.println();
        writer.println("Name,City,Average Score,Number of Ratings");

        for (ReportRow row : rows) {
            writer.println( escapeCsv(row.getName()) + "," +
                            escapeCsv(row.getCity()) + "," +
                            row.getAverageScore() + "," +
                            row.getRatingCount()

            );
        }

        writer.flush();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;


    }
}

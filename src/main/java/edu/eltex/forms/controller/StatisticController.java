package edu.eltex.forms.controller;

import edu.eltex.forms.dto.statistic.StatisticDTO;
import edu.eltex.forms.service.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/statistic")
@RequiredArgsConstructor
@Validated
public class StatisticController {

    private final StatisticService statisticService;

    @Operation(summary = "Find number of completions for specific form ID")
    @PreAuthorize("hasRole('CREATOR')")
    @GetMapping("/{formId}")
    public ResponseEntity<StatisticDTO> getNumberOfCompletions(@PathVariable Integer formId) {
        StatisticDTO completions = statisticService.getFormStatistic(formId);
        return ResponseEntity.ok(completions);
    }

    @Operation(summary = "Download statistics as Excel file")
    @GetMapping("/download/{formId}")
    public ResponseEntity<ByteArrayResource> downloadExcelStatistic(@PathVariable Integer formId) throws IOException {
        byte[] excelData = statisticService.generateExcelStatistic(formId);

        ByteArrayResource resource = new ByteArrayResource(excelData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=statistic.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(excelData.length)
                .body(resource);
    }
}
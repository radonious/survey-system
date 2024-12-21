package edu.eltex.forms.controller;

import edu.eltex.forms.dto.statistic.StatisticDTO;
import edu.eltex.forms.service.StatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class StatisticControllerTest {

    @Mock
    private StatisticService statisticService;

    @InjectMocks
    private StatisticController statisticController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Проверка контроллера для статистики")
    @Test
    public void testGetNumberOfCompletions() {
        // Arrange
        int formId = 1;
        StatisticDTO statisticDTO = StatisticDTO.builder()
                .numberOfCompletions(2)
                .questionStatistic(Collections.emptyList())
                .build();

        when(statisticService.getFormStatistic(formId)).thenReturn(statisticDTO);

        // Act
        ResponseEntity<StatisticDTO> responseEntity = statisticController.getNumberOfCompletions(formId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(statisticDTO, responseEntity.getBody());
    }
}
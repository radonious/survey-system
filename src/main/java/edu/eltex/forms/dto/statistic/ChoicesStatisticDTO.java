package edu.eltex.forms.dto.statistic;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
public class ChoicesStatisticDTO {
    private List<String> answers;
    private List<Integer> numberOfAnswered;
    private List<Double> percentageOfAnswered;

    public static ChoicesStatisticDTO getFullChoisesStatisticDTO(List<String> answeredAnswers, List<String> allPossibleAnswers, int numberOfCompletions) {
        Map<String, Long> answerCounts = answeredAnswers.stream()
                .collect(Collectors.groupingBy(answer -> answer, Collectors.counting()));

        List<Integer> counts = allPossibleAnswers.stream()
                .map(answer -> answerCounts.getOrDefault(answer, 0L).intValue())
                .collect(Collectors.toList());

        List<Double> percentages = counts.stream()
                .map(count -> BigDecimal.valueOf(numberOfCompletions == 0 ? 0 : (double) count / numberOfCompletions * 100)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue())
                .collect(Collectors.toList());

        return ChoicesStatisticDTO.builder()
                .answers(allPossibleAnswers)
                .numberOfAnswered(counts)
                .percentageOfAnswered(percentages)
                .build();
    }
}

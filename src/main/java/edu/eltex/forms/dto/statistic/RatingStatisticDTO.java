package edu.eltex.forms.dto.statistic;

import edu.eltex.forms.entities.Answer;
import edu.eltex.forms.entities.Option;
import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class RatingStatisticDTO {
    private List<Integer> position;
    private List<String> answerText;
    private List<Double> avgPosition;

    public static RatingStatisticDTO getFullRatingStatistic(List<Option> questionOptions, List<Answer> questionAnswers) {
        Map<String, Integer> optionPositionSum = new HashMap<>();

        for (Option option : questionOptions) {
            optionPositionSum.put(option.getText(), 0);
        }

        for (Answer answer : questionAnswers) {
            List<Option> selectedOptions = answer.getSelectedOptions();
            for (int i = 0; i < selectedOptions.size(); i++) {
                Option option = selectedOptions.get(i);
                optionPositionSum.put(option.getText(), optionPositionSum.get(option.getText()) + (i + 1));
            }
        }

        List<Integer> positions = new ArrayList<>();
        List<String> answerTexts = new ArrayList<>();
        List<Double> avgPositions = new ArrayList<>();

        questionOptions.stream()
                .map(option -> {
                    int sumPositions = optionPositionSum.get(option.getText());
                    double avgPos = sumPositions == 0 ? 0 : sumPositions / (double) questionAnswers.size();
                    return new AbstractMap.SimpleEntry<>(option.getText(), avgPos);
                })
                .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .forEachOrdered(entry -> {
                    positions.add(positions.size() + 1);
                    answerTexts.add(entry.getKey());
                    avgPositions.add(entry.getValue());
                });

        return RatingStatisticDTO.builder()
                .position(positions)
                .answerText(answerTexts)
                .avgPosition(avgPositions)
                .build();
    }
}

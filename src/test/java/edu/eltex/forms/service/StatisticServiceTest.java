package edu.eltex.forms.service;

import edu.eltex.forms.dto.statistic.ChoicesStatisticDTO;
import edu.eltex.forms.dto.statistic.NumericStatisticDTO;
import edu.eltex.forms.dto.statistic.QuestionStatisticDTO;
import edu.eltex.forms.dto.statistic.StatisticDTO;
import edu.eltex.forms.entities.Answer;
import edu.eltex.forms.entities.Option;
import edu.eltex.forms.entities.Question;
import edu.eltex.forms.enums.QuestionType;
import edu.eltex.forms.repository.StatisticRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class StatisticServiceTest {

    @Mock
    private StatisticRepository statisticRepository;

    @InjectMocks
    private StatisticService statisticService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Проверка получения всей статистики")
    @Test
    public void testGetFormStatistic() {
        // Arrange
        int formId = 1;
        Integer numberOfCompletions = 2;

        Question question1 = new Question();
        question1.setId(1);
        question1.setText("Как тебя зовут?");
        question1.setType(QuestionType.TEXT);

        Question question2 = new Question();
        question2.setId(2);
        question2.setText("Твой возраст?");
        question2.setType(QuestionType.NUMERIC);

        Question question3 = new Question();
        question3.setId(3);
        question3.setText("Выбери любимые предметы");
        question3.setType(QuestionType.MULTIPLE_CHOICE);

        Question question4 = new Question();
        question4.setId(4);
        question4.setText("Выбери любимую команду");
        question4.setType(QuestionType.SINGLE_CHOICE);

        List<Question> questions = Arrays.asList(question1, question2, question3, question4);

        Option option1 = new Option();
        option1.setText("Математика");
        option1.setQuestion(question3);

        Option option2 = new Option();
        option2.setText("Физика");
        option2.setQuestion(question3);

        Option option3 = new Option();
        option3.setText("Барселона");
        option3.setQuestion(question4);

        Option option4 = new Option();
        option4.setText("Реал Мадрид");
        option4.setQuestion(question4);

        List<Option> options = Arrays.asList(option1, option2, option3, option4);

        Answer answer1 = new Answer();
        answer1.setQuestion(question1);
        answer1.setAnswerText("Пётр Звщиньский");

        Answer answer2 = new Answer();
        answer2.setQuestion(question2);
        answer2.setAnswerText("35");

        Answer answer3 = new Answer();
        answer3.setQuestion(question3);
        answer3.setSelectedOptions(List.of(option2));

        Answer answer4 = new Answer();
        answer4.setQuestion(question4);
        answer4.setSelectedOptions(List.of(option4));

        Answer answer5 = new Answer();
        answer5.setQuestion(question1);
        answer5.setAnswerText("Василий Бщихьговски");

        Answer answer6 = new Answer();
        answer6.setQuestion(question2);
        answer6.setAnswerText("55");

        Answer answer7 = new Answer();
        answer7.setQuestion(question3);
        answer7.setSelectedOptions(List.of(option1, option2));

        Answer answer8 = new Answer();
        answer8.setQuestion(question4);
        answer8.setSelectedOptions(List.of(option3));

        List<Answer> answers = Arrays.asList(answer1, answer2, answer3, answer4, answer5, answer6, answer7, answer8);

        when(statisticRepository.countNumberOfCompletions(formId)).thenReturn(numberOfCompletions);
        when(statisticRepository.getAllQuestions(formId)).thenReturn(questions);
        when(statisticRepository.getAllAnswers(formId)).thenReturn(answers);
        when(statisticRepository.getAllOptions(formId)).thenReturn(options);

        // Act
        StatisticDTO result = statisticService.getFormStatistic(formId);

        // Assert
        assertNotNull(result);
        assertEquals(numberOfCompletions, result.getNumberOfCompletions());
        assertEquals(questions.size(), result.getQuestionStatistic().size());

        QuestionStatisticDTO textQuestionStatistic = result.getQuestionStatistic().get(0);
        assertEquals("Как тебя зовут?", textQuestionStatistic.getQuestionText());
        List<String> textAnswers = (List<String>) textQuestionStatistic.getStatistic();
        assertEquals(2, textAnswers.size());
        assertTrue(textAnswers.contains("Пётр Звщиньский"));
        assertTrue(textAnswers.contains("Василий Бщихьговски"));

        QuestionStatisticDTO numericQuestionStatistic = result.getQuestionStatistic().get(1);
        assertEquals("Твой возраст?", numericQuestionStatistic.getQuestionText());
        assertTrue(numericQuestionStatistic.getStatistic() instanceof NumericStatisticDTO);
        NumericStatisticDTO numericStatistic = (NumericStatisticDTO) numericQuestionStatistic.getStatistic();
        assertEquals(2, numericStatistic.getAnswers().size());
        assertEquals(35, numericStatistic.getMinAnswer());
        assertEquals(55, numericStatistic.getMaxAnswer());
        assertEquals(45, numericStatistic.getAvgAnswer());

        QuestionStatisticDTO multipleChoiceQuestionStatistic = result.getQuestionStatistic().get(2);
        assertEquals("Выбери любимые предметы", multipleChoiceQuestionStatistic.getQuestionText());
        assertTrue(multipleChoiceQuestionStatistic.getStatistic() instanceof ChoicesStatisticDTO);
        ChoicesStatisticDTO multipleChoiceStatistic = (ChoicesStatisticDTO) multipleChoiceQuestionStatistic.getStatistic();
        assertEquals(2, multipleChoiceStatistic.getAnswers().size());
        assertEquals(1, multipleChoiceStatistic.getNumberOfAnswered().get(0));
        assertEquals(2, multipleChoiceStatistic.getNumberOfAnswered().get(1));
        assertEquals(50.0, multipleChoiceStatistic.getPercentageOfAnswered().get(0));
        assertEquals(100.0, multipleChoiceStatistic.getPercentageOfAnswered().get(1));

        QuestionStatisticDTO singleChoiceQuestionStatistic = result.getQuestionStatistic().get(3);
        assertEquals("Выбери любимую команду", singleChoiceQuestionStatistic.getQuestionText());
        assertTrue(singleChoiceQuestionStatistic.getStatistic() instanceof ChoicesStatisticDTO);
        ChoicesStatisticDTO singleChoiceStatistic = (ChoicesStatisticDTO) singleChoiceQuestionStatistic.getStatistic();
        assertEquals(2, singleChoiceStatistic.getAnswers().size());
        assertEquals(1, singleChoiceStatistic.getNumberOfAnswered().get(0));
        assertEquals(1, singleChoiceStatistic.getNumberOfAnswered().get(1));
        assertEquals(50.0, singleChoiceStatistic.getPercentageOfAnswered().get(0));
        assertEquals(50.0, singleChoiceStatistic.getPercentageOfAnswered().get(1));
    }
}
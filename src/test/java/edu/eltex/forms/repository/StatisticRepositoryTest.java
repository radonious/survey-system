package edu.eltex.forms.repository;

import edu.eltex.forms.entities.Answer;
import edu.eltex.forms.entities.Option;
import edu.eltex.forms.entities.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatisticRepositoryTest {

    @Mock
    private StatisticRepository statisticRepository;

    private List<Answer> answers;
    private List<Option> options;
    private List<Question> questions;

    @BeforeEach
    public void setUp() {
        answers = Arrays.asList(
                new Answer(),
                new Answer()
        );

        options = Arrays.asList(
                new Option(),
                new Option()
        );

        questions = Arrays.asList(
                new Question(),
                new Question()
        );
    }

    @Test
    public void testCountNumberOfCompletions() {
        //Arrange
        int formId = 1;
        when(statisticRepository.countNumberOfCompletions(formId)).thenReturn(2);

        //Act
        Integer result = statisticRepository.countNumberOfCompletions(formId);

        //Assert
        assertEquals(2, result);
    }

    @Test
    public void testGetAllAnswers() {
        //Arrange
        int formId = 1;
        when(statisticRepository.getAllAnswers(formId)).thenReturn(answers);

        //Act
        List<Answer> result = statisticRepository.getAllAnswers(formId);

        //Assert
        assertEquals(answers, result);
    }

    @Test
    public void testGetAllOptions() {
        //Arrange
        int formId = 1;
        when(statisticRepository.getAllOptions(formId)).thenReturn(options);

        //Act
        List<Option> result = statisticRepository.getAllOptions(formId);

        //Assert
        assertEquals(options, result);
    }

    @Test
    public void testGetAllQuestions() {
        //Arrange
        int formId = 1;
        when(statisticRepository.getAllQuestions(formId)).thenReturn(questions);

        //Act
        List<Question> result = statisticRepository.getAllQuestions(formId);

        //Assert
        assertEquals(questions, result);
    }
}
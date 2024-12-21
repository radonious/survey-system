package edu.eltex.forms.service;

import edu.eltex.forms.dto.CompletionRequestDTO;
import edu.eltex.forms.dto.CompletionResponseDTO;
import edu.eltex.forms.entities.*;
import edu.eltex.forms.enums.QuestionType;
import edu.eltex.forms.enums.UserRole;
import edu.eltex.forms.mapper.CompletionMapper;
import edu.eltex.forms.repository.CompletionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class CompletionServiceTest {

    @MockitoBean
    OptionService optionService;
    @Autowired
    private CompletionService service;
    @MockitoBean
    private CompletionRepository repository;
    @MockitoSpyBean
    private CompletionMapper mapper;

    private List<Completion> completions;

    @BeforeEach
    void setUp() {
        User dummyUser = new User(1, "username", "pass", UserRole.CREATOR, "token");
        Question dummyQuestion = new Question(6, null, "question text", QuestionType.MULTIPLE_CHOICE, "url", null);
        Form dummyForm = new Form(12, dummyUser, "title", "description", List.of(dummyQuestion));
        dummyQuestion.setForm(dummyForm);

        Option dummyOptionA = new Option(20, dummyQuestion, "Option A");
        Option dummyOptionB = new Option(21, dummyQuestion, "Option B");

        Answer dummyAnswer1 = Answer.builder().question(dummyQuestion).id(1).answerText("dummy").selectedOptions(null).build();
        Answer dummyAnswer2 = Answer.builder().question(dummyQuestion).id(2).answerText(null).selectedOptions(List.of(dummyOptionA)).build();
        Answer dummyAnswer3 = Answer.builder().question(dummyQuestion).id(3).answerText(null).selectedOptions(List.of(dummyOptionB)).build();
        Answer dummyAnswer4 = Answer.builder().question(dummyQuestion).id(4).answerText(null).selectedOptions(List.of(dummyOptionA, dummyOptionB)).build();

        completions = List.of(
                Completion.builder().id(1).user(dummyUser).form(dummyForm).answers(List.of(dummyAnswer1)).build(),
                Completion.builder().id(2).user(dummyUser).form(dummyForm).answers(List.of(dummyAnswer3, dummyAnswer4)).build(),
                Completion.builder().id(3).user(dummyUser).form(dummyForm).answers(List.of(dummyAnswer1, dummyAnswer2, dummyAnswer3, dummyAnswer4)).build()
        );

        completions.forEach(completion -> {
            completion.getAnswers().forEach(answer -> {
                answer.setCompletion(completion);
            });
        });
    }

    @Test
    void getAllCompletions() {
        final List<CompletionResponseDTO> expected = completions.stream().map(mapper::toDTO).toList();

        when(repository.findAll()).thenReturn(completions);
        final List<CompletionResponseDTO> actual = service.getAllCompletions();

        Assertions.assertNotNull(actual);
        actual.forEach(Assertions::assertNotNull);
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void getCompletionById(int parameter) {
        final Completion data = completions.get(parameter);
        final CompletionResponseDTO expected = mapper.toDTO(data);

        when(repository.findById(anyInt())).thenReturn(Optional.ofNullable(data));
        final CompletionResponseDTO actual = service.getCompletionById(1);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getCompletionByIdException() {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            final CompletionResponseDTO actual = service.getCompletionById(-100);
        });
    }

    @Test
    void createCompletion() {
        final Completion data = completions.get(2);
        final CompletionResponseDTO expected = mapper.toDTO(data);
        final CompletionRequestDTO req = new CompletionRequestDTO();

        when(repository.save(any(Completion.class))).thenReturn(data);
        when(mapper.toEntity(any())).thenReturn(data);
        when(optionService.convertIntoExistingOptions(anyList(), any()))
                .thenReturn(data.getAnswers().get(1).getSelectedOptions())
                .thenReturn(data.getAnswers().get(2).getSelectedOptions())
                .thenReturn(data.getAnswers().get(3).getSelectedOptions());
        final CompletionResponseDTO actual = service.createCompletion(req);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void deleteCompletion(int parameter) {
        final Completion data = completions.get(parameter);
        final boolean expected = true;
        when(repository.findById(anyInt())).thenReturn(Optional.ofNullable(data));
        final boolean actual = service.deleteCompletion(1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void deleteCompletionInvalid() {
        final boolean expected = false;
        when(repository.findById(anyInt())).thenReturn(Optional.empty());
        final boolean actual = service.deleteCompletion(-100);
        Assertions.assertEquals(expected, actual);
    }
}
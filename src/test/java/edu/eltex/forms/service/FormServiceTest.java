package edu.eltex.forms.service;

import edu.eltex.forms.dto.FormRequestDTO;
import edu.eltex.forms.dto.FormResponseDTO;
import edu.eltex.forms.entities.Form;
import edu.eltex.forms.entities.Option;
import edu.eltex.forms.entities.Question;
import edu.eltex.forms.entities.User;
import edu.eltex.forms.enums.QuestionType;
import edu.eltex.forms.enums.UserRole;
import edu.eltex.forms.mapper.FormMapper;
import edu.eltex.forms.repository.FormRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class FormServiceTest {

    @Autowired
    private FormService formService;

    @MockitoBean
    private FormRepository formRepository;

    @MockitoSpyBean
    private FormMapper formMapper;

    private List<Form> forms;

    @BeforeEach
    void setUp() {
        User u1 = new User(1, "test_username", "12345", UserRole.CREATOR, "test_token");
        Question q1 = new Question(1, null, "How old are you?", QuestionType.NUMERIC, null, null);
        Form f1 = new Form(1, u1, "test_title_1", "test_description_1", List.of(q1));
        q1.setForm(f1);

        Question q2 = new Question(2, null, "How are you?", QuestionType.SINGLE_CHOICE, null, null);
        Option o1 = new Option(1, q2, "Good");
        Option o2 = new Option(2, q2, "Bad");
        q2.setOptions(List.of(o1, o2));
        Question q3 = new Question(3, null, "How old are you?", QuestionType.NUMERIC, null, null);
        Form f2 = new Form(2, u1, "test_title_2", "test_description_2", List.of(q2, q3));
        q2.setForm(f2);
        q3.setForm(f2);

        User u2 = new User(2, "test_username_2", "123456", UserRole.CREATOR, "test_token_2");
        Form f3 = new Form(3, u2, "test_title_3", "test_description_3", null);

        forms = List.of(f1, f2, f3);
    }

    @Test
    void getAllForms() {
        List<FormResponseDTO> expected = forms.stream().map(formMapper::toDto).toList();

        when(formRepository.findAll()).thenReturn(forms);

        List<FormResponseDTO> result = formService.getAllForms();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(expected, result);
        verify(formRepository).findAll();
    }

    @Test
    void getAllFormsByCreatorName() {
        String creatorName = "test_username";
        List<Form> formsByCreatorName = forms.stream()
                .filter(f -> f.getCreator().getUsername().equals(creatorName))
                .toList();
        List<FormResponseDTO> expected = formsByCreatorName.stream()
                .map(formMapper::toDto)
                .toList();

        when(formRepository.findAllByCreator_Username(creatorName)).thenReturn(formsByCreatorName);

        List<FormResponseDTO> result = formService.getAllFormsByCreatorName(creatorName);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(expected, result);
        verify(formRepository).findAllByCreator_Username(creatorName);
    }

    @Test
    void getFormByTitle() {
        String title = "test_title_1";
        Optional<Form> form = forms.stream()
                .filter(f -> f.getTitle().equals(title))
                .findFirst();

        Form form1 = form.orElse(null);
        FormResponseDTO expected = formMapper.toDto(form1);

        when(formRepository.findByTitle(title)).thenReturn(Optional.ofNullable(form1));

        FormResponseDTO result = formService.getFormByTitle(title);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);
        verify(formRepository).findByTitle(title);
    }

    @Test
    void getFormByTitle_shouldThrowExceptionWhenFormWithGivenTitleDoesNotExist() {
        String title = "no_title";

        when(formRepository.findByTitle(title)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> formService.getFormByTitle(title)
        );

        Assertions.assertEquals("Form with title '" + title + "' not found", thrown.getMessage());
        verify(formRepository).findByTitle(title);
    }

    @Test
    void getFormById() {
        Form form = forms.getFirst();
        FormResponseDTO expected = formMapper.toDto(form);

        when(formRepository.findById(1)).thenReturn(Optional.ofNullable(form));

        FormResponseDTO result = formService.getFormById(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void getFormById_shouldThrowExceptionWhenFormWithGivenIdDoesNotExist() {
        int id = -1;

        when(formRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> formService.getFormById(id)
        );

        Assertions.assertEquals("Form not found", thrown.getMessage());
        verify(formRepository).findById(id);
    }

    @Test
    void createForm() {
        Form form = forms.get(1);
        FormResponseDTO expected = formMapper.toDto(form);

        when(formMapper.toEntity(any())).thenReturn(form);
        when(formRepository.save(any(Form.class))).thenReturn(form);

        FormResponseDTO result = formService.createForm(new FormRequestDTO());

        Assertions.assertEquals(expected, result);
    }

    @Test
    void deleteForm() {
        int id = 1;
        boolean expected = true;

        when(formRepository.findById(id)).thenReturn(Optional.ofNullable(forms.getFirst()));

        boolean result = formService.deleteForm(id);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void deleteForm_shouldReturnFalseWhenFormWithGivenIdDoesNotExist() {
        int id = 0;
        boolean expected = false;

        when(formRepository.findById(id)).thenReturn(Optional.empty());

        boolean result = formService.deleteForm(id);

        Assertions.assertEquals(expected, result);
        verify(formRepository).findById(id);
    }
}
package edu.eltex.forms.controller;

import edu.eltex.forms.dto.AnswerRequestDTO;
import edu.eltex.forms.dto.AnswerResponseDTO;
import edu.eltex.forms.dto.CompletionRequestDTO;
import edu.eltex.forms.dto.CompletionResponseDTO;
import edu.eltex.forms.service.CompletionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CompletionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompletionService service;

    @Autowired
    private Jackson2ObjectMapperBuilder mapperBuilder;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(mockMvc);
        Assertions.assertNotNull(service);
    }

    @Test
    @WithMockUser(roles = {"CREATOR"})
    void getAllCompletions() throws Exception {
        final String uri = "/api/v1/completions";
        final List<CompletionResponseDTO> resp = List.of(
                CompletionResponseDTO.builder().id(1).userId(10).formId(100).answers(null).build(),
                CompletionResponseDTO.builder().id(2).userId(10).formId(100).answers(null).build(),
                CompletionResponseDTO.builder().id(3).userId(10).formId(100).answers(null).build()
        );
        when(service.getAllCompletions()).thenReturn(resp);
        mockMvc.perform(get(uri))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapperBuilder.build().writerWithDefaultPrettyPrinter().writeValueAsString(resp)));
    }

    @Test
    @WithMockUser(roles = {"CREATOR"})
    void getCompletionOkIndex() throws Exception {
        final String uri = "/api/v1/completions/1";
        final CompletionResponseDTO resp = CompletionResponseDTO.builder().id(1).userId(10).formId(100).answers(List.of(
                AnswerResponseDTO.builder().id(1).completionId(1).answerText("TESTING").build(),
                AnswerResponseDTO.builder().id(1).completionId(1).answerText("TESTING").build()
        )).build();
        when(service.getCompletionById(Mockito.anyInt())).thenReturn(resp);
        mockMvc.perform(get(uri))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapperBuilder.build().writerWithDefaultPrettyPrinter().writeValueAsString(resp)));
    }

    @Test
    void getCompletionBadIndex() throws Exception {
        final String uri = "/api/v1/completions/-1";
        mockMvc.perform(get(uri))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    void createCompletionValid() throws Exception {
        final String uri = "/api/v1/completions";
        final CompletionRequestDTO req = CompletionRequestDTO.builder().userId(10).formId(100).answers(List.of(
                AnswerRequestDTO.builder().questionId(1).answerText("TESTING").build(),
                AnswerRequestDTO.builder().questionId(1).answerText("TESTING").build()
        )).build();
        final CompletionResponseDTO resp = CompletionResponseDTO.builder().id(1).userId(10).formId(100).answers(List.of(
                AnswerResponseDTO.builder().id(1).questionId(1).completionId(1).answerText("TESTING").build(),
                AnswerResponseDTO.builder().id(1).questionId(1).completionId(1).answerText("TESTING").build()
        )).build();

        String reqJSON = mapperBuilder.build().writerWithDefaultPrettyPrinter().writeValueAsString(req);
        String respJSON = mapperBuilder.build().writerWithDefaultPrettyPrinter().writeValueAsString(resp);

        when(service.createCompletion(Mockito.any())).thenReturn(resp);
        mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(reqJSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(respJSON));
    }

    @Test
    void createCompletionInvalid() throws Exception {
        final String uri = "/api/v1/completions";
        final CompletionRequestDTO req = CompletionRequestDTO.builder().userId(null).formId(null).answers(null).build();
        mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(mapperBuilder.build().writeValueAsString(req)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = {"CREATOR"})
    void deleteCompletionOkIndex() throws Exception {
        final String uri = "/api/v1/completions/1";
        when(service.deleteCompletion(Mockito.anyInt())).thenReturn(true);
        mockMvc.perform(delete(uri))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser(roles = {"CREATOR"})
    void deleteCompletionBadIndex() throws Exception {
        final String uri = "/api/v1/completions/1";
        when(service.deleteCompletion(Mockito.anyInt())).thenReturn(false);
        mockMvc.perform(delete(uri))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Completion with given ID not found"));
    }
}
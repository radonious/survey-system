package edu.eltex.forms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormResponseDTO {

    private Integer id;
    private Integer creatorId;
    private String creatorName;
    private String title;
    private String description;
    private List<QuestionResponseDTO> questions;
}

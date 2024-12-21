package edu.eltex.forms.controller;

import edu.eltex.forms.dto.CompletionRequestDTO;
import edu.eltex.forms.dto.CompletionResponseDTO;
import edu.eltex.forms.service.CompletionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/completions")
@RequiredArgsConstructor
public class CompletionController {

    private final CompletionService completionService;

    @Operation(summary = "Find all completions")
    @PreAuthorize("hasRole('CREATOR')")
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<CompletionResponseDTO>> getAllCompletions() {
        List<CompletionResponseDTO> completions = completionService.getAllCompletions();
        return ResponseEntity.ok(completions);
    }

    @Operation(summary = "Find completions by specific ID")
    @PreAuthorize("hasRole('CREATOR') or " +
            "(hasRole('USER') and @completionService.isUserOwnerOfCompletion(@authService.getAuthenticatedUserId(),#id))")
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<CompletionResponseDTO> getCompletion(@PathVariable Integer id) {
        CompletionResponseDTO completion = completionService.getCompletionById(id);
        return completion != null ? ResponseEntity.ok(completion) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Create new completion")
    @PreAuthorize("hasRole('USER')")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CompletionResponseDTO> createCompletion(@Valid @RequestBody CompletionRequestDTO completionRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(completionService.createCompletion(completionRequestDTO));
    }

    @Operation(summary = "Delete completions by specific ID")
    @PreAuthorize("hasRole('CREATOR') or " +
            "(hasRole('USER') and @completionService.isUserOwnerOfCompletion(@authService.getAuthenticatedUserId(),#id))")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteCompletion(@PathVariable Integer id) {
        boolean deleted = completionService.deleteCompletion(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Completion with given ID not found");
    }

    @Operation(summary = "Identify if user passed form or no")
    @PreAuthorize("hasRole('CREATOR') or " +
            "(hasRole('USER'))")
    @GetMapping(value = "/user-form-completion")
    public ResponseEntity<Integer> getCompletionIdByUserAndForm(
            @RequestParam Integer formId,
            @RequestParam Integer userId) {
        Integer completionId = completionService.getCompletionIdByUserAndForm(formId, userId);
        return completionId != -1
                ? ResponseEntity.ok(completionId)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

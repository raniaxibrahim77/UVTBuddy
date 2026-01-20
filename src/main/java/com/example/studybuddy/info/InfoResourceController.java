package com.example.studybuddy.info;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/info")
public class InfoResourceController {

    private final InfoResourceRepository repo;

    public InfoResourceController(InfoResourceRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<InfoResource> list(
            @RequestParam(required = false) InfoCategory category,
            @RequestParam(required = false) String q
    ) {
        if (q != null && !q.isBlank()) {
            return repo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q);
        }
        if (category != null) {
            return repo.findByCategory(category);
        }
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<InfoResource> create(@Valid @RequestBody CreateInfoReq req) {

        InfoResource r = new InfoResource(
                req.title().trim(),
                req.content().trim(),
                req.category(),
                req.location(),
                req.link()
        );

        return ResponseEntity.ok(repo.save(r));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateInfoReq(
            @NotBlank String title,
            @NotBlank String content,
            @NotNull InfoCategory category,
            Location location,
            String link
    ) {}

    public record UpdateInfoReq(
            String title,
            String content,
            InfoCategory category,
            Location location,
            String link
    ) {}

}

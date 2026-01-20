package com.example.studybuddy.posts;

import com.example.studybuddy.files.FileStorage;
import com.example.studybuddy.users.User;
import com.example.studybuddy.users.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.studybuddy.errors.PostNotFound;
import com.example.studybuddy.errors.UnauthorizedException;
import java.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.studybuddy.errors.ForbiddenException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Stream;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class StudyPostsController {

    private final StudyPostsRepository posts;
    private final UserRepository users;
    private final FileStorage fileStorage;

    public record CreatePostReq(
            @NotBlank String title,
            @NotBlank String description,
            String course,
            String major,
            Set<String> tags,
            PostType type
    ) {}

    public record UpdatePostReq(
            String title,
            String description,
            String course,
            String major,
            Set<String> tags
    ) {}

    @GetMapping
    public List<StudyPosts> all(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) PostType type
    ) {
        List<StudyPosts> all = posts.findAll();
        Stream<StudyPosts> s = all.stream();

        if (type != null) {
            s = s.filter(p -> p.getType() == type);
        }

        if (q != null && !q.isBlank()) {
            String needle = q.toLowerCase(Locale.ROOT).trim();

            s = s.filter(p ->
                    contains(p.getTitle(), needle) ||
                            contains(p.getDescription(), needle) ||
                            contains(p.getCourse(), needle) ||
                            contains(p.getMajor(), needle) ||
                            (p.getTags() != null && p.getTags().stream()
                                    .filter(t -> t != null)
                                    .anyMatch(t -> t.toLowerCase(Locale.ROOT).contains(needle)))
            );
        }

        return s.sorted(
                Comparator.comparing(
                        StudyPosts::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed()
        ).toList();

    }

    private boolean contains(String value, String needle) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(needle);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyPosts> one(@PathVariable Long id) {
        return posts.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new PostNotFound(id));
    }

    @PostMapping
    public ResponseEntity<StudyPosts> create(@Valid @RequestBody CreatePostReq req, java.security.Principal principal) {

        var author = users.findByEmail(principal.getName())
                .orElseThrow(() -> new com.example.studybuddy.errors.UnauthorizedException("User not found for current login"));

        PostType type = req.type() == null ? PostType.NOTE : req.type();

        String title = req.title().trim();
        if (type == PostType.NOTE && title.length() > 120) {
            throw new IllegalArgumentException("Note title must be one line (max 120 characters)");
        }

        var post = StudyPosts.builder()
                .title(req.title())
                .description(req.description())
                .course(req.course())
                .major(req.major())
                .tags(req.tags() == null ? Set.of() : req.tags())
                .type(type)
                .author(author)
                .build();

        var saved = posts.save(post);
        fileStorage.saveToFile();
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyPosts> update(@PathVariable Long id,
                                             @Valid @RequestBody UpdatePostReq req,
                                             Principal principal) {
        User me = currentUser(principal);

        return posts.findById(id).map(p -> {
            boolean owner = p.getAuthor().getId().equals(me.getId());
            if (!owner && !isAdmin()) {
                throw new ForbiddenException("Only the author or an admin can update this post");
            }

            if (req.title() != null) {
                if (req.title().isBlank()) throw new IllegalArgumentException("title must not be blank");
                p.setTitle(req.title());
            }
            if (req.description() != null) {
                if (req.description().isBlank()) throw new IllegalArgumentException("description must not be blank");
                p.setDescription(req.description());
            }
            if (req.course() != null) p.setCourse(req.course());
            if (req.major() != null) p.setMajor(req.major());
            if (req.tags() != null) p.setTags(req.tags());

            var saved = posts.save(p);
            fileStorage.saveToFile();
            return ResponseEntity.ok(saved);
        }).orElseThrow(() -> new PostNotFound(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {

        User me = currentUser(principal);

        StudyPosts p = posts.findById(id).orElseThrow(() -> new PostNotFound(id));

        boolean owner = p.getAuthor().getId().equals(me.getId());
        if (!owner && !isAdmin()) {
            throw new ForbiddenException("Only the author or an admin can delete this post");
        }

        posts.deleteById(id);
        fileStorage.saveToFile();
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/images")
    public ResponseEntity<StudyPosts> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files,
            Principal principal
    ) {
        User me = currentUser(principal);
        StudyPosts p = posts.findById(id).orElseThrow(() -> new PostNotFound(id));

        boolean owner = p.getAuthor().getId().equals(me.getId());
        if (!owner && !isAdmin()) {
            throw new ForbiddenException("Only the author or an admin can upload images");
        }

        if (files == null || files.isEmpty()) throw new IllegalArgumentException("No files uploaded");
        if (files.size() > 5) throw new IllegalArgumentException("Max 5 images");

        for (MultipartFile f : files) {
            if (f.getSize() > 5_000_000) throw new IllegalArgumentException("Max 5MB per image");
            String url = fileStorage.savePostImage(id, f);
            p.getImageUrls().add(url);
        }

        StudyPosts saved = posts.save(p);
        fileStorage.saveToFile();
        return ResponseEntity.ok(saved);
    }


    private User currentUser(Principal principal) {
        return users.findByEmail(principal.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found for current login"));
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }


}

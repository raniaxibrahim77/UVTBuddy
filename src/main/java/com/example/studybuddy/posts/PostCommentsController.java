package com.example.studybuddy.posts;

import com.example.studybuddy.errors.ForbiddenException;
import com.example.studybuddy.errors.PostNotFound;
import com.example.studybuddy.errors.UnauthorizedException;
import com.example.studybuddy.users.User;
import com.example.studybuddy.users.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class PostCommentsController {

    private final PostCommentRepository comments;
    private final StudyPostsRepository posts;
    private final UserRepository users;

    public record AuthorDto(Long id, String name) {}
    public record CommentDto(Long id, String text, LocalDateTime createdAt, AuthorDto author) {}

    public record CreateCommentReq(@NotBlank String text) {}

    @GetMapping
    public List<CommentDto> list(@PathVariable Long postId) {
        // Ensure post exists (optional but nice)
        posts.findById(postId).orElseThrow(() -> new PostNotFound(postId));

        return comments.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @PostMapping
    public CommentDto create(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentReq req,
            Principal principal
    ) {
        StudyPosts post = posts.findById(postId).orElseThrow(() -> new PostNotFound(postId));
        User me = users.findByEmail(principal.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found for current login"));

        PostComment saved = comments.save(PostComment.builder()
                .post(post)
                .author(me)
                .text(req.text().trim())
                .build());

        return toDto(saved);
    }

    @DeleteMapping("/{commentId}")
    public void delete(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            Principal principal
    ) {
        User me = users.findByEmail(principal.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found for current login"));

        posts.findById(postId).orElseThrow(() -> new PostNotFound(postId));

        PostComment c = comments.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        boolean owner = c.getAuthor().getId().equals(me.getId());
        if (!owner && !isAdmin()) {
            throw new ForbiddenException("Only the author or an admin can delete this comment");
        }

        comments.delete(c);
    }

    private CommentDto toDto(PostComment c) {
        return new CommentDto(
                c.getId(),
                c.getText(),
                c.getCreatedAt(),
                new AuthorDto(c.getAuthor().getId(), c.getAuthor().getName())
        );
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}

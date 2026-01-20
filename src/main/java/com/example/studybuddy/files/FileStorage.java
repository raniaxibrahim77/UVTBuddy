package com.example.studybuddy.files;

import com.example.studybuddy.posts.StudyPosts;
import com.example.studybuddy.posts.StudyPostsRepository;
import com.example.studybuddy.users.User;
import com.example.studybuddy.users.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Component
public class FileStorage {

    private final StudyPostsRepository posts;
    private final UserRepository users;
    private final ObjectMapper mapper;
    private final Path filePath;
    private final boolean reset;
    private final Path uploadsDir;

    public FileStorage(
            StudyPostsRepository posts,
            UserRepository users,
            ObjectMapper mapper,
            @Value("${app.posts.path:data/posts.json}") String postsPath,
            @Value("${app.posts.reset:false}") boolean reset,
            @Value("${app.uploads.dir:uploads}") String uploadsDir
    ) {
        this.posts = posts;
        this.users = users;
        this.mapper = mapper;
        this.filePath = Path.of(postsPath);
        this.reset = reset;
        this.uploadsDir = Path.of(uploadsDir);
    }

    private User systemUser() {
        return users.findByEmail("system@studybuddy.local")
                .orElseGet(() -> users.save(User.builder()
                        .name("System")
                        .email("system@studybuddy.local")
                        .passwordHash("seeded")
                        .role(User.Role.ADMIN)
                        .build()));
    }

    public void importPostsFromFile() {
        try {
            Files.createDirectories(filePath.getParent());

            if (reset) {
                Files.writeString(filePath, "[]");
                System.out.println("Reset dataset file to empty: " + filePath);
            }

            if (!Files.exists(filePath)) {
                Files.writeString(filePath, "[]");
                System.out.println("No dataset file found. Created empty file at: " + filePath);
                return;
            }

            String json = Files.readString(filePath).trim();
            if (json.isBlank()) json = "[]";

            List<StudyPosts> savedPosts = List.of(mapper.readValue(json, StudyPosts[].class));

            for (StudyPosts post : savedPosts) {
                post.setId(null);

                User author = post.getAuthor();
                User persistedAuthor = null;

                if (author != null && author.getEmail() != null && !author.getEmail().isBlank()) {
                    persistedAuthor = users.findByEmail(author.getEmail()).orElse(null);
                }

                if (persistedAuthor == null) {
                    System.out.println("Author not found for post '" + post.getTitle()
                            + "'. Using system user.");
                    persistedAuthor = systemUser();
                }

                post.setAuthor(persistedAuthor);
                posts.save(post);
            }

            System.out.println("Loaded posts from file (" + filePath + "): " + savedPosts.size());

        } catch (NoSuchFileException e) {
            System.out.println("Dataset file missing: " + e.getMessage());
        } catch (JsonProcessingException e) {
            System.err.println("Dataset JSON is invalid in " + filePath + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error reading dataset " + filePath + ": " + e.getMessage());
        }
    }

    public void saveToFile() {
        try {
            List<StudyPosts> all = posts.findAll();
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(all));
            System.out.println("Saved " + all.size() + " posts to file: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to save posts to " + filePath + ": " + e.getMessage());
        }
    }

    public String savePostImage(Long postId, MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Empty file");
            }

            String original = file.getOriginalFilename();
            if (original == null || original.isBlank()) {
                original = "image";
            }

            String filename = UUID.randomUUID() + "-" + original.replaceAll("\\s+", "_");

            Path postDir = uploadsDir.resolve("posts").resolve(postId.toString());
            Files.createDirectories(postDir);

            Path target = postDir.resolve(filename);
            file.transferTo(target);

            return "/uploads/posts/" + postId + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }
    }
}

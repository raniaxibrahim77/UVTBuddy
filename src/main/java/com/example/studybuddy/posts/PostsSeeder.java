package com.example.studybuddy.posts;

import com.example.studybuddy.files.FileStorage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true")
public class PostsSeeder implements CommandLineRunner {

    private final FileStorage fileStorage;

    public PostsSeeder(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public void run(String... args) {
        fileStorage.importPostsFromFile();
    }
}

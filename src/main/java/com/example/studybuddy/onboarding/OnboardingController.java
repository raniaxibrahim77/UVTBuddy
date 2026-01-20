package com.example.studybuddy.onboarding;

import com.example.studybuddy.errors.UserNotFound;
import com.example.studybuddy.users.User;
import com.example.studybuddy.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final ChecklistItemRepository itemRepo;
    private final UserChecklistProgressRepository progressRepo;
    private final UserRepository userRepo;

    @GetMapping("/checklist")
    public List<ChecklistItem> checklist(@RequestParam(defaultValue = "en") String language) {
        return itemRepo.findByLanguageOrderBySortOrderAsc(language);
    }

    @PostMapping("/checklist/{itemId}/done")
    public String setDone(
            @PathVariable Long itemId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "true") boolean done
    ) {
        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFound(userId));
        ChecklistItem item = itemRepo.findById(itemId).orElseThrow();

        var existing = progressRepo.findByUserIdAndItemId(userId, itemId);
        UserChecklistProgress p = existing.orElseGet(() ->
                UserChecklistProgress.builder().user(user).item(item).build()
        );

        p.setDone(done);
        p.setDoneAt(done ? LocalDateTime.now() : null);
        progressRepo.save(p);

        return done ? "Marked done" : "Marked not done";
    }

    @GetMapping("/progress")
    public List<Long> progress(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "en") String language
    ) {
        return progressRepo.findDoneItemIds(userId, language);
    }

}

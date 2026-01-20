package com.example.studybuddy.onboarding;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true")
public class OnboardingSeeder implements CommandLineRunner {

    private final ChecklistItemRepository repo;

    public OnboardingSeeder(ChecklistItemRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {

        if (!repo.existsByLanguage("en")) {
            seedEnglish();
        }

        if (!repo.existsByLanguage("ro")) {
            seedRomanian();
        }
    }

    private void seedEnglish() {
        add("en", 1, "Activate UVT institutional email",
                "Set up your @e-uvt email and verify you can log in.");
        add("en", 2, "Log into StudentWeb",
                "Check enrollment status, grades section, and personal data.");
        add("en", 3, "Access course platforms (E-learning / Google Classroom)",
                "Confirm you can log into E-learning and Google Classroom. Courses use one or both for materials, assignments, and announcements.");
        add("en", 4, "Find your classrooms using UVT Maps",
                "Use the UVT virtual tour map to locate buildings and classrooms before your first lectures.");
        add("en", 5, "Get your student card",
                "Find where your faculty issues student cards and required documents.");
        add("en", 6, "Sort out accommodation / dorm registration",
                "If you live in a dorm, confirm your registration and rules.");
        add("en", 7, "Set up transport card / learn ticketing apps",
                "Install local transport apps and learn how tickets work.");
    }

    private void seedRomanian() {
        add("ro", 1, "Activează emailul instituțional UVT",
                "Configurează emailul @e-uvt și verifică dacă te poți autentifica.");
        add("ro", 2, "Autentifică-te în StudentWeb",
                "Verifică situația școlară, notele și datele personale.");
        add("ro", 3, "Accesează platformele de curs (E-learning / Google Classroom)",
                "Confirmă că te poți autentifica pe E-learning și Google Classroom pentru materiale, teme și anunțuri.");
        add("ro", 4, "Găsește sălile de curs folosind hărțile UVT",
                "Folosește harta virtuală UVT pentru a localiza clădirile și sălile înainte de primele cursuri.");
        add("ro", 5, "Obține legitimația de student",
                "Află unde se eliberează legitimațiile și ce documente sunt necesare.");
        add("ro", 6, "Rezolvă cazarea / înscrierea la cămin",
                "Dacă locuiești la cămin, confirmă înscrierea și regulile.");
        add("ro", 7, "Configurează abonamentul de transport",
                "Instalează aplicațiile locale de transport și învață cum funcționează biletele.");
    }

    private void add(String language, int order, String title, String description) {
        repo.save(ChecklistItem.builder()
                .language(language)
                .sortOrder(order)
                .title(title)
                .description(description)
                .build());
    }
}

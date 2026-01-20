package com.example.studybuddy.info;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true")
public class InfoSeeder implements CommandLineRunner {

    private final InfoResourceRepository repo;

    public InfoSeeder(InfoResourceRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;

        repo.save(new InfoResource(
                "Faculty of Informatics — Contact & Location",
                "Info Center (Secretariat)\n" +
                        "Phone: +40 256 592 155\n" +
                        "Email: secretariat.informatica@e-uvt.ro\n" +
                        "Address: B-dul Vasile Pârvan nr. 4, 300223 Timișoara, România",
                InfoCategory.CONTACT,
                Location.FACULTY_INFORMATICS,
                "https://www.google.com/maps/search/?api=1&query=B-dul%20Vasile%20P%C3%A2rvan%204%2C%20Timi%C8%99oara"
        ));

        repo.save(new InfoResource(
                "StudentWeb",
                "Official UVT platform for grades, enrollment status, financial information, and academic records.",
                InfoCategory.PLATFORMS,
                Location.GENERAL,
                "https://studentweb.uvt.ro/ums/do/secure/inregistrare_user"
        ));

        repo.save(new InfoResource(
                "E-learning UVT",
                "Official online learning platform used for courses, assignments, announcements, and communication with professors.",
                InfoCategory.PLATFORMS,
                Location.GENERAL,
                "https://elearning.e-uvt.ro/"
        ));

        repo.save(new InfoResource(
                "Google Classroom",
                "Alternative platform used by some courses for organizing materials, assignments, and communication.",
                InfoCategory.PLATFORMS,
                Location.GENERAL,
                "https://classroom.google.com/"
        ));

        repo.save(new InfoResource(
                "UVT Digital Resources & Tutorials",
                "Official tutorials and guides for using UVT digital platforms such as E-learning and Google Classroom.",
                InfoCategory.PLATFORMS,
                Location.GENERAL,
                "https://resurse.e-uvt.ro/"
        ));

        repo.save(new InfoResource(
                "UVT Maps — Virtual Campus Tour",
                "Interactive UVT map and virtual tour to help you find classrooms, and campus locations before your lectures.",
                InfoCategory.PLATFORMS,
                Location.GENERAL,
                "https://maps.uvt.ro/sediul-central/"
        ));

        repo.save(new InfoResource(
                "Student InfoCenter — Requests & Questions Form",
                "Submit administrative requests or ask questions via this official form. Contact: info@e-uvt.ro",
                InfoCategory.FORMS,
                Location.GENERAL,
                "https://docs.google.com/forms/d/e/1FAIpQLSd8x1XThBXhupUeY7HKgWuHFtb5JTUjUGQlIm4mKhqznu64Kw/viewform"
        ));

        repo.save(new InfoResource(
                "Erasmus",
                "Official Erasmus mobility information (incoming/outgoing).",
                InfoCategory.INTERNATIONAL,
                Location.GENERAL,
                "https://uvt.ro/en/international/erasmus/"
        ));

        repo.save(new InfoResource(
                "Accommodation Overview (Admission)",
                "Dormitory options and general accommodation overview.",
                InfoCategory.INTERNATIONAL,
                Location.GENERAL,
                "https://admitere.uvt.ro/en/student-la-uvt/cazare-in-caminele-uvt/"
        ));


        repo.save(new InfoResource(
                "Accommodation in UVT Dormitories",
                "Official dorm accommodation info and application details.",
                InfoCategory.INTERNATIONAL,
                Location.GENERAL,
                "https://uvt.ro/en/educatie/campus-uvt/cazare-in-caminele-uvt/"
        ));

        repo.save(new InfoResource(
                "Incoming Students — International Relations Contacts (PDF)",
                "Official info sheet with contact emails for Erasmus/International office.",
                InfoCategory.INTERNATIONAL,
                Location.GENERAL,
                "https://ri.uvt.ro/wp-content/uploads/2024/10/INFO-sheet-UVT-RO-TIMISOA01-for-incoming-students_.pdf"
        ));

    }
}

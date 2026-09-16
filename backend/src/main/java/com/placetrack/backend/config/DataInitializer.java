package com.placetrack.backend.config;

import com.placetrack.backend.entity.*;
import com.placetrack.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Initializes realistic sample placement records and interview preparation questions.
 *
 * IMPORTANT DISCLAIMER (as required by system specifications):
 * These are sample and configurable placement simulation records for academic demonstration.
 * They do NOT claim that these companies are currently recruiting from this institution, nor that
 * the displayed requirements represent official current company standards.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CompanyRepository companyRepository;
    private final JobOpportunityRepository jobRepository;
    private final PlacementDriveRepository driveRepository;
    private final InterviewQuestionRepository questionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        seedCompaniesAndJobs();
        seedInterviewQuestions();
    }

    private void seedCompaniesAndJobs() {
        if (companyRepository.count() >= 20) {
            log.info("Company directory already seeded ({} records found).", companyRepository.count());
            return;
        }

        log.info("Seeding realistic company directory and job opportunities...");

        LocalDate today = LocalDate.now();

        // 1. Google
        createCompanyWithJob("Google", "Technology & Cloud",
                "Google is an American multinational technology company focusing on artificial intelligence, search engine technology, cloud computing, and computer software.",
                "https://about.google", "Mountain View, CA", "Bangalore, Hyderabad, Gurgaon", "100,000+ employees",
                "Software Engineer", 32.5, 8.5, "CSE, IT, ECE", 2026, 0,
                "Java, Python, C++, Data Structures, Algorithms, Distributed Systems",
                "GCP, Kubernetes, System Design", today.plusDays(35), today.plusDays(25),
                "Online Assessment -> Technical Interview 1 -> Technical Interview 2 -> Googliness & Leadership");

        // 2. Microsoft
        createCompanyWithJob("Microsoft", "Software & Cloud",
                "Microsoft Corporation produces computer software, consumer electronics, personal computers, and related services, leading with Azure and enterprise tools.",
                "https://www.microsoft.com", "Redmond, WA", "Hyderabad, Bangalore, Noida", "100,000+ employees",
                "Software Development Engineer", 28.0, 8.0, "CSE, IT, ECE, EEE", 2026, 0,
                "C++, C#, Java, Data Structures, Algorithms, OOP",
                "Azure, SQL, Web Services", today.plusDays(28), today.plusDays(20),
                "Online Coding Challenge -> Technical Round 1 -> Technical Round 2 -> AA (As Appropriate) Round");

        // 3. Amazon
        createCompanyWithJob("Amazon", "E-Commerce & Cloud Computing",
                "Amazon focuses on e-commerce, cloud computing (AWS), digital streaming, and artificial intelligence, operating at immense global scale.",
                "https://www.amazon.jobs", "Seattle, WA", "Bangalore, Hyderabad, Chennai, Pune", "100,000+ employees",
                "Software Development Engineer I (SDE-1)", 26.0, 7.5, "CSE, IT, ECE, EEE, MECH", 2026, 0,
                "Java, Python, Algorithms, Data Structures, OOP, Problem Solving",
                "AWS, System Architecture, Leadership Principles", today.plusDays(21), today.plusDays(14),
                "Online Assessment (Code + Work Style) -> Technical Interview 1 -> Technical Interview 2 -> Bar Raiser");

        // 4. Apple
        createCompanyWithJob("Apple", "Consumer Electronics & Software",
                "Apple is a premier technology firm famous for iPhone, Mac, iOS, iPadOS, and pioneering integrated hardware and software ecosystems.",
                "https://www.apple.com/careers", "Cupertino, CA", "Bangalore, Hyderabad", "100,000+ employees",
                "Software Quality & Tools Engineer", 30.0, 8.5, "CSE, IT, ECE", 2026, 0,
                "Python, Swift, C++, Operating Systems, Computer Networks",
                "macOS, Linux, Test Automation", today.plusDays(45), today.plusDays(30),
                "Resume Screening -> Technical Screen -> 3 Technical Rounds -> Manager Round");

        // 5. Meta
        createCompanyWithJob("Meta", "Social Media & Metaverse",
                "Meta builds technologies that help people connect, find communities, and grow businesses across Facebook, Instagram, WhatsApp, and Quest.",
                "https://www.metacareers.com", "Menlo Park, CA", "Bangalore, Gurgaon", "50,000+ employees",
                "Software Engineer - Core Systems", 35.0, 8.5, "CSE, IT", 2026, 0,
                "C++, Python, Algorithms, Systems Programming, SQL",
                "Distributed Systems, React, PyTorch", today.plusDays(50), today.plusDays(35),
                "Online Assessment -> Technical Phone Screen -> Onsite Virtual (Coding, System Design, Behavioral)");

        // 6. IBM
        createCompanyWithJob("IBM", "Enterprise Technology & Hybrid Cloud",
                "International Business Machines Corporation is a global technology pioneer in hybrid cloud, artificial intelligence (watsonx), and consulting.",
                "https://www.ibm.com", "Armonk, NY", "Bangalore, Chennai, Hyderabad, Pune", "100,000+ employees",
                "Associate System Engineer", 8.5, 6.5, "CSE, IT, ECE, EEE, AIDS, AIML", 2026, 1,
                "Java, Python, DBMS, SQL, Linux Basics",
                "Cloud Computing, Docker, Git", today.plusDays(15), today.plusDays(10),
                "Cognitive Ability Assessment -> Coding Assessment -> Technical Interview -> HR Round");

        // 7. Oracle
        createCompanyWithJob("Oracle", "Database Software & Cloud",
                "Oracle Corporation offers enterprise software products including the Oracle Autonomous Database and Oracle Cloud Infrastructure (OCI).",
                "https://www.oracle.com/careers", "Austin, TX", "Bangalore, Hyderabad, Pune, Noida", "100,000+ employees",
                "Associate Applications Developer", 18.0, 7.5, "CSE, IT, ECE", 2026, 0,
                "Java, SQL, PL/SQL, Database Design, Data Structures",
                "OCI, Spring Boot, Microservices", today.plusDays(30), today.plusDays(22),
                "Online Test (Aptitude + Coding) -> 2 Technical Rounds -> Techno-Managerial Round");

        // 8. Salesforce
        createCompanyWithJob("Salesforce", "Enterprise CRM & SaaS",
                "Salesforce is a global leader in customer relationship management (CRM) software and enterprise cloud applications.",
                "https://www.salesforce.com", "San Francisco, CA", "Hyderabad, Bangalore", "50,000+ employees",
                "Associate Member Technical Staff (AMTS)", 24.0, 8.0, "CSE, IT, ECE", 2026, 0,
                "Java, Python, OOP, Data Structures, Web Development",
                "Apex, JavaScript, Lightning Web Components", today.plusDays(40), today.plusDays(28),
                "Online Coding Assessment -> Technical Round 1 -> Technical Round 2 -> Hiring Manager");

        // 9. Adobe
        createCompanyWithJob("Adobe", "Digital Media & Experience Software",
                "Adobe is famous for Photoshop, Acrobat, Creative Cloud, and digital marketing experience solutions.",
                "https://www.adobe.com/careers", "San Jose, CA", "Bangalore, Noida", "25,000+ employees",
                "Software Engineer - Product Development", 22.0, 8.0, "CSE, IT, ECE", 2026, 0,
                "C++, Java, Algorithms, Data Structures, Problem Solving",
                "Computer Graphics, Machine Learning, Web Technologies", today.plusDays(38), today.plusDays(26),
                "Online Test -> Technical Interview 1 -> Technical Interview 2 -> HR Round");

        // 10. Intel
        createCompanyWithJob("Intel", "Semiconductors & Computing",
                "Intel Corporation designs and manufactures microprocessors and foundry semiconductors driving modern computing.",
                "https://www.intel.com", "Santa Clara, CA", "Bangalore, Hyderabad", "100,000+ employees",
                "Graduate Software Engineer", 16.5, 7.5, "CSE, IT, ECE, EEE", 2026, 0,
                "C, C++, Computer Architecture, Operating Systems, Python",
                "Embedded Systems, Linux Kernel, FPGA", today.plusDays(32), today.plusDays(22),
                "Technical Screening -> Architecture Round -> Coding Round -> HR Discussion");

        // 11. NVIDIA
        createCompanyWithJob("NVIDIA", "GPU, AI & Accelerated Computing",
                "NVIDIA pioneers GPU design, CUDA parallel computing architecture, and foundational AI computing hardware and software.",
                "https://www.nvidia.com", "Santa Clara, CA", "Bangalore, Pune, Hyderabad", "25,000+ employees",
                "System Software Engineer", 25.0, 8.5, "CSE, IT, ECE, EEE", 2026, 0,
                "C++, C, CUDA, Computer Architecture, Data Structures",
                "Deep Learning, Linux Internals, Multi-threading", today.plusDays(42), today.plusDays(30),
                "Coding Test -> Technical Round 1 -> Technical Round 2 -> Director Round");

        // 12. Cisco
        createCompanyWithJob("Cisco", "Networking & Cybersecurity",
                "Cisco Systems designs, manufactures, and sells networking hardware, software, telecommunications equipment, and cybersecurity tools.",
                "https://www.cisco.com", "San Jose, CA", "Bangalore, Chennai", "50,000+ employees",
                "Software Engineer - Network Applications", 17.5, 7.5, "CSE, IT, ECE, EEE", 2026, 0,
                "Python, C, Computer Networks, TCP/IP, Linux, Data Structures",
                "SDN, Docker, REST APIs", today.plusDays(25), today.plusDays(18),
                "Online Assessment -> Technical Interview 1 -> Technical Interview 2 -> Managerial/HR");

        // 13. SAP
        createCompanyWithJob("SAP", "Enterprise ERP Software",
                "SAP is market leader in enterprise application software, helping companies of all sizes manage business operations and customer relations.",
                "https://www.sap.com", "Walldorf, Germany", "Bangalore, Gurgaon, Pune", "100,000+ employees",
                "Developer Associate", 15.0, 7.5, "CSE, IT, ECE", 2026, 0,
                "Java, ABAP, SQL, OOP, Web Technologies",
                "Spring Boot, SAP BTP, OData", today.plusDays(29), today.plusDays(21),
                "Aptitude & Coding Assessment -> Technical Round 1 -> Technical Round 2 -> HR");

        // 14. Dell Technologies
        createCompanyWithJob("Dell Technologies", "Hardware & Enterprise Infrastructure",
                "Dell Technologies helps organizations build digital future with IT infrastructure, servers, workstations, and storage systems.",
                "https://jobs.dell.com", "Round Rock, TX", "Bangalore, Hyderabad", "100,000+ employees",
                "Software Development Engineer", 12.0, 7.0, "CSE, IT, ECE, EEE", 2026, 1,
                "Java, Python, C++, Operating Systems, Computer Networks",
                "Cloud, Microservices, Storage Protocols", today.plusDays(22), today.plusDays(15),
                "Online Assessment -> Technical Round -> Management Interview -> HR");

        // 15. Tata Consultancy Services (TCS)
        createCompanyWithJob("Tata Consultancy Services (TCS)", "IT Services & Consulting",
                "TCS is an Indian multinational IT services, consulting, and business solutions organisation with presence in 50+ countries.",
                "https://www.tcs.com/careers", "Mumbai, Maharashtra", "Chennai, Bangalore, Hyderabad, Pune, Kolkata", "500,000+ employees",
                "Assistant System Engineer (Digital & Prime)", 7.2, 6.0, "CSE, IT, ECE, EEE, MECH, CIVIL, AIDS, AIML, MCA", 2026, 1,
                "Java, Python, C, SQL, Aptitude, Problem Solving",
                "Web Development, Cloud Basics, Git", today.plusDays(10), today.plusDays(5),
                "TCS NQT (Cognitive + Programming) -> Technical Interview -> HR Round");

        // 16. Infosys
        createCompanyWithJob("Infosys", "IT Services & Digital Transformation",
                "Infosys is a global leader in next-generation digital services and consulting, enabling clients across 56 countries to navigate their digital transformation.",
                "https://www.infosys.com/careers", "Bangalore, Karnataka", "Bangalore, Chennai, Hyderabad, Pune, Mysore", "300,000+ employees",
                "Systems Engineer (Specialist Programmer & DSE)", 9.5, 6.5, "CSE, IT, ECE, EEE, MECH, CIVIL, AIDS, AIML, MCA", 2026, 1,
                "Java, Python, Data Structures, OOP, SQL",
                "Full Stack, Spring Boot, Angular/React", today.plusDays(12), today.plusDays(7),
                "InfyTQ / Certification Test -> Technical Interview -> HR Interview");

        // 17. Wipro
        createCompanyWithJob("Wipro", "Information Technology & Consulting",
                "Wipro Limited is an Indian multinational corporation that provides information technology, consulting, and business process services.",
                "https://careers.wipro.com", "Bangalore, Karnataka", "Bangalore, Chennai, Hyderabad, Pune, Coimbatore", "200,000+ employees",
                "Project Engineer - Elite & Turbo", 6.5, 6.0, "CSE, IT, ECE, EEE, MECH, CIVIL, AIDS, AIML", 2026, 1,
                "Java, C++, Python, Aptitude, SQL Basics",
                "Cloud, Web Technologies", today.plusDays(14), today.plusDays(8),
                "Wipro NLTH (Aptitude + Coding) -> Technical Interview -> HR Discussion");

        // 18. HCLTech
        createCompanyWithJob("HCLTech", "Next-Gen IT & Engineering Services",
                "HCLTech offers integrated services across IT, Engineering and R&D, and Enterprise software products to global businesses.",
                "https://www.hcltech.com/careers", "Noida, Uttar Pradesh", "Chennai, Bangalore, Noida, Madurai, Lucknow", "200,000+ employees",
                "Graduate Engineer Trainee", 6.0, 6.0, "CSE, IT, ECE, EEE, AIDS, AIML", 2026, 1,
                "C, C++, Java, Database Concepts, Networking",
                "Cloud Basics, Python", today.plusDays(16), today.plusDays(10),
                "Online Assessment -> Technical Interview -> HR Round");

        // 19. Tech Mahindra
        createCompanyWithJob("Tech Mahindra", "Telecommunications & Digital IT",
                "Tech Mahindra represents the connected world, offering innovative and customer-centric digital experiences across telecom and enterprise verticals.",
                "https://careers.techmahindra.com", "Pune, Maharashtra", "Pune, Hyderabad, Chennai, Bangalore", "150,000+ employees",
                "Associate Software Engineer", 5.5, 6.0, "CSE, IT, ECE, EEE, MECH, AIDS, AIML", 2026, 1,
                "Java, Python, C++, Aptitude, Logical Reasoning",
                "Telecom Concepts, Cloud", today.plusDays(18), today.plusDays(12),
                "Online Aptitude & Coding -> Technical Round -> HR Round");

        // 20. Cognizant
        createCompanyWithJob("Cognizant", "IT Consulting & Digital Operations",
                "Cognizant helps clients modernize technology, reimagine processes and transform experiences in the fast-changing world.",
                "https://careers.cognizant.com", "Teaneck, NJ", "Chennai, Bangalore, Hyderabad, Coimbatore, Kolkata", "300,000+ employees",
                "Programmer Analyst Trainee (GenC Next)", 6.75, 6.5, "CSE, IT, ECE, EEE, AIDS, AIML, MCA", 2026, 0,
                "Java, Python, SQL, OOP, Data Structures",
                "Spring Boot, React, Cloud", today.plusDays(20), today.plusDays(14),
                "GenC Assessment (Aptitude + Advanced Coding) -> Technical Interview -> HR");

        // 21. Accenture
        createCompanyWithJob("Accenture", "Strategy, Consulting & Digital Technology",
                "Accenture is a leading global professional services company providing a broad range of services in strategy, consulting, digital, and cloud.",
                "https://www.accenture.com/careers", "Dublin, Ireland", "Bangalore, Chennai, Hyderabad, Pune, Mumbai", "500,000+ employees",
                "Advanced Application Engineering Associate", 6.5, 6.5, "CSE, IT, ECE, EEE, AIDS, AIML, MCA", 2026, 1,
                "Java, Python, Full Stack, Data Structures, Critical Thinking",
                "Cloud, DevOps, Agile", today.plusDays(17), today.plusDays(11),
                "Cognitive & Technical Assessment -> Coding Test -> Communication Test -> Technical/HR Interview");

        // 22. Capgemini
        createCompanyWithJob("Capgemini", "Digital Transformation & Technology",
                "Capgemini is a global leader in partnering with companies to transform and manage their business by harnessing the power of technology.",
                "https://www.capgemini.com/careers", "Paris, France", "Bangalore, Chennai, Hyderabad, Pune, Mumbai", "300,000+ employees",
                "Software Analyst / Senior Software Analyst", 7.5, 6.5, "CSE, IT, ECE, EEE, AIDS, AIML", 2026, 0,
                "Java, Python, Cloud, Data Structures, SQL",
                "Spring, Angular, React", today.plusDays(24), today.plusDays(16),
                "Online Assessment -> Spoken English Test -> Technical Round -> HR Round");

        // 23. LTIMindtree
        createCompanyWithJob("LTIMindtree", "Digital Solutions & Enterprise Consulting",
                "LTIMindtree is a global technology consulting and digital solutions company helping more than 700 clients reimagine business models.",
                "https://www.ltimindtree.com", "Mumbai, Maharashtra", "Bangalore, Chennai, Mumbai, Pune, Hyderabad", "80,000+ employees",
                "Graduate Engineer Trainee", 6.5, 6.5, "CSE, IT, ECE, AIDS, AIML", 2026, 0,
                "Java, Python, SQL, OOP, Web Basics",
                "Spring Boot, Cloud", today.plusDays(26), today.plusDays(19),
                "Online Assessment -> Technical Interview -> HR Discussion");

        // 24. Mphasis
        createCompanyWithJob("Mphasis", "Cloud & Cognitive Services",
                "Mphasis applies next-generation technology to help enterprises transform businesses globally with cloud and cognitive services.",
                "https://www.mphasis.com/careers", "Bangalore, Karnataka", "Bangalore, Chennai, Pune, Hyderabad", "35,000+ employees",
                "Associate Software Engineer", 5.0, 6.0, "CSE, IT, ECE, EEE", 2026, 1,
                "Java, SQL, C++, Aptitude, Communication",
                "HTML, CSS, JavaScript", today.plusDays(27), today.plusDays(19),
                "Online Test -> Technical Interview -> HR Round");

        // 25. Persistent Systems
        createCompanyWithJob("Persistent Systems", "Digital Engineering & Enterprise Modernization",
                "Persistent Systems is a trusted digital engineering and enterprise modernization partner with strong focus on software product engineering.",
                "https://www.persistent.com/careers", "Pune, Maharashtra", "Pune, Bangalore, Hyderabad, Goa", "22,000+ employees",
                "Software Engineer Trainee", 7.0, 6.5, "CSE, IT, ECE", 2026, 0,
                "Java, C++, Python, Data Structures, SQL",
                "Spring, Cloud, Microservices", today.plusDays(31), today.plusDays(23),
                "Objective Test -> Coding Challenge -> Technical Round -> HR Round");

        // 26. Coforge
        createCompanyWithJob("Coforge", "Digital Services & Enterprise Software",
                "Coforge is a global digital services and solutions provider that leverages emerging technologies to deliver real-world business impact.",
                "https://www.coforge.com", "Noida, Uttar Pradesh", "Noida, Bangalore, Hyderabad, Pune", "25,000+ employees",
                "Graduate Trainee Engineer", 5.5, 6.0, "CSE, IT, ECE, EEE", 2026, 1,
                "Java, SQL, OOP, Problem Solving, Aptitude",
                "Web Technologies, Git", today.plusDays(33), today.plusDays(24),
                "Aptitude + Technical Test -> Technical Interview -> HR");

        // 27. Zoho
        createCompanyWithJob("Zoho", "SaaS & Business Software",
                "Zoho Corporation makes software to help you run your entire business, completely engineered in India from Chennai, Tenkasi, and beyond.",
                "https://www.zoho.com/careers", "Chennai, Tamil Nadu", "Chennai, Tenkasi, Coimbatore, Salem, Madurai", "15,000+ employees",
                "Member Technical Staff - Software Development", 8.4, 6.0, "CSE, IT, ECE, EEE, MECH, CIVIL, AIDS, AIML, MCA", 2026, 2,
                "C, Java, C++, Problem Solving, Logic, Data Structures",
                "Clean Coding, Web Basics, Algorithms", today.plusDays(15), today.plusDays(8),
                "Round 1 (Basic Programming) -> Round 2 (Advanced Programming) -> Round 3 (App Design) -> HR");

        // 28. Freshworks
        createCompanyWithJob("Freshworks", "Cloud-Based Customer & IT Software",
                "Freshworks makes delightful SaaS software for customer relationship management and IT service management used across 60,000+ companies.",
                "https://www.freshworks.com/careers", "San Mateo, CA", "Chennai, Bangalore", "5,000+ employees",
                "Product Development Engineer", 14.0, 7.5, "CSE, IT, ECE", 2026, 0,
                "JavaScript, Ruby, Java, Python, Web Architecture, Data Structures",
                "React, Node.js, AWS, Redis", today.plusDays(36), today.plusDays(25),
                "Online Coding Round -> Technical Interview 1 -> Technical Interview 2 -> Culture Fit Round");

        // 29. Flipkart
        createCompanyWithJob("Flipkart", "E-Commerce Ecosystem",
                "Flipkart is India's leading e-commerce marketplace with over 500 million registered customers and state-of-the-art logistics technology.",
                "https://www.flipkartcareers.com", "Bangalore, Karnataka", "Bangalore", "30,000+ employees",
                "Software Development Engineer - 1", 20.0, 7.5, "CSE, IT, ECE", 2026, 0,
                "Java, Python, Algorithms, Data Structures, OOP, SQL",
                "Microservices, Kafka, Redis, Distributed Systems", today.plusDays(34), today.plusDays(22),
                "Online Coding Test -> Machine Coding Round -> Problem Solving / DSA -> Hiring Manager");

        // 30. PhonePe
        createCompanyWithJob("PhonePe", "Digital Payments & FinTech",
                "PhonePe is India's leading digital payments and financial services company processing billions of UPI transactions every month.",
                "https://www.phonepe.com/careers", "Bangalore, Karnataka", "Bangalore, Pune", "5,000+ employees",
                "Software Engineer - Backend Platform", 22.0, 8.0, "CSE, IT", 2026, 0,
                "Java, Go, Data Structures, Algorithms, Concurrency, SQL",
                "High-Throughput Systems, Kafka, Cassandra", today.plusDays(39), today.plusDays(27),
                "Online Assessment -> Problem Solving Round -> System Design / Concurrency -> HR");

        // 31. Paytm
        createCompanyWithJob("Paytm", "Fintech & Payments Gateway",
                "One97 Communications (Paytm) is India's leading digital payments and financial services company empowering merchants and consumers.",
                "https://paytm.com/careers", "Noida, Uttar Pradesh", "Noida, Bangalore", "10,000+ employees",
                "Associate Software Engineer", 12.0, 7.0, "CSE, IT, ECE", 2026, 1,
                "Java, Python, MySQL, Spring Boot, Data Structures",
                "RESTful APIs, Redis, Docker", today.plusDays(28), today.plusDays(20),
                "Coding Test -> Technical Round 1 -> Technical Round 2 -> HR Round");

        // 32. Razorpay
        createCompanyWithJob("Razorpay", "Payments & Banking Platform",
                "Razorpay provides payment gateway and financial infrastructure services that power payments for top businesses in India.",
                "https://razorpay.com/jobs", "Bangalore, Karnataka", "Bangalore", "3,000+ employees",
                "Software Development Engineer I", 21.0, 7.5, "CSE, IT, ECE", 2026, 0,
                "Java, Golang, Python, MySQL, Algorithms, System Concepts",
                "AWS, Docker, Microservices, FinTech Security", today.plusDays(41), today.plusDays(29),
                "Online Coding -> Technical Discussion (DSA) -> System Design / Architecture -> Culture Fit");

        // 33. Swiggy
        createCompanyWithJob("Swiggy", "Hyperlocal Delivery & Food Tech",
                "Swiggy is India's premier on-demand convenience platform connecting consumers to food delivery, groceries, and dining out.",
                "https://careers.swiggy.com", "Bangalore, Karnataka", "Bangalore", "6,000+ employees",
                "Software Development Engineer - 1", 18.0, 7.5, "CSE, IT", 2026, 0,
                "Java, Go, Python, Algorithms, Data Structures, SQL",
                "Kafka, Distributed Caching, Microservices", today.plusDays(37), today.plusDays(26),
                "Online Coding Test -> Data Structures Round -> Machine Coding -> Managerial Discussion");

        // 34. Zomato
        createCompanyWithJob("Zomato", "Food Delivery & Restaurant Tech",
                "Zomato connects customers, restaurant partners, and delivery partners across India and international markets with world-class tech.",
                "https://www.zomato.com/careers", "Gurgaon, Haryana", "Gurgaon, Bangalore", "5,000+ employees",
                "Software Engineer - Platform", 18.5, 7.5, "CSE, IT, ECE", 2026, 0,
                "Python, Go, Java, Data Structures, Relational Databases",
                "FastAPI, Docker, Cloud, High Availability", today.plusDays(43), today.plusDays(31),
                "Coding Round -> Problem Solving Round -> Engineering Manager Discussion -> HR");

        // 35. Meesho
        createCompanyWithJob("Meesho", "Social Commerce & Marketplace",
                "Meesho is India's fastest-growing internet commerce company democratizing e-commerce for everyone.",
                "https://www.meesho.io/jobs", "Bangalore, Karnataka", "Bangalore", "2,000+ employees",
                "SDE 1 - Backend & Growth", 19.0, 7.5, "CSE, IT", 2026, 0,
                "Java, Python, Algorithms, MySQL, Redis, OOP",
                "Distributed Systems, Spring Boot, Big Data", today.plusDays(44), today.plusDays(32),
                "Online Assessment -> Problem Solving Round -> Low Level Design -> Bar Raiser");

        // 36. Groww
        createCompanyWithJob("Groww", "Investment & WealthTech",
                "Groww is India's leading financial services platform allowing millions of users to invest in stocks, mutual funds, and IPOs easily.",
                "https://groww.in/careers", "Bangalore, Karnataka", "Bangalore", "2,000+ employees",
                "Software Development Engineer", 19.5, 8.0, "CSE, IT, ECE", 2026, 0,
                "Java, Kotlin, Spring Boot, Data Structures, Algorithms, SQL",
                "Microservices, Kafka, Redis, Distributed Transactions", today.plusDays(46), today.plusDays(33),
                "Online Test -> 2 Problem Solving & Coding Rounds -> Managerial Round");

        // 37. Zerodha
        createCompanyWithJob("Zerodha", "Discount Broking & Fintech Infrastructure",
                "Zerodha is the largest retail stockbroker in India, famous for its lean, open-source technology stack operating at massive financial scale.",
                "https://zerodha.com/careers", "Bangalore, Karnataka", "Bangalore", "1,000+ employees",
                "Junior Software Engineer", 16.0, 7.0, "CSE, IT, ECE, EEE", 2026, 0,
                "Python, Go, PostgreSQL, Redis, Linux, Networking, Git",
                "Open Source Contributions, Clean Code, Performance Tuning", today.plusDays(48), today.plusDays(35),
                "Code Review & Assignment -> Technical Discussion -> Senior Tech Architect Round");

        log.info("Finished seeding company directory and job opportunities successfully.");
    }

    private void createCompanyWithJob(String name, String industry, String desc, String website,
                                     String hq, String location, String size,
                                     String role, double packageLpa, double minCgpa, String depts,
                                     int gradYear, int maxBacklogs, String reqSkills, String prefSkills,
                                     LocalDate driveDate, LocalDate deadline, String selectionProcess) {
        Company company = new Company();
        company.setName(name);
        company.setIndustry(industry);
        company.setDescription(desc);
        company.setWebsite(website);
        company.setHeadquarters(hq);
        company.setLocation(location);
        company.setCompanySize(size);
        company.setActive(true);
        company.setJobRole(role);
        company.setPackageLpa(packageLpa);
        company.setDriveEligibilityCgpa(minCgpa);
        company.setDriveDate(driveDate);
        company.setApplicationDeadline(deadline);
        company.setDriveLocation(location.contains(",") ? location.split(",")[0].trim() : location);
        company.setSelectionProcess(selectionProcess);
        company.setDriveStatus(driveDate.isAfter(LocalDate.now()) ? Company.DriveStatus.UPCOMING : Company.DriveStatus.OPEN);
        company.setDriveEligibilitySummary("Min " + minCgpa + " CGPA | " + depts + " | " + gradYear + " Batch | Max " + maxBacklogs + " Backlogs");
        Company savedCompany = companyRepository.save(company);

        // Link primary JobOpportunity
        JobOpportunity job = new JobOpportunity();
        job.setCompany(savedCompany);
        job.setJobRole(role);
        job.setDescription(desc);
        job.setPackageLpa(packageLpa);
        job.setJobType(JobOpportunity.JobType.FULL_TIME);
        job.setWorkLocation(company.getDriveLocation());
        job.setMinCgpa(minCgpa);
        job.setEligibleDepartments(depts);
        job.setEligibleYear(4);
        job.setGraduationYear(gradYear);
        job.setBacklogsAllowed(maxBacklogs);
        job.setRequiredSkills(reqSkills);
        job.setPreferredSkills(prefSkills);
        job.setDriveDate(driveDate);
        job.setApplicationDeadline(deadline);
        job.setSelectionProcess(selectionProcess);
        job.setStatus(JobOpportunity.OpportunityStatus.OPEN);
        JobOpportunity savedJob = jobRepository.save(job);

        // Also create a PlacementDrive calendar entry
        PlacementDrive drive = new PlacementDrive();
        drive.setCompany(savedCompany);
        drive.setJobOpportunity(savedJob);
        drive.setTitle(name + " On-Campus Recruitment Drive 2026");
        drive.setPackageLpa(packageLpa);
        drive.setMinCgpa(minCgpa);
        drive.setEligibleDepartments(depts);
        drive.setEligibleYear(4);
        drive.setGraduationYear(gradYear);
        drive.setBacklogsAllowed(maxBacklogs);
        drive.setRequiredSkills(reqSkills);
        drive.setPreferredSkills(prefSkills);
        drive.setDriveDate(driveDate);
        drive.setApplicationDeadline(deadline);
        drive.setDriveLocation(company.getDriveLocation());
        drive.setSelectionProcess(selectionProcess);
        drive.setStatus(PlacementDrive.Status.UPCOMING);
        driveRepository.save(drive);
    }

    private void seedInterviewQuestions() {
        if (questionRepository.count() >= 30) {
            log.info("Interview question bank already seeded ({} questions found).", questionRepository.count());
            return;
        }

        log.info("Seeding interview preparation question bank...");
        List<InterviewQuestion> list = new ArrayList<>();

        // Aptitude / Quant / Logical
        list.add(new InterviewQuestion(null, InterviewQuestion.Category.QUANTITATIVE_APTITUDE, InterviewQuestion.Difficulty.EASY,
                "A train running at the speed of 60 km/hr crosses a pole in 9 seconds. What is the length of the train?",
                "150 metres",
                "Speed = 60 * (5/18) m/sec = 50/3 m/sec. Length of train = Speed * Time = (50/3) * 9 = 150 metres.",
                "Speed Time Distance", true));

        list.add(new InterviewQuestion(null, InterviewQuestion.Category.QUANTITATIVE_APTITUDE, InterviewQuestion.Difficulty.MEDIUM,
                "A person incurs 10% loss by selling a watch for Rs. 1800. At what price should the watch be sold to earn a 10% profit?",
                "Rs. 2200",
                "Let CP be Cost Price. Selling at 10% loss means SP = 0.9 * CP = 1800 => CP = 2000. To gain 10%, SP = 1.1 * 2000 = Rs. 2200.",
                "Profit and Loss", true));

        list.add(new InterviewQuestion(null, InterviewQuestion.Category.LOGICAL_REASONING, InterviewQuestion.Difficulty.EASY,
                "Find the next number in the sequence: 3, 7, 15, 31, 63, ...",
                "127",
                "Each term is generated by (previous_term * 2) + 1. So (63 * 2) + 1 = 126 + 1 = 127.",
                "Number Series", true));

        list.add(new InterviewQuestion(null, InterviewQuestion.Category.LOGICAL_REASONING, InterviewQuestion.Difficulty.MEDIUM,
                "Pointing to a photograph, a man said, 'I have no brother or sister, but that man's father is my father's son.' Whose photograph was it?",
                "His son's photograph",
                "Since he has no brother or sister, 'my father's son' is himself. So 'that man's father is me' -> photograph is his son.",
                "Blood Relations", true));

        // Programming / Java / Python / C++
        list.add(new InterviewQuestion(null, InterviewQuestion.Category.JAVA, InterviewQuestion.Difficulty.EASY,
                "What is the difference between equals() and == in Java?",
                "== checks reference equality (memory location), whereas equals() checks value equality based on implementation.",
                "== compares if two references point to the exact same object in memory. String or wrapper classes override .equals() to compare actual content rather than memory address.",
                "Core Java", true));

        list.add(new InterviewQuestion(null, InterviewQuestion.Category.JAVA, InterviewQuestion.Difficulty.MEDIUM,
                "How does HashMap work internally in Java?",
                "HashMap works on hashing. It uses an array of Node (Bucket) consisting of hash, key, value, and next pointer. From Java 8, bucket treeifies to Red-Black Tree if collisions exceed threshold 8.",
                "When put(K, V) is invoked, hash(key) calculates index: (n - 1) & hash. If index is empty, new Node is placed. If collision occurs, LinkedList chaining is used. If chain length reaches 8 and capacity >= 64, it converts to TreeNode for O(log N) lookup.",
                "Collections", true));

        list.add(new InterviewQuestion(null, InterviewQuestion.Category.PYTHON, InterviewQuestion.Difficulty.EASY,
                "What is the difference between a list and a tuple in Python?",
                "Lists are mutable and enclosed in [], while tuples are immutable and enclosed in ().",
                "Tuples cannot be modified after creation, which makes them faster, memory-efficient, and eligible to be used as dictionary keys if all their elements are hashable.",
                "Python Basics", true));

        list.add(new InterviewQuestion(null, InterviewQuestion.Category.PYTHON, InterviewQuestion.Difficulty.MEDIUM,
                "Explain the Global Interpreter Lock (GIL) in Python.",
                "GIL is a mutex that allows only one thread to hold the control of the Python interpreter at any given time.",
                "This means in CPython, multi-threaded CPU-bound programs will not utilize multiple CPU cores concurrently. For CPU-heavy parallel tasks, multiprocessing or asynchronous libraries should be used instead.",
                "Python Concurrency", true));

        // SQL / DBMS
        list.add(new InterviewQuestion(null, InterviewQuestion.Category.SQL, InterviewQuestion.Difficulty.EASY,
                "What is the difference between WHERE and HAVING clauses in SQL?",
                "WHERE filters rows before grouping occurs; HAVING filters grouped rows after the GROUP BY clause.",
                "WHERE cannot use aggregate functions like COUNT(), SUM(), AVG(), whereas HAVING is specifically designed to filter on aggregate calculations.",
                "SQL Queries", true));

        list.add(new InterviewQuestion(null, InterviewQuestion.Category.SQL, InterviewQuestion.Difficulty.MEDIUM,
                "Write an SQL query to find the second highest salary from an Employee table.",
                "SELECT MAX(salary) FROM Employee WHERE salary < (SELECT MAX(salary) FROM Employee); OR using DENSE_RANK(): SELECT salary FROM (SELECT salary, DENSE_RANK() OVER(ORDER BY salary DESC) as rnk FROM Employee) t WHERE rnk = 2 LIMIT 1;",
                "The subquery approach finds the maximum salary strictly less than the top salary. DENSE_RANK() window function correctly handles ties when multiple employees earn the top salary.",
                "SQL Subqueries", true));

        list.add(new InterviewQuestion(null, InterviewQuestion.Category.DBMS, InterviewQuestion.Difficulty.MEDIUM,
                "What are ACID properties in Database Management Systems?",
                "Atomicity (all or nothing), Consistency (preserves DB invariants), Isolation (concurrent transactions do not interfere), Durability (committed changes persist).",
                "Transactions ensure data integrity under concurrent access and system crashes. For instance, in banking transfers, money must be debited and credited in one atomic action.",
                "Transactions", true));

        // OOP
        list.add(new InterviewQuestion(null, InterviewQuestion.Category.OOP, InterviewQuestion.Difficulty.EASY,
                "Explain the four main pillars of Object-Oriented Programming (OOP).",
                "1. Encapsulation (bundling data and methods), 2. Abstraction (hiding implementation details), 3. Inheritance (reusing parent class properties), 4. Polymorphism (ability to take multiple forms).",
                "Encapsulation protects internal state via private fields and getters/setters. Abstraction uses interfaces/abstract classes. Polymorphism is achieved via overloading (compile time) and overriding (runtime).",
                "OOP Fundamentals", true));

        // Data Structures & Algorithms
        list.add(new InterviewQuestion(null, InterviewQuestion.Category.DATA_STRUCTURES, InterviewQuestion.Difficulty.MEDIUM,
                "How do you detect a cycle in a singly linked list?",
                "Use Floyd's Cycle-Finding Algorithm (Tortoise and Hare) with two pointers moving at different speeds.",
                "Initialize slow and fast pointers at head. Move slow by 1 step and fast by 2 steps. If slow == fast at any point, a cycle exists. If fast or fast.next reaches null, there is no cycle. Time: O(N), Space: O(1).",
                "Linked List", true));

        list.add(new InterviewQuestion(null, InterviewQuestion.Category.ALGORITHMS, InterviewQuestion.Difficulty.HARD,
                "Explain the difference between Dijkstra's algorithm and Bellman-Ford algorithm.",
                "Dijkstra uses a greedy approach with a priority queue (O((V + E) log V)) and works only on non-negative edge weights. Bellman-Ford is dynamic programming (O(V * E)) and can handle negative edge weights and detect negative cycles.",
                "Dijkstra greedily locks finalized shortest paths, which breaks if a negative edge later reduces total cost. Bellman-Ford relaxes all edges V-1 times, guaranteeing correct shortest paths or reporting a negative weight cycle on the Vth relaxation.",
                "Graph Algorithms", true));

        // Operating Systems & Networks
        list.add(new InterviewQuestion(null, InterviewQuestion.Category.OPERATING_SYSTEMS, InterviewQuestion.Difficulty.MEDIUM,
                "What is the difference between a process and a thread?",
                "A process is an executing instance of a program with its own memory address space. A thread is a lightweight unit of execution within a process that shares memory and resources with other threads of that process.",
                "Context switching between processes requires OS memory remapping and is expensive. Thread context switching is faster because code, data, and open files are shared. Threads only have their own stack and program counter.",
                "Process Management", true));

        list.add(new InterviewQuestion(null, InterviewQuestion.Category.COMPUTER_NETWORKS, InterviewQuestion.Difficulty.MEDIUM,
                "What happens when you type https://www.google.com in your browser and press Enter?",
                "1. DNS resolution to find IP address. 2. TCP 3-way handshake (SYN, SYN-ACK, ACK). 3. TLS/SSL handshake for encryption. 4. Browser sends HTTP GET request. 5. Server responds with HTML. 6. Browser renders DOM and fetches assets.",
                "DNS lookup checks browser cache, OS cache, router cache, and recursive resolvers. The TCP handshake establishes a reliable transport connection. TLS negotiates cipher suites and exchanges certificates. The browser parses HTML, CSS, and JS to paint the page.",
                "Network Protocols", true));

        // HR & Behavioral
        list.add(new InterviewQuestion(null, InterviewQuestion.Category.HR, InterviewQuestion.Difficulty.EASY,
                "Tell me about yourself and your career aspirations.",
                "Structure your answer using Present -> Past -> Future: who you are today (degree, key technical skills), past accomplishments (notable projects, internships), and why this role aligns with your career trajectory.",
                "Keep response concise (under 2 minutes). Highlight problem solving enthusiasm, teamwork, and passion for continuous learning. Connect your skillset directly to what the company builds.",
                "Self Introduction", true));

        list.add(new InterviewQuestion(null, InterviewQuestion.Category.HR, InterviewQuestion.Difficulty.MEDIUM,
                "Describe a situation where you faced a significant challenge in a project and how you handled it.",
                "Use the STAR method: Situation (project context), Task (what needed to be done), Action (specific steps you took, technical or leadership), Result (measurable positive outcome and learning).",
                "Focus on accountability, clear communication, debugging methodologies, and resilience rather than placing blame on teammates or circumstances.",
                "Behavioral / STAR", true));

        questionRepository.saveAll(list);
        log.info("Finished seeding {} interview questions.", list.size());
    }
}

package com.mentoria.agil.backend.config;

import com.mentoria.agil.backend.model.*;
import com.mentoria.agil.backend.repository.*;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
@Profile("dev")
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PerfilMentorRepository perfilMentorRepository;
    private final MentoriaRequestRepository requestRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker(new Locale("pt-BR"));

    public DatabaseSeeder(UserRepository userRepository, 
                          PerfilMentorRepository perfilMentorRepository, 
                          MentoriaRequestRepository requestRepository, 
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.perfilMentorRepository = perfilMentorRepository;
        this.requestRepository = requestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Evita duplicar dados se o banco não for resetado
        if (userRepository.count() > 0) return;

        System.out.println("🌱 Semeando dados com padrões mentor[i] e mentorado[i] @ufape.edu.br...");

        // 1. Criar Mentores: mentor0@ufape.edu.br, mentor1@ufape.edu.br...
        List<User> mentores = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String email = "mentor" + i + "@ufape.edu.br";
            User m = criarUsuario(faker.name().fullName(), email, Role.MENTOR); //
            
            // Cria o perfil detalhado do mentor
            perfilMentorRepository.save(new PerfilMentor(
                faker.programmingLanguage().name() + " Expert", 
                faker.lorem().sentence(10), 
                m
            )); //
            mentores.add(m);
        }

        // 2. Criar Mentorados: mentorado0@ufape.edu.br, mentorado1@ufape.edu.br...
        List<User> mentorados = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String email = "mentorado" + i + "@ufape.edu.br";
            mentorados.add(criarUsuario(faker.name().fullName(), email, Role.ESTUDANTE)); //
        }

        // 3. Criar Solicitações de Mentoria Pendentes entre eles
        for (int i = 0; i < 15; i++) {
            MentoriaRequest req = new MentoriaRequest(); //
            req.setMentor(mentores.get(faker.random().nextInt(mentores.size())));
            req.setMentorado(mentorados.get(faker.random().nextInt(mentorados.size())));
            req.setMessage(faker.lorem().paragraph());
            req.setStatus(MentoriaStatus.PENDING); //
            requestRepository.save(req); //
        }

        System.out.println("✅ Seeding concluído com sucesso!");
    }

    private User criarUsuario(String nome, String email, Role role) {
        User user = new User(); //
        user.setName(nome);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("senha123")); // Senha padrão para todos os testes
        user.setRole(role); //
        return userRepository.save(user); //
    }
}
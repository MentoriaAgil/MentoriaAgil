package com.mentoria.agil.backend.config;

import com.mentoria.agil.backend.enums.DisponibilidadeStatus;
import com.mentoria.agil.backend.enums.FormatoSessao;
import com.mentoria.agil.backend.enums.MentoriaStatus;
import com.mentoria.agil.backend.enums.Role;
import com.mentoria.agil.backend.enums.SessaoStatus;
import com.mentoria.agil.backend.enums.TipoMaterial;
import com.mentoria.agil.backend.enums.TipoMentoria;
import com.mentoria.agil.backend.model.*;
import com.mentoria.agil.backend.repository.*;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
@Profile("dev")
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PerfilMentorRepository perfilMentorRepository;
    private final MentoriaRequestRepository requestRepository;
    private final DisponibilidadeRepository disponibilidadeRepository;
    private final SessaoRepository sessaoRepository;
    private final FeedbackRepository feedbackRepository;
    private final MaterialRepository materialRepository;
    private final MaterialMentoradoRepository materialMentoradoRepository;
    private final SessaoMaterialRepository sessaoMaterialRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker(new Locale("pt-BR"));

    public DatabaseSeeder(UserRepository userRepository,
            PerfilMentorRepository perfilMentorRepository,
            MentoriaRequestRepository requestRepository,
            DisponibilidadeRepository disponibilidadeRepository,
            SessaoRepository sessaoRepository,
            FeedbackRepository feedbackRepository,
            MaterialRepository materialRepository,
            MaterialMentoradoRepository materialMentoradoRepository,
            SessaoMaterialRepository sessaoMaterialRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.perfilMentorRepository = perfilMentorRepository;
        this.requestRepository = requestRepository;
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.sessaoRepository = sessaoRepository;
        this.feedbackRepository = feedbackRepository;
        this.materialRepository = materialRepository;
        this.materialMentoradoRepository = materialMentoradoRepository;
        this.sessaoMaterialRepository = sessaoMaterialRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0)
            return;

        System.out.println("🌱 Iniciando Seeding detalhado do sistema...");

        // 1. CRIAR MENTORES E PERFIS
        List<User> mentores = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User m = criarUsuario(faker.name().fullName(), "mentor" + i + "@ufape.edu.br", Role.MENTOR);

            // Perfil detalhado (relação 1:1 com User)
            perfilMentorRepository.save(new PerfilMentor(
                    faker.job().title(),
                    "Especialista em " + faker.programmingLanguage().name() + ". " + faker.lorem().paragraph(),
                    faker.educator().course(),
                    faker.job().field(),
                    TipoMentoria.values()[faker.random().nextInt(TipoMentoria.values().length)],
                    DisponibilidadeStatus.DISPONIVEL,
                    m));

            // 2. DISPONIBILIDADES (Agenda do Mentor para os próximos 7 dias)
            for (int d = 1; d <= 7; d++) {
                LocalDateTime manha = LocalDateTime.now().plusDays(d).withHour(9).withMinute(0);
                LocalDateTime tarde = LocalDateTime.now().plusDays(d).withHour(14).withMinute(0);

                disponibilidadeRepository.save(new Disponibilidade(m, manha, manha.plusHours(1)));
                disponibilidadeRepository.save(new Disponibilidade(m, tarde, tarde.plusHours(1)));
            }
            mentores.add(m);
        }

        // 3. CRIAR MENTORADOS (Estudantes)
        List<User> mentorados = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            mentorados.add(criarUsuario(faker.name().fullName(), "mentorado" + i + "@ufape.edu.br", Role.ESTUDANTE));
        }

        // 4. MATERIAIS DIDÁTICOS (Criados pelos Mentores)
        List<Material> materiais = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            User mentorDono = mentores.get(i % mentores.size());
            Material mat = new Material();
            mat.setTitulo("Masterclass: " + faker.programmingLanguage().name());
            mat.setDescricao(faker.lorem().sentence());
            mat.setConteudo("https://github.com/mentoria-agil/aula-" + i); // Nome correto do campo: conteudo
            mat.setTipo(i % 2 == 0 ? TipoMaterial.LINK : TipoMaterial.DOCUMENTO); //
            mat.setMentor(mentorDono);
            materiais.add(materialRepository.save(mat));
        }

        // 5. FLUXO COMPLETO: SOLICITAÇÕES, SESSÕES E FEEDBACKS
        for (int i = 0; i < mentorados.size(); i++) {
            User aluno = mentorados.get(i);
            User mentor = mentores.get(i % mentores.size());

            // Solicitação de Mentoria (PENDENTE)
            MentoriaRequest req = new MentoriaRequest();
            req.setMentor(mentor);
            req.setMentorado(aluno);
            req.setMessage("Gostaria de agendar uma mentoria sobre " + faker.job().field());
            req.setStatus(MentoriaStatus.PENDING);
            requestRepository.save(req);

            // Sessão Agendada (FUTURA - ONLINE)
            Sessao futura = new Sessao();
            futura.setMentor(mentor);
            futura.setMentorado(aluno);
            futura.setDataHoraInicio(LocalDateTime.now().plusDays(3).withHour(10).withMinute(0));
            futura.setDataHoraFim(futura.getDataHoraInicio().plusHours(1));
            futura.setFormato(FormatoSessao.ONLINE);
            futura.setLinkReuniao("https://meet.google.com/" + faker.random().hex(10));
            futura.setStatus(SessaoStatus.AGENDADA);
            sessaoRepository.save(futura);

            // Sessão Concluída (PASSADA - PRESENCIAL) com Feedback
            Sessao concluida = new Sessao();
            concluida.setMentor(mentor);
            concluida.setMentorado(aluno);
            concluida.setDataHoraInicio(LocalDateTime.now().minusDays(5).withHour(15).withMinute(0));
            concluida.setDataHoraFim(concluida.getDataHoraInicio().plusHours(1));
            concluida.setFormato(FormatoSessao.PRESENCIAL);
            concluida.setEndereco("Bloco B, Sala " + faker.number().numberBetween(101, 305));
            concluida.setStatus(SessaoStatus.CONCLUIDA);
            Sessao sSalva = sessaoRepository.save(concluida);

            // Feedback da Sessão
            Feedback feedback = new Feedback();
            feedback.setSessao(sSalva);
            feedback.setNota(faker.number().numberBetween(4, 5));
            feedback.setComentario("A sessão foi incrível, aprendi muito!");
            feedbackRepository.save(feedback);

            // 6. RELAÇÕES DE MATERIAIS (Material compartilhado e vinculado à sessão)
            if (!materiais.isEmpty()) {
                // Atribuir material ao aluno
                MaterialMentorado mm = new MaterialMentorado();
                mm.setMentorado(aluno);
                mm.setMaterial(materiais.get(i % materiais.size()));
                materialMentoradoRepository.save(mm);

                // Vincular material à sessão específica
                SessaoMaterial sm = new SessaoMaterial();
                sm.setSessao(sSalva);
                sm.setMaterial(materiais.get(i % materiais.size()));
                sessaoMaterialRepository.save(sm);
            }
        }

        System.out.println("✅ Seeding finalizado com sucesso! Todas as tabelas e relações foram populadas.");
    }

    private User criarUsuario(String nome, String email, Role role) {
        User user = new User();
        user.setName(nome);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("senha123")); // Senha padrão para dev
        user.setRole(role);
        user.setAtivo(true);
        return userRepository.save(user);
    }
}
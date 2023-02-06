package yeonjeans.saera;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import yeonjeans.saera.Service.StatementService;
import yeonjeans.saera.Service.StatementServiceImpl;
import yeonjeans.saera.Service.MemberService;
import yeonjeans.saera.Service.MemberServiceImpl;
import yeonjeans.saera.domain.statement.StatementRepository;
import yeonjeans.saera.domain.member.MemberRepository;
import yeonjeans.saera.domain.statement.TagRepository;

@Configuration
public class AppConfig {

    private StatementRepository statementRepository;
    private MemberRepository userRepository;
    private TagRepository tagRepository;

    @Bean
    public StatementService StatementService() {
        return new StatementServiceImpl(statementRepository, tagRepository, webClient());
    }

    @Bean
    public MemberService UserService(){
        return new MemberServiceImpl(userRepository);
    }

    @Bean
    public WebClient webClient() {
        return WebClient
                .create("http://34.64.207.59/");
    }
}

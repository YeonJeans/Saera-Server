package yeonjeans.saera;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        return new StatementServiceImpl(statementRepository, tagRepository);
    }

    @Bean
    public MemberService UserService(){
        return new MemberServiceImpl(userRepository);
    }

}

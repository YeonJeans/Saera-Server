package yeonjeans.saera.domain.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import yeonjeans.saera.domain.entity.Statement;
import yeonjeans.saera.domain.repository.StatementRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class StatementRepositoryTest {

    @Autowired
    StatementRepository statementRepository;

    @Transactional
    @Test
    public void saveStatement(){
        //given
        String content = "content";
        String recordImg = "record";

        Statement result = statementRepository.save(Statement.builder().content(content).pitchX(recordImg).pitchY(recordImg).build());
        //when
        Optional<Statement> state = statementRepository.findById(result.getId());

        //then
        assertThat(state.isPresent());
    }

}

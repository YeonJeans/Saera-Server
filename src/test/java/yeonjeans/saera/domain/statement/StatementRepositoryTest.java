package yeonjeans.saera.domain.statement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class StatementRepositoryTest {

    @Autowired
    StatementRepository statementRepository;

    @Transactional
    @Test
    @DisplayName("Statement가 저장된다.")
    public void saveStatement(){
        //given
        String content = "content";
        String recordImg = "record";

        Statement result = statementRepository.save(Statement.builder().content(content).pitchX(recordImg).pitchY(recordImg).build());
        //when
        Optional<Statement> state = statementRepository.findById(result.getId());

        //then
        assertThat(state.get().getContent()).isInstanceOf(String.class);
    }

}

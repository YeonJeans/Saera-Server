package yeonjeans.saera.domain.statement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class StatementRepositoryTest {

    @Autowired
    StatementRepository statementRepository;

    @Test
    @DisplayName("Statement가 저장된다.")
    public void saveStatement(){
        //given
        String content = "content";
        String record = "rcord";
        String recordImg = "record";

        statementRepository.save(new Statement().builder().record(record).content(content).graphX(recordImg).graphY(recordImg).build());
        //when
        List<Statement> statementList = statementRepository.findAll();

        //then
        Statement statement = statementList.get(0);
        assertThat(statement.getContent()).isInstanceOf(String.class);
    }

}

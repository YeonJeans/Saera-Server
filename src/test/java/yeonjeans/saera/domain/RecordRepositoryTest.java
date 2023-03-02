package yeonjeans.saera.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.client.WebClient;
import yeonjeans.saera.domain.practiced.Record;
import yeonjeans.saera.domain.practiced.RecordRepository;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.exception.CustomException;

import javax.persistence.Column;
import java.io.IOException;
import java.util.stream.IntStream;

import static yeonjeans.saera.exception.ErrorCode.STATEMENT_NOT_FOUND;

@SpringBootTest
public class RecordRepositoryTest {
    @Autowired
    RecordRepository recordRepository;
    @Autowired
    WebClient webClient;
    @Autowired
    String MLserverBaseUrl;

    @Transactional
    @Test
    public void saveRecord() throws IOException {
        //given
        String content = "record save 잘 되었으면 좋겠다.";

        Resource resource =  webClient.get()
                .uri(MLserverBaseUrl + "/tts?text="+content)
                .retrieve()
                .bodyToMono(Resource.class)
                .block();

        byte[] audioBytes = StreamUtils.copyToByteArray(resource.getInputStream());

        //when
        Record record = recordRepository.save(new Record("path", audioBytes));

        //then
        System.out.println(record.getId());
        System.out.println(record.getWavFile());
        Assertions.assertNotNull(record);
    }

}

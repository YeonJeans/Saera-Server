package yeonjeans.saera.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.BodyInserters;
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
    @Value("${clova.client-id}")
    private String CLOVA_ID;
    @Value("${clova.client-secret}")
    private String CLOVA_SECRET;

    @Transactional
    @Test
    public void saveRecord() throws IOException {
        //given
        String content = "record save 잘 되었으면 좋겠다.";

        Resource resource =   webClient.post()
                .uri("https://naveropenapi.apigw.ntruss.com/tts-premium/v1/tts")
                .headers(headers -> {
                    headers.set("Content-Type", "application/x-www-form-urlencoded");
                    headers.set("X-NCP-APIGW-API-KEY-ID", CLOVA_ID);
                    headers.set("X-NCP-APIGW-API-KEY", CLOVA_SECRET);
                })
                .body(BodyInserters.fromFormData
                                ("speaker", "nara")
                        .with("text", content)
                        .with("format", "wav"))
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

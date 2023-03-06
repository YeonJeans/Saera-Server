package yeonjeans.saera;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import yeonjeans.saera.domain.entity.Bookmark;
import yeonjeans.saera.domain.entity.example.Statement;
import yeonjeans.saera.domain.entity.example.StatementTag;
import yeonjeans.saera.domain.entity.example.Tag;
import yeonjeans.saera.domain.repository.BookmarkRepository;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.member.MemberRepository;
import yeonjeans.saera.domain.entity.member.MemberRole;
import yeonjeans.saera.domain.entity.member.Platform;
import yeonjeans.saera.domain.repository.example.StatementRepository;
import yeonjeans.saera.domain.repository.example.StatementTagRepository;
import yeonjeans.saera.domain.repository.example.TagRepository;
import yeonjeans.saera.dto.ML.PitchGraphDto;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class CreateDummy {
    @Autowired
    private MemberRepository memberRepo;
    @Autowired
    private BookmarkRepository bookmarkRepo;
    @Autowired
    private StatementRepository statementRepo;
    @Autowired
    private TagRepository tagRepo;
    @Autowired
    private StatementTagRepository statementTagRepo;
    @Autowired
    private WebClient webClient;
    @Autowired
    private String MLserverBaseUrl;
    @Value("${ml.secret}")
    private String ML_SECRET;
    @Value("${clova.client-id}")
    private String CLOVA_ID;
    @Value("${clova.client-secret}")
    private String CLOVA_SECRET;

    @Transactional
    @Test
    public void createDummy(){
        String[] contentArray = {"화장실은 어디에 있나요?", "점심시간은 언제부터입니까?", "잠깐 전화 좀 받고 오겠습니다.", "신분증은 안 가져왔습니다.", "본인인데도 안 될까요?"};
        List<Statement> statementList = Arrays.stream(contentArray).map(this::makeStatement).collect(Collectors.toList());
        statementRepo.saveAll(statementList);

        String[] tagNameArray = {"질문", "업무", "은행"};
        List<Tag> tagList = Arrays.stream(tagNameArray).map(Tag::new).map(tag->tagRepo.save(tag)).collect(Collectors.toList());

        statementTagRepo.save(new StatementTag(statementList.get(0), tagList.get(0)));
        statementTagRepo.save(new StatementTag(statementList.get(1), tagList.get(0)));
        statementTagRepo.save(new StatementTag(statementList.get(1), tagList.get(1)));
        statementTagRepo.save(new StatementTag(statementList.get(2), tagList.get(1)));
        statementTagRepo.save(new StatementTag(statementList.get(3), tagList.get(2)));
        statementTagRepo.save(new StatementTag(statementList.get(4), tagList.get(0)));
        statementTagRepo.save(new StatementTag(statementList.get(4), tagList.get(2)));

        Member member1 = Member.builder().email("test1@gmail.com").nickname("testuser1").platform(Platform.GOOGLE).profile("testProfile").build();
        Member member2 = Member.builder().email("test2@gmail.com").nickname("testuser2").platform(Platform.GOOGLE).profile("testProfile").build();
        member1.addMemberRole(MemberRole.USER);
        memberRepo.save(member1);
        memberRepo.save(member2);

        bookmarkRepo.save(new Bookmark(member1,statementList.get(0)));
        bookmarkRepo.save(new Bookmark(member1,statementList.get(2)));
        bookmarkRepo.save(new Bookmark(member1,statementList.get(3)));
        bookmarkRepo.save(new Bookmark(member2,statementList.get(0)));
    }

    @Test
    public Statement makeStatement(String content) {
        Resource resource = webClient.post()
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

        byte[] audioBytes = new byte[0];
        try {
            audioBytes = StreamUtils.copyToByteArray(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayResource audioResource = new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return "audio.wav";
            }
        };

        PitchGraphDto graphDto = webClient.post()
                .uri(MLserverBaseUrl + "pitch-graph")
                .header("access-token",ML_SECRET)
                .body(BodyInserters.fromMultipartData("audio", audioResource))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    System.err.println("Client error: "+ response.statusCode());
                    return response.bodyToMono(String.class)
                            .flatMap(body -> {
                                System.err.println("Response body: " + body);
                                return Mono.error(new RuntimeException("Client error"));
                            });
                })
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    System.err.println("Server error: "+ response.statusCode());
                    return response.bodyToMono(String.class)
                            .flatMap(body -> {
                                System.err.println("Response body: {}"+ body);
                                return Mono.error(new RuntimeException("Server error"));
                            });
                })
                .bodyToMono(PitchGraphDto.class)
                .block();

        Statement result = Statement.builder()
                .content(content)
                .pitchX(graphDto.getPitch_x().toString())
                .pitchY(graphDto.getPitch_y().toString())
                .build();

        return result;
    }
}

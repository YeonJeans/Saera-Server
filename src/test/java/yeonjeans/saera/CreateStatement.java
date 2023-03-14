package yeonjeans.saera;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import yeonjeans.saera.domain.entity.example.Statement;
import yeonjeans.saera.domain.entity.example.StatementTag;
import yeonjeans.saera.domain.entity.example.Tag;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.entity.member.MemberRole;
import yeonjeans.saera.domain.entity.member.Platform;
import yeonjeans.saera.domain.repository.BookmarkRepository;
import yeonjeans.saera.domain.repository.member.MemberRepository;
import yeonjeans.saera.domain.repository.example.StatementRepository;
import yeonjeans.saera.domain.repository.example.StatementTagRepository;
import yeonjeans.saera.domain.repository.example.TagRepository;
import yeonjeans.saera.dto.ML.PitchGraphDto;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class CreateStatement {
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
        String[] contentArray = {
                "화장실은 어디에 있나요?", "신분증은 안 가져왔는데요.", "본인인데도 안 되나요?", "괜찮습니다.", "안녕하세요?",
                "이건 얼마예요?", "언제 한 번 놀러 오세요.", "공연 보러 가자.", "넌 좋아하는 게 뭐야?",  "넌 쉬는 날이 언제야?",

                "신용카드 발급하려고 하는데요.", "이제 괜찮아졌습니다.", "많이 아프세요?", "몰랐어요.", "다시 말씀해 주실래요?",
                "예약 취소하려고요.", "어떻게 할까요?", "여기로 오시겠어요?", "좋습니다.", "이게 끝이에요.",

                "죄송합니다.", "사용하는 언어가 달라서 모르겠습니다.", "물어본다는 게 깜빡했어요.", "밥이나 먹자.", "저 운전면허 있어요.",
                "주민등록증 새로 발급 받으려고요.", "아이스 아메리카노 하나 주세요.", "우유 대신 두유로 바꿀 수 있나요?", "현금 결제도 되나요?", "이건 뭐예요?",

                "제가 커피는 잘 못 마셔요.", "매운 음식은 잘 못 먹어요.", "커피 마시러 가실래요?", "실례합니다.", "가까운 병원으로 가주세요.",
                "천천히 말씀해 주세요.", "어디 출신이세요?", "삼겹살 4인분 주세요.", "소주 한 병 주세요.", "몇시에 문 닫아요?",

                "여기서 먹고 갈게요.", "테이크 아웃이요.", "포장해 주세요.", "와이파이 비밀번호가 뭔가요?", "콘센트 어디에 있어요?",
                "영수증은 버려 주세요.", "봉투에 넣어주세요.", "적립은 안 해요.", "이거 환불하려고요.", "이거 교환하려고요.",

                "일시불로 결제해 주세요.", "다 해서 얼마예요?", "비밀번호를 재발급 받고 싶어요.", "공인인증서 발급하고 싶어요.", "체크카드 만들려고 하는데요.",
                "오이 알레르기가 있어요.", "근처에 약국은 어디에 있나요?", "타이레놀 있나요?", "여기서 세워 주세요.", "맛있게 드세요.",

                "새해 복 많이 받으세요!", "즐거운 추석 보내세요.", "메리 크리스마스!", "휴대폰 충전해 주세요.", "술은 잘 못 마셔요.", // -> 새해복 많이 받으세요 중복 없앰..
                "수돗물이 안 나와요.", "따뜻한 물이 안 나와요.", "옆집이 너무 시끄러워요.", "변기가 막혔어요.", "보일러가 고장났어요.",

                "지하철역에서 얼마나 걸려요?", "이 버스는 어디로 가나요?", "여기 와이파이 되나요?", "먹고 싶은 음식이 있나요?", "편안한 밤 되시길 바래요.",
                "만나서 반갑습니다.", "아무것도 아닙니다.", "감사합니다.", "나중에 봬요.", "조금만 깎아주세요.",

                "메세지 남겨 드릴까요?", "여기에서 1km 정도 떨어져있어요.", "휴대폰을 잠시 빌릴 수 있을까요?", "MBTI가 뭐예요?", "진통제 주세요.",
                "해열제 주세요.", "지금 몇 시에요?", "추천하는 메뉴가 있나요?", "버스를 놓쳐서 늦었어요.", "거기서 거기에요.",

                "지금 집에 가야해서요.", "노래 추천해주세요.", "뜨거우니까 조심하세요.", "영수증 드릴까요?", "내일까지 해 볼게요.",
                "내일로 미룰 수 있을까요?", "이따가 미팅 있어요.", "조금만 더 주세요.", "아무거나 다 좋아.", "저는 상관 없어요.",

                "그거 좋은 것 같아.", "그건 좀 별로야.", "마음에 안 들어."
        };

        List<Statement> statementList = Arrays.stream(contentArray).map(this::makeStatement).collect(Collectors.toList());
        statementRepo.saveAll(statementList);

        String[] tagNameArray1 = {"일상", "은행/공공기관", "소비", "회사", "인사"}; //0 1 2 3 4
        String[] tagNameArray2 = {"의문문", "존댓말", "부정문", "감정표현"}; //0 1 2 3
        String[] tagNameArray3 = {"구개음화", "두음법칙", "치조마찰음화", "ㄴ첨가", "ㄹ첨가", "여→애", "단모음화", "으→우", "어→오", "오→어", "모음조화"};
        String[] tagNameArray4 = {"축약", "부정어강조", "이 역행동화", "하→해", "자음탈락", "말토막억양", "어→오", "게→기", "강조"};

        List<Tag> tagList1 = Arrays.stream(tagNameArray1).map(Tag::new).map(tag->tagRepo.save(tag)).collect(Collectors.toList());
        List<Tag> tagList2 = Arrays.stream(tagNameArray2).map(Tag::new).map(tag->tagRepo.save(tag)).collect(Collectors.toList());
        List<Tag> tagList3 = Arrays.stream(tagNameArray3).map(Tag::new).map(tag->tagRepo.save(tag)).collect(Collectors.toList());
        List<Tag> tagList4 = Arrays.stream(tagNameArray4).map(Tag::new).map(tag->tagRepo.save(tag)).collect(Collectors.toList());

        //화장실은 어디에 있나요?
        statementTagRepo.save(new StatementTag(statementList.get(0), tagList1.get(0)));//일상
        statementTagRepo.save(new StatementTag(statementList.get(0), tagList2.get(0)));//의문문
        statementTagRepo.save(new StatementTag(statementList.get(0), tagList2.get(1)));//존댓말
        //신분증은 안 가져왔는데요.
        statementTagRepo.save(new StatementTag(statementList.get(1), tagList1.get(1)));//은행/공공기관
        statementTagRepo.save(new StatementTag(statementList.get(1), tagList2.get(2)));//부정문
        statementTagRepo.save(new StatementTag(statementList.get(1), tagList2.get(1)));//존댓말
        statementTagRepo.save(new StatementTag(statementList.get(1), tagList3.get(0)));//축약
        statementTagRepo.save(new StatementTag(statementList.get(1), tagList3.get(1)));//부정어강조
        //본인인데도 안 되나요?
        statementTagRepo.save(new StatementTag(statementList.get(2), tagList1.get(1)));//은행/공공기관
        statementTagRepo.save(new StatementTag(statementList.get(2), tagList2.get(0)));//의문문
        statementTagRepo.save(new StatementTag(statementList.get(2), tagList2.get(1)));//존댓말
        statementTagRepo.save(new StatementTag(statementList.get(2), tagList3.get(1)));//부정어강조
        //괜찮습니다.
        statementTagRepo.save(new StatementTag(statementList.get(3), tagList1.get(0)));//일상
        statementTagRepo.save(new StatementTag(statementList.get(3), tagList2.get(1)));//존댓말
        statementTagRepo.save(new StatementTag(statementList.get(3), tagList3.get(8))); //강조
        //안녕하세요?
        statementTagRepo.save(new StatementTag(statementList.get(4), tagList1.get(4)));//인사
        statementTagRepo.save(new StatementTag(statementList.get(4), tagList2.get(1)));//존댓말
        //이건 얼마예요?
        statementTagRepo.save(new StatementTag(statementList.get(5), tagList1.get(2))); //소비
        statementTagRepo.save(new StatementTag(statementList.get(5), tagList2.get(1))); //존댓말
        statementTagRepo.save(new StatementTag(statementList.get(5), tagList2.get(0))); //의문문
        statementTagRepo.save(new StatementTag(statementList.get(5), tagList3.get(2))); //이 역행동화
        //언제 한 번 놀러오세요.
        statementTagRepo.save(new StatementTag(statementList.get(6), tagList1.get(0))); //일상
        statementTagRepo.save(new StatementTag(statementList.get(6), tagList2.get(1))); //존댓말
        //공연 보러 가자
        statementTagRepo.save(new StatementTag(statementList.get(7), tagList1.get(0))); //일상
        //넌 좋아하는 게 뭐야?
        statementTagRepo.save(new StatementTag(statementList.get(8), tagList1.get(1))); //일상
        statementTagRepo.save(new StatementTag(statementList.get(8), tagList4.get(3))); //하->해
        statementTagRepo.save(new StatementTag(statementList.get(8), tagList2.get(0))); //의문문
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
                                ("speaker", "vhyeri")
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
                .file(audioBytes)
                .pitchX(graphDto.getPitch_x().toString())
                .pitchY(graphDto.getPitch_y().toString())
                .build();

        return result;
    }
}

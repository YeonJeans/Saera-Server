package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import yeonjeans.saera.domain.entity.custom.Custom;
import yeonjeans.saera.domain.entity.example.ReferenceType;
import yeonjeans.saera.domain.entity.example.Word;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.custom.CustomRepository;
import yeonjeans.saera.domain.repository.example.WordRepository;
import yeonjeans.saera.domain.repository.member.MemberRepository;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.domain.repository.PracticeRepository;
import yeonjeans.saera.domain.entity.example.Statement;
import yeonjeans.saera.domain.repository.example.StatementRepository;
import yeonjeans.saera.dto.PracticedRequestDto;
import yeonjeans.saera.dto.PracticedResponseDto;
import yeonjeans.saera.dto.ML.PitchGraphDto;
import yeonjeans.saera.dto.ML.ScoreRequestDto;
import yeonjeans.saera.exception.CustomException;
import yeonjeans.saera.exception.ErrorCode;
import yeonjeans.saera.util.Parsing;

import static yeonjeans.saera.exception.ErrorCode.*;
import static yeonjeans.saera.util.XPConstant.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PracticeServiceImpl implements PracticeService {

    private final MemberRepository memberRepository;
    private final PracticeRepository practiceRepository;
    private final StatementRepository statementRepository;
    private final WordRepository wordRepository;
    private final CustomRepository customRepository;

    private final WebClient webClient;
    private final String MLserverBaseUrl;
    @Value("${ml.secret}")
    private String ML_SECRET;

    @Transactional
    public Practice create(PracticedRequestDto dto, Long memberId){
        LocalDate currentDate = LocalDate.now();
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        Practice practice = practiceRepository.findByMemberAndTypeAndFk(member, dto.getType(), dto.getFk()).orElse(null);

        if(practice == null){
            member.addXp(dto.getType() != ReferenceType.WORD ? XP_STATEMENT : XP_WORD);
            practice = Practice.builder().member(member).fk(dto.getFk()).type(dto.getType()).build();
        }else {
            LocalDateTime modifiedDate = practice.getModifiedDate();
            if(modifiedDate.toLocalDate().isEqual(currentDate)){
                if(!dto.isTodayStudy()) practice.setCount(practice.getCount()+1);
            }else{
                practice.setCount(0);
            }
        }

        switch (dto.getType()){
            case STATEMENT :
                createPracticeStatement(dto, practice);
                break;
            case WORD:
                createPracticeWord(dto, practice);
                break;
            case CUSTOM:
                createPracticeCustom(dto, practice);
                break;
        }
        return practiceRepository.save(practice);
    }

    private void createPracticeStatement(PracticedRequestDto dto, Practice practice) {
        Statement state = statementRepository.findById(dto.getFk())
                .orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));

        PitchGraphDto userGraph = getPitchGraph(dto.getRecord().getResource());
        PitchGraphDto targetGraph = new PitchGraphDto(Parsing.stringToIntegerArray(state.getPitchX()), Parsing.stringToDoubleArray(state.getPitchY()));

        Double score = getScore(userGraph, targetGraph);

        byte[] audioBytes = new byte[0];
        try {
            InputStream inputStream = dto.getRecord().getInputStream();
            audioBytes = StreamUtils.copyToByteArray(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        practice.setFile(audioBytes);
        practice.setPitchX(userGraph.getPitch_x().toString());
        practice.setPitchY(userGraph.getPitch_y().toString());
        practice.setScore(score);
    };

    private void createPracticeWord(PracticedRequestDto dto, Practice practice) {
        Word word = wordRepository.findById(dto.getFk())
                .orElseThrow(()->new CustomException(WORD_NOT_FOUND));
    };

    private void  createPracticeCustom(PracticedRequestDto dto, Practice practice) {
        Custom custom = customRepository.findById(dto.getFk())
                .orElseThrow(()->new CustomException(CUSTOM_NOT_FOUND));


        PitchGraphDto userGraph = getPitchGraph(dto.getRecord().getResource());
        PitchGraphDto targetGraph = new PitchGraphDto(Parsing.stringToIntegerArray(custom.getPitchX()), Parsing.stringToDoubleArray(custom.getPitchY()));

        Double score = getScore(userGraph, targetGraph);

        byte[] audioBytes = new byte[0];
        try {
            InputStream inputStream = dto.getRecord().getInputStream();
            audioBytes = StreamUtils.copyToByteArray(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        practice.setFile(audioBytes);
        practice.setPitchX(userGraph.getPitch_x().toString());
        practice.setPitchY(userGraph.getPitch_y().toString());
        practice.setScore(score);
    };

    private PitchGraphDto getPitchGraph(Resource resource){
        PitchGraphDto graphDto = webClient.post()
                .uri(MLserverBaseUrl + "pitch-graph")
                .header("access-token", ML_SECRET)
                .body(BodyInserters.fromMultipartData("audio", resource))
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    if(response.statusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
                        throw new CustomException(ErrorCode.UNPROCESSABLE_ENTITY);
                    throw new CustomException(ErrorCode.COMMUNICATION_FAILURE);
                })
                .bodyToMono(PitchGraphDto.class)
                .block();
        return graphDto;
    }

    private Double getScore(PitchGraphDto userGraph, PitchGraphDto targetGraph ){
        ScoreRequestDto requestDto = ScoreRequestDto.builder()
                .target_pitch_x(targetGraph.getPitch_x())
                .target_pitch_y(targetGraph.getPitch_y())
                .user_pitch_x(userGraph.getPitch_x())
                .user_pitch_y(userGraph.getPitch_y())
                .build();

        String response = webClient.post()
                .uri(MLserverBaseUrl + "score")
                .header("access-token", ML_SECRET)
                .body(BodyInserters.fromValue(requestDto))
                .retrieve()
                .onStatus(HttpStatus::isError, res -> {
                    if(res.statusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
                        throw new CustomException(ErrorCode.UNPROCESSABLE_ENTITY);
                    throw new CustomException(ErrorCode.COMMUNICATION_FAILURE);
                })
                .bodyToMono(String.class)
                .block();

        Double score = new JSONObject(response).getDouble("DTW_score");
        return score;
    }

    public PracticedResponseDto read(ReferenceType type, Long fk, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        Practice practice = practiceRepository.findByMemberAndTypeAndFk(member, type, fk).orElseThrow(()->new CustomException(PRACTICED_NOT_FOUND));

        return new PracticedResponseDto(practice);
    }

    public Resource getRecord(ReferenceType type, Long fk, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        Practice practice = practiceRepository.findByMemberAndTypeAndFk(member, type, fk).orElseThrow(()->new CustomException(PRACTICED_NOT_FOUND));

        return new ByteArrayResource(practice.getFile()) {
            @Override
            public String getFilename() {
                return "audio.wav";
            }
        };
    }
}
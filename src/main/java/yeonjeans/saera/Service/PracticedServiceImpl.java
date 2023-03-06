package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import yeonjeans.saera.domain.entity.example.ReferenceType;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.member.MemberRepository;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.domain.repository.PracticeRepository;
import yeonjeans.saera.domain.entity.example.Statement;
import yeonjeans.saera.domain.repository.example.StatementRepository;
import yeonjeans.saera.dto.PracticedRequestDto;
import yeonjeans.saera.dto.PracticedResponseDto;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.dto.ML.PitchGraphDto;
import yeonjeans.saera.dto.ML.ScoreRequestDto;
import yeonjeans.saera.exception.CustomException;
import yeonjeans.saera.util.Parsing;

import static yeonjeans.saera.exception.ErrorCode.*;
import static yeonjeans.saera.util.File.saveFile;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PracticedServiceImpl {

    private final MemberRepository memberRepository;
    private final PracticeRepository practiceRepository;
    private final StatementRepository statementRepository;
    private final WebClient webClient;
    private final String MLserverBaseUrl;

    @Transactional
    public Practice create(PracticedRequestDto dto, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        Statement statement = statementRepository.findById(dto.getId()).orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));

        Optional<Practice> oldPracticed = practiceRepository.findByMemberAndTypeAndFk(member, ReferenceType.STATEMENT, dto.getId());
        if(oldPracticed.isPresent()){
//            String oldPath = oldPracticed.get().getRecord().getPath();
//            practiceRepository.delete(oldPracticed.get());
//            recordRepository.delete(oldPracticed.get().getRecord());
//            new File(oldPath).deleteOnExit();
        }
        String savePath = saveFile(dto.getRecord());
        Resource resource = new FileSystemResource(savePath);

        try{
            //get graph
            PitchGraphDto graphDto = webClient.post()
                    .uri(MLserverBaseUrl + "pitch-graph")
                    .body(BodyInserters.fromMultipartData("audio", resource))
                    .retrieve()
                    .bodyToMono(PitchGraphDto.class)
                    .block();
            //get score
            ScoreRequestDto requestDto = ScoreRequestDto.builder()
                    .target_pitch_x(Parsing.stringToIntegerArray(statement.getPitchX()))
                    .target_pitch_y(Parsing.stringToDoubleArray(statement.getPitchY()))
                    .user_pitch_x(graphDto.getPitch_x())
                    .user_pitch_y(graphDto.getPitch_y())
                    .build();

            String response = webClient.post()
                    .uri(MLserverBaseUrl + "score")
                    .body(BodyInserters.fromValue(requestDto))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            Double score = new JSONObject(response).getDouble("DTW_score");

            //record
//            Record record = recordRepository.save(new Record(savePath));

            Practice practice = Practice.builder()
                    .file(null)
                    .pitchX(graphDto.getPitch_x().toString())
                    .pitchY(graphDto.getPitch_y().toString())
                    .score(score)
                    .type(ReferenceType.STATEMENT)
                    .fk(dto.getId())
                    .build();

            return practiceRepository.save(practice);

        }catch (Exception e){
            new File(savePath).deleteOnExit();
            throw e;
        }
    }

    public PracticedResponseDto read(Long statementId, Long memberId){
        Statement statement = statementRepository.findById(statementId).orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        Practice practice = practiceRepository.findByMemberAndTypeAndFk(member, ReferenceType.STATEMENT, statementId).orElseThrow(()->new CustomException(PRACTICED_NOT_FOUND));
        return new PracticedResponseDto(practice);
    }

    public List<StateListItemDto> getList(Long userId){
        Member member = memberRepository.findById(userId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        return practiceRepository.findAllByMemberAndTypeOrderByCreatedDateDesc(member, ReferenceType.STATEMENT)
                .stream()
                .map(practiced -> new StateListItemDto(practiced, member.getId()))
                .collect(Collectors.toList());
    }

    public Resource getRecord(Long statementId, Long memberId){
        Statement statement = statementRepository.findById(statementId).orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        Practice practice = practiceRepository.findByMemberAndTypeAndFk(member, ReferenceType.STATEMENT, statementId).orElseThrow(()->new CustomException(PRACTICED_NOT_FOUND));

//        String path = practice.getRecord().getPath();
//        return new FileSystemResource(path);
        return null;
    }
}

package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import yeonjeans.saera.domain.entity.Record;
import yeonjeans.saera.domain.repository.RecordRepository;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.MemberRepository;
import yeonjeans.saera.domain.entity.Practiced;
import yeonjeans.saera.domain.repository.PracticedRepository;
import yeonjeans.saera.domain.entity.Statement;
import yeonjeans.saera.domain.repository.StatementRepository;
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
    private final PracticedRepository practicedRepository;
    private final StatementRepository statementRepository;
    private final RecordRepository recordRepository;
    private final WebClient webClient;
    private final String MLserverBaseUrl;

    @Transactional
    public Practiced create(PracticedRequestDto dto, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        Statement statement = statementRepository.findById(dto.getId()).orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));

        Optional<Practiced> oldPracticed = practicedRepository.findByStatementAndMember(statement, member);
        if(oldPracticed.isPresent()){
            String oldPath = oldPracticed.get().getRecord().getPath();
            practicedRepository.delete(oldPracticed.get());
            recordRepository.delete(oldPracticed.get().getRecord());
            new File(oldPath).deleteOnExit();
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
            Record record = recordRepository.save(new Record(savePath));

            Practiced practiced = new Practiced(member, statement, record, graphDto.getPitch_x().toString(), graphDto.getPitch_y().toString(), score);

            return practicedRepository.save(practiced);

        }catch (Exception e){
            new File(savePath).deleteOnExit();
            throw e;
        }
    }

    public PracticedResponseDto read(Long statementId, Long memberId){
        Statement statement = statementRepository.findById(statementId).orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        Practiced practiced = practicedRepository.findByStatementAndMember(statement, member).orElseThrow(()->new CustomException(PRACTICED_NOT_FOUND));
        return new PracticedResponseDto(practiced);
    }

    public List<StateListItemDto> getList(Long userId){
        Member member = memberRepository.findById(userId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        return practicedRepository.findAllByMemberOrderByCreatedDateDesc(member)
                .stream()
                .map(practiced -> new StateListItemDto(practiced, member.getId()))
                .collect(Collectors.toList());
    }

    public Resource getRecord(Long statementId, Long memberId){
        Statement statement = statementRepository.findById(statementId).orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        Practiced practiced = practicedRepository.findByStatementAndMember(statement, member).orElseThrow(()->new CustomException(PRACTICED_NOT_FOUND));

        String path = practiced.getRecord().getPath();
        return new FileSystemResource(path);
    }
}

package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import yeonjeans.saera.domain.Record;
import yeonjeans.saera.domain.RecordRepository;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.member.MemberRepository;
import yeonjeans.saera.domain.practiced.Practiced;
import yeonjeans.saera.domain.practiced.PracticedRepository;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.domain.statement.StatementRepository;
import yeonjeans.saera.dto.PracticedRequestDto;
import yeonjeans.saera.dto.PracticedResponseDto;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.dto.webClient.PitchGraphDto;
import yeonjeans.saera.dto.webClient.ScoreRequestDto;
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

    @Transactional
    public Practiced create(PracticedRequestDto dto){
        Member member = memberRepository.findById(1L).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        Statement statement = statementRepository.findById(dto.getId()).orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));

        Optional<Practiced> oldPracticed = practicedRepository.findById(dto.getId());
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
                    .uri("pitch-graph")
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
                    .uri("score")
                    .body(BodyInserters.fromValue(requestDto))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            Double score = (Double) new JSONObject(response).getJSONObject("score").get("0");

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
        Member member = memberRepository.findById(1L).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        Practiced practiced = practicedRepository.findByStatementAndMember(statement, member).orElseThrow(()->new CustomException(PRACTICED_NOT_FOUND));
        return new PracticedResponseDto(practiced);
    }

    public List<StateListItemDto> getList(Long userId){
        Member member = memberRepository.findById(userId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));
        return practicedRepository.findAllByMember(member)
                .stream()
                .map(StateListItemDto::new)
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

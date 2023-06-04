package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.domain.entity.custom.Custom;
import yeonjeans.saera.domain.entity.example.ReferenceType;
import yeonjeans.saera.domain.entity.example.Statement;
import yeonjeans.saera.domain.entity.example.Word;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.PracticeRepository;
import yeonjeans.saera.domain.repository.custom.CustomRepository;
import yeonjeans.saera.domain.repository.example.StatementRepository;
import yeonjeans.saera.domain.repository.example.WordRepository;
import yeonjeans.saera.domain.repository.member.MemberRepository;
import yeonjeans.saera.dto.ML.PitchGraphDto;
import yeonjeans.saera.dto.PracticeRequestDto;
import yeonjeans.saera.dto.PracticeResponseDto;
import yeonjeans.saera.exception.CustomException;
import yeonjeans.saera.util.Parsing;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;

import static yeonjeans.saera.exception.ErrorCode.*;
import static yeonjeans.saera.util.XPConstant.XP_STATEMENT;
import static yeonjeans.saera.util.XPConstant.XP_WORD;

@Slf4j
@RequiredArgsConstructor
@Service
public class PracticeServiceImpl implements PracticeService {
    private final MemberRepository memberRepository;
    private final PracticeRepository practiceRepository;
    private final StatementRepository statementRepository;
    private final WordRepository wordRepository;
    private final CustomRepository customRepository;

    private final WebClientService webClient;

    @Transactional
    public Practice create(PracticeRequestDto dto, Long memberId){
        LocalDate todayDate = LocalDate.now();
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        Practice practice = practiceRepository.findByMemberAndTypeAndFk(member, dto.getType(), dto.getFk()).orElse(null);
        LocalDate lastPracticeDate = getLastPracticeDate(member);

        //set Count
        if(practice == null){
            practice = Practice.builder().member(member).fk(dto.getFk()).type(dto.getType()).build();
        }else {
            LocalDate modifiedDate = practice.getModifiedDate().toLocalDate();
            if(modifiedDate.isEqual(todayDate)){
                if(!dto.isTodayStudy()) practice.setCount(practice.getCount() + 1);
            }else{
                practice.setCount(1);
            }
        }

        switch (dto.getType()){
            case STATEMENT :
                Statement state = statementRepository.findById(dto.getFk())
                        .orElseThrow(()->new CustomException(STATEMENT_NOT_FOUND));
                PitchGraphDto statementGraph = new PitchGraphDto(Parsing.stringToIntegerArray(state.getPitchX()), Parsing.stringToDoubleArray(state.getPitchY()));
                createPractice(dto, statementGraph, practice);
                break;
            case CUSTOM:
                Custom custom = customRepository.findById(dto.getFk())
                        .orElseThrow(()->new CustomException(CUSTOM_NOT_FOUND));
                PitchGraphDto customGraph = new PitchGraphDto(Parsing.stringToIntegerArray(custom.getPitchX()), Parsing.stringToDoubleArray(custom.getPitchY()));
                createPractice(dto, customGraph, practice);
                break;
            default:
                throw new CustomException(INVALID_TYPE);
        }

        //연속 학습 일수
        updatePracticeDays(member, lastPracticeDate, todayDate);
        member.addXp(XP_STATEMENT);
        memberRepository.save(member);
        return practiceRepository.save(practice);
    }

    @Transactional
    public Boolean createWord(PracticeRequestDto dto, Long memberId) {
        if(!dto.getType().equals(ReferenceType.WORD)){
            throw new CustomException(INVALID_TYPE);
        }

        LocalDate todayDate = LocalDate.now();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        Practice practice = practiceRepository.findByMemberAndTypeAndFk(member, ReferenceType.WORD, dto.getFk()).orElse(null);
        LocalDate lastPracticeDate = getLastPracticeDate(member);

        if(practice == null){
            practice = Practice.builder().member(member).fk(dto.getFk()).type(dto.getType()).build();
        }else {
            LocalDate modifiedDate = practice.getModifiedDate().toLocalDate();
            if(modifiedDate.isEqual(todayDate)){
                if(!dto.isTodayStudy()) practice.setCount(practice.getCount() + 1);
            }else{
                practice.setCount(1);
            }
        }

        Word word = wordRepository.findById(dto.getFk())
                .orElseThrow(()->new CustomException(WORD_NOT_FOUND));

        practice.setPitchLength(LocalTime.now().getNano());

        practiceRepository.save(practice);
        //연속 학습 일수
        updatePracticeDays(member, lastPracticeDate, todayDate);

        member.addXp(XP_WORD);
        memberRepository.save(member);
        return webClient.getWordScore(word.getNotation(), dto.getRecord());
    }

    private void createPractice(PracticeRequestDto dto, PitchGraphDto targetGraph, Practice practice){
        PitchGraphDto userGraph = webClient.getPitchGraph(dto.getRecord().getResource());
        Double score = webClient.getScore(userGraph, targetGraph);

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
        practice.setPitchLength(userGraph.getPitch_length());
        practice.setScore(score);
    }

    public PracticeResponseDto read(ReferenceType type, Long fk, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        Practice practice = practiceRepository.findByMemberAndTypeAndFk(member, type, fk)
                .orElseThrow(()->new CustomException(PRACTICED_NOT_FOUND));

        return new PracticeResponseDto(practice);
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

    public LocalDate getLastPracticeDate(Member member) {
        Practice practice = practiceRepository.findFirstByMemberOrderByModifiedDateDesc(member)
                        .orElse(null);

        return practice != null ? practice.getModifiedDate().toLocalDate() : null;
    }

    private void updatePracticeDays(Member member, LocalDate lastPracticeDate, LocalDate todayDate){
        if(  lastPracticeDate == null || lastPracticeDate.isEqual(todayDate.minusDays(1)) ) {
            member.setAttendance_count(member.getAttendance_count() + 1);
        }else if( !lastPracticeDate.isEqual(todayDate) ){
            member.setAttendance_count(1);
        }
    }
}
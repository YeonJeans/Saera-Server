package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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
import yeonjeans.saera.exception.CustomException;

import static yeonjeans.saera.exception.ErrorCode.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PracticedServiceImpl {

    private final MemberRepository memberRepository;
    private final PracticedRepository practicedRepository;
    private final StatementRepository statementRepository;
    private final RecordRepository recordRepository;
    private final WebClient webClient;

    private final String uploadPath = Paths.get(System.getProperty("user.home")).resolve("upload").toString();

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

        //record
        String saveName = saveFile(dto.getRecord());
        Record record = recordRepository.save(Record.builder().path(saveName).build());

        Practiced practiced = new Practiced(member, statement, record, statement.getPitchX(), statement.getPitchY(), 80.0);

        return practicedRepository.save(practiced);
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

    public String makeFolder(){
        String str = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("\\", File.separator);

        File uploadPathFolder = new File(uploadPath, folderPath);

        if(!uploadPathFolder.exists()){
            uploadPathFolder.mkdirs();
        }
        return folderPath;
    }

    public String saveFile(MultipartFile file){
        String originalName = file.getOriginalFilename();
        String fileName = originalName.substring(originalName.lastIndexOf("\\")+1);

        String folderPath = makeFolder();
        String uuid = UUID.randomUUID().toString();

        String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + "_" +fileName;
        Path savePath = Paths.get(saveName);

        try{
            file.transferTo(savePath);
        }catch (IOException e){
            throw new CustomException(UPLOAD_FAILURE);
        }
        return saveName;
    }

}

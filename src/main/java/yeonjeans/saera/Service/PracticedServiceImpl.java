package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PracticedServiceImpl {

    private final MemberRepository memberRepository;
    private final PracticedRepository practicedRepository;
    private final StatementRepository statementRepository;
    private final RecordRepository recordRepository;

    private final String uploadPath = Paths.get(System.getProperty("user.home")).resolve("upload").toString();

    @Transactional
    public Practiced create(PracticedRequestDto dto){
        //Memeber
        Optional<Member> member = memberRepository.findById(1L);
        if(member.isEmpty())return null;
        //statement
        Optional<Statement> statement = statementRepository.findById(dto.getId());
        if(statement.isEmpty()){
            System.out.println("찾을 수 없는 문장 id");
            return null;
        }

        Optional<Practiced> oldPrac = practicedRepository.findById(dto.getId());
        if(oldPrac.isPresent()){
            String oldPath = oldPrac.get().getRecord().getPath();
            practicedRepository.delete(oldPrac.get());
            recordRepository.delete(oldPrac.get().getRecord());
            new File(oldPath).deleteOnExit();
        }

        //file 저장하고
        MultipartFile file = dto.getRecord();
        String originalName = file.getOriginalFilename();
        String fileName = originalName.substring(originalName.lastIndexOf("\\")+1);

        String folderPath = makeFolder();
        String uuid = UUID.randomUUID().toString();

        String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + "_" +fileName;
        Path savePath = Paths.get(saveName);

        try{
            file.transferTo(savePath);
        }catch (IOException e){
            System.out.println("ioexception");
            return null;
        }
        Record record = recordRepository.save(Record.builder().path(saveName).build());

        Practiced practiced = new Practiced(member.get(), statement.get(), record, "pitch_x", "pitch_y", 80);

        return practicedRepository.save(practiced);
    }

    public PracticedResponseDto read(Practiced practiced){
        return null;
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

}

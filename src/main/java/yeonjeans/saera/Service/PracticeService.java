package yeonjeans.saera.Service;

import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.domain.entity.example.ReferenceType;
import yeonjeans.saera.dto.PracticedRequestDto;
import yeonjeans.saera.dto.PracticedResponseDto;

public interface PracticeService {

    @Transactional
    public Practice create(PracticedRequestDto dto, Long memberId);

    public PracticedResponseDto read(ReferenceType type, Long fk, Long memberId);

    public Resource getRecord(ReferenceType type, Long fk, Long memberId);
}

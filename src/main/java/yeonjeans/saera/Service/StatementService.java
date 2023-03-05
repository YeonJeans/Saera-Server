package yeonjeans.saera.Service;

import org.springframework.core.io.Resource;
import yeonjeans.saera.domain.entity.example.Statement;
import yeonjeans.saera.dto.StateListItemDto;

import java.util.ArrayList;
import java.util.List;

public interface StatementService {

    Statement searchById(Long id);
    List<StateListItemDto> search(String content, ArrayList<String> tags, Long memberId);

    List<StateListItemDto> searchHistory(Long memberId);
    Resource getTTS(Long id);
}

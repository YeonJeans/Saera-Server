package yeonjeans.saera.domain.repository.example;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.entity.example.Word;

public interface WordRepository extends JpaRepository<Word, Long> {
}

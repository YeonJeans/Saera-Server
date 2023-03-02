package yeonjeans.saera.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.entity.Record;

public interface RecordRepository extends JpaRepository<Record, Long> {
}

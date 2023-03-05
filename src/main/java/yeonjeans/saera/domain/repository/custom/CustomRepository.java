package yeonjeans.saera.domain.repository.custom;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.entity.custom.Custom;

public interface CustomRepository extends JpaRepository<Custom, Long> {
}

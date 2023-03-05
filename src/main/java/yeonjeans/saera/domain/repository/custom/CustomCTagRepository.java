package yeonjeans.saera.domain.repository.custom;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.entity.custom.CustomCtag;

public interface CustomCTagRepository extends JpaRepository<CustomCtag, Long> {
}

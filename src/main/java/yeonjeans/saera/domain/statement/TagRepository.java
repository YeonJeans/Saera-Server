package yeonjeans.saera.domain.statement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    public Tag findByName(String tagname);
}

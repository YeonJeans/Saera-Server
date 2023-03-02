package yeonjeans.saera.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    public Tag findByName(String tagname);

}

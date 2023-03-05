package yeonjeans.saera.domain.repository.example;

import org.springframework.data.jpa.repository.JpaRepository;
import yeonjeans.saera.domain.entity.example.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    public Tag findByName(String tagname);

}

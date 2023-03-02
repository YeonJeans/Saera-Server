package yeonjeans.saera.domain.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import yeonjeans.saera.domain.entity.Tag;
import yeonjeans.saera.domain.repository.TagRepository;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

    @DisplayName("Tag 검색")
    @Transactional
    @Test
    public void findByName(){
        //given
        String name1 = "test1";
        String name2 = "test2";

        tagRepository.save(new Tag(name1));
        tagRepository.save(new Tag(name2));

        //when
        Tag tag= tagRepository.findByName("test1");
        //then
        assertThat(tag.getName()).isEqualTo(name1);
    }
}
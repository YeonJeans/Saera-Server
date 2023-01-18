package yeonjeans.saera.domain.statement;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeonjeans.saera.domain.user.User;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Statement statement;

    @Builder
    public Bookmark(User user, Statement statement) {
        this.user = user;
        this.statement = statement;
    }
}

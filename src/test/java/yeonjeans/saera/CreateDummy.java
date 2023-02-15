package yeonjeans.saera;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import yeonjeans.saera.domain.bookmark.Bookmark;
import yeonjeans.saera.domain.bookmark.BookmarkRepository;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.member.MemberRepository;
import yeonjeans.saera.domain.member.MemberRole;
import yeonjeans.saera.domain.member.Platform;
import yeonjeans.saera.domain.statement.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class CreateDummy {
    @Autowired
    private MemberRepository memberRepo;
    @Autowired
    private BookmarkRepository bookmarkRepo;
    @Autowired
    private StatementRepository statementRepo;
    @Autowired
    private TagRepository tagRepo;
    @Autowired
    private StatementTagRepository statementTagRepo;

    @Commit
    @Transactional
    @Test
    public void createDummy(){
        String[] contentArray = {"화장실은 어디에 있나요?", "점심시간은 언제부터입니까?", "잠깐 전화 좀 받고 오겠습니다.", "신분증은 안 가져왔습니다.", "본인인데도 안 될까요?"};
        List<Statement> statementList = Arrays.stream(contentArray).map(this::makeDummyState).collect(Collectors.toList());
        statementRepo.saveAll(statementList);

        String[] tagNameArray = {"질문", "업무", "은행"};
        List<Tag> tagList = Arrays.stream(tagNameArray).map(Tag::new).map(tag->tagRepo.save(tag)).collect(Collectors.toList());

        statementTagRepo.save(new StatementTag(statementList.get(0), tagList.get(0)));
        statementTagRepo.save(new StatementTag(statementList.get(1), tagList.get(0)));
        statementTagRepo.save(new StatementTag(statementList.get(1), tagList.get(1)));
        statementTagRepo.save(new StatementTag(statementList.get(2), tagList.get(1)));
        statementTagRepo.save(new StatementTag(statementList.get(3), tagList.get(2)));
        statementTagRepo.save(new StatementTag(statementList.get(4), tagList.get(0)));
        statementTagRepo.save(new StatementTag(statementList.get(4), tagList.get(2)));

        Member member1 = Member.builder().email("test1@gmail.com").nickname("testuser1").platform(Platform.GOOGLE).profile("testProfile").build();
        Member member2 = Member.builder().email("test2@gmail.com").nickname("testuser2").platform(Platform.GOOGLE).profile("testProfile").build();
        member1.addMemberRole(MemberRole.USER);
        memberRepo.save(member1);
        memberRepo.save(member2);

        bookmarkRepo.save(new Bookmark(member1,statementList.get(0)));
        bookmarkRepo.save(new Bookmark(member1,statementList.get(2)));
        bookmarkRepo.save(new Bookmark(member1,statementList.get(3)));
        bookmarkRepo.save(new Bookmark(member2,statementList.get(0)));
    }

    public Statement makeDummyState(String content){
        String pitch_x = "[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98]";
        String pitch_y = "[0.38510799407958984, 0.3961954116821289, 0.3965352773666382, 0.39658570289611816, 0.4045286178588867, -1, 0.4626607596874237, 0.5112909078598022, 0.5252417325973511, 0.538426399230957, 0.5354131460189819, 0.5655378699302673, 0.5637024641036987, 0.5505946278572083, 0.5375474691390991, 0.5223244428634644, 0.5087040662765503, 0.5060124397277832, 0.49677348136901855, 0.5502279996871948, 0.560879111289978, 0.5784233808517456, 0.5985513925552368, 0.6065154671669006, 0.599539041519165, 0.6032493114471436, -1, -1, -1, 0.6075941324234009, 0.614975094795227, 0.6226295828819275, -1, -1, 0.6397407054901123, 0.6425567865371704, 0.6352427005767822, -1, -1, 0.5909004211425781, 0.5903264284133911, 0.5791071653366089, 0.5487216711044312, 0.5015572309494019, 0.49479204416275024, -1, 0.4544275104999542, 0.45289337635040283, 0.44299525022506714, -1, 0.4093781113624573, 0.3708370327949524, 0.3400304317474365, 0.32419827580451965, 0.31759271025657654, 0.31315454840660095, 0.3132156729698181, 0.31786876916885376, 0.3291468024253845, -1, -1, 0.5153849720954895, 0.5520882606506348, 0.5790427923202515, 0.5729116201400757, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0.3852044939994812, 0.3933887481689453, 0.3998890519142151, 0.42188912630081177, 0.4338732957839966, -1, 0.5041986703872681, 0.48860660195350647, 0.47642093896865845, 0.4627712070941925, 0.44499653577804565, 0.43287256360054016, 0.42191410064697266, 0.4325133264064789, 0.4409240484237671, 0.4445713758468628, 0.4552234709262848, 0.46373701095581055, 0.47526684403419495, 0.5011102557182312, 0.540361762046814, 0.5549062490463257]";

        return Statement.builder()
                .content(content)
                .pitchX(pitch_x)
                .pitchY(pitch_y)
                .build();
    }
}

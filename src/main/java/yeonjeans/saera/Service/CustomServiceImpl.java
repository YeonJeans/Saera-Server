package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeonjeans.saera.domain.entity.Bookmark;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.domain.entity.custom.CTag;
import yeonjeans.saera.domain.entity.custom.Custom;
import yeonjeans.saera.domain.entity.custom.CustomCtag;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.BookmarkRepository;
import yeonjeans.saera.domain.repository.PracticeRepository;
import yeonjeans.saera.domain.repository.custom.CTagRepository;
import yeonjeans.saera.domain.repository.custom.CustomCTagRepository;
import yeonjeans.saera.domain.repository.custom.CustomRepository;
import yeonjeans.saera.domain.repository.member.MemberRepository;
import yeonjeans.saera.dto.*;
import yeonjeans.saera.dto.ML.PitchGraphDto;
import yeonjeans.saera.exception.CustomException;
import yeonjeans.saera.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static yeonjeans.saera.domain.entity.example.ReferenceType.CUSTOM;
import static yeonjeans.saera.exception.ErrorCode.CUSTOM_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomServiceImpl {
    private final CustomRepository customRepository;
    private final CTagRepository cTagRepository;
    private final CustomCTagRepository customCTagRepository;
    private final MemberRepository memberRepository;
    private final PracticeRepository practiceRepository;
    private final BookmarkRepository bookmarkRepository;

    private final StatementServiceImpl statementService;

    private final WebClientService webClient;

    @Transactional
    public CustomResponseDto create(CustomRequestDto dto, Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String content = dto.getContent();
        ArrayList<String> tags = dto.getTags();
        Boolean setPublic = dto.getSetPublic();

//        byte[] audioBytes = webClient.getTTS(content);
//        ByteArrayResource audioResource = new ByteArrayResource(audioBytes) {
//            @Override
//            public String getFilename() {
//                return "audio.wav";
//            }
//        };
        ByteArrayResource audioResource = (ByteArrayResource) statementService.getTTS(78L);
        PitchGraphDto graphDto = webClient.getPitchGraph(audioResource);

        Custom custom = Custom.builder()
                .content(content)
                .file(audioResource.getByteArray())
                .pitchX(graphDto.getPitch_x().toString())
                .pitchY(graphDto.getPitch_y().toString())
                .member(member)
                .isPublic(setPublic)
                .build();

        List<CTag> tagList = tags.stream().map(ctag->saveTag(ctag, member)).collect(Collectors.toList());

        List<CustomCtag> relationList = tagList.stream().map(cTag -> new CustomCtag(custom, cTag)).collect(Collectors.toList());
        customCTagRepository.saveAll(relationList);

        return new CustomResponseDto(customRepository.save(custom));
    }

    @Transactional
    public void delete(Long fk, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Custom custom = customRepository.findById(fk).orElseThrow(()-> new CustomException(ErrorCode.CUSTOM_NOT_FOUND));
        if(custom.getIsPublic()){
            throw new CustomException(ErrorCode.PUBLIC_CANT_DEL);
        }
        List<CustomCtag> ctList = customCTagRepository.findAllByCustom(custom);
        customCTagRepository.deleteAll(ctList);

        //member가 만들었고, CustomCtag에 연관관계가 없는 tag
        List<CTag> tList = cTagRepository.findAllByMemberNotInCustomCTag(member);
        cTagRepository.deleteAll(tList);

        customRepository.delete(custom);

        Practice practice = practiceRepository.findByMemberAndTypeAndFk(member, CUSTOM, fk).orElse(null);
        Bookmark bookmark = bookmarkRepository.findByMemberAndTypeAndFk(member, CUSTOM, fk).orElse(null);
        if(practice != null) practiceRepository.delete(practice);
        if(bookmark != null) bookmarkRepository.delete(bookmark);
    }

    public List<NameIdDto> getTagList(Long memberId) {
        List<CTag> cTagList = cTagRepository.findAllByMemberId(memberId);
        return cTagList.stream().map(NameIdDto::new).collect(Collectors.toList());
    }

    public CustomResponseDto read(Long id, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Object[]> list = customRepository.findByIdWithBookmarkAndPractice(member, id);
        if(list.isEmpty()) throw new CustomException(CUSTOM_NOT_FOUND);
        Object[] result = list.get(0);

        Custom custom = result[0] instanceof Custom ? ((Custom) result[0]) : null;
        Bookmark bookmark = result[1] instanceof Bookmark ? ((Bookmark) result[1]) : null;
        Practice practice = result[2] instanceof Practice ? ((Practice) result[2]) : null;

        return new CustomResponseDto(custom, bookmark, practice);
    }

    public PracticeWordDto checkUnique(String content){
        Boolean isDuplicate = customRepository.existsByContentAndIsPublicTrue(content);
        return new PracticeWordDto(!isDuplicate);
    }

    private CTag saveTag(String tagname, Member member){
        Optional<CTag> cTag = cTagRepository.findByMemberAndName(member, tagname);
        if(cTag.isPresent())
            return cTag.get();
        return cTagRepository.save(new CTag(tagname, member));
    }

    public List<CustomListItemDto> getCustoms(String content, ArrayList<String> tags, Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Stream<Object[]> stream;
        if(content==null&&tags==null){
            stream = customRepository.findAllWithBookmarkAndPractice(member).stream();
        }else if(content!=null&&tags!=null){
            stream = Stream.concat(customRepository.findAllByContentContaining(member,'%'+content+'%').stream(), searchByTagList(tags, member)).distinct();
        }else if(content!=null){
            stream = customRepository.findAllByContentContaining(member,'%'+content+'%').stream();
        }else{
            stream = searchByTagList(tags, member);
        }
        return stream.map(i->new CustomListItemDto(i, true)).collect(Collectors.toList());
    }

    public List<CustomListItemDto> getPublicCustoms(String content, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Stream<Object[]> stream;
        if (content == null) {
            stream = customRepository.findAllByIsPublicTrueWithBookmarkAndPractice(member).stream();
        } else {
            stream = customRepository.findAllByIsPublicTrueAndContentContaining(member, '%'+content+'%').stream();
        }
        return stream.map(CustomListItemDto::new).collect(Collectors.toList());
    }

    public List<CustomListItemDto> getBookmarkedCustoms(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Stream<CustomListItemDto> stream = Stream.concat(
                customRepository.findBookmarkedAllwithPractice(member).stream()
                .map(i->new CustomListItemDto(i,true)),
                customRepository.findBookmarkedAllByMemberNotwithPractice(member).stream()
                .map(i->new CustomListItemDto(i, false))
            );

        return stream.collect(Collectors.toList());
    }

    private Stream<Object[]> searchByTagList(ArrayList<String> tags, Member member) {
        List<Long> idList = customRepository.findAllByTagnameIn(tags);
        return customRepository.findAllByIdWithBookmarkAndPractice(member, idList).stream();
    }

    public Resource getExampleRecord(Long id) {
        Custom custom = customRepository.findById(id).orElseThrow(()->new CustomException(CUSTOM_NOT_FOUND));
        return new ByteArrayResource(custom.getFile()) {
            @Override
            public String getFilename() {
                return "audio.wav";
            }
        };
    }
}
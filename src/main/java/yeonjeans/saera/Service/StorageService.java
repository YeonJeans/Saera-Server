package yeonjeans.saera.Service;

import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.member.MemberRepository;
import yeonjeans.saera.exception.CustomException;
import yeonjeans.saera.exception.ErrorCode;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class StorageService {
    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;
    private final MemberRepository memberRepository;
    private final Storage storage;

    public String updateMemberInfo(MultipartFile file, Long memberId) throws IOException {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String uuid = UUID.randomUUID().toString();
        String contentType = file.getContentType();

        checkContentType(contentType);
        deleteProfilePicture(member.getProfileUrl());

        BlobId blobId = BlobId.of(bucketName, uuid);

        // Create BlobInfo with the BlobId and set the content type
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();

        // Upload the file to GCP Storage
        Blob blob = storage.create(blobInfo, file.getBytes());
        return blob.getMediaLink();
    }

    private void checkContentType(String contentType){
        if(contentType != null && contentType.startsWith("image/")) return;
        throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
    }

    private void deleteProfilePicture(String profileUrl) {
        if(!profileUrl.startsWith("https://storage.googleapis.com")) return;
        int start = profileUrl.lastIndexOf('/');
        int end = profileUrl.lastIndexOf('?');
        if (start > 0 && end < profileUrl.length() - 1) {
            String objectName = profileUrl.substring(start + 1, end);
            storage.delete(bucketName, objectName);
        }
    }
}
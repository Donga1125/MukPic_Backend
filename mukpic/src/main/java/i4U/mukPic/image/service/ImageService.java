package i4U.mukPic.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import i4U.mukPic.global.exception.BusinessLogicException;
import i4U.mukPic.global.exception.ExceptionCode;
import i4U.mukPic.image.entity.Image;
import i4U.mukPic.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 s3Client;


    // 여러 개 파일 S3 업로드
    public List<String> uploadFile(List<MultipartFile> multipartFile, Short type) {
        List<String> fileNameList = new ArrayList<>();

        multipartFile.forEach(file -> {
            String fileName = createFileName(type, file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                // S3에 파일 업로드
                s3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
            } catch (IOException e) {
                //todo: 에러 처리 수정 필요
                throw new RuntimeException("파일 업로드 에러");

            } catch (AmazonS3Exception e) {
                //todo: 에러 처리 수정 필요
                throw new RuntimeException("파일 업로드 에러");
            }

            // S3에 저장된 파일의 URL 생성
            String fileUrl = s3Client.getUrl(bucket, fileName).toString();
            fileNameList.add(fileUrl);  // 업로드된 파일의 URL을 반환
        });

        saveImages(fileNameList);
        return fileNameList;
    }

    /**
     * db에 이미지 관련 정보 수정 메서드
     * @param referenceId
     * @param imageType
     * @param imageUrls
     */
    public void updateReferenceIdAndType (Long referenceId, Short imageType, List<String> imageUrls) {
        // 해당 URL 목록을 사용하여 이미지 테이블의 레퍼런스 정보 업데이트
        for (String imageUrl : imageUrls) {
            Image image = imageRepository.findByImageUrl(imageUrl).orElseThrow(
                    () -> new RuntimeException("파일을 찾을 수 없음"));
            if (image != null) {
                image.updateImage(imageType, referenceId);
                imageRepository.save(image);
            }
        }
    }

    /**
     * 프로필 이미지 수정
     * @param referenceId
     * @param imageType
     * @param imageUrl
     */
    public void updateProfileImage(Long referenceId, Short imageType, String imageUrl) {
        Optional<Image> originImage = imageRepository.findByImageTypeAndReferenceId(referenceId, imageType);
        originImage.ifPresentOrElse(
                image -> {
                    // 해당 이미지가 이미 존재하는 경우
                    deleteImage(originImage.get().getImageUrl());
                    updateImage(referenceId, imageType, imageUrl);
                },
                () -> {
                    // 해당 이미지가 존재하지 않는 경우
                    updateImage(referenceId, imageType, imageUrl);
                }
        );
    }



    /**
     * 게시글,리뷰에 연관된 이미지 list 조회
     * @param referenceId
     * @param imageType
     */
    public List<String> getImagesByReferenceIdAndType(Long referenceId, Short imageType) {
        return imageRepository.findByReferenceIdAndImageType(referenceId, imageType)
                .stream()
                .map(Image::getImageUrl)
                .toList();
    }


    @Transactional
    public void deleteImage(String imageUrl) {
        Image image = imageRepository.findByImageUrl(imageUrl).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.FILE_NOT_FOUND));

        try {
            imageRepository.delete(image);
            String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            deleteS3File(fileName);

        } catch (Exception e) {
            log.error("Failed to delete image with URL {}: {}", imageUrl, e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.FILE_DELETE_ERROR);
        }
    }


    // 기본 이미지 URL을 반환하는 메서드
    public String getDefaultImageUrl() {
        return "https://mukpic-image.s3.ap-northeast-2.amazonaws.com/NpcNmlOcp02.jpeg";
    }

    // S3 버킷에서 파일 삭제
    private void deleteS3File(String fileName) {
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));

        } catch (AmazonS3Exception e) {
            throw new RuntimeException("파일을 삭제 오류");
        }
    }


    // 이미지 DB 저장 (URL만 저장)
    private void saveImages(List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            Image image = Image.builder()
                    .imageUrl(imageUrl)
                    .build();
            imageRepository.save(image);
        }
    }


    private void updateImage (Long referenceId, Short imageType, String imageUrl){
        Image newImage = imageRepository.findByImageUrl(imageUrl).orElseThrow(
                () -> new RuntimeException("파일을 찾을 수 없음"));
        newImage.updateImage(imageType,referenceId);
        imageRepository.save(newImage);
    }

    // 파일 유효성 검사
    private String getFileExtension(String fileName) {
        if (fileName.length() == 0) {
            //todo: 예외처리 수정 필요
            throw new RuntimeException("파일 이름 에러");
        }
        List<String> fileValidate = List.of(".jpg", ".jpeg", ".JPG", ".JPEG", ".png", ".PNG");
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!fileValidate.contains(idxFileName)) {
            throw new RuntimeException("파일 확장자 에러");
        }
        return idxFileName;
    }

    // 파일명 중복 방지 (UUID) 및 타입별 디렉토리 생성
    private String createFileName(Short type, String fileName) {
        return type + "/" + UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }
}

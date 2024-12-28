package i4U.mukPic.image.controller;

import i4U.mukPic.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadImages( @RequestParam(value = "file") List<MultipartFile> file,
                                                      @RequestParam("type") Short type) {
        //todo: 예외 처리 클래스 생성 필요
        //imageFiles가 null인 경우 처리
        if (file == null) {
            throw new RuntimeException("imageFiles가 null");
        }
        // 최대 이미지 파일 수 제한
        if (file.size() > 5) {
            throw new RuntimeException("최대 이미지 파일 수 제한 초과");
        }

        List<String> imageUrls = imageService.uploadFile(file,type);
        return ResponseEntity.ok(imageUrls);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFile(@RequestParam String imageUrl) {
        imageService.deleteImage(imageUrl);
        return ResponseEntity.noContent().build();
    }
}

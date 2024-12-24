package i4U.mukPic.community;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feeds")
public class CommunityController {

    @GetMapping
    public ResponseEntity<String> getAllFeed() {
        return ResponseEntity.ok("test");
    }
}

package practice.hongxeob.a_try;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RetryController {
    private final RetryService retryService;

    @GetMapping("/api-call")
    public String apiCall(@RequestParam String param) {
        return retryService.process(param);
    }
}

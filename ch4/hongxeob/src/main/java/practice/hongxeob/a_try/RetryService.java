package practice.hongxeob.a_try;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import practice.hongxeob.exception.RetryException;

@Service
@Slf4j
public class RetryService {
    private static final String SIMPLE_RETRY_CONFIG = "simpleRetryConfig";

    @Retry(name = SIMPLE_RETRY_CONFIG, fallbackMethod = "fallback")
    public String process(String param) {
        return callAnotherServer(param);
    }

    private String fallback(Exception ex, String param) {
        log.info("Fallback!! your request is : {}", param);
        return "Recovered : " + ex.toString();
    }

    private String callAnotherServer(String param) {
        //retry exception은 retry가 된다.
        log.info("Processing param: {}", param);
        throw new RetryException("Retry Exception");
        // ignore exception은 retry하지 않고 바로 예외가 클라이언트에게 전달된다.
        // throw new IgnoreException("Ignore Exception");

    }
}

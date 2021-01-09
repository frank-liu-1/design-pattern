package bootsrc.rpclib.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 采样输出。比如10000处理中，只有前20次是会输出日志的。这样在生产环境中通过少了的日志能判断出
 * 相应的逻辑是否正常。
 * @author bootsrc
 */
@Component
public class SamplingUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SamplingUtils.class);

    private static final long HIT_PER_PERIOD = 20L;
    private final AtomicLong value = new AtomicLong(0);

    @Value("${cache.sampling.modulus:10000}")
    private long modulus;

    @Value("${cache.sampling.threshold:1000000}")
    private long threshold;

    public boolean hit() {
        boolean hitValue = false;
        try {
            if (value.get() > threshold) {
                value.set(0);
            }
            long current = value.incrementAndGet();
            if (current % modulus < HIT_PER_PERIOD) {
                LOGGER.info("SamplingUtils.current={}", current);
                hitValue = true;
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
        return hitValue;
    }
}

package example.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisUtilTest {
    @Autowired
    private RedisUtil redisUtil;

    @Test
    void setRedisTemplate() {
        redisUtil.set("k1", "v1");
        if (redisUtil.hasKey("k1")) {
            Object value = redisUtil.get("k1");
            System.out.println(value);
        }
    }
}
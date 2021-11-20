package example.lock;

import example.exception.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DistributionLockFactory {
    @Autowired
    private Map<String, DistributionLock> lockMap;

    public static final class LockType {
        public static final String MYSQL_OPTIMISTIC = "MysqlOptimistic";
        public static final String REDIS = "RedisLock";
        public static final String ZOOKEEPER = "ZookeeperLock";
    }

    public DistributionLock getLockByType(String lockType) {
        switch (lockType) {
            case LockType.MYSQL_OPTIMISTIC:
                return lockMap.get(lockType);
            case LockType.REDIS:
            case LockType.ZOOKEEPER:
                throw new MyException("暂未实现的锁类型");
            default:
                throw new MyException("未知锁类型");
        }
    }
}

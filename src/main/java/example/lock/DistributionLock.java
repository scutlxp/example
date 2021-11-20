package example.lock;

import example.utils.Result;

public interface DistributionLock {
    Result<String> lock(String lockKey, long getTimeout, long lockTimeout, String lockInfo);

    Result<String> unLock(String lockKey);

    String getLockType();
}

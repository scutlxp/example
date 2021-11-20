package example.lock;

import example.dao.LockModelDao;
import example.entity.LockModel;
import example.utils.IDUtil;
import example.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = DistributionLockFactory.LockType.MYSQL_OPTIMISTIC)
public class MysqlOptimisticLock implements DistributionLock {
    @Autowired
    private LockModelDao lockModelDao;

    private final ThreadLocal<String> requestIdTL = new ThreadLocal<>();

    @Override
    public Result<String> lock(String lockKey, long getTimeout, long lockTimeout, String lockInfo) {
        if (StringUtils.isBlank(lockKey)) {
            return Result.failed("lockKey cannot be empty");
        }

        long startTime = System.currentTimeMillis();
        String currentRequestId = getRequestId();

        while (true) {
            LockModel lockModel = lockModelDao.findByLockKey(lockKey);

            // 1. 目标资源(lockKey第一次加锁)
            if (lockModel == null) {
                lockModel = new LockModel();
                lockModel.setLockKey(lockKey);
                lockModel.setLockInfo(lockInfo);
                lockModel.setLockCount(0L);
                lockModel.setVersion(0L);
                lockModel.setLockTimeout(lockTimeout + startTime);
                lockModel.setRequestId(getRequestId());
                lockModelDao.save(lockModel);
                return Result.success();
            }

            // 2. 没有设置requestId表示该锁未被占用
            if (StringUtils.isBlank(lockModel.getRequestId())) {
                lockModel.setRequestId(currentRequestId);
                lockModel.setLockKey(lockKey);
                lockModel.setLockCount(0L);
                lockModel.setLockInfo(lockInfo);
                lockModel.setLockTimeout(lockTimeout + startTime);
                if (lockModelDao.update(lockModel) == 1) {
                    return Result.success();
                }
                continue;
            }

            // 3. 可重入实现
            if (StringUtils.equals(lockModel.getRequestId(), currentRequestId)) {
                lockModel.setLockCount(lockModel.getLockCount() + 1);
                lockModel.setLockTimeout(lockTimeout + startTime);
                lockModel.setLockInfo(lockInfo);
                if (lockModelDao.update(lockModel) == 1) {
                    return Result.success();
                }
                continue;
            }

            // 4. 锁被其他线程占用，但是已超时，重置锁
            // 如果执行到这里则会导致锁失效，即多个线程都获得锁
            if (System.currentTimeMillis() > lockModel.getLockTimeout()) {
                resetLockModel(lockModel);
                continue;
            }

            // 5. 锁被占用则等待再次获取直到超时或者获取成功
            if (System.currentTimeMillis() - startTime > getTimeout) {
                return Result.failed(lockModel.getLockInfo());
            }

            try {
                Thread.sleep(100);
            } catch (Exception e) {
                log.warn("sleep error,", e);
            }
        }
    }

    @Override
    public Result<String> unLock(String lockKey) {
        LockModel lockModel = lockModelDao.findByLockKey(lockKey);
        if (lockModel == null || !StringUtils.equals(lockModel.getRequestId(), getRequestId())) {
            return Result.success();
        }

        if (lockModel.getLockCount() <= 1) {
            resetLockModel(lockModel);
        } else {
            lockModel.setLockCount(lockModel.getLockCount() - 1);
            lockModelDao.update(lockModel);
        }

        return Result.success();
    }

    @Override
    public String getLockType() {
        return DistributionLockFactory.LockType.MYSQL_OPTIMISTIC;
    }

    private String getRequestId() {
        String requestId = requestIdTL.get();
        if (StringUtils.isBlank(requestId)) {
            requestId = IDUtil.getUUId();
            requestIdTL.set(requestId);
        }
        return requestId;
    }

    private void resetLockModel(LockModel lockModel) {
        lockModel.setRequestId("");
        lockModel.setLockInfo("");
        lockModel.setLockCount(0L);
        lockModel.setLockTimeout(0L);
        lockModelDao.update(lockModel);
    }
}

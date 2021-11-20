package example.controller;

import example.dao.LockModelDao;
import example.entity.LockModel;
import example.lock.DistributionLock;
import example.lock.DistributionLockFactory;
import example.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example/lock")
public class LockController {
    @Autowired
    private LockModelDao lockModelDao;

    @Autowired
    private DistributionLockFactory lockFactory;

    @GetMapping("/create")
    public Result<LockModel> createLockModel(@RequestParam String lockKey) {
        LockModel lockModel = new LockModel();
        lockModel.setLockKey(lockKey);
        lockModel.setLockCount(1L);
        lockModel.setLockTimeout(1000L);
        lockModel.setVersion(1L);
        lockModel.setRequestId("xxx");
        lockModelDao.save(lockModel);
        return Result.success();
    }

    @GetMapping("/update")
    public Result<String> updateLockModel(@RequestParam String lockKey) {
        LockModel lockModel = lockModelDao.findByLockKey(lockKey);
        if (lockModel == null) {
            return Result.failed("不存在");
        }
        lockModel.setLockInfo("testxxxxxx");
        int result= lockModelDao.update(lockModel);
        return Result.success("操作成功" + result);
    }

    @GetMapping("/lock")
    public Result<String> lock(@RequestParam String lockKey, @RequestParam Long getTimeout, @RequestParam Long lockTimeout,
                               @RequestParam String lockInfo, @RequestParam String lockType) throws InterruptedException {
        DistributionLock lock = lockFactory.getLockByType(lockType);
        Result<String> lockResult = lock.lock(lockKey, getTimeout, lockTimeout, lockInfo);
        if (lockResult.isSuccess()) {
            Thread.sleep(30000);
            lock.unLock(lockKey);
            return Result.success();
        }
        return Result.failed(lockResult.getMsg());
    }

    @GetMapping("/unlock")
    public Result<String> unLock(@RequestParam String lockKey, @RequestParam String lockType) {
        DistributionLock lock = lockFactory.getLockByType(lockType);
        lock.unLock(lockKey);
        return Result.success();
    }
}

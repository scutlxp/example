package example.dao;

import example.entity.LockModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface LockModelDao extends JpaRepository<LockModel, Integer> {
    @Modifying
    @Transactional
    @Query("update LockModel lock set lock.lockKey = :#{#lockModel.lockKey}, " +
            "lock.requestId = :#{#lockModel.requestId}, " +
            "lock.lockTimeout = :#{#lockModel.lockTimeout}, " +
            "lock.lockCount = :#{#lockModel.lockCount}, " +
            "lock.lockInfo = :#{#lockModel.lockInfo}, lock.version = :#{#lockModel.version + 1} " +
            "where lock.lockKey = :#{#lockModel.lockKey} and lock.version = :#{#lockModel.version}")
    int update(@Param("lockModel") LockModel lockModel);

    LockModel findByLockKey(String lockKey);
}

package example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_lock")
public class LockModel extends CommonBean{
    @Column(name = "lock_key")
    private String lockKey;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "version")
    private Long version;

    /**
     * 持有锁超时截止时间，等于获取锁时间+超时时间
     */
    @Column(name = "lock_timeout")
    private Long lockTimeout;

    @Column(name = "lock_count")
    private Long lockCount;

    @Column(name = "lock_info", length = 2048)
    private String lockInfo;
}

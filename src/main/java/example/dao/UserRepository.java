package example.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import example.bean.User;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * findBy查询，约定大于配置
     */
    List<User> findByName(String name);

    /**
     * 映射SQL
     */
    @Query("select user from User user where user.name = ?1")
    List<User> listByName1(String name);

    /**
     * 原生SQL查询
     */
    @Query(value = "select * from t_user where name = ?1", nativeQuery = true)
    List<User> listByName2(String name);

    void deleteById(int id);

    /**
     * 删除更新需加事务注解
     */
    @Modifying
    @Transactional
    @Query("delete from User user where user.id = ?1")
    void deleteById1(int id);
}

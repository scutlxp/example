package example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_user")
public class User extends CommonBean{
    @Column(name = "name", length = 128)
    private String name;

    @Column(name = "age")
    private Integer age;

    /**
     * 默认长度255
     */
    @Column(name = "address")
    private String address;
}

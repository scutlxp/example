package example.bean;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "t_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

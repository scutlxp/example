package example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import example.bean.User;
import example.dao.UserRepository;
import example.exception.MyException;
import example.utils.Result;

import java.util.List;

@RestController
@RequestMapping("/example/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/echoParam")
    public Result<String> echoParam(@RequestParam(name = "name", defaultValue = "lxp") String name) throws MyException {
        if ("lxp".equals(name)) {
            throw new MyException("exception test");
        }
        return Result.success(name + ", hello!");
    }

    @GetMapping("/save")
    public Result<User> saveUser(@RequestParam String name, @RequestParam String address) {
        User user = new User();
        user.setAddress(address);
        user.setName(name);
        userRepository.save(user);
        return Result.success(user);
    }

    @GetMapping("/query")
    public Result<List<User>> query(@RequestParam String name) {
        return Result.success(userRepository.listByName2(name));
    }

    @GetMapping("/delete")
    public Result<String> delete(@RequestParam int id) {
        userRepository.deleteById1(id);
        return Result.success();
    }
}

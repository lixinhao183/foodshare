package xinhao.foodshare;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import xinhao.foodshare.mapper.MenuMapper;
import xinhao.foodshare.mapper.UserMapper;
import xinhao.foodshare.pojo.entity.User;

import java.util.List;

@SpringBootTest
class FoodshareApplicationTests {


    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUserMapper() {
        List<User> users = userMapper.selectList(null);
        System.out.println(users);
    }

    @Test
    public void testBCryptPasswordEncoder() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("lixinhao123");
        String encode2 = passwordEncoder.encode("1234");
        System.out.println(encode);
        System.out.println(encode2);
    }


    @Autowired
    private MenuMapper menuMapper;

    @Test
    public void testSelectPermKeysByUserId() {
        List<String> permKeys = menuMapper.selectPermKeysByUserId(1L);
        System.out.println(permKeys);
    }
}

package xinhao.foodshare;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

    @Test
    public void testBCrypt() {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("lixinhao123");
        String encode2 = passwordEncoder.encode("1234");
        
        System.out.println("密码1: " + encode);
        System.out.println("密码2: " + encode2);
    }
}
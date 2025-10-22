package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sms_verification")
public class SmsVerification implements Serializable {
    private Long id;                  // 验证码记录ID

    private Long phone;               // 手机号

    private String code;              // 验证码内容

    private String purpose;            // 验证码用途(注册,登录,修改密码)

    private LocalDateTime updateTime;  // 生成时间

    private LocalDateTime expiresTime; // 过期时间

    private Integer attempts;           // 尝试次数
}
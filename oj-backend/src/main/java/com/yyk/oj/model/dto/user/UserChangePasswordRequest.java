package com.yyk.oj.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户修改密码请求
 */
@Data
public class UserChangePasswordRequest implements Serializable {

    /**
     * 当前密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认新密码
     */
    private String confirmPassword;

    private static final long serialVersionUID = 1L;
}

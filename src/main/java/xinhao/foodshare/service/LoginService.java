package xinhao.foodshare.service;

import xinhao.foodshare.pojo.entity.User;
import xinhao.foodshare.result.ResponseResult;

public interface LoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}

package xinhao.foodshare.service;

import xinhao.foodshare.pojo.dto.UserRegisterDTO;
import xinhao.foodshare.pojo.dto.UserUpdateDTO;
import xinhao.foodshare.pojo.vo.UserVO;
import xinhao.foodshare.result.ResponseResult;

public interface UserService {
    ResponseResult<UserVO> register(UserRegisterDTO userRegisterDTO);

    ResponseResult<Void> update(UserUpdateDTO userUpdateDTO);

    ResponseResult<UserVO> info();
}


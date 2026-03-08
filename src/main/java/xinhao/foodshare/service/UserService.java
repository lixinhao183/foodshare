package xinhao.foodshare.service;

import java.util.List;

import xinhao.foodshare.pojo.dto.UserRegisterDTO;
import xinhao.foodshare.pojo.dto.UserUpdateDTO;
import xinhao.foodshare.pojo.entity.Post;
import xinhao.foodshare.pojo.entity.ViewHistory;
import xinhao.foodshare.pojo.vo.FollowsVO;
import xinhao.foodshare.pojo.vo.UserVO;

public interface UserService {
    /**
     * 用户注册
     * @param userRegisterDTO 用户注册信息
     * @return 注册结果
     */
    UserVO register(UserRegisterDTO userRegisterDTO);

    /**
     * 用户更新信息
     * @param userUpdateDTO 用户更新信息
     */
    void update(UserUpdateDTO userUpdateDTO);

    /**
     * 用户获取个人信息
     * @return 用户个人信息
     */
    UserVO info();

    /**
     * 查询关注用户
     * @return 关注用户列表
     */
    List<FollowsVO> follow();
    
    /**根据帖子id列表查询帖子信息
     * @param postIds 帖子ID列表
     * @return 帖子信息列表
     */
    List<Post> queryPostsByIds(List<Long> postIds);
    
    /**
     * 查询用户游览记录
     * @return 用户游览记录列表
     */
    List<ViewHistory> viewHistory();

    /**
     * 查询收藏帖子
     * @return 收藏帖子列表
     */
    List<Post> favourite();


}


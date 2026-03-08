package xinhao.foodshare.service;

import xinhao.foodshare.pojo.vo.PostVO;
import xinhao.foodshare.result.PageResult;

import java.util.List;

import xinhao.foodshare.pojo.dto.PostDTO;
import xinhao.foodshare.pojo.entity.Post;

public interface PostService {

    /**
     * 分页查询帖子
     * @param page 页码
     * @param pageSize 每页数量
     * @param sort 排序字段
     * @return 帖子分页数据
     */
    PageResult<PostVO> pageQuery(Integer page, Integer pageSize, String sort, String local);

    /**
     * 查询帖子详情
     * @param postId 帖子ID
     * @return 帖子详情数据
     */
    PostVO detail(Long postId);

    /**
     * 发布帖子
     * @param postDTO 帖子数据
     * @return 发布结果
     */
    void publish(PostDTO postDTO);

        /**
     * 查询用户发布的帖子
     * @return 帖子列表
     */
    List<Post> getPosts(Long userId);
}
package xinhao.foodshare.service;

import xinhao.foodshare.pojo.entity.Comment;
import xinhao.foodshare.pojo.entity.Tag;
import xinhao.foodshare.pojo.vo.PostVO;
import xinhao.foodshare.result.PageResult;

import java.util.List;

public interface PostService {

    /**
     * 分页查询帖子
     * @param page 页码
     * @param pageSize 每页数量
     * @param sort 排序字段
     * @param local 地点
     * @param price 价格范围
     * @param keyword 搜索关键词
     * @param tags 标签列表
     * @return 帖子分页数据
     */
    PageResult<PostVO> pageQuery(Integer page, Integer pageSize, String sort, String local, String price, String keyword, List<String> tags);

    /**
     * 查询帖子详情
     * @param postId 帖子ID
     * @return 帖子详情数据
     */
    PostVO detail(Long postId);


        /**
     * 分页查询评论
     * @param postId 帖子ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 评论列表
     */
    PageResult<Comment> getComments(Long postId, Integer page, Integer pageSize);

        /**
     * 分页查询标签
     * @param page 页码
     * @param pageSize 每页数量
     * @param tagName 标签名称（可选，模糊查询）
     * @return 标签列表
     */
    PageResult<Tag> getTags(Integer page, Integer pageSize, String tagName);
}
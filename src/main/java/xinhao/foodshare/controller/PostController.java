package xinhao.foodshare.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import xinhao.foodshare.pojo.entity.Comment;
import xinhao.foodshare.pojo.entity.Tag;
import xinhao.foodshare.pojo.vo.PostVO;
import xinhao.foodshare.result.PageResult;
import xinhao.foodshare.result.ResponseResult;
import xinhao.foodshare.service.PostService;

@RestController
@Slf4j
@RequestMapping("/post")
public class PostController{
    
    @Autowired
    private PostService postService;

    /**
     * 分页查询帖子列表 (用于无限加载)
     * @param page 页码
     * @param pageSize 每页数量
     * @param sort 排序方式
     * @param local 地点
     * @param price 价格范围
     * @param keyword 搜索关键词
     * @param tags 标签列表
     * @return 帖子列表数据
     */
    @GetMapping("/list")
    public ResponseResult<PageResult<PostVO>> list(@RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestParam(defaultValue = "new") String sort,
                                                   @RequestParam(required = false) String local,
                                                   @RequestParam(required = false) String price,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) List<String> tags) {
        PageResult<PostVO> result = postService.pageQuery(page, pageSize, sort, local, price, keyword, tags);
        return ResponseResult.success(result);
    }

    /**
     * 查询帖子详情
     * @param postId 帖子ID
     * @return 帖子详情数据
     */
    @GetMapping("/detail")
    public ResponseResult<PostVO> detail(@RequestParam Long postId) {
        PostVO result = postService.detail(postId);
        return ResponseResult.success(result);
    }

    /**
     * 分页查询评论
     * 
     * @param postId   帖子ID
     * @param page     页码
     * @param pageSize 每页数量
     * @return 评论列表
     */
    @GetMapping("/comment/list")
    public ResponseResult<PageResult<Comment>> getComments(
            @RequestParam Long postId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("用户查询评论: postId={}, page={}, pageSize={}", postId, page, pageSize);
        PageResult<Comment> comments = postService.getComments(postId, page, pageSize);
        return ResponseResult.success(comments);
    }

    /**
     * 分页查询标签
     * 
     * @param page     页码
     * @param pageSize 每页数量
     * @param tagName  标签名称（可选，模糊查询）
     * @return 标签列表
     */
    @GetMapping("/tag")
    public ResponseResult<PageResult<Tag>> getTags(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String tagName) {
        log.info("用户查询标签: page={}, pageSize={}, tagName={}", page, pageSize, tagName);
        PageResult<Tag> tagList = postService.getTags(page, pageSize, tagName);
        return ResponseResult.success(tagList);
    }

}

package xinhao.foodshare.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import xinhao.foodshare.pojo.dto.PostDTO;
import xinhao.foodshare.pojo.entity.Post;
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
     * @return 帖子列表数据
     */
    @GetMapping("/list")
    public ResponseResult<PageResult<PostVO>> list(@RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestParam(defaultValue = "new") String sort,
                                                   @RequestParam(required = false) String local) {
        PageResult<PostVO> result = postService.pageQuery(page, pageSize, sort, local);
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
     * 发布帖子
     * @param post 帖子数据
     * @return 发布结果
     */
    @PostMapping("/publish")
    public ResponseResult<Void> publish(@RequestBody PostDTO postDTO) {
        postService.publish(postDTO);
        return ResponseResult.success("发布成功");
    }

    /**
     * 查询用户发布的帖子记录
     * @return 帖子列表
     */
    @GetMapping("/user")
    public ResponseResult<List<Post>> posts(@RequestParam Long userId) {
        log.info("用户查询发布的帖子");
        List<Post> postList = postService.getPosts(userId);
        return ResponseResult.success(postList);
    }
}
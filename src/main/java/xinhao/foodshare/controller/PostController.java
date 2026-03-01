package xinhao.foodshare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
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
                                                   @RequestParam(defaultValue = "new") String sort) {
        PageResult<PostVO> result = postService.pageQuery(page, pageSize, sort);
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
}
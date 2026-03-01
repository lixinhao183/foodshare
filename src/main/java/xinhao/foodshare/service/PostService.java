package xinhao.foodshare.service;

import xinhao.foodshare.pojo.vo.PostVO;
import xinhao.foodshare.result.PageResult;

public interface PostService {

    PageResult<PostVO> pageQuery(Integer page, Integer pageSize, String sort);

    PostVO detail(Long postId);

}
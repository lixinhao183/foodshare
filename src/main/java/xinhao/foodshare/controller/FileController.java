package xinhao.foodshare.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import xinhao.foodshare.result.ResponseResult;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Value("${file.upload.path}")
    private String BASE_DIR;



    @PostMapping("/upload")
    public ResponseResult<String> upload(MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseResult.error("文件不能为空");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成新的文件名
        String newFilename = UUID.randomUUID().toString() + extension;

        File dest = new File(BASE_DIR, newFilename);
        
        // 确保目录存在
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try {
            file.transferTo(dest);

            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(newFilename)
                    .toUriString();
            log.info("文件上传成功: {}", url);
            return ResponseResult.success(url);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return ResponseResult.error("文件上传失败");
        }
    }
}

package com.sky.web.admin;

import com.sky.annotation.AutoFill;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/admin/common")
public class CommonController {
    @Autowired
    AliOssUtil aliOssUtil;

    /*文件上传*/
    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        if (file==null || file.getSize()<=0) {
            return Result.error("请选择正确的资源上传");
        }
        String url = aliOssUtil.upload(file.getOriginalFilename(), file.getInputStream());
        return Result.error(url);
    }

}

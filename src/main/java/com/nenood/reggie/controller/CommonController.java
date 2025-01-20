package com.nenood.reggie.controller;

import com.nenood.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件的上传和下载
 */
@RestController
@RequestMapping("common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basepath;

    @PostMapping("upload")
    public R<String> upload(MultipartFile file) {
        // 获取文件的原始名称
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        log.info("上传文件: {}", originalFilename);

        // 生成随机文件名，防止重复文件覆盖
        String randomFilename = UUID.randomUUID().toString() + suffix;
        // 创建保存目标目录
        File dir = new File(basepath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 存储文件
        try {
            file.transferTo(new File(basepath + randomFilename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(randomFilename);
    }

    /**
     * 下载文件后将图片在浏览器中显示
     */
    @GetMapping("download")
    public void download(@RequestParam("name") String name, HttpServletResponse response) {
        try {
            log.info("下载图片{}...", name);
            // 获取输入输出流
            FileInputStream fileInputStream = new FileInputStream(basepath + name);
            ServletOutputStream outputStream = response.getOutputStream();

            // 设置响应数据的格式
            response.setContentType("image/jpeg");

            // 读取图片并显示在浏览器内
            int len = 0;
            byte[] buffer = new byte[1024];
            while((len = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }

            // 关闭资源
            fileInputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.hejz.runtime_sh_file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author:hejz 75412985@qq.com
 * @create: 2023-02-10 21:30
 * @Description: 运行sh文件
 */
@RestController
@Slf4j
public class RuntimeController {
    @GetMapping
    public void run() throws IOException {
        log.info("项目部署………………………………………………");
        Process process = Runtime.getRuntime().exec("sh /root/start.sh" );
        InputStreamReader ips = new InputStreamReader(process.getInputStream());
        BufferedReader br = new BufferedReader(ips);
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }
}

package com.hejz.runtime_sh_file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

    int num = 0;
    @Autowired
    RestTemplate restTemplate;

    @GetMapping
    public void run() throws IOException {
        //1次连接不上就启动小米球
        //3次连载不上就启动服务
        //5次连接重启服务器
        Process process = null;
//        if (num == 1) {
            log.info("小米球重新部署………………………………………………");
            process= Runtime.getRuntime().exec("sh /root/url.sh");
//        }
//        if (num == 3) {
//            log.info("项目重新部署………………………………………………");
//            process= Runtime.getRuntime().exec("sh /root/start.sh");
//        }
//        if (num >= 5) {
//            log.info("服务器重新部署………………………………………………");
//            process= Runtime.getRuntime().exec("shutdown -r now");
//        }
        InputStreamReader ips = new InputStreamReader(process.getInputStream());
        BufferedReader br = new BufferedReader(ips);
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    @Scheduled(cron = "* 0/1 * * * ? ")
    public void runtime() throws IOException {
        String url = "http://nqql1sqmuqbt.ngrok.xiaomiqiu123.top/deployServer/heartbeat";
        try {
            ResponseEntity<Object> entity = restTemplate.getForEntity(url, null);
        } catch (Exception e) {
            num++;
            run();
        }
    }
}

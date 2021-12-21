package grain.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author wulifu
 */
@Slf4j
public class RequestUtils {
    public static final String HTTP_PREFIX = "http://";

    public static String getUrl(String host, String port, String target) {
        return HTTP_PREFIX + host + ":" + port + "/" + target;
    }

    public static String sendPost(String url, MultiValueMap<String, ?> keyValues) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(10000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, ?>> mapHttpEntity = new HttpEntity<>(keyValues, httpHeaders);
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, mapHttpEntity, String.class);
        return stringResponseEntity.getBody();
    }

    public static <T> T sendPost(String url, MultiValueMap<String, ? extends Object> keyValues, Class<T> clazz) {
        String s = sendPost(url, keyValues);
        return JSON.parseObject(s, clazz);
    }

    public static void postUploadFile(String url, MultiValueMap<String, Object> multiValueMap, File file) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        multiValueMap.add("file", new FileSystemResource(file));
        restTemplate.postForObject(url, multiValueMap, String.class);
    }

    public static boolean postGetFile(String url, MultiValueMap<String, ?> keyValues, String filePath) {
        try {
            int static1024 = 1024;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, ?>> mapHttpEntity = new HttpEntity<>(keyValues, httpHeaders);
            ResponseEntity<byte[]> response = restTemplate.postForEntity(url, mapHttpEntity, byte[].class);
            if (Objects.isNull(response.getBody())) {
                log.info("文件下载流为空");
                throw new RuntimeException();
            }
            InputStream inputStream = new ByteArrayInputStream(response.getBody());
            OutputStream outputStream = new FileOutputStream(new File(filePath));
            int len;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf, 0, static1024)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
            return true;
        } catch (Exception e) {
            log.info("文件请求失败 {}", e.getMessage());
            return false;
        }
    }
}

package grain.controller;

import grain.Msg;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author wulifu
 */
@Controller
@RequestMapping("file")
public class FileController {
    @PostMapping(value = "download", produces = "application/octet-stream;charset=UTF-8")
    public ResponseEntity<byte[]> download(String id) throws IOException {
        File file = new File("C:\\Users\\wulifu\\Desktop\\task.sh");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
    }

    @PostMapping(value = "upload")
    public Msg upload(String id, MultipartFile file) {
        try {
            System.out.println(id);
            file.transferTo(new File("C:\\Users\\wulifu\\Desktop\\task.sh." + id));
            return Msg.success();
        } catch (IOException e) {
            return Msg.error(Msg.code_file_transfer_error, e.getMessage());
        }
    }
}

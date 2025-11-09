package com.example.lionsforest.domain.group.service;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class LocalUploadService {
    @Value("${upload.dir}") // application.yml에서 설정한 경로
    private String uploadDir;

    public String upload(MultipartFile file, String dirName) {
        try {
            // 1. 파일 원본 이름
            String originalFilename = file.getOriginalFilename();
            // 2. 고유한 파일 이름 생성 (UUID)
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            // 3. 저장할 전체 경로 (예: ./uploads/group-photos/uuid.jpg)
            String savePath = uploadDir + dirName + File.separator + uniqueFileName;

            // 4. 로컬 디렉토리 생성 (없으면)
            File saveDir = new File(uploadDir + dirName);
            if (!saveDir.exists()) {
                saveDir.mkdirs(); // mkdirs()로 중간 경로까지 모두 생성
            }

            // 5. 파일 저장
            file.transferTo(new File(savePath));

            log.info("로컬 파일 저장 성공: {}", savePath);

            // 6. [중요] 웹에서 접근 가능한 URL 반환
            // (예: /uploads/group-photos/uuid.jpg)
            // WebConfig 설정이 필요합니다. (다음 단계 참고)
            return "/uploads/" + dirName + "/" + uniqueFileName;

        } catch (IOException e) {
            log.error("로컬 파일 업로드 실패", e);
            throw new RuntimeException("로컬 파일 업로드 중 오류가 발생했습니다.", e);
        }
    }
}

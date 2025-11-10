package com.example.lionsforest.global.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3UploadService {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * S3에 파일 업로드 (AWS SDK v2)
     * @param multipartFile 업로드할 파일
     * @param dirName S3 버킷 내 디렉토리 경로 (예: "group-photos", "review-photos")
     * @return 업로드된 파일의 S3 URL
     */
    public String upload(MultipartFile multipartFile, String dirName) {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + originalFilename;

        try {
            // PutObjectRequest 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(multipartFile.getContentType())
                    .contentLength(multipartFile.getSize())
                    .build();

            // S3에 업로드
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

            // 업로드된 파일의 S3 URL 생성 및 반환
            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);
            log.info("S3 파일 업로드 성공: {}", fileUrl);

            return fileUrl;

        } catch (S3Exception e) {
            log.error("S3 업로드 실패 (S3Exception): {}", originalFilename, e);
            throw new IllegalArgumentException("S3 파일 업로드에 실패했습니다: " + e.awsErrorDetails().errorMessage());
        } catch (IOException e) {
            log.error("파일 읽기 실패: {}", originalFilename, e);
            throw new IllegalArgumentException("파일 업로드 중 IO 오류가 발생했습니다.");
        }
    }

    /**
     * S3에서 파일 삭제
     * @param fileUrl 삭제할 파일의 S3 URL
     */
    public void delete(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("S3 파일 삭제 성공: {}", fileName);

        } catch (S3Exception e) {
            log.error("S3 파일 삭제 실패 (S3Exception): {}", fileUrl, e);
            throw new IllegalArgumentException("S3 파일 삭제에 실패했습니다: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", fileUrl, e);
            throw new IllegalArgumentException("파일 삭제에 실패했습니다.");
        }
    }

    /**
     * S3 URL에서 파일명(키) 추출
     */
    private String extractFileNameFromUrl(String fileUrl) {
        try {
            // https://bucket-name.s3.region.amazonaws.com/path/to/file.jpg
            String[] parts = fileUrl.split(".com/");
            if (parts.length < 2) {
                throw new IllegalArgumentException("잘못된 S3 URL 형식입니다: " + fileUrl);
            }
            return parts[1];
        } catch (Exception e) {
            log.error("URL 파싱 실패: {}", fileUrl, e);
            throw new IllegalArgumentException("잘못된 S3 URL 형식입니다: " + fileUrl);
        }
    }
}

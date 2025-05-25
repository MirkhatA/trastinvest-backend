package kz.trastinvest.demo.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.public-url}")
    private String publicUrl;

    public String uploadFile(MultipartFile file) {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        try (InputStream is = file.getInputStream()) {

            boolean bucketExists = minioClient.bucketExists(
                    io.minio.BucketExistsArgs.builder().bucket(bucket).build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(
                        io.minio.MakeBucketArgs.builder().bucket(bucket).build()
                );

                String policy = "{\n" +
                        "  \"Version\":\"2012-10-17\",\n" +
                        "  \"Statement\":[\n" +
                        "    {\n" +
                        "      \"Effect\":\"Allow\",\n" +
                        "      \"Principal\":\"*\",\n" +
                        "      \"Action\":[\"s3:GetObject\"],\n" +
                        "      \"Resource\":[\"arn:aws:s3:::" + bucket + "/*\"]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

                minioClient.setBucketPolicy(
                        io.minio.SetBucketPolicyArgs.builder()
                                .bucket(bucket)
                                .config(policy)
                                .build()
                );
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

        } catch (Exception e) {
            log.warn("File upload failed", e);
            throw new RuntimeException("File upload failed", e);
        }

        return publicUrl + "/" + fileName;
    }
}

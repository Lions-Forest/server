package com.example.lionsforest.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    //firebase 계정에 대한 json 파일 경로
    @Value("classpath:firebase-service-account.json")
    private Resource serviceAccountKey;

    @PostConstruct
    public void initializeFirebase(){
        try(InputStream is = serviceAccountKey.getInputStream()){
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(is))
                    .build();

            //이미 초기화되었는지 확인
            if(FirebaseApp.getApps().isEmpty()){
                FirebaseApp.initializeApp(options);
            }
        }catch(IOException e){
            // TODO: 예외처리
            e.printStackTrace();
            }
        }
    }
}

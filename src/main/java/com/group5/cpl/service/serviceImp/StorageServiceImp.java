package com.group5.cpl.service.serviceImp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.group5.cpl.service.StorageService;
@Service
public class StorageServiceImp implements StorageService{
    private final Path rootLocation;
    public StorageServiceImp(){
        this.rootLocation = Paths.get("src/main/resources/static/images");
    }
    @Override
    public void init() {
        try{
            Files.createDirectories(rootLocation);
        }catch(Exception e){

        }
    }

    @Override
    public void store(MultipartFile file) {
        try{
            Path destinationFile = this.rootLocation.resolve(Paths.get(file.getOriginalFilename())).normalize().toAbsolutePath();
            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream,destinationFile,StandardCopyOption.REPLACE_EXISTING);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
}

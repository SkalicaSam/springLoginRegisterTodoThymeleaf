package com.example.registration.service;

import com.example.registration.model.Task;
import com.example.registration.model.File;
import com.example.registration.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FileUploadService {
    private final FileRepository fileRepository;

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public FileUploadService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public void storeFiles(Task task, MultipartFile[] files) {
        for (MultipartFile file : files) {
            try {
//                String fileName = file.getOriginalFilename();
                String originalFileName = file.getOriginalFilename();
                String fileName = resolveUniqueFileName(originalFileName);
                Path targetLocation = this.fileStorageLocation.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation);

                File fileEntity = new File();
                fileEntity.setName(fileName);
                fileEntity.setTask(task);
                fileRepository.save(fileEntity);
            } catch (IOException ex) {
                throw new RuntimeException("Could not store file " + file.getOriginalFilename() + ". Please try again!", ex);
            }
        }
    }

    private String resolveUniqueFileName(String originalFileName) {
        String baseName = getBaseName(originalFileName);
        String extension = getExtension(originalFileName);
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = baseName + "_" + timeStamp + "." + extension;
        Path targetLocation = this.fileStorageLocation.resolve(fileName);

        while (Files.exists(targetLocation)) {
            timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            fileName = baseName + "_" + timeStamp + "." + extension;
            targetLocation = this.fileStorageLocation.resolve(fileName);
        }

        return fileName;
    }

    private String getBaseName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, dotIndex);
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }



}

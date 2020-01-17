package com.evergarden.cms.context.user.application.service;

import com.evergarden.cms.context.user.domain.entity.Avatar;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Map;

@Service
public class AvatarFolderHelper {

    private CRUDAvatarService avatarService;

    public AvatarFolderHelper(CRUDAvatarService avatarService) {
        this.avatarService = avatarService;
    }

    public void createFolder(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public Mono<Void> processAvatarFile(MultiValueMap<String, Part> multiValueMap, String path, String token) {
        Map<String, Part> map = multiValueMap.toSingleValueMap();
        FilePart filepart = (FilePart) map.get("avatar");

        String fileName = filepart.filename().replace(' ', '_');

        Avatar avatar = Avatar.builder()
            .filePath(path + fileName)
            .fileName(fileName)
            .relativeUri("/images/avatar/" + fileName)
            .build();
        return filepart
            .transferTo(new File(avatar.getFilePath()).toPath())
            .then(avatarService.saveAvatarForUser(token, avatar));
    }
}

package com.goMaddy.multithreaded_http_fileserver.specification;

import com.goMaddy.multithreaded_http_fileserver.entity.FileMetadata;
import com.goMaddy.multithreaded_http_fileserver.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class FileSpecification {

    public static Specification<FileMetadata> hasUser(User user) {

        return (root, query, cb) ->
                cb.equal(root.get("user"), user);
    }

    public static Specification<FileMetadata> filenameContains(
            String filename
    ) {

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("originalFilename")),
                        "%" + filename.toLowerCase() + "%"
                );
    }

    public static Specification<FileMetadata> hasContentType(
            String contentType
    ) {

        return (root, query, cb) ->
                cb.equal(root.get("contentType"), contentType);
    }

}
package com.graphResearcher.resources;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

public class TestUtils {

    public static MultipartFile createTestFile(String fileName, String content) {
        return new MockMultipartFile(
                "file",
                fileName,
                MediaType.TEXT_PLAIN_VALUE,
                content.getBytes()
        );
    }
}

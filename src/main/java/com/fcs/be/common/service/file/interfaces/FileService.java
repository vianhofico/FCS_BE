package com.fcs.be.common.service.file.interfaces;

import com.fcs.be.common.service.file.PresignedUrlResult;
import java.time.Duration;

public interface FileService {

    PresignedUrlResult generateUploadUrl(String objectKey, Duration expiry);

    PresignedUrlResult generateDownloadUrl(String objectKey, Duration expiry);
}

package org.lxc.platform.service;

import org.lxc.platform.exception.HttpException;
import org.lxc.platform.model.BaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class VersionService {
    private static final Logger LOG = LoggerFactory.getLogger(VersionService.class);
    private static final String VERSION_HEADER = "If-Match";

    public Long extractVersion(HttpServletRequest req) {
        String versionHeader = req.getHeader(VERSION_HEADER);
        if (versionHeader == null || versionHeader.isEmpty()) {
            LOG.debug("Invalid request: Version header is missing");
            throw new HttpException("Invalid request: Version header is missing", HttpStatus.BAD_REQUEST);
        }

        try {
            versionHeader = versionHeader.replace("\"", "");
            return Long.valueOf(versionHeader);
        } catch (NumberFormatException e) {
            LOG.debug("Invalid request: Version header could not be interpreted as number.", e);
            throw new HttpException(
                    "Invalid request: Version header could not be interpreted as number.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public void validateVersion(BaseModel baseModel, Long version) {
        if (version < baseModel.getVersion()) {
            String msg = String.format(
                    "Provided version of entity with id %d is outdated. Outdated version: %d. Current version: %d",
                    baseModel.getId(), version, baseModel.getVersion());
            LOG.info(msg);
            throw new HttpException(msg, HttpStatus.BAD_REQUEST);
        }

        if (version > baseModel.getVersion()) {
            String msg = String.format(
                    "Provided version of entity with id %d is manipulated (too high). Manipulated version: %d. Current version: %d",
                    baseModel.getId(), version, baseModel.getVersion());
            LOG.info(msg);
            throw new HttpException(msg, HttpStatus.BAD_REQUEST);
        }
    }
}

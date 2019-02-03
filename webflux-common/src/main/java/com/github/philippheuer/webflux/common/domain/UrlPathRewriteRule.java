package com.github.philippheuer.webflux.common.domain;

import lombok.Getter;

/**
 * Url Rewrite Rule
 */
@Getter
public class UrlPathRewriteRule {

    /**
     * Source Path
     */
    private String sourcePath;

    /**
     * Target Path
     */
    private String targetPath;

    /**
     * Constructor
     *
     * @param sourcePath The source pattern
     * @param targetPath The target pattern
     */
    public UrlPathRewriteRule(String sourcePath, String targetPath) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }

}
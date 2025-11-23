package com.palangwi.soup.common.utils;

public class KeywordNormalizer {
    private KeywordNormalizer () {
        throw new AssertionError("Cannot instantiate utility class");
    }
    // TODO : normalize 규정에 대해 고민 후 구현이 필요합니다.
    public static String normalize (String keyword) {
        return keyword.toLowerCase();
    }
}

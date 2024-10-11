package com.springjwt.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify text processing rules for String fields.
 * Applies transformations like uppercase, lowercase, capitalization of words or sentences, and other text processing options.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StrProc {

    /**
     * Enum to define the types of text transformations.
     */
    enum TransformType {
        NONE,                // No transformation applied
        UPPERCASE,           // Convert text to uppercase
        LOWERCASE,           // Convert text to lowercase
        CAPITALIZE_WORDS,   // Capitalize the first letter of every word
        CAPITALIZE_SENTENCES // Capitalize the first letter of every sentence
    }

    /**
     * Specifies the type of text transformation to apply.
     * Default is NONE (no transformation).
     *
     * @return the type of text transformation
     */
    TransformType transform() default TransformType.NONE;

    /**
     * Indicates whether to trim leading and trailing spaces.
     * Default is true.
     *
     * @return true if leading and trailing spaces should be trimmed, false otherwise
     */
    boolean trimSpaces() default true;

    /**
     * Indicates whether to replace multiple spaces between words with a single space.
     * Default is true.
     *
     * @return true if multiple spaces between words should be replaced, false otherwise
     */
    boolean trimMultipleSpaces() default true;

    /**
     * Specifies the maximum length for the string.
     * Default is -1 (no length constraint).
     *
     * @return the maximum length of the string
     */
    int maxLength() default -1;

    /**
     * Specifies the default value to use if the field is null.
     * Default is an empty string.
     *
     * @return the default value for the field
     */
    String defaultValue() default "";
}

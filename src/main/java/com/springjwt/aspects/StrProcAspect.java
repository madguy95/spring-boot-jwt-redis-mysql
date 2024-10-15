package com.springjwt.aspects;


import com.springjwt.annotations.StrProc;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Aspect to process fields annotated with {@link StrProc}.
 * Applies transformations and processing rules as specified by the annotation.
 */
@Slf4j
@Aspect
@Component
public class StrProcAspect {

    /**
     * Pointcut expression to match all methods in the specified package.
     * Currently, Can't aplly to package that contain filter class
     */
    @Pointcut("execution(* com.springjwt.controllers..*(..))")
    public void applicationLayer() {
    }

    /**
     * Advice that runs before the execution of methods matched by the pointcut.
     * Processes fields annotated with {@link StrProc} in method arguments.
     *
     * @param joinPoint the join point providing access to method arguments
     * @throws IllegalAccessException if access to the field is denied
     */
    @Before("applicationLayer()")
    public void applyStringProcessing(JoinPoint joinPoint) throws IllegalAccessException {
        for (Object arg : joinPoint.getArgs()) {
            processFields(arg);
        }
    }

    /**
     * Processes fields of an object that are annotated with {@link StrProc}.
     * Applies text transformations, trimming, and default values based on the annotation settings.
     *
     * @param obj the object to process
     * @throws IllegalAccessException if access to the field is denied
     */
    private void processFields(Object obj) throws IllegalAccessException {
        if (obj == null) return;

        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(StrProc.class) && field.getType().equals(String.class)) {
                field.setAccessible(true);
                String value = (String) field.get(obj);

                StrProc processorAnnotation = field.getAnnotation(StrProc.class);

                // Apply default value if field is null or empty
                if (value == null || value.isEmpty()) {
                    value = processorAnnotation.defaultValue();
                }

                // Process value if not null
                if (value != null) {
                    value = processValue(value, processorAnnotation);
                    field.set(obj, value);
                }
            }
        }
    }

    /**
     * Processes the value of a string field according to the {@link StrProc} annotation settings.
     * Applies text transformation, enforces maximum length, trims spaces, and replaces multiple spaces.
     *
     * @param value               the value of the string field
     * @param processorAnnotation the annotation containing processing rules
     * @return the processed string value
     */
    private String processValue(String value, StrProc processorAnnotation) {
        // Apply text transformation based on annotation settings
        value = applyTextTransformation(value, processorAnnotation.transform());

        // Enforce maximum length if specified
        int maxLength = processorAnnotation.maxLength();
        if (maxLength > 0 && value.length() > maxLength) {
            value = value.substring(0, maxLength);
        }

        // Trim leading and trailing spaces if required
        if (processorAnnotation.trimSpaces()) {
            value = value.trim();
        }

        // Replace multiple spaces between words with a single space if required
        if (processorAnnotation.trimMultipleSpaces()) {
            value = value.replaceAll("\\s+", " ");
        }

        return value;
    }

    /**
     * Applies text transformation based on the specified type.
     *
     * @param value         the string value to transform
     * @param transformType the type of transformation to apply
     * @return the transformed string value
     */
    private String applyTextTransformation(String value, StrProc.TransformType transformType) {
        return switch (transformType) {
            case UPPERCASE -> value.toUpperCase();
            case LOWERCASE -> value.toLowerCase();
            case CAPITALIZE_WORDS -> capitalizeFirstLetters(value);
            case CAPITALIZE_SENTENCES -> capitalizeSentences(value);
            default -> value;
        };
    }

    /**
     * Capitalizes the first letter of each sentence in the input string.
     *
     * @param input the input string to process


     * @return the string with the first letter of each sentence capitalized
     */
    private String capitalizeSentences(String input) {
        String[] sentences = input.split("(?<=[.!?])\\s*");
        StringBuilder result = new StringBuilder();
        for (String sentence : sentences) {
            if (!sentence.isEmpty()) {
                result.append(Character.toUpperCase(sentence.charAt(0)))
                        .append(sentence.substring(1))
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

    /**
     * Capitalizes the first letter of each word in the input string.
     *
     * @param input the input string to process
     * @return the string with the first letter of each word capitalized
     */
    private String capitalizeFirstLetters(String input) {
        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return result.toString().trim();
    }
}
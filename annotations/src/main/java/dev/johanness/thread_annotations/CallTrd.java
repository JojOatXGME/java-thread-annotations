package dev.johanness.thread_annotations;

import java.lang.annotation.*;

/**
 * Declares the threading context in which developers may call the annotated
 * method.
 * <p>
 * The default is {@code "main"} for instance methods, and {@code "*"} for
 * static methods and constructors. The {@link ThreadSafe @ThreadSafe}
 * annotation changes to default to {@code "*"} for all methods.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface CallTrd {
  /**
   * Returns an array of threading contexts which need to be available to call
   * the method.
   *
   * @return An Array of threading contexts which need to be available to call
   * the method.
   */
  String[] value();
}

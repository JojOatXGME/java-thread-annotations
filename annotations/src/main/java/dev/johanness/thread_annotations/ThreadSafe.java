package dev.johanness.thread_annotations;

import java.lang.annotation.*;

/**
 * Declares the type as being thread-safe which means that all methods and
 * fields can be accessed by any thread. The thread safety can be overwritten
 * for each individual method and field by using {@link CallTrd @CallTrd} and
 * {@link Trd @Trd}.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ThreadSafe {
}

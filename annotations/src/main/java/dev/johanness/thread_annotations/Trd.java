package dev.johanness.thread_annotations;

import java.lang.annotation.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Declares the threading context in which developers may use the annotated
 * element. You can specify multiple values to declare that the requirements for
 * all of them must be satisfied. Each value can be one of the following
 * options:
 * <ul>
 *   <li>The asterisk ({@code "*"}) which means that the element can be used
 *       from all threads.
 *   <li>A threading context declared with {@link DeclareTrd}.
 *   <li>A reference to a field which must be used for synchronization. E.g.
 *       {@code "#myLock"}. The type of the field may either implement one of
 *       the interfaces {@link Lock} and {@link ReadWriteLock}, or has any
 *       arbitrary type for the usage with the {@code synchronized} keyword.
 *       Beside normal fields, you can also use {@code "#this"} or
 *       {@code "#class"} to reference to the instance or class respectively.
 *   <li>Any of the options above with a plus sign ({@code +}) to specify that a
 *       read lock is enough. E.g with {@code #myReadWriteLock+}, the element
 *       requires at least a read lock from field {@code myReadWriteLock}.
 *   <li>Two colons ({@code ::}) to refer to the context which calls the method.
 *       Only available in method declarations and definitions.
 *   <li>Any valid values separated with vertical bars ({@code |}) to specify
 *       that only one of them need to be satisfied.
 * </ul>
 * The default value for fields is {@code "main"} for instance fields, and
 * {@code "*"} for static fields. The {@link ThreadSafe @ThreadSafe} annotation
 * changes to default to {@code "*"} for all fields.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE_USE)
public @interface Trd {
  /**
   * Returns an array of threading contexts which need to be available to use
   * the object.
   *
   * @return An Array of threading contexts which need to be available to use
   * the object.
   */
  String[] value();
}

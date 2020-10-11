package dev.johanness.thread_annotations;

import java.lang.annotation.*;

/**
 * Declares a threading context. The value must be an alphanumeric sequence
 * optionally prepend with an exclamation mark ({@code !}). Values with an
 * exclamation mark declare a global name for a thread. Values without an
 * exclamation mark declare a local name. In contrast to global names, local
 * names don't need to match between multiple classes. When one class uses
 * another class, there will be an implicit mapping between these declarations.
 * Note that local names can only be used by constructors and instance members.
 * Declarations and definitions of static members can only use global names.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface DeclareTrd {
  /**
   * Returns the context declared by this annotation.
   *
   * @return The context declared by this annotation.
   */
  String value();
}

package dev.johanness.thread_annotation_processor;

import dev.johanness.thread_annotations.CallTrd;
import dev.johanness.thread_annotations.DeclareTrd;
import dev.johanness.thread_annotations.ThreadSafe;
import dev.johanness.thread_annotations.Trd;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
public final class ThreadAnnotationProcessor extends AbstractProcessor {

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Stream.of(CallTrd.class, DeclareTrd.class, ThreadSafe.class, Trd.class)
        .map(ThreadAnnotationProcessor::toCanonicalNameWithModule)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    return false;
  }

  private static @NotNull String toCanonicalNameWithModule(@NotNull Class<?> clazz) {
    Module module = clazz.getModule();
    if (module.isNamed()) {
      return module.getName() + "/" + clazz.getCanonicalName();
    }
    else {
      return clazz.getCanonicalName();
    }
  }
}

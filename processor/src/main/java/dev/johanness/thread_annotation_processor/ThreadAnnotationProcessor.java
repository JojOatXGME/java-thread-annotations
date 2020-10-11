package dev.johanness.thread_annotation_processor;

import com.google.auto.common.MoreElements;
import com.google.common.collect.Sets;
import dev.johanness.thread_annotation_processor.util.ElementScanner;
import dev.johanness.thread_annotation_processor.util.TypeValidator;
import dev.johanness.thread_annotations.CallTrd;
import dev.johanness.thread_annotations.DeclareTrd;
import dev.johanness.thread_annotations.ThreadSafe;
import dev.johanness.thread_annotations.Trd;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
public final class ThreadAnnotationProcessor extends AbstractProcessor {
  private static final String OPTION_VERIFY_ALL = "dev.johanness.thread_annotations.verify_all";
  private final List<DeferredType> deferredElements = new ArrayList<>();

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    if (isInitialized() && processingEnv.getOptions().containsKey(OPTION_VERIFY_ALL)) {
      return Set.of("*");
    }
    return Stream.of(CallTrd.class, DeclareTrd.class, ThreadSafe.class, Trd.class)
        .map(ThreadAnnotationProcessor::toCanonicalNameWithModule)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public Set<String> getSupportedOptions() {
    return Set.of(OPTION_VERIFY_ALL);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    List<DeferredType> deferredElementsCopy = List.copyOf(deferredElements);
    deferredElements.clear();
    for (DeferredType root : deferredElementsCopy) {
      tryVerifyCode(root.get(processingEnv), roundEnv.processingOver());
    }
    for (Element root : roundEnv.getRootElements()) {
      if (root.getKind().isClass() || root.getKind().isInterface()) {
        validateAnnotations(root);
        tryVerifyCode(MoreElements.asType(root), roundEnv.processingOver());
      }
    }
    return false;
  }

  private void validateAnnotations(@NotNull Element root) {
    root.accept(new ElementScanner<@NotNull Set<String>>() {
      @Override
      public Void visitType(TypeElement e, @NotNull Set<String> declaredThreads) {
        // todo: Check DeclareTrd annotations
        Set<String> declaredThreadsOnType = Set.of();
        return super.visitType(e, Sets.union(declaredThreads, declaredThreadsOnType));
      }

      @Override
      public Void scan(Element e, @NotNull Set<String> strings) {
        // todo: Check all the other annotations
        return super.scan(e, strings);
      }
    }, Set.of());
  }

  private void tryVerifyCode(@NotNull TypeElement root, boolean finalRound) {
    // To avoid duplicate error messages, the processor defers verification
    // until the compiler has resolves all references.
    if (finalRound || TypeValidator.allTypesResolved(root)) {
      verifyCode(root, !finalRound);
    }
    else {
      deferredElements.add(new DeferredType(processingEnv, root));
    }
  }

  private void verifyCode(@NotNull TypeElement root, boolean typesValidated) {
    // todo: Verify code
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

  private static final class DeferredType {
    private final @Nullable Name moduleName;
    private final @NotNull Name className;

    private DeferredType(@NotNull ProcessingEnvironment processingEnv, @NotNull TypeElement element) {
      Elements elements = processingEnv.getElementUtils();
      this.moduleName = elements.getModuleOf(element).getQualifiedName();
      this.className = element.getQualifiedName();
    }

    private @NotNull TypeElement get(@NotNull ProcessingEnvironment processingEnv) {
      Elements elements = processingEnv.getElementUtils();
      ModuleElement module = moduleName == null
          ? null
          : elements.getModuleElement(moduleName);
      return Objects.requireNonNull(module == null
          ? elements.getTypeElement(className)
          : elements.getTypeElement(module, className));
    }
  }
}

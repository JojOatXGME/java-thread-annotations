package dev.johanness.thread_annotation_processor.util;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner9;

public abstract class ElementScanner<P> extends ElementScanner9<Void, P> {
  protected ElementScanner() {
    super(null);
  }

  @Override
  public Void visitType(TypeElement e, P p) {
    // For some reason, ElementScanner9 skips type parameters.
    scan(e.getTypeParameters(), p);
    return super.visitType(e, p);
  }

  @Override
  public Void visitExecutable(ExecutableElement e, P p) {
    // For some reason, ElementScanner9 skips type parameters.
    scan(e.getTypeParameters(), p);
    return super.visitExecutable(e, p);
  }
}

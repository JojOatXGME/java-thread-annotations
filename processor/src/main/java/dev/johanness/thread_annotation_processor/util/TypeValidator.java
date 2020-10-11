package dev.johanness.thread_annotation_processor.util;

import com.google.common.collect.Iterables;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleAnnotationValueVisitor9;
import javax.lang.model.util.SimpleTypeVisitor9;
import java.util.List;

public final class TypeValidator {
  private TypeValidator() {} // This class cannot be instantiated.

  public static boolean allTypesResolved(@NotNull TypeMirror typeMirror) {
    return typeMirror.accept(new SimpleTypeVisitor9<@NotNull Boolean, Void>() {
      @Override
      protected @NotNull Boolean defaultAction(TypeMirror e, Void unused) {
        return allTypesResolvedA(e.getAnnotationMirrors());
      }

      @Override
      public @NotNull Boolean visitIntersection(IntersectionType t, Void unused) {
        return allTypesResolvedT(t.getBounds()) &&
               allTypesResolvedA(t.getAnnotationMirrors());
      }

      @Override
      public @NotNull Boolean visitUnion(UnionType t, Void unused) {
        return allTypesResolvedT(t.getAlternatives()) &&
               allTypesResolvedA(t.getAnnotationMirrors());
      }

      @Override
      public @NotNull Boolean visitArray(ArrayType t, Void unused) {
        return allTypesResolved(t.getComponentType()) &&
               allTypesResolvedA(t.getAnnotationMirrors());
      }

      @Override
      public @NotNull Boolean visitDeclared(DeclaredType t, Void unused) {
        return allTypesResolvedT(t.getTypeArguments()) &&
               allTypesResolvedA(t.getAnnotationMirrors());
      }

      @Override
      public @NotNull Boolean visitTypeVariable(TypeVariable t, Void unused) {
        return allTypesResolved(t.getLowerBound()) &&
               allTypesResolved(t.getUpperBound()) &&
               allTypesResolvedA(t.getAnnotationMirrors());
      }

      @Override
      public @NotNull Boolean visitWildcard(WildcardType t, Void unused) {
        TypeMirror extendsBound = t.getExtendsBound();
        TypeMirror superBound = t.getSuperBound();
        return (extendsBound == null || allTypesResolved(extendsBound)) &&
               (superBound == null || allTypesResolved(superBound)) &&
               allTypesResolvedA(t.getAnnotationMirrors());
      }

      @Override
      public @NotNull Boolean visitExecutable(ExecutableType t, Void unused) {
        return allTypesResolvedT(t.getParameterTypes()) &&
               allTypesResolved(t.getReturnType()) &&
               allTypesResolvedT(t.getTypeVariables()) &&
               allTypesResolvedT(t.getThrownTypes()) &&
               allTypesResolvedA(t.getAnnotationMirrors());
      }

      @Override
      public @NotNull Boolean visitError(ErrorType t, Void unused) {
        return false;
      }
    }, null);
  }

  public static boolean allTypesResolved(@NotNull AnnotationValue annotationValue) {
    return annotationValue.accept(new SimpleAnnotationValueVisitor9<@NotNull Boolean, Void>() {
      @Override
      protected @NotNull Boolean defaultAction(Object o, Void unused) {
        return true;
      }

      @Override
      public @NotNull Boolean visitType(TypeMirror t, Void unused) {
        return allTypesResolved(t);
      }

      @Override
      public @NotNull Boolean visitEnumConstant(VariableElement c, Void unused) {
        return allTypesResolved(c);
      }

      @Override
      public @NotNull Boolean visitAnnotation(AnnotationMirror a, Void unused) {
        return allTypesResolved(a);
      }

      @Override
      public @NotNull Boolean visitArray(List<? extends AnnotationValue> vals, Void unused) {
        return allTypesResolvedV(vals);
      }
    }, null);
  }

  public static boolean allTypesResolved(@NotNull AnnotationMirror annotationMirror) {
    return allTypesResolved(annotationMirror.getAnnotationType()) &&
           allTypesResolvedE(annotationMirror.getElementValues().keySet()) &&
           allTypesResolvedV(annotationMirror.getElementValues().values());
  }

  public static boolean allTypesResolved(@NotNull Element element) {
    boolean[] resultContainer = {true};
    element.accept(new ElementScanner<Void>() {
      @Override
      public Void visitType(TypeElement e, Void unused) {
        if (allTypesResolved(e.asType()) &&
            allTypesResolvedA(e.getAnnotationMirrors()) &&
            allTypesResolved(e.getSuperclass()) &&
            allTypesResolvedT(e.getInterfaces())) {
          return super.visitType(e, unused);
        }
        else {
          resultContainer[0] = false;
          return null;
        }
      }

      @Override
      public Void visitExecutable(ExecutableElement e, Void unused) {
        if (allTypesResolved(e.asType()) &&
            allTypesResolvedA(e.getAnnotationMirrors()) &&
            allTypesResolved(e.getReturnType()) &&
            allTypesResolvedT(e.getThrownTypes())) {
          return super.visitExecutable(e, unused);
        }
        else {
          resultContainer[0] = false;
          return null;
        }
      }

      @Override
      public Void visitVariable(VariableElement e, Void unused) {
        if (allTypesResolved(e.asType()) &&
            allTypesResolvedA(e.getAnnotationMirrors())) {
          return super.visitVariable(e, unused);
        }
        else {
          resultContainer[0] = false;
          return null;
        }
      }

      @Override
      public Void visitTypeParameter(TypeParameterElement e, Void unused) {
        if (allTypesResolved(e.asType()) &&
            allTypesResolvedA(e.getAnnotationMirrors()) &&
            allTypesResolvedT(e.getBounds())) {
          return super.visitTypeParameter(e, unused);
        }
        else {
          resultContainer[0] = false;
          return null;
        }
      }
    }, null);
    return resultContainer[0];
  }

  private static boolean allTypesResolvedT(@NotNull Iterable<? extends TypeMirror> typeMirrors) {
    return Iterables.all(typeMirrors, TypeValidator::allTypesResolved);
  }

  private static boolean allTypesResolvedV(@NotNull Iterable<? extends AnnotationValue> annotationValues) {
    return Iterables.all(annotationValues, TypeValidator::allTypesResolved);
  }

  private static boolean allTypesResolvedA(@NotNull Iterable<? extends AnnotationMirror> annotationMirrors) {
    return Iterables.all(annotationMirrors, TypeValidator::allTypesResolved);
  }

  private static boolean allTypesResolvedE(@NotNull Iterable<? extends Element> elements) {
    return Iterables.all(elements, TypeValidator::allTypesResolved);
  }
}

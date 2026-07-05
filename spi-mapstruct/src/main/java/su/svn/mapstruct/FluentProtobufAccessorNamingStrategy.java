/*
 * This file was last modified at 2026.03.01 01:11 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * FluentProtobufAccessorNamingStrategy.java
 * $Id$
 */

package su.svn.mapstruct;

import org.mapstruct.ap.spi.DefaultAccessorNamingStrategy;
import org.mapstruct.ap.spi.MapStructProcessingEnvironment;
import org.mapstruct.ap.spi.util.IntrospectorUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Victor N. Skurikhin
 */
public class FluentProtobufAccessorNamingStrategy extends DefaultAccessorNamingStrategy {

    public static final String PROTOBUF_MESSAGE_OR_BUILDER = "com.google.protobuf.MessageOrBuilder";
    public static final String LIST_SUFFIX = "List";
    public static final Pattern JAVA_JAVAX_PACKAGE = Pattern.compile("^javax?\\..*");

    protected TypeMirror protobufMessageOrBuilderType;

    @Override
    public void init(MapStructProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        TypeElement typeElement = elementUtils.getTypeElement(PROTOBUF_MESSAGE_OR_BUILDER);
        if (typeElement != null) {
            protobufMessageOrBuilderType = typeElement.asType();
        }
    }

    @Override
    public String getElementName(ExecutableElement adderMethod) {
        var receiver = adderMethod.getEnclosingElement();
        if (receiver != null
                && protobufMessageOrBuilderType != null
                && typeUtils.isAssignable(receiver.asType(), protobufMessageOrBuilderType)) {
            return super.getElementName(adderMethod) + LIST_SUFFIX;
        }
        var methodName = adderMethod.getSimpleName().toString();
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            return super.getElementName(adderMethod);
        }
        if (this.isFluentGetterMethod(adderMethod) || this.isFluentSetter(adderMethod)) {
            return adderMethod.getSimpleName().toString();
        }
        return null;
    }

    @Override
    public boolean isSetterMethod(ExecutableElement method) {
        return (super.isSetterMethod(method) || super.isFluentSetter(method))
                && !isProtoBufMethod(method)
                && !isRemoveMethod(method)
                && !isMergeFrom(method)
                && isNotInitializationError(method);
    }

    @Override
    public boolean isGetterMethod(ExecutableElement method) {
        return (super.isGetterMethod(method) || this.isFluentGetterMethod(method))
                && isNotBuilderListMethod(method)
                && !isGetAllFieldsMethod(method)
                && isNotBuilderListMethod(method)
                && !isValueListMethod(method)
                && isNotInitializationError(method);
    }

    @Override
    public String getPropertyName(ExecutableElement getterOrSetterMethod) {

        var methodName = getterOrSetterMethod.getSimpleName().toString();

        if (this.isFluentSetter(getterOrSetterMethod)) {
            if (methodName.startsWith("set")
                    && methodName.length() > 3
                    && Character.isUpperCase(methodName.charAt(3))) {
                return IntrospectorUtils.decapitalize(methodName.substring(3));
            } else if (this.isFluentGetterMethod(getterOrSetterMethod)) {
                return methodName;
            }
        }
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            return IntrospectorUtils.decapitalize(methodName.substring(3));
        }
        if (methodName.startsWith("is")) {
            return IntrospectorUtils.decapitalize(methodName.substring(2));
        }
        return methodName;
    }

    @Override
    protected boolean isFluentSetter(ExecutableElement method) {
        return method.getParameters().size() == 1
                && method.getReturnType().getKind() == TypeKind.VOID
                && !JAVA_JAVAX_PACKAGE.matcher(method.getEnclosingElement().asType().toString()).matches()
                && !isAdderWithUpperCase4thCharacter(method);
    }

    public boolean isFluentGetterMethod(ExecutableElement method) {
        if (!method.getParameters().isEmpty()) {
            // If the method has parameters it can't be a getter
            return false;
        }
        var methodName = method.getSimpleName().toString();

        return methodName.length() > 1
                && Character.isLowerCase(methodName.charAt(0))
                && method.getReturnType().getKind() != TypeKind.VOID;
    }

    private boolean isAdderWithUpperCase4thCharacter(ExecutableElement method) {
        return isAdderMethod(method) && Character.isUpperCase(method.getSimpleName().toString().charAt(3));
    }

    /* the following methods might be to crude for some use cases, more precise matching might be required */
    private boolean isProtoBufMethod(ExecutableElement method) {
        String argTypeName = method.getParameters().get(0).asType().toString();
        return argTypeName.startsWith("com.google.protobuf");
    }

    private boolean isRemoveMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        String argTypeName = method.getParameters().get(0).asType().toString();
        return methodName.startsWith("remove") && argTypeName.equals("int");
    }

    private boolean isNotBuilderListMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        String returnTypeName = typeUtils.erasure(method.getReturnType()).toString();
        return !methodName.endsWith("BuilderList") || !returnTypeName.equals(List.class.getName());
    }

    private boolean isValueListMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        String returnTypeName = typeUtils.erasure(method.getReturnType()).toString();
        return methodName.endsWith("ValueList") && returnTypeName.equals(List.class.getName());
    }

    private boolean isGetAllFieldsMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        String returnTypeName = typeUtils.erasure(method.getReturnType()).toString();
        return "getAllFields".equals(methodName) && returnTypeName.equals(Map.class.getName());
    }

    private boolean isMergeFrom(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        return "mergeFrom".equals(methodName);
    }

    private boolean isNotInitializationError(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        return !methodName.contains("InitializationError");
    }
}
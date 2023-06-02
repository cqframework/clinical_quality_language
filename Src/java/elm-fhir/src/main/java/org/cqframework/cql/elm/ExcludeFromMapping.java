package org.cqframework.cql.elm;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.mapstruct.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface ExcludeFromMapping {
}

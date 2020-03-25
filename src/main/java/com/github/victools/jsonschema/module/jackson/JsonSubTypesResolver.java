/*
 * Copyright 2020 VicTools.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.victools.jsonschema.module.jackson;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.CustomDefinition;
import com.github.victools.jsonschema.generator.CustomDefinitionProviderV2;
import com.github.victools.jsonschema.generator.SchemaGenerationContext;
import com.github.victools.jsonschema.generator.SchemaKeyword;
import com.github.victools.jsonschema.generator.SubtypeResolver;
import com.github.victools.jsonschema.generator.TypeContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Look-up of subtypes from a {@link JsonSubTypes} annotation.
 */
public class JsonSubTypesResolver implements SubtypeResolver, CustomDefinitionProviderV2 {

    /*
     * Looking-up declared subtypes for encountered supertype in general.
     */
    @Override
    public List<ResolvedType> findSubtypes(ResolvedType declaredType, SchemaGenerationContext context) {
        JsonSubTypes subtypesAnnotation = declaredType.getErasedType().getAnnotation(JsonSubTypes.class);
        return this.resolveSubtypes(declaredType, subtypesAnnotation, context.getTypeContext());
    }

    /*
     * Providing custom schema definition for subtype (assumed to be resolved via one of the two methods above).
     */
    @Override
    public CustomDefinition provideCustomSchemaDefinition(ResolvedType javaType, SchemaGenerationContext context) {
        Class<?> erasedTargetType = javaType.getErasedType();
        JsonTypeInfo typeInfoAnnotation = erasedTargetType.getAnnotation(JsonTypeInfo.class);
        if (typeInfoAnnotation == null || (typeInfoAnnotation.include() != JsonTypeInfo.As.WRAPPER_ARRAY
                && typeInfoAnnotation.include() != JsonTypeInfo.As.WRAPPER_OBJECT)) {
            return null;
        }
        String typeIdentifier;
        switch (typeInfoAnnotation.use()) {
        case NAME:
            JsonTypeName typeNameAnnotation = erasedTargetType.getAnnotation(JsonTypeName.class);
            if (typeNameAnnotation == null) {
                return null;
            }
            typeIdentifier = typeNameAnnotation.value();
            if (typeIdentifier.isEmpty()) {
                typeIdentifier = erasedTargetType.getSimpleName();
            }
            break;
        case CLASS:
            typeIdentifier = erasedTargetType.getName();
            break;
        default:
            return null;
        }
        final ObjectNode definition = context.getGeneratorConfig().createObjectNode();
        switch (typeInfoAnnotation.include()) {
        case WRAPPER_ARRAY:
            definition.put(context.getKeyword(SchemaKeyword.TAG_TYPE), context.getKeyword(SchemaKeyword.TAG_TYPE_ARRAY));
            definition.withArray(context.getKeyword(SchemaKeyword.TAG_ITEMS))
                    .add(typeIdentifier)
                    .add(context.createStandardDefinitionReference(javaType, this));
            break;
        case WRAPPER_OBJECT:
            definition.put(context.getKeyword(SchemaKeyword.TAG_TYPE), context.getKeyword(SchemaKeyword.TAG_TYPE_OBJECT));
            definition.with(context.getKeyword(SchemaKeyword.TAG_PROPERTIES))
                    .set(typeIdentifier, context.createStandardDefinitionReference(javaType, this));
            break;
        default:
            definition.withArray(context.getKeyword(SchemaKeyword.TAG_ALLOF))
                    .add(context.createStandardDefinitionReference(javaType, this))
                    .addObject()
                    .put(context.getKeyword(SchemaKeyword.TAG_TYPE), context.getKeyword(SchemaKeyword.TAG_TYPE_OBJECT))
                    .with(context.getKeyword(SchemaKeyword.TAG_PROPERTIES))
                    .with(typeInfoAnnotation.property())
                    .put(context.getKeyword(SchemaKeyword.TAG_CONST), typeIdentifier);
            break;
        }
        return new CustomDefinition(definition);
    }

    /**
     * Mapping the declared erased types from the annotation to resolved types. Returns {@code null} if annotation is {@code null}/not present.
     *
     * @param declaredType supertype encountered while generating a schema
     * @param subtypesAnnotation annotation to derive applicable subtypes from.
     * @param context type context for proper type resolution
     * @return resolved annotated subtypes (or {@code null} if given annotation is {@code null})
     */
    public List<ResolvedType> resolveSubtypes(ResolvedType declaredType, JsonSubTypes subtypesAnnotation, TypeContext context) {
        if (subtypesAnnotation == null) {
            return null;
        }
        return Stream.of(subtypesAnnotation.value())
                .map(entry -> this.resolveSubtype(declaredType, entry, context))
                .collect(Collectors.toList());
    }

    /**
     * Safe way of resolving an erased subtype from its supertype. If the subtype introduces generic parameters not present on the supertype, the
     * subtype will be resolved without any type parameters – for simplicity's sake not even the ones declared alongside the supertype then.
     *
     * @param declaredType supertype encountered while generating a schema
     * @param annotatedSubtype single subtype declared via {@link JsonSubTypes} on the super class
     * @param context type context for proper type resolution
     * @return resolved annotated subtype
     */
    private ResolvedType resolveSubtype(ResolvedType declaredType, JsonSubTypes.Type annotatedSubtype, TypeContext context) {
        try {
            return context.resolveSubtype(declaredType, annotatedSubtype.value());
        } catch (IllegalArgumentException ex) {
            return context.resolve(annotatedSubtype.value());
        }
    }
}

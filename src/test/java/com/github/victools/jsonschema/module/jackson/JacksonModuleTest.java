/*
 * Copyright 2019 VicTools.
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

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.github.victools.jsonschema.generator.ConfigFunction;
import com.github.victools.jsonschema.generator.FieldScope;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigPart;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Test for the {@link JacksonModule}.
 */
@RunWith(JUnitParamsRunner.class)
public class JacksonModuleTest {

    private SchemaGeneratorConfigBuilder configBuilder;
    private SchemaGeneratorConfigPart<FieldScope> fieldConfigPart;

    @Before
    public void setUp() {
        this.configBuilder = Mockito.mock(SchemaGeneratorConfigBuilder.class);
        this.fieldConfigPart = Mockito.spy(new SchemaGeneratorConfigPart<>());
        Mockito.when(this.configBuilder.forFields()).thenReturn(this.fieldConfigPart);
    }

    @Test
    public void testApplyToConfigBuilder() {
        new JacksonModule().applyToConfigBuilder(this.configBuilder);

        Mockito.verify(this.configBuilder).getObjectMapper();
        Mockito.verify(this.configBuilder).forFields();

        Mockito.verify(this.fieldConfigPart).withDescriptionResolver(Mockito.any());
        Mockito.verify(this.fieldConfigPart).withIgnoreCheck(Mockito.any());
        Mockito.verify(this.fieldConfigPart).withPropertyNameOverrideResolver(Mockito.any());

        Mockito.verifyNoMoreInteractions(this.configBuilder, this.fieldConfigPart);
    }

    Object parametersForTestPropertyNameOverride() {
        return new Object[][]{
            {"unannotatedField", null},
            {"fieldWithEmptyPropertyAnnotation", null},
            {"fieldWithSameValuePropertyAnnotation", null},
            {"fieldWithNameOverride", "field override 1"},
            {"fieldWithNameOverrideOnGetter", "method override 1"},
            {"fieldWithNameOverrideAndOnGetter", "field override 2"}
        };
    }

    @Test
    @Parameters
    public void testPropertyNameOverride(String fieldName, String expectedOverrideValue) throws Exception {
        new JacksonModule().applyToConfigBuilder(this.configBuilder);

        ArgumentCaptor<ConfigFunction<FieldScope, String>> captor = ArgumentCaptor.forClass(ConfigFunction.class);
        Mockito.verify(this.fieldConfigPart).withPropertyNameOverrideResolver(captor.capture());

        FieldScope field = new TestType(TestClassForPropertyNameOverride.class).getMemberField(fieldName);
        String overrideValue = captor.getValue().apply(field);
        Assert.assertEquals(expectedOverrideValue, overrideValue);
    }

    Object parametersForTestDescriptionResolver() {
        return new Object[][]{
            {"unannotatedField", null},
            {"fieldWithDescription", "field description 1"},
            {"fieldWithDescriptionOnGetter", "getter description 1"},
            {"fieldWithDescriptionAndOnGetter", "field description 2"},
            {"fieldWithDescriptionOnType", "class description text"}
        };
    }

    @Test
    @Parameters
    public void testDescriptionResolver(String fieldName, String expectedDescription) throws Exception {
        new JacksonModule().applyToConfigBuilder(this.configBuilder);

        FieldScope field = new TestType(TestClassForDescription.class).getMemberField(fieldName);

        ArgumentCaptor<ConfigFunction<FieldScope, String>> captor = ArgumentCaptor.forClass(ConfigFunction.class);
        Mockito.verify(this.fieldConfigPart).withDescriptionResolver(captor.capture());
        String description = captor.getValue().apply(field);
        Assert.assertEquals(expectedDescription, description);
    }

    private static class TestClassForPropertyNameOverride {

        Integer unannotatedField;
        @JsonProperty
        Double fieldWithEmptyPropertyAnnotation;
        @JsonProperty(value = "fieldWithSameValuePropertyAnnotation")
        Float fieldWithSameValuePropertyAnnotation;
        @JsonProperty(value = "field override 1")
        Long fieldWithNameOverride;
        Boolean fieldWithNameOverrideOnGetter;
        @JsonProperty(value = "field override 2")
        String fieldWithNameOverrideAndOnGetter;

        public Integer getUnannotatedField() {
            return this.unannotatedField;
        }

        @JsonProperty(value = "method override 1")
        public boolean isFieldWithNameOverrideOnGetter() {
            return this.fieldWithNameOverrideOnGetter;
        }

        @JsonProperty(value = "method override 2")
        public String getFieldWithNameOverrideAndOnGetter() {
            return this.fieldWithNameOverrideAndOnGetter;
        }
    }

    @JsonClassDescription(value = "class description text")
    private static class TestClassForDescription {

        Integer unannotatedField;
        @JsonPropertyDescription(value = "field description 1")
        Double fieldWithDescription;
        Float fieldWithDescriptionOnGetter;
        @JsonPropertyDescription(value = "field description 2")
        Long fieldWithDescriptionAndOnGetter;
        TestClassForDescription fieldWithDescriptionOnType;

        @JsonPropertyDescription(value = "getter description 1")
        public Float getFieldWithDescriptionOnGetter() {
            return this.fieldWithDescriptionOnGetter;
        }

        @JsonPropertyDescription(value = "getter description 2")
        public Long getFieldWithDescriptionAndOnGetter() {
            return fieldWithDescriptionAndOnGetter;
        }
    }
}

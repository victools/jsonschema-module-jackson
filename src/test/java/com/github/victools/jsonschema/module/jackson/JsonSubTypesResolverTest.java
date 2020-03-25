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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for the {@link JsonSubTypesResolver}.
 */
@RunWith(JUnitParamsRunner.class)
public class JsonSubTypesResolverTest {

    private final JsonSubTypesResolver instance = new JsonSubTypesResolver();

    @Test
    public void testFindSubtypes() {

    }

    @Test
    public void testProvideCustomSchemaDefinition() {

    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
    @JsonSubTypes({
        @JsonSubTypes.Type(TestSubClass1.class),
        @JsonSubTypes.Type(TestSubClass2.class),
        @JsonSubTypes.Type(TestSubClass3.class)
    })
    private static class TestSuperClassWithNameAsProperty {

    }

    private enum SubClassType {
        SUB_CLASS_1, SUB_CLASS_2, SUB_CLASS_3;
    }

    @JsonTypeName("SUB_CLASS_1")
    private static class TestSubClass1 extends TestSuperClassWithNameAsProperty {

        public String type;
    }

    @JsonTypeName("SUB_CLASS_2")
    private static class TestSubClass2 extends TestSuperClassWithNameAsProperty {

        public SubClassType type;
    }

    @JsonTypeName("SUB_CLASS_3")
    private static class TestSubClass3 extends TestSuperClassWithNameAsProperty {
    }
}

# Java JSON Schema Generation – Module jackson
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.victools/jsonschema-module-jackson/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.victools/jsonschema-module-jackson)

Module for the `jsonschema-generator` – deriving JSON Schema attributes from `jackson` annotations.

## Features
1. Populate "description" as per `@JsonPropertyDescription` (falling-back on `@JsonClassDescription`).
2. Apply alternative field names defined in `@JsonProperty` annotations.
3. Ignore fields that are deemed to be ignored according to various `jackson-annotations` (e.g. `@JsonIgnore`, `@JsonIgnoreType`, `@JsonIgnoreProperties`) or are otherwise supposed to be excluded.

Schema attributes derived from validation annotations on getter methods are also applied to their associated fields.

## Usage
### Dependency (Maven)
```xml
<dependency>
    <groupId>com.github.victools</groupId>
    <artifactId>jsonschema-module-jackson</artifactId>
    <version>3.0.0</version>
</dependency>
```

### Code
#### Passing into SchemaGeneratorConfigBuilder.with(Module)
```java
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
```
```java
JacksonModule module = new JacksonModule();
SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(objectMapper)
    .with(module);
```

#### Complete Example
```java
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
```
```java
ObjectMapper objectMapper = new ObjectMapper();
JacksonModule module = new JacksonModule();
SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(objectMapper, OptionPreset.PLAIN_JSON)
    .with(module);
SchemaGeneratorConfig config = configBuilder.build();
SchemaGenerator generator = new SchemaGenerator(config);
JsonNode jsonSchema = generator.generateSchema(YourClass.class);

System.out.println(jsonSchema.toString());
```

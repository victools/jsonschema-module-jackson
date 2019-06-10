# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [3.0.0] – 2019-06-10
### Added
- Populate "description" as per `@JsonPropertyDescription` (falling-back on `@JsonClassDescription`).
- Apply alternative field names defined in `@JsonProperty` annotations.
- Ignore fields that are deemed to be ignored according to various `jackson-annotations` (e.g. `@JsonIgnore`, `@JsonIgnoreType`, `@JsonIgnoreProperties`) or are otherwise supposed to be excluded.

[Unreleased]: https://github.com/victools/jsonschema-generator/compare/v3.0.0...HEAD
[3.0.0]: https://github.com/victools/jsonschema-generator/releases/tag/v3.0.0

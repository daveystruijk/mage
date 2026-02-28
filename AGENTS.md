# AGENTS.md

Guidance for coding agents working in this repository: safe edits, correct Maven usage, and consistency with existing Java conventions.

## Project Overview
- Repository: XMage (`magefree/mage` style multi-module Maven monorepo)
- Purpose: open source client/server platform for playing Magic: The Gathering online
- Language: Java
- Build tool: Maven (no `mvnw` wrapper committed)
- Root build file: `pom.xml`
- Common modules: `Mage`, `Mage.Common`, `Mage.Server`, `Mage.Client`, `Mage.Sets`, `Mage.Tests`, `Mage.Verify`
- Encoding: UTF-8

## Quick Start Commands
- Compile all modules quickly: `mvn -DskipTests compile`
- Run all tests: `mvn test`
- Run one gameplay test class: `mvn -pl Mage.Tests -am -Dtest=TempleOfPowerTest test`
- Run one gameplay test method: `mvn -pl Mage.Tests -am -Dtest=TempleOfPowerTest#test_TransformRevert test`
- Run one engine unit test: `mvn -pl Mage -am -Dtest=ManaTest test`
- Build artifacts without tests: `mvn install package -DskipTests`

## Java / Environment Baseline
- CI runs on JDK 17 (`.github/workflows/maven.yml`, `.travis.yml`)
- Root POM compiles with `source/target = 1.8`
- Agent rule: build with JDK 17, but keep production code Java 8 compatible unless the repo changes that baseline

## Build Commands
Run from repo root.

- Clean: `mvn clean`
- Full compile/install without tests: `mvn -DskipTests install`
- Build + package without tests: `mvn install package -DskipTests`
- Module-only build with dependencies:
  - `mvn -pl Mage -am -DskipTests package`
  - `mvn -pl Mage.Server -am -DskipTests package`
  - `mvn -pl Mage.Client -am -DskipTests package`

### Packaging Helpers
- Convenience flow: `make build` then `make package`
- `make package` creates/copies client and server zip archives
- Preferred release bundling script noted in repo: `Utils/build-and-package.pl`

## Lint / Static Analysis
There is no dedicated Checkstyle/SpotBugs/PMD/Spotless config in this repository.
Use these as practical lint-equivalent checks:
- Compile checks: `mvn -DskipTests compile`
- Test-based validation: `mvn test`

## Test Commands
### Full test run
- `mvn test`
### Module test runs
- `mvn -pl Mage.Tests -am test`
- `mvn -pl Mage -am test`
- `mvn -pl Mage.Server -am test`
### Run a single test class (important)
- `mvn -pl Mage.Tests -am -Dtest=TempleOfPowerTest test`
- `mvn -pl Mage -am -Dtest=ManaTest test`
### Run a single test method (important)
- `mvn -pl Mage.Tests -am -Dtest=TempleOfPowerTest#test_TransformRevert test`
- `mvn -pl Mage -am -Dtest=ManaTest#shouldCreateManaFromCopy test`
### Test hygiene
- If stale DB files affect local runs, clean with `./clean_dbs.sh`

## Source Layout Notes
- Core engine code: `Mage/src/main/java`
- Card implementations: `Mage.Sets/src/mage/cards/**`
- Gameplay/regression tests: `Mage.Tests/src/test/java/org/mage/test/**`
- Utility scripts/templates: `Utils/`

### Module Purpose (Quick)
- `Mage`: core rules engine, game model, abilities/effects
- `Mage.Sets`: card implementations and set metadata
- `Mage.Server`: server runtime, matchmaking, config loading
- `Mage.Client`: Swing client and UI-related logic
- `Mage.Tests`: gameplay regression and card interaction tests
- `Mage.Verify`: verification/consistency checks for card data

## Coding Style (Follow Existing File First)
- Preserve local style in touched files; do not mass-reformat unrelated code
- Keep diffs minimal and task-focused
- Avoid broad import churn

## Formatting Conventions
- 4-space indentation, no tabs
- K&R brace style (`if (...) {`)
- One top-level public class per file (class name = file name)
- Group class members clearly (fields, constructors, methods)
- Keep UTF-8 text and existing line ending style

## Import Conventions
- Typical order:
  1) package line
  2) non-static imports
  3) blank line
  4) static imports
- Both wildcard and explicit imports exist throughout this codebase
- Preferred rule: match surrounding file style; do not do repository-wide import normalization

## Types and API Usage
- Prefer explicit readable types when that matches nearby code
- Use `final` for locals/parameters when it improves clarity and matches local style
- Be careful with nullability: many engine APIs may return `null`; guard early
- In gameplay hot paths, favor safe guards over exception-driven control flow

## Naming Conventions
- Packages: lowercase
- Types: PascalCase
- Methods/fields: camelCase
- Constants: `static final` UPPER_SNAKE_CASE
- Tests:
  - Class names end in `Test`
  - Card tests usually extend `CardTestPlayerBase`
  - Method naming varies (camelCase and underscore style both exist); follow local pattern

## Card Implementation Conventions
Before implementing or making changes to cards, always consult the cards database:
- `unzip -p Mage.Verify/AtomicCards.json.zip | jq --arg name "<Card Name>" '.data[$name]'`

In `Mage.Sets`, new card classes usually follow this shape:
- `public final class <CardName> extends CardImpl`
- Public constructor `(UUID ownerId, CardSetInfo setInfo)`
- Private copy constructor
- `copy()` override returning `new <CardName>(this)`
- Constructor sets card types/subtypes/PT/abilities
- For newly added card classes, use `@author daveystruijk`

### Card Data Lookup (AtomicCards)
- Recommended lookup (without manually extracting the zip): 

### Comments for Complex Card Logic
- Add concise, reason-focused comments for non-obvious logic (for example: combat reassignment, trigger batching, replacement interactions, layer/dependency edge cases, multiplayer targeting restrictions).
- Prefer comments that capture rules intent (Oracle/Gatherer rulings and relevant CR references) when that intent drives implementation details.
- Explain why a specific implementation structure is required (for example, splitting effects to satisfy rules processing), not just what the code does.
- Keep comments close to the tricky block and avoid repeating obvious card text or restating straightforward code.

Use existing effects, abilities, and watchers when possible instead of creating duplicates.

## Adding New Cards to the Card Pool
- Implement the card class in `Mage.Sets/src/mage/cards/<first-letter>/` using existing card files as templates
- Register the card in its set definition under `Mage.Sets/src/mage/sets/<SetName>.java` with `cards.add(new SetCardInfo(...))`
- Reuse existing engine components first (effects, costs, conditions, watchers, keyword abilities)
- Add or update focused regression tests in `Mage.Tests/src/test/java/org/mage/test/cards/**`
- For bugfixes, prefer adding a failing test first, then implement behavior to make it pass
- Validate with targeted single-test commands before broader module/full-suite runs

## Error Handling and Logging
- Logging commonly uses `org.apache.log4j.Logger`
- Write concise, contextual log messages
- For infra/config failures, throw meaningful runtime exceptions (existing pattern)
- For expected game-state edge cases, prefer guard checks and graceful returns (`false`/no-op)

## Testing Conventions
- Repo supports JUnit 4 and JUnit 5 (Surefire + JUnit platform/vintage)
- AssertJ is used in several modules
- Gameplay/card tests are typically deterministic:
  - setup cards/zones explicitly
  - use strict choice mode when relevant
  - assert concrete battlefield/stack/life/zone outcomes

## Generated Sources and Build Artifacts
- Do not commit `target/` outputs
- Proto classes are generated during `Mage` build (`generate-sources`)
- JAXB config classes are generated during `Mage.Server` build
- Edit source/config inputs, not generated outputs

## Change Scope Discipline
- Do not rename/move files unless required by the task
- Avoid touching unrelated modules for single-card or single-rule fixes
- If you change behavior, add or update a focused test in the nearest relevant module

This `AGENTS.md` should be treated as the in-repo agent guidance.

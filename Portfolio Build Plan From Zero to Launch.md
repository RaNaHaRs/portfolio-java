# Portfolio Build Plan: From Zero to Launch

Version 1.0 · November 11, 2026 · Companion to the [Master PRD](https://claude.ai/artifact/1YeFKapFKKDtG3JU1xBhB1) and the [UI PRD](https://claude.ai/artifact/S6WMBSYX6Y9GWx7ruyLzgM)

## 1. How to use this plan

- Work top to bottom. Every step has an ID, a **needs** list, actions, and a **done when** check. Do not start a step before its needs are done.
- Sizes: S under 2 hours, M half a day, L a day or more. These are rough planning numbers.
- Use one branch per step group (for example `feat/c4-security`). Commit messages start with the step ID (`C4: add security headers`). Merge to `main` only when the build and tests pass.
- This plan implements the Master PRD. Requirement IDs (FR-...) come from it, and visual details come from the UI PRD. Where this plan changes a PRD decision, it is listed in section 13.
- Versions checked on November 11, 2026: Spring Boot 4.1.x is the current stable line, and 4.2.0 is at release-candidate stage and expected this month. Use a final release only. Never pick a SNAPSHOT, milestone or RC. Use Java 21 LTS. springdoc 3.x is the line for Boot 4.

## 2. IDE decision

- Use either one. Maven is the source of truth, so both open the same folder.
- **Recommended split:** IntelliJ IDEA for the Java side (refactoring, debugging, run configurations, Spring support) and your VS Code-based editor (Antigravity) for templates, CSS and JavaScript if you prefer your current Emmet and syntax setup.
- IntelliJ note: some Spring and Thymeleaf features have been Ultimate-only. Check the current free edition’s feature list before relying on them. This project does not require them.
- If you use only the VS Code-based editor, install the extensions in step A5. Antigravity installs extensions from the Open VSX registry rather than Microsoft’s marketplace. If an extension ID is not found, search by name or install its VSIX file.
- Your existing editor preferences stay as they are. The settings file in section 5 contains only Java, Maven, XML and YAML keys. Merge it into your settings, do not overwrite them.
- You can also generate the project inside the editor: Command Palette, then Spring Initializr: Create a Maven Project (from the Spring Boot extension pack). The selections in step B1 are identical.

## 3. Phase A: Machine setup (no code yet)

**A1. Install JDK 21** (S, needs: none)

- Windows: `winget install EclipseAdoptium.Temurin.21.JDK`. macOS: `brew install --cask temurin@21`. Linux: install SDKMAN, then `sdk install java` and pick the latest 21.x Temurin.
- Open a new terminal. **Done when:** `java -version` and `javac -version` both report 21, and `JAVA_HOME` points to the JDK folder (PowerShell: `$env:JAVA_HOME`). Set it in the system environment variables if empty.

**A2. Install Git** (S, needs: none)

- `winget install Git.Git`, then `git config --global user.name "Your Name"`, `git config --global user.email "you@example.com"`, `git config --global init.defaultBranch main`.
- **Done when:** `git --version` works.

**A3. Install Docker Desktop** (M, needs: none)

- `winget install Docker.DockerDesktop`. Enable WSL 2 if asked, restart, start Docker Desktop.
- Docker runs local PostgreSQL, a mail catcher, and the Testcontainers tests. **Done when:** `docker version` and `docker compose version` both work.

**A4. Install the IDEs** (S, needs: none)

- `winget install JetBrains.IntelliJIDEA.Community` (or download from jetbrains.com) and make sure your VS Code-based editor is installed. **Done when:** both open.

**A5. Editor extensions, VS Code-based editor** (S, needs: A4)

- Required: `vscjava.vscode-java-pack` (Extension Pack for Java), `vmware.vscode-boot-dev-pack` (Spring Boot Extension Pack).
- Recommended: `ms-azuretools.vscode-docker`, `redhat.vscode-yaml`, `editorconfig.editorconfig`, `eamodio.gitlens`, `sonarsource.sonarlint-vscode`, `usernamehw.errorlens`.
- Optional: `cweijan.vscode-database-client2` (browse PostgreSQL), `humao.rest-client` (call endpoints from `.http` files).
- If the Java extension cannot find the JDK, set `java.jdt.ls.java.home` in your user settings to the JDK folder. **Done when:** a Java file shows syntax highlighting and a Run button.

**A6. Create the GitHub repository** (S, needs: none)

- Create an empty repository, suggested name `portfolio-java`, with no README or .gitignore. Keep the old `Portfolio` repository untouched until launch. **Done when:** you have the clone URL.

**A7. Start the open decisions in parallel** (needs: none, must finish before F3)

- Choose the host, the SMTP provider and the domain (Master PRD §17). Collect the photo, real screenshots and approvals (UI PRD §16). None of this blocks Phases B to E.

## 4. Phase B: Bootstrap (needs A1 to A6)

**B1. Generate the project** (S)

- Open https://start.spring.io and choose: Project **Maven**, Language **Java**, Spring Boot **the latest final release (4.1.x or a final 4.2.x)**, Group `com.harshrana`, Artifact `portfolio`, Name `portfolio`, Package name `com.harshrana.portfolio`, Packaging **Jar**, Java **21**.
- Dependencies, searched by these exact names: **Spring Web**, **Thymeleaf**, **Spring Security**, **Validation**, **Spring Data JPA**, **PostgreSQL Driver**, **Flyway Migration**, **Spring Boot Actuator**, **Spring Boot DevTools**, **Java Mail Sender**, **Docker Compose Support**, **Testcontainers**.
- Click Generate and unzip. **Done when:** you have a folder with `pom.xml`, `mvnw` and `src`.

**B2. Put it in your repository and open it** (S, needs: B1)

- `git clone <your repo url>`, copy the generated files into the clone, and open the folder in IntelliJ (trust the project, wait for Maven import, set Project SDK to 21).
- **Done when:** the Maven panel shows no errors.

**B3. Add the configuration files** (S, needs: B2)

- Paste each file from section 5: `.gitattributes`, `.editorconfig`, the `.gitignore` additions, `compose.yaml`, `application.yml` (delete `application.properties`), the Flyway migration, and the two `.vscode` files.
- **Done when:** the files exist at the paths shown in section 5.

**B4. Add the extra dependencies** (S, needs: B2)

- Paste the dependency and plugin blocks from section 5 into `pom.xml`, then reload Maven. **Done when:** the reload finishes without errors.

**B5. First run** (S, needs: B3, B4, A3)

- Start Docker Desktop, then run `./mvnw spring-boot:run` (Windows cmd: `mvnw spring-boot:run`). Docker Compose support starts PostgreSQL and the mail catcher for you.
- Expected: the app starts, and http://localhost:8080 shows a login page. That is Spring Security’s default and is fixed in step C4.
- **Done when:** the log shows `Started PortfolioApplication` and `docker ps` lists `postgres` and `mailpit`.

**B6. First tests** (S, needs: B5)

- Run `./mvnw test`. **Done when:** the generated context test passes (it needs Docker).

**B7. First commit** (S, needs: B6)

- `git add .`, `git commit -m "B7: initial Spring Boot skeleton"`, `git push -u origin main`.
- **Done when:** GitHub shows the files and no `.env` file or secret is in the repository.

## 5. Paste-ready files

**.gitattributes** (repository root). This keeps `mvnw` on LF so Linux CI can run it.

```
* text=auto eol=lf
*.cmd text eol=crlf
*.bat text eol=crlf
*.png binary
*.jpg binary
*.webp binary
*.woff2 binary
*.pdf binary
```

**.editorconfig** (repository root)

```
root = true

[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true
indent_style = space
indent_size = 4

[*.{html,css,js,json,yml,yaml,xml,md,sql}]
indent_size = 2

[*.md]
trim_trailing_whitespace = false
```

**.gitignore** (append to the generated file)

```
.env
.env.*
*.pem
/private/
.vscode/*
!.vscode/settings.json
!.vscode/extensions.json
```

**compose.yaml** (repository root, used only for local development)

```
services:
  postgres:
    image: postgres:17-alpine
    environment:
      POSTGRES_DB: portfolio
      POSTGRES_USER: portfolio
      POSTGRES_PASSWORD: portfolio
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  mailpit:
    image: axllent/mailpit
    ports:
      - "1025:1025"
      - "8025:8025"

volumes:
  pgdata:
```

Mailpit shows every email the app sends at http://localhost:8025, so you can test the contact flow without a real SMTP account.

**src/main/resources/application.yml** (replaces `application.properties`). Leave the `spring.config.import` list commented until step C6 creates those files.

```
spring:
  application:
    name: portfolio
  # Uncomment in step C6, once these files exist:
  # config:
  #   import:
  #     - classpath:content/profile.yml
  #     - classpath:content/home.yml
  #     - classpath:content/projects.yml
  #     - classpath:content/experience.yml
  #     - classpath:content/skills.yml
  #     - classpath:content/timeline.yml
  #     - classpath:content/certifications.yml
  #     - classpath:content/seo.yml
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
  mail:
    host: ${MAIL_HOST:localhost}
    port: ${MAIL_PORT:1025}
    username: ${MAIL_USERNAME:}
    password: ${MAIL_PASSWORD:}
  mvc:
    problemdetails:
      enabled: true
  web:
    resources:
      cache:
        cachecontrol:
          max-age: 365d
          cache-public: true
      chain:
        strategy:
          content:
            enabled: true
            paths: /**

server:
  compression:
    enabled: true
  shutdown: graceful
  forward-headers-strategy: framework
  error:
    whitelabel:
      enabled: false

management:
  endpoints:
    web:
      exposure:
        include: health
  endpoint:
    health:
      probes:
        enabled: true

portfolio:
  site:
    base-url: ${SITE_BASE_URL:http://localhost:8080}
  contact:
    enabled: ${CONTACT_FORM_ENABLED:true}
    recipient: ${CONTACT_RECIPIENT:me@example.com}
    ip-hash-salt: ${IP_HASH_SALT:dev-only-change-me}
```

**src/main/resources/db/migration/V1\_\_create\_contact\_message.sql**

```
CREATE TABLE contact_message (
  id         UUID PRIMARY KEY,
  name       VARCHAR(100)  NOT NULL,
  email      VARCHAR(254)  NOT NULL,
  subject    VARCHAR(160)  NOT NULL,
  message    VARCHAR(2000) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  ip_hash    VARCHAR(64),
  notified   BOOLEAN NOT NULL DEFAULT FALSE,
  status     VARCHAR(10) NOT NULL DEFAULT 'NEW'
);

CREATE INDEX idx_contact_message_created_at ON contact_message (created_at);
```

**pom.xml additions.** Paste these inside the existing `<dependencies>` block.

```
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>3.0.3</version>
</dependency>
<dependency>
  <groupId>org.commonmark</groupId>
  <artifactId>commonmark</artifactId>
  <version>0.24.0</version>
</dependency>
<dependency>
  <groupId>com.github.ben-manes.caffeine</groupId>
  <artifactId>caffeine</artifactId>
</dependency>
```

If Maven says the Caffeine version is missing, add the latest 3.x version number. For springdoc and commonmark, newer patch versions are fine: check Maven Central and keep springdoc on the 3.x line. Then add the `build-info` goal to the `spring-boot-maven-plugin` entry that already exists in `<build><plugins>` (do not add a second entry):

```
<plugin>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-maven-plugin</artifactId>
  <executions>
    <execution>
      <goals>
        <goal>build-info</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

**.vscode/settings.json** (merge into your settings, do not replace them)

```
{
  "java.configuration.updateBuildConfiguration": "automatic",
  "maven.executable.preferMavenWrapper": true,
  "files.exclude": { "**/target": true },
  "[yaml]": { "editor.defaultFormatter": "redhat.vscode-yaml", "editor.tabSize": 2 },
  "[xml]": { "editor.tabSize": 2 }
}
```

**.vscode/extensions.json**

```
{
  "recommendations": [
    "vscjava.vscode-java-pack",
    "vmware.vscode-boot-dev-pack",
    "redhat.vscode-yaml",
    "ms-azuretools.vscode-docker",
    "editorconfig.editorconfig",
    "eamodio.gitlens",
    "sonarsource.sonarlint-vscode",
    "usernamehw.errorlens"
  ]
}
```

**Dockerfile** (repository root, used from step F1)

```
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline
COPY src src
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 appuser
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV SPRING_DOCKER_COMPOSE_ENABLED=false
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70", "-XX:+UseSerialGC", "-jar", "app.jar"]
```

Tests are skipped in the image build because they run in CI before the image is built (Testcontainers cannot run inside a Docker build).

## 6. Phase C: Foundation (needs B7)

**C1. Package skeleton** (S). Create the packages `web`, `api`, `content`, `contact`, `status`, `seo`, `config` under `com.harshrana.portfolio` (Master PRD §6). **Done when:** the project compiles.

**C2. Vendor and font files** (S, needs: C1). Download into `src/main/resources/static/`: Inter weights 400, 500, 600, 700 and JetBrains Mono 400, 500 as WOFF2 files into `fonts/` (from fontsource or Google Fonts); the latest htmx 2.x as `js/vendor/htmx.min.js`; a custom highlight.js build with Java, JSON, SQL, YAML, Bash and XML as `js/vendor/highlight.min.js`. All lowercase kebab-case names. **Done when:** `http://localhost:8080/fonts/...` returns the file (after C4).

**C3. Design tokens and base CSS** (M, needs: C1). Create `static/css/base.css` with every token from UI PRD §4 (both themes, type scale, spacing, radius, focus ring, motion tokens, reduced-motion rules) and the base styles for buttons, chips, cards, callouts and code blocks (UI PRD §5). **Done when:** a test page shows both themes and all text passes 4.5:1 contrast.

**C4. Security configuration** (M, needs: C1). In `config`, define the filter chain: all public routes allowed, CSRF on, security headers (CSP with a per-request nonce, HSTS for production, nosniff, Referrer-Policy, Permissions-Policy), `/actuator/health` open and every other actuator path denied (Master PRD §11). **Done when:** `/` returns 200 with no login and `curl -I` shows the headers.

**C5. Base layout and fragments** (L, needs: C3, C4). Create `templates/layout/base.html` (head with SEO model, theme script with nonce so there is no flash, skip link, header, footer shell) and one fragment per UI PRD §5 component. Link assets with `th:href="@{...}"` so versioned URLs work. **Done when:** an empty page renders the header and footer, and the theme toggle works without a flash.

**C6. Content loader** (L, needs: C1). Uncomment `spring.config.import` in `application.yml`. Create the files under `src/main/resources/content/` with the real data from Master PRD §3 and the field lists in Master PRD §8. Bind each file to a Java record with `@ConfigurationProperties` and `@Validated`, so a missing required field stops the app at startup. Projects live in one `projects.yml` list, and each project’s case study is a Markdown file `content/projects/{slug}.md` (decision D10). **Done when:** the app starts with the content, and a deliberately broken file makes startup fail with a clear message.

**C7. Error handling** (S, needs: C5). API errors use problem details. HTML routes use the IDE-style 404 and 500 pages (UI PRD §7.7) with correct status codes. **Done when:** an unknown route returns 404 and renders the page.

## 7. Phase D: Plain pages and API (needs C5, C6)

Build each page without effects, with real content, following the page card in Master PRD §5 and the UI PRD section shown.

**D1. Projects** (L, needs: C6). List and case-study pages with Markdown rendering through commonmark, a generated table of contents and highlight.js blocks (FR-PROJ-1, FR-PROJ-3; UI PRD §7.5). Write Rentify first: it is the flagship and sets the template. **Done when:** all three projects render and a hidden project appears nowhere.

**D2. Home** (M, needs: D1). Hero, proof strip, the journey as a static list of cards, featured projects, closing band (FR-HOME-1 to 3; UI PRD §7.1).

**D3. About** (M, needs: C5, C6). Story, timeline as an ordered list, education, certifications, motto (FR-ABOUT; UI PRD §7.2).

**D4. Skills** (M, needs: C6). Server-rendered list grouped by level with where each skill is used (FR-SKILLS-1; UI PRD §7.3). Node positions for the later graph are stored in `skills.yml`.

**D5. Experience** (M, needs: C6). Internship card and the static blueprint SVGs, desktop and mobile (FR-EXP; UI PRD §7.4).

**D6. Contact cards** (S, needs: C5). Email, LinkedIn, GitHub, resume. The form comes in Phase E.

**D7. JSON API and status** (M, needs: C6, D1). Build every endpoint in Master PRD §9 with springdoc annotations. The status service reads uptime from the JVM runtime bean, memory from the runtime, request count from the Micrometer `http.server.requests` timer, and build data from build-info. **Done when:** `/swagger-ui` lists the endpoints and `/api/v1/status` returns real values.

**D8. SEO and resume** (M, needs: D1 to D6). Per-page meta, canonical, Open Graph, JSON-LD, `sitemap.xml`, `robots.txt`, and the `/resume` endpoint (UI PRD §10).

**D9. Page tests** (M, needs: D1 to D8). MockMvc tests for status code, title, one H1, canonical and JSON-LD on every page, plus the content and API contract tests (Master PRD §13).

## 8. Phase E: Contact flow (needs C4, C5, D6)

**E1. Entity and repository** (S). `ContactMessage` mapped to the V1 table, with a Spring Data repository.

**E2. Rate limiter** (S). A small in-memory limiter built on Caffeine (5 per hour for `/contact`, 60 per minute for `/api/**`), with unit tests (decision D11).

**E3. Service** (M, needs: E1, E2). Validate, check the honeypot, hash the client address with the salt, and persist.

**E4. Controller and view** (M, needs: E3). POST with post-redirect-get for normal requests and a result fragment for htmx requests. CSRF token in the form. Behavior per UI PRD §7.6.

**E5. Mail** (M, needs: E3). Asynchronous notification through Spring Mail. A mail failure keeps the message and sets `notified=false`.

**E6. Retention job** (S, needs: E1). A scheduled job deletes messages older than 90 days.

**E7. Tests and manual check** (M, needs: E4, E5). Automated tests from Master PRD §13 and Master PRD §10, then send a real message and read it in Mailpit at http://localhost:8025. **Done when:** every contact test passes and the email arrives in Mailpit.

## 9. Phase F: Quality gate, container and staging (needs D9, E7)

**F1. Container** (S). Add the Dockerfile from section 5 and build it with `docker build -t portfolio .`. **Done when:** the container starts and serves the site.

**F2. CI/CD workflow** (M, needs: F1). Send me the repository link, `pom.xml`, the Dockerfile, your chosen host and the environment variable names (names only, never values). I will write the complete workflow. It must run `./mvnw -B verify`, build the image, and deploy from `main` after a green run (Master PRD §14).

**F3. Host, database, SMTP, staging deploy** (L, needs: A7, F2). Create the services, set the environment variables from Master PRD §14 in the host, deploy, and measure the cold-start time after idle. **Done when:** the staging URL serves HTTPS and a contact message arrives by email.

**F4. Milestone M2 gate** (M, needs: F3). All tests green, manual keyboard and mobile pass, Lighthouse check, and every applicable item in UI PRD §15 passes without effects.

## 10. Phase G: Signature effects (needs F4)

One effect at a time, in this order. Each ships with its fallback, its mobile version and its reduced-motion state, and stays inside the JavaScript budget (UI PRD §4.3.1, §10).

- **G1.** Home request journey (L; UI PRD §7.1): `pages/home.js`, `page-home.css`.
- **G2.** Projects API playground (L; UI PRD §7.5): real `fetch` to `/api/v1/projects/{slug}`.
- **G3.** About metro map (L; UI PRD §7.2).
- **G4.** Skills constellation (L; UI PRD §7.3), with hand-tuned node positions (decision D12).
- **G5.** Experience blueprint draw-in (M; UI PRD §7.4).

## 11. Phase H: Launch (needs G5)

- **H1.** Footer live status strip (M; UI PRD §5.1).
- **H2.** Playwright smoke tests and Lighthouse CI in the pipeline (M).
- **H3.** Performance, accessibility and SEO audit against UI PRD §9 and §10 (M).
- **H4.** Content freeze: Harsh confirms every fact (Master PRD §17) (S).
- **H5.** Production deploy, custom domain, HTTPS (M).
- **H6.** Test the contact form on the live site (S).
- **H7.** Replace the old GitHub Pages site with a redirect page to the new URL (S).
- **H8.** Update the resume, LinkedIn and GitHub profile links (S).
- **H9.** Final README: architecture, local run, how to add a project, measured cold start, Lighthouse scores (M).

## 12. Dependency map and critical path

- Order: A, B, C, D, E, F, G, H. Inside phases, follow the **needs** lists.
- Critical path: B7, then C4, C5 and C6, then D1, D2, D7, D9, then E, then F3. Everything after F4 is polish on a working site.
- Parallel work: assets and approvals from A7 can happen at any time. A missing screenshot never blocks a step, because the element is simply left out until it exists.
- Cannot start early: F3 needs the host decision, and H5 needs the domain.

## 13. Decisions made in this plan

- **D10.** Content binds through `@ConfigurationProperties` and `spring.config.import`. Projects are one `projects.yml` list plus one Markdown file per project. This replaces the one-YAML-per-project layout in Master PRD §8, and it avoids an extra YAML library.
- **D11.** Rate limiting uses a small Caffeine-based limiter instead of Bucket4j, which means fewer dependencies. This replaces the Bucket4j mention in Master PRD §7.
- **D12.** The constellation uses hand-tuned node positions from `skills.yml`. d3-force is used only if that proves too laborious.
- **D13.** Spring Boot 4.1.x (final release), Java 21, springdoc 3.0.x, Maven wrapper.
- **D14.** Two-IDE workflow: IntelliJ for Java, the VS Code-based editor for front-end files.

## 14. Troubleshooting

- **`java -version` shows an old version:** fix the order of PATH entries and JAVA\_HOME, then open a new terminal.
- **Port 8080 is busy:** stop the other program, or run with `--server.port=8081`.
- **App fails at startup with a database error:** start Docker Desktop first.
- **Flyway says `Unsupported Database: PostgreSQL`:** add `org.flywaydb:flyway-database-postgresql` to `pom.xml`.
- **Every page shows a login form:** that is Spring Security’s default until step C4.
- **`Config data resource ... does not exist`:** a file listed in `spring.config.import` has not been created yet.
- **The host gives `DATABASE_URL` as `postgres://...`:** convert it to `jdbc:postgresql://host:5432/dbname` and set the user and password separately.
- **`mvnw` fails on Linux CI (`bad interpreter` or `Permission denied`):** check `.gitattributes`, then run `git update-index --chmod=+x mvnw`.
- **A template or asset is missing on Linux only:** a file name differs in case. Use lowercase kebab-case and reference names exactly.
- **Flyway checksum mismatch:** never edit an applied migration. Add a new version. In local development only, `docker compose down -v` resets the database.
- **The browser shows old CSS:** link assets with `th:href="@{/css/base.css}"` so the versioned URL is used.
- **The production app starts a Docker Compose lookup:** set `SPRING_DOCKER_COMPOSE_ENABLED=false` (the Dockerfile already does).

## 15. Handoff checklist for the CI/CD workflow

Send, in one message: the repository link; `pom.xml`; the Dockerfile; the host you chose; the environment variable names the app reads; and whether the tests currently pass. Never include passwords, keys or `.env` files.

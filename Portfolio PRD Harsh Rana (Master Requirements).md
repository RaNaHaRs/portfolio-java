# Portfolio PRD: Harsh Rana (Master Requirements)

Version 1.0 · November 11, 2026 · Audience: developers and AI agents who will build the site

## 0. How to use this document

This is the master PRD for the portfolio website of Harsh Rana. It defines what to build, with which technology, where each technology is used, and how to verify the result. An agent with no other context should be able to build the site from it.

**Companion document:** [Portfolio UI PRD](https://claude.ai/artifact/S6WMBSYX6Y9GWx7ruyLzgM). It is the single source of truth for visual design and interaction: color tokens, typography, spacing, motion, components and the signature effect on each page. This PRD cites it as UI PRD §n (for example UI PRD §7.1 is the Home page). Do not copy UI details into code from memory; read the cited section.

**Precedence:** (1) the UI PRD for anything visual or interaction-related; (2) this PRD for everything else (scope, architecture, data, API, security, delivery); (3) if the two conflict on a technical point, this PRD wins and the conflict is recorded in the Decision log (section 18).

**Conventions:** MUST, SHOULD and MAY carry their RFC 2119 meaning. Requirement IDs such as FR-HOME-1 are stable, so cite them in commits and tests. Never invent content. If a fact is missing from section 3 or the content files, leave the element out and add it to section 17. When you decide something this document leaves open, add it to the Decision log.

## 1. Product overview

**Product:** the personal portfolio of Harsh Rana, a Java backend developer (B.Tech in Information Technology, graduating 2027, with a placement at Hyperlink InfoSystem).

**Why rebuild:** the current site is a client-rendered React single-page app. It does not show Java skills, crawlers and link previews see almost nothing, its build fails on case-sensitive systems, and its project pages are thin on proof.

**Goals**

- G1. A visitor understands the role, the placement and the best proof of work within 30 seconds.
- G2. The site itself is evidence of Java backend skill: a tested Spring Boot application with CI, API docs and a live status strip.
- G3. Every claim is verifiable (live link, repository, certificate).
- G4. Each page has one memorable interaction that never blocks the content.
- G5. The site is fast, accessible and search-friendly.
- G6. Content changes need no code changes.

**Non-goals for v1:** admin panel or CMS, blog, user accounts, AI chatbot, terminal-style “Harsh OS”, multiple languages, comments, third-party analytics or trackers, and any unreleased project (SecureGate stays hidden until Harsh marks it ready).

**Success measures:** Lighthouse 90 or above in every category, LCP under 2.5 seconds on a mid-range phone, zero broken links, the contact flow working in production, and every fact matching the resume.

## 2. Scope of v1

Six pages (Home, About, Skills, Experience, Projects, Contact), one case-study page per project, error pages, a read-only JSON API, a contact flow, a live status endpoint, SEO assets, tests, CI/CD and deployment.

## 3. Content source of truth

These facts seed the content files. Items marked (confirm) need Harsh’s approval before launch (UI PRD §16).

- **Identity:** Harsh Rana, Java Backend Developer. B.Tech in Information Technology, A.D. Patel Institute of Technology, CVM University, graduating 2027. CGPA 7.82 (display controlled by a flag). Anand, Gujarat, India.
- **Placement:** Hyperlink InfoSystem (confirm exact wording).
- **Links:** GitHub https://github.com/RaNaHaRs/, LinkedIn https://www.linkedin.com/in/harsh-rana-ba792a296/, email address as in the existing site data.
- **Internship:** Web Development Intern, Selkey Cybersecurity Pvt. Ltd., Anand, Gujarat, May to June 2026. Stack: React.js, Node.js, Express.js, PostgreSQL. Work: responsive UI modules, REST API integration, inventory and case management modules, dynamic field configuration, user administration, debugging and cross-browser testing. Recognition: internship completion certificate and appreciation letter (confirm which files may be public).
- **Project 1, Rentify:** full-stack rental marketplace. Java, Spring Boot, Thymeleaf, MySQL, Bootstrap, JPA and Hibernate, cloud image storage, deployed on Render. Live https://rentify-render-g9w0.onrender.com/. Repository https://github.com/RaNaHaRs/Rentify-render. Flagship project.
- **Project 2, RecoverLab CRM:** production multi-tenant CRM for a data recovery business. React.js, Node.js, Express.js, PostgreSQL, JWT, Socket.IO, role-based access, 70+ database migrations, deployed with aaPanel, Nginx and SSL. Live https://datarescuecrm.com/home. No public repository. It is an internship contribution, never a solo project, and every page that shows it MUST say so.
- **Project 3, MovieSearch Recommendation Engine:** Spring Boot backend plus a Python Flask recommendation service. Repository https://github.com/RaNaHaRs/Movie-recommendation-system. No live demo yet.
- **Other:** Smart India Hackathon 2024 and 2025 (participant); IT support volunteer at the International Conference STSTD-2025; certifications GeeksforGeeks Java, GeeksforGeeks Git, Programming Vacation Batch.
- **Skills:** Java, JavaScript, SQL, Python, C++; Spring Boot, Spring MVC, Hibernate, JPA, REST APIs, Node.js, Express.js, JWT; React.js, HTML5, CSS3, Bootstrap, Thymeleaf; PostgreSQL, MySQL, MongoDB; Git, GitHub, Maven, Postman, IntelliJ IDEA, VS Code; learning system design, backend architecture and distributed systems. Levels per UI PRD §7.3 (confirm).
- **Migration sources:** the existing repository’s `src/data/*.js` files and the Knowledge Base v2.0 PDF.

## 4. Information architecture and routes

- `/` Home. `/about`. `/skills`. `/experience`. `/projects`. `/projects/{slug}`. `/contact` (GET shows the page, POST submits the form). `/resume` streams the resume PDF.
- `/api/v1/**` read-only JSON API (section 9). `/swagger-ui` API docs. `/actuator/health` health probe.
- `/sitemap.xml`, `/robots.txt`. A custom page for 404 and 500 (UI PRD §7.7). Unknown slugs and routes MUST return a real 404 status.
- Navigation labels and order: Home, About, Skills, Experience, Projects, Contact (UI PRD §5).

## 5. Page build cards

Each card lists what to wire together. Visual and interaction detail lives in the cited UI PRD section.

### FR-HOME. Home `/` (UI PRD §7.1)

- Controller `HomeController`, view `pages/home`, CSS `page-home.css`, module `pages/home.js` (request journey).
- Data: profile, proof items, journey stops (all from content files) and the first three projects by order.
- FR-HOME-1: render hero, proof strip, journey, featured projects and closing band server-side.
- FR-HOME-2: journey cards are plain links and fully visible without JavaScript.
- FR-HOME-3: proof items contain only verifiable facts. Do not claim “3 deployed projects” until all three have a live link.

### FR-ABOUT. About `/about` (UI PRD §7.2)

- View `pages/about`, module `pages/about.js` (metro map). Data: story, timeline, education, certifications, motto.
- FR-ABOUT-1: the timeline renders as an ordered list server-side, and JavaScript upgrades it to the map.
- FR-ABOUT-2: link a certificate only if its content entry has `public: true`.

### FR-SKILLS. Skills `/skills` (UI PRD §7.3)

- View `pages/skills`, module `pages/skills.js`, with d3-force lazy-loaded only here.
- Data: skill categories and levels plus the project-to-technology relations, embedded in the page as a JSON block (`script type=application/json`), so no extra request is needed.
- FR-SKILLS-1: the list view is the server-rendered default. The graph is an enhancement.
- FR-SKILLS-2: every graph node has a keyboard-focusable equivalent in the list.

### FR-EXP. Experience `/experience` (UI PRD §7.4)

- View `pages/experience`, module `pages/experience.js` (draw-in). Static SVGs `static/svg/recoverlab-blueprint.svg` and `recoverlab-blueprint-mobile.svg` are inlined by a fragment.
- FR-EXP-1: parts Harsh built are marked Contributed, everything else Platform context. Harsh confirms each marking.

### FR-PROJ. Projects list and case studies `/projects`, `/projects/{slug}` (UI PRD §7.5)

- Views `pages/projects` and `pages/project-detail`, modules `pages/projects.js` (playground) and `pages/project-detail.js` (table-of-contents scrollspy, copy buttons).
- Data: one YAML file and one Markdown file per project (section 8).
- FR-PROJ-1: order by the `order` field. Show only projects with status `LIVE` (or `IN_PROGRESS` when `showInProgress` is true). `HIDDEN` projects appear nowhere: not in pages, API or sitemap.
- FR-PROJ-2: the playground calls the real API with `fetch`, measures time with `performance.now()`, and never mocks a response.
- FR-PROJ-3: case-study Markdown is rendered at startup with commonmark-java, the table of contents is generated from the headings, and code blocks are highlighted (Decision D8).

### FR-CONTACT. Contact `/contact` (UI PRD §7.6)

- Controller `ContactController`, view `pages/contact`, module `pages/contact.js` (status-line effect only). The form is behind `contact.form.enabled`.
- The full flow is in section 10.

### FR-GLOBAL. Layout, footer status strip and errors (UI PRD §5, §5.1, §7.7)

- One `layout/base.html` provides head, SEO meta, header, footer and skip link. A per-request CSP nonce is applied to the one inline theme script.
- The footer strip calls `/api/v1/status` every 15 seconds from `core.js`. On failure it shows “Status unavailable”. It never shows invented numbers.
- The error controller renders the IDE-style page with the correct status code.

## 6. Architecture

**Shape:** a single Spring Boot application, server-rendered, serving HTML pages, a small JSON API and static assets. One PostgreSQL database (contact messages only). No separate frontend build.

**Request flow:** browser to host proxy (TLS) to Spring Boot. Security filters (headers, CSRF, rate limiting) run first, then controllers, services, content store or repository, then Thymeleaf or JSON serialization.

**Packages** (base `com.harshrana.portfolio`, adjust to the chosen group id)

- `web`: page controllers returning views. `api`: REST controllers and DTO records.
- `content`: loaders, validators and immutable content records. `contact`: entity, repository, service, mail sender, retention job.
- `status`: status service. `seo`: sitemap and meta factory. `config`: security, caching, web and rate-limit configuration.
- Rules: controller to service to repository only. Constructor injection only. DTOs and content models are Java records. No business logic in controllers or templates.

**Resource layout**

- `templates/layout/base.html`, `templates/pages/*.html`, `templates/fragments/*.html` (one fragment per UI PRD §5 component).
- `static/css/base.css` and `page-*.css`, `static/js/core.js` and `static/js/pages/*.js`, `static/svg`, `static/img`, `static/fonts`.
- `content/` for YAML and Markdown content, `db/migration/` for Flyway scripts.
- All file and path names use lowercase kebab-case and are referenced with exact case. CI runs on Linux, which is case-sensitive.

## 7. Tech stack and where each piece is used

- **Java:** an LTS release (21 or newer). Everywhere in the backend.
- **Spring Boot** (current stable release when the build starts; pin all versions) with **Maven**. The application and the build.
- **Spring MVC and Thymeleaf:** all page rendering. Layout via `th:replace` fragments. Use `th:text`, never `th:utext` for dynamic data.
- **HTMX:** only the contact form submit and result swap (and optional project filtering). Self-hosted file, no CDN.
- **Vanilla ES modules and inline SVG:** the signature effects (UI PRD §7). No React, Vue, Angular or jQuery.
- **d3-force:** the Skills graph only, lazy-loaded, about 30 KB gzipped at most.
- **highlight.js** (custom build with Java, JSON, SQL, YAML, Bash): case-study pages only.
- **CSS:** hand-written with the custom properties from UI PRD §4 (Decision D6). No Node toolchain.
- **Spring Data JPA and Hibernate, PostgreSQL, Flyway:** contact messages and migrations. H2 or Testcontainers PostgreSQL for tests.
- **Bean Validation:** form and content validation.
- **Spring Security:** CSRF, security headers, request-scoped CSP nonce. Public read-only access, no login in v1.
- **Bucket4j** (in-memory): rate limiting on `/contact` and `/api/**`.
- **Spring Mail:** the notification email (SMTP provider still to be chosen, section 17).
- **springdoc-openapi:** Swagger UI at `/swagger-ui`.
- **Spring Boot Actuator and Micrometer:** health, uptime, request counts, JVM memory for the status endpoint. Only `/actuator/health` is public.
- **Caffeine:** in-memory cache for parsed content and rendered fragments. Hashed asset file names with long cache headers through Spring resource versioning.
- **commonmark-java:** Markdown to HTML for case studies.
- **JUnit 5, Mockito, Spring Boot Test, MockMvc:** tests. **Playwright** for end-to-end smoke tests and **Lighthouse CI** for budgets (Phase 3).
- **Docker** (multi-stage), **GitHub Actions**, **Dependabot**.

## 8. Data and content model

**Content files** in `src/main/resources/content/`, loaded and validated at startup. A missing required field MUST stop the application from starting.

- `profile.yml`: name, role, tagline, statement, location, email, links, resume path, optional photo, education, career goal, motto, flags (`showCgpa`, `contact.form.enabled`).
- `home.yml`: proof items and journey stops (label, teaser, target route, status chip).
- `projects/{slug}.yml` plus `projects/{slug}.md`: slug, order, status (`LIVE`, `IN_PROGRESS`, `HIDDEN`), title, category, type note, summary, stack, links (live, repository), screenshots (src, alt, width, height), highlights, API surface (method, path, purpose), architecture and ER diagram file names. The Markdown holds Problem, What I built, Key decisions, Result, What I’d improve.
- `experience.yml`, `skills.yml` (category, skill, level, used-in project slugs), `timeline.yml`, `certifications.yml` (title, issuer, year, `public`, file), `seo.yml` (defaults and per-page title, description, Open Graph image).

**Database** (Flyway migration V1): table `contact_message` with id (UUID), name, email, subject, message, created\_at, ip\_hash (salted hash, never the raw address), notified (boolean), status (`NEW`, `READ`). A scheduled job deletes messages older than 90 days.

## 9. HTTP API

Versioned, read-only, same-origin. Errors use RFC 7807 problem details. Fields are camelCase.

- `GET /api/v1/profile`, `/api/v1/projects`, `/api/v1/projects/{slug}`, `/api/v1/skills`, `/api/v1/experience`. Hidden projects never appear. Unknown slug returns 404.
- `GET /api/v1/status` returns `uptimeSeconds`, `requestsServed`, `jvm.usedMb`, `jvm.maxMb`, `build.version`, `build.commit`, `build.time`. The CI badge is an image loaded from GitHub, not a server call.
- `GET /resume`, `GET /sitemap.xml`, `GET /robots.txt`.
- Rate limits: `/api/**` 60 requests per minute per client; 429 with a `Retry-After` header. No CORS.
- Every endpoint is documented with springdoc, and the Swagger page is linked from the footer.

## 10. Contact flow (end to end)

1. `GET /contact` renders the cards and, if enabled, the form with a CSRF token and a hidden honeypot field.
2. `POST /contact` validates: name up to 100, email up to 254 and well-formed, subject up to 160 with no line breaks, message up to 2000. A filled honeypot is treated as spam: respond as if successful and store nothing.
3. Rate limit: 5 submissions per hour per client. Exceeding returns 429 with `Retry-After`.
4. On success: persist the message first, then send the notification email asynchronously. A mail failure MUST NOT lose the message. It sets `notified=false` and logs the error.
5. Responses: an HTMX request gets a result fragment (`201 Created` panel). A normal form post redirects to `/contact?sent` (post-redirect-get), so the flow works with JavaScript off. Validation errors return 400 with field messages. Success is shown only after the server confirms.
6. The page tells the visitor how the data is used: to reply, kept at most 90 days.

UI behavior for each state is in UI PRD §7.6.

## 11. Security and privacy

- Headers: a strict CSP using a per-request nonce (default-src, script-src, style-src, font-src and connect-src limited to self; img-src self, data and https://github.com for the CI badge; frame-ancestors none), HSTS in production, `X-Content-Type-Options: nosniff`, a restrictive Referrer-Policy and Permissions-Policy.
- Output encoding by default. User input is never rendered as HTML. Prevent header injection in email subjects.
- Secrets only in environment variables, never in the repository. `.env` files and personal documents (marksheets and similar) MUST be gitignored.
- Dependabot for Maven and GitHub Actions. Keep dependencies current.
- No third-party trackers. Only the CSRF session cookie is set, so no cookie banner is needed. If analytics is wanted later, choose a privacy-friendly option and revisit this section.
- Logs never contain message bodies or email addresses.

## 12. Performance, SEO and accessibility

- Performance budgets, SEO requirements (titles, meta, canonical, Open Graph, JSON-LD Person) and accessibility rules are defined in UI PRD §9 and §10 and are mandatory.
- Server targets: pages respond in under 200 ms at p95 when warm. Compression on. Static assets cached for a year with hashed names.
- The sitemap is generated from the routes and the visible projects. Open Graph images are static files named in `seo.yml`.
- The first request after idle depends on the host (section 14). Record the measured cold-start time in the README.

## 13. Testing and quality gates

- **Unit and slice tests:** content loader and validator, status service, rate limiter, contact service.
- **MockMvc tests:** every page returns 200 with the right title, one H1, canonical and JSON-LD; unknown routes return 404; hidden projects absent from pages, API and sitemap.
- **API contract tests** for each endpoint’s JSON shape and for 404 and 429.
- **Contact tests:** valid post, each validation failure, honeypot, missing CSRF, rate limit, mail failure keeps the message.
- **Content test:** every YAML file parses, required fields exist, internal links resolve.
- **Migration test** against PostgreSQL (Testcontainers).
- **Phase 3:** Playwright smoke tests for each page and the playground, an automated accessibility scan, and Lighthouse CI budgets from UI PRD §10.
- **Gate:** the pipeline fails on any failing test. Target at least 70 percent line coverage on `web`, `api`, `content` and `contact`.

## 14. Build, CI/CD and deployment

- **Dockerfile:** multi-stage (Maven build, then a slim JRE image), non-root user, container-aware JVM flags. Tune and measure memory for the chosen host.
- **CI (GitHub Actions):** on every push and pull request: set up Java, run `mvn -B verify`, build the image. On `main` after green: deploy through the host’s auto-deploy or a deploy hook stored as a repository secret. The workflow file is written once the repository exists. Requirements here define what it must do.
- **Host (decision pending, section 17):** it MUST support Java containers or jars, HTTPS, environment variables and a managed or attached PostgreSQL. Check the current free-tier terms: sleeping apps, memory limits and database expiry. A visitor must never face a long blank wait. If the chosen tier sleeps, add a static snapshot fallback (Decision D9).
- **Environment variable names** (values live only in the host and GitHub secrets): `SPRING_PROFILES_ACTIVE`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `CONTACT_RECIPIENT`, `SITE_BASE_URL`, `IP_HASH_SALT`, `CONTACT_FORM_ENABLED`.
- **Monitoring:** an external uptime check on `/actuator/health` where the host’s policy allows it.
- **Launch tasks:** point the custom domain with HTTPS, replace the old GitHub Pages site with a redirect page to the new URL, and update the resume, LinkedIn and GitHub profile links.

## 15. Delivery plan

Phases match UI PRD §13. A milestone is done only when its checks pass.

- **M0. Decisions and inputs:** host, database provider, SMTP provider, domain, assets (section 17).
- **M1. Foundation:** repository, project skeleton, CI, layout, design tokens, content loader with validation, base tests.
- **M2. Plain site:** all pages with real content and no effects, case-study template, JSON API, contact flow, SEO assets, error pages, staging deploy.
- **M3. Signature effects**, in this order: Home journey, Projects playground, About metro map, Skills constellation, Experience blueprint. Each ships with its fallback and mobile version.
- **M4. Launch:** footer status strip, performance and accessibility audit, Playwright and Lighthouse gates, production deploy, redirect from the old site.

## 16. Risks

- **Cold starts on free hosting** make the first visit slow. Mitigation: choose the host deliberately, measure, keep a static fallback.
- **Free database limits or expiry.** Mitigation: verify terms, export data, keep the contact flow resilient.
- **Email deliverability.** Mitigation: persist first, use a transactional SMTP provider, monitor `notified=false`.
- **Effects eating the schedule.** Mitigation: M2 ships a complete site before any effect work.
- **Content errors.** Mitigation: section 3 is the single source, and Harsh approves wording before launch.
- **Memory limits on small hosts.** Mitigation: tune JVM flags and keep caches small.

## 17. Open decisions and required inputs

- Host and plan, database provider, SMTP provider, domain name.
- A real photo (optional), Open Graph images, real screenshots for Rentify and MovieSearch (a RecoverLab screenshot exists), a MovieSearch live demo link or confirmation that none exists.
- Confirmation of skill levels, hero and placement wording, the Experience blueprint markings, and which certificates are public (UI PRD §16).
- Confirmation that SecureGate stays hidden until Harsh marks it ready.

## 18. Decision log

- D1. Server-rendered multi-page site, not an SPA.
- D2. Java LTS with Spring Boot and Maven, so the site is itself a Java project.
- D3. Content in YAML and Markdown files, not a database. The database holds contact messages only.
- D4. PostgreSQL with Flyway.
- D5. HTMX limited to the contact flow. Signature effects use vanilla JavaScript and SVG.
- D6. Hand-written CSS using the UI PRD tokens. The UI PRD allows Tailwind, but this PRD chooses no Node toolchain.
- D7. Rate limiting is in-memory (Bucket4j), which is enough for one instance.
- D8. Code highlighting uses a small highlight.js build on case-study pages only. This replaces the “highlight at render time” wording in UI PRD §5 item 7. Update the UI PRD when convenient.
- D9. If the host sleeps idle apps, ship a static HTML snapshot fallback, generated in CI from the running app.

## 19. Definition of done

- All acceptance items in UI PRD §15 pass, plus every requirement ID in section 5 has a test or a documented manual check.
- The pipeline is green, the site is deployed over HTTPS, and the contact form is tested on the live site.
- The README explains the architecture, how to run locally, how to add a project through content files, the measured cold-start time and the Lighthouse scores.
- The old site redirects to the new one, and no placeholder content or secret remains in the repository.

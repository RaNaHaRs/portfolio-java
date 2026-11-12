# Portfolio UI PRD: Harsh Rana

Version 1.0 · November 11, 2026 · Audience: the developers and agents who will build the site

## 1. Purpose and scope

This document defines how the portfolio website must look and behave: pages, components, colors, typography, spacing, motion, responsive rules, accessibility and content rules. It is a UI document only. Backend design is out of scope except for the endpoints the UI depends on (section 12).

Read it fully before building. Where something is missing, choose the simplest option that fits the principles in section 2 and record the decision.

**Product:** a professional portfolio for Harsh Rana, a Java backend developer (B.Tech in Information Technology, graduating 2027). The interface behaves like a backend system, and its content is verifiable.

**Visitors:** recruiters and hiring engineers (they skim for 30 to 60 seconds), engineering managers (they look for depth), and peers.

**Goals**

- Within 30 seconds a visitor knows the role, the placement and the best proof of work.
- Every claim can be checked (live link, repository, certificate).
- The site is memorable and fun to explore, and the fun never blocks the content.

## 2. Design principles

1. Content first, effects second. Every page must work with JavaScript off and animation off. Effects only enhance.
2. One signature idea per page. Everything around it stays calm.
3. Real or nothing. No placeholders, fake metrics or stock art. If an asset is missing, leave the element out.
4. Backend personality. The interface borrows the language of requests, responses, status codes and diagrams.
5. Fast and accessible by default.
6. One system. Use only the tokens and components defined here.

## 3. Technical constraints that affect the UI

- Server-rendered multi-page site on Spring Boot and Thymeleaf. Not an SPA. Every page is its own URL and uses one shared layout template. Components are Thymeleaf fragments.
- Interactivity: HTMX for small server-driven updates, and vanilla JavaScript modules plus inline SVG for the signature effects. No React, Vue, Angular or jQuery.
- Styling: CSS custom properties for every token in section 4, written with the Tailwind standalone CLI or hand-written CSS. Files: `base.css` plus one `page-*.css` per page.
- JavaScript: one small `core.js` on every page (theme, navigation, motion preference) and one module per page that needs it. A page never loads another page’s script. d3-force is allowed only on the Skills page and must be lazy-loaded.
- Fonts: Inter and JetBrains Mono, self-hosted WOFF2, `font-display: swap`, with system fallbacks.
- Images: WebP or AVIF with `srcset`, explicit width and height, lazy-loaded below the fold.
- All copy comes from content files (YAML or JSON) read by a ContentService. No copy is hardcoded in templates.

## 4. Visual system

### 4.1 Color tokens

**Dark theme (default)**

- `--bg` `#0B0D10`: page background
- `--surface` `#12151A`: cards and panels
- `--surface-2` `#181C22`: hover and raised surfaces
- `--border` `#232932`
- `--text` `#E6E8EB`
- `--text-muted` `#9AA3AF`
- `--accent` `#F59E0B`: Java amber. Primary buttons, links, active navigation, key numbers.
- `--accent-ink` `#0B0D10`: text on an amber fill
- `--signal` `#22D3EE`: cyan. Reserved for anything live or data in motion: the request packet, live dots, playground responses, graph highlights, focus rings.
- `--ok` `#34D399`: 200 and 201 statuses, success
- `--warn` `#FBBF24` and `--error` `#F87171`

**Light theme**

- `--bg` `#FAFAF9`, `--surface` `#FFFFFF`, `--surface-2` `#F3F4F6`, `--border` `#E5E7EB`
- `--text` `#111827`, `--text-muted` `#4B5563`
- `--accent` `#B45309` for text and links, `--accent-fill` `#F59E0B` for button fills, `--accent-ink` `#111827`
- `--signal` `#0E7490`, `--ok` `#047857`, `--error` `#B91C1C`

**Rules**

- Accent covers about 5 percent of any screen. No gradient text anywhere.
- One soft background glow is allowed in the Home hero: a radial gradient of `--accent` at 6 percent opacity. Glows are otherwise allowed only on signal elements, for example `box-shadow: 0 0 12px` of `--signal` at 60 percent.
- All body text must meet 4.5:1 contrast and large text 3:1. Verify both themes.
- Theme follows `prefers-color-scheme`, with a toggle in the header that persists the choice. An inline script in the head sets `data-theme` before first paint so there is no flash.

**Blueprint tokens (Experience page only, the panel stays dark in both themes)**

- `--bp-bg` `#0A1A2F`, `--bp-grid` `#14304F`, `--bp-line` `#38BDF8`, `--bp-note` `#BAE6FD`, `--bp-mark` `#F59E0B`

### 4.2 Typography

- Sans: Inter (400, 500, 600, 700). Mono: JetBrains Mono (400, 500).
- Mono is used for the name wordmark, eyebrow labels, chips, code, status lines and proof-strip numbers.
- Scale: Display `clamp(2.5rem, 6vw, 4rem)` at line height 1.05, weight 700, tracking -0.02em. H1 `clamp(2rem, 4vw, 2.75rem)`. H2 `1.75rem`. H3 `1.25rem`. Body `1.0625rem` at 1.65. Small `0.875rem`. Label (mono) `0.8125rem` with 0.06em tracking.
- Maximum line length for body copy is 68 characters.

### 4.3 Spacing, layout and shape

- 4px base unit. Scale: 4, 8, 12, 16, 24, 32, 48, 64, 96.
- Container max width 1100px with 24px side padding (16px under 480px). 12 columns on desktop, 4 on mobile.
- Section padding: 96px desktop, 64px mobile.
- Breakpoints: 480, 768, 1024, 1280. Build mobile-first and test at 360px.
- Radius: 12px for cards and panels, 8px for buttons and inputs, 999px for chips. Borders are 1px `--border`. Light-theme shadow: `0 1px 2px` and `0 8px 24px` of the text color at 6 percent. Dark theme relies on borders, not shadows.
- Focus ring: 2px `--signal` outline with 2px offset, always visible.

### 4.3.1 Motion tokens

- Durations: `--t-fast` 150ms, `--t-base` 250ms, `--t-slow` 600ms. Signature animations may run longer but must be scroll-driven or user-triggered.
- Easing: `cubic-bezier(0.2, 0.8, 0.2, 1)` for entrances.
- Animate only `transform`, `opacity` and SVG `stroke-dashoffset`. Nothing that shifts layout.
- Reduced motion (`prefers-reduced-motion: reduce` or the site’s own Reduce motion toggle): turn off all signature animation and show the final static state. No looping animation anywhere except the live-dot pulse, which also stops.

## 5. Global components

Each is a Thymeleaf fragment and looks identical on every page.

1. **Header:** wordmark `harsh.rana` (mono) on the left. Links: Home, About, Skills, Experience, Projects, Contact. Right side: Resume button (secondary), theme toggle. Sticky, 64px tall, `--bg` at 85 percent opacity with a 1px bottom border. The current page uses `aria-current=page` and an accent underline. Under 1024px it collapses into a full-screen sheet with a focus trap and Esc to close.
2. **Footer and live status strip:** see 5.1.
3. **Buttons:** primary (amber fill, ink text), secondary (1px border, text color), ghost (text only). 44px minimum height, 8px radius, hover lifts 2px over 150ms, visible focus ring, disabled and loading states.
4. **Chips:** mono 13px, 999px radius, `--surface-2` fill, 1px border. Maximum 5 per card.
5. **Cards:** `--surface`, 1px border, 12px radius, 24px padding. Hover raises border to `--accent` at 40 percent over 150ms.
6. **Browser frame:** a 12px-radius frame with a 32px top bar (three dots and a URL label in mono) around a real screenshot. Never wraps a mockup.
7. **Code block:** mono, `--surface-2`, 12px radius, horizontal scroll inside the block, copy button, syntax colors from the tokens (keywords `--accent`, strings `--ok`, comments `--text-muted`). Highlight at render time, not in the browser.
8. **Callout:** 4px left border in `--accent`, `--surface` fill. Used for “What I’d improve”.
9. **Status line:** mono text shaped like an HTTP status, for example `200 OK · 38 ms`, colored with `--ok` or `--error`.
10. **Section header:** mono eyebrow label, H2, optional one-sentence intro.
11. **Skip link:** first focusable element, reveals on focus.
12. **Motion toggle:** a small “Reduce motion” control in the footer, persisted, honoring the OS setting by default.
13. **Messages:** inline field errors and a polite live region for form and playground results.

### 5.1 Footer and live status strip

- A strip above the footer shows live numbers from `GET /api/v1/status`: uptime, requests served, JVM memory (used of max), short build commit (links to the repository commit), and CI status.
- A `--signal` dot with the word LIVE pulses beside it.
- Poll every 15 seconds. Numbers tween over 400ms. If the request fails, show “Status unavailable” in muted text. Never show invented or stale numbers without a label.
- Below the strip: “Built with Spring Boot and Thymeleaf”, View source, API docs (Swagger), copyright, and the Reduce motion toggle.

## 6. Pages and routes

- `/` Home. Signature: request journey.
- `/about` About. Signature: metro-map timeline.
- `/skills` Skills. Signature: constellation graph.
- `/experience` Experience. Signature: blueprint diagram.
- `/projects` and `/projects/{slug}` Projects. Signature: API playground.
- `/contact` Contact. Signature: the 200 OK ending.
- `/resume` serves the resume PDF.
- A 404 and 500 page styled like an IDE.

## 7. Page specifications

### 7.1 Home `/`

**Purpose:** a 30-second pitch and a guided entry to every page.

**Sections, in order**

1. **Hero.** Two columns from 1024px (text about 7 columns, snapshot card about 5), stacked on mobile with text first.
   - Eyebrow (mono): Java Backend Developer
   - H1 (display): Harsh Rana
   - Statement, two lines at most: builds REST APIs and database-backed applications with Spring Boot, has contributed to a production CRM, and has a placement at Hyperlink InfoSystem. Final wording is approved by Harsh.
   - Buttons: primary View projects (to `/projects`), secondary Download resume.
   - Snapshot card: mono rows for Role, Stack (Java, Spring Boot, MySQL, PostgreSQL), Location (Anand, Gujarat, India) and Status (a signal dot plus the placement). If a real photo is supplied it sits at the top of the card at 4:5 with 12px radius. Without a photo, omit it. Never show an initials or icon placeholder, and never show a fake code window.
2. **Proof strip.** Four items in a row (two by two on mobile). Each has a mono value and a short label, and links where possible: the Hyperlink InfoSystem placement; RecoverLab CRM as a live production platform (links to the live site); Smart India Hackathon 2024 and 2025; B.Tech IT, expected 2027. Only verifiable facts. Do not write “3 deployed projects” until all three have a live link.
3. **Request journey (signature).** A vertical SVG path runs down the center on desktop and along the left edge on mobile. The hero is the starting point, labeled Browser. Five stops follow, each a card with a mono label, a one-line teaser and an arrow link:
   - Filter: About, “Who is behind the API”
   - Controller: Skills, “What I route work through”
   - Service: Experience, “Where the real work happened”
   - Repository: Projects, “Where the data lives”
   - 200 OK: Contact, “The response”
4. **Featured projects.** Three compact project cards linking to their case studies.
5. **Closing band.** A short line and a `mailto:` button, plus a link to Contact.

**Journey effect**

- A small packet (8px circle, `--signal`, soft glow) travels the path, tied to scroll position using IntersectionObserver or CSS scroll-driven animation. The stroke draws progressively with `stroke-dashoffset`.
- When the packet reaches a stop, the card slides in (translateY 16px to 0, 600ms) and a status chip ticks on in `--ok`: PASS, ROUTED, QUERY OK, 200 OK.
- Total length is about two to three screen heights on desktop and less on mobile. No scroll hijacking and no pinning.
- Fallback (no JS or reduced motion): a static line with all five cards visible and no packet. The cards are always clickable and keyboard reachable.

**Acceptance:** role, placement and projects are visible within two screens. LCP under 2.5 seconds.

### 7.2 About `/about`

**Purpose:** the story and the credibility.

**Sections**

1. **Header and story.** Three to four lines from the content file: a programming vacation batch after Class 12, a first full-stack application, hackathons, an internship on a production CRM, and the backend focus.
2. **Metro-map timeline (signature).**
   - Horizontal on 1024px and wider, vertical below. One continuous line in `--accent`. Stations alternate above and below the line on desktop.
   - Stations, from the timeline content: 2023 programming vacation batch; 2024 Java and web development, Rentify built and deployed; 2024 Smart India Hackathon; 2025 backend growth and Smart India Hackathon 2025; May to June 2026 internship at Selkey Cybersecurity (RecoverLab CRM); 2026 MovieSearch; 2026 placement at Hyperlink InfoSystem (current, with a `--signal` ring); 2027 graduation (upcoming, dashed line).
   - A 20px train marker moves along the line as the section scrolls into view. The map is not pinned. Stations are buttons: click or Enter expands a card with a description and tags (`aria-expanded`).
   - Fallback: a plain vertical list of the same stations.
3. **Education and facts card.** Degree, institute (A.D. Patel Institute of Technology, CVM University), expected graduation 2027, location, career goal. CGPA display is controlled by a content flag.
4. **Certifications and activities.** Compact cards: GeeksforGeeks Java, GeeksforGeeks Git, Programming Vacation Batch certificate, internship completion certificate and appreciation letter, Smart India Hackathon 2024 and 2025 participation, IT support volunteer at the International Conference STSTD-2025. Link a certificate only when Harsh has approved it for public display.
5. **Motto.** “Curiosity started my coding journey. Building real-world software keeps it moving forward.”

### 7.3 Skills `/skills`

**Purpose:** show skills together with the evidence for them.

**Constellation (signature)**

- Header, one intro sentence, and a Graph view / List view toggle. List view is the default under 768px and for reduced motion.
- Nodes: technologies and projects. Edges connect each technology to the projects that use it, from the content file: Rentify (Java, Spring Boot, Thymeleaf, MySQL, Bootstrap, JPA and Hibernate), RecoverLab (React, Node.js, Express.js, PostgreSQL, JWT, Socket.IO), MovieSearch (Java, Spring Boot, Python, Flask, JavaScript).
- Projects are larger nodes in the middle band with an accent ring. Technologies orbit them. Group by category with shape plus label, never by color alone. Maximum 18 nodes in the graph. Tools such as Git, Maven and Postman appear only in the list view.
- Layout: hand-tuned positions per breakpoint, or a light d3-force settle (600ms once on load). No idle drift.
- Hover or focus a node: connected nodes and edges turn `--signal`, everything else fades to 25 percent opacity over 150ms. Click a technology: a side panel (desktop) or bottom sheet (mobile) lists where it was used and the level. Click a project: go to its case study.
- Levels, proposed and to be confirmed by Harsh: **Strong** (Java, Spring Boot, REST APIs, MySQL, JPA and Hibernate), **Working knowledge** (React, Node.js and Express, PostgreSQL, Git, Maven, Python and Flask), **Learning** (system design, distributed systems).
- Accessibility: nodes are focusable buttons in a logical order, the side panel is a polite live region, and the list view carries identical data.

### 7.4 Experience `/experience`

**Purpose:** show the internship honestly and in technical depth.

**Sections**

1. **Internship card.** Web Development Intern, Selkey Cybersecurity Pvt. Ltd., Anand, Gujarat, May to June 2026. Technology chips (React.js, Node.js, Express.js, PostgreSQL). Responsibilities as short bullets: responsive UI modules, REST API integration, inventory and case management modules, dynamic field configuration, user administration, debugging and cross-browser testing. Achievement and recognition lines, with links to the completion certificate and appreciation letter once approved.
2. **Blueprint panel (signature): RecoverLab CRM, architecture and my contribution.**
   - Components: browser (React client), Nginx reverse proxy with SSL, Express API on Node.js, PostgreSQL. Cross-cutting items: JWT authentication and role-based access, Socket.IO real-time channel, multi-tenant model, 70+ database migrations, deployment through aaPanel and Nginx.
   - Honesty rule: the parts Harsh built (inventory, case management, dynamic field configuration, user administration, frontend to REST API integration) are drawn with amber `--bp-mark` call-outs labeled Contributed. Everything else is drawn in `--bp-line` and labeled Platform context. A legend explains both. Harsh confirms every marking.
   - Style: 40px grid with minor lines, mono annotations, dimension lines with arrowheads, and an engineering-drawing title block at the bottom right (Project, Role, Date, Sheet 1 of 1).
   - Effect: when the panel scrolls into view the lines draw in dependency order (browser, proxy, API, database) over about 900ms using `stroke-dashoffset`, then nodes fade in. Contributed items get a single amber ring pulse, not a loop. Hover, focus or tap shows an annotation.
   - Mobile: render a separate vertical diagram, not a scaled-down wide one, with annotations listed below.
   - Fallback: the complete static SVG with a text alternative.

### 7.5 Projects `/projects` and `/projects/{slug}`

**List page**

- Order: Rentify (flagship, deepest content), RecoverLab CRM, MovieSearch Recommendation Engine.
- Each project is a large card: screenshot in a browser frame on one side, content on the other (stacked on mobile). Content: mono category label, title, one-line summary, up to five chips, then buttons: Send request (secondary, `--signal` accent), Case study (primary), Live demo and GitHub (icon buttons). RecoverLab carries an “Internship contribution” note.
- The data model has a `status` field (`live` or `in_progress`) and the layout handles four or more projects. Unfinished projects are not shown until the owner marks them ready.

**API playground (signature)**

- Send request opens a panel under the card. It shows a request line in mono, for example `GET /api/v1/projects/rentify`, with a method badge and a Send button.
- The response shows a status line (`200 OK · 38 ms · application/json`) and pretty-printed JSON with keys in `--signal`, strings in `--text`, numbers in `--accent`. Maximum height 320px with its own scroll. Buttons: Copy as cURL and Open in Swagger.
- The request is real: a same-origin `fetch`, timed with `performance.now()`. Never mocked.
- States: idle, loading (spinner after 150ms), success, error (red status line with a message), rate limited (429, “Slow down, try again in N seconds”).
- Optional typewriter reveal of the JSON, 600ms at most, skipped for reduced motion.

**Case-study page template**

- Header with title, category, chips and links. A summary paragraph. A sticky table of contents on 1024px and wider.
- Sections: Problem; What I built; Architecture (theme-aware SVG diagram); Data model (ER diagram); API surface (method badge, path, purpose); Key decisions (three to five, each with the reasoning); Result; What I’d improve (callout); Screenshots; Stack chips. A Next project link at the bottom.
- Every case study needs at least one real proof: a metric, a Swagger link, tests, or a live demo. Rentify gets the deepest page.

### 7.6 Contact `/contact`

**Purpose:** make it easy to reach Harsh. This is the journey’s final response.

**Sections**

1. **Header.** A large mono `200 OK` in `--ok` and an H1 along the lines of “Response received. Let’s talk.” A short line about backend roles, projects and ideas.
2. **Contact cards.** Email (a plain `mailto:` link, not a webmail compose link), LinkedIn, GitHub, Resume (download). Addresses and URLs come from the content file.
3. **Contact form,** behind the feature flag `contact.form.enabled`. Fields: Name, Email, Subject, Message, all required, with maximum lengths 100, 254, 160 and 2000. Plus a hidden honeypot field and a CSRF token.
   - Validate on blur and on submit. Show inline errors under each field, linked with `aria-describedby`.
   - While sending, disable the button and show a spinner.
   - Success replaces the form with a response panel: `201 Created` in `--ok` and “Message received. I’ll reply by email.” Show success only after the server confirms with a 2xx.
   - Errors: 400 shows field errors, 429 shows a friendly wait message, 500 or a network error points to the email link. Move focus to the result panel, which is a polite live region.
   - Build this last. If it is not ready or the flag is off, show only the cards.

**Effect:** on load, the status line types out `HTTP/1.1 200 OK` (500ms at most), then the cards enter with a 60ms stagger and 250ms each. Reduced motion: all static.

### 7.7 404 and 500 pages

- An IDE-style window: dark frame, a file tree on the left (`src/main/java/...`), an editor tab named `NotFoundException.java` (or `ServerErrorException.java`), and a short message such as “404: this route isn’t mapped.”
- Links to the six main pages and a Go home button. Only effect: a blinking caret, disabled for reduced motion. Static and light.

## 8. Responsive rules

- No horizontal page scroll at any width. Wide content scrolls inside its own container.
- Touch targets are at least 44px.
- Mobile versions of the signature effects: vertical journey, vertical metro map, list view by default for Skills, stacked blueprint, playground full width.
- Test at 360, 768, 1024 and 1440px, on real phones as well as in a browser.

## 9. Accessibility (WCAG 2.2 AA)

- Semantic landmarks, one H1 per page, a logical heading order, and a skip link.
- Everything works by keyboard. Focus is always visible. Menus and sheets trap focus and close with Esc.
- Meaningful images have alt text. SVG diagrams have `<title>` and `<desc>` plus a text alternative.
- Color is never the only carrier of meaning. All form fields have labels. The page declares its language.
- A Reduce motion control exists and the OS setting is respected.

## 10. Performance and SEO

- Budgets on a mid-range phone over 4G: LCP under 2.5 seconds, CLS under 0.1, INP under 200ms, and under 80 KB of gzipped JavaScript per page (core plus page module).
- Preload the primary font weights. No render-blocking JavaScript. Lazy-load images below the fold.
- Every page has a unique title in the form “Page · Harsh Rana, Java Backend Developer”, a meta description, a canonical URL, Open Graph and Twitter tags with a 1200 by 630 image, plus `sitemap.xml`, `robots.txt` and JSON-LD for Person.
- Target Lighthouse 90 or above in all categories. Publish only real scores.

## 11. Content rules

- Tone: plain, specific, first person. No filler words such as “passionate” or “hardworking”. Prove it with a fact instead.
- Facts must match the resume, LinkedIn and GitHub.
- RecoverLab is an internship contribution, not a solo project. Label it that way everywhere.
- Do not publish marksheets or other personal documents. Link only the certificates Harsh approves.
- Use typographic punctuation in displayed text (’ and “ ”).

## 12. Endpoints the UI depends on

All same-origin. The backend defines the JSON shapes.

- `GET /api/v1/projects` and `GET /api/v1/projects/{slug}`: the playground.
- `GET /api/v1/status`: the live status strip.
- `POST /contact`: the form (CSRF protected, rate limited).
- `GET /sitemap.xml`, `GET /robots.txt`, `GET /resume`.

## 13. Delivery phases

1. **Phase 1, launch-ready, no effects:** tokens, layout, header and footer, all six pages with real content in plain form, case-study template, contact cards and form, SEO, accessibility, 404 and 500.
2. **Phase 2, signature effects, in this order:** Home request journey; Projects playground; About metro map; Skills constellation; Experience blueprint.
3. **Phase 3:** live status strip and final polish.

A phase is done only when every item in section 15 passes.

## 14. Do-not list

- No neon glows beyond signal elements, and no gradient text.
- No particles, parallax, scroll hijacking or looping typing animations.
- No stock illustrations, mockups presented as products, or placeholder images.
- No fake numbers, no cookie banner unless cookies are actually used, no autoplaying media.

## 15. Acceptance checklist

- All pages render and read correctly with JavaScript off.
- Each signature effect has its fallback, its mobile version and its reduced-motion state.
- Light and dark themes both pass contrast, with no flash on load.
- Keyboard-only walkthrough of every page succeeds.
- No horizontal scroll at 360px.
- Performance budgets and Lighthouse targets met.
- All links work, the contact form is tested on the live site, and every claim matches the resume.
- No placeholder content or template leftovers anywhere.

## 16. Inputs needed from Harsh before building

- A real photo (optional) and an Open Graph image.
- Real screenshots for Rentify and MovieSearch (a RecoverLab screenshot exists).
- A MovieSearch live demo link, or confirmation that there is none.
- Confirmation of the skill levels, the hero and placement wording, the blueprint “Contributed” markings, and which certificates may be shown.
- Confirmation of the amber accent, or a replacement color. Changing `--accent` in the tokens updates the whole site.

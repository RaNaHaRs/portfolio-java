const root = document.documentElement;
const themePreferenceKey = "portfolio.theme";
const motionPreferenceKey = "portfolio.reduceMotion";
const themeMedia = window.matchMedia("(prefers-color-scheme: light)");
const motionMedia = window.matchMedia("(prefers-reduced-motion: reduce)");

function readPreference(key) {
  try {
    return window.localStorage.getItem(key);
  } catch (error) {
    console.warn(`Preference "${key}" could not be read.`, error);
    return null;
  }
}

function savePreference(key, value) {
  try {
    window.localStorage.setItem(key, value);
  } catch (error) {
    console.warn(`Preference "${key}" could not be saved.`, error);
  }
}

function currentTheme() {
  return root.dataset.theme ?? (themeMedia.matches ? "light" : "dark");
}

function updateThemeButton(button) {
  const nextTheme = currentTheme() === "dark" ? "light" : "dark";
  button.setAttribute("aria-label", `Switch to ${nextTheme} theme`);
  button.setAttribute("aria-pressed", String(currentTheme() === "light"));
}

function initializeTheme() {
  const button = document.querySelector("[data-theme-toggle]");
  if (!button) {
    return;
  }

  updateThemeButton(button);
  button.addEventListener("click", () => {
    const nextTheme = currentTheme() === "dark" ? "light" : "dark";
    root.dataset.theme = nextTheme;
    savePreference(themePreferenceKey, nextTheme);
    updateThemeButton(button);
  });

  themeMedia.addEventListener("change", () => {
    if (readPreference(themePreferenceKey) === null) {
      root.removeAttribute("data-theme");
      updateThemeButton(button);
    }
  });
}

function currentMotionPreference() {
  const preference = readPreference(motionPreferenceKey);
  if (preference === "true" || preference === "false") {
    return preference === "true";
  }
  return motionMedia.matches;
}

function applyMotionPreference(reduceMotion, explicitPreference = false) {
  if (reduceMotion) {
    root.dataset.reduceMotion = "true";
  } else {
    root.removeAttribute("data-reduce-motion");
  }
  if (explicitPreference) {
    root.dataset.motionPreference = reduceMotion ? "reduce" : "allow";
  } else {
    root.removeAttribute("data-motion-preference");
  }
}

function initializeMotionToggle() {
  const button = document.querySelector("[data-motion-toggle]");
  if (!button) {
    return;
  }

  const updateButton = () => {
    button.setAttribute("aria-pressed", String(currentMotionPreference()));
  };

  const savedPreference = readPreference(motionPreferenceKey);
  applyMotionPreference(
    currentMotionPreference(),
    savedPreference === "true" || savedPreference === "false",
  );
  updateButton();

  button.addEventListener("click", () => {
    const reduceMotion = !currentMotionPreference();
    savePreference(motionPreferenceKey, String(reduceMotion));
    applyMotionPreference(reduceMotion, true);
    updateButton();
  });

  motionMedia.addEventListener("change", () => {
    if (readPreference(motionPreferenceKey) === null) {
      applyMotionPreference(motionMedia.matches);
      updateButton();
    }
  });
}

function initializeNavigation() {
  const toggle = document.querySelector("[data-nav-toggle]");
  const panel = document.querySelector("[data-nav-panel]");
  const closeButton = document.querySelector("[data-nav-close]");
  if (!toggle || !panel || !closeButton) {
    return;
  }

  let previousBodyOverflow = "";

  const focusableElements = () => Array.from(
    panel.querySelectorAll('a[href], button:not(:disabled), [tabindex]:not([tabindex="-1"])'),
  );

  const closeMenu = (restoreFocus = true) => {
    panel.removeAttribute("data-open");
    toggle.setAttribute("aria-expanded", "false");
    document.body.style.overflow = previousBodyOverflow;
    if (restoreFocus) {
      toggle.focus();
    }
  };

  const openMenu = () => {
    previousBodyOverflow = document.body.style.overflow;
    panel.dataset.open = "true";
    toggle.setAttribute("aria-expanded", "true");
    document.body.style.overflow = "hidden";
    focusableElements()[0]?.focus();
  };

  toggle.addEventListener("click", () => {
    if (toggle.getAttribute("aria-expanded") === "true") {
      closeMenu();
    } else {
      openMenu();
    }
  });
  closeButton.addEventListener("click", () => closeMenu());
  panel.querySelectorAll("a[href]").forEach((link) => {
    link.addEventListener("click", () => closeMenu(false));
  });

  panel.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
      closeMenu();
      return;
    }

    if (event.key !== "Tab") {
      return;
    }

    const items = focusableElements();
    const first = items[0];
    const last = items[items.length - 1];
    if (event.shiftKey && document.activeElement === first) {
      event.preventDefault();
      last?.focus();
    } else if (!event.shiftKey && document.activeElement === last) {
      event.preventDefault();
      first?.focus();
    }
  });

  window.matchMedia("(min-width: 1024px)").addEventListener("change", (event) => {
    if (event.matches && toggle.getAttribute("aria-expanded") === "true") {
      closeMenu(false);
    }
  });
}

function initializeCodeCopy() {
  document.querySelectorAll("[data-copy-code]").forEach((button) => {
    button.addEventListener("click", async () => {
      const code = document.getElementById(button.dataset.copyCode);
      if (!code) {
        console.error("The requested code block was not found.", button.dataset.copyCode);
        return;
      }

      try {
        await navigator.clipboard.writeText(code.textContent ?? "");
        button.textContent = "Copied";
      } catch (error) {
        console.error("The code block could not be copied.", error);
        button.textContent = "Copy failed";
      }
    });
  });
}

function initializeSyntaxHighlighting() {
  const highlightCode = () => {
    if (!window.hljs) {
      return;
    }

    document.querySelectorAll("pre code[class*='language-']").forEach((code) => {
      window.hljs.highlightElement(code);
    });
  };

  if (document.readyState === "complete") {
    highlightCode();
  } else {
    window.addEventListener("load", highlightCode, { once: true });
  }
}

function initializeStatusStrip() {
  const strip = document.querySelector("[data-status-strip]");
  if (!strip) {
    return;
  }

  const unavailable = strip.querySelector("[data-status-unavailable]");
  const metrics = strip.querySelector("[data-status-metrics]");
  const commitLink = strip.querySelector("[data-status-commit-link]");
  const commitUnavailable = strip.querySelector("[data-status-commit-unavailable]");

  const setText = (selector, value) => {
    const element = strip.querySelector(selector);
    if (!element) {
      throw new Error(`The status strip is missing ${selector}.`);
    }
    element.textContent = value;
  };

  const formatUptime = (seconds) => {
    const days = Math.floor(seconds / 86400);
    const hours = Math.floor((seconds % 86400) / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const remainingSeconds = seconds % 60;
    return days > 0
      ? `${days}d ${hours}h ${minutes}m`
      : `${hours}h ${minutes}m ${remainingSeconds}s`;
  };

  const refresh = async () => {
    try {
      const response = await fetch("/api/v1/status", {
        headers: { Accept: "application/json" },
        cache: "no-store",
      });
      if (!response.ok) {
        throw new Error(`Status request failed with HTTP ${response.status}.`);
      }

      const status = await response.json();
      if (!Number.isFinite(status.uptimeSeconds)
          || status.uptimeSeconds < 0
          || !Number.isFinite(status.requestsServed)
          || status.requestsServed < 0
          || !Number.isFinite(status.jvm?.usedMb)
          || status.jvm.usedMb < 0
          || !Number.isFinite(status.jvm?.maxMb)
          || status.jvm.maxMb < 0
          || typeof status.build?.version !== "string"
          || typeof status.build?.time !== "string"
          || !Number.isFinite(Date.parse(status.build.time))) {
        throw new TypeError("The status response is missing required values.");
      }

      setText("[data-status-uptime]", formatUptime(status.uptimeSeconds));
      setText("[data-status-requests]", status.requestsServed.toLocaleString());
      setText("[data-status-memory]", `${status.jvm.usedMb} / ${status.jvm.maxMb} MB`);
      setText("[data-status-build]", status.build.version);
      setText("[data-status-build-time]", new Date(status.build.time).toLocaleString());

      const commit = status.build.commit;
      if (typeof commit === "string" && /^[a-f0-9]{7,40}$/i.test(commit)) {
        commitLink.href = `https://github.com/RaNaHaRs/portfolio-java/commit/${commit}`;
        commitLink.textContent = commit.slice(0, 7);
        commitLink.hidden = false;
        commitUnavailable.hidden = true;
      } else {
        commitLink.hidden = true;
        commitUnavailable.hidden = false;
      }

      metrics.hidden = false;
      unavailable.hidden = true;
    } catch (error) {
      metrics.hidden = true;
      unavailable.hidden = false;
      console.warn("Live status is unavailable.", error);
    }
  };

  void refresh();
  window.setInterval(refresh, 15000);
}

initializeTheme();
initializeMotionToggle();
initializeNavigation();
initializeCodeCopy();
initializeSyntaxHighlighting();
initializeStatusStrip();

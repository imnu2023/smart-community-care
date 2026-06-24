# Design System: 智慧社区养老平台 (Smart Community Elderly Care)

## 1. Visual Theme & Atmosphere

A warm, trustworthy product interface in the "Soft Structuralism" family. The atmosphere is
like a well-appointed community health center — clean and professional but never clinical.
Generous whitespace so elderly users feel unhurried. Soft diffused shadows carry a
blue-brand tint rather than generic black, creating a single coherent light source.

- **Density:** 4/10 (Daily App Balanced — cards have breathing room, forms never feel cramped)
- **Variance:** 5/10 (Offset Asymmetric — bento grids and varied card spans, never 3-equal-columns)
- **Motion:** 5/10 (Fluid CSS — spring-physics easing, staggered cascade reveals, no cinematic overkill)

**Scene:** A family member checks their aging parent's health data on a tablet during their
morning commute. The interface should feel reassuring and clear, not like a clinical dashboard.

## 2. Color Palette & Roles

Single accent (trustworthy blue), warm-tinted neutrals throughout. No purple, no neon, no AI gradients.

- **Canvas Warm** (#F5F6F9) — Page background, slightly warm-tinted off-white
- **Pure Surface** (#FDFDFC) — Card and container fill, micro-warm (not pure #FFF)
- **Container Low** (#F0F2F6) — Subtle surface elevation for nested content
- **Container Mid** (#EAECF0) — Dividers, disabled states, skeleton base
- **Charcoal Ink** (#191C1E) — Primary text, headings, high-emphasis content
- **Muted Steel** (#424656) — Secondary text, descriptions, metadata, timestamps
- **Whisper Border** (#C2C6D8) — Card borders, structural 1px lines
- **Outline Variant** (rgba(194, 198, 216, 0.6)) — Subtle inner borders, fieldset edges
- **Trust Blue** (#0050CB) — **Sole accent.** CTAs, active nav, focus rings, links
- **Trust Blue Hover** (#003FA4) — Hover/pressed state of accent
- **Blue Fixed** (#DAE1FF) — Subtle accent background (badges, active nav background)
- **Warm Amber** (#A53B18) — Warning/caution indicators. Used sparingly for health alerts
- **Life Green** (#006825) — Success state, healthy indicators, payment confirmed
- **Alert Red** (#BA1A1A) — Errors, emergency, destructive actions
- **Alert Red Container** (#FFDAD6) — Error/warning background pills

**Shadow Tokens (tinted to brand blue, never pure black):**
- `shadow-card`: `0 2px 12px rgba(0, 47, 112, 0.04), 0 0 0 1px rgba(0, 47, 112, 0.03)`
- `shadow-soft-hover`: `0 8px 32px rgba(0, 47, 112, 0.10)`

**Banned:** Pure `#000000`, pure `#FFFFFF`, purple/blue neon gradients, `rgba(0,0,0,0.X)` shadows.

## 3. Typography Rules

Sans-serif only (this is a product dashboard, not editorial). Latin glyphs via Geist,
CJK via system-native PingFang SC / Microsoft YaHei for reliable rendering in China.

- **Display/Heading:** Geist, Satoshi, -apple-system, PingFang SC — weight 700 Bold,
  letter-spacing -0.03em, line-height 1.15. `clamp(32px, 5vw, 48px)` for hero titles.
- **Subheading:** Geist, PingFang SC — weight 600 SemiBold, letter-spacing -0.01em, line-height 1.3.
- **Body:** Geist, -apple-system, Microsoft YaHei — weight 400 Regular, line-height 1.6,
  max-width 65ch for prose blocks. Minimum 16px (elderly readability).
- **Label/UI:** Geist — weight 500 Medium, 14-16px, letter-spacing 0.01em.
- **Mono/Numbers:** JetBrains Mono, SF Mono — weight 500 Medium. All health data,
  currency, timestamps use `font-variant-numeric: tabular-nums` for alignment.
- **Text wrapping:** `text-wrap: balance` on h1-h3, `text-wrap: pretty` on prose paragraphs.

**Banned:** Generic serif fonts. Title Case on every header (use sentence case).
Numbers in proportional font for data displays. Body text below 16px in production.

## 4. Component Stylings

### Buttons
- **Primary CTA:** Trust Blue (#0050CB) fill, white text. `border-radius: 9999px` (full pill).
  Hover: background shifts to #003FA4, `translateY(-1px)`. Active: `scale(0.97)` tactile press.
  Focus: 2px Trust Blue outline with 2px offset. No outer glow, no neon.
- **Secondary:** Transparent with Whisper Border. Hover: Blue Fixed (#DAE1FF) background.
- **Ghost/Tertiary:** No border, transparent background. Hover: Container High background.
- **Danger:** Alert Red fill, white text. Used only for destructive actions.
- **Button-in-Button pattern:** When a trailing icon exists, nest it in its own
  `24px × 24px` circular wrapper with `rgba(255,255,255,0.2)` background.
  On hover, icon circle translates `translateX(2px) translateY(-1px)`.
- **Minimum touch target:** 48px (WCAG AAA for elderly users).

### Cards
- **Standard Card:** Pure Surface (#FDFDFC) background, 1px Whisper Border,
  `border-radius: 20px` (--radius-xl), `shadow-card`. Hover: `shadow-soft-hover`,
  `translateY(-2px)`. Use only when elevation communicates hierarchy.
- **Double-Bezel Card (premium):** Outer shell: Pure Surface + 3px padding + `border-radius: 28px`.
  Inner core: gradient background `linear-gradient(135deg, #FCFDFF, #F8F9FD)` +
  `border-radius: calc(28px - 4px)`. Creates machined-hardware depth.
- **High-density alternative:** Replace cards with border-top dividers and negative space.
- **Card groups:** Buttons pinned to bottom. Consistent vertical rhythm across columns.

### Inputs & Forms
- Label above input. 16px Medium weight. 4px gap between label and field.
- Input: Pure Surface background, 1px Whisper Border, `border-radius: 8px`,
  minimum height 56px (elderly-friendly). Placeholder in Muted Steel.
- Focus: 2px Trust Blue border (not box-shadow glow). Error: 2px Alert Red border.
- Error text below input in Alert Red, 14px. Helper text in Muted Steel.
- No floating labels. No `window.alert()` for errors.

### Navigation
- **Elder Dashboard:** Left sidebar (264px), Warm Canvas gradient background,
  profile section with SVG health ring, nav items with colored icon containers,
  sliding active indicator pill.
- **Family Portal:** Top navigation bar (72px), semi-transparent glass (`backdrop-blur: 12px`),
  active indicator as 3px bottom accent line.
- **Admin Panel:** Left sidebar (240px), Trust Blue gradient logo mark, icon-based nav.
- **Mobile:** Sidebar hidden, content full-width. Bottom nav bar optional.

### Modals & Dialogs
- `border-radius: 28px` (--radius-2xl). `shadow-soft-hover`.
- Header with bottom border, body with generous padding, footer with top border.
- Backdrop: `rgba(0,0,0,0.25)` + `backdrop-blur(6px)`.
- Slide-up panel variant for mobile (bottom-sheet style).

### Badges & Tags
- Square-cornered (`border-radius: 4px`) for critical states (warnings, errors).
- Rounded pills (`border-radius: 9999px`) for status indicators.
- Colored backgrounds with matching dark text — never white text on colored pills.

### Loading States
- Skeletal shimmer loaders matching exact layout dimensions.
- Gradient: `Container Mid → Container High → Container Mid` with 1.5s animation.
- **Banned:** Generic circular spinners (`.el-loading` only as last resort).

### Empty States
- Composed compositions: large icon (48px, 35% opacity), heading, descriptive paragraph,
  optional CTA. Dashed border container for "not yet set up" states.

## 5. Layout Principles

- **Max-width container:** 1280px centered. Content never stretches edge-to-edge.
- **Grid-first:** CSS Grid for 2D layouts (`grid-template-columns: repeat(auto-fit, minmax(340px, 1fr))`).
  Flexbox only for 1D flows. Never use `calc()` percentage hacks.
- **Asymmetric Bento:** Hero/feature sections use `repeat(6, 1fr)` with varying `col-span`
  values. Never 3 equal cards in a row.
- **Macro-whitespace:** Section padding minimum `var(--space-2xl)` (48px).
  Page-level padding `var(--space-xl)` (32px). Generous internal card padding (24px).
- **Spacing scale:** 4px → 8px → 12px → 16px → 24px → 32px → 48px → 64px → 96px.
  8px base unit.
- **Sidebar offset:** Main content `margin-left` equals sidebar width. No overlap.
- **No absolute-positioned content stacking.** Every element in its own spatial zone.
- **Dashboard left sidebar:** The default for elder/admin roles. Family role uses top nav.

## 6. Motion & Interaction

- **Easing:** Custom cubic-bezier `cubic-bezier(0.32, 0.72, 0, 1)` for all transitions.
  Simulates spring physics (mass-damper model). No `linear` or default `ease-in-out`.
- **Duration:** Fast 150ms (micro-interactions), Normal 250ms, Slow 300-400ms (page transitions).
- **Staggered cascade:** List items fade in sequentially with 50-100ms delays.
  Animation: `opacity: 0 → 1` + `translateY(20px → 0)` over 500-600ms.
- **Hover:** Cards lift `translateY(-2px to -3px)` with shadow intensification.
  Buttons: `translateY(-1px)` with background shift. Active: `scale(0.97-0.98)`.
- **Page transitions:** `fade + translateY(12px → 0)` on enter, reverse on leave.
  Duration 250-350ms. `mode="out-in"` for Vue Router.
- **Health score rings:** SVG `stroke-dashoffset` animation over 800-1000ms
  with spring easing. Gradient stroke for multi-color ranges.
- **GPU-safe only:** Animate exclusively via `transform` and `opacity`.
- **Reduced motion:** `@media (prefers-reduced-motion: reduce)` disables all
  animations, replaces with instant crossfade. Skeleton shimmer becomes static.
- **Banned:** Animating `top`, `left`, `width`, `height`. Scroll-jacking. Bounce/elastic curves.

## 7. Responsive Rules

- **≥1280px:** Full desktop. Sidebar visible, bento grids active, max-width content.
- **1024-1279px:** Sidebar narrows to 220px. Bento grids collapse to 3-column.
  Hero titles reduce via `clamp()`.
- **768-1023px:** Tablet. Sidebar persists. Grids simplify to 2-column.
  Card descriptions may truncate.
- **<768px:** Mobile. Sidebar hidden. Single-column layouts. Bottom padding accounts
  for mobile browser chrome (`calc(var(--bottombar-height) + var(--space-md))`).
  Typography scales down via CSS custom properties.
- **Touch targets:** All interactive elements ≥48px (44px absolute minimum).
- **No horizontal scroll** on any viewport. Text wraps, never overflows.
- **Full-height sections:** `min-height: 100dvh` (never `100vh` — iOS Safari viewport bug).

## 8. Iconography

- **Icon system:** Custom SVG component (`AppIcon.vue`). 24×24 viewBox, 2px stroke,
  `stroke-linecap: round`, `stroke-linejoin: round`. Color via `currentColor` for context awareness.
- **30+ icons:** dashboard, heart, pulse, clipboard, tool, package, calendar, bell,
  home, message, user, moon, droplet, thermometer, foot, pill, sport, book, people,
  palette, lock, eye, logout, wave, refresh, captcha, check, chevron-right,
  external-link, phone, mail, clock, x, star, map-pin.
- **Banned:** Emoji as icons. Lucide/Feather as default choice. Inconsistent stroke widths.

## 9. Anti-Patterns (Banned)

- ❌ Emoji as icons or UI elements — always use SVG AppIcon
- ❌ Inter as the primary font — BANNED. Use Geist or Satoshi for premium, non-generic character.
- ❌ Generic serif fonts in dashboard/product UI
- ❌ Pure black (#000000) or pure white (#FFFFFF) surfaces
- ❌ Neon/outer glow shadows on buttons or cards
- ❌ Purple/blue AI gradients (the "SaaS fingerprint")
- ❌ Oversaturated accent colors (keep saturation < 80%)
- ❌ 3-column equal-card feature rows
- ❌ Centered hero layouts (high-variance projects)
- ❌ Generic placeholder names ("John Doe", "Acme Corp")
- ❌ Fake round numbers (99.99%, 50%) — use organic data
- ❌ AI copywriting clichés ("Elevate", "Seamless", "Unleash", "Next-Gen")
- ❌ Filler UI text ("Scroll to explore", bouncing chevrons)
- ❌ `h-screen` for full-height sections — always `100dvh`
- ❌ `z-index: 9999` — use systematic scale
- ❌ `window.alert()` for errors — inline form validation only
- ❌ Circular spinners — skeletal loaders matching layout shape
- ❌ Dead links (`href="#"`) — link to real destinations or disable visually
- ❌ Floating labels on inputs — labels above, always
- ❌ Glassmorphism as default card style — rare and purposeful only

## 10. Z-Index Scale

| Layer | Value | Usage |
|-------|-------|-------|
| Base | 0 | Default content |
| Dropdown | 100 | Select menus, popovers |
| Sticky | 200 | Sticky headers, topbar |
| Sidebar | 300 | Fixed side navigation |
| Overlay | 400 | Sheet overlays, drawers |
| Modal | 500 | Dialogs, confirmations |
| Toast | 600 | Notifications, toasts |
| Tooltip | 700 | Tooltips, highest priority |

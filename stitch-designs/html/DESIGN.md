# Design System: 智伴 · 智慧社区养老平台

> **Target:** Google Stitch screen generation — semantic, descriptive rules with precise values.
> **Project:** Smart Community Elderly Care Platform (Spring Boot + Vue 3 + Element Plus)
> **Users:** Elderly residents, family members, community administrators
> **Generated:** 2026-06-24

---

## 1. Visual Theme & Atmosphere

A **warm, trusted clinical space** — like a well-lit community health center with soft morning light filtering through frosted glass. The interface serves elderly residents who need clarity and reassurance, family members who need visibility into their loved ones' wellbeing, and administrators who need efficient data management. Every pixel must convey safety, warmth, and quiet competence — never cold institutional sterility, never childish patronizing.

**Density: 4–5 (Daily App Balanced).** Generous whitespace around health metrics and form fields. Cards breathe with 24px internal padding. The admin panel leans toward 6 (moderate data density) with comfortable 56px table row heights — data-rich but never cramped. Health alerts and emergency actions command attention through color and position, not through density spikes.

**Variance: 6 (Offset Asymmetric).** The dashboard uses an asymmetric 5-column bento grid (2fr 2fr 2fr 1fr 1fr) for quick actions — 3 large spanning cards paired with 5 smaller cards. The sidebar is left-anchored with a subtle warm gradient depth — never flat. Health vital cards each carry a unique soft color tint and a 2px top accent strip revealed on hover. The login page is centered as a card but floats above an intentionally asymmetric field of drifting radial glow orbs — the centered form sits above an unbalanced, organic light field.

**Motion: 5 (Fluid CSS + Restrained Springs).** Page transitions are gentle fades with a 12px vertical drift (300ms in, 200ms out). Card hover lifts translate up 2px with a diffused shadow bloom over 250ms. The sidebar emergency-call indicator carries a perpetual soft pulse — breathing opacity between 0.4 and 1.0, not a neon blink. Health data rings animate stroke-dashoffset with spring-physics easing on mount. Motion should feel reassuring and weighty, never theatrical. All animations respect prefers-reduced-motion: reduce globally.

**Mood keywords:** warm, trustworthy, clinical-clean, accessible, grounded, quietly confident, human-centered, dignified.

---

## 2. Color Palette & Roles

A single cohesive palette built on a **warm-gray Zinc base** with one calibrated blue accent. No purple. No neon. No pure black. No warm/cool gray fluctuation.

### Surface Hierarchy (Warm-White to Deep Gray)

- **Canvas Warm White** (#F5F6F9) — Page background. Slightly warm, never sterile #FFFFFF. The canvas breathes.
- **Pure Surface** (#FDFDFC) — Card and container fills atop Canvas. Micro-warm tint — you only notice it is not pure white when it sits next to white.
- **Whisper Surface** (#F0F2F6) — Hovered table rows, secondary card fills, sidebar background mid-layer.
- **Still Surface** (#EAECF0) — Selected table row backgrounds, active chip fills, input base fills, disabled state surfaces.
- **Cloud Border** (#DEE0E4) — Structural outlines at rest: card borders, sidebar right-border, table row dividers.
- **Deep Zinc Boundary** (#C2C6D8) — Stronger borders for elevated cards, focus-state outlines, and high-emphasis dividers.

### Text Hierarchy (High contrast for elderly readability)

- **Charcoal Ink** (#191C1E) — Primary body text and all headlines. Off-black with subtle warmth; never #000000. WCAG AAA at all sizes against Canvas.
- **Muted Steel** (#424656) — Secondary text, metadata, timestamps, form helper captions, card descriptions. WCAG AA minimum guaranteed.
- **Faded Stone** (#727687) — Placeholder text, tertiary labels, disabled text. Used sparingly — never for critical information.

### Accent (One accent only. Saturation: 65%. Not purple. Not neon.)

- **Trustworthy Blue** (#0050CB) — The single accent color for the entire platform. Used exclusively for:
  - Primary CTA buttons (solid fill).
  - Focus rings on inputs and interactive elements.
  - Active navigation item backgrounds (as a soft tint).
  - Link text and active state indicators.
  - **Never** for decorative glows, gradient fills, or colored shadows.
- **Trustworthy Blue — Hover** (#003FA4) — Deeper, grounded blue. No brightness jump — a calm darkening.
- **Trustworthy Blue — Container Tint** (#DAE1FF) — Soft lilac-blue tint for active menu item backgrounds and selected chip fills.
- **On-Accent Text** (#FFFFFF) — Always white on the accent solid fill. Contrast ratio 8.6:1.

### Semantic Colors (Functional — never used as decorative accents)

- **Warm Clay** (#A53B18) — Warning states, low-balance alerts, medium-severity health flags. Used only where attention is needed but urgency is moderate.
  - Container: #FFDBD0 — Soft peachy tint for warning chips and alert cards.
- **Living Green** (#006825) — Success states, normal health ranges, confirmed/payment-complete badges, positive health indicators.
  - Container: #E6FFE1 — Soft green tint for health-positive card fills.
- **Critical Red** (#BA1A1A) — Error text, destructive actions, emergency status, high-severity health warnings (heart rate critical, blood pressure danger zone).
  - Container: #FFDAD6 — Soft red tint for error card backgrounds and destructive hover zones.

### Shadow Tinting

Shadows are tinted with Trustworthy Blue's hue at very low opacity — never pure black shadows:
- **Card Rest Shadow:** 0 2px 12px rgba(0, 47, 112, 0.04), 0 0 0 1px rgba(0, 47, 112, 0.03) — Double-layer: soft spread + hairline structural ring.
- **Card Hover Shadow:** 0 8px 32px rgba(0, 47, 112, 0.10) — Single expanded bloom on hover.
- **Dialog/Modal Shadow:** 0 4px 32px rgba(0, 47, 112, 0.07), 0 1px 3px rgba(0, 0, 0, 0.04) — Layered depth for elevated surfaces.

### Banned Color Patterns (Explicit — Never Apply These)

1. No pure black (#000000) — always use Charcoal Ink #191C1E as the darkest tone.
2. No purple or neon-blue accents (#7C3AED, #3B82F6, #6366F1, #8B5CF6). The only accent is #0050CB.
3. No oversaturated accents — all accent saturation must stay below 80%.
4. No neon/outer-glow shadows on buttons, cards, or inputs. Shadows are subtle, tinted, diffused.
5. No gradient text on any headline or body copy. Solid Charcoal Ink only.
6. No warm-gray / cool-gray palette mixing. Stay in the warm-Zinc family throughout.


---

## 3. Typography Rules

### Font Stack — Inter is BANNED

- **Display & Headlines:** "Geist", "Geist Medium", "PingFang SC", "Noto Sans SC", system-ui, sans-serif.
  Control hierarchy through weight and color, not just massive size. Track-tight (-0.02em to -0.03em). Weight range: 500-700. Weight 700 reserved exclusively for the largest display text (Hero headline, 48px).
- **Body:** "Geist", "PingFang SC", "Noto Sans SC", system-ui, sans-serif.
  Relaxed leading at 1.6. Maximum 65 characters per line. Color: Charcoal Ink for primary body, Muted Steel for secondary.
- **Mono (Numbers / Code / Metadata):** "JetBrains Mono", "Fira Code", "Consolas", "SF Mono", monospace.
  All numeric values — health metrics, wallet balances, timestamps, pagination, admin data table cells — must use monospace with font-variant-numeric: tabular-nums for digit alignment.

### Font Hierarchy Scale (Mobile-First with clamp())

| Token | Size (clamp) | Weight | Leading | Tracking | Usage |
|-------|-------------|--------|---------|----------|-------|
| Display | clamp(28px, 5vw, 48px) | Bold 700 | 1.2 | -0.03em | Hero headline on login page |
| Headline LG | clamp(24px, 3.5vw, 32px) | Semi-Bold 600 | 1.3 | -0.02em | Page titles, section headers |
| Headline MD | clamp(20px, 2.5vw, 24px) | Semi-Bold 600 | 1.4 | -0.01em | Card titles, dialog headers |
| Body LG | clamp(18px, 2vw, 20px) | Regular 400 | 1.6 | 0 | Hero subtitle, guided prompts |
| Body MD | clamp(16px, 1.5vw, 18px) | Regular 400 | 1.6 | 0 | Default body, card descriptions, list items |
| Label MD | clamp(14px, 1.2vw, 16px) | Medium 500 | 1.4 | +0.01em | Button text, nav labels, form labels |
| Caption | 14px (fixed) | Regular 400 | 1.2 | 0 | Timestamps, helper text, table metadata |

### Banned Typography Patterns

1. **Inter is BANNED** — it is the default AI font choice and immediately signals generic output. Use Geist as the primary sans-serif throughout.
2. **Generic serif fonts are BANNED** (Times New Roman, Georgia, Garamond, Palatino). This is a dashboard/software application — sans-serif exclusively.
3. **No all-caps text** on body copy, navigation labels, or section titles. Only the 11px sidebar cluster labels may use uppercase with tracking.
4. **No font-size below 14px** anywhere. Elderly users are the primary audience — 14px is the absolute floor, for captions only.
5. **No weight 700 on anything except the Display token.** Headlines use Semi-Bold 600. Over-bolding feels shouty.

---

## 4. Hero Section (Login Page)

The login page is the first human impression. It must feel trustworthy, warm, and quietly beautiful — never a generic centered form on a flat background.

### Background Field
Three large, soft-focus radial gradient orbs float behind the login card:
- **Blue orb:** radial-gradient(circle, rgba(0, 80, 203, 0.08), transparent 70%) — 500x500px, positioned top-left, drifting ~30px over 20s.
- **Green orb:** radial-gradient(circle, rgba(0, 104, 37, 0.06), transparent 70%) — 400x400px, positioned bottom-right, drifting with a -7s delay.
- **Warm Clay orb:** radial-gradient(circle, rgba(165, 59, 24, 0.04), transparent 70%) — 350x350px, positioned center, drifting with a -14s delay.
- Each orb animates in a 20-second ease-in-out infinite cycle with small translation shifts (~30px on each axis) and subtle scale breathing (0.95-1.08). Almost subliminal — you feel it more than you see it.

### Login Card
- **Position:** Centered vertically and horizontally, but the asymmetric background field prevents a generic centered-only feel.
- **Surface:** #FDFDFC fill with a micro-glass inset highlight: inset 0 1px 0 rgba(255, 255, 255, 0.6).
- **Border:** 1px Cloud Border (#DEE0E4).
- **Border-radius:** Generously rounded at 28px.
- **Shadow:** Dialog shadow — 0 4px 32px rgba(0, 47, 112, 0.07), 0 1px 3px rgba(0, 0, 0, 0.04).
- **Width:** 440px, max-width: 100%.
- **Padding:** 48px (top) / 32px (sides) / 32px (bottom).
- **Entrance animation:** Fade up 16px + scale 0.98 to 1.0 over 600ms with spring-ease-out (cubic-bezier(0.16, 1, 0.3, 1)).

### Logo Mark
- **Container:** 52x52px, border-radius 14px, gradient fill linear-gradient(135deg, #0050CB, #003FA4). Soft drop-shadow 0 4px 16px rgba(0, 80, 203, 0.18). White SVG icon inside (26x26px).
- **Placement:** Left of the platform name, aligned center with the headline row.

### Headline & Subtitle
- **Headline:** "欢迎回来" (Welcome Back) — Geist Semi-Bold 600, 32px, Charcoal Ink. No gradient text. Clean and human.
- **Subtitle:** "请输入您的账号信息" — Geist Regular 400, 18px, Muted Steel. Natural, not robotic. Never "Sign in to your account" or "Enter your credentials."

### Form Fields (Staggered Cascade Entrance)
- Each input group fades up 10px with cascading 50ms delays:
  - Username field: 200ms delay
  - Password field: 250ms delay
  - Captcha field: 300ms delay
  - Options row (remember me + forgot password): 350ms delay
  - Submit button: 400ms delay
- All use the same 0.45s cubic-bezier(0.16, 1, 0.3, 1) transition.

### Banned Hero Elements
1. No "Scroll to explore" / "Swipe down" / bouncing chevrons / scroll arrow icons. The login page is not scrolled.
2. No generic stock photography of elderly people smiling at cameras.
3. No floating 3D avatars, glowing neon rings, or purple gradient overlays.
4. No filler taglines like "Elevate your care experience" or "Next-gen elderly platform."

---

## 5. Component Stylings

### Buttons (All Roles)
- **Primary CTA:** Trustworthy Blue (#0050CB) fill, white text. 8px border-radius (`.radius-md`) — consistent across all variants, never pill-shaped. On hover: darkens to #003FA4. On active/press: `translateY(1px)` — a tactile "push" feedback. No outer glow, no box-shadow bloom, no neon. Minimum 48px height for touch.
- **Secondary/Outline:** Transparent fill, Trustworthy Blue text, Cloud Border (#C2C6D8) stroke. On hover: Blue Container Tint (#DAE1FF) background, Trustworthy Blue border. On active: `translateY(1px)`.
- **Ghost:** Transparent, Muted Steel text. On hover: Still Surface (#EAECF0) background. Used for tertiary actions, "View all" links, toolbar buttons.
- **Danger:** Critical Red fill. Used only for destructive actions (delete, cancel emergency, logout hover). Contains confirmation step — never single-click destructive.
- **Success:** Living Green fill. Used for confirmations, "Mark Complete," health data save.
- **Size variants:** Standard: 48px height, 12px/24px padding. Large: 56px height, 16px/32px padding (login submit, primary dashboard CTAs). Small: 40px height (admin tables only — compact data views).
- **Disabled:** 45% opacity, `pointer-events: none`. No hover states.
- **Focus:** 2px solid Trustworthy Blue outline, 2px offset. No box-shadow glow — a crisp, accessible ring.
- **Loading:** Replace button label with "处理中..." text + 16px inline spinner. Button remains sized, preventing layout shift.

### Cards (Elder Dashboard & General Purpose)
- **Standard Card:** Card Face (#FDFDFC) background, 1px Cloud Border (#C2C6D8), `border-radius: 20px` (`.radius-xl`). Generously rounded corners signal softness — critical for elderly-focused UI where sharp edges read as threatening.
- **Card Shadow:** Single tinted soft shadow: `0 2px 12px rgba(0, 47, 112, 0.04), 0 0 0 1px rgba(0, 47, 112, 0.03)`. Shadows tinted to brand blue hue — reads as air, not dirt. Only 2 shadow layers in the entire system.
- **Card Hover:** Shadow deepens to `0 8px 32px rgba(0, 47, 112, 0.10)`. Card lifts `translateY(-2px)` with spring-physics transition (250ms).
- **Feature Cards (Bento Grid):** Same radius, colored left-border accents replacing shadow as differentiator. Blue border for health, green for services, warm-amber for activities. Hover: border color intensifies + radial spotlight effect from top edge.
- **Health Data Cards:** Living Green tint background (#E6FFE1), no border. Used for normal-range health readings. Alert variant: Critical Red tint background (#FFDAD6).
- **Usage Rule:** Cards ONLY when elevation communicates hierarchy. In high-density admin tables, replace cards with border-top dividers (1px Cloud Border) and negative space — no unnecessary wrapping.
- **High-Density Admin Tables:** Flat rows, 56px height, Cloud Border bottom divider. Hover: Still Surface (#EAECF0) background. No card wrapping around tables.

### Inputs & Forms
- **Label position:** Above the input, always visible. 16px Medium weight Charcoal Ink. 4px gap between label and field. Floating labels are BANNED — they reduce discoverability for elderly users who need persistent orientation cues.
- **Input fill:** Card Face (#FDFDFC) with 1px Cloud Border. `border-radius: 8px`. Minimum height 56px — oversized for elderly tap precision.
- **Placeholder:** Muted Steel (#424656), 16px. Never lighter than AA contrast.
- **Focus state:** 2px Trustworthy Blue solid border (not a box-shadow glow — a crisp, unambiguous stroke).
- **Error state:** 2px Critical Red border. Error text in Danger Container (#FFDAD6) background pill below input, with alert icon. 14px font.
- **Helper text:** Muted Steel, 14px, below input. Optional — only when the field needs explanation.
- **Textarea:** Same styling, 120px min-height. Resize vertical only.
- **Structure pattern (top to bottom):** Label → Input → Helper text (optional) → Error text (conditional).

### Navigation Components
- **Elder Sidebar (264px):** Warm Canvas gradient background (`#F9FAFC` → Container Low). 1px Cloud Border right edge. Profile section with SVG health-score ring (green/amber/red). Navigation clusters with uppercase 11px labels. Items: 48px min-height, 8px radius, colored icon container (36px, soft-tinted backgrounds). Active item: Blue Container Tint (#DAE1FF) background, Trustworthy Blue text, Semibold weight. Emergency item: critical — Alert Red pulse dot when active, Danger Container background.
- **Family Top Bar (72px):** Semi-transparent glass surface (`backdrop-blur: 12px`). Active nav indicator: 3px Trustworthy Blue bottom accent line. Profile avatar on right.
- **Admin Sidebar (240px):** Trustworthy Blue gradient logo mark. Icon-based nav with tooltips on collapse.
- **Mobile (< 768px):** Sidebar hidden. Bottom navigation bar (64px) with 4-5 primary destinations. Emergency call gets a prominent FAB-style button breaking the bottom bar vertically. Content area adds bottom padding: `calc(64px + 16px)`.

### Modals & Dialogs
- **Shape:** 28px border-radius (`.radius-2xl`). Soft, welcoming entry.
- **Surface:** Card Face (#FDFDFC) fill with inset 1px highlight: `inset 0 1px 0 rgba(255, 255, 255, 0.6)`.
- **Shadow:** Dialog shadow — `0 4px 32px rgba(0, 47, 112, 0.07), 0 1px 3px rgba(0, 0, 0, 0.04)`.
- **Backdrop:** Charcoal Ink at 25% opacity + `backdrop-filter: blur(6px)`. Dims without obscuring.
- **Animation:** Scale 0.95 → 1.0 + opacity fade, spring-ease-out, 300ms.
- **Header:** Headline MD weight, bottom border (1px Cloud Border). 32px top padding, 16px bottom.
- **Body:** Body MD, 24px horizontal padding. Maximum height 60vh with overflow-y auto.
- **Footer:** Right-aligned actions, top border. Primary button on the right, secondary on the left.
- **Mobile variant:** Bottom-sheet style — slides up from bottom, full-width, 28px top-radius only.

### Tags & Badges
- **Status badges:** Pill shape (`border-radius: 9999px`). 14px font, Semibold (600) weight. 4px/12px padding. Semantic fills with matching dark text — never white text on colored pills.
- **Colors:** Info = Blue Container Tint (#DAE1FF) + Trustworthy Blue text. Success = Living Green Container (#E6FFE1) + Living Green text. Warning = Warm Clay Container (#FFDBD0) + Warm Clay text. Danger = Critical Red Container (#FFDAD6) + Critical Red text. Neutral = Still Surface (#EAECF0) + Muted Steel text.
- **Notification badge:** Critical Red fill, white text, 20px diameter circle. Positioned absolutely top-right of icons.

### Loading States
- **Skeletal Loaders:** Shimmer animation across placeholder rectangles matching exact layout dimensions. Gradient: `Container Mid → Container High → Container Mid` at 200% width, animating `background-position` over 1.5s infinite.
- **No circular spinners** for page loads, data fetches, or empty states. The ONLY exception: 16px inline spinner inside a button during form submission.
- **Page transitions:** 300ms opacity fade with 12px vertical slide. Staggered cascade for dashboard widgets — each card enters 80ms after the previous.

### Empty States
- **Composed compositions** — never just "No data" text. Each empty state: contextual icon (48px, Muted Steel at 60% opacity), Headline MD title, Body MD instructional text (max 48ch), optional primary CTA button.
- **Examples:** "还没有健康数据" → "记录您的第一份健康数据，开始追踪健康状况" → [录入健康数据]. "暂无订单" → "浏览社区服务并下单" → [查看服务].
- **Dashed border container** (`2px dashed Cloud Border`) for "not yet set up" states like unconfigured devices or empty wallets.

### Error States
- **Inline field errors:** Danger Container (#FFDAD6) pill below input, with alert icon + 14px Critical Red text.
- **Toast notifications:** 20px radius, soft shadow, auto-dismiss 4 seconds. Success = Living Green Container. Warning = Warm Clay Container. Error = Critical Red Container. Each with icon + single-line message.
- **Page-level errors:** Card with friendly illustration, error title, human-readable description, "重试" (Retry) button. Never raw stack traces or technical error codes to users.

---

## 6. Layout Principles

### Grid Architecture
- **Base unit:** 8px. All spacing multiples of 8px: 4px (half), 8px, 12px, 16px, 24px, 32px, 48px, 64px, 96px.
- **Page structure:** Fixed sidebar (264px) + fluid main content. Internal content constrained to 1280px max-width — never stretches edge-to-edge on wide screens (>1440px).
- **Dashboard bento grid:** Asymmetric 5-column: `2fr 2fr 2fr 1fr 1fr`. 3 large feature cards (span 2 rows each) + 5 compact tiles. NOT a 3-equal-column layout — the asymmetry is the signature pattern.
- **Two-column layouts:** `1fr 1fr` grid, 24px gap. Side-by-side content (activities + tips, order detail + payment).
- **Section vertical rhythm:** 64px between major sections, 24px between adjacent components within a section.

### Spatial Rules
- **No overlapping elements.** Every element in its own clean spatial zone. No absolute-positioned content stacking, no z-index battles, no text-over-image.
- **Generous internal padding:** Cards: 24px. Sections: 64px top/bottom. Form groups: 16px gap. Sidebar nav items: 12px horizontal + 4px margin.
- **Whitespace as primary separator:** Spacing between sections is the main divider. Borders and lines are secondary — used only when space alone can't communicate the boundary.
- **Full-height sections:** `min-height: 100dvh` — NEVER `h-screen` or `100vh`. iOS Safari address bar collapse causes catastrophic jumps with `100vh`.

### Responsive Strategy
- **≥1280px (Desktop):** Full sidebar, bento grids active, max-width content containment.
- **1024–1279px:** Sidebar narrows to 220px. Bento grids collapse to 3 columns. Hero titles reduce via `clamp()`.
- **768–1023px (Tablet):** Sidebar persists. Grids simplify to 2-column. Card descriptions may truncate.
- **<768px (Mobile):** Sidebar HIDDEN — replaced by bottom nav bar (64px). ALL multi-column grids collapse to single column. No exceptions. Typography scales via CSS custom properties. Bottom padding accounts for browser chrome.
- **<480px:** Single column only. Modals 92% width. Side padding 16px.
- **Touch targets:** All interactive elements ≥48px (WCAG 2.5.5 AAA for elderly). Inputs: 56px.
- **NO horizontal scroll at any viewport.** Test at 320px width. Text wraps, never overflows.

### Layout Bans
1. **No centered hero sections** — left-aligned with asymmetric right anchor.
2. **No 3-equal-column card rows** — the #1 AI-generated cliché. Asymmetric bento or 2-column zig-zag instead.
3. **No flexbox `calc()` percentage hacks** — always CSS Grid for multi-column.
4. **No `h-screen`** — always `min-h-[100dvh]`.
5. **No overlapping elements via absolute positioning.**
6. **No edge-to-edge content on ultra-wide screens** — max-width containment.

---

## 7. Motion & Interaction

### Spring Physics Default
All interactive transitions use spring-physics simulation — never linear or default `ease-in-out`:
- **Custom easing curve:** `cubic-bezier(0.16, 1, 0.3, 1)` — the system's sole CSS ease-out. Simulates stiffness: 100, damping: 20 mass-damper model.
- **Hover lifts:** Cards translate `-2px` on Y, shadows deepen. Duration: ~250ms natural spring settlement.
- **Press/tap:** Buttons translate `+1px` on Y (tactile push feedback). Scale: 0.98. Duration: 150ms.
- **Modal entry/exit:** Scale 0.95 → 1.0 + opacity fade. 300ms.
- **Page transitions:** Opacity 0 → 1 + `translateY(12px → 0)`. 300ms in, 200ms out. Vue Router `mode="out-in"`.

### Staggered Orchestration
Never mount lists or widgets simultaneously. Use cascade delays:
- **Dashboard widgets:** Greeting header (0ms) → Vitals cards (80ms each, starting 80ms) → Bento cards (80ms each, starting 300ms) → Bottom sections (400ms+).
- **Login form fields:** Logo (0ms) → Header (100ms) → Username (200ms) → Password (250ms) → Captcha (300ms) → Options (350ms) → Submit (400ms). Each fades up 10px over 450ms.
- **List items (activities, orders, messages):** Each item enters 60ms after the previous, `opacity + translateY(20px → 0)`, 500ms duration.

### Perpetual Micro-Interactions
Every active-state component has an infinite loop:
- **Emergency call indicator:** Pulse — opacity 0.4 → 1 → 0.4, scale 1 → 1.3 → 1. 2-second cycle. Red dot on sidebar navigation item.
- **Health score ring:** Breathing glow — outer ring shadow expands/contracts every 3s (scale 1 → 1.03 → 1).
- **Skeleton loaders:** Shimmer sweeps left-to-right every 1.5s while data loads.

### Performance Constraints
- **Animate exclusively via `transform` and `opacity`.** Never `top`, `left`, `width`, `height`, or layout-triggering properties.
- **`will-change: transform`** scoped to hover state only — removed on mouse leave to free GPU memory.
- **Noise/grain filter:** Fixed pseudo-element (SVG turbulence at 0.015 opacity), single compositor layer, never repaints.
- **`@media (prefers-reduced-motion: reduce)`** disables ALL animations globally: `animation-duration: 0.01ms !important`, `transition-duration: 0.01ms !important`. Skeleton shimmer becomes static fill.

### Motion Bans
1. **No linear easing** — always spring-physics or cubic-bezier(0.16, 1, 0.3, 1).
2. **No scroll-jacking** — browser-native scroll speed only.
3. **No custom mouse cursors** — standard system cursor everywhere.
4. **No infinite spin animations** — skeletons shimmer, never spin.
5. **No auto-playing video backgrounds** — distracting and performance-destructive.
6. **No parallax scroll effects** — disorienting for elderly users.
7. **No bounce/elastic easing curves** — weighty and grounded, never playful.

---

## 8. Icon System

Custom inline SVG icon component (`AppIcon.vue`). Lucide-family outlined style: 24×24 viewBox, 2px stroke, `stroke-linecap: round`, `stroke-linejoin: round`. Color via `currentColor` for automatic context inheritance.

**30+ icons available:** dashboard, heart, pulse, clipboard, tool, package, calendar, bell, home, message, user, moon, droplet, thermometer, foot, pill, sport, book, people, palette, lock, eye, logout, wave, refresh, captcha, check, chevron-right, external-link, phone, mail, clock, x, star, map-pin.

**Icon containers (sidebar nav):** Icons sit inside 36×36 rounded containers with soft-tinted backgrounds:
- Blue (#E8ECFC) — Dashboard/home
- Green (#E6F5EC) — Health data
- Purple (#F0E8F8) — Data entry
- Amber (#FEF6E8) — Services/orders/activities
- Red (#FDE8E8) — Emergency
- Slate (Still Surface) — Utility (messages, devices, profile)

The container color provides a secondary recognition cue beyond icon shape — critical for elderly users.

**Banned:** Emoji as icons. Icon fonts (Font Awesome, Material Icons). Inconsistent stroke widths. Icons without `aria-label` when used as standalone buttons.

---

## 9. Anti-Patterns (Explicit Bans)

### Typography
- ❌ Inter font — BANNED. Default AI choice. Use Geist or Satoshi.
- ❌ Generic serif fonts (Times New Roman, Georgia, Garamond, Palatino) — BANNED. Dashboard app = sans-serif only.
- ❌ System fonts as primary typeface — fallbacks only.
- ❌ All-caps body text or nav labels — only 11px sidebar cluster labels.
- ❌ Font-size below 14px anywhere — elderly users are primary audience.

### Color
- ❌ Pure black (#000000) — use Charcoal Ink #191C1E.
- ❌ Pure white (#FFFFFF) — use Card Face #FDFDFC.
- ❌ AI Purple/Blue neon gradients (#7C3AED, #3B82F6, #6366F1).
- ❌ Oversaturated decorative accents (>90% saturation).
- ❌ Neon/outer glow shadows on buttons or cards.
- ❌ Gradient text on any headline or body copy.
- ❌ Warm/cool gray fluctuation — one gray family throughout.

### Layout
- ❌ Centered hero sections (variance > 4).
- ❌ 3-equal-column card feature rows.
- ❌ `h-screen` — always `100dvh`.
- ❌ Flexbox `calc()` percentage hacks.
- ❌ Overlapping elements via absolute positioning.
- ❌ Horizontal scroll on mobile — CRITICAL FAILURE.

### Components
- ❌ Neon/outer glow shadows on buttons.
- ❌ Custom mouse cursors.
- ❌ Floating labels on inputs.
- ❌ Circular spinners for page loads.
- ❌ Carousels/auto-rotating sliders.
- ❌ `window.alert()` for errors — inline form validation only.
- ❌ Dead links (`href="#"`) — link real destinations or disable visually.

### Content
- ❌ Emojis in UI text (greetings, buttons, headers, notifications).
- ❌ AI copywriting clichés ("Elevate", "Seamless", "Unleash", "Next-Gen", "Revolutionary").
- ❌ Generic placeholder names ("John Doe", "Acme Corp", "Nexus").
- ❌ Fake round numbers ("99.99%", "50%") — use real or realistic data.
- ❌ Filler UI text ("Scroll to explore", "Swipe down", bouncing chevrons).
- ❌ Broken Unsplash links — use `picsum.photos` or SVG avatars.

### Z-Index
- ❌ `z-index: 9999` or arbitrary large values — use systematic scale: Base 0 → Dropdown 100 → Sticky 200 → Sidebar 300 → Overlay 400 → Modal 500 → Toast 600 → Tooltip 700.

---

## 10. Design Token Quick Reference (for Stitch)

Copy these CSS custom properties into generated screens for instant design-system compliance:

```css
/* Accent (sole decorative accent — trustworthy healthcare blue) */
--color-primary:             #0050CB;
--color-primary-hover:       #003FA4;
--color-primary-container:   #DAE1FF;

/* Semantic functional colors */
--color-tertiary:            #006825;   /* Living Green — health/success */
--color-secondary:           #A53B18;   /* Warm Clay — warning/attention */
--color-error:               #BA1A1A;   /* Critical Red — danger/emergency */

/* Surface hierarchy (warm-gray, consistent family) */
--color-surface:             #F5F6F9;
--color-surface-container-lowest: #FDFDFC;
--color-surface-container-low:    #F0F2F6;
--color-surface-container-high:   #E4E6EA;
--color-surface-container-highest: #DEE0E4;

/* Text */
--color-on-surface:          #191C1E;   /* Charcoal Ink */
--color-on-surface-variant:  #424656;   /* Muted Steel */
--color-outline-variant:     #C2C6D8;   /* Cloud Border */

/* Typography */
--font-family: 'Geist', 'Satoshi', 'Outfit', -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
--font-mono:   'JetBrains Mono', 'Fira Code', 'Consolas', monospace;

/* Radius */
--radius-sm:    4px;
--radius-md:    8px;
--radius-lg:   14px;
--radius-xl:   20px;
--radius-2xl:  28px;
--radius-full: 9999px;

/* Spacing (8px base) */
--space-xs:  8px;
--space-sm: 12px;
--space-md: 16px;
--space-lg: 24px;
--space-xl: 32px;
--space-2xl: 48px;

/* Touch & layout */
--touch-min:         48px;
--input-min-height:  56px;
--content-max-width: 1280px;
--sidebar-width:     264px;

/* Motion */
--duration-fast:   150ms;
--duration-normal: 250ms;
--duration-slow:   300ms;
--ease-out:        cubic-bezier(0.16, 1, 0.3, 1);

/* Shadows (tinted to brand blue hue) */
--shadow-card: 0 2px 12px rgba(0, 47, 112, 0.04), 0 0 0 1px rgba(0, 47, 112, 0.03);
--shadow-soft-hover: 0 8px 32px rgba(0, 47, 112, 0.10);
```

---

*Generated for Google Stitch screen generation. This DESIGN.md is the single source of truth for the 智伴智慧社区养老 visual language. All new screens, components, and layouts must comply with the rules and bans encoded above. When in doubt, prioritize clarity and warmth over decoration — elderly users deserve interfaces that feel like a helping hand, not a puzzle.*

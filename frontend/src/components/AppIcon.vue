<template>
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    :width="size"
    :height="size"
    fill="none"
    :stroke="strokeColor || 'currentColor'"
    :stroke-width="strokeWidth"
    stroke-linecap="round"
    stroke-linejoin="round"
    :class="className"
  >
    <!-- Dashboard / Overview -->
    <template v-if="name === 'dashboard'">
      <rect x="3" y="3" width="7" height="7" rx="1" />
      <rect x="14" y="3" width="7" height="7" rx="1" />
      <rect x="3" y="14" width="7" height="7" rx="1" />
      <rect x="14" y="14" width="7" height="7" rx="1" />
    </template>

    <!-- Heart / Health -->
    <template v-else-if="name === 'heart'">
      <path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 0 0-7.8 7.8l1 1L12 21l7.8-7.8 1-1a5.5 5.5 0 0 0 0-7.6Z" />
    </template>

    <!-- Heart pulse / Activity -->
    <template v-else-if="name === 'pulse'">
      <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
    </template>

    <!-- Clipboard / Data entry -->
    <template v-else-if="name === 'clipboard'">
      <rect x="8" y="2" width="8" height="4" rx="1" />
      <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2" />
      <line x1="12" y1="11" x2="16" y2="11" />
      <line x1="8" y1="11" x2="9.5" y2="11" />
      <line x1="12" y1="15" x2="16" y2="15" />
      <line x1="8" y1="15" x2="9.5" y2="15" />
    </template>

    <!-- Tool / Services -->
    <template v-else-if="name === 'tool'">
      <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.1 2.1 0 0 1-2.97-2.97L10.3 9.3a6 6 0 0 1 7.94-7.94l-3.54 3.54Z" />
    </template>

    <!-- Package / Orders -->
    <template v-else-if="name === 'package'">
      <path d="M12 2L2 7l10 5 10-5-10-5Z" />
      <path d="M2 17l10 5 10-5" />
      <path d="M2 12l10 5 10-5" />
      <line x1="12" y1="22" x2="12" y2="12" />
    </template>

    <!-- Calendar / Activities -->
    <template v-else-if="name === 'calendar'">
      <rect x="3" y="4" width="18" height="18" rx="2" />
      <line x1="16" y1="2" x2="16" y2="6" />
      <line x1="8" y1="2" x2="8" y2="6" />
      <line x1="3" y1="10" x2="21" y2="10" />
      <rect x="7" y="13" width="3" height="3" rx="0.5" />
      <rect x="14" y="13" width="3" height="3" rx="0.5" />
    </template>

    <!-- Bell / Emergency -->
    <template v-else-if="name === 'bell'">
      <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
      <path d="M13.7 21a2 2 0 0 1-3.4 0" />
    </template>

    <!-- Settings / Smart Home -->
    <template v-else-if="name === 'home'">
      <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2Z" />
      <polyline points="9,22 9,12 15,12 15,22" />
    </template>

    <!-- Message -->
    <template v-else-if="name === 'message'">
      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2Z" />
      <line x1="8" y1="9" x2="16" y2="9" />
      <line x1="8" y1="13" x2="13" y2="13" />
    </template>

    <!-- User / Profile -->
    <template v-else-if="name === 'user'">
      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
      <circle cx="12" cy="7" r="4" />
    </template>

    <!-- Moon / Sleep -->
    <template v-else-if="name === 'moon'">
      <path d="M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8Z" />
    </template>

    <!-- Droplet / Blood -->
    <template v-else-if="name === 'droplet'">
      <path d="M12 2.7 6.3 11a8 8 0 0 0 11.4 0Z" />
      <path d="M12 2.7v0" />
    </template>

    <!-- Thermometer -->
    <template v-else-if="name === 'thermometer'">
      <path d="M14 14.8V4a2 2 0 0 0-4 0v10.8a4 4 0 1 0 4 0Z" />
      <circle cx="12" cy="18" r="2" />
    </template>

    <!-- Foot / Steps -->
    <template v-else-if="name === 'foot'">
      <path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1Z" />
      <path d="M4 22v-7" />
    </template>

    <!-- Pill / Medicine -->
    <template v-else-if="name === 'pill'">
      <path d="M10.5 20.5 3.5 13.5a4.95 4.95 0 1 1 7-7l7 7a4.95 4.95 0 1 1-7 7Z" />
      <path d="M8.5 8.5 15.5 15.5" />
    </template>

    <!-- Sport -->
    <template v-else-if="name === 'sport'">
      <circle cx="12" cy="5" r="2" />
      <path d="M7 21 9.7 9.2 12 11l2.3-1.8L17 21" />
      <path d="M6 14h2l1 4h6l1-4h2" />
    </template>

    <!-- Study / Book -->
    <template v-else-if="name === 'book'">
      <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
      <path d="M4 4.5A2.5 2.5 0 0 1 6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5Z" />
      <line x1="8" y1="7" x2="16" y2="7" />
      <line x1="8" y1="11" x2="14" y2="11" />
    </template>

    <!-- Social / People -->
    <template v-else-if="name === 'people'">
      <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
      <circle cx="9" cy="7" r="4" />
      <path d="M22 21v-2a4 4 0 0 0-3-3.9" />
      <circle cx="19" cy="5" r="2.5" />
    </template>

    <!-- Culture / Palette -->
    <template v-else-if="name === 'palette'">
      <circle cx="12" cy="12" r="10" />
      <path d="M12 2a10 10 0 0 1 0 20" />
      <path d="M12 2a10 10 0 0 0 0 20" />
      <circle cx="8" cy="9" r="1.5" fill="currentColor" />
      <circle cx="16" cy="9" r="1.5" fill="currentColor" />
      <circle cx="8" cy="15" r="1.5" fill="currentColor" />
      <circle cx="12" cy="17" r="1.5" fill="currentColor" />
    </template>

    <!-- Lock (for login) -->
    <template v-else-if="name === 'lock'">
      <rect x="3" y="11" width="18" height="11" rx="2" />
      <path d="M7 11V7a5 5 0 0 1 10 0v4" />
    </template>

    <!-- Eye (for password visibility) — not used directly, but available -->
    <template v-else-if="name === 'eye'">
      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8Z" />
      <circle cx="12" cy="12" r="3" />
    </template>

    <!-- Log out -->
    <template v-else-if="name === 'logout'">
      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
      <polyline points="16,17 21,12 16,7" />
      <line x1="21" y1="12" x2="9" y2="12" />
    </template>

    <!-- Wave / Greeting -->
    <template v-else-if="name === 'wave'">
      <path d="M7 11.5a1 1 0 0 1 1-1h0a1 1 0 0 1 1 1v5a1 1 0 0 1-1 1h0a1 1 0 0 1-1-1Z" />
      <path d="M11 9.5a1 1 0 0 1 1-1h0a1 1 0 0 1 1 1v7a1 1 0 0 1-1 1h0a1 1 0 0 1-1-1Z" />
      <path d="M15 7.5a1 1 0 0 1 1-1h0a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1h0a1 1 0 0 1-1-1Z" />
      <path d="M3 14.5v-1a9 9 0 0 1 18 0v1" />
    </template>

    <!-- Refresh -->
    <template v-else-if="name === 'refresh'">
      <polyline points="23 4 23 10 17 10" />
      <path d="M20.5 15a9 9 0 1 1-2.15-9.36L23 10" />
    </template>

    <!-- Phone -->
    <template v-else-if="name === 'phone'">
      <rect x="5" y="2" width="14" height="20" rx="2" />
      <line x1="12" y1="18" x2="12.01" y2="18" />
    </template>

    <!-- Mail -->
    <template v-else-if="name === 'mail'">
      <rect x="2" y="4" width="20" height="16" rx="2" />
      <path d="m22 4-10 8L2 4" />
    </template>

    <!-- Captcha / Shield-check -->
    <template v-else-if="name === 'captcha'">
      <rect x="3" y="3" width="18" height="18" rx="2" />
      <line x1="9" y1="9" x2="15" y2="9" />
      <line x1="9" y1="13" x2="15" y2="13" />
      <line x1="9" y1="17" x2="12" y2="17" />
    </template>

    <!-- Check -->
    <template v-else-if="name === 'check'">
      <polyline points="20 6 9 17 4 12" />
    </template>

    <!-- Chevron Right -->
    <template v-else-if="name === 'chevron-right'">
      <polyline points="9 18 15 12 9 6" />
    </template>

    <!-- External Link -->
    <template v-else-if="name === 'external-link'">
      <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6" />
      <polyline points="15 3 21 3 21 9" />
      <line x1="10" y1="14" x2="21" y2="3" />
    </template>

    <!-- Clock -->
    <template v-else-if="name === 'clock'">
      <circle cx="12" cy="12" r="10" />
      <polyline points="12 6 12 12 16 14" />
    </template>

    <!-- X / Close -->
    <template v-else-if="name === 'x'">
      <line x1="18" y1="6" x2="6" y2="18" />
      <line x1="6" y1="6" x2="18" y2="18" />
    </template>

    <!-- Star -->
    <template v-else-if="name === 'star'">
      <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
    </template>

    <!-- Map pin -->
    <template v-else-if="name === 'map-pin'">
      <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z" />
      <circle cx="12" cy="10" r="3" />
    </template>

    <!-- Fallback circle -->
    <template v-else>
      <circle cx="12" cy="12" r="10" />
      <line x1="12" y1="8" x2="12" y2="12" />
      <line x1="12" y1="16" x2="12.01" y2="16" />
    </template>
  </svg>
</template>

<script setup>
defineProps({
  name: { type: String, required: true },
  size: { type: [Number, String], default: 24 },
  strokeWidth: { type: [Number, String], default: 2 },
  strokeColor: { type: String, default: '' },
  className: { type: String, default: '' }
})
</script>

<template>
  <div class="w-[55%] p-6">
    <div class="w-full h-full rounded-[2rem] bg-[#4A3AFF] relative overflow-hidden flex flex-col justify-center items-center text-center p-12">
      <!-- Decorative elements -->
      <div class="absolute top-12 left-12 w-3 h-3 rounded-full bg-white opacity-60"></div>
      <div class="absolute top-24 right-24 w-2 h-2 rounded-full bg-white opacity-80"></div>
      <div class="absolute bottom-40 left-32 w-2 h-2 rounded-full bg-white opacity-40"></div>
      <div class="absolute bottom-20 right-20 w-4 h-4 rounded-full bg-[#00E5FF] opacity-80"></div>
      <svg class="absolute top-1/4 left-1/4 w-48 h-48 text-white/10" fill="none" stroke="currentColor" stroke-width="1" viewBox="0 0 100 100">
        <polygon points="50,10 90,90 10,90"></polygon>
      </svg>
      <!-- Planet/Globe -->
      <div class="relative w-80 h-80 mb-16">
        <div class="absolute inset-0 m-auto w-40 h-40 rounded-full bg-gradient-to-br from-[#00E5FF] to-[#0055FF] border-[3px] border-black flex items-center justify-center z-10 shadow-2xl">
          <div class="absolute w-52 h-12 bg-white border-[3px] border-black rounded-full flex items-center justify-center transform -rotate-12 z-20">
            <span class="font-bold text-black text-lg uppercase tracking-widest">智伴科技</span>
          </div>
        </div>
        <!-- Floating elements -->
        <svg class="absolute top-0 right-0 w-32 h-32 transform rotate-12 z-0" fill="none" viewBox="0 0 100 100">
          <ellipse cx="50" cy="50" fill="black" rx="30" ry="10" transform="rotate(-30 50 50)"></ellipse>
          <path d="M70 30 C 50 40, 40 60, 20 50" fill="none" stroke="#D1D5DB" stroke-width="8"></path>
        </svg>
        <svg class="absolute bottom-0 left-0 w-32 h-32 transform -rotate-12 z-0" fill="none" viewBox="0 0 100 100">
          <ellipse cx="50" cy="50" fill="black" rx="30" ry="10" transform="rotate(30 50 50)"></ellipse>
          <path d="M30 70 C 50 60, 60 40, 80 50" fill="none" stroke="#D1D5DB" stroke-width="8"></path>
        </svg>
        <!-- Ai Text -->
        <div class="absolute top-2 left-12 text-white font-serif italic text-6xl z-20 opacity-90">Ai</div>
      </div>
      <div class="relative z-10 max-w-md">
        <h3 class="text-5xl font-bold text-white mb-4 leading-tight">拥有您的<br>专属智慧护工</h3>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

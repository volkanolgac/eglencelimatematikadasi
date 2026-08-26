// Lightweight audio engine using the Web Audio API + Turkish TTS fallback engine.
// All sounds are synthesized or browser-native — no external assets required.

let ctx: AudioContext | null = null;

function getCtx(): AudioContext | null {
  if (typeof window === 'undefined') return null;
  if (!ctx) {
    try {
      ctx = new (window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext)();
    } catch {
      return null;
    }
  }
  if (ctx.state === 'suspended') ctx.resume().catch(() => {});
  return ctx;
}

let soundOn = true;
let musicOn = true;

export function setSoundEnabled(on: boolean) { soundOn = on; }
export function setMusicEnabled(on: boolean) { musicOn = on; }

function tone(freq: number, start: number, dur: number, type: OscillatorType = 'sine', vol = 0.18) {
  const c = getCtx();
  if (!c) return;
  const osc = c.createOscillator();
  const gain = c.createGain();
  osc.type = type;
  osc.frequency.value = freq;
  const t0 = c.currentTime + start;
  gain.gain.setValueAtTime(0, t0);
  gain.gain.linearRampToValueAtTime(vol, t0 + 0.01);
  gain.gain.exponentialRampToValueAtTime(0.001, t0 + dur);
  osc.connect(gain).connect(c.destination);
  osc.start(t0);
  osc.stop(t0 + dur + 0.05);
}

const PRAISE = ['Harika!', 'Süpersin!', 'Muhteşem!', 'Bravo Kaşif!', 'Çok iyi!', 'Mükemmel!'];

export const COMBO_ENGLISH_PRAISES = [
  'Great job!',
  'Well done!',
  'Awesome!',
  'Excellent!',
  'Amazing!',
  'You did it!',
];

// Voice Cache & Detection
let cachedVoices: SpeechSynthesisVoice[] = [];

function refreshVoices() {
  if (typeof window !== 'undefined' && window.speechSynthesis) {
    try {
      cachedVoices = window.speechSynthesis.getVoices() || [];
    } catch {
      cachedVoices = [];
    }
  }
}

if (typeof window !== 'undefined' && window.speechSynthesis) {
  refreshVoices();
  window.speechSynthesis.onvoiceschanged = () => {
    refreshVoices();
  };
}

const ENGLISH_FEMALE_KEYWORDS = [
  'female', 'woman', 'samantha', 'karen', 'victoria', 'zira', 'jenny',
  'ava', 'allison', 'susan', 'cathy', 'fiona', 'stephanie', 'moira',
  'veena', 'tessa', 'serena', 'joanna', 'salli', 'ivy', 'kendra',
  'kimberly', 'catherine', 'google us english', 'microsoft zira',
  'en-us-standard-c', 'en-us-wavenet-c', 'en-us-neural2-f',
  'en-gb-wavenet-a', 'en-gb-neural2-a', 'natural (female)', 'natural female',
  'online (natural)'
];

export function getEnglishVoice(): SpeechSynthesisVoice | null {
  if (typeof window === 'undefined' || !window.speechSynthesis) return null;
  if (!cachedVoices || cachedVoices.length === 0) {
    refreshVoices();
  }

  // Filter all English voices
  const enVoices = cachedVoices.filter((v) => {
    const lang = (v.lang || '').toLowerCase().replace(/_/g, '-');
    return lang.startsWith('en-') || lang === 'en';
  });

  if (enVoices.length === 0) {
    return null;
  }

  // 1. Prioritize en-US and en-GB voices
  const priorityEnVoices = enVoices.filter((v) => {
    const lang = (v.lang || '').toLowerCase().replace(/_/g, '-');
    return lang === 'en-us' || lang === 'en-gb' || lang.startsWith('en-us') || lang.startsWith('en-gb');
  });

  const searchPool = priorityEnVoices.length > 0 ? priorityEnVoices : enVoices;

  // 2. Look for female English voice first in preferred pool
  const femaleVoice = searchPool.find((v) => {
    const name = (v.name || '').toLowerCase();
    return ENGLISH_FEMALE_KEYWORDS.some((kw) => name.includes(kw));
  });

  if (femaleVoice) {
    return femaleVoice;
  }

  // 3. Look for default English voice in preferred pool
  const defaultEn = searchPool.find((v) => v.default) || searchPool[0];
  if (defaultEn) {
    return defaultEn;
  }

  // 4. Return any available English voice
  return enVoices[0] || null;
}

let lastComboSpeakTime = 0;

export function speakEnglish(text: string) {
  if (!soundOn || typeof window === 'undefined' || !window.speechSynthesis) return;

  const now = Date.now();
  // Prevent duplicate spam on the same frame / rapid renders
  if (now - lastComboSpeakTime < 300) return;
  lastComboSpeakTime = now;

  try {
    window.speechSynthesis.cancel();

    const u = new SpeechSynthesisUtterance(text);
    u.lang = 'en-US';
    u.rate = 1.0;
    u.pitch = 1.15; // Cheerful, child-friendly high pitch

    const voice = getEnglishVoice();
    if (voice) {
      u.voice = voice;
      if (voice.lang) u.lang = voice.lang;
    }

    window.speechSynthesis.speak(u);
  } catch {
    // Ignore TTS errors safely
  }
}

export const audio = {
  unlock() {
    getCtx();
    refreshVoices();
  },

  correct() {
    if (!soundOn) return;
    tone(523.25, 0, 0.12, 'sine', 0.2);    // C5
    tone(659.25, 0.08, 0.12, 'sine', 0.2);  // E5
    tone(783.99, 0.16, 0.18, 'sine', 0.22); // G5
  },

  wrong() {
    if (!soundOn) return;
    tone(311.13, 0, 0.18, 'sawtooth', 0.12);
    tone(233.08, 0.12, 0.22, 'sawtooth', 0.12);
  },

  gameOver() {
    if (!soundOn) return;
    // Melodic, sorrowful descending tone sequence for Game Over
    tone(329.63, 0, 0.3, 'triangle', 0.18);    // E4
    tone(293.66, 0.25, 0.3, 'triangle', 0.18);  // D4
    tone(261.63, 0.5, 0.35, 'triangle', 0.18);  // C4
    tone(220.00, 0.8, 0.6, 'triangle', 0.2);   // A3
  },

  victory() {
    if (!soundOn) return;
    // Celebratory triumph fanfare for completing a level
    tone(523.25, 0, 0.14, 'triangle', 0.22);    // C5
    tone(659.25, 0.10, 0.14, 'triangle', 0.22); // E5
    tone(783.99, 0.20, 0.16, 'triangle', 0.24); // G5
    tone(1046.5, 0.32, 0.25, 'triangle', 0.26); // C6
    tone(1318.5, 0.45, 0.35, 'triangle', 0.28); // E6
    tone(1567.9, 0.60, 0.60, 'sine', 0.25);     // G6 chord finish
    tone(1046.5, 0.60, 0.60, 'triangle', 0.18); // C6 resonance
  },

  click() {
    if (!soundOn) return;
    tone(440, 0, 0.05, 'square', 0.08);
  },

  diamond() {
    if (!soundOn) return;
    tone(987.77, 0, 0.08, 'triangle', 0.16);
    tone(1318.51, 0.06, 0.12, 'triangle', 0.16);
  },

  chest() {
    if (!soundOn) return;
    tone(523.25, 0, 0.1, 'triangle', 0.18);
    tone(659.25, 0.1, 0.1, 'triangle', 0.18);
    tone(783.99, 0.2, 0.1, 'triangle', 0.18);
    tone(1046.5, 0.3, 0.25, 'triangle', 0.2);
  },

  combo(level: number): string {
    return audio.speakCombo(level);
  },

  praise(): string {
    return PRAISE[Math.floor(Math.random() * PRAISE.length)];
  },

  speakCombo(level?: number): string {
    if (level && soundOn) {
      const base = 523.25;
      for (let i = 0; i < Math.min(level, 5); i++) {
        tone(base * Math.pow(1.2, i), i * 0.06, 0.1, 'triangle', 0.16);
      }
    }
    const phrase = COMBO_ENGLISH_PRAISES[Math.floor(Math.random() * COMBO_ENGLISH_PRAISES.length)];
    speakEnglish(phrase);
    return phrase;
  },

  speak(text: string) {
    // English TTS for any voice prompts
    speakEnglish(text);
  },
};

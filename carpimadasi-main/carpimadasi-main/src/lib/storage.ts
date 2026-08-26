import type { SaveState, IslandProgress, LevelProgress, SessionStat } from '@/types';

const KEY = 'carpim-adasi-save-v1';

function today(): string {
  return new Date().toISOString().slice(0, 10);
}

function makeIsland(): IslandProgress {
  const levels: LevelProgress[] = [];
  for (let i = 0; i < 10; i++) {
    levels.push({ stars: 0, bestStreak: 0, completed: false });
  }
  return { levels };
}

export function syncIslandUnlocks(state: SaveState): void {
  if (!Array.isArray(state.unlockedIslands)) {
    state.unlockedIslands = [1];
  }
  if (!state.unlockedIslands.includes(1)) {
    state.unlockedIslands.push(1);
  }
  // Check if any level is completed in island T, then T+1 unlocks
  for (let t = 1; t <= 8; t++) {
    const island = state.islands[t];
    const hasCompletedLevel = Boolean(island && island.levels && island.levels.some((l) => l.completed));
    if (hasCompletedLevel && !state.unlockedIslands.includes(t + 1)) {
      state.unlockedIslands.push(t + 1);
    }
  }
  // Clean duplicates and sort
  state.unlockedIslands = Array.from(new Set(state.unlockedIslands)).sort((a, b) => a - b);
}

export function defaultSave(): SaveState {
  const base: SaveState = {
    diamonds: 0,
    unlockedIslands: [1],
    islands: Object.fromEntries(
      [1, 2, 3, 4, 5, 6, 7, 8, 9].map((n) => [n, makeIsland()])
    ),
    ownedCosmetics: ['avatar_classic', 'color_amber', 'theme_sky'],
    equipped: { avatar: 'avatar_classic', hat: null, pet: null, color: 'color_amber', theme: 'theme_sky' },
    soundEnabled: true,
    musicEnabled: true,
    sessions: [],
    streak: 0,
    lastPlayDate: null,
    parentPin: '1234',
    badges: [],
  };
  syncIslandUnlocks(base);
  return base;
}

export function loadSave(): SaveState {
  try {
    const raw = localStorage.getItem(KEY);
    if (!raw) return defaultSave();
    const parsed = JSON.parse(raw) as Partial<SaveState>;
    const base = defaultSave();
    const equipped = { ...base.equipped, ...(parsed.equipped ?? {}) };
    if (!equipped.avatar) equipped.avatar = 'avatar_classic';
    // Normalize color if legacy 'amber' was stored
    if (equipped.color && !equipped.color.startsWith('color_') && !['amber','sky','mint','coral','grape'].includes(equipped.color)) {
      equipped.color = 'color_amber';
    }
    const owned = Array.isArray(parsed.ownedCosmetics) ? [...parsed.ownedCosmetics] : [];
    if (!owned.includes('avatar_classic')) owned.push('avatar_classic');
    if (!owned.includes('color_amber')) owned.push('color_amber');
    if (!owned.includes('theme_sky')) owned.push('theme_sky');

    const loaded: SaveState = {
      ...base,
      ...parsed,
      parentPin: typeof parsed.parentPin === 'string' && parsed.parentPin.trim().length === 4 ? parsed.parentPin.trim() : '1234',
      islands: parsed.islands ?? base.islands,
      unlockedIslands: Array.isArray(parsed.unlockedIslands) ? parsed.unlockedIslands : [1],
      ownedCosmetics: owned,
      equipped,
    };
    syncIslandUnlocks(loaded);
    return loaded;
  } catch {
    return defaultSave();
  }
}

export function saveSave(state: SaveState): void {
  try {
    localStorage.setItem(KEY, JSON.stringify(state));
  } catch {
    /* ignore quota errors */
  }
}

export function getTodaySession(state: SaveState): SessionStat {
  const t = today();
  let s = state.sessions.find((x) => x.date === t);
  if (!s) {
    s = { date: t, minutesPlayed: 0, questionsAnswered: 0, correctCount: 0, wrongCount: 0, wrongRecords: [] };
    state.sessions.push(s);
    // keep last 60 days
    if (state.sessions.length > 60) state.sessions = state.sessions.slice(-60);
  }
  return s;
}

export function updateStreak(state: SaveState): void {
  const t = today();
  if (state.lastPlayDate === t) return;
  const yesterday = new Date(Date.now() - 86400000).toISOString().slice(0, 10);
  if (state.lastPlayDate === yesterday) {
    state.streak += 1;
  } else {
    state.streak = 1;
  }
  state.lastPlayDate = t;
  if (state.streak >= 7 && !state.badges.includes('streak7')) {
    state.badges.push('streak7');
  }
}

export { today };

export type Screen =
  | 'home'
  | 'map'
  | 'game'
  | 'shop'
  | 'settings'
  | 'parent'
  | 'chest'
  | 'minigame';

export type QuestionFormat =
  | 'standard'        // a × b = ?
  | 'reversed'        // b × a = ?
  | 'missing_first'   // ? × b = c
  | 'missing_second'  // a × ? = c
  | 'visual';         // word problem with groups

export interface Question {
  a: number;
  b: number;
  answer: number;
  format: QuestionFormat;
  options: number[];
  prompt: string;
  subPrompt?: string;
}

export interface LevelProgress {
  stars: number;       // 0-3
  bestStreak: number;
  completed: boolean;
}

export interface IslandProgress {
  // index 0..9 -> level 1..10
  levels: LevelProgress[];
}

export interface WrongRecord {
  // key "a xb" -> times wrong
  key: string;
  a: number;
  b: number;
  count: number;
}

export interface SessionStat {
  date: string;        // YYYY-MM-DD
  minutesPlayed: number;
  questionsAnswered: number;
  correctCount: number;
  wrongCount: number;
  wrongRecords: WrongRecord[];
}

export interface ParentStats {
  todayMinutes: number;
  todayQuestions: number;
  todayCorrect: number;
  totalQuestions: number;
  hardest: WrongRecord[];
  completedIslands: number[];
  history: { date: string; minutes: number; correct: number; questions: number }[];
  streak: number;
}

export interface CosmeticItem {
  id: string;
  name: string;
  type: 'avatar' | 'hat' | 'pet' | 'color' | 'theme';
  price: number;
  emoji: string;
}

export interface SaveState {
  diamonds: number;
  unlockedIslands: number[];      // table numbers 1..9
  islands: Record<number, IslandProgress>;
  ownedCosmetics: string[];
  equipped: { avatar: string; hat: string | null; pet: string | null; color: string; theme: string | null };
  soundEnabled: boolean;
  musicEnabled: boolean;
  sessions: SessionStat[];
  streak: number;
  lastPlayDate: string | null;
  parentPin: string;
  badges: string[];
}

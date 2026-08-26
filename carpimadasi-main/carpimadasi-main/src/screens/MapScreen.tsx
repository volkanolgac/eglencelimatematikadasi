import { motion } from 'motion/react';
import { DiamondBadge } from '@/components/ui';
import { audio } from '@/lib/audio';
import type { SaveState } from '@/types';
import { useState } from 'react';

interface MapScreenProps {
  save: SaveState;
  onSelect: (table: number, level: number) => void;
  onBack: () => void;
}

const ISLAND_THEMES: Record<number, { emoji: string; bg: string; name: string; shortName: string }> = {
  1: { emoji: '🌴', bg: 'from-sky-300 to-sky-400', name: "1'ler Adası", shortName: "1'ler" },
  2: { emoji: '🏝️', bg: 'from-emerald-300 to-emerald-400', name: "2'ler Adası", shortName: "2'ler" },
  3: { emoji: '🌋', bg: 'from-orange-300 to-orange-400', name: "3'ler Adası", shortName: "3'ler" },
  4: { emoji: '🏖️', bg: 'from-amber-300 to-amber-400', name: "4'ler Adası", shortName: "4'ler" },
  5: { emoji: '🌳', bg: 'from-lime-300 to-lime-400', name: "5'ler Adası", shortName: "5'ler" },
  6: { emoji: '🌈', bg: 'from-pink-300 to-pink-400', name: "6'ler Adası", shortName: "6'ler" },
  7: { emoji: '⛰️', bg: 'from-cyan-300 to-cyan-400', name: "7'ler Adası", shortName: "7'ler" },
  8: { emoji: '👑', bg: 'from-violet-300 to-violet-400', name: "8'ler Adası", shortName: "8'ler" },
  9: { emoji: '🏰', bg: 'from-rose-300 to-rose-400', name: "9'lar Adası", shortName: "9'lar" },
};

export function MapScreen({ save, onSelect, onBack }: MapScreenProps) {
  const [selectedIsland, setSelectedIsland] = useState<number | null>(null);
  const tables = [1, 2, 3, 4, 5, 6, 7, 8, 9];

  return (
    <div className="relative min-h-screen flex flex-col safe-top safe-bottom px-3 py-2 max-w-lg mx-auto w-full select-none">
      {/* Header */}
      <div className="flex items-center justify-between mb-2">
        <button
          id="map-back-btn"
          onClick={() => { audio.click(); onBack(); }}
          className="bg-white/90 shadow-md rounded-2xl w-10 h-10 flex items-center justify-center text-xl btn-3d active:scale-95"
        >
          ⬅️
        </button>
        <h2 className="text-xl sm:text-2xl font-black text-white drop-shadow-[0_2px_3px_rgba(0,0,0,0.3)]">
          {selectedIsland ? ISLAND_THEMES[selectedIsland].name : 'Adalar'}
        </h2>
        <DiamondBadge count={save.diamonds} />
      </div>

      {!selectedIsland ? (
        <div className="flex-1 flex flex-col justify-center my-auto">
          {/* 
            3x3 Compact Island Grid:
            Scaled down by ~20% in area with proportional fonts, emojis, and padding.
          */}
          <div className="grid grid-cols-3 gap-2 sm:gap-2.5 max-w-xs sm:max-w-sm mx-auto w-full my-auto">
            {tables.map((t, i) => {
              const unlocked = save.unlockedIslands.includes(t);
              const theme = ISLAND_THEMES[t];
              const island = save.islands[t];
              const completedLevels = island?.levels.filter((l) => l.completed).length ?? 0;

              return (
                <motion.button
                  key={t}
                  id={`island-card-${t}`}
                  initial={{ scale: 0, y: 15 }}
                  animate={{ scale: 1, y: 0 }}
                  transition={{ delay: i * 0.03, type: 'spring', stiffness: 160 }}
                  whileTap={{ scale: 0.93 }}
                  disabled={!unlocked}
                  onClick={() => {
                    if (unlocked) {
                      audio.click();
                      setSelectedIsland(t);
                    }
                  }}
                  className={`relative rounded-2xl bg-gradient-to-b ${theme.bg} p-1.5 sm:p-2 aspect-square flex flex-col items-center justify-between
                    border-2 sm:border-3 border-white/80 shadow-md btn-3d transition-transform ${
                      unlocked ? 'cursor-pointer' : 'opacity-60 grayscale cursor-not-allowed'
                    }`}
                >
                  {/* Top indicator / badge */}
                  <div className="w-full flex justify-end">
                    {unlocked ? (
                      <span className="text-[9px] sm:text-[10px] font-black bg-white/50 text-slate-800 px-1.5 py-0.5 rounded-full leading-none">
                        {completedLevels}/10
                      </span>
                    ) : (
                      <span className="text-[10px] sm:text-xs">🔒</span>
                    )}
                  </div>

                  {/* Center emoji */}
                  <span className="text-2xl sm:text-3xl filter drop-shadow-xs select-none my-auto">
                    {unlocked ? theme.emoji : '🔒'}
                  </span>

                  {/* Bottom Island Name */}
                  <div className="w-full text-center">
                    <span className="font-extrabold text-white text-[11px] sm:text-xs drop-shadow-[0_1px_2px_rgba(0,0,0,0.4)] whitespace-nowrap block">
                      {theme.shortName}
                    </span>
                  </div>
                </motion.button>
              );
            })}
          </div>
        </div>
      ) : (
        <LevelSelect
          table={selectedIsland}
          save={save}
          onSelect={(lvl) => { audio.click(); onSelect(selectedIsland, lvl); }}
          onBack={() => setSelectedIsland(null)}
        />
      )}
    </div>
  );
}

function LevelSelect({
  table,
  save,
  onSelect,
  onBack,
}: {
  table: number;
  save: SaveState;
  onSelect: (lvl: number) => void;
  onBack: () => void;
}) {
  const theme = ISLAND_THEMES[table];
  const island = save.islands[table];
  const levels = island?.levels ?? [];

  return (
    <div className="flex-1 flex flex-col justify-center my-auto">
      {/* Subheader bar */}
      <div className="flex items-center justify-between bg-white/85 backdrop-blur-xs rounded-2xl px-3 py-1.5 mb-2 shadow-xs">
        <button
          id="level-back-btn"
          onClick={() => { audio.click(); onBack(); }}
          className="bg-white rounded-xl px-2.5 py-1 text-xs font-bold text-slate-700 shadow-xs btn-3d"
        >
          ⬅️ Adalar
        </button>
        <span className="font-black text-slate-800 text-xs sm:text-sm flex items-center gap-1">
          <span>{theme.emoji}</span> {theme.name}
        </span>
        <span className="text-xs font-bold text-slate-500">
          {levels.filter((l) => l.completed).length}/10 Bölüm
        </span>
      </div>

      {/* 5x2 Compact, Rounded Square Level Grid (2 Rows fits on all screens) */}
      <div className="grid grid-cols-5 gap-1.5 sm:gap-2.5 max-w-xs sm:max-w-sm mx-auto w-full my-auto">
        {levels.map((lvl, i) => {
          const prevDone = i === 0 || levels[i - 1].completed;
          const playable = prevDone;

          return (
            <motion.button
              key={i}
              id={`level-btn-${i + 1}`}
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ delay: i * 0.03, type: 'spring', stiffness: 160 }}
              whileTap={{ scale: 0.92 }}
              disabled={!playable}
              onClick={() => playable && onSelect(i)}
              className={`rounded-2xl p-1 sm:p-1.5 aspect-square flex flex-col items-center justify-between border-2 shadow-xs btn-3d transition-transform ${
                lvl.completed
                  ? 'bg-amber-100 border-amber-300 text-amber-900'
                  : playable
                  ? 'bg-white/95 border-sky-300 text-sky-900'
                  : 'bg-slate-200/80 border-slate-300 text-slate-400 opacity-60 cursor-not-allowed'
              }`}
            >
              {/* Level index */}
              <span className="text-[10px] font-bold opacity-75 leading-none">#{i + 1}</span>

              {/* Center status */}
              <div className="my-auto flex flex-col items-center">
                {playable ? (
                  <span className="text-base sm:text-lg font-black leading-none">{i + 1}</span>
                ) : (
                  <span className="text-sm sm:text-base">🔒</span>
                )}
              </div>

              {/* Stars earned */}
              <div className="flex items-center justify-center gap-0.5">
                {lvl.completed ? (
                  [0, 1, 2].map((s) => (
                    <span
                      key={s}
                      className={`text-[8px] leading-none ${s < lvl.stars ? 'text-amber-500' : 'text-slate-300'}`}
                    >
                      ★
                    </span>
                  ))
                ) : (
                  <span className="text-[8px] font-bold text-slate-400 leading-none">
                    {playable ? 'BAŞLA' : 'KİLİTLİ'}
                  </span>
                )}
              </div>
            </motion.button>
          );
        })}
      </div>
    </div>
  );
}

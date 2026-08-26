import { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import confetti from 'canvas-confetti';
import { Stars } from '@/components/ui';
import { audio } from '@/lib/audio';
import { BADGE_INFO } from '@/lib/cosmetics';

interface ChestScreenProps {
  stars: number;
  diamonds: number;
  bestStreak: number;
  newBadge?: string | null;
  onContinue: () => void;
}

export function ChestScreen({ stars, diamonds, bestStreak, newBadge, onContinue }: ChestScreenProps) {
  const [opened, setOpened] = useState(false);
  const [showRewards, setShowRewards] = useState(false);

  useEffect(() => {
    audio.unlock();

    // Trigger triumphant victory fanfare & confetti celebration!
    audio.victory();

    try {
      confetti({
        particleCount: 70,
        spread: 65,
        origin: { y: 0.6 },
        colors: ['#fbbf24', '#38bdf8', '#f43f5e', '#34d399', '#c084fc'],
        ticks: 200,
        disableForReducedMotion: true,
      });
    } catch {
      // safe fallback
    }

    const t1 = setTimeout(() => {
      setOpened(true);
      audio.chest();

      // Second light burst on chest opening
      try {
        confetti({
          particleCount: 45,
          spread: 80,
          origin: { y: 0.5 },
          colors: ['#facc15', '#60a5fa', '#fb7185'],
          ticks: 180,
        });
      } catch {
        // safe fallback
      }
    }, 600);

    const t2 = setTimeout(() => setShowRewards(true), 1400);

    return () => {
      clearTimeout(t1);
      clearTimeout(t2);
    };
  }, []);

  return (
    <div className="relative h-[100dvh] max-h-[100dvh] overflow-hidden flex flex-col items-center justify-between safe-top safe-bottom px-4 py-3 sm:py-6 select-none max-w-sm mx-auto w-full">
      {/* Top Header & Stars */}
      <div className="flex flex-col items-center w-full shrink-0">
        <motion.h2
          initial={{ y: -20, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          className="text-2xl sm:text-3xl font-extrabold text-white drop-shadow-[0_2px_4px_rgba(0,0,0,0.35)] text-center mb-1"
        >
          🎉 Bölüm Tamamlandı!
        </motion.h2>

        <div className="my-1">
          <Stars stars={stars} size={36} />
        </div>
      </div>

      {/* Center Gift / Chest */}
      <div className="relative flex-1 flex items-center justify-center min-h-0 w-full py-1 my-auto">
        <div className="relative flex items-center justify-center">
          <motion.div
            animate={opened ? { rotate: [0, -12, 10, -5, 0], scale: [1, 1.15, 1] } : {}}
            transition={{ duration: 0.5 }}
            className="text-6xl sm:text-7xl md:text-8xl select-none filter drop-shadow-md"
          >
            {opened ? '🎁' : '🧰'}
          </motion.div>
          <AnimatePresence>
            {opened && (
              <>
                <motion.span initial={{ y: 0, opacity: 0 }} animate={{ y: -45, opacity: [0, 1, 0] }} transition={{ duration: 1.2 }} className="absolute text-2xl select-none" style={{ left: '-20%' }}>✨</motion.span>
                <motion.span initial={{ y: 0, opacity: 0 }} animate={{ y: -40, opacity: [0, 1, 0] }} transition={{ duration: 1, delay: 0.2 }} className="absolute text-2xl select-none" style={{ right: '-20%' }}>⭐</motion.span>
              </>
            )}
          </AnimatePresence>
        </div>
      </div>

      {/* Bottom Rewards & Continue Button */}
      <div className="w-full shrink-0 flex flex-col items-center">
        <AnimatePresence>
          {showRewards && (
            <motion.div
              initial={{ scale: 0.85, opacity: 0, y: 20 }}
              animate={{ scale: 1, opacity: 1, y: 0 }}
              transition={{ type: 'spring', stiffness: 150 }}
              className="flex flex-col items-center gap-1.5 sm:gap-2 w-full"
            >
              <div className="bg-white/95 rounded-xl sm:rounded-2xl px-4 py-1.5 sm:py-2 flex items-center gap-2.5 border-2 border-sky-200 shadow-sm w-full justify-center">
                <span className="text-2xl">💎</span>
                <span className="text-base sm:text-lg font-extrabold text-sky-600">+{diamonds} Elmas</span>
              </div>
              <div className="bg-white/95 rounded-xl sm:rounded-2xl px-4 py-1.5 sm:py-2 flex items-center gap-2.5 border-2 border-orange-200 shadow-sm w-full justify-center">
                <span className="text-2xl">🔥</span>
                <span className="text-base sm:text-lg font-extrabold text-orange-500">En İyi Seri: {bestStreak}</span>
              </div>
              {newBadge && BADGE_INFO[newBadge] && (
                <motion.div
                  initial={{ scale: 0, rotate: -10 }}
                  animate={{ scale: 1, rotate: 0 }}
                  transition={{ delay: 0.2, type: 'spring' }}
                  className="bg-gradient-to-r from-amber-300 to-amber-500 rounded-xl sm:rounded-2xl px-4 py-1.5 sm:py-2 flex items-center gap-2.5 border-2 border-white shadow-sm w-full"
                >
                  <span className="text-2xl">{BADGE_INFO[newBadge].emoji}</span>
                  <div>
                    <div className="text-[9px] font-black text-white/90">YENİ ROZET!</div>
                    <div className="text-xs sm:text-sm font-extrabold text-white">{BADGE_INFO[newBadge].name}</div>
                  </div>
                </motion.div>
              )}
              <motion.button
                id="chest-continue-btn"
                whileTap={{ scale: 0.95 }}
                whileHover={{ scale: 1.02 }}
                onClick={() => { audio.click(); onContinue(); }}
                className="w-full bg-gradient-to-r from-emerald-400 to-emerald-500 hover:from-emerald-500 hover:to-emerald-600 text-white font-black rounded-2xl py-3 sm:py-3.5 text-base sm:text-lg shadow-lg btn-3d active:scale-95 transition-all mt-1"
              >
                Devam Et ➡️
              </motion.button>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </div>
  );
}

import { motion } from 'motion/react';
import { Explorer } from '@/components/Explorer';
import { BigButton, DiamondBadge } from '@/components/ui';
import { audio } from '@/lib/audio';
import type { SaveState } from '@/types';

interface HomeScreenProps {
  save: SaveState;
  onPlay: () => void;
  onShop: () => void;
  onSettings: () => void;
  onParent: () => void;
}

export function HomeScreen({ save, onPlay, onShop, onSettings, onParent }: HomeScreenProps) {
  return (
    <div className="relative min-h-screen flex flex-col items-center justify-between safe-top safe-bottom px-6 py-8">
      <div className="w-full flex items-center justify-between">
        <DiamondBadge count={save.diamonds} />
        <button
          onClick={() => { audio.click(); onSettings(); }}
          className="bg-white/80 backdrop-blur rounded-full w-12 h-12 flex items-center justify-center text-2xl btn-3d"
        >
          ⚙️
        </button>
      </div>

      <div className="flex flex-col items-center gap-2">
        <motion.h1
          initial={{ y: -30, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          transition={{ type: 'spring', stiffness: 120 }}
          className="text-5xl font-extrabold text-white text-stroke-white text-center"
          style={{ WebkitTextStroke: '3px #0ea5e9' }}
        >
          Çarpım Adası
        </motion.h1>
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.3 }}
          className="text-lg font-bold text-white/90"
        >
          Oyun oynarken çarpım tablosunu öğren!
        </motion.p>
      </div>

      <motion.div
        initial={{ scale: 0 }}
        animate={{ scale: 1 }}
        transition={{ type: 'spring', stiffness: 140, delay: 0.2 }}
        className="my-4"
      >
        <Explorer
          avatar={save.equipped.avatar}
          color={save.equipped.color}
          hat={save.equipped.hat}
          pet={save.equipped.pet}
          size={180}
        />
      </motion.div>

      <div className="w-full max-w-sm flex flex-col gap-3 pb-4">
        <BigButton
          onClick={() => { audio.click(); onPlay(); }}
          color="bg-sand-deep"
          className="w-full text-2xl py-5"
        >
          🚀 Oyna
        </BigButton>
        <div className="flex gap-3">
          <BigButton
            onClick={() => { audio.click(); onShop(); }}
            color="bg-coral"
            className="flex-1"
          >
            🛍️ Mağaza
          </BigButton>
          <BigButton
            onClick={() => { audio.click(); onParent(); }}
            color="bg-island-deep"
            className="flex-1"
          >
            👨‍👩‍👧 Veli
          </BigButton>
        </div>
      </div>

      {save.streak > 0 && (
        <div className="absolute top-20 right-4 bg-white/80 rounded-full px-3 py-1.5 font-bold text-orange-500 text-sm flex items-center gap-1">
          🔥 {save.streak} gün
        </div>
      )}
    </div>
  );
}

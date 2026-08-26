import { motion } from 'motion/react';
import { audio } from '@/lib/audio';
import { BADGE_INFO } from '@/lib/cosmetics';
import type { SaveState } from '@/types';

interface SettingsScreenProps {
  save: SaveState;
  onToggleSound: () => void;
  onToggleMusic: () => void;
  onReset: () => void;
  onBack: () => void;
}

export function SettingsScreen({ save, onToggleSound, onToggleMusic, onReset, onBack }: SettingsScreenProps) {
  return (
    <div className="relative min-h-screen flex flex-col safe-top safe-bottom px-4 py-4">
      <div className="flex items-center justify-between mb-4">
        <button onClick={() => { audio.click(); onBack(); }} className="bg-white/80 rounded-full w-11 h-11 flex items-center justify-center text-xl btn-3d">⬅️</button>
        <h2 className="text-2xl font-extrabold text-white text-stroke-white" style={{ WebkitTextStroke: '2px #0ea5e9' }}>Ayarlar</h2>
        <div className="w-11" />
      </div>

      <div className="flex flex-col gap-3 max-w-md w-full mx-auto">
        <ToggleRow label="🔊 Ses Efektleri" enabled={save.soundEnabled} onToggle={() => { audio.click(); onToggleSound(); }} />
        <ToggleRow label="🎵 Müzik & Ritim" enabled={save.musicEnabled} onToggle={() => { audio.click(); onToggleMusic(); }} />

        {/* badges */}
        <div className="bg-white/90 rounded-2xl p-4 border-4 border-white/60 mt-2">
          <h3 className="font-extrabold text-ink mb-2">🏆 Rozetlerim</h3>
          {save.badges.length === 0 ? (
            <p className="text-sm text-ink-soft">Henüz rozet yok. Oynamaya devam et!</p>
          ) : (
            <div className="grid grid-cols-2 gap-2">
              {save.badges.map((b) => {
                const info = BADGE_INFO[b];
                if (!info) return null;
                return (
                  <div key={b} className="flex items-center gap-2 bg-amber-100 rounded-xl px-3 py-2">
                    <span className="text-2xl">{info.emoji}</span>
                    <span className="text-xs font-bold text-ink">{info.name}</span>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        <button
          onClick={() => { if (confirm('Tüm ilerleme silinsin mi? Bu geri alınamaz!')) { audio.wrong(); onReset(); } }}
          className="mt-2 bg-coral text-white font-bold rounded-2xl py-3 btn-3d border-2 border-white/40"
        >
          🗑️ İlerlemeyi Sıfırla
        </button>
      </div>
    </div>
  );
}

function ToggleRow({ label, enabled, onToggle }: { label: string; enabled: boolean; onToggle: () => void; }) {
  return (
    <div className="bg-white/90 rounded-2xl p-4 flex items-center justify-between border-4 border-white/60">
      <span className="font-bold text-ink">{label}</span>
      <motion.button
        whileTap={{ scale: 0.9 }}
        onClick={onToggle}
        className={`w-16 h-9 rounded-full p-1 transition-colors ${enabled ? 'bg-green-400' : 'bg-gray-300'}`}
      >
        <motion.div className="w-7 h-7 bg-white rounded-full shadow" animate={{ x: enabled ? 28 : 0 }} transition={{ type: 'spring', stiffness: 500 }} />
      </motion.button>
    </div>
  );
}

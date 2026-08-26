import { useState } from 'react';
import { motion } from 'motion/react';
import { Explorer } from '@/components/Explorer';
import { DiamondBadge } from '@/components/ui';
import { audio } from '@/lib/audio';
import { COSMETICS } from '@/lib/cosmetics';
import type { SaveState, CosmeticItem } from '@/types';

interface ShopScreenProps {
  save: SaveState;
  onBuy: (item: CosmeticItem) => void;
  onEquip: (item: CosmeticItem) => void;
  onUnequip: (item: CosmeticItem) => void;
  onBack: () => void;
}

export function ShopScreen({ save, onBuy, onEquip, onUnequip, onBack }: ShopScreenProps) {
  const [tab, setTab] = useState<'avatar' | 'hat' | 'pet' | 'color' | 'theme'>('avatar');
  const items = COSMETICS.filter((c) => c.type === tab);

  return (
    <div className="relative min-h-screen flex flex-col safe-top safe-bottom px-3 py-3 max-w-lg mx-auto w-full select-none">
      {/* Header */}
      <div className="flex items-center justify-between mb-2">
        <button
          id="shop-back-btn"
          onClick={() => { audio.click(); onBack(); }}
          className="bg-white/90 shadow-sm rounded-2xl w-10 h-10 flex items-center justify-center text-xl btn-3d"
        >
          ⬅️
        </button>
        <h2 className="text-2xl font-black text-white drop-shadow-[0_2px_3px_rgba(0,0,0,0.3)]">
          Mağaza
        </h2>
        <DiamondBadge count={save.diamonds} />
      </div>

      {/* Live Character Preview */}
      <div className="flex flex-col items-center justify-center bg-white/40 backdrop-blur-xs rounded-3xl p-3 mb-2 shadow-inner border border-white/50">
        <Explorer
          avatar={save.equipped.avatar}
          color={save.equipped.color}
          hat={save.equipped.hat}
          pet={save.equipped.pet}
          size={110}
        />
        <div className="flex items-center gap-1.5 mt-1 flex-wrap justify-center">
          <span className="text-xs font-black text-slate-800 bg-white/85 px-3 py-0.5 rounded-full shadow-xs">
            {COSMETICS.find((c) => c.id === save.equipped.avatar)?.name ?? 'Klasik Kaşif'}
          </span>
          {save.equipped.theme && (
            <span className="text-[11px] font-black text-sky-800 bg-sky-100/90 px-2.5 py-0.5 rounded-full shadow-xs border border-sky-300 flex items-center gap-1">
              <span>{COSMETICS.find((c) => c.id === save.equipped.theme)?.emoji}</span>
              <span>{COSMETICS.find((c) => c.id === save.equipped.theme)?.name.replace(' Teması', '')}</span>
            </span>
          )}
        </div>
      </div>

      {/* Category Tabs */}
      <div className="flex gap-1.5 mb-2 overflow-x-auto pb-1 no-scrollbar">
        {(
          [
            { id: 'avatar', label: '🧑‍🌾 Karakter' },
            { id: 'hat', label: '🎩 Şapka' },
            { id: 'pet', label: '🐾 Hayvan' },
            { id: 'color', label: '🎨 Renk' },
            { id: 'theme', label: '🌍 Tema' },
          ] as const
        ).map((t) => (
          <button
            key={t.id}
            id={`shop-tab-${t.id}`}
            onClick={() => { audio.click(); setTab(t.id); }}
            className={`px-3 py-1.5 rounded-xl font-black text-xs whitespace-nowrap btn-3d transition-all ${
              tab === t.id
                ? 'bg-amber-400 text-slate-900 shadow-md scale-105 border-2 border-amber-300'
                : 'bg-white/85 text-slate-700 shadow-xs'
            }`}
          >
            {t.label}
          </button>
        ))}
      </div>

      {/* Items Grid */}
      <div className="grid grid-cols-2 gap-2.5 pb-4 overflow-y-auto flex-1">
        {items.map((item) => {
          const owned = save.ownedCosmetics.includes(item.id);
          const equipped =
            (item.type === 'avatar' && save.equipped.avatar === item.id) ||
            (item.type === 'color' && save.equipped.color === item.id) ||
            (item.type === 'theme' && save.equipped.theme === item.id) ||
            (item.type === 'hat' && save.equipped.hat === item.id) ||
            (item.type === 'pet' && save.equipped.pet === item.id);
          const canAfford = save.diamonds >= item.price;

          return (
            <motion.div
              key={item.id}
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              className={`rounded-2xl p-2.5 flex flex-col items-center justify-between border-2 shadow-sm transition-all ${
                equipped
                  ? 'bg-emerald-50 border-emerald-400 ring-2 ring-emerald-300'
                  : owned
                  ? 'bg-white border-sky-200'
                  : 'bg-white/90 border-white'
              }`}
            >
              {/* Item Emoji */}
              <span className="text-3xl sm:text-4xl my-1 select-none">{item.emoji}</span>

              {/* Item Name */}
              <span className="text-xs font-black text-slate-800 text-center leading-tight">
                {item.name}
              </span>

              {/* Action Button / Badge */}
              <div className="mt-2 w-full flex flex-col gap-1 items-center">
                {equipped ? (
                  <div className="flex flex-col gap-1 w-full">
                    <span className="text-[11px] font-black text-emerald-700 bg-emerald-100 px-2 py-0.5 rounded-full border border-emerald-300 flex items-center justify-center gap-1">
                      ✓ Giydirildi
                    </span>
                    {/* "ÇIKAR" Unequip Button */}
                    <button
                      id={`unequip-btn-${item.id}`}
                      onClick={() => {
                        audio.click();
                        onUnequip(item);
                      }}
                      className="w-full bg-rose-100 hover:bg-rose-200 text-rose-700 border border-rose-300 text-xs font-black py-1 rounded-xl shadow-2xs btn-3d active:scale-95 transition-colors"
                    >
                      ✕ Çıkar
                    </button>
                  </div>
                ) : owned ? (
                  <button
                    id={`equip-btn-${item.id}`}
                    onClick={() => {
                      audio.click();
                      onEquip(item);
                    }}
                    className="w-full bg-sky-500 hover:bg-sky-600 text-white text-xs font-black py-1.5 rounded-xl shadow-xs btn-3d active:scale-95"
                  >
                    Kullan
                  </button>
                ) : (
                  <button
                    id={`buy-btn-${item.id}`}
                    onClick={() => {
                      if (canAfford) {
                        audio.diamond();
                        onBuy(item);
                      } else {
                        audio.wrong();
                      }
                    }}
                    className={`w-full text-xs font-black py-1.5 rounded-xl btn-3d flex items-center justify-center gap-1 shadow-xs ${
                      canAfford
                        ? 'bg-gradient-to-r from-amber-400 to-amber-500 text-slate-900 active:scale-95'
                        : 'bg-slate-200 text-slate-400 cursor-not-allowed'
                    }`}
                  >
                    <span>💎</span>
                    <span>{item.price}</span>
                  </button>
                )}
              </div>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}

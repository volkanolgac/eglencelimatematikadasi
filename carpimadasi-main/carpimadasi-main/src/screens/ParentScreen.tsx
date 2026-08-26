import { useEffect, useMemo, useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { audio } from '@/lib/audio';
import { getTodaySession, syncIslandUnlocks } from '@/lib/storage';
import type { SaveState, ParentStats } from '@/types';

interface ParentScreenProps {
  save: SaveState;
  update: (fn: (s: SaveState) => void) => void;
  onReset: () => void;
  onBack: () => void;
}

const ISLAND_NAMES: Record<number, { name: string; emoji: string }> = {
  1: { name: "1'ler Adası", emoji: '🌴' },
  2: { name: "2'ler Adası", emoji: '🏝️' },
  3: { name: "3'ler Adası", emoji: '🌋' },
  4: { name: "4'ler Adası", emoji: '🏖️' },
  5: { name: "5'ler Adası", emoji: '🌳' },
  6: { name: "6'ler Adası", emoji: '🌈' },
  7: { name: "7'ler Adası", emoji: '⛰️' },
  8: { name: "8'ler Adası", emoji: '👑' },
  9: { name: "9'lar Adası", emoji: '🏰' },
};

export function ParentScreen({ save, update, onReset, onBack }: ParentScreenProps) {
  const [pin, setPin] = useState('');
  const [entered, setEntered] = useState(false);
  const [error, setError] = useState(false);
  const [activeTab, setActiveTab] = useState<'stats' | 'islands' | 'settings'>('stats');
  const [showResetConfirm, setShowResetConfirm] = useState(false);
  const [newPinInput, setNewPinInput] = useState('');
  const [pinChangeMessage, setPinChangeMessage] = useState<string | null>(null);

  // Keypad / PIN check logic
  const handleDigit = (k: string) => {
    if (k === '⌫') {
      setPin((prev) => prev.slice(0, -1));
      audio.click();
      return;
    }

    if (!k || pin.length >= 4) return;

    const nextPin = pin + k;
    setPin(nextPin);
    audio.click();

    if (nextPin.length === 4) {
      const validPin = (save.parentPin || '1234').trim();
      if (nextPin.trim() === validPin || nextPin === '1234') {
        setEntered(true);
        setError(false);
        setPin('');
      } else {
        setError(true);
        audio.wrong();
        setTimeout(() => {
          setError(false);
          setPin('');
        }, 500);
      }
    }
  };

  // Keyboard support for desktop
  useEffect(() => {
    if (entered) return;
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key >= '0' && e.key <= '9') {
        handleDigit(e.key);
      } else if (e.key === 'Backspace') {
        handleDigit('⌫');
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [entered, pin, save.parentPin]);

  const stats: ParentStats = useMemo(() => {
    const today = getTodaySession(save);
    const completedIslands: number[] = [];
    for (const t of [1, 2, 3, 4, 5, 6, 7, 8, 9]) {
      const island = save.islands[t];
      if (island && island.levels.every((l) => l.completed)) completedIslands.push(t);
    }
    const wrongMap = new Map<string, { a: number; b: number; count: number }>();
    for (const s of save.sessions) {
      for (const w of s.wrongRecords) {
        const ex = wrongMap.get(w.key);
        if (ex) ex.count += w.count;
        else wrongMap.set(w.key, { a: w.a, b: w.b, count: w.count });
      }
    }
    const hardest = [...wrongMap.entries()]
      .map(([key, v]) => ({ key, a: v.a, b: v.b, count: v.count }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 5);
    const totalQ = save.sessions.reduce((s, x) => s + x.questionsAnswered, 0);
    const history = save.sessions.slice(-14).map((s) => ({
      date: s.date,
      minutes: s.minutesPlayed,
      correct: s.correctCount,
      questions: s.questionsAnswered,
    }));
    return {
      todayMinutes: today.minutesPlayed,
      todayQuestions: today.questionsAnswered,
      todayCorrect: today.correctCount,
      totalQuestions: totalQ,
      hardest,
      completedIslands,
      history,
      streak: save.streak,
    };
  }, [save]);

  // Toggle single island unlock
  const handleToggleIslandUnlock = (tableNum: number) => {
    if (tableNum === 1) return; // Island 1 always unlocked
    audio.click();
    update((s) => {
      if (s.unlockedIslands.includes(tableNum)) {
        s.unlockedIslands = s.unlockedIslands.filter((x) => x !== tableNum);
      } else {
        s.unlockedIslands.push(tableNum);
      }
      syncIslandUnlocks(s);
    });
  };

  // Unlock all islands
  const handleUnlockAll = () => {
    audio.click();
    update((s) => {
      s.unlockedIslands = [1, 2, 3, 4, 5, 6, 7, 8, 9];
    });
  };

  // Lock islands based on earned progression
  const handleLockToEarned = () => {
    audio.click();
    update((s) => {
      s.unlockedIslands = [1];
      syncIslandUnlocks(s);
    });
  };

  // Change PIN
  const handleSavePin = () => {
    if (newPinInput.length === 4 && /^\d+$/.test(newPinInput)) {
      update((s) => {
        s.parentPin = newPinInput;
      });
      audio.click();
      setPinChangeMessage('PIN başarıyla güncellendi! ✅');
      setNewPinInput('');
      setTimeout(() => setPinChangeMessage(null), 3000);
    } else {
      audio.wrong();
      setPinChangeMessage('PIN 4 haneli rakam olmalıdır! ❌');
    }
  };

  if (!entered) {
    return (
      <div className="relative min-h-screen flex flex-col items-center justify-center safe-top safe-bottom px-6 select-none">
        <button
          id="parent-login-back-btn"
          onClick={() => { audio.click(); onBack(); }}
          className="absolute top-4 left-4 bg-white/80 rounded-full w-11 h-11 flex items-center justify-center text-xl btn-3d shadow-md active:scale-95"
        >
          ⬅️
        </button>
        <h2 className="text-2xl font-extrabold text-white text-stroke-white mb-2" style={{ WebkitTextStroke: '2px #0ea5e9' }}>
          Veli Paneli
        </h2>
        <p className="text-white/90 font-bold mb-4 text-sm sm:text-base">Lütfen 4 Haneli PIN Kodunu Girin</p>

        {/* PIN Circles */}
        <motion.div animate={error ? { x: [-10, 10, -6, 6, 0] } : {}} className="flex gap-2.5 mb-6">
          {[0, 1, 2, 3].map((i) => (
            <div
              key={i}
              className={`w-12 h-14 rounded-2xl flex items-center justify-center text-2xl font-extrabold border-3 shadow-md transition-colors ${
                error ? 'bg-red-100 border-red-400 text-red-500' : 'bg-white/95 border-sky-300 text-slate-800'
              }`}
            >
              {pin[i] ? '●' : ''}
            </div>
          ))}
        </motion.div>

        {/* Keypad */}
        <div className="grid grid-cols-3 gap-2.5 max-w-xs w-full">
          {['1', '2', '3', '4', '5', '6', '7', '8', '9', '', '0', '⌫'].map((k, idx) => (
            <button
              key={k || `empty-${idx}`}
              id={k ? `pin-btn-${k}` : undefined}
              onClick={() => handleDigit(k)}
              className={`h-14 rounded-2xl font-black text-xl btn-3d border-2 transition-transform active:scale-90 select-none ${
                k === '⌫'
                  ? 'bg-red-400 hover:bg-red-500 border-red-200 text-white shadow-md'
                  : k
                  ? 'bg-white/95 hover:bg-white text-slate-800 border-white/80 shadow-md'
                  : 'opacity-0 pointer-events-none'
              }`}
              disabled={!k}
            >
              {k}
            </button>
          ))}
        </div>

        <div className="mt-6 bg-white/20 backdrop-blur-xs px-4 py-1.5 rounded-full border border-white/40">
          <p className="text-white text-xs font-bold">Varsayılan Şifre: <span className="font-extrabold text-amber-200">1234</span></p>
        </div>
      </div>
    );
  }

  return (
    <div className="relative min-h-screen flex flex-col safe-top safe-bottom px-3 py-3 select-none max-w-md mx-auto w-full">
      {/* Header */}
      <div className="flex items-center justify-between mb-3 shrink-0">
        <button
          id="parent-panel-back-btn"
          onClick={() => { audio.click(); onBack(); }}
          className="bg-white/90 shadow-md rounded-2xl w-10 h-10 flex items-center justify-center text-xl btn-3d active:scale-95"
        >
          ⬅️
        </button>
        <h2 className="text-xl sm:text-2xl font-black text-white drop-shadow-[0_2px_4px_rgba(0,0,0,0.3)]">
          Veli Portalı
        </h2>
        <div className="w-10" />
      </div>

      {/* Tabs */}
      <div className="flex bg-white/30 backdrop-blur-xs p-1 rounded-2xl mb-3 shrink-0 border border-white/40 gap-1">
        <button
          onClick={() => { audio.click(); setActiveTab('stats'); }}
          className={`flex-1 py-1.5 text-xs sm:text-sm font-black rounded-xl transition-all ${
            activeTab === 'stats'
              ? 'bg-white text-sky-700 shadow-md scale-100'
              : 'text-white/90 hover:text-white'
          }`}
        >
          📊 Raporlar
        </button>
        <button
          onClick={() => { audio.click(); setActiveTab('islands'); }}
          className={`flex-1 py-1.5 text-xs sm:text-sm font-black rounded-xl transition-all ${
            activeTab === 'islands'
              ? 'bg-white text-sky-700 shadow-md scale-100'
              : 'text-white/90 hover:text-white'
          }`}
        >
          🏝️ Adalar & Kilit
        </button>
        <button
          onClick={() => { audio.click(); setActiveTab('settings'); }}
          className={`flex-1 py-1.5 text-xs sm:text-sm font-black rounded-xl transition-all ${
            activeTab === 'settings'
              ? 'bg-white text-sky-700 shadow-md scale-100'
              : 'text-white/90 hover:text-white'
          }`}
        >
          ⚙️ Ayarlar & PIN
        </button>
      </div>

      {/* Tab 1: Stats & Reports */}
      {activeTab === 'stats' && (
        <div className="flex-1 flex flex-col gap-2.5 overflow-y-auto pb-4">
          <div className="grid grid-cols-2 gap-2">
            <StatCard icon="⏱️" label="Bugün Oynama" value={`${stats.todayMinutes} dk`} color="bg-sky-50 border-sky-200 text-sky-900" />
            <StatCard icon="📝" label="Bugün Soru" value={`${stats.todayQuestions}`} color="bg-amber-50 border-amber-200 text-amber-900" />
            <StatCard icon="✅" label="Bugün Doğru" value={`${stats.todayCorrect}`} color="bg-emerald-50 border-emerald-200 text-emerald-900" />
            <StatCard icon="🔥" label="Gün Serisi" value={`${stats.streak} Gün`} color="bg-orange-50 border-orange-200 text-orange-900" />
          </div>

          <StatCard icon="📊" label="Toplam Çözülen Soru" value={`${stats.totalQuestions}`} color="bg-violet-50 border-violet-200 text-violet-900" />

          {/* Hardest */}
          <div className="bg-white/95 rounded-2xl p-3 border-2 border-white shadow-sm">
            <h3 className="font-extrabold text-slate-800 text-xs sm:text-sm mb-2 flex items-center gap-1.5">
              <span>🧩</span> En Çok Zorlanılan Çarpımlar
            </h3>
            {stats.hardest.length === 0 ? (
              <p className="text-xs text-slate-500 italic py-1">Henüz yanlış yapılan soru kaydı yok.</p>
            ) : (
              <div className="flex flex-col gap-1.5">
                {stats.hardest.map((w, i) => (
                  <div key={i} className="flex items-center justify-between bg-red-50 border border-red-100 rounded-xl px-3 py-1.5">
                    <span className="font-black text-slate-800 text-sm">{w.a} × {w.b} = {w.a * w.b}</span>
                    <span className="text-xs font-extrabold text-red-500">{w.count} kez yanlış</span>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Completed Islands */}
          <div className="bg-white/95 rounded-2xl p-3 border-2 border-white shadow-sm">
            <h3 className="font-extrabold text-slate-800 text-xs sm:text-sm mb-2 flex items-center gap-1.5">
              <span>🏆</span> Tamamen Bitirilen Adalar (10/10)
            </h3>
            {stats.completedIslands.length === 0 ? (
              <p className="text-xs text-slate-500 italic py-1">Henüz 10 bölümü tam biten ada yok.</p>
            ) : (
              <div className="flex flex-wrap gap-1.5">
                {stats.completedIslands.map((t) => (
                  <span key={t} className="bg-emerald-100 text-emerald-800 border border-emerald-200 font-black px-2.5 py-1 rounded-xl text-xs flex items-center gap-1">
                    <span>{ISLAND_NAMES[t]?.emoji}</span> {ISLAND_NAMES[t]?.name}
                  </span>
                ))}
              </div>
            )}
          </div>

          {/* History Chart */}
          <div className="bg-white/95 rounded-2xl p-3 border-2 border-white shadow-sm">
            <h3 className="font-extrabold text-slate-800 text-xs sm:text-sm mb-2 flex items-center gap-1.5">
              <span>📈</span> Son 14 Gün Başarı Grafiği
            </h3>
            {stats.history.length === 0 ? (
              <p className="text-xs text-slate-500 italic py-1">Oturum verisi kaydedildiğinde burada görünecek.</p>
            ) : (
              <div className="flex items-end gap-1 h-28 pt-2">
                {stats.history.map((h, i) => {
                  const max = Math.max(...stats.history.map((x) => x.questions), 1);
                  const heightPct = (h.questions / max) * 100;
                  const accPct = h.questions > 0 ? Math.round((h.correct / h.questions) * 100) : 0;
                  return (
                    <div key={i} className="flex-1 flex flex-col items-center justify-end h-full">
                      <div className="text-[7px] font-bold text-slate-600 mb-0.5">{accPct}%</div>
                      <motion.div
                        initial={{ height: 0 }}
                        animate={{ height: `${Math.max(heightPct, 8)}%` }}
                        className="w-full rounded-t-md bg-gradient-to-t from-sky-500 to-sky-400 min-h-[4px]"
                      />
                      <div className="text-[7px] text-slate-500 mt-0.5 whitespace-nowrap">{h.date.slice(5)}</div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Tab 2: Islands & Level Access Management */}
      {activeTab === 'islands' && (
        <div className="flex-1 flex flex-col gap-2.5 overflow-y-auto pb-4">
          {/* Quick Island Action Buttons */}
          <div className="flex gap-2">
            <button
              onClick={handleUnlockAll}
              className="flex-1 bg-emerald-500 hover:bg-emerald-600 text-white font-black py-2 rounded-xl text-xs btn-3d shadow-sm"
            >
              🔓 Tüm Adaları Aç
            </button>
            <button
              onClick={handleLockToEarned}
              className="flex-1 bg-sky-500 hover:bg-sky-600 text-white font-black py-2 rounded-xl text-xs btn-3d shadow-sm"
            >
              🔒 İlerlemeye Göre Kitle
            </button>
          </div>

          <div className="bg-white/80 rounded-xl p-2 text-[11px] text-slate-700 border border-white">
            ℹ️ <span className="font-bold">Kural:</span> Oyuncu bir adada en az 1 bölüm tamamladığında bir sonraki ada otomatik açılır. Buradan istediğiniz adayı anında açabilir veya kilitleyebilirsiniz.
          </div>

          {/* 9 Island Rows */}
          <div className="flex flex-col gap-2">
            {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((t) => {
              const isUnlocked = save.unlockedIslands.includes(t);
              const island = save.islands[t];
              const completedCount = island?.levels.filter((l) => l.completed).length ?? 0;
              const info = ISLAND_NAMES[t];

              return (
                <div
                  key={t}
                  className={`rounded-2xl p-2.5 flex items-center justify-between border-2 transition-all shadow-xs ${
                    isUnlocked
                      ? 'bg-white/95 border-emerald-300'
                      : 'bg-slate-100/90 border-slate-300 opacity-75'
                  }`}
                >
                  <div className="flex items-center gap-2.5">
                    <span className="text-2xl">{info.emoji}</span>
                    <div>
                      <div className="font-black text-slate-800 text-xs sm:text-sm">{info.name}</div>
                      <div className="text-[10px] font-bold text-slate-500">
                        {completedCount}/10 Bölüm Oynandı ({completedCount > 0 ? `${completedCount * 10}%` : 'Başlanmadı'})
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center gap-2">
                    <span
                      className={`text-[10px] font-black px-2 py-0.5 rounded-full ${
                        isUnlocked
                          ? 'bg-emerald-100 text-emerald-800 border border-emerald-300'
                          : 'bg-slate-200 text-slate-600 border border-slate-300'
                      }`}
                    >
                      {isUnlocked ? 'AÇIK 🔓' : 'KİLİTLİ 🔒'}
                    </span>

                    {t !== 1 && (
                      <button
                        onClick={() => handleToggleIslandUnlock(t)}
                        className={`text-xs font-black px-2.5 py-1 rounded-xl btn-3d shadow-xs active:scale-95 ${
                          isUnlocked
                            ? 'bg-amber-100 hover:bg-amber-200 text-amber-900 border border-amber-300'
                            : 'bg-emerald-500 hover:bg-emerald-600 text-white'
                        }`}
                      >
                        {isUnlocked ? 'Kilitle' : 'Aç'}
                      </button>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Tab 3: Settings & PIN & Reset */}
      {activeTab === 'settings' && (
        <div className="flex-1 flex flex-col gap-3 overflow-y-auto pb-4">
          {/* Change PIN Card */}
          <div className="bg-white/95 rounded-2xl p-3.5 border-2 border-white shadow-sm">
            <h3 className="font-black text-slate-800 text-sm mb-1 flex items-center gap-1.5">
              <span>🔐</span> Veli Giriş PIN Kodu
            </h3>
            <p className="text-xs text-slate-600 mb-3">
              Mevcut PIN: <span className="font-black text-sky-700 bg-sky-100 px-2 py-0.5 rounded-md">{save.parentPin || '1234'}</span>
            </p>

            <div className="flex gap-2">
              <input
                type="text"
                maxLength={4}
                placeholder="Yeni 4 Haneli PIN"
                value={newPinInput}
                onChange={(e) => setNewPinInput(e.target.value.replace(/\D/g, ''))}
                className="flex-1 bg-slate-50 border-2 border-slate-200 rounded-xl px-3 py-2 text-sm font-black text-slate-800 focus:outline-none focus:border-sky-400"
              />
              <button
                onClick={handleSavePin}
                className="bg-sky-500 hover:bg-sky-600 text-white font-black px-4 py-2 rounded-xl text-xs btn-3d shadow-xs"
              >
                Kaydet
              </button>
            </div>

            {pinChangeMessage && (
              <p className="text-xs font-bold mt-2 text-slate-700">{pinChangeMessage}</p>
            )}
          </div>

          {/* Reset Progress Card */}
          <div className="bg-white/95 rounded-2xl p-3.5 border-2 border-red-200 shadow-sm">
            <h3 className="font-black text-red-700 text-sm mb-1 flex items-center gap-1.5">
              <span>⚠️</span> Öğrenci İlerlemesini Sıfırla
            </h3>
            <p className="text-xs text-slate-600 mb-3">
              Tüm çözülen bölümleri, elmasları ve ada kilitlerini sıfırlayarak baştan başlatır. (Veli PIN kodu ve ayarlar korunur).
            </p>

            <button
              onClick={() => setShowResetConfirm(true)}
              className="w-full bg-red-500 hover:bg-red-600 text-white font-black py-2.5 rounded-xl text-xs sm:text-sm btn-3d shadow-md"
            >
              🗑️ Tüm İlerlemeyi Sıfırla
            </button>
          </div>
        </div>
      )}

      {/* Reset Confirmation Modal */}
      <AnimatePresence>
        {showResetConfirm && (
          <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
              className="bg-white rounded-3xl p-5 max-w-xs w-full shadow-2xl border-4 border-red-300 text-center"
            >
              <span className="text-4xl mb-2 block">⚠️</span>
              <h3 className="text-lg font-black text-slate-900 mb-2">Emin misiniz?</h3>
              <p className="text-xs text-slate-600 mb-4">
                Tüm ada ilerlemeleri, yıldızlar ve elmaslar silinecek. Bu işlem geri alınamaz!
              </p>
              <div className="flex gap-2">
                <button
                  onClick={() => setShowResetConfirm(false)}
                  className="flex-1 bg-slate-200 hover:bg-slate-300 text-slate-800 font-black py-2 rounded-xl text-xs btn-3d"
                >
                  İptal
                </button>
                <button
                  onClick={() => {
                    setShowResetConfirm(false);
                    audio.click();
                    onReset();
                  }}
                  className="flex-1 bg-red-500 hover:bg-red-600 text-white font-black py-2 rounded-xl text-xs btn-3d shadow-md"
                >
                  Evet, Sıfırla
                </button>
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
}

function StatCard({
  icon,
  label,
  value,
  color,
}: {
  icon: string;
  label: string;
  value: string;
  color: string;
}) {
  return (
    <div className={`rounded-2xl p-2.5 border-2 shadow-xs ${color}`}>
      <div className="text-xl mb-0.5">{icon}</div>
      <div className="text-[10px] font-bold text-slate-600">{label}</div>
      <div className="text-base sm:text-lg font-black leading-tight">{value}</div>
    </div>
  );
}

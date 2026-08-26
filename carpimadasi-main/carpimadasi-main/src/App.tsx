import { useCallback, useEffect, useState } from 'react';
import { AnimatePresence, motion } from 'motion/react';
import { useSaveState } from '@/hooks/useSaveState';
import { setSoundEnabled, setMusicEnabled } from '@/lib/audio';
import { getTodaySession, syncIslandUnlocks } from '@/lib/storage';
import { Clouds } from '@/components/ui';
import { THEME_MAP } from '@/lib/cosmetics';
import type { Screen, CosmeticItem } from '@/types';
import { HomeScreen } from '@/screens/HomeScreen';
import { MapScreen } from '@/screens/MapScreen';
import { GameScreen } from '@/screens/GameScreen';
import { ChestScreen } from '@/screens/ChestScreen';
import { ShopScreen } from '@/screens/ShopScreen';
import { SettingsScreen } from '@/screens/SettingsScreen';
import { ParentScreen } from '@/screens/ParentScreen';

interface GameSession {
  table: number;
  levelIndex: number;
}

export default function App() {
  const { state: save, update } = useSaveState();
  const [screen, setScreen] = useState<Screen>('home');
  const [session, setSession] = useState<GameSession | null>(null);
  const [lastResult, setLastResult] = useState<{ stars: number; diamonds: number; bestStreak: number } | null>(null);
  const [newBadge, setNewBadge] = useState<string | null>(null);

  // sync audio settings
  useEffect(() => {
    setSoundEnabled(save.soundEnabled);
    setMusicEnabled(save.musicEnabled);
  }, [save.soundEnabled, save.musicEnabled]);

  const startLevel = useCallback((table: number, levelIndex: number) => {
    setSession({ table, levelIndex });
    setScreen('game');
  }, []);

  const handleGameComplete = useCallback((result: { stars: number; diamonds: number; bestStreak: number }) => {
    if (!session) return;
    update((s) => {
      const island = s.islands[session.table];
      if (island && island.levels[session.levelIndex]) {
        const lvl = island.levels[session.levelIndex];
        lvl.completed = true;
        lvl.stars = Math.max(lvl.stars, result.stars);
        lvl.bestStreak = Math.max(lvl.bestStreak, result.bestStreak);
      }
      s.diamonds += result.diamonds;

      // When ANY level is played and completed on this island, unlock the next island!
      const nextTable = session.table + 1;
      if (nextTable <= 9 && !s.unlockedIslands.includes(nextTable)) {
        s.unlockedIslands.push(nextTable);
      }
      syncIslandUnlocks(s);

      // Whole island completed badge check
      const wholeIslandFinished = island && island.levels.every((l) => l.completed);
      if (wholeIslandFinished) {
        const badgeKey = `island${session.table}`;
        if (!s.badges.includes(badgeKey)) {
          s.badges.push(badgeKey);
          setNewBadge(badgeKey);
        }
      }

      // combo badge
      if (result.bestStreak >= 10 && !s.badges.includes('combo10')) {
        s.badges.push('combo10');
        setNewBadge('combo10');
      }

      // first chest badge
      if (!s.badges.includes('first_chest')) {
        s.badges.push('first_chest');
        setNewBadge('first_chest');
      }

      // record session time
      const sess = getTodaySession(s);
      sess.minutesPlayed += 2;
    });
    setLastResult(result);
    setScreen('chest');
  }, [session, update]);

  const handleBuy = useCallback((item: CosmeticItem) => {
    update((s) => {
      if (s.diamonds >= item.price && !s.ownedCosmetics.includes(item.id)) {
        s.diamonds -= item.price;
        s.ownedCosmetics.push(item.id);
      }
    });
  }, [update]);

  const handleEquip = useCallback((item: CosmeticItem) => {
    update((s) => {
      if (item.type === 'avatar') s.equipped.avatar = item.id;
      else if (item.type === 'hat') s.equipped.hat = item.id;
      else if (item.type === 'pet') s.equipped.pet = item.id;
      else if (item.type === 'color') s.equipped.color = item.id;
      else if (item.type === 'theme') s.equipped.theme = item.id;
    });
  }, [update]);

  const handleUnequip = useCallback((item: CosmeticItem) => {
    update((s) => {
      if (item.type === 'hat') s.equipped.hat = null;
      else if (item.type === 'pet') s.equipped.pet = null;
      else if (item.type === 'avatar') s.equipped.avatar = 'avatar_classic';
      else if (item.type === 'color') s.equipped.color = 'color_amber';
      else if (item.type === 'theme') s.equipped.theme = null;
    });
  }, [update]);

  const handleReset = useCallback(() => {
    update((s) => {
      const defaults = {
        diamonds: 0,
        unlockedIslands: [1],
        ownedCosmetics: ['avatar_classic', 'color_amber', 'theme_sky'],
        equipped: { avatar: 'avatar_classic', hat: null, pet: null, color: 'color_amber', theme: 'theme_sky' },
        badges: [],
        streak: 0,
        lastPlayDate: null,
        sessions: [],
      };
      Object.assign(s, defaults);
      // re-init islands
      for (const t of [1,2,3,4,5,6,7,8,9]) {
        s.islands[t] = { levels: Array.from({ length: 10 }, () => ({ stars: 0, bestStreak: 0, completed: false })) };
      }
      syncIslandUnlocks(s);
    });
    setScreen('home');
  }, [update]);

  const toggleSound = useCallback(() => update((s) => { s.soundEnabled = !s.soundEnabled; }), [update]);
  const toggleMusic = useCallback(() => update((s) => { s.musicEnabled = !s.musicEnabled; }), [update]);

  const activeTheme = save.equipped.theme ? (THEME_MAP[save.equipped.theme] ?? THEME_MAP.theme_sky) : THEME_MAP.theme_sky;
  const currentBackground = activeTheme.background;

  return (
    <div
      className="relative min-h-screen overflow-hidden transition-all duration-500"
      style={{
        background: currentBackground,
      }}
    >
      <Clouds theme={save.equipped.theme} />

      <div className="relative z-10">
        <AnimatePresence mode="wait">
          <motion.div
            key={screen}
            initial={{ opacity: 0, scale: 0.98 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.98 }}
            transition={{ duration: 0.2 }}
          >
            {screen === 'home' && (
              <HomeScreen
                save={save}
                onPlay={() => setScreen('map')}
                onShop={() => setScreen('shop')}
                onSettings={() => setScreen('settings')}
                onParent={() => setScreen('parent')}
              />
            )}

            {screen === 'map' && (
              <MapScreen
                save={save}
                onSelect={(table, level) => startLevel(table, level)}
                onBack={() => setScreen('home')}
              />
            )}

            {screen === 'game' && session && (
              <GameScreen
                save={save}
                table={session.table}
                levelIndex={session.levelIndex}
                onComplete={handleGameComplete}
                onExit={() => setScreen('map')}
              />
            )}

            {screen === 'chest' && lastResult && (
              <ChestScreen
                stars={lastResult.stars}
                diamonds={lastResult.diamonds}
                bestStreak={lastResult.bestStreak}
                newBadge={newBadge}
                onContinue={() => {
                  setNewBadge(null);
                  setScreen('map');
                }}
              />
            )}

            {screen === 'shop' && (
              <ShopScreen
                save={save}
                onBuy={handleBuy}
                onEquip={handleEquip}
                onUnequip={handleUnequip}
                onBack={() => setScreen('home')}
              />
            )}

            {screen === 'settings' && (
              <SettingsScreen
                save={save}
                onToggleSound={toggleSound}
                onToggleMusic={toggleMusic}
                onReset={handleReset}
                onBack={() => setScreen('home')}
              />
            )}

            {screen === 'parent' && (
              <ParentScreen
                save={save}
                update={update}
                onReset={handleReset}
                onBack={() => setScreen('home')}
              />
            )}
          </motion.div>
        </AnimatePresence>
      </div>
    </div>
  );
}

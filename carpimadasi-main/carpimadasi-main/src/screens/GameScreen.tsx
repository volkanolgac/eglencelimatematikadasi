import { useEffect, useMemo, useRef, useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Explorer } from '@/components/Explorer';
import { Monster } from '@/components/Monster';
import { DiamondBadge } from '@/components/ui';
import { audio } from '@/lib/audio';
import { makeQuestion, wrongKey } from '@/lib/questions';
import { getTodaySession, updateStreak } from '@/lib/storage';
import type { Question, SaveState } from '@/types';

interface GameScreenProps {
  save: SaveState;
  table: number;
  levelIndex: number;
  onComplete: (result: { stars: number; diamonds: number; bestStreak: number }) => void;
  onExit: () => void;
}

const TOTAL_QUESTIONS = 10;
const MAX_LIVES = 3;
const QUESTION_SECONDS = 12; // Generous child-friendly timer

export function GameScreen({ save, table, levelIndex, onComplete, onExit }: GameScreenProps) {
  const session = useMemo(() => getTodaySession(save), [save]);
  const wrongs = session.wrongRecords;

  const [qIndex, setQIndex] = useState(0);
  const [question, setQuestion] = useState<Question>(() => makeQuestion(table, levelIndex + 1, wrongs, 0));
  const [lives, setLives] = useState(MAX_LIVES);
  const [streak, setStreak] = useState(0);
  const [bestStreak, setBestStreak] = useState(0);
  const [diamonds, setDiamonds] = useState(0);

  // Question timer
  const [timeLeft, setTimeLeft] = useState(QUESTION_SECONDS);
  const [isTimerActive, setIsTimerActive] = useState(true);

  // Visual feedback states
  const [feedback, setFeedback] = useState<'correct' | 'wrong' | null>(null);
  const [praise, setPraise] = useState('');
  const [comboWord, setComboWord] = useState('');
  const [monsterState, setMonsterState] = useState<'idle' | 'approach' | 'attack' | 'flee' | 'eat'>('idle');
  const [explorerEyes, setExplorerEyes] = useState<'normal' | 'x' | 'happy'>('normal');
  const [explorerKick, setExplorerKick] = useState(false);
  const [selected, setSelected] = useState<number | null>(null);
  const [showCombo, setShowCombo] = useState(0);
  const [progress, setProgress] = useState(0);

  // Game over state
  const [gameOverPhase, setGameOverPhase] = useState<'none' | 'defeated_pause' | 'modal'>('none');

  const lockedRef = useRef(false);
  const isGameOverRef = useRef(false);

  // Update streak on mount
  useEffect(() => {
    updateStreak(save);
    audio.unlock();
  }, [save]);

  // Restart level helper
  const restartLevel = () => {
    lockedRef.current = false;
    isGameOverRef.current = false;
    setQIndex(0);
    setLives(MAX_LIVES);
    setStreak(0);
    setBestStreak(0);
    setDiamonds(0);
    setProgress(0);
    setFeedback(null);
    setMonsterState('idle');
    setExplorerEyes('normal');
    setExplorerKick(false);
    setSelected(null);
    setGameOverPhase('none');
    setQuestion(makeQuestion(table, levelIndex + 1, wrongs, 0));
    setTimeLeft(QUESTION_SECONDS);
    setIsTimerActive(true);
  };

  // Per-Question Countdown Timer
  useEffect(() => {
    if (!isTimerActive || gameOverPhase !== 'none') return;

    const interval = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 0.1) {
          clearInterval(interval);
          handleTimeUp();
          return 0;
        }
        return Math.max(0, prev - 0.1);
      });
    }, 100);

    return () => clearInterval(interval);
  }, [isTimerActive, gameOverPhase, qIndex]);

  // Trigger game over flow (3 seconds defeat scene, then OYUN BİTTİ modal + sound)
  const triggerGameOver = () => {
    if (isGameOverRef.current) return;
    isGameOverRef.current = true;
    lockedRef.current = true;
    setIsTimerActive(false);

    // Monster attacks avatar, avatar eyes turn X
    setMonsterState('eat');
    setExplorerEyes('x');
    setGameOverPhase('defeated_pause');

    // 3 Seconds defeat display before showing OYUN BİTTİ modal
    setTimeout(() => {
      audio.gameOver();
      setGameOverPhase('modal');
    }, 3000);
  };

  // When time runs out on a question
  const handleTimeUp = () => {
    if (lockedRef.current || isGameOverRef.current) return;
    setFeedback('wrong');
    audio.wrong();
    setStreak(0);

    session.questionsAnswered += 1;
    session.wrongCount += 1;
    const key = wrongKey(question.a || table, question.b);
    const existing = session.wrongRecords.find((w) => w.key === key);
    if (existing) existing.count += 1;
    else session.wrongRecords.push({ key, a: question.a || table, b: question.b, count: 1 });

    const newLives = lives - 1;
    setLives(newLives);

    if (newLives <= 0) {
      triggerGameOver();
    } else {
      lockedRef.current = true;
      setMonsterState('attack');
      setExplorerEyes('x');

      setTimeout(() => {
        setExplorerEyes('normal');
        setMonsterState('idle');
        setFeedback(null);
        setSelected(null);
        lockedRef.current = false;
        advance();
      }, 1300);
    }
  };

  function comboMultiplier(s: number): number {
    if (s >= 9) return 5;
    if (s >= 6) return 3;
    if (s >= 3) return 2;
    return 1;
  }

  function handleAnswer(value: number) {
    if (lockedRef.current || isGameOverRef.current) return;
    lockedRef.current = true;
    setIsTimerActive(false);
    setSelected(value);
    const correct = value === question.answer;

    session.questionsAnswered += 1;

    if (correct) {
      const newStreak = streak + 1;
      setStreak(newStreak);
      const newBest = Math.max(bestStreak, newStreak);
      setBestStreak(newBest);
      const mult = comboMultiplier(newStreak);
      const gained = 5 * mult;
      setDiamonds((d) => d + gained);
      session.correctCount += 1;

      setFeedback('correct');
      setMonsterState('flee');
      setExplorerEyes('happy');
      setExplorerKick(true);
      const p = audio.praise();
      setPraise(p);
      audio.correct();
      audio.diamond();

      if (newStreak >= 3) {
        setShowCombo(mult);
        // English TTS combo voice praise
        const spoken = audio.speakCombo(mult);
        setComboWord(spoken);
        setTimeout(() => setShowCombo(0), 1400);
      }

      setTimeout(() => {
        setProgress((p) => Math.min(100, p + 100 / TOTAL_QUESTIONS));
        advance();
      }, 1100);
    } else {
      setStreak(0);
      session.wrongCount += 1;
      const key = wrongKey(question.a || table, question.b);
      const existing = session.wrongRecords.find((w) => w.key === key);
      if (existing) existing.count += 1;
      else session.wrongRecords.push({ key, a: question.a || table, b: question.b, count: 1 });

      setFeedback('wrong');
      audio.wrong();
      const newLives = lives - 1;
      setLives(newLives);

      if (newLives <= 0) {
        triggerGameOver();
      } else {
        setMonsterState('attack');
        setExplorerEyes('x');

        setTimeout(() => {
          setMonsterState('idle');
          setExplorerEyes('normal');
          setFeedback(null);
          setSelected(null);
          lockedRef.current = false;
          setIsTimerActive(true);
        }, 1200);
      }
    }
  }

  function advance() {
    if (isGameOverRef.current) return;
    const next = qIndex + 1;
    if (next >= TOTAL_QUESTIONS) {
      // Level completed successfully!
      const stars = lives >= 3 ? 3 : lives >= 2 ? 2 : 1;
      onComplete({ stars, diamonds, bestStreak });
      return;
    }
    setQIndex(next);
    setQuestion(makeQuestion(table, levelIndex + 1, wrongs, next));
    setFeedback(null);
    setMonsterState('idle');
    setExplorerEyes('normal');
    setExplorerKick(false);
    setSelected(null);
    setTimeLeft(QUESTION_SECONDS);
    setIsTimerActive(true);
    lockedRef.current = false;
  }

  const monsterVariant = (qIndex + table) % 6;

  // Continuous Proximity Calculation:
  // timeFraction is 1 when full time, 0 when time expires.
  // When timeFraction = 1 -> monster is at rightmost position (offset = 0)
  // When timeFraction = 0 -> monster has moved all the way to the avatar (offset = -140px)
  const timeFraction = Math.max(0, Math.min(1, timeLeft / QUESTION_SECONDS));
  const maxMonsterTravel = 135;
  const currentMonsterX = - (maxMonsterTravel * (1 - timeFraction));

  return (
    <div className="relative h-[100dvh] max-h-[100dvh] overflow-hidden flex flex-col justify-between safe-top safe-bottom px-3 py-2 max-w-lg mx-auto w-full select-none">
      {/* Top Header Bar */}
      <div className="shrink-0 flex items-center justify-between mb-1 gap-2">
        <button
          id="game-back-btn"
          onClick={() => { audio.click(); onExit(); }}
          className="bg-white/90 shadow-sm rounded-2xl w-9 h-9 sm:w-10 sm:h-10 flex items-center justify-center text-lg btn-3d"
        >
          ⬅️
        </button>

        {/* Level Progress Bar */}
        <div className="flex-1">
          <div className="h-3 bg-white/70 rounded-full overflow-hidden shadow-inner p-0.5">
            <motion.div
              className="h-full bg-amber-400 rounded-full"
              animate={{ width: `${progress}%` }}
              transition={{ duration: 0.3 }}
            />
          </div>
        </div>

        {/* Lives */}
        <div className="flex gap-0.5 bg-white/60 backdrop-blur-xs px-2 py-0.5 rounded-2xl shadow-xs">
          {Array.from({ length: MAX_LIVES }).map((_, i) => (
            <span key={i} className="text-sm sm:text-base">
              {i < lives ? '❤️' : '🤍'}
            </span>
          ))}
        </div>

        <DiamondBadge count={diamonds} />
      </div>

      {/* Question Index & Per-Question Countdown Timer Bar */}
      <div className="shrink-0 flex items-center justify-between px-1 mb-1">
        <span className="text-xs font-black text-white drop-shadow-[0_1px_2px_rgba(0,0,0,0.3)]">
          Soru {qIndex + 1} / {TOTAL_QUESTIONS}
        </span>

        {/* Time Remaining Bar */}
        <div className="flex items-center gap-1.5 bg-white/85 rounded-full px-2.5 py-0.5 shadow-xs">
          <span className="text-xs">⏱️</span>
          <div className="w-16 sm:w-20 h-1.5 sm:h-2 bg-slate-200 rounded-full overflow-hidden">
            <div
              className={`h-full transition-all duration-100 ${
                timeFraction < 0.3 ? 'bg-rose-500 animate-pulse' : timeFraction < 0.6 ? 'bg-amber-400' : 'bg-emerald-400'
              }`}
              style={{ width: `${timeFraction * 100}%` }}
            />
          </div>
          <span className={`text-xs font-black tabular-nums ${timeFraction < 0.3 ? 'text-rose-600' : 'text-slate-700'}`}>
            {Math.ceil(timeLeft)}s
          </span>
        </div>
      </div>

      {/* Battle Scene & Question Box Area (Flexible Center) */}
      <div className="flex-1 min-h-0 flex flex-col items-center justify-center relative my-auto">
        {/* Arena Stage */}
        <div className="relative w-full h-32 sm:h-40 flex items-center justify-center overflow-visible">
          {/* Ground Platform Shadow */}
          <div className="absolute bottom-1 w-4/5 h-5 bg-emerald-700/20 rounded-[100%] blur-[2px]" />

          {/* Explorer Avatar (Left / Center-Left) */}
          <div className="absolute left-[14%] sm:left-[18%] bottom-2 z-10">
            <motion.div
              animate={explorerKick ? { y: [0, -30, 0], scale: [1, 1.08, 1] } : {}}
              transition={{ duration: 0.45 }}
            >
              <Explorer
                avatar={save.equipped.avatar}
                color={save.equipped.color}
                hat={save.equipped.hat}
                pet={save.equipped.pet}
                size={100}
                animate={!explorerKick && gameOverPhase === 'none'}
                eyes={explorerEyes}
              />
            </motion.div>
          </div>

          {/* Monster (Continuously visible & smoothly approaches avatar as time decreases) */}
          <div className="absolute right-[8%] sm:right-[12%] bottom-2 z-20">
            <motion.div
              animate={{
                x: currentMonsterX,
              }}
              transition={{ duration: 0.15, ease: 'linear' }}
            >
              <Monster
                variant={monsterVariant}
                state={monsterState === 'idle' && timeFraction < 0.35 ? 'approach' : monsterState}
                size={95}
              />
            </motion.div>
          </div>
        </div>

        {/* Speech Bubble / Question Card */}
        <motion.div
          key={`q-${qIndex}`}
          initial={{ scale: 0.85, y: 10, opacity: 0 }}
          animate={{ scale: 1, y: 0, opacity: 1 }}
          transition={{ type: 'spring', stiffness: 220, damping: 20 }}
          className="bg-white rounded-2xl px-4 py-2 sm:py-2.5 shadow-md border-3 border-sky-300 relative max-w-xs sm:max-w-sm w-full text-center mt-1"
        >
          <div className="text-2xl sm:text-3xl font-black text-slate-800 tracking-wide">
            {question.prompt}
          </div>
          {question.subPrompt && (
            <div className="text-xs sm:text-sm font-bold text-sky-700 mt-0.5">
              {question.subPrompt}
            </div>
          )}
        </motion.div>

        {/* Feedback / Combo Floating Badges */}
        <AnimatePresence>
          {feedback === 'correct' && (
            <motion.div
              initial={{ scale: 0, y: 10 }}
              animate={{ scale: 1, y: 0 }}
              exit={{ scale: 0 }}
              className="absolute top-1 bg-emerald-500 text-white font-black px-3.5 py-1 rounded-full text-sm sm:text-base border-2 border-white shadow-md z-30"
            >
              {praise} ✨
            </motion.div>
          )}

          {feedback === 'wrong' && gameOverPhase === 'none' && (
            <motion.div
              initial={{ scale: 0, y: 10 }}
              animate={{ scale: 1, y: 0 }}
              exit={{ scale: 0 }}
              className="absolute top-1 bg-rose-500 text-white font-black px-3.5 py-1 rounded-full text-sm sm:text-base border-2 border-white shadow-md z-30"
            >
              Dikkat! Tekrar dene! 💪
            </motion.div>
          )}

          {showCombo > 0 && (
            <motion.div
              initial={{ scale: 0, y: 0 }}
              animate={{ scale: [1, 1.15, 1], y: -20 }}
              exit={{ opacity: 0 }}
              className="absolute top-4 bg-gradient-to-r from-amber-400 to-orange-500 text-white font-black px-3.5 py-1 rounded-full text-xs sm:text-sm border-2 border-white shadow-lg z-30"
            >
              {comboWord || 'Awesome!'} Combo x{showCombo}! 🔥
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      {/* 
        Answer Options Grid:
        Always fixed inside viewport height without needing any scrolling.
      */}
      <div className="shrink-0 grid grid-cols-2 gap-2 sm:gap-2.5 pb-2 sm:pb-3 max-w-md w-full mx-auto">
        {question.options.map((opt) => {
          const isWrong = selected === opt && feedback === 'wrong';
          const isRight = selected === opt && feedback === 'correct';

          return (
            <motion.button
              key={opt}
              id={`option-btn-${opt}`}
              whileTap={{ scale: 0.94 }}
              disabled={lockedRef.current || gameOverPhase !== 'none'}
              onClick={() => handleAnswer(opt)}
              animate={isWrong ? { x: [-6, 6, -4, 4, 0] } : isRight ? { scale: [1, 1.08, 1] } : {}}
              className={`rounded-2xl py-3 sm:py-4 text-2xl sm:text-3xl font-black border-3 shadow-md btn-3d transition-all ${
                isRight
                  ? 'bg-emerald-500 border-emerald-300 text-white'
                  : isWrong
                  ? 'bg-rose-500 border-rose-300 text-white'
                  : 'bg-white/95 border-white text-slate-800 hover:bg-sky-50'
              }`}
            >
              {opt}
            </motion.button>
          );
        })}
      </div>

      {/* GAME OVER MODAL (Appears after 3 seconds of defeat scene) */}
      <AnimatePresence>
        {gameOverPhase === 'modal' && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-slate-900/70 backdrop-blur-xs flex items-center justify-center p-4 z-50"
          >
            <motion.div
              initial={{ scale: 0.7, y: 30 }}
              animate={{ scale: 1, y: 0 }}
              exit={{ scale: 0.8, opacity: 0 }}
              transition={{ type: 'spring', stiffness: 220, damping: 20 }}
              className="bg-white rounded-3xl p-6 max-w-sm w-full text-center shadow-2xl border-4 border-rose-400 relative"
            >
              {/* Monster & Defeated Avatar preview */}
              <div className="flex items-center justify-center gap-2 mb-2">
                <Monster variant={monsterVariant} state="eat" size={65} />
                <Explorer
                  avatar={save.equipped.avatar}
                  color={save.equipped.color}
                  hat={save.equipped.hat}
                  pet={save.equipped.pet}
                  size={70}
                  animate={false}
                  eyes="x"
                />
              </div>

              {/* Game Over Title */}
              <h2 className="text-3xl font-black text-rose-500 tracking-wide mb-1 drop-shadow-xs">
                OYUN BİTTİ
              </h2>

              <p className="text-sm font-bold text-slate-600 mb-6">
                Üzülme Kaşif! Biraz daha pratik yapıp bu adayı fethedebilirsin. 💪
              </p>

              {/* Action Buttons */}
              <div className="flex flex-col gap-2.5">
                <button
                  id="game-over-retry-btn"
                  onClick={() => {
                    audio.click();
                    restartLevel();
                  }}
                  className="w-full bg-gradient-to-r from-emerald-400 to-emerald-500 hover:from-emerald-500 hover:to-emerald-600 text-white text-lg font-black py-3 rounded-2xl shadow-md btn-3d active:scale-98"
                >
                  🔄 Tekrar Dene
                </button>

                <button
                  id="game-over-exit-btn"
                  onClick={() => {
                    audio.click();
                    onExit();
                  }}
                  className="w-full bg-slate-100 hover:bg-slate-200 text-slate-700 text-base font-bold py-2.5 rounded-2xl shadow-xs active:scale-98"
                >
                  🏝️ Adalara Dön
                </button>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

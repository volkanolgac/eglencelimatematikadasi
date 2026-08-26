import { motion } from 'motion/react';
import type { ReactNode } from 'react';

interface AtmosphereProps {
  theme?: string | null;
}

export function Clouds({ theme = 'theme_sky' }: AtmosphereProps) {
  const isSpace = theme === 'theme_space';
  const isSunset = theme === 'theme_sunset';

  const clouds = [
    { top: '8%', dur: 38, delay: 0, scale: 1 },
    { top: '20%', dur: 50, delay: -15, scale: 0.7 },
    { top: '55%', dur: 45, delay: -30, scale: 0.85 },
    { top: '70%', dur: 60, delay: -8, scale: 0.6 },
  ];

  if (isSpace) {
    // Space theme: Stars, Moon/Planet, shooting star
    const stars = [
      { top: '10%', left: '15%', size: 3, dur: 2.2, delay: 0 },
      { top: '18%', left: '75%', size: 4, dur: 3.1, delay: 0.8 },
      { top: '35%', left: '30%', size: 2.5, dur: 2.5, delay: 1.2 },
      { top: '48%', left: '85%', size: 3.5, dur: 1.8, delay: 0.4 },
      { top: '65%', left: '12%', size: 4, dur: 2.8, delay: 1.6 },
      { top: '78%', left: '60%', size: 3, dur: 2.1, delay: 0.2 },
      { top: '88%', left: '80%', size: 2.5, dur: 3.4, delay: 1.0 },
    ];

    return (
      <div className="pointer-events-none fixed inset-0 overflow-hidden z-0 select-none">
        {/* Floating Planet */}
        <motion.div
          className="absolute top-12 right-8 text-4xl opacity-80"
          animate={{ y: [0, -8, 0], rotate: [0, 5, 0] }}
          transition={{ duration: 6, repeat: Infinity, ease: 'easeInOut' }}
        >
          🪐
        </motion.div>
        {/* Twinkling Stars */}
        {stars.map((s, i) => (
          <motion.div
            key={i}
            className="absolute rounded-full bg-white shadow-[0_0_8px_#ffffff]"
            style={{
              top: s.top,
              left: s.left,
              width: s.size * 2,
              height: s.size * 2,
            }}
            animate={{ opacity: [0.3, 1, 0.3], scale: [0.8, 1.2, 0.8] }}
            transition={{ duration: s.dur, repeat: Infinity, delay: s.delay, ease: 'easeInOut' }}
          />
        ))}
        {/* Floating Space Dust Nebulae */}
        {clouds.map((c, i) => (
          <motion.div
            key={i}
            className="absolute"
            style={{ top: c.top, left: '-30vw' }}
            initial={{ x: 0 }}
            animate={{ x: '160vw' }}
            transition={{ duration: c.dur * 1.3, repeat: Infinity, delay: c.delay, ease: 'linear' }}
          >
            <svg width={140 * c.scale} height={80 * c.scale} viewBox="0 0 140 80">
              <ellipse cx="40" cy="50" rx="35" ry="20" fill="#818cf8" opacity="0.25" />
              <ellipse cx="75" cy="40" rx="40" ry="25" fill="#c084fc" opacity="0.2" />
              <ellipse cx="105" cy="52" rx="30" ry="18" fill="#38bdf8" opacity="0.25" />
            </svg>
          </motion.div>
        ))}
      </div>
    );
  }

  const cloudFill = isSunset ? '#fed7aa' : '#ffffff';
  const cloudOpacity = isSunset ? 0.82 : 0.9;

  return (
    <div className="pointer-events-none fixed inset-0 overflow-hidden z-0 select-none">
      {isSunset && (
        <motion.div
          className="absolute top-10 right-10 text-4xl opacity-90"
          animate={{ scale: [1, 1.05, 1] }}
          transition={{ duration: 4, repeat: Infinity, ease: 'easeInOut' }}
        >
          🌅
        </motion.div>
      )}
      {clouds.map((c, i) => (
        <motion.div
          key={i}
          className="absolute"
          style={{ top: c.top, left: '-30vw' }}
          initial={{ x: 0 }}
          animate={{ x: '160vw' }}
          transition={{ duration: c.dur, repeat: Infinity, delay: c.delay, ease: 'linear' }}
        >
          <svg width={120 * c.scale} height={70 * c.scale} viewBox="0 0 120 70">
            <ellipse cx="35" cy="45" rx="30" ry="22" fill={cloudFill} opacity={cloudOpacity} />
            <ellipse cx="65" cy="38" rx="35" ry="26" fill={cloudFill} opacity={cloudOpacity} />
            <ellipse cx="95" cy="48" rx="25" ry="18" fill={cloudFill} opacity={cloudOpacity} />
          </svg>
        </motion.div>
      ))}
    </div>
  );
}

interface BigButtonProps {
  children: ReactNode;
  onClick?: () => void;
  className?: string;
  color?: string;
  disabled?: boolean;
}

export function BigButton({ children, onClick, className = '', color = 'bg-sky-400', disabled }: BigButtonProps) {
  return (
    <motion.button
      whileTap={{ scale: 0.95 }}
      whileHover={{ scale: 1.03 }}
      onClick={onClick}
      disabled={disabled}
      className={`btn-3d ${color} text-white font-bold rounded-3xl px-6 py-4 text-xl
        border-2 border-white/40 disabled:opacity-50 ${className}`}
    >
      {children}
    </motion.button>
  );
}

interface DiamondBadgeProps {
  count: number;
  className?: string;
}

export function DiamondBadge({ count, className = '' }: DiamondBadgeProps) {
  return (
    <div className={`flex items-center gap-1.5 bg-white/80 backdrop-blur rounded-full px-3 py-1.5 font-bold text-cyan-600 ${className}`}>
      <span className="text-xl">💎</span>
      <span className="tabular-nums">{count}</span>
    </div>
  );
}

interface StarsProps {
  stars: number;
  size?: number;
}

export function Stars({ stars, size = 24 }: StarsProps) {
  return (
    <div className="flex gap-1">
      {[0, 1, 2].map((i) => (
        <motion.span
          key={i}
          initial={{ scale: 0, rotate: -180 }}
          animate={{ scale: 1, rotate: 0 }}
          transition={{ delay: i * 0.15, type: 'spring', stiffness: 200 }}
          style={{ fontSize: size }}
        >
          {i < stars ? '⭐' : '☆'}
        </motion.span>
      ))}
    </div>
  );
}

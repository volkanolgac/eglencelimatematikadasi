import { motion } from 'motion/react';

interface MonsterProps {
  variant: number;
  size?: number;
  state?: 'idle' | 'approach' | 'attack' | 'flee' | 'eat';
}

const MONSTER_COLORS = [
  { body: '#c084fc', belly: '#f3e8ff', eye: '#1e293b' },
  { body: '#fb7185', belly: '#ffe4e6', eye: '#1e293b' },
  { body: '#34d399', belly: '#d1fae5', eye: '#1e293b' },
  { body: '#fbbf24', belly: '#fef3c7', eye: '#1e293b' },
  { body: '#60a5fa', belly: '#dbeafe', eye: '#1e293b' },
  { body: '#f472b6', belly: '#fce7f3', eye: '#1e293b' },
];

export function Monster({ variant, size = 110, state = 'idle' }: MonsterProps) {
  const c = MONSTER_COLORS[variant % MONSTER_COLORS.length];

  // Monster remains 100% VISIBLE at all times (opacity never drops to 0)
  const animate =
    state === 'eat'
      ? { scale: [1, 1.2, 0.95, 1.15, 1], rotate: [0, -10, 8, -4, 0] }
      : state === 'attack'
      ? { x: [0, -18, 0], rotate: [0, -10, 0], scale: [1, 1.12, 1] }
      : state === 'flee'
      ? { y: [0, -16, 0], rotate: [0, -12, 12, 0], scale: [1, 0.92, 1.08, 1] }
      : state === 'approach'
      ? { y: [0, -5, 0], scale: [1, 1.04, 1] }
      : { y: [0, -7, 0] };

  const transition =
    state === 'flee'
      ? { duration: 0.5, ease: 'easeOut' as const }
      : state === 'eat'
      ? { duration: 0.8, repeat: 2 }
      : state === 'attack'
      ? { duration: 0.35 }
      : { duration: 1.4, repeat: Infinity, ease: 'easeInOut' as const };

  return (
    <motion.svg
      width={size}
      height={size}
      viewBox="0 0 100 100"
      animate={animate}
      transition={transition}
      className="overflow-visible select-none"
    >
      {/* body */}
      <path
        d="M 50 15
           C 25 15 12 35 12 55
           C 12 75 25 88 50 88
           C 75 88 88 75 88 55
           C 88 35 75 15 50 15 Z"
        fill={c.body}
        stroke="#1e293b"
        strokeWidth="3"
      />
      {/* belly */}
      <ellipse cx="50" cy="60" rx="24" ry="20" fill={c.belly} opacity="0.85" />
      {/* horns */}
      <path d="M 30 18 L 26 6 L 38 14 Z" fill={c.body} stroke="#1e293b" strokeWidth="2.5" />
      <path d="M 70 18 L 74 6 L 62 14 Z" fill={c.body} stroke="#1e293b" strokeWidth="2.5" />
      
      {/* eyes */}
      <circle cx="38" cy="42" r="10" fill="#fff" stroke="#1e293b" strokeWidth="2.5" />
      <circle cx="62" cy="42" r="10" fill="#fff" stroke="#1e293b" strokeWidth="2.5" />
      
      {state === 'eat' || state === 'attack' ? (
        <>
          {/* Attack pupils */}
          <circle cx="36" cy="43" r="5" fill={c.eye} />
          <circle cx="60" cy="43" r="5" fill={c.eye} />
          <circle cx="38" cy="41" r="1.5" fill="#fff" />
          <circle cx="62" cy="41" r="1.5" fill="#fff" />
          {/* Big Chomp Mouth */}
          <path d="M 32 60 Q 50 78 68 60 Q 50 54 32 60 Z" fill="#b91c1c" stroke="#1e293b" strokeWidth="2.5" />
          {/* Teeth */}
          <polygon points="38,58 42,66 46,58" fill="#fff" stroke="#1e293b" strokeWidth="1" />
          <polygon points="46,58 50,67 54,58" fill="#fff" stroke="#1e293b" strokeWidth="1" />
          <polygon points="54,58 58,66 62,58" fill="#fff" stroke="#1e293b" strokeWidth="1" />
          <polygon points="42,70 46,63 50,70" fill="#fff" stroke="#1e293b" strokeWidth="1" />
          <polygon points="50,70 54,63 58,70" fill="#fff" stroke="#1e293b" strokeWidth="1" />
        </>
      ) : (
        <>
          <circle cx="38" cy="44" r="5" fill={c.eye} />
          <circle cx="62" cy="44" r="5" fill={c.eye} />
          <circle cx="39.5" cy="42" r="1.8" fill="#fff" />
          <circle cx="63.5" cy="42" r="1.8" fill="#fff" />
          {/* goofy teeth smile */}
          <path d="M 35 62 Q 50 74 65 62" stroke="#1e293b" strokeWidth="2.5" fill="#fff" strokeLinecap="round" />
          <rect x="42" y="62" width="4" height="6" fill="#fff" stroke="#1e293b" strokeWidth="1.5" rx="1" />
          <rect x="54" y="62" width="4" height="6" fill="#fff" stroke="#1e293b" strokeWidth="1.5" rx="1" />
        </>
      )}

      {/* feet */}
      <ellipse cx="35" cy="90" rx="8" ry="4" fill={c.body} stroke="#1e293b" strokeWidth="2.5" />
      <ellipse cx="65" cy="90" rx="8" ry="4" fill={c.body} stroke="#1e293b" strokeWidth="2.5" />
    </motion.svg>
  );
}

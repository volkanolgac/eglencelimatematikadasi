import { motion } from 'motion/react';
import { COLOR_MAP, HAT_EMOJI, PET_EMOJI } from '@/lib/cosmetics';

interface ExplorerProps {
  avatar?: string;
  color?: string;
  hat?: string | null;
  pet?: string | null;
  size?: number;
  animate?: boolean;
  eyes?: 'normal' | 'x' | 'happy';
  yOffset?: number;
}

// Precise Head Anchor points based on actual SVG geometry and head boundary of each avatar
interface AnchorPoint {
  x: number;
  y: number;
  scale: number;
  rotation?: number;
}

const AVATAR_HEAD_ANCHORS: Record<string, AnchorPoint> = {
  // Classic Leo: Head circle at cx=50, cy=36, r=24 (Top boundary = y=12)
  avatar_classic: { x: 50, y: 7, scale: 1.0 },

  // Space Astronaut: Helmet at cx=50, cy=34, r=26 (Top boundary = y=8)
  avatar_astro: { x: 50, y: 3, scale: 1.05 },

  // Panda: Head at cx=50, cy=36, r=24, ears at cy=18 r=9 (Top boundary between ears = y=6)
  avatar_panda: { x: 50, y: 5, scale: 0.95 },

  // Wizard: Head at cx=50, cy=36, r=24 (Top boundary = y=12)
  avatar_wizard: { x: 50, y: 7, scale: 1.0 },

  // Knight: Helmet at cx=50, cy=36, r=24 with crest (Top boundary = y=10)
  avatar_knight: { x: 50, y: 4, scale: 1.0 },

  // Robot: Head screen at x=26, y=14, w=48, h=34 (Top boundary = y=14)
  avatar_robot: { x: 50, y: 7, scale: 0.95 },
};

// Hat-specific optical height offsets & scaling
const HAT_OFFSETS: Record<string, { dy: number; scale: number }> = {
  hat_crown: { dy: 2, scale: 1.0 },
  hat_pirate: { dy: 0, scale: 0.95 },
  hat_cap: { dy: 2.5, scale: 0.95 },
  hat_wizard: { dy: -1.5, scale: 1.0 },
  hat_party: { dy: -0.5, scale: 0.95 },
};

export function Explorer({
  avatar = 'avatar_classic',
  color = 'color_amber',
  hat = null,
  pet = null,
  size = 120,
  animate = true,
  eyes = 'normal',
}: ExplorerProps) {
  const c = COLOR_MAP[color] ?? COLOR_MAP.color_amber ?? { body: '#fbbf24', cheek: '#fb7185' };
  const float = animate ? { y: [0, -6, 0] } : {};
  const transition = animate ? { duration: 2.5, repeat: Infinity, ease: 'easeInOut' as const } : {};

  const hatEmoji = hat ? (HAT_EMOJI[hat] ?? null) : null;
  const petEmoji = pet ? (PET_EMOJI[pet] ?? null) : null;

  // Resolve head anchor for current avatar
  const headAnchor = AVATAR_HEAD_ANCHORS[avatar] ?? AVATAR_HEAD_ANCHORS.avatar_classic;
  const hatAdjustment = hat ? (HAT_OFFSETS[hat] ?? { dy: 0, scale: 1 }) : { dy: 0, scale: 1 };

  const hatPosX = headAnchor.x;
  const hatPosY = headAnchor.y + hatAdjustment.dy;
  const hatScale = headAnchor.scale * hatAdjustment.scale;

  return (
    <div className="relative flex items-end justify-center select-none" style={{ width: size, height: size }}>
      {/* 
        Single Animated Container:
        Both the SVG body, head-anchored wearable hat, and companion pet are children of this motion container.
        When the avatar jumps, bobs, kicks, or floats, all wearable accessories move in 100% exact synchronization!
      */}
      <motion.div
        className="relative flex items-center justify-center"
        style={{ width: size, height: size }}
        animate={float}
        transition={transition}
      >
        <svg width={size} height={size} viewBox="0 0 100 100" className="overflow-visible">
          {/* Avatar specific body rendering */}
          {avatar === 'avatar_astro' ? (
            /* ASTRO / ASTRONAUT AVATAR */
            <>
              {/* Space suit backpack */}
              <rect x="22" y="45" width="56" height="38" rx="8" fill="#cbd5e1" stroke="#1e293b" strokeWidth="2.5" />
              {/* Body */}
              <ellipse cx="50" cy="64" rx="30" ry="26" fill="#f8fafc" stroke="#1e293b" strokeWidth="3" />
              {/* Chest control panel */}
              <rect x="38" y="58" width="24" height="14" rx="4" fill={c.body} stroke="#1e293b" strokeWidth="2" />
              <circle cx="44" cy="65" r="2.5" fill="#38bdf8" />
              <circle cx="56" cy="65" r="2.5" fill="#fb7185" />
              {/* Feet */}
              <ellipse cx="38" cy="89" rx="9" ry="6" fill="#64748b" stroke="#1e293b" strokeWidth="2.5" />
              <ellipse cx="62" cy="89" rx="9" ry="6" fill="#64748b" stroke="#1e293b" strokeWidth="2.5" />
              {/* Arms */}
              <ellipse cx="20" cy="62" rx="7" ry="11" fill="#f8fafc" stroke="#1e293b" strokeWidth="2.5" transform="rotate(-15 20 62)" />
              <ellipse cx="80" cy="62" rx="7" ry="11" fill="#f8fafc" stroke="#1e293b" strokeWidth="2.5" transform="rotate(15 80 62)" />
              {/* Helmet */}
              <circle cx="50" cy="34" r="26" fill="#f1f5f9" stroke="#1e293b" strokeWidth="3" />
              {/* Helmet Visor */}
              <ellipse cx="50" cy="35" rx="20" ry="16" fill={c.body} stroke="#1e293b" strokeWidth="2.5" />
              <ellipse cx="50" cy="35" rx="18" ry="14" fill="#0f172a" opacity="0.4" />
              {/* Cheeks */}
              <circle cx="36" cy="41" r="3.5" fill={c.cheek} opacity="0.7" />
              <circle cx="64" cy="41" r="3.5" fill={c.cheek} opacity="0.7" />
              {/* Helmet Antenna */}
              <line x1="50" y1="8" x2="50" y2="2" stroke="#1e293b" strokeWidth="2.5" strokeLinecap="round" />
              <circle cx="50" cy="2" r="3.5" fill="#fbbf24" stroke="#1e293b" strokeWidth="1.5" />
            </>
          ) : avatar === 'avatar_panda' ? (
            /* SEVİMLİ PANDA AVATAR */
            <>
              {/* Panda Ears */}
              <circle cx="28" cy="18" r="9" fill="#1e293b" stroke="#1e293b" strokeWidth="2.5" />
              <circle cx="72" cy="18" r="9" fill="#1e293b" stroke="#1e293b" strokeWidth="2.5" />
              <circle cx="28" cy="18" r="4.5" fill="#e2e8f0" />
              <circle cx="72" cy="18" r="4.5" fill="#e2e8f0" />
              {/* Body */}
              <ellipse cx="50" cy="62" rx="32" ry="28" fill="#f8fafc" stroke="#1e293b" strokeWidth="3" />
              {/* Belly vest */}
              <ellipse cx="50" cy="68" rx="22" ry="18" fill={c.body} opacity="0.85" />
              {/* Feet */}
              <ellipse cx="38" cy="88" rx="8" ry="5" fill="#1e293b" stroke="#1e293b" strokeWidth="2.5" />
              <ellipse cx="62" cy="88" rx="8" ry="5" fill="#1e293b" stroke="#1e293b" strokeWidth="2.5" />
              {/* Arms */}
              <ellipse cx="20" cy="60" rx="7" ry="12" fill="#1e293b" stroke="#1e293b" strokeWidth="2.5" transform="rotate(-15 20 60)" />
              <ellipse cx="80" cy="60" rx="7" ry="12" fill="#1e293b" stroke="#1e293b" strokeWidth="2.5" transform="rotate(15 80 60)" />
              {/* Head */}
              <circle cx="50" cy="36" r="24" fill="#f8fafc" stroke="#1e293b" strokeWidth="3" />
              {/* Panda Eye Patches */}
              <ellipse cx="39" cy="34" rx="7" ry="9" fill="#1e293b" transform="rotate(-12 39 34)" />
              <ellipse cx="61" cy="34" rx="7" ry="9" fill="#1e293b" transform="rotate(12 61 34)" />
              {/* Cheeks */}
              <circle cx="33" cy="43" r="5" fill={c.cheek} opacity="0.6" />
              <circle cx="67" cy="43" r="5" fill={c.cheek} opacity="0.6" />
              {/* Cute Panda Nose */}
              <ellipse cx="50" cy="41" rx="3.5" ry="2.5" fill="#1e293b" />
            </>
          ) : avatar === 'avatar_wizard' ? (
            /* KÜÇÜK BÜYÜCÜ AVATAR */
            <>
              {/* Wizard Cape */}
              <path d="M 20 50 Q 50 40 80 50 L 88 88 L 12 88 Z" fill="#6366f1" stroke="#1e293b" strokeWidth="2.5" />
              {/* Body */}
              <ellipse cx="50" cy="62" rx="30" ry="26" fill={c.body} stroke="#1e293b" strokeWidth="3" />
              {/* Magic Amulet */}
              <circle cx="50" cy="62" r="6" fill="#facc15" stroke="#1e293b" strokeWidth="1.5" />
              <circle cx="50" cy="62" r="3" fill="#38bdf8" />
              {/* Feet */}
              <ellipse cx="38" cy="88" rx="8" ry="5" fill="#4338ca" stroke="#1e293b" strokeWidth="2.5" />
              <ellipse cx="62" cy="88" rx="8" ry="5" fill="#4338ca" stroke="#1e293b" strokeWidth="2.5" />
              {/* Head */}
              <circle cx="50" cy="36" r="24" fill={c.body} stroke="#1e293b" strokeWidth="3" />
              {/* Cheeks */}
              <circle cx="34" cy="42" r="5" fill={c.cheek} opacity="0.6" />
              <circle cx="66" cy="42" r="5" fill={c.cheek} opacity="0.6" />
              {/* Star on forehead */}
              <polygon points="50,18 52,22 56,22 53,25 54,29 50,27 46,29 47,25 44,22 48,22" fill="#fbbf24" stroke="#1e293b" strokeWidth="0.8" />
            </>
          ) : avatar === 'avatar_knight' ? (
            /* CESUR ŞÖVALYE AVATAR */
            <>
              {/* Armor Shoulder guards */}
              <circle cx="20" cy="52" r="9" fill="#94a3b8" stroke="#1e293b" strokeWidth="2" />
              <circle cx="80" cy="52" r="9" fill="#94a3b8" stroke="#1e293b" strokeWidth="2" />
              {/* Armor Body */}
              <ellipse cx="50" cy="62" rx="30" ry="26" fill="#cbd5e1" stroke="#1e293b" strokeWidth="3" />
              <ellipse cx="50" cy="65" rx="18" ry="15" fill={c.body} stroke="#1e293b" strokeWidth="2" />
              {/* Shield emblem on chest */}
              <polygon points="50,58 58,62 50,72 42,62" fill="#ef4444" stroke="#1e293b" strokeWidth="1.5" />
              {/* Feet */}
              <ellipse cx="38" cy="88" rx="8" ry="5" fill="#64748b" stroke="#1e293b" strokeWidth="2.5" />
              <ellipse cx="62" cy="88" rx="8" ry="5" fill="#64748b" stroke="#1e293b" strokeWidth="2.5" />
              {/* Knight Helmet */}
              <circle cx="50" cy="36" r="24" fill="#94a3b8" stroke="#1e293b" strokeWidth="3" />
              <rect x="30" y="24" width="40" height="22" rx="6" fill="#e2e8f0" stroke="#1e293b" strokeWidth="2" />
              {/* Red feather plume */}
              <path d="M 50 14 Q 60 2 68 8 Q 56 12 50 16 Z" fill="#ef4444" stroke="#1e293b" strokeWidth="1.5" />
              {/* Cheeks */}
              <circle cx="35" cy="40" r="4" fill={c.cheek} opacity="0.6" />
              <circle cx="65" cy="40" r="4" fill={c.cheek} opacity="0.6" />
            </>
          ) : avatar === 'avatar_robot' ? (
            /* ROBOT KAŞİF AVATAR */
            <>
              {/* Robot Antenna */}
              <line x1="50" y1="12" x2="50" y2="3" stroke="#1e293b" strokeWidth="2.5" />
              <circle cx="50" cy="3" r="4" fill="#38bdf8" stroke="#1e293b" strokeWidth="1.5" />
              {/* Robot Ears */}
              <rect x="18" y="28" width="6" height="14" rx="2" fill="#94a3b8" stroke="#1e293b" strokeWidth="2" />
              <rect x="76" y="28" width="6" height="14" rx="2" fill="#94a3b8" stroke="#1e293b" strokeWidth="2" />
              {/* Robot Body */}
              <rect x="22" y="44" width="56" height="40" rx="12" fill={c.body} stroke="#1e293b" strokeWidth="3" />
              {/* Chest Screen */}
              <rect x="34" y="54" width="32" height="20" rx="6" fill="#0f172a" stroke="#1e293b" strokeWidth="2" />
              <path d="M 38 64 L 44 58 L 50 68 L 56 60 L 62 64" fill="none" stroke="#34d399" strokeWidth="2" strokeLinecap="round" />
              {/* Feet */}
              <rect x="32" y="84" width="12" height="8" rx="3" fill="#64748b" stroke="#1e293b" strokeWidth="2" />
              <rect x="56" y="84" width="12" height="8" rx="3" fill="#64748b" stroke="#1e293b" strokeWidth="2" />
              {/* Head Screen */}
              <rect x="26" y="14" width="48" height="34" rx="10" fill="#f1f5f9" stroke="#1e293b" strokeWidth="3" />
              {/* Cheeks */}
              <circle cx="34" cy="40" r="4" fill={c.cheek} opacity="0.6" />
              <circle cx="66" cy="40" r="4" fill={c.cheek} opacity="0.6" />
            </>
          ) : (
            /* DEFAULT / KLASİK KAŞİF LEO */
            <>
              {/* Body */}
              <ellipse cx="50" cy="62" rx="32" ry="28" fill={c.body} stroke="#1e293b" strokeWidth="3" />
              {/* Belly */}
              <ellipse cx="50" cy="68" rx="22" ry="18" fill="#fff" opacity="0.85" />
              {/* Feet */}
              <ellipse cx="38" cy="88" rx="8" ry="5" fill={c.body} stroke="#1e293b" strokeWidth="2.5" />
              <ellipse cx="62" cy="88" rx="8" ry="5" fill={c.body} stroke="#1e293b" strokeWidth="2.5" />
              {/* Arms */}
              <ellipse cx="20" cy="60" rx="7" ry="12" fill={c.body} stroke="#1e293b" strokeWidth="2.5" transform="rotate(-15 20 60)" />
              <ellipse cx="80" cy="60" rx="7" ry="12" fill={c.body} stroke="#1e293b" strokeWidth="2.5" transform="rotate(15 80 60)" />
              {/* Head */}
              <circle cx="50" cy="36" r="24" fill={c.body} stroke="#1e293b" strokeWidth="3" />
              {/* Ears */}
              <circle cx="30" cy="20" r="7" fill={c.body} stroke="#1e293b" strokeWidth="2.5" />
              <circle cx="70" cy="20" r="7" fill={c.body} stroke="#1e293b" strokeWidth="2.5" />
              {/* Cheeks */}
              <circle cx="34" cy="42" r="5" fill={c.cheek} opacity="0.6" />
              <circle cx="66" cy="42" r="5" fill={c.cheek} opacity="0.6" />
            </>
          )}

          {/* EYES (NORMAL, X X on lose, or HAPPY) */}
          {eyes === 'x' ? (
            /* X X Defeated / Game Over Eyes (Black X with clean white circular backing) */
            <g>
              {/* White eye backing discs for maximum legibility and clean contrast */}
              <circle cx="40" cy="34" r="5.5" fill="#ffffff" stroke="#1e293b" strokeWidth="1.2" />
              <circle cx="60" cy="34" r="5.5" fill="#ffffff" stroke="#1e293b" strokeWidth="1.2" />
              {/* Bold Black X marks */}
              <g stroke="#1e293b" strokeWidth="2.8" strokeLinecap="round">
                {/* Left Eye X */}
                <line x1="37" y1="31" x2="43" y2="37" />
                <line x1="43" y1="31" x2="37" y2="37" />
                {/* Right Eye X */}
                <line x1="57" y1="31" x2="63" y2="37" />
                <line x1="63" y1="31" x2="57" y2="37" />
              </g>
              {/* Dizzy mouth */}
              <path d="M 42 46 Q 46 43 50 46 Q 54 49 58 46" stroke="#1e293b" strokeWidth="2.5" fill="none" />
            </g>
          ) : eyes === 'happy' ? (
            /* Happy curved eyes */
            <>
              <path d="M 36 34 Q 40 28 44 34" stroke="#1e293b" strokeWidth="3" fill="none" strokeLinecap="round" />
              <path d="M 56 34 Q 60 28 64 34" stroke="#1e293b" strokeWidth="3" fill="none" strokeLinecap="round" />
              <path d="M 42 43 Q 50 51 58 43" stroke="#1e293b" strokeWidth="2.5" fill="#f43f5e" strokeLinecap="round" />
            </>
          ) : (
            /* Normal Cute Sparkly Eyes */
            <>
              <circle cx="40" cy="34" r="4.5" fill="#1e293b" />
              <circle cx="60" cy="34" r="4.5" fill="#1e293b" />
              <circle cx="41.5" cy="32.5" r="1.8" fill="#fff" />
              <circle cx="61.5" cy="32.5" r="1.8" fill="#fff" />
              {/* Smile */}
              <path d="M 42 44 Q 50 50 58 44" stroke="#1e293b" strokeWidth="2.5" fill="none" strokeLinecap="round" />
            </>
          )}

          {/* 
            DYNAMIC HEAD ANCHOR WEARABLE SYSTEM:
            Positions the hat strictly at the avatar's real head top boundary.
          */}
          {hatEmoji && (
            <g transform={`translate(${hatPosX}, ${hatPosY}) scale(${hatScale})`}>
              <text
                x="0"
                y="0"
                textAnchor="middle"
                dominantBaseline="central"
                style={{ fontSize: 32, userSelect: 'none' }}
              >
                {hatEmoji}
              </text>
            </g>
          )}
        </svg>

        {/* 
          COMPANION PET (Synchronized within the same motion container)
        */}
        {petEmoji && (
          <div
            className="absolute -right-2 bottom-0 select-none pointer-events-none filter drop-shadow-xs"
            style={{ fontSize: size * 0.28, lineHeight: 1 }}
          >
            {petEmoji}
          </div>
        )}
      </motion.div>
    </div>
  );
}

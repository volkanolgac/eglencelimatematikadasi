import type { CosmeticItem } from '@/types';

export const COSMETICS: CosmeticItem[] = [
  // Avatars
  { id: 'avatar_classic', name: 'Klasik Kaşif', type: 'avatar', price: 0, emoji: '🧑‍🌾' },
  { id: 'avatar_astro', name: 'Uzay Kaptanı', type: 'avatar', price: 100, emoji: '🧑‍🚀' },
  { id: 'avatar_panda', name: 'Sevimli Panda', type: 'avatar', price: 120, emoji: '🐼' },
  { id: 'avatar_wizard', name: 'Küçük Büyücü', type: 'avatar', price: 150, emoji: '🧙‍♂️' },
  { id: 'avatar_knight', name: 'Cesur Şövalye', type: 'avatar', price: 180, emoji: '🛡️' },
  { id: 'avatar_robot', name: 'Robot Kaşif', type: 'avatar', price: 200, emoji: '🤖' },

  // Hats
  { id: 'hat_pirate', name: 'Korsan Şapkası', type: 'hat', price: 50, emoji: '🏴‍☠️' },
  { id: 'hat_crown', name: 'Taç', type: 'hat', price: 80, emoji: '👑' },
  { id: 'hat_cap', name: 'Beyzbol Şapkası', type: 'hat', price: 30, emoji: '🧢' },
  { id: 'hat_wizard', name: 'Büyücü Şapkası', type: 'hat', price: 100, emoji: '🧙' },
  { id: 'hat_party', name: 'Parti Şapkası', type: 'hat', price: 40, emoji: '🥳' },

  // Pets
  { id: 'pet_cat', name: 'Kedi Yavrusu', type: 'pet', price: 120, emoji: '🐱' },
  { id: 'pet_dog', name: 'Köpek Yavrusu', type: 'pet', price: 120, emoji: '🐶' },
  { id: 'pet_bird', name: 'Kuş', type: 'pet', price: 90, emoji: '🐦' },
  { id: 'pet_turtle', name: 'Kaplumbağa', type: 'pet', price: 70, emoji: '🐢' },
  { id: 'pet_dragon', name: 'Bebe Ejderha', type: 'pet', price: 200, emoji: '🐲' },

  // Colors
  { id: 'color_amber', name: 'Turuncu', type: 'color', price: 0, emoji: '🟠' },
  { id: 'color_sky', name: 'Mavi', type: 'color', price: 40, emoji: '🔵' },
  { id: 'color_mint', name: 'Yeşil', type: 'color', price: 40, emoji: '🟢' },
  { id: 'color_coral', name: 'Pembe', type: 'color', price: 60, emoji: '🌸' },
  { id: 'color_grape', name: 'Mor', type: 'color', price: 80, emoji: '🟣' },
  { id: 'color_gold', name: 'Altın Sarısı', type: 'color', price: 90, emoji: '🟡' },

  // Themes
  { id: 'theme_sky', name: 'Gökyüzü Teması', type: 'theme', price: 0, emoji: '☁️' },
  { id: 'theme_sunset', name: 'Gün Batımı Teması', type: 'theme', price: 100, emoji: '🌅' },
  { id: 'theme_space', name: 'Uzay Teması', type: 'theme', price: 150, emoji: '🚀' },
];

export const COLOR_MAP: Record<string, { body: string; cheek: string }> = {
  amber: { body: '#fbbf24', cheek: '#fb7185' },
  color_amber: { body: '#fbbf24', cheek: '#fb7185' },
  sky: { body: '#38bdf8', cheek: '#fb7185' },
  color_sky: { body: '#38bdf8', cheek: '#fb7185' },
  mint: { body: '#34d399', cheek: '#fb7185' },
  color_mint: { body: '#34d399', cheek: '#fb7185' },
  coral: { body: '#fb7185', cheek: '#fbbf24' },
  color_coral: { body: '#fb7185', cheek: '#fbbf24' },
  grape: { body: '#c084fc', cheek: '#fbbf24' },
  color_grape: { body: '#c084fc', cheek: '#fbbf24' },
  gold: { body: '#facc15', cheek: '#f43f5e' },
  color_gold: { body: '#facc15', cheek: '#f43f5e' },
};

export const HAT_EMOJI: Record<string, string> = {
  hat_pirate: '🏴‍☠️',
  hat_crown: '👑',
  hat_cap: '🧢',
  hat_wizard: '🧙',
  hat_party: '🥳',
};

export const PET_EMOJI: Record<string, string> = {
  pet_cat: '🐱',
  pet_dog: '🐶',
  pet_bird: '🐦',
  pet_turtle: '🐢',
  pet_dragon: '🐲',
};

export interface ThemeConfig {
  id: string;
  name: string;
  background: string;
  type: 'sky' | 'sunset' | 'space';
}

export const THEME_MAP: Record<string, ThemeConfig> = {
  theme_sky: {
    id: 'theme_sky',
    name: 'Gökyüzü Teması',
    background: 'linear-gradient(180deg, #7dd3fc 0%, #bae6fd 40%, #86efac 100%)',
    type: 'sky',
  },
  theme_sunset: {
    id: 'theme_sunset',
    name: 'Gün Batımı Teması',
    background: 'linear-gradient(180deg, #f97316 0%, #fb7185 30%, #a855f7 70%, #6366f1 100%)',
    type: 'sunset',
  },
  theme_space: {
    id: 'theme_space',
    name: 'Uzay Teması',
    background: 'linear-gradient(180deg, #090d16 0%, #1e1b4b 35%, #312e81 70%, #0f172a 100%)',
    type: 'space',
  },
};

export const BADGE_INFO: Record<string, { name: string; emoji: string }> = {
  streak7: { name: '7 Gün Serisi', emoji: '🔥' },
  island2: { name: '2\'ler Adası Şampiyonu', emoji: '🏆' },
  island3: { name: '3\'ler Adası Şampiyonu', emoji: '🏆' },
  island4: { name: '4\'ler Adası Şampiyonu', emoji: '🏆' },
  island5: { name: '5\'ler Adası Şampiyonu', emoji: '🏆' },
  island6: { name: '6\'ler Adası Şampiyonu', emoji: '🏆' },
  island7: { name: '7\'ler Adası Şampiyonu', emoji: '🏆' },
  island8: { name: '8\'ler Adası Şampiyonu', emoji: '🏆' },
  island9: { name: '9\'ler Adası Şampiyonu', emoji: '🏆' },
  first_chest: { name: 'İlk Sandık', emoji: '🎁' },
  combo10: { name: '10 Combo Ustası', emoji: '⚡' },
};

import type { Question, QuestionFormat, WrongRecord } from '@/types';

const TURKISH_NUMS = ['sıfır','bir','iki','üç','dört','beş','altı','yedi','sekiz','dokuz','on',
  'on bir','on iki','on üç','on dört','on beş','on altı','on yedi','on sekiz','on dokuz',
  'yirmi','yirmi bir','yirmi iki','yirmi üç','yirmi dört','yirmi beş','yirmi altı',
  'yirmi yedi','yirmi sekiz','yirmi dokuz','otuz','otuz bir','otuz iki','otuz üç',
  'otuz dört','otuz beş','otuz altı','otuz yedi','otuz sekiz','otuz dokuz','kırk',
  'kırk bir','kırk iki','kırk üç','kırk dört','kırk beş','kırk altı','kırk yedi',
  'kırk sekiz','kırk dokuz','elli','elli bir','elli iki','elli üç','elli dört','elli beş',
  'elli altı','elli yedi','elli sekiz','elli dokuz','altmış','altmış bir','altmış iki',
  'altmış üç','altmış dört','altmış beş','altmış altı','altmış yedi','altmış sekiz',
  'altmış dokuz','yetmiş','yetmiş bir','yetmiş iki','yetmiş üç','yetmiş dört','yetmiş beş',
  'yetmiş altı','yetmiş yedi','yetmiş sekiz','yetmiş dokuz','seksen','seksen bir','seksen iki'];

function numToTr(n: number): string {
  return TURKISH_NUMS[n] ?? String(n);
}

function shuffle<T>(arr: T[]): T[] {
  const a = [...arr];
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [a[i], a[j]] = [a[j], a[i]];
  }
  return a;
}

function makeOptions(answer: number, table: number): number[] {
  const opts = new Set<number>([answer]);
  let guard = 0;
  
  // Plausible distractors (nearby numbers, table multiples, small offsets)
  const deltas = [-3, -2, -1, 1, 2, 3, 4, -4, table, -table, table * 2, -table * 2];
  
  while (opts.size < 4 && guard < 60) {
    guard++;
    const delta = shuffle(deltas)[0];
    let cand = answer + delta;
    if (cand > 0 && cand !== answer) {
      opts.add(cand);
    }
  }

  // Fallbacks if set is not yet 4 items
  let offset = 1;
  while (opts.size < 4) {
    if (answer - offset > 0 && !opts.has(answer - offset)) {
      opts.add(answer - offset);
    }
    if (opts.size < 4 && !opts.has(answer + offset)) {
      opts.add(answer + offset);
    }
    offset++;
  }

  return shuffle([...opts]);
}

const VISUAL_TEMPLATES = [
  (a: number, b: number) => `${a} kutuda her birinde ${b} elma var. Toplam kaç elma?`,
  (a: number, b: number) => `${a} sepetin her birinde ${b} muz var. Toplam kaç muz?`,
  (a: number, b: number) => `${a} kavanozda her birinde ${b} bisküvi var. Toplam kaç bisküvi?`,
  (a: number, b: number) => `${a} sırada her birinde ${b} öğrenci var. Toplam kaç öğrenci?`,
  (a: number, b: number) => `${a} çantada her birinde ${b} kalem var. Toplam kaç kalem?`,
];

export function makeQuestion(table: number, level: number, wrongs: WrongRecord[], questionIndex: number): Question {
  // Spaced repetition: weight questions by wrongness.
  // Higher wrong count => more likely to appear.
  const pool: { a: number; b: number; weight: number }[] = [];
  for (let m = 1; m <= 10; m++) {
    const a = table;
    const b = m;
    const key = `${a}x${b}`;
    const rec = wrongs.find((w) => w.key === key);
    const weight = rec ? 1 + rec.count * 2 : 1;
    pool.push({ a, b, weight });
  }

  // Weighted pick
  const totalWeight = pool.reduce((s, p) => s + p.weight, 0);
  let r = Math.random() * totalWeight;
  let pick = pool[0];
  for (const p of pool) {
    r -= p.weight;
    if (r <= 0) { pick = p; break; }
  }

  const a = pick.a;
  const b = pick.b;
  const product = a * b;

  // Vary format — earlier levels favor standard, later levels mix more.
  const formats: QuestionFormat[] = ['standard', 'reversed'];
  if (level >= 2) formats.push('missing_second', 'missing_first');
  if (level >= 3) formats.push('visual');
  
  // Alternate to avoid repeating same format back-to-back
  const format = formats[questionIndex % formats.length] ?? 'standard';

  let prompt = '';
  let subPrompt: string | undefined;
  let expectedAnswer = product;
  let displayA = a;
  let displayB = b;

  switch (format) {
    case 'standard':
      // a × b = ? -> answer is a * b (e.g. 7 × 2 = ? -> 14)
      prompt = `${a} × ${b} = ?`;
      expectedAnswer = product;
      break;

    case 'reversed':
      // b × a = ? -> answer is a * b (e.g. 2 × 7 = ? -> 14)
      prompt = `${b} × ${a} = ?`;
      expectedAnswer = product;
      break;

    case 'missing_second':
      // a × ? = product -> answer is b (e.g. 3 × ? = 3 -> 1, 4 × ? = 8 -> 2, 5 × ? = 20 -> 4)
      prompt = `${a} × ? = ${product}`;
      expectedAnswer = b;
      break;

    case 'missing_first':
      // ? × b = product -> answer is a (e.g. ? × 3 = 9 -> 3, ? × 4 = 16 -> 4)
      prompt = `? × ${b} = ${product}`;
      expectedAnswer = a;
      break;

    case 'visual': {
      const tpl = VISUAL_TEMPLATES[Math.floor(Math.random() * VISUAL_TEMPLATES.length)];
      prompt = tpl(a, b);
      subPrompt = `${a} × ${b} = ?`;
      expectedAnswer = product;
      break;
    }
  }

  const options = makeOptions(expectedAnswer, table);

  return {
    a: displayA,
    b: displayB,
    answer: expectedAnswer,
    format,
    options,
    prompt,
    subPrompt,
  };
}

export function wrongKey(a: number, b: number): string {
  return `${a}x${b}`;
}

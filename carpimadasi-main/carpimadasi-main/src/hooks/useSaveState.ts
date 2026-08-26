import { useCallback, useEffect, useRef, useState } from 'react';
import type { SaveState } from '@/types';
import { loadSave, saveSave } from '@/lib/storage';

export function useSaveState() {
  const [state, setState] = useState<SaveState>(() => loadSave());
  const timer = useRef<number | null>(null);

  useEffect(() => {
    if (timer.current) window.clearTimeout(timer.current);
    timer.current = window.setTimeout(() => saveSave(state), 200);
    return () => {
      if (timer.current) window.clearTimeout(timer.current);
    };
  }, [state]);

  const update = useCallback((fn: (s: SaveState) => void) => {
    setState((prev) => {
      const next: SaveState = JSON.parse(JSON.stringify(prev));
      fn(next);
      return next;
    });
  }, []);

  return { state, update };
}

import { useCallback, useEffect, useReducer, useState } from 'react'

// key가 바뀌면 이전 요청을 취소한다. 갱신 오류가 나도 마지막 성공 데이터는 남긴다.
export function useResource(key, loader, interval = 0) {
  const [revision, refresh] = useReducer((value) => value + 1, 0)
  const [state, setState] = useState({ key: null, data: null, error: null, updatedAt: null })
  useEffect(() => {
    const controller = new AbortController()
    let busy = false
    const load = async () => {
      if (busy || document.hidden) return
      busy = true
      try {
        const data = await loader(controller.signal)
        if (!controller.signal.aborted)
          setState({ key, data, error: null, updatedAt: new Date().toISOString() })
      } catch (error) {
        if (!controller.signal.aborted)
          setState((previous) => ({
            key,
            data: previous.key === key ? previous.data : null,
            updatedAt: previous.key === key ? previous.updatedAt : null,
            error,
          }))
      } finally {
        busy = false
      }
    }
    void load()
    const timer = interval ? window.setInterval(load, interval) : null
    const onVisible = () => {
      if (!document.hidden) void load()
    }
    document.addEventListener('visibilitychange', onVisible)
    return () => {
      controller.abort()
      window.clearInterval(timer)
      document.removeEventListener('visibilitychange', onVisible)
    }
  }, [key, loader, interval, revision])
  return {
    ...state,
    data: state.key === key ? state.data : null,
    error: state.key === key ? state.error : null,
    loading: state.key !== key || (!state.data && !state.error),
    refresh: useCallback(() => refresh(), []),
  }
}

import { useCallback, useEffect, useState } from 'react'
import { AuthContext } from './auth'
import { api, TOKEN_KEY, tokenExpiresAt } from '../lib/api'
import { useResource } from '../hooks/useResource'

export default function AuthProvider({ children }) {
  const [token, setToken] = useState(() => {
    const stored = sessionStorage.getItem(TOKEN_KEY)
    if (stored && tokenExpiresAt(stored) > Date.now()) return stored
    sessionStorage.removeItem(TOKEN_KEY)
    return null
  })
  const [expired, setExpired] = useState(false)
  // 헤더와 주문 화면이 같은 회원 정보를 공유한다. 로그아웃하면 이전 요청도 취소된다.
  const loadMember = useCallback(
    (signal) => (token ? api('/mypage/overview', { auth: true, signal }) : Promise.resolve(null)),
    [token],
  )
  const overview = useResource(`member:${token || 'guest'}`, loadMember, token ? 30000 : 0)
  useEffect(() => {
    function expire() {
      sessionStorage.removeItem(TOKEN_KEY)
      setToken(null)
      setExpired(true)
    }
    window.addEventListener('zoostock:expired', expire)
    const timer = token
      ? window.setTimeout(expire, Math.max(0, tokenExpiresAt(token) - Date.now()))
      : null
    return () => {
      window.removeEventListener('zoostock:expired', expire)
      window.clearTimeout(timer)
    }
  }, [token])
  function signIn(value) {
    if (!value || tokenExpiresAt(value) <= Date.now())
      throw new Error('유효한 로그인 응답을 받지 못했습니다.')
    sessionStorage.setItem(TOKEN_KEY, value)
    setExpired(false)
    setToken(value)
  }
  function signOut() {
    sessionStorage.removeItem(TOKEN_KEY)
    setExpired(false)
    setToken(null)
  }
  return (
    <AuthContext.Provider
      value={{
        token,
        expired,
        signIn,
        signOut,
        member: token ? overview.data : null,
        memberLoading: Boolean(token && overview.loading),
        memberError: token ? overview.error : null,
        refreshMember: overview.refresh,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

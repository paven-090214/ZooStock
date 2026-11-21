import { useEffect, useState } from 'react'
import { AuthContext } from './auth'
import { TOKEN_KEY, tokenExpiresAt } from '../lib/api'

export default function AuthProvider({ children }) {
  const [token, setToken] = useState(() => {
    const stored = sessionStorage.getItem(TOKEN_KEY)
    if (stored && tokenExpiresAt(stored) > Date.now()) return stored
    sessionStorage.removeItem(TOKEN_KEY)
    return null
  })
  const [expired, setExpired] = useState(false)
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
    <AuthContext.Provider value={{ token, expired, signIn, signOut }}>
      {children}
    </AuthContext.Provider>
  )
}

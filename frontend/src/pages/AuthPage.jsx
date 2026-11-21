import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router'
import { useAuth } from '../auth/auth'
import { api } from '../lib/api'

export default function AuthPage({ signup = false }) {
  const { signIn } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  async function submit(event) {
    event.preventDefault()
    if (busy) return
    const data = new FormData(event.currentTarget)
    const body = {
      memberId: data.get('memberId').trim(),
      memberPassword: data.get('memberPassword'),
    }
    if (signup) body.memberName = data.get('memberName').trim()
    if (!body.memberId || !body.memberPassword.trim() || (signup && !body.memberName)) {
      setError('모든 입력란을 채워주세요.')
      return
    }
    if (signup && body.memberPassword !== data.get('confirmation')) {
      setError('비밀번호 확인이 일치하지 않습니다.')
      return
    }
    setBusy(true)
    setError('')
    try {
      const result = await api(signup ? '/signup' : '/login', { method: 'POST', body })
      if (signup) navigate('/login', { replace: true, state: { signedUp: true } })
      else {
        signIn(result)
        const destination = location.state?.from
        navigate(
          typeof destination === 'string' &&
            destination.startsWith('/') &&
            !destination.startsWith('//')
            ? destination
            : '/token',
          { replace: true },
        )
      }
    } catch (failure) {
      setError(failure.message)
    } finally {
      setBusy(false)
    }
  }
  return (
    <div className="auth-layout">
      <div className="auth-intro">
        <span className="eyebrow">WELCOME TO THE WILD</span>
        <h1>
          좋아하는 동물과
          <br />
          <em>함께 시작하세요.</em>
        </h1>
        <p>
          처음이어도 괜찮아요.
          <br />
          가상의 포인트로 나만의 투자 경험을 만들어보세요.
        </p>
        <div className="auth-decoration" aria-hidden="true">
          z<span>•</span>
        </div>
      </div>
      <section className="panel auth-panel">
        <span className="eyebrow">{signup ? 'JOIN ZOOSTOCK' : 'GOOD TO SEE YOU'}</span>
        <h2>{signup ? '회원가입' : '로그인'}</h2>
        <p>{signup ? '나만의 포트폴리오를 만들어보세요.' : '오늘은 어떤 동물을 만나볼까요?'}</p>
        {location.state?.signedUp && (
          <p className="notice success" role="status">
            회원가입이 완료되었습니다. 로그인해주세요.
          </p>
        )}
        <form onSubmit={submit}>
          {signup && (
            <label className="field">
              닉네임
              <input
                name="memberName"
                autoComplete="nickname"
                maxLength={50}
                required
                disabled={busy}
              />
            </label>
          )}
          <label className="field">
            아이디
            <input
              name="memberId"
              autoComplete="username"
              maxLength={100}
              required
              disabled={busy}
            />
          </label>
          <label className="field">
            비밀번호
            <input
              name="memberPassword"
              type="password"
              autoComplete={signup ? 'new-password' : 'current-password'}
              required
              disabled={busy}
            />
          </label>
          {signup && (
            <label className="field">
              비밀번호 확인
              <input
                name="confirmation"
                type="password"
                autoComplete="new-password"
                required
                disabled={busy}
              />
            </label>
          )}
          {error && (
            <p className="notice error" role="alert">
              {error}
            </p>
          )}
          <button type="submit" className="button full-width" disabled={busy}>
            {busy ? '처리 중…' : signup ? '회원가입' : '로그인'} <span>↗</span>
          </button>
        </form>
        <div className="auth-switch">
          {signup ? '이미 계정이 있으신가요?' : 'ZooStock이 처음이신가요?'}{' '}
          <Link to={signup ? '/login' : '/signup'}>{signup ? '로그인' : '회원가입'}</Link>
        </div>
        {!signup && (
          <p className="form-hint">
            로그인은 현재 탭에서 유지됩니다. 탭을 닫거나 토큰이 만료되면 다시 로그인해주세요.
          </p>
        )}
      </section>
    </div>
  )
}

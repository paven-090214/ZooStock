import { BrowserRouter, Link, Navigate, Outlet, Route, Routes, useLocation } from 'react-router'
import AuthProvider from '../auth/AuthProvider'
import { isAdminMember, useAuth } from '../auth/auth'
import Layout from '../components/Layout'
import Home from '../pages/Home'
import Market from '../pages/Market'
import TokenList from '../pages/TokenList'
import TokenDetail from '../pages/TokenDetail'
import AuthPage from '../pages/AuthPage'
import Portfolio from '../pages/Portfolio'

import BoardList from '../pages/BoardList'
import BoardDetail from '../pages/BoardDetail'

import Ranking from '../pages/Ranking'
import Admin from '../pages/Admin'

import { useEffect } from 'react'

function ScrollToTop() {
  const { pathname } = useLocation()
  useEffect(() => {
    window.scrollTo(0, 0)
  }, [pathname])
  return null
}

function RequireAuth() {
  const { token } = useAuth()
  const location = useLocation()
  return token ? (
    <Outlet />
  ) : (
    <Navigate to="/login" replace state={{ from: location.pathname + location.search }} />
  )
}

// 관리자 화면 보호. 화면을 숨기는 용도이며, 실제 차단은 백엔드(403)가 한다.
// 새로고침 직후에는 회원 정보가 오기 전이라 기다렸다가 판단한다.
function RequireAdmin() {
  const { token, member, memberLoading, memberError } = useAuth()
  const location = useLocation()
  if (!token) return <Navigate to="/login" replace state={{ from: location.pathname }} />
  if (!member && memberLoading)
    return (
      <div className="state-box" role="status">
        <span className="spinner" /> 권한을 확인하는 중이에요.
      </div>
    )
  if (!member)
    return (
      <div className="state-box error" role="alert">
        {memberError?.message || '회원 정보를 확인하지 못했습니다.'}
      </div>
    )
  return isAdminMember(member) ? <Outlet /> : <Navigate to="/" replace />
}

// 로그인한 사용자가 회원가입·로그인 화면에 다시 들어가지 않도록 한다.
function GuestOnly() {
  const { token } = useAuth()
  const location = useLocation()
  const from = location.state?.from
  const destination =
    typeof from === 'string' &&
    from.startsWith('/') &&
    !from.startsWith('//') &&
    !/^\/(login|signup)([/?#]|$)/.test(from)
      ? from
      : '/token'
  return token ? <Navigate to={destination} replace /> : <Outlet />
}

export default function AppRouter() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ScrollToTop />
        <Routes>
          <Route element={<Layout />}>
            <Route index element={<Home />} />
            <Route path="token" element={<Market />} />
            <Route path="token/all" element={<TokenList />} />
            <Route path="token/search" element={<TokenList search />} />
            <Route path="token/:id" element={<TokenDetail />} />
            <Route path="ranking" element={<Ranking />} />
            <Route element={<RequireAdmin />}>
              <Route path="admin" element={<Admin />} />
            </Route>
            <Route element={<GuestOnly />}>
              <Route path="login" element={<AuthPage key="login" />} />
              <Route path="signup" element={<AuthPage key="signup" signup />} />
            </Route>
            <Route element={<RequireAuth />}>
              <Route path="mypage" element={<Portfolio />} />
              <Route path="mypage/history" element={<Portfolio history />} />
              <Route path="board" element={<BoardList />} />
              <Route path="board/:id" element={<BoardDetail />} />
            </Route>
            <Route
              path="*"
              element={
                <section className="not-found">
                  <span className="eyebrow">404 / LOST IN THE WILD</span>
                  <h1>길을 조금 벗어났네요.</h1>
                  <p>찾으시는 페이지가 없습니다.</p>
                  <Link className="button" to="/">
                    홈으로 돌아가기 ↗
                  </Link>
                </section>
              }
            />
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

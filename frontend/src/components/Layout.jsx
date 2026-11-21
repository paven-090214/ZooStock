import { Link, NavLink, Outlet, useNavigate } from 'react-router'
import { useAuth } from '../auth/auth'

export default function Layout() {
  const { token, expired, signOut } = useAuth()
  const navigate = useNavigate()
  function search(event) {
    event.preventDefault()
    const name = new FormData(event.currentTarget).get('name').trim()
    if (name) navigate(`/token/search?name=${encodeURIComponent(name)}`)
  }
  return (
    <div className="app-shell">
      <a className="skip-link" href="#main">
        본문으로 이동
      </a>
      <header className="site-header">
        <div className="header-inner">
          <Link className="brand" to="/" aria-label="ZooStock 홈">
            <span className="brand-mark">
              z<span>•</span>
            </span>
            ZooStock<span className="brand-tag">모의 투자</span>
          </Link>
          <nav className="primary-nav" aria-label="주 메뉴">
            <NavLink to="/token" end>
              마켓
            </NavLink>
            <NavLink to="/token/all">전체 토큰</NavLink>
          </nav>
          <form className="header-search" role="search" onSubmit={search}>
            <span aria-hidden="true">⌕</span>
            <input
              name="name"
              aria-label="토큰 이름 검색"
              placeholder="어떤 동물을 찾으세요?"
              required
              maxLength={100}
            />
            <button type="submit" aria-label="검색">
              ↵
            </button>
          </form>
          <nav className="account-nav" aria-label="회원 메뉴">
            {token ? (
              <>
                <NavLink to="/mypage">내 정보</NavLink>
                <button
                  className="text-button"
                  onClick={() => {
                    signOut()
                    navigate('/')
                  }}
                >
                  로그아웃
                </button>
              </>
            ) : (
              <>
                <Link to="/login">로그인</Link>
                <Link className="button small" to="/signup">
                  회원가입
                </Link>
              </>
            )}
          </nav>
        </div>
      </header>
      {expired && (
        <div className="session-notice" role="status">
          로그인이 만료되었습니다. <Link to="/login">다시 로그인</Link>해주세요.
        </div>
      )}
      <main id="main" className="main-content">
        <Outlet />
      </main>
      <footer className="site-footer">
        <Link className="footer-brand" to="/">
          ZooStock<span>동물과 함께, 투자의 첫걸음.</span>
        </Link>
        <p>
          실제 자산을 거래하지 않는 모의 투자 서비스입니다.
          <br />
          표시된 가격과 수익률은 가상의 데이터입니다.
        </p>
        <span className="footer-credit">동물 이미지 · iNaturalist</span>
      </footer>
    </div>
  )
}

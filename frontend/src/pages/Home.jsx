import { Link } from 'react-router'
import { useAuth } from '../auth/auth'

export default function Home() {
  const { token } = useAuth()
  return (
    <div className="home-page">
      <section className="home-hero">
        <div className="hero-copy">
          <span className="eyebrow">
            <span className="status-dot" /> A WILDER WAY TO INVEST
          </span>
          <h1>
            새로운 투자의 세계,
            <br />
            <em>동물에서 시작하다.</em>
          </h1>
          <p>
            사자부터 작은 새까지, 좋아하는 동물이 토큰이 됩니다.
            <br />
            가상의 포인트로 거래하며 나만의 투자 감각을 키워보세요.
          </p>
          <div className="hero-actions">
            <Link className="button" to="/token">
              마켓 둘러보기 <span>↗</span>
            </Link>
            <Link className="button secondary" to={token ? '/token' : '/signup'}>
              {token ? '매매 시작하기' : '무료로 시작하기'}
            </Link>
          </div>
          <span className="hero-note">실제 돈 없이, 부담 없이 시작하는 모의 투자</span>
        </div>
        <div className="hero-art" aria-hidden="true">
          <div className="art-ring ring-one" />
          <div className="art-ring ring-two" />
          <div className="art-orbit orbit-one">↗</div>
          <div className="art-orbit orbit-two">Z</div>
          <div className="hero-terminal">
            <div className="terminal-heading">
              <span className="mini-logo">z.</span>
              <span>
                THE WILD MARKET<small>당신의 가능성이 자라는 곳</small>
              </span>
              <span className="terminal-dot" />
            </div>
            <div className="terminal-title">
              작은 호기심이
              <br />
              <strong>새로운 경험으로.</strong>
            </div>
            <svg viewBox="0 0 340 150">
              <defs>
                <linearGradient id="hero-fill" x1="0" y1="0" x2="0" y2="1">
                  <stop stopColor="#b9e2ac" stopOpacity=".35" />
                  <stop offset="1" stopColor="#b9e2ac" stopOpacity="0" />
                </linearGradient>
              </defs>
              <path
                d="M0 130 L25 112 L48 122 L76 88 L102 97 L130 65 L160 82 L184 44 L215 58 L242 28 L267 43 L297 17 L340 5 L340 150 L0 150Z"
                fill="url(#hero-fill)"
              />
              <path
                d="M0 130 L25 112 L48 122 L76 88 L102 97 L130 65 L160 82 L184 44 L215 58 L242 28 L267 43 L297 17 L340 5"
                fill="none"
                stroke="#d0ee9e"
                strokeWidth="3"
              />
            </svg>
            <div className="terminal-footer">
              <span>EXPLORE. LEARN. GROW.</span>
              <span>↗</span>
            </div>
          </div>
          <div className="art-label">
            A little wild.
            <br />
            <strong>A lot of possibilities.</strong>
          </div>
        </div>
      </section>
      <section className="home-features" aria-label="서비스 소개">
        <article>
          <span>01 / EXPLORE</span>
          <h2>발견하는 즐거움</h2>
          <p>다양한 동물 토큰과 매일 달라지는 시장의 흐름을 만나보세요.</p>
          <Link to="/token/all">전체 토큰 보기 ↗</Link>
        </article>
        <article>
          <span>02 / TRADE</span>
          <h2>경험으로 배우는 투자</h2>
          <p>가격 차트를 살펴보고 포인트로 매수·매도하며 직접 경험해보세요.</p>
          <Link to="/token">매매 시작하기 ↗</Link>
        </article>
        <article>
          <span>03 / REFLECT</span>
          <h2>쌓여가는 나만의 기록</h2>
          <p>보유 토큰과 거래 기록을 돌아보며 나만의 투자 방식을 찾아보세요.</p>
          <Link to="/mypage">내 포트폴리오 보기 ↗</Link>
        </article>
      </section>
    </div>
  )
}

import { useCallback, useRef, useState } from 'react'
import { Link } from 'react-router'
import { api } from '../lib/api'
import { points } from '../lib/format'
import { useResource } from '../hooks/useResource'
import { Change, RefreshStatus, ResourceState, TokenImage } from '../components/ui'

function Ranking({ title, subtitle, tokens, kind }) {
  return (
    <section className="panel ranking-panel">
      <div className="panel-heading">
        <div>
          <span className={`ranking-icon ${kind}`}>{kind === 'up' ? '↗' : '↘'}</span>
          <h2>{title}</h2>
        </div>
        <span>{subtitle}</span>
      </div>
      {!tokens.length ? (
        <div className="state-box">아직 순위 데이터가 없습니다.</div>
      ) : (
        <ol className="ranking-list">
          {tokens.map((token, index) => (
            <li key={token.tokenId}>
              <Link to={`/token/${token.tokenId}`}>
                <span className="rank-number">{String(index + 1).padStart(2, '0')}</span>
                <span className="rank-name">
                  <strong>{token.tokenName}</strong>
                  <small>ZOO #{token.tokenId}</small>
                </span>
                <span className="rank-price">
                  <strong>
                    {points(token.currentPrice)} <small>P</small>
                  </strong>
                  <Change value={token.changeRate} />
                </span>
                <span className="rank-arrow">↗</span>
              </Link>
            </li>
          ))}
        </ol>
      )}
      <p className="panel-footnote">오늘 첫 가격 기록 대비 · 현재 API의 변동률 순위</p>
    </section>
  )
}

export default function Market() {
  const fixedIds = useRef(null)
  const reselectRequested = useRef(false)
  const [selecting, setSelecting] = useState(false)
  const loader = useCallback(async (signal) => {
    // 처음 진입하거나 사용자가 버튼을 누른 경우에만 서버의 새 추첨을 사용한다.
    const reselect = fixedIds.current === null || reselectRequested.current
    try {
      const overview = await api('/token', { signal })
      if (signal.aborted) return null
      const randomTokens = reselect
        ? overview.randomTokens
        : await Promise.all(fixedIds.current.map((id) => api(`/token/search/${id}`, { signal })))
      if (signal.aborted) return null
      // 성공한 응답만 반영한다. 실패하면 기존 카드와 선택을 유지한다.
      fixedIds.current = randomTokens.map((token) => token.tokenId)
      return { ...overview, randomTokens }
    } finally {
      if (!signal.aborted) {
        reselectRequested.current = false
        setSelecting(false)
      }
    }
  }, [])
  const resource = useResource('market', loader, 30000)
  function refreshRandomTokens() {
    if (selecting) return
    reselectRequested.current = true
    setSelecting(true)
    resource.refresh()
  }
  return (
    <>
      <div className="page-heading">
        <div>
          <span className="eyebrow">THE WILD MARKET</span>
          <h1>
            오늘의 마켓<span className="heading-dot">.</span>
          </h1>
          <p>마음에 드는 동물을 발견하고, 시장의 흐름을 살펴보세요.</p>
        </div>
        <Link className="button secondary" to="/token/all">
          전체 토큰 보기 ↗
        </Link>
      </div>
      <RefreshStatus
        resource={{ ...resource, refresh: refreshRandomTokens, refreshing: selecting }}
      />
      <ResourceState resource={resource} />
      {resource.data && (
        <>
          <section className="discovery-section">
            <div className="section-heading">
              <h2>
                새롭게 만나는 동물들{' '}
                <span className="count-badge">{resource.data.randomTokens.length}</span>
              </h2>
              <button
                className="text-button random-picks-refresh"
                onClick={refreshRandomTokens}
                disabled={selecting}
                aria-label="랜덤 동물 새로고침"
              >
                {selecting ? '새 동물 불러오는 중…' : '다른 동물 보기 ↻'}
              </button>
            </div>
            {!resource.data.randomTokens.length ? (
              <div className="state-box">등록된 동물 토큰이 아직 없어요.</div>
            ) : (
              <div className="discovery-grid">
                {resource.data.randomTokens.map((token, index) => (
                  <Link
                    className="discovery-card"
                    to={`/token/${token.tokenId}`}
                    key={token.tokenId}
                  >
                    <div className="card-image">
                      <TokenImage token={token} large />
                      <span className="card-index">PICK {String(index + 1).padStart(2, '0')}</span>
                      <span className="card-arrow">↗</span>
                    </div>
                    <div className="card-body">
                      <small>ZOO #{token.tokenId}</small>
                      <h3>{token.tokenName}</h3>
                      <div className="card-price">
                        {points(token.tokenPrice)} <small>P</small>
                      </div>
                    </div>
                  </Link>
                ))}
              </div>
            )}
          </section>
          <div className="section-heading ranking-heading">
            <h2>지금, 시장의 움직임</h2>
            <span>DAILY MOVERS</span>
          </div>
          <div className="ranking-grid">
            <Ranking
              title="급등 토큰"
              subtitle="변동률 상위"
              kind="up"
              tokens={resource.data.dailyRankingResponse.gainers}
            />
            <Ranking
              title="급락 토큰"
              subtitle="변동률 하위"
              kind="down"
              tokens={resource.data.dailyRankingResponse.losers}
            />
          </div>
        </>
      )}
      <div className="market-note">
        <span>ⓘ</span> 모든 가격은 가상 포인트 기준입니다. 실제 동물의 가치나 보전 상태를 나타내지
        않습니다.
      </div>
    </>
  )
}

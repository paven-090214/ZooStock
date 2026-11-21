import { useCallback } from 'react'
import { Link } from 'react-router'
import { useAuth } from '../auth/auth'
import { api } from '../lib/api'
import { dateTime, points } from '../lib/format'
import { useResource } from '../hooks/useResource'
import { RefreshStatus, ResourceState, TokenImage } from '../components/ui'

export default function Portfolio({ history = false }) {
  const { token } = useAuth()
  const loader = useCallback(
    async (signal) => {
      const [items, tokens] = await Promise.all([
        api(history ? '/mypage/history' : '/mypage', { auth: true, signal }),
        api('/token/all', { signal }),
      ])
      const byId = new Map(tokens.map((item) => [String(item.tokenId), item]))
      return {
        items: history
          ? items.toSorted((a, b) => new Date(b.tradedAt) - new Date(a.tradedAt))
          : items,
        byId,
      }
    },
    [history],
  )
  const resource = useResource(`personal:${history}:${token}`, loader, 30000)
  return (
    <>
      <div className="page-heading">
        <div>
          <span className="eyebrow">MY ZOOSTOCK</span>
          <h1>
            {history ? '거래 기록' : '내 포트폴리오'}
            <span className="heading-dot">.</span>
          </h1>
          <p>
            {history ? '지금까지의 투자 경험을 돌아보세요.' : '내가 보유한 동물 토큰을 모아보세요.'}
          </p>
        </div>
        <Link className="button secondary" to={history ? '/mypage' : '/mypage/history'}>
          {history ? '보유 토큰 보기' : '거래 기록 보기'} ↗
        </Link>
      </div>
      <RefreshStatus resource={resource} />
      <div className="panel">
        <ResourceState
          resource={resource}
          empty={resource.data?.items.length === 0}
          emptyMessage={
            history
              ? '아직 거래 기록이 없어요.'
              : '아직 보유한 토큰이 없어요. 마켓에서 첫 거래를 시작해보세요.'
          }
        />
        {!!resource.data?.items.length && (
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  {(history
                    ? ['거래 시각', '토큰', '구분', '수량', '체결가']
                    : ['보유 토큰', '보유 수량', '평균 매수가', '현재 가격']
                  ).map((title) => (
                    <th key={title} scope="col">
                      {title}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {resource.data.items.map((item) => {
                  const animal = resource.data.byId.get(String(item.tokenId)) || {
                    tokenId: item.tokenId,
                    tokenName: `토큰 #${item.tokenId}`,
                  }
                  const identity = (
                    <Link to={`/token/${item.tokenId}`} className="token-identity">
                      <TokenImage token={animal} />
                      <strong>{animal.tokenName}</strong>
                    </Link>
                  )
                  return (
                    <tr key={history ? item.tradeId : item.holdingId}>
                      {history ? (
                        <>
                          <td className="muted nowrap">{dateTime(item.tradedAt)}</td>
                          <td>{identity}</td>
                          <td>
                            <span
                              className={`trade-badge ${item.tradeType === 'BUY' ? 'up' : 'down'}`}
                            >
                              {item.tradeType === 'BUY' ? '매수' : '매도'}
                            </span>
                          </td>
                          <td className="numeric">{points(item.quantity)}개</td>
                          <td className="numeric">{points(item.tradePrice)} P</td>
                        </>
                      ) : (
                        <>
                          <td>{identity}</td>
                          <td className="numeric">{points(item.quantity)}개</td>
                          <td className="numeric">{points(item.buyPriceAvg)} P</td>
                          <td className="numeric">
                            {animal.tokenPrice === undefined
                              ? '—'
                              : `${points(animal.tokenPrice)} P`}
                          </td>
                        </>
                      )}
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
      {resource.data?.items.length === 0 && (
        <div className="empty-action">
          <Link className="button" to="/token">
            마켓 둘러보기 ↗
          </Link>
        </div>
      )}
    </>
  )
}

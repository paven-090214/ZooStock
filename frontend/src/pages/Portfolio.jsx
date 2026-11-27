import { useCallback } from 'react'
import { Link, useSearchParams } from 'react-router'
import { useAuth } from '../auth/auth'
import { api, fetchAllTokens, readPage } from '../lib/api'
import { dateTime, points } from '../lib/format'
import { useResource } from '../hooks/useResource'
import { Pagination, Rate, RefreshStatus, ResourceState, TokenImage } from '../components/ui'

// 표 제목. numeric이 true인 칸은 숫자 칸과 같이 오른쪽에 맞춘다.
const HISTORY_COLUMNS = [
  ['거래 시각', false],
  ['토큰', false],
  ['구분', false],
  ['수량', true],
  ['체결가', true],
]
const HOLDING_COLUMNS = [
  ['보유 토큰', false],
  ['보유 수량', true],
  ['평균 매수가', true],
  ['현재 가격', true],
  ['수익률', true],
]

export default function Portfolio({ history = false }) {
  const { token } = useAuth()
  const [params, setParams] = useSearchParams()
  // 거래 기록만 페이지가 있다. 1페이지부터 세고, 잘못된 값은 1페이지로 본다.
  const page = history ? Math.max(1, Number.parseInt(params.get('page'), 10) || 1) : 1
  const loader = useCallback(
    async (signal) => {
      const [data, tokens] = await Promise.all([
        api(history ? `/mypage/history?page=${page}` : '/mypage', { auth: true, signal }),
        fetchAllTokens(signal),
      ])
      const byId = new Map(tokens.map((item) => [String(item.tokenId), item]))
      // 거래 기록은 서버가 최신순으로 나눠 보내는 Page, 보유 토큰은 배열이다.
      return { ...readPage(data), byId }
    },
    [history, page],
  )
  const resource = useResource(`personal:${history}:${page}:${token}`, loader, 30000)
  const changePage = (value) => {
    setParams({ page: String(value) })
    window.scrollTo(0, 0)
  }
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
      {history && resource.data && (
        <div className="list-toolbar">
          <span>
            <strong>{resource.data.totalElements}</strong>건의 거래
            {resource.data.totalPages > 1 && (
              <small className="toolbar-page">
                {' '}
                · {page} / {resource.data.totalPages} 페이지
              </small>
            )}
          </span>
        </div>
      )}
      <RefreshStatus resource={resource} />
      <div className="panel">
        <ResourceState
          resource={resource}
          empty={resource.data?.items.length === 0}
          emptyMessage={
            history
              ? page > 1
                ? '이 페이지에는 거래 기록이 없습니다.'
                : '아직 거래 기록이 없어요.'
              : '아직 보유한 토큰이 없어요. 마켓에서 첫 거래를 시작해보세요.'
          }
        />
        {!!resource.data?.items.length && (
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  {(history ? HISTORY_COLUMNS : HOLDING_COLUMNS).map(([title, numeric]) => (
                    <th key={title} scope="col" className={numeric ? 'numeric' : undefined}>
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
                          <td className="numeric">
                            <Rate value={item.profitRate} />
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
      {history && resource.data && (
        <Pagination
          page={page}
          totalPages={resource.data.totalPages}
          onChange={changePage}
          label="거래 기록 페이지"
        />
      )}
      {resource.data?.items.length === 0 && page === 1 && (
        <div className="empty-action">
          <Link className="button" to="/token">
            마켓 둘러보기 ↗
          </Link>
        </div>
      )}
    </>
  )
}

import { useCallback, useEffect, useRef, useState } from 'react'
import { Link, useLocation, useParams } from 'react-router'
import { useAuth } from '../auth/auth'
import { api } from '../lib/api'
import { points } from '../lib/format'
import { useResource } from '../hooks/useResource'
import PriceChart from '../components/PriceChart'
import { Rate, RefreshStatus, ResourceState, TokenImage } from '../components/ui'

// 차트 기간. 백엔드의 history 값과 같아야 한다. 기간을 바꿀 때는 이 목록만 고친다.
const PERIODS = [
  ['1h', '1시간'],
  ['5h', '5시간'],
  ['1d', '1일'],
]

function OrderPanel({ token, holding, refresh, holdingError }) {
  const { token: accessToken, refreshMember } = useAuth()
  const location = useLocation()
  const [side, setSide] = useState('buy')
  const [quantity, setQuantity] = useState('1')
  const [confirming, setConfirming] = useState(false)
  const [busy, setBusy] = useState(false)
  const [notice, setNotice] = useState(null)
  const number = Number(quantity)
  const valid =
    /^[1-9]\d*$/.test(quantity) &&
    Number.isSafeInteger(number) &&
    Number.isSafeInteger(number * token.tokenPrice)
  const insufficient = side === 'sell' && (holdingError || !holding || number > holding.quantity)
  // 로그인 변경·화면 이탈 시 진행 중 요청의 후속 UI 갱신을 중단한다.
  const controller = useRef(null)
  useEffect(() => {
    const current = new AbortController()
    controller.current = current
    return () => current.abort()
  }, [])
  async function submit(event) {
    event.preventDefault()
    if (!valid || insufficient || busy) return
    if (!confirming) {
      setConfirming(true)
      setNotice(null)
      return
    }
    setBusy(true)
    setNotice(null)
    try {
      // 성공 문구(message)와 체결 결과(result)를 함께 쓰려고 공통 응답 전체를 받는다.
      const response = await api(`/trade/${token.tokenId}/${side}?quantity=${quantity}`, {
        method: 'POST',
        auth: true,
        signal: controller.current.signal,
        envelope: true,
      })
      const trade = response.result
      const detail =
        trade && typeof trade === 'object' && trade.quantity != null && trade.price != null
          ? ` ${points(trade.quantity)}개 × ${points(trade.price)} P`
          : ''
      setNotice({
        type: 'success',
        message: `${response.message || '거래가 완료되었습니다.'}${detail}`,
      })
      setConfirming(false)
      // 거래가 끝나면 수량을 1로 되돌린다. 매도 후 줄어든 보유 수량과 비교되어
      // "보유 수량보다 많습니다" 문구가 바로 뜨는 것을 막는다.
      setQuantity('1')
      refresh()
      // 서버가 거래를 확정한 후 잔액을 다시 조회해 공통 헤더에 반영한다.
      refreshMember()
    } catch (error) {
      if (error.name === 'AbortError') return
      setNotice({
        type: 'error',
        message:
          error.status === 0
            ? `${error.message} 거래 기록을 확인한 뒤 다시 주문해주세요.`
            : error.message,
      })
      setConfirming(false)
      if (error.status === 409) {
        refresh()
        refreshMember()
      }
    } finally {
      if (!controller.current.signal.aborted) setBusy(false)
    }
  }
  return (
    <aside className="panel order-panel">
      <div className="section-heading">
        <h2>토큰 거래</h2>
        <span className="pill">모의 투자</span>
      </div>
      <div className="order-tabs" role="group" aria-label="주문 종류">
        {[
          ['buy', '매수'],
          ['sell', '매도'],
        ].map(([value, title]) => (
          <button
            disabled={busy}
            aria-pressed={side === value}
            key={value}
            className={side === value ? `selected ${value}` : ''}
            onClick={() => {
              setSide(value)
              setConfirming(false)
              setNotice(null)
            }}
          >
            {title}
          </button>
        ))}
      </div>
      {accessToken ? (
        <form onSubmit={submit}>
          <label className="field">
            수량
            <input
              type="number"
              min="1"
              step="1"
              inputMode="numeric"
              value={quantity}
              disabled={busy}
              onChange={(event) => {
                setQuantity(event.target.value)
                setConfirming(false)
                setNotice(null)
              }}
              required
            />
          </label>
          <div className="quantity-buttons">
            {[1, 5, 10].map((value) => (
              <button
                key={value}
                type="button"
                disabled={busy}
                onClick={() => {
                  setQuantity(String(value))
                  setConfirming(false)
                }}
              >
                {value}개
              </button>
            ))}
          </div>
          {!valid && (
            <p className="form-hint error">거래 가능한 범위의 양의 정수를 입력해주세요.</p>
          )}
          <dl className="order-summary">
            <div>
              <dt>현재 가격</dt>
              <dd>{points(token.tokenPrice)} P</dd>
            </div>
            <div>
              <dt>보유 수량</dt>
              <dd>{holdingError ? '조회 실패' : `${points(holding?.quantity || 0)}개`}</dd>
            </div>
            {/* 보유 중일 때만 평균 매수가와 수익률을 보여준다. */}
            {!holdingError && holding && (
              <>
                <div>
                  <dt>평균 매수가</dt>
                  <dd>{points(holding.buyPriceAvg)} P</dd>
                </div>
                <div>
                  <dt>수익률</dt>
                  <dd className="profit-rate">
                    <Rate value={holding.profitRate} />
                  </dd>
                </div>
              </>
            )}
            <div className="total">
              <dt>예상 거래 금액</dt>
              <dd>
                {valid ? points(number * token.tokenPrice) : '—'} <small>P</small>
              </dd>
            </div>
          </dl>
          <p className="form-hint">
            실제 체결가는 주문 처리 시점의 가격으로 결정됩니다. 최신 잔액과 보유 수량은 서버에서
            검증합니다.
          </p>
          {insufficient && (
            // 보유 수량이 0인 것은 오류가 아니라 현재 상태라서 회색 안내로 보여준다.
            <p className={`form-hint ${!holdingError && !holding ? '' : 'error'}`}>
              {holdingError
                ? '보유 수량을 확인하지 못했습니다. 새로고침해주세요.'
                : !holding
                  ? '보유한 수량이 없어 매도할 수 없습니다.'
                  : '매도할 수량이 보유 수량보다 많습니다.'}
            </p>
          )}
          {notice && (
            <p className={`notice ${notice.type}`} role="status">
              {notice.message}
            </p>
          )}
          {confirming && (
            <div className="confirm-box" role="status">
              {token.tokenName} {points(number)}개를 {side === 'buy' ? '매수' : '매도'}할까요?
              <button
                className="text-button"
                disabled={busy}
                type="button"
                onClick={() => setConfirming(false)}
              >
                취소
              </button>
            </div>
          )}
          <button
            className={`button order-submit ${side}`}
            disabled={!valid || insufficient || busy}
            type="submit"
          >
            {busy
              ? '거래 처리 중…'
              : confirming
                ? `${side === 'buy' ? '매수' : '매도'} 확정`
                : `${side === 'buy' ? '매수' : '매도'} 주문 확인`}
          </button>
        </form>
      ) : (
        <div className="login-prompt">
          <span>나만의 투자를 시작해보세요.</span>
          <p>
            로그인 후 포인트로
            <br />
            동물 토큰을 거래할 수 있어요.
          </p>
          <Link className="button" to="/login" state={{ from: location.pathname }}>
            로그인하고 거래하기 ↗
          </Link>
        </div>
      )}
    </aside>
  )
}

export default function TokenDetail() {
  const { id } = useParams()
  const { token: accessToken } = useAuth()
  const [period, setPeriod] = useState('1h')
  const validId = /^[1-9]\d*$/.test(id)
  const loader = useCallback(
    async (signal) => {
      if (!validId) throw new Error('올바른 토큰 주소가 아닙니다.')
      return api(`/token/search/${id}`, { signal })
    },
    [id, validId],
  )
  const resource = useResource(`detail:${id}`, loader, 30000)
  const historyLoader = useCallback(
    async (signal) => {
      if (!validId) throw new Error('올바른 토큰 주소가 아닙니다.')
      return api(`/token/${id}?history=${period}`, { signal })
    },
    [id, period, validId],
  )
  const history = useResource(`history:${id}:${period}`, historyLoader, 30000)
  const holdingLoader = useCallback(
    (signal) => (accessToken ? api('/mypage', { auth: true, signal }) : Promise.resolve([])),
    [accessToken],
  )
  const holdings = useResource(
    `holdings:${accessToken || 'guest'}`,
    holdingLoader,
    accessToken ? 30000 : 0,
  )
  // 로그인했을 때만 이 토큰의 내 매매 기록을 차트 기간에 맞춰 가져온다.
  const tradeLoader = useCallback(
    (signal) =>
      accessToken && validId
        ? api(`/token/history/${id}?history=${period}`, { auth: true, signal })
        : Promise.resolve([]),
    [accessToken, id, period, validId],
  )
  const trades = useResource(
    `trades:${id}:${period}:${accessToken || 'guest'}`,
    tradeLoader,
    accessToken ? 30000 : 0,
  )
  const refresh = () => {
    resource.refresh()
    history.refresh()
    holdings.refresh()
    trades.refresh()
  }
  const periodLabel = PERIODS.find(([value]) => value === period)?.[1] || period
  return (
    <>
      <Link className="breadcrumb" to="/token/all">
        ← 전체 토큰
      </Link>
      <ResourceState resource={resource} />
      {resource.data && (
        <>
          <div className="detail-heading">
            <TokenImage token={resource.data} large />
            <div>
              <span className="eyebrow">ANIMAL TOKEN · #{id}</span>
              <h1>{resource.data.tokenName}</h1>
            </div>
            <div className="detail-price">
              <small>현재 가격</small>
              <strong>
                {points(resource.data.tokenPrice)} <span>P</span>
              </strong>
            </div>
          </div>
          <RefreshStatus
            resource={{ ...resource, error: resource.error || history.error, refresh }}
          />
          <div className="detail-grid">
            <section className="panel chart-panel">
              <div className="chart-heading">
                <h2>가격 차트</h2>
                <div className="period-tabs" role="group" aria-label="차트 조회 기간">
                  {PERIODS.map(([value, label]) => (
                    <button
                      key={value}
                      aria-pressed={period === value}
                      className={period === value ? 'active' : ''}
                      onClick={() => setPeriod(value)}
                    >
                      {label}
                    </button>
                  ))}
                </div>
              </div>
              <ResourceState resource={history} />
              {history.data && (
                <PriceChart
                  key={`${id}:${period}`}
                  history={history.data}
                  trades={Array.isArray(trades.data) ? trades.data : []}
                />
              )}
              <p className="panel-footnote">
                최근 {periodLabel}의 실제 저장 기록 · 한국 시각 기준
                {accessToken &&
                  ' · 빨간 점은 내 매수, 파란 점은 내 매도, 숫자가 있는 점은 가까운 매매 묶음'}
                {accessToken && trades.error && ' · 매매 기록을 불러오지 못했습니다'}
              </p>
            </section>
            <OrderPanel
              key={`${id}:${accessToken || 'guest'}`}
              token={resource.data}
              holding={holdings.data?.find((item) => String(item.tokenId) === id)}
              holdingError={holdings.error || holdings.loading}
              refresh={refresh}
            />
          </div>
        </>
      )}
    </>
  )
}

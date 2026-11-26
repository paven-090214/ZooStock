import { useEffect, useId, useRef, useState } from 'react'
import { dateTime, historyTime, points } from '../lib/format'

// 매매 점 위에 띄우는 작은 설명. 차트 밖으로 나가지 않게 위치를 조정한다.
function TradeLabel({ mark, width, left, right, top }) {
  const buy = mark.tradeType === 'BUY'
  const lines = [
    `${buy ? '매수' : '매도'} ${points(mark.price)}P × ${points(mark.quantity)}개`,
    dateTime(mark.tradedAt),
  ]
  const boxWidth = 150,
    boxHeight = 40
  const x = Math.min(Math.max(mark.x - boxWidth / 2, left), width - right - boxWidth)
  const above = mark.y - boxHeight - 12
  const y = above < top - 16 ? mark.y + 12 : above
  return (
    <g className="trade-label" pointerEvents="none">
      <rect x={x} y={y} width={boxWidth} height={boxHeight} rx="6" />
      <text x={x + 10} y={y + 17} className={buy ? 'up' : 'down'}>
        {lines[0]}
      </text>
      <text x={x + 10} y={y + 32}>
        {lines[1]}
      </text>
    </g>
  )
}

export default function PriceChart({ history, trades = [] }) {
  const id = useId().replace(/:/g, '')
  const [selected, setSelected] = useState(null)
  // 마우스를 올리거나 키보드로 선택한 매매 점
  const [activeTrade, setActiveTrade] = useState(null)
  const container = useRef(null)
  const [chartWidth, setChartWidth] = useState(800)
  useEffect(() => {
    const observer = new ResizeObserver(([entry]) =>
      setChartWidth(Math.max(280, Math.round(entry.contentRect.width))),
    )
    if (container.current) observer.observe(container.current)
    return () => observer.disconnect()
  }, [])
  const data = history
    .filter(
      (point) =>
        Number.isFinite(Number(point.price)) &&
        Number.isFinite(Date.parse(historyTime(point.recordedAt))),
    )
    .toSorted(
      (a, b) => Date.parse(historyTime(a.recordedAt)) - Date.parse(historyTime(b.recordedAt)),
    )
  if (!data.length)
    return (
      <div ref={container} className="chart-empty">
        아직 이 기간에 기록된 가격이 없어요.
      </div>
    )
  const width = chartWidth,
    height = chartWidth < 500 ? 250 : 300,
    left = 68,
    right = 20,
    top = 24,
    bottom = 38
  // tradedAt은 시간대가 붙은 값이라 그대로 읽는다.
  const marks = trades
    .map((trade) => ({ ...trade, time: Date.parse(trade.tradedAt), price: Number(trade.tradePrice) }))
    .filter((trade) => Number.isFinite(trade.time) && Number.isFinite(trade.price))
  // 매매 점이 차트 밖으로 나가지 않도록 가격·시간 범위에 함께 넣는다.
  const prices = [...data.map((point) => Number(point.price)), ...marks.map((mark) => mark.price)]
  const min = Math.min(...prices),
    max = Math.max(...prices)
  const padding = Math.max((max - min) * 0.15, max * 0.005, 1)
  const low = min - padding,
    high = max + padding
  const times = [
    Date.parse(historyTime(data[0].recordedAt)),
    Date.parse(historyTime(data.at(-1).recordedAt)),
    ...marks.map((mark) => mark.time),
  ]
  const firstTime = Math.min(...times),
    lastTime = Math.max(...times)
  const xOf = (time) =>
    firstTime === lastTime
      ? (width + left - right) / 2
      : left + ((time - firstTime) / (lastTime - firstTime)) * (width - left - right)
  const yOf = (price) => top + ((high - price) / (high - low)) * (height - top - bottom)
  const coordinates = data.map((point) => ({
    x: xOf(Date.parse(historyTime(point.recordedAt))),
    y: yOf(Number(point.price)),
  }))
  const markPoints = marks.map((mark) => ({ ...mark, x: xOf(mark.time), y: yOf(mark.price) }))
  const active = markPoints.find((mark) => mark.tradeId === activeTrade)
  const line = coordinates.map(({ x, y }, index) => `${index ? 'L' : 'M'}${x},${y}`).join(' ')
  const area = `${line} L${coordinates.at(-1).x},${height - bottom} L${coordinates[0].x},${height - bottom} Z`
  const index = selected === null ? data.length - 1 : Math.min(selected, data.length - 1)
  function select(event) {
    const rect = event.currentTarget.getBoundingClientRect()
    const x = ((event.clientX - rect.left) / rect.width) * width
    let nearest = 0
    coordinates.forEach((point, i) => {
      if (Math.abs(point.x - x) < Math.abs(coordinates[nearest].x - x)) nearest = i
    })
    setSelected(nearest)
  }
  return (
    <div ref={container} className="price-chart">
      <div className="chart-readout" aria-live="polite">
        <strong>
          {points(data[index].price)} <small>P</small>
        </strong>
        <span>{dateTime(historyTime(data[index].recordedAt))}</span>
      </div>
      <svg
        viewBox={`0 0 ${width} ${height}`}
        role="img"
        aria-label={`가격 이력. 최저 ${points(min)} 포인트, 최고 ${points(max)} 포인트`}
        onPointerMove={select}
        onPointerLeave={() => setSelected(null)}
      >
        <defs>
          <linearGradient id={id} x1="0" y1="0" x2="0" y2="1">
            <stop stopColor="#39836d" stopOpacity=".17" />
            <stop offset="1" stopColor="#39836d" stopOpacity="0" />
          </linearGradient>
        </defs>
        {[0, 1, 2, 3, 4].map((step) => {
          const y = top + (step / 4) * (height - top - bottom)
          return (
            <g key={step}>
              <line x1={left} x2={width - right} y1={y} y2={y} className="grid-line" />
              <text x={left - 12} y={y + 4} textAnchor="end">
                {points(Math.round(high - (step / 4) * (high - low)))}
              </text>
            </g>
          )
        })}
        <path d={area} fill={`url(#${id})`} />
        <path d={line} fill="none" stroke="#26745e" strokeWidth="2.5" strokeLinejoin="round" />
        <line
          x1={coordinates[index].x}
          x2={coordinates[index].x}
          y1={top}
          y2={height - bottom}
          stroke="#a1b8af"
          strokeDasharray="4 4"
        />
        <circle
          cx={coordinates[index].x}
          cy={coordinates[index].y}
          r="5"
          fill="#26745e"
          stroke="white"
          strokeWidth="2"
        />
        {/* 내 매매: 매수 빨강, 매도 파랑. 세로 점선과 점은 항상 보인다. */}
        {markPoints.map((mark) => {
          const buy = mark.tradeType === 'BUY'
          const color = buy ? 'var(--red)' : 'var(--blue)'
          const summary = `${buy ? '매수' : '매도'} ${points(mark.price)}P × ${points(mark.quantity)}개 · ${dateTime(mark.tradedAt)}`
          return (
            <g
              key={mark.tradeId}
              className="trade-mark"
              tabIndex={0}
              role="img"
              aria-label={summary}
              onPointerEnter={() => setActiveTrade(mark.tradeId)}
              onPointerLeave={() => setActiveTrade(null)}
              onFocus={() => setActiveTrade(mark.tradeId)}
              onBlur={() => setActiveTrade(null)}
            >
              <line
                x1={mark.x}
                x2={mark.x}
                y1={top}
                y2={height - bottom}
                stroke={color}
                strokeOpacity="0.55"
                strokeDasharray="3 4"
              />
              {/* 마우스를 올리기 쉽도록 보이지 않는 큰 원을 겹친다. */}
              <circle cx={mark.x} cy={mark.y} r="11" fill="transparent" />
              <circle cx={mark.x} cy={mark.y} r="5" fill={color} stroke="white" strokeWidth="2" />
            </g>
          )
        })}
        {active && (
          <TradeLabel mark={active} width={width} left={left} right={right} top={top} />
        )}
        {/* 가로축 양 끝 시각: 매매 점까지 포함한 범위 */}
        <text x={left} y={height - 8}>
          {dateTime(firstTime)}
        </text>
        {firstTime !== lastTime && (
          <text x={width - right} y={height - 8} textAnchor="end">
            {dateTime(lastTime)}
          </text>
        )}
      </svg>
      <label className="chart-slider">
        기록 탐색
        <input
          aria-label="가격 기록 탐색"
          type="range"
          min="0"
          max={data.length - 1}
          value={index}
          onChange={(event) => setSelected(Number(event.target.value))}
        />
      </label>
    </div>
  )
}

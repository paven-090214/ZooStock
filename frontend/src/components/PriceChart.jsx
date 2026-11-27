import { useEffect, useId, useRef, useState } from 'react'
import { dateTime, historyTime, points } from '../lib/format'

// 가로로 이 거리(px) 안에 있는 매매는 점 하나로 묶는다.
const GROUP_GAP = 14
// 묶인 점의 설명 창에 보여줄 최대 줄 수. 넘치면 "외 n건"으로 줄인다.
const LABEL_ROWS = 4

const tradeText = (mark) =>
  `${mark.tradeType === 'BUY' ? '매수' : '매도'} ${points(mark.price)}P × ${points(mark.quantity)}개`

// 매매 점(묶음) 위에 띄우는 작은 설명. 차트 밖으로 나가지 않게 위치를 조정한다.
function TradeLabel({ group, width, left, right, top }) {
  const single = group.trades.length === 1
  // 한 건이면 [내용, 시각], 여러 건이면 [제목, 건마다 한 줄, 외 n건]
  const rows = single
    ? [
        { text: tradeText(group.trades[0]), tone: group.tone },
        { text: dateTime(group.trades[0].tradedAt) },
      ]
    : [
        { text: `내 매매 ${group.trades.length}건` },
        ...group.trades.slice(0, LABEL_ROWS).map((mark) => ({
          text: `${tradeText(mark)} · ${dateTime(mark.tradedAt)}`,
          tone: mark.tradeType === 'BUY' ? 'up' : 'down',
        })),
        ...(group.trades.length > LABEL_ROWS
          ? [{ text: `외 ${group.trades.length - LABEL_ROWS}건` }]
          : []),
      ]
  const boxWidth = single ? 150 : 250,
    boxHeight = 10 + rows.length * 15
  const x = Math.min(Math.max(group.x - boxWidth / 2, left), width - right - boxWidth)
  const above = group.y - boxHeight - 12
  const y = above < top - 16 ? group.y + 12 : above
  return (
    <g className="trade-label" pointerEvents="none">
      <rect x={x} y={y} width={boxWidth} height={boxHeight} rx="6" />
      {rows.map((row, index) => (
        <text key={index} x={x + 10} y={y + 17 + index * 15} className={row.tone}>
          {row.text}
        </text>
      ))}
    </g>
  )
}

// 시간순 매매 점을 가까운 것끼리 묶는다. 점이 겹쳐 차트를 가리지 않게 하기 위함이다.
function groupMarks(markPoints) {
  const groups = []
  for (const mark of markPoints.toSorted((a, b) => a.x - b.x)) {
    const last = groups.at(-1)
    if (last && mark.x - last.startX <= GROUP_GAP) last.trades.push(mark)
    else groups.push({ startX: mark.x, trades: [mark] })
  }
  return groups.map(({ trades }) => {
    const buys = trades.filter((mark) => mark.tradeType === 'BUY').length
    // 모두 매수면 빨강, 모두 매도면 파랑, 섞이면 초록
    const tone = buys === trades.length ? 'up' : buys === 0 ? 'down' : 'mixed'
    return {
      key: trades.map((mark) => mark.tradeId).join('-'),
      trades,
      tone,
      x: trades.reduce((sum, mark) => sum + mark.x, 0) / trades.length,
      y: trades.reduce((sum, mark) => sum + mark.y, 0) / trades.length,
    }
  })
}

const TONE_COLORS = { up: 'var(--red)', down: 'var(--blue)', mixed: 'var(--green)' }

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
  const groups = groupMarks(markPoints)
  const active = groups.find((group) => group.key === activeTrade)
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
        {/* 선택한 매매 점에만 세로 점선을 그린다. 항상 그리면 매매가 많을 때 차트를 가린다. */}
        {active && (
          <line
            x1={active.x}
            x2={active.x}
            y1={top}
            y2={height - bottom}
            stroke={TONE_COLORS[active.tone]}
            strokeOpacity="0.55"
            strokeDasharray="3 4"
            pointerEvents="none"
          />
        )}
        {/* 내 매매: 매수 빨강, 매도 파랑, 섞인 묶음 초록. 여러 건이면 점 안에 건수를 쓴다. */}
        {groups.map((group) => {
          const color = TONE_COLORS[group.tone]
          const count = group.trades.length
          const summary =
            count === 1
              ? `${tradeText(group.trades[0])} · ${dateTime(group.trades[0].tradedAt)}`
              : `내 매매 ${count}건: ${group.trades.map(tradeText).join(', ')}`
          return (
            <g
              key={group.key}
              className="trade-mark"
              tabIndex={0}
              role="img"
              aria-label={summary}
              onPointerEnter={() => setActiveTrade(group.key)}
              onPointerLeave={() => setActiveTrade(null)}
              onFocus={() => setActiveTrade(group.key)}
              onBlur={() => setActiveTrade(null)}
            >
              {/* 마우스를 올리기 쉽도록 보이지 않는 큰 원을 겹친다. */}
              <circle cx={group.x} cy={group.y} r="11" fill="transparent" />
              <circle
                cx={group.x}
                cy={group.y}
                r={count > 9 ? 10 : count > 1 ? 8 : 5}
                fill={color}
                stroke="white"
                strokeWidth="2"
              />
              {count > 1 && (
                <text x={group.x} y={group.y + 3.5} textAnchor="middle" className="trade-count">
                  {count > 99 ? '99+' : count}
                </text>
              )}
            </g>
          )
        })}
        {active && (
          <TradeLabel group={active} width={width} left={left} right={right} top={top} />
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

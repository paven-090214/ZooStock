import { useEffect, useId, useRef, useState } from 'react'
import { dateTime, historyTime, points } from '../lib/format'

export default function PriceChart({ history }) {
  const id = useId().replace(/:/g, '')
  const [selected, setSelected] = useState(null)
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
  const prices = data.map((point) => Number(point.price))
  const min = Math.min(...prices),
    max = Math.max(...prices)
  const padding = Math.max((max - min) * 0.15, max * 0.005, 1)
  const low = min - padding,
    high = max + padding
  const firstTime = Date.parse(historyTime(data[0].recordedAt)),
    lastTime = Date.parse(historyTime(data.at(-1).recordedAt))
  const coordinates = data.map((point) => ({
    x:
      firstTime === lastTime
        ? (width + left - right) / 2
        : left +
          ((Date.parse(historyTime(point.recordedAt)) - firstTime) / (lastTime - firstTime)) *
            (width - left - right),
    y: top + ((high - Number(point.price)) / (high - low)) * (height - top - bottom),
  }))
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
        <text x={left} y={height - 8}>
          {dateTime(historyTime(data[0].recordedAt))}
        </text>
        {data.length > 1 && (
          <text x={width - right} y={height - 8} textAnchor="end">
            {dateTime(historyTime(data.at(-1).recordedAt))}
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

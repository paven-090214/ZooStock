export const points = (value) =>
  Number(value ?? 0).toLocaleString('ko-KR', { maximumFractionDigits: 2 })
export const rate = (value) => `${Number(value) > 0 ? '+' : ''}${Number(value ?? 0).toFixed(2)}%`
export const direction = (value) =>
  Number(value) > 0 ? 'up' : Number(value) < 0 ? 'down' : 'neutral'
export const dateTime = (value) =>
  value
    ? new Intl.DateTimeFormat('ko-KR', {
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        timeZone: 'Asia/Seoul',
      }).format(new Date(value))
    : '—'

// recordedAt은 시간대가 없는 LocalDateTime이다. 배포 서버도 한국 시각으로 맞춘다.
export function historyTime(value) {
  return /(?:Z|[+-]\d{2}:\d{2})$/.test(value) ? value : `${value}+09:00`
}

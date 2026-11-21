import { useState } from 'react'
import { Link } from 'react-router'
import { direction, points, rate } from '../lib/format'

export function TokenImage({ token, large = false }) {
  const [failed, setFailed] = useState(false)
  return (
    <span className={`token-photo ${large ? 'large' : ''}`}>
      {token.imgPath && !failed ? (
        <img
          src={token.imgPath}
          alt={token.tokenName}
          loading="lazy"
          onError={() => setFailed(true)}
        />
      ) : (
        <span aria-label="동물 이미지 없음">{token.tokenName?.slice(0, 1) || 'Z'}</span>
      )}
    </span>
  )
}

export function Change({ value }) {
  return (
    <span className={`change ${direction(value)}`}>
      {Number(value) > 0 ? '↗ ' : Number(value) < 0 ? '↘ ' : ''}
      {rate(value)}
    </span>
  )
}

export function ResourceState({
  resource,
  empty = false,
  emptyMessage = '아직 표시할 데이터가 없습니다.',
}) {
  if (resource.loading)
    return (
      <div className="state-box" role="status">
        <span className="spinner" /> 정보를 불러오는 중이에요.
      </div>
    )
  if (resource.error && !resource.data)
    return (
      <div className="state-box error" role="alert">
        <p>{resource.error.message}</p>
        <button className="button secondary" onClick={resource.refresh}>
          다시 시도
        </button>
      </div>
    )
  if (empty)
    return (
      <div className="state-box">
        <span className="empty-symbol">◌</span>
        {emptyMessage}
      </div>
    )
  return null
}

export function RefreshStatus({ resource }) {
  return (
    <div className="refresh-status">
      <span className={`status-dot ${resource.error ? 'offline' : ''}`} />
      <span>
        {resource.error ? '갱신 실패 · 마지막 데이터 표시 중' : '30초마다 업데이트'}
        {resource.updatedAt && ` · ${new Date(resource.updatedAt).toLocaleTimeString('ko-KR')}`}
      </span>
      <button
        className="text-button"
        onClick={resource.refresh}
        disabled={resource.refreshing}
        aria-label="지금 새로고침"
      >
        ↻ 새로고침
      </button>
    </div>
  )
}

export function TokenTable({ tokens }) {
  return (
    <div className="table-scroll">
      <table className="token-table">
        <thead>
          <tr>
            <th scope="col">동물 토큰</th>
            <th scope="col">현재 가격</th>
            <th scope="col">
              <span className="sr-only">상세 보기</span>
            </th>
          </tr>
        </thead>
        <tbody>
          {tokens.map((token) => (
            <tr key={token.tokenId}>
              <td>
                <Link className="token-identity" to={`/token/${token.tokenId}`}>
                  <TokenImage token={token} />
                  <span>
                    <strong>{token.tokenName}</strong>
                    <small>ZOO #{token.tokenId}</small>
                  </span>
                </Link>
              </td>
              <td className="numeric">
                <strong>{points(token.tokenPrice)}</strong> <small>P</small>
              </td>
              <td>
                <Link
                  className="row-link"
                  to={`/token/${token.tokenId}`}
                  aria-label={`${token.tokenName} 상세 보기`}
                >
                  ↗
                </Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

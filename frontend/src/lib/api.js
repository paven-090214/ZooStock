export class ApiError extends Error {
  constructor(status, message) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

export const TOKEN_KEY = 'zoostock.accessToken'

export function tokenExpiresAt(token) {
  try {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    const expiresAt = Number(JSON.parse(atob(payload)).exp) * 1000
    return Number.isFinite(expiresAt) && expiresAt > 0 ? expiresAt : 0
  } catch {
    return 0
  }
}

// Spring Page 응답을 화면용 값으로 바꾼다. number는 0부터, 화면의 page는 1부터 센다.
// 배열 응답도 받아서, 페이징 전 API와 함께 쓸 수 있다.
export function readPage(data) {
  if (Array.isArray(data))
    return { items: data, page: 1, size: data.length, totalPages: 1, totalElements: data.length }
  const items = Array.isArray(data?.content) ? data.content : []
  return {
    items,
    page: (Number(data?.number) || 0) + 1,
    size: Number(data?.size) || items.length,
    totalPages: Number(data?.totalPages) || 0,
    totalElements: Number(data?.totalElements) || 0,
  }
}

// 이름·사진·현재 가격을 연결하려고 전체 토큰을 모은다. 페이지를 차례로 요청한다.
export async function fetchAllTokens(signal, maxPages = 20) {
  const tokens = []
  for (let page = 1; page <= maxPages; page++) {
    const result = readPage(await api(`/token/all?sort=nameAsc&page=${page}`, { signal }))
    tokens.push(...result.items)
    if (page >= result.totalPages || result.items.length === 0) break
  }
  return tokens
}

export async function api(path, { method = 'GET', body, auth = false, signal } = {}) {
  const headers = { Accept: 'application/json, text/plain' }
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  if (auth) {
    const token = sessionStorage.getItem(TOKEN_KEY)
    if (!token || tokenExpiresAt(token) <= Date.now()) {
      window.dispatchEvent(new Event('zoostock:expired'))
      throw new ApiError(401, '로그인이 필요하거나 만료되었습니다. 다시 로그인해주세요.')
    }
    headers.Authorization = `Bearer ${token}`
  }
  let response
  try {
    response = await fetch(`/api${path}`, {
      method,
      headers,
      signal,
      body: body === undefined ? undefined : JSON.stringify(body),
    })
  } catch (error) {
    if (error.name === 'AbortError') throw error
    throw new ApiError(0, '서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.')
  }
  const text = await response.text()
  let data = text
  try {
    data = JSON.parse(text)
  } catch {
    /* 로그인 JWT·거래 응답은 일반 문자열이다. */
  }
  if (!response.ok) {
    if (auth && response.status === 401) window.dispatchEvent(new Event('zoostock:expired'))
    const fallback =
      response.status === 409
        ? '다른 거래가 먼저 처리되었습니다. 잔액을 확인하고 다시 시도해주세요.'
        : response.status === 401
          ? '아이디와 비밀번호 또는 로그인 상태를 확인해주세요.'
          : response.status >= 500
            ? '요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.'
            : '요청한 정보와 입력값을 확인해주세요.'
    // HTML 오류 페이지나 내부 스택을 사용자에게 그대로 노출하지 않는다.
    const message =
      typeof data === 'object' && data !== null
        ? data.message || data.detail
        : !text.trim().startsWith('<') && text.length < 300
          ? text
          : null
    throw new ApiError(response.status, message || fallback)
  }
  return data
}

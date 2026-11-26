import { useCallback } from 'react'
import { useSearchParams } from 'react-router'
import { api, readPage } from '../lib/api'
import { useResource } from '../hooks/useResource'
import { Pagination, RefreshStatus, ResourceState, TokenTable } from '../components/ui'

export default function TokenList({ search = false }) {
  const [params, setParams] = useSearchParams()
  const name = params.get('name')?.trim() || ''
  const sort = params.get('sort') || 'nameAsc'
  // 전체 목록은 1페이지부터 센다. 잘못된 값은 1페이지로 본다.
  const page = Math.max(1, Number.parseInt(params.get('page'), 10) || 1)
  const path = search
    ? `/token/search?name=${encodeURIComponent(name)}`
    : `/token/all?sort=${encodeURIComponent(sort)}&page=${page}`
  const loader = useCallback(
    (signal) => (search && !name ? Promise.resolve([]) : api(path, { signal })),
    [path, search, name],
  )
  const resource = useResource(path, loader, 30000)
  // 검색은 배열, 전체 목록은 Page 응답이다.
  const list = resource.data ? readPage(resource.data) : null
  const changePage = (value) => {
    setParams({ sort, page: String(value) })
    window.scrollTo(0, 0)
  }
  return (
    <>
      <div className="page-heading">
        <div>
          <span className="eyebrow">{search ? 'FIND YOUR FAVORITES' : 'EXPLORE THE MARKET'}</span>
          <h1>
            {search ? '토큰 검색' : '전체 토큰'}
            <span className="heading-dot">.</span>
          </h1>
          <p>
            {search
              ? name
                ? `“${name}” 검색 결과입니다.`
                : '헤더에서 동물 이름을 검색해주세요.'
              : '다양한 동물 토큰을 한눈에 살펴보세요.'}
          </p>
        </div>
      </div>
      <div className="list-toolbar">
        <span>
          <strong>{list ? list.totalElements : '—'}</strong>개의 토큰
          {!search && list?.totalPages > 1 && (
            <small className="toolbar-page">
              {' '}
              · {list.page} / {list.totalPages} 페이지
            </small>
          )}
        </span>
        {!search && (
          <label className="sort-control">
            정렬
            {/* 정렬을 바꾸면 1페이지부터 다시 본다. */}
            <select value={sort} onChange={(event) => setParams({ sort: event.target.value })}>
              <option value="nameAsc">이름 오름차순</option>
              <option value="nameDesc">이름 내림차순</option>
              <option value="priceAsc">가격 낮은 순</option>
              <option value="priceDesc">가격 높은 순</option>
            </select>
          </label>
        )}
      </div>
      <RefreshStatus resource={resource} />
      <div className="panel">
        <ResourceState
          resource={resource}
          empty={list?.items.length === 0}
          emptyMessage={
            !search && page > 1
              ? '이 페이지에는 토큰이 없습니다.'
              : '검색 결과 또는 등록된 토큰이 없습니다.'
          }
        />
        {!!list?.items.length && <TokenTable tokens={list.items} />}
      </div>
      {!search && list && (
        <Pagination
          page={page}
          totalPages={list.totalPages}
          onChange={changePage}
          label="전체 토큰 페이지"
        />
      )}
    </>
  )
}

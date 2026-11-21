import { useCallback } from 'react'
import { useSearchParams } from 'react-router'
import { api } from '../lib/api'
import { useResource } from '../hooks/useResource'
import { RefreshStatus, ResourceState, TokenTable } from '../components/ui'

export default function TokenList({ search = false }) {
  const [params, setParams] = useSearchParams()
  const name = params.get('name')?.trim() || ''
  const sort = params.get('sort') || 'nameAsc'
  const path = search
    ? `/token/search?name=${encodeURIComponent(name)}`
    : `/token/all?sort=${encodeURIComponent(sort)}`
  const loader = useCallback(
    (signal) => (search && !name ? Promise.resolve([]) : api(path, { signal })),
    [path, search, name],
  )
  const resource = useResource(path, loader, 30000)
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
          <strong>{resource.data?.length ?? '—'}</strong>개의 토큰
        </span>
        {!search && (
          <label className="sort-control">
            정렬
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
          empty={resource.data?.length === 0}
          emptyMessage="검색 결과 또는 등록된 토큰이 없습니다."
        />
        {!!resource.data?.length && <TokenTable tokens={resource.data} />}
      </div>
    </>
  )
}

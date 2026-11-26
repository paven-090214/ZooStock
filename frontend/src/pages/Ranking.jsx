import { useCallback } from 'react'
import { useSearchParams } from 'react-router'
import { useAuth } from '../auth/auth'
import { api, readPage } from '../lib/api'
import { points } from '../lib/format'
import { useResource } from '../hooks/useResource'
import { Pagination, RefreshStatus, ResourceState } from '../components/ui'

// 회원 총자산 순위. 비로그인도 볼 수 있다.
export default function Ranking() {
  const { member } = useAuth()
  const [params, setParams] = useSearchParams()
  const page = Math.max(1, Number.parseInt(params.get('page'), 10) || 1)
  const path = `/ranking?page=${page}`
  const loader = useCallback((signal) => api(path, { signal }), [path])
  const resource = useResource(path, loader, 30000)
  const list = resource.data ? readPage(resource.data) : null
  // 순위 = 앞 페이지의 인원 + 이 페이지에서의 위치
  const offset = list ? (list.page - 1) * list.size : 0
  const changePage = (value) => {
    setParams({ page: String(value) })
    window.scrollTo(0, 0)
  }
  return (
    <>
      <div className="page-heading">
        <div>
          <span className="eyebrow">ZOOSTOCK RANKING</span>
          <h1>
            자산 랭킹<span className="heading-dot">.</span>
          </h1>
          <p>보유 포인트와 보유 토큰의 현재 가치를 합한 총자산 순위입니다.</p>
        </div>
      </div>
      <div className="list-toolbar">
        <span>
          <strong>{list ? list.totalElements : '—'}</strong>명의 투자자
          {list?.totalPages > 1 && (
            <small className="toolbar-page">
              {' '}
              · {list.page} / {list.totalPages} 페이지
            </small>
          )}
        </span>
      </div>
      <RefreshStatus resource={resource} />
      <div className="panel">
        <ResourceState
          resource={resource}
          empty={list?.items.length === 0}
          emptyMessage={page > 1 ? '이 페이지에는 순위가 없습니다.' : '아직 순위가 없습니다.'}
        />
        {!!list?.items.length && (
          <div className="table-scroll">
            <table className="ranking-table">
              <thead>
                <tr>
                  <th scope="col">순위</th>
                  <th scope="col">닉네임</th>
                  <th scope="col">총자산</th>
                </tr>
              </thead>
              <tbody>
                {list.items.map((item, index) => {
                  const rank = offset + index + 1
                  // 닉네임은 중복될 수 없어서 이름으로 내 순위를 찾는다.
                  const mine = member?.memberName && member.memberName === item.memberName
                  return (
                    <tr key={`${rank}:${item.memberName}`} className={mine ? 'mine' : ''}>
                      <td>
                        <span className={`rank-badge ${rank <= 3 ? `top top-${rank}` : ''}`}>
                          {rank}
                        </span>
                      </td>
                      <td>
                        <strong className="rank-member">{item.memberName}</strong>
                        {mine && <span className="pill mine-pill">나</span>}
                      </td>
                      <td className="numeric">
                        {/* 백엔드 이름이 totalAssets로 바뀌어도 동작하도록 둘 다 읽는다. */}
                        <strong>{points(item.totalAssets ?? item.memberPoint)}</strong>{' '}
                        <small>P</small>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
        <p className="panel-footnote">총자산 = 보유 포인트 + 보유 수량 × 현재 가격</p>
      </div>
      {list && (
        <Pagination
          page={page}
          totalPages={list.totalPages}
          onChange={changePage}
          label="랭킹 페이지"
        />
      )}
    </>
  )
}

import { useCallback, useState } from 'react'
import { Link } from 'react-router'
import { api } from '../lib/api'
import { dateTime } from '../lib/format'
import { useResource } from '../hooks/useResource'
import { ResourceState } from '../components/ui'

export default function BoardList() {
  const loader = useCallback((signal) => api('/board', { auth: true, signal }), [])
  const resource = useResource('board', loader)
  const [error, setError] = useState('')

  async function submit(event) {
    event.preventDefault()
    const form = event.currentTarget
    const data = new FormData(form)
    setError('')
    try {
      await api('/board/create', {
        method: 'POST',
        auth: true,
        body: { title: data.get('title'), content: data.get('content') },
      })
      form.reset()
      resource.refresh()
    } catch (e) {
      setError(e.message)
    }
  }

  return (
    <>
      <div className="page-heading">
        <div>
          <span className="eyebrow">TALK ABOUT ANIMALS</span>
          <h1>
            게시판<span className="heading-dot">.</span>
          </h1>
          <p>투자 이야기와 동물 이야기를 나눠보세요.</p>
        </div>
      </div>

      <form className="panel" onSubmit={submit}>
        <label className="field">
          제목
          <input name="title" required maxLength={255} />
        </label>
        <label className="field">
          내용
          <textarea name="content" required rows={4} style={{ width: '100%' }} />
        </label>
        {error && (
          <p className="notice error" role="alert">
            {error}
          </p>
        )}
        <button type="submit" className="button">
          글쓰기
        </button>
      </form>

      <div className="panel">
        <ResourceState resource={resource} empty={resource.data?.length === 0} emptyMessage="아직 글이 없습니다." />
        {!!resource.data?.length && (
          <div className="table-scroll">
            <table className="token-table">
              <thead>
                <tr>
                  <th scope="col">번호</th>
                  <th scope="col">제목</th>
                  <th scope="col">작성자</th>
                  <th scope="col">조회</th>
                  <th scope="col">작성일</th>
                </tr>
              </thead>
              <tbody>
                {resource.data.map((post) => (
                  <tr key={post.boardId}>
                    <td>{post.boardId}</td>
                    <td>
                      <Link to={`/board/${post.boardId}`}>
                        <strong>{post.title}</strong>
                      </Link>
                    </td>
                    <td>#{post.memberUid}</td>
                    <td>{post.viewCount}</td>
                    <td>{dateTime(post.boardCreatedAt)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </>
  )
}

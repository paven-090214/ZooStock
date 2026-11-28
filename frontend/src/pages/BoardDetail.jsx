import { useCallback, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import { useAuth } from '../auth/auth'
import { api } from '../lib/api'
import { dateTime } from '../lib/format'
import { useResource } from '../hooks/useResource'
import { ResourceState } from '../components/ui'

// JWT의 sub 클레임이 memberUid다.
function memberUidOf(token) {
  try {
    return Number(JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/'))).sub)
  } catch {
    return null
  }
}

export default function BoardDetail() {
  const { id } = useParams()
  const { token } = useAuth()
  const navigate = useNavigate()
  const loader = useCallback((signal) => api(`/board/${id}`, { auth: true, signal }), [id])
  const resource = useResource(`board:${id}`, loader)
  const [editing, setEditing] = useState(false)
  const [error, setError] = useState('')
  const post = resource.data
  const mine = post && post.memberUid === memberUidOf(token)

  async function run(action) {
    setError('')
    try {
      await action()
    } catch (e) {
      setError(e.message)
    }
  }

  const save = (event) => {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    return run(async () => {
      await api(`/board/${id}/Update`, {
        method: 'POST',
        auth: true,
        body: { title: data.get('title'), content: data.get('content') },
      })
      setEditing(false)
      resource.refresh()
    })
  }

  const remove = () =>
    window.confirm('이 글을 삭제할까요?') &&
    run(async () => {
      await api(`/board/${id}/delete`, { method: 'POST', auth: true })
      navigate('/board')
    })

  const like = () =>
    run(async () => {
      await api(`/board/${id}/like`, {
        method: 'POST',
        auth: true,
        body: { memberUid: memberUidOf(token) },
      })
      resource.refresh()
    })

  return (
    <>
      <Link className="text-button" to="/board">
        ← 목록으로
      </Link>
      <div className="panel">
        <ResourceState resource={resource} />
        {post &&
          (editing ? (
            <form onSubmit={save}>
              <label className="field">
                제목
                <input name="title" defaultValue={post.title} required maxLength={255} />
              </label>
              <label className="field">
                내용
                <textarea
                  name="content"
                  defaultValue={post.content}
                  required
                  rows={6}
                  style={{ width: '100%' }}
                />
              </label>
              <button type="submit" className="button">
                저장
              </button>{' '}
              <button type="button" className="button secondary" onClick={() => setEditing(false)}>
                취소
              </button>
            </form>
          ) : (
            <>
              <h1>{post.title}</h1>
              <p>
                <small>
                  작성자 #{post.memberUid} · 조회 {post.viewCount} · {dateTime(post.boardCreatedAt)}
                </small>
              </p>
              <p style={{ whiteSpace: 'pre-wrap' }}>{post.content}</p>
              <button className="button secondary" onClick={like}>
                ♥ 좋아요 {post.likeCount}
              </button>{' '}
              {mine && (
                <>
                  <button className="button secondary" onClick={() => setEditing(true)}>
                    수정
                  </button>{' '}
                  <button className="button secondary" onClick={remove}>
                    삭제
                  </button>
                </>
              )}
            </>
          ))}
        {error && (
          <p className="notice error" role="alert">
            {error}
          </p>
        )}
      </div>
    </>
  )
}

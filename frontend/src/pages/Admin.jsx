import { useCallback, useState } from 'react'
import { useSearchParams } from 'react-router'
import { useAuth } from '../auth/auth'
import { api, readPage } from '../lib/api'
import { dateTime, points } from '../lib/format'
import { useResource } from '../hooks/useResource'
import { Pagination, RefreshStatus, ResourceState } from '../components/ui'

const ROLES = [
  ['USER', '일반 회원'],
  ['ADMIN', '관리자'],
]

// 수정 양식. 번호·아이디·가입일은 보기만 하고, 닉네임·포인트·권한만 바꾼다.
// 백엔드도 이 세 값만 저장한다.
function MemberEditor({ member, isSelf, onCancel, onSaved }) {
  const { token } = useAuth()
  const [form, setForm] = useState({
    memberName: member.memberName ?? '',
    memberPoint: String(member.memberPoint ?? 0),
    role: member.role ?? 'USER',
  })
  const [busy, setBusy] = useState(false)
  const [notice, setNotice] = useState(null)
  const point = Number(form.memberPoint)
  const nameValid = form.memberName.trim().length > 0
  const pointValid = /^\d+$/.test(form.memberPoint) && Number.isSafeInteger(point)
  const change = (key) => (event) => {
    setForm((previous) => ({ ...previous, [key]: event.target.value }))
    setNotice(null)
  }
  async function submit(event) {
    event.preventDefault()
    if (!nameValid || !pointValid || busy || !token) return
    setBusy(true)
    setNotice(null)
    try {
      await api(`/admin/members/${member.memberUid}/set`, {
        method: 'PUT',
        auth: true,
        // 바꿀 수 없는 값도 함께 보내지만, 서버는 무시한다.
        body: { ...member, memberName: form.memberName.trim(), memberPoint: point, role: form.role },
      })
      onSaved()
    } catch (error) {
      setNotice(error.message)
      setBusy(false)
    }
  }
  return (
    <form className="admin-editor" onSubmit={submit}>
      <div className="admin-editor-grid">
        <label className="field">
          회원 번호
          <input value={member.memberUid} readOnly />
        </label>
        <label className="field">
          아이디
          <input value={member.memberId ?? ''} readOnly />
        </label>
        <label className="field">
          가입일
          <input value={dateTime(member.memberCreatedAt)} readOnly />
        </label>
        <label className="field">
          닉네임
          <input value={form.memberName} onChange={change('memberName')} maxLength={50} required />
        </label>
        <label className="field">
          포인트
          <input
            type="number"
            min="0"
            step="1"
            inputMode="numeric"
            value={form.memberPoint}
            onChange={change('memberPoint')}
            required
          />
        </label>
        <label className="field">
          권한
          {/* 자기 권한을 내리면 관리자가 사라질 수 있어 화면에서 막는다. */}
          <select value={form.role} onChange={change('role')} disabled={isSelf}>
            {ROLES.map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </label>
      </div>
      {isSelf && <p className="form-hint">내 계정의 권한은 이 화면에서 바꿀 수 없습니다.</p>}
      {!nameValid && <p className="form-hint error">닉네임을 입력해주세요.</p>}
      {!pointValid && <p className="form-hint error">포인트는 0 이상의 정수로 입력해주세요.</p>}
      {notice && (
        <p className="notice error" role="alert">
          {notice}
        </p>
      )}
      <div className="admin-editor-actions">
        <button type="button" className="button secondary small" onClick={onCancel} disabled={busy}>
          취소
        </button>
        <button type="submit" className="button small" disabled={!nameValid || !pointValid || busy}>
          {busy ? '저장 중…' : '저장'}
        </button>
      </div>
    </form>
  )
}

export default function Admin() {
  const { token, member: me, refreshMember } = useAuth()
  const [params, setParams] = useSearchParams()
  // 회원 목록은 서버가 회원 번호 순으로 10명씩 나눠 보낸다. 1페이지부터 센다.
  const page = Math.max(1, Number.parseInt(params.get('page'), 10) || 1)
  const [editing, setEditing] = useState(null)
  const [saved, setSaved] = useState(null)
  const loader = useCallback(
    async (signal) => readPage(await api(`/admin/members?page=${page}`, { auth: true, signal })),
    [page],
  )
  // 수정 중에 목록이 바뀌지 않도록 자동 갱신은 하지 않는다.
  const resource = useResource(`admin-members:${page}:${token}`, loader, 0)
  const list = resource.data
  const members = list ? list.items : null
  const changePage = (value) => {
    setEditing(null)
    setSaved(null)
    setParams({ page: String(value) })
    window.scrollTo(0, 0)
  }
  return (
    <>
      <div className="page-heading">
        <div>
          <span className="eyebrow">ADMIN</span>
          <h1>
            회원 관리<span className="heading-dot">.</span>
          </h1>
          <p>회원의 닉네임, 포인트, 권한을 수정할 수 있습니다.</p>
        </div>
      </div>
      <div className="list-toolbar">
        <span>
          <strong>{list ? list.totalElements : '—'}</strong>명의 회원
          {list?.totalPages > 1 && (
            <small className="toolbar-page">
              {' '}
              · {page} / {list.totalPages} 페이지
            </small>
          )}
        </span>
      </div>
      <RefreshStatus resource={resource} auto={false} />
      {saved && (
        <p className="notice success" role="status">
          {saved} 회원의 정보를 저장했습니다.
        </p>
      )}
      <div className="panel">
        <ResourceState resource={resource} empty={members?.length === 0} emptyMessage="회원이 없습니다." />
        {!!members?.length && (
          <div className="table-scroll">
            <table className="admin-table">
              <thead>
                <tr>
                  {['번호', '아이디', '닉네임', '포인트', '권한', '가입일', ''].map((title, index) => (
                    <th key={index} scope="col">
                      {title || <span className="sr-only">수정</span>}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {members.map((member) => {
                  const isSelf = me?.memberName === member.memberName
                  return editing === member.memberUid ? (
                    <tr key={member.memberUid} className="editing">
                      <td colSpan={7}>
                        <MemberEditor
                          member={member}
                          isSelf={isSelf}
                          onCancel={() => setEditing(null)}
                          onSaved={() => {
                            setEditing(null)
                            setSaved(member.memberId)
                            // 저장된 실제 값을 보려고 목록과 헤더의 회원 정보를 다시 불러온다.
                            resource.refresh()
                            if (isSelf) refreshMember()
                          }}
                        />
                      </td>
                    </tr>
                  ) : (
                    <tr key={member.memberUid}>
                      <td className="muted">{member.memberUid}</td>
                      <td>{member.memberId}</td>
                      <td>
                        <strong>{member.memberName}</strong>
                        {isSelf && <span className="pill mine-pill">나</span>}
                      </td>
                      <td className="numeric">{points(member.memberPoint)} P</td>
                      <td>
                        <span className={`role-badge ${member.role === 'ADMIN' ? 'admin' : ''}`}>
                          {member.role === 'ADMIN' ? '관리자' : '일반'}
                        </span>
                      </td>
                      <td className="muted nowrap">{dateTime(member.memberCreatedAt)}</td>
                      <td>
                        <button
                          type="button"
                          className="text-button"
                          onClick={() => {
                            setEditing(member.memberUid)
                            setSaved(null)
                          }}
                        >
                          수정
                        </button>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
      {list && (
        <Pagination
          page={page}
          totalPages={list.totalPages}
          onChange={changePage}
          label="회원 목록 페이지"
        />
      )}
    </>
  )
}

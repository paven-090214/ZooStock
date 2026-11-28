import { createContext, useContext } from 'react'

export const AuthContext = createContext(null)
export const useAuth = () => useContext(AuthContext)

// /mypage/overview의 권한 값. 백엔드 필드 이름이 roleType이며, role로 바뀌어도 동작하게 둔다.
export const isAdminMember = (member) => (member?.roleType ?? member?.role) === 'ADMIN'

import { routes as auth } from '@/modules/auth'
import { routes as resetPassword } from '@/modules/reset-password'
import { routes as dashboard } from '@/views/Dashboard'
import { routes as myProfile } from '@/views/MyProfile'
import { routes as user } from '@/views/User'
import { routes as restrictedArea } from '@/views/RestrictedArea'
import { routes as contents } from '@/views/Content'
import { routes as events } from '@/views/Event'
import { routes as commons } from '@/views/Commons'

export default [
  ...auth,
  ...resetPassword,
  ...dashboard,
  ...myProfile,
  ...user,
  ...restrictedArea,
  ...contents,
  ...events,
  ...commons,
]

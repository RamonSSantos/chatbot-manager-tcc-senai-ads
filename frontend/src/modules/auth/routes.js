export default [
  {
    name: 'Login',
    path: '/login',
    component: () => import('@/modules/auth/pages/Login'),
    meta: {
      title: 'Login',
    },
  },
]

export default [
  {
    name: 'ResetPassword',
    path: '/recuperar-senha',
    component: () => import('@/modules/reset-password/pages/ResetPassword'),
    meta: {
      title: 'Recuperar Senha',
    },
  },
]

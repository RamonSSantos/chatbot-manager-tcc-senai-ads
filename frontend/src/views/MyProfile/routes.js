export default [
  {
    name: 'MyProfile',
    path: '/meu-perfil',
    component: () => import('./MyProfile'),
    meta: {
      title: 'Meu perfil',
      breadcrumb: 'Meu perfil',
    },
  },
]

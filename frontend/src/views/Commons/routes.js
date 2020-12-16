export default [
  {
    name: 'NetworkIssue',
    path: '/network-issue',
    component: () => import('./NetworkIssue'),
    meta: {
      title: 'Site fora do ar',
    },
  },
  {
    name: 'PageNotFound',
    path: '/page-not-found',
    component: () => import('./PageNotFound'),
    meta: {
      title: 'Página não encontrada',
    },
  },
  {
    path: '*',
    redirect: { name: 'PageNotFound' },
  },
]
